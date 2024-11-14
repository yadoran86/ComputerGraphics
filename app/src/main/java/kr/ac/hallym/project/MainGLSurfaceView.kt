package kr.ac.hallym.project

import android.content.Context
import android.opengl.GLSurfaceView

class MainGLSurfaceView(context: Context): GLSurfaceView(context) {

    private var mainRenderer:MainGLRenderer

    init {
        setEGLContextClientVersion(3)

        mainRenderer = MainGLRenderer(context)

        setRenderer(mainRenderer)

        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }
}