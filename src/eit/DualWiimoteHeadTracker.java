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

/**
 *
 * @author vegar
 */
public class DualWiimoteHeadTracker implements HeadTracker, WiimoteDiscoveryListener, WiimoteListener {
    WiimoteDiscoverer discoverer;
    Wiimote leftWiimote;
    Wiimote rightWiimote;
    WiiIREvent leftPoint = new WiiIREvent(null, 0, 0, 0, 0);
    WiiIREvent rightPoint = leftPoint;

    double leftOffsetRadians = Math.PI / 2.0;
    double rightOffsetRadians = Math.PI / 2.0;
    double wiimoteDistanceInMM = 1200.0;
    double screenHeightInMM = 7.5f * 25.4f;

    double calibrationDistanceInMM = 2000.0;

    double radiansPerPixel = (Math.PI / 4.0) / 1024.0;

    double headX, headY, headZ;
    double relativeVerticalAngle;

    private void calculate() {
        if(leftPoint == null || rightPoint == null) return;
        headZ = wiimoteDistanceInMM / ( 1.0/Math.tan(leftAngle() + leftOffsetRadians) + 1.0/Math.tan(rightAngle() + rightOffsetRadians));
        double avgY = (leftPoint.getY() + rightPoint.getY()) / 2.0;
        relativeVerticalAngle = (avgY - 384) * radiansPerPixel;
        headY = Math.sin(relativeVerticalAngle) * headZ;
        headX = wiimoteDistanceInMM / 2.0 - headZ/Math.tan(leftAngle() + leftOffsetRadians);
    }

    public void start() {
        discoverer = WiimoteDiscoverer.getWiimoteDiscoverer();
        discoverer.addWiimoteDiscoveryListener(this);
        discoverer.startWiimoteSearch();
    }

    public void stop() {
    }

    public void calibrate() {
        System.out.println("Calibrating");
        double angle = Math.atan2(calibrationDistanceInMM, wiimoteDistanceInMM / 2.0);
        leftOffsetRadians = angle - radiansPerPixel* (double)(leftPoint.getX());
        rightOffsetRadians = angle - radiansPerPixel * (double)(1024 - rightPoint.getX());
    }
    
    public double leftAngle() {
        return radiansPerPixel* (double)(leftPoint.getX());

    }
    public double rightAngle() {
        return radiansPerPixel * (double)(1024 - rightPoint.getX());
    }

    public float getHeadX() {
        return (float)headX;
    }

    public float getHeadY() {
        return (float)headY;
    }

    public float getHeadZ() {
        return (float)headZ;
    }

    public float getRotX() {
        return 0.0f;
    }

    public float getRotY() {
        return 0.0f;
    }

    public float getRotZ() {
        return 0.0f;
    }

    public void wiimoteDiscovered(Wiimote w) {
        if(leftWiimote == null) {
            System.out.println("Left wiimote discovered");
            leftWiimote = w;
        } else {
            System.out.println("Right wiimote discovered");
            rightWiimote = w;
            discoverer.stopWiimoteSearch();
        }
        w.addListener(this);
        w.vibrate(200);
    }

    public void wiiIRInput(WiiIREvent e) {
        if(e.getWiimote() == leftWiimote) {
            leftPoint = e;
        } else {
            rightPoint = e;
        }
        calculate();
    }

    public void wiiAccelInput(WiiAccelEvent e) {
    }

    public void wiiButtonPress(WiiButtonEvent e) {
        e.getWiimote().vibrate(200);
        e.getWiimote().enableIREvents();
    }

    public void wiiButtonRelease(WiiButtonEvent e) {
    }

}
