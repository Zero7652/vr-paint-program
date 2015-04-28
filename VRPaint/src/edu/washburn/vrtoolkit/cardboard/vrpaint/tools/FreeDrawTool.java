package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import com.google.vrtoolkit.cardboard.HeadTransform;

public class FreeDrawTool extends ToolGeneric{

	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	
	public void onNewFrame(HeadTransform headTransform){
		if(moving){
	    	double cubeDistance = Math.sqrt(
	    			((world.currentOld.getModel()[12] - world.currentNew.getModel()[12])*(world.currentOld.getModel()[12] - world.currentNew.getModel()[12])) +
	    			((world.currentOld.getModel()[13] - world.currentNew.getModel()[13])*(world.currentOld.getModel()[13] - world.currentNew.getModel()[13])) +
	    			((world.currentOld.getModel()[14] - world.currentNew.getModel()[14])*(world.currentOld.getModel()[14] - world.currentNew.getModel()[14]))
	    			);
	    	if(cubeDistance < 1) return;
	    	world.placeObjectInfrontOfCamera(world.currentNew);
	    	world.currentOld = world.currentNew;
	    	world.cubes.add(world.currentOld);
	    	
	    	world.createNewObject();
	    	
	    	world.currentNew.onSurfaceCreated(world.vertexShader, world.gridShader, world.passthroughShader);
		}
	}
}
