package eit.point;
/*
 * @(#)FrameAccess.java	1.5 01/03/13
 *
 * Copyright (c) 1999-2001 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Sun grants you ("Licensee") a non-exclusive, royalty free, license to use,
 * modify and redistribute this software in source and binary code form,
 * provided that i) this copyright notice and license appear on all copies of
 * the software; and ii) Licensee does not utilize the software in a manner
 * which is disparaging to Sun.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * This software is not designed or intended for use in on-line control of
 * aircraft, air traffic, aircraft navigation or aircraft communications; or in
 * the design, construction, operation or maintenance of any nuclear
 * facility. Licensee represents and warrants that it will not use or
 * redistribute the Software for such purposes.
 */

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import javax.media.*;
import javax.media.control.TrackControl;
import javax.media.Format;
import javax.media.format.*;
import javax.media.protocol.DataSource;

/**
 * Sample program to access individual video frames by using a "pass-thru"
 * codec. The codec is inserted into the data flow path. As data pass through
 * this codec, a callback is invoked for each frame of video data.
 */
public class VideoGrabber extends Frame implements ControllerListener, KeyListener {

	Processor p;
	Object waitSync = new Object();
	boolean stateTransitionOK = true;
	private TrackerCodec trackerCodec;
	static private VideoGrabber grabber; // singleton
	
	static float[][] getCoords() {
		if (grabber == null)
			return null;
		
		return grabber.trackerCodec.getCoords();
	}

	/**
	 * Given a media locator, create a processor and use that processor as a
	 * player to playback the media.
	 * 
	 * During the processor's Configured state, two "pass-thru" codecs,
	 * PreAccessCodec and PostAccessCodec, are set on the video track. These
	 * codecs are used to get access to individual video frames of the media.
	 * 
	 * Much of the code is just standard code to present media in JMF.
	 */
	public boolean open(MediaLocator ml) {

		DataSource src = null;
		try {
			src = Manager.createDataSource(ml);
		} catch (Exception e1) {
			e1.printStackTrace();
			System.exit(0);
		}
		
		try {
			//p = Manager.createProcessor(ml);
			p = Manager.createProcessor(src);
		} catch (Exception e) {
			System.err.println("Failed to create a processor from the given url: " + e);
			return false;
		}

		p.addControllerListener(this);
		
		// Put the Processor into configured state.
		p.configure();
		if (!waitForState(p.Configured)) {
			System.err.println("Failed to configure the processor.");
			return false;
		}

		// So I can use it as a player.
		p.setContentDescriptor(null);

		// Obtain the track controls.
		TrackControl tc[] = p.getTrackControls();

		if (tc == null) {
			System.err.println("Failed to obtain track controls from the processor.");
			return false;
		}

		// Search for the track control for the video track.
		TrackControl videoTrack = null;

		System.out.printf("Available formats (%d):\n", tc.length);
		for (int i = 0; i < tc.length; i++)
			System.out.println(i + ": " + tc[i].getFormat().toString());
		
		for (int i = 0; i < tc.length; i++) {
			if (tc[i].getFormat() instanceof VideoFormat) {
				videoTrack = tc[i];
				break;
			}
		}

		if (videoTrack == null) {
			System.err.println("The input media does not contain a video track.");
			return false;
		}

		System.out.println("Video format: " + videoTrack.getFormat());

		// Instantiate and set the frame access codec to the data flow path.
		try {
			trackerCodec = new TrackerCodec();
			Codec codec[] = {trackerCodec};
			videoTrack.setCodecChain(codec);
		} catch (UnsupportedPlugInException e) {
			System.err.println("The process does not support effects.");
		}

		// Realize the processor.
		p.prefetch();
		if (!waitForState(p.Prefetched)) {
			System.err.println("Failed to realize the processor.");
			return false;
		}

		// Display the visual & control component if there's one.

		setLayout(new BorderLayout());

		Component cc;

		Component vc;
		if ((vc = p.getVisualComponent()) != null) {
			add("Center", vc);
		}

		if ((cc = p.getControlPanelComponent()) != null) {
			add("South", cc);
		}

		// Start the processor.
		p.start();

		setVisible(true);

		return true;
	}

	public void addNotify() {
		super.addNotify();
		pack();
	}

	/**
	 * Block until the processor has transitioned to the given state. Return
	 * false if the transition failed.
	 */
	boolean waitForState(int state) {
		synchronized (waitSync) {
			try {
				while (p.getState() != state && stateTransitionOK)
					waitSync.wait();
			} catch (Exception e) {
			}
		}
		return stateTransitionOK;
	}

	/**
	 * Controller Listener.
	 */
	public void controllerUpdate(ControllerEvent evt) {

		if (evt instanceof ConfigureCompleteEvent
				|| evt instanceof RealizeCompleteEvent
				|| evt instanceof PrefetchCompleteEvent) {
			synchronized (waitSync) {
				stateTransitionOK = true;
				waitSync.notifyAll();
			}
		} else if (evt instanceof ResourceUnavailableEvent) {
			synchronized (waitSync) {
				stateTransitionOK = false;
				waitSync.notifyAll();
			}
		} else if (evt instanceof EndOfMediaEvent) {
			p.close();
			System.exit(0);
		}
	}

	/**
	 * Main program
	 */
	public static void main(String[] args) {

		String url = "vfw:Microsoft WDM Image Capture (Win32):0";

		MediaLocator ml;

		if ((ml = new MediaLocator(url)) == null) {
			System.err.println("Cannot build media locator from: " + url);
			System.exit(0);
		}

		grabber = new VideoGrabber();
		
		grabber.addKeyListener(grabber);

		if (!grabber.open(ml))
			System.exit(0);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyChar() == ' ');
			trackerCodec.calibrate();
	}

	@Override
	public void keyReleased(KeyEvent arg0) {}

	@Override
	public void keyTyped(KeyEvent arg0) {}

	
}
