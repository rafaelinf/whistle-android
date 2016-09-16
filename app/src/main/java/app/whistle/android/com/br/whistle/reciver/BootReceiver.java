package app.whistle.android.com.br.whistle.reciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import app.whistle.android.com.br.whistle.service.ManagerContactService;
import app.whistle.android.com.br.whistle.service.UploadLocalizationService;

/**
 * Created by rafael on 24/03/2016.
 */
public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Log.i("BootReceiver", "onReceive...");

            Intent intentUploadLocalization = new Intent(context, UploadLocalizationService.class);
            context.startService(intentUploadLocalization);

            Intent intentManagerContactService = new Intent(context, ManagerContactService.class);
            context.startService(intentManagerContactService);

        }
    }

}