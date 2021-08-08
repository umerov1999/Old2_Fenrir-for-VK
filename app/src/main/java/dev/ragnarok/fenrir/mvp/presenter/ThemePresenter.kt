package dev.ragnarok.fenrir.mvp.presenter

import android.os.Bundle
import dev.ragnarok.fenrir.model.ThemeValue
import dev.ragnarok.fenrir.mvp.core.AbsPresenter
import dev.ragnarok.fenrir.mvp.view.IThemeView

class ThemePresenter(savedInstanceState: Bundle?) : AbsPresenter<IThemeView>(savedInstanceState) {
    private val data: Array<ThemeValue>
    private fun createInitialData(): Array<ThemeValue> {
        return arrayOf(
            ThemeValue("#448AFF", "#1E88E5", "ice", "Ice"),
            ThemeValue("#448AFF", "#82B1FF", "old_ice", "Old Ice"),
            ThemeValue("#FF9800", "#FFA726", "fire", "Fire"),
            ThemeValue("#FF0000", "#F44336", "red", "Red"),
            ThemeValue("#9800FF", "#8500FF", "violet", "Violet"),
            ThemeValue("#444444", "#777777", "gray", "Gray"),
            ThemeValue("#448AFF", "#8500FF", "blue_violet", "Ice Violet"),
            ThemeValue("#448AFF", "#FF0000", "blue_red", "Ice Red"),
            ThemeValue("#448AFF", "#FFA726", "blue_yellow", "Ice Fire"),
            ThemeValue("#FF9800", "#8500FF", "yellow_violet", "Fire Violet"),
            ThemeValue("#8500FF", "#FF9800", "violet_yellow", "Violet Fire"),
            ThemeValue("#9800FF", "#F44336", "violet_red", "Violet Red"),
            ThemeValue("#F44336", "#9800FF", "red_violet", "Red Violet"),
            ThemeValue(
                "#000000", "#444444", "#FFFFFF",
                "#777777", "contrast", "Contrast"
            ),
            ThemeValue("#FF5722", "#777777", "fire_gray", "Fire Gray"),
            ThemeValue("#8500FF", "#777777", "violet_gray", "Violet Gray"),
            ThemeValue("#FF4F8B", "#777777", "pink_gray", "Pink Gray"),
            ThemeValue("#8500FF", "#268000", "violet_green", "Violet Green"),
            ThemeValue("#268000", "#8500FF", "green_violet", "Green Violet"),
            ThemeValue("#448AFF", "#4CAF50", "ice_green", "Ice Green"),
            ThemeValue("#268000", "#4CAF50", "green", "Green"),
            ThemeValue("#167C80", "#63FFDE", "lineage", "Lineage"),
            ThemeValue("#FE59C2", "#CFFF04", "fuxia_neon_yellow", "Fuxia Neon Yellow"),
            ThemeValue("#FE4164", "#BC13FE", "fuxia_neon_violet", "Fuxia Neon Violet"),
            ThemeValue("#AAD300", "#04D9FF", "neon_yellow_ice", "Neon Yellow Ice")
        )
    }

    override fun onGuiCreated(viewHost: IThemeView) {
        super.onGuiCreated(viewHost)
        viewHost.displayData(data)
    }

    init {
        data = createInitialData()
    }
}