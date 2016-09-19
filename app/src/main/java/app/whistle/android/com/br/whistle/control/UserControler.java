package app.whistle.android.com.br.whistle.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.brns.whistle.backend.protocol.vo.entity.*;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.util.Date;
import app.whistle.android.com.br.whistle.auxiliary.JsonResponse;
import app.whistle.android.com.br.whistle.database.WhistleBD;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.utils.WhistleImage;
import app.whistle.android.com.br.whistle.utils.WhistleJson;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;

/**
 * Created by rafael on 02/12/2015.
 */
public class UserControler {

    private static final String LOG_CLASS = "UserControler";

    private SQLiteDatabase db;
    private WhistleBD whistleBD = null;

    private Context context;

    public UserControler(Context ctx){
        whistleBD = new WhistleBD(ctx);
        this.context = context;
    }

    public boolean registrationRequest(String identification, String codeCountry, String prefix, String name, String number, String email){
        try {

            String registrationCode = WhistleUtils.generateValueAlphaNumber(4);

            JsonResponse jsonResponse = WhistleJson.makeRequest("user/registrationRequest/" + registrationCode + "/" + identification + "/" + codeCountry + "/" + prefix + "/" + number + "/" + name + "/" + email);
            if(jsonResponse.getStatus() == 200){

                User user = new User();
                user.setIdentification(identification);
                user.setName(name);
                user.setCodecountry(codeCountry);
                user.setPrefix(prefix);
                user.setNumber(number);
                user.setEmail(email);
                user.setDtcreate(new Date());
                user.setRegistrationcode(registrationCode);

                boolean success = save(user);
                if(success){
                    return true;
                }
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "registrationRequest - " + e.getMessage());
        }

        return false;
    }

    public boolean confirmRegistration(User user, String registrationcode){
        try {

            JsonResponse jsonResponse = WhistleJson.makeRequest("user/confirmRegistration/" + registrationcode + "/" + user.getIdentification());
            if(jsonResponse.getStatus() == 200){

                User userConfirm = findUser();
                if(userConfirm != null){

                    userConfirm.setDtactive(new Date());
                    userConfirm.setStatus(100);

                    boolean success = edit(userConfirm);
                    if(success){

                        Log.i(LOG_CLASS, "Usuário ativado com sucesso.");
                        return true;

                    }else{
                        Toast.makeText(WhistleSingleton.getInstance().getContext(), "Erro ao ativar usuário", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_CLASS, "Erro ao ativar usuário.");
                    }

                }else{
                    Log.i(LOG_CLASS, "Usuário não encontrado");
                }

            }else{
                Log.i(LOG_CLASS, "Erro no metodo response.getResponseCode()");
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo confirmRegistration: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean editImageProfile(Bitmap bitmap){
        try {

            if(bitmap != null){

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 80, stream);
                byte[] imgByteArray = stream.toByteArray();
                String imgArray = Base64.encodeToString(imgByteArray, Base64.DEFAULT);

                User user = findUser();
                UserVO userVO = WhistleUtils.buildUserVO(user);
                ImageUploadVO imageUploadVO = new ImageUploadVO(userVO, imgArray, 0, 0);

                Gson gson = new Gson();
                String json = gson.toJson(imageUploadVO);

                JsonResponse jsonResponse = WhistleJson.sendPost("user", "editImageProfile", json);
                if(jsonResponse.getStatus() == 200){

                    WhistleImage whistleImage = new WhistleImage(WhistleSingleton.getInstance().getContext());
                    String url = whistleImage.saveImage(bitmap, "imgUserProfile");

                    try {
                        db = whistleBD.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.put("urlImageProfile", url);
                        db.update(User.TABLE_NAME, values, "_id=" + user.getId(), null);
                    }catch (Exception e){
                        return false;
                    }

                    return true;

                }else {
                    return false;
                }

            }else{
                Log.i(LOG_CLASS, "A imagem é obrigatória");
                return false;
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo editImageProfile: " + e.getMessage());
            return false;
        }
    }

    public boolean save(User user){
        try {
            db = whistleBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("identification", user.getIdentification());
            values.put("codecountry", user.getCodecountry());
            values.put("prefix", user.getPrefix());
            values.put("number", user.getNumber());
            values.put("name", user.getName());
            values.put("email", user.getEmail());
            //values.put("statusmsg", user.getStatusmsg());
            //values.put("status", user.getStatus());
            values.put("dtcreate", WhistleUtils.dateParseTmzFormat(user.getDtcreate()));
            //values.put("dtlastlogin", WhistleUtils.dateIso8601Format(new Date()));
            values.put("registrationcode", user.getRegistrationcode());
            //values.put("dtactive", WhistleUtils.dateIso8601Format(new Date()));
            return db.insert(User.TABLE_NAME, null, values) > 0;
        }catch (Exception e){
            Log.e(LOG_CLASS, "Save - " + e.getMessage());
            return false;
        }
    }

    public boolean edit(User user){
        try {
            db = whistleBD.getWritableDatabase();
            ContentValues values = new ContentValues();
            //values.put("identification", user.getIdentification());
            //values.put("number", user.getNumber());
            //values.put("name", user.getName());
            //values.put("email", user.getEmail());
            //values.put("statusmsg", user.getStatusmsg());
            values.put("status", user.getStatus());
            //values.put("dtcreate", WhistleUtils.dateIso8601Format(user.getDtcreate()));
            //values.put("dtlastlogin", WhistleUtils.dateIso8601Format(new Date()));
            //values.put("registrationcode", user.getRegistrationcode());
            values.put("dtactive", WhistleUtils.dateParseTmzFormat(user.getDtactive()));
            return db.update(User.TABLE_NAME, values, "_id=" + user.getId(), null) > 0;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Save - " + e.getMessage());
            return false;
        }
    }

    public User findUser() {
        Cursor cursor = null;
        try{

            db = whistleBD.getReadableDatabase();

            //String where = "LOGIN = ?";
            //String argumentos[] = new String[] { userLogin};

            cursor = db.query(User.TABLE_NAME, User.COLUMNS, null, null, null, null, null);
            User user = convertToUser(cursor);
            return user;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findUser - " + e.getMessage());
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public User convertToUser(Cursor cursor){
        try {

            if(cursor != null){

                if(cursor.moveToFirst()){

                    User user = new User();
                    user.setId(Integer.parseInt(cursor.getString(0)));
                    user.setIdentification(cursor.getString(1));
                    user.setCodecountry(cursor.getString(2));
                    user.setPrefix(cursor.getString(3));
                    user.setNumber(cursor.getString(4));
                    user.setName(cursor.getString(5));
                    user.setEmail(cursor.getString(6));
                    user.setStatusmsg(cursor.getString(7));
                    //user.setStatus(WhistleUtils.intToParse(cursor.getString(8)));
                    user.setDtcreate(WhistleUtils.dateParseTmz(cursor.getString(9)));
                    user.setLastlogin(WhistleUtils.dateParseTmz(cursor.getString(10)));
                    user.setRegistrationcode(cursor.getString(11));
                    user.setUrlImageProfile(cursor.getString(12));
                    user.setDtactive(WhistleUtils.dateParseTmz(cursor.getString(13)));
                    return user;

                }else{
                    Log.i(LOG_CLASS, "ConvertToUser - cursor.moveToFirst()");
                    return null;
                }

            }else{
                Log.i(LOG_CLASS, "ConvertToUser - Cursor = null");
                return null;
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "ConvertToUser - " + e.getMessage());
            return null;
        }
    }

}
