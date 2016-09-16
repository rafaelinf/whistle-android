package app.whistle.android.com.br.whistle.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import app.whistle.android.com.br.whistle.utils.ContactObserver;

/**
 * Created by rafael on 20/04/2016.
 */
public class ManagerContactService extends Service {

    private static final String TAG = "MngContactService";

    private Handler handler = new Handler();
    ContactObserver contactObserver = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        contactObserver = new ContactObserver(handler, this);
        getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, contactObserver);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

}
