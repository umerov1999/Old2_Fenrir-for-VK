package dev.ragnarok.fenrir.settings;

import android.content.Context;
import android.content.pm.PackageManager;

public class AppPrefs {

    public static boolean isCoubInstalled(Context context) {
        return isPackageIntalled(context, "com.coub.android");
    }

    public static boolean isNewPipeInstalled(Context context) {
        return isPackageIntalled(context, "org.schabi.newpipe");
    }

    private static boolean isPackageIntalled(Context context, String name) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(name, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException ignored) {
            return false;
        }
    }
}
