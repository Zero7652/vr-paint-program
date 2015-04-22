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

import javax.microedition.khronos.egl.EGLConfig;

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
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff.GLSelectableObject;

import java.net.*;
import java.io.*;
import android.app.Activity;

/**
 * A Cardboard sample application.
 */
public class MainActivity extends CardboardActivity implements CardboardView.StereoRenderer {

  private int score = 0;

  private Vibrator vibrator;
  private CardboardOverlayView overlayView;
  private OpenGlStuff openGlStuff;
  private Socket s;
  private BufferedReader in;
    private String line;

  /**
   * Sets the view to our CardboardView and initializes the transformation matrices we will use
   * to render our scene.
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
    overlayView.show3DToast("Pull the magnet when you find an object.");
      runThread();
      //controllerThread(MainActivity.this);
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
   * <p>OpenGL doesn't use Java arrays, but rather needs data in a format it can understand.
   * Hence we use ByteBuffers.
   *
   * @param config The EGL configuration used when creating the surface.
   */
  @Override
  public void onSurfaceCreated(EGLConfig config) {
    Log.i(OpenGlStuff.TAG, "onSurfaceCreated");
    openGlStuff.onSurfaceCreated(config);
  }

  /**
   * Prepares OpenGL ES before we draw a frame.
   *
   * @param headTransform The head transformation in the new frame.
   */
  @Override
  public void onNewFrame(HeadTransform headTransform) {
    openGlStuff.onNewFrame(headTransform);
  }

  /**
   * Draws a frame for an eye.
   *
   * @param eye The eye to render. Includes all required transformations.
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
    processTrigger();
  }

    /////////////N E T W O R K  /////////////S O C K E T ////////////C O D E Z Z Z Z
    public void runThread()
    {
        Thread tr = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //s=new Socket("172.18.3.206",5009);
                    s=new Socket("192.168.3.2",5009);
                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    while(true)
                    {
                        while((line=in.readLine())!=null)
                        {
                            runThread2(MainActivity.this);
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        });
        tr.start();
    }

    private void runThread2(Activity act) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vibrator.vibrate(50);

                if (line.equals("7")) openGlStuff.moveCursor(-0.5, 0.5);
                if (line.equals("8")) openGlStuff.moveCursor(0, 0.5);
                if (line.equals("9")) openGlStuff.moveCursor(0.5, 0.5);
                if (line.equals("4")) openGlStuff.moveCursor(-0.5, 0);
                if (line.equals("6")) openGlStuff.moveCursor(0.5, 0);
                if (line.equals("1")) openGlStuff.moveCursor(-0.5, -0.5);
                if (line.equals("2")) openGlStuff.moveCursor(0, -0.5);
                if (line.equals("3")) openGlStuff.moveCursor(0.5, -0.5);
                if (line.equals("f")) openGlStuff.selectMode(1);
                if (line.equals("l")) openGlStuff.selectMode(2);
                if (line.equals("c")) openGlStuff.selectMode(3);
                if (line.equals("p")) openGlStuff.selectMode(4);
                if (line.equals("e")) openGlStuff.selectMode(0);
                if (line.equals("5")&&openGlStuff.drawing==false) {
                    openGlStuff.drawStuff(true);
                } else {
                    openGlStuff.drawStuff(false);
                }
            }
        });
    }

    private void processTrigger() {
        GLSelectableObject cube;
        if ((cube = openGlStuff.isLookingAtObject()) != null) {
            score++;
            overlayView.show3DToast("Cube Successfully Deleted.\nEterminate the Others!\nScore = " + score);
            openGlStuff.hideObject(cube);
        } else {
            overlayView.show3DToast("Look around to find the object!");
        }

        // Always give user feedback.
        vibrator.vibrate(50);
    }

    private void toastMode(int i){
        if(i==0)
            overlayView.show3DToast(i+" No-Drawing Mode");
        if(i==1)
            overlayView.show3DToast(i+" Free Draw!");
        if(i==2)
            overlayView.show3DToast(i+" Straight Lines!");
        if(i==3)
            overlayView.show3DToast(i+" Circles! (not yet implemented)");
        if(i==4)
            overlayView.show3DToast(i+" Polygons!");
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK ) {

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
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_X, historyPos);
        float y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Y, historyPos);
        openGlStuff.moveCursor(x,y);

        if (x == 0 || y == 0) {
            x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_X, historyPos);
            y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_HAT_Y, historyPos);
            //openGlStuff.moveCursor(-x,-y);
        }
        if (x == 0 || y == 0) {
            x = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_Z, historyPos);
            y = getCenteredAxis(event, mInputDevice, MotionEvent.AXIS_RZ, historyPos);
            //openGlStuff.processMove(x,y);
        }

    }


//  jason kopecky
//  american family insurance
//  po box 2570 meryland heights mo 63043

    private static float getCenteredAxis(MotionEvent event, InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range = device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis):
                            event.getHistoricalAxisValue(axis, historyPos);

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
          if (event.getRepeatCount() == 0 && event.getAction() == KeyEvent.ACTION_DOWN) {
            int keyCode = event.getKeyCode();
            int mode;
            Log.i(OpenGlStuff.TAG, ""+keyCode);
            switch (keyCode) {
                case 188: // -> X <-  B U T T O N
                        openGlStuff.selectMode(0);
                        overlayView.show3DToast("No-Drawing Mode");
                    break;
                case 189: // -> A <-  B U T T O N
                        /* probably better to allow holding this button only when free drawing */
                        if(openGlStuff.drawing==false && openGlStuff.drawingMode!=0) {
                            openGlStuff.drawStuff(true);
                        } else {
                            if(openGlStuff.drawing==false)
                                overlayView.show3DToast("Nothing to Draw!");
                            openGlStuff.drawStuff(false);
                        }
                    break;
                case 190: // -> B <-  B U T T O N
                        processTrigger();
                    break;
                case 191: // -> Y <-  B U T T O N
                    break;
                case 192: // -> TL <-  B U T T O N
                        mode = openGlStuff.getMode();
                        if(mode == 0)
                            mode = 4;
                        else
                            mode = (mode-1)%5;
                        toastMode(mode);
                        openGlStuff.selectMode(mode);
                    break;
                case 193: // -> TR <-  B U T T O N
                    mode = openGlStuff.getMode();
                    if(mode == 4)
                        mode = 0;
                    else
                        mode = (mode+1)%5;
                    toastMode(mode);
                    openGlStuff.selectMode(mode);
                    break;
                case 194: // -> BL <-  B U T T O N
                    break;
                case 195: // -> BR <-  B U T T O N
                    break;
                case 196: // -> Select <-  B U T T O N
                    break;
                case 197: // -> Start <-  B U T T O N
                    break;
                case 198: // -> Left <-  A N A L O G U E   S T I C K
                        openGlStuff.centerCursor();
                    break;
                case 199: // -> Right <-  A N A L O G U E   S T I C K
                    break;
                default:
                    if (CardboardOverlayView.isFireKey(keyCode)) {
                        // Update the ship object to fire lasers

                        Log.i(OpenGlStuff.TAG, "on SOURCE_GAMEPAD");

//                    	    processTrigger();
                        handled = true;
                    }
                    break;
            }
        }
        if (handled) {
            return true;
        }
      }
        return super.dispatchKeyEvent(event);
    }
}
