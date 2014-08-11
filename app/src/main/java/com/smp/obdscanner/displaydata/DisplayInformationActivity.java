package com.smp.obdscanner.displaydata;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.smp.obdscanner.R;
import com.smp.obdscanner.connect.BluetoothConnectDialogFragment;
import com.smp.obdscanner.connect.OnBluetoothDeviceSelectedListener;
import com.smp.obdscanner.servicedata.ObdCommandService;

import java.lang.reflect.Method;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DisplayInformationActivity extends Activity implements ActionBar.TabListener,
        OBDAdapterFragment.OnAdapterFragmentInteractionListener, OnBluetoothDeviceSelectedListener
{
    @InjectView(R.id.pager)
    ViewPager mViewPager;

    private DisplayInformationPagerAdapter mSectionsPagerAdapter;
    private boolean serviceConnected;
    private ObdCommandService service;
    private final ServiceConnection connection = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            ObdCommandService.ObdCommandBinder binder = (ObdCommandService.ObdCommandBinder) iBinder;
            DisplayInformationActivity.this.service = binder.getService();
            serviceConnected = true;
            //DisplayInformationActivity.this.beginBluetoothConnection();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {
            serviceConnected = false;
        }
    };

    /*
    private void beginBluetoothConnection()
    {
        try
        {
            service.startObdConnection();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_information);
        ButterKnife.inject(this);
        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new DisplayInformationPagerAdapter(getFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        /*
        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        */
        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
        {
            @Override
            public void onPageSelected(int position)
            {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++)
        {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this)
            );
        }

        mViewPager.setCurrentItem(InformationType.ADAPTER.getValue());
        connectBluetooth();
    }

    @Override
    protected void onDestroy()
    {
        unbindService(connection);
        super.onDestroy();
    }

    private void connectBluetooth()
    {
        DialogFragment dialog = new BluetoothConnectDialogFragment();
        dialog.show(getFragmentManager(), getString(R.string.tag_bluetooth_connect));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.display_information, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings)
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
    {
    }


    @Override
    public void onAdapterFragmentInteraction(Uri uri)
    {

    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device)
    {
        Intent intent = new Intent(this, ObdCommandService.class);
        intent.putExtra(getString(R.string.intent_bluetooth_device), device);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }


}
