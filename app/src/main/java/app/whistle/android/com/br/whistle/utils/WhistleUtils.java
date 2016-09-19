package app.whistle.android.com.br.whistle.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;

import com.brns.whistle.backend.protocol.vo.entity.UserVO;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 03/12/2015.
 */
public class WhistleUtils {

    private static final String LOG_CLASS = "WhistleUtils";

    public static final DateFormat dateFormatTmz = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public static String generateValueAlphaNumber(int amount){

        String[] lsSecrets = {"A","B","C","D"
                ,"E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W",
                "X","Y","Z", "1","2","3","4","5","6","7","8","9"};

        String secret = "";

        for (int x=0; x < amount; x++){
            int j = (int) (Math.random()*lsSecrets.length);
            secret += lsSecrets[j];
        }

        return secret;
    }

    public static UserVO buildUserVO(User u){
        try {

            UserVO userVO = null;

            if(u != null){
                userVO = new UserVO();
                userVO.setUsidentification(u.getIdentification());
                userVO.setUsnumber(u.getNumber());
                userVO.setUsprefix(u.getPrefix());
                userVO.setUsnumberfull(u.getCodecountry() + u.getPrefix() + u.getNumber());
            }

            return userVO;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo buildUserVO: " + e.getMessage());
            return null;
        }
    }


    public static String convertDateGmt0ToDB(Date dt){
        try {

/*
            SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormatLocal.parse( dateFormatGmt.format(new Date()) );
*/

        }catch(Exception e){
        }

        return null;
    }

    public static boolean isOnline(Context context) {
        try {

            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            if(isConnected){
                Log.i("Wh_Internet", "SIM: " + isConnected);
            }else{
                Log.i("Wh_Internet", "NÃO: " + isConnected);
            }
            return isConnected;

        }catch (Exception e){
            Log.e("WhistleUtils", " Erro isOnline - " + e.getMessage());
            return false;
        }
    }

    public static boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping " + ip);
            //Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e){
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return false;

    }

    public static Date longDateParseTmz(String longDate){
        try {

            Date date = new Date(new Long(longDate));
            return date;

        }catch (Exception e){
            Log.e(LOG_CLASS, "longDateParseTmz - " + e.getMessage());
        }

        return null;
    }

    public static Date dateParseTmz(String date){
        try {

            if(date != null){
                return dateFormatTmz.parse(date);
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "dateParseTmz - " + e.getMessage());
        }

        return null;
    }

    public static String dateParseTmzFormat(Date date){
        try {

            if(date != null){
                return dateFormatTmz.format(date);
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "dateIso8601Format - " + e.getMessage());
        }

        return null;
    }

    public static Integer intToParse(String value){
        try {

            Integer result = null;

            if(value != null){

                try {
                    result = Integer.parseInt(value);
                }catch (Exception e){
                }

            }

            return result;

        }catch (Exception e){
            Log.e(LOG_CLASS, "intToParse - " + e.getMessage());
        }

        return null;
    }

    public static Integer booleanToInt(Boolean value){
        try {

            Integer result = 0;

            if(value != null){

                if(value){
                    result = 1;
                }else{
                    result = 0;
                }

            }

            return result;

        }catch (Exception e){
            Log.e(LOG_CLASS, "booleanToInt - " + e.getMessage());
        }

        return 0;
    }

    public static Boolean intToBoolean(Integer value){
        try {

            Boolean result = null;

            if(value != null){

                if(value == 0){
                    result = false;

                }else if(value == 1){
                    result = true;
                }

            }

            return result;

        }catch (Exception e){
            Log.e(LOG_CLASS, "intToBoolean - " + e.getMessage());
        }

        return null;
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

    public static Bitmap base64ToBitmap(String encodedImage){
        try {

            byte[] decodedString = Base64.decode(encodedImage.getBytes("UTF-8"), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return decodedByte;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo base64ToBitmap: " + e.getMessage());
            return null;
        }
    }

    public static Bitmap loadBitmap(String path){
        try {

            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo loadBitmap: " + e.getMessage());
            return null;
        }
    }

    public static String saveImage(String encodedImage, String descriptionUrl){
        String url = null;
        try {

            Bitmap bitmap = null;

            bitmap = WhistleUtils.base64ToBitmap(encodedImage);
            if(bitmap != null){
                url = saveImage(bitmap, descriptionUrl);
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "saveImage - " + e.getMessage());
        }finally {
            return url;
        }
    }

    public static String saveImage(Bitmap bitmap, String descriptionUrl){
        String url = null;
        try {

            if(bitmap != null){

                File file = WhistleUtils.storeImage(WhistleSingleton.getInstance().getContext(), descriptionUrl, bitmap);
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

    public static boolean checkGPSEnable(Context context){
        try {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            LocationManager lm = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(isGPSEnabled || isNetworkEnabled){
                return true;
            }

            return false;

        }catch (Exception e){
            return false;
        }
    }

    public static Icon getIconMap(Context context, Drawable image, int width, int heigh){
        try {

            IconFactory mIconFactory = IconFactory.getInstance(context);
            //Drawable mIconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_whistle_12);
            image = WhistleUtils.resizeImage(context, image, width, heigh);
            Icon icon = mIconFactory.fromDrawable(image);
            return icon;

        }catch (Exception e){
            Log.e(LOG_CLASS, "getIconMap: " + e.getMessage());
            return null;
        }
    }

    public static Drawable resizeImage(Context context, Drawable image, int width, int heigh) {
        try {
            Bitmap b = ((BitmapDrawable)image).getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width, heigh, false);
            return new BitmapDrawable(context.getResources(), bitmapResized);
        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo resizeImage: " + e.getMessage());
            return image;
        }
    }

    public static String adjustNumber(String prefix, String number){
        try {

            String res = number.replace("-", "").replace("+", "").replace(" ", "");

            if(res.length() <= 9 ){
                res = prefix + res;
            }

            return res;

        } catch (Exception e) {
            System.err.println("Erro no metodo adjustNumber: " + e.getMessage());
            e.printStackTrace();
            return number;
        }
    }

}
