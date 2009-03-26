package eit.headtracking.camera;
import java.util.ArrayList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;

import eit.headtracking.camera.dataholders.TrackedPoint;
import eit.headtracking.camera.util.TrackingUtil;

public class TrackerCodec extends GenericAccessCodec {
	private int counter = 0;
	private int frameWidth = 640; // TODO: Hard coded video dimensions = bad
	private int frameHeight = 480;
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
			int dims[] = TrackingUtil.getFrameDimensions(frame);
			frameWidth = dims[0];
			frameHeight = dims[1];
		}
		
		counter++;
		this.lastFrame = frame;
		cht.updatePoints();

		String type = frame.getData().getClass().getSimpleName();
		
		if (frame.getData() instanceof short[]) {
			
			// Printout
			if (false && counter % 7 == 0) {
				type = "short[]";
				if (points.size() > 0) System.out.printf("Coords: %f,%f\n", points.get(0).normX, points.get(0).normY);
				System.out.printf("Type: %s, length: %d, dims: %d,%d\n", type, frame.getLength(), frameWidth, frameHeight);
				System.out.printf("Format: %s\n", frame.getFormat());
			}
			
			int pos;
			
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
		} else {
			// Printout
			if (true && counter % 7 == 0) {
				type = frame.getData().getClass().getSimpleName();
				String format = frame.getFormat().toString();
				String[] dimstrings = format.split(",")[1].trim().split("x");
				int dims[] = {Integer.parseInt(dimstrings[0]), Integer.parseInt(dimstrings[1])};
				if (points.size() > 0) System.out.printf("Coords: %f,%f\n", points.get(0).normX, points.get(0).normY);
				System.out.printf("Type: %s, length: %d, dims: %d,%d\n", type, frame.getLength(), dims[0], dims[1]);
				System.out.printf("Format: %s\n", format);
			}
		}
	}

	public String getName() {
		return "Tracker Codec";
	}

	public void addListener(CameraHeadTracker cameraHeadTracker) {
		this.cht = cameraHeadTracker;
	}
}