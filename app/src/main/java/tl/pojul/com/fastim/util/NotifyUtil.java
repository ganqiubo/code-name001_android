package tl.pojul.com.fastim.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.message.chat.ReplyMessage;

import java.lang.ref.WeakReference;

import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.MainActivity;

public class NotifyUtil {

    private static WeakReference<RemoteViews> updateApkRv;
    private static String channelId = "footstep_channe2";
    private static String channelId3 = "footstep_channe3";

    public static void notifyChatMess(Conversation conversation, Context context) {

        int rawVolume = AudioManager.getInstance().getNotifiVolume();
        AudioManager.getInstance().setNotifySoundLevel((int) (AudioManager.getInstance().getMaxNotifiVolume() * 0.4f));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notify_friend_mess);
        String notifyTitle = getNotifyTitle(conversation);
        rv.setTextViewText(R.id.title, notifyTitle);
        rv.setTextViewText(R.id.from, conversation.getConversationName());
        rv.setTextViewText(R.id.content, conversation.getConversationLastChat());
        rv.setTextViewText(R.id.time, DateUtil.getFormatDate().split(" ")[1]);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId3,"Location",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(context, channelId3);
        }else{
            mBuilder = new NotificationCompat.Builder(context);
        }
        mBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)//设置铃声及震动效果等
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setPriority(Notification.PRIORITY_DEFAULT)  //通知的优先级
                .setCategory(Notification.CATEGORY_MESSAGE)  //通知的类型
                .setContentIntent(pi)
                .setCustomContentView(rv)
                //.setCustomBigContentView(rv);
                .setFullScreenIntent(pi, true);
        Notification notification = mBuilder.build();
        notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
        notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
        notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
        notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示

        NotificationTarget notificationTarget = new NotificationTarget(context, R.id.photo, rv, notification, 1);
        notificationManager.notify(1, notification);
        Glide.with(context).asBitmap().load(conversation.getConversationPhoto()).into(notificationTarget);

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            AudioManager.getInstance().setNotifySoundLevel(rawVolume);
        }, 900);
    }

    public static String getNotifyTitle(Conversation conversation) {
        String title = "";
        switch (conversation.getConversationType()) {
            case 1:
                title = "好友消息";
                break;
            case 2:
                title = "";
                break;
            case 3:
                title = "回复消息";
                break;
            default:
                title = "";
                break;
        }
        return title;
    }

    public static void notifyChatMess(String notifyTitle, String name, String content, String photo, Context context) {
        int rawVolume = AudioManager.getInstance().getNotifiVolume();
        AudioManager.getInstance().setNotifySoundLevel((int) (AudioManager.getInstance().getMaxNotifiVolume() * 0.4f));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notify_friend_mess);
        rv.setTextViewText(R.id.title, notifyTitle);
        rv.setTextViewText(R.id.from, name);
        rv.setTextViewText(R.id.content, content);
        rv.setTextViewText(R.id.time, DateUtil.getFormatDate().split(" ")[1]);

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId3,"Location",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(context, channelId3);
        }else{
            mBuilder = new NotificationCompat.Builder(context);
        }
        mBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)//设置铃声及震动效果等
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setPriority(Notification.PRIORITY_DEFAULT)  //通知的优先级
                .setCategory(Notification.CATEGORY_MESSAGE)  //通知的类型
                .setContentIntent(pi)
                .setCustomContentView(rv)
                //.setCustomBigContentView(rv);
                .setFullScreenIntent(pi, true);
        Notification notification = mBuilder.build();
        notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
        notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
        notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
        notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示

        NotificationTarget notificationTarget = new NotificationTarget(context, R.id.photo, rv, notification, 1);
        notificationManager.notify(1, notification);
        Glide.with(context).asBitmap().load(photo).into(notificationTarget);

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            AudioManager.getInstance().setNotifySoundLevel(rawVolume);
        }, 900);
    }

    public static void notify(String notifyTitle, String name, String content, String photo, Intent intent, Context context){
        int rawVolume = AudioManager.getInstance().getNotifiVolume();
        AudioManager.getInstance().setNotifySoundLevel((int) (AudioManager.getInstance().getMaxNotifiVolume() * 0.4f));

        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notify_friend_mess);
        rv.setTextViewText(R.id.title, notifyTitle);
        rv.setTextViewText(R.id.from, name);
        rv.setTextViewText(R.id.content, content);
        rv.setTextViewText(R.id.time, DateUtil.getFormatDate().split(" ")[1]);

        PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder;
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId3,"Location",NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(context, channelId3);
        }else{
            mBuilder = new NotificationCompat.Builder(context);
        }
        mBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)//设置铃声及震动效果等
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setPriority(Notification.PRIORITY_DEFAULT)  //通知的优先级
                .setCategory(Notification.CATEGORY_MESSAGE)  //通知的类型
                .setContentIntent(pi)
                .setCustomContentView(rv)
                //.setCustomBigContentView(rv);
                .setFullScreenIntent(pi, true);
        Notification notification = mBuilder.build();
        notification.ledARGB = Color.GREEN;// 控制 LED 灯的颜色，一般有红绿蓝三种颜色可选
        notification.ledOnMS = 1000;// 指定 LED 灯亮起的时长，以毫秒为单位
        notification.ledOffMS = 1000;// 指定 LED 灯暗去的时长，也是以毫秒为单位
        notification.flags = Notification.FLAG_SHOW_LIGHTS;// 指定通知的一些行为，其中就包括显示

        NotificationTarget notificationTarget = new NotificationTarget(context, R.id.photo, rv, notification, 1);
        notificationManager.notify(1, notification);
        Glide.with(context).asBitmap().load(photo).into(notificationTarget);

        new Handler(Looper.getMainLooper()).postDelayed(()->{
            AudioManager.getInstance().setNotifySoundLevel(rawVolume);
        }, 900);
    }

    public static void notifyUpdateApk(int progress, Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        /*PendingIntent pi = PendingIntent
                .getActivity(context, 0, new Intent(context, MainActivity.class
        ), 0);*/
        //if(updateApkRv == null || updateApkRv.get() == null){
            RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.notify_update_apk);
            //updateApkRv = new WeakReference<>(rv);
            rv.setProgressBar(R.id.progress, 100, progress, false);
            /*Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_NO_CREATE);*/
            NotificationCompat.Builder mBuilder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationManager nm = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
                NotificationChannel channel = new NotificationChannel(channelId,"Location",NotificationManager.IMPORTANCE_DEFAULT);
                channel.setSound(null, null);
                nm.createNotificationChannel(channel);
                mBuilder = new NotificationCompat.Builder(context, channelId);
            }else{
                mBuilder = new NotificationCompat.Builder(context);
            }
            mBuilder
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setPriority(Notification.PRIORITY_DEFAULT)  //通知的优先级
                    .setCategory(Notification.CATEGORY_MESSAGE)  //通知的类型
                    //.setContentIntent(pi)
                    .setSound(null)
                    .setCustomContentView(rv);
                    //.setCustomBigContentView(rv)
                    //.setFullScreenIntent(pi, true);
            Notification notification = mBuilder.build();
            notificationManager.notify(2, notification);
        /*}else{
            updateApkRv.get().setProgressBar(R.id.progress, 100, progress, false);

        }*/
    }

    public static void unNotifyUpdateApk(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(2);
        updateApkRv = null;
    }

}
