package tl.pojul.com.fastim.View.widget.TransitImage.Transit;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.Log;

import tl.pojul.com.fastim.View.widget.TransitImage.TransitImageView;

/**
 *从右往左淡入
 */
public class RightAlphaInTransit extends Transit{

    private static final String TAG = "RightAlphaInTransit";

    private Canvas c1;

    private Paint paint;

    private float startLeft = -1;

    /**
     * 偏移量,0 - 1; 1为控件宽度
     */
    private float offsetX = 0.4f;

    public RightAlphaInTransit() {
        transitType = Transit.RIGHT_ALPHA_IN;
        paint = new Paint();
    }

    public void drawRightAlpaIn(TransitImageView transitImageView, Bitmap mBitmap, Canvas canvas, float progress, int width, int height) {
        if(mBitmap == null){
            return;
        }
        //long mill = System.currentTimeMillis();
        if(startLeft == -1){
            startLeft = (width - width * offsetX);
        }
        canvas.clipRect(startLeft * (1 - progress), 0, width, height);
        canvas.drawBitmap(mBitmap, 0, 0, paint);
        /*@SuppressLint("WrongConstant") int sc = canvas.saveLayer(0, 0, width, height, null,
                Canvas.MATRIX_SAVE_FLAG |
                        Canvas.CLIP_SAVE_FLAG |
                        Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                        Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                        Canvas.CLIP_TO_LAYER_SAVE_FLAG);*/

        /*if(c1 == null){
            Canvas c1 = new Canvas(mBitmap);
            Paint p1 = new Paint();
            c1.drawBitmap(mBitmap, 0, 0 , p1);
            this.c1 = c1;
            startLeft = (width - width * offsetX);
        }

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        Canvas c2 = new Canvas(bitmap2);
        Paint mPaint = new Paint();
        mPaint.setColor(0xff00ff00);
        int left = (int)(startLeft * (1 - progress));
        c2.drawRect(new Rect(left, 0, width, height), mPaint);

        canvas.saveLayerAlpha(0, 0, width, height, 255,
                Canvas.ALL_SAVE_FLAG);

        mPaint.reset();
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(bitmap2, 0, 0, mPaint);
        //canvas.restoreToCount(sc);*/
        transitImageView.setTranslationX((width * offsetX - width * offsetX * progress));
    }

    public float getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }
}
