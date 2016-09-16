package app.whistle.android.com.br.whistle.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.User;
import br.com.brns.whistle.protocol.vo.entity.UserVO;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;


/**
 * Created by rafael on 27/01/2016.
 */
public class ConfirmRegistrationActivity extends AppCompatActivity {

    private static final String LOG_CLASS = "ConfirmRegistration";

    private User user;
    EditText txtCode;
    Button btnRegistrySave, btnRegistryCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_confirmregistry);

        initComponents();
    }

    public void initComponents(){
        try {

            user = ControlerFactoryMethod.getUserControler(getBaseContext()).findUser();

            txtCode = (EditText) findViewById(R.id.txtCode);

            btnRegistrySave = (Button) findViewById(R.id.btnRegistrySave);
            btnRegistrySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmUser();
                }
            });

            btnRegistryCancel = (Button) findViewById(R.id.btnRegistryCancel);
            btnRegistryCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            txtCode.setText(user.getRegistrationcode());

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro initComponents - " + e.getMessage());
        }
    }

    private void confirmUser(){
        try {

            boolean success = ControlerFactoryMethod.getUserControler(getApplicationContext()).confirmRegistration(user, txtCode.getText().toString());
            if(success){

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

/*
                        ActivityCompat.requestPermissions(getApplicationContext(),
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACT);
*/

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }

                //Intent intentUploadLocalization = new Intent(getBaseContext(), UploadLocalizationService.class);
                //startService(intentUploadLocalization);

                Intent i = new Intent(ConfirmRegistrationActivity.this, SetupActivity.class);
                startActivity(i);
                finish();

            }else{
                Toast.makeText(this, R.string.msgTheCodeIncorrect, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo saveUser: " + e.getMessage());
        }
    }

}
