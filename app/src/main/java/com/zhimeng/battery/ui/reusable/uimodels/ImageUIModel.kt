package com.zhimeng.battery.ui.reusable.uimodels

import android.os.Parcelable
import com.zhimeng.battery.R
import com.zhimeng.battery.utilities.Debug
import com.zhimeng.battery.utilities.Dimens
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ImageUIModel(
    var imageUrl: String? = null,
    var imageRes: Int? = null,
    var placeholder: Int? = null,
    var targetWidth: Int = 0,
    var targetHeight: Int = 0
) : Parcelable {

    companion object {

        private val displayImageLength = Dimens.dpToPx(50)
        private val feedThumbnailMaximumWidth = Dimens.screenWidth / 2
        private val chatThumbnailMaximumWidth = (Dimens.screenWidth * 0.75).toInt()

        fun fake(): ImageUIModel {
            return ImageUIModel(
                imageUrl = Debug.images.random(),
                placeholder = R.drawable.placeholder_image
            )
        }

        fun fakeList(): List<ImageUIModel> {
            return (0..(5..10).random()).map {
                ImageUIModel(
                    imageUrl = Debug.images.random(),
                    placeholder = R.drawable.placeholder_image
                )
            }
        }

        /**
         * 纯粹的Placeholder.
         */
        fun placeholder(imageRes: Int): ImageUIModel {
            return ImageUIModel(
                imageRes = imageRes
            )
        }

        /**
         * 用户/偶像头像.
         */
        fun displayImage(imageUrl: String?): ImageUIModel {
            return ImageUIModel(
                imageUrl = imageUrl,
                placeholder = R.drawable.placeholder_user,
                targetWidth = displayImageLength,
                targetHeight = displayImageLength
            )
        }

        /**
         * 首页的缩略图.
         */
        fun feedThumbnail(imageUrl: String?): ImageUIModel {
            return ImageUIModel(
                imageUrl = imageUrl,
                placeholder = R.drawable.placeholder_image,
                targetWidth = feedThumbnailMaximumWidth
            )
        }

        /**
         * 聊天的缩略图.
         */
        fun chatThumbnail(imageUrl: String?): ImageUIModel {
            return ImageUIModel(
                imageUrl = imageUrl,
                placeholder = R.drawable.placeholder_image,
                targetWidth = chatThumbnailMaximumWidth
            )
        }

        /**
         * 原图.
         */
        fun full(imageUrl: String?): ImageUIModel {
            return ImageUIModel(
                imageUrl = imageUrl,
                placeholder = R.drawable.placeholder_image
            )
        }
    }
}