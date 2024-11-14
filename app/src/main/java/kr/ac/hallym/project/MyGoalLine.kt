package kr.ac.hallym.project

import android.content.Context
import android.opengl.GLES30
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.ShortBuffer

class MyGoalLine(myContext: Context) {
    private val vertexCoords = floatArrayOf(
        -0.25f,  -0.25f, -0.25f,
        -0.25f, -0.5f, -0.25f,
        0.25f, -0.5f, -0.25f,
        0.25f,  -0.25f, -0.25f,
        -0.25f,  -0.25f, 0.25f,
        -0.25f, -0.5f, 0.25f,
        0.25f, -0.5f, 0.25f,
        0.25f,  -0.25f, 0.25f
    )

    private val drawOrder = shortArrayOf(
        0, 3, 2, 0, 2, 1,
        2, 3, 7, 2, 7, 6,
        1, 2, 6, 1, 6, 5,
        4, 0, 1, 4, 1, 5,
        3, 0, 4, 3, 4, 7,
        5, 6, 7, 5, 7, 4
    )

    private var vertexBuffer: FloatBuffer =
        ByteBuffer.allocateDirect(vertexCoords.size * 4).run {
            order(ByteOrder.nativeOrder())
            asFloatBuffer().apply {
                put(vertexCoords)
                position(0)
            }
        }
    private val indexBuffer: ShortBuffer =
        ByteBuffer.allocateDirect(drawOrder.size * 2).run {
            order(ByteOrder.nativeOrder())
            asShortBuffer().apply {
                put(drawOrder)
                position(0)
            }
        }

    private val color = floatArrayOf(1.0f, 1.0f, 1.0f, 1.0f)

    private var mProgram: Int = -1

    private var mColorHandle = -1


    private var mvpMatrixHandle = -1
    private var mWorldMatHandle = -1

    private val vertexStride = COORDS_PER_VERTEX * 4

    init {
        val vertexShader: Int =
            loadShader(GLES30.GL_VERTEX_SHADER, "goalLine_vert.glsl", myContext)
        val fragmentShader: Int =
            loadShader(GLES30.GL_FRAGMENT_SHADER, "goalLine_frag.glsl", myContext)

        mProgram = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
        }

        GLES30.glUseProgram(mProgram)

        GLES30.glEnableVertexAttribArray(7)

        GLES30.glVertexAttribPointer(
            7,
            COORDS_PER_VERTEX,
            GLES30.GL_FLOAT,
            false,
            vertexStride,
            vertexBuffer
        )

        mColorHandle = GLES30.glGetUniformLocation(mProgram, "fColor").also {
            GLES30.glUniform4fv(it, 1, color, 0)
        }


        mvpMatrixHandle = GLES30.glGetUniformLocation(mProgram, "uMVPMatrix")
        mWorldMatHandle = GLES30.glGetUniformLocation(mProgram, "worldMat")
    }

    fun draw(mvpMatrix: FloatArray, worldMat: FloatArray){
        GLES30.glUseProgram(mProgram)

        GLES30.glUniformMatrix4fv(mvpMatrixHandle,1,false, mvpMatrix,0)
        GLES30.glUniformMatrix4fv(mWorldMatHandle,1,false, worldMat,0)


        GLES30.glDrawElements(GLES30.GL_TRIANGLES, drawOrder.size, GLES30.GL_UNSIGNED_SHORT, indexBuffer)
    }
}