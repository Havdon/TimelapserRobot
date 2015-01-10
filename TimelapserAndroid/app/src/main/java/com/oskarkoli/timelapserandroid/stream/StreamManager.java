package com.oskarkoli.timelapserandroid.stream;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.SeekBar;

import com.oskarkoli.timelapserandroid.R;
import com.oskarkoli.timelapserandroid.TimelapseActivity;

import java.io.IOException;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;

/**
 * Manages the GoPro video stream.
 */
public class StreamManager implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener {
    private final String GOPRO_LIVE_URL = "http://10.5.5.9:8080/live/amba.m3u8";

    private boolean mStreamOn = false;
    private boolean mSurfaceReady = false;
    private boolean mWaitingForSurface = false;

    private Activity mActivity;
    private SurfaceView mSurface;
    private SurfaceHolder mSurfaceHolder;
    private MediaPlayer mMediaPlayer;
    private Handler mUiHandler;

    private boolean mIsValid = true;


    /**
     * Starts the video stream.
     */
    public void start() {
        if(!mIsValid) return;
        if (!mSurfaceReady) {
            mWaitingForSurface = true;
        } else {
            try {
                if (!mStreamOn) {
                    Log.d("TimelapseThread", "Playing video.");
                    mMediaPlayer.setDataSource(GOPRO_LIVE_URL);
                    mMediaPlayer.prepareAsync();
                    mStreamOn = true;
                    setOverlayText("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Stops the video stream.
     */
    public void stop() {
        if(!mIsValid) return;
        if(mStreamOn && mSurfaceReady) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mStreamOn = false;
        }
    }


    public void release() {
        if(!mIsValid) return;
        stop();
        mMediaPlayer.release();
        mIsValid = false;
    }

    public void init(Activity activity, Handler uiHandler) {
        mActivity = activity;
        if(!LibsChecker.checkVitamioLibs(activity))
            return;

        mStreamOn = false;
        mSurfaceReady = false;
        mWaitingForSurface = false;
        mIsValid = true;

        mSurface = (SurfaceView) activity.findViewById(R.id.surface);
        mSurfaceHolder = mSurface.getHolder();
        mSurfaceHolder.setFormat(PixelFormat.RGBA_8888);
        mSurfaceHolder.addCallback(this);

        mUiHandler = uiHandler;


    }

    /**
     * @return A bitmap of the current stream frame.
     */
    public Bitmap getFrameBitmap() {
        if(!mIsValid) return null;
        assert mSurfaceReady;
        return mMediaPlayer.getCurrentFrame();
    }

    /**
     * Sends a message to UI activity to set the text that is overlayed over video stream.
     * @param text Text to display in overlay.
     */
    private void setOverlayText(String text) {
        if(!mIsValid) return;
        Message msg = Message.obtain();
        msg.what = TimelapseActivity.OVERLAY_TEXT;
        msg.obj = new String(text);
        this.mUiHandler.sendMessage(msg);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mMediaPlayer = new MediaPlayer(mActivity);
        mMediaPlayer.setDisplay(mSurfaceHolder);

        mMediaPlayer.setOnPreparedListener(this);
        mSurfaceReady = true;

        if(mWaitingForSurface) {
            mWaitingForSurface = false;
            start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {}

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

}
