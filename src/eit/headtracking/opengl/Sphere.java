/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import com.sun.opengl.util.GLUT;
import javax.media.opengl.GL;

/**
 *
 * @author vegar
 */
public class Sphere {

    public Vector3D pos;
    public Vector3D vel = new Vector3D(0.000, 0.0, 0.09);
    public Vector3D accel = new Vector3D(0.0, -0.00002, 0);
    public double radius;
    public double mass = 1.0;
    public float[] color = {(float)Math.random(), (float)Math.random(), (float)Math.random()};
    public Sphere(double radius, Vector3D position) {
        this.radius = radius;
        this.pos = position;
    }
    public boolean isIntersecting(Sphere other) {
        return this.pos.subtract(other.pos).dot() <= (radius + other.radius)*(radius + other.radius);
    }

    public void doCollision(Sphere other) {
        Vector3D e_axis = this.pos.subtract(other.pos).unify();
        Vector3D a_paral = e_axis.mult(e_axis.dot(this.vel));
        Vector3D a_perp = this.vel.subtract(a_paral); // Independent of collision
        Vector3D b_paral = e_axis.mult(e_axis.dot(other.vel));
        Vector3D b_perp = other.vel.subtract(b_paral); // Independent of collision
        
        this.vel = a_perp.add( a_paral.mult(mass-other.mass).add(b_paral.mult(2*other.mass))).scale(1.0/(mass+other.mass));
        other.vel = b_perp.add( b_paral.mult(other.mass-mass).add(a_paral.mult(2*mass))).scale(1.0/(mass+other.mass));
    }

    public void draw(GL gl) {
        GLUT glut = new GLUT();
        gl.glColor3fv(color,0);
        gl.glPushMatrix();
        gl.glTranslated(pos.x, pos.y, pos.z);
        glut.glutSolidSphere(radius, 100, 100);
        gl.glPopMatrix();
    }
}
