package tl.pojul.com.fastim.View.widget.TransitImage;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import tl.pojul.com.fastim.View.widget.TransitImage.Transit.LeftSlowOutTransit;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.RightAlphaInTransit;
import tl.pojul.com.fastim.View.widget.TransitImage.Transit.Transit;
import tl.pojul.com.fastim.util.CustomTimeDown;

public class TransitImageView extends android.support.v7.widget.AppCompatImageView{

    private static final String TAG = "TransitImageView";

    private float progress;
    private Bitmap mBitmap;

    private Transit transit;

    public boolean isAnimator;

    public boolean isTouched;

    private TransitListener transitListener;

    private String viewTag;
    private ValueAnimator animator;

    private boolean isMove;

    //private int moveDirection = 0;

    public TransitImageView(Context context) {
        super(context);
    }

    public TransitImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransitImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        setBitmap();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setBitmap();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mBitmap == null){
            setBitmap();
        }
        try{
            if(!isAnimator && !isTouched){
                canvas.drawBitmap(mBitmap, 0, 0 , new Paint());
                return;
            }
        }catch (Exception e){Log.e(TAG, "" + e.getMessage());return;}

        if(isTouched && !isMove){
            return;
        }
        if(transit == null || (!isAnimator && !isMove)){
            return;
        }
        try{
            if(mBitmap == null){
                setBitmap();
            }
            switch(transit.getTransitType()){
                case Transit.RIGHT_ALPHA_IN:
                    ((RightAlphaInTransit)transit).drawRightAlpaIn(this ,mBitmap , canvas, progress, getWidth(), getHeight());
                    break;
                case Transit.LEFT_SLOW_OUT:
                    ((LeftSlowOutTransit)transit).drawLeftOut(this ,mBitmap , canvas, progress, getWidth());
                    break;
            }
        }catch (Exception e){Log.e(TAG, "" + e.getMessage());}
    }

    public void startTransitAnimator(Transit transit){
        if(transit == null){
            return;
        }
        this.transit = transit;
        switch(this.transit.getTransitType()) {
            case Transit.RIGHT_ALPHA_IN:
                this.setTranslationX(((RightAlphaInTransit)this.transit).getOffsetX() * getWidth());
                break;
            case Transit.LEFT_SLOW_OUT:
                break;
        }
        startAnimator(0, 1);
    }

    private void startAnimator(float startProgress, float stopProgress){
        animator = ValueAnimator.ofFloat(startProgress, stopProgress);
        animator.setDuration(transit.getDuring());
        isAnimator = true;
        progress = 0;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(transit == null || isTouched){
                    return;
                }
                progress = (Float) animation.getAnimatedValue();
                transitListener.onProgress(getViewTag(), progress);
                invalidate();
                if(stopProgress >= 1 && progress >= 1){
                    isAnimator = false;
                    progress = 0;
                    if(transitListener != null){
                        transitListener.onFinish(getViewTag());
                    }
                }
                if(stopProgress <= 0 && progress <= 0){
                    isAnimator = false;
                    progress = 0;
                    if(transitListener != null){
                        transitListener.onFinish(getViewTag());
                    }
                }
            }
        });
        animator.start();
    }

    private void startAnimatorByProgress(Transit transit, int direction, boolean reversed) {
        //float startProgress = 0.0f;
        /*if(progress >= 1 || progress <= 0){
            return;
        }*/
        if(progress >= 1){
            progress = 1;
        }
        if(progress <= 0){
            progress = 0;
        }
        float endProgress = 0;
        int passedDuring  = (int) (progress * transit.getDuring());;
        if(direction == 1){
            transit.setDuring(transit.getDuring() - passedDuring);
            endProgress = 1;
        }else{
            transit.setDuring(passedDuring);
        }
        this.transit = transit;
        startAnimator(progress, endProgress);
    }

    public void setBitmap() {
        Drawable drawable = getDrawable();
        if(drawable == null){
            Log.i(TAG, "图片为空");
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        if(bitmap == null){
            Log.i(TAG, "图片为空");
            return;
        }
        int width = getWidth();
        int height = getHeight();
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        if(height == 0 || width == 0){
            return;
        }
        float scale = height * 1.0f / width;
        int cropHeight = (int) (widthBitmap * scale);
        Bitmap cropBitmap;
        if(cropHeight < heightBitmap){
            cropBitmap = Bitmap.createBitmap(bitmap, 0, (int)((heightBitmap - cropHeight) * 0.5f), widthBitmap, cropHeight);
        }else{
            int cropWidth = (int) (heightBitmap / scale);
            cropBitmap = Bitmap.createBitmap(bitmap, (int)((widthBitmap - cropWidth) * 0.5f), 0, cropWidth, heightBitmap);
        }
        Bitmap scaleBitmap = Bitmap.createScaledBitmap(cropBitmap, width, height, true);
        this.mBitmap = scaleBitmap;
        //Log.e(TAG, width + "::" + height + "; " + widthBitmap + "::" + heightBitmap);
    }

    public void setTransitListener(TransitListener transitListener) {
        this.transitListener = transitListener;
    }

    public String getViewTag() {
        return viewTag;
    }

    public void setViewTag(String viewTag) {
        this.viewTag = viewTag;
    }

    public interface TransitListener{
        void onFinish(String tag);
        void onProgress(String tag, float progress);
    }

    public void onTouchDown(){
        isTouched = true;
        if(isAnimator && animator != null){
            animator.cancel();
        }
    }

    public void onTouchUp(Transit transit, int direction, boolean reversed){
        isTouched = false;
        isMove = false;
        startAnimatorByProgress(transit, direction, reversed);
    }

    public void moveByProgress(int transitType, float dpro, boolean reversed){
        isMove = true;
        isTouched = true;
        switch (transitType){
            case Transit.RIGHT_ALPHA_IN:
                if(transit == null || !(transit instanceof RightAlphaInTransit)){
                    transit = new RightAlphaInTransit();
                }
                if(getVisibility() == INVISIBLE){
                    setTranslationX(((RightAlphaInTransit)transit).getOffsetX() * getWidth());
                    setVisibility(VISIBLE);
                }
                //progress = 1 - getTranslationX() * 1.0f / (((RightAlphaInTransit)transit).getOffsetX() * getWidth()) + dpro;
                break;
            case Transit.LEFT_SLOW_OUT:
                if(transit == null || !(transit instanceof LeftSlowOutTransit)){
                    transit = new LeftSlowOutTransit();
                }
                if(getVisibility() == INVISIBLE){
                    setVisibility(VISIBLE);
                }
                //progress = - getTranslationX()  / (((LeftSlowOutTransit)transit).getToOffsetX() * getWidth()) + dpro;
                break;
        }
        if(reversed && progress == 0){
            progress = 1;
        }
        progress = progress + dpro;
        if(progress >= 1 || progress <= 0){
            return;
        }
        invalidate();
        transitListener.onProgress(getViewTag(), progress);
    }

    public float getProgress(){
        return progress;
    }

}
