package dev.ragnarok.fenrir.dedicated

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.picasso.PicassoInstance.Companion.with
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
        with().cancelRequest(holder.imageView)
        if (!res.isVideo) {
            with().load(res.asset).into(holder.imageView)
        } else {
            //holder.imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            holder.imageView.setDecoderCallback { success: Boolean ->
                if (success) {
                    holder.imageView.playAnimation()
                } else {
                    holder.imageView.setImageResource(R.drawable.report_red)
                }
            }
            holder.imageView.fromRes(res.res)
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

    interface ClickListener {
        fun onToggleHelper()
    }

    class DedicatedHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: AnimatedShapeableImageView = itemView.findViewById(R.id.dedicated_photo)
    }
}
