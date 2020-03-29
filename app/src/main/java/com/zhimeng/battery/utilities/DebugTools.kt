package com.zhimeng.battery.utilities

import android.util.Log
import com.zhimeng.battery.BuildConfig

fun printf(vararg args: Any) {
    if (BuildConfig.DEBUGGING_MODE) {
        Log.d("battery", args.joinToString(" "))
    }
}

fun printfNetwork(vararg args: Any) {
    if (BuildConfig.DEBUGGING_MODE) {
        Log.d("battery", args.joinToString(" "))
    }
}

class Debug {
    companion object {
        val images = listOf(
            "https://cdn.pixabay.com/photo/2019/11/30/04/03/bharatanatyam-4662487_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/05/01/21/46/tango-108483_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/05/12/23/03/lamb-1388937_1280.png",
            "https://cdn.pixabay.com/photo/2015/04/03/16/36/bee-705412_1280.png",
            "https://cdn.pixabay.com/photo/2016/09/14/20/50/teeth-1670434_1280.png",
            "https://cdn.pixabay.com/photo/2015/12/05/08/25/fairy-tale-1077863_1280.jpg",
            "https://cdn.pixabay.com/photo/2012/12/27/19/41/halloween-72939_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/09/07/11/37/tropical-1651423_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/06/07/06/51/tree-117582_1280.jpg",
            "https://cdn.pixabay.com/photo/2014/12/22/00/07/tree-576847_1280.png",
            "https://cdn.pixabay.com/photo/2015/07/27/20/16/book-863418_1280.jpg",
            "https://cdn.pixabay.com/photo/2013/07/18/10/56/house-163526_1280.jpg",
            "https://cdn.pixabay.com/photo/2014/12/08/11/49/love-560783_1280.jpg",
            "https://cdn.pixabay.com/photo/2016/06/14/14/09/skeleton-1456627_1280.png",
            "https://cdn.pixabay.com/photo/2016/10/19/02/21/moon-1751987_1280.png",
            "https://cdn.pixabay.com/photo/2016/07/02/12/21/eclipse-1492818_1280.jpg"
        )
    }
}
