package eit.headtracking;

import eit.headtracking.wiimote.DualWiimoteHeadTracker;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import eit.headtracking.wiimote.WiimoteHeadTracker;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class JOGL implements GLEventListener, KeyListener {

    private static Fullscreen fullscreen = new Fullscreen();

    public static void main(String[] args) {
        Frame frame = new Frame("Simple JOGL Application");
        GLCanvas canvas = new GLCanvas();
        JOGL jogl = new JOGL();
        canvas.addGLEventListener(jogl);
        frame.add(canvas);
        frame.addKeyListener(jogl);

        frame.setSize(640, 480);
        final Animator animator = new Animator(canvas);
        frame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {

                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        //fullscreen.init(frame);
        frame.setVisible(true);

        animator.start();
    }
    private Texture texture;
    private HeadTracker head;
    private Test test;
    float screenAspect = 1.6f;
    private boolean imageMode = false;
    float ballDepth[] = { 1.0f, 3.0f, 5.0f, 7.0f, 9.0f };
    //FloatBuffer light = new

    public JOGL() {
        this.head = new WiimoteHeadTracker();
        this.head.start();
        this.test = new Test(head);
    }

    public void init(GLAutoDrawable drawable) {
        System.out.println("WTF=");
        // Use debug pipeline
        // drawable.setGL(new DebugGL(drawable.getGL()));
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        gl.glEnable(GL.GL_TEXTURE_2D);
        if (imageMode) {
            texture = load("/home/vegar/Bilder/startsv7.jpg");
        }
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL gl = drawable.getGL();
        GLU glu = new GLU();

        if (height <= 0) { // avoid a divide by zero error!

            height = 1;
        }
        final float h = (float) width / (float) height;
        gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(45.0f, h, 0.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glLightfv
    }

    public void display(GLAutoDrawable drawable) {
        test.repaint();
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        float nearPlane = .05f;
        //gl.glFrustum(-.5f, .5f, -.5f, .5f, 0.0f, 50.0f);
        
        gl.glFrustum(nearPlane * (-.5f * screenAspect - head.getHeadX()) / head.getHeadZ(),
                nearPlane * (.5f * screenAspect - head.getHeadX()) / head.getHeadZ(),
                nearPlane * (-.5f - head.getHeadY()) / head.getHeadZ(),
                nearPlane * (.5f - head.getHeadY()) / head.getHeadZ(),
                nearPlane, 50);
                
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        glu.gluLookAt(head.getHeadX(), head.getHeadY(), head.getHeadZ(), head.getHeadX(), head.getHeadY(), -5f, .0f, 1.0f, .0f);
        if (!imageMode) {
            drawGrid(gl, 0.0f);
            drawTargets(gl, .01f, .02f, ballDepth[0]);
            drawTargets(gl, .05f, .02f, ballDepth[1]);
            drawTargets(gl, -.05f, -.02f, ballDepth[2]);
            drawTargets(gl, -.005f, -.002f, ballDepth[3]);
            drawTargets(gl, -.01f, .01f, ballDepth[4]);
        } else {
            drawImage(gl);
        }
        for(int i = 0; i < ballDepth.length; i++) {
            ballDepth[i] += 0.1f;
            if(ballDepth[i] > 15.0) {
                ballDepth[i] = -5f;
            }
        }

        gl.glFlush();
    }

    void drawTargets(GL gl, float x, float y, float z) {
        GLUT glut = new GLUT();

        //gl.glClear(gl.GL_COLOR_BUFFER_BIT);
        /*
        float width = 0.002f;
        
        gl.glBegin(GL.GL_QUADS);
        gl.glVertex3f(x-width, y-width, z);
        gl.glVertex3f(x+width, y-width, z);
        gl.glVertex3f(x+width, y+width, z);
        gl.glVertex3f(x-width, y+width, z);
        gl.glEnd();
         */
        gl.glColor3f(0.0f, 1.0f, 0.0f);
        /*
        gl.glBegin(gl.GL_LINES);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(.0f, 0.0f, -5.0f);
        gl.glEnd();
         */

        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glTranslatef(x, y, z);
        glut.glutSolidSphere(0.05f, 100, 100);
        gl.glTranslatef(-x, -y, -z);
    }

    void drawGrid(GL gl, float depth) {
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glScalef(screenAspect, 1.0f, 1.0f);
        gl.glBegin(gl.GL_LINES);
        float step = 1.0f;
        int numLines = 5;
        for (int i = 0; i <= numLines; i++) {
            // Left
            gl.glVertex3f(-.5f, -.5f, depth - i * step);
            gl.glVertex3f(-.5f, .5f, depth - i * step);
            // Right
            gl.glVertex3f(.5f, -.5f, depth - i * step);
            gl.glVertex3f(.5f, .5f, depth - i * step);
            // Floor
            gl.glVertex3f(-.5f, -.5f, depth - i * step);
            gl.glVertex3f(.5f, -.5f, depth - i * step);
            // Roof
            gl.glVertex3f(-.5f, .5f, depth - i * step);
            gl.glVertex3f(.5f, .5f, depth - i * step);

            // Left depth lines
            gl.glVertex3f(-.5f, -.5f + i * step / numLines, depth);
            gl.glVertex3f(-.5f, -.5f + i * step / numLines, depth - numLines * step);

            // Right depth lines
            gl.glVertex3f(.5f, -.5f + i * step / numLines, depth);
            gl.glVertex3f(.5f, -.5f + i * step / numLines, depth - numLines * step);

            // Roof depth lines
            gl.glVertex3f(-.5f + i * step / numLines, .5f, depth);
            gl.glVertex3f(-.5f + i * step / numLines, .5f, depth - numLines * step);

            // Floor depth lines
            gl.glVertex3f(-.5f + i * step / numLines, -.5f, depth);
            gl.glVertex3f(-.5f + i * step / numLines, -.5f, depth - numLines * step);

            // Background grid
            gl.glVertex3f(-.5f, -.5f + i * step / numLines, depth - step * numLines);
            gl.glVertex3f(.5f, -.5f + i * step / numLines, depth - step * numLines);

            gl.glVertex3f(-.5f + i * step / numLines, .5f, depth - step * numLines);
            gl.glVertex3f(-.5f + i * step / numLines, -.5f, depth - step * numLines);
        }
        gl.glEnd();
        gl.glPopMatrix();

    }

    void drawImage(GL gl) {
        gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        gl.glScalef(-screenAspect * texture.getAspectRatio(), 1.0f, 1.0f);
        //gl.glScalef(8.0f, 8.0f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        texture.bind();
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, -0.5f);	// Bottom Left Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, -0.5f);	// Bottom Right Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, -0.5f);	// Top Right Of The Texture and Quad
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, -0.5f);	// Top Left Of The Texture and Quad
        gl.glEnd();
    }

    void drawCube(GL gl) {
        gl.glBegin(GL.GL_QUADS);
        gl.glColor3f(0.0f, 1.0f, 0.0f);			// Set The Color To Green
        gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Top)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Top)
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Bottom Left Of The Quad (Top)
        gl.glVertex3f(1.0f, 1.0f, 1.0f);

        gl.glColor3f(1.0f, 0.5f, 0.0f);			// Set The Color To Orange
        gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Top Right Of The Quad (Bottom)
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Top Left Of The Quad (Bottom)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        //
        gl.glColor3f(1.0f, 0.0f, 0.0f);			// Set The Color To Red
        // top left
        gl.glVertex3f(1.0f, 1.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)

        gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Front)
        gl.glVertex3f(1.0f, -1.0f, 1.0f);


        gl.glColor3f(1.0f, 1.0f, 0.0f);			// Set The Color To Yellow
        gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Back)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Back)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Back)
        gl.glVertex3f(1.0f, 1.0f, -1.0f);

        gl.glColor3f(0.0f, 0.0f, 1.0f);			// Set The Color To Blue
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Top Right Of The Quad (Left)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Left)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Left)
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);

        gl.glColor3f(1.0f, 0.0f, 1.0f);			// Set The Color To Violet
        gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Right)
        gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Top Left Of The Quad (Right)
        gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Right)
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glEnd();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public static Texture load(String fileName) {
        System.out.println("LOADING");
        Texture text = null;
        try {
            text = TextureIO.newTexture(new File(fileName), false);
            text.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
            text.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Error loading texture " + fileName);
        }
        return text;
    }

    public static void drawLocalAxis(GL gl, float axisLength) {
        gl.glLineWidth(2);
        gl.glBegin(GL.GL_LINES);
        gl.glColor3f(1, 0, 0);
        gl.glVertex3f(-1.0f * axisLength, 0, 0);
        gl.glVertex3f(1.0f * axisLength, 0.0f, 0.0f);

        gl.glColor3f(0, 1, 0);
        gl.glVertex3f(0, -1.0f * axisLength, 0);
        gl.glVertex3f(0.0f, 1.0f * axisLength, 0.0f);

        gl.glColor3f(0, 0, 1);
        gl.glVertex3f(0, 0, -1.0f * axisLength);
        gl.glVertex3f(0.0f, 0.0f, 1.0f * axisLength);
        gl.glEnd();
    }

    public void keyTyped(KeyEvent arg0) {
    }

    public void keyPressed(KeyEvent arg0) {
        if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
            head.calibrate();
        } else if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        //System.exit(0);
        }
    }

    public void keyReleased(KeyEvent arg0) {
    }
}

