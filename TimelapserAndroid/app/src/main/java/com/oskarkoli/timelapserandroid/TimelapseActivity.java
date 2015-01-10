package com.oskarkoli.timelapserandroid;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.oskarkoli.timelapserandroid.bluetooth.BluetoothThread;
import com.oskarkoli.timelapserandroid.movement.MovementPlan;
import com.oskarkoli.timelapserandroid.movement.MovementPlanPoint;
import com.oskarkoli.timelapserandroid.stream.StreamManager;
import com.oskarkoli.timelapserandroid.widgets.KeyPointSlider;


/**
 * Main UI activity of the appication.
 */
public class TimelapseActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    // Message ids used to communicate between UI thread and other threads.
    public static final int OVERLAY_TEXT = 901;
    public static final int MSG_START_STEAM = 920;
    public static final int MSG_END_STEAM = 921;

    private StreamManager mStreamManager;

    private RobotMessage mCurrentRobotMessage;
    private MovementPlan mMovementPlan;

    private SeekBar mVerticalSeekBar;
    private SeekBar mHorizontalSeekBar;
    private TextView mOverlayText;
    private KeyPointSlider mKeyPointSlider;
    private MainLoop mMainLoop;

    private Button mStartButton;


    private static TimelapseActivity sInstance;
    public static TimelapseActivity getsInstance() {
        return sInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert TimelapseActivity.sInstance == null;
        TimelapseActivity.sInstance = this;

        setContentView(R.layout.activity_timelapse_acticity);

        mOverlayText = (TextView) findViewById(R.id.overlay_text);
        mVerticalSeekBar = (SeekBar) findViewById(R.id.verticalSeekBar);
        mVerticalSeekBar.setProgress(50);
        mHorizontalSeekBar = (SeekBar) findViewById(R.id.horizontalSeekBar);
        mHorizontalSeekBar.setProgress(50);

        mVerticalSeekBar.setOnSeekBarChangeListener(this);
        mHorizontalSeekBar.setOnSeekBarChangeListener(this);

        mCurrentRobotMessage = new RobotMessage();
        mMovementPlan = new MovementPlan();

        mKeyPointSlider = (KeyPointSlider) findViewById(R.id.horizontalScrollView);

        mStartButton = (Button) findViewById(R.id.startButton);
        mStartButton.setEnabled(false);

        mStreamManager = new StreamManager();
        mStreamManager.init(this, _handler);

        mMainLoop = new MainLoop(this, _handler);
        Thread loopThread = new Thread(mMainLoop);
        loopThread.start();

    }

    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case Globals.OVERLAY_TEXT:
                    mOverlayText.setText((String) msg.obj);
                    break;
                case Globals.MSG_START:
                    mStreamManager.start();
                    break;
                case Globals.MSG_PAUSE:
                    mStreamManager.stop();
                    break;
            }

            super.handleMessage(msg);
        }
    };

    public void startMovement(View v) {
        mStreamManager.release();
        Globals.movementPlan = mMovementPlan;
        Intent intent = new Intent(getApplicationContext(), ExecutingActivity.class);
        finish();
        startActivity(intent);
    }

    /**
     * Removes the latest keypoint from the robot movement plan.
     * Called from button in view.
     */
    public void popKeyPoint(View v) {
        mKeyPointSlider.popKeyPoint();
        MovementPlanPoint planPoint = mMovementPlan.popKeyPoint();
        if (planPoint != null) {
            mCurrentRobotMessage = planPoint.getMessage(); // Set current state to the second to last state.
            updateRobotState();
        }

        if (mMovementPlan.pointCount() < 2) {
            mStartButton.setEnabled(false);
        }
    }

    /**
     * Adds a keypoint to the robot movement plan.
     * Called from button in view.
     */
    public void setKeyPoint(View v) {
        Bitmap bitmap = mStreamManager.getFrameBitmap();
        mKeyPointSlider.addKeyPoint(bitmap);
        MovementPlanPoint point = new MovementPlanPoint(mCurrentRobotMessage.getVerticalRotation(), mCurrentRobotMessage.getHorizontalRotation());
        mMovementPlan.addKeyPoint(point);

        if (mMovementPlan.pointCount() >= 2) {
            mStartButton.setEnabled(true);
        }
    }

    private boolean shouldResumeStream = false;

    @Override
    protected void onResume() {
        super.onResume();
        // Enables immersive mode.
        /*
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
                */

        mStreamManager.init(this, _handler);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStreamManager.release();
    }



    private final int TEETH_HORIZONTAL_IN_GEAR = 24;
    private final int TEETH_VERTICAL_IN_GEAR = 12;

    private final int MAX_VERT_ROTATION = (360 * 2) * TEETH_VERTICAL_IN_GEAR;
    private final int MAX_HOR_ROTATION = 360 * TEETH_HORIZONTAL_IN_GEAR; // 1 rotation of motor moves gear by one tooth.
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(seekBar == mVerticalSeekBar) {
            int vertRot = (int) (MAX_VERT_ROTATION * (progress / 100.0f) - (MAX_VERT_ROTATION / 2));
            mCurrentRobotMessage.setVerticalRotation(vertRot);
        } else if(seekBar == mHorizontalSeekBar) {
            int horRot = (int) (MAX_HOR_ROTATION * (progress / 100.0f) - (MAX_HOR_ROTATION / 2));
            mCurrentRobotMessage.setHorizontalRotation(horRot);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        updateRobotState();
    }

    /**
     * Sends the current state to the EV3 device.
     */
    private void updateRobotState() {
        BluetoothThread.getInstance().sendMessageToDevice(mCurrentRobotMessage.clone());
    }
}
