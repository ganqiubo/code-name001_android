package tl.pojul.com.fastim.util;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.pojul.fastIM.entity.ScreenPosition;

import tl.pojul.com.fastim.MyApplication;

public class AnimatorUtil {

    public static void startPopAnimator(View view, AnimatorListener animatoviewrListener){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float rawX = view.getX();
        float rawY = view.getY();

        float rawWidth = layoutParams.width;
        float rawHeight = layoutParams.height;

        float scalex = MyApplication.SCREEN_WIDTH *1.0f / rawWidth;
        float scaley = MyApplication.SCREEN_HEIGHT *1.0f / rawHeight;

        float dscale;
        float dy;
        float dx;
        if(scalex <= scaley){
            dscale = scalex - 1;
            dx =  - view.getX();
            dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
        }else{
            dscale = scaley - 1;
            dx =  - view.getX() + (scalex - scaley) * rawWidth *0.5f;
            dy = - view.getY();
        }
        view.setPivotX(0);
        view.setPivotY(0);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(380);



        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                view.setScaleX((1 +  dscale * animatedValue));
                view.setScaleY((1 +  dscale * animatedValue));
                view.setTranslationX((rawX + dx * animatedValue));
                view.setTranslationY((rawY + dy * animatedValue));
                if(animatedValue >= 1){
                    if(animatoviewrListener != null){
                        animatoviewrListener.onFinished();
                    }
                }
            }
        });
        animator.start();
    }

    public static void startPopObjAnimator(ImageView view, AnimatorListener animatoviewrListener){
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        float rawX = view.getX();
        float rawY = view.getY();

        float rawWidth = layoutParams.width;
        float rawHeight = layoutParams.height;

        float scalex = MyApplication.SCREEN_WIDTH *1.0f / rawWidth;
        float scaley = MyApplication.SCREEN_HEIGHT *1.0f / rawHeight;

        float scale;
        float dy;
        float dx;

        dx =  - view.getX();
        dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
        scale = scalex;
        /*if(scalex <= scaley){
            dx =  - view.getX();
            dy = - view.getY() + (scaley - scalex) * rawHeight *0.5f;
            scale = scalex;
        }else{
            dx =  - view.getX() + (scalex - scaley) * rawWidth *0.5f;
            dy = - view.getY();
            scale = scaley;
        }*/
        view.setPivotX(0);
        view.setPivotY(0);

        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view, "translationX", (rawX + dx));
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "translationY", (rawY + dy));
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleX", scale);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "scaleY", scale);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(400);
        //animatorSet.setStartDelay(50);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3).with(objectAnimator4);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                if(animatoviewrListener != null){
                    animatoviewrListener.onFinished();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animatorSet.start();
    }

    public static void startThumbUpAnimator(View view){
        ObjectAnimator objectAnimator1 = ObjectAnimator.ofFloat(view, "translationX", 35);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofFloat(view, "translationY", -35);
        ObjectAnimator objectAnimator3 = ObjectAnimator.ofFloat(view, "scaleX", 1.5f);
        ObjectAnimator objectAnimator4 = ObjectAnimator.ofFloat(view, "scaleY", 1.5f);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(700);
        animatorSet.play(objectAnimator1).with(objectAnimator2).with(objectAnimator3).with(objectAnimator4);
        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(View.GONE);
                view.setTranslationX(0);
                view.setTranslationY(0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        view.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    public static void startMoveScaleAni(ScreenPosition from, ScreenPosition to, int type,View view, AnimatorListener callback){
        float dsX = from.getX() - to.getX();
        float dsY = from.getY() - to.getY();
        ValueAnimator ani = ValueAnimator.ofFloat(0, 1);
        if(type == 1){ //显示
            view.setScaleX(0);
            view.setScaleY(0);
            ani.setStartDelay(100);
        }else{ //隐藏
            view.setScaleX(1);
            view.setScaleY(1);
        }
        ani.setDuration(460);
        view.setVisibility(View.VISIBLE);
        ani.addUpdateListener(animation -> {
            float value = (float) ani.getAnimatedValue();
            view.setTranslationX((from.getX() - dsX * value - view.getWidth() * 0.5f));
            view.setTranslationY((from.getY() - dsY * value) - view.getHeight() * 0.5f);
            if(type == 1){
                view.setScaleX(value);
                view.setScaleY(value);
            }else{
                view.setScaleX(1 - (value));
                view.setScaleY(1 - (value));
            }
            if(value >= 1){
                if(type == 2){
                    view.setVisibility(View.GONE);
                }
                if(callback != null){
                    callback.onFinished();
                }
            }
        });
        ani.start();
    }

    public interface AnimatorListener{
        void onFinished();
    }

}
