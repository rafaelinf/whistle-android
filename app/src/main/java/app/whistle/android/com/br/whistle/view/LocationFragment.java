package app.whistle.android.com.br.whistle.view;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.views.MapView;

import java.util.List;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.location.GPSTracker;
import app.whistle.android.com.br.whistle.service.UploadLocalizationService;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.thread.RefreshDataContact;
import app.whistle.android.com.br.whistle.thread.UpdateMapLocalization;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import br.com.brns.whistle.protocol.vo.entity.LocalizationVO;

/**
 * Created by rafael on 09/03/2016.
 */
public class LocationFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private static final String LOG_CLASS = "LocationFragment";

    public GPSTracker gpsTracker;
    public Location location;
    public MapView mapView;


    public static LocationFragment newInstance() {
        Log.i(LOG_CLASS, "LocationFragment newInstance...");
        LocationFragment fragment = new LocationFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_CLASS, "LocationFragment onCreate...");
        gpsTracker = new GPSTracker(getContext());

        WhistleSingleton.getInstance().setViewLocalizationFragment(null);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;

        if(WhistleSingleton.getInstance().getViewLocalizationFragment() == null) {

            Location location = gpsTracker.getLocation();

            view = inflater.inflate(R.layout.fragment_localization, container, false);

            mapView = (MapView) view.findViewById(R.id.mapview);
            mapView.setAccessToken(WhistleSingleton.TOKEN_MAPBOX);
            mapView.setStyleUrl(Style.MAPBOX_STREETS);

            if (location != null) {
                mapView.setCenterCoordinate(new LatLng(location.getLatitude(), location.getLongitude()));
                mapView.addMarker(new MarkerOptions().title("EU").snippet("Estou aqui!").position(new LatLng(location.getLatitude(), location.getLongitude())));
            } else {
                mapView.setCenterCoordinate(new LatLng(-23.5475000, -46.6361100));
            }

            mapView.setZoomLevel(11);
            mapView.onCreate(savedInstanceState);

            new Thread(threadLocalizationContacts).start();
            WhistleSingleton.getInstance().setViewLocalizationFragment(view);

        }else{
            view = WhistleSingleton.getInstance().getViewLocalizationFragment();
        }

        return view;
    }

    public Runnable threadLocalizationContacts = new Runnable() {

        public void run() {

            Log.i(LOG_CLASS, "threadLocalizationContacts");

            while (true){

                updateLocalizationContacts();

                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                }

            }
        }
    };

    public void updateLocalizationContacts(){
        try {

            Log.i(LOG_CLASS, "updateLocalizationContacts");

            mapView.removeAllAnnotations();

            try {
                location = gpsTracker.getLocation();
            }catch (Exception e){
            }

            if (location != null) {
                ///mapView.setCenterCoordinate(new LatLng(gpsTracker.getLocation().getLatitude(), location.getLongitude()));
                mapView.addMarker(new MarkerOptions().title("EU").snippet("Estou aqui!").position(new LatLng(location.getLatitude(), location.getLongitude())));
            } else {
                mapView.setCenterCoordinate(new LatLng(-23.5475000, -46.6361100));
            }

            List<LocalizationVO> lsLocalizationVOs = WhistleSingleton.getInstance().getLsLocalizationVOs();
            if(lsLocalizationVOs != null && !lsLocalizationVOs.isEmpty()){

                for (LocalizationVO l : lsLocalizationVOs){
                    mapView.addMarker(new MarkerOptions().title(l.getUserowner().getUsname()).position(new LatLng(l.getLolat().doubleValue(), l.getLolng().doubleValue())));
                }

            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo updateLocalizationContacts: " + e.getMessage());
            e.printStackTrace();
        }
    }

/*
    public Icon getIconUser(){
        try {
            Drawable mIconDrawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_whistle_12);
            Icon icon = WhistleUtils.getIconMap(getContext(), mIconDrawable, 55, 60);
            return icon;
        }catch (Exception e){
            Log.e(LOG_CLASS, "getIconUser: " + e.getMessage());
            return null;
        }
    }

    public Icon getIconContact(Drawable mIconDrawable){
        try {
            Icon icon = WhistleUtils.getIconMap(getContext(), mIconDrawable, 70, 70);
            return icon;
        }catch (Exception e){
            Log.e(LOG_CLASS, "getIconContact: " + e.getMessage());
            return null;
        }
    }
*/

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_CLASS, "onStart...");
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_CLASS, "onStop...");
        mapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_CLASS, "onDestroy...");
        mapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_CLASS, "onResume...");
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_CLASS, "onPause...");
        mapView.onPause();
    }

}
