package com.oskarkoli.timelapserandroid.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import io.vov.vitamio.utils.Log;

/**
 * Manages the bluetooth connection thread.
 */
public class BluetoothManager implements IBluetoothListener {

    // Hardcoded address off the EV3 device.
    public final String EV3_ADDRESS = "00:16:53:46:76:FD";

    private Activity mActivity;
    private boolean mIsConnected = false;

    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothThread mBluetoothThread = null;
    private BluetoothDevice mBluetoothDevice = null;


    public BluetoothManager(Activity activity) {
        mActivity = activity;

    }

    /**
     * Inits the bluetooth connection thread and hangs until connection is made.
     */
    public void connect() {
        assert (mBluetoothAdapter == null && mBluetoothDevice == null && mBluetoothThread == null);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.e("Bluetooth", "Bluetooth is not supported on this device!");
            System.exit(-1);
        }
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, 1);
        }

        // Connect to EV3 device.
        mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(EV3_ADDRESS);
        mBluetoothThread = new BluetoothThread(mBluetoothDevice);
        mBluetoothThread.addBluetoothListener(this);
        mBluetoothThread.start();


        while(true) {
            if(mIsConnected) {
                return;
            }
        }
    }

    /**
     * @return  Returns true if connected.
     */
    public boolean isConnected() {
        return mIsConnected;
    }

    @Override
    public void btConnectionOpened() {
        mIsConnected = true;
    }

    @Override
    public void btConnectionClosed(boolean error, String message) {
        mIsConnected = false;
    }
}
