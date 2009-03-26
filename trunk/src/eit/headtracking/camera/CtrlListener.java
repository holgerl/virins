package eit.headtracking.camera;

import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;

class CtrlListener implements ControllerListener {
	@Override
	public void controllerUpdate(ControllerEvent evt) {
		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (CameraHeadTracker.waitSync) {
				CameraHeadTracker.stateTransitionOK = true;
				CameraHeadTracker.waitSync.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (CameraHeadTracker.waitSync) {
				CameraHeadTracker.stateTransitionOK = false;
				CameraHeadTracker.waitSync.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			CameraHeadTracker.processor.close();
			System.exit(0);
		}
	}
}
