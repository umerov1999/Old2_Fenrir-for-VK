package dev.ragnarok.fenrir.settings.theme

import androidx.annotation.StyleRes
import dev.ragnarok.fenrir.R
import dev.ragnarok.fenrir.settings.Settings

object ThemesController {
    val themes = arrayOf(
        ThemeValue(
            "ice",
            "#448AFF",
            "#1E88E5",
            "Ice",
            R.style.App_DayNight_Ice,
            R.style.App_DayNight_Ice_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "old_ice",
            "#448AFF",
            "#82B1FF",
            "Old Ice",
            R.style.App_DayNight_OldIce,
            R.style.App_DayNight_OldIce_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "fire",
            "#FF9800",
            "#FFA726",
            "Fire",
            R.style.App_DayNight_Fire,
            R.style.App_DayNight_Fire_Amoled
        ),
        ThemeValue(
            "red",
            "#FF0000",
            "#F44336",
            "Red",
            R.style.App_DayNight_Red,
            R.style.App_DayNight_Red_Amoled
        ),
        ThemeValue(
            "violet",
            "#9800FF",
            "#8500FF",
            "Violet",
            R.style.App_DayNight_Violet,
            R.style.App_DayNight_Violet_Amoled
        ),
        ThemeValue(
            "gray",
            "#444444",
            "#777777",
            "Gray",
            R.style.App_DayNight_Gray,
            R.style.App_DayNight_Gray_Amoled
        ),
        ThemeValue(
            "blue_violet",
            "#448AFF",
            "#8500FF",
            "Ice Violet",
            R.style.App_DayNight_BlueViolet,
            R.style.App_DayNight_BlueViolet_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "blue_red",
            "#448AFF",
            "#FF0000",
            "Ice Red",
            R.style.App_DayNight_BlueRed,
            R.style.App_DayNight_BlueRed_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "blue_yellow",
            "#448AFF",
            "#FFA726",
            "Ice Fire",
            R.style.App_DayNight_BlueYellow,
            R.style.App_DayNight_BlueYellow_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "yellow_violet",
            "#FF9800",
            "#8500FF",
            "Fire Violet",
            R.style.App_DayNight_YellowViolet,
            R.style.App_DayNight_YellowViolet_Amoled
        ),
        ThemeValue(
            "violet_yellow",
            "#8500FF",
            "#FF9800",
            "Violet Fire",
            R.style.App_DayNight_VioletYellow,
            R.style.App_DayNight_VioletYellow_Amoled
        ),
        ThemeValue(
            "violet_red",
            "#9800FF",
            "#F44336",
            "Violet Red",
            R.style.App_DayNight_VioletRed,
            R.style.App_DayNight_VioletRed_Amoled
        ),
        ThemeValue(
            "red_violet",
            "#F44336",
            "#9800FF",
            "Red Violet",
            R.style.App_DayNight_RedViolet,
            R.style.App_DayNight_RedViolet_Amoled
        ),
        ThemeValue(
            "contrast",
            "#000000",
            "#444444",
            "#FFFFFF",
            "#777777",
            "Contrast",
            R.style.App_DayNight_Contrast,
            R.style.App_DayNight_Contrast_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "orange",
            "#FF5722",
            "#FF6F00",
            "Orange",
            R.style.App_DayNight_Orange,
            R.style.App_DayNight_Orange_Amoled
        ),
        ThemeValue(
            "orange_gray",
            "#FF5722",
            "#777777",
            "Orange Gray",
            R.style.App_DayNight_OrangeGray,
            R.style.App_DayNight_OrangeGray_Amoled
        ),
        ThemeValue(
            "violet_gray",
            "#8500FF",
            "#777777",
            "Violet Gray",
            R.style.App_DayNight_VioletGray,
            R.style.App_DayNight_VioletGray_Amoled
        ),
        ThemeValue(
            "pink_gray",
            "#FF4F8B",
            "#777777",
            "Pink Gray",
            R.style.App_DayNight_PinkGray,
            R.style.App_DayNight_PinkGray_Amoled
        ),
        ThemeValue(
            "violet_green",
            "#8500FF",
            "#268000",
            "Violet Green",
            R.style.App_DayNight_VioletGreen,
            R.style.App_DayNight_VioletGreen_Amoled
        ),
        ThemeValue(
            "green_violet",
            "#268000",
            "#8500FF",
            "Green Violet",
            R.style.App_DayNight_GreenViolet,
            R.style.App_DayNight_GreenViolet_Amoled
        ),
        ThemeValue(
            "ice_green",
            "#448AFF",
            "#4CAF50",
            "Ice Green",
            R.style.App_DayNight_IceGreen,
            R.style.App_DayNight_IceGreen_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "green",
            "#268000",
            "#4CAF50",
            "Green",
            R.style.App_DayNight_Green,
            R.style.App_DayNight_Green_Amoled
        ).toast("#4d7198", "#448AFF"),
        ThemeValue(
            "lineage",
            "#167C80",
            "#63FFDE",
            "Lineage",
            R.style.App_DayNight_Lineage,
            R.style.App_DayNight_Lineage_Amoled
        ),
        ThemeValue(
            "fuxia_neon_yellow",
            "#FE59C2",
            "#CFFF04",
            "Fuxia Neon Yellow",
            R.style.App_DayNight_FuxiaNeonYellow,
            R.style.App_DayNight_FuxiaNeonYellow_Amoled
        ),
        ThemeValue(
            "fuxia_neon_violet",
            "#FE4164",
            "#BC13FE",
            "Fuxia Neon Violet",
            R.style.App_DayNight_FuxiaNeonViolet,
            R.style.App_DayNight_FuxiaNeonViolet_Amoled
        ),
        ThemeValue(
            "neon_yellow_ice",
            "#AAD300",
            "#04D9FF",
            "Neon Yellow Ice",
            R.style.App_DayNight_NeonYellowIce,
            R.style.App_DayNight_NeonYellowIce_Amoled
        ),

        ThemeValue(
            "random",
            "#ffffff",
            "#ffffff",
            "Random",
            R.style.App_DayNight_Ice,
            R.style.App_DayNight_Ice_Amoled
        )
    )
    private val randomTheme = themes.random()
    private val defaultTheme = ThemeValue(
        "ice",
        "#448AFF",
        "#1E88E5",
        "Ice",
        R.style.App_DayNight_Ice,
        R.style.App_DayNight_Ice_Amoled
    ).toast("#4d7198", "#448AFF")

    private fun getCurrentTheme(): ThemeValue {
        val key: String? = Settings.get().ui().mainThemeKey
        key ?: return defaultTheme

        if (key == "random") {
            return randomTheme
        }
        for (i in themes) {
            if (i.id == key) {
                return i
            }
        }
        return defaultTheme
    }

    @StyleRes
    fun currentStyle(): Int {
        val t = getCurrentTheme()
        return if (Settings.get().main().isAmoledTheme) t.themeAmoledRes else t.themeRes
    }

    fun toastColor(isReadMessage: Boolean): Int {
        val t = getCurrentTheme()
        return if (isReadMessage) t.colorReadToast else t.colorToast
    }
}