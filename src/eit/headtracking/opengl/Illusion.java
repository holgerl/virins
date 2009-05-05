/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import java.awt.event.KeyEvent;
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
       
        for (int i = 0; i < targets.length; i++) {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_LINES);
            gl.glVertex3d(targets[i].pos.x, targets[i].pos.y, roomDepth);
            gl.glVertex3d(targets[i].pos.x, targets[i].pos.y, targets[i].pos.z);
            gl.glEnd();
            gl.glPushMatrix();

            gl.glTranslated(targets[i].pos.x, targets[i].pos.y, targets[i].pos.z);
            targets[i].draw(gl);
            //drawCube(gl, .02f);
            gl.glPopMatrix();
        }
         gl.glScalef(screenAspect, 1, 1);
        drawGrid(gl);
        gl.glFlush();
    }

    public void randomizeTargets() {

        for (int i = 0; i < targets.length; i++) {
            targets[i] = new Target(new Vector3D(
                    (.5f - random.nextFloat()) * screenAspect,
                    .5f - random.nextFloat(),
                    (5.0f - roomDepth)*Math.random() + roomDepth));
            if (targets[i].pos.z > 0.0f) {
                targets[i].pos.x /= 3.0f;
                targets[i].pos.y /= 3.0f;
            }
        }
    }
    public void keyPressed(KeyEvent arg0) {
        super.keyPressed(arg0);
        if(arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            randomizeTargets();
        }
    }
}
