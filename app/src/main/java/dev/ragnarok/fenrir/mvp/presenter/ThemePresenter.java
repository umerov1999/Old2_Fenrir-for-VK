package dev.ragnarok.fenrir.mvp.presenter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import dev.ragnarok.fenrir.model.ThemeValue;
import dev.ragnarok.fenrir.mvp.core.AbsPresenter;
import dev.ragnarok.fenrir.mvp.view.IThemeView;


public class ThemePresenter extends AbsPresenter<IThemeView> {

    private final ThemeValue[] data;

    public ThemePresenter(@Nullable Bundle savedInstanceState) {
        super(savedInstanceState);
        data = createInitialData();
    }

    private ThemeValue[] createInitialData() {
        return new ThemeValue[]{
                new ThemeValue("#448AFF", "#1E88E5", "ice", "Ice"),
                new ThemeValue("#448AFF", "#82B1FF", "old_ice", "Old Ice"),
                new ThemeValue("#FF9800", "#FFA726", "fire", "Fire"),
                new ThemeValue("#FF0000", "#F44336", "red", "Red"),
                new ThemeValue("#9800FF", "#8500FF", "violet", "Violet"),
                new ThemeValue("#444444", "#777777", "gray", "Gray"),
                new ThemeValue("#448AFF", "#8500FF", "blue_violet", "Ice Violet"),
                new ThemeValue("#448AFF", "#FF0000", "blue_red", "Ice Red"),
                new ThemeValue("#448AFF", "#FFA726", "blue_yellow", "Ice Fire"),
                new ThemeValue("#FF9800", "#8500FF", "yellow_violet", "Fire Violet"),
                new ThemeValue("#8500FF", "#FF9800", "violet_yellow", "Violet Fire"),
                new ThemeValue("#9800FF", "#F44336", "violet_red", "Violet Red"),
                new ThemeValue("#F44336", "#9800FF", "red_violet", "Red Violet"),
                new ThemeValue("#000000", "#444444", "#FFFFFF",
                        "#777777", "contrast", "Contrast"),
                new ThemeValue("#FF5722", "#777777", "fire_gray", "Fire Gray"),
                new ThemeValue("#8500FF", "#777777", "violet_gray", "Violet Gray"),
                new ThemeValue("#FF4F8B", "#777777", "pink_gray", "Pink Gray"),
                new ThemeValue("#8500FF", "#268000", "violet_green", "Violet Green"),
                new ThemeValue("#268000", "#8500FF", "green_violet", "Green Violet"),
                new ThemeValue("#448AFF", "#4CAF50", "ice_green", "Ice Green"),
                new ThemeValue("#268000", "#4CAF50", "green", "Green"),
                new ThemeValue("#167C80", "#63FFDE", "lineage", "Lineage"),
                new ThemeValue("#FE59C2", "#CFFF04", "fuxia_neon_yellow", "Fuxia Neon Yellow"),
                new ThemeValue("#FE4164", "#BC13FE", "fuxia_neon_violet", "Fuxia Neon Violet"),
                new ThemeValue("#AAD300", "#04D9FF", "neon_yellow_ice", "Neon Yellow Ice")
        };
    }

    @Override
    public void onGuiCreated(@NonNull IThemeView viewHost) {
        super.onGuiCreated(viewHost);
        viewHost.displayData(data);
    }
}
