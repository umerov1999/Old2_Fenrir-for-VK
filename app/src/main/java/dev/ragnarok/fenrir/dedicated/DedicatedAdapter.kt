package dev.ragnarok.fenrir.dedicated

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso3.BitmapTarget
import com.squareup.picasso3.Picasso
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.picasso.PicassoInstance.Companion.with
import dev.ragnarok.fenrir.view.CHBAnimDrawable
import dev.ragnarok.fenrir.view.natives.video.AnimatedShapeableImageView

class DedicatedAdapter(
    private var drawables: ArrayList<DedicatedSource>
) :
    RecyclerView.Adapter<DedicatedAdapter.DedicatedHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DedicatedHolder {
        return DedicatedHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_dedicated, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DedicatedHolder, position: Int) {
        val res = drawables[position]
        holder.imageView.setOnClickListener {
            clickListener?.onToggleHelper()
        }
        if (!isDark) {
            with().cancelRequest(holder.imageView)
        } else {
            with().cancelRequest(holder.darkTarget)
        }
        if (!res.isVideo) {
            if (!isDark) {
                with().load(res.asset).into(holder.imageView)
            } else {
                with().load(res.asset).into(holder.darkTarget)
            }
        } else {
            //holder.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageView.setDecoderCallback { success: Boolean ->
                if (success) {
                    holder.imageView.playAnimation()
                } else {
                    holder.imageView.setImageResource(R.drawable.report_red)
                }
            }
            if (!isDark) {
                holder.imageView.fromRes(res.res)
            } else {
                holder.imageView.fromResFade(res.res)
            }
        }
    }

    override fun getItemCount(): Int {
        return drawables.size
    }

    private var clickListener: ClickListener? = null

    fun setClickListener(clickListener: ClickListener?) {
        this.clickListener = clickListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: ArrayList<DedicatedSource>) {
        drawables = data
        notifyDataSetChanged()
    }

    fun setDataOnly(data: ArrayList<DedicatedSource>) {
        drawables = data
    }

    fun toDark(pos: Int) {
        isDark = true
        if (!drawables[pos].isVideo) {
            darkCurrent = pos
        }
        notifyDataSetChanged()
    }

    fun notifyDark(pos: Int) {
        if (drawables[pos].isVideo) {
            return
        }
        darkCurrent = pos
        notifyItemChanged(pos)
    }

    private var isDark: Boolean = false
    private var darkCurrent: Int = -1

    interface ClickListener {
        fun onToggleHelper()
    }

    inner class DedicatedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: AnimatedShapeableImageView = itemView.findViewById(R.id.dedicated_photo)
        val darkTarget = object : BitmapTarget {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                CHBAnimDrawable.setBitmap(
                    imageView,
                    imageView.context,
                    bitmap,
                    darkCurrent == bindingAdapterPosition,
                    false
                )
            }

            override fun onBitmapFailed(e: Exception, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

        }
    }
}
