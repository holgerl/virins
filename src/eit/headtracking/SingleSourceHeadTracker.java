/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking;

/**
 *
 * @author vegar
 */
public abstract class SingleSourceHeadTracker implements HeadTracker {
    protected double XMAX = 1024.0;
    protected double YMAX = 768.0;
    protected static final int LEFT = 0;
    protected static final int CENTER = 1;
    protected static final int RIGHT = 2;
    protected double dotDistanceInMM = 8.5f * 25.4f;//width of the wii sensor bar
    protected double screenHeightinMM = 600.0f; // 7.5f * 25.4f; //Laptopskjermen min
    protected double radiansPerPixel = (float) (Math.PI / 4) / XMAX; //45 degree field of view with a 1024x768 camera
    protected double movementScaling = 1.0f;
    protected boolean cameraIsAboveScreen = true;
    protected double relativeVerticalAngle;
    protected double cameraVerticalAngle;
    protected double headX,  headY,  headZ;
    protected double rotX,  rotY,  rotZ;
    protected Point[] point = new Point[3];

    public abstract void start();

    public abstract void stop();

    public abstract void calibrate();

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
        double angle = radiansPerPixel * pointDist / 2.0;

        headZ = movementScaling * ((dotDistanceInMM / 2.0) / Math.tan(angle)) / screenHeightinMM;
        double avgX = (point[LEFT].x + point[LEFT + 1].x) / 2.0f;
        double avgY = (point[LEFT].y + point[LEFT + 1].y) / 2.0f;

        headX = movementScaling * Math.sin(radiansPerPixel * (avgX - XMAX/2.0)) * headZ;
        relativeVerticalAngle = (avgY - YMAX/2.0) * radiansPerPixel;//relative angle to camera axis

        if (cameraIsAboveScreen) {
            headY = .5f + (movementScaling * Math.sin(relativeVerticalAngle + cameraVerticalAngle) * headZ);
        } else {
            headY = -.5f + (movementScaling * Math.sin(relativeVerticalAngle + cameraVerticalAngle) * headZ);
        }
    }
}
