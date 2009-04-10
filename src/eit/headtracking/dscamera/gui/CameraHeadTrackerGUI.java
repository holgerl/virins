package eit.headtracking.dscamera.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import javax.media.Processor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import de.humatic.dsj.DSCapture;

import eit.headtracking.dscamera.CameraHeadTracker;
import eit.headtracking.dscamera.util.TrackingUtil;

public class CameraHeadTrackerGUI extends JFrame {
	
	class PaintThread extends Thread {
		private JFrame frame;
		public double fps = 15;

		public PaintThread(JFrame frame) {
			super();
			this.frame = frame;	
		}

		public void run() {
			while (true) {
				try {
					sleep((int) (1000/fps));
				} catch (Throwable t) {}
				frame.repaint();
			}
		}
	}
	
	private JLabel label;
	private CameraHeadTracker cht;
	
	public CameraHeadTrackerGUI(CameraHeadTracker cht) {
		this.cht = cht;
		this.addKeyListener(new CalibrateKeyListener(cht));
		label = new JLabel();
		this.setContentPane(label);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize((int) cht.XMAX, (int) cht.YMAX);
		this.addKeyListener(new ShowCamKeyListener(this));
		new PaintThread(this).start();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D ga = (Graphics2D)g;
		ga.setPaint(Color.red);
		if (cht.point[0] != null) ga.draw(new Rectangle2D.Double(cht.point[0].x, cht.point[0].y, 20, 20));
		ga.setPaint(Color.green);
		if (cht.point[1] != null) ga.draw(new Rectangle2D.Double(cht.point[1].x, cht.point[1].y, 20, 20));
		ga.setPaint(Color.blue);
		if (cht.point[2] != null) ga.draw(new Rectangle2D.Double(cht.point[2].x, cht.point[2].y, 20, 20));
	}

	public void setShowingCam(boolean b) {
		if (b) {
			label.setIcon(new ImageIcon(cht.graph.getImage()));
		} else {
			label.setIcon(null);
		}
	}
}
