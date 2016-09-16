package app.whistle.android.com.br.whistle.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 09/03/2016.
 */
public class ContactActivity  extends AppCompatActivity {

    private static final String LOG_CLASS = "ContactActivity";

    private Contact contact = null;

    ImageView imgContact;
    TextView txtName, txtNumber;
    public Switch switchShareLocalization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_contact);

        Bundle extras = getIntent().getExtras();
        Integer id = extras.getInt("idcontact");

        Log.i(LOG_CLASS, "ID = " + id);

        contact = ControlerFactoryMethod.getContactControler(WhistleSingleton.getInstance().getContext()).findContactById(id);
        if(contact != null){
            Log.i(LOG_CLASS, "Contact = " + contact.getName());
        }else{
            Log.i(LOG_CLASS, "Contact = NULL");
        }


        initComponents();
    }

    public void initComponents() {
        try {

            imgContact = (ImageView) findViewById(R.id.imgContact);
            txtName = (TextView) findViewById(R.id.txtName);
            txtNumber = (TextView) findViewById(R.id.txtNumber);
            switchShareLocalization = (Switch) findViewById(R.id.switchShareLocalization);

            if(contact.getUrlimage() != null){
                Picasso.with(this).load(new File(contact.getUrlimage()))
                        .error(R.drawable.ic_whistle_24)
                        .into(imgContact);
            }

            txtName.setText(contact.getName());
            txtNumber.setText(contact.getNumber());
            switchShareLocalization.setChecked(contact.isSharelocation());

            switchShareLocalization.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    EditShareLocationThread editShareLocationThread = new EditShareLocationThread(ContactActivity.this, contact, isChecked);
                    editShareLocationThread.execute();
                }
            });

        } catch (Exception e) {
            Log.e(LOG_CLASS, "Erro initComponents - " + e.getMessage());
            e.printStackTrace();
        }
    }

    class EditShareLocationThread extends AsyncTask<Void, Void, Boolean> {

        private ProgressDialog dialog;
        private Context context;

        private Contact contact;
        private boolean checked;

        public EditShareLocationThread(Context context, Contact contact, boolean checked) {
            this.context = context;
            this.contact = contact;
            this.checked = checked;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, context.getString(R.string.app_name), context.getString(checked == true ? R.string.msgFreeingLocation : R.string.msgBlockingLocation), true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return ControlerFactoryMethod.getContactControler(context).editShareLocation(contact, checked);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            switchShareLocalization.setChecked(result);
            dialog.dismiss();
        }
    }

}