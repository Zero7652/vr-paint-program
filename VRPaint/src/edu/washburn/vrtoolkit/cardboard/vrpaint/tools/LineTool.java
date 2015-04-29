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
	protected boolean wasMoving = true;
	protected GLSelectableObject start = null;
	protected GLSelectableObject end = null;
	protected List<GLSelectableObject> currentLine = new ArrayList<GLSelectableObject>();
	protected List<GLSelectableObject> fullList = new ArrayList<GLSelectableObject>();
	protected Iterator<GLSelectableObject> fullListIterator;
	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	@Override
	public void onNewFrame(HeadTransform headTransform){
		fullListIterator = fullList.iterator();
		if(wasMoving && !moving){
			fullList.removeAll(currentLine);
			world.cubes.addAll(currentLine);
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
        	currentLine.add(end);
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            wasMoving = true;
        }
	}

	@Override
	public void onDrawEye(Eye eye){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		for(GLSelectableObject cube : currentLine){
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
        currentLine.add(currentMid);
        createLine(cube1,currentMid);
        createLine(cube2,currentMid);
        return;
    }

	public void register(OpenGlStuff world) {
		this.world = world;
	}
	
	protected GLSelectableObject getNewObject(float[] pos){
		return getNewObject(pos[0], pos[1], pos[2]);
	}
	
	protected GLSelectableObject getNewObject(float x, float y, float z){
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
