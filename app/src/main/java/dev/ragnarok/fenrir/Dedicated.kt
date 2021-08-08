package dev.ragnarok.fenrir

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.AnyRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dev.ragnarok.fenrir.Dedicated.ImageDedicatedAdapter.SourceType
import dev.ragnarok.fenrir.picasso.PicassoInstance.Companion.with
import dev.ragnarok.fenrir.util.HelperSimple
import dev.ragnarok.fenrir.util.HelperSimple.needHelp
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView
import dev.ragnarok.fenrir.view.natives.video.AnimatedShapeableImageView
import java.util.*

object Dedicated {
    @JvmStatic
    @SuppressLint("ClickableViewAccessibility")
    fun showDedicated(context: Context) {
        val view = View.inflate(context, R.layout.dialog_dedicated, null)
        val pager: RecyclerView = view.findViewById(R.id.dedicated_pager)
        pager.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
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
                SourceType(R.raw.dedicated_video1),
                SourceType(R.raw.dedicated_video2)
            )
        )
        val anim: RLottieImageView = view.findViewById(R.id.dedicated_anim)
        pager.setOnTouchListener { _: View?, event: MotionEvent ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                anim.clearAnimationDrawable()
            }
            false
        }
        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setCancelable(true)
            .show()
    }

    private class ImageHolder(rootView: View) : RecyclerView.ViewHolder(
        rootView
    )

    private class ImageDedicatedAdapter(drawables: Array<SourceType>) :
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
            if (!needHelp(HelperSimple.DEDICATED_COUNTER, 2)) {
                this.drawables.shuffle()
            }
        }
    }
}