package com.smp.obdscanner.servicedata;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

public class ObdCommandService extends Service
{
    private static final String TAG = "OBD_SERVICE";

    private BluetoothDevice device;
    private BluetoothSocket socket;
    private BluetoothSocket socketFallback;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
       return new ObdCommandBinder();
    }

    public void stopService()
    {
        Log.d(TAG, "Stopping service..");

        if (socket != null)
            // close socket
            try
            {
                socket.close();
            } catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }

        stopSelf();
    }

    public class ObdCommandBinder extends Binder
    {
        public ObdCommandService getService()
        {
            return ObdCommandService.this;
        }
    }
}
