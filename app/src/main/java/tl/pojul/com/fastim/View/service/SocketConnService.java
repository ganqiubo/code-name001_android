package tl.pojul.com.fastim.View.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.MainActivity;
import tl.pojul.com.fastim.util.DateUtil;

public class SocketConnService extends Service {

    private static final String TAG = "SocketConnService";
    private String channelId = "footstep_channel";

    public SocketConnService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
            Notification notification;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nm.createNotificationChannel(new NotificationChannel(channelId,"Location",NotificationManager.IMPORTANCE_DEFAULT));
                notification = new NotificationCompat.Builder(this, channelId)
                        .setContentTitle("脚步圈")
                        .setContentText("脚步圈正在运行")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.icon)
                        .setContentIntent(pi)
                        .build();
            }else{
                notification = new NotificationCompat.Builder(this)
                        .setContentTitle("脚步圈")
                        .setContentText("脚步圈正在运行")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.icon)
                        .setContentIntent(pi)
                        .build();
            }
            startForeground(1, notification);
        }
        //serviceThread.start();
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

    /*private Thread serviceThread = new Thread(() -> {
        while(true){
            Log.e(TAG, "serviceThread: " + DateUtil.getFormatDate());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    });*/

}
