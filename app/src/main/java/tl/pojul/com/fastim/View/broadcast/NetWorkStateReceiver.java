package tl.pojul.com.fastim.View.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import tl.pojul.com.fastim.MyApplication;
import tl.pojul.com.fastim.util.NetWorkUtil;

public class NetWorkStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(NetWorkUtil.isNetWorkable(context) && !MyApplication.getApplication().isConnected()){
            MyApplication.getApplication().reConn(false);
        }
    }
}
