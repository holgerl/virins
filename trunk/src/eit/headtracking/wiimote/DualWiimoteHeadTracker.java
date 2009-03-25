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

/**
 *
 * @author vegar
 */
public class DualWiimoteHeadTracker extends DualSourceHeadTracker implements WiimoteDiscoveryListener, WiimoteListener {
    WiimoteDiscoverer discoverer;
    Wiimote leftWiimote;
    Wiimote rightWiimote;

    public void start() {
        discoverer = WiimoteDiscoverer.getWiimoteDiscoverer();
        discoverer.addWiimoteDiscoveryListener(this);
        discoverer.startWiimoteSearch();
    }

    public void stop() {
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
            leftPoint.x = e.getX();
            leftPoint.y = e.getY();
        } else {
            rightPoint.x = e.getX();
            rightPoint.y = e.getY();
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
