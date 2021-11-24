package dev.ragnarok.fenrir.dedicated

import android.os.Bundle
import com.google.android.exoplayer2.ExoPlayer
import dev.ragnarok.fenrir.Extensions.Companion.fromIOToMain
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.mvp.presenter.base.AccountDependencyPresenter
import dev.ragnarok.fenrir.util.HelperSimple
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

class DedicatedPresenter(accountId: Int, savedInstanceState: Bundle?) :
    AccountDependencyPresenter<IDedicatedView>(accountId, savedInstanceState) {
    private val sourcesPortrait: ArrayList<DedicatedSource> = makeSources("dedicated", 0, 49, false)
    private val sourcesLand: ArrayList<DedicatedSource> = makeSources("dedicated", 1, 23, true)
    private val shufflePhotos = !HelperSimple.needHelp(DEDICATED_COUNTER, 1)
    private var land = false
    private var positionLand = 0
    private var positionPortrait = 0
    private var showHeart = true
    private var showHelper = true
    private var isDark = false
    private var exoPlayer: ExoPlayer? = null
    private var disposableDark = Disposable.disposed()
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
        disposableDark.dispose()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    fun fireReceivePlayer(player: ExoPlayer) {
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
            !shufflePhotos,
            isDark
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
        if (isDark) {
            view?.notifyCurrentDark(position)
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
        sourcesPortrait.add(DedicatedSource(R.raw.dedicated_video3))

        if (shufflePhotos) {
            sourcesLand.shuffle()
            sourcesPortrait.shuffle()
        }
        disposableDark = Completable.create {
            it.onComplete()
        }.delay(20, TimeUnit.SECONDS)
            .fromIOToMain()
            .subscribe {
                showHeart = false
                isDark = true
                view?.toggleDarkHeart()
                view?.goToStartDark(if (land) positionLand else positionPortrait)
                exoPlayer?.let { view?.playDarkAudio(it) }
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
