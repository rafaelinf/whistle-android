package app.whistle.android.com.br.whistle.control;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.gson.*;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.ConsoleHandler;

import app.whistle.android.com.br.whistle.auxiliary.ContactMobileVO;
import app.whistle.android.com.br.whistle.auxiliary.JsonResponse;
import app.whistle.android.com.br.whistle.database.DatabaseManager;
import app.whistle.android.com.br.whistle.database.WhistleBD;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.utils.WhistleContact;
import app.whistle.android.com.br.whistle.utils.WhistleJson;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;
import br.com.brns.whistle.protocol.auxiliary.ContactAux;
import br.com.brns.whistle.protocol.vo.entity.*;
import br.com.brns.whistle.protocol.vo.rest.ReturnRSVO;
import br.com.brns.whistle.protocol.vo.rest.TypeReturnRSVO;

/**
 * Created by rafael on 04/03/2016.
 */
public class ContactControler {

    private static final String LOG_CLASS = "ContactControler";

    private Context ctx;
    private SQLiteDatabase database = null;

    public ContactControler(Context ctx){
        this.ctx = ctx;
        DatabaseManager.initializeInstance(new WhistleBD(ctx));
        database = DatabaseManager.getInstance().getDatabase();
    }

    public void saveAllBD(UserVO userVO){
        try {

            int count = 0;

            List<ContactMobileVO> lsContactDS = WhistleContact.readContact(ctx, userVO);
            if(lsContactDS != null && !lsContactDS.isEmpty()){

                for (ContactMobileVO ct: lsContactDS) {

                    int countContact = countContactByNumber(ct.getNumber());
                    if(countContact == 0){
                        save(ct);
                    }

                }

            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo saveAll: " + e.getMessage());
        }
    }

    public void threadSaveAllWS(UserVO userVO, Contact c, String contactIdPhone){
        try {

            ContactVO ct = new ContactVO();
            ct.setUserVO(userVO);
            ct.setConame(c.getName());
            ct.setConumber(c.getNumber());

            Gson gson = new Gson();
            String jsonPost = gson.toJson(ct);

            JsonResponse jsonResponse = WhistleJson.sendPost___("contact", "saveContact", jsonPost);

            //HttpURLConnection response = WhistleJson.sendPost_("contact", "saveContact", jsonPost);
            //Log.i(LOG_CLASS, "response.getResponseCode()" + response.getResponseCode());

            if(jsonResponse.getStatus() == 200){

                Contact contactExist = findContactByNumber(ct.getConumber());
                if(contactExist != null) {

                    if(jsonResponse.getJsonString() != null && !jsonResponse.getJsonString().equals("")){

                    }

                    ContactVO contactVO = gson.fromJson(jsonResponse.getJsonString(), ContactVO.class);
                    if(contactVO != null) {

                        try {

                            ContentValues values = new ContentValues();
                            values.put("name", contactVO.getConame());
                            values.put("number", contactVO.getConumber());
                            values.put("sharelocation", contactVO.isCosharelocation());
                            values.put("allowtrace", contactVO.isCoallowtrace());
                            values.put("status", contactVO.getCostatus());
                            values.put("version", contactVO.getCoversion());

                            if (contactIdPhone != null) {
                                values.put("contactidphone", contactIdPhone);
                            }

                            if (database.update(Contact.TABLE_NAME, values, "_id=" + contactExist.getId(), null) > 0) {
                                Log.i(LOG_CLASS, "refreshData OK: " + contactExist.getId());
                            } else {
                                Log.i(LOG_CLASS, "refreshData ERRO : " + contactExist.getId());
                            }

                        } catch (Exception e) {
                            Log.e(LOG_CLASS, "Erro no metodo saveAllWS no BD:" + e.getMessage());
                        }

                        if (contactVO.getCostatus() == ContactAux.STATUS_ACTIVE) {
                            getImageProfile(contactVO.getConumber());
                            Log.e(LOG_CLASS, "Imagem: " + c.getName());
                        }

                    }else{
                        Log.e(LOG_CLASS, "Contato não foi encontrado...");
                    }

                }

            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo threadSaveAllWS: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean checkUpdateContacts(UserVO userVO){
        try {

            Log.i(LOG_CLASS, "controle checkUpdateContacts...");

            int countRows = 10;
            int skipRows = 0;
            boolean exec = true;

            while (exec) {

                List<Contact> lsContacts = findContactByAll(countRows, skipRows);
                if (lsContacts != null && !lsContacts.isEmpty()) {

                    for (Contact c : lsContacts) {

                        String response = WhistleJson.makeRequest("contact/getContactNumber/" + userVO.getUsidentification() + "/" + c.getNumber());
                        //Log.i(LOG_CLASS, "checkUpdateContacts response = " + response);

                        if (response != null && !response.equals("")) {

                            GsonBuilder builder = new GsonBuilder();
                            builder.registerTypeAdapter(Date.class, new JsonDeserializer() {
                                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                                    return new Date(json.getAsJsonPrimitive().getAsLong());
                                }
                            });

                            Gson gson = builder.create();
                            ContactVO contactVO = gson.fromJson(response, ContactVO.class);
                            if (contactVO != null) {

                                if (c.getVersion() != contactVO.getCoversion()) {

                                    try {

                                        ContentValues values = new ContentValues();
                                        values.put("name", contactVO.getConame());
                                        values.put("sharelocation", WhistleUtils.booleanToInt(contactVO.isCosharelocation()));
                                        values.put("allowtrace", WhistleUtils.booleanToInt(contactVO.isCoallowtrace()));
                                        values.put("status", contactVO.getCostatus());
                                        values.put("dtupdate", WhistleUtils.dateParseTmzFormat(contactVO.getCodtupdate()));
                                        values.put("version", contactVO.getCoversion());

                                        if (database.update(Contact.TABLE_NAME, values, "_id=" + c.getId(), null) > 0) {
                                            Log.i(LOG_CLASS, "checkUpdateContacts OK: " + c.getId());
                                        } else {
                                            Log.i(LOG_CLASS, "checkUpdateContacts ERRO : " + c.getId());
                                        }

                                    } catch (Exception e) {
                                        Log.e(LOG_CLASS, "Erro no metodo refreshData salvar bd:" + e.getMessage());
                                    }

                                }

                                final String number = c.getNumber();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        getImageProfile(number);
                                    }
                                }).start();

                            } else {
                                Log.i(LOG_CLASS, "checkUpdateContacts... contato não foi encontrado JSON");
                            }

                        }

                    }

                }else{
                    exec = false;
                }

                skipRows += countRows;

            }

            return true;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo refreshData: " + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    public boolean editShareLocation(Contact contact, boolean shareLocation){
        try {

            if(contact != null){

                User user = ControlerFactoryMethod.getUserControler(ctx).findUser();
                UserVO userVO = WhistleUtils.buildUserVO(user);

                ContactVO contactVO = new ContactVO();
                contactVO.setUserVO(userVO);
                contactVO.setCosharelocation(shareLocation);
                contactVO.setConumber(contact.getNumber());

                Gson gson = new Gson();
                String json = gson.toJson(contactVO);

                String response = WhistleJson.sendPost("contact", "editShareLocation", json);
                if(response != null && !response.equals("")) {

                    ReturnRSVO returnRSVO = gson.fromJson(response, ReturnRSVO.class);
                    if (returnRSVO != null && returnRSVO.getMsg().equals(TypeReturnRSVO.OK)) {

                        try {
                            //db = whistleBD.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("sharelocation", shareLocation);
                            database.update(Contact.TABLE_NAME, values, "number=" + contact.getNumber(), null);
                        }catch (Exception e){
                        }

                        //refreshData(contact);
                        return shareLocation;

                    }
                }
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo editShareLocation: " + e.getMessage());
            e.printStackTrace();
        }

        return contact.isSharelocation();
    }

    public boolean editAllowtrace(Contact contact, boolean allowtrace){
        try {

            if(contact != null){

                User user = ControlerFactoryMethod.getUserControler(ctx).findUser();
                UserVO userVO = WhistleUtils.buildUserVO(user);

                ContactVO contactVO = new ContactVO();
                contactVO.setUserVO(userVO);
                contactVO.setCoallowtrace(allowtrace);

                Gson gson = new Gson();
                String json = gson.toJson(contactVO);

                String response = WhistleJson.sendPost("contact", "editAllowtrace", json);

                if(response != null && !response.equals("")) {

                    ReturnRSVO returnRSVO = gson.fromJson(response, ReturnRSVO.class);
                    if (returnRSVO != null && returnRSVO.getMsg().equals(TypeReturnRSVO.OK)) {

                        try {
                            //db = whistleBD.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("allowtrace", allowtrace);
                            database.update(Contact.TABLE_NAME, values, "number=" + contact.getNumber(), null);
                            return allowtrace;
                        }catch (Exception e){
                        }
                    }
                }
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo editAllowtrace: " + e.getMessage());
        }

        return contact.isAllowtrace();
    }


    public String getImageProfile(String number){
        try {

            String jsonString = WhistleJson.makeRequest("contact/getImageProfile/" + number);
            Log.i(LOG_CLASS, "getImageProfile response = " + jsonString);

            if(jsonString != null && !jsonString.equals("")) {

                Gson gson = new Gson();
                ImageUploadVO img = gson.fromJson(jsonString, ImageUploadVO.class);

                if (img != null) {
                    Log.i(LOG_CLASS, "Imagem OK");

                    String url = WhistleUtils.saveImage(img.getImgArray(), "imgContact" + number);
                    Log.i(LOG_CLASS, "url = " + url);

                    if(url != null){

                        try {
                            //db = whistleBD.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("urlimage", url);
                            database.update(Contact.TABLE_NAME, values, "number=" + number, null);
                        }catch (Exception e){
                            return null;
                        }

                        return url;

                    }

                    return null;

                }else{
                    Log.i(LOG_CLASS, "Imagem NULL");
                }

            }

            return null;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo getImageProfile: " + e.getMessage());
            return null;
        }
    }

    public Contact findContactById(int id) {
        Cursor cursor = null;
        Contact contact = null;
        try{

            //db = whistleBD.getReadableDatabase();

            String where = "_id = ?";
            String argumentos[] = new String[] { String.valueOf(id) };

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, where, argumentos, null, null, null);
            if(cursor != null && cursor.moveToFirst() ){
                contact = convertToContact(cursor);
            }

            return contact;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findProductByAll - " + e.getMessage());
            return null;

        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public Contact findContactByNumber(String number) {
        Cursor cursor = null;
        Contact contact = null;
        try{

            //db = whistleBD.getReadableDatabase();

            String where = "number = ?";
            String argumentos[] = new String[] { number };

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, where, argumentos, null, null, null);
            if(cursor != null && cursor.moveToFirst() ){
                contact = convertToContact(cursor);
            }

            return contact;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByNumber - " + e.getMessage());
            return null;

        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public Contact findContactByIdPhone(String idphone) {
        Cursor cursor = null;
        Contact contact = null;
        try{

            String where = "contactidphone = ?";
            String argumentos[] = new String[] { idphone };

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, where, argumentos, null, null, null);
            if(cursor != null && cursor.moveToFirst() ){
                contact = convertToContact(cursor);
            }

            return contact;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByIdPhone - " + e.getMessage());
            e.printStackTrace();
            return null;

        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public int countContactByNumber(String number) {
        Cursor cursor = null;
        int count = -1;
        try{

            //db = whistleBD.getReadableDatabase();

            cursor = database.rawQuery("select count(*) from " + Contact.TABLE_NAME + " where number = '" + number + "'", null);
            if(cursor != null){
                cursor.moveToFirst();
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;

        }catch (Exception e){
            Log.e(LOG_CLASS, "countContactByNumber - " + e.getMessage());
            return -1;
        } finally {
            if (cursor != null){
                cursor.close();
            }
        }
    }

    public List<Contact> findContactByAll() {
        Cursor cursor = null;
        List<Contact> lsContacts = new ArrayList<>();
        try{

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, null, null, null, null, "name ASC");
            if(cursor != null){
                cursor.moveToFirst();
                lsContacts = convertToContactList(cursor);
            }

            return lsContacts;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByAll - " + e.getMessage());
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public int countContactByAll() {
        Cursor cursor = null;
        int count = 0;
        try{

            cursor = database.rawQuery("select count(*) from " + Contact.TABLE_NAME, null);
            if(cursor != null){
                cursor.moveToFirst();
                count = cursor.getInt(0);
                cursor.close();
            }

            return count;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByAll - " + e.getMessage());
            return -1;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }


    public List<Contact> findContactByAll(int count, int skip) {
        Cursor cursor = null;
        List<Contact> lsContacts = new ArrayList<>();
        try{

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, null, null, null, null, "name ASC LIMIT " + count + " OFFSET " + skip);
            if(cursor != null){
                cursor.moveToFirst();
                lsContacts = convertToContactList(cursor);
            }

            return lsContacts;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByAll - " + e.getMessage());
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public List<Contact> findContactByActive() {
        Cursor cursor = null;
        List<Contact> lsContacts = new ArrayList<>();
        try{

            String where = "status = " + ContactAux.STATUS_ACTIVE;

            cursor = database.query(Contact.TABLE_NAME, Contact.COLUMNS, where, null, null, null, "name ASC");
            if(cursor != null){
                cursor.moveToFirst();
                lsContacts = convertToContactList(cursor);
            }

            return lsContacts;

        }catch (Exception e){
            Log.e(LOG_CLASS, "findContactByAll - " + e.getMessage());
            return null;
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public boolean save(ContactMobileVO ct){
        try {

            ContentValues values = new ContentValues();
            values.put("name", ct.getName());
            values.put("number", ct.getNumber());
            values.put("sharelocation", WhistleUtils.booleanToInt(false));
            values.put("allowtrace", WhistleUtils.booleanToInt(false));
            values.put("status", ContactAux.STATUS_INACTIVE);
            values.put("dtupdate", WhistleUtils.dateParseTmzFormat(new Date()));
            values.put("dtcreate", WhistleUtils.dateParseTmzFormat(new Date()));
            //values.put("version", ct.getCoversion());
            values.put("contactidphone", ct.getId());
            return database.insert(Contact.TABLE_NAME, null, values) > 0;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro Save - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean remove(Contact contact){
        try {

            if(contact != null){
                return database.delete(Contact.TABLE_NAME, "_id=" + contact.getId(), null) > 0;
            }else{
                return false;
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro Save - " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Contact> convertToContactList(Cursor cursor){
        List<Contact> lsContacts = new ArrayList<>();
        try {

            if(cursor != null){

                while (cursor.isAfterLast() == false) {
                    Contact contact = convertToContact(cursor);
                    if(contact != null){
                        lsContacts.add(contact);
                    }
                    cursor.moveToNext();
                }

            }else{
                Log.i(LOG_CLASS, "convertToContactList - Cursor = null");
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "convertToContactList - " + e.getMessage());
        }

        return lsContacts;

    }

    public Contact convertToContact(Cursor cursor){
        try {

            if(cursor != null){

                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setNumber(cursor.getString(2));
                contact.setLat(cursor.getString(3));
                contact.setLng(cursor.getString(4));
                contact.setSharelocation(WhistleUtils.intToBoolean(Integer.parseInt(cursor.getString(5))));
                contact.setAllowtrace(WhistleUtils.intToBoolean(Integer.parseInt(cursor.getString(6))));
                contact.setStatus(Integer.parseInt(cursor.getString(7)));
                contact.setUrlimage(cursor.getString(8));
                contact.setDtupdate(WhistleUtils.dateParseTmz(cursor.getString(9)));
                contact.setDtcreate(WhistleUtils.dateParseTmz(cursor.getString(10)));
                contact.setVersion(cursor.getInt(11));
                contact.setContactidphone(cursor.getString(12));
                return contact;

            }else{
                Log.i(LOG_CLASS, "convertToContact - Cursor = null");
                return null;
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no convertToContact - " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
