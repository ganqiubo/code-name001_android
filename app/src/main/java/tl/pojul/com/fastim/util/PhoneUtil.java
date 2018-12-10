package tl.pojul.com.fastim.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneUtil {

    /**
     * 获取手机IMEI号
     */
    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String imei = telephonyManager.getDeviceId();
            return imei;
        }
        return null;
    }

    /**
     * 获取手机IMSI号
     */
    public String getIMSI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String imsi = mTelephonyMgr.getSubscriberId();
            return imsi;
        }
        return null;
    }

    /**
     * 判断是否包含SIM卡
     *
     * @return 状态
     */
    public boolean hasSimCard(Context context) {
        TelephonyManager telMgr = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        boolean result = true;
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                result = false; // 没有SIM卡
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN:
                result = false;
                break;
        }
        return result;
    }

}
