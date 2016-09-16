package app.whistle.android.com.br.whistle.thread;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import app.whistle.android.com.br.whistle.view.ContactFragment;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;

/**
 * Created by rafael on 09/03/2016.
 */
public class RefreshDataContact extends AsyncTask<Void, Void, Void> {
    private ProgressDialog dialog;
    private Context context;
    private ContactFragment contactFragment;

    public RefreshDataContact(Context context, ContactFragment contactFragment){
        this.context = context;
        this.contactFragment = contactFragment;
    }

    @Override
    protected void onPreExecute() {
        dialog = ProgressDialog.show(context, context.getString(R.string.app_name), context.getString(R.string.msgRefreshContacts), true);
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<Contact> lsContacts = ControlerFactoryMethod.getContactControler(context).findContactByAll(1, 0);
        if(lsContacts != null && !lsContacts.isEmpty()){
            User user = ControlerFactoryMethod.getUserControler(context).findUser();
            //ControlerFactoryMethod.getContactControler(context).saveAllWS(WhistleUtils.buildUserVO(user));
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        contactFragment.initComponents(contactFragment.getView());
        dialog.dismiss();
    }

}
