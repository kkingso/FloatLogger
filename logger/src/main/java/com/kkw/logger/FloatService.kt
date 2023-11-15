package com.kkw.logger

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

/**
 * 开启悬浮窗的服务
 * @author kkw
 * @date 2023/11/14
 */
class FloatService : Service() {

    private val mFloatView: FloatView by lazy {
        FloatView(this)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate: ")
        mFloatView.show()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand: ")
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        mFloatView.dismiss()
    }

    companion object {
        private const val TAG = "FloatService"
    }
}