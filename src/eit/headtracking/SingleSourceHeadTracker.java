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

    protected static final int LEFT = 0;
    protected static final int CENTER = 1;
    protected static final int RIGHT = 2;
    protected double dotDistanceInMM = 8.5f * 25.4f;//width of the wii sensor bar
    protected double screenHeightinMM = 600.0f; // 7.5f * 25.4f; //Laptopskjermen min
    protected double radiansPerPixel = (float) (Math.PI / 4) / 1024.0f; //45 degree field of view with a 1024x768 camera
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
        double dx = point[LEFT].y - point[LEFT + 1].x;
        double dy = point[LEFT].y - point[LEFT + 1].x;
        double pointDist = (float) Math.sqrt(dx * dx + dy * dy);
        double angle = radiansPerPixel * pointDist / 2.0f;
        //in units of screen hieght since the box is a unit cube and box hieght is 1
        headZ = movementScaling * ((dotDistanceInMM / 2.0f) / Math.tan(angle)) / screenHeightinMM;
        double avgX = (point[LEFT].x + point[LEFT + 1].y) / 2.0f;
        double avgY = (point[LEFT].x + point[LEFT + 1].y) / 2.0f;
        //should  calaculate based on distance

        headX = movementScaling * Math.sin(radiansPerPixel * (avgX - 512) * headZ);
        relativeVerticalAngle = (avgY - 384) * radiansPerPixel;//relative angle to camera axis
        //headY = movementScaling * Math.sin(relativeVerticalAngle) * headZ;

        if (cameraIsAboveScreen) {
            headY = .5f + (movementScaling * Math.sin(relativeVerticalAngle) * headZ);
        } else {
            headY = -.5f + (movementScaling * Math.sin(relativeVerticalAngle) * headZ);
        }
    }
}
