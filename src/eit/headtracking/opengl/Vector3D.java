/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking.opengl;

/**
 *
 * @author vegar
 */
public class Vector3D {
    public double x;
    public double y;
    public double z;
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x-other.x, y-other.y, z-other.z);
    }
    public Vector3D add(Vector3D other) {
        return new Vector3D(x+other.x, y+other.y, z+other.z);
    }
    public Vector3D mult(double c) {
        return new Vector3D(x*c, y*c, z*c);
    }
    public Vector3D scale(double c) {
        x *= c;
        y *= c;
        z *= c;
        return this;
    }
   public double dot(Vector3D other) {
        return x*other.x + y*other.y + z*other.z;
   }

   public Vector3D cross(Vector3D other) {
       return new Vector3D(y*other.z - z*other.y, z*other.x - x*other.z, x*other.y - y*other.x);
   }
   public Vector3D unify() {
        scale(1.0/magnitude());
        return this;
   }
   public double magnitude() {
       return Math.sqrt(this.dot(this));
   }
   public String toString() {
       return "(" + x + ", " + y + ", " + z + ")";
   }
}
