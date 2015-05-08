package edu.washburn.vrtoolkit.cardboard.vrpaint.tools;

import java.util.ArrayList;
import java.util.List;

import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;

import edu.washburn.vrtoolkit.cardboard.vrpaint.GLSelectableObject;
import edu.washburn.vrtoolkit.cardboard.vrpaint.OpenGlStuff;

public class CircleTool extends AbstractCacheTool{
	private boolean wasMoving = false;
	private GLSelectableObject start = null;
	private GLSelectableObject end = null;
	private List<GLSelectableObject> currentList = new ArrayList<GLSelectableObject>();
    public float[] hView = new float[16];
    
	@Override
    public boolean processButtonA(boolean pressed){
    	moving = pressed;
		return true;
    }
	@Override
	public void onNewFrame(HeadTransform headTransform){
		onNewFrameAbstractCacheTool();
		if(start == null){
			start = getNewObject(0, 0, 0);
		}
		if(wasMoving && !moving){
			removeFromCache(currentList);
			readyLine.addAll(currentList);
			if(currentList.contains(end)){
				end = null;
			}
			if(currentList.contains(start)){
				start = getNewObject(0, 0, 0);
			}
			currentList.clear();
			wasMoving = false;
		}
		if(!wasMoving && !moving){
			headTransform.getHeadView(hView, 0);
        	currentList.clear();
        	currentList.add(start);
			world.placeObjectInfrontOfCamera(start);
		}
        if(moving) {
        	if(end == null){
        		end = getNewObject(0, 0, 0);
        	}
        	currentList.clear();
        	currentList.add(start);
        	currentList.add(end);
			world.placeObjectInfrontOfCamera(end);
            createCircle(start, end, hView);
            wasMoving = true;
        }
	}

	@Override
	public void onDrawEye(Eye eye, float[] view, float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection){
        float[] perspective = eye.getPerspective(OpenGlStuff.Z_NEAR, OpenGlStuff.Z_FAR);
		for(GLSelectableObject cube : currentList){
			// Build the ModelView and ModelViewProjection matrices
			// for calculating cube position and light.
			Matrix.multiplyMM(modelView, 0, view, 0, cube.getModel(), 0);
			Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
			cube.drawCube(lightPosInEyeSpace, modelView, headView, modelViewProjection);
		}
	}

    private void createCircle(GLSelectableObject cube1, GLSelectableObject cube2, float[] h){
        float[] circleVector = {
                cube1.getModel()[12] - cube2.getModel()[12],
                cube1.getModel()[13] - cube2.getModel()[13],
                cube1.getModel()[14] - cube2.getModel()[14]
        };
        float cubeDistance = (float) Math.sqrt(
                (circleVector[0]*circleVector[0]) +
                (circleVector[1]*circleVector[1]) +
                (circleVector[2]*circleVector[2])
        );
        if(cubeDistance<0.7) {
        	currentList.remove(end);
        	return;
        }
        currentList.remove(start);

        float[] cCoord3 = {0,cubeDistance,0};
        float[] resultVector = new float[3];
        float[] resultVector2 = new float[3];
        world.mvMult(resultVector, h, cCoord3);
        GLSelectableObject cube4 = getNewObject(world.cubeCoords);
        cube4.getModel()[12] = cube1.getModel()[12]+ resultVector[0];
        cube4.getModel()[13] = cube1.getModel()[13]+ resultVector[1];
        cube4.getModel()[14] = cube1.getModel()[14]+ resultVector[2];
        currentList.add(cube4);

        float[] cCoord4 = {0,-cubeDistance,0};
        world.mvMult(resultVector, h, cCoord4);
        GLSelectableObject cube5 = getNewObject(world.cubeCoords);
        cube5.getModel()[12] = cube1.getModel()[12]+ resultVector[0];
        cube5.getModel()[13] = cube1.getModel()[13]+ resultVector[1];
        cube5.getModel()[14] = cube1.getModel()[14]+ resultVector[2];
        currentList.add(cube5);

        float[] cCoord = {cubeDistance,0,0};
        world.mvMult(resultVector, h, cCoord);
        cube2.getModel()[12] = cube1.getModel()[12]+ resultVector[0];
        cube2.getModel()[13] = cube1.getModel()[13]+ resultVector[1];
        cube2.getModel()[14] = cube1.getModel()[14]+ resultVector[2];
        currentList.add(cube2);


        float[] cCoord2 = {-cubeDistance,0,0};
        world.mvMult(resultVector2, h, cCoord2);
        GLSelectableObject cube3 = getNewObject(world.cubeCoords);
        cube3.getModel()[12] = cube1.getModel()[12]+ resultVector2[0];
        cube3.getModel()[13] = cube1.getModel()[13]+ resultVector2[1];
        cube3.getModel()[14] = cube1.getModel()[14]+ resultVector2[2];
        currentList.add(cube3);
        float[] origin = {cube1.getModel()[12],cube1.getModel()[13],cube1.getModel()[14]};

        createArc2(cCoord, cCoord3, cCoord, origin, hView, cubeDistance);
    }
    
    private void createArc2(float[] cube1, float[] cube2, float[] start, float[] o, float[] hVZ, float radiusZ){
        double cubeDistance = Math.sqrt(
                ((cube1[0] - cube2[0])*(cube1[0] - cube2[0])) +
                ((cube1[1] - cube2[1])*(cube1[1] - cube2[1])) +
                ((cube1[2] - cube2[2])*(cube1[2] - cube2[2]))
        );
        if(cubeDistance < 2) return;
        float[] resultVector = new float[3];
        GLSelectableObject cubeNew;

        float dot = start[0] * cube1[0] + start[1] * cube1[1];
        float det = start[0] * cube1[1] - start[1] * cube1[0];
        double angle1 = Math.atan2(det,dot);
        dot = start[0] * cube2[0] + start[1] * cube2[1];
        det = start[0] * cube2[1] - start[1] * cube2[0];
        double angle2 = Math.atan2(det,dot);
        double angle = (angle1+angle2)/2;

        Log.i(OpenGlStuff.TAG, "Angle: " + angle);
        float[] mid = {(float)Math.cos(angle)*-radiusZ, (float)Math.sin(angle)*-radiusZ, 0};
        world.mvMult(resultVector, hVZ, mid);
        cubeNew = getNewObject(0,0,0);
        cubeNew.getModel()[12] = o[0]+ resultVector[0];
        cubeNew.getModel()[13] = o[1]+ resultVector[1];
        cubeNew.getModel()[14] = o[2]+ resultVector[2];
        currentList.add(cubeNew);

        mid[0] = -1*mid[0];
        world.mvMult(resultVector, hVZ, mid);
        cubeNew = getNewObject(0,0,0);
        cubeNew.getModel()[12] = o[0]+ resultVector[0];
        cubeNew.getModel()[13] = o[1]+ resultVector[1];
        cubeNew.getModel()[14] = o[2]+ resultVector[2];
        currentList.add(cubeNew);

        mid[0] = -1*mid[0];
        mid[1] = -1*mid[1];
        world.mvMult(resultVector, hVZ, mid);
        cubeNew = getNewObject(0,0,0);
        cubeNew.getModel()[12] = o[0]+ resultVector[0];
        cubeNew.getModel()[13] = o[1]+ resultVector[1];
        cubeNew.getModel()[14] = o[2]+ resultVector[2];
        currentList.add(cubeNew);

        mid[0] = -1*mid[0];
        world.mvMult(resultVector, hVZ, mid);
        cubeNew = getNewObject(0,0,0);
        cubeNew.getModel()[12] = o[0]+ resultVector[0];
        cubeNew.getModel()[13] = o[1]+ resultVector[1];
        cubeNew.getModel()[14] = o[2]+ resultVector[2];
        currentList.add(cubeNew);

        createArc2(cube1,mid,start,o,hVZ, radiusZ);
        createArc2(mid,cube2,start,o,hVZ, radiusZ);
    }
	
}
