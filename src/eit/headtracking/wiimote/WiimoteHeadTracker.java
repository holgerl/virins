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
    private WiiIREvent[] event = new WiiIREvent[2];

    public WiimoteHeadTracker() {
        this.point[0] = new Point();
        this.point[1] = new Point();
    //this.point[2] = new Point();
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

    public void wiiIRInput(WiiIREvent light) {
        if (event[0] == null || event[0].getLightSource() == light.getLightSource()) {
            event[0] = light;
            point[0].x = event[0].getX();
            point[0].y = event[0].getY();
        } else {
            event[1] = light;
            point[1].x = event[1].getX();
            point[1].y = event[1].getY();
        }
        calculate();
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
