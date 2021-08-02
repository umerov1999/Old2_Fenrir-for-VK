package dev.ragnarok.fenrir.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.ragnarok.fenrir.Constants;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.model.Photo;
import dev.ragnarok.fenrir.model.PhotoSize;
import dev.ragnarok.fenrir.picasso.PicassoInstance;
import dev.ragnarok.fenrir.settings.CurrentTheme;
import dev.ragnarok.fenrir.util.AppTextUtils;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.view.AspectRatioImageView;

public class LocalServerPhotosAdapter extends RecyclerView.Adapter<LocalServerPhotosAdapter.ViewHolder> {
    private final int mColorSecondaryWithAlpha;
    private final Context mContext;
    private List<Photo> data;
    private PhotoSelectionListener photoSelectionListener;

    public LocalServerPhotosAdapter(Context context, List<Photo> data) {
        this.data = data;
        mContext = context;
        mColorSecondaryWithAlpha = Utils.adjustAlpha(CurrentTheme.getColorSecondary(context), 0.60F);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_local_server_photo, parent, false);
        return new ViewHolder(root);
    }

    @Override
    public void onBindViewHolder(@NonNull LocalServerPhotosAdapter.ViewHolder viewHolder, int position) {
        Photo photo = data.get(position);

        PicassoInstance.with()
                .load(photo.getUrlForSize(PhotoSize.X, false))
                .tag(Constants.PICASSO_TAG)
                .placeholder(R.drawable.background_gray)
                .into(viewHolder.photoImageView);

        viewHolder.photoImageView.setOnClickListener(v -> {
            if (photoSelectionListener != null) {
                photoSelectionListener.onPhotoClicked(viewHolder.getBindingAdapterPosition(), photo);
            }
        });

        viewHolder.bottomTop.setBackgroundColor(mColorSecondaryWithAlpha);
        viewHolder.tvDate.setText(AppTextUtils.getDateFromUnixTimeShorted(mContext, photo.getDate()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(List<Photo> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void setPhotoSelectionListener(PhotoSelectionListener photoSelectionListener) {
        this.photoSelectionListener = photoSelectionListener;
    }

    public interface PhotoSelectionListener {
        void onPhotoClicked(int position, Photo photo);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final AspectRatioImageView photoImageView;
        final TextView tvDate;
        final ViewGroup bottomTop;

        public ViewHolder(View itemView) {
            super(itemView);
            photoImageView = itemView.findViewById(R.id.imageView);
            tvDate = itemView.findViewById(R.id.vk_photo_item_date);
            bottomTop = itemView.findViewById(R.id.vk_photo_item_top);
        }
    }
}
