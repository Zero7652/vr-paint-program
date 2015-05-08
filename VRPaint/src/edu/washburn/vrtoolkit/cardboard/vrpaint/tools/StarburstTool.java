package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

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
		onNewFrameAbstractCacheTool();
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
			removeFromCache(currentLine);
			currentLineList.add(currentLine);
			currentLine = new ArrayList<GLSelectableObject>();
			moving = false;
		}
        if(phase == 3) {
        	currentLine.clear();
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            currentLine.add(end);
            wasMoving = true;
        }
        if(phase == END_PHASE){
			removeFromCache(currentLine);
			currentLineList.add(currentLine);
			currentLine = new ArrayList<GLSelectableObject>();
        	readyLine.add(start);
        	for(List<GLSelectableObject> currentLine : currentLineList)
        		readyLine.addAll(currentLine);
			currentLineList.clear();
			phase = 1;
        }
	}

	@Override
	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		Matrix.multiplyMM(modelView, 0, view, 0, start.getModel(), 0);
		Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
		start.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);
		
		for(List<GLSelectableObject> cubes : currentLineList){
			for(GLSelectableObject cube : cubes){
				// Build the ModelView and ModelViewProjection matrices
				// for calculating cube position and light.
				Matrix.multiplyMM(modelView, 0, view, 0, cube.getModel(), 0);
				Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
				cube.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);
			}
		}
		
		for(GLSelectableObject cube : currentLine){
			// Build the ModelView and ModelViewProjection matrices
			// for calculating cube position and light.
			Matrix.multiplyMM(modelView, 0, view, 0, cube.getModel(), 0);
			Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
			cube.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);
		}
	}
}
