package dev.ragnarok.fenrir.dedicated

import com.google.android.exoplayer2.ExoPlayer
import dev.ragnarok.fenrir.mvp.core.IMvpView
import dev.ragnarok.fenrir.mvp.view.IErrorView
import dev.ragnarok.fenrir.mvp.view.base.IAccountDependencyView

interface IDedicatedView : IAccountDependencyView, IMvpView, IErrorView {
    fun displayData(
        sources: ArrayList<DedicatedSource>,
        position: Int,
        showHeart: Boolean,
        showHelper: Boolean,
        showSwipe: Boolean,
        darkHeart: Boolean
    )

    fun openUserWall(accountId: Int, userId: Int)
    fun requestOrientation()
    fun doToggleHelper(visible: Boolean, showSwipe: Boolean)
    fun createPlayer()
    fun toggleDarkHeart()
    fun goToStartDark(pos: Int)
    fun notifyCurrentDark(pos: Int)
    fun playDarkAudio(exoPlayer: ExoPlayer)
}