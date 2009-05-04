/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLDrawable;

/**
 *
 * @author vegar
 */
public class Illusion extends HeadTrackerDemo {

    public static void main(String args[]) {
        Illusion illusion = new Illusion();
    }

    GridRoom gridRoom = new GridRoom();
    Target[] targets = new Target[5];

    public Illusion() {
        randomizeTargets();
    }

    public void init(GLAutoDrawable drawable) {
        targets = new Target[5];
        randomizeTargets();
        super.init(drawable);
    }

    public void display(GLAutoDrawable drawable) {
        super.display(drawable);
        GL gl = drawable.getGL();
        gl.glScalef(screenAspect, 1, 1);
        for (int i = 0; i < targets.length; i++) {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_LINES);
            gl.glVertex3f(targets[i].x, targets[i].y, roomDepth);
            gl.glVertex3f(targets[i].x, targets[i].y, targets[i].z);
            gl.glEnd();
            gl.glPushMatrix();

            gl.glTranslatef(targets[i].x, targets[i].y, targets[i].z);
            drawCube(gl, .02f);
            gl.glPopMatrix();
        }
        drawGrid(gl);
        gl.glFlush();
    }

    public void randomizeTargets() {

        for (int i = 0; i < targets.length; i++) {
            targets[i] = new Target();
            targets[i].x = (.5f - random.nextFloat()) * screenAspect;
            targets[i].y = .5f - random.nextFloat();
            targets[i].z = 5.0f + random.nextFloat() * roomDepth;
            if (targets[i].z > 0.0f) {
                targets[i].x /= 3.0f;
                targets[i].y /= 3.0f;
            }
        }
    }
}
