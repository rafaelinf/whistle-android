package app.whistle.android.com.br.whistle.utils;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.brns.whistle.backend.protocol.vo.entity.*;
import java.util.ArrayList;
import java.util.List;
import app.whistle.android.com.br.whistle.auxiliary.ContactMobileVO;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;

/**
 * Created by rafael on 27/03/2016.
 */
public class ContactObserver extends ContentObserver {

    private static final String TAG = "MngContactService";


    private Context context;
    private Worker worker;

    public ContactObserver(Handler handler,  Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        Log.i("ContactObserver", "ContactObserver change");

        if(worker == null || !worker.isAlive()){
            worker = new Worker();
            worker.start();
        }
    }

    public class Worker extends Thread{

        @Override
        public void run() {

            Log.i("ContactObserver", "Service running - Contatos");

            try {

                User user = ControlerFactoryMethod.getUserControler(context).findUser();
                if(user != null) {

                    UserVO userVO = WhistleUtils.buildUserVO(user);
                    if (userVO != null) {

                        int countDB = ControlerFactoryMethod.getContactControler(context).countContactByAll();
                        int countMobile = WhistleContact.countContact(context);

                        if(countDB < countMobile){
                            newContact(userVO);

                        }else if(countDB > countMobile){
                            removeContact(userVO);
                        }

                    }

                }

            }catch (Exception e){
                Log.e("ContactObserver", "Erro no run Worker: " + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    public void newContact(UserVO userVO){
        try {

            Log.i("ContactObserver", "NOVO CONTATO...");

            List<ContactMobileVO> lsContactsAdd = new ArrayList<>();

            for (Contact cN : ControlerFactoryMethod.getContactControler(context).findContactByAll()) {
                Contact c = ControlerFactoryMethod.getContactControler(context).findContactByIdPhone(cN.getContactidphone());
                if (c == null) {

                    Log.i("ContactObserver", "NOVO CONTATO: " + c.getName() + " -- " + c.getNumber());

                }
            }


/*            List<ContactMobileVO> lsContactsAdd = new ArrayList<>();
            List<ContactMobileVO> lsContactDS = WhistleContact.readContact(context, userVO);

            if(lsContactDS != null && !lsContactDS.isEmpty()) {

                for (ContactMobileVO ct : lsContactDS) {

                    String number = WhistleUtils.adjustNumber(userVO.getUsprefix(), ct.getNumber());
                    Contact contact = ControlerFactoryMethod.getContactControler(context).findContactByNumber(number);
                    if (contact == null) {
                        lsContactsAdd.add(ct);
                    }

                }

                for (ContactMobileVO ct : lsContactsAdd) {

                    Log.i("ContactObserver", "NOVO CONTATO: " + ct.getName() + " -- " + ct.getNumber());
                    ControlerFactoryMethod.getContactControler(context).save(ct);
                    Contact contact = ControlerFactoryMethod.getContactControler(context).findContactByNumber(ct.getNumber());
                    if(contact != null){
                        ControlerFactoryMethod.getContactControler(context).threadSaveAllWS(userVO, contact, ct.getId());
                    }

                }

            }*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void removeContact(UserVO userVO){
        try {

            Log.i("ContactObserver", "REMOVER CONTATO...");
            Contact contact = ControlerFactoryMethod.getContactControler(context).findContactByNumber("911112222");
            ControlerFactoryMethod.getContactControler(context).remove(contact);

/*
            List<ContactMobileVO> lsContactsAdd = new ArrayList<>();
            List<ContactMobileVO> lsContactDS = WhistleContact.readContact(context, userVO);

            if(lsContactDS != null && !lsContactDS.isEmpty()) {

                for (ContactMobileVO ct : lsContactDS) {

                    String number = WhistleUtils.adjustNumber(userVO.getUsprefix(), ct.getNumber());
                    Contact contact = ControlerFactoryMethod.getContactControler(context).findContactByNumber(number);
                    if (contact == null) {
                        lsContactsAdd.add(ct);
                    }

                }

                for (ContactMobileVO ct : lsContactsAdd) {

                    Log.i("ContactObserver", "NOVO CONTATO: " + ct.getName() + " -- " + ct.getNumber());
                    ControlerFactoryMethod.getContactControler(context).save(ct);
                    Contact contact = ControlerFactoryMethod.getContactControler(context).findContactByNumber(ct.getNumber());
                    if(contact != null){
                        ControlerFactoryMethod.getContactControler(context).threadSaveAllWS(userVO, contact, ct.getId());
                    }

                }

            }
*/

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
