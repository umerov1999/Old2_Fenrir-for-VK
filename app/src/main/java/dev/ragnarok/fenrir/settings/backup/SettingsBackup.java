package dev.ragnarok.fenrir.settings.backup;

import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.JsonObject;

import javax.annotation.Nullable;

import dev.ragnarok.fenrir.Injection;
import dev.ragnarok.fenrir.fragment.PreferencesFragment;
import dev.ragnarok.fenrir.settings.Settings;
import io.reactivex.rxjava3.annotations.NonNull;

public class SettingsBackup {
    private final SettingCollector[] settings;

    public SettingsBackup() {
        //Main
        settings = new SettingCollector[]{
                new SettingCollector("send_by_enter", SettingTypes.TYPE_BOOL),
                new SettingCollector("amoled_theme", SettingTypes.TYPE_BOOL),
                new SettingCollector("audio_round_icon", SettingTypes.TYPE_BOOL),
                new SettingCollector("use_long_click_download", SettingTypes.TYPE_BOOL),
                new SettingCollector("revert_play_audio", SettingTypes.TYPE_BOOL),
                new SettingCollector("is_player_support_volume", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_bot_keyboard", SettingTypes.TYPE_BOOL),
                new SettingCollector("my_message_no_color", SettingTypes.TYPE_BOOL),
                new SettingCollector("notification_bubbles", SettingTypes.TYPE_BOOL),
                new SettingCollector("messages_menu_down", SettingTypes.TYPE_BOOL),
                new SettingCollector("image_size", SettingTypes.TYPE_STRING),
                new SettingCollector("start_news", SettingTypes.TYPE_STRING),
                new SettingCollector("crypt_version", SettingTypes.TYPE_STRING),
                new SettingCollector("photo_preview_size", SettingTypes.TYPE_STRING),
                new SettingCollector("viewpager_page_transform", SettingTypes.TYPE_STRING),
                new SettingCollector("player_cover_transform", SettingTypes.TYPE_STRING),
                new SettingCollector("pref_display_photo_size", SettingTypes.TYPE_INT),
                new SettingCollector("photo_rounded_view", SettingTypes.TYPE_STRING),
                new SettingCollector("font_size", SettingTypes.TYPE_STRING),
                new SettingCollector("custom_tabs", SettingTypes.TYPE_BOOL),
                new SettingCollector("webview_night_mode", SettingTypes.TYPE_BOOL),
                new SettingCollector("load_history_notif", SettingTypes.TYPE_BOOL),
                new SettingCollector("snow_mode", SettingTypes.TYPE_BOOL),
                new SettingCollector("dont_write", SettingTypes.TYPE_BOOL),
                new SettingCollector("over_ten_attach", SettingTypes.TYPE_BOOL),
                //UI
                new SettingCollector(PreferencesFragment.KEY_AVATAR_STYLE, SettingTypes.TYPE_INT),
                new SettingCollector("app_theme", SettingTypes.TYPE_STRING),
                new SettingCollector("night_switch", SettingTypes.TYPE_STRING),
                new SettingCollector(PreferencesFragment.KEY_DEFAULT_CATEGORY, SettingTypes.TYPE_STRING),
                new SettingCollector("last_closed_place_type", SettingTypes.TYPE_INT),
                new SettingCollector("emojis_type", SettingTypes.TYPE_BOOL),
                new SettingCollector("emojis_full_screen", SettingTypes.TYPE_BOOL),
                new SettingCollector("stickers_by_theme", SettingTypes.TYPE_BOOL),
                new SettingCollector("stickers_by_new", SettingTypes.TYPE_BOOL),
                new SettingCollector("photo_swipe_triggered_pos", SettingTypes.TYPE_STRING),
                new SettingCollector("show_profile_in_additional_page", SettingTypes.TYPE_BOOL),
                new SettingCollector("swipes_for_chats", SettingTypes.TYPE_STRING),
                new SettingCollector("display_writing", SettingTypes.TYPE_BOOL),
                //Other
                new SettingCollector("swipes_for_chats", SettingTypes.TYPE_STRING),
                new SettingCollector("broadcast", SettingTypes.TYPE_BOOL),
                new SettingCollector("comments_desc", SettingTypes.TYPE_BOOL),
                new SettingCollector("keep_longpoll", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_error_fcm", SettingTypes.TYPE_BOOL),
                new SettingCollector("settings_no_push", SettingTypes.TYPE_BOOL),
                new SettingCollector("max_bitmap_resolution", SettingTypes.TYPE_STRING),
                new SettingCollector("ffmpeg_audio_codecs", SettingTypes.TYPE_STRING),
                new SettingCollector("lifecycle_music_service", SettingTypes.TYPE_STRING),
                new SettingCollector("autoplay_gif", SettingTypes.TYPE_BOOL),
                new SettingCollector("strip_news_repost", SettingTypes.TYPE_BOOL),
                new SettingCollector("ad_block_story_news", SettingTypes.TYPE_BOOL),
                new SettingCollector("vk_api_domain", SettingTypes.TYPE_STRING),
                new SettingCollector("vk_auth_domain", SettingTypes.TYPE_STRING),
                new SettingCollector("developer_mode", SettingTypes.TYPE_BOOL),
                new SettingCollector("force_cache", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_history", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_wall_cover", SettingTypes.TYPE_BOOL),
                new SettingCollector("custom_chat_color", SettingTypes.TYPE_INT),
                new SettingCollector("custom_chat_color_second", SettingTypes.TYPE_INT),
                new SettingCollector("custom_chat_color_usage", SettingTypes.TYPE_BOOL),
                new SettingCollector("custom_message_color", SettingTypes.TYPE_INT),
                new SettingCollector("custom_second_message_color", SettingTypes.TYPE_INT),
                new SettingCollector("custom_message_color_usage", SettingTypes.TYPE_BOOL),
                new SettingCollector("info_reading", SettingTypes.TYPE_BOOL),
                new SettingCollector("auto_read", SettingTypes.TYPE_BOOL),
                new SettingCollector("not_update_dialogs", SettingTypes.TYPE_BOOL),
                new SettingCollector("be_online", SettingTypes.TYPE_BOOL),
                new SettingCollector("donate_anim_set", SettingTypes.TYPE_STRING),
                new SettingCollector("use_stop_audio", SettingTypes.TYPE_BOOL),
                new SettingCollector("blur_for_player", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_mini_player", SettingTypes.TYPE_BOOL),
                new SettingCollector("enable_last_read", SettingTypes.TYPE_BOOL),
                new SettingCollector("not_read_show", SettingTypes.TYPE_BOOL),
                new SettingCollector("headers_in_dialog", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_recent_dialogs", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_audio_top", SettingTypes.TYPE_BOOL),
                new SettingCollector("use_internal_downloader", SettingTypes.TYPE_BOOL),
                new SettingCollector("music_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("photo_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("video_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("docs_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("sticker_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("sticker_dir", SettingTypes.TYPE_STRING),
                new SettingCollector("photo_to_user_dir", SettingTypes.TYPE_BOOL),
                new SettingCollector("delete_cache_images", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_encryption", SettingTypes.TYPE_BOOL),
                new SettingCollector("download_photo_tap", SettingTypes.TYPE_BOOL),
                new SettingCollector("audio_save_mode_button", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_mutual_count", SettingTypes.TYPE_BOOL),
                new SettingCollector("not_friend_show", SettingTypes.TYPE_BOOL),
                new SettingCollector("do_zoom_photo", SettingTypes.TYPE_BOOL),
                new SettingCollector("change_upload_size", SettingTypes.TYPE_BOOL),
                new SettingCollector("show_photos_line", SettingTypes.TYPE_BOOL),
                new SettingCollector("do_auto_play_video", SettingTypes.TYPE_BOOL),
                new SettingCollector("video_controller_to_decor", SettingTypes.TYPE_BOOL),
                new SettingCollector("video_swipes", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_likes", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_notifications", SettingTypes.TYPE_BOOL),
                new SettingCollector("native_parcel_enable", SettingTypes.TYPE_BOOL),
                new SettingCollector("extra_debug", SettingTypes.TYPE_BOOL),
                new SettingCollector("dump_fcm", SettingTypes.TYPE_BOOL),
                new SettingCollector("hint_stickers", SettingTypes.TYPE_BOOL),
                new SettingCollector("enable_native", SettingTypes.TYPE_BOOL),
                new SettingCollector("enable_cache_ui_anim", SettingTypes.TYPE_BOOL),
                new SettingCollector("disable_sensored_voice", SettingTypes.TYPE_BOOL),
                new SettingCollector("local_media_server", SettingTypes.TYPE_STRING),
                new SettingCollector("pagan_symbol", SettingTypes.TYPE_STRING),
                new SettingCollector("kate_gms_token", SettingTypes.TYPE_STRING),
                new SettingCollector("language_ui", SettingTypes.TYPE_STRING),
                new SettingCollector("end_list_anim", SettingTypes.TYPE_STRING),
                new SettingCollector("runes_show", SettingTypes.TYPE_BOOL),
                new SettingCollector("player_background_json", SettingTypes.TYPE_STRING),
                new SettingCollector("use_hls_downloader", SettingTypes.TYPE_BOOL),
                new SettingCollector("is_side_navigation", SettingTypes.TYPE_BOOL),
                new SettingCollector("is_side_no_stroke", SettingTypes.TYPE_BOOL),
                new SettingCollector("is_side_transition", SettingTypes.TYPE_BOOL),
                new SettingCollector("chats_notification_backup", SettingTypes.TYPE_STRING),
                new SettingCollector("notification_force_link", SettingTypes.TYPE_BOOL),
                new SettingCollector("recording_to_opus", SettingTypes.TYPE_BOOL)
        };
    }

    public @Nullable
    JsonObject doBackup() {
        boolean has = false;
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext());
        JsonObject ret = new JsonObject();
        for (SettingCollector i : settings) {
            JsonObject temp = i.requestSetting(pref);
            if (temp != null) {
                if (!has)
                    has = true;
                ret.add(i.name, temp);
            }
        }
        if (!has)
            return null;
        return ret;
    }

    public void doRestore(JsonObject ret) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext());
        for (SettingCollector i : settings) {
            i.restore(pref, ret);
        }
        Settings.get().notifications().parseBackupNotifications();
    }

    private static class SettingCollector {
        private final @SettingTypes
        int type;
        private final String name;

        public SettingCollector(String name, @SettingTypes int type) {
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void restore(@NonNull SharedPreferences pref, @NonNull JsonObject ret) {
            try {
                if (!ret.has(name))
                    return;
                JsonObject o = ret.getAsJsonObject(name);
                if (o.get("type").getAsInt() != type)
                    return;
                switch (type) {
                    case SettingTypes.TYPE_BOOL:
                        pref.edit().putBoolean(name, o.get("value").getAsBoolean()).apply();
                        break;
                    case SettingTypes.TYPE_INT:
                        pref.edit().putInt(name, o.get("value").getAsInt()).apply();
                        break;
                    case SettingTypes.TYPE_STRING:
                        pref.edit().putString(name, o.get("value").getAsString()).apply();
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public @Nullable
        JsonObject requestSetting(@NonNull SharedPreferences pref) {
            if (!pref.contains(name)) {
                return null;
            }
            JsonObject temp = new JsonObject();
            temp.addProperty("type", type);
            switch (type) {
                case SettingTypes.TYPE_BOOL:
                    temp.addProperty("value", pref.getBoolean(name, false));
                    break;
                case SettingTypes.TYPE_INT:
                    temp.addProperty("value", pref.getInt(name, 0));
                    break;
                case SettingTypes.TYPE_STRING:
                    temp.addProperty("value", pref.getString(name, ""));
                    break;
            }
            return temp;
        }
    }
}
