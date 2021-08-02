package dev.ragnarok.fenrir.fragment

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.SparseIntArray
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.squareup.picasso3.Callback
import dev.ragnarok.fenrir.Extra
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.activity.ActivityFeatures
import dev.ragnarok.fenrir.activity.ActivityUtils
import dev.ragnarok.fenrir.activity.SendAttachmentsActivity
import dev.ragnarok.fenrir.adapter.horizontal.ImageAdapter
import dev.ragnarok.fenrir.domain.ILikesInteractor
import dev.ragnarok.fenrir.fragment.base.BaseMvpFragment
import dev.ragnarok.fenrir.listener.BackPressCallback
import dev.ragnarok.fenrir.model.*
import dev.ragnarok.fenrir.module.FenrirNative
import dev.ragnarok.fenrir.module.parcel.ParcelNative
import dev.ragnarok.fenrir.mvp.core.IPresenterFactory
import dev.ragnarok.fenrir.mvp.presenter.photo.*
import dev.ragnarok.fenrir.mvp.view.IPhotoPagerView
import dev.ragnarok.fenrir.picasso.PicassoInstance
import dev.ragnarok.fenrir.place.Place
import dev.ragnarok.fenrir.place.PlaceFactory
import dev.ragnarok.fenrir.place.PlaceUtil
import dev.ragnarok.fenrir.settings.CurrentTheme
import dev.ragnarok.fenrir.settings.Settings
import dev.ragnarok.fenrir.util.AppPerms
import dev.ragnarok.fenrir.util.CustomToast.Companion.CreateCustomToast
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
import java.util.*


class PhotoPagerFragment : BaseMvpFragment<PhotoPagerPresenter, IPhotoPagerView>(), IPhotoPagerView,
    GoBackCallback, BackPressCallback {
    companion object {
        private const val EXTRA_PHOTOS = "photos"
        private const val EXTRA_NEED_UPDATE = "need_update"
        private val SIZES = SparseIntArray()
        private const val DEFAULT_PHOTO_SIZE = PhotoSize.W

        @JvmStatic
        fun buildArgsForSimpleGallery(
            aid: Int, index: Int, photos: ArrayList<Photo>,
            needUpdate: Boolean
        ): Bundle {
            val args = Bundle()
            args.putInt(Extra.ACCOUNT_ID, aid)
            args.putParcelableArrayList(EXTRA_PHOTOS, photos)
            args.putInt(Extra.INDEX, index)
            args.putBoolean(EXTRA_NEED_UPDATE, needUpdate)
            return args
        }

        @JvmStatic
        fun buildArgsForAlbum(
            aid: Int,
            albumId: Int,
            ownerId: Int,
            source: TmpSource,
            position: Int,
            readOnly: Boolean,
            invert: Boolean
        ): Bundle {
            val args = Bundle()
            args.putInt(Extra.ACCOUNT_ID, aid)
            args.putInt(Extra.OWNER_ID, ownerId)
            args.putInt(Extra.ALBUM_ID, albumId)
            args.putInt(Extra.INDEX, position)
            args.putBoolean(Extra.READONLY, readOnly)
            args.putBoolean(Extra.INVERT, invert)
            args.putParcelable(Extra.SOURCE, source)
            return args
        }

        @JvmStatic
        fun buildArgsForAlbum(
            aid: Int,
            albumId: Int,
            ownerId: Int,
            photos: ArrayList<Photo>,
            position: Int,
            readOnly: Boolean,
            invert: Boolean
        ): Bundle {
            val args = Bundle()
            args.putInt(Extra.ACCOUNT_ID, aid)
            args.putInt(Extra.OWNER_ID, ownerId)
            args.putInt(Extra.ALBUM_ID, albumId)
            args.putInt(Extra.INDEX, position)
            args.putBoolean(Extra.READONLY, readOnly)
            args.putBoolean(Extra.INVERT, invert)
            if (FenrirNative.isNativeLoaded() && Settings.get().other().isNative_parcel) {
                args.putLong(EXTRA_PHOTOS, ParcelNative.createParcelableList(photos))
            } else {
                args.putParcelableArrayList(EXTRA_PHOTOS, photos)
            }
            return args
        }

        @JvmStatic
        fun buildArgsForFave(aid: Int, photos: ArrayList<Photo>, index: Int): Bundle {
            val args = Bundle()
            args.putInt(Extra.ACCOUNT_ID, aid)
            args.putParcelableArrayList(EXTRA_PHOTOS, photos)
            args.putInt(Extra.INDEX, index)
            return args
        }

        @JvmStatic
        fun newInstance(placeType: Int, args: Bundle?): PhotoPagerFragment {
            val targetArgs = Bundle()
            targetArgs.putAll(args)
            targetArgs.putInt(Extra.PLACE_TYPE, placeType)
            val fragment = PhotoPagerFragment()
            fragment.arguments = targetArgs
            return fragment
        }

        private fun addPhotoSizeToMenu(menu: PopupMenu, id: Int, size: Int, selectedItem: Int) {
            menu.menu
                .add(0, id, 0, getTitleForPhotoSize(size)).isChecked = selectedItem == size
        }

        private fun getTitleForPhotoSize(size: Int): String {
            return when (size) {
                PhotoSize.X -> 604.toString() + "px"
                PhotoSize.Y -> 807.toString() + "px"
                PhotoSize.Z -> 1024.toString() + "px"
                PhotoSize.W -> 2048.toString() + "px"
                else -> throw IllegalArgumentException("Unsupported size")
            }
        }

        init {
            SIZES.put(1, PhotoSize.X)
            SIZES.put(2, PhotoSize.Y)
            SIZES.put(3, PhotoSize.Z)
            SIZES.put(4, PhotoSize.W)
        }
    }

    private val requestWritePermission = AppPerms.requestPermissions(
        this,
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    ) {
        presenter?.fireWriteExternalStoragePermissionResolved()
    }

    private val mGoBackAnimationAdapter = WeakGoBackAnimationAdapter(this)
    private var mViewPager: ViewPager2? = null
    private var mButtonWithUser: CircleCounterButton? = null
    private var mButtonLike: CircleCounterButton? = null
    private var mButtonComments: CircleCounterButton? = null
    private var buttonShare: CircleCounterButton? = null
    private var mLoadingProgressBar: RLottieImageView? = null
    private var mToolbar: Toolbar? = null
    private var mButtonsRoot: View? = null
    private var mPreviewsRecycler: RecyclerView? = null
    private var mButtonRestore: MaterialButton? = null
    private var mPagerAdapter: Adapter? = null
    private var mCanSaveYourself = false
    private var mCanDelete = false
    private val bShowPhotosLine = Settings.get().other().isShow_photos_line
    private val mAdapterRecycler = ImageAdapter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_photo_pager_new, container, false)
        mLoadingProgressBar = root.findViewById(R.id.loading_progress_bar)
        mButtonRestore = root.findViewById(R.id.button_restore)
        mButtonsRoot = root.findViewById(R.id.buttons)
        mPreviewsRecycler = root.findViewById(R.id.previews_photos)
        mToolbar = root.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(mToolbar)
        mViewPager = root.findViewById(R.id.view_pager)
        mViewPager?.offscreenPageLimit = 1
        mViewPager?.setPageTransformer(
            Utils.createPageTransform(
                Settings.get().main().viewpager_page_transform
            )
        )
        mViewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                presenter?.firePageSelected(position)

                if (bShowPhotosLine) {
                    val currentSelected = mAdapterRecycler.getSelectedItem()
                    if (currentSelected != position) {
                        mAdapterRecycler.selectPosition(position)
                        if (currentSelected < position) {
                            mPreviewsRecycler?.scrollToPosition(position)
                        } else {
                            if (position == 0) {
                                mPreviewsRecycler?.scrollToPosition(position)
                            } else
                                mPreviewsRecycler?.scrollToPosition(position)
                        }
                    }
                }
            }
        })
        mButtonLike = root.findViewById(R.id.like_button)
        mButtonLike?.setOnClickListener { presenter?.fireLikeClick() }
        mButtonLike?.setOnLongClickListener {
            presenter?.fireLikeLongClick()
            false
        }
        mButtonWithUser = root.findViewById(R.id.with_user_button)
        mButtonWithUser?.setOnClickListener { presenter?.fireWithUserClick() }
        mButtonComments = root.findViewById(R.id.comments_button)
        mButtonComments?.setOnClickListener { presenter?.fireCommentsButtonClick() }
        buttonShare = root.findViewById(R.id.share_button)
        buttonShare?.setOnClickListener { presenter?.fireShareButtonClick() }
        mButtonRestore?.setOnClickListener { presenter?.fireButtonRestoreClick() }

        if (bShowPhotosLine) {
            mPreviewsRecycler?.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            mAdapterRecycler.setListener(object : ImageAdapter.OnRecyclerImageClickListener {
                override fun onRecyclerImageClick(index: Int) {
                    mViewPager?.currentItem = index
                }
            })
            mPreviewsRecycler?.adapter = mAdapterRecycler
        } else {
            mPreviewsRecycler?.visibility = View.GONE
        }

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.vkphoto_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.photo_size -> onPhotoSizeClicked()
            R.id.save_on_drive -> {
                presenter?.fireSaveOnDriveClick()
                return true
            }
            R.id.save_yourself -> presenter?.fireSaveYourselfClick()
            R.id.action_delete -> presenter?.fireDeleteClick()
            R.id.info -> presenter?.fireInfoButtonClick()
            R.id.detect_qr -> presenter?.fireDetectQRClick(requireActivity())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun goToLikesList(accountId: Int, ownerId: Int, photoId: Int) {
        PlaceFactory.getLikesCopiesPlace(
            accountId,
            "photo",
            ownerId,
            photoId,
            ILikesInteractor.FILTER_LIKES
        ).tryOpenWith(requireActivity())
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (!Utils.isHiddenCurrent()) {
            menu.findItem(R.id.save_yourself).isVisible = mCanSaveYourself
            menu.findItem(R.id.action_delete).isVisible = mCanDelete
        } else {
            menu.findItem(R.id.save_yourself).isVisible = false
            menu.findItem(R.id.action_delete).isVisible = false
        }
        val imageSize = photoSizeFromPrefs
        menu.findItem(R.id.photo_size).title = getTitleForPhotoSize(imageSize)
    }

    private fun onPhotoSizeClicked() {
        val view = requireActivity().findViewById<View>(R.id.photo_size)
        val current = photoSizeFromPrefs
        val popupMenu = PopupMenu(requireActivity(), view)
        for (i in 0 until SIZES.size()) {
            val key = SIZES.keyAt(i)
            val value = SIZES[key]
            addPhotoSizeToMenu(popupMenu, key, value, current)
        }
        popupMenu.menu.setGroupCheckable(0, true, true)
        popupMenu.setOnMenuItemClickListener { item: MenuItem ->
            val key = item.itemId
            Settings.get()
                .main()
                .setPrefDisplayImageSize(SIZES[key])
            requireActivity().invalidateOptionsMenu()
            true
        }
        popupMenu.show()
    }

    override fun displayAccountNotSupported() {}

    override fun getPresenterFactory(saveInstanceState: Bundle?): IPresenterFactory<PhotoPagerPresenter> =
        object : IPresenterFactory<PhotoPagerPresenter> {
            override fun create(): PhotoPagerPresenter {
                val placeType = requireArguments().getInt(Extra.PLACE_TYPE)
                val aid = requireArguments().getInt(Extra.ACCOUNT_ID)
                when (placeType) {
                    Place.SIMPLE_PHOTO_GALLERY -> {
                        val index = requireArguments().getInt(Extra.INDEX)
                        val needUpdate = requireArguments().getBoolean(EXTRA_NEED_UPDATE)
                        val photos: ArrayList<Photo> =
                            requireArguments().getParcelableArrayList(EXTRA_PHOTOS)!!
                        return SimplePhotoPresenter(
                            photos,
                            index,
                            needUpdate,
                            aid,
                            requireActivity(),
                            saveInstanceState
                        )
                    }
                    Place.VK_PHOTO_ALBUM_GALLERY_SAVED -> {
                        val indexx = requireArguments().getInt(Extra.INDEX)
                        val ownerId = requireArguments().getInt(Extra.OWNER_ID)
                        val albumId = requireArguments().getInt(Extra.ALBUM_ID)
                        val readOnly = requireArguments().getBoolean(Extra.READONLY)
                        val invert = requireArguments().getBoolean(Extra.INVERT)
                        val source: TmpSource = requireArguments().getParcelable(Extra.SOURCE)!!
                        return PhotoAlbumPagerPresenter(
                            indexx,
                            aid,
                            ownerId,
                            albumId,
                            source,
                            readOnly,
                            invert,
                            requireActivity(),
                            saveInstanceState
                        )
                    }
                    Place.VK_PHOTO_ALBUM_GALLERY -> {
                        val indexx = requireArguments().getInt(Extra.INDEX)
                        val ownerId = requireArguments().getInt(Extra.OWNER_ID)
                        val albumId = requireArguments().getInt(Extra.ALBUM_ID)
                        val readOnly = requireArguments().getBoolean(Extra.READONLY)
                        val invert = requireArguments().getBoolean(Extra.INVERT)
                        val photos_album: ArrayList<Photo> =
                            if (FenrirNative.isNativeLoaded() && Settings.get()
                                    .other().isNative_parcel
                            ) ParcelNative.loadParcelableArrayList(
                                requireArguments().getLong(
                                    EXTRA_PHOTOS
                                ), Photo.NativeCreator
                            ) else requireArguments().getParcelableArrayList(EXTRA_PHOTOS)!!
                        if (FenrirNative.isNativeLoaded() && Settings.get()
                                .other().isNative_parcel
                        ) {
                            requireArguments().putLong(EXTRA_PHOTOS, 0)
                        }
                        return PhotoAlbumPagerPresenter(
                            indexx,
                            aid,
                            ownerId,
                            albumId,
                            photos_album,
                            readOnly,
                            invert,
                            requireActivity(),
                            saveInstanceState
                        )
                    }
                    Place.FAVE_PHOTOS_GALLERY -> {
                        val findex = requireArguments().getInt(Extra.INDEX)
                        val favePhotos: ArrayList<Photo> =
                            requireArguments().getParcelableArrayList(EXTRA_PHOTOS)!!
                        return FavePhotoPagerPresenter(
                            favePhotos,
                            findex,
                            aid,
                            requireActivity(),
                            saveInstanceState
                        )
                    }
                    Place.VK_PHOTO_TMP_SOURCE -> {
                        val source: TmpSource = requireArguments().getParcelable(Extra.SOURCE)!!
                        return TmpGalleryPagerPresenter(
                            aid,
                            source,
                            requireArguments().getInt(Extra.INDEX),
                            requireActivity(),
                            saveInstanceState
                        )
                    }
                }
                throw UnsupportedOperationException()
            }
        }

    override fun setupLikeButton(visible: Boolean, like: Boolean, likes: Int) {
        mButtonLike?.visibility = if (visible) View.VISIBLE else View.GONE
        mButtonLike?.isActive = like
        mButtonLike?.count = likes
        mButtonLike?.setIcon(if (like) R.drawable.heart_filled else R.drawable.heart)
    }

    override fun setupWithUserButton(users: Int) {
        mButtonWithUser?.visibility = if (users > 0) View.VISIBLE else View.GONE
        mButtonWithUser?.count = users
    }

    override fun setupShareButton(visible: Boolean) {
        buttonShare?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setupCommentsButton(visible: Boolean, count: Int) {
        mButtonComments?.visibility = if (visible) View.VISIBLE else View.GONE
        mButtonComments?.count = count
    }

    override fun displayPhotos(photos: List<Photo>, initialIndex: Int) {
        if (bShowPhotosLine) {
            if (photos.size <= 1) {
                mAdapterRecycler.setData(Collections.emptyList())
                mAdapterRecycler.notifyDataSetChanged()
            } else {
                mAdapterRecycler.setData(photos)
                mAdapterRecycler.notifyDataSetChanged()
                mAdapterRecycler.selectPosition(initialIndex)
            }
        }
        mPagerAdapter = Adapter(photos)
        mViewPager?.adapter = mPagerAdapter
        mViewPager?.setCurrentItem(initialIndex, false)
    }

    override fun setToolbarTitle(title: String?) {
        ActivityUtils.supportToolbarFor(this)?.title = title
    }

    override fun setToolbarSubtitle(subtitle: String?) {
        ActivityUtils.supportToolbarFor(this)?.subtitle = subtitle
    }

    override fun sharePhoto(accountId: Int, photo: Photo) {
        val items = arrayOf(
            getString(R.string.share_link),
            getString(R.string.repost_send_message),
            getString(R.string.repost_to_wall)
        )
        MaterialAlertDialogBuilder(requireActivity())
            .setItems(items) { _: DialogInterface?, i: Int ->
                when (i) {
                    0 -> Utils.shareLink(requireActivity(), photo.generateWebLink(), photo.text)
                    1 -> SendAttachmentsActivity.startForSendAttachments(
                        requireActivity(),
                        accountId,
                        photo
                    )
                    2 -> presenter?.firePostToMyWallClick()
                }
            }
            .setCancelable(true)
            .setTitle(R.string.share_photo_title)
            .show()
    }

    override fun postToMyWall(photo: Photo, accountId: Int) {
        PlaceUtil.goToPostCreation(
            requireActivity(),
            accountId,
            accountId,
            EditingPostType.TEMP,
            listOf(photo)
        )
    }

    override fun requestWriteToExternalStoragePermission() {
        requestWritePermission.launch()
    }

    override fun setButtonRestoreVisible(visible: Boolean) {
        mButtonRestore?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun setupOptionMenu(canSaveYourself: Boolean, canDelete: Boolean) {
        mCanSaveYourself = canSaveYourself
        mCanDelete = canDelete
        requireActivity().invalidateOptionsMenu()
    }

    override fun goToComments(aid: Int, commented: Commented) {
        PlaceFactory.getCommentsPlace(aid, commented, null).tryOpenWith(requireActivity())
    }

    override fun displayPhotoListLoading(loading: Boolean) {
        mLoadingProgressBar?.visibility = if (loading) View.VISIBLE else View.GONE
        if (loading) {
            mLoadingProgressBar?.fromRes(
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
            mLoadingProgressBar?.playAnimation()
        } else {
            mLoadingProgressBar?.stopAnimation()
        }
    }

    override fun setButtonsBarVisible(visible: Boolean) {
        mButtonsRoot?.visibility = if (visible) View.VISIBLE else View.GONE
        mPreviewsRecycler?.visibility = if (visible && bShowPhotosLine) View.VISIBLE else View.GONE
    }

    override fun setToolbarVisible(visible: Boolean) {
        mToolbar?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun rebindPhotoAt(position: Int) {
        mPagerAdapter?.notifyItemChanged(position)
        if (bShowPhotosLine && mAdapterRecycler.getSize() > 1) {
            mAdapterRecycler.notifyItemChanged(position)
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

    override fun onResume() {
        super.onResume()
        ActivityFeatures.Builder()
            .begin()
            .setHideNavigationMenu(true)
            .setBarsColored(false, false)
            .build()
            .apply(requireActivity())
    }

    private fun canGoBack(): Boolean {
        return requireActivity().supportFragmentManager.backStackEntryCount > 1
    }

    @get:PhotoSize
    val photoSizeFromPrefs: Int
        get() = Settings.get()
            .main()
            .getPrefDisplayImageSize(DEFAULT_PHOTO_SIZE)

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

    private inner class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view), Callback {
        val reload: FloatingActionButton
        private val mPicassoLoadCallback: WeakPicassoLoadCallback
        val photo: TouchImageView
        val progress: RLottieImageView
        private var mLoadingNow = false
        fun bindTo(@NonNull photo_image: Photo) {
            photo.resetZoom()
            val size: Int = photoSizeFromPrefs
            val url = photo_image.getUrlForSize(size, true)
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
                CreateCustomToast(requireActivity()).showToastError(R.string.empty_url)
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
            photo.setOnClickListener { presenter?.firePhotoTap() }
        }
    }

    private inner class Adapter(val mPhotos: List<Photo>) :
        RecyclerView.Adapter<PhotoViewHolder>() {
        @SuppressLint("ClickableViewAccessibility")
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): PhotoViewHolder {
            val ret = PhotoViewHolder(
                LayoutInflater.from(container.context)
                    .inflate(R.layout.content_photo_page, container, false)
            )
            val ui = from(ret.photo)
            ui.settle = SettleOnTopAction()
            ui.sideEffect =
                VerticalSwipeBehavior.PropertySideEffect(View.ALPHA, View.SCALE_X, View.SCALE_Y)
            val clampDelegate = VerticalSwipeBehavior.BelowFractionalClamp(3f, 3f)
            ui.clamp = VerticalSwipeBehavior.SensitivityClamp(0.5f, clampDelegate, 0.5f)
            ui.listener = object : VerticalSwipeBehavior.SwipeListener {
                override fun onReleased() {
                    container.requestDisallowInterceptTouchEvent(false)
                }

                override fun onCaptured() {
                    container.requestDisallowInterceptTouchEvent(true)
                }

                override fun onPreSettled(diff: Int) {}
                override fun onPostSettled(success: Boolean) {
                    if (success) {
                        goBack()
                    } else container.requestDisallowInterceptTouchEvent(false)
                }
            }
            if (Settings.get().other().isDownload_photo_tap) {
                ret.photo.setOnLongClickListener {
                    presenter?.fireSaveOnDriveClick()
                    true
                }
            }
            ret.photo.setOnTouchListener { view: View, event: MotionEvent ->
                if (event.pointerCount >= 2 || view.canScrollHorizontally(1) && view.canScrollHorizontally(
                        -1
                    )
                ) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                            ui.canSwipe = false
                            container.requestDisallowInterceptTouchEvent(true)
                            return@setOnTouchListener false
                        }
                        MotionEvent.ACTION_UP -> {
                            ui.canSwipe = true
                            container.requestDisallowInterceptTouchEvent(false)
                            return@setOnTouchListener true
                        }
                    }
                }
                true
            }
            return ret
        }

        override fun onViewDetachedFromWindow(holder: PhotoViewHolder) {
            super.onViewDetachedFromWindow(holder)
            PicassoInstance.with().cancelRequest(holder.photo)
        }

        override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
            val photo = mPhotos[position]
            holder.bindTo(photo)
        }

        override fun getItemCount(): Int {
            return mPhotos.size
        }
    }
}
