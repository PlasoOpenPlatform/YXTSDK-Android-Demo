package cn.plaso.yxt.demo.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cn.plaso.yxt.demo.SessionStore
import cn.plaso.yxt.demo.network.TokenApi
import cn.plaso.yxt.demo.network.TokenResult
import cn.plaso.yxt.demo.sdk.SdkBootstrap
import cn.plaso.yxt.yxtsdk.SDKCallback
import cn.plaso.yxt.yxtsdk.YxtSDK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(app: Application) : AndroidViewModel(app) {

    private val tag = "AuthViewModel"
    private val stateFlow = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val state: StateFlow<AuthUiState> = stateFlow

    fun selectEnvironment(environment: ServerEnvironment) {
        if (SessionStore.environment == environment) {
            return
        }
        SessionStore.environment = environment
        SessionStore.clearToken()
        if (SdkBootstrap.isInitialized) {
            SdkBootstrap.ensureInitialized(getApplication(), environment)
        }
    }

    fun login(
        environment: ServerEnvironment?,
        appId: String,
        signKey: String,
        loginName: String,
        userType: UserType?
    ) {
        if (stateFlow.value is AuthUiState.Loading) {
            return
        }
        val id = appId.trim()
        val key = signKey.trim()
        val name = loginName.trim()
        if (environment == null || userType == null || id.isEmpty() || key.isEmpty() || name.isEmpty()) {
            stateFlow.value = AuthUiState.Failed("请填写服务器环境、AppId、SignKey、用户名和用户类型")
            return
        }
        val form = SessionStore.Form(environment, id, key, name, userType)
        SessionStore.saveForm(form)
        stateFlow.value = AuthUiState.Loading
        viewModelScope.launch {
            SdkBootstrap.ensureInitialized(getApplication(), environment)
            val result = withContext(Dispatchers.IO) {
                TokenApi.getToken(environment.host, id, key, name, userType.apiValue)
            }
            when (result) {
                is TokenResult.Fail -> stateFlow.value = AuthUiState.Failed(result.message)
                is TokenResult.Ok -> updateSdkToken(result)
            }
        }
    }

    private fun updateSdkToken(result: TokenResult.Ok) {
        val form = SessionStore.loadForm()
        YxtSDK.updateToken(result.token, form.userType.apiValue, object : SDKCallback {
            override fun onInitSuccess() {
                Log.i(tag, "updateToken success, userType=${form.userType.apiValue}")
                SessionStore.saveToken(result.token, result.expireSeconds)
                viewModelScope.launch {
                    stateFlow.value = AuthUiState.Success
                }
            }

            override fun onInitError(code: Int?) {
                Log.e(tag, "updateToken failed, code=$code")
                SessionStore.clearToken()
                viewModelScope.launch {
                    stateFlow.value = AuthUiState.Failed("SDK 登录失败")
                }
            }
        })
    }
}
