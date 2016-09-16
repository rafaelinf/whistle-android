package app.whistle.android.com.br.whistle.utils;

import android.content.Context;

import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 15/12/2015.
 */
public class WhistleInternet implements Runnable {

    private Context context;

    public WhistleInternet(Context context){
        this.context = context;
    }

    @Override
    public void run() {

        while(true){

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //WhistleSingleton.getInstance().setIsOnline(WhistleUtils.isOnline(context));

        }
    }

}
