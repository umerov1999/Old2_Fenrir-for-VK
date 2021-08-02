package dev.ragnarok.fenrir.model;

import android.graphics.Color;

public class ThemeValue {

    public final int color_day_primary;
    public final int color_day_secondary;
    public final int color_night_primary;
    public final int color_night_secondary;
    public final String id;
    public final String name;

    public ThemeValue(String color_primary, String color_secondary, String id, String name) {
        color_day_primary = Color.parseColor(color_primary);
        color_day_secondary = Color.parseColor(color_secondary);
        color_night_primary = color_day_primary;
        color_night_secondary = color_day_secondary;
        this.id = id;
        this.name = name;
    }

    public ThemeValue(String color_day_primary, String color_day_secondary, String color_night_primary, String color_night_secondary, String id, String name) {
        this.color_day_primary = Color.parseColor(color_day_primary);
        this.color_day_secondary = Color.parseColor(color_day_secondary);
        this.color_night_primary = Color.parseColor(color_night_primary);
        this.color_night_secondary = Color.parseColor(color_night_secondary);
        this.id = id;
        this.name = name;
    }
}
