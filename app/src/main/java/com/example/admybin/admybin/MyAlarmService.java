package com.example.admybin.admybin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;


public class MyAlarmService extends Service 
{
      
   //private NotificationManager mManager;

 
    @Override
    public IBinder onBind(Intent arg0)
    {
       // TODO Auto-generated method stub
        return null;
    }
 
    @Override
    public void onCreate() 
    {
       // TODO Auto-generated method stub  
       super.onCreate();
    }
 
   @SuppressWarnings("deprecation")
   @Override
   public void onStart(Intent intent, int startId)
   {

       super.onStart(intent, startId);
       Context context;
      
      /* mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
       Intent intent1 = new Intent(this.getApplicationContext(),Bin.class);
     
       Notification notification = new Notification(R.drawable.logo,"This is a test message!", System.currentTimeMillis());
       intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);
 
       PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
       notification.flags |= Notification.FLAG_AUTO_CANCEL;
       notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", "This is a test message!", pendingNotificationIntent);
 
       mManager.notify(0, notification);*/

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(),Bin.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.getApplicationContext());
        stackBuilder.addParentStack(Bin.class);
        stackBuilder.addNextIntent(intent1);

       PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

       NotificationCompat.Builder builder = new NotificationCompat.Builder(this.getApplicationContext());
       Notification notification = builder.setContentTitle("Demo App Notification")
               .setContentText("New Notification From Demo App..")
               .setTicker("New Message Alert!")
               .setSmallIcon(R.drawable.logo)
               .setContentIntent(pendingIntent).build();
       notification.flags |= Notification.FLAG_AUTO_CANCEL;

       notificationManager.notify(0, notification);
    }
 
    @Override
    public void onDestroy() 
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
 
}