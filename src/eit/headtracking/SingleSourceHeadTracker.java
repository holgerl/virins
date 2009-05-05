/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking;

/**
 *
 * @author vegar
 */
public abstract class SingleSourceHeadTracker extends HeadTracker {
    public double XMAX = 1024.0;
    public double YMAX = 768.0;
    protected static final int LEFT = 0;
    protected static final int CENTER = 1;
    protected static final int RIGHT = 2;
    protected double dotDistanceInMM = 8.5f * 25.4f;//width of the wii sensor bar
    protected double screenHeightinMM = 600.0f; // 7.5f * 25.4f; //Laptopskjermen min
    protected double radiansPerPixel = (float) (Math.PI / 4.0) / XMAX; //45 degree field of view with a 1024x768 camera
    //protected double movementScaling = 1.0f;

    protected boolean cameraIsAboveScreen = true;
    protected boolean normalize = true;

    protected double cameraAngleOffset;
    protected double beta;
    //protected double cameraOffset = (cameraIsAboveScreen ? 1.0 : -1.0) * screenHeightinMM / 2.0;
    protected double cameraOffset = 0;
    protected double headX,  headY,  headZ = 8.0;
    protected double rotX,  rotY,  rotZ;
    public Point[] point = new Point[3];

    public SingleSourceHeadTracker() {
        this.screenHeightinMM = Double.parseDouble(System.getProperty("eit.headtracking.screenheightmm"));
        this.dotDistanceInMM = Double.parseDouble(System.getProperty("eit.headtracking.singlesourceheadtracker.pointdistancemm"));
    }

    public abstract void start();

    public abstract void stop();

    public void calibrate() {
        System.out.println("Calibrating");
        double angle = Math.acos(.5 / headZ) - Math.PI / 2;//angle of head to screen
        if (!cameraIsAboveScreen) {
            angle = -angle;
        }
        cameraAngleOffset = (float) ((angle - beta));//absolute camera angle
        System.out.println("Done calibrating");
    }

    public float getHeadX() {
        return (float) headX;
    }

    public float getHeadY() {
        return (float) headY;
    }

    public float getHeadZ() {
        return (float) headZ;
    }

    public float getRotX() {
        return 0.0f;
    }

    public float getRotY() {
        return (float) rotY;
    }

    public float getRotZ() {
        return 0.0f;
    }

    protected void calculate() {
        double dx = point[LEFT].x - point[LEFT + 1].x;
        double dy = point[LEFT].y - point[LEFT + 1].y;
        double pointDist = Math.sqrt(dx * dx + dy * dy);
        double theta = radiansPerPixel * pointDist;
        double alpha = radiansPerPixel * ((double)((point[LEFT].x + point[LEFT+1].x) - XMAX) / 2.0);
        beta = radiansPerPixel * ((double)((point[LEFT].y + point[LEFT+1].y) - YMAX) / 2.0);
        double r = (dotDistanceInMM/2.0) / Math.tan(theta/2.0);
        headZ = r*Math.cos(alpha);
        headX = -r*Math.sin(alpha);
        headY = cameraOffset + r*Math.sin(beta + cameraAngleOffset);   
        if(normalize) {
            headX /= screenHeightinMM;
            headY /= screenHeightinMM;
            headZ /= screenHeightinMM;
        }
    }
}
