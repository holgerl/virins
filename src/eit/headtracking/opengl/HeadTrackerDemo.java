package eit.headtracking.opengl;

import eit.headtracking.wiimote.DualWiimoteHeadTracker;
import com.sun.opengl.util.Animator;
import com.sun.opengl.util.GLUT;
import com.sun.opengl.util.texture.Texture;
import com.sun.opengl.util.texture.TextureCoords;
import com.sun.opengl.util.texture.TextureIO;
import eit.headtracking.HeadTracker;
import eit.headtracking.Test;
import eit.headtracking.wiimote.WiimoteHeadTracker;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.DebugGL;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

public class HeadTrackerDemo extends JFrame implements GLEventListener, KeyListener {

    private static Fullscreen fullscreen;
    Random random = new Random();
    private Texture texture;
     HeadTracker head;
    private Test test;
    public float screenAspect = 1.7f;
    public float roomDepth = -5.0f;
    float roomWidth = -.5f;
    float roomHeight = -.5f;
    //FloatBuffer light = new
    int filter;						// Which Filter To Use
    int fogMode[] = {GL.GL_EXP, GL.GL_EXP2, GL.GL_LINEAR};	// Storage For Three Types Of Fog
    int fogfilter = 0;					// Which Fog To Use
    float[] fogColor = {0.0f, 0.0f, 0.0f, 1.0f};		// Fog Color

    //GridRoom gridRoom = new GridRoom()
    public HeadTrackerDemo() {
        System.out.println("HeadTrackerDemo");
        {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream("headtracking.properties");
                Properties prop = new Properties(System.getProperties());
                prop.load(fis);
                System.setProperties(prop);
                this.head = (HeadTracker) Class.forName(System.getProperty("eit.headtracking.headtracker")).newInstance();
                this.screenAspect = Float.parseFloat(System.getProperty("eit.headtracking.screenaspect"));
            } catch (IOException ex) {
                Logger.getLogger(HeadTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(HeadTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InstantiationException ex) {
                Logger.getLogger(HeadTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(HeadTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    fis.close();
                } catch (IOException ex) {
                    Logger.getLogger(HeadTrackerDemo.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        GLCanvas canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        add(canvas);
        addKeyListener(this);
        canvas.addKeyListener(this);

        setSize(640, 480);
        final Animator animator = new Animator(canvas);
        addWindowListener(new WindowAdapter() {

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
        setLocationRelativeTo(null);
        fullscreen = new Fullscreen();
        fullscreen.init(this);
        setVisible(true);

        animator.start();
        this.head.start();
        //this.test = new Test(head);
    }

    public void init(GLAutoDrawable drawable) {
        System.out.println("HeadTrackerdemo init gl");
        this.test = new Test(head);
        //fullscreen = new Fullscreen();
        //fullscreen.init(this);
        drawable.setGL(new DebugGL(drawable.getGL()));
        GL gl = drawable.getGL();
        System.err.println("INIT GL IS: " + gl.getClass().getName());

        // Enable VSync
        gl.setSwapInterval(1);

        // Setup the drawing area and shading mode
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_SMOOTH); // try setting this to GL_FLAT and see what happens.
        gl.glEnable(GL.GL_TEXTURE_2D);
        gl.glEnable(GL.GL_DEPTH_TEST);

        gl.glFogi(GL.GL_FOG_MODE, fogMode[fogfilter]);		// Fog Mode
        gl.glFogfv(GL.GL_FOG_COLOR, fogColor, 0);
        gl.glFogf(GL.GL_FOG_DENSITY, 0.15f);				// How Dense Will The Fog Be
        gl.glHint(GL.GL_FOG_HINT, GL.GL_DONT_CARE);			// Fog Hint Value
        gl.glFogf(GL.GL_FOG_START, 0.0f);				// Fog Start Depth
        gl.glFogf(GL.GL_FOG_END, -roomDepth);				// Fog End Depth
        gl.glEnable(GL.GL_FOG);					// Enables GL_FOG
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
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        //gl.glScalef(screenAspect, 1, 1);
    }

    public void display(GLAutoDrawable drawable) {
        test.repaint();
        GL gl = drawable.getGL();
        GLU glu = new GLU();
        // Clear the drawing area
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        // Do the projection
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        float nearPlane = .01f;
        //float nearPlane = head.getHeadZ();
        gl.glFrustum(nearPlane * (-.5f * screenAspect - head.getHeadX()) / head.getHeadZ(),
                nearPlane * (.5f * screenAspect - head.getHeadX()) / head.getHeadZ(),
                nearPlane * (-.5f - head.getHeadY()) / head.getHeadZ(),
                nearPlane * (.5f - head.getHeadY()) / head.getHeadZ(),
                nearPlane, 100);

        // Set up the model
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
        //glu.gluLookAt(0.0f, 0.0f, -2.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f);
        glu.gluLookAt(head.getHeadX(), head.getHeadY(), head.getHeadZ(), head.getHeadX(), head.getHeadY(), .0f, .0f, 1.0f, .0f);


        gl.glFlush();
    }

    void drawCube(GL gl, float scale) {
        gl.glPushMatrix();
        gl.glScalef(scale, scale, scale);
        gl.glBegin(GL.GL_QUADS);
        gl.glColor3f(0.0f, 1.0f, 0.0f);			// Set The Color To Green
        gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Top)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Top)
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Bottom Left Of The Quad (Top)
        gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Bottom Right Of The Quad (Top)

        gl.glColor3f(1.0f, 0.5f, 0.0f);			// Set The Color To Orange
        gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Top Right Of The Quad (Bottom)
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Top Left Of The Quad (Bottom)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Bottom)
        gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Bottom)

        gl.glColor3f(1.0f, 0.0f, 0.0f);			// Set The Color To Red
        gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Top Right Of The Quad (Front)
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Top Left Of The Quad (Front)
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Front)
        gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Bottom Right Of The Quad (Front)

        gl.glColor3f(1.0f, 1.0f, 0.0f);			// Set The Color To Yellow
        gl.glVertex3f(1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Back)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Right Of The Quad (Back)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Back)
        gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Back)

        gl.glColor3f(0.0f, 0.0f, 1.0f);			// Set The Color To Blue
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);			// Top Right Of The Quad (Left)
        gl.glVertex3f(-1.0f, 1.0f, -1.0f);			// Top Left Of The Quad (Left)
        gl.glVertex3f(-1.0f, -1.0f, -1.0f);			// Bottom Left Of The Quad (Left)
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);			// Bottom Right Of The Quad (Left)

        gl.glColor3f(1.0f, 0.0f, 1.0f);			// Set The Color To Violet
        gl.glVertex3f(1.0f, 1.0f, -1.0f);			// Top Right Of The Quad (Right)
        gl.glVertex3f(1.0f, 1.0f, 1.0f);			// Top Left Of The Quad (Right)
        gl.glVertex3f(1.0f, -1.0f, 1.0f);			// Bottom Left Of The Quad (Right)
        gl.glVertex3f(1.0f, -1.0f, -1.0f);
        gl.glEnd();
        gl.glPopMatrix();
    }

    void drawTexturedTarget(GL gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(0.0f, 0.0f, -5.0f);
        gl.glVertex3f(x, y, z);
        gl.glEnd();
        gl.glTranslatef(x, y, z);
        gl.glScalef(0.05f, 0.05f, 0.05f);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-1.0f, -1.0f, 1.0f);	// Bottom Left Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(1.0f, -1.0f, 1.0f);	// Bottom Right Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(1.0f, 1.0f, 1.0f);	// Top Right Of The Texture and Quad
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-1.0f, 1.0f, 1.0f);
        gl.glEnd();
        gl.glPopMatrix();
    }

    void drawSphere(GL gl, float x, float y, float z) {
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

        gl.glBegin(gl.GL_LINES);
        gl.glVertex3f(x, y, z);
        gl.glVertex3f(.0f, 0.0f, -5.0f);
        gl.glEnd();


        gl.glColor3f(1.0f, 0.0f, 0.0f);
        gl.glTranslatef(x, y, z);
        //glut.glutSolidCube(.2f);
        glut.glutSolidSphere(0.05f, 100, 100);
        gl.glTranslatef(-x, -y, -z);
    }

    void drawImage(GL gl) {
        gl.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
        gl.glScalef(-screenAspect * texture.getAspectRatio(), 1.0f, 1.0f);
        //gl.glScalef(-3, 2, 3);
        //gl.glScalef(8.0f, 8.0f, 1.0f);
        gl.glBegin(GL.GL_QUADS);
        //texture.bind();
        gl.glTexCoord2f(0.0f, 0.0f);
        gl.glVertex3f(-.5f, -.5f, 0.0f);	// Bottom Left Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 0.0f);
        gl.glVertex3f(.5f, -.5f, 0.0f);	// Bottom Right Of The Texture and Quad
        gl.glTexCoord2f(1.0f, 1.0f);
        gl.glVertex3f(.5f, .5f, 0.0f);	// Top Right Of The Texture and Quad
        gl.glTexCoord2f(0.0f, 1.0f);
        gl.glVertex3f(-.5f, .5f, 0.0f);	// Top Left Of The Texture and Quad
        gl.glEnd();
    }

    void drawGrid(GL gl) {
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(gl.GL_LINES);
        int numLines = 5;
        float zstep = roomDepth / numLines;
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
            gl.glVertex3f(-roomWidth, -roomHeight + i * ystep, roomDepth);

            // Right depth lines
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, 0.0f);
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, roomDepth);

            // Roof depth lines
            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, 0.0f);
            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, roomDepth);

            // Floor depth lines
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, 0.0f);
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, roomDepth);

            // Background grid
            gl.glVertex3f(-roomWidth, -roomHeight + i * ystep, roomDepth);
            gl.glVertex3f(roomWidth, -roomHeight + i * ystep, roomDepth);

            gl.glVertex3f(-roomWidth + i * xstep, roomHeight, roomDepth);
            gl.glVertex3f(-roomWidth + i * xstep, -roomHeight, roomDepth);

        }
        gl.glEnd();

    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged, boolean deviceChanged) {
    }

    public static Texture load(String fileName) {
        System.out.println("LOADING");
        Texture text = null;
        try {
            text = TextureIO.newTexture(new File(fileName), false);
            text.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
            text.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
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
            System.out.println("SPACE!");
            //randomizeTargets();
            head.calibrate();
        } else if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        }
    }

    public void keyReleased(KeyEvent arg0) {
    }
}

