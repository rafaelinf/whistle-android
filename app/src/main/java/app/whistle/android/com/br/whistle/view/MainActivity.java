package app.whistle.android.com.br.whistle.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.reciver.BootReceiver;
import app.whistle.android.com.br.whistle.service.LocalizationContactsService;
import app.whistle.android.com.br.whistle.service.ManagerContactService;
import app.whistle.android.com.br.whistle.service.UploadLocalizationService;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_CLASS = "MainActivity";

    private Handler handler = new Handler();

    Intent intentUploadLocalization;
    Intent intentLocalizationContact;
    Intent intentManagerContactService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getSupportActionBar().setElevation(0);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SampleFragmentPagerAdapter(getSupportFragmentManager(), MainActivity.this));

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        startServicesWhistle();
        startReciver();

    }

    public void startServicesWhistle(){
        try {

            intentUploadLocalization = new Intent(getBaseContext(), UploadLocalizationService.class);
            startService(intentUploadLocalization);

            intentLocalizationContact = new Intent(this, LocalizationContactsService.class);
            startService(intentLocalizationContact);

            intentManagerContactService = new Intent(this, ManagerContactService.class);
            startService(intentManagerContactService);

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo startServicesWhistle: " + e.getMessage());
        }
    }

    public void startReciver(){
        try {

            ComponentName receiver = new ComponentName(getApplicationContext(), BootReceiver.class);
            PackageManager pm = getApplicationContext().getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);


        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo startReciver: " + e.getMessage());
            e.printStackTrace();
        }
    }

/*
    public class ContactObserver extends ContentObserver {

        public ContactObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            Log.i(LOG_CLASS, "ContactObserver change");

            //WhistleNotification.notificationSimple(getBaseContext());
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            Log.i(LOG_CLASS, "ContactObserver change");
            WhistleNotification.notificationSimple(getBaseContext());
        }
    }
*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {

            Intent i = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(intentLocalizationContact != null){
            stopService(intentLocalizationContact);
        }

    }

}
