package cn.plaso.yxt.demo.splash

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.plaso.yxt.demo.SessionStore
import cn.plaso.yxt.demo.sdk.SdkBootstrap
import cn.plaso.yxt.yxtsdk.SDKCallback
import cn.plaso.yxt.yxtsdk.YxtSDK
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SplashDestination {
    Pending,
    SigningIn,
    Auth,
    Main
}

class SplashViewModel(app: Application) : AndroidViewModel(app) {

    private val destinationFlow = MutableStateFlow(SplashDestination.Pending)
    val destination: StateFlow<SplashDestination> = destinationFlow
    private var started = false

    fun start() {
        if (started) {
            return
        }
        started = true
        viewModelScope.launch {
            delay(1_200)
            val token = SessionStore.validToken()
            if (token == null) {
                destinationFlow.value = SplashDestination.Auth
                return@launch
            }
            destinationFlow.value = SplashDestination.SigningIn
            val form = SessionStore.loadForm()
            SdkBootstrap.ensureInitialized(getApplication(), form.environment)
            YxtSDK.updateToken(token, form.userType.apiValue, object : SDKCallback {
                override fun onInitSuccess() {
                    viewModelScope.launch {
                        destinationFlow.value = SplashDestination.Main
                    }
                }

                override fun onInitError(code: Int?) {
                    Log.e("SplashViewModel", "auto login failed, code=$code")
                    SessionStore.clearToken()
                    viewModelScope.launch {
                        destinationFlow.value = SplashDestination.Auth
                    }
                }
            })
        }
    }
}
