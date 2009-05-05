/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import eit.headtracking.opengl.level.TestLevel;
import java.util.Iterator;
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
    Texture texture;

    public static void main(String args[]) {
        System.out.println("DodgeGame starting");
        DodgeGame dg = new DodgeGame();
    }

    public void init(GLAutoDrawable gl) {
        System.out.println("Init gl");
        super.init(gl);
        level = new TestLevel(this);
        player = new Sphere(Double.parseDouble(System.getProperty("eit.headtracking.opengl.dodgegame.headradius")), new Vector3D(0.0, 0.0, 0.0));
        // Walls of the room excepth the back wall
        wall = new Rectangle3D[4];
        wall[0] = new Rectangle3D(new Vector3D(0.5, 0, roomDepth / 2.0), new Vector3D(-1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[1] = new Rectangle3D(new Vector3D(-0.5, 0, roomDepth / 2.0), new Vector3D(1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[2] = new Rectangle3D(new Vector3D(0, -0.5, roomDepth / 2.0), new Vector3D(0.0, 1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[3] = new Rectangle3D(new Vector3D(0, 0.5, roomDepth / 2.0), new Vector3D(0.0, -1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        texture = load("pow.jpg");
    }

    public void drawPow(GL gl) {
        gl.glBegin(GL.GL_QUADS);
        		gl.glTexCoord2f(0.0f, 0.0f); gl.glVertex3f(-.5f, -.5f,  0.0f);	// Bottom Left Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 0.0f); gl.glVertex3f( .5f, -.5f,  0.0f);	// Bottom Right Of The Texture and Quad
		gl.glTexCoord2f(1.0f, 1.0f); gl.glVertex3f( .5f,  .5f,  0.0f);	// Top Right Of The Texture and Quad
		gl.glTexCoord2f(0.0f, 1.0f); gl.glVertex3f(-.5f,  .5f,  0.0f);	// Top Left Of The Texture and Quad
        gl.glEnd();
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
                Iterator<Rectangle3D> itr = level.rectangles.iterator();
                Rectangle3D r;
        while(itr.hasNext()) {
            r = itr.next();
            if(r.pos.z > 10.0) itr.remove();
            r.pos = r.pos.add(r.vel);
            r.draw(gl);
            if (r.isIntersecting(player)) {
                drawPow(gl);
            }
        }
        Iterator<Sphere> its = level.spheres.iterator();
        Sphere s;
        while(its.hasNext()) {
            s = its.next();
            if(s.pos.z > 10.0) its.remove();
            s.pos = s.pos.add(s.vel);
            s.draw(gl);
            if (s.isIntersecting(player)) {
                drawPow(gl);
            }
        }
        Iterator<Circle> itc = level.circles.iterator();
        Circle c;
        while(itc.hasNext()) {
            c = itc.next();
            if(c.pos.z > 10.0) itc.remove();
            c.pos = c.pos.add(c.vel);
            gl.glPushMatrix();
            gl.glTranslated(c.pos.x, c.pos.y, c.pos.z);
            gl.glColor3fv(c.color,0);
            glut.glutSolidTorus(0.01, c.radius, 20, 20);
            gl.glPopMatrix();
        }
    }
}
