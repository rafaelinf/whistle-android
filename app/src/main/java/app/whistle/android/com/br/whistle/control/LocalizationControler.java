package app.whistle.android.com.br.whistle.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import app.whistle.android.com.br.whistle.database.WhistleBD;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import app.whistle.android.com.br.whistle.utils.WhistleJson;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import br.com.brns.whistle.protocol.vo.entity.ContactVO;
import br.com.brns.whistle.protocol.vo.entity.LocalizationVO;
import br.com.brns.whistle.protocol.vo.entity.UserVO;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;

/**
 * Created by rafael on 17/03/2016.
 */
public class LocalizationControler {

    private static final String LOG_CLASS = "LocalControler";

    private Context ctx;

    private SQLiteDatabase db;
    private WhistleBD whistleBD = null;

    public LocalizationControler(Context ctx){
        this.ctx = ctx;
        whistleBD = new WhistleBD(ctx);
    }

    public void save(double latitude, double longitude){
        try {

            User user = ControlerFactoryMethod.getUserControler(ctx).findUser();
            if(user != null){

                if(WhistleUtils.isOnline(ctx)){

                    UserVO userVO = WhistleUtils.buildUserVO(user);

                    BigDecimal lat = BigDecimal.ZERO;
                    lat = lat.setScale(7, BigDecimal.ROUND_UNNECESSARY);
                    lat = lat.add(new BigDecimal(Double.toString(latitude)));

                    BigDecimal lng = BigDecimal.ZERO;
                    lng = lng.setScale(7, BigDecimal.ROUND_UNNECESSARY);
                    lng = lng.add(new BigDecimal(Double.toString(longitude)));

                    LocalizationVO localizationJson = new LocalizationVO();
                    localizationJson.setLolat(lat);
                    localizationJson.setLolng(lng);
                    localizationJson.setUserowner(userVO);

                    Gson gson = new Gson();
                    String json = gson.toJson(localizationJson);
                    //Log.i(LOG_CLASS, "json localização = " + json);

                    String response = WhistleJson.sendPost("localization", "save", json);
                    //Log.i(LOG_CLASS, "response localização = " + response);

                    if(response != null) {

                        ReturnRSVO returnRSVO = gson.fromJson(response, ReturnRSVO.class);
                        if (returnRSVO != null && returnRSVO.getMsg().equals(TypeReturnRSVO.OK)) {
                            Log.i(LOG_CLASS, "A localização foi salva");
                        } else {
                            Log.i(LOG_CLASS, "Erro ao salvar localização: " + returnRSVO.getErrorRSVO().getMessage());
                        }
                    }

                }else{
                    Log.i(LOG_CLASS, "Sem acesso a internet");
                }

            }else{
                Log.i(LOG_CLASS, "O usuário não foi encontrado");
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo save: " + e.getMessage());
        }
    }

    public List<LocalizationVO> getLocalizationContacts(){
        try {

            List<LocalizationVO> lsLocalizationVOs = new ArrayList<>();

            User user = ControlerFactoryMethod.getUserControler(ctx).findUser();
            if(user != null){

                if(WhistleUtils.isOnline(ctx)){

                    //if(WhistleSingleton.getInstance().isServerConnect()){

                        UserVO userVO = WhistleUtils.buildUserVO(user);

                        GsonBuilder builder = new GsonBuilder();
                        // Register an adapter to manage the date types as long values
                        builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
                            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                return new Date(json.getAsJsonPrimitive().getAsLong());
                            }
                        });
                        Gson gson = builder.create();

                        String json = gson.toJson(userVO);
                        //Log.i(LOG_CLASS, "json getLocalizationContacts = " + json);

                        String response = WhistleJson.sendPost("localization", "findLocalizationVOByContact", json);
                        //Log.i(LOG_CLASS, "response getLocalizationContacts = " + response);

                        if(response != null){

                            Type listType = new TypeToken<ArrayList<LocalizationVO>>() {}.getType();
                            lsLocalizationVOs = gson.fromJson(response, listType);

/*                            if(lsLocalizationVOs != null && !lsLocalizationVOs.isEmpty()){

                                for (LocalizationVO l : lsLocalizationVOs){
                                    Log.i(LOG_CLASS, "Localização Contato: " + l.getUserowner().getUsname());
                                    Log.i(LOG_CLASS, "Localização latitude: " + l.getLolat());
                                    Log.i(LOG_CLASS, "Localização Contato: " + l.getLolng());
                                    Log.i(LOG_CLASS, "---------------------------------------------");
                                }

                            }else{
                                Log.i(LOG_CLASS, "Nenhum contato para listar");
                            }*/

                        }else{
                            Log.i(LOG_CLASS, "Não obteve resposta");
                        }

                    /*}else{
                        Log.i(LOG_CLASS, "O servidor não está conectado");
                    }*/

                }else{
                    Log.i(LOG_CLASS, "Sem acesso a internet");
                }
            }else{
                Log.i(LOG_CLASS, "O usuário não foi encontrado");
            }

            return lsLocalizationVOs;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo getLocalizationContacts: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public LocalizationVO findLocalizationVOByContactUnique(Contact contact){

        LocalizationVO  localizationVO = null;

        User user = ControlerFactoryMethod.getUserControler(ctx).findUser();
        if(user != null){

            if(WhistleUtils.isOnline(ctx)){

                //if(WhistleSingleton.getInstance().isServerConnect()){

                    UserVO userVO = WhistleUtils.buildUserVO(user);
                    ContactVO contactVO = new ContactVO();
                    contactVO.setUserVO(userVO);
                    contactVO.setConumber(contact.getNumber());

                    GsonBuilder builder = new GsonBuilder();
                    builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong());
                        }
                    });
                    Gson gson = builder.create();

                    String json = gson.toJson(contactVO);
                    Log.i(LOG_CLASS, "json findLocalizationVOByContactUnique = " + json);

                    String response = WhistleJson.sendPost("localization", "findLocalizationVOByContactUnique", json);
                    Log.i(LOG_CLASS, "response findLocalizationVOByContactUnique = " + response);

                    if(response != null){

                        localizationVO = gson.fromJson(response, LocalizationVO.class);
/*                        if(localizationVO != null){

                            Log.i(LOG_CLASS, "Localização Contato: " + localizationVO.getUserowner().getUsname());
                            Log.i(LOG_CLASS, "Localização latitude: " + localizationVO.getLolat());
                            Log.i(LOG_CLASS, "Localização Contato: " + localizationVO.getLolng());
                            Log.i(LOG_CLASS, "---------------------------------------------");

                        }else{
                            Log.i(LOG_CLASS, "Nenhum localizationVO para listar");
                        }*/

                    }else{
                        Log.i(LOG_CLASS, "Não obteve resposta");
                    }

                /*}else{
                    Log.i(LOG_CLASS, "O servidor não está conectado");
                }
*/
            }else{
                Log.i(LOG_CLASS, "Sem acesso a internet");
            }
        }else{
            Log.i(LOG_CLASS, "O usuário não foi encontrado");
        }

        return localizationVO;
    }

}
