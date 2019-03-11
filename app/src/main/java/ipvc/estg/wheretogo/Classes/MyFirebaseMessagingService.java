package ipvc.estg.wheretogo.Classes;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import ipvc.estg.wheretogo.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private NotificationManager mNotificationManager;

    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d("TAG", "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d("a", "Message data payload: " + remoteMessage.getData());

            /*if (true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }*/

        }


        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d("TAG", "Message MyFirebaseMessagingService Body: " + remoteMessage.getNotification().getBody());
            Random rand = new Random();

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(getApplicationContext(), "notify_001");
            Intent ii = new Intent(getApplicationContext(), MyFirebaseMessagingService.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);


            mBuilder.setContentIntent(pendingIntent);
            mBuilder.setSmallIcon(R.drawable.ic_place_black_24dp);
            mBuilder.setBadgeIconType(R.drawable.ic_place_black_24dp);
            mBuilder.setColor(getResources().getColor(R.color.colorNotification));
            mBuilder.setSound(Settings.System.DEFAULT_RINGTONE_URI);
            mBuilder.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            mBuilder.setContentText(remoteMessage.getNotification().getBody());
            mBuilder.setAutoCancel(true);
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_place_black_24dp));
            mBuilder.setPriority(Notification.PRIORITY_MAX);

            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            inboxStyle.setBigContentTitle("Event tracker details:");
            inboxStyle.addLine("Nova linha");
            inboxStyle.addLine("Nova linha");
            inboxStyle.addLine("Nova linha");
            inboxStyle.addLine("Nova linha");
            inboxStyle.addLine("Nova linha");
            mBuilder.setStyle(inboxStyle);


            mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "YOUR_CHANNEL_ID";
                NotificationChannel channel = new NotificationChannel(channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.enableLights(true);
                channel.setLightColor(Color.WHITE);
                channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                mBuilder.setChannelId(channelId);
                mNotificationManager.createNotificationChannel(channel);
            }

            mNotificationManager.notify(rand.nextInt(100), mBuilder.build());
        }

    }

    @Override
    public void onNewToken(String token) {
        Log.d("TAG", "Refreshed token: " + token);
    }

}
