package eit.headtracking.dscamera.gui;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import eit.headtracking.HeadTracker;

public class ShowCamKeyListener implements KeyListener {
	CameraHeadTrackerGUI gui;
	
	public ShowCamKeyListener(CameraHeadTrackerGUI gui) {
		this.gui = gui;
	}
	
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == 'p')
			gui.setShowingCam(true);
	}

	public void keyReleased(KeyEvent arg0) {
		if (arg0.getKeyChar() == 'p')
			gui.setShowingCam(false);
	}

	public void keyTyped(KeyEvent arg0) {}
}
