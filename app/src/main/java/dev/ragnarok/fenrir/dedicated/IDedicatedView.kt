package dev.ragnarok.fenrir.dedicated

import dev.ragnarok.fenrir.mvp.core.IMvpView
import dev.ragnarok.fenrir.mvp.view.IErrorView
import dev.ragnarok.fenrir.mvp.view.base.IAccountDependencyView

interface IDedicatedView : IAccountDependencyView, IMvpView, IErrorView {
    fun displayData(
        sources: ArrayList<DedicatedSource>,
        position: Int,
        showHeart: Boolean,
        showHelper: Boolean,
        showSwipe: Boolean
    )

    fun openUserWall(accountId: Int, userId: Int)
    fun requestOrientation()
    fun doToggleHelper(visible: Boolean, showSwipe: Boolean)
    fun createPlayer()
}