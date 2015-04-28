package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff.GLSelectableObject;

public class LineTool extends ToolGeneric{
	private boolean initialized = false;
	private boolean wasMoving = false;
	private GLSelectableObject start = null;
	private GLSelectableObject end = null;
	private List<GLSelectableObject> currentList = new ArrayList<GLSelectableObject>();
	private List<GLSelectableObject> fullList = new ArrayList<GLSelectableObject>();
	private Iterator<GLSelectableObject> fullListIterator;
	@Override
    public void processButtonA(boolean pressed){
    	moving = pressed;
    }
	@Override
	public void onNewFrame(HeadTransform headTransform){
		if(!initialized){
			initialized = true;
			start = world.new GLSelectableObject(world.cubeCoords);
	        start.onSurfaceCreated(world.vertexShader, world.passthroughShader, world.passthroughShader);
			world.placeObjectInfrontOfCamera(start);
			end = world.new GLSelectableObject(world.cubeCoords);
	        end.onSurfaceCreated(world.vertexShader, world.passthroughShader, world.passthroughShader);
		}

		if(!wasMoving && !moving){
        	currentList.clear();
        	currentList.add(start);
			world.placeObjectInfrontOfCamera(start);
		}
        if(moving) {
        	currentList.clear();
        	currentList.add(start);
        	currentList.add(end);
			world.placeObjectInfrontOfCamera(end);
			fullListIterator = fullList.iterator();
            createLine(start, end);
            wasMoving = true;
        }
        if(wasMoving && !moving){
        	fullList.removeAll(currentList);
        	world.cubes.addAll(currentList);
        	currentList.clear();
        	wasMoving = false;
        }
	}

	@Override
	public void onDrawEye(Eye eye){
        float[] perspective = eye.getPerspective(world.Z_NEAR, world.Z_FAR);
		for(GLSelectableObject cube : currentList){
			// Build the ModelView and ModelViewProjection matrices
			// for calculating cube position and light.
			Matrix.multiplyMM(world.modelView, 0, world.view, 0, cube.getModel(), 0);
			Matrix.multiplyMM(world.modelViewProjection, 0, perspective, 0, world.modelView, 0);
			cube.drawCube();
		}
	}

    public void createLine(GLSelectableObject cube1, GLSelectableObject cube2){
        //System.out.println("Head View: " + cube1.getModel()[12] + " || " + cube1.getModel()[13] + " || " + cube1.getModel()[14]);
        double cubeDistance = Math.sqrt(
                ((cube1.getModel()[12] - cube2.getModel()[12])*(cube1.getModel()[12] - cube2.getModel()[12])) +
                        ((cube1.getModel()[13] - cube2.getModel()[13])*(cube1.getModel()[13] - cube2.getModel()[13])) +
                        ((cube1.getModel()[14] - cube2.getModel()[14])*(cube1.getModel()[14] - cube2.getModel()[14]))
        );
        if(cubeDistance < 0.7) return;
        float mX = (cube1.getModel()[12] + cube2.getModel()[12])/2;
        float mY = (cube1.getModel()[13] + cube2.getModel()[13])/2;
        float mZ = (cube1.getModel()[14] + cube2.getModel()[14])/2;

        GLSelectableObject currentMid = getNewObject(-mX,-mY,-mZ);
        currentList.add(currentMid);
        createLine(cube1,currentMid);
        createLine(cube2,currentMid);
        return;
    }

	public void register(OpenGlStuff world) {
		this.world = world;
	}
	
	private GLSelectableObject getNewObject(float x, float y, float z){
		if(fullListIterator.hasNext()){
			GLSelectableObject cube = fullListIterator.next();
			cube.getModel()[12] = x;
			cube.getModel()[13] = y;
			cube.getModel()[14] = z;
			return cube;
		}
		GLSelectableObject cube = world.new GLSelectableObject(x,y,z);
        cube.onSurfaceCreated(world.vertexShader, world.passthroughShader, world.passthroughShader);
        return cube;
	}
	
}
