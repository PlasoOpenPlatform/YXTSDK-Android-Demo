package cn.plaso.yxt.demo.auth

import cn.plaso.yxt.yxtsdk.EnvManager

/** OpenAPI 与 SDK 使用的伯索主机。授权页选择。 */
enum class ServerEnvironment(
    val displayName: String,
    val host: String,
    val sdkEnv: String
) {
    Production("正式", "www.plaso.cn", EnvManager.LOGIN_PUBLISH_RELEASE),
    Development("Dev", "dev.plaso.cn", EnvManager.LOGIN_DEV),
    Test("Test", "test.plaso.cn", EnvManager.LOGIN_TEST),
    IntegrationTest("集成测试", "itest.plaso.cn", EnvManager.LOGIN_FTEST);

    val serviceHost: String
        get() = "https://$host/"

    companion object {
        fun fromName(name: String?): ServerEnvironment {
            return entries.firstOrNull { it.name == name } ?: Production
        }
    }
}
