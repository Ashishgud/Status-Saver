package com.developer.ashishtech.statussaver.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.developer.ashishtech.statussaver.R;
import com.developer.ashishtech.statussaver.Utils.Constants;



public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("STEP 1", "Tap to open WhatsApp.", R.drawable.intro_one, Color.parseColor("#00695c")));
        addSlide(AppIntroFragment.newInstance("STEP 2", "View Recent Status and Open Status Saver.", R.drawable.intro_three, Color.parseColor("#2B3A3D")));
        addSlide(AppIntroFragment.newInstance("STEP 3", "Download Status you want!", R.drawable.intro_two, Color.parseColor("#2B3A3D")));
        showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        SharedPreferences.Editor editor = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE).edit();
        editor.putBoolean("AppIntro", false);
        editor.apply();
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
    }
}
