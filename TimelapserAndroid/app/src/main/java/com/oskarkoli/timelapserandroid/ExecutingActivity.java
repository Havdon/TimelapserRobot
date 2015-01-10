package com.oskarkoli.timelapserandroid;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.oskarkoli.timelapserandroid.bluetooth.BluetoothThread;
import com.oskarkoli.timelapserandroid.movement.MovementPlan;
import com.oskarkoli.timelapserandroid.movement.MovementPlanPoint;
import com.oskarkoli.timelapserandroid.movement.MovementPlanThread;
import com.oskarkoli.timelapserandroid.util.TimeObject;
import com.oskarkoli.timelapserandroid.widgets.IntervalPickerFragment;
import com.oskarkoli.timelapserandroid.widgets.TimePickerFragment;

/**
 * Activity that handles the setting of the time for the movement plan, and the execution view of the movement plan.
 */
public class ExecutingActivity extends Activity {

    public static final int INTERVAL_SELECTED = 100;
    public static final int TIME_SELECTED = 101;
    public static final int APPEND_TEXT = 102;

    private TimeObject mTimelapseVideoLengthTime = new TimeObject(0, 0, 12);
    private TimeObject mIntervalTime = new TimeObject(0, 0, 2);
    private TimeObject mRealTime = new TimeObject(0, 0, 0);

    private TextView mTimelapseVideoLengthTimeText;
    private TextView mIntervalTimeText;
    private TextView mShotLength;
    private TextView mThreadText;

    private Button mMovementTimeButton;
    private Button mIntervalButton;
    private Button mStartButton;

    private MovementPlan mPlan;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_executing);

        mTimelapseVideoLengthTimeText = (TextView) findViewById(R.id.timeText);
        mTimelapseVideoLengthTimeText.setText(mTimelapseVideoLengthTime.toString());

        mIntervalTimeText = (TextView) findViewById(R.id.intervalText);
        mIntervalTimeText.setText(mIntervalTime.toString());


        mThreadText = (TextView) findViewById(R.id.threadText);
        mThreadText.setMovementMethod(new ScrollingMovementMethod());

        mShotLength = (TextView) findViewById(R.id.shotLengthText);


        mMovementTimeButton = (Button) findViewById(R.id.timeButton);
        mStartButton = (Button) findViewById(R.id.startButton);
        mIntervalButton = (Button) findViewById(R.id.intervaButton);


        mPlan = Globals.movementPlan;
        mPlan.setIntervalLength(mIntervalTime);
        mPlan.setTimelapseLength(mTimelapseVideoLengthTime);
        mPlan.setRealLength(mRealTime);

        MovementPlanPoint point = mPlan.getPointAtIndex(0);
        BluetoothThread.getInstance().sendMessageToDevice(point.getMessage());

        // Change main loop handler, so that this activity recieves the start & pause messages.
        MainLoop.getInstance().setUIHandler(_handler);

        calculateShotLength();
    }


    /**
     * Calculates the time it will take to move trough the movement plan, and take the images.
     * This is defined by picture frequency and wanted video length.
     */
    private void calculateShotLength() {
        int sec = (int) (mTimelapseVideoLengthTime.milliseconds() / 1000f);
        int frameCount = sec * 30; // Target time sec * 30 fps = frame count
        int realWorldTime = frameCount * mIntervalTime.seconds();
        mRealTime = TimeObject.fromSeconds(realWorldTime);
        String str = mRealTime.toString();
        mShotLength.setText(str);
        mPlan.setRealLength(mRealTime);
    }


    public void showTimePickerDialog(View v) {
        TimePickerFragment timeFragment = new TimePickerFragment();
        timeFragment.setHandler(_handler);
        timeFragment.setStartTime(mTimelapseVideoLengthTime);
        timeFragment.setDialogType(TIME_SELECTED);
        timeFragment.show(getFragmentManager(), "timePicker");
    }

    public void showIntervalDialog(View v) {
        IntervalPickerFragment intervalFragment = new IntervalPickerFragment();
        intervalFragment.setHandler(_handler);
        intervalFragment.setStartTime(mIntervalTime);
        intervalFragment.show(getFragmentManager(), "intervalPicker");
    }

    /**
     * Starts the execution of the movement.
     * Called by the start button.
     */
    public void start(View v) {
        mMovementTimeButton.setEnabled(false);
        mIntervalButton.setEnabled(false);
        mStartButton.setEnabled(false);

        MovementPlanThread movementPlanThread = new MovementPlanThread(mPlan, _handler);
        Thread thread = new Thread(movementPlanThread);
        thread.start();
    }

    /**
     * Scrolls thread textfield to bottom.
     */
    private void scrollText() {
        mThreadText.post(new Runnable() {
            @Override
            public void run() {
                final int scrollAmount = mThreadText.getLayout().getLineTop(mThreadText.getLineCount()) - mThreadText.getHeight();
                if (scrollAmount > 0) {
                    mThreadText.scrollTo(0, scrollAmount);
                } else {
                    mThreadText.scrollTo(0, 0);
                }
            }
        });
    }

    public Handler _handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_SELECTED: // Timelapse video length has been selected
                    mTimelapseVideoLengthTime = (TimeObject) msg.obj;
                    mTimelapseVideoLengthTimeText.setText(mTimelapseVideoLengthTime.toString());
                    mPlan.setTimelapseLength(mTimelapseVideoLengthTime);
                    break;
                case INTERVAL_SELECTED: // Picture interval has been selected.
                    mIntervalTime = (TimeObject) msg.obj;
                    mIntervalTimeText.setText(mIntervalTime.toString());
                    mPlan.setIntervalLength(mIntervalTime);
                    break;
                case APPEND_TEXT: // Appends text to the debug info view.
                    String text = (String) msg.obj;
                    mThreadText.append(text + "\n");
                    scrollText();
                    break;
            }

            calculateShotLength();

            super.handleMessage(msg);
        }
    };

}
