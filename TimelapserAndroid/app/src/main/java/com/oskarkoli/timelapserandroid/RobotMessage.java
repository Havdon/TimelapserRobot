package com.oskarkoli.timelapserandroid;

import java.io.Serializable;

/**
 * Message object shared with the EV3 LejOS project.
 * It is serialized, sent over the bluetooth connection and deserialized in the other end.
 */
public class RobotMessage implements Serializable {
    private static final long serialVersionUID = 1273756063252153508L;
    private int mVerticalRotation = 0;
    private float mVerticalSpeed = 800f;
    private int mVerticalAcceleration = 7000;

    private int mHorizontalRotation = 0;
    private float mHorizontalSpeed = 1000f;
    private int mHorizontalAcceleration = 10000;


    public RobotMessage() {
        this(0, 0);
    }

    public RobotMessage(int vertical, int horizontal) {
        this.mVerticalRotation = vertical;
        this.mHorizontalRotation = horizontal;
    }


    /**
     * Clones the message.
     */
    public RobotMessage clone() {
        RobotMessage state = new RobotMessage();
        state.mVerticalRotation = this.mVerticalRotation;
        state.mVerticalSpeed = this.mVerticalSpeed;
        state.mVerticalAcceleration = this.mVerticalAcceleration;
        state.mHorizontalRotation = this.mHorizontalRotation;
        state.mHorizontalSpeed = this.mHorizontalSpeed;
        state.mHorizontalAcceleration = this.mHorizontalAcceleration;
        return state;
    }

    public int getHorizontalRotation() {
        return mHorizontalRotation;
    }
    public float getHorizontalSpeed() {
        return mHorizontalSpeed;
    }
    public int getHorizontalAcceleration() {
        return mHorizontalAcceleration;
    }

    public int getVerticalRotation() {
        return mVerticalRotation;
    }
    public float getVerticalSpeed() {
        return mVerticalSpeed;
    }
    public int getVerticalAcceleration() {
        return mVerticalAcceleration;
    }

    public void setHorizontalRotation(int value) {
        mHorizontalRotation = value;
    }
    public void setVerticalRotation(int value) {
        mVerticalRotation = value;
    }

    public void setVerticalParameters(int rotation, float speed, int acceleration) {
        mVerticalRotation = rotation;
        mVerticalSpeed = speed;
        mVerticalAcceleration = acceleration;
    }

    public void setVerticalParameters(float speed, int acceleration) {
        mVerticalSpeed = speed;
        mVerticalAcceleration = acceleration;
    }


    public void setHorizontalParameters(int rotation, float speed, int acceleration) {
        mHorizontalRotation = rotation;
        mHorizontalSpeed = speed;
        mHorizontalAcceleration = acceleration;
    }

    public void setHorizontalParameters(float speed, int acceleration) {
        mHorizontalSpeed = speed;
        mHorizontalAcceleration = acceleration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RobotMessage state = (RobotMessage) o;
        if(mVerticalRotation != state.mVerticalRotation
                || mVerticalSpeed != state.mVerticalSpeed
                || mVerticalAcceleration != state.mVerticalAcceleration) return false;
        if(mHorizontalRotation != state.mHorizontalRotation
                || mHorizontalSpeed != state.mHorizontalSpeed
                || mHorizontalAcceleration != state.mHorizontalAcceleration) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 8;
        hash = hash * 6 + mVerticalRotation;
        hash = hash * 8 + Float.floatToIntBits(mVerticalSpeed);
        hash = hash * 10 + mVerticalAcceleration;
        hash = hash * 12 + mHorizontalRotation;
        hash = hash * 14 + Float.floatToIntBits(mHorizontalSpeed);
        hash = hash * 16 + mHorizontalAcceleration;
        return hash;
    }

    @Override
    public String toString() {
        return "V" + mVerticalRotation + ":H" + mHorizontalRotation;
    }
}
