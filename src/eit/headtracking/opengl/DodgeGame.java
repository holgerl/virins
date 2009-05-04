/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import com.sun.opengl.util.GLUT;
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
        level = new Level(this);
        player = new Sphere(0.2, new Vector3D(0.0,0.0,0.0));
        wall = new Rectangle3D[5];
        wall[0] = new Rectangle3D(new Vector3D(0.5, 0, roomDepth / 2.0), new Vector3D(-1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[1] = new Rectangle3D(new Vector3D(-0.5, 0, roomDepth / 2.0), new Vector3D(1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[2] = new Rectangle3D(new Vector3D(0, -0.5, roomDepth / 2.0), new Vector3D(0.0, 1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[3] = new Rectangle3D(new Vector3D(0, 0.5, roomDepth / 2.0), new Vector3D(0.0, -1.0, 0.0), new Vector3D(0.0, 0.0, 1.0), 2.5, 2.5);
        wall[4] = new Rectangle3D(new Vector3D(0, 0, -roomDepth), new Vector3D(0.0, 0.0, -1.0), new Vector3D(1.0, 0.0, 0.0), 2.5, 2.5);
        
        obstacle = new Rectangle3D[0];
        //obstacle[0] = new Rectangle3D(new Vector3D(0.0, 0.0, -4), new Vector3D(0.0, 0.0, 1.0), .5, .5);
        //obstacle[1] = new Rectangle3D(new Vector3D(0.0, 0.0, -3), new Vector3D(0.0, 0.0, 1.0), .5, .5);

        sphere = new Sphere[0];
        collisions = new boolean[4][4];
        //sphere[0] = new Sphere(.1, new Vector3D(0, 0, -4));
        //sphere[1] = new Sphere(.1, new Vector3D(-.2, .2, -4));


        //sphere[0].vel = new Vector3D(.0, 0.0, .004);
        //sphere[1].vel = new Vector3D(-.005, 0.005, .008);
        
    }

    public void display(GLAutoDrawable drawable) {
        level.frame();
        player.pos.x = head.getHeadX();
        player.pos.y = head.getHeadY();
        player.pos.z = head.getHeadZ();
        super.display(drawable);
        GL gl = drawable.getGL();
        GLUT glut = new GLUT();
        drawGrid(gl);
        //System.out.println("Wawawewa1");
        for(int i = 0; i < level.rectangles.size(); i++) {
            //System.out.println("Wawawewa2");
            level.rectangles.get(i).pos = level.rectangles.get(i).pos.add(level.rectangles.get(i).vel);
            level.rectangles.get(i).draw(gl);
            if(level.rectangles.get(i).isIntersecting(player)) {
                System.out.println("REKTANGELPANG!");
            }
        }
        for (int i = 0; i < sphere.length; i++) {
            sphere[i].vel = sphere[i].vel.add(sphere[i].accel);
            sphere[i].pos = sphere[i].pos.add(sphere[i].vel);
            for (int j = i + 1; j < sphere.length; j++) {
                if (sphere[i].isIntersecting(sphere[j])) {
                    if (!collisions[i][j]) {
                        System.out.println("Colliding");
                        sphere[i].doCollision(sphere[j]);
                        collisions[i][j] = true;
                    }
                } else if (collisions[i][j]) {
                    collisions[i][j] = false;
                }

            }
            for (int j = 0; j < wall.length; j++) {
                if (wall[j].isIntersecting(sphere[i])) {
                    System.out.println("Sphere " + i + ", wall " + j);
                    wall[j].doCollision(sphere[i]);
                }
            }
            if(player.isIntersecting(sphere[i])) {
                System.out.println("PANG");
            }
            gl.glPushMatrix();
            gl.glTranslated(sphere[i].pos.x, sphere[i].pos.y, sphere[i].pos.z);
            glut.glutSolidSphere(sphere[i].radius, 100, 100);
            gl.glPopMatrix();
        }

        for(int i = 0; i < obstacle.length; i++) {
            obstacle[i].pos = obstacle[i].pos.add(obstacle[i].vel);
            gl.glPushMatrix();
            gl.glTranslated(obstacle[i].pos.x, obstacle[i].pos.y, obstacle[i].pos.z);
            drawCube(gl, 0.2f);
            gl.glPopMatrix();
            if(obstacle[i].isIntersecting(player)) {
                System.out.println("PANG");
            }
        }
    }
}
