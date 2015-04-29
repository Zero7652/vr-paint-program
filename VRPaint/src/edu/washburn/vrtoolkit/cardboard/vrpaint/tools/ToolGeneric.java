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
	
    public boolean processButtonStart(boolean pressed){
		return false;
    }

    public boolean processButtonSelect(boolean pressed){
		return false;
    }

    public boolean processButtonX(boolean pressed){
		return false;
    }

    public boolean processButtonY(boolean pressed){
		return false;
    }

    public boolean processButtonA(boolean pressed){
		return false;
    }

    public boolean processButtonB(boolean pressed){
		return false;
    }

    public boolean processButtonR1(boolean pressed){
    	if(pressed){
	        world.currentTool = world.currentTool.next();
	        world.currentTool.getTool().register(world);
	        world.getMain().getOverlayView().show3DToast(" " + world.currentTool.getToolText());
			return true;
    	}
		return false;
    }

    public boolean processButtonL1(boolean pressed){
    	if(pressed){
	        world.currentTool = world.currentTool.previous();
	        world.currentTool.getTool().register(world);
	        world.getMain().getOverlayView().show3DToast(" " + world.currentTool.getToolText());
			return true;
    	}
		return false;
    }

    public boolean processButtonR2(boolean pressed){
		return false;
    }

    public boolean processButtonL2(boolean pressed){
		return false;
    }

    public boolean processButtonR3(boolean pressed){
		return false;
    }

    public boolean processButtonL3(boolean pressed){
		return false;
    }

    public boolean processLeftStick(float x, float y){
		return false;
    }

    public boolean processRightStick(float x, float y){
		return false;
    }

    public boolean processDpad(float x, float y){
		return false;
    }

    public boolean processTriggers(float l, float r){
		return false;
    }
	
	public void onDrawEye(Eye eye){
		
	}
	
	public void onNewFrame(HeadTransform headTransform){
	}
	
	public void register(OpenGlStuff world) {
		this.world = world;
	}
}
