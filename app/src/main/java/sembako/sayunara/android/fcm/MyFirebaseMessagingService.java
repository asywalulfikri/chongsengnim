package sembako.sayunara.android.fcm;

import android.annotation.SuppressLint;
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
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

import sembako.sayunara.android.R;
import sembako.sayunara.android.constant.Constant;
import sembako.sayunara.android.ui.component.articles.ArticleDetailActivity;
import sembako.sayunara.constant.valueApp;
import sembako.sayunara.main.MainActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String CHANNEL_ID = "admin_channel";

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d("Xenix", "From: " + remoteMessage.getFrom()+" ---- ");
        Log.d("Xenix", "From: " + remoteMessage.getData()+" ---- ");


        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationID = new Random().nextInt(3000);
        PendingIntent pendingIntent = null;

        if (remoteMessage.getData().containsKey("type")) {
            String type = remoteMessage.getData().get("type");
            String id = remoteMessage.getData().get("id");

            if(type.equals(Constant.TypeNotification.article)){
                final Intent intent = new Intent(this, ArticleDetailActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("id",id);
                pendingIntent = PendingIntent.getActivity(this , 0, intent, PendingIntent.FLAG_ONE_SHOT);
            }
        }





        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);

        Uri notificationSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri rawPathUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sayunara);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.icon_notif)
                .setLargeIcon(largeIcon)
                .setContentTitle(remoteMessage.getData().get("title"))
                .setContentText(remoteMessage.getData().get("message"))
                .setSound(rawPathUri)
                .setAutoCancel(true)
                .setSound(notificationSoundUri)
                .setContentIntent(pendingIntent);

        //Set notification color to match your app color template
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            notificationBuilder.setColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        notificationManager.notify(notificationID, notificationBuilder.build());
    }


    private void sendNotification(){

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setupChannels(NotificationManager notificationManager){
        CharSequence adminChannelName = "New notification";
        String adminChannelDescription = "Device to devie notification";

        NotificationChannel adminChannel;
        adminChannel = new NotificationChannel(CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH);
        adminChannel.setDescription(adminChannelDescription);
        adminChannel.enableLights(true);
        adminChannel.getAudioAttributes();
        adminChannel.setLightColor(Color.RED);
        adminChannel.enableVibration(true);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(adminChannel);
        }
    }
}
