/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking;

/**
 *
 * @author vegar
 */
public abstract class DualSourceHeadTracker implements HeadTracker {

    protected double leftOffsetRadians = Math.PI / 2.0;
    protected double rightOffsetRadians = Math.PI / 2.0;
    protected double sourceDistanceInMM = 850;// 1930.0;
    protected double screenHeightInMM = 500; // 600;
    protected double calibrationDistanceInMM = 2000.0;
    protected double radiansPerPixel = (Math.PI / 4.0) / 1024.0;
    protected double headX, headY, headZ;
    protected double relativeVerticalAngle;
    protected Point leftPoint = new Point();
    protected Point rightPoint = new Point();

    public DualSourceHeadTracker() {
        this.screenHeightInMM = Double.parseDouble(System.getProperty("eit.headtracking.screenheightmm"));
        this.sourceDistanceInMM = Double.parseDouble(System.getProperty("eit.headtracking.dualsourceheadtracker.sourcedistancemm"));
        this.calibrationDistanceInMM = Double.parseDouble(System.getProperty("eit.headtracking.dualsourceheadtracker.calibrationdistancemm"));
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
        return 0.0f;
    }

    public float getRotZ() {
        return 0.0f;
    }
    
    public void calibrate() {
        System.out.println("Calibrating");
        double angle = Math.atan2(calibrationDistanceInMM, sourceDistanceInMM / 2.0);
        leftOffsetRadians = angle - leftAngle();
        rightOffsetRadians = angle - rightAngle();
    }

    protected void calculate() {
        if (leftPoint == null || rightPoint == null) {
            return;
        }
        headZ = (sourceDistanceInMM / (1.0 / Math.tan(leftAngle() + leftOffsetRadians) + 1.0 / Math.tan(rightAngle() + rightOffsetRadians))) / screenHeightInMM;
        double avgY = (leftPoint.y + rightPoint.y) / 2.0;
        relativeVerticalAngle = (avgY - 384) * radiansPerPixel;
        headY = Math.sin(relativeVerticalAngle) * headZ;
        headX = -(sourceDistanceInMM / (2.0 * screenHeightInMM) - headZ / Math.tan(leftAngle() + leftOffsetRadians));
    }

    public double leftAngle() {
        return radiansPerPixel * (double) (leftPoint.x);
    }

    public double rightAngle() {
        return radiansPerPixel * (double) (1024 - rightPoint.x);
    }
}
