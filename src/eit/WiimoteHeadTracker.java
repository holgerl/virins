/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit;

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
public class WiimoteHeadTracker implements HeadTracker, WiimoteDiscoveryListener, WiimoteListener {

    private static final double XMAX = 1024;
    private static final double YMAX = 768;

    // float dotDistanceInMM = 5.75f*25.4f;
    private double dotDistanceInMM = 8.5f * 25.4f;//width of the wii sensor bar
    private double screenHeightinMM = 600.0f; // 7.5f * 25.4f; //Laptopskjermen min
    private double radiansPerPixel = (float) (Math.PI / 4) / 1024.0f; //45 degree field of view with a 1024x768 camera
    private double movementScaling = 1.0f;
    private boolean cameraIsAboveScreen = true;
    public double headX;
    public double headY;
    public double headZ;
    private double rotY;
    private Wiimote wiimote;
    private static final int LEFT = 0;
    private static final int CENTER = 1;
    private static final int RIGHT = 2;
    private WiiIREvent[] point = new WiiIREvent[2];
    private WiimoteDiscoverer discoverer;
    //private int[] dots = new int[20];
    //private double radius = 4.25f * 25.4f; // halve av avstanden mellom to leds
    private boolean calibrating = true;
    private double relativeVerticalAngle = 0;
    private double cameraVerticaleAngle = 0;

    public WiimoteHeadTracker() {
    }

    private void calculateSensorbar() {
        double dx = point[LEFT].getX() - point[LEFT + 1].getX();
        double dy = point[LEFT].getY() - point[LEFT + 1].getY();
        double pointDist = (float) Math.sqrt(dx * dx + dy * dy);
        double angle = radiansPerPixel * pointDist / 2.0f;
        //in units of screen hieght since the box is a unit cube and box hieght is 1
        headZ = movementScaling * ((dotDistanceInMM / 2.0f) / Math.tan(angle)) / screenHeightinMM;
        double avgX = (point[LEFT].getX() + point[LEFT + 1].getX()) / 2.0f;
        double avgY = (point[LEFT].getY() + point[LEFT + 1].getY()) / 2.0f;
        //should  calaculate based on distance

        headX = movementScaling * Math.sin(radiansPerPixel * (avgX - 512) * headZ);
        relativeVerticalAngle = (avgY - 384) * radiansPerPixel;//relative angle to camera axis
        //headY = movementScaling * Math.sin(relativeVerticalAngle) * headZ;

        if (cameraIsAboveScreen) {
            headY = .5f + (movementScaling * Math.sin(relativeVerticalAngle) * headZ);
        } else {
            headY = -.5f + (movementScaling * Math.sin(relativeVerticalAngle) * headZ);
        }
    }

    public float getHeadX() {
        return (float) headX;
    }

    public float getHeadY() {
        return (float) headY;
    }

    public float getHeadZ() {
        return (float) headZ;
    }

    public float getRotX() {
        return 0.0f;
    }

    public float getRotY() {
        return (float) rotY;
    }

    public float getRotZ() {
        return 0.0f;
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
        for (int i = 0; i < point.length; i++) {
            point[i] = null;
        }
        this.calibrating = true;
    }
    // Returns true when all points calibrated

    private boolean calibratePoint(WiiIREvent light) {
        WiiIREvent temp = null;
        for (int i = 0; i < point.length; i++) { // Start from the left
            if (point[i] == null) {
                point[i] = light;
                return i == point.length - 1; // If we're done i == 2
            } else {
                if (point[i].getLightSource() == light.getLightSource()) { // Point already assigned
                    point[i] = light;
                } else if (light.getX() < point[i].getX()) { // Assigned but wrong, must interchange
                    temp = light;
                    light = point[i];
                    point[i] = temp;
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
                cameraVerticaleAngle = (float) ((angle - relativeVerticalAngle));//absolute camera angle
            }
        } else {
            for (int i = 0; i < point.length; i++) {
                if (light.getLightSource() == point[i].getLightSource()) {
                    point[i] = light;
                }
            }
            calculateSensorbar();
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
