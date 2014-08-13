package com.smp.obdscanner.servicedata;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.smp.obdscanner.R;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ObdCommandService extends Service
{
    private static final String TAG = "OBD_SERVICE";

    private UUID MY_UUID;

    private ExecutorService mExecutor;

    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private BluetoothSocket mSocketFallback;

    private BluetoothAdapter mAdapter;

    private ObdCommandServiceListener mListener;

    private Handler handler;

    private volatile boolean running;
    private volatile boolean connected;

    @Override
    public void onCreate()
    {
        super.onCreate();
        String androidId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //lolwhat
        mExecutor = Executors.newSingleThreadExecutor();
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        handler = new Handler();
        /*
        try
        {

            //MY_UUID = UUID.nameUUIDFromBytes(androidId.getBytes("utf8"));
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        */
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        mDevice = intent.getParcelableExtra(getString(R.string.intent_bluetooth_device));
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

        if (mSocket != null)
            // close mSocket
            try
            {
                mSocket.close();
            } catch (IOException e)
            {
                Log.e(TAG, e.getMessage());
            }

        stopSelf();
    }

    //TODO Definetly can't be on main thread.
    public void startObdConnection() throws IOException
    {
        Log.d(TAG, "Starting OBD connection..");
        mExecutor.submit(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    mAdapter.cancelDiscovery();
                    // Instantiate a BluetoothSocket for the remote mDevice and connect it.
                    mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
                    mSocket.connect();
                    onConnected();
                } catch (IOException e1)
                {
                    Log.e(TAG, "There was an error while establishing Bluetooth connection. Falling back..", e1);
                    Class<?> clazz = mSocket.getRemoteDevice().getClass();
                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
                    try
                    {
                        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                        Object[] params = new Object[]{Integer.valueOf(1)};
                        mSocketFallback = (BluetoothSocket) m.invoke(mSocket.getRemoteDevice(), params);
                        mSocketFallback.connect();
                        mSocket = mSocketFallback;
                        onConnected();
                    } catch (IOException e2)
                    {
                        e2.printStackTrace();
                        Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection. Stopping app..", e2);
                        stopService();
                        return;
                    } catch (ReflectiveOperationException e2)
                    {
                        e2.printStackTrace();
                        Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection. Stopping app..", e2);
                        stopService();
                        return;
                    }
                }
                running = true;
            }
        });

    }
    private void onConnected()
    {
        connected = true;
        handler.post(new Runnable() {

            @Override
            public void run()
            {
                mListener.onDeviceConnected();
            }
        });

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

        public void setListener(ObdCommandServiceListener listener)
        {
            ObdCommandService.this.mListener = listener;
        }
    }
}