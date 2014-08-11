package com.smp.obdscanner.servicedata;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.smp.obdscanner.R;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

public class ObdCommandService extends Service
{
    private static final String TAG = "OBD_SERVICE";

    private UUID MY_UUID;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private BluetoothSocket socketFallback;
    private boolean running;

    @Override
    public void onCreate()
    {
        super.onCreate();
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        try
        {
            MY_UUID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        device = intent.getParcelableExtra(getString(R.string.intent_bluetooth_device));
        try
        {
            startObdConnection();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return new ObdCommandBinder();
    }

    public void stopService()
    {
        running = false;
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

    //TODO figure out what the hell it's doing.
    //TODO Definetly can't be on main thread.
    public void startObdConnection() throws IOException
    {
        Log.d(TAG, "Starting OBD connection..");

        try
        {
            // Instantiate a BluetoothSocket for the remote device and connect it.
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            socket.connect();
        } catch (Exception e1)
        {
            Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
            Class<?> clazz = socket.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try
            {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                socketFallback = (BluetoothSocket) m.invoke(socket.getRemoteDevice(), params);
                socketFallback.connect();
                socket = socketFallback;
            } catch (Exception e2)
            {
                Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection. Stopping app..", e2);
                stopService();
                return;
            }
        }
        running = true;
    }
    public boolean isRunning()
    {
        return isRunning();
    }
    public class ObdCommandBinder extends Binder
    {
        public ObdCommandService getService()
        {
            return ObdCommandService.this;
        }
    }
}