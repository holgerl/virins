/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking;

/**
 *
 * @author vegar
 */
public class Point {
    public double x;
    public double y;
    public Point() {
    }
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public String toString() {
    	return (int) x + "," + (int) y;
    }
}
