/*
 * Copyright 2014 Google Inc. All Rights Reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.washburn.vrtoolkit.cardboard.vrpaint;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import javax.microedition.khronos.egl.EGLConfig;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.google.vrtoolkit.cardboard.CardboardActivity;
import com.google.vrtoolkit.cardboard.CardboardView;
import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.Viewport;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;

/**
 * A Cardboard sample application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

	private Vibrator vibrator;
	private CardboardOverlayView overlayView;
	private OpenGlStuff openGlStuff;
	private Socket s;
	private BufferedReader in;
	private String line;

	/**
	 * Sets the view to our CardboardView and initializes the transformation
	 * matrices we will use to render our scene.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		openGlStuff = new OpenGlStuff(this);

		setContentView(R.layout.common_ui);
		CardboardView cardboardView = (CardboardView) findViewById(R.id.cardboard_view);
		cardboardView.setRenderer(this);
		setCardboardView(cardboardView);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		overlayView = (CardboardOverlayView) findViewById(R.id.overlay);
	}

	@Override
	public void onRendererShutdown() {
		Log.i(OpenGlStuff.TAG, "onRendererShutdown");
	}

	@Override
	public void onSurfaceChanged(int width, int height) {
		Log.i(OpenGlStuff.TAG, "onSurfaceChanged");
	}

	/**
	 * Creates the buffers we use to store information about the 3D world.
	 *
	 * <p>
	 * OpenGL doesn't use Java arrays, but rather needs data in a format it can
	 * understand. Hence we use ByteBuffers.
	 *
	 * @param config
	 *            The EGL configuration used when creating the surface.
	 */
	@Override
	public void onSurfaceCreated(EGLConfig config) {
		Log.i(OpenGlStuff.TAG, "onSurfaceCreated");
		openGlStuff.onSurfaceCreated(config);
	}

	/**
	 * Prepares OpenGL ES before we draw a frame.
	 *
	 * @param headTransform
	 *            The head transformation in the new frame.
	 */
	@Override
	public void onNewFrame(HeadTransform headTransform) {
		openGlStuff.onNewFrame(headTransform);
	}

	/**
	 * Draws a frame for an eye.
	 *
	 * @param eye
	 *            The eye to render. Includes all required transformations.
	 */
	@Override
	public void onDrawEye(Eye eye) {
		openGlStuff.onDrawEye(eye);
	}

	@Override
	public void onFinishFrame(Viewport viewport) {
	}

	/**
	 * Called when the Cardboard trigger is pulled.
	 */
	@Override
	public void onCardboardTrigger() {
		Log.i(OpenGlStuff.TAG, "onCardboardTrigger");
		vibrator.vibrate(50);
		overlayView.show3DToast("Cardboard Trigger not available");
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event) {
		// Check that the event came from a game controller
		if ((event.getSource() & InputDevice.SOURCE_UNKNOWN) == InputDevice.SOURCE_UNKNOWN) {

			// Process all historical movement samples in the batch
			final int historySize = event.getHistorySize();

			// Process the movements starting from the
			// earliest historical position in the batch
			for (int i = 0; i < historySize; i++) {
				// Process the event at historical position i
				processJoystickInput(event, i);
			}

			// Process the current movement sample in the batch (position -1)
			processJoystickInput(event, -1);
			return true;
		}
		return super.onGenericMotionEvent(event);
	}

	private void processJoystickInput(MotionEvent event, int historyPos) {
		InputDevice mInputDevice = event.getDevice();

		// Calculate the horizontal distance to move by
		// using the input value from one of these physical controls:
		// the left control stick,
		float x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
		float y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        openGlStuff.processLeftStick(x,y);

		// right control stick
		x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);
		y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);
        openGlStuff.processRightStick(x, y);

		// dpad
		x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
		y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
        openGlStuff.processDpad(x, y);

		// left and right triggers
		x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_LTRIGGER, historyPos);
		y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RTRIGGER, historyPos);
		openGlStuff.processTriggers(x, y);
	}

	private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
		final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());

		// A joystick at rest does not always report an absolute position of
		// (0,0). Use the getFlat() method to determine the range of values
		// bounding the joystick axis center.
		if (range != null) {
			final float flat = range.getFlat();
			final float value = historyPos < 0 ? event.getAxisValue(axis) : event.getHistoricalAxisValue(axis, historyPos);

			// Ignore axis values that are within the 'flat' region of the
			// joystick axis center.
			if (Math.abs(value) > flat) {
				return value;
			}
		}
		return 0;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean handled = false;
		if ((event.getSource() & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD) {
			handled = true;
			if (event.getRepeatCount() == 0) {
				if(event.getAction() == KeyEvent.ACTION_DOWN)
					vibrator.vibrate(50);
				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_BUTTON_X:
				case 188: // -> X <- B U T T O N
					openGlStuff.processButtonX(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_Y:
				case 191: // -> Y <- B U T T O N
					openGlStuff.processButtonY(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_A:
				case 189: // -> A <- B U T T O N
					openGlStuff.processButtonA(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_B:
				case 190: // -> B <- B U T T O N
					openGlStuff.processButtonB(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BACK:
				case KeyEvent.KEYCODE_BUTTON_SELECT:
				case 196: // -> Select <- B U T T O N
					openGlStuff.processButtonSelect(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_START:
				case 197: // -> Start <- B U T T O N
					openGlStuff.processButtonStart(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_L1:
				case 192: // -> TL <- B U T T O N
					openGlStuff.processButtonL1(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_R1:
				case 193: // -> TR <- B U T T O N
					openGlStuff.processButtonR1(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_L2:
				case 194: // -> BL <- B U T T O N
					openGlStuff.processButtonL2(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_R2:
				case 195: // -> BR <- B U T T O N
					openGlStuff.processButtonR2(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_THUMBL:
				case 198: // -> Left <- A N A L O G U E S T I C K
					openGlStuff.processButtonL3(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				case KeyEvent.KEYCODE_BUTTON_THUMBR:
				case 199: // -> Right <- A N A L O G U E S T I C K
					openGlStuff.processButtonR3(event.getAction() == KeyEvent.ACTION_DOWN);
					break;
				default:
					handled = false;
					break;
				}
			}
		}
		return handled ? true : super.dispatchKeyEvent(event);
	}

	public CardboardOverlayView getOverlayView() {
		return overlayView;
	}

	public void setOverlayView(CardboardOverlayView overlayView) {
		this.overlayView = overlayView;
	}
}
