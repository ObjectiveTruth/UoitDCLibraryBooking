package com.objectivetruth.uoitlibrarybooking.userinterface.common;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

public class CustomAnimators {
    /**
     * starts the action bar bouncy animation. you can stop it by calling
     * myMenuItem.setActionView(null). Inflate, animate then set for actionbar icons.
     * @param iv View to be animated.
     */
    static public void startActionBarBounceAnimation(View iv){
        final ObjectAnimator animY = ObjectAnimator.ofFloat(iv, "translationY", 0f, -20f);
        animY.setDuration(125);
        animY.setInterpolator(new DecelerateInterpolator());
        animY.setRepeatCount(1);
        animY.setStartDelay(1000);
        animY.addListener(new Animator.AnimatorListener() {
            int repeatCount = 0;
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (repeatCount < 5) {
                    repeatCount++;
                    animY.setStartDelay(750);
                    animY.start();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        animY.setRepeatMode(ObjectAnimator.REVERSE);
        animY.start();
    }
}
