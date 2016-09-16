package app.whistle.android.com.br.whistle.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.auxiliary.CountryVO;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 09/12/2015.
 */
public class RegistryActivity extends AppCompatActivity {

    private static final String LOG_CLASS = "RegistryActivity";

    private EditText txtPrefix, txtNumber, txtName, txtEmail;
    private Spinner spnCountry;
    private Button btnRegistrySave, btnRegistryCancel;
    private CountryVO countryVO = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_registry);

        initComponents();
    }

    public void initComponents(){
        try {

            txtPrefix = (EditText) findViewById(R.id.txtPrefix);
            txtNumber = (EditText) findViewById(R.id.txtNumber);
            txtName = (EditText) findViewById(R.id.txtName);
            txtEmail = (EditText) findViewById(R.id.txtEmail);

            btnRegistrySave = (Button) findViewById(R.id.btnRegistrySave);
            btnRegistrySave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveUser();
                }
            });

            btnRegistryCancel = (Button) findViewById(R.id.btnRegistryCancel);
            btnRegistryCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });

            spnCountry = (Spinner) findViewById(R.id.spnCountry);
            fillDataCountries();

            spnCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    CountryVO selectedCountry = (CountryVO) parent.getItemAtPosition(position);
                    if (position > 0) {
                        countryVO = selectedCountry;
                    }else{
                        countryVO = null;
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }catch (Exception e){
            Log.e("RegistryActivity", "Erro initComponents - " + e.getMessage());
        }
    }

    private void saveUser(){
        try {

            if(countryVO != null){

                String ID = (Settings.Secure.getString(getBaseContext().getContentResolver(), Settings.Secure.ANDROID_ID));

                boolean success = ControlerFactoryMethod.getUserControler(getApplicationContext()).registrationRequest(ID, countryVO.getCode(), txtPrefix.getText().toString(), txtName.getText().toString(), txtNumber.getText().toString(), txtEmail.getText().toString());
                if(success){

                    Toast.makeText(this, getString(R.string.welcome) + " " + txtName.getText().toString(), Toast.LENGTH_SHORT).show();

                    Intent i = new Intent(RegistryActivity.this, SplashScreen.class);
                    startActivity(i);
                    finish();

                }else{
                    Toast.makeText(this, R.string.incorrectData, Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(this, R.string.pleaseSelectCountryOrigin, Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo saveUser: " + e.getMessage());
        }
    }

    public void fillDataCountries(){
        try {

            List<CountryVO> lsCountryVOs = new ArrayList<>();
            CountryVO cDefault = new CountryVO();
            cDefault.setCode(null);
            cDefault.setName(getString(R.string.selectCountry));
            lsCountryVOs.add(cDefault);

            CountryVO c1 = new CountryVO();
            c1.setCode("+55");
            c1.setName("Brasil");
            lsCountryVOs.add(c1);

            ArrayAdapter<CountryVO> dataAdapter = new ArrayAdapter<CountryVO>(this, R.layout.spinner_country, lsCountryVOs){
                @Override
                public boolean isEnabled(int position){
                    if(position == 0)
                    {
                        // Disable the first item from Spinner
                        // First item will be use for hint
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(position == 0){
                        // Set the hint text color gray
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnCountry.setAdapter(dataAdapter);

            //spnCountry.setSelection(dataAdapter.getCount());

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no fillDataLocation: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
