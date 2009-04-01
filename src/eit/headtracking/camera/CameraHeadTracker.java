package eit.headtracking.camera;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.media.Buffer;
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
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.humatic.dsj.DSCapture;
import de.humatic.dsj.DSFilterInfo;
import de.humatic.dsj.DSFiltergraph;

import eit.headtracking.HeadTracker;
import eit.headtracking.Point;
import eit.headtracking.SingleSourceHeadTracker;
import eit.headtracking.Test;
import eit.headtracking.camera.gui.CalibrateKeyListener;
import eit.headtracking.camera.gui.ProcessorGUI;


public class CameraHeadTracker extends SingleSourceHeadTracker implements java.beans.PropertyChangeListener {

	static Processor processor;
	static Object waitSync = new Object();
	static boolean stateTransitionOK = true;
	
	private static DSCapture graph;
	
	TrackerCodec trackerCodec;
	ProcessorGUI frame;
	
    public static void main(String args[]) {
        CameraHeadTracker t = new CameraHeadTracker();
        Test test = new Test(t); // Test kaller headtracker start.
        new Thread(test).start();
    }

    public CameraHeadTracker() {
        this.XMAX = 1280; // TODO: Hard coded = bad
        this.YMAX = 1080;
        this.screenHeightinMM = 150;
        this.point[LEFT] = new Point();
        this.point[LEFT+1] = new Point();
        this.point[LEFT+2] = new Point();
    }
    
	public void propertyChange(java.beans.PropertyChangeEvent pe) {
		System.out.println("Received event or callback from "+pe.getPropagationId());
	}
    
    @Override
    public void start() {
    	if (true)
    		startDS();
    	else
    		startJMF();
    }
    
    private void startDS() {
    	DSFilterInfo[][] dsi = DSCapture.queryDevices();
		graph = new DSCapture(DSFiltergraph.RENDER_NATIVE, dsi[0][0], false, DSFilterInfo.doNotRender(), this);
		
		JFrame f = new JFrame();
		f.setSize(1920/4, 1080/4);
		JLabel label = new JLabel();
		f.setContentPane(label);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addKeyListener(new CalibrateKeyListener(this));
		
		trackerCodec = new TrackerCodec();
		trackerCodec.addListener(this);
		
	    class LocalThread extends Thread {
	    	JLabel label;
	    	JFrame f;
	    	public LocalThread(JLabel label, JFrame f) {
	    		super();
	    		this.label = label;
	    		this.f = f;
	    	}
	        public void run() {
	        	while (true) {
	        		try {
	    				if (graph != null && graph.getData() != null) {
	    					BufferedImage img = graph.getImage();
	    					//label.setIcon(new ImageIcon(img.getScaledInstance(f.getWidth(), f.getHeight(), BufferedImage.SCALE_FAST)));
	    					Buffer b = new Buffer();
	    					b.setData(graph.getData());
	    					b.setLength(graph.getDataSize());
	    					trackerCodec.accessFrame(b);
	    				}
	        		} catch (Throwable t) {}
	    		}
	        }
	    }
	    Thread thread = new LocalThread(label, f);
	    thread.start();
    }
    
    private void startJMF() {
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
        processor.stop();
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
    		//System.out.printf("(%3.0f,%3.1f) (%3.0f,%3.0f)\n", this.point[LEFT].x, this.point[LEFT].y, this.point[LEFT+1].x, this.point[LEFT+1].y);
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
