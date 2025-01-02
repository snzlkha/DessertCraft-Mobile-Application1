package com.yourapp.desertcraftadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

public class PrefManager {
    private final String prefName = "sharedPreferences";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private static final String IS_LOGIN = "isLoggedIn";

    public PrefManager(Context context) {
        pref = Objects.requireNonNull(context).getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public boolean isLoggedIn() {
        return pref != null && pref.getBoolean(IS_LOGIN, false);
    }

    public void setLoggedIn(boolean isLoggedIn) {
        if (editor != null) {
            editor.putBoolean(IS_LOGIN, isLoggedIn);
            editor.apply();
        }
    }

    public void signOut() {
        if (editor != null) {
            editor.clear();
            editor.apply();
        }
    }

    public void setUserData(String uid, String name, String email, String dob, String gender, String mobile, String created, String updated) {
        if (editor != null) {
            editor.putString("uid", uid);
            editor.putString("name", name);
            editor.putString("email", email);
            editor.putString("dob", dob);
            editor.putString("gender", gender);
            editor.putString("mobile", mobile);
            editor.putString("created_on", created);
            editor.putString("updated_on", updated);
            editor.commit();
        }
    }

    public String getUid() {
        return pref != null ? pref.getString("uid", "") : "";
    }

    public String getName() {
        return pref != null ? pref.getString("name", "") : "";
    }

    public String getEmail() {
        return pref != null ? pref.getString("email", "") : "";
    }

    public String getDob() {
        return pref != null ? pref.getString("dob", "") : "";
    }

    public String getGender() {
        return pref != null ? pref.getString("gender", "") : "";
    }

    public String getMobile() {
        return pref != null ? pref.getString("mobile", "") : "";
    }

    public String getCreatedOn() {
        return pref != null ? pref.getString("created_on", "") : "";
    }

    public String getUpdatedOn() {
        return pref != null ? pref.getString("updated_on", "") : "";
    }


    private static final String IS_REFRESH_AVAILABLE = "isRefreshAvailable";

    public boolean isRefreshAvailable() {
        return pref != null && pref.getBoolean(IS_REFRESH_AVAILABLE, false);
    }

    public void setRefreshAvailable(boolean isRefresh) {
        if (editor != null) {
            editor.putBoolean(IS_REFRESH_AVAILABLE, isRefresh);
            editor.apply();
        }
    }
}


