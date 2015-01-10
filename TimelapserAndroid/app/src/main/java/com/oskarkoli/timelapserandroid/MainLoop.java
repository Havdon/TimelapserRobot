package com.oskarkoli.timelapserandroid;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.oskarkoli.timelapserandroid.bluetooth.BluetoothManager;
import com.oskarkoli.timelapserandroid.gopro.GoProManager;
import com.oskarkoli.timelapserandroid.wifi.ApplicationWifiManager;

/**
 * Thread that handles Wifi and Bluetooth connections, and ensures proper state of GoPro camera.
 */
public class MainLoop implements Runnable {

    private ApplicationWifiManager mWifiManager;
    private GoProManager mGoProManager;
    private BluetoothManager mBluetoothManager;

    private boolean mRunning = true;

    private Activity mActivity;
    private Handler mUiHandler;

    private static MainLoop sInstance;

    public static MainLoop getInstance() {
        return sInstance;
    }

    public MainLoop(Activity activity, Handler uiHandler) {
        assert (sInstance == null);

        this.mActivity = activity;
        this.mUiHandler = uiHandler;

        sInstance = this;

        mWifiManager = new ApplicationWifiManager(activity, "EagleEye", "fagervik");
        mGoProManager = new GoProManager();
        mBluetoothManager = new BluetoothManager(activity);
    }

    public void setUIHandler(Handler handler) {
        mUiHandler = handler;
    }


    public boolean isGoProReady() {
        return mWifiManager.isConnected() && mGoProManager.isReady();
    }

    /**
     * Sends a message to UI activity to set the text that is overlayed over video stream.
     * @param text Text to display in overlay.
     */
    private void setOverlayText(String text) {
        Message msg = Message.obtain();
        msg.what = TimelapseActivity.OVERLAY_TEXT;
        msg.obj = new String(text);
        this.mUiHandler.sendMessage(msg);
    }

    /**
     * Sends a message to the UI activity.
     * @param code
     */
    private void sendMessage(int code) {
        Message msg = Message.obtain();
        msg.what = code;
        this.mUiHandler.sendMessage(msg);
    }

    @Override
    public void run() {
        try {
            while(mRunning) {

                // Check bluetooth connection, and connect if not connected.
                if (!mBluetoothManager.isConnected()) {
                    setOverlayText("Connecting to Bluetooth...");
                    mBluetoothManager.connect();
                }

                // Check wifi connection, and connect if down.
                if (!mWifiManager.isConnected()) {
                    sendMessage(Globals.MSG_PAUSE);
                    setOverlayText("Connecting to Wifi...");
                    mGoProManager.invalidateState();
                    mWifiManager.connect(); // Connect blocks until it is connected.
                }
                // Check if GoPro is powered on and in picture mode, sets it so if not.
                boolean inValidState = true;
                if(!mGoProManager.isReady()) {
                    setOverlayText("Reseting GoPro...");
                    inValidState = mGoProManager.makeReady();
                }
                // If GoPro is in invalid state (usually caused by being in recharge mode), hang and wait for user to fix camera.
                if (!inValidState) {
                    sendMessage(Globals.MSG_PAUSE);
                    setOverlayText("GoPro in invalid state. \nReconnecting...");
                    continue;
                }
                /*
                // Checks to make sure GoPro preview is working.
                if(!mGoProManager.checkPreview()) {
                    setOverlayText("Preview down...");
                    continue;
                }
                */

                setOverlayText("");
                sendMessage(Globals.MSG_START);


            }
            Thread.sleep(50);

        } catch (InterruptedException e) {
            setOverlayText("Unexpected InterruptedException!");
            return;
        }
    }


    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            mUiHandler.handleMessage(msg);
        }
    };
}
