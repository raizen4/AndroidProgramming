package projects.boldurbogdan.mymediaplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

/**
 * Created by boldurbogdan on 14/07/2016.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments=new ArrayList<>();
    private ArrayList<String> fragment_names=new ArrayList<>();

    public void add_frag(Fragment frag,String name_frag){
        fragments.add(frag);
        fragment_names.add(name_frag);
    }
    public ViewPagerAdapter(FragmentManager fm){
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }


    @Override
    public int getCount() {
        return fragments.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return fragment_names.get(position);
    }
}
