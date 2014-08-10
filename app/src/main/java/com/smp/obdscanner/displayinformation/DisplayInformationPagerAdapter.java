package com.smp.obdscanner.displayinformation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;

import com.smp.obdscanner.R;

import java.util.Locale;

/**
 * Created by Steve on 8/6/14.
 */
public class DisplayInformationPagerAdapter extends FragmentPagerAdapter
{
    private final Context context;

    public DisplayInformationPagerAdapter(FragmentManager fm, Context context)
    {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position)
    {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).

        return OBDAdapterFragment.newInstance("", "");

    }

    @Override
    public int getCount()
    {
        return InformationType.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        Locale l = Locale.getDefault();
        InformationType type = InformationType.values()[position];
        switch (type)
        {
            case PERFORMANCE:
                return context.getString(R.string.title_tab_performance).toUpperCase(l);
            case DASH:
                return context.getString(R.string.title_tab_dash).toUpperCase(l);
            case DATA:
                return context.getString(R.string.title_tab_data).toUpperCase(l);
            case FAULTS:
                return context.getString(R.string.title_tab_faults).toUpperCase(l);
            case ADAPTER:
                return context.getString(R.string.title_tab_adapter).toUpperCase(l);
        }
        return null;
    }
}
