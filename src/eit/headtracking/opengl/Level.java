/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 *
 * @author vegar
 */
public abstract class Level {

    protected DodgeGame game;
    protected LinkedList<Rectangle3D> rectangles = new LinkedList<Rectangle3D>();
    protected LinkedList<Sphere> spheres = new LinkedList<Sphere>();
    protected LinkedList<Circle> circles = new LinkedList<Circle>();

    public Level(DodgeGame g) {
        this.game = g;
    }

    public abstract void frame();
}
