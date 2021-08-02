package dev.ragnarok.fenrir.settings;

import static dev.ragnarok.fenrir.util.Utils.hasFlag;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.model.Peer;
import dev.ragnarok.fenrir.util.Utils;

public class NotificationsPrefs implements ISettings.INotificationSettings {

    private static final String KEY_NOTIFICATION_RINGTONE = "notification_ringtone";
    private static final String NOTIF_PREF_NAME = "dev.ragnarok.notifpref";
    private static final String KEY_VIBRO_LENGTH = "vibration_length";

    private final Context app;
    private final SharedPreferences preferences;

    NotificationsPrefs(Context context) {
        app = context.getApplicationContext();
        preferences = context.getSharedPreferences(NOTIF_PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String keyFor(int aid, int peerId) {
        return "peerid" + aid + "_" + peerId;
    }

    @Override
    public void setNotifPref(int aid, int peerid, int mask) {
        preferences.edit()
                .putInt(keyFor(aid, peerid), mask)
                .apply();
        putChatNotificationSettingsBackup(aid, peerid, mask);
    }

    private boolean isOtherNotificationsEnable() {
        return hasFlag(getOtherNotificationMask(), FLAG_SHOW_NOTIF);
    }

    @Override
    public int getOtherNotificationMask() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(app);
        int mask = 0;
        if (preferences.getBoolean("other_notifications_enable", true)) {
            mask = mask + FLAG_SHOW_NOTIF;
        }

        if (preferences.getBoolean("other_notif_sound", true)) {
            mask = mask + FLAG_SOUND;
        }

        if (preferences.getBoolean("other_notif_vibration", true)) {
            mask = mask + FLAG_VIBRO;
        }

        if (preferences.getBoolean("other_notif_led", true)) {
            mask = mask + FLAG_LED;
        }
        return mask;
    }

    @Override
    public boolean isCommentsNotificationsEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("new_comment_notification", true);
    }

    @Override
    public boolean isFriendRequestAcceptationNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("friend_request_accepted_notification", true);
    }

    @Override
    public boolean isNewFollowerNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("new_follower_notification", true);
    }

    @Override
    public boolean isWallPublishNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("wall_publish_notification", true);
    }

    @Override
    public boolean isGroupInvitedNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("group_invited_notification", true);
    }

    @Override
    public boolean isReplyNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("reply_notification", true);
    }

    @Override
    public boolean isNewPostOnOwnWallNotifEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("new_wall_post_notification", true);
    }

    @Override
    public boolean isNewPostsNotificationEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("new_posts_notification", true);
    }

    @Override
    public boolean isBirthdayNotifyEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("birtday_notification", true);
    }

    @Override
    public boolean isMentionNotifyEnabled() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("mention_notification", true);
    }

    @Override
    public boolean isLikeNotificationEnable() {
        return isOtherNotificationsEnable() && PreferenceManager.getDefaultSharedPreferences(app)
                .getBoolean("likes_notification", true);
    }

    @Override
    public Uri getFeedbackRingtoneUri() {
        String path = "android.resource://" + app.getPackageName() + "/" + R.raw.feedback_sound;
        return Uri.parse(path);
    }

    @Override
    public String getDefNotificationRingtone() {
        return "android.resource://" + app.getPackageName() + "/" + R.raw.notification_sound;
    }

    @Override
    public String getNotificationRingtone() {
        return PreferenceManager.getDefaultSharedPreferences(app)
                .getString(KEY_NOTIFICATION_RINGTONE, getDefNotificationRingtone());
    }

    @Override
    public void setNotificationRingtoneUri(String path) {
        PreferenceManager.getDefaultSharedPreferences(app)
                .edit()
                .putString(KEY_NOTIFICATION_RINGTONE, path)
                .apply();
    }

    @Override
    public long[] getVibrationLength() {
        switch (PreferenceManager.getDefaultSharedPreferences(app)
                .getString(KEY_VIBRO_LENGTH, "4")) {
            case "0":
                return new long[]{0, 300};
            case "1":
                return new long[]{0, 400};
            case "2":
                return new long[]{0, 500};
            case "3":
                return new long[]{0, 300, 250, 300};
            case "5":
                return new long[]{0, 500, 250, 500};
            default:
                return new long[]{0, 400, 250, 400};
        }
    }

    @Override
    public boolean isQuickReplyImmediately() {
        return PreferenceManager.getDefaultSharedPreferences(app).getBoolean("quick_reply_immediately", false);
    }

    @Override
    public void setDefault(int aid, int peerId) {
        preferences.edit()
                .remove(keyFor(aid, peerId))
                .apply();
        removeChatNotificationSettingsBackup(aid, peerId);
    }

    @Override
    public int getNotifPref(int aid, int peerid) {
        return preferences.getInt(keyFor(aid, peerid), getGlobalNotifPref(Peer.isGroupChat(peerid)));
    }

    private int getGlobalNotifPref(boolean isGroup) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(app);
        int value = sharedPreferences.getBoolean("high_notif_priority", false) ? FLAG_HIGH_PRIORITY : 0;

        if (!isGroup) {
            if (sharedPreferences.getBoolean("new_dialog_message_notif_enable", true)) {
                value += FLAG_SHOW_NOTIF;
            }

            if (sharedPreferences.getBoolean("new_dialog_message_notif_sound", true)) {
                value += FLAG_SOUND;
            }

            if (sharedPreferences.getBoolean("new_dialog_message_notif_vibration", true)) {
                value += FLAG_VIBRO;
            }

            if (sharedPreferences.getBoolean("new_dialog_message_notif_led", true)) {
                value += FLAG_LED;
            }
        } else {
            if (sharedPreferences.getBoolean("new_groupchat_message_notif_enable", true)) {
                value += FLAG_SHOW_NOTIF;
            }

            if (sharedPreferences.getBoolean("new_groupchat_message_notif_sound", true)) {
                value += FLAG_SOUND;
            }

            if (sharedPreferences.getBoolean("new_groupchat_message_notif_vibration", true)) {
                value += FLAG_VIBRO;
            }

            if (sharedPreferences.getBoolean("new_groupchat_message_notif_led", true)) {
                value += FLAG_LED;
            }
        }
        return value;
    }

    @Override
    public void putChatNotificationSettingsBackup(int aid, int peerId, int mask) {
        String tmp = PreferenceManager.getDefaultSharedPreferences(app).getString("chats_notification_backup", null);
        NotificationChatSettings settings = Utils.isEmpty(tmp) ? new NotificationChatSettings().init() : new Gson().fromJson(tmp, NotificationChatSettings.class);
        settings.chats_notification.put(keyFor(aid, peerId), mask);
        PreferenceManager.getDefaultSharedPreferences(app).edit().putString("chats_notification_backup", new Gson().toJson(settings)).apply();
    }

    @Override
    public void removeChatNotificationSettingsBackup(int aid, int peerId) {
        String tmp = PreferenceManager.getDefaultSharedPreferences(app).getString("chats_notification_backup", null);
        NotificationChatSettings settings = Utils.isEmpty(tmp) ? new NotificationChatSettings().init() : new Gson().fromJson(tmp, NotificationChatSettings.class);
        settings.chats_notification.remove(keyFor(aid, peerId));
        PreferenceManager.getDefaultSharedPreferences(app).edit().putString("chats_notification_backup", new Gson().toJson(settings)).apply();
    }

    @Override
    public @NonNull
    List<Integer> getSilentChats(int aid) {
        List<Integer> ret = new ArrayList<>();
        String tmp = PreferenceManager.getDefaultSharedPreferences(app).getString("chats_notification_backup", null);
        NotificationChatSettings settings = Utils.isEmpty(tmp) ? new NotificationChatSettings().init() : new Gson().fromJson(tmp, NotificationChatSettings.class);
        for (String i : settings.chats_notification.keySet()) {
            Integer value = settings.chats_notification.get(i);
            if (value == null) {
                continue;
            }
            if (i.contains("peerid" + aid) && !hasFlag(value, FLAG_SHOW_NOTIF)) {
                ret.add(Integer.parseInt(i.replace("peerid" + aid + "_", "")));
            }
        }
        return ret;
    }

    @Override
    public void parseBackupNotifications() {
        String tmp = PreferenceManager.getDefaultSharedPreferences(app).getString("chats_notification_backup", null);
        NotificationChatSettings settings = Utils.isEmpty(tmp) ? new NotificationChatSettings().init() : new Gson().fromJson(tmp, NotificationChatSettings.class);
        for (String i : settings.chats_notification.keySet()) {
            Integer value = settings.chats_notification.get(i);
            if (value == null) {
                continue;
            }
            preferences.edit()
                    .putInt(i, value)
                    .apply();
        }
    }

    @Keep
    private static class NotificationChatSettings {
        @SerializedName("chats_notification")
        public Map<String, Integer> chats_notification;

        public NotificationChatSettings init() {
            chats_notification = new HashMap<>();
            return this;
        }
    }
}
