/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import javax.media.opengl.GL;

/**
 *
 * @author vegar
 */
public class Rectangle3D {

    public Vector3D pos;
    public Vector3D vel = new Vector3D(0.0, 0.0, 0.09);
    public Vector3D normal;
    public Vector3D orientation;
    public Vector3D cross;
    public double height;
    public double width;
    float[] color = {(float)Math.random(), (float)Math.random(), (float)Math.random()};

    public Rectangle3D(Vector3D position, Vector3D normal, Vector3D orientation, double width, double height) {
        this.pos = position;
        this.orientation = orientation.unify();
        this.normal = normal.unify();
        this.cross = normal.cross(orientation).unify();
        this.width = width;
        this.height = height;
    }

    public boolean isIntersecting(Sphere sphere) {
        // Check plane intersection
        Vector3D ray = sphere.pos.subtract(pos);
        //Vector3D e_axis = sphere.pos.subtract(pos);
        Vector3D paral = normal.mult(ray.dot(normal));
        Vector3D perp = sphere.pos.add(paral).subtract(pos);
        if(paral.dot() <= sphere.radius*sphere.radius && (Math.abs(perp.dot(orientation)) < width/2.0 + sphere.radius && Math.abs(perp.dot(cross)) < height/2.0 + sphere.radius) ) {
            System.out.println(perp.dot(orientation) + ", " + perp.dot(cross));
            return true;
        }
        return false;
    }

    public void doCollision(Sphere sphere) {
        // Simple collision, doesn't do anything special with edges
        Vector3D paral = normal.mult(this.normal.dot(sphere.vel));
        Vector3D perp = sphere.vel.subtract(paral);
        sphere.vel = perp.add(paral.scale(-1));
    }

    public void draw(GL gl) {
        Vector3D topright =  orientation.mult(width/2.0).add(cross.mult(height/2.0));
        Vector3D bottomleft = topright.mult(-1);
        Vector3D bottomright = orientation.mult(width/2.0).add(cross.mult(-height/2.0));
        Vector3D topleft = bottomright.mult(-1);
        topright = topright.add(pos);
        bottomleft = bottomleft.add(pos);
        bottomright = bottomright.add(pos);
        topleft = topleft.add(pos);
        gl.glColor3fv(color, 0);
        //System.out.println(topleft);
        //System.out.println(topright);
        //System.out.println(bottomright);
        //System.out.println(bottomleft);
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3d(topleft.x, topleft.y, topleft.z);
        gl.glVertex3d(topright.x, topright.y, topright.z);
        gl.glVertex3d(bottomright.x, bottomright.y, bottomright.z);
        gl.glVertex3d(bottomleft.x, bottomleft.y, bottomleft.z);
        gl.glEnd();
    }
}
