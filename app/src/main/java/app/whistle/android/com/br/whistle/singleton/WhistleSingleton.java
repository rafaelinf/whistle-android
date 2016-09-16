package app.whistle.android.com.br.whistle.singleton;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.location.GPSTracker;
import app.whistle.android.com.br.whistle.service.UploadLocalizationService;
import app.whistle.android.com.br.whistle.view.LocationFragment;
import br.com.brns.whistle.protocol.vo.entity.LocalizationVO;
import br.com.brns.whistle.protocol.vo.entity.UserVO;

/**
 * Created by rafael on 09/12/2015.
 */
public class WhistleSingleton {

    private static final String LOG_CLASS = "WhistleSingleton";

    private static WhistleSingleton _instance;
    private Context context;

    public final static String TOKEN_MAPBOX = "pk.eyJ1IjoicmFmYWVsaW5mIiwiYSI6ImNpbHk0enFmdDA4ODh2OWtzYmJlcWs4cTQifQ.wgXzsHewmj_PqzZt5Fuklg";

    //public final static String URL_WHISTLE_WS = "http://api.whistle-message.com:8080/whistle-comm-ws/";
    public final static String URL_WHISTLE_WS = "http://192.168.43.94:8080/whistle-backend-ws/";
    //public final static String URL_WHISTLE_WS = "http://192.168.0.56:8080/whistle-comm-ws/";
    //public final static String URL_WHISTLE_WS = "http://192.168.0.101:8080/whistle-comm-ws/";

    private List<LocalizationVO> lsLocalizationVOs = new ArrayList<>();

    private int statusConnection;

    private View viewLocalizationFragment;

    private boolean isRunning_LocalizationContactsService = true;
    private boolean isRunning_SynchronizeContactsService = true;

    private WhistleSingleton() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public synchronized static WhistleSingleton getInstance() {
        if (_instance == null) {
            _instance = new WhistleSingleton();
        }
        return _instance;
    }

    public void initSetup(Context context){
        this.context = context;
        ControlerFactoryMethod.getConfigControler(context).pingWhistle();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getStatusConnection() {
        return statusConnection;
    }

    public void setStatusConnection(int statusConnection) {
        this.statusConnection = statusConnection;
    }

    public View getViewLocalizationFragment() {
        return viewLocalizationFragment;
    }

    public void setViewLocalizationFragment(View viewLocalizationFragment) {
        this.viewLocalizationFragment = viewLocalizationFragment;
    }

    public boolean isRunning_LocalizationContactsService() {
        return isRunning_LocalizationContactsService;
    }

    public void setIsRunning_LocalizationContactsService(boolean isRunning_LocalizationContactsService) {
        this.isRunning_LocalizationContactsService = isRunning_LocalizationContactsService;
    }

    public List<LocalizationVO> getLsLocalizationVOs() {
        return lsLocalizationVOs;
    }

    public boolean isRunning_SynchronizeContactsService() {
        return isRunning_SynchronizeContactsService;
    }

    public void setIsRunning_SynchronizeContactsService(boolean isRunning_SynchronizeContactsService) {
        this.isRunning_SynchronizeContactsService = isRunning_SynchronizeContactsService;
    }

}
