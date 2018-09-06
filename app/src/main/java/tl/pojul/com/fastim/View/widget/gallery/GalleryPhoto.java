package tl.pojul.com.fastim.View.widget.gallery;

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

public class GalleryPhoto extends android.support.v7.widget.AppCompatImageView{

    private float baseScale = 1; //最小缩放比例
    private float scale = 1;

    public GalleryPhoto(Context context) {
        super(context);
    }

    public GalleryPhoto(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public GalleryPhoto(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        /*canvas.drawBitmap(tempBitmap,
                (getWidth() - tempBitmap.getWidth()) * 0.5f,
                (getHeight() - tempBitmap.getHeight()) * 0.5f,
                new Paint());
        canvas.drawBitmap();*/
        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        Bitmap tempBitmap = getBitmap();
        if(tempBitmap == null || getWidth() <= 0 || getHeight() <= 0){
            return;
        }

        /*int scaleWidth = (int) (tempBitmap.getWidth() * 1.0f / scale);
        int bitmapLeft = (int) ((tempBitmap.getWidth()- scaleWidth) * 0.5f);
        int scaleHeight = (int) (tempBitmap.getHeight() * scale);
        int height = tempBitmap.getHeight();
        int topHeight = 0;
        if(getHeight() < scaleHeight){
            topHeight = (int) ((scaleHeight - getHeight()) * 0.5f);
            scaleHeight = getHeight();
        }*/

        /*int bitmapWidth = (int) (tempBitmap.getWidth() * 1.0f / scale);
        int bitmapLeft = (int) ((tempBitmap.getWidth() - bitmapWidth) * 0.5f);
        int bitmapHeight = (int) (tempBitmap.getWidth() * 1.0f / scale);
        int bitmapTop = 0;
        if(bitmapHeight * scale * getWidth() * 1.0f/ (bitmapWidth) > getHeight()){
            int tempBitmapHeight = (int) (getHeight() * bitmapWidth / (scale * getWidth() * 1.0f));
            bitmapTop = (int) ((bitmapHeight - tempBitmapHeight) * 0.5f);
            bitmapHeight = tempBitmapHeight;
        }
        Rect bitmapRect = new Rect(bitmapLeft, bitmapTop, bitmapWidth, (bitmapTop + bitmapHeight));
        Log.e("GalleryPhoto", "scale1: " + bitmapWidth + "::" +  bitmapHeight);

        int canvasLeft = 0;
        int canvasTop = 0;
        int canvasWidth = getWidth();
        int canvasHeight = (int) (bitmapHeight * scale * (getWidth() * 1.0f/ (bitmapWidth)));
        if(canvasHeight < getHeight()){
            canvasTop = (int) ((getHeight() - canvasHeight ) * 0.5f);
        }

        Rect canvasRect = new Rect(canvasLeft, canvasTop, (canvasLeft + canvasWidth), (canvasTop + canvasHeight));
        Log.e("GalleryPhoto", "scale2: " + bitmapWidth + "::" +  bitmapHeight);
        canvas.drawBitmap(tempBitmap, bitmapRect, canvasRect, new Paint());*/

        int bitmapWidth = (int) (tempBitmap.getWidth() * 1.0f / scale);
        int bitmapLeft = (int) ((tempBitmap.getWidth() - bitmapWidth) * 0.5f);
        int bitmapHeight = tempBitmap.getHeight();
        int bitmapTop = 0;

        Rect bitmapRect = new Rect(bitmapLeft, bitmapTop, bitmapWidth, bitmapHeight);


    }

    public Bitmap getBitmap(){
        //Log.e("GalleryPhoto", "getbitmap start:");
        long startMilli = System.currentTimeMillis();
        Drawable drawable = getDrawable();
        if(drawable == null){
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        int width = getWidth();
        int height = getHeight();
        int widthBitmap = bitmap.getWidth();
        int heightBitmap = bitmap.getHeight();
        if(height == 0 || width == 0){
            return null;
        }
        float scaleX =  width * 1.0f / widthBitmap;
        float scaleY =  height * 1.0f / heightBitmap;
        if(scaleX > scaleY){
            baseScale = scaleY;
        }else{
            baseScale = scaleX;
        }
        /*Bitmap clipBitmap = Bitmap.createBitmap(bitmap, (int) (widthBitmap * 1.0f / (1 - scale)), 0, (int) (widthBitmap * 1.0f / scale), heightBitmap);
        Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, (int) (widthBitmap * baseScale), (int) (heightBitmap * baseScale), true);*/
        Log.e("GalleryPhoto", "getbitmap end: " + bitmap.getWidth() + "::" + bitmap.getHeight() );
        return bitmap;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_UP){
            scale = scale + 0.05f;
            invalidate();
            Log.e("GalleryPhoto", "onTouchEvent up scale: " + scale);
        }

        return super.onTouchEvent(event);
    }
}
