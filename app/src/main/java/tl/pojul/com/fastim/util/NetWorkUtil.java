package tl.pojul.com.fastim.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;

public class NetWorkUtil {

    public static boolean isNetWorkable(Context context) {
        boolean netConn = false;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (!wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                netConn = false;
            } else {
                netConn = true;
            }
            Log.e("NetWorkStateReceiver", "netConn: " + netConn);
        } else {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            Network[] networks = connMgr.getAllNetworks();
            for (int i = 0; i < networks.length; i++) {
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);
                if (networkInfo != null && networkInfo.isConnected()) {
                    netConn = true;
                    break;
                }
            }
        }
        return netConn;
    }
}
