package dev.ragnarok.fenrir.dedicated

import androidx.annotation.AnyRes

class DedicatedSource {
    var isVideo: Boolean

    @AnyRes
    var res = 0
    var asset: String? = null

    constructor(@AnyRes video_res: Int) {
        isVideo = true
        res = video_res
    }

    constructor(landscape: Boolean, asset_file: String) {
        isVideo = false
        asset =
            "file:///android_asset/dedicated/" + (if (landscape) "land" else "portrait") + "/$asset_file"
    }
}
