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

import edu.washburn.vrtoolkit.cardboard.vrpaint.tools.OpenGlControl.Tools;

public class OpenGlStuff {
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
    public Tools currentTool = Tools.NOT_DRAWING;

    private GLObject floor = new GLObject(0f, 20f, 0f);
    public List<GLSelectableObject> cubes = new ArrayList<GLSelectableObject>();

    public float[] camera = new float[16];
    public float[] view = new float[16];
    public float[] headView = new float[16];
    public float[] modelViewProjection = new float[16];
    public float[] modelView = new float[16];
    private float[] centerZ = {0,0,0};
    private float[] lookingZ = {Eyes[0],Eyes[1],CAMERA_Z};
    private float[] locationZ = new float[3];

    private static final double GRAVITY = 0.25;
    private boolean isFalling = false;

    private MainActivity main;

    public OpenGlStuff(MainActivity main) {
        this.main = main;
        currentTool.getTool().register(this);
    }

    public void processButtonStart(boolean pressed){
    	if(!currentTool.getTool().processButtonStart(pressed)){
    		
    	}
    }

    public void processButtonSelect(boolean pressed){
    	if(!currentTool.getTool().processButtonSelect(pressed)){
    		
    	}
    }

    public void processButtonX(boolean pressed){
    	if(!currentTool.getTool().processButtonX(pressed)){
	        selectMode(0);
	        main.getOverlayView().show3DToast("No-Drawing Mode");
    	}
    }


    public void processButtonY(boolean pressed){
    	if(!currentTool.getTool().processButtonY(pressed)){
    		
    	}
    }

    public void processButtonA(boolean pressed){
    	if(!currentTool.getTool().processButtonA(pressed)){
	    	 if(currentTool.ordinal() != Tools.NOT_DRAWING.ordinal()) {
	    		 drawing = pressed;
	         } else {
	        	 main.getOverlayView().show3DToast("Nothing to Draw!");
	         }
    	}
    }

    public void processButtonB(boolean pressed){
    	if(!currentTool.getTool().processButtonB(pressed)){
            isFalling = !isFalling;
    	}
    }

    public void processButtonR1(boolean pressed){
    	if(!currentTool.getTool().processButtonR1(pressed)){
    		
    	}
    }

    public void processButtonL1(boolean pressed){
    	if(!currentTool.getTool().processButtonL1(pressed)){
    		
    	}
    }

    public void processButtonR2(boolean pressed){
    	if(!currentTool.getTool().processButtonR2(pressed)){
    		r2Pressed = pressed;
    	}
    }

    public void processButtonL2(boolean pressed){
    	if(!currentTool.getTool().processButtonL2(pressed)){
    		l2Pressed = pressed;
    	}
    }

    public void processButtonR3(boolean pressed){
    	if(!currentTool.getTool().processButtonR3(pressed)){
	        centerCursor();
	    }
    }

    public void processButtonL3(boolean pressed){
    	if(!currentTool.getTool().processButtonL3(pressed)){
    		centerUser();
    	}
    }

    public void processLeftStick(float x, float y){
    	if(!currentTool.getTool().processLeftStick(x, y)){
        	moveUser(x,0,y);
    	}
    }

    public void processRightStick(float x, float y){
    	if(!currentTool.getTool().processRightStick(x, y)){
	        if(Math.abs(cubeCoords[0]+ x)<15){
	            cubeCoords[0]= cubeCoords[0] + x;
	        }
	        if(Math.abs(cubeCoords[1]- y)<15){
	            cubeCoords[1]= cubeCoords[1] - y;
	        }
    		
    	}
    }

    public void processDpad(float x, float y){
    	if(!currentTool.getTool().processDpad(x, y)){
    		moveUser(x, -y,0);
    	}
    }

    public void processTriggers(float l, float r){
    	if(!currentTool.getTool().processTriggers(l, r)){
    		
    	}
    }
    
    public void centerUser(){
    	centerZ[0] = 0.0f;
    	centerZ[1] = 0.0f;
    	centerZ[2] = 0.0f;
    	lookingZ[0] = 0.0f;
    	lookingZ[1] = 0.0f;
    	lookingZ[2] = 0.1f;
    }
    
    public void centerCursor(){
    	cubeCoords[0] = 0;
    	cubeCoords[1] = 0;
    }

    public void moveUser(float xZ, float yZ, float zZ) {
    	float[] resultVector = {xZ, yZ, zZ};
    	mvMult(resultVector, headView, resultVector);
    	centerZ[0] = centerZ[0] + resultVector[0];
    	centerZ[1] = centerZ[1] + resultVector[1];
    	centerZ[2] = centerZ[2] + resultVector[2];
    	lookingZ[0] = lookingZ[0] + resultVector[0];
    	lookingZ[1] = lookingZ[1] + resultVector[1];
    	lookingZ[2] = lookingZ[2] + resultVector[2];
    }
    
    public void moveCursor(double i, double j, double k) {
    	double limitZ = cubeCoords[2] / Math.sqrt(3);
    	if (Math.abs(cubeCoords[0] + (float) i) < Math.abs(limitZ)) {
    		cubeCoords[0] = cubeCoords[0] + (float) i;
    	}
    	if (Math.abs(cubeCoords[1] - (float) j) < Math.abs(limitZ)) {
    		cubeCoords[1] = cubeCoords[1] - (float) j;
    	}
    	if (((cubeCoords[2] + (float) k) <= 80) || ((cubeCoords[2] - (float) k) <= 1)) {
    		cubeCoords[2] = cubeCoords[2] + (float) k;
    		//System.out.println("Zcoord: " + cubeCoords[2]);
    	}
    }

    public void selectMode(int drawingModeInt){
    	if(drawingModeInt >= 0 && drawingModeInt < Tools.values().length)
    		currentTool = Tools.values()[drawingModeInt];
    	else 
    		main.getOverlayView().show3DToast("Error selecting mode, please choose a value between 0 and " + (Tools.values().length-1));
    }
    
    public void placeObjectInfrontOfCamera(GLObject moveObject) {
        float[] resultVector = new float[3];
        mvMult(resultVector, headView, cubeCoords);
        moveObject.getModel()[12] = resultVector[0] + -camera[12];
        moveObject.getModel()[13] = resultVector[1] + -camera[13];
        moveObject.getModel()[14] = resultVector[2] + -camera[14];
    }
//    public void placeObjectInfrontOfCamera(GLObject moveObject) {
//        mvMult(moveObject.getModel(), 12, headView, cubeCoords);
//    }

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
        // Build the camera matrix and apply it to the ModelView.
        Matrix.setLookAtM(camera, 0, lookingZ[0], lookingZ[1], lookingZ[2], centerZ[0], centerZ[1], centerZ[2], 0.0f, 1.0f, 0.0f);

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

        currentTool.getTool().onNewFrame(headTransform);

        if(isFalling){
            for(GLSelectableObject cube : cubes){
                cube.velocity += GRAVITY;
                cube.getModel()[13] -= cube.velocity;
                if(cube.getModel()[13] <= -19){
                    cube.velocity *= -((Math.random()*0.2)+0.45);
                    cube.getModel()[13] = -19;
                    double rand = randInterval(-cube.velocity*2,cube.velocity*2);
                    cube.getModel()[12] -= rand;
                    cube.getModel()[14] -= rand;
                }
            }
        }

        checkGLError("onReadyToDraw");
    }

    private double randInterval(double min,double max)
    {
        return Math.floor(Math.random()*(max-min+1)+min);
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

        // Set modelView for the floor, so we draw floor in the correct location
        Matrix.multiplyMM(modelView, 0, view, 0, floor.getModel(), 0);
        Matrix.multiplyMM(modelViewProjection, 0, perspective, 0, modelView, 0);
        floor.drawFloor();
        currentTool.getTool().onDrawEye(eye);
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
		public int getProgram() {
			return program;
		}
		public void setProgram(int program) {
			this.program = program;
		}
    }

    public class GLSelectableObject extends GLObject {

        private FloatBuffer cubeFoundColors;

        public double velocity = 0;

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

	public MainActivity getMain() {
		return main;
	}

	public void setMain(MainActivity main) {
		this.main = main;
	}
}
