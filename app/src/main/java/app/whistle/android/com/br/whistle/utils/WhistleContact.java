package app.whistle.android.com.br.whistle.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.whistle.android.com.br.whistle.auxiliary.ContactMobileVO;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;
import br.com.brns.whistle.protocol.auxiliary.ContactAux;
import br.com.brns.whistle.protocol.vo.entity.ContactVO;
import br.com.brns.whistle.protocol.vo.entity.UserVO;

/**
 * Created by rafael on 27/01/2016.
 */
public class WhistleContact {

    private static final String LOG_CLASS = "WhistleContact";

    public static List<ContactMobileVO> readContact(Context context, UserVO userVO){
        List<ContactMobileVO> lsContactVOs = new ArrayList<>();

        try {

            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            ContactMobileVO c = new ContactMobileVO(id, name, phoneNo);
                            lsContactVOs.add(c);

                        }

                        pCur.close();
                    }
                }
            }

            return lsContactVOs;

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo readContact: " + e.getMessage());
            return null;
        }
    }

    public static int countContact(Context context){
        Set<String> lsContactVOs = new HashSet<>();

        try {

            ContentResolver cr = context.getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

            if (cur.getCount() > 0) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
                                new String[]{id}, null);

                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            lsContactVOs.add(phoneNo);
                        }

                        pCur.close();
                    }
                }
            }

            return lsContactVOs.size();

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo countContact: " + e.getMessage());
            return -1;
        }
    }

}
