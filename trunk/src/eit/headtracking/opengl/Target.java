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
public class Target implements Drawable {
    public Vector3D pos;
    public Target(Vector3D position) {
        this.pos = position;
    }
    public void draw(GL gl) {
        drawTarget(gl, .05f);
    }

    void drawTarget(GL gl, double radius) {
        int n = 5;
        int c = 0;
        float[][] colors = {{.8f, 0.0f, 0.0f}, {0.3f, 0.3f, 0.3f}, {.1f, .1f, .1f}, {1.0f, 1.0f, 1.0f}};
        //float[][] colors = {{1.0f, 1.0f, 1.0f}, {0.0f, 0.0f, 0.0f}};
        double step = radius / (double) n;
        for (int i = 0; i < n; i++) {
            gl.glColor3f(colors[c][0], colors[c][1], colors[c][2]);
            drawDonut(gl, radius - i * step, radius - (i + 1) * step, 20);
            c = (c + 1) % colors.length;
        }
    }

    void drawDonut(GL gl, double outer, double inner, int slices) {
        double step = Math.PI * 2.0 / (double) slices;
        double theta, dtheta;
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < slices; i++) {
            theta = i * step;
            dtheta = (i + 1) * step;

            gl.glVertex2d(outer * Math.cos(theta), outer * Math.sin(theta));
            gl.glVertex2d(inner * Math.cos(theta), inner * Math.sin(theta));
            gl.glVertex2d(outer * Math.cos(dtheta), outer * Math.sin(dtheta));

            gl.glVertex2d(outer * Math.cos(dtheta), outer * Math.sin(dtheta));
            gl.glVertex2d(inner * Math.cos(theta), inner * Math.sin(theta));
            gl.glVertex2d(inner * Math.cos(dtheta), inner * Math.sin(dtheta));
        }
        gl.glEnd();

    }
}
