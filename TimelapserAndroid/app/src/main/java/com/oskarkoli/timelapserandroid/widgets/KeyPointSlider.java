package com.oskarkoli.timelapserandroid.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.oskarkoli.timelapserandroid.R;

import java.util.ArrayList;

/**
 * Widget that handles the adding of stream frames to lower part of screen when a new movement key point is added.
 */
public class KeyPointSlider extends HorizontalScrollView {

    private LinearLayout mScrollLayout;
    private ArrayList<ImageView> mItems = new ArrayList<>();


    public KeyPointSlider(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public KeyPointSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public KeyPointSlider(Context context) {
        super(context);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        init();
    }

    protected void init() {
        mScrollLayout = (LinearLayout) findViewById(R.id.scrollImageHolder);
    }


    /**
     * @param bitmap Bitmap to add to slider.
     */
    public void addKeyPoint(Bitmap bitmap) {

        ImageView image = new ImageView(getContext());
        image.setVisibility(View.VISIBLE);
        image.setImageBitmap(bitmap);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
        layoutParams.width = mScrollLayout.getHeight();
        layoutParams.setMargins(24, 0, 24, 0);
        layoutParams.height = mScrollLayout.getHeight();

        mScrollLayout.addView(image, layoutParams);
        image.invalidate();
        mScrollLayout.invalidate();

        mItems.add(image);

        scrollToEnd();
    }


    /**
     * Removes the latest bitmap that was added to slider.
     */
    public void popKeyPoint() {
        if (mItems.size() > 0) {
            ImageView image = mItems.remove(mItems.size() - 1);
            mScrollLayout.removeView(image);
        }
    }

    /**
     * Scrolls the slider to the end.
     */
    private void scrollToEnd() {
        final KeyPointSlider self = this;
        this.post(new Runnable()
        {
            public void run()
            {
                self.fullScroll(View.FOCUS_RIGHT);
            }
        });
    }

}
