package kr.ac.hallym.project

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import java.io.BufferedInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

const val COORDS_PER_VERTEX = 3

val eyePos = floatArrayOf(0.0f, 3.0f, 15.0f)
val eyeAt = floatArrayOf(0.0f, 0.0f, 0.0f)
var cameraVec = floatArrayOf(0.0f, -0.7071f, -0.7071f)

var prevPosX = 3.0f
var prevPosZ = -7.0f
var outwardDir = true
var outwardDirZ = true
var objectPos = arrayOf(
    floatArrayOf(3.0f, 0.0f, -7.0f),floatArrayOf(-3.0f, 0.0f, -7.0f),
    floatArrayOf(3.0f, 0.0f, -5.0f),floatArrayOf(-3.0f, 0.0f, -5.0f),
    floatArrayOf(3.0f, 0.0f, -3.0f),floatArrayOf(-3.0f, 0.0f, -3.0f),
    floatArrayOf(3.0f, 0.0f, -1.0f),floatArrayOf(-3.0f, 0.0f, -1.0f),
    floatArrayOf(3.0f, 0.0f, 1.0f),floatArrayOf(-3.0f, 0.0f, 1.0f),
    floatArrayOf(3.0f, 0.0f, 3.0f),floatArrayOf(-3.0f, 0.0f, 3.0f),
    floatArrayOf(3.0f, 0.0f, 5.0f),floatArrayOf(-3.0f, 0.0f, 5.0f),
    floatArrayOf(3.0f, 0.0f, 7.0f),floatArrayOf(-3.0f, 0.0f, 7.0f),
    floatArrayOf(3.0f, 0.0f, 9.0f),floatArrayOf(-3.0f, 0.0f, 9.0f),
    floatArrayOf(0.0f, 0.0f, -7.0f),
    floatArrayOf(-5.0f, 0.0f, -7.0f),
    floatArrayOf(5.0f, 0.0f, -7.0f)
)
class MainGLRenderer(val context:Context):GLSurfaceView.Renderer {
    private lateinit var mGround: MyLitTexGround
    private lateinit var mCube:MyLitTexCube
    private lateinit var mArcball: MyArcball
    private lateinit var mHead:MyLitCube
    private lateinit var mWall: MyWall
    private lateinit var mGoalLine: MyGoalLine


    private var modelMatrix = FloatArray(16)
    private var viewMatrix = FloatArray(16)
    private var projectionMatrix = FloatArray(16)
    private var vpMatrix = FloatArray(16)
    private var mvpMatrix = floatArrayOf(
        1f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f,
        0f, 0f, 1f, 0f,
        0f, 0f, 0f, 1f
    )

    private var startTime = SystemClock.uptimeMillis()
    private var rotXAngle = 0f
    private var rotZAngle = 0f

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES30.glClearColor(0.53f, 0.81f, 0.92f, 1.0f)

        GLES30.glEnable(GLES30.GL_DEPTH_TEST)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.setIdentityM(viewMatrix, 0)
        Matrix.setIdentityM(projectionMatrix, 0)
        Matrix.setIdentityM(vpMatrix, 0)

        mGround = MyLitTexGround(context)
        mCube = MyLitTexCube(context)
        mArcball = MyArcball()
        mHead = MyLitCube(context)
        mWall = MyWall(context)
        mGoalLine = MyGoalLine(context)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES30.glViewport(0, 0, width, height)

        mArcball.resize(width,height)

        val ratio = width.toFloat() / height.toFloat()
        Matrix.perspectiveM(projectionMatrix, 0, 90f, ratio, 0.001f, 1000f)

        Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f, 1f, 0f)

        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT or GLES30.GL_DEPTH_BUFFER_BIT)

        eyeAt[0] = eyePos[0] + cameraVec[0]
        eyeAt[1] = eyePos[1] + cameraVec[1]
        eyeAt[2] = eyePos[2] + cameraVec[2]
        Matrix.setLookAtM(viewMatrix, 0, eyePos[0], eyePos[1], eyePos[2], eyeAt[0], eyeAt[1], eyeAt[2], 0f, 1f, 0f)
        Matrix.multiplyMM(vpMatrix, 0, projectionMatrix, 0, viewMatrix, 0)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, eyePos[0], 0f, eyePos[2])
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mHead.draw(mvpMatrix, modelMatrix)

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, mArcball.rotationMatrix, 0)
        mGround.draw(mvpMatrix, mArcball.rotationMatrix)

        for(x in -10 .. 10) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, x.toFloat(), -1.0f, -15.0f)
            Matrix.multiplyMM(modelMatrix, 0, mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mWall.draw(mvpMatrix, mArcball.rotationMatrix)
        }
        for(z in -15 .. 15) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, -10.0f, -1.0f, z.toFloat())
            Matrix.multiplyMM(modelMatrix, 0, mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mWall.draw(mvpMatrix, mArcball.rotationMatrix)
        }
        for(z in -15 .. 15) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, 10.0f, -1.0f, z.toFloat())
            Matrix.multiplyMM(modelMatrix, 0, mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mWall.draw(mvpMatrix, mArcball.rotationMatrix)
        }

        for(x in -15 .. 15) {
            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, x.toFloat(), -0.5f, -10.0f)
            Matrix.multiplyMM(modelMatrix, 0, mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mGoalLine.draw(mvpMatrix, mArcball.rotationMatrix)
        }

        val endTime = SystemClock.uptimeMillis()
        val angle = 0.1f * (endTime - startTime).toFloat()
        val angle1 = 0.3f * (endTime - startTime).toFloat()
        startTime = endTime
        rotXAngle += angle
        rotZAngle += angle
        var rotXMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotXMatrix, 0, rotXAngle, 1f, 0f,0f)
        var rotZMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.rotateM(rotZMatrix, 0, rotZAngle, 0f, 0f,1f)

        val posX:Float
        if(outwardDir)
            posX = prevPosX + angle * 0.01f
        else
            posX = prevPosX - angle * 0.01f
        if(posX > 9)
            outwardDir = false
        else if (posX < 2)
            outwardDir = true
        prevPosX = posX

        var rotMatrix = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        Matrix.multiplyMM(rotMatrix, 0, rotZMatrix, 0 ,rotMatrix, 0)

        var objectId = 0
        for(z in -7 .. 10 step 2){
            objectPos[objectId++][0] = posX

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, posX, 0f, z.toFloat())
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
            Matrix.multiplyMM(modelMatrix, 0 , mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mCube.draw(mvpMatrix,modelMatrix)

            objectPos[objectId++][0] = -posX

            Matrix.setIdentityM(modelMatrix, 0)
            Matrix.translateM(modelMatrix, 0, -posX, 0f, z.toFloat())
            Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
            Matrix.multiplyMM(modelMatrix, 0 , mArcball.rotationMatrix, 0, modelMatrix, 0)
            Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
            mCube.draw(mvpMatrix,modelMatrix)
        }

        val posZ:Float
        if(outwardDirZ)
            posZ = prevPosZ + angle1 * 0.01f
        else
            posZ = prevPosZ - angle1 * 0.01f
        if(posZ > 10)
            outwardDirZ = false
        else if (posZ < -8)
            outwardDirZ = true
        prevPosZ = posZ

        Matrix.setIdentityM(rotMatrix, 0)
        Matrix.multiplyMM(rotMatrix, 0, rotXMatrix, 0 ,rotMatrix, 0)

        objectPos[objectId++][2] = posZ
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, -5.0f, 0f, posZ)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0 , mArcball.rotationMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mCube.draw(mvpMatrix,modelMatrix)

        objectPos[objectId++][2] = posZ

        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 5.0f, 0f, posZ)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0 , mArcball.rotationMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mCube.draw(mvpMatrix,modelMatrix)

        objectPos[objectId++][2] = posZ
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix, 0, 0.0f, 0f, posZ)
        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, rotMatrix, 0)
        Matrix.multiplyMM(modelMatrix, 0 , mArcball.rotationMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, vpMatrix, 0, modelMatrix, 0)
        mCube.draw(mvpMatrix,modelMatrix)

        if(detecCollision(eyePos[0], eyePos[2])) {
            eyePos[0] = 0.0f
            eyePos[1] = 3.0f
            eyePos[2] = 3.0f
            cameraVec[0] = 0.0f
            cameraVec[1] = -0.7071f
            cameraVec[2] = -0.7071f
        }
    }

    fun onTouchEvent(event: MotionEvent):Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()

        when(event.action) {
            MotionEvent.ACTION_DOWN -> mArcball.start(x,y)
            MotionEvent.ACTION_MOVE -> mArcball.end(x,y)
        }
        return true
    }
}

fun loadShader(type: Int, filename: String, myContext: Context): Int {

    return GLES30.glCreateShader(type).also { shader ->

        val inputStream = myContext.assets.open(filename)
        val inputBuffer = ByteArray(inputStream.available())
        inputStream.read(inputBuffer)
        val shaderCode = String(inputBuffer)

        GLES30.glShaderSource(shader, shaderCode)
        GLES30.glCompileShader(shader)

        val compiled = ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder()).asIntBuffer()
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled)
        if(compiled.get(0) == 0) {
            GLES30.glGetShaderiv(shader, GLES30.GL_INFO_LOG_LENGTH, compiled)
            if(compiled.get(0) > 1) {
                Log.e("Shader", "$type shader: "+ GLES30.glGetShaderInfoLog(shader))
            }
            GLES30.glDeleteShader(shader)
            Log.e("Shader", "$type shader compile error.")
        }
    }


}

fun loadBitmap(filename: String, myContext: Context): Bitmap {
    val manager = myContext.assets
    val inputStream = BufferedInputStream(manager.open(filename))
    val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)
    return bitmap!!
}

fun cameraRotate(theta: Float) {
    val sinTheta = sin(theta)
    val cosTheta = cos(theta)
    val newVecZ = cosTheta * cameraVec[2] - sinTheta * cameraVec[0]
    val newVecX = sinTheta * cameraVec[2] + cosTheta * cameraVec[0]
    cameraVec[0] = newVecX
    cameraVec[2] = newVecZ
}

fun cameraMove(distance: Float) {
    val newPosX = eyePos[0] + distance * cameraVec[0]
    val newPosZ = eyePos[2] + distance * cameraVec[2]
    //if (newPosX > -10 && newPosX < 10 && newPosZ > -15 && newPosZ < 15){
    if(!detecCollision(newPosX, newPosZ)){
        eyePos[0] = newPosX
        eyePos[2] = newPosZ
    }

    if(!complete(newPosX, newPosZ)){
        eyePos[0] = newPosX
        eyePos[2] = newPosZ
    }
}

fun detecCollision(newPosX: Float, newPosZ: Float):Boolean {
    if (newPosX < -10 || newPosX > 10 || newPosZ < -15 || newPosZ > 15){
        return true
    }

    for(i in 0 .. objectPos.size-1) {
        if(abs(newPosX - objectPos[i][0]) < 1.0 && abs(newPosZ - objectPos[i][2]) < 1.0) {
            println("***** You have been hit by a soccer ball. Please make your way to the goal line, avoiding the soccer ball. *****")
        }
    }
    return false
}

fun complete(newPosX: Float, newPosZ: Float):Boolean {
    if (newPosX < -10 || newPosX > 10 || newPosZ < -15 || newPosZ > 15){
        return true
    }

    if( newPosZ  < (-11.0f)) {
            println("***** Goal In! *****")
    }
    return false
}