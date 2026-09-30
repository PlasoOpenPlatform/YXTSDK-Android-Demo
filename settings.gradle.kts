dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        maven("https://mirrors.cloud.tencent.com/repo/")
        maven("https://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven("https://maven.aliyun.com/nexus/content/repositories/releases/")
        maven("https://nexus.plaso.cn/repository/maven-public/")
        maven("https://maven.aliyun.com/repository/google")
        maven("https://developer.huawei.com/repo/")
        maven("https://jitpack.io")
        maven("https://storage.googleapis.com/download.flutter.io")
        google()
        mavenCentral()
    }
}

rootProject.name = "YxtClientAndroid"
include(":app")
