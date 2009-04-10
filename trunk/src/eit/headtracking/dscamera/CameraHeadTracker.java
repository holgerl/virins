package eit.headtracking.dscamera;

import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.humatic.dsj.DSCapture;
import de.humatic.dsj.DSFilterInfo;
import de.humatic.dsj.DSFiltergraph;
import de.humatic.dsj.DSJException;

import eit.headtracking.Point;
import eit.headtracking.SingleSourceHeadTracker;
import eit.headtracking.Test;
import eit.headtracking.dscamera.gui.CalibrateKeyListener;
import eit.headtracking.dscamera.gui.CameraHeadTrackerGUI;
import eit.headtracking.dscamera.util.TrackingUtil;

public class CameraHeadTracker extends SingleSourceHeadTracker implements
java.beans.PropertyChangeListener {
	
	class CallbackThread extends Thread {
		private CameraHeadTracker cht;
		public int fps = 15;
		public boolean running = true;

		public CallbackThread(CameraHeadTracker cht) {
			super();
			this.cht = cht;	
		}

		public void run() {
			while (running) {
				try {
					sleep(1000/fps);
				} catch (Throwable t) {}
				cht.updatePoints();
			}
		}
	}
	
	class PrintThread extends Thread {
		private DSCapture graph;
		public double printsPerSec = 0.25;

		public PrintThread(DSCapture graph) {
			super();
			this.graph = graph;	
		}

		public void run() {
			while (true) {
				try {
					sleep((int) (1000/printsPerSec));
				} catch (Throwable t) {}
				System.out.println(TrackingUtil.bufferToIntensityString(graph.getImage()));
			}
		}
	}

	public DSCapture graph;
	private CameraHeadTrackerGUI gui;

	public static void main(String args[]) {
		CameraHeadTracker t = new CameraHeadTracker();
		Test test = new Test(t);
		new Thread(test).start();
	}

	public CameraHeadTracker() {
		this.screenHeightinMM = 120;
		this.point[LEFT] = new Point();
		this.point[CENTER] = new Point();
		this.point[RIGHT] = new Point();
		
		DSFilterInfo dsi = DSCapture.queryDevices()[0][1];
		System.out.println("Using device: "+dsi);
		graph = new DSCapture(DSFiltergraph.RENDER_NATIVE, dsi, false,
				DSFilterInfo.doNotRender(), this);
		
		this.XMAX = graph.getMediaDimension().width;
		this.YMAX = graph.getMediaDimension().height;
		
		new CallbackThread(this).start();
		//new PrintThread(this.graph).start();
		
		gui = new CameraHeadTrackerGUI(this);
	}

	public void propertyChange(java.beans.PropertyChangeEvent pe) {
		System.out.println("Received event or callback from "
				+ pe.getPropagationId());
	}

	@Override
	public void start() {
	}

	@Override
	public void stop() {
	}

	@Override
	public void calibrate() {
		Point[] points = TrackingUtil.findPoints(graph.getImage());
		point[LEFT] = points[0];
		point[CENTER] = points[1];
		point[RIGHT] = points[2];
		//printPoints();
	}
	
	private void printPoints() {
		System.out.printf("LEFT=%s, CENTER=%s, RIGHT=%s\n", point[LEFT], point[CENTER], point[RIGHT]);
	}

	private void updatePoints() {
		Point[] points = {point[LEFT], point[CENTER], point[RIGHT]};
		BufferedImage buffer = null;
		try {
			buffer = graph.getImage();
		} catch (DSJException e) {}
		if (buffer != null)
			TrackingUtil.updatePoints(points, buffer);
		calculate();
		//System.out.println("Pos(xyz): (" + this.getHeadX() + ", " + this.getHeadY() + ", " + this.getHeadZ() + ")");
	}
}
