# YXT SDK Demo（Android）

伯索云课堂 Android SDK 接入示例。提供实时课堂、作业巩固、资料中心。启动时检查本地 Token，授权页用 OpenAPI 换 Token，再进入主界面。

## 环境要求

- Android Studio，JDK 17
- `minSdk` 26，`compileSdk` 34，`targetSdk` 33
- 伯索开放平台分配的 AppId、SignKey，以及机构里已有用户的登录名 loginName

## 打开工程

用 Android Studio 打开本目录，编 `:app` 的 `assembleDebug`。仓库里没有签名文件。

SDK 依赖 `cn.plaso:yxtsdk:2.0.21`。Gradle 7.6.1，Android Gradle Plugin 7.4.2，Kotlin 1.9.24。

角色在 `YxtSDK.updateToken` 里传 `student`、`teacher` 或 `assistant`。

## 使用流程

1. **Splash**：本地 Token 未过期则自动 `updateToken`，否则进入授权页。
2. **授权配置**：选择服务器环境（正式、Dev、Test、集成测试），填写 AppId、SignKey、用户名（loginName）和用户类型，点击「登录授权」。打开时这些项都是空的。
3. **主界面**
   - 课堂：`YxtSDK.startLiveClass`
   - 巩固：`YxtSDK.startHomework`
   - 资料中心：平板显示，`YxtSDK.startMyFileActivity`
   - 我的：会话信息与退出登录

机构名传 `plaso`。细节见 [接入说明.md](接入说明.md)。
