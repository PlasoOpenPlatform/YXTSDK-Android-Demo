package cn.plaso.yxt.demo.main

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import cn.plaso.yxt.demo.R
import cn.plaso.yxt.demo.SessionStore
import cn.plaso.yxt.demo.auth.AuthActivity
import cn.plaso.yxt.demo.databinding.ActivityMainBinding
import cn.plaso.yxt.yxtsdk.YxtSDK

/**
 * 授权后的主界面。课堂、巩固、资料中心通过 YxtSDK 打开，我的页展示会话。
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (SessionStore.validToken() == null) {
            openAuth()
            return
        }
        requestedOrientation = if (YxtSDK.isPad()) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottom.menu.findItem(R.id.tab_file).isVisible = YxtSDK.isPad()
        if (supportFragmentManager.findFragmentByTag(TAG_MINE) == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.tab_container, MineFragment(), TAG_MINE)
                .commit()
        }
        binding.bottom.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_classroom -> {
                    YxtSDK.startLiveClass(this, true)
                    false
                }
                R.id.tab_homework -> {
                    YxtSDK.startHomework(this, true)
                    false
                }
                R.id.tab_file -> {
                    YxtSDK.startMyFileActivity(this, true)
                    false
                }
                else -> true
            }
        }
        binding.bottom.selectedItemId = R.id.tab_mine
    }

    fun logout() {
        YxtSDK.doLogout()
        SessionStore.clearToken()
        openAuth()
    }

    private fun openAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    companion object {
        private const val TAG_MINE = "mine"
    }
}
