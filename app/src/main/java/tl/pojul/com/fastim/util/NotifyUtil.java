package tl.pojul.com.fastim.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.pojul.fastIM.entity.Conversation;
import com.pojul.fastIM.message.chat.ReplyMessage;
import tl.pojul.com.fastim.Media.AudioManager;
import tl.pojul.com.fastim.R;
import tl.pojul.com.fastim.View.activity.MainActivity;

public class NotifyUtil {

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

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
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

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
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

}
