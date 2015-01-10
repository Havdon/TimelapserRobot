package com.oskarkoli.timelapserandroid.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.ikovac.timepickerwithseconds.view.MyTimePickerDialog;
import com.oskarkoli.timelapserandroid.ExecutingActivity;
import com.oskarkoli.timelapserandroid.util.TimeObject;

/**
 * Created by Oskar Koli on 05/01/2015.
 */
public class IntervalPickerFragment extends DialogFragment implements DialogInterface.OnClickListener {
    private Handler mUiHandler;
    private TimeObject mStartTime;

    private final String[] INTERVAL_OPTIONS_STRING = {"0.5 sec", "1 sec", "2 sec", "5 sec", "10 sec", "30 sec", "60 sec"};
    private final float[] INTERVAL_OPTIONS_INT = {0.5f, 1f, 2f, 5f, 10f, 30f, 60f};

    public void setHandler(Handler uiHandler) {
        mUiHandler = uiHandler;
    }

    public void setStartTime(TimeObject obj) {
        mStartTime = obj;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mStartTime == null) {
            mStartTime = new TimeObject(0, 0);
        }

        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle("Picture Interval");
        b.setItems(INTERVAL_OPTIONS_STRING, this);

        return b.show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (mUiHandler != null) {
            Message message = Message.obtain();
            message.what = ExecutingActivity.INTERVAL_SELECTED;

            message.obj = TimeObject.fromMilliseconds((int) (INTERVAL_OPTIONS_INT[which] * 1000));
            mUiHandler.sendMessage(message);
        }
    }
}
