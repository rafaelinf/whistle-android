package app.whistle.android.com.br.whistle.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;
import com.brns.whistle.backend.protocol.vo.entity.UserVO;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import app.whistle.android.com.br.whistle.auxiliary.ContactMobileVO;

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

                            Log.i(LOG_CLASS, "phoneNo = " + phoneNo);

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
