package app.whistle.android.com.br.whistle.thread;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.brns.whistle.backend.protocol.vo.entity.LocalizationVO;

import java.util.List;

import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.view.LocationFragment;

/**
 * Created by rafael on 18/03/2016.
 */
public class UpdateMapLocalization  extends AsyncTask<Void, Void, List<LocalizationVO>> {

    private static final String LOG_CLASS = "UpdateMapLocaliza";

    private Context context;
    private LocationFragment locationFragment;

    public UpdateMapLocalization(Context context, LocationFragment locationFragment){
        this.context = context;
        this.locationFragment = locationFragment;
    }

    @Override
    protected void onPreExecute() {
        Log.i(LOG_CLASS, "Iniciando UpdateMapLocalization...");
    }

    @Override
    protected List<LocalizationVO> doInBackground(Void... params) {
        List<LocalizationVO> lsLocalizationVOs = ControlerFactoryMethod.getLocalizationControler(WhistleSingleton.getInstance().getContext()).getLocalizationContacts();
        return lsLocalizationVOs;
    }

    @Override
    protected void onPostExecute(List<LocalizationVO> lsLocalizationVOs) {
        //locationFragment.updateLocalizationContacts();
    }

}
