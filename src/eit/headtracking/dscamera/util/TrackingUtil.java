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
	
	public static Point[] findPoints(BufferedImage buffer) {
		Point[] points = new Point[3];
		int nofFoundPoints = 0;
		outer: for (int y = 0; y < buffer.getHeight(); y++)
			for (int x = 0; x < buffer.getWidth(); x++) {
				if (intensity(buffer.getRGB(x, y)) < 2) {
					boolean alreadyFound = false;
					for (Point point : points)
						if (point != null)
							if (x > point.x-20 && x < point.x+20)
								if (y > point.y-20 && y < point.y+20)
									alreadyFound = true;
					if (alreadyFound == false)
						points[nofFoundPoints++] = new Point(x, y);
					if (nofFoundPoints >= points.length)
						break outer;
				}
			}
		return points;
	}
	
	public static void updatePoints(Point[] points, BufferedImage buffer) {
		for (Point point : points) {
			if (point == null) continue;
			int searchStartX = (int) point.x-30;
			int searchStartY = (int) point.y-30;
			int searchEndX = (int) point.x+30;
			int searchEndY = (int) point.y+30;
			outer: for (int y = searchStartY; y < searchEndY; y++) {
				for (int x = searchStartX; x < searchEndX; x++) {
					try {
						if (intensity(buffer.getRGB(x, y)) < 2) {
							point.x = x;
							point.y = y;
							break outer;
						}
					} catch (ArrayIndexOutOfBoundsException e) {}
				}
			}
		}
	}
}
