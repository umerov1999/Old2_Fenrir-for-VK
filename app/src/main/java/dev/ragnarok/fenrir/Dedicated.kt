package dev.ragnarok.fenrir

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.AnyRes
import androidx.annotation.NonNull
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.audio.AudioAttributes
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.ragnarok.fenrir.Dedicated.ImageDedicatedAdapter.SourceType
import dev.ragnarok.fenrir.media.exo.ExoUtil
import dev.ragnarok.fenrir.picasso.PicassoInstance.Companion.with
import dev.ragnarok.fenrir.place.PlaceFactory
import dev.ragnarok.fenrir.settings.Settings
import dev.ragnarok.fenrir.util.HelperSimple
import dev.ragnarok.fenrir.util.HelperSimple.needHelp
import dev.ragnarok.fenrir.util.Utils
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView
import dev.ragnarok.fenrir.view.natives.video.AnimatedShapeableImageView
import java.util.*

class Dedicated : DialogFragment() {
    private var exoPlayer: SimpleExoPlayer? = null
    private fun createPlayer(context: Context) {
        var extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
        when (Settings.get().other().fFmpegPlugin) {
            0 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_OFF
            1 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON
            2 -> extensionRenderer = DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER
        }
        exoPlayer = SimpleExoPlayer.Builder(
            context,
            DefaultRenderersFactory(context).setExtensionRendererMode(extensionRenderer)
        ).build()
        exoPlayer?.setWakeMode(C.WAKE_MODE_NETWORK)
        exoPlayer?.setMediaSource(
            ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(
                    context,
                    Constants.USER_AGENT(Account_Types.BY_TYPE)
                )
            )
                .createMediaSource(Utils.makeMediaItem("file:///android_asset/dedicated/dedicated_audio.ogg"))
        )
        exoPlayer?.prepare()
        exoPlayer?.setAudioAttributes(
            AudioAttributes.Builder().setContentType(C.CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA).build(), true
        )
        exoPlayer?.let {
            ExoUtil.startPlayer(it)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        createPlayer(requireActivity())
        val view = View.inflate(context, R.layout.dialog_dedicated, null)
        val swipe: ImageView = view.findViewById(R.id.dedicated_swipe)
        val pager: RecyclerView = view.findViewById(R.id.dedicated_pager)
        pager.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val shuffle = !needHelp(HelperSimple.DEDICATED_COUNTER, 2)
        if (shuffle) {
            swipe.visibility = View.GONE
        }
        (view.findViewById(R.id.dedicated_button) as MaterialButton).setOnClickListener {
            PlaceFactory.getOwnerWallPlace(Settings.get().accounts().current, 255645173, null)
                .tryOpenWith(requireActivity())
            dismiss()
        }
        pager.adapter = ImageDedicatedAdapter(
            arrayOf(
                SourceType("dedicated1.webp"),
                SourceType("dedicated2.webp"),
                SourceType("dedicated3.webp"),
                SourceType("dedicated4.webp"),
                SourceType("dedicated5.webp"),
                SourceType("dedicated6.webp"),
                SourceType("dedicated7.webp"),
                SourceType("dedicated8.webp"),
                SourceType("dedicated9.webp"),
                SourceType("dedicated10.webp"),
                SourceType("dedicated11.webp"),
                SourceType("dedicated12.webp"),
                SourceType("dedicated13.webp"),
                SourceType("dedicated14.webp"),
                SourceType("dedicated15.webp"),
                SourceType("dedicated16.webp"),
                SourceType("dedicated17.webp"),
                SourceType("dedicated18.webp"),
                SourceType("dedicated19.webp"),
                SourceType("dedicated20.webp"),
                SourceType("dedicated21.webp"),
                SourceType("dedicated22.webp"),
                SourceType("dedicated23.webp"),
                SourceType(R.raw.dedicated_video1),
                SourceType(R.raw.dedicated_video2)
            ),
            shuffle
        )
        val anim: RLottieImageView = view.findViewById(R.id.dedicated_anim)
        pager.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                anim.clearAnimationDrawable()
                anim.visibility = View.GONE
                pager.setOnTouchListener(null)
            }
            false
        }
        return MaterialAlertDialogBuilder(requireActivity())
            .setView(view)
            .setCancelable(true)
            .create()
    }

    override fun dismiss() {
        super.dismiss()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.stop()
        exoPlayer?.release()
    }

    private class ImageHolder(rootView: View) : RecyclerView.ViewHolder(
        rootView
    )

    private class ImageDedicatedAdapter(drawables: Array<SourceType>, shuffle: Boolean) :
        RecyclerView.Adapter<ImageHolder>() {
        private val drawables: ArrayList<SourceType> = ArrayList(listOf(*drawables))
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
            return ImageHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_dedicated, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ImageHolder, position: Int) {
            val res = drawables[position]
            val imageView: AnimatedShapeableImageView =
                holder.itemView.findViewById(R.id.dedicated_photo)
            with().cancelRequest(imageView)
            if (!res.isVideo) {
                imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                with().load(res.asset).into(imageView)
            } else {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.setDecoderCallback { success: Boolean ->
                    if (success) {
                        imageView.playAnimation()
                    } else {
                        imageView.setImageResource(R.drawable.report_red)
                    }
                }
                imageView.fromRes(res.res)
            }
        }

        override fun getItemCount(): Int {
            return drawables.size
        }

        class SourceType {
            var isVideo: Boolean

            @AnyRes
            var res = 0
            var asset: String? = null

            constructor(@AnyRes video_res: Int) {
                isVideo = true
                res = video_res
            }

            constructor(asset_file: String) {
                isVideo = false
                asset = "file:///android_asset/dedicated/$asset_file"
            }
        }

        init {
            if (shuffle) {
                this.drawables.shuffle()
            }
        }
    }

    companion object {
        fun showDedicated(activity: FragmentActivity) {
            Dedicated().show(activity.supportFragmentManager, "dedicated")
        }
    }
}