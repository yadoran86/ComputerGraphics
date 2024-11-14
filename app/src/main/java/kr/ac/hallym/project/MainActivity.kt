package kr.ac.hallym.project

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import kr.ac.hallym.project.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    //private lateinit var mainSurfaceView: MainGLSurfaceView

    val binding:ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //mainSurfaceView = MainGLSurfaceView(this)
        //setContentView(mainSurfaceView)
        supportActionBar?.hide()
        initSurfaceView()
        setContentView(binding.root)

        binding.eyeLeft.setOnClickListener {
            cameraRotate(0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeRight.setOnClickListener {
            cameraRotate(-0.174f)
            binding.surfaceView.requestRender()
        }
        binding.eyeForward.setOnClickListener {
            cameraMove(0.5f)
            binding.surfaceView.requestRender()
        }
        binding.eyeBackward.setOnClickListener {
            cameraMove(-0.5f)
            binding.surfaceView.requestRender()
        }

    }

    fun initSurfaceView() {
        binding.surfaceView.setEGLContextClientVersion(3)

        val mainRenderer = MainGLRenderer(this)
        binding.surfaceView.setRenderer(mainRenderer)

        binding.surfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

        binding.surfaceView.setOnTouchListener { v, event ->
            mainRenderer.onTouchEvent(event)
        }
    }
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (event.keyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> {
                    // 방향키 위로 눌림 처리
                    cameraMove(0.5f)
                    binding.surfaceView.requestRender()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    // 방향키 아래로 눌림 처리
                    cameraMove(-0.5f)
                    binding.surfaceView.requestRender()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    // 방향키 왼쪽으로 눌림 처리
                    cameraRotate(0.174f)
                    binding.surfaceView.requestRender()
                    return true
                }
                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    // 방향키 오른쪽으로 눌림 처리
                    cameraRotate(-0.174f)
                    binding.surfaceView.requestRender()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }
}