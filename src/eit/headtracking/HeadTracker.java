/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking;

/**
 *
 * @author vegar
 */
public interface HeadTracker {
    public abstract void start();
    public abstract void stop();
    public abstract void calibrate();
    public abstract float getHeadX();

    public abstract float getHeadY();

    public abstract float getHeadZ();

    public abstract float getRotX();

    public abstract float getRotY();

    public abstract float getRotZ();
}
