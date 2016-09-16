package app.whistle.android.com.br.whistle.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import app.whistle.android.com.br.whistle.R;
import app.whistle.android.com.br.whistle.control.ControlerFactoryMethod;
import app.whistle.android.com.br.whistle.entity.Contact;
import app.whistle.android.com.br.whistle.singleton.WhistleSingleton;

/**
 * Created by rafael on 07/02/2016.
 */
public class ContactListAdapter extends BaseAdapter {

    private Activity activity;
    private List<Contact> lsContacts = new ArrayList<>();
    private static LayoutInflater inflater=null;

    public ContactListAdapter(Activity a, List<Contact> lsContacts) {
        activity = a;
        this.lsContacts = lsContacts;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return lsContacts.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        try {

            View vi=convertView;
            if(convertView == null)
                vi = inflater.inflate(R.layout.list_contact, null);

            TextView title = (TextView)vi.findViewById(R.id.title); // title
            TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
            TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
            ImageView thumb_image = (ImageView)vi.findViewById(R.id.list_image); // thumb image
            ImageView imgSharelocation = (ImageView)vi.findViewById(R.id.imgSharelocation); // thumb image
            ImageView imgAllowtrace = (ImageView)vi.findViewById(R.id.imgAllowtrace); // thumb image

            Contact c = lsContacts.get(position);
            Log.i("getView", c.getName());

            // Setting all values in listview
            title.setText(c.getName());
            artist.setText(c.getNumber());
            duration.setText("");

            if(c.getUrlimage() != null && !c.getUrlimage().equals("")){
                Picasso.with(vi.getContext()).load(new File(c.getUrlimage()))
                        .error(R.drawable.ic_whistle_gray_24)
                        .into(thumb_image);
            }else{

                String uri = "@drawable/ic_whistle_gray_24";  // where myresource (without the extension) is the file
                int imageResource = vi.getResources().getIdentifier(uri, null, activity.getPackageName());

                Picasso.with(vi.getContext()).load(imageResource)
                        .error(R.drawable.ic_whistle_gray_24)
                        .into(thumb_image);
            }

            if(c.isSharelocation()){
                Picasso.with(vi.getContext()).load(R.drawable.ic_compass_24)
                        .error(R.drawable.ic_compass_gray_24)
                        .into(imgSharelocation);
            }else{

                String uri = "@drawable/ic_compass_gray_24";  // where myresource (without the extension) is the file
                int imageResource = vi.getResources().getIdentifier(uri, null, activity.getPackageName());

                Picasso.with(vi.getContext()).load(imageResource)
                        .error(R.drawable.ic_compass_gray_24)
                        .into(imgSharelocation);
            }

            if(c.isAllowtrace()){
                Picasso.with(vi.getContext()).load(R.drawable.ic_map_24)
                        .error(R.drawable.ic_map_gray_24)
                        .into(imgAllowtrace);
            }else{

                String uri = "@drawable/ic_map_gray_24";  // where myresource (without the extension) is the file
                int imageResource = vi.getResources().getIdentifier(uri, null, activity.getPackageName());

                Picasso.with(vi.getContext()).load(imageResource)
                        .error(R.drawable.ic_map_gray_24)
                        .into(imgAllowtrace);
            }
            return vi;

        }catch (Exception e){
            Log.e("AdapterProd", "Erro no metodo getView: " + e.getMessage());
        }

        return null;

    }
}
