package eit.headtracking.camera;
import java.util.ArrayList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;

import eit.headtracking.camera.dataholders.TrackedPoint;
import eit.headtracking.camera.util.TrackingUtil;

public class TrackerCodec extends GenericAccessCodec {
	private int counter = 0;
	private int frameWidth;
	private int frameHeight;
	public ArrayList<TrackedPoint> points = new ArrayList<TrackedPoint>();
	Buffer lastFrame;
	
	private CameraHeadTracker cht;
	
	float[][] getCoords() {
		return null;
	}

	// We'll advertize as supporting all video formats.
	public TrackerCodec() {
		supportedIns = new Format[] {new RGBFormat()};
	}
	
	public void calibrate() {
		points = TrackingUtil.findPoints(lastFrame, 2, frameWidth, frameHeight);
	}

	/**
	 * Callback to access individual video frames.
	 */
	void accessFrame(Buffer frame) {
		if (lastFrame == null) {
			//int dims[] = TrackingUtil.getFrameDimensions(frame);
			//frameWidth = dims[0];
			//frameHeight = dims[1];
			frameWidth = 1920;
			frameHeight = 1080;
		}
		
		counter++;
		this.lastFrame = frame;
		cht.updatePoints();

		// Printout
		if (true && counter % 15 == 0) {
			String type = frame.getData().getClass().getSimpleName();
			if (points.size() > 0) System.out.printf("Coords: %f,%f\n", points.get(0).normX, points.get(0).normY);
			System.out.printf("Type: %s, length: %d, dims: %d,%d\n", type, frame.getLength(), frameWidth, frameHeight);
			System.out.printf("Format: %s\n", frame.getFormat());
			for (int y = 0; y < frameHeight; y++) {
				for (int x = 0; x < frameWidth; x++) {
					if (frame.getData() instanceof byte[]) {
						byte[] data = (byte[]) frame.getData();
						System.out.println(TrackingUtil.intensity(data, x*3+y*frameWidth*3));
					}
				}
			}
		}
		
		// Point updates
		TrackingUtil.updatePoints(points, frame, frameWidth, frameHeight);
		
		// Draw boxes
		for (TrackedPoint point : points) {
			TrackingUtil.DrawBox(frame, point.coordX, point.coordY, frameWidth, frameHeight);
		}
		
		// Draw IDs
		for (TrackedPoint point : points) {
			int locX = point.coordX+12;
			int locY = point.coordY+5;
			TrackingUtil.DrawId(frame, point.id, locX, locY, frameWidth, frameHeight);
		}
	}

	public String getName() {
		return "Tracker Codec";
	}

	public void addListener(CameraHeadTracker cameraHeadTracker) {
		this.cht = cameraHeadTracker;
	}
}