package app.whistle.android.com.br.whistle.control;

import android.content.Context;

/**
 * Created by rafael on 05/12/2015.
 */
public class ControlerFactoryMethod {

    public static UserControler getUserControler(Context ctx) {
        return new UserControler(ctx);
    }

    public static ConfigControler getConfigControler(Context ctx){
        return new ConfigControler(ctx);
    }

    public static ContactControler getContactControler(Context ctx){
        return new ContactControler(ctx);
    }

/*    public static ContactControler getContactControler(){
        return new ContactControler();
    }*/

    public static LocalizationControler getLocalizationControler(Context ctx){
        return new LocalizationControler(ctx);
    }

}
