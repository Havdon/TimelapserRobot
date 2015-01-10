package com.oskarkoli.timelapserandroid.wifi;

import android.app.Activity;
import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;


/**
 * Manages the wifi connection to the GoPro camera.
 */
public class ApplicationWifiManager {


    private WifiManager mWifiManager;
    private ConnectivityManager mConnectivityManager;
    private String mWifiSSID;


    public ApplicationWifiManager(Activity activity, String wifiSSID, String wifiPassword) {
        mWifiSSID = wifiSSID;
        mWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + wifiSSID + "\"";
        conf.preSharedKey = "\"" + wifiPassword + "\"";
        int netId = mWifiManager.addNetwork(conf);
        mWifiManager.saveConfiguration();
        mWifiManager.disconnect();
        mWifiManager.enableNetwork(netId, true);
        mWifiManager.reconnect();
    }

    /**
     * Checks if wifi connection is up and valid.
     * @return True if connection is working.
     */
    public boolean isConnected() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        if (wifiInfo.getSSID() != null && wifiInfo.getSSID().equals("\"" + mWifiSSID + "\"")) {
            NetworkInfo ni = mConnectivityManager.getActiveNetworkInfo();
            if (ni != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Blocks until connection is made to camera. Retried every 200 ms.
     * @throws InterruptedException
     */
    public void connect() throws InterruptedException {
        mWifiManager.reconnect();
        while(!isConnected()) {
            Thread.sleep(200);
            mWifiManager.reconnect();
        }
    }

}
