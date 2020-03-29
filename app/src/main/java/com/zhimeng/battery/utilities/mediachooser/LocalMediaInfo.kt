package com.zhimeng.battery.utilities.mediachooser


typealias LocalMediaInfos = ArrayList<LocalMediaInfo>

data class LocalMediaInfo(
    var path: String? = null,
    var isSelected: Boolean = false,
    var mimeType: Int = 0,
    var pictureType: String? = null,
    var width: Int = 0,
    var height: Int = 0
)
