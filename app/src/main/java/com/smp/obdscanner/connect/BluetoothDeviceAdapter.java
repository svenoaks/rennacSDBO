package com.smp.obdscanner.connect;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.http.message.BasicNameValuePair;

import java.util.List;

/**
 * Created by Steve on 8/10/14.
 */
public class BluetoothDeviceAdapter extends ArrayAdapter<BluetoothDevice>
{
    private List<BluetoothDevice> list;

    public BluetoothDeviceAdapter(Context context, int resource, int textViewResourceId, List<BluetoothDevice> list)
    {
        super(context, resource, textViewResourceId, list);
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);

        TextView text1 = (TextView) view.findViewById(android.R.id.text1);
        TextView text2 = (TextView) view.findViewById(android.R.id.text2);

        BluetoothDevice data = list.get(position);

        text1.setText(data.getName());
        text2.setText(data.getAddress());
        return view;
    }
}
