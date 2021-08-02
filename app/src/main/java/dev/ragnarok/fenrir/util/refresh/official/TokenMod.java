package dev.ragnarok.fenrir.util.refresh.official;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import dev.ragnarok.fenrir.Injection;
import dev.ragnarok.fenrir.api.ProxyUtil;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TokenMod {
    private static final String agent = String.format("Android-GCM/1.5 (%s %s)", "Nexus 5", "Nexus 5");
    public static ArrayList<String> langs = new ArrayList<>();
    private static KeyPair pair;
    private static int rid;

    static {
        genNewKey();
    }

    public static String requestToken() {
        String str;
        String str2;
        try {
            System.out.println("Token register start");
            String[] strArr = {"3974055026275073921", "4418584909973341826", "4585634953328772978"};
            int nextInt = new Random().nextInt(strArr.length - 1);
            String str3 = "AidLogin " + strArr[nextInt] + ":" + new String[]{"1932960345884890854", "6594645578425092292", "1792344590975444730"}[nextInt];
            String genNewKey = genNewKey();
            String sig = getSig(genNewKey);
            byte[] encoded = pair.getPublic().getEncoded();
            try {
                encoded = MessageDigest.getInstance("SHA1").digest(encoded);
                str = null;
            } catch (NoSuchAlgorithmException unused) {
                str = "";
            }
            if (str == null) {
                encoded[0] = (byte) (((encoded[0] & 15) + 112) & 255);
                str2 = Base64.encodeToString(encoded, 2).substring(0, 11);
            } else {
                str2 = str;
            }
            StringBuilder sb = new StringBuilder();
            ArrayList<String> arrayList = new ArrayList<>();
            fillParams(arrayList, sig, genNewKey, str2, Long.parseLong(str3.split(" ")[1].split(":")[0]), false);
            sb.append(doRequest("https://android.clients.google.com/c2dm/register3", arrayList, str3).substring(20));
            String sb3 = sb.toString();
            if (sb3.equals("EGISTRATION_ERROR")) {
                System.out.println("Token register fail, retrying");
                return requestToken();
            }
            rid = 0;
            String genNewKey2 = genNewKey();
            String sig2 = getSig(genNewKey2);
            arrayList.clear();
            fillParams(arrayList, sig2, genNewKey2, str2, Long.parseLong(str3.split(" ")[1].split(":")[0]), true);
            doRequest("https://android.clients.google.com/c2dm/register3", arrayList, str3);
            System.out.println("Token register OK");
            return sb3;
        } catch (FileNotFoundException unused2) {
            return requestToken();
        } catch (Exception unused3) {
            return null;
        }
    }

    private static void fillParams(List<String> list, String str, String str2, String str3, long j, boolean z) {
        rid++;
        list.add("X-subtype=841415684880");
        if (z) {
            list.add("X-delete=1");
            list.add("X-X-delete=1");
        } else {
            list.add("X-X-subscription=841415684880");
        }
        list.add("X-X-subtype=841415684880");
        list.add("X-app_ver=1193");
        list.add("X-kid=|ID|" + rid + "|");
        list.add("X-osv=23");
        list.add("X-sig=" + str);
        list.add("X-cliv=fiid-9877000");
        list.add("X-gmsv=11949480");
        list.add("X-pub2=" + str2);
        list.add("X-X-kid=|ID|" + rid + "|");
        String sb = "X-appid=" +
                str3;
        list.add(sb);
        if (z) {
            list.add("X-scope=GCM");
        } else {
            list.add("X-scope=*");
        }
        list.add("X-subscription=841415684880");
        if (!z) {
            list.add("X-gmp_app_id=1:841415684880:android:632f429381141121");
        }
        list.add("X-app_ver_name=4.13.1");
        list.add("app=com.vkontakte.android");
        list.add("sender=841415684880");
        list.add("device=" + j);
        list.add("cert=48761eef50ee53afc4cc9c5f10e6bde7f8f5b82f");
        list.add("app_ver=1193");
        list.add("gcm_ver=11949470");
    }

    private static String join(String str, Iterable<String> iterable) {
        StringBuilder str2 = new StringBuilder();
        for (String next : iterable) {
            if (str2.length() == 0) {
                str2 = new StringBuilder(next);
            } else {
                str2.append(str).append(next);
            }
        }
        return str2.toString();
    }

    private static String join(String str, String[] strArr) {
        return join(str, Arrays.asList(strArr));
    }

    private static String doRequest(String str, List<String> list, String str3) throws IOException {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(chain -> chain.proceed(chain.request().newBuilder()
                        .addHeader("User-Agent", agent)
                        .addHeader("Authorization", str3)
                        .addHeader("app", "com.vkontakte.android")
                        .addHeader("Gcm-ver", "11947470")
                        .addHeader("Gcm-cert", "48761eef50ee53afc4cc9c5f10e6bde7f8f5b82f")
                        .build()));
        ProxyUtil.applyProxyConfig(builder, Injection.provideProxySettings().getActiveProxy());
        FormBody.Builder formBody = new FormBody.Builder();
        for (String i : list) {
            String[] v = i.split("=");
            formBody.add(v[0], v[1]);
        }
        Request request = new Request.Builder()
                .url(str)
                .post(formBody.build())
                .build();
        Response response = builder.build().newCall(request).execute();
        return new BufferedReader(new InputStreamReader(response.body().byteStream())).readLine();
    }

    private static String genNewKey() {
        try {
            KeyPairGenerator instance = KeyPairGenerator.getInstance("RSA");
            instance.initialize(2048);
            pair = instance.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(pair.getPublic().getEncoded(), 0);
    }

    private static String getSig(String str) {
        try {
            PrivateKey privateKey = pair.getPrivate();
            Signature instance = Signature.getInstance(privateKey instanceof RSAPrivateKey ? "SHA256withRSA" : "SHA256withECDSA");
            instance.initSign(privateKey);
            instance.update(join("\n", new String[]{"com.vkontakte.android", str}).getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(instance.sign(), 0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
