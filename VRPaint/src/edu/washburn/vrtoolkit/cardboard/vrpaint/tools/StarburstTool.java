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
			start = getNewObject(0, 0, 0);
			end = getNewObject(0, 0, 0);
			phase++;
		}
		if(phase == 2 && moving){
			moving = false;
			phase++;
		}
		if(phase == 2){
        	world.placeObjectInfrontOfCamera(start);
		}
		if(phase == 3 && moving){
			fullList.removeAll(currentLine);
			currentLineList.add(currentLine);
			currentLine = new ArrayList<OpenGlStuff.GLSelectableObject>();
			moving = false;
		}
        if(phase == 3) {
        	currentLine.clear();
        	currentLine.add(end);
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            wasMoving = true;
        }
        if(phase == END_PHASE){
        	world.cubes.add(start);
        	for(List<GLSelectableObject> currentLine : currentLineList)
        		world.cubes.addAll(currentLine);
			currentLineList.clear();
			phase = 1;
        }
	}

	@Override
	public void onDrawEye(Eye eye){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		Matrix.multiplyMM(world.modelView, 0, world.view, 0, start.getModel(), 0);
		Matrix.multiplyMM(world.modelViewProjection, 0, perspective, 0, world.modelView, 0);
		start.drawCube();
		
		for(List<GLSelectableObject> cubes : currentLineList){
			for(GLSelectableObject cube : cubes){
				// Build the ModelView and ModelViewProjection matrices
				// for calculating cube position and light.
				Matrix.multiplyMM(world.modelView, 0, world.view, 0, cube.getModel(), 0);
				Matrix.multiplyMM(world.modelViewProjection, 0, perspective, 0, world.modelView, 0);
				cube.drawCube();
			}
		}
		
		for(GLSelectableObject cube : currentLine){
			// Build the ModelView and ModelViewProjection matrices
			// for calculating cube position and light.
			Matrix.multiplyMM(world.modelView, 0, world.view, 0, cube.getModel(), 0);
			Matrix.multiplyMM(world.modelViewProjection, 0, perspective, 0, world.modelView, 0);
			cube.drawCube();
		}
	}
}
