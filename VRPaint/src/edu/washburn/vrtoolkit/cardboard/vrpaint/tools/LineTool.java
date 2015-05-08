package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class LineTool extends AbstractCacheTool{
	private static float touchingDistance = .7f;
	protected boolean wasMoving = true;
	protected GLSelectableObject start = null;
	protected GLSelectableObject end = null;
	protected List<GLSelectableObject> currentLine = new ArrayList<GLSelectableObject>();
	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	@Override
	public void onNewFrame(HeadTransform headTransform){
		onNewFrameAbstractCacheTool();
		if(wasMoving && !moving){
			removeFromCache(currentLine);
			readyLine.addAll(currentLine);
			start = getNewObject(0, 0, 0);
			end = getNewObject(0, 0, 0);
			currentLine.clear();
			wasMoving = false;
		}
		if(!wasMoving && !moving){
        	currentLine.clear();
        	currentLine.add(start);
			world.placeObjectInfrontOfCamera(start);
		}
        if(moving) {
        	currentLine.clear();
        	currentLine.add(start);
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            currentLine.add(end);
            wasMoving = true;
        }
	}

	@Override
	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		for(GLSelectableObject cube : currentLine){
			// Build the ModelView and ModelViewProjection matrices
			// for calculating cube position and light.
			Matrix.multiplyMM(modelView, 0, view, 0, cube.getModel(), 0);
			Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
			cube.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);
		}
	}

    public void createLine(GLSelectableObject cube1, GLSelectableObject cube2){
        if(isCubeTouching(cube1,cube2)) return;
        float mX = (cube1.getModel()[12] + cube2.getModel()[12])/2;
        float mY = (cube1.getModel()[13] + cube2.getModel()[13])/2;
        float mZ = (cube1.getModel()[14] + cube2.getModel()[14])/2;

        GLSelectableObject currentMid = getNewObject(-mX,-mY,-mZ);
        currentLine.add(currentMid);
        createLine(cube1,currentMid);
        createLine(cube2,currentMid);
    }

	
	protected boolean isCubeTouching(GLSelectableObject cube1, GLSelectableObject cube2){
	   double cubeDistance = Math.sqrt(
	            ((cube1.getModel()[12] - cube2.getModel()[12])*(cube1.getModel()[12] - cube2.getModel()[12])) +
	            ((cube1.getModel()[13] - cube2.getModel()[13])*(cube1.getModel()[13] - cube2.getModel()[13])) +
	            ((cube1.getModel()[14] - cube2.getModel()[14])*(cube1.getModel()[14] - cube2.getModel()[14]))
	    );
	    return cubeDistance < touchingDistance;
	}
}
