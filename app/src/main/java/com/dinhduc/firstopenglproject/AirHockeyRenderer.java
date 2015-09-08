package com.dinhduc.firstopenglproject;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.dinhduc.firstopenglproject.util.LoggerConfig;
import com.dinhduc.firstopenglproject.util.ShaderHelper;
import com.dinhduc.firstopenglproject.util.TextResouceReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.perspectiveM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by Nguyen Dinh Duc on 8/23/2015.
 */
public class AirHockeyRenderer implements Renderer {
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData;
    private int program;
    Context context;
    public static final String A_POSITION = "a_Position";
    public static final String A_COLOR = "a_Color";
    public static final String U_MATRIX = "u_Matrix";
    public static final int COLOR_COMPONENT_COUNT = 3;
    public static final int POSITION_COMPONENT_COUNT = 2;
    public static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aPositionLocation;
    private int aColorLocation;
    private int uMatrixLocation;
    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];

    public AirHockeyRenderer(Context context) {
        this.context = context;
        float[] tableVertices = {
                0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
                -0.5f, -0.8f, 0.6f, 0.6f, 0.6f,
                0.5f, -0.8f, 0.6f, 0.6f, 0.6f,
                0.5f, 0.8f, 0.6f, 0.6f, 0.6f,
                -0.5f, 0.8f, 0.6f, 0.6f, 0.6f,
                -0.5f, -0.8f, 0.6f, 0.6f, 0.6f,
                //line 1
                -0.5f, 0f, 1f, 0f, 0f,
                0.5f, 0f, 1f, 0f, 0f,
                //mallets
                0f, 0.4f, 0f, 0f, 1f,
                0f, -0.4f, 1f, 0f, 0f,
        };
        vertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        vertexData.put(tableVertices);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0, 0, 0, 0);
        String vertexFileResource = TextResouceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragFileResource = TextResouceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);
        int vertexShader = ShaderHelper.compileVertexShader(vertexFileResource);
        int fragShader = ShaderHelper.compileFragmentShader(fragFileResource);
        program = ShaderHelper.linkProgram(vertexShader, fragShader);
        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }
        glUseProgram(program);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);
        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0, 0, width, height);
        perspectiveM(projectionMatrix, 0, 60, (float) width / (float) height, 1f, 10f);
        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0, 0, -2f);
        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        final float[] temp = new float[16];
        multiplyMM(temp, 0, projectionMatrix, 0, modelMatrix, 0);
        System.arraycopy(temp, 0, projectionMatrix, 0, 16);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
        glDrawArrays(GL_LINES, 6, 2);
        glDrawArrays(GL_POINTS, 8, 1);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}
