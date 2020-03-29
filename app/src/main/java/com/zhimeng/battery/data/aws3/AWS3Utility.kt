package com.zhimeng.battery.data.aws3

import android.content.Context
import com.amazonaws.ClientConfiguration
import com.amazonaws.auth.CognitoCachingCredentialsProvider
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.client.Callback
import com.amazonaws.mobile.client.UserStateDetails
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.zhimeng.battery.network.BatteryApi
import com.zhimeng.battery.network.model.DownloadProgressModel
import com.zhimeng.battery.network.model.UploadProgressModel
import com.zhimeng.battery.utilities.printf
import io.reactivex.Observable
import java.io.File
import kotlin.math.min

class AWS3Utility {
    companion object {
        const val TAG = "AWS3Utility"
        private const val poolID = "cn-north-1:9869b00b-70bd-47a3-9068-41defdc71567"
        private const val awsUrl = ".s3.cn-north-1.amazonaws.com.cn/"
        private lateinit var client: AmazonS3Client

        fun awsSDKInit(context: Context) {
//            context.startService(Intent(context, TransferService::class.java))
            val credentialsProvider = CognitoCachingCredentialsProvider(
                    context,
                    poolID,
                    Regions.CN_NORTH_1
            )
            client = AmazonS3Client(credentialsProvider,
                    Region.getRegion(Regions.CN_NORTH_1),
                    ClientConfiguration())
            AWSMobileClient.getInstance().initialize(context, object : Callback<UserStateDetails> {
                override fun onResult(result: UserStateDetails?) {
                    printf("AWSMobileClient initialized, user state is ${result?.userState}")
                }

                override fun onError(e: Exception?) {
                    printf("init error")
                    e?.printStackTrace()
                }
            })
        }

        /**
         * 上传图片文件至某个bucket.
         *
         */
        private fun uploadImage(context: Context,
                                file: File,
                                imageName: String,
                                bucketName: String,
                                isPublic: Boolean): Observable<UploadProgressModel> {

            return Observable.create {

                val transferUtility = TransferUtility.builder()
                        .context(context)
                        .defaultBucket(bucketName)
                        .awsConfiguration(AWSMobileClient.getInstance().configuration)
                        .s3Client(client)
                        .build()

                val transferObserver: TransferObserver = if (isPublic) {
                    transferUtility.upload(bucketName, imageName, file, CannedAccessControlList.PublicRead)
                } else {
                    transferUtility.upload(bucketName, imageName, file)
                }
                transferObserver.setTransferListener(object : TransferListener {

                    override fun onStateChanged(id: Int, state: TransferState) {
                        if (state == TransferState.COMPLETED) {
                            it.onNext(UploadProgressModel(1f))
                            it.onComplete()
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        it.onNext(UploadProgressModel(min(0.999f, bytesCurrent.toFloat() / bytesTotal.toFloat())))
                    }

                    override fun onError(id: Int, ex: Exception) {
                        it.onError(ex)
                    }
                })

                it.setCancellable {
                    transferObserver.cleanTransferListener()
                    if (transferObserver.state != TransferState.COMPLETED) {
                        transferUtility.cancel(transferObserver.id)
                    }
                }
            }
        }

        private fun downloadData(context: Context,
                                 file: File,
                                 dataName: String,
                                 bucketName: String,
                                 requiredTimeAfter: Long): Observable<DownloadProgressModel> {
            return Observable.create {

                /* Check metadata first */
                val metadata = client.getObjectMetadata(bucketName, dataName)
                if (metadata.lastModified.time < requiredTimeAfter) {
                    it.onError(Error("Resource not recent enough"))
                } else {

                    val transferUtility = TransferUtility.builder()
                            .context(context)
                            .defaultBucket(bucketName)
                            .awsConfiguration(AWSMobileClient.getInstance().configuration)
                            .s3Client(client)
                            .build()

                    val transferObserver = transferUtility.download(bucketName, dataName, file)
                    transferObserver.setTransferListener(object : TransferListener {
                        override fun onStateChanged(id: Int, state: TransferState) {
                            if (state == TransferState.COMPLETED) {
                                it.onNext(DownloadProgressModel(progress = 1f, file = file))
                                it.onComplete()
                            }
                        }

                        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                            it.onNext(DownloadProgressModel(progress = min(0.999f, bytesCurrent.toFloat() / bytesTotal.toFloat()), file = null))
                        }

                        override fun onError(id: Int, ex: Exception) {
                            it.onError(ex)
                        }
                    })

                    it.setCancellable {
                        transferObserver.cleanTransferListener()
                        if (transferObserver.state != TransferState.COMPLETED) {
                            transferUtility.cancel(transferObserver.id)
                        }
                    }
                }
            }
        }

        val userPictureUrlPrefix: String
            get() = "https://$userDisplayImageBucketName$awsUrl"

        private val userDisplayImageBucketName: String
            get() {
                return BatteryApi.sharedInstance.selectedServerInfo
                        .amazonS3BucketConfig.userDisplayImageBucketName
            }

        private val avatarGenerationBucketName: String
            get() {
                return BatteryApi.sharedInstance.selectedServerInfo
                        .amazonS3BucketConfig.avatarGenerationBucketName
            }

        private val outputBucketName: String
            get() {
                return BatteryApi.sharedInstance.selectedServerInfo
                        .amazonS3BucketConfig.outputBucketName
            }

        fun uploadImageForGeneration(context: Context, file: File, imageName: String): Observable<UploadProgressModel> {
            return uploadImage(context = context, file = file,
                    imageName = imageName,
                    isPublic = false,
                    bucketName = avatarGenerationBucketName
            )
        }

        fun downloadAvatarData(context: Context, file: File, tatameCode: String, lastModifiedDate: Long? = null): Observable<DownloadProgressModel> {
            return downloadData(context = context,
                    file = file,
                    dataName = tatameCode,
                    bucketName = outputBucketName,
                    requiredTimeAfter = lastModifiedDate ?: 0)
        }

        fun uploadUserPicture(context: Context, file: File, imageSaveName: String): Observable<UploadProgressModel> {
            return uploadImage(context = context,
                    file = file,
                    imageName = imageSaveName,
                    isPublic = true,
                    bucketName = userDisplayImageBucketName
            )
        }
    }
}