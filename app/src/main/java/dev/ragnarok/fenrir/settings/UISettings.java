package dev.ragnarok.fenrir.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.preference.PreferenceManager;

import java.util.Objects;

import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.fragment.PreferencesFragment;
import dev.ragnarok.fenrir.fragment.fave.FaveTabsFragment;
import dev.ragnarok.fenrir.fragment.friends.FriendsTabsFragment;
import dev.ragnarok.fenrir.fragment.search.SearchTabsFragment;
import dev.ragnarok.fenrir.place.Place;
import dev.ragnarok.fenrir.place.PlaceFactory;

class UISettings implements ISettings.IUISettings {

    private final Context app;

    UISettings(Context context) {
        app = context.getApplicationContext();
    }

    @Override
    public int getAvatarStyle() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        return preferences.getInt(PreferencesFragment.KEY_AVATAR_STYLE, AvatarStyle.CIRCLE);
    }

    @Override
    public void storeAvatarStyle(@AvatarStyle int style) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putInt(PreferencesFragment.KEY_AVATAR_STYLE, style)
                .apply();
    }

    @Override
    public String getMainThemeKey() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        return preferences.getString("app_theme", "ice");
    }

    @Override
    public int getMainTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        String theme = preferences.getString("app_theme", "ice");
        boolean Amoled = Settings.get().main().isAmoledTheme();
        if (theme == null)
            return Amoled ? R.style.App_DayNight_Ice_Amoled : R.style.App_DayNight_Ice;
        switch (theme) {
            case "fire":
                return Amoled ? R.style.App_DayNight_Fire_Amoled : R.style.App_DayNight_Fire;
            case "old_ice":
                return Amoled ? R.style.App_DayNight_OldIce_Amoled : R.style.App_DayNight_OldIce;
            case "red":
                return Amoled ? R.style.App_DayNight_Red_Amoled : R.style.App_DayNight_Red;
            case "violet":
                return Amoled ? R.style.App_DayNight_Violet_Amoled : R.style.App_DayNight_Violet;
            case "violet_green":
                return Amoled ? R.style.App_DayNight_VioletGreen_Amoled : R.style.App_DayNight_VioletGreen;
            case "green_violet":
                return Amoled ? R.style.App_DayNight_GreenViolet_Amoled : R.style.App_DayNight_GreenViolet;
            case "red_violet":
                return Amoled ? R.style.App_DayNight_RedViolet_Amoled : R.style.App_DayNight_RedViolet;
            case "gray":
                return Amoled ? R.style.App_DayNight_Gray_Amoled : R.style.App_DayNight_Gray;
            case "fire_gray":
                return Amoled ? R.style.App_DayNight_FireGray_Amoled : R.style.App_DayNight_FireGray;
            case "blue_red":
                return Amoled ? R.style.App_DayNight_BlueRed_Amoled : R.style.App_DayNight_BlueRed;
            case "blue_yellow":
                return Amoled ? R.style.App_DayNight_BlueYellow_Amoled : R.style.App_DayNight_BlueYellow;
            case "blue_violet":
                return Amoled ? R.style.App_DayNight_BlueViolet_Amoled : R.style.App_DayNight_BlueViolet;
            case "yellow_violet":
                return Amoled ? R.style.App_DayNight_YellowViolet_Amoled : R.style.App_DayNight_YellowViolet;
            case "violet_yellow":
                return Amoled ? R.style.App_DayNight_VioletYellow_Amoled : R.style.App_DayNight_VioletYellow;
            case "fuxia_neon_yellow":
                return Amoled ? R.style.App_DayNight_FuxiaNeonYellow_Amoled : R.style.App_DayNight_FuxiaNeonYellow;
            case "fuxia_neon_violet":
                return Amoled ? R.style.App_DayNight_FuxiaNeonViolet_Amoled : R.style.App_DayNight_FuxiaNeonViolet;
            case "neon_yellow_ice":
                return Amoled ? R.style.App_DayNight_NeonYellowIce_Amoled : R.style.App_DayNight_NeonYellowIce;
            case "violet_red":
                return Amoled ? R.style.App_DayNight_VioletRed_Amoled : R.style.App_DayNight_VioletRed;
            case "contrast":
                return Amoled ? R.style.App_DayNight_Contrast_Amoled : R.style.App_DayNight_Contrast;
            case "ice_green":
                return Amoled ? R.style.App_DayNight_IceGreen_Amoled : R.style.App_DayNight_IceGreen;
            case "lineage":
                return Amoled ? R.style.App_DayNight_Lineage_Amoled : R.style.App_DayNight_Lineage;
            case "green":
                return Amoled ? R.style.App_DayNight_Green_Amoled : R.style.App_DayNight_Green;
            case "violet_gray":
                return Amoled ? R.style.App_DayNight_VioletGray_Amoled : R.style.App_DayNight_VioletGray;
            case "pink_gray":
                return Amoled ? R.style.App_DayNight_PinkGray_Amoled : R.style.App_DayNight_PinkGray;
            case "ice":
            default:
                return Amoled ? R.style.App_DayNight_Ice_Amoled : R.style.App_DayNight_Ice;
        }
    }

    @Override
    public void setMainTheme(String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        preferences.edit().putString("app_theme", key).apply();
    }

    @Override
    public void switchNightMode(@NightMode int key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        preferences.edit().putString("night_switch", String.valueOf(key)).apply();
    }

    @Override
    public boolean isDarkModeEnabled(Context context) {
        int nightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return nightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    @NightMode
    @Override
    public int getNightMode() {
        String mode = PreferenceManager.getDefaultSharedPreferences(app)
                .getString("night_switch", String.valueOf(NightMode.ENABLE));
        return Integer.parseInt(mode);
    }

    @Override
    public Place getDefaultPage(int accountId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        String page = preferences.getString(PreferencesFragment.KEY_DEFAULT_CATEGORY, "last_closed");

        if ("last_closed".equals(page)) {
            int type = PreferenceManager.getDefaultSharedPreferences(app).getInt("last_closed_place_type", Place.DIALOGS);
            switch (type) {
                case Place.DIALOGS:
                    return PlaceFactory.getDialogsPlace(accountId, accountId, null);
                case Place.FEED:
                    return PlaceFactory.getFeedPlace(accountId);
                case Place.FRIENDS_AND_FOLLOWERS:
                    return PlaceFactory.getFriendsFollowersPlace(accountId, accountId, FriendsTabsFragment.TAB_ALL_FRIENDS, null);
                case Place.NOTIFICATIONS:
                    return PlaceFactory.getNotificationsPlace(accountId);
                case Place.NEWSFEED_COMMENTS:
                    return PlaceFactory.getNewsfeedCommentsPlace(accountId);
                case Place.COMMUNITIES:
                    return PlaceFactory.getCommunitiesPlace(accountId, accountId);
                case Place.VK_PHOTO_ALBUMS:
                    return PlaceFactory.getVKPhotoAlbumsPlace(accountId, accountId, null, null);
                case Place.AUDIOS:
                    return PlaceFactory.getAudiosPlace(accountId, accountId);
                case Place.DOCS:
                    return PlaceFactory.getDocumentsPlace(accountId, accountId, null);
                case Place.BOOKMARKS:
                    return PlaceFactory.getBookmarksPlace(accountId, FaveTabsFragment.TAB_PAGES);
                case Place.SEARCH:
                    return PlaceFactory.getSearchPlace(accountId, SearchTabsFragment.TAB_PEOPLE);
                case Place.VIDEOS:
                    return PlaceFactory.getVideosPlace(accountId, accountId, null);
                case Place.PREFERENCES:
                    return PlaceFactory.getPreferencesPlace(accountId);
            }
        }

        switch (page) {
            case "1":
                return PlaceFactory.getFriendsFollowersPlace(accountId, accountId, FriendsTabsFragment.TAB_ALL_FRIENDS, null);
            case "3":
                return PlaceFactory.getFeedPlace(accountId);
            case "4":
                return PlaceFactory.getNotificationsPlace(accountId);
            case "5":
                return PlaceFactory.getCommunitiesPlace(accountId, accountId);
            case "6":
                return PlaceFactory.getVKPhotoAlbumsPlace(accountId, accountId, null, null);
            case "7":
                return PlaceFactory.getVideosPlace(accountId, accountId, null);
            case "8":
                return PlaceFactory.getAudiosPlace(accountId, accountId);
            case "9":
                return PlaceFactory.getDocumentsPlace(accountId, accountId, null);
            case "10":
                return PlaceFactory.getBookmarksPlace(accountId, FaveTabsFragment.TAB_PAGES);
            case "11":
                return PlaceFactory.getSearchPlace(accountId, SearchTabsFragment.TAB_PEOPLE);
            case "12":
                return PlaceFactory.getNewsfeedCommentsPlace(accountId);
            default:
                return PlaceFactory.getDialogsPlace(accountId, accountId, null);
        }
    }

    @Override
    public void notifyPlaceResumed(int type) {
        PreferenceManager.getDefaultSharedPreferences(app).edit()
                .putInt("last_closed_place_type", type)
                .apply();
    }

    @Override
    public boolean isSystemEmoji() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("emojis_type", false);
    }

    @Override
    public boolean isEmojis_full_screen() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("emojis_full_screen", false);
    }

    @Override
    public boolean isStickers_by_theme() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("stickers_by_theme", true);
    }

    @Override
    public boolean isStickers_by_new() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("stickers_by_new", false);
    }

    @Override
    public int isPhoto_swipe_triggered_pos() {
        try {
            return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(app).getString("photo_swipe_triggered_pos", "180"));
        } catch (Exception e) {
            return 180;
        }
    }

    @Override
    public boolean isShow_profile_in_additional_page() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("show_profile_in_additional_page", true);
    }

    @SwipesChatMode
    @Override
    public int getSwipes_chat_mode() {
        try {
            return Integer.parseInt(Objects.requireNonNull(PreferenceManager.getDefaultSharedPreferences(app).getString("swipes_for_chats", "1")));
        } catch (Exception e) {
            return SwipesChatMode.SLIDR;
        }
    }

    @Override
    public boolean isDisplay_writing() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("display_writing", true);
    }
}
