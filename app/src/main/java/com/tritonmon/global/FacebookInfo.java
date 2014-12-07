package com.tritonmon.global;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.tritonmon.fragment.FacebookLoginFragment;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.Getter;
import lombok.Setter;

public class FacebookInfo {

    public static FacebookLoginFragment fragment;
    public static Session session;

    @Getter
    @Setter
    public static GraphUser facebookUser = null;

    public static String getId() {
        return facebookUser.getId();
    }

    public static String getName() {
        return facebookUser.getName();
    }

    public static void clear() {
        facebookUser = null;
    }

    /**
     * Helper method to get the key hash sent to Facebook
     *
     * @param context Calling context
     * @return Facebook key hash
     */
    public static String getFacebookHash(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo("com.tritonmon", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hash = new String(Base64.encode(md.digest(), 0));
                Log.d("FacebookInfo/getFacebookHash", "Facebook hash key" + hash);
                return hash;
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("FacebookInfo/getFacebookHash", "name not found - " + e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("FacebookInfo/getFacebookHash", "no such an algorithm - " +  e.toString());
        } catch (Exception e) {
            Log.e("FacebookInfo/getFacebookHash", "exception - " +  e.toString());
        }

        return null;
    }
}
