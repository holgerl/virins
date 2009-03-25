/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit;

/**
 *
 * @author vegar
 */
public class Vector3D {
    public double x;
    public double y;
    public double z;
    public Vector3D() {
    }
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public double dot(Vector3D b) {
        return this.x*b.x + this.y*b.y + this.z*b.z;
    }
}
