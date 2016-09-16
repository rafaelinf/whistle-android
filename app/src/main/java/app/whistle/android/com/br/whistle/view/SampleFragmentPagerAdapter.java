package app.whistle.android.com.br.whistle.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import app.whistle.android.com.br.whistle.entity.Contact;

/**
 * Created by rafael on 02/03/2016.
 */
public class SampleFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Mensagens", "Contatos", "Localização" };
    private Context context;
    private FragmentManager fm;

    public SampleFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.fm = fm;
        this.context = context;
    }



    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public Fragment getItem(int position) {

        Log.i("Trocando TAB", String.valueOf(position));

        switch (position){

            case 0:
                return MessageFragment.newInstance(1);
            case 1:
                return ContactFragment.newInstance();
            case 2:
                return LocationFragment.newInstance();

            default:
                return MessageFragment.newInstance(1);

        }

    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }

}