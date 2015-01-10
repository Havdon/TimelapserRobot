package com.oskarkoli.timelapserandroid.gopro;

/**
 * Created by Oskar Koli on 19/12/2014.
 */
public class GoProManager {
    public static final String BASE_URL = "http://10.5.5.9/";
    public static final String PASSWORD = "fagervik";



    private boolean mIsCameraOn = false;
    private boolean mIsInPhotoMode = false;


    /**
     * Forces the manager to do all "ready" checks again.
     * Called for example when WiFi connection is lost.
     */
    public void invalidateState() {
        mIsCameraOn = false;
        mIsInPhotoMode = false;
    }


    /**
     * Checks if camera is powered on and is in photomode.
     * @return True if ready.
     */
    public boolean isReady() {
        return mIsCameraOn && mIsInPhotoMode;
    }

    /**
     * Checks validity of camera preview, by doing a ping to the camera server.
     */
    public boolean checkPreview() throws InterruptedException {
        if(!GoProHelper.isPreviewValid()) {
            invalidateState();
            return false;
        }
        return true;
    }

    /**
     * Sets GoPro to a ready state.
     * @return  Return false if GoPro is in an invalid state. Invalid state is usually caused by camera being in recharge mode which causes server to return HTTP response 402.
     */
    public boolean makeReady() throws InterruptedException {
        boolean invalid = (!GoProHelper.setPowerMode(true) || !GoProHelper.setPictureMode() || !GoProHelper.enablePreview());
        if(invalid) {
            return false;
        }
        mIsCameraOn = true;
        mIsInPhotoMode = true;
        return true;
    }






}
