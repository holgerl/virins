/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking.camera;

import eit.headtracking.SingleSourceHeadTracker;
import eit.headtracking.Test;

/**
 *
 * @author Holgiboy
 */
public class CameraHeadTracker extends SingleSourceHeadTracker {

    public static void main(String args[]) {
        CameraHeadTracker t = new CameraHeadTracker();
        Test test = new Test(t); // Test kaller headtracker start.
        new Thread(test).start();
    }

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
