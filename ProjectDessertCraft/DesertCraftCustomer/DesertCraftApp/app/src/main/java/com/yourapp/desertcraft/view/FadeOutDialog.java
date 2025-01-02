package com.yourapp.desertcraft.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.yourapp.desertcraft.R;

public class FadeOutDialog extends Dialog {

    private Context context;

    public FadeOutDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void dismiss() {
        Animation fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out);
        getWindow().getDecorView().startAnimation(fadeOut);
        new Handler().postDelayed(super::dismiss, fadeOut.getDuration());
    }
}
