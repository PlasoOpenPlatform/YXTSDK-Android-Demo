package cn.plaso.yxt.demo.network

import java.math.BigInteger
import java.nio.charset.Charset
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SignHelper {

    /**
     * 按 key 排序拼 `k=v&k=v`，再用 appKey 做 HmacSHA1。
     * 十六进制用 BigInteger，服务端认这个结果。前面的 0 会被丢掉，不要改成补零。
     */
    fun sign(params: MutableMap<String, Any>, signKey: String) {
        val keys = params.keys.sorted()
        val sorted = StringBuilder()
        for ((index, key) in keys.withIndex()) {
            if (index != 0) {
                sorted.append("&")
            }
            sorted.append("$key=${params[key]}")
        }
        params["signature"] = hmacSha1(sorted.toString(), signKey)
    }

    private fun hmacSha1(text: String, signKey: String): String {
        val charset = Charset.forName("UTF-8")
        val mac = Mac.getInstance("HmacSHA1")
        mac.init(SecretKeySpec(signKey.toByteArray(charset), "HmacSHA1"))
        val raw = mac.doFinal(text.toByteArray(charset))
        return BigInteger(1, raw).toString(16).uppercase()
    }
}
