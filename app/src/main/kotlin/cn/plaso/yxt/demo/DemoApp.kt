package cn.plaso.yxt.demo

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import cn.plaso.yxt.demo.auth.AuthActivity
import cn.plaso.yxt.yxtsdk.SDKCallback
import cn.plaso.yxt.yxtsdk.YxtSDK
import java.util.Collections

class DemoApp : Application() {

    private val activities = Collections.synchronizedList(mutableListOf<Activity>())

    override fun onCreate() {
        super.onCreate()
        appContext = this
        SessionStore.init(this)
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activities.add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                activities.remove(activity)
            }

            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityResumed(activity: Activity) = Unit
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        })
    }

    private fun finishAll() {
        synchronized(activities) {
            activities.toList().forEach { activity ->
                if (!activity.isFinishing && !activity.isDestroyed) {
                    activity.finish()
                }
            }
            activities.clear()
        }
    }

    companion object {
        private const val TAG = "DemoApp"
        private lateinit var appContext: DemoApp

        val sdkCallback: SDKCallback = object : SDKCallback {
            override fun onInitSuccess() {
                Log.i(TAG, "SDK init success")
            }

            override fun onInitError(code: Int?) {
                Log.e(TAG, "SDK init failed, code=$code")
            }

            override fun onTokenError() {
                Log.e(TAG, "token invalid")
                onTokenInvalid()
            }
        }

        fun onTokenInvalid() {
            if (!::appContext.isInitialized) {
                return
            }
            SessionStore.clearToken()
            YxtSDK.doLogout()
            appContext.finishAll()
            val intent = Intent(appContext, AuthActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            appContext.startActivity(intent)
            Toast.makeText(appContext, R.string.token_invalid, Toast.LENGTH_LONG).show()
        }
    }
}
