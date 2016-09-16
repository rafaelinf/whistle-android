package app.whistle.android.com.br.whistle.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.thread.RefreshDataContact;
import br.com.brns.whistle.protocol.vo.entity.LocalizationVO;

/**
 * Created by rafael on 09/03/2016.
 */
public class ContactFragment extends Fragment {
    public static final String ARG_PAGE = "ARG_PAGE";

    private static final String LOG_CLASS = "ContactFragment";

    private AdapterView.AdapterContextMenuInfo infoMenuContext;
    private Handler handler;
    private List<Contact> lsContacts = new ArrayList<>();
    Contact contact;

    ListView list;
    ContactListAdapter adapter;

    //private int mPage;

    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        initComponents(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(contact != null){
            initComponents(getView());
        }
    }

    public void initComponents(View view) {
        try {

            handler = new Handler();

            lsContacts = ControlerFactoryMethod.getContactControler(getContext()).findContactByActive();

            list = (ListView) view.findViewById(R.id.list_contact);
            adapter = new ContactListAdapter(getActivity(), lsContacts);
            list.setAdapter(adapter);

            list.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    contact = lsContacts.get(position);
                    Log.i(LOG_CLASS, "Selecionando contato: " + contact.getName());

                }
            });

            registerForContextMenu(list);

        } catch (Exception e) {
            Log.e(LOG_CLASS, "Erro no initComponents: " + e.getMessage());
        }
    }

    private void refreshContacts(){
        try {

            RefreshDataContact refreshDataContact = new RefreshDataContact(getContext(), this);
            refreshDataContact.execute();

        }catch (Exception e){
            Log.e(LOG_CLASS, "Erro no metodo refreshContacts: " + e.getMessage());
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_contact, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_refresh) {
            refreshContacts();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_context_contact, menu);

        MenuItem item = menu.findItem(R.id.action_navigation);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        contact = lsContacts.get(info.position);

        if(contact != null){
            if(contact.isAllowtrace() == false){
                item.setVisible(false);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Contact contactSelected = lsContacts.get(info.position);
        switch (item.getItemId()) {
            case R.id.action_detail:
                Intent i = new Intent(getActivity(), ContactActivity.class);
                i.putExtra("idcontact", contactSelected.getId());
                startActivity(i);
                return true;

            case R.id.action_navigation:

                LocalizationVO l = ControlerFactoryMethod.getLocalizationControler(getContext()).findLocalizationVOByContactUnique(contactSelected);
                if(l != null){
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + l.getLolat().toString() + "," + l.getLolng().toString());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else{
                    Toast.makeText(getActivity(), getString(R.string.msgCouldNotFindContact), Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

}
