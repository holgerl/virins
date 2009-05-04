/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package eit.headtracking.opengl;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author vegar
 */
public class Level {
    DodgeGame game;
    long frame;
    HashMap<Integer, ArrayList<Rectangle3D>> schedRectangles = new HashMap<Integer, ArrayList<Rectangle3D>>();
    HashMap<Integer, ArrayList<Sphere>> schedSpheres = new HashMap<Integer, ArrayList<Sphere>>();
    ArrayList<Rectangle3D> rectangles = new ArrayList<Rectangle3D>();
    ArrayList<Sphere> spheres = new ArrayList<Sphere>();
    public Level(DodgeGame g) {
        this.game = g;
    }

    public void wallHole() {
        Vector3D normal = new Vector3D(0.0, 0.0, 1.0);
        Vector3D orientation = new Vector3D(1.0, 0.0, 0.0);
        double width = 1.0/8.0;
        rectangles.add(new Rectangle3D(new Vector3D(0,-1.0/4.0,game.roomDepth), normal, orientation, .5, .1));
        rectangles.add(new Rectangle3D(new Vector3D(0,1.0/4.0, game.roomDepth), normal, orientation, .5, .1));
        //rectangles.add(new Rectangle3D(new Vector3D(-1.0/8.0, 0, game.roomDepth), normal, orientation, width, width));
        //rectangles.add(new Rectangle3D(new Vector3D(1.0/8.0, 0, game.roomDepth), normal, orientation, width, width));
    }
    
    public void frame() {
        
        if(frame % 100000 == 0) {
            wallHole();
        }
        frame++;
    }
}
