package com.hamstechapp.push;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hamstechapp.R;
import com.hamstechapp.activities.AboutUsActivity;
import com.hamstechapp.activities.AffiliationsActivity;
import com.hamstechapp.activities.ContactUsActivity;
import com.hamstechapp.activities.DynamicLinkingActivity;
import com.hamstechapp.activities.HomeActivity;
import com.hamstechapp.activities.LifeatHamstechActivity;
import com.hamstechapp.activities.MentorsActivity;
import com.hamstechapp.activities.NotificationActivity;
import com.hamstechapp.activities.RegisterCourseActivity;
import com.hamstechapp.activities.SplashActivity;
import com.hamstechapp.common.LogEventsActivity;
import com.hamstechapp.database.User;
import com.hamstechapp.database.UserDatabase;
import com.hamstechapp.utils.UserDataConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Intent intent;
    Bitmap image;
    String imageLarge,activityLog,PagenameLog,notiTitle,userMobile,userName;
    NotificationCompat.Builder notificationBuilder;
    LogEventsActivity logEventsActivity;
    UserDatabase userDatabase;
    ArrayList<User> userDetails = new ArrayList<>();

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        logEventsActivity = new LogEventsActivity();
        activityLog = "Notification";
        Log.e("Notification","67   "+remoteMessage.getData().toString());
        /*userDatabase = Room.databaseBuilder(this,
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();*/


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
        }

        sendNotification(remoteMessage);
    }

    private class getUserDetails extends AsyncTask<User,Void,Void> {

        @Override
        protected Void doInBackground(User... users) {
            userDetails.clear();
            userDetails.addAll(userDatabase.userDao().getAll());
            if (userDetails.size()!=0){
                UserDataConstants.userMobile = userDetails.get(0).getPhone();
                UserDataConstants.userName = userDetails.get(0).getName();
            } else {
                UserDataConstants.userMobile = "";
                UserDataConstants.userName = "";
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    RoomDatabase.Callback callback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase database) {
            super.onCreate(database);
            Log.i("HomeActivity","onCreate oinvoked");
            /*db.userDao().insertAll(new User(0,"name1","last1"));
            db.userDao().insertAll(new User(0,"name2","last2"));
            db.userDao().insertAll(new User(0,"name3","last3"));*/
            //new addNew().execute();

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase database) {
            super.onOpen(database);
            Log.i("HomeActivity","onOpen oinvoked");
        }
    };

    @Override
    public void onNewToken(String token) {

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    // [END on_new_token]

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance().beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    private void sendNotification(RemoteMessage messageBody) {
        userDatabase = Room.databaseBuilder(this,
                UserDatabase.class, "database-name").allowMainThreadQueries().addCallback(callback).build();
        new getUserDetails().execute();
        Log.e("messageBody","132    "+messageBody.getData().get("notificationID"));
        if (messageBody.getData().get("status").equals("notification")){
            intent = new Intent(this, NotificationActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Notification Activity";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notiTitle",notiTitle);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("home_page")){
            intent = new Intent(this, HomeActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "HomePage Activity";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("contactus_page")){
            intent = new Intent(this, ContactUsActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Contact Us Activity";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("aboutus_page")){
            intent = new Intent(this, AboutUsActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "About Us Activity";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("courses_page")){
            intent = new Intent(this, DynamicLinkingActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Course Page";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("affliations_page")){
            intent = new Intent(this, AffiliationsActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Affiliations Page";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("life_at_hamstech")){
            intent = new Intent(this, LifeatHamstechActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Life at hamstech";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("mentors")){
            intent = new Intent(this, MentorsActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Mentors";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("register_course_page")){
            intent = new Intent(this, RegisterCourseActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Register course page";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("chat_page")){
            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
            startActivity(myIntent);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "chat with whatsapp";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.putExtra("notificationId",messageBody.getData().get("notificationID"));
            intent.putExtra("notiTitle",notiTitle);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("chat_with_whats_app")){
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Chat with Whatsapp";
            getLogEvent(MyFirebaseMessagingService.this);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getResources().getString(R.string.chatURL)));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("register_course")){
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Register for course";
            getLogEvent(MyFirebaseMessagingService.this);
            intent = new Intent(this, RegisterCourseActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else if (messageBody.getData().get("status").equals("ratings")){
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Ratings Page";
            getLogEvent(MyFirebaseMessagingService.this);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hamstechonline&hl=en"));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(this, SplashActivity.class);
            notiTitle = messageBody.getData().get("title");
            PagenameLog = "Push Notification status issue";
            getLogEvent(MyFirebaseMessagingService.this);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        int unique = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, unique /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        String channelId = "HamstechApp";
        imageLarge = messageBody.getData().get("image");
        try {
            URL url = new URL(messageBody.getData().get("image"));
            image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch(IOException e) {
            System.out.println(e);
        }
        if (imageLarge == null || imageLarge.equals("false")){
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            //.setLargeIcon(R.drawable.ic_l)
                            .setContentTitle(messageBody.getData().get("title"))
                            .setContentText(messageBody.getData().get("body"))
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody.getData().get("body")))
                            .setAutoCancel(true)
                            .setSmallIcon(R.mipmap.generic_notification)
                            .setContentIntent(pendingIntent);
        }else {
            notificationBuilder =
                    new NotificationCompat.Builder(this, channelId)
                            //.setLargeIcon(R.drawable.ic_l)
                            .setContentTitle(messageBody.getData().get("title"))
                            .setContentText(messageBody.getData().get("body"))
                            .setStyle(new NotificationCompat.BigPictureStyle()
                                    .bigPicture(image).setSummaryText(messageBody.getData().get("body")))
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setAutoCancel(true)
                            //.setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody.getData().get("body")))
                            .setSmallIcon(R.mipmap.generic_notification)
                            .setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(channelId, "HamstechApp", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(mChannel);
            imageLarge = messageBody.getData().get("image");
            if (imageLarge == null || imageLarge.equals("false")){
                notificationBuilder = new NotificationCompat.Builder(this)
                        //.setLargeIcon(R.drawable.ic_l)
                        .setChannelId(channelId)
                        .setContentTitle(messageBody.getData().get("title"))
                        .setContentText(messageBody.getData().get("body"))
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody.getData().get("body")))
                        .setSmallIcon(R.mipmap.generic_notification)
                        .setContentIntent(pendingIntent);
            } else {
                notificationBuilder = new NotificationCompat.Builder(this, channelId)
                        //.setLargeIcon(R.drawable.ic_l)
                        .setContentTitle(messageBody.getData().get("title"))
                        .setContentText(messageBody.getData().get("body"))
                        .setStyle(new NotificationCompat.BigPictureStyle()
                                .bigPicture(image).setSummaryText(messageBody.getData().get("body")))
                        .setWhen(System.currentTimeMillis())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setSmallIcon(R.mipmap.generic_notification)
                        .setContentIntent(pendingIntent);
            }
        }

        notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
    }
    public void getLogEvent(Context context){
        JSONObject data = new JSONObject();
        try {
            data.put("apikey",context.getResources().getString(R.string.lblApiKey));
            data.put("appname","Dashboard");
            data.put("mobile", UserDataConstants.userMobile);
            data.put("fullname", UserDataConstants.userName);
            data.put("email","");
            data.put("category","");
            data.put("course","");
            data.put("lesson",notiTitle);
            data.put("activity",activityLog);
            data.put("pagename",PagenameLog);
            logEventsActivity.LogEventsActivity(context,data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
