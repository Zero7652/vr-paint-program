package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class PolygonTool extends LineTool {
	protected final int END_PHASE = 6;
	protected int phase = 0;
	protected List<List<GLSelectableObject>> currentLineList = new ArrayList<List<GLSelectableObject>>();
	private GLSelectableObject first = null;
	private GLSelectableObject start = null;
	private GLSelectableObject end = null;
	
	@Override
    public boolean processButtonA(boolean pressed){
		phase++;
		return true;
    }
	
	@Override
	public boolean processButtonB(boolean pressed){
		if(pressed)
			phase = END_PHASE;
		return true;
	}
	
	@Override
	public void onNewFrame(HeadTransform headTransform){
		onNewFrameAbstractCacheTool();
		//initialize 
		if(phase == 0){
			phase++;
		}
		if(start == null){
			start = getNewObject(0, 0, 0);
		}
		//Beginning before any buttons are pressed start cube tracks head.
		if(phase == 1){
        	currentLine.add(start);
			phase++;
		}
		if(phase == 2){
			world.placeObjectInfrontOfCamera(start);
		}
		//once button is pressed to create the start of the first line
		if(phase == 3){
			phase++;
			first = start;
			world.placeObjectInfrontOfCamera(start);
		}
		//track the line to the current end
		if(phase == 4){
			if(end == null){
				end = getNewObject(0, 0, 0);
			}
        	currentLine.clear();
        	currentLine.add(start);
			world.placeObjectInfrontOfCamera(end);
            createLine(start, end);
            currentLine.add(end);
		}
		//button was pressed to end current line segment
		if(phase == 5){
			if(currentLineList.size()>1 && isCubeTouching(first, end)){
				phase = END_PHASE;
			} else {
	        	currentLine.clear();
	        	currentLine.add(start);
				world.placeObjectInfrontOfCamera(end);
	            createLine(start, end);
	            currentLine.add(end);
	            removeFromCache(currentLine);
	            currentLineList.add(currentLine);
	            currentLine = new ArrayList<GLSelectableObject>();
	            start = end;
	            end = null;
	            phase--;
            }
		}
		if(phase == END_PHASE){
        	currentLine.clear();
        	currentLine.add(start);
            createLine(start, first);
            removeFromCache(currentLine);
            currentLineList.add(currentLine);
            
        	for(List<GLSelectableObject> currentLine : currentLineList)
        		readyLine.addAll(currentLine);
			currentLineList.clear();
			currentLine.clear();;
			phase = 1;
			start = null;
			first = null;
			end = null;
		}
	}
	
	@Override
	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		
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
