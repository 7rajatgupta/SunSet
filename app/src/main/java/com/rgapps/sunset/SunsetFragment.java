package com.rgapps.sunset;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Toast;

/**
 * Created by rajat on 25/1/17.
 */

public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;
    private View mShadowView;
    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private int mHeatSunColor;
    private int mColdSunColor;
    private int DURATION = 4000;

    private boolean mSunset = true;
    private float mSunYCurrent = Float.NaN;
    private float mShadowYCurrent = Float.NaN;

    private int mSunsetSkyColorCurrent;
    private int mNightSkyColorCurrent;

    private AnimatorSet mSunsetAnimatorSet;
    private AnimatorSet mSunriseAnimatorSet;



    public static SunsetFragment newInstance(){
        return  new SunsetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View scene = inflater.inflate(R.layout.fragment_sunset, container, false);
        mSceneView = scene;
        mSkyView = (View) scene.findViewById(R.id.sky);
        mSunView =  (View) scene.findViewById(R.id.sun);
        mShadowView = (View) scene.findViewById(R.id.shadow);
        Resources resources = getResources();



        //Listening for click and according to that deciding whether to start sunrise animation or sunset animation.
        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startAnimation(); //Animate the sun to the sea


                if (mSunset){

                    //starting the sunset animation:
                    startSunsetAnimation();
                    Toast.makeText(getActivity(), "The Sun's going down !", Toast.LENGTH_LONG).show();
                    if (mSunriseAnimatorSet != null){

                        mSunriseAnimatorSet.end();
                        mSunriseAnimatorSet = null;
                    }
                }
                else {
                    startSunriseAnimation();
                    Toast.makeText(getActivity(), "Wakey Wakey, The sun's up !", Toast.LENGTH_LONG).show();

                    if (mSunsetAnimatorSet != null){
                        mSunsetAnimatorSet.end();
                        mSunsetAnimatorSet = null;
                    }
                }
                mSunset = !mSunset;
                //startSunPulsateAnimation();          //Working Phase. Stay Calm
            }
        });
        mBlueSkyColor = ContextCompat.getColor(getActivity(), R.color.blue_sky);
        mSunsetSkyColor = ContextCompat.getColor(getActivity(), R.color.sun_set_sky);
        mNightSkyColor = ContextCompat.getColor(getActivity(), R.color.night_sky);

        mHeatSunColor = ContextCompat.getColor(getActivity(), R.color.heat_sun);
        mColdSunColor = ContextCompat.getColor(getActivity(), R.color.cold_sun);
        return  scene;

    }


    private void startSunriseAnimation() {
        float sunYStart = (Float.valueOf(mSunYCurrent).isNaN() ?
                mSkyView.getHeight() : mSunYCurrent);
        float sunYEnd = mSunView.getTop();

        float shadowYStart = (Float.valueOf(mShadowYCurrent).isNaN() ?
                -mShadowView.getHeight() : mShadowYCurrent);
        float shadowYEnd = mShadowView.getTop();

        int sunsetSkyColorStart = (Float.valueOf(mSunYCurrent).isNaN() ?
                mSunsetSkyColor : mSunsetSkyColorCurrent);

        long duration = (Float.valueOf(mSunYCurrent).isNaN() ?
                DURATION : (long) (DURATION / (mSunView.getTop() - mSkyView.getHeight())
                * (mSunView.getTop() - mSunYCurrent)));

        int nightSkyColorStart = (mNightSkyColorCurrent == 0 ?
                mNightSkyColor : mNightSkyColorCurrent);

        ObjectAnimator sunHeightAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                sunYStart, sunYEnd)
                .setDuration(duration);

        sunHeightAnimator.setInterpolator(new DecelerateInterpolator());

        sunHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator shadowHeightAnimator = ObjectAnimator.ofFloat(mShadowView, "y",
                shadowYStart, shadowYEnd)
                .setDuration(duration);

        shadowHeightAnimator.setInterpolator(new DecelerateInterpolator());

        shadowHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mShadowYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator sunriseSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), sunsetSkyColorStart, mBlueSkyColor)
                .setDuration(duration);

        sunriseSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), nightSkyColorStart, mSunsetSkyColor)
                .setDuration(mSunYCurrent ==  mSkyView.getHeight() ? DURATION : 0);

        nightSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNightSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        mSunriseAnimatorSet = new AnimatorSet();
        mSunriseAnimatorSet
                .play(sunHeightAnimator)
                .with(shadowHeightAnimator)
                .with(sunriseSkyAnimator)
                .after(nightSkyAnimator);

        mSunriseAnimatorSet.start();
    }

    private void startSunsetAnimation() {
        float sunYStart = (Float.valueOf(mSunYCurrent).isNaN() ?
                mSunView.getTop() : mSunYCurrent);
        float sunYEnd = mSkyView.getHeight();

        float shadowYStart = (Float.valueOf(mShadowYCurrent).isNaN() ?
                mShadowView.getTop() : mShadowYCurrent);
        float shadowYEnd = -mShadowView.getHeight();

        int sunsetSkyColorStart = (mSunsetSkyColorCurrent == 0 ?
                mBlueSkyColor : mSunsetSkyColorCurrent);

        long duration = (Float.valueOf(mSunYCurrent).isNaN() ?
                DURATION : (long) (DURATION / (mSkyView.getHeight() - mSunView.getTop())
                * (mSkyView.getHeight() - mSunYCurrent)));

        int nightSkyColorStart = (mNightSkyColorCurrent == 0 ?
                mSunsetSkyColor : mNightSkyColorCurrent);

        ObjectAnimator sunHeightAnimator = ObjectAnimator.ofFloat(mSunView, "y",
                sunYStart, sunYEnd)
                .setDuration(duration);

        sunHeightAnimator.setInterpolator(new AccelerateInterpolator());

        sunHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator shadowHeightAnimator = ObjectAnimator.ofFloat(mShadowView, "y",
                shadowYStart, shadowYEnd)
                .setDuration(duration);

        shadowHeightAnimator.setInterpolator(new AccelerateInterpolator());

        shadowHeightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mShadowYCurrent = (float) animation.getAnimatedValue();
            }
        });

        ObjectAnimator sunsetSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), sunsetSkyColorStart, mSunsetSkyColor)
                .setDuration(duration);

        sunsetSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mSunsetSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        ObjectAnimator nightSkyAnimator = ObjectAnimator.ofObject(mSkyView, "backgroundColor",
                new ArgbEvaluator(), nightSkyColorStart, mNightSkyColor)
                .setDuration(DURATION);

        nightSkyAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mNightSkyColorCurrent = (int) animation.getAnimatedValue();
            }
        });

        mSunsetAnimatorSet = new AnimatorSet();
        mSunsetAnimatorSet
                .play(sunHeightAnimator)
                .with(shadowHeightAnimator)
                .with(sunsetSkyAnimator)
                .before(nightSkyAnimator);

        mSunsetAnimatorSet.start();
    }

    private void startSunPulsateAnimation() {

        //Beta Phase.. >_<

        ObjectAnimator sunShadowPulsateAnimator = ObjectAnimator.ofObject(mShadowView, "BackgroundColor",
                new ArgbEvaluator(), mHeatSunColor, mColdSunColor)
                .setDuration(2000);

        sunShadowPulsateAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sunShadowPulsateAnimator.setRepeatCount(ValueAnimator.INFINITE);

        ObjectAnimator sunPulsateAnimator = ObjectAnimator.ofObject(mSunView, "BackgroundColor",
                new ArgbEvaluator(), mHeatSunColor, mColdSunColor)
                .setDuration(2000);

        sunPulsateAnimator.setRepeatMode(ValueAnimator.REVERSE);
        sunPulsateAnimator.setRepeatCount(ValueAnimator.INFINITE);

        AnimatorSet sunShadowAnimatorSet = new AnimatorSet();
        sunShadowAnimatorSet.play(sunShadowPulsateAnimator).with(sunPulsateAnimator);
        sunShadowAnimatorSet.start();
    }


}
