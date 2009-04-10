package eit.headtracking.dscamera.dataholders;

public class TrackedPoint {
	public int id;
	public int coordX; // Video coordinates
	public int coordY;
	public float normX; // Normalized coordinates
	public float normY;
	private int dimX; // Video size
	private int dimY;
	public int dx; // Change in video coordinates
	public int dy;
	
	public TrackedPoint(int id, int dimX, int dimY, int coordX, int coordY) {
		this.id = id;
		this.dimX = dimX;
		this.dimY = dimY;
		update(coordX, coordY);
		dx = 0;
		dy = 0;
	}
	
	public void update(int coordX, int coordY) {
		dx = coordX-this.coordX;
		dy = coordY-this.coordY;
		this.coordX = coordX;
		this.coordY = coordY;
		this.normX = (float) ((-dimX/2+coordX)/(dimX/2.0));
		this.normY = (float) ((-dimY/2+coordX)/(dimY/2.0));
	}
	
	public void update(float normX, float normY) {
		throw new Error("Is this method really neccessary?");
	}
}
