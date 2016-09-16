package app.whistle.android.com.br.whistle.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.squareup.picasso.Picasso;
import java.io.File;
import java.io.IOException;
import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.utils.WhistleImage;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;

/**
 * Created by rafael on 03/03/2016.
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TEMP_PHOTO_FILE = "temporary_holder.jpg";

    private static final String LOG_CLASS = "ProfileActivity";

    public static final int GET_FROM_GALLERY = 3;

    private ImageView imgProfile;
    private Button btnChangeImage;
    private Switch switchMyLocalization;
    private TextView txtName, txtNumber, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_profile);

        setTitle(R.string.profile);
        initComponents();
    }

    private void initComponents() {
        try {

            imgProfile = (ImageView) findViewById(R.id.imgProfile);

            User user = ControlerFactoryMethod.getUserControler(getBaseContext()).findUser();
            if(user.getUrlImageProfile() != null){
                Picasso.with(this).load(new File(user.getUrlImageProfile()))
                        .error(R.drawable.ic_whistle_256)
                        .into(imgProfile);
            }

            btnChangeImage = (Button) findViewById(R.id.btnChangeImage);
            btnChangeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent cropIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                    cropIntent.setType("image/*");
                    cropIntent.putExtra("crop", "true");
                    cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, getTempUri());
                    cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                    cropIntent.putExtra("aspectX", 1);
                    cropIntent.putExtra("aspectY", 1);
                    cropIntent.putExtra("outputX", 256);
                    cropIntent.putExtra("outputY", 256);

                    startActivityForResult(cropIntent, GET_FROM_GALLERY);
                }
            });

            switchMyLocalization = (Switch) findViewById(R.id.switchMyLocalization);
            checkStatusLocation();

            txtName = (TextView) findViewById(R.id.txtName);
            txtName.setText(user.getName());

            txtNumber = (TextView) findViewById(R.id.txtNumber);
            txtNumber.setText(user.getNumber());

            txtEmail = (TextView) findViewById(R.id.txtEmail);
            txtEmail.setText(user.getEmail());

        }catch (Exception e){
            Log.e(LOG_CLASS, "initComponents - " + e.getMessage());
        }
    }

    private Uri getTempUri() {
        return Uri.fromFile(getTempFile());
    }

    private File getTempFile() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            File file = new File(Environment.getExternalStorageDirectory(),TEMP_PHOTO_FILE);
            try {
                file.createNewFile();
            } catch (IOException e) {}

            return file;
        } else {

            return null;
        }
    }

    private void checkStatusLocation(){
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            private long startTime = System.currentTimeMillis();
            public void run() {
                while (true) {

                    handler.post(new Runnable(){
                        public void run() {
                            switchMyLocalization.setClickable(false);
                            switchMyLocalization.setChecked(WhistleUtils.checkGPSEnable(getBaseContext()));
                        }
                    });

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        new Thread(runnable).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {

            File tempFile = getTempFile();
            String filePath= Environment.getExternalStorageDirectory() +"/" + TEMP_PHOTO_FILE;

            Bitmap selectedImage =  BitmapFactory.decodeFile(filePath);
            if(selectedImage != null){
                UploadImageProfile uploadImageProfile = new UploadImageProfile(WhistleSingleton.getInstance().getContext());
                uploadImageProfile.execute(selectedImage);
            }

            if (tempFile.exists()) tempFile.delete();

        }
    }

    private final class UploadImageProfile extends AsyncTask<Bitmap, Void, ReturnRSVO> {
        private ProgressDialog dialog;
        private Context context;

        public UploadImageProfile(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ProfileActivity.this, context.getString(R.string.app_name), context.getString(R.string.msgUpdatingProfilePhoto), true);
        }

        @Override
        protected ReturnRSVO doInBackground(Bitmap... bitmap) {
            return ControlerFactoryMethod.getUserControler(context).editImageProfile(bitmap[0]);
        }

        @Override
        protected void onPostExecute(ReturnRSVO returnRSVO) {
            dialog.dismiss();
            refreshImage(returnRSVO);
        }
    }

    public void refreshImage(ReturnRSVO returnRSVO){
        try {

            User user = ControlerFactoryMethod.getUserControler(getBaseContext()).findUser();
            if(user.getUrlImageProfile() != null){
                Picasso.with(this).load(new File(user.getUrlImageProfile()))
                        .error(R.drawable.ic_whistle_256)
                        .into(imgProfile);
            }

            if(returnRSVO != null && returnRSVO.getMsg().equals(TypeReturnRSVO.OK)){
                Toast.makeText(this, R.string.msgTheImageSuccessfullyChanged, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, returnRSVO.getErrorRSVO().getMessage(), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo refreshImage: " + e.getMessage());
            e.printStackTrace();
        }
    }

}