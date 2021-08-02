package dev.ragnarok.fenrir.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso3.Callback
import dev.ragnarok.fenrir.App.Companion.instance
import dev.ragnarok.fenrir.Extra
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.activity.ActivityFeatures
import dev.ragnarok.fenrir.fragment.base.BaseFragment
import dev.ragnarok.fenrir.listener.BackPressCallback
import dev.ragnarok.fenrir.picasso.PicassoInstance
import dev.ragnarok.fenrir.settings.CurrentTheme
import dev.ragnarok.fenrir.settings.Settings
import dev.ragnarok.fenrir.util.AppPerms
import dev.ragnarok.fenrir.util.CustomToast.Companion.CreateCustomToast
import dev.ragnarok.fenrir.util.DownloadWorkUtils.doDownloadPhoto
import dev.ragnarok.fenrir.util.DownloadWorkUtils.makeLegalFilename
import dev.ragnarok.fenrir.util.Utils
import dev.ragnarok.fenrir.util.Utils.nonEmpty
import dev.ragnarok.fenrir.view.CircleCounterButton
import dev.ragnarok.fenrir.view.TouchImageView
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView
import dev.ragnarok.fenrir.view.pager.GoBackCallback
import dev.ragnarok.fenrir.view.pager.WeakGoBackAnimationAdapter
import dev.ragnarok.fenrir.view.pager.WeakPicassoLoadCallback
import dev.ragnarok.fenrir.view.swipehelper.VerticalSwipeBehavior
import dev.ragnarok.fenrir.view.swipehelper.VerticalSwipeBehavior.Companion.from
import dev.ragnarok.fenrir.view.swipehelper.VerticalSwipeBehavior.SettleOnTopAction
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class SinglePhotoFragment : BaseFragment(), GoBackCallback, BackPressCallback {
    private val mGoBackAnimationAdapter = WeakGoBackAnimationAdapter(this)
    private var url: String? = null
    private var prefix: String? = null
    private var photo_prefix: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = requireArguments().getString(Extra.URL)
        prefix = makeLegalFilename(requireArguments().getString(Extra.STATUS)!!, null)
        photo_prefix = makeLegalFilename(requireArguments().getString(Extra.KEY)!!, null)
    }

    private val requestWritePermission = AppPerms.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ) {
        doSaveOnDrive(false)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_single_url_photo, container, false)
        val mDownload: CircleCounterButton = root.findViewById(R.id.button_download)
        mDownload.visibility =
            if (url!!.contains("content://") || url!!.contains("file://")) View.GONE else View.VISIBLE
        val ret = PhotoViewHolder(root)
        ret.bindTo(url!!)
        val ui = from(ret.photo)
        ui.settle = SettleOnTopAction()
        ui.sideEffect =
            VerticalSwipeBehavior.PropertySideEffect(View.ALPHA, View.SCALE_X, View.SCALE_Y)
        val clampDelegate = VerticalSwipeBehavior.BelowFractionalClamp(3f, 3f)
        ui.clamp = VerticalSwipeBehavior.SensitivityClamp(0.5f, clampDelegate, 0.5f)
        ui.listener = object : VerticalSwipeBehavior.SwipeListener {
            override fun onReleased() {}
            override fun onCaptured() {}
            override fun onPreSettled(diff: Int) {}
            override fun onPostSettled(success: Boolean) {
                if (success) {
                    goBack()
                }
            }
        }
        ret.photo.setOnLongClickListener {
            doSaveOnDrive(true)
            true
        }
        ret.photo.setOnTouchListener { view: View, event: MotionEvent ->
            if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && view.canScrollHorizontally(
                    -1
                )
            ) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        ui.canSwipe = false
                        container?.requestDisallowInterceptTouchEvent(true)
                        return@setOnTouchListener false
                    }
                    MotionEvent.ACTION_UP -> {
                        ui.canSwipe = true
                        container?.requestDisallowInterceptTouchEvent(false)
                        return@setOnTouchListener true
                    }
                }
            }
            true
        }
        mDownload.setOnClickListener { doSaveOnDrive(true) }
        return root
    }

    private fun doSaveOnDrive(Request: Boolean) {
        if (Request) {
            if (!AppPerms.hasReadWriteStoragePermission(instance)) {
                requestWritePermission.launch()
            }
        }
        var dir = File(Settings.get().other().photoDir)
        if (!dir.isDirectory) {
            val created = dir.mkdirs()
            if (!created) {
                CreateCustomToast(requireActivity()).showToastError("Can't create directory $dir")
                return
            }
        } else dir.setLastModified(Calendar.getInstance().time.time)
        if (prefix != null && Settings.get().other().isPhoto_to_user_dir) {
            val dir_final = File(dir.absolutePath + "/" + prefix)
            if (!dir_final.isDirectory) {
                val created = dir_final.mkdirs()
                if (!created) {
                    CreateCustomToast(requireActivity()).showToastError("Can't create directory $dir")
                    return
                }
            } else dir_final.setLastModified(Calendar.getInstance().time.time)
            dir = dir_final
        }
        val DOWNLOAD_DATE_FORMAT: DateFormat =
            SimpleDateFormat("yyyyMMdd_HHmmss", Utils.getAppLocale())
        url?.let {
            doDownloadPhoto(
                requireActivity(),
                it,
                dir.absolutePath,
                prefix + "." + photo_prefix + ".profile." + DOWNLOAD_DATE_FORMAT.format(Date())
            )
        }
    }

    override fun goBack() {
        if (isAdded) {
            if (canGoBack()) {
                requireActivity().supportFragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }
    }

    private fun canGoBack(): Boolean {
        return requireActivity().supportFragmentManager.backStackEntryCount > 1
    }

    override fun onBackPressed(): Boolean {
        val objectAnimatorPosition = ObjectAnimator.ofFloat(view, "translationY", -600f)
        val objectAnimatorAlpha = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f)
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(objectAnimatorPosition, objectAnimatorAlpha)
        animatorSet.duration = 200
        animatorSet.addListener(mGoBackAnimationAdapter)
        animatorSet.start()
        return false
    }

    override fun onResume() {
        super.onResume()
        ActivityFeatures.Builder()
            .begin()
            .setHideNavigationMenu(true)
            .setBarsColored(false, false)
            .build()
            .apply(requireActivity())
    }

    private inner class PhotoViewHolder(view: View) : Callback {
        val reload: FloatingActionButton
        private val mPicassoLoadCallback: WeakPicassoLoadCallback
        val photo: TouchImageView
        val progress: RLottieImageView
        private var mLoadingNow = false
        fun bindTo(@NonNull url: String?) {
            reload.setOnClickListener {
                reload.visibility = View.INVISIBLE
                if (nonEmpty(url)) {
                    loadImage(url)
                } else PicassoInstance.with().cancelRequest(photo)
            }
            if (nonEmpty(url)) {
                loadImage(url)
            } else {
                PicassoInstance.with().cancelRequest(photo)
                CreateCustomToast(requireActivity()).showToast(R.string.empty_url)
            }
        }

        private fun resolveProgressVisibility() {
            progress.visibility = if (mLoadingNow) View.VISIBLE else View.GONE
            if (mLoadingNow) {
                progress.fromRes(
                    R.raw.loading,
                    Utils.dp(80F),
                    Utils.dp(80F),
                    intArrayOf(
                        0xffffff,
                        CurrentTheme.getColorPrimary(requireActivity()),
                        0x000000,
                        CurrentTheme.getColorSecondary(requireActivity())
                    )
                )
                progress.playAnimation()
            } else {
                progress.stopAnimation()
            }
        }

        private fun loadImage(@NonNull url: String?) {
            mLoadingNow = true
            resolveProgressVisibility()
            PicassoInstance.with()
                .load(url)
                .into(photo, mPicassoLoadCallback)
        }

        @IdRes
        private fun idOfImageView(): Int {
            return R.id.image_view
        }

        @IdRes
        private fun idOfProgressBar(): Int {
            return R.id.progress_bar
        }

        override fun onSuccess() {
            mLoadingNow = false
            resolveProgressVisibility()
            reload.visibility = View.INVISIBLE
        }

        override fun onError(t: Throwable) {
            mLoadingNow = false
            resolveProgressVisibility()
            reload.visibility = View.VISIBLE
        }

        init {
            photo = view.findViewById(idOfImageView())
            photo.maxZoom = 8f
            photo.doubleTapScale = 2f
            photo.doubleTapMaxZoom = 4f
            progress = view.findViewById(idOfProgressBar())
            reload = view.findViewById(R.id.goto_button)
            mPicassoLoadCallback = WeakPicassoLoadCallback(this)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(args: Bundle?): SinglePhotoFragment {
            val fragment = SinglePhotoFragment()
            fragment.arguments = args
            return fragment
        }

        @JvmStatic
        fun buildArgs(url: String?, download_prefix: String?, photo_prefix: String?): Bundle {
            val args = Bundle()
            args.putString(Extra.URL, url)
            args.putString(Extra.STATUS, download_prefix)
            args.putString(Extra.KEY, photo_prefix)
            return args
        }
    }
}
