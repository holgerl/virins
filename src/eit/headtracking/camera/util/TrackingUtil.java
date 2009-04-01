package eit.headtracking.camera.util;

import java.util.ArrayList;

import javax.media.Buffer;

import eit.headtracking.camera.dataholders.TrackedPoint;

public class TrackingUtil {
	public static short bit001 = Short.parseShort("00000000011111", 2);
	public static short red = Short.parseShort("111110000000000", 2);
	
	public static int intensity(short color) {
		return ((color>>10 & bit001)+(color>>5 & bit001)+(color & bit001));
	}
	
	public static int intensity(byte colors[], int pos) {
		return (colors[pos] + colors[pos+1] + colors[pos+2]);
	}
	
	public static ArrayList<TrackedPoint> findPoints(Buffer frame, int count, int frameWidth, int frameHeight) {
		ArrayList<TrackedPoint> points = new ArrayList<TrackedPoint>();
		if (frame.getData() instanceof short[]) {
			short[] datashit = (short[]) frame.getData();
			outer: for (int y = 0; y < frameHeight; y++)
				for (int x = 0; x < frameWidth; x++) {
					if (intensity(datashit[x + y * frameWidth]) < 10) {
						boolean notAlreadyFound = true;
						for (TrackedPoint point : points)
							if (x > point.coordX-20 && x < point.coordX+20)
								if (y > point.coordY-20 && y < point.coordY+20)
									notAlreadyFound = false;
						if (notAlreadyFound) points.add(new TrackedPoint(points.size(), frameWidth, frameHeight, x, y));
					}
					if (points.size() >= count) break outer;
				}
		} else if (frame.getData() instanceof byte[]) {
			System.out.println("TrackingUtil.calibrate");
			byte[] datashit = (byte[]) frame.getData();
			outer: for (int y = 0; y < frameHeight; y++)
				for (int x = 0; x < frameWidth; x++) {
					if (intensity(datashit, x*3 + y * frameWidth*3) > 200) System.out.println(intensity(datashit, x*3 + y * frameWidth*3));
					if (intensity(datashit, x*3 + y * frameWidth*3) > 200) {
						System.out.println("found point at " + x/3 + "," + y);
						boolean notAlreadyFound = true;
						for (TrackedPoint point : points)
							if (x > point.coordX-20 && x < point.coordX+20)
								if (y > point.coordY-20 && y < point.coordY+20)
									notAlreadyFound = false;
						if (notAlreadyFound) points.add(new TrackedPoint(points.size(), frameWidth, frameHeight, x*3, y));
					}
					if (points.size() >= count) break outer;
				}
		} else {
			throw new Error("findPoints does not support "+frame.getData().getClass().getSimpleName());
		}
		return points;
	}
	
	public static void updatePoints(ArrayList<TrackedPoint> points, Buffer frame, int frameWidth, int frameHeight) {
		if (frame.getData() instanceof short[]) {
			short[] datashit = (short[]) frame.getData();
			for (TrackedPoint point : points) {
				for (int y = point.coordY-30+point.dy; y < point.coordY+30+point.dy; y++)
					for (int x = point.coordX-30+point.dx; x < point.coordX+30+point.dx; x++) {
						int pos = x + y * frameWidth;
						if (pos >= 0 && pos < frame.getLength()) {
							if (intensity(datashit[pos]) < 30)
								point.update(x, y);
							datashit[pos] &= 15328; // Discolor area where point is looked for
						}
					}
			}
		} else if (frame.getData() instanceof byte[]) {
			byte[] datashit = (byte[]) frame.getData();
			for (TrackedPoint point : points) {
				for (int y = point.coordY-20+point.dy; y < point.coordY+20+point.dy; y++)
					for (int x = point.coordX-20+point.dx; x < point.coordX+20*3+point.dx; x++) {
						int pos = x + y * frameWidth;
						if (pos >= 0 && pos < frame.getLength()-3) {
							if (intensity(datashit, pos) > 200)
								point.update(x, y);
							datashit[pos+3] = 0; // Discolor area where point is looked for
						}
					}
			}
		} else {
			throw new Error("updatePoints does not support "+frame.getData().getClass().getSimpleName());
		}
		
		
	}

	public static void DrawBox(Buffer frame, int dotX, int dotY, int frameWidth, int frameHeight) {
		if (frame.getData() instanceof short[]) {
			short[] datashit = (short[]) frame.getData();
			dotX -= 10;
			dotY -= 10;
			for (int y = dotY; y <= dotY+20; y++) {
				int foo = (y==dotY||y==dotY+20) ? 1 : 20;
				for (int x = dotX; x <= dotX+20; x+=foo) {
					int pos = x + y * frameWidth;
					if (pos >= 0 && pos < frame.getLength())
						datashit[pos] = red;
				}
			}
		} else if (frame.getData() instanceof byte[]) {
			byte[] datashit = (byte[]) frame.getData();
			dotX -= 10;
			dotY -= 10;
			for (int y = dotY; y <= dotY+20; y++) {
				int foo = (y==dotY||y==dotY+20) ? 1 : 20;
				for (int x = dotX; x <= dotX+20; x+=foo) {
					int pos = x + y * frameWidth;
					if (pos >= 0 && pos < frame.getLength()-3) {
						datashit[pos] = (byte) 255;
						datashit[pos+1] = 0;
						datashit[pos+2] = 0;
					}
				}
			}
		} else {
			throw new Error("DrawBox does not support "+frame.getData().getClass().getSimpleName());
		}
	}

	public static void DrawId(Buffer frame, int id, int locX, int locY, int frameWidth, int frameHeight) {
		if (frame.getData() instanceof short[]) {
			short[] datashit = (short[]) frame.getData();
			int offsetX = 0;
			for (int i = 0; i < id+1; i++) {
				locX += offsetX;
				for (int y = locY; y < locY+5; y++) {
					for (int x = locX; x < locX+5; x++) {
						int pos = x + y * frameWidth;
						if (pos >= 0 && pos < frame.getLength())
							datashit[pos] = red;
					}
				}
				offsetX+=7;
			}
		} else if (frame.getData() instanceof byte[]) {
			byte[] datashit = (byte[]) frame.getData();
			int offsetX = 0;
			for (int i = 0; i < id+1; i++) {
				locX += offsetX;
				for (int y = locY; y < locY+5; y++) {
					for (int x = locX; x < locX+5; x++) {
						int pos = x + y * frameWidth;
						if (pos >= 0 && pos < frame.getLength()-3) {
							datashit[pos] = (byte) 255;
							datashit[pos+1] = 0;
							datashit[pos+2] = 0;
						}
					}
				}
				offsetX+=7;
			}
		} else {
			throw new Error("DrawId does not support "+frame.getData().getClass().getSimpleName());
		}
	}

	public static int[] getFrameDimensions(Buffer frame) {
		String format = frame.getFormat().toString();
		String[] dimstrings = format.split(",")[1].trim().split("x");
		int dims[] = {Integer.parseInt(dimstrings[0]), Integer.parseInt(dimstrings[1])};
		return dims;
	}
}
