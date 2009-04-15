package eit.headtracking.dscamera.util;

import java.awt.image.BufferedImage;

import eit.headtracking.Point;

public class TrackingUtil {
	
	public static void intToByte(int in, byte[] out) {
		out[0]= (byte) in;
		in = in >> 8;
		out[1]= (byte) in;
		in = in >> 8;
		out[2]= (byte) in;
	}
	
	public static String bufferToIntensityString(BufferedImage buffer) {
		int res = 30;
		String str = "";
		for (int y = 0; y < buffer.getHeight(); y+=res) {
			for (int x = 0; x < buffer.getWidth(); x+=res) {
				str += intensity(buffer.getRGB(x, y));
			}
			str += "\n";
		}
		return str;
	}
	
	public static String bufferToRGBString(BufferedImage buffer) {
		int res = 60;
		String str = "";
		for (int y = 0; y < buffer.getHeight(); y+=res) {
			for (int x = 0; x < buffer.getWidth(); x+=res) {
				byte[] rgb = new byte[3];
				intToByte(buffer.getRGB(x, y), rgb);
				str += String.format("%2d", rgb[0]/10) + "," + String.format("%2d", rgb[1]/10) + "," + String.format("%2d", rgb[2]/10) + " ";
			}
			str += "\n";
		}
		return str;
	}
	
	/**
	 * Returns intensity (brightness) of a 24 bit RGB color on a scale of 0 to 9
	 */
	public static int intensity(int color) {
		return ((color>>16 & 255)+(color>>8 & 255)+(color & 255)) / 77;
	}
	
	public static final int dotRadius = 100;
	
	public static Point[] findPoints(BufferedImage buffer) {
		Point[] points = new Point[3];
		int nofFoundPoints = 0;
		int counter = 0;
		outer: for (int y = 0; y < buffer.getHeight(); y++)
			for (int x = 0; x < buffer.getWidth(); x++) {
				if (intensity(buffer.getRGB(x, y)) >= 9)
					counter++;
				else
					counter = Math.max(0, counter-1);
				if (counter > dotRadius/2) {
					counter = 0;
					boolean alreadyFound = false;
					for (Point point : points)
						if (point != null)
							if (x > point.x-dotRadius && x < point.x+dotRadius)
								if (y > point.y-dotRadius && y < point.y+dotRadius)
									alreadyFound = true;
					if (alreadyFound == false)
						points[nofFoundPoints++] = new Point(x, y);
					if (nofFoundPoints >= points.length)
						break outer;
				}
			}
		return points;
	}
	
	public static final int searchRadius = 200;
	
	public static void updatePoints(Point[] points, BufferedImage buffer) {
		int counter = 0;
		for (Point point : points) {
			if (point == null) continue;
			int searchStartX = Math.max(0, (int) point.x-searchRadius);
			int searchStartY = Math.max(0, (int) point.y-searchRadius);
			int searchEndX = Math.min(buffer.getWidth()-1, (int) point.x+searchRadius);
			int searchEndY = Math.min(buffer.getHeight()-1, (int) point.y+searchRadius);
			outer: for (int y = searchStartY; y < searchEndY; y++) {
				for (int x = searchStartX; x < searchEndX; x++) {
					if (intensity(buffer.getRGB(x, y)) >= 9)
						counter++;
					else
						counter = Math.max(0, counter-1);
					if (counter > dotRadius/2) {
						counter = 0;
						point.x = x;
						point.y = y;
						break outer;
					}
				}
			}
		}
	}
}
