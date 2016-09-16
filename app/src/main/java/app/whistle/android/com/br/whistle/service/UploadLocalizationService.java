package app.whistle.android.com.br.whistle.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.location.GPSTracker;

/**
 * Created by rafael on 23/03/2016.
 */
public class UploadLocalizationService extends Service {

    private static final String TAG = "UploadLocService";

    private static final String LOCK_NAME = UploadLocalizationService.class.getName() + ".Lock";
    private static volatile PowerManager.WakeLock lockStatic = null; // notice static

    private GPSTracker gpsTracker;
    private Location location;

    synchronized private static PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            lockStatic = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, LOCK_NAME);
            lockStatic.setReferenceCounted(true);
        }
        return (lockStatic);
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        gpsTracker = new GPSTracker(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");

        PowerManager.WakeLock lock = getLock(this.getApplicationContext());
        lock.acquire();

        new Thread(new Runnable() {

            @Override
            public void run() {

                while (true){

                    Log.i(TAG, "Service running");

                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                    }

                    try {

                        location = gpsTracker.getLocation();
                        if(location != null){

                            //Log.i(TAG, "Latitude: " + gpsTracker.getLatitude());
                            //Log.i(TAG, "Longitude: " + gpsTracker.getLongitude());

                            ControlerFactoryMethod.getLocalizationControler(getBaseContext()).save(gpsTracker.getLatitude(), gpsTracker.getLongitude());

                        }else{
                            Log.i(TAG, "A localização está desativada");
                        }

                    }catch (Exception e){
                        Log.i(TAG, "Erro no metodo UploadLocalizationService: " + e.getMessage());
                        e.printStackTrace();
                    }

                }

            }
        }).start();

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;
    }

}
