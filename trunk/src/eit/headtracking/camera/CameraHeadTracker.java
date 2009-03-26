/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking.camera;

import eit.headtracking.SingleSourceHeadTracker;

/**
 *
 * @author Holgiboy
 */
public class CameraHeadTracker extends SingleSourceHeadTracker {

    public CameraHeadTracker() {
        this.XMAX = 1024;
        this.YMAX = 768;
        this.screenHeightinMM = 600;
    }
    @Override
    public void start() {
        // Start tracking
    }

    @Override
    public void stop() {
    }

    @Override
    public void calibrate() {
    }

    // Eksempel
    public void updatePoints() {
        //this.point[LEFT].x = kamerapunkt1.x;
        //this.point[LEFT].y = kamerapunkt1.y;
        //this.point[LEFT+1].x = kamerapunkt2.x;
        //this.point[LEFT+1].y = kamerapunkt2.y;
    }

}
