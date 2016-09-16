package app.whistle.android.com.br.whistle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rafael on 26/03/2016.
 */
public class WhistleImage {

    private static final String LOG_CLASS = "WhistleImage";

    private Context context;

    public WhistleImage(Context context){
        this.context = context;
    }

    public String saveImage(Bitmap bitmap, String descriptionUrl){
        String url = null;
        try {

            if(bitmap != null){

                File file = storeImage(getContext(), descriptionUrl, bitmap);
                if(file != null){
                    url = file.getAbsolutePath();
                }

            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "saveImage - " + e.getMessage());
        }finally {
            return url;
        }
    }

    public static File storeImage(Context context, String description, Bitmap image) {

        File pictureFile = getOutputMediaFile(context, description);

        if (pictureFile == null) {
            Log.d(LOG_CLASS, "Error creating media file, check storage permissions: " );// e.getMessage());
            return null;
        }

        try {

            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();

            return pictureFile;

        } catch (FileNotFoundException e) {
            Log.d(LOG_CLASS, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(LOG_CLASS, "Error accessing file: " + e.getMessage());
        }

        return null;
    }

    private static File getOutputMediaFile(Context context, String description){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Images");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName="WS_"+ description + "_" + timeStamp +".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
