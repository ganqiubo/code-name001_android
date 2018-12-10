package tl.pojul.com.fastim.transfer.lockscreenimg;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Transfer {

    protected Paint paint;
    protected long lastExecuteMill;
    protected Bitmap showScale;
    protected Bitmap disappearScale;

    public Transfer() {
        this.paint = new Paint();
    }

    public void setBitmaps(Bitmap showing, Rect rectShowing, Bitmap disappear, Rect rectDisappear, int width, int height){
        if(showing == null || disappear == null){
            return;
        }
        showScale = Bitmap.createBitmap(showing, rectShowing.left, rectShowing.top, width, height);
        disappearScale = Bitmap.createBitmap(disappear, rectDisappear.left, rectDisappear.top, width,  height);
    }

    public void drawBitmap(Canvas canvas, float progress){
    }

    public void clearData(){
        if(showScale != null){
            showScale.recycle();
            showScale = null;
        }
        if(disappearScale != null){
            disappearScale.recycle();
            disappearScale = null;
        }
    }

}
