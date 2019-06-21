package in.rto.collections.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;


import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import in.rto.collections.R;
import in.rto.collections.activities.Notification_Activity;
import in.rto.collections.activities.SplashScreen_Activity;


public class FirebaseMessageService extends FirebaseMessagingService {

    public static final int NOTIFICATION_REQUEST_CODE = 100;
    private static PendingIntent pendingIntent;
    private static Notification.Builder builder;
    private static NotificationManagerCompat notificationManager;
    private static Uri notificationSound;
    private static Bitmap iconBitmap;
    private static Random random;

/*
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
        }
        if (remoteMessage.getData() != null) {
            sendNotification(remoteMessage.getData().get("title").toString(), remoteMessage.getData().get("message").toString());
        }



        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("message");
        Log.i("MESSSSSSSSSAGE",""+title+" "+body);
        int notificationId = new Random().nextInt(60000);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.app_notification_icon)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId , notificationBuilder.build());
    }

    private void sendNotification(String messageBody, String messageTitle) {
        Intent intent = new Intent(this, SplashScreen_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
               PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            notificationBuilder.setSmallIcon(R.drawable.app_notification_icon);
        else
            notificationBuilder.setSmallIcon(R.drawable.app_notification_icon);

        notificationBuilder.setContentTitle("Todojee Insurance");
        notificationBuilder.setContentText(messageBody);
        notificationBuilder.setContentTitle(messageTitle);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSound(defaultSoundUri);
        notificationBuilder.setVibrate(new long[]{100, 250});
        notificationBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());

   }
*/

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("remoteMessage", remoteMessage.getData().get("title") + " " +
                remoteMessage.getData().get("message") + " " +
                remoteMessage.getData().get("image") + " " +
                remoteMessage.getData().get("icon") + " " +
                remoteMessage.getData().get("type") + " " +
                remoteMessage.getData().get("userId") + " " +
                remoteMessage.getData().get("message_id"));
        createNotificationChannel();
        Intent notificationIntent = new Intent(getApplicationContext(), SplashScreen_Activity.class);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle data = new Bundle();
        data.putString("action", "notificationfromfcm");
        data.putString("message_id",remoteMessage.getData().get("message_id") );
        data.putString("type", "notification");
        notificationIntent.putExtras(data);
        if (remoteMessage.getData().get("image") != null && remoteMessage.getData().get("image").isEmpty()) {
            showNewNotification(
                    getApplicationContext(),
                    notificationIntent,
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"),
                    remoteMessage.getData().get("image"),
                    remoteMessage.getData().get("icon"),
                    remoteMessage.getData().get("type"),
                    remoteMessage.getData().get("userId"),
                    remoteMessage.getData().get("message_id"));
        } else {
            generatepicture(
                    getApplicationContext(),
                    notificationIntent,
                    remoteMessage.getData().get("title"),
                    remoteMessage.getData().get("message"),
                    remoteMessage.getData().get("image"),
                    remoteMessage.getData().get("message_id"));
        }
    }


    public static void showNewNotification(Context context, Intent intent,
                                           String title, String msg, String image, String icon, String type, String userId, String message_id) {

        notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder = new Notification.Builder(context);
        notificationManager = NotificationManagerCompat.from(context);


        int m1 = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        Intent notificationIntent;
        // Log.d("ingenaretesimple", "simple");
        if (intent != null) {
            notificationIntent = intent;
       /*     Bundle data = new Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("message_id",message_id );
            data.putString("type", type);
            notificationIntent.putExtras(data);
       */ } else {

            notificationIntent = new Intent(context, SplashScreen_Activity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle data = new Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("message_id", message_id);
            data.putString("type", "notification");
            notificationIntent.putExtras(data);
        }
        int requestID = (int) System.currentTimeMillis();

        pendingIntent = PendingIntent.getActivity((context), requestID, notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT );
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = builder
                    .setContentTitle(title)
                    .setChannelId("4565")
                    .setContentText(msg)
                    //                        .setTicker(Application.getContext().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.app_notification_icon)
                    //  .setLargeIcon()
                    .setSound(notificationSound)
                    .setLights(Color.YELLOW, 1000, 1000)
                    .setVibrate(new long[]{500, 500})
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }else{
            notification = builder
                    .setContentTitle(title)
                    .setContentText(msg)
                    //                        .setTicker(Application.getContext().getString(R.string.app_name))
                    .setSmallIcon(R.drawable.app_notification_icon)
                    //    .setLargeIcon(iconBitmap)
                    .setSound(notificationSound)
                    .setLights(Color.YELLOW, 1000, 1000)
                    .setVibrate(new long[]{500, 500})
                    .setWhen(System.currentTimeMillis())
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }
        notificationManager.notify(m1, notification);
//        notificationIntent = new Intent(context, SplashScreenActivity.class);
//        Bundle data = new Bundle();
//        data.putString("action", "");
//        notificationIntent.putExtras(data);
//        pendingIntent = PendingIntent.getActivity((context), 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        new generatePictureStyleNotification(context,title, msg,
//                image).execute();
/*
        new generatePictureStyleNotification(App.getContext(),title, msg,
                ConstantValues.ECOMMERCE_URL + image).execute();
*/

    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel1";
            String description = "channel1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("4565", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static void generatepicture(Context context, Intent notificationIntent, String title, String message, String imageUrl,String message_id) {
        //   Log.d("ingeneratepicture", "picture");
        Intent intent;
        if (notificationIntent != null) {
            intent = notificationIntent;
        } else {
            intent = new Intent(context, SplashScreen_Activity.class);
            notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP  | Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle data = new Bundle();
            data.putString("action", "notificationfromfcm");
            data.putString("message_id", message_id);
            data.putString("type", "notification");
            intent.putExtras(data);
        }
        new generatePictureStyleNotification(context, intent, title, message,
                imageUrl).execute();

    }

    public static class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, Intent notificationIntent, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            //     Log.d("title", title + "hii");
            this.message = message;
            this.imageUrl = imageUrl;
            int requestID = (int) System.currentTimeMillis();

            pendingIntent = PendingIntent.getActivity((context), requestID, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            builder = new Notification.Builder(mContext);
            notificationManager = NotificationManagerCompat.from(mContext);
            Notification notification;
            Log.d("title", title);
            if (result == null || this.imageUrl == null || this.imageUrl.isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = builder
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId("4565")
                            //                        .setTicker(Application.getContext().getString(R.string.app_name))
                            .setSmallIcon(R.drawable.app_notification_icon)
                            //    .setLargeIcon(iconBitmap)
                            .setSound(notificationSound)
                            .setLights(Color.YELLOW, 1000, 1000)
                            .setVibrate(new long[]{500, 500})
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build();
                }else{
                    notification = builder
                            .setContentTitle(title)
                            .setContentText(message)
                            //                        .setTicker(Application.getContext().getString(R.string.app_name))
                            .setSmallIcon(R.drawable.app_notification_icon)
                            //    .setLargeIcon(iconBitmap)
                            .setSound(notificationSound)
                            .setLights(Color.YELLOW, 1000, 1000)
                            .setVibrate(new long[]{500, 500})
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build();
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    notification = builder
                            .setContentTitle(title)
                            .setContentText(message)
                            .setChannelId("4565")
                            //                        .setTicker(getContext().getString(R.string.app_name))
                            .setSmallIcon(R.drawable.app_notification_icon)
                            //.setLargeIcon(result)
                            .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                            .setSound(notificationSound)
                            .setLights(Color.YELLOW, 1000, 1000)
                            .setVibrate(new long[]{500, 500})
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build();
                }else{
                    notification = builder
                            .setContentTitle(title)
                            .setContentText(message)
                            //                        .setTicker(getContext().getString(R.string.app_name))
                            .setSmallIcon(R.drawable.app_notification_icon)
                            //.setLargeIcon(result)
                            .setStyle(new Notification.BigPictureStyle().bigPicture(result))
                            .setSound(notificationSound)
                            .setLights(Color.YELLOW, 1000, 1000)
                            .setVibrate(new long[]{500, 500})
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .build();
                }
            }
            random = new Random();
            int m = random.nextInt(9999 - 1000) + 1000;

            notificationManager.notify(m, notification);

        }
    }



}