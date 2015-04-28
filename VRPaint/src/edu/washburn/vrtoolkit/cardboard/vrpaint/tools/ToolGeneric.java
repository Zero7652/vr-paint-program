package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class ToolGeneric {
	protected OpenGlStuff world = null;
	boolean moving = false;
	float[] pos = {0, 0, 0};
	float scale = 1;
	public ToolGeneric(){};
	public ToolGeneric(boolean moving){
		this.moving = moving;
	}
	


    public void processButtonStart(boolean pressed){
    }

    public void processButtonSelect(boolean pressed){
    }

    public void processButtonX(boolean pressed){
    }

    public void processButtonY(boolean pressed){
    }

    public void processButtonA(boolean pressed){
    	moving = pressed;
    }

    public void processButtonB(boolean pressed){
    }

    public void processButtonR1(boolean pressed){
    	if(pressed){
	        world.currentTool = world.currentTool.next();
	        world.currentTool.getTool().register(world);
	        world.toastMode(world.currentTool.ordinal());
    	}
    }

    public void processButtonL1(boolean pressed){
    	if(pressed){
	        world.currentTool = world.currentTool.previous();
	        world.currentTool.getTool().register(world);
	        world.toastMode(world.currentTool.ordinal());
    	}
    }

    public void processButtonR2(boolean pressed){
    }

    public void processButtonL2(boolean pressed){
    }

    public void processButtonR3(boolean pressed){
    }

    public void processButtonL3(boolean pressed){
    }

    public void processLeftStick(float x, float y){
    }

    public void processRightStick(float x, float y){
    }

    public void processDpad(float x, float y){
    }

    public void processTriggers(float l, float r){
    }
	
	public void onDrawEye(Eye eye){
		
	}
	
	public void onNewFrame(HeadTransform headTransform){
//		if(moving)
//			world.createObject();

//    	double cubeDistance = Math.sqrt(
//    			((currentOld.getModel()[12] - currentNew.getModel()[12])*(currentOld.getModel()[12] - currentNew.getModel()[12])) +
//    			((currentOld.getModel()[13] - currentNew.getModel()[13])*(currentOld.getModel()[13] - currentNew.getModel()[13])) +
//    			((currentOld.getModel()[14] - currentNew.getModel()[14])*(currentOld.getModel()[14] - currentNew.getModel()[14]))
//    			);
//    	if(cubeDistance < 1) return;
//    	placeObjectInfrontOfCamera(currentNew);
//    	currentOld = currentNew;
//    	cubes.add(currentOld);
//    	
//    	currentNew = new GLSelectableObject(cubeCoords);
//    	
//    	currentNew.onSurfaceCreated(vertexShader, gridShader, passthroughShader);
	}
	
	public void register(OpenGlStuff world) {
		this.world = world;
	}
}
