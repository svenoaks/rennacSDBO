package com.smp.obdscanner.connect;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.smp.obdscanner.R;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Steve on 8/7/14.
 */
public class BluetoothConnectDialogFragment extends DialogFragment
{
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
    public static final int REQUEST_ENABLE_BT = 4343;
    private final BroadcastReceiver mFoundReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action))
            {
                mScanFlipper.setDisplayedChild(FoundFlipperState.LIST.getValue());
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                mFoundArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    };
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedArrayAdapter;
    private ArrayAdapter<String> mFoundArrayAdapter;
    private ListView mPairedDeviceListView;
    private ListView mFoundDeviceListView;
    private View rootView;
    private Button mScanButton;
    private ViewFlipper mScanFlipper;

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
                mPairedArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        }
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    */

    @Override
    public void onPause()
    {
        super.onPause();
    }

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

        mPairedArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());
        mFoundArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, new ArrayList<String>());

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
        getActivity().unregisterReceiver(mFoundReceiver);
        super.onDestroy();
    }
}
