package com.oskarkoli.timelapserandroid.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.oskarkoli.timelapserandroid.RobotMessage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Thread that manages the bluetooth connection.
 */
public class BluetoothThread extends Thread {
    private final BluetoothSocket mSocket;
    private ArrayList<IBluetoothListener> mListeners = new ArrayList<IBluetoothListener>();

    private RobotMessage mStateToSend = null;

    private static BluetoothThread sInstance;

    public static BluetoothThread getInstance() {
        return sInstance;
    }


    public BluetoothThread(BluetoothDevice device) {
        assert BluetoothThread.sInstance == null;
        BluetoothThread.sInstance = this;

        BluetoothSocket tmp = null;

        Log.i("Bluetooth", "Creating socket...");
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        try {
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            Log.e("Bluetooth", "Failed to create socket!");
        }
        mSocket = tmp;
        Log.i("Bluetooth", "Success!");
    }


    /**
     * Sets the message to be send to the EV3 device.
     * Messages are not queued, only the latest set message will be sent next time the thread is processed.
     * @param message Message to send to EV3 device.
     */
    public void sendMessageToDevice(RobotMessage message) {
        mStateToSend = message;
    }


    public void addBluetoothListener(IBluetoothListener listener) {
        mListeners.add(listener);
    }

    private void notifyConnectionSuccessfull() {
        for(IBluetoothListener listener : mListeners) {
            listener.btConnectionOpened();
        }
    }

    private void notifyConnectionClosed(boolean error, String msg) {

        for(IBluetoothListener listener : mListeners) {
            listener.btConnectionClosed(error, msg);
        }
    }

    public void run() {
        try {
            Log.i("Bluetooth", "Connecting socket...");
            mSocket.connect();
            Log.i("Bluetooth", "Success!");
        } catch (IOException connectException) {
            // Unable to connect; close the socket and get out
            try {
                notifyConnectionClosed(true, "Got IOException at connect: " + connectException.getMessage());
                mSocket.close();
            } catch (IOException closeException) { }
            return;
        }

        notifyConnectionSuccessfull();

        Log.i("Bluetooth", "Waiting on socket...");
        try {
            ObjectOutputStream out = new ObjectOutputStream(mSocket.getOutputStream());
            int ix = 0;
            while(mSocket.isConnected()) {
                if (mStateToSend != null) {
                    out.writeObject(mStateToSend);
                    mStateToSend = null;
                    out.flush();
                    Thread.sleep(500);
                }
            }
        } catch (IOException e) {
            notifyConnectionClosed(true, "Got IOException at read: " + e.getMessage());
            return;
        } catch (InterruptedException e) {
            notifyConnectionClosed(true, "Got InterruptedException at read: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /** Will cancel an in-progress connection, and close the socket */
    public void cancel() {
        try {
            mSocket.close();
        } catch (IOException e) { }
    }
}