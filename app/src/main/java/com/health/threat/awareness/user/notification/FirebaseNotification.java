package com.health.threat.awareness.user.notification;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.health.threat.awareness.user.R;
import com.health.threat.awareness.user.SplashActivity;
import com.health.threat.awareness.user.app.App;

/**
 * Helper class for showing and canceling location
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */

public class FirebaseNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private static final String NOTIFICATION_TAG = "AmitawsNotification";
    private static Notification notification;
    private static Notification.Builder builder;
    @SuppressLint("StaticFieldLeak")
    private static NotificationCompat.Builder builderCompact;
    //private static Intent iStopService;
    private static PendingIntent resultPendingIntent;
    private static boolean isFirst = true;

    public static void notify(final Context context,
                              final String title, final String text, String notificationType, FirebaseMessagingService activity) {
        resultPendingIntent = PendingIntent.getActivity(context, 1, new Intent(context, SplashActivity.class), FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notifyO(context, title, text, notificationType, activity);
        } else {
            notifyPre(context, title, text, notificationType, activity);
        }
    }

    private static String createOrderChannel(Context ctx) {
        // Create a channel.
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        String channelName = ctx.getString(R.string.channel_id);
        int importance = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_DEFAULT;
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(
                    ctx.getString(R.string.channel_id), channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(true);
            notificationChannel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), audioAttributes);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        return channelName;
    }

    public static void notifyO(Context context, final String title, final String text, String notificationType, FirebaseMessagingService activity) {
        if (isFirst) {
            String channelId = createOrderChannel(context);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                builder = new Notification.Builder(activity, App.channel);

                builder = new Notification.Builder(context, channelId)
                        .setSmallIcon(R.drawable.outline_notifications_active_24)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setOnlyAlertOnce(true)
                        .setOngoing(false)
                        .setAutoCancel(true)
                        .setWhen(System.currentTimeMillis())
                        .setContentIntent(resultPendingIntent);
            }

            notification = builder.build();
            //notification.flags = Notification.FLAG_NO_CLEAR ; // & Notification.FLAG_ONLY_ALERT_ONCE & Notification.FLAG_ONGOING_EVENT;
            notify(context, notification);
            isFirst = false;
        } else {
            builder.setContentText(text);
            notify(context, notification = builder.build());
        }
    }

    public static void notifyPre(final Context context,
                                 final String title, final String text, String notificationType, FirebaseMessagingService activity) {

        if (isFirst) {
            builderCompact = new NotificationCompat.Builder(context, context.getString(R.string.channel_id));

            builderCompact.setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setOnlyAlertOnce(true)
                    .setOngoing(false)
                    .setAutoCancel(true)
                    .setWhen(System.currentTimeMillis())
                    .setContentText(text)
                    .setContentIntent(resultPendingIntent);

            notification = builderCompact.build();
            //notification.flags = Notification.FLAG_NO_CLEAR ; // & Notification.FLAG_ONLY_ALERT_ONCE & Notification.FLAG_ONGOING_EVENT;
            notify(context, notification);
            isFirst = false;
        } else {
            builderCompact.setContentText(text);
            notify(context, notification = builderCompact.build());
        }
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_TAG, 0, notification);
    }

    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_TAG, 0);
    }
}
