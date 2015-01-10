package com.oskarkoli.timelapserandroid.movement;

import com.oskarkoli.timelapserandroid.RobotMessage;

/**
 * Point in movement plan
 */
public class MovementPlanPoint {

    public int verticalRotation = 0;
    public int horizontalRotation = 0;

    public MovementPlanPoint() {
        this(0, 0);
    }

    // Vector style 'length' of the movement point. Used in interpolation.
    public float length() {
        return (float) Math.sqrt((double) (verticalRotation * verticalRotation + horizontalRotation * horizontalRotation));
    }

    public MovementPlanPoint(int verticalRotation, int horizontalRotation) {
        this.verticalRotation = verticalRotation;
        this.horizontalRotation = horizontalRotation;
    }

    public RobotMessage getMessage() {
        RobotMessage message = new RobotMessage(verticalRotation, horizontalRotation);

        return message;
    }

    public String toString() {
        return "Vertical: " + verticalRotation + " Horizontal: " + horizontalRotation;
    }

}
