package eit.headtracking.dscamera.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import eit.headtracking.HeadTracker;

public class CalibrateKeyListener implements KeyListener {
	HeadTracker tracker;
	
	public CalibrateKeyListener(HeadTracker tracker) {
		this.tracker = tracker;
	}
	
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == ' ')
			tracker.calibrate();
	}

	public void keyReleased(KeyEvent arg0) {}

	public void keyTyped(KeyEvent arg0) {}
}
