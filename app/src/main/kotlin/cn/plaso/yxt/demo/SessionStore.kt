package cn.plaso.yxt.demo

import android.content.Context
import cn.plaso.yxt.demo.auth.ServerEnvironment
import cn.plaso.yxt.demo.auth.UserType

/**
 * 本机会话。保存授权表单和 Token，供启动时自动登录。
 * SignKey 和 Token 只写在本机。
 */
object SessionStore {

    private const val PREF = "yxt_demo_session"
    private const val KEY_ENV = "environment"
    private const val KEY_APP_ID = "app_id"
    private const val KEY_SIGN_KEY = "sign_key"
    private const val KEY_LOGIN_NAME = "login_name"
    private const val KEY_USER_TYPE = "user_type"
    private const val KEY_TOKEN = "token"
    private const val KEY_EXPIRE = "expire_at"

    private lateinit var prefs: android.content.SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    var environment: ServerEnvironment
        get() = ServerEnvironment.fromName(prefs.getString(KEY_ENV, null))
        set(value) {
            prefs.edit().putString(KEY_ENV, value.name).commit()
        }

    data class Form(
        val environment: ServerEnvironment,
        val appId: String,
        val signKey: String,
        val loginName: String,
        val userType: UserType
    )

    fun loadForm(): Form {
        return Form(
            environment = environment,
            appId = prefs.getString(KEY_APP_ID, "").orEmpty(),
            signKey = prefs.getString(KEY_SIGN_KEY, "").orEmpty(),
            loginName = prefs.getString(KEY_LOGIN_NAME, "").orEmpty(),
            userType = UserType.fromApi(prefs.getString(KEY_USER_TYPE, null))
        )
    }

    fun saveForm(form: Form) {
        environment = form.environment
        prefs.edit()
            .putString(KEY_APP_ID, form.appId)
            .putString(KEY_SIGN_KEY, form.signKey)
            .putString(KEY_LOGIN_NAME, form.loginName)
            .putString(KEY_USER_TYPE, form.userType.apiValue)
            .commit()
    }

    /** 仍在有效期内的 token，预留 60 秒。 */
    fun validToken(): String? {
        val token = prefs.getString(KEY_TOKEN, null).orEmpty()
        if (token.isEmpty()) {
            return null
        }
        val expireAt = prefs.getLong(KEY_EXPIRE, 0L)
        return if (System.currentTimeMillis() + 60_000L < expireAt) token else null
    }

    fun tokenExpireAt(): Long {
        return prefs.getLong(KEY_EXPIRE, 0L)
    }

    fun saveToken(token: String, expireSeconds: Int) {
        val expireAt = System.currentTimeMillis() + expireSeconds.coerceAtLeast(0) * 1000L
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putLong(KEY_EXPIRE, expireAt)
            .commit()
    }

    fun clearToken() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_EXPIRE)
            .commit()
    }
}
