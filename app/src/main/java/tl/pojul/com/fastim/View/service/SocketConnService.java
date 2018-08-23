package tl.pojul.com.fastim.View.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import tl.pojul.com.fastim.util.DateUtil;

public class SocketConnService extends Service {

    private static final String TAG = "SocketConnService";

    public SocketConnService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        serviceThread.start();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    private Thread serviceThread = new Thread(() -> {
        while(true){
            Log.e(TAG, "serviceThread: " + DateUtil.getFormatDate());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });

}
