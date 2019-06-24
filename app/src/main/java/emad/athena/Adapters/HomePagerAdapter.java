package emad.athena.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import emad.athena.Fragments.FeautresFragment;
import emad.athena.Fragments.HomeFragment;
import emad.athena.Fragments.MoreFragment;
import emad.athena.Fragments.ProfileFragment;

public class HomePagerAdapter extends FragmentPagerAdapter {
    public HomePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:return new HomeFragment();
            case 1:return new FeautresFragment();
            case 2:return new ProfileFragment();
            case 3:return new MoreFragment();
            default:return null;
        }
    }

    @Override
    public int getCount() {
        return 4;
    }
}
