package app.whistle.android.com.br.whistle.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import app.whistle.android.com.br.whistle.R;

/**
 * Created by rafael on 27/03/2016.
 */
public class WhistleNotification {
    private static final String LOG_CLASS = "WhistleNotification";

    public static void notificationSimple(Context context){
        try {

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.ic_whistle_16)
                            .setContentTitle("My notification")
                            .setContentText("Hello World!");


            int mNotificationId = 001;

            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

            mNotifyMgr.notify(mNotificationId, mBuilder.build());

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo notificationSimple: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
