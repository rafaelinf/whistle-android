package app.whistle.android.com.br.whistle.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.brns.whistle.backend.protocol.vo.entity.UserVO;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.entity.User;
import app.whistle.android.com.br.whistle.thread.ThreadPool;
import app.whistle.android.com.br.whistle.utils.WhistleUtils;

/**
 * Created by rafael on 07/04/2016.
 */
public class SetupActivity extends AppCompatActivity {

    private static final String LOG_CLASS = "SetupActivity";

    private ThreadPool threadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lyt_setup);

        syncContacts();
    }

    public void syncContacts(){
        try {

            User user = ControlerFactoryMethod.getUserControler(this).findUser();
            if(user != null) {

                UserVO userVO = WhistleUtils.buildUserVO(user);
                if (userVO != null) {

                    int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
                    threadPool = new ThreadPool(
                            NUMBER_OF_CORES * 2,
                            NUMBER_OF_CORES * 2,
                            60L,
                            TimeUnit.SECONDS,
                            new LinkedBlockingQueue<Runnable>()
                    );

                    SynchronizeContacts synchronizeContacts = new SynchronizeContacts(this, userVO);
                    synchronizeContacts.execute();

                }else{
                    Log.i(LOG_CLASS, "Não foi possível converter entidade user para VO");
                }

            }else{
                Log.i(LOG_CLASS, "O usuário não foi encontrado");
            }

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo syncContacts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    class SynchronizeContacts extends AsyncTask<Void, Void, Void> {

        private ProgressDialog progressDialog;
        private Context context;
        private UserVO userVO;

        public SynchronizeContacts(Context context, UserVO userVO) {
            this.context = context;
            this.userVO = userVO;
        }

        @Override
        protected void onPreExecute(){
            progressDialog = ProgressDialog.show(context, context.getString(R.string.app_name), context.getString(R.string.msgRefreshContacts), true);
        }

        @Override
        protected Void doInBackground(Void... params) {

            //Salvar contatos no banco local
            ControlerFactoryMethod.getContactControler(context).saveAllBD(userVO);

            //Salvar contatos no banco WEB
            int countRows = 10;
            int skipRows = 0;
            boolean exec = true;

            while (exec){

                List<Contact> lsContacts = ControlerFactoryMethod.getContactControler(context).findContactByAll(countRows, skipRows);
                if(lsContacts != null && !lsContacts.isEmpty()){

                    for (Contact c: lsContacts) {
                        threadPool.execute(new SynchronizeContactsRunnable(context, userVO, c));
                    }

                }else{
                    exec = false;
                }

                skipRows += countRows;

            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }

            boolean syncProgress = true;

            while (syncProgress){

                int countThreadsProgress = threadPool.getQueue().size();
                if(countThreadsProgress == 0){
                    syncProgress = false;
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                }

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.cancel();

            Intent i = new Intent(SetupActivity.this, SplashScreen.class);
            startActivity(i);
            finish();
        }

    }

    class SynchronizeContactsRunnable implements Runnable {

        private Context context;
        private UserVO userVO;
        private Contact contact;

        public SynchronizeContactsRunnable(Context context, UserVO userVO, Contact contact){
            this.context = context;
            this.userVO = userVO;
            this.contact = contact;
        }

        @Override
        public void run() {
            ControlerFactoryMethod.getContactControler(context).threadSaveAllWS(userVO, contact, contact.getContactidphone());
        }
    }

}
