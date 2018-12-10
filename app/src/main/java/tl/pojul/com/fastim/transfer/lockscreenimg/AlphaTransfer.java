package tl.pojul.com.fastim.transfer.lockscreenimg;

import android.graphics.Canvas;
import android.util.Log;

public class AlphaTransfer extends Transfer{

    private static final String TAG = "AlphaTransfer";

    @Override
    public void drawBitmap(Canvas canvas, float progress){
        if(showScale == null || disappearScale == null || canvas == null || paint == null){
            return;
        }
        int alphaShowing = 230 +  (int) (progress * (255 - 230));
        paint.setAlpha(alphaShowing);
        canvas.drawBitmap(showScale, 0,0, paint);

        int alphaDisappear = (int) ((1 - progress) * 255);
        paint.setAlpha(alphaDisappear);
        canvas.drawBitmap(disappearScale, 0,0, paint);
    }

}
