package dev.ragnarok.fenrir.dedicated

import android.os.Bundle
import com.google.android.exoplayer2.SimpleExoPlayer
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.mvp.presenter.base.AccountDependencyPresenter
import dev.ragnarok.fenrir.util.HelperSimple
import java.util.*

class DedicatedPresenter(accountId: Int, savedInstanceState: Bundle?) :
    AccountDependencyPresenter<IDedicatedView>(accountId, savedInstanceState) {
    private val sourcesPortrait: ArrayList<DedicatedSource> = makeSources("dedicated", 1, 37, false)
    private val sourcesLand: ArrayList<DedicatedSource> = makeSources("dedicated", 1, 23, true)
    private val shufflePhotos = !HelperSimple.needHelp(DEDICATED_COUNTER, 2)
    private var land = false
    private var positionLand = 0
    private var positionPortrait = 0
    private var showHeart = true
    private var showHelper = true
    private var exoPlayer: SimpleExoPlayer? = null
    override fun onGuiCreated(viewHost: IDedicatedView) {
        super.onGuiCreated(viewHost)
        viewHost.requestOrientation()
    }

    override fun onGuiResumed() {
        super.onGuiResumed()
        exoPlayer ?: view?.createPlayer()
    }

    override fun onDestroyed() {
        super.onDestroyed()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    fun fireReceivePlayer(player: SimpleExoPlayer) {
        exoPlayer = player
    }

    fun hideHeart() {
        showHeart = false
    }

    fun fireReceiveOrientation(isLand: Boolean) {
        land = isLand
        view?.displayData(
            if (land) sourcesLand else sourcesPortrait,
            if (land) positionLand else positionPortrait,
            showHeart,
            showHelper,
            !shufflePhotos
        )
    }

    fun nextPosition(position: Int) {
        if (!guiIsResumed) {
            return
        }
        if (land) {
            positionLand = position
        } else {
            positionPortrait = position
        }
    }

    fun fireUserClick() {
        view?.openUserWall(accountId, 255645173)
    }

    fun fireToggleHelper() {
        showHelper = !showHelper
        view?.doToggleHelper(showHelper, !shufflePhotos)
    }

    fun getCurrentPosition(isLand: Boolean): Int {
        land = isLand
        return if (isLand) {
            positionLand
        } else {
            positionPortrait
        }
    }

    init {
        sourcesLand.add(DedicatedSource(R.raw.dedicated_video1))
        sourcesLand.add(DedicatedSource(R.raw.dedicated_video2))

        sourcesPortrait.add(DedicatedSource(R.raw.dedicated_video1))
        sourcesPortrait.add(DedicatedSource(R.raw.dedicated_video2))

        if (shufflePhotos) {
            sourcesLand.shuffle()
            sourcesPortrait.shuffle()
        }
    }

    companion object {
        private const val DEDICATED_COUNTER = "dedicated_counter"
        fun makeSources(
            prefix: String,
            from: Int,
            to: Int,
            land: Boolean
        ): ArrayList<DedicatedSource> {
            val ret: ArrayList<DedicatedSource> = ArrayList(to - from + 1)
            for (i in from..to) {
                ret.add(DedicatedSource(land, "$prefix$i.webp"))
            }
            return ret
        }
    }
}