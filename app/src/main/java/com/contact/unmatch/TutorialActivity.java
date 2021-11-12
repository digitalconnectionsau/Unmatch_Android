package com.contact.unmatch;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.contact.unmatch.authentication.AuthenticationActivity;
import com.contact.unmatch.utils.Utils;
import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.model.SliderPage;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setTitle(getString(R.string.menu_items));
        }*/

        setSkipButtonEnabled(false);
        showStatusBar(true);
        //setStatusBarColorRes(R.color.colorPrimaryDark);
        setIndicatorColor(getColor(R.color.red), getColor(R.color.gray));
        setColorDoneText(Color.BLACK);

        SliderPage sliderPage1 = new SliderPage();
        sliderPage1.setTitle(getString(R.string.intro_title1));
        sliderPage1.setTitleColor(Color.BLACK);
        sliderPage1.setDescription(getString(R.string.intro_description1));
        sliderPage1.setDescriptionColor(Color.BLACK);
        sliderPage1.setImageDrawable(R.drawable.ic_onboarding1);
        sliderPage1.setBackgroundColor(Color.WHITE);
        //sliderPage1.setBackgroundDrawable(R.drawable.onboarding1);
        addSlide(AppIntroFragment.newInstance(sliderPage1));

        SliderPage sliderPage2 = new SliderPage();
        sliderPage2.setTitle(getString(R.string.intro_description2));
        sliderPage2.setTitleColor(Color.BLACK);
        sliderPage2.setDescription(getString(R.string.Tutorial_2_Content));
        sliderPage2.setDescriptionColor(Color.BLACK);
        sliderPage2.setImageDrawable(R.drawable.ic_onboarding2);
        sliderPage2.setBackgroundColor(Color.WHITE);
        //sliderPage2.setBackgroundDrawable(R.drawable.onboarding2);
        addSlide(AppIntroFragment.newInstance(sliderPage2));

        SliderPage sliderPage3 = new SliderPage();
        sliderPage3.setTitle(getString(R.string.intro_title3));
        sliderPage3.setTitleColor(Color.BLACK);
        sliderPage3.setDescription(getString(R.string.intro_description3));
        sliderPage3.setDescriptionColor(Color.BLACK);
        sliderPage3.setImageDrawable(R.drawable.ic_onboarding3);
        sliderPage3.setBackgroundColor(Color.WHITE);
        //sliderPage3.setBackgroundDrawable(R.drawable.onboarding3);
        addSlide(AppIntroFragment.newInstance(sliderPage3));

        SliderPage sliderPage4 = new SliderPage();
        sliderPage4.setTitle(getString(R.string.intro_title4));
        sliderPage4.setTitleColor(Color.BLACK);
        sliderPage4.setDescription(getString(R.string.intro_description4));
        sliderPage4.setDescriptionColor(Color.BLACK);
        sliderPage4.setImageDrawable(R.drawable.ic_onboarding4);
        sliderPage4.setBackgroundColor(Color.WHITE);
        //sliderPage4.setBackgroundDrawable(R.drawable.onboarding4);
        addSlide(AppIntroFragment.newInstance(sliderPage4));

        /*SliderPage sliderPage5 = new SliderPage();
        sliderPage5.setTitle(getString(R.string.Tutorial_5_Title));
        //sliderPage5.setDescription(getString(R.string.Tutorial_5_Content));
        sliderPage5.setImageDrawable(R.drawable.tutor5);
        sliderPage5.setBackgroundColor(Color.TRANSPARENT);
        sliderPage5.setBackgroundDrawable(R.drawable.back_slide2);
        addSlide(AppIntroFragment.newInstance(sliderPage5));

        SliderPage sliderPage6 = new SliderPage();
        sliderPage6.setTitle(getString(R.string.Tutorial_6_Title));
        //sliderPage6.setDescription(getString(R.string.Tutorial_6_Content));
        sliderPage6.setImageDrawable(R.drawable.tutor6);
        sliderPage6.setBackgroundColor(Color.TRANSPARENT);
        sliderPage6.setBackgroundDrawable(R.drawable.back_slide2);
        addSlide(AppIntroFragment.newInstance(sliderPage6));*/
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        SharedPreferences sharedPreferences = Utils.getPreferences();
        boolean isFirstLaunch = sharedPreferences.getBoolean("FIRST_LAUNCH", true);
        if (isFirstLaunch) {
            sharedPreferences.edit().putBoolean("FIRST_LAUNCH", false).apply();
            startActivity(new Intent(this, AuthenticationActivity.class));
            //overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
