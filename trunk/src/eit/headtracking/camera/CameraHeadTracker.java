package eit.headtracking.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.Codec;
import javax.media.ConfigureCompleteEvent;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.PrefetchCompleteEvent;
import javax.media.Processor;
import javax.media.RealizeCompleteEvent;
import javax.media.ResourceUnavailableEvent;
import javax.media.UnsupportedPlugInException;
import javax.media.control.TrackControl;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;

import eit.headtracking.HeadTracker;
import eit.headtracking.Point;
import eit.headtracking.SingleSourceHeadTracker;
import eit.headtracking.Test;
import eit.headtracking.camera.gui.CalibrateKeyListener;
import eit.headtracking.camera.gui.ProcessorGUI;


public class CameraHeadTracker extends SingleSourceHeadTracker {

	static Processor processor;
	static Object waitSync = new Object();
	static boolean stateTransitionOK = true;
	
	TrackerCodec trackerCodec;
	ProcessorGUI frame;
	
    public static void main(String args[]) {
        CameraHeadTracker t = new CameraHeadTracker();
        Test test = new Test(t); // Test kaller headtracker start.
        new Thread(test).start();
    }

    public CameraHeadTracker() {
        this.XMAX = 640; // TODO: Hard coded = bad
        this.YMAX = 480;
        this.screenHeightinMM = 150;
        this.point[LEFT] = new Point();
        this.point[LEFT+1] = new Point();
        this.point[LEFT+2] = new Point();
    }
    @Override
    public void start() {
    	String url = "vfw:Microsoft WDM Image Capture (Win32):0";
        //String url = "/dev/video0";

		MediaLocator ml = new MediaLocator(url);

		if (ml == null) throw new Error("Cannot build media locator from: " + url);

		DataSource src;
		try {
			src = Manager.createDataSource(ml);
			//p = Manager.createProcessor(ml);
			processor = Manager.createProcessor(src);
		} catch (Exception e) {
			throw new Error(e);
		}
		
		processor.addControllerListener(new CtrlListener());
		
		// Put the Processor into configured state.
		processor.configure();
		if (!waitForState(Processor.Configured)) throw new Error("Failed to configure the processor.");

		processor.setContentDescriptor(null);
		TrackControl tc[] = processor.getTrackControls();

		if (tc == null) throw new Error("Failed to obtain track controls from the processor.");

		TrackControl videoTrack = null;
		System.out.printf("Available formats (%d):\n", tc.length);
		for (int i = 0; i < tc.length; i++)
			System.out.println(i + ": " + tc[i].getFormat().toString());
		
		// Search for the track control for the video track.
		for (int i = 0; i < tc.length; i++) {
			if (tc[i].getFormat() instanceof VideoFormat) {
				videoTrack = tc[i];
				break;
			}
		}

		if (videoTrack == null) throw new Error("The input media does not contain a video track.");

		System.out.println("Video format: " + videoTrack.getFormat());

		trackerCodec = new TrackerCodec();
		trackerCodec.addListener(this);
		Codec codecs[] = {trackerCodec};
		
		try {
			videoTrack.setCodecChain(codecs);
		} catch (UnsupportedPlugInException e) {
			throw new Error("The process does not support effects.");
		}

		processor.prefetch();
		if (!waitForState(Processor.Prefetched)) throw new Error("Failed to realize the processor.");

		processor.start();

		frame = new ProcessorGUI(processor);
		frame.addKeyListener(new CalibrateKeyListener(this));
		frame.setVisible(true);
    }

    @Override
    public void stop() {
    	frame.setVisible(false);
    }

    @Override
    public void calibrate() {
    	trackerCodec.calibrate();
    }

    public void updatePoints() {
    	if (trackerCodec.points.size() >= 2) {
    		this.point[LEFT].x = trackerCodec.points.get(0).coordX;
    		this.point[LEFT].y = trackerCodec.points.get(0).coordY;
    		this.point[LEFT+1].x = trackerCodec.points.get(1).coordX;
    		this.point[LEFT+1].y = trackerCodec.points.get(1).coordY;
    		calculate(); // Beregner virtuelle koordinater fra pixelkoordinater
    	}
    }
    
	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 */
	private static boolean waitForState(int state) {
		synchronized (waitSync) {
			try {
				while (processor.getState() != state && stateTransitionOK)
					waitSync.wait();
			} catch (Exception e) {}
		}
		return stateTransitionOK;
	}

}
