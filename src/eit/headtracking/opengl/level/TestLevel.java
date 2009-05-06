/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl.level;

import eit.headtracking.opengl.*;

/**
 *
 * @author vegar
 */
public class TestLevel extends Level {
    long frame;
    public TestLevel(DodgeGame g) {
        super(g);
    }

    public void wallHole() {
        Vector3D normal = new Vector3D(0.0, 0.0, 1.0);
        Vector3D orientation = new Vector3D(1.0, 0.0, 0.0);
        double width = 1.0 / 4.0;
        rectangles.add(new Rectangle3D(new Vector3D(0, -3.0 / 8.0, game.roomDepth), normal, orientation, 1.0*game.screenAspect, width));
        rectangles.add(new Rectangle3D(new Vector3D(0, 3.0 / 8.0, game.roomDepth), normal, orientation, 1.0*game.screenAspect, width));
        rectangles.add(new Rectangle3D(new Vector3D(-3.0 / 8.0, 0, game.roomDepth), normal, orientation, width, 1.0));
        rectangles.add(new Rectangle3D(new Vector3D(3.0 / 8.0, 0, game.roomDepth), normal, orientation, width, 1.0));
    }

    public void beam() {
        double width = 0.5*game.screenAspect;
        double height = 0.15;
        double y = (Math.random() * 0.9 - .5);
        //Top
        rectangles.add(new Rectangle3D(new Vector3D(0, y + height / 2.0, game.roomDepth), new Vector3D(0.0, 1.0, 0.0), new Vector3D(1.0, 0.0, 0.0), width, height));
        // Bottom
        rectangles.add(new Rectangle3D(new Vector3D(0, y + -height / 2.0, game.roomDepth), new Vector3D(0.0, -1.0, 0.0), new Vector3D(1.0, 0.0, 0.0), width, height));
        // Front
        rectangles.add(new Rectangle3D(new Vector3D(0, y, height / 2.0 + game.roomDepth), new Vector3D(0.0, 0.0, 1.0), new Vector3D(1.0, 0.0, 0.0), width, height));
        // Back
        rectangles.add(new Rectangle3D(new Vector3D(0, y, -height / 2.0 + game.roomDepth), new Vector3D(0.0, 0.0, -1.0), new Vector3D(1.0, 0.0, 0.0), width, height));
        // Sides
        rectangles.add(new Rectangle3D(new Vector3D(width / 2.0, y, game.roomDepth), new Vector3D(1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), height, height));
        rectangles.add(new Rectangle3D(new Vector3D(-width / 2.0, y, game.roomDepth), new Vector3D(-1.0, 0.0, 0.0), new Vector3D(0.0, 0.0, 1.0), height, height));
    }

    void ball() {
        spheres.add(new Sphere(0.1, new Vector3D((Math.random() - .5f) * .8, (Math.random() - .5)*.8, game.roomDepth)));
    }
    void circle() {
        circles.add(new Circle(new Vector3D((Math.random() - .5f)*.8, (Math.random() - .5f)*.8, game.roomDepth), new Vector3D(0.0,0.0,1.0), 0.1));
    }
    public void frame() {
        if((frame + 50L) % 300 == 0) {
            circle();
        }
        if (frame % 400 == 0) {
            beam();
        }
        if ((frame + 100L) % 400 == 0) {
            wallHole();
        }
        if ((frame + 200L) % 50 == 0) {
            ball();
        }
        frame++;
    }
}
