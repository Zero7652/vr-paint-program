package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class FreeDrawTool extends ToolGeneric{
	GLSelectableObject current = null;
	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	
	public void onNewFrame(HeadTransform headTransform, float[] headView){
		if(current == null){
			current = new GLSelectableObject(0, 0, 0);
			current.onSurfaceCreated(world.vertexShader, world.gridShader, world.passthroughShader);
		}
		world.placeObjectInfrontOfCamera(current);
		if(moving){
	    	double cubeDistance = 9;
	    	if(!world.cubes.isEmpty()){
	    		cubeDistance = Math.sqrt(
	    			((world.cubes.get(world.cubes.size()-1).getModel()[12] - current.getModel()[12])*(world.cubes.get(world.cubes.size()-1).getModel()[12] - current.getModel()[12])) +
	    			((world.cubes.get(world.cubes.size()-1).getModel()[13] - current.getModel()[13])*(world.cubes.get(world.cubes.size()-1).getModel()[13] - current.getModel()[13])) +
	    			((world.cubes.get(world.cubes.size()-1).getModel()[14] - current.getModel()[14])*(world.cubes.get(world.cubes.size()-1).getModel()[14] - current.getModel()[14]))
	    			);
	    	}
	    	if(cubeDistance < 1) return;
	    	world.cubes.add(current);
			current = new GLSelectableObject(0, 0, 0);
			current.onSurfaceCreated(world.vertexShader, world.gridShader, world.passthroughShader);
			world.placeObjectInfrontOfCamera(current);
		}
	}
	
	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
		
    	double cubeDistance = 9;
    	if(!world.cubes.isEmpty()){
    		cubeDistance = Math.sqrt(
    			((world.cubes.get(world.cubes.size()-1).getModel()[12] - current.getModel()[12])*(world.cubes.get(world.cubes.size()-1).getModel()[12] - current.getModel()[12])) +
    			((world.cubes.get(world.cubes.size()-1).getModel()[13] - current.getModel()[13])*(world.cubes.get(world.cubes.size()-1).getModel()[13] - current.getModel()[13])) +
    			((world.cubes.get(world.cubes.size()-1).getModel()[14] - current.getModel()[14])*(world.cubes.get(world.cubes.size()-1).getModel()[14] - current.getModel()[14]))
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
