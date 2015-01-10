package com.oskarkoli.timelapserandroid;

import com.oskarkoli.timelapserandroid.movement.MovementPlan;

/**
 *
 */
public class Globals {
    public static final int OVERLAY_TEXT = 901;
    public static final int MSG_START = 920;
    public static final int MSG_PAUSE = 921;

    // Bad style; breaks encapsulation. But idealism should not stand before a working program, especially when the program will be discarded in 2 weeks.
    public static MovementPlan movementPlan;

}
