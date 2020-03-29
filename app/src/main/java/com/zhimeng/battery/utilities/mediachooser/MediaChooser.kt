package com.zhimeng.battery.utilities.mediachooser

import android.app.Activity
import android.content.CursorLoader
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore

typealias LocalMediaType = Int

class MediaChooser(private val typeLocal: LocalMediaType, private val activity: Activity) {

    interface LocalMediaLoadListener {
        fun localMediaLoadComplete(localMediaInfos: LocalMediaInfos)
    }

    companion object {
        const val TAG = "MediaChooser"
        private const val ALL_TYPE_LOCAL: LocalMediaType = 1
        const val IMAGE_TYPE: LocalMediaType = ALL_TYPE_LOCAL + 1
        private val QUERY_URI = MediaStore.Files.getContentUri("external")
        private const val ORDER_BY = MediaStore.Files.FileColumns._ID + " DESC"
        private const val NOT_GIF = "!='image/gif'"
        private const val SELECTION_NOT_GIF =
            (MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + " AND " + MediaStore.MediaColumns.SIZE + ">0" + " AND " + MediaStore.MediaColumns.MIME_TYPE + NOT_GIF)
    }

    /**
     * 媒体文件数据库字段
     */
    private val projections = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.MediaColumns.DATA,
        MediaStore.MediaColumns.MIME_TYPE,
        MediaStore.MediaColumns.WIDTH,
        MediaStore.MediaColumns.HEIGHT
    )

    /**
     * 获取指定类型的文件
     *
     * @param mediaType
     * @return
     */
    private fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String> {
        return arrayOf(mediaType.toString())
    }

    fun loadAllMedia(listener: LocalMediaLoadListener) = run {
        activity.loaderManager?.initLoader<Cursor>(
            this.typeLocal,
            Bundle(),
            object : android.app.LoaderManager.LoaderCallbacks<Cursor> {
                override fun onCreateLoader(
                    id: Int,
                    p1: Bundle?
                ): android.content.Loader<Cursor?>? {
                    var cursorLoader: CursorLoader? = null
                    when (id) {
                        IMAGE_TYPE -> {
                            val imageType =
                                getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
                            cursorLoader = CursorLoader(
                                activity,
                                QUERY_URI,
                                projections,
                                SELECTION_NOT_GIF,
                                imageType,
                                ORDER_BY
                            )
                            return cursorLoader
                        }
                    }
                    return cursorLoader
                }

                override fun onLoadFinished(
                    loader: android.content.Loader<Cursor>?,
                    cursor: Cursor
                ) {
                    val count: Int = cursor.count
                    if (count == 0) return
                    val lMI = LocalMediaInfos()
                    cursor.moveToFirst()
                    do {
                        val path = cursor.getString(cursor.getColumnIndexOrThrow(projections[1]))
                        val pictureType =
                            cursor.getString(cursor.getColumnIndexOrThrow(projections[2]))
                        val w = cursor.getInt(cursor.getColumnIndexOrThrow(projections[3]))
                        val h = cursor.getInt(cursor.getColumnIndexOrThrow(projections[4]))
                        lMI.add(LocalMediaInfo(path, false, IMAGE_TYPE, pictureType, w, h))
                    } while (cursor.moveToNext())
                    listener.localMediaLoadComplete(lMI)
                }

                override fun onLoaderReset(cursor: android.content.Loader<Cursor>?) {}
            })
    }
}
