/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit;

/**
 *
 * @author vegar
 */
public interface HeadTracker {
    public void start();
    public void stop();
    public void calibrate();
    // Right and left( Between -1 and 1 )
    public float getHeadX();
    // Up and down( Between -1 and 1 )
    public float getHeadY();
    // In and out ( Between 0 and 2pi)
    public float getHeadZ();
    // Rotation about X-axis ( Between 0 and 2pi )
    public float getRotX();
    // Rotation about Y-axis ( Between 0 and 2pi )
    public float getRotY();
    // Rotation about Z-axis ( Between 0 and 2pi )
    public float getRotZ();
}
