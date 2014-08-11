package com.smp.obdscanner.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.smp.obdscanner.R;
import com.smp.obdscanner.displaydata.DisplayInformationActivity;
import com.smp.obdscanner.servicedata.ObdCommandService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Steve on 8/7/14.
 */
public class BluetoothConnectDialogFragment extends DialogFragment implements OnBluetoothDeviceSelectedListener
{
    public static final int REQUEST_ENABLE_BT = 4343;
    private final BroadcastReceiver mFoundReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (!isAlreadyPaired(device))
                {
                    mScanFlipper.setDisplayedChild(FoundFlipperState.LIST.getValue());
                    mFoundArrayAdapter.add(device);
                }
            }
        }
    };

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDeviceAdapter mPairedArrayAdapter;
    private BluetoothDeviceAdapter mFoundArrayAdapter;
    private ListView mPairedDeviceListView;
    private ListView mFoundDeviceListView;
    private View rootView;
    private Button mScanButton;
    private ViewFlipper mScanFlipper;
    private OnBluetoothDeviceSelectedListener listener;


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_OK)
        {
            showPairedDevices();
        } else
        {
            Toast.makeText(getActivity(), getActivity().getString(R.string.toast_enable_bluetooth), Toast.LENGTH_LONG).show();
        }
    }

    private void showPairedDevices()
    {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        {
            for (BluetoothDevice device : pairedDevices)
            {
                mPairedArrayAdapter.add(device);
            }
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        listener = (OnBluetoothDeviceSelectedListener) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setMessage(R.string.message_bluetooth_connect)
                .setNegativeButton(R.string.button_negative, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User cancelled the dialog
                    }
                });
        rootView = inflater.inflate(R.layout.dialog_connect, null);
        builder.setView(rootView);
        return builder.create();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        mPairedArrayAdapter = new BluetoothDeviceAdapter(getActivity(), android.R.layout.simple_list_item_2,
                android.R.id.text1, new ArrayList<BluetoothDevice>(), this);
        mFoundArrayAdapter = new BluetoothDeviceAdapter(getActivity(), android.R.layout.simple_list_item_2,
                android.R.id.text1, new ArrayList<BluetoothDevice>(), this);

        mScanFlipper = (ViewFlipper) rootView.findViewById(R.id.flipper_found_device);
        mScanButton = (Button) rootView.findViewById(R.id.button_bluetooth_scan);
        mPairedDeviceListView = (ListView) (rootView.findViewById(R.id.listview_paired_device));
        mFoundDeviceListView = (ListView) (rootView.findViewById(R.id.listview_found_device));

        mPairedDeviceListView.setAdapter(mPairedArrayAdapter);
        mFoundDeviceListView.setAdapter(mFoundArrayAdapter);

        mScanButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startBluetoothScan();
            }
        });

        initBluetooth();

    }

    private void startBluetoothScan()
    {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mFoundReceiver, filter);
        mScanFlipper.setDisplayedChild(FoundFlipperState.PROGRESS.getValue());
        mBluetoothAdapter.startDiscovery();
    }

    private void initBluetooth()
    {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null)
        {
            Toast.makeText(getActivity(), getActivity().getString(R.string.toast_no_bluetooth), Toast.LENGTH_LONG).show();
            getActivity().finish();
            return;
        }

        if (!mBluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        showPairedDevices();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        if (mBluetoothAdapter != null) mBluetoothAdapter.cancelDiscovery();
        try
        {
            getActivity().unregisterReceiver(mFoundReceiver);
        }
        catch (IllegalArgumentException e)
        {
            e.printStackTrace();
            //oh well.
        }
        super.onDestroy();
    }

    private boolean isAlreadyPaired(BluetoothDevice device)
    {
        boolean alreadyPaired = false;
        for (int i = 0; i < mPairedArrayAdapter.getCount(); ++i)
        {
            BluetoothDevice otherDevice = mPairedArrayAdapter.getItem(i);
            if (device.getAddress().equals(otherDevice.getAddress()) &&
                    device.getName().equals(otherDevice.getName()))
            {
                alreadyPaired = true;
                break;
            }
        }
        return alreadyPaired;
    }

    @Override
    public void onBluetoothDeviceSelected(BluetoothDevice device)
    {
        if (!isAlreadyPaired(device))
        {
            pairDevice(device);
        }

        listener.onBluetoothDeviceSelected(device);
        dismiss();
    }

    private void pairDevice(BluetoothDevice device)
    {
        try
        {
            Log.d("pairDevice()", "Start Pairing...");
            Method m = device.getClass().getMethod("createBond", (Class[]) null);
            m.invoke(device, (Object[]) null);
            Log.d("pairDevice()", "Pairing finished.");
        } catch (Exception e)
        {
            Log.e("pairDevice()", e.getMessage());
        }
    }
    public enum FoundFlipperState
    {
        BUTTON(0), PROGRESS(1), LIST(2), NOT_FOUND(3);

        private int value;

        private FoundFlipperState(int value)
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }
}
