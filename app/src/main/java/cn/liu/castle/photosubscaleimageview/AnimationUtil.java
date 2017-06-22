package cn.liu.castle.photosubscaleimageview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by asstea on 2017/3/22
 */

public class AnimationUtil {
    //拦截器，用于实现旋转动画
    private static AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();
    private static OvershootInterpolator OVERSHOOT_INTERPOLATOR = new OvershootInterpolator(4f);

    public AnimationUtil() {
        throw new UnsupportedOperationException("为什么要new我");
    }

    public static AnimationSet getBottomTranslateInAnimation(Context context) {
        AnimationSet in = new AnimationSet(context, null);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(150);
        in.addAnimation(translateAnimation);
        return in;
    }

    public static AnimationSet getBottomTranslateOutAnimation(Context context) {
        AnimationSet out = new AnimationSet(context, null);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f);
        translateAnimation.setDuration(150);
        out.addAnimation(translateAnimation);
        return out;
    }

    public static AnimationSet getTopTranslateInAnimation(Context context) {
        AnimationSet in = new AnimationSet(context, null);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        translateAnimation.setDuration(150);
        in.addAnimation(translateAnimation);
        return in;
    }

    public static AnimationSet getTopTranslateOutAnimation(Context context) {
        AnimationSet in = new AnimationSet(context, null);
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        translateAnimation.setDuration(150);
        in.addAnimation(translateAnimation);
        return in;
    }


    public static AnimationSet getScaleInAnimation(Context context) {
        AlphaAnimation alpha = new AlphaAnimation(0.0f, 1.0f);
        AnimationSet in = new AnimationSet(context, null);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                0.6f, 1.0f, 0.6f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setInterpolator(OVERSHOOT_INTERPOLATOR);
        scaleAnimation.setDuration(150);
        alpha.setDuration(150);
        in.addAnimation(scaleAnimation);
        in.addAnimation(alpha);
        return in;
    }

    public static AnimationSet getScaleOutAnimation(Context context) {
        AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.0f);
        AnimationSet out = new AnimationSet(context, null);
        ScaleAnimation scaleAnimation = new ScaleAnimation(
                1.0f, 0.60f, 1.0f, 0.6f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(150);
        scaleAnimation.setInterpolator(OVERSHOOT_INTERPOLATOR);
        alpha.setDuration(150);
        out.addAnimation(scaleAnimation);
        out.addAnimation(alpha);
        return out;
    }

    public static void animateLikeButton(TextView userLikeTv, AnimatorListenerAdapter animatorListenerAdapter) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator rotationAnim = ObjectAnimator.ofFloat(userLikeTv, "rotation", 0f, 360f);
        rotationAnim.setDuration(150);
        rotationAnim.setInterpolator(ACCELERATE_INTERPOLATOR);

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(userLikeTv, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(150);
        bounceAnimX.setInterpolator(OVERSHOOT_INTERPOLATOR);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(userLikeTv, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(150);
        bounceAnimY.setInterpolator(OVERSHOOT_INTERPOLATOR);
        bounceAnimY.addListener(animatorListenerAdapter);
        animatorSet.play(bounceAnimX).with(bounceAnimY).after(rotationAnim);
        animatorSet.start();
    }

    /**
     * 共享元素转换option生成类
     *
     * @param activity       activity上下文
     * @param transitionView 共享元素View
     * @param transitionName 共享元素name,注意要保持独特性
     * @return 生成的option, 用于跳转的时候使用
     */
    public static Bundle transitionOptions(Activity activity, View transitionView, String transitionName) {

        View decorView = activity.getWindow().getDecorView();
        View statusBar = decorView.findViewById(android.R.id.statusBarBackground);
        View navigationBar = decorView.findViewById(android.R.id.navigationBarBackground);

        List<Pair<View, String>> pairs = new ArrayList<>();
        if (statusBar != null) {
//            pairs.add(Pair.create(statusBar, Window.STATUS_BAR_BACKGROUND_TRANSITION_NAME));
        }
        if (navigationBar != null) {
            //pairs.add(Pair.create(navigationBar, Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME));
        }
        if (transitionView != null) {
            pairs.add(Pair.create(transitionView, transitionName));
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs.toArray(new Pair[pairs.size()]))
                .toBundle();
    }


    public static void variedHeight(final View view, final int variedHeight, final long duration, Animator.AnimatorListener listener) {
        final int height = view.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(1, 100);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (Integer) animation.getAnimatedValue();
                float fraction = currentValue / 100f;
                IntEvaluator mEvaluator = new IntEvaluator();
                view.getLayoutParams().height = mEvaluator.evaluate(fraction, height, variedHeight);
                view.requestLayout();
            }
        });
        if (listener != null) {
            valueAnimator.addListener(listener);
        }
        valueAnimator.setDuration(duration).start();
    }
}
