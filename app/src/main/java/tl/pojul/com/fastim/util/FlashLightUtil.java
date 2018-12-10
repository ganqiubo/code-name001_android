package tl.pojul.com.fastim.util;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;

public class FlashLightUtil {

    private static final String TAG = "FlashLightUtil";
    private Context context;
    private Camera m_Camera;

    public FlashLightUtil(Context context) {
        this.context = context;
    }


    /**
     * 手电筒控制方法
     *
     * @param lightStatus
     * @return
     */
    public void lightSwitch(boolean lightStatus) {
        if (lightStatus) {
            try {
                m_Camera = Camera.open();
                Camera.Parameters mParameters;
                mParameters = m_Camera.getParameters();
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                m_Camera.setParameters(mParameters);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        } else {
            try {
                if(m_Camera == null){
                    return;
                }
                Camera.Parameters mParameters;
                mParameters = m_Camera.getParameters();
                mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                m_Camera.setParameters(mParameters);
                m_Camera.release();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void onDestory(){
        if(m_Camera != null){
            m_Camera.release();
        }
    }

}
