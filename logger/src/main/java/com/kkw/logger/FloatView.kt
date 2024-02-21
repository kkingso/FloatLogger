package com.kkw.logger

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.kkw.logger.databinding.ViewFloatBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.Executors
import kotlin.math.abs

/**
 * 承载日志的悬浮窗
 * @author kkw
 * @date 2023/11/14
 */
class FloatView(private val mContext: Context) {

    private val mBinding: ViewFloatBinding by lazy {
        ViewFloatBinding.inflate(LayoutInflater.from(mContext), null, false)
    }
    private var windowManager: WindowManager? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    // 日志适配器
    private var mLogAdapter: LogAdapter? = null
    private var mHandler: Handler
    private val pools = Executors.newSingleThreadExecutor()

    init {
        initWindowManager()
        initView()
        initAdapter()
        mHandler = object : Handler(Looper.getMainLooper()) {

            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                mLogAdapter?.add(LogEntity(msg.obj as String?))
                mBinding.logList.smoothScrollToPosition(mLogAdapter?.itemCount?.minus(1) ?: 0)
            }
        }
    }

    /**
     * 配置WindowManager
     */
    private fun initWindowManager() {
        windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        layoutParams = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            layoutParams?.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        layoutParams?.format = PixelFormat.RGBA_8888
        layoutParams?.gravity = Gravity.START or Gravity.TOP
        layoutParams?.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.x = 900
        layoutParams?.y = 300
        layoutParams?.alpha = 0.5f
    }

    /**
     * 初始化EditText布局
     */
    private fun initView() {
//        mBinding.logTag.setOnClickListener {
//            showSoftInput()
//        }
//        mBinding.logTag.addTextChangedListener {
//            mBinding.logTag.setText(it?.toString())
//        }
        mBinding.logOk.setOnClickListener {
            initData(mBinding.logTag.text.toString().trim())
        }
    }

    /**
     * 初始化日志适配器
     */
    private fun initAdapter() {
        mLogAdapter = LogAdapter()
        mBinding.logList.apply {
            adapter = mLogAdapter
            layoutManager = LinearLayoutManager(mContext)
        }
    }

    /**
     * 显示浮窗
     */
    fun show() {
        if (Settings.canDrawOverlays(mContext)) {
            mBinding.root.setOnTouchListener(FloatingOnTouchListener())
            windowManager?.addView(mBinding.root, layoutParams)
        } else {
            Toast.makeText(mContext, "需要开启应用悬浮窗权限", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * 关闭浮窗
     */
    fun dismiss() {
        pools.shutdownNow()
        if (Settings.canDrawOverlays(mContext)) {
            windowManager?.removeView(mBinding.root)
        }
    }

    /**
     * 获取系统logcat日志
     */
    private fun initData(tag: String?) {
        pools.execute {
            // 使用 adb 命令获取所有应用的 log 日志
            var bufferedReader: BufferedReader? = null
            try {
                val cmd = "logcat -s ${tag}"
//                val cmd = "logcat | grep ${tag}"
//                val cmd = "logcat | grep -E \"${tag}\""
//                val cmd = "logcat com.kkw.floatlogger.*:V"
//                val cmd = arrayOf("logcat", "-v", "long")
                val process = Runtime.getRuntime().exec(cmd)
                bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

                var line: String?
                do {
                    line = bufferedReader.readLine()
                    line?.let {
                        mHandler.sendMessage(Message.obtain(mHandler, 0, it))
                        Thread.sleep(100)
                    }
                } while (line != null)

            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                bufferedReader?.close()
            }
        }
    }

    /**
     * 显示软键盘
     */
    private fun showSoftInput() {
        mBinding.logTag.isEnabled = true
        //设置可获得焦点
        mBinding.logTag.isFocusable = true;
        mBinding.logTag.isFocusableInTouchMode = true;
        //请求获得焦点
        mBinding.logTag.requestFocus();
        KeyboardUtil.toggleSoftInput(mBinding.logTag)
    }

    /**
     * 触摸移动
     */
    private inner class FloatingOnTouchListener : View.OnTouchListener {
        private var lastX = 0
        private var lastY = 0

        // 视图是否有移动
        private var hasMoved = false

        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = event.rawX.toInt()
                    lastY = event.rawY.toInt()
                    hasMoved = false
                }

                MotionEvent.ACTION_MOVE -> {
                    val nowX = event.rawX.toInt()
                    val nowY = event.rawY.toInt()
                    val movedX = nowX - lastX
                    val movedY = nowY - lastY
                    lastX = nowX
                    lastY = nowY

                    // 更新视图位置
                    layoutParams?.let {
                        it.x = it.x + movedX
                        it.y = it.y + movedY
                    }
                    windowManager?.updateViewLayout(view, layoutParams)
                    // 点击防抖
                    if (abs(movedX) > 6 || abs(movedY) > 6) {
                        hasMoved = true
                    }

                }

                MotionEvent.ACTION_UP -> {
                    // 返回true消费此次事件，后续不会触发click事件
                    // 返回false不消费，触发click事件
                    return hasMoved
                }

                else -> {}
            }
            return false
        }
    }
}