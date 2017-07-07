package edu.umbc.ebiquity.mithril.util.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import edu.umbc.ebiquity.mithril.R;
import edu.umbc.ebiquity.mithril.ui.activities.CoreActivity;
import edu.umbc.ebiquity.mithril.ui.activities.FeedbackActivity;

/**
 * Created by prajit on 7/6/17.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * Posts a notification in the notification bar when a transition is detected.
         * If the user clicks the notification, control goes to the MainActivity.
         */
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, FeedbackActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(CoreActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.map_marker)
                // In a real app, you may want to use a library like Volley to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.map_marker))
                .setColor(context.getResources().getColor(R.color.colorPrimary, context.getTheme()))
                .setContentTitle(context.getString(R.string.title_activity_feedback))
                .setContentText(context.getString(R.string.mind_uploading_some_feedback))
                .setContentIntent(notificationPendingIntent)
                .addAction(android.R.string.ok, "Okay", notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }
}