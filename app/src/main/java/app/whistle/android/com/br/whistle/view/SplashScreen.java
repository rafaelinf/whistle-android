package app.whistle.android.com.br.whistle.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 17/11/2015.
 */
public class SplashScreen extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private ImageView imgSplash;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        try
        {((View)findViewById(android.R.id.title).getParent()).setVisibility(View.GONE);
        }
        catch (Exception e) {}
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        initSplashScreen();
        WhistleSingleton.getInstance().initSetup(getApplicationContext());

    }

    private void checkPermission() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add(getString(R.string.gps));
        if (!addPermission(permissionsList, Manifest.permission.READ_CONTACTS))
            permissionsNeeded.add(getString(R.string.readContacts));
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add(getString(R.string.writeStorage));
        if (!addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add(getString(R.string.readStorage));

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = getString(R.string.msgYouNeedGrantAccess) + " " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]), REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }

        showActivity();
        //insertDummyContact();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(SplashScreen.this)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<>();
                // Initial
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted

                    showActivity();

                } else {
                    // Permission Denied
                    Toast.makeText(SplashScreen.this, "Permissão negada", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);



        }

        //showActivity();
    }
    
    public void initSplashScreen(){
        try{

            imgSplash = (ImageView) findViewById(R.id.imgSplash);
            AnimationSet animation = new AnimationSet(true);
            animation.addAnimation(new AlphaAnimation(0.0F, 1.0F));
            animation.addAnimation(new ScaleAnimation(0.0f, 1, 0.0f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)); // Change args as desired
            animation.setDuration(1500);
            imgSplash.startAnimation(animation);

            new CountDownTimer(3500, 1000) {

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        checkPermission();

                    } else {
                        // Pre-Marshmallow
                        showActivity();
                    }



                }

            }.start();

        }catch (Exception e){
            Log.e("SplashScreen", "InitSplash - " + e.getMessage());
        }
    }

    public void showActivity(){
        User user = ControlerFactoryMethod.getUserControler(getBaseContext()).findUser();
        if(user != null){

            if(user.getDtactive() != null){

                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();

            }else{

                Intent i = new Intent(SplashScreen.this, ConfirmRegistrationActivity.class);
                startActivity(i);
                finish();

            }

        }else{
            Intent i = new Intent(SplashScreen.this, RegistryActivity.class);
            startActivity(i);
            finish();
        }

    }
}
