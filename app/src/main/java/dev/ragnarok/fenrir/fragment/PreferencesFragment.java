package dev.ragnarok.fenrir.fragment;

import static dev.ragnarok.fenrir.util.Objects.nonNull;
import static dev.ragnarok.fenrir.util.Utils.isEmpty;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.AnyRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.CustomTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso3.BitmapSafeResize;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import dev.ragnarok.fenrir.Account_Types;
import dev.ragnarok.fenrir.CheckDonate;
import dev.ragnarok.fenrir.Constants;
import dev.ragnarok.fenrir.Extra;
import dev.ragnarok.fenrir.Injection;
import dev.ragnarok.fenrir.R;
import dev.ragnarok.fenrir.activity.ActivityFeatures;
import dev.ragnarok.fenrir.activity.ActivityUtils;
import dev.ragnarok.fenrir.activity.EnterPinActivity;
import dev.ragnarok.fenrir.activity.PhotosActivity;
import dev.ragnarok.fenrir.activity.ProxyManagerActivity;
import dev.ragnarok.fenrir.activity.alias.BlackFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.BlueFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.DefaultFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.GreenFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.LineageFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.RedFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.ToggleAlias;
import dev.ragnarok.fenrir.activity.alias.VKFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.VioletFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.WhiteFenrirAlias;
import dev.ragnarok.fenrir.activity.alias.YellowFenrirAlias;
import dev.ragnarok.fenrir.api.model.LocalServerSettings;
import dev.ragnarok.fenrir.api.model.PlayerCoverBackgroundSettings;
import dev.ragnarok.fenrir.db.DBHelper;
import dev.ragnarok.fenrir.filepicker.model.DialogConfigs;
import dev.ragnarok.fenrir.filepicker.model.DialogProperties;
import dev.ragnarok.fenrir.filepicker.view.FilePickerDialog;
import dev.ragnarok.fenrir.listener.OnSectionResumeCallback;
import dev.ragnarok.fenrir.media.record.AudioRecordWrapper;
import dev.ragnarok.fenrir.model.LocalPhoto;
import dev.ragnarok.fenrir.model.SwitchableCategory;
import dev.ragnarok.fenrir.picasso.PicassoInstance;
import dev.ragnarok.fenrir.picasso.transforms.EllipseTransformation;
import dev.ragnarok.fenrir.picasso.transforms.RoundTransformation;
import dev.ragnarok.fenrir.place.Place;
import dev.ragnarok.fenrir.place.PlaceFactory;
import dev.ragnarok.fenrir.service.KeepLongpollService;
import dev.ragnarok.fenrir.settings.AvatarStyle;
import dev.ragnarok.fenrir.settings.ISettings;
import dev.ragnarok.fenrir.settings.NightMode;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.settings.VkPushRegistration;
import dev.ragnarok.fenrir.util.AppPerms;
import dev.ragnarok.fenrir.util.CustomToast;
import dev.ragnarok.fenrir.util.HelperSimple;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.view.MySearchView;
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView;
import dev.ragnarok.fenrir.view.natives.video.AnimatedShapeableImageView;

public class PreferencesFragment extends PreferenceFragmentCompat {

    public static final String KEY_DEFAULT_CATEGORY = "default_category";
    public static final String KEY_AVATAR_STYLE = "avatar_style";
    private static final String KEY_APP_THEME = "app_theme";
    private static final String KEY_NIGHT_SWITCH = "night_switch";
    private static final String KEY_NOTIFICATION = "notifications";
    private static final String KEY_SECURITY = "security";
    private static final String KEY_DRAWER_ITEMS = "drawer_categories";
    private static final String KEY_SIDE_DRAWER_ITEMS = "side_drawer_categories";

    private final ActivityResultLauncher<Intent> requestLightBackgound = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(false, result.getData());
                    //requireActivity().recreate();
                }
            });

    private final ActivityResultLauncher<Intent> requestDarkBackgound = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    changeDrawerBackground(true, result.getData());
                    //requireActivity().recreate();
                }
            });

    private final ActivityResultLauncher<Intent> requestPin = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
                }
            });

    private final AppPerms.doRequestPermissions requestContactsPermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.READ_CONTACTS},
            () -> PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity()));

    private final AppPerms.doRequestPermissions requestReadPermission = AppPerms.requestPermissions(this,
            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
            () -> CustomToast.CreateCustomToast(requireActivity()).showToast(R.string.permission_all_granted_text));

    public static Bundle buildArgs(int accountId) {
        Bundle args = new Bundle();
        args.putInt(Extra.ACCOUNT_ID, accountId);
        return args;
    }

    public static PreferencesFragment newInstance(Bundle args) {
        PreferencesFragment fragment = new PreferencesFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static File getDrawerBackgroundFile(Context context, boolean light) {
        return new File(context.getFilesDir(), light ? "chat_light.jpg" : "chat_dark.jpg");
    }

    public static void CleanCache(Context context, boolean notify) {
        try {
            PicassoInstance.clear_cache();
            File cache = new File(context.getCacheDir(), "notif-cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            cache = new File(context.getCacheDir(), "lottie_cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            cache = new File(context.getCacheDir(), "video_network_cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            cache = AudioRecordWrapper.getRecordingDirectory(context);
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            if (notify)
                CustomToast.CreateCustomToast(context).showToast(R.string.success);
        } catch (Exception e) {
            e.printStackTrace();
            if (notify)
                CustomToast.CreateCustomToast(context).showToastError(e.getLocalizedMessage());
        }
    }

    public static void CleanUICache(Context context, boolean notify) {
        try {
            File cache = new File(context.getCacheDir(), "lottie_cache/rendered");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            cache = new File(context.getCacheDir(), "video_resource_cache");
            if (cache.exists() && cache.isDirectory()) {
                String[] children = cache.list();
                assert children != null;
                for (String child : children) {
                    File rem = new File(cache, child);
                    if (rem.isFile()) {
                        rem.delete();
                    }
                }
            }
            if (notify)
                CustomToast.CreateCustomToast(context).showToast(R.string.success);
        } catch (Exception e) {
            e.printStackTrace();
            if (notify)
                CustomToast.CreateCustomToast(context).showToastError(e.getLocalizedMessage());
        }
    }

    private static Bitmap checkBitmap(@NonNull Bitmap bitmap) {
        if (bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0 || (bitmap.getWidth() <= 4000 && bitmap.getHeight() <= 4000)) {
            return bitmap;
        }
        int mWidth = bitmap.getWidth();
        int mHeight = bitmap.getHeight();
        float mCo = (float) Math.min(mHeight, mWidth) / Math.max(mHeight, mWidth);
        if (mWidth > mHeight) {
            mWidth = 4000;
            mHeight = (int) (4000 * mCo);
        } else {
            mHeight = 4000;
            mWidth = (int) (4000 * mCo);
        }
        if (mWidth <= 0 || mHeight <= 0) {
            return bitmap;
        }
        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, mWidth, mHeight, true);
        bitmap.recycle();
        return tmp;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);
        assert root != null;
        MySearchView searchView = root.findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new MySearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Preference pref = findPreferenceByName(query);
                if (nonNull(pref)) {
                    scrollToPreference(pref);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Preference pref = findPreferenceByName(newText);
                if (nonNull(pref)) {
                    scrollToPreference(pref);
                }
                return false;
            }
        });
        searchView.setRightButtonVisibility(false);
        searchView.setLeftIcon(R.drawable.magnify);
        searchView.setQuery("", true);
        return root;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.preference_fenrir_list_fragment;
    }

    private void selectLocalImage(boolean isDark) {
        if (!AppPerms.hasReadStoragePermission(requireActivity())) {
            requestReadPermission.launch();
            return;
        }

        Intent intent = new Intent(getActivity(), PhotosActivity.class);
        intent.putExtra(PhotosActivity.EXTRA_MAX_SELECTION_COUNT, 1);
        if (isDark) {
            requestDarkBackgound.launch(intent);
        } else {
            requestLightBackgound.launch(intent);
        }
    }

    private void EnableChatPhotoBackground(int index) {
        boolean bEnable;
        switch (index) {
            case 0:
            case 1:
            case 2:
            case 3:
                bEnable = false;
                break;
            default:
                bEnable = true;
                break;
        }
        Preference prefLightChat = findPreference("chat_light_background");
        Preference prefDarkChat = findPreference("chat_dark_background");
        Preference prefResetPhotoChat = findPreference("reset_chat_background");
        if (prefDarkChat == null || prefLightChat == null || prefResetPhotoChat == null)
            return;
        prefDarkChat.setEnabled(bEnable);
        prefLightChat.setEnabled(bEnable);
        prefResetPhotoChat.setEnabled(bEnable);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);

        findPreference(KEY_NIGHT_SWITCH).setOnPreferenceChangeListener((preference, newValue) -> {
            switch (Integer.parseInt(newValue.toString())) {
                case NightMode.DISABLE:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    break;
                case NightMode.ENABLE:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    break;
                case NightMode.AUTO:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY);
                    break;
                case NightMode.FOLLOW_SYSTEM:
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    break;
            }

            return true;
        });

        findPreference("messages_menu_down").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("is_side_no_stroke").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("is_side_transition").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("donate_anim_set").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("amoled_theme").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("show_mini_player").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("vk_auth_domain").setOnPreferenceChangeListener((preference, newValue) -> {
            Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
            return true;
        });

        findPreference("vk_api_domain").setOnPreferenceChangeListener((preference, newValue) -> {
            Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
            return true;
        });

        findPreference("player_background").setOnPreferenceClickListener((newValue) -> {
            if (!CheckDonate.isFullVersion(requireActivity())) {
                return false;
            }
            View view = View.inflate(requireActivity(), R.layout.entry_player_background, null);
            MaterialCheckBox enabled_rotation = view.findViewById(R.id.edit_enabled);
            MaterialCheckBox invert_rotation = view.findViewById(R.id.edit_invert_rotation);
            SeekBar rotation_speed = view.findViewById(R.id.edit_rotation_speed);
            SeekBar zoom = view.findViewById(R.id.edit_zoom);
            SeekBar blur = view.findViewById(R.id.edit_blur);
            MaterialTextView text_rotation_speed = view.findViewById(R.id.text_rotation_speed);
            MaterialTextView text_zoom = view.findViewById(R.id.text_zoom);
            MaterialTextView text_blur = view.findViewById(R.id.text_blur);
            zoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_zoom.setText(getString(R.string.rotate_scale, progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            rotation_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_rotation_speed.setText(getString(R.string.rotate_speed, progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            blur.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    text_blur.setText(getString(R.string.player_blur, progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            PlayerCoverBackgroundSettings settings = Settings.get().other().getPlayerCoverBackgroundSettings();
            enabled_rotation.setChecked(settings.enabled_rotation);
            invert_rotation.setChecked(settings.invert_rotation);
            blur.setProgress(settings.blur);
            rotation_speed.setProgress((int) (settings.rotation_speed * 1000));
            zoom.setProgress((int) ((settings.zoom - 1) * 10));
            text_zoom.setText(getString(R.string.rotate_scale, (int) ((settings.zoom - 1) * 10)));
            text_rotation_speed.setText(getString(R.string.rotate_speed, (int) (settings.rotation_speed * 1000)));
            text_blur.setText(getString(R.string.player_blur, settings.blur));

            new MaterialAlertDialogBuilder(requireActivity())
                    .setView(view)
                    .setCancelable(true)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setNeutralButton(R.string.set_default, (dialog, which) -> Settings.get().other().setPlayerCoverBackgroundSettings(new PlayerCoverBackgroundSettings().set_default()))
                    .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                        PlayerCoverBackgroundSettings st = new PlayerCoverBackgroundSettings();
                        st.blur = blur.getProgress();
                        st.invert_rotation = invert_rotation.isChecked();
                        st.enabled_rotation = enabled_rotation.isChecked();
                        st.rotation_speed = (float) rotation_speed.getProgress() / 1000;
                        st.zoom = ((float) zoom.getProgress() / 10) + 1f;
                        Settings.get().other().setPlayerCoverBackgroundSettings(st);
                    })
                    .show();
            return true;
        });

        findPreference("local_media_server").setOnPreferenceClickListener((newValue) -> {
            if (!CheckDonate.isFullVersion(requireActivity())) {
                return false;
            }
            View view = View.inflate(requireActivity(), R.layout.entry_local_server, null);
            TextInputEditText url = view.findViewById(R.id.edit_url);
            TextInputEditText password = view.findViewById(R.id.edit_password);
            MaterialCheckBox enabled = view.findViewById(R.id.edit_enabled);
            LocalServerSettings settings = Settings.get().other().getLocalServer();
            url.setText(settings.url);
            password.setText(settings.password);
            enabled.setChecked(settings.enabled);

            new MaterialAlertDialogBuilder(requireActivity())
                    .setView(view)
                    .setCancelable(true)
                    .setNegativeButton(R.string.button_cancel, null)
                    .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                        boolean en_vl = enabled.isChecked();
                        String url_vl = url.getEditableText().toString();
                        String psv_vl = password.getEditableText().toString();
                        if (en_vl && (isEmpty(url_vl) || isEmpty(psv_vl))) {
                            return;
                        }
                        LocalServerSettings srv = new LocalServerSettings();
                        srv.enabled = en_vl;
                        srv.password = psv_vl;
                        srv.url = url_vl;
                        Settings.get().other().setLocalServer(srv);
                        Injection.provideProxySettings().setActive(Injection.provideProxySettings().getActiveProxy());
                    })
                    .show();
            return true;
        });

        findPreference("max_bitmap_resolution").setOnPreferenceChangeListener((preference, newValue) -> {
            int sz = -1;
            try {
                sz = Integer.parseInt(newValue.toString().trim());
            } catch (NumberFormatException ignored) {
            }
            if (BitmapSafeResize.INSTANCE.isOverflowCanvas(sz) || sz < 100 && sz >= 0) {
                return false;
            } else {
                BitmapSafeResize.INSTANCE.setMaxResolution(sz);
            }
            requireActivity().recreate();
            return true;
        });

        findPreference("audio_round_icon").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("show_profile_in_additional_page").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("show_recent_dialogs").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("do_zoom_photo").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("font_size").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("language_ui").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("snow_mode").setOnPreferenceChangeListener((preference, newValue) -> {
            requireActivity().recreate();
            return true;
        });

        findPreference("photo_preview_size").setOnPreferenceChangeListener((preference, newValue) -> {
            Settings.get().main().notifyPrefPreviewSizeChanged();
            return true;
        });
        ListPreference defCategory = findPreference(KEY_DEFAULT_CATEGORY);
        initStartPagePreference(defCategory);


        Preference notification = findPreference(KEY_NOTIFICATION);
        if (notification != null) {
            notification.setOnPreferenceClickListener(preference -> {
                if (Utils.hasOreo()) {
                    Intent intent = new Intent();
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("android.provider.extra.APP_PACKAGE", requireContext().getPackageName());
                    requireContext().startActivity(intent);
                } else {
                    PlaceFactory.getNotificationSettingsPlace().tryOpenWith(requireActivity());
                }
                return true;
            });
        }

        Preference security = findPreference(KEY_SECURITY);
        if (nonNull(security)) {
            security.setOnPreferenceClickListener(preference -> {
                onSecurityClick();
                return true;
            });
        }

        Preference drawerCategories = findPreference(KEY_DRAWER_ITEMS);
        if (drawerCategories != null) {
            drawerCategories.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getDrawerEditPlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference sideDrawerCategories = findPreference(KEY_SIDE_DRAWER_ITEMS);
        if (sideDrawerCategories != null) {
            sideDrawerCategories.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getSideDrawerEditPlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference avatarStyle = findPreference(KEY_AVATAR_STYLE);
        if (avatarStyle != null) {
            avatarStyle.setOnPreferenceClickListener(preference -> {
                showAvatarStyleDialog();
                return true;
            });
        }

        Preference appTheme = findPreference(KEY_APP_THEME);
        if (appTheme != null) {
            appTheme.setOnPreferenceClickListener(preference -> {
                PlaceFactory.getSettingsThemePlace().tryOpenWith(requireActivity());
                return true;
            });
        }

        Preference version = findPreference("version");
        if (version != null) {
            version.setSummary(Utils.getAppVersionName(requireActivity()) + ", VK API " + Constants.API_VERSION);
            version.setOnPreferenceClickListener(preference -> {
                View view = View.inflate(requireActivity(), R.layout.dialog_about_us, null);
                new MaterialAlertDialogBuilder(requireActivity())
                        .setView(view)
                        .setOnDismissListener(dialog -> {
                            showDedicated();
                        })
                        .show();
                return true;
            });
        }

        findPreference("dedicated").setOnPreferenceClickListener(preference -> {
            showDedicated();
            return true;
        });

        Preference additional_debug = findPreference("additional_debug");
        if (additional_debug != null) {
            additional_debug.setOnPreferenceClickListener(preference -> {
                ShowAdditionalInfo();
                return true;
            });
        }

        findPreference("notification_bubbles").setVisible(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R);

        Preference scoped_storage = findPreference("scoped_storage");
        if (scoped_storage != null) {
            if (!Utils.hasScopedStorage()) {
                scoped_storage.setVisible(false);
            } else {
                scoped_storage.setVisible(true);
                scoped_storage.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                    intent.setData(uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    requireActivity().startActivity(intent);
                    return true;
                });
            }
        }

        Preference select_icon = findPreference("select_custom_icon");
        if (select_icon != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                select_icon.setVisible(false);
            } else {
                select_icon.setVisible(true);
                select_icon.setOnPreferenceClickListener(preference -> {
                    ShowSelectIcon();
                    return true;
                });
            }
        }

        ListPreference chat_background = findPreference("chat_background");
        if (chat_background != null) {
            chat_background.setOnPreferenceChangeListener((preference, newValue) -> {
                String val = newValue.toString();
                int index = Integer.parseInt(val);
                EnableChatPhotoBackground(index);
                return true;
            });
            EnableChatPhotoBackground(Integer.parseInt(chat_background.getValue()));
        }

        Preference lightSideBarPreference = findPreference("chat_light_background");
        if (lightSideBarPreference != null) {
            lightSideBarPreference.setOnPreferenceClickListener(preference -> {
                selectLocalImage(false);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), true);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                lightSideBarPreference.setIcon(d);
            } else
                lightSideBarPreference.setIcon(R.drawable.dir_photo);
        }

        Preference darkSideBarPreference = findPreference("chat_dark_background");
        if (darkSideBarPreference != null) {
            darkSideBarPreference.setOnPreferenceClickListener(preference -> {
                selectLocalImage(true);
                return true;
            });
            File bitmap = getDrawerBackgroundFile(requireActivity(), false);
            if (bitmap.exists()) {
                Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                darkSideBarPreference.setIcon(d);
            } else
                darkSideBarPreference.setIcon(R.drawable.dir_photo);
        }

        Preference resetDrawerBackground = findPreference("reset_chat_background");
        if (resetDrawerBackground != null) {
            resetDrawerBackground.setOnPreferenceClickListener(preference -> {
                File chat_light = getDrawerBackgroundFile(requireActivity(), true);
                File chat_dark = getDrawerBackgroundFile(requireActivity(), false);

                try {
                    tryDeleteFile(chat_light);
                    tryDeleteFile(chat_dark);
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
                if (darkSideBarPreference != null && lightSideBarPreference != null) {
                    File bitmap = getDrawerBackgroundFile(requireActivity(), true);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        lightSideBarPreference.setIcon(d);
                    } else
                        lightSideBarPreference.setIcon(R.drawable.dir_photo);
                    bitmap = getDrawerBackgroundFile(requireActivity(), false);
                    if (bitmap.exists()) {
                        Drawable d = Drawable.createFromPath(bitmap.getAbsolutePath());
                        darkSideBarPreference.setIcon(d);
                    } else
                        darkSideBarPreference.setIcon(R.drawable.dir_photo);
                }
                return true;
            });
        }

        CustomTextPreference music_dir = findPreference("music_dir");
        if (nonNull(music_dir)) {
            music_dir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getMusicDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.music_dir);
                dialog.setDialogSelectionListener(files -> {
                    PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("music_dir", files[0]).apply();
                    music_dir.refresh();
                });
                dialog.show();
                return true;
            });
        }
        CustomTextPreference photo_dir = findPreference("photo_dir");
        if (nonNull(photo_dir)) {
            photo_dir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getPhotoDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.photo_dir);
                dialog.setDialogSelectionListener(files -> {
                    PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("photo_dir", files[0]).apply();
                    photo_dir.refresh();
                });
                dialog.show();
                return true;
            });
        }
        CustomTextPreference video_dir = findPreference("video_dir");
        if (nonNull(video_dir)) {
            video_dir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getVideoDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.video_dir);
                dialog.setDialogSelectionListener(files -> {
                    PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("video_dir", files[0]).apply();
                    video_dir.refresh();
                });
                dialog.show();
                return true;
            });
        }
        CustomTextPreference docs_dir = findPreference("docs_dir");
        if (nonNull(docs_dir)) {
            docs_dir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getDocDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.docs_dir);
                dialog.setDialogSelectionListener(files -> {
                    PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("docs_dir", files[0]).apply();
                    docs_dir.refresh();
                });
                dialog.show();
                return true;
            });
        }
        CustomTextPreference sticker_dir = findPreference("sticker_dir");
        if (nonNull(sticker_dir)) {
            sticker_dir.setOnPreferenceClickListener(preference -> {
                if (!AppPerms.hasReadStoragePermission(requireActivity())) {
                    requestReadPermission.launch();
                    return true;
                }
                DialogProperties properties = new DialogProperties();
                properties.selection_mode = DialogConfigs.SINGLE_MODE;
                properties.selection_type = DialogConfigs.DIR_SELECT;
                properties.root = Environment.getExternalStorageDirectory();
                properties.error_dir = Environment.getExternalStorageDirectory();
                properties.offset = new File(Settings.get().other().getStickerDir());
                properties.extensions = null;
                properties.show_hidden_files = true;
                FilePickerDialog dialog = new FilePickerDialog(requireActivity(), properties, Settings.get().ui().getMainTheme());
                dialog.setTitle(R.string.docs_dir);
                dialog.setDialogSelectionListener(files -> {
                    PreferenceManager.getDefaultSharedPreferences(Injection.provideApplicationContext()).edit().putString("sticker_dir", files[0]).apply();
                    sticker_dir.refresh();
                });
                dialog.show();
                return true;
            });
        }
        findPreference("kate_gms_token").setVisible(Constants.DEFAULT_ACCOUNT_TYPE == Account_Types.KATE);

        findPreference("show_logs")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getLogsPlace().tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("request_executor")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getRequestExecutorPlace(getAccountId()).tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("cache_cleaner")
                .setOnPreferenceClickListener(preference -> {
                    CleanUICache(requireActivity(), false);
                    CleanCache(requireActivity(), true);
                    return true;
                });

        findPreference("ui_cache_cleaner")
                .setOnPreferenceClickListener(preference -> {
                    CleanUICache(requireActivity(), true);
                    return true;
                });

        findPreference("account_cache_cleaner")
                .setOnPreferenceClickListener(preference -> {
                    DBHelper.removeDatabaseFor(requireActivity(), getAccountId());
                    CleanUICache(requireActivity(), false);
                    CleanCache(requireActivity(), true);
                    return true;
                });

        findPreference("blacklist")
                .setOnPreferenceClickListener(preference -> {
                    PlaceFactory.getUserBlackListPlace(getAccountId()).tryOpenWith(requireActivity());
                    return true;
                });

        findPreference("friends_by_phone")
                .setOnPreferenceClickListener(preference -> {
                    if (!AppPerms.hasContactsPermission(requireActivity())) {
                        requestContactsPermission.launch();
                    } else {
                        PlaceFactory.getFriendsByPhonesPlace(getAccountId()).tryOpenWith(requireActivity());
                    }
                    return true;
                });

        findPreference("proxy")
                .setOnPreferenceClickListener(preference -> {
                    startActivity(new Intent(requireActivity(), ProxyManagerActivity.class));
                    return true;
                });

        findPreference("keep_longpoll").setOnPreferenceChangeListener((preference, newValue) -> {
            boolean keep = (boolean) newValue;
            if (keep) {
                KeepLongpollService.start(preference.getContext());
            } else {
                KeepLongpollService.stop(preference.getContext());
            }
            return true;
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(view.findViewById(R.id.toolbar));
    }

    private void onSecurityClick() {
        if (Settings.get().security().isUsePinForSecurity()) {
            requestPin.launch(new Intent(requireActivity(), EnterPinActivity.class));
        } else {
            PlaceFactory.getSecuritySettingsPlace().tryOpenWith(requireActivity());
        }
    }

    private void tryDeleteFile(@NonNull File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("Can't delete file " + file);
        }
    }

    private void changeDrawerBackground(boolean isDark, Intent data) {
        ArrayList<LocalPhoto> photos = data.getParcelableArrayListExtra(Extra.PHOTOS);
        if (isEmpty(photos)) {
            return;
        }
        LocalPhoto photo = photos.get(0);
        boolean light = !isDark;
        File file = getDrawerBackgroundFile(requireActivity(), light);
        Bitmap original;
        try (FileOutputStream fos = new FileOutputStream(file)) {
            original = BitmapFactory.decodeFile(photo.getFullImageUri().getPath());
            original = checkBitmap(original);
            original.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            original.recycle();
            Drawable d = Drawable.createFromPath(file.getAbsolutePath());
            if (light) {
                Preference lightSideBarPreference = findPreference("chat_light_background");
                if (lightSideBarPreference != null)
                    lightSideBarPreference.setIcon(d);
            } else {
                Preference darkSideBarPreference = findPreference("chat_dark_background");
                if (darkSideBarPreference != null)
                    darkSideBarPreference.setIcon(d);
            }
        } catch (IOException e) {
            CustomToast.CreateCustomToast(requireActivity()).setDuration(Toast.LENGTH_LONG).showToastError(e.getMessage());
        }
    }

    private String PushToken() {
        int accountId = Settings.get().accounts().getCurrent();

        if (accountId == ISettings.IAccountsSettings.INVALID_ID) {
            return null;
        }

        List<VkPushRegistration> available = Settings.get().pushSettings().getRegistrations();
        boolean can = available.size() == 1 && available.get(0).getUserId() == accountId;
        return can ? available.get(0).getGmcToken() : null;
    }

    @SuppressLint("SetTextI18n")
    private void ShowAdditionalInfo() {
        View view = View.inflate(requireActivity(), R.layout.dialog_additional_us, null);
        ((TextView) view.findViewById(R.id.item_user_agent)).setText("User-Agent: " + Constants.USER_AGENT(Account_Types.BY_TYPE));
        ((TextView) view.findViewById(R.id.item_device_id)).setText("Device-ID: " + Utils.getDeviceId(requireActivity()));
        ((TextView) view.findViewById(R.id.item_gcm_token)).setText("GMS-Token: " + PushToken());

        new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .show();
    }

    private void ShowSelectIcon() {
        if (!CheckDonate.isFullVersion(requireActivity())) {
            return;
        }
        View view = View.inflate(requireActivity(), R.layout.icon_select_alert, null);
        view.findViewById(R.id.default_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), DefaultFenrirAlias.class));
        view.findViewById(R.id.blue_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), BlueFenrirAlias.class));
        view.findViewById(R.id.green_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), GreenFenrirAlias.class));
        view.findViewById(R.id.violet_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), VioletFenrirAlias.class));
        view.findViewById(R.id.red_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), RedFenrirAlias.class));
        view.findViewById(R.id.yellow_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), YellowFenrirAlias.class));
        view.findViewById(R.id.black_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), BlackFenrirAlias.class));
        view.findViewById(R.id.vk_official).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), VKFenrirAlias.class));
        view.findViewById(R.id.white_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), WhiteFenrirAlias.class));
        view.findViewById(R.id.lineage_icon).setOnClickListener(v -> new ToggleAlias().toggleTo(requireActivity(), LineageFenrirAlias.class));
        new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .show();
    }

    private void resolveAvatarStyleViews(int style, ImageView circle, ImageView oval) {
        switch (style) {
            case AvatarStyle.CIRCLE:
                circle.setVisibility(View.VISIBLE);
                oval.setVisibility(View.INVISIBLE);
                break;
            case AvatarStyle.OVAL:
                circle.setVisibility(View.INVISIBLE);
                oval.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showAvatarStyleDialog() {
        int current = Settings.get()
                .ui()
                .getAvatarStyle();

        View view = View.inflate(requireActivity(), R.layout.dialog_avatar_style, null);
        ImageView ivCircle = view.findViewById(R.id.circle_avatar);
        ImageView ivOval = view.findViewById(R.id.oval_avatar);
        ImageView ivCircleSelected = view.findViewById(R.id.circle_avatar_selected);
        ImageView ivOvalSelected = view.findViewById(R.id.oval_avatar_selected);

        ivCircle.setOnClickListener(v -> resolveAvatarStyleViews(AvatarStyle.CIRCLE, ivCircleSelected, ivOvalSelected));
        ivOval.setOnClickListener(v -> resolveAvatarStyleViews(AvatarStyle.OVAL, ivCircleSelected, ivOvalSelected));

        resolveAvatarStyleViews(current, ivCircleSelected, ivOvalSelected);

        PicassoInstance.with()
                .load(R.drawable.ava_settings)
                .transform(new RoundTransformation())
                .into(ivCircle);

        PicassoInstance.with()
                .load(R.drawable.ava_settings)
                .transform(new EllipseTransformation())
                .into(ivOval);

        new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(R.string.avatar_style_title)
                .setView(view)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                    boolean circle = ivCircleSelected.getVisibility() == View.VISIBLE;
                    Settings.get()
                            .ui()
                            .storeAvatarStyle(circle ? AvatarStyle.CIRCLE : AvatarStyle.OVAL);
                    requireActivity().recreate();
                })
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    private int getAccountId() {
        return requireArguments().getInt(Extra.ACCOUNT_ID);
    }

    private void initStartPagePreference(ListPreference lp) {
        ISettings.IDrawerSettings drawerSettings = Settings.get()
                .drawerSettings();

        ArrayList<String> enabledCategoriesName = new ArrayList<>();
        ArrayList<String> enabledCategoriesValues = new ArrayList<>();

        enabledCategoriesName.add(getString(R.string.last_closed_page));
        enabledCategoriesValues.add("last_closed");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.FRIENDS)) {
            enabledCategoriesName.add(getString(R.string.friends));
            enabledCategoriesValues.add("1");
        }

        enabledCategoriesName.add(getString(R.string.dialogs));
        enabledCategoriesValues.add("2");

        enabledCategoriesName.add(getString(R.string.feed));
        enabledCategoriesValues.add("3");

        enabledCategoriesName.add(getString(R.string.drawer_feedback));
        enabledCategoriesValues.add("4");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.GROUPS)) {
            enabledCategoriesName.add(getString(R.string.groups));
            enabledCategoriesValues.add("5");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.PHOTOS)) {
            enabledCategoriesName.add(getString(R.string.photos));
            enabledCategoriesValues.add("6");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.VIDEOS)) {
            enabledCategoriesName.add(getString(R.string.videos));
            enabledCategoriesValues.add("7");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.MUSIC)) {
            enabledCategoriesName.add(getString(R.string.music));
            enabledCategoriesValues.add("8");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.DOCS)) {
            enabledCategoriesName.add(getString(R.string.attachment_documents));
            enabledCategoriesValues.add("9");
        }

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.BOOKMARKS)) {
            enabledCategoriesName.add(getString(R.string.bookmarks));
            enabledCategoriesValues.add("10");
        }

        enabledCategoriesName.add(getString(R.string.search));
        enabledCategoriesValues.add("11");

        if (drawerSettings.isCategoryEnabled(SwitchableCategory.NEWSFEED_COMMENTS)) {
            enabledCategoriesName.add(getString(R.string.drawer_newsfeed_comments));
            enabledCategoriesValues.add("12");
        }

        lp.setEntries(enabledCategoriesName.toArray(new CharSequence[0]));
        lp.setEntryValues(enabledCategoriesValues.toArray(new CharSequence[0]));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showDedicated() {
        View view = View.inflate(requireActivity(), R.layout.dialog_dedicated, null);
        RecyclerView pager = view.findViewById(R.id.dedicated_pager);
        pager.setLayoutManager(new LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false));
        pager.setAdapter(new ImageDedicatedAdapter(new ImageDedicatedAdapter.SourceType[]{new ImageDedicatedAdapter.SourceType("dedicated1.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated2.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated3.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated4.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated5.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated6.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated7.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated8.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated9.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated10.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated11.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated12.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated13.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated14.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated15.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated16.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated17.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated18.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated19.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated20.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated21.webp"),
                new ImageDedicatedAdapter.SourceType("dedicated22.webp"),
                new ImageDedicatedAdapter.SourceType(R.raw.dedicated_video1),
                new ImageDedicatedAdapter.SourceType(R.raw.dedicated_video2)}));
        RLottieImageView anim = view.findViewById(R.id.dedicated_anim);
        pager.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                anim.clearAnimationDrawable();
            }
            return false;
        });
        new MaterialAlertDialogBuilder(requireActivity())
                .setView(view)
                .show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Settings.get().ui().notifyPlaceResumed(Place.PREFERENCES);

        ActionBar actionBar = ActivityUtils.supportToolbarFor(this);
        if (actionBar != null) {
            actionBar.setTitle(R.string.settings);
            actionBar.setSubtitle(null);
        }

        if (requireActivity() instanceof OnSectionResumeCallback) {
            ((OnSectionResumeCallback) requireActivity()).onSectionResume(AbsNavigationFragment.SECTION_ITEM_SETTINGS);
        }

        new ActivityFeatures.Builder()
                .begin()
                .setHideNavigationMenu(false)
                .setBarsColored(requireActivity(), true)
                .build()
                .apply(requireActivity());
    }

    private static final class ImageHolder extends RecyclerView.ViewHolder {
        ImageHolder(View rootView) {
            super(rootView);
        }
    }

    private static class ImageDedicatedAdapter extends RecyclerView.Adapter<ImageHolder> {

        private final List<SourceType> drawables;

        public ImageDedicatedAdapter(SourceType[] drawables) {
            this.drawables = Arrays.asList(drawables);
            if (!HelperSimple.INSTANCE.needHelp(HelperSimple.DEDICATED_COUNTER, 2)) {
                Collections.shuffle(this.drawables);
            }
        }

        @NonNull
        @Override
        public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dedicated, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
            SourceType res = drawables.get(position);
            AnimatedShapeableImageView imageView = holder.itemView.findViewById(R.id.dedicated_photo);
            PicassoInstance.with().cancelRequest(imageView);
            if (!res.isVideo) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                PicassoInstance.with().load(res.asset).into(imageView);
            } else {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setDecoderCallback(success -> {
                    if (success) {
                        imageView.playAnimation();
                    } else {
                        imageView.setImageResource(R.drawable.report_red);
                    }
                });
                imageView.fromRes(res.res);
            }
        }

        @Override
        public int getItemCount() {
            return drawables.size();
        }

        public static class SourceType {
            public boolean isVideo;
            public @AnyRes
            int res;
            public String asset;

            public SourceType(@AnyRes int video_res) {
                isVideo = true;
                res = video_res;
            }

            public SourceType(@NonNull String asset_file) {
                isVideo = false;
                asset = "file:///android_asset/dedicated/" + asset_file;
            }
        }
    }
}
