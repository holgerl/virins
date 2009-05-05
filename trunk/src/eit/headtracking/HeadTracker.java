/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vegar
 */
public abstract class HeadTracker {;
    public HeadTracker() {
    }
    public abstract void start();
    public abstract void stop();
    public abstract void calibrate();
    public abstract float getHeadX();

    public abstract float getHeadY();

    public abstract float getHeadZ();

    public abstract float getRotX();

    public abstract float getRotY();

    public abstract float getRotZ();

    public abstract void setScaleX(double xscale);
}
