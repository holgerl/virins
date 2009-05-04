/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eit.headtracking.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author vegar
 */
public class GridRoom implements Drawable {

    float depth = -10;
    float roomWidth = .5f;
    float roomHeight = .5f;
    int numLines = 5;

    public void draw(GL gl) {
        drawGrid(gl);
    }

    void drawGrid(GL gl) {
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(gl.GL_LINES);
        float zstep = depth / numLines;
        float xstep = 2.0f * roomWidth / numLines;
        float ystep = 2.0f * roomHeight / numLines;

        for (int i = 0; i <= numLines; i++) {
            // Left
            gl.glVertex3f(-roomWidth, -roomHeight, i * zstep);
            gl.glVertex3f(-roomWidth, roomHeight, i * zstep);
            // Right
            gl.glVertex3f(roomWidth, -roomHeight, i * zstep);
            gl.glVertex3f(roomWidth, roomHeight, i * zstep);
            // Floor
            gl.glVertex3f(-roomWidth, -roomHeight, i * zstep);
            gl.glVertex3f(roomWidth, -roomHeight, i * zstep);
            // Roof
            gl.glVertex3f(-roomWidth, roomHeight, i * zstep);
            gl.glVertex3f(roomWidth, roomHeight, i * zstep);

            // Left depth lines
            gl.glVertex3f(-roomWidth, -roomHeight + i * ystep, 0.0f);
            gl.glVertex3f(-roomWidth, -roomHeight + i * ystep, depth);

            // Right depth lines
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, 0.0f);
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, depth);

            // Roof depth lines
            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, 0.0f);
            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, depth);

            // Floor depth lines
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, 0.0f);
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, depth);

            // Background grid
            gl.glVertex3f(-roomWidth, -roomHeight + i * ystep, depth);
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, depth);

            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, depth);
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, depth);

        }
        gl.glEnd();

    }
}
