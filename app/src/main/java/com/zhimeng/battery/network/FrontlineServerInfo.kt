package com.zhimeng.battery.network

data class BatteryServerInfo(
    val name: String,
    var description: String,
    val key: String,
    val baseUrl: String,
    val amazonS3BucketConfig: AmazonS3BucketConfig
)

data class AmazonS3BucketConfig(
    val userDisplayImageBucketName: String,
    val avatarGenerationBucketName: String,
    val outputBucketName: String
)

const val kProductionServer = "production"
const val kTestServer = "test"
const val kStagingServer = "staging"
const val kLocalServer = "localhost"

val availableServers = mapOf(
    kProductionServer to BatteryServerInfo(
        name = "Production",
        description = "商店版本用的服务器",
        key = kProductionServer,
        baseUrl = "http://52.81.84.200:8094/",
        amazonS3BucketConfig = AmazonS3BucketConfig(
            userDisplayImageBucketName = "fentiao-user-profile",
            avatarGenerationBucketName = "tatame-user-photo",
            outputBucketName = "tatame-avatar-model"
        )
    ),

    kTestServer to BatteryServerInfo(
        name = "内测服务器",
        description = "给内测用户用的服务器",
        key = kTestServer,
        baseUrl = "http://52.81.84.200:8094/",
        amazonS3BucketConfig = AmazonS3BucketConfig(
            userDisplayImageBucketName = "test-fentiao-user-profile",
            avatarGenerationBucketName = "test-tatame-user-photo",
            outputBucketName = "test-tatame-avatar-model"
        )
    ),

    kStagingServer to BatteryServerInfo(
        name = "开发服务器",
        description = "开发用的服务器",
        key = kStagingServer,
        baseUrl = "http://52.81.84.200:8094/",
        amazonS3BucketConfig = AmazonS3BucketConfig(
            userDisplayImageBucketName = "dev-fentiao-user-profile",
            avatarGenerationBucketName = "dev-tatame-user-photo",
            outputBucketName = "dev-tatame-avatar-model"
        )
    ),

    kLocalServer to BatteryServerInfo(
        name = "本地服务器",
        description = "本地电脑服务器",
        key = kLocalServer,
        baseUrl = "http://52.81.84.200:8094/",
        amazonS3BucketConfig = AmazonS3BucketConfig(
            userDisplayImageBucketName = "dev-tatame-user-profile-pictures",
            avatarGenerationBucketName = "dev-tatame-user-photo",
            outputBucketName = "dev-tatame-avatar-model"
        )
    )
)