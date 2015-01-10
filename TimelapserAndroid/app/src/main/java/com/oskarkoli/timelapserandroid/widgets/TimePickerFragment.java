package com.oskarkoli.timelapserandroid.widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;


import com.ikovac.timepickerwithseconds.view.MyTimePickerDialog;
import com.oskarkoli.timelapserandroid.util.TimeObject;

/**
 * Created by Oskar Koli on 04/01/2015.
 */
public class TimePickerFragment extends DialogFragment
        implements MyTimePickerDialog.OnTimeSetListener {

    private Handler mUiHandler;
    private TimeObject mStartTime;
    private int mDialogType;

    public void setHandler(Handler uiHandler) {
        mUiHandler = uiHandler;
    }

    public void setStartTime(TimeObject obj) {
        mStartTime = obj;
    }

    public void setDialogType(int str) { mDialogType = str; }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mStartTime == null) {
            mStartTime = new TimeObject(0, 0);
        }
        int h = mStartTime.hours();
        int m = mStartTime.minutes();
        int s = mStartTime.seconds();

        return new MyTimePickerDialog(getActivity(), this, h, m, s, true);
    }



    @Override
    public void onTimeSet(com.ikovac.timepickerwithseconds.view.TimePicker view, int hourOfDay, int minute, int seconds) {
        if (mUiHandler != null) {
            Message message = Message.obtain();
            message.what = mDialogType;

            message.obj = new TimeObject(hourOfDay, minute, seconds);
            mUiHandler.sendMessage(message);
        }
    }
}