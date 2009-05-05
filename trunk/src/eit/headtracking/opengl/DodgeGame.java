/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import com.sun.opengl.util.GLUT;
import eit.headtracking.opengl.level.TestLevel;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author vegar
 */
public class DodgeGame extends HeadTrackerDemo {

    Sphere[] sphere;
    boolean collisions[][];
    Sphere player;
    Rectangle3D[] wall;
    Level level;
    Rectangle3D[] obstacle;

    public static void main(String args[]) {
        DodgeGame dg = new DodgeGame();
    }

    public void init(GLAutoDrawable gl) {
        super.init(gl);
        level = new TestLevel(this);
        player = new Sphere(0.2, new Vector3D(0.0, 0.0, 0.0));
        // Walls of the room excepth the back wall
        wall = new Rectangle3D[4];
        wall[0] = new Rectangle3D(new Vector3D(0.5, 0, roomDepth / 2.0), new Vector3D(-1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[1] = new Rectangle3D(new Vector3D(-0.5, 0, roomDepth / 2.0), new Vector3D(1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[2] = new Rectangle3D(new Vector3D(0, -0.5, roomDepth / 2.0), new Vector3D(0.0, 1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[3] = new Rectangle3D(new Vector3D(0, 0.5, roomDepth / 2.0), new Vector3D(0.0, -1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
    }

    public void display(GLAutoDrawable drawable) {
        GLUT glut = new GLUT();
        //glut.glutSolidTorus()
        level.frame();
        player.pos.x = head.getHeadX();
        player.pos.y = head.getHeadY();
        player.pos.z = head.getHeadZ();
        super.display(drawable);
        GL gl = drawable.getGL();
        gl.glScalef(screenAspect, 1.0f, 1.0f);
        drawGrid(gl);
        for (Rectangle3D r : level.rectangles) {
            r.pos = r.pos.add(r.vel);
            r.draw(gl);
            if (r.isIntersecting(player)) {
                System.out.println("REKTANGELPANG!");
            }
        }
        for (Sphere s : level.spheres) {
            s.pos = s.pos.add(s.vel);
            s.draw(gl);
            if (s.isIntersecting(player)) {
                System.out.println("KULEPANG");
            }
        }
        for(Circle c : level.circles) {
            c.pos = c.pos.add(c.vel);
            gl.glPushMatrix();
            gl.glTranslated(c.pos.x, c.pos.y, c.pos.z);
            glut.glutSolidTorus(0.01, c.radius, 20, 20);
            gl.glPopMatrix();
        }
    }
}
