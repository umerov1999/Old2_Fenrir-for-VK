package dev.ragnarok.fenrir.adapter;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.model.ThemeValue;
import dev.ragnarok.fenrir.module.FenrirNative;
import dev.ragnarok.fenrir.settings.CurrentTheme;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView;

public class ThemeAdapter extends RecyclerView.Adapter<ThemeAdapter.ViewHolder> {

    private List<ThemeValue> data;
    private ClickListener clickListener;

    public ThemeAdapter(List<ThemeValue> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_theme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ThemeValue category = data.get(position);
        boolean isDark = Settings.get().ui().isDarkModeEnabled(holder.itemView.getContext());
        boolean isSelected = Settings.get().ui().getMainThemeKey().equals(category.id);

        holder.title.setText(category.name);
        holder.primary.setBackgroundColor(isDark ? category.color_night_primary : category.color_day_primary);
        holder.secondary.setBackgroundColor(isDark ? category.color_night_secondary : category.color_day_secondary);
        holder.selected.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        if (Utils.hasMarshmallow() && FenrirNative.isNativeLoaded()) {
            if (isSelected) {
                holder.selected.fromRes(R.raw.theme_selected, Utils.dp(120), Utils.dp(120), new int[]{0x333333, CurrentTheme.getColorWhite(holder.selected.getContext()), 0x777777, CurrentTheme.getColorPrimary(holder.selected.getContext()), 0x999999, CurrentTheme.getColorSecondary(holder.selected.getContext())});
                holder.selected.playAnimation();
            } else {
                holder.selected.stopAnimation();
            }
        } else {
            if (isSelected) {
                holder.selected.setImageResource(R.drawable.theme_select);
            }
        }

        holder.clicked.setOnClickListener(v -> clickListener.onClick(position, category));
        holder.gradient.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{isDark ? category.color_night_primary : category.color_day_primary, isDark ? category.color_night_secondary : category.color_day_secondary}));
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setData(ThemeValue[] data) {
        this.data = Arrays.asList(data);
        notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(int index, ThemeValue value);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final ImageView primary;
        final ImageView secondary;
        final RLottieImageView selected;
        final ImageView gradient;
        final ViewGroup clicked;
        final TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            primary = itemView.findViewById(R.id.theme_icon_primary);
            secondary = itemView.findViewById(R.id.theme_icon_secondary);
            selected = itemView.findViewById(R.id.selected);
            clicked = itemView.findViewById(R.id.theme_type);
            title = itemView.findViewById(R.id.item_title);
            gradient = itemView.findViewById(R.id.theme_icon_gradient);
        }
    }
}
