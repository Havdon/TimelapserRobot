package com.oskarkoli.timelapserandroid.bluetooth;

/**
 * Interface for BluetoothThread listeners.
 */
public interface IBluetoothListener {

    public void btConnectionOpened();
    public void btConnectionClosed(boolean error, String message);

}
