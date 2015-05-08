package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class FreeDrawTool extends AbstractCacheTool{
	GLSelectableObject current = null;
	GLSelectableObject previous = null;
	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	
	public void onNewFrame(HeadTransform headTransform){
		onNewFrameAbstractCacheTool();
		if(current == null){
			current = getNewObject(0,0,0);
		}
		world.placeObjectInfrontOfCamera(current);
		if(moving){
	    	double cubeDistance = 9000;
	    	if(previous != null){
	    		cubeDistance = Math.sqrt(
	    			((previous.getModel()[12] - current.getModel()[12])*(previous.getModel()[12] - current.getModel()[12])) +
	    			((previous.getModel()[13] - current.getModel()[13])*(previous.getModel()[13] - current.getModel()[13])) +
	    			((previous.getModel()[14] - current.getModel()[14])*(previous.getModel()[14] - current.getModel()[14]))
	    			);
	    	}
	    	if(cubeDistance < 1) return;
	    	previous = current;
	    	readyLine.add(current);
			current = getNewObject(0,0,0);
			world.placeObjectInfrontOfCamera(current);
		}
	}

	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
		
    	double cubeDistance = 9;
    	if(!world.cubes.isEmpty()){
    		cubeDistance = Math.sqrt(
    			((previous.getModel()[12] - current.getModel()[12])*(previous.getModel()[12] - current.getModel()[12])) +
    			((previous.getModel()[13] - current.getModel()[13])*(previous.getModel()[13] - current.getModel()[13])) +
    			((previous.getModel()[14] - current.getModel()[14])*(previous.getModel()[14] - current.getModel()[14]))
    			);
    	}
    	if(cubeDistance < 1) return;
    	if(current != null){

            float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
			Matrix.multiplyMM(modelView, 0, view, 0, current.getModel(), 0);
			Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
    		current.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);;
    	}
	}
}
