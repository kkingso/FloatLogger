package com.kkw.floatlogger

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kkw.floatlogger.databinding.ActivityMainBinding
import com.kkw.logger.FloatService
import com.kkw.logger.FloatView

class MainActivity : AppCompatActivity() {
    private lateinit var mFloatView: FloatView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mFloatView = FloatView(this)

        binding.show.setOnClickListener {
            handleService(true)

//            if (ContextCompat.checkSelfPermission(
//                    this,
//                    android.Manifest.permission.SYSTEM_ALERT_WINDOW
//                ) == PackageManager.PERMISSION_GRANTED
//            ) {
//                handleService(true)
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(android.Manifest.permission.SYSTEM_ALERT_WINDOW),
//                    10
//                )
//                // 跳转应用信息界面
////                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
////                val uri = Uri.fromParts("package", packageName, null)
////                intent.data = uri
////                startActivity(intent)
//            }
        }

        binding.dismiss.setOnClickListener {
            handleService(false)
        }
    }

    /**
     * 是否开启服务
     */
    private fun handleService(start: Boolean) {
        val intent = Intent(this, FloatService::class.java)
        if (start) {
            startService(intent)
        } else {
            stopService(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handleService(true)
            } else {
                Toast.makeText(this, "需要开启应用悬浮窗权限", Toast.LENGTH_SHORT).show()
            }
        }
    }
}