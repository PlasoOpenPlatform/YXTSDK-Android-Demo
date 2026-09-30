package cn.plaso.yxt.demo.sdk

import android.content.Context
import android.util.Log
import cn.plaso.yxt.demo.DemoApp
import cn.plaso.yxt.demo.auth.ServerEnvironment
import cn.plaso.yxt.yxtsdk.EnvManager
import cn.plaso.yxt.yxtsdk.YxtSDK
import com.plaso.util.PlasoProp

/**
 * SDK 初始化。先写入伯索域名，再 init，返回后把业务域名设为授权页选中的主机。
 */
object SdkBootstrap {

    private const val TAG = "SdkBootstrap"

    const val ORG_NAME = "plaso"

    var isInitialized: Boolean = false
        private set

    fun ensureInitialized(context: Context, environment: ServerEnvironment) {
        applyPlasoHosts()
        if (!isInitialized) {
            YxtSDK.init(context.applicationContext, ORG_NAME, DemoApp.sdkCallback)
            isInitialized = true
            Log.i(TAG, "SDK init, org=$ORG_NAME")
        }
        EnvManager.setEnv(environment.sdkEnv)
        PlasoProp.setRealoem_server(environment.host)
        PlasoProp.setOem_server(environment.serviceHost)
        Log.i(TAG, "service host ${environment.host}")
    }

    private fun applyPlasoHosts() {
        EnvManager.HOST_RELEASE = ServerEnvironment.Production.host
        EnvManager.RHOST_RELEASE = ServerEnvironment.Production.serviceHost
        EnvManager.HOST_DEV = ServerEnvironment.Development.host
        EnvManager.RHOST_DEV = ServerEnvironment.Development.serviceHost
        EnvManager.HOST_TEST = ServerEnvironment.Test.host
        EnvManager.RHOST_TEST = ServerEnvironment.Test.serviceHost
        EnvManager.HOST_FTEST = ServerEnvironment.IntegrationTest.host
        EnvManager.RHOST_FTEST = ServerEnvironment.IntegrationTest.serviceHost
    }
}
