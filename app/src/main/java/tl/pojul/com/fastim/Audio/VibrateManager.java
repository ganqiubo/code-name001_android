package tl.pojul.com.fastim.Audio;

import android.app.Service;
import android.content.Context;
import android.os.Vibrator;

public class VibrateManager {

    private static VibrateManager mVibrateManager;
    private final Context mContext;

    private VibrateManager(Context mContext) {
        this.mContext = mContext;
    }

    public static void Instance(Context context) {
        if (mVibrateManager == null) {
            synchronized (VibrateManager.class) {
                if (mVibrateManager == null) {
                    mVibrateManager = new VibrateManager(context);
                }
            }
        }
    }

    public static VibrateManager getInstance() {
        return mVibrateManager;
    }

    public void vibrate(long time){
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
        vibrator.vibrate(time);
    }

}
