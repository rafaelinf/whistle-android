package app.whistle.android.com.br.whistle.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.List;

import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.location.GPSTracker;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import br.com.brns.whistle.protocol.vo.entity.LocalizationVO;

/**
 * Created by rafael on 23/03/2016.
 */
public class LocalizationContactsService  extends Service {

    private static final String TAG = "LocContactsService";

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        try {

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true){

                        Log.i(TAG, "Service running");

                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                        }

                        List<LocalizationVO> ls = ControlerFactoryMethod.getLocalizationControler(getBaseContext()).getLocalizationContacts();
                        if(ls != null && !ls.isEmpty()){
                            WhistleSingleton.getInstance().getLsLocalizationVOs().clear();
                            WhistleSingleton.getInstance().getLsLocalizationVOs().addAll(ls);
                        }

                    }

                    //stopSelf();
                }
            }).start();

        }catch (Exception e){
            stopSelf();
        }

        return Service.START_STICKY;
    }


    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

    @Override
    public void onDestroy() {
        WhistleSingleton.getInstance().setIsRunning_LocalizationContactsService(false);
        Log.i(TAG, "Service onDestroy");
    }

}
