package study.heaven7.com.android_study.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Created by heaven7 on 2016/3/11.
 */
public class SingnUtil {

    public static String getPublicKey(byte[] signature) {
        try {

            CertificateFactory certFactory = CertificateFactory
                    .getInstance("X.509");
            X509Certificate cert = (X509Certificate) certFactory
                    .generateCertificate(new ByteArrayInputStream(signature));

            String publickey = cert.getPublicKey().toString();
            publickey = publickey.substring(publickey.indexOf("modulus: ") + 9,
                    publickey.indexOf("\n", publickey.indexOf("modulus:")));

            Log.d("TRACK", publickey);
            return publickey;
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static byte[] getSign(Context context,String expectPackname) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> apps = pm
                .getInstalledPackages(PackageManager.GET_SIGNATURES);

        for (PackageInfo info : apps){
            //按包名 取签名
            if (info.packageName.equals(expectPackname)) {
                return info.signatures[0].toByteArray();

            }
        }
        return null;
    }
}
