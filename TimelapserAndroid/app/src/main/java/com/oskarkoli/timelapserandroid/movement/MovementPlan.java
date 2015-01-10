package com.oskarkoli.timelapserandroid.movement;

import com.oskarkoli.timelapserandroid.util.TimeObject;

import java.util.ArrayList;

/**
 * Contains data for the movement of the robot.
 */
public class MovementPlan {

    private ArrayList<MovementPlanPoint> mMovementPoints = new ArrayList<>();
    private TimeObject mIntervalLength;
    private TimeObject mTimelapseLength;
    private TimeObject mRealLength;


    public void setIntervalLength(TimeObject obj) {
        mIntervalLength = obj;
    }

    public TimeObject getIntervalLength() {
        return mIntervalLength;
    }
    public void setTimelapseLength(TimeObject obj) {
        mTimelapseLength = obj;
    }
    public TimeObject getTimelapseLength() {
        return mTimelapseLength;
    }

    public void setRealLength(TimeObject obj) {
        mRealLength = obj;
    }
    public TimeObject getRealLength() {
        return mRealLength;
    }

    public int pointCount() {return mMovementPoints.size(); }

    /**
     * Calculates an interpolated value from the movement plan
     * @param value Value between 0 and 1
     * @return  Interpolated point.
     */
    public MovementPlanPoint getInterpolatedPoint(float value) {
        assert(value >= 0 && value <= 1);
        float totalLength = 0f;
        for(int i = 1; i < mMovementPoints.size(); i++) {
            MovementPlanPoint p = mMovementPoints.get(i);
            totalLength += p.length();
        }
        float targetInterpol = totalLength * value;
        float currentLength = 0f;
        MovementPlanPoint prevPoint = mMovementPoints.get(0);
        for(int i = 1; i < mMovementPoints.size(); i++) {
            MovementPlanPoint p = mMovementPoints.get(i);
            float len = p.length();
            if (currentLength + len < targetInterpol) {
                currentLength += len;
            } else {
                MovementPlanPoint point = new MovementPlanPoint();
                float scale = (targetInterpol - currentLength) / len;
                float horDiff = p.horizontalRotation - prevPoint.horizontalRotation;
                float vertDiff = p.verticalRotation - prevPoint.verticalRotation;
                point.horizontalRotation = prevPoint.horizontalRotation + (int) (horDiff * scale);
                point.verticalRotation = prevPoint.verticalRotation + (int) (vertDiff * scale);
                return point;
            }
            prevPoint = p;
        }

        // None was found, return last.
        return mMovementPoints.get(mMovementPoints.size() - 1);
    }


    public MovementPlanPoint getPointAtIndex(int index) {
        if (index < mMovementPoints.size()) {
            return mMovementPoints.get(index);
        }
        return null;
    }

    /**
     * Adds a keypoint to the movement plan.
     */
    public void addKeyPoint(MovementPlanPoint point) {
        mMovementPoints.add(point);
    }


    /**
     * Removes the latest keypoint from the movement plan.
     */
    public MovementPlanPoint popKeyPoint() {
        if (mMovementPoints.size() > 0) {
            return mMovementPoints.remove(mMovementPoints.size() - 1);
        }
        return null;
    }

}
