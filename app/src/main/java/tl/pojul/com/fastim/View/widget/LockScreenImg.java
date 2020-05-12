package tl.pojul.com.fastim.View.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.pojul.objectsocket.utils.LogUtil;

import tl.pojul.com.fastim.transfer.lockscreenimg.Transfer;

public class LockScreenImg extends android.support.v7.widget.AppCompatImageView{

    private static final String TAG = "LockScreenImg";
    private String imgMode = "width";

    private Bitmap mBitmap;
    private Bitmap prevBitmap;

    private int offsetX = 0;
    private Rect prevDrawRect;
    private Rect currentDrawRect;
    private float prevTouchX;
    private float prevDsx;
    private float prevTouchY;
    private int touchMode = -1; //1: 移动模式; 2: 缩放模式; 3: 锁屏解锁模式

    public boolean isTouch;
    public boolean isAnimator;

    private float dsX;
    private float dsY;
    private float startDsy;
    private float scaleImg;

    public boolean isTransferAni = false;

    private ScaleListener scaleListener;
    private Transfer transfer;
    private float progress;
    private boolean canSlideToUnlock; //滑动解锁
    private OnSlideToUnlock onSlideToUnlockListener;
    private float unlockScale = 1;
    private float rawTouchX;
    private float rawTouchY;
    private float maxUnlockRate = 0.7f;
    private float unlockScaleRate = 1.7f;
    private float maxUnlockScale = 1.54f;

    public LockScreenImg(Context context) {
        super(context);
    }

    public LockScreenImg(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockScreenImg(Context context, AttributeSet attrs, int defStyleAttr) {
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
        if(mBitmap == null){
            return;
        }
        try{
            if(isTransferAni && transfer != null && prevBitmap != null && mBitmap != null){
                transfer.drawBitmap(canvas, progress);
            }else{
                if("height".equals(imgMode)){
                    currentDrawRect = new Rect(0, 0, getWidth(), getHeight());
                    canvas.drawBitmap(mBitmap, 0, 0, new Paint());
                }else{
                    int left = (int) ((mBitmap.getWidth() - getWidth()) * 0.5f) + offsetX;
                    currentDrawRect = new Rect(left, 0 , (left + getWidth()), getHeight());
                    canvas.drawBitmap(mBitmap, currentDrawRect,
                            new Rect(0, 0, getWidth(), getHeight()), new Paint());
                }
            }

        }catch(Exception e){
        }
    }

    public void setBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            Log.i(TAG, "图片为空");
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        if (bitmap == null) {
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
        float bitmapScale = heightBitmap * 1.0f / widthBitmap;
        if(mBitmap != null){
            prevBitmap = mBitmap;
            /*int left = (int) ((prevBitmap.getWidth() - getWidth()) * 0.5f) + offsetX;*/
            prevDrawRect = currentDrawRect;
        }
        offsetX = 0;
        isAnimator = false;
        scaleImg = 1;
        Log.e(TAG, "setBitmap");
        if(bitmapScale > scale){
            imgMode = "height";
            int cropHeight = (int) (widthBitmap * scale);
            Bitmap cropBitmap = Bitmap.createBitmap(bitmap, 0, (int)((heightBitmap - cropHeight) * 0.5f), widthBitmap, cropHeight);
            this.mBitmap = Bitmap.createScaledBitmap(cropBitmap, width, height, true);
            currentDrawRect = new Rect(0, 0, getWidth(), getHeight());
        }else{
            imgMode = "width";
            float scaleWidth = heightBitmap * 1.0f/ height;
            this.mBitmap = Bitmap.createScaledBitmap(bitmap, (int) (widthBitmap * 1.0f/ scaleWidth), height, true);
            int left = (int) ((mBitmap.getWidth() - getWidth()) * 0.5f) + offsetX;
            currentDrawRect = new Rect(left, 0 , (left + getWidth()), getHeight());
        }
        if(transfer != null && prevBitmap != null){
            startTransferAni();
        }
    }

    private void startTransferAni() {
        ValueAnimator transferAni = ValueAnimator.ofFloat(0, 1);
        transferAni.setDuration(1200);
        progress = 0;
        if(transfer != null){
            transfer.setBitmaps(mBitmap, currentDrawRect, prevBitmap, prevDrawRect, getWidth(), getHeight());
        }
        transferAni.addUpdateListener(animation -> {
            if(!isTransferAni || transfer == null && prevBitmap == null && mBitmap == null){
                isTransferAni = false;
                progress = 0;
                invalidate();
                transferAni.cancel();
                if(transfer != null){
                    transfer.clearData();
                }
                return;
            }
            progress = (float) transferAni.getAnimatedValue();
            invalidate();
            if(progress >= 1){
                progress = 0;
                isTransferAni = false;
                if(transfer != null){
                    transfer.clearData();
                }
            }
        });
        isTransferAni = true;
        transferAni.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(isTransferAni){
            return super.onTouchEvent(event);
        }
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isAnimator){
                    isAnimator = false;
                }
                isTouch = true;
                prevTouchX = event.getX();
                prevTouchY = event.getY();
                rawTouchX = event.getX();
                rawTouchY = event.getY();
                unlockScale = 1;
                break;
            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                onTouchUp();
                break;
            case MotionEvent.ACTION_UP:
                onTouchUp();
                break;
        }
        return super.onTouchEvent(event);
    }

    private void onTouchUp() {
        isTouch = false;
        if(touchMode == 1){
            if(Math.abs(dsX) * 2f <  Math.abs(prevDsx)){
                startPanAnimator(prevDsx);
            }else{
                startPanAnimator(dsX);
            }
        }else if(touchMode == 2){
            Log.e(TAG, "scaleImg: " + scaleImg);
            if(scaleImg >= 1.6f){
                if(scaleListener != null){
                    scaleListener.onTouchUpOver();
                }
            }else{
                startScaleAnimator(scaleImg);
            }
        }else if(touchMode==3){
            float currentScale = getScaleX();
            LogUtil.e("onSlideToUnlockListener current scale: " + getScaleX());
            if(currentScale<maxUnlockScale){
                onUnlockCancel();
            }else if(onSlideToUnlockListener!=null){
                onSlideToUnlockListener.onUnlock();
            }
        }
        if(touchMode!=3){
            touchMode = -1;
        }
        dsX = 0;
        dsY = 0;
    }

    private void onUnlockCancel() {
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat((getScaleY()-1), 0);
        scaleAnimator.setDuration(350);
        scaleAnimator.addUpdateListener(animation -> {
            if(!isAnimator){
                scaleAnimator.cancel();
                return;
            }
            float value = (float) scaleAnimator.getAnimatedValue();
            float scale = 1 + value;
            LockScreenImg.this.setScaleY(scale);
            LockScreenImg.this.setScaleX(scale);
            if(value <= 0){
                LockScreenImg.this.setScaleY(1);
                LockScreenImg.this.setScaleX(1);
                isAnimator = false;
                touchMode = -1;
            }
        });
        isAnimator = true;
        scaleAnimator.start();
    }

    private void onTouchMove(MotionEvent event) {
        prevDsx = dsX;
        dsX = event.getX() - prevTouchX;
        dsY = event.getY() - prevTouchY;
        float rate = (dsX/dsY);
        LogUtil.e(TAG, ("MOVE RATE: " + dsX/dsY));
        if(touchMode == -1){
            if(Math.abs(rate)>maxUnlockRate){
                touchMode = 1;
            }else if(canSlideToUnlock){
                touchMode = 3;
            }
            /*if((Math.abs(dsY) > (Math.abs(dsX) * 1.6f)) || scaleImg > 1.01f){
                touchMode = 2;
                startDsy = dsY;
            }else{
                touchMode = 1;
            }*/
        }
        if(touchMode == 1){
            if(Math.abs(rate)>maxUnlockRate){
                panImage(dsX, dsY);
            }
        }else if(touchMode == 2){
            scaleImage(dsX, dsY);
        }else if(touchMode == 3){
            float distanceRawX = Math.abs(event.getX()-rawTouchX);
            float distanceRawY = Math.abs(event.getY()-rawTouchY);
            float scaleRateX = distanceRawX/getWidth()*unlockScaleRate;
            float scaleRateY = distanceRawY/getHeight()*unlockScaleRate;
            LogUtil.e("scaleRateX: " + scaleRateX + "; "+"scaleRateY: " + scaleRateY);
            unlockScale = 1 + (scaleRateX+scaleRateY);
            setScaleX(unlockScale);
            setScaleY(unlockScale);
        }
        prevTouchX = event.getX();
        prevTouchY = event.getY();
    }

    private void scaleImage(float dsX, float dsY) {
        float compareStart = startDsy * dsY;
        float tempScaleImg = scaleImg + Math.abs(dsY) * 0.0012f * (compareStart/Math.abs(compareStart));
        if(tempScaleImg <= 1){
            return;
        }
        scaleImg = tempScaleImg;
        this.setScaleX(scaleImg);
        this.setScaleY(scaleImg);
        if(scaleListener != null){
            scaleListener.onProgress();
        }
    }

    private void panImage(float dsX, float dsY) {
        if(mBitmap == null){
            return;
        }
        if("height".equals(imgMode)){
            return;
        }
        int tempOffertX = (int) (offsetX - dsX * 0.9f);
        if(Math.abs(tempOffertX) > ((mBitmap.getWidth() - getWidth()) * 0.5f)){
            if(isAnimator){
                isAnimator = false;
            }
            return;
        }
        offsetX = tempOffertX;
        invalidate();
    }

    private void startPanAnimator(float tempDsX){
        ValueAnimator panAnimator = ValueAnimator.ofFloat(1, 0);
        panAnimator.setDuration(450);
        panAnimator.addUpdateListener(animation -> {
            if(!isAnimator){
                panAnimator.cancel();
                return;
            }
            float value = (float) panAnimator.getAnimatedValue();
            panImage(tempDsX * value * 0.9f, 0);
            if(value <= 0){
                isAnimator = false;
            }
        });
        isAnimator = true;
        panAnimator.start();
    }

    private void startScaleAnimator(float scaleImg){
        ValueAnimator scaleAnimator = ValueAnimator.ofFloat(1, 0);
        scaleAnimator.setDuration(400);
        scaleAnimator.addUpdateListener(animation -> {
            if(!isAnimator){
                scaleAnimator.cancel();
                if(scaleListener != null){
                    scaleListener.onCancel();
                }
                LockScreenImg.this.scaleImg = LockScreenImg.this.getScaleX();
                return;
            }
            float value = (float) scaleAnimator.getAnimatedValue();
            float scale = (1 + (scaleImg -1) * value);
            LockScreenImg.this.setScaleY(scale);
            LockScreenImg.this.setScaleX(scale);
            if(scaleListener != null){
                scaleListener.onProgress();
            }
            if(value <= 0){
                LockScreenImg.this.setScaleY(1);
                LockScreenImg.this.setScaleX(1);
                isAnimator = false;
                LockScreenImg.this.scaleImg = 1;
                if(scaleListener != null){
                    scaleListener.onFinish();
                }
            }
        });
        isAnimator = true;
        scaleAnimator.start();
    }

    public interface ScaleListener{
        void onFinish();
        void onProgress();
        void onCancel();
        void onTouchUpOver();
    }

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public ScaleListener getScaleListener() {
        return scaleListener;
    }

    public void setScaleListener(ScaleListener scaleListener) {
        this.scaleListener = scaleListener;
    }

    public Bitmap getmBitmap() {
        return mBitmap;
    }

    public void setCanSlideToUnlock(boolean canSlideToUnlock) {
        this.canSlideToUnlock = canSlideToUnlock;
    }

    public void setOnSlideToUnlockListener(OnSlideToUnlock onSlideToUnlockListener) {
        this.onSlideToUnlockListener = onSlideToUnlockListener;
    }

    public interface OnSlideToUnlock{
        void onUnlock();
    }
}
