package cn.plaso.yxt.demo.network

import android.util.Log
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object TokenApi {

    private const val TAG = "TokenApi"
    private const val DEFAULT_EXPIRE_SECONDS = 36000

    /**
     * Open API 换 token。密码不参与。
     * https://open.plaso.cn/api-120430699
     */
    fun getToken(
        environmentHost: String,
        appId: String,
        signKey: String,
        loginName: String,
        userType: String
    ): TokenResult {
        val params = linkedMapOf<String, Any>(
            "appId" to appId,
            "loginName" to loginName,
            "userType" to userType,
            "validBegin" to System.currentTimeMillis() / 1000,
            "validTime" to 1200
        )
        SignHelper.sign(params, signKey)
        val body = JSONObject()
        params.forEach { (key, value) -> body.put(key, value) }
        val url = "https://$environmentHost/dataentry/user/getToken"
        Log.i(TAG, "getToken host=$environmentHost loginName=$loginName userType=$userType")
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = 30_000
                readTimeout = 30_000
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
            }
            connection.outputStream.use { stream ->
                stream.write(body.toString().toByteArray(Charsets.UTF_8))
            }
            val httpCode = connection.responseCode
            val text = readBody(connection)
            parse(httpCode, text)
        } catch (e: Exception) {
            Log.e(TAG, "getToken request failed, host=$environmentHost", e)
            TokenResult.Fail("网络请求失败")
        } finally {
            connection?.disconnect()
        }
    }

    private fun readBody(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) {
            connection.inputStream
        } else {
            connection.errorStream
        }
        return stream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
    }

    private fun parse(httpCode: Int, text: String): TokenResult {
        if (httpCode !in 200..299) {
            Log.e(TAG, "getToken http=$httpCode")
            return TokenResult.Fail("HTTP 状态码 $httpCode")
        }
        if (text.isEmpty()) {
            return TokenResult.Fail("服务器返回异常")
        }
        val root = try {
            JSONObject(text)
        } catch (e: Exception) {
            Log.e(TAG, "getToken decode failed", e)
            return TokenResult.Fail("响应解析失败")
        }
        val obj = root.optJSONObject("obj")
        val code = when {
            root.has("code") -> root.optInt("code")
            obj != null && obj.has("code") -> obj.optInt("code")
            else -> -1
        }
        val message = root.optString("msg")
            .ifEmpty { root.optString("message") }
            .ifEmpty { obj?.optString("msg").orEmpty() }
            .ifEmpty { "授权失败" }
        if (code != 0) {
            Log.e(TAG, "getToken rejected, code=$code")
            return TokenResult.Fail(message)
        }
        val token = obj?.optString("token").orEmpty()
        if (token.isEmpty()) {
            return TokenResult.Fail("未获取到有效 Token")
        }
        val expire = obj?.optInt("expire", DEFAULT_EXPIRE_SECONDS) ?: DEFAULT_EXPIRE_SECONDS
        return TokenResult.Ok(token, if (expire > 0) expire else DEFAULT_EXPIRE_SECONDS)
    }
}

sealed interface TokenResult {
    data class Ok(val token: String, val expireSeconds: Int) : TokenResult
    data class Fail(val message: String) : TokenResult
}
