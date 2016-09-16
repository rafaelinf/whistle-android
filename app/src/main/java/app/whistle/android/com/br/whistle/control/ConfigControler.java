package app.whistle.android.com.br.whistle.control;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.utils.WhistleJson;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;

/**
 * Created by rafael on 23/01/2016.
 */
public class ConfigControler {

    private static final String LOG_CLASS = "ConfigControler";

    private Context ctx;

    public ConfigControler(Context ctx){
        this.ctx = ctx;
    }

    public void pingWhistle() {
        new Thread() {

            @Override
            public void run() {

                boolean cont = true;

                while(cont){

                    if(WhistleUtils.isOnline(ctx)) {
                        cont = false;

                    }else {

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                }


            }

        }.start();

    }

    public boolean wsKeepAliveComm(User user){
        try {

            String jsonString = WhistleJson.makeRequest("keepAliveComm/" + user.getIdentification());
            Log.i(LOG_CLASS, "Response wsKeepAliveComm = " + jsonString);
            return true;

        }catch (Exception e){
            Log.e(LOG_CLASS, "wsKeepAliveComm - " + e.getMessage());
            return false;
        }
    }

}
