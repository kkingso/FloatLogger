package com.kkw.logger

import android.util.Log

/**
 * 日志工具
 * @author kkw
 * @date 2023/11/14
 */
object Logger {

    private const val TAG = "Logger"

    fun v(tag: String, msg: String) {
        Log.v("$TAG-$tag", msg)
    }

    fun i(tag: String, msg: String) {
        Log.i("$TAG-$tag", msg)
    }

    fun d(tag: String, msg: String) {
        Log.d("$TAG-$tag", msg)
    }

    fun e(tag: String, msg: String) {
        Log.e("$TAG-$tag", msg)
    }
}