package edu.washburn.vrtoolkit.cardboard.vrpaint;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

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
    public void drawCube(float[] lightPosInEyeSpace, float[] modelView, float[] headView, float[] modelViewProjection) {
        GLES20.glUseProgram(program);

        GLES20.glUniform3fv(lightPosParam, 1, lightPosInEyeSpace, 0);

        // Set the Model in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelParam, 1, false, model, 0);

        // Set the ModelView in the shader, used to calculate lighting
        GLES20.glUniformMatrix4fv(modelViewParam, 1, false, modelView, 0);

        // Set the position of the cube
        GLES20.glVertexAttribPointer(positionParam, OpenGlStuff.COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertices);

        // Set the ModelViewProjection matrix in the shader.
        GLES20.glUniformMatrix4fv(modelViewProjectionParam, 1, false, modelViewProjection, 0);

        // Set the normal positions of the cube, again for shading
        GLES20.glVertexAttribPointer(normalParam, 3, GLES20.GL_FLOAT, false, 0, normals);
        GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, isLookingAtObject(modelView, headView) ? colors : colors);
        //GLES20.glVertexAttribPointer(colorParam, 4, GLES20.GL_FLOAT, false, 0, isLookingAtObject(this) ? cubeFoundColors : colors);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
//        checkGLError("Drawing cube");
    }

    /**
     * Check if user is looking at object by calculating where the object is in
     * eye-space.
     *
     * @return true if the user is looking at the object.
     */
    public boolean isLookingAtObject(float[] modelView, float[] headView) {
        float[] initVec = { 0, 0, 0, 1.0f };
        float[] objPositionVec = new float[4];

        // Convert object space to camera space. Use the headView from
        // onNewFrame.
        Matrix.multiplyMM(modelView, 0, headView, 0, getModel(), 0);
        Matrix.multiplyMV(objPositionVec, 0, modelView, 0, initVec, 0);
        float pitch = (float) Math.atan2(objPositionVec[1], -objPositionVec[2]);
        float yaw = (float) Math.atan2(objPositionVec[0], -objPositionVec[2]);

        /*float pitch = (float) Math.atan2(cubeCoords[1], -cubeCoords[2]);
        float yaw = (float) Math.atan2(cubeCoords[0], -cubeCoords[2]);*/

        return Math.abs(pitch) < OpenGlStuff.PITCH_LIMIT && Math.abs(yaw) < OpenGlStuff.YAW_LIMIT;
    }
}