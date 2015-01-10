package com.oskarkoli.timelapserandroid.movement;

import android.os.Handler;
import android.os.Message;

import com.oskarkoli.timelapserandroid.ExecutingActivity;
import com.oskarkoli.timelapserandroid.MainLoop;
import com.oskarkoli.timelapserandroid.RobotMessage;
import com.oskarkoli.timelapserandroid.bluetooth.BluetoothThread;
import com.oskarkoli.timelapserandroid.gopro.GoProHelper;
import com.oskarkoli.timelapserandroid.util.TimeObject;

/**
 * Thread that handles the execution of the movement plan.
 */
public class MovementPlanThread implements Runnable {

    private MovementPlan mPlan;
    private Handler mUIHandler;
    private boolean running = true;

    public MovementPlanThread(MovementPlan plan, Handler handler) {
        mPlan = plan;
        mUIHandler = handler;

    }


    private void sendText(String text) {
        if (mUIHandler != null) {
            Message msg = Message.obtain();
            msg.what = ExecutingActivity.APPEND_TEXT;
            msg.obj = text;
            mUIHandler.sendMessage(msg);
        }
    }

    @Override
    public void run() {


       try {

            Thread.sleep(2000);
            long prevTime = System.currentTimeMillis();
            TimeObject intervalTime = mPlan.getIntervalLength();
            final int intervalMs = intervalTime.milliseconds();
            final int timelapeLength = mPlan.getRealLength().milliseconds();
            long intervalCurTime = 0;
            long totalTime = 0;
            int i = 0;

            while(running) {
                long currentTime = System.currentTimeMillis();
                long delta = currentTime - prevTime;
                intervalCurTime += delta;
                totalTime += delta;



                if (intervalCurTime >= intervalMs) {
                    if (MainLoop.getInstance().isGoProReady()) {
                        GoProHelper.takePicture();
                    } else {
                        sendText("Failed to take picture with GoPro.");
                    }

                    intervalCurTime -= intervalMs;

                    float interpol = totalTime / (float) timelapeLength;
                    MovementPlanPoint point = mPlan.getInterpolatedPoint(interpol);
                    sendText(point.toString());
                    sendText("Interpolation: " + interpol);

                    Thread.sleep(intervalMs / 2);

                    RobotMessage message = point.getMessage();


                    BluetoothThread.getInstance().sendMessageToDevice(message);

                    if (interpol >= 0.99999999f) {
                        running = false;
                    }

                }

                prevTime = currentTime;
            }
           sendText("Completed!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
