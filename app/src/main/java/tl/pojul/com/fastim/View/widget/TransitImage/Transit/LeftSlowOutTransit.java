package tl.pojul.com.fastim.View.widget.TransitImage.Transit;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import tl.pojul.com.fastim.View.widget.TransitImage.TransitImageView;

/**
 *从左往右移出
 */
public class LeftSlowOutTransit extends Transit{

    /**
     * 偏移量,0 - 1; 1为控件宽度
     */
    private float toOffsetX = 0.3f;
    private Paint paint;

    public LeftSlowOutTransit() {
        transitType = Transit.LEFT_SLOW_OUT;
        paint = new Paint();
    }

    public void drawLeftOut(TransitImageView transitImageView, Bitmap mBitmap, Canvas canvas, float progress, int width){
        canvas.drawBitmap(mBitmap, 0, 0 , paint);
        transitImageView.setTranslationX((-width * toOffsetX * progress));
    }

    public float getToOffsetX() {
        return toOffsetX;
    }

    public void setToOffsetX(float toOffsetX) {
        this.toOffsetX = toOffsetX;
    }
}
