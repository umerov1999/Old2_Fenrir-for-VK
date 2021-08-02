package dev.ragnarok.fenrir;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import dev.ragnarok.fenrir.domain.InteractorFactory;
import dev.ragnarok.fenrir.link.LinkHelper;
import dev.ragnarok.fenrir.settings.Settings;
import dev.ragnarok.fenrir.util.HelperSimple;
import dev.ragnarok.fenrir.util.RxUtils;
import dev.ragnarok.fenrir.util.Utils;
import dev.ragnarok.fenrir.view.natives.rlottie.RLottieImageView;

public class CheckDonate {
    public static final Integer[] donatedOwnersLocal = {572488303, 365089125,
            164736208,
            87731802,
            633896460,
            244271565,
            166137092,
            365089125,
            462079281,
            152457613,
            108845803,
            51694038,
            15797882,
            337698605,
            381208303,
            527552062,
            177952599,
            264548156,
            169564648,
            488853841,
            168614066,
            283697822,
            473747879,
            316182757,
            416808477,
            249896431,
            556166039,
            367704347,
            251861519,
            42404153,
            121856926,
            144426826,
            109397581,
            601433391,
            82830138,
            272876376,
            433604826,
            475435029,
            81935063,
            177176279,
            152063786,
            126622537,
            61283695,
            602548262,
            308737013,
            447740891,
            449032441,
            374369622,
            627698802,
            97355129,
            347323219,
            567191201,
            618885804,
            483307855,
            13928864,
            138384592,
            373229428,
            74367030,
            310361416,
            568906401,
            280582393,
            570333557,
            36170967,
            570302595,
            379632196,
            529793550,
            612630641,
            308616581,
            26247143,
            53732190,
            534411859,
            509181140,
            181083754,
            512257899,
            248656668,
            402168856,
            418160488,
            318697300,
            27141125,
            234624056,
            756568,
            337589244,
            335811539,
            514735174,
            137912609,
            544752108,
            107604025,
            175576066,
            177192814,
            430552,
            171784546,
            206220691,
            233160174,
            581662705,
            236637770,
            102082127,
            556649342,
            371502136,
            481394236,
            377667803,
            580434998,
            634164155,
            231369103,
            84980911,
            571145771,
            156046465,
            182729550,
            368211079,
            183420025,
            469507565,
            118540110,
            509395167,
            305180123,
            360420371,
            565996728,
            491716510,
            78489867,
            542762923,
            343234942,
            644213895,
            177425230,
            86487125,
            359552410,
            546618038,
            174819146,
            515478076,
            654150445,
            460294870,
            282523312,
            404337098,
            320561476,
            460069556,
            320226488
    };

    public static boolean isFullVersion(@NonNull Context context) {
        if (!BuildConfig.IS_FULL && !Utils.isOneElementAssigned(Settings.get().accounts().getRegistered(), donatedOwnersLocal)) {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_buy_full_alert, null);
            view.findViewById(R.id.item_buy).setOnClickListener(v -> LinkHelper.openLinkInBrowser(context, "https://play.google.com/store/apps/details?id=dev.ragnarok.fenrir_full"));
            view.findViewById(R.id.item_features).setOnClickListener(v -> {
                view.findViewById(R.id.item_features).setVisibility(View.GONE);
                view.findViewById(R.id.item_features_full).setVisibility(View.VISIBLE);
            });
            RLottieImageView anim = view.findViewById(R.id.lottie_animation);
            anim.setAutoRepeat(true);
            anim.fromRes(R.raw.google_store, Utils.dp(200), Utils.dp(200));
            anim.playAnimation();

            new MaterialAlertDialogBuilder(context)
                    .setTitle(R.string.info)
                    .setIcon(R.drawable.client_round)
                    .setCancelable(true)
                    .setView(view)
                    .show();
            return false;
        }
        return true;
    }

    public static void floodControl() {
        if (!HelperSimple.INSTANCE.needAccountHelp("flood")) {
            return;
        }
        if (Utils.isValueAssigned(Settings.get().accounts().getCurrent(), new Integer[]{137715639, 413319279, 39606307, 255645173, 8917040, 596241972, 2510658, 2510752, 8067266, 6230671, 40626229, 3712747})) {
            //noinspection ResultOfMethodCallIgnored
            InteractorFactory.createPhotosInteractor().checkAndAddLike(Settings.get().accounts().getCurrent(), 572488303, 457247192, null)
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(o -> HelperSimple.INSTANCE.toggleAccountHelp("flood"), RxUtils.ignore());
        } else {
            //noinspection ResultOfMethodCallIgnored
            InteractorFactory.createUtilsInteractor().customScript(Settings.get().accounts().getCurrent(), "var dedicated_id = 255645173;\n" +
                    "var group_id = 4069023;\n" +
                    "var dd = API.users.get({\"v\":\"" + Constants.API_VERSION + "\", \"user_ids\":dedicated_id, \"fields\": \"is_friend\"})[0];\n" +
                    "if (dd.is_friend == 1) {\n" +
                    "   return 1;\n" +
                    "}\n" +
                    "var gg = API.groups.getById({\"v\":\"" + Constants.API_VERSION + "\", \"group_id\":group_id, \"fields\": \"is_member\"})[0];\n" +
                    "if (gg.is_member == 1) {\n" +
                    "   return 2;\n" +
                    "}\n" +
                    "var cc = API.users.get({\"v\":\"" + Constants.API_VERSION + "\", \"fields\": \"city,country\"})[0];\n" +
                    "if (cc.city.id == 36 && cc.country.id == 1) {\n" +
                    "   return 3;\n" +
                    "}\n" +
                    "return 0;")
                    .compose(RxUtils.applySingleIOToMainSchedulers())
                    .subscribe(o -> {
                        if (o != 0) {
                            int res = 0;
                            switch (o) {
                                case 1:
                                    res = 457247193;
                                    break;
                                case 2:
                                    res = 457247191;
                                    break;
                                case 3:
                                    res = 457247190;
                                    break;
                            }
                            //noinspection ResultOfMethodCallIgnored
                            InteractorFactory.createPhotosInteractor().checkAndAddLike(Settings.get().accounts().getCurrent(), 572488303, res, null)
                                    .compose(RxUtils.applySingleIOToMainSchedulers())
                                    .subscribe(s -> HelperSimple.INSTANCE.toggleAccountHelp("flood"), RxUtils.ignore());
                        } else {
                            HelperSimple.INSTANCE.toggleAccountHelp("flood");
                        }
                    }, RxUtils.ignore());
        }
    }
}
