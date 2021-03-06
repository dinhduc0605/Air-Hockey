package com.dinhduc.firstopenglproject.data;

import android.opengl.GLES20;

import com.dinhduc.firstopenglproject.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.*;
import static com.dinhduc.firstopenglproject.Constants.BYTE_PER_FLOAT;

/**
 * Created by Nguyen Dinh Duc on 9/4/2015.
 */
public class VertextArray {
    private final FloatBuffer floatBuffer;

    public VertextArray(float[] vertextData) {
        floatBuffer = ByteBuffer
                .allocate(vertextData.length * BYTE_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertextData);
    }

    public void setVertexAttriPointer(int dataOffset, int attributeLocation, int componentCount, int stride) {
        floatBuffer.position(dataOffset);
        glVertexAttribPointer(attributeLocation, componentCount, GL_FLOAT, false, stride, floatBuffer);
        glEnableVertexAttribArray(attributeLocation);
        floatBuffer.position(0);
    }
}
