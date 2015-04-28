package edu.washburn.vrtoolkit.cardboard.vrpaint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.google.vrtoolkit.cardboard.Eye;
import com.google.vrtoolkit.cardboard.HeadTransform;
import com.google.vrtoolkit.cardboard.samples.treasurehunt.R;

import edu.washburn.vrtoolkit.cardboard.vrpaint.tools.OpenGlControl;
import edu.washburn.vrtoolkit.cardboard.vrpaint.tools.OpenGlControl.Tools;

public class OpenGlStuff {
    private static final int NOT_DRAWING = 0;
    private static final int LINE = 2;
    private static final int CIRCLE = 3;
    private static final int CIRCLE_END = 31;
    private static final int POLYGON = 4;
    public static final int LINE_END = 20;
    public static final int POLYGON_MID = 40;
    private static final int POLYGON_END = 41;
    public static final String TAG = "MainActivity";

    public static final float Z_NEAR = 0.1f;
    public static final float Z_FAR = 100.0f;
    private static final float CAMERA_Z = 0.01f;
    private static final float TIME_DELTA = 0.3f;
    private static final float YAW_LIMIT = 0.05f;
    private static final float PITCH_LIMIT = 0.05f;
    private static final int COORDS_PER_VERTEX = 3;


    // We keep the light always position just above the user.
    private static final float[] LIGHT_POS_IN_WORLD_SPACE = new float[] { 0.0f, 2.0f, 0.0f, 1.0f };
    private float[] Eyes = new float[] { 0f, 0f };
    private final float[] lightPosInEyeSpace = new float[4];
    public float[] cubeCoords = {0f,0f,-20f};

    public int vertexShader;
    public int gridShader;
    public int passthroughShader;
    public boolean drawing = false;
    public boolean l2Pressed= false;
    public boolean r2Pressed = false;
    public int drawingMode = NOT_DRAWING;
    public Tools currentTool = Tools.NOT_DRAWING;
    private int sDMode;

    private GLObject floor = new GLObject(0f, 20f, 0f);
    public List<GLSelectableObject> cubes = new ArrayList<GLSelectableObject>();
    public GLSelectableObject currentNew = new GLSelectableObject(cubeCoords);

    public GLSelectableObject currentOlder = new GLSelectableObject(0,0,0);
    public GLSelectableObject currentOld = new GLSelectableObject(0,0,0);
    public GLSelectableObject currentOldJR;

    public float[] camera = new float[16];
    public float[] view = new float[16];
    public float[] headView = new float[16];
    public float[] modelViewProjection = new float[16];
    public float[] modelView = new float[16];

    private MainActivity main;

    public OpenGlStuff(MainActivity main) {
        this.main = main;
        currentTool.getTool().register(this);
//        for(Tools tool : Tools.values()){
//        	tool.getTool().register(this);
//        }
    }

    public void processButtonStart(boolean pressed){
    	currentTool.getTool().processButtonStart(pressed);
    }

    public void processButtonSelect(boolean pressed){
    	currentTool.getTool().processButtonSelect(pressed);
    }

    public void processButtonX(boolean pressed){
    	currentTool.getTool().processButtonX(pressed);
        selectMode(0);
        main.getOverlayView().show3DToast("No-Drawing Mode");
    }

    public void processButtonY(boolean pressed){
    	currentTool.getTool().processButtonY(pressed);
    }

    public void processButtonA(boolean pressed){
    	currentTool.getTool().processButtonA(pressed);
    	 if(currentTool.ordinal() != Tools.NOT_DRAWING.ordinal()) {
    		 drawing = pressed;
         } else {
        	 main.getOverlayView().show3DToast("Nothing to Draw!");
         }
    }

    public void processButtonB(boolean pressed){
    	currentTool.getTool().processButtonB(pressed);
    }

    public void processButtonR1(boolean pressed){
    	currentTool.getTool().processButtonR1(pressed);
//    	if(pressed){
//	        drawingMode = (drawingMode+1)%5;
//	        currentTool = currentTool.next();
//	        currentTool.getTool().register(this);
//	        toastMode(currentTool.ordinal());
//    	}
    }

    public void processButtonL1(boolean pressed){
    	currentTool.getTool().processButtonL1(pressed);
//    	if(pressed){
//        	drawingMode = (drawingMode+4)%5;
//	        currentTool = currentTool.previous();
//	        currentTool.getTool().register(this);
//	        toastMode(currentTool.ordinal());
//    	}
    }

    public void processButtonR2(boolean pressed){
    	currentTool.getTool().processButtonR2(pressed);
    	r2Pressed = pressed;
    }

    public void processButtonL2(boolean pressed){
    	currentTool.getTool().processButtonL2(pressed);
    	l2Pressed = pressed;
    }

    public void processButtonR3(boolean pressed){
    	currentTool.getTool().processButtonR3(pressed);
    }

    public void processButtonL3(boolean pressed){
    	currentTool.getTool().processButtonL3(pressed);
    	centerCursor();
    }

    public void processLeftStick(float x, float y){
        if(Math.abs(cubeCoords[0]+ x)<15){
            cubeCoords[0]= cubeCoords[0] + x;
        }
        if(Math.abs(cubeCoords[1]- y)<15){
            cubeCoords[1]= cubeCoords[1] - y;
        }
    }

    public void processRightStick(float x, float y){
    }

    public void processDpad(float x, float y){
    }

    public void processTriggers(float l, float r){
    }
    
    public void centerCursor(){
    	cubeCoords[0] = 0;
    	cubeCoords[1] = 0;
    }
    
    public void createObject(){
    	double cubeDistance = Math.sqrt(
    			((currentOld.getModel()[12] - currentNew.getModel()[12])*(currentOld.getModel()[12] - currentNew.getModel()[12])) +
    			((currentOld.getModel()[13] - currentNew.getModel()[13])*(currentOld.getModel()[13] - currentNew.getModel()[13])) +
    			((currentOld.getModel()[14] - currentNew.getModel()[14])*(currentOld.getModel()[14] - currentNew.getModel()[14]))
    			);
    	if(cubeDistance < 1) return;
    	placeObjectInfrontOfCamera(currentNew);
    	currentOld = currentNew;
    	cubes.add(currentOld);
    	
    	currentNew = new GLSelectableObject(cubeCoords);
    	
    	currentNew.onSurfaceCreated(vertexShader, gridShader, passthroughShader);
    }

    public void toastMode(int i){
    	String toastString;
    	switch(i){
    	case 0:
    		toastString = i+" No-Drawing Mode";
    		break;
    	case 1:
    		toastString = i+" Free Draw!";
    		break;
    	case 2:
    		toastString = i+" Straight Lines!";
    		break;
    	case 3:
    		toastString = i+" Circles!";
    		break;
    	case 4:
    		toastString = i+" Polygons!";
    		break;
    	default:
    		toastString = i+" error occured";
    		break;
    	}
        main.getOverlayView().show3DToast(toastString);
    }

    public void selectMode(int drawingModeInt){
    	if(drawingModeInt >= 0 && drawingModeInt < Tools.values().length)
    		currentTool = Tools.values()[drawingModeInt];
//    	else 
//    		main.getOverlayView().show3DToast("Error selecting mode, please choose a value between 0 and " + (Tools.values().length-1));
        if(drawingMode==POLYGON_MID){
            sDMode = drawingModeInt;
            drawingMode = POLYGON_END;
            drawing = true;
            return;
        }
        drawing = false;
        drawingMode = drawingModeInt;
    }

    public void createLine(int test)
    {
        if(test==1) {
            placeObjectInfrontOfCamera(currentNew);
            currentOld = currentNew;
            currentOlder = currentOld;
            cubes.add(currentOld);

            currentNew = new GLSelectableObject(cubeCoords);
            currentNew.onSurfaceCreated(vertexShader, passthroughShader, passthroughShader);
            drawingMode = LINE_END;
            return;
        }
        if(test==2){

            placeObjectInfrontOfCamera(currentNew);
            currentOldJR = currentNew;
            cubes.add(currentOldJR);

            currentNew = new GLSelectableObject(cubeCoords);

            currentNew.onSurfaceCreated(vertexShader, passthroughShader, passthroughShader);
            createLine(currentOld, currentOldJR);
            if(drawingMode==POLYGON_MID){
                currentOld = currentOldJR;
            }
            return;
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

        GLSelectableObject currentMid = new GLSelectableObject(-mX,-mY,-mZ);
        currentMid.onSurfaceCreated(vertexShader, passthroughShader, passthroughShader);
        cubes.add(currentMid);
        createLine(cube1,currentMid);
        createLine(cube2,currentMid);
        return;
    }

    public void placeObjectInfrontOfCamera(GLObject moveObject) {
        mvMult(moveObject.getModel(), 12, headView, cubeCoords);
    }

    /**
     * Converts a raw text file, saved as a resource, into an OpenGL ES shader.
     *
     * @param type
     *            The type of shader we will be creating.
     * @param resId
     *            The resource ID of the raw text file about to be turned into a
     *            shader.
     * @return The shader object handler.
     */
    private int loadGLShader(int type, int resId) {
        String code = readRawTextFile(resId);
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, code);
        GLES20.glCompileShader(shader);

        // Get the compilation status.
        final int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);

        // If the compilation failed, delete the shader.
        if (compileStatus[0] == 0) {
            Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shader));
            GLES20.glDeleteShader(shader);
            shader = 0;
        }

        if (shader == 0) {
            throw new RuntimeException("Error creating shader.");
        }

        return shader;
    }

    /**
     * Converts a raw text file into a string.
     *
     * @param resId
     *            The resource ID of the raw text file about to be turned into a
     *            shader.
     * @return The context of the text file, or null in case of error.
     */
    private String readRawTextFile(int resId) {
        InputStream inputStream = main.getResources().openRawResource(resId);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Creates the buffers we use to store information about the 3D world.
     *
     * <p>
     * OpenGL doesn't use Java arrays, but rather needs data in a format it can
     * understand. Hence we use ByteBuffers.
     *
     * @param config
     *            The EGL configuration used when creating the surface.
     */
    public void onSurfaceCreated(EGLConfig config) {
        Log.i(TAG, "onSurfaceCreated");
        GLES20.glClearColor(0.8f, 0.8f, 0.8f, 0.5f); // Dark background so text
        // shows up well.

        vertexShader = loadGLShader(GLES20.GL_VERTEX_SHADER, R.raw.light_vertex);
        gridShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.grid_fragment);
        passthroughShader = loadGLShader(GLES20.GL_FRAGMENT_SHADER, R.raw.passthrough_fragment);


        currentNew = new GLSelectableObject(0f, 0f, 20f);

        currentNew.onSurfaceCreated(vertexShader, gridShader, passthroughShader);

        for (GLSelectableObject cube : cubes) {
            cube.onSurfaceCreated(vertexShader, gridShader, passthroughShader);
        }

        floor.onSurfaceCreated(vertexShader, gridShader, passthroughShader);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        checkGLError("onSurfaceCreated");
    }

    /**
     * Checks if we've had an error inside of OpenGL ES, and if so what that
     * error is.
     *
     * @param label
     *            Label to report in case of error.
     */
    private static void checkGLError(String label) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(OpenGlStuff.TAG, label + ": glError " + error);
            throw new RuntimeException(label + ": glError " + error);
        }
    }

    public void printMatrix(float[] matrix) {
        for (int i = 0; i < 4; i++)
            Log.i("TEST", "|" + String.format("%.3f", matrix[i * 4]) + "|" + "" + String.format("%.3f", matrix[i * 4 + 1]) + "|" + "" + String.format("%.3f", matrix[i * 4 + 2])
                    + "|" + "" + String.format("%.3f", matrix[i * 4 + 3]) + "|");
        Log.i("TEST", "End");
    }

    public void mvMult(float a[], float b[], float c[]) {
        a[0] = b[0] * c[0] + b[1] * c[1] + b[2] * c[2];
        a[1] = b[4] * c[0] + b[5] * c[1] + b[6] * c[2];
        a[2] = b[8] * c[0] + b[9] * c[1] + b[10] * c[2];
    }

    public void mvMult(float a[], int offset, float b[], float c[]) {
        a[offset    ] = b[0] * c[0] + b[1] * c[1] + b[2 ] * c[2];
        a[offset + 1] = b[4] * c[0] + b[5] * c[1] + b[6 ] * c[2];
        a[offset + 2] = b[8] * c[0] + b[9] * c[1] + b[10] * c[2];
    }


    public void onNewFrame(HeadTransform headTransform) {
        headTransform.getHeadView(headView, 0);

        if(l2Pressed){
            if(((cubeCoords[2]-1)<=80)||((cubeCoords[2]+1)<=0)){
                cubeCoords[2] = cubeCoords[2] - 1;
            }
        }
        if(r2Pressed){
        	if(((cubeCoords[2]+1)<=80)||((cubeCoords[2]-1)<=0)){
                cubeCoords[2] = cubeCoords[2] + 1;
            }
        }

        if(currentTool.ordinal() == Tools.FREE_DRAWING.ordinal() || currentTool.ordinal() == Tools.LINE.ordinal() || currentTool.ordinal() == Tools.CIRCLE.ordinal()){
            currentTool.getTool().onNewFrame(headTransform);
        }
        if(drawingMode==POLYGON && drawing==true){
            createLine(1);
            drawingMode=POLYGON_MID;
        }
        if(drawingMode==POLYGON_MID && drawing==true){
            createLine(2);
            drawing = false;
        }
        if(drawingMode==POLYGON_END && drawing==true){
            createLine(currentOlder,currentOldJR);
            drawing = false;
            drawingMode = sDMode;
        }

        placeObjectInfrontOfCamera(currentNew);
        float[] uVector = new float[4];
        headTransform.getUpVector(uVector,0);

        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, lookingZ[0], lookingZ[1], lookingZ[2], centerZ[0], centerZ[1], centerZ[2], 0.0f, 1.0f, 0.0f);

        checkGLError("onReadyToDraw");
    }

    /**
     * Draws a frame for an eye.
     *
     * @param eye
     *            The eye to render. Includes all required transformations.
     */
    public void onDrawEye(Eye eye) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        checkGLError("colorParam");

        // Apply the eye transformation to the camera.
        Matrix.multiplyMM(view, 0, eye.getEyeView(), 0, camera, 0);

        // Set the position of the light
        Matrix.multiplyMV(lightPosInEyeSpace, 0, view, 0, LIGHT_POS_IN_WORLD_SPACE, 0);

        float[] perspective = eye.getPerspective(Z_NEAR, Z_FAR);
        for (GLSelectableObject cube : cubes) {
            // Build the ModelView and ModelViewProjection matrices
            // for calculating cube position and light.
            Matrix.multiplyMM(modelView, 0, view, 0, cube.getModel(), 0);
            Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
            cube.drawCube();
        }

        if(currentNew != null){
            Matrix.multiplyMM(modelView, 0, view, 0, currentNew.getModel(), 0);
            Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
            currentNew.drawCube();
        }

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, floor.getModel(), 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        floor.drawFloor();
        currentTool.getTool().onDrawEye(eye);
    }

    /**
     * Find a new random position for the object.
     *
     * <p>
     * We'll rotate it around the Y-axis so it's out of sight, and then up or
     * down by a little bit.
     */
    public void hideObject(GLSelectableObject cube) {
        float[] rotationMatrix = new float[16];
        float[] posVec = new float[4];

        // First rotate in XZ plane, between 90 and 270 deg away, and scale so
        // that we vary
        // the object's distance from the user.
        float angleXZ = (float) Math.random() * 180 + 90;
        Matrix.setRotateM(rotationMatrix, 0, angleXZ, 0f, 1f, 0f);
        float oldObjectDistance = cube.getDistance();
        cube.setDistance((float) Math.random() * 15 + 5);
        float objectScalingFactor = cube.getDistance() / oldObjectDistance;
        Matrix.scaleM(rotationMatrix, 0, objectScalingFactor, objectScalingFactor, objectScalingFactor);
        Matrix.multiplyMV(posVec, 0, rotationMatrix, 0, cube.getModel(), 12);

        // Now get the up or down angle, between -20 and 20 degrees.
        float angleY = (float) Math.random() * 80 - 40; // Angle in Y plane,
        // between -40 and 40.
        angleY = (float) Math.toRadians(angleY);
        float newY = (float) Math.tan(angleY) * cube.getDistance();

        Matrix.setIdentityM(cube.getModel(), 0);
        Matrix.translateM(cube.getModel(), 0, posVec[0], newY, posVec[2]);
    }
    public void hideObject2(GLSelectableObject cube){
        cubes.remove(cube);
    }

    /**
     * Check if user is looking at object by calculating where the object is in
     * eye-space.
     *
     * @return true if the user is looking at the object.
     */
    public GLSelectableObject isLookingAtObject() {
        for (GLSelectableObject cube : cubes) {
            if (isLookingAtObject(cube)) {
                return cube;
            }
        }
        return null;
    }

    /**
     * Check if user is looking at object by calculating where the object is in
     * eye-space.
     *
     * @return true if the user is looking at the object.
     */
    public boolean isLookingAtObject(GLSelectableObject cube) {
        float[] initVec = { 0, 0, 0, 1.0f };
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from
        // onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, cube.getModel(), 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);

        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        return Math.abs(pitch) < PITCH_LIMIT && Math.abs(yaw) < YAW_LIMIT;
    }

    public class GLObject {
        protected FloatBuffer vertices;
        protected FloatBuffer colors;
        protected FloatBuffer normals;

        protected int program;

        protected int positionParam;
        protected int normalParam;
        protected int colorParam;
        protected int modelParam;
        protected int modelViewParam;
        protected int modelViewProjectionParam;
        protected int lightPosParam;

        protected float[] model = new float[16];
        protected float xPos = 0f;
        protected float yPos = 0f;
        protected float zPos = 0f;

        public GLObject(float xPos, float yPos, float zPos) {
            this.xPos = xPos;
            this.yPos = yPos;
            this.zPos = zPos;
        }
        public GLObject(float[] pos) {
            this(pos[0], pos[1], pos[2]);
        }

        protected void stuff() {
            // make a floor
            ByteBuffer bbFloorVertices = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COORDS.length * 4);
            bbFloorVertices.order(ByteOrder.nativeOrder());
            vertices = bbFloorVertices.asFloatBuffer();
            vertices.put(WorldLayoutData.FLOOR_COORDS);
            vertices.position(0);

            ByteBuffer bbFloorNormals = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_NORMALS.length * 4);
            bbFloorNormals.order(ByteOrder.nativeOrder());
            normals = bbFloorNormals.asFloatBuffer();
            normals.put(WorldLayoutData.FLOOR_NORMALS);
            normals.position(0);

            ByteBuffer bbFloorColors = ByteBuffer.allocateDirect(WorldLayoutData.FLOOR_COLORS.length * 4);
            bbFloorColors.order(ByteOrder.nativeOrder());
            colors = bbFloorColors.asFloatBuffer();
            colors.put(WorldLayoutData.FLOOR_COLORS);
            colors.position(0);
        }

        /**
         * Draw the floor.
         *
         * <p>
         * This feeds in data for the floor into the shader. Note that this
         * doesn't feed in data about position of the light, so if we rewrite
         * our code to draw the floor first, the lighting might look strange.
         */
        public void drawFloor() {
            GLES20.glUseProgram(program);

            // Set ModelView, MVP, position, normals, and color.
            GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);
            GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);
            GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
            GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);
            GLES20.glVertexAttribPointer(positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);
            GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
            GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colors);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

            checkGLError("drawing floor");
        }

        /**
         * Creates the buffers we use to store information about the 3D world.
         *
         * <p>
         * OpenGL doesn't use Java arrays, but rather needs data in a format it
         * can understand. Hence we use ByteBuffers.
         *
         * @param //config
         *            The EGL configuration used when creating the surface.
         */
        public void onSurfaceCreated(int vertexShader, int gridShader, int passthroughShader) {
        	stuff();
            program = GLES20.glCreateProgram();
            GLES20.glAttachShader(program, vertexShader);
            if (this instanceof GLSelectableObject) {
                GLES20.glAttachShader(program, passthroughShader);
            } else {
                GLES20.glAttachShader(program, gridShader);
            }
            GLES20.glLinkProgram(program);
            GLES20.glUseProgram(program);

            checkGLError("Floor program");

            modelParam = GLES20.glGetUniformLocation(program, "u_Model");
            modelViewParam = GLES20.glGetUniformLocation(program, "u_MVMatrix");
            modelViewProjectionParam = GLES20.glGetUniformLocation(program, "u_MVP");
            lightPosParam = GLES20.glGetUniformLocation(program, "u_LightPos");

            positionParam = GLES20.glGetAttribLocation(program, "a_Position");
            normalParam = GLES20.glGetAttribLocation(program, "a_Normal");
            colorParam = GLES20.glGetAttribLocation(program, "a_Color");

            GLES20.glEnableVertexAttribArray(positionParam);
            GLES20.glEnableVertexAttribArray(normalParam);
            GLES20.glEnableVertexAttribArray(colorParam);

            checkGLError("Floor program params");
            Matrix.setIdentityM(model, 0);
            Matrix.translateM(model, 0, -xPos, -yPos, -zPos); // Floor appears
            // below user.

        }

        public float[] getModel() {
            return model;
        }

        public void setModel(float[] modelFloor) {
            for (int k = 0; k > modelFloor.length; k++)
                model[k] = modelFloor[k];
            // this.model = modelFloor;
        }

        public float getDistance() {
            return zPos;
        }

        public void setDistance(float distance) {
            this.zPos = distance;
        }
    }

    public class GLSelectableObject extends GLObject {

        private FloatBuffer cubeFoundColors;

        public GLSelectableObject(float xPos, float yPos, float zPos) {
            super(xPos, yPos, zPos);
        }
        
        public GLSelectableObject(float[] pos) {
            super(pos);
        }

        @Override
        protected void stuff() {
            ByteBuffer bbVertices = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COORDS.length * 4);
            bbVertices.order(ByteOrder.nativeOrder());
            vertices = bbVertices.asFloatBuffer();
            vertices.put(WorldLayoutData.CUBE_COORDS);
            vertices.position(0);

            ByteBuffer bbColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_COLORS.length * 4);
            bbColors.order(ByteOrder.nativeOrder());
            colors = bbColors.asFloatBuffer();
            colors.put(WorldLayoutData.CUBE_COLORS);
            colors.position(0);

            ByteBuffer bbFoundColors = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_FOUND_COLORS.length * 4);
            bbFoundColors.order(ByteOrder.nativeOrder());
            cubeFoundColors = bbFoundColors.asFloatBuffer();
            cubeFoundColors.put(WorldLayoutData.CUBE_FOUND_COLORS);
            cubeFoundColors.position(0);

            ByteBuffer bbNormals = ByteBuffer.allocateDirect(WorldLayoutData.CUBE_NORMALS.length * 4);
            bbNormals.order(ByteOrder.nativeOrder());
            normals = bbNormals.asFloatBuffer();
            normals.put(WorldLayoutData.CUBE_NORMALS);
            normals.position(0);
        }

        /**
         * Draw the cube.
         *
         * <p>
         * We've set all of our transformation matrices. Now we simply pass them
         * into the shader.
         */
        public void drawCube() {
            GLES20.glUseProgram(program);

            GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);

            // Set the Model in the shader, used to calculate lighting
            GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);

            // Set the ModelView in the shader, used to calculate lighting
            GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);

            // Set the position of the cube
            GLES20.glVertexAttribPointer(positionParam, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);

            // Set the ModelViewProjection matrix in the shader.
            GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

            // Set the normal positions of the cube, again for shading
            GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
            GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, isLookingAtObject(this) ? cubeFoundColors : colors);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
            checkGLError("Drawing cube");
        }
    }

	public void createNewObject() {
		currentNew = new GLSelectableObject(cubeCoords);
	}
}