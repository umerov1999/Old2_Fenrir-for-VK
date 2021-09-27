package dev.ragnarok.fenrir.dedicated

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import dev.ragnarok.fenrir.Account_Types
import dev.ragnarok.fenrir.Constants
import dev.ragnarok.fenrir.Extra
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.fragment.base.BaseMvpBottomSheetDialogFragment
import dev.ragnarok.fenrir.media.exo.ExoUtil
import dev.ragnarok.fenrir.mvp.core.IPresenterFactory
import dev.ragnarok.fenrir.place.PlaceFactory
import dev.ragnarok.fenrir.settings.Settings
import dev.ragnarok.fenrir.util.Utils
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView
import dev.ragnarok.fenrir.view.pager.DepthTransformer
import java.util.*
import kotlin.collections.ArrayList

class DedicatedDialog :
    BaseMvpBottomSheetDialogFragment<DedicatedPresenter, IDedicatedView>(),
    IDedicatedView, DedicatedAdapter.ClickListener {
    private var mGoto: ViewGroup? = null
    private var mText: MaterialTextView? = null
    private var swipe: ImageView? = null
    private var dedicatedIcon: ImageView? = null
    private var anim: RLottieImageView? = null
    private var darkAnim: RLottieImageView? = null
    private var pager: ViewPager2? = null
    private var adapter: DedicatedAdapter? = null

    override
    fun createPlayer() {
        var extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        when (Settings.get().other().fFmpegPlugin) {
            0 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
            1 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
            2 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        }
        val exoPlayer = SimpleExoPlayer.Builder(
            requireActivity(),
            DefaultRenderersFactory(requireActivity()).setExtensionRendererMode(extensionRenderer)
        ).build()
        exoPlayer.setWakeMode(C.WAKE_MODE_NETWORK)
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(
                    requireActivity(),
                    Constants.USER_AGENT(Account_Types.BY_TYPE)
                )
            )
                .createMediaSource(Utils.makeMediaItem("file:///android_asset/dedicated/dedicated_audio.ogg"))
        )
        exoPlayer.prepare()
        exoPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA).build(), true
        )
        exoPlayer.let {
            ExoUtil.startPlayer(it)
        }
        presenter?.fireReceivePlayer(exoPlayer)
    }

    override fun toggleDarkHeart() {
        anim?.visibility = View.GONE
        darkAnim?.visibility = View.VISIBLE
        darkAnim?.fromRes(R.raw.unheart, Utils.dp(200f), Utils.dp(200f))
        darkAnim?.playAnimation()
        dedicatedIcon?.setImageResource(R.drawable.dedicated_icon_dark)
    }

    override fun playDarkAudio(exoPlayer: SimpleExoPlayer) {
        exoPlayer.setMediaSource(
            ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(
                    requireActivity(),
                    Constants.USER_AGENT(Account_Types.BY_TYPE)
                )
            )
                .createMediaSource(Utils.makeMediaItem("file:///android_asset/dedicated/unrequited_love.ogg"))
        )
        exoPlayer.prepare()
        exoPlayer.repeatMode = REPEAT_MODE_ONE
        exoPlayer.setAudioAttributes(
            AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA).build(), true
        )
        exoPlayer.let {
            ExoUtil.startPlayer(it)
        }
    }

    override fun goToStartDark(pos: Int) {
        adapter?.toDark(pos)
    }

    override fun notifyCurrentDark(pos: Int) {
        pager?.post {
            adapter?.notifyDark(pos)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireActivity(), theme)
        val behavior = dialog.behavior
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true
        return dialog
    }

    override fun openUserWall(accountId: Int, userId: Int) {
        PlaceFactory.getOwnerWallPlace(accountId, 255645173, null)
            .tryOpenWith(requireActivity())
        dismiss()
    }

    override fun requestOrientation() {
        presenter?.fireReceiveOrientation(Utils.isLandscape(requireActivity()))
    }

    override fun displayData(
        sources: ArrayList<DedicatedSource>,
        position: Int,
        showHeart: Boolean,
        showHelper: Boolean,
        showSwipe: Boolean,
        darkHeart: Boolean
    ) {
        if (showHeart) {
            anim?.visibility = View.VISIBLE
            anim?.fromRes(R.raw.heart, Utils.dp(200f), Utils.dp(200f))
            anim?.playAnimation()
            pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    if (position == 0 || anim?.visibility == View.GONE) {
                        return
                    }

                    anim?.let {
                        val fadeOut = ObjectAnimator.ofFloat(it, "alpha", 1f, 0f)
                        fadeOut.duration = 2000
                        val mAnimationSet = AnimatorSet()

                        mAnimationSet.play(fadeOut)
                        mAnimationSet.addListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                super.onAnimationEnd(animation)
                                it.clearAnimationDrawable()
                                it.visibility = View.GONE
                                presenter?.hideHeart()
                            }
                        })
                        mAnimationSet.start()
                        pager?.unregisterOnPageChangeCallback(this)
                    }
                }
            })
        } else {
            anim?.clearAnimationDrawable()
            anim?.visibility = View.GONE
        }
        swipe?.visibility = if (showSwipe) View.VISIBLE else View.GONE
        doToggleHelper(showHelper, showSwipe)
        if (darkHeart) {
            toggleDarkHeart()
            adapter?.setDataOnly(sources)
            adapter?.toDark(position)
        } else {
            adapter?.setData(sources)
        }
        pager?.setCurrentItem(position, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = View.inflate(context, R.layout.dialog_dedicated, null)
        swipe = view.findViewById(R.id.dedicated_swipe)
        dedicatedIcon = view.findViewById(R.id.dedicated_icon)
        pager = view.findViewById(R.id.dedicated_pager)
        mGoto = view.findViewById(R.id.dedicated_go_to)
        mText = view.findViewById(R.id.dedicated_text)
        pager?.offscreenPageLimit = 1
        pager?.setPageTransformer(DepthTransformer())

        (view.findViewById(R.id.dedicated_button) as MaterialButton).setOnClickListener {
            presenter?.fireUserClick()
        }
        adapter = DedicatedAdapter(ArrayList(Collections.emptyList()))
        adapter?.setClickListener(this)

        pager?.adapter = adapter
        anim = view.findViewById(R.id.dedicated_anim)
        darkAnim = view.findViewById(R.id.dedicated_dark_anim)
        pager?.setRestorePositionListener {
            presenter?.getCurrentPosition(
                Utils.isLandscape(
                    requireActivity()
                )
            ) ?: 0
        }
        pager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                presenter?.nextPosition(position)
            }
        })
        return view
    }

    override
    fun onToggleHelper() {
        presenter?.fireToggleHelper()
    }

    override
    fun doToggleHelper(visible: Boolean, showSwipe: Boolean) {
        mGoto?.visibility = if (visible) View.VISIBLE else View.GONE
        mText?.visibility = if (visible) View.VISIBLE else View.GONE
        if (showSwipe) {
            swipe?.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    companion object {
        fun showDedicated(activity: FragmentActivity, accountId: Int) {
            val args = Bundle()
            args.putInt(Extra.ACCOUNT_ID, accountId)
            val dialog = DedicatedDialog()
            dialog.arguments = args
            dialog.show(activity.supportFragmentManager, "dedicated")
        }
    }

    override fun getPresenterFactory(saveInstanceState: Bundle?): IPresenterFactory<DedicatedPresenter> =
        object : IPresenterFactory<DedicatedPresenter> {
            override fun create(): DedicatedPresenter {
                val aid = requireArguments().getInt(Extra.ACCOUNT_ID)
                return DedicatedPresenter(
                    aid,
                    saveInstanceState
                )
            }
        }
}