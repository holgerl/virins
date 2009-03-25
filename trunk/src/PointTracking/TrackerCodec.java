package PointTracking;
import java.util.ArrayList;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.format.RGBFormat;

public class TrackerCodec extends GenericCodec {
	private int counter = 0;
	private int dimX = 320; // TODO: Hard coded video dimensions
	private int dimY = 240;
	private ArrayList<TrackedPoint> points = new ArrayList<TrackedPoint>();
	private short[] datashit;
	private byte[] bytedatashit;
	
	private short bit001 = Short.parseShort("00000000011111", 2);
	private short red = Short.parseShort("111110000000000", 2);
	
	float[][] getCoords() {
		return null;
	}
	
	private int intensity(short color) {
		return ((color>>10 & bit001)+(color>>5 & bit001)+(color & bit001));
	}
	
	private int intensity(byte colors[], int pos) {
		return (colors[pos] + colors[pos+1] + colors[pos+2]);
	}

	// We'll advertize as supporting all video formats.
	public TrackerCodec() {
		supportedIns = new Format[] {new RGBFormat()};
	}
	
	public void calibrate() {
		points.clear();
		outer: for (int y = 0; y < dimY; y++)
			for (int x = 0; x < dimX; x++) {
				if (intensity(datashit[x + y * dimX]) < 10) {
					boolean notFound = true;
					for (TrackedPoint point : points)
						if (x > point.coordX-20 && x < point.coordX+20)
							if (y > point.coordY-20 && y < point.coordY+20)
								notFound = false;
					if (notFound) points.add(new TrackedPoint(points.size(), dimX, dimY, x, y));
				}
				if (points.size() >= 3) break outer;
			}
	}

	/**
	 * Callback to access individual video frames.
	 */
	void accessFrame(Buffer frame) {
		counter++;

		String type = frame.getData().getClass().getSimpleName();
		if (frame.getData() instanceof byte[]) {
			bytedatashit = (byte[]) frame.getData();
			
			// Printout
			if (true && counter % 7 == 0) {
				type = "byte[]";
				String format = frame.getFormat().toString();
				String[] dimstrings = format.split(",")[1].trim().split("x");
				int dims[] = {Integer.parseInt(dimstrings[0]), Integer.parseInt(dimstrings[1])};
				if (points.size() > 0) System.out.printf("Coords: %f,%f\n", points.get(0).normX, points.get(0).normY);
				System.out.printf("Type: %s, length: %d, dims: %d,%d\n", type, frame.getLength(), dimX, dimY);
				System.out.printf("Format: %s\n", format);
			}
		}
		
		if (frame.getData() instanceof short[]) {
			datashit = (short[]) frame.getData();
			
			// Printout
			if (false && counter % 7 == 0) {
				type = "short[]";
				String format = frame.getFormat().toString();
				String[] dimstrings = format.split(",")[1].trim().split("x");
				int dims[] = {Integer.parseInt(dimstrings[0]), Integer.parseInt(dimstrings[1])};
				if (points.size() > 0) System.out.printf("Coords: %f,%f\n", points.get(0).normX, points.get(0).normY);
				System.out.printf("Type: %s, length: %d, dims: %d,%d\n", type, frame.getLength(), dimX, dimY);
				System.out.printf("Format: %s\n", format);
			}
			
			int pos;
			
			// Point updates
			for (TrackedPoint point : points) {
				for (int y = point.coordY-20+point.dy; y < point.coordY+20+point.dy; y++)
					for (int x = point.coordX-20+point.dx; x < point.coordX+20+point.dx; x++) {
						pos = x + y * dimX;
						if (pos >= 0 && pos < frame.getLength()) {
							if (intensity(datashit[pos]) < 30)
								point.update(x, y);
							datashit[pos] &= 15328; // Discolor area where point is looked for
						}
					}
			}
			
			// Draw boxes
			for (TrackedPoint point : points) {
				int dotX = point.coordX;
				int dotY = point.coordY;
				dotX -= 10;
				dotY -= 10;
				for (int y = dotY; y <= dotY+20; y++) {
					int foo = (y==dotY||y==dotY+20) ? 1 : 20;
					for (int x = dotX; x <= dotX+20; x+=foo) {
						pos = x + y * dimX;
						if (pos >= 0 && pos < frame.getLength())
							datashit[pos] = red;
					}
				}
			}
			
			// Draw IDs
			for (TrackedPoint point : points) {
				int offsetX = 0;
				for (int i = 0; i < point.id+1; i++) {
					int locX = point.coordX+12+offsetX;
					int locY = point.coordY+5;
					for (int y = locY; y < locY+5; y++) {
						for (int x = locX; x < locX+5; x++) {
							pos = x + y * dimX;
							if (pos >= 0 && pos < frame.getLength())
								datashit[pos] = red;
						}
					}
					offsetX+=7;
				}
			}
		}
	}

	public String getName() {
		return "Tracker Codec";
	}
}