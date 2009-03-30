/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.wiimote;

import eit.headtracking.*;
import ca.ubc.cs.wiimote.Wiimote;
import ca.ubc.cs.wiimote.WiimoteDiscoverer;
import ca.ubc.cs.wiimote.WiimoteDiscoveryListener;
import ca.ubc.cs.wiimote.event.WiiAccelEvent;
import ca.ubc.cs.wiimote.event.WiiButtonEvent;
import ca.ubc.cs.wiimote.event.WiiIREvent;
import ca.ubc.cs.wiimote.event.WiimoteListener;
import java.util.HashMap;

/**
 *
 * @author vegar
 */
public class WiimoteHeadTracker extends SingleSourceHeadTracker implements WiimoteDiscoveryListener, WiimoteListener {
    private Wiimote wiimote;

    private WiimoteDiscoverer discoverer;
    private boolean calibrating = true;

    private WiiIREvent[] event = new WiiIREvent[2];

    public WiimoteHeadTracker() {
        this.point[0] = new Point();
        this.point[1] = new Point();
        this.point[2] = new Point();
    }

    public void start() {
        discoverer = WiimoteDiscoverer.getWiimoteDiscoverer();
        discoverer.addWiimoteDiscoveryListener(this);
        discoverer.startWiimoteSearch();
    }

    public void stop() {
        wiimote.cleanup();
    }

    public void wiimoteDiscovered(Wiimote w) {
        this.wiimote = w;
        this.wiimote.enableIREvents();
        this.wiimote.addListener(this);
        discoverer.stopWiimoteSearch();
        this.wiimote.vibrate(200);
    }

    public void calibrate() {
        for (int i = 0; i < event.length; i++) {
            event[i] = null;
        }
        this.calibrating = true;
    }
    // Returns true when all points calibrated

    private boolean calibratePoint(WiiIREvent light) {
        WiiIREvent temp = null;
        for (int i = 0; i < event.length; i++) { // Start from the left
            if (event[i] == null) {
                event[i] = light;
                return i == event.length - 1; // If we're done i == 2
            } else {
                if (event[i].getLightSource() == light.getLightSource()) { // Point already assigned
                    event[i] = light;
                } else if (light.getX() < event[i].getX()) { // Assigned but wrong, must interchange
                    temp = light;
                    light = event[i];
                    event[i] = temp;
                }
            }
        }
        return true;
    }

    public void wiiIRInput(WiiIREvent light) {
        if (calibrating) {
            boolean doneCalibrating = calibratePoint(light);
            calibrating = !doneCalibrating;
            if (doneCalibrating) {
                double angle = Math.acos(.5 / headZ) - Math.PI / 2;//angle of head to screen
                if (!cameraIsAboveScreen) {
                    angle = -angle;
                }
                cameraVerticalAngle = (float) ((angle - relativeVerticalAngle));//absolute camera angle
            }
        } else {
            for (int i = 0; i < event.length; i++) {
                if (light.getLightSource() == event[i].getLightSource()) {
                    point[i].x = light.getX();
                    point[i].y = light.getY();
                }
            }
            calculate();
        }
    }

    public void wiiAccelInput(WiiAccelEvent arg0) {
        //System.out.println("\twiiAccelInput (" + arg0.x + ", " + arg0.y + ", " + arg0.z);
    }

    public void wiiButtonPress(WiiButtonEvent e) {
        wiimote.vibrate(200);
    }

    public void wiiButtonRelease(WiiButtonEvent e) {
    }
}
