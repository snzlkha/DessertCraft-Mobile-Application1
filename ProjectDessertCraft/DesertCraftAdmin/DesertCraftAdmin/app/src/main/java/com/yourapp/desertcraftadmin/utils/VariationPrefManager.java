package com.yourapp.desertcraftadmin.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Objects;

public class VariationPrefManager {
    private final String prefName = "VariationSharedPreferences";
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    public VariationPrefManager(Context context) {
        pref = Objects.requireNonNull(context).getSharedPreferences(prefName, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setVariationData(String variation) {
        if (editor != null) {
            editor.putString("variation", variation);
            editor.commit();
        }
    }

    public String getVariationData() {
        return pref != null ? pref.getString("variation", "") : "";
    }

    public void setCombinationData(String variation) {
        if (editor != null) {
            editor.putString("combination", variation);
            editor.commit();
        }
    }

    public String getCombinationData() {
        return pref != null ? pref.getString("combination", "") : "";
    }

    public void clearVariation() {
        if (editor != null) {
            editor.clear();
            editor.apply();
        }
    }

}


