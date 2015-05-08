package edu.washburn.vrtoolkit.cardboard.vrpaint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLObject{
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
    public void drawFloor(float[] lightPosInEyeSpace, float[] modelView, float[] modelViewProjection) {
        GLES20.glUseProgram(program);

        // Set ModelView, MVP, position, normals, and color.
        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);
        GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);
        GLES20.glVertexAttribPointer(positionParam, OpenGlStuff.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, colors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 6);

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

//        checkGLError("Floor program");

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

//        checkGLError("Floor program params");
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
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + colorParam;
		result = prime * result + ((colors == null) ? 0 : colors.hashCode());
		result = prime * result + lightPosParam;
		result = prime * result + Arrays.hashCode(model);
		result = prime * result + modelParam;
		result = prime * result + modelViewParam;
		result = prime * result + modelViewProjectionParam;
		result = prime * result + normalParam;
		result = prime * result + ((normals == null) ? 0 : normals.hashCode());
		result = prime * result + positionParam;
		result = prime * result + program;
		result = prime * result + ((vertices == null) ? 0 : vertices.hashCode());
		result = prime * result + Float.floatToIntBits(xPos);
		result = prime * result + Float.floatToIntBits(yPos);
		result = prime * result + Float.floatToIntBits(zPos);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GLObject other = (GLObject) obj;
		if (colorParam != other.colorParam)
			return false;
		if (colors == null) {
			if (other.colors != null)
				return false;
		} else if (!colors.equals(other.colors))
			return false;
		if (lightPosParam != other.lightPosParam)
			return false;
		if (!Arrays.equals(model, other.model))
			return false;
		if (modelParam != other.modelParam)
			return false;
		if (modelViewParam != other.modelViewParam)
			return false;
		if (modelViewProjectionParam != other.modelViewProjectionParam)
			return false;
		if (normalParam != other.normalParam)
			return false;
		if (normals == null) {
			if (other.normals != null)
				return false;
		} else if (!normals.equals(other.normals))
			return false;
		if (positionParam != other.positionParam)
			return false;
		if (program != other.program)
			return false;
		if (vertices == null) {
			if (other.vertices != null)
				return false;
		} else if (!vertices.equals(other.vertices))
			return false;
		if (Float.floatToIntBits(xPos) != Float.floatToIntBits(other.xPos))
			return false;
		if (Float.floatToIntBits(yPos) != Float.floatToIntBits(other.yPos))
			return false;
		if (Float.floatToIntBits(zPos) != Float.floatToIntBits(other.zPos))
			return false;
		return true;
	}
}
