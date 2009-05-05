/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking;

/**
 *
 * @author vegar
 */
public abstract class DualSourceHeadTracker extends HeadTracker {
    protected double XMAX = 1024;
    protected double YMAX = 768;
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
    private boolean cameraIsAboveScreen = true;
    private float cameraVerticalAngle;

    protected boolean normalize = true;

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
        leftOffsetRadians = angle - radiansPerPixel * (double) (leftPoint.x);
        rightOffsetRadians = angle - radiansPerPixel * (double) (1024 - rightPoint.x);
        calculate();
        angle = Math.acos(.5 / headZ) - Math.PI / 2;//angle of head to screen
        if (!cameraIsAboveScreen) {
            angle = -angle;
        }
        cameraVerticalAngle = (float) ((angle - relativeVerticalAngle));//absolute camera angle
        System.out.println("Done calibrating");
    }

    protected void calculate() {
        if (leftPoint == null || rightPoint == null) {
            return;
        }
        headZ = sourceDistanceInMM / (1.0 / Math.tan(leftAngle()) + 1.0 / Math.tan(rightAngle()));
        double avgY = (leftPoint.y + rightPoint.y) / 2.0;
        relativeVerticalAngle = (avgY - YMAX/2) * radiansPerPixel;
        headX = -(sourceDistanceInMM / (2.0) - headZ / Math.tan(leftAngle()));

        if(cameraIsAboveScreen) {
            headY = .5f + Math.sin(relativeVerticalAngle + cameraVerticalAngle) * headZ;
        } else {
            headY = Math.sin(relativeVerticalAngle + cameraVerticalAngle) * headZ;
        }
        if(normalize) {
            headX /= screenHeightInMM;
            headY /= screenHeightInMM;
            headZ /= screenHeightInMM;
        }
        
    }

    public double leftAngle() {
        return leftOffsetRadians + radiansPerPixel * (double) (leftPoint.x);
    }

    public double rightAngle() {
        return rightOffsetRadians + radiansPerPixel * (double) (XMAX - rightPoint.x);
    }
}
