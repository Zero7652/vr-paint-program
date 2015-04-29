package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff.GLSelectableObject;

public class StarburstTool extends LineTool {
	protected final int END_PHASE = 4;
	protected int phase = 0;
	protected List<List<GLSelectableObject>> currentLineList = new ArrayList<List<GLSelectableObject>>();
	@Override
    public boolean processButtonB(boolean pressed){
		if(pressed)
			phase = END_PHASE;
		return true;
    }
	@Override
	public void onNewFrame(HeadTransform headTransform){
		fullListIterator = fullList.iterator();
		if(phase == 0){
			phase++;
		}
		if(phase == 1){
			
		}
		if(!wasMoving && !moving){
        	currentLine.clear();
        	currentLine.add(start);
			world.placeObjectInfrontOfCamera(start);
		}
        if(moving && phase == 2) {
        	currentLine.clear();
        	currentLine.add(start);
        	currentLine.add(end);
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            wasMoving = true;
        }
        if(phase == 1){
        	fullList.removeAll(currentLine);
        	currentLineList.add(currentLine);
        	currentLine = new ArrayList<GLSelectableObject>();
        	end = getNewObject(0, 0, 0);
        	currentLine.clear();
        	wasMoving = false;
        	phase++;
        }
	}

	@Override
	public void onDrawEye(Eye eye){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		for(List<GLSelectableObject> cubes : currentLineList){
			for(GLSelectableObject cube : cubes){
				// Build the ModelView and ModelViewProjection matrices
				// for calculating cube position and light.
				Matrix.multiplyMM(world.modelView, 0, world.view, 0, cube.getModel(), 0);
				Matrix.multiplyMM(world.modelViewProjection, 0, perspective, 0, world.modelView, 0);
				cube.drawCube();
			}
		}
	}
}
