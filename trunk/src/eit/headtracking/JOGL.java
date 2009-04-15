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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

public class JOGL implements GLEventListener, KeyListener {

    private static Fullscreen fullscreen = new Fullscreen();

    public static void main(String[] args) throws IOException {
        Frame frame = new Frame("Simple JOGL Application");
        File file = new File("headtracking.properties");
        FileInputStream fis = new FileInputStream("headtracking.properties");
        Properties prop = new Properties(System.getProperties());
        prop.load(fis);
        System.setProperties(prop);
        fis.close();
        System.out.println(file.getAbsolutePath());
        GLCanvas canvas = new GLCanvas();
        JOGL jogl = new JOGL();
        canvas.addGLEventListener(jogl);
        frame.add(canvas);
        frame.addKeyListener(jogl);
        canvas.addKeyListener(jogl);

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
    Random random = new Random();
    private Texture texture;
    private HeadTracker head;
    private Test test;
    float screenAspect = 1.7f;
    private boolean imageMode = false;
    float ballDepth[][] = new float[10][3];
    float roomDepth = -10.0f;
    //FloatBuffer light = new

    public JOGL() {
        try {
            this.head = (HeadTracker) Class.forName(System.getProperty("eit.headtracking.headtracker")).newInstance();
            this.screenAspect = Float.parseFloat(System.getProperty("eit.headtracking.screenaspect"));
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(JOGL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(JOGL.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(JOGL.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.head.start();
        this.test = new Test(head);
    }

    public void randomizeTargets() {
        for (int i = 0; i < ballDepth.length; i++) {
            ballDepth[i][0] = (.5f - random.nextFloat()) * screenAspect;
            ballDepth[i][1] = .5f - random.nextFloat();
            ballDepth[i][2] = 5.0f - random.nextFloat() * 10.0f;
            if (ballDepth[i][2] > 0.0f) {
                ballDepth[i][0] /= 3.0f;
                ballDepth[i][1] /= 3.0f;
            }
        }
    }

    public void init(GLAutoDrawable drawable) {
        randomizeTargets();
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
        gl.glEnable(GL.GL_DEPTH_TEST);
        //gl.glEnable(GL.GL_CULL);
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
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();
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
        float nearPlane = .05f;

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
        if (!imageMode) {
            drawGrid(gl, roomDepth);
        } else {
            drawImage(gl);
        }
        for (int i = 0; i < ballDepth.length; i++) {
            gl.glColor3f(1.0f, 1.0f, 1.0f);
            gl.glBegin(GL.GL_LINES);
            gl.glVertex3f(ballDepth[i][0], ballDepth[i][1], roomDepth);
            gl.glVertex3f(ballDepth[i][0], ballDepth[i][1], ballDepth[i][2]);
            gl.glEnd();
            gl.glPushMatrix();
            gl.glTranslatef(ballDepth[i][0], ballDepth[i][1], ballDepth[i][2]);
            drawTarget(gl, .04f);
            //drawCube(gl, ballDepth[i][0], ballDepth[i][1], ballDepth[i][2]);
            gl.glPopMatrix();
        }

        gl.glFlush();
    }

    void drawCircle(GL gl, double outer, double inner, int slices) {
        double step = Math.PI * 2.0 / (double) slices;
        double theta, dtheta;
        gl.glBegin(GL.GL_TRIANGLES);
        for (int i = 0; i < slices; i++) {
            theta = i * step;
            dtheta = (i + 1) * step;

            gl.glVertex2d(outer * Math.cos(theta), outer * Math.sin(theta));
            gl.glVertex2d(inner * Math.cos(theta), inner * Math.sin(theta));
            gl.glVertex2d(outer * Math.cos(dtheta), outer * Math.sin(dtheta));

            gl.glVertex2d(outer * Math.cos(dtheta), outer * Math.sin(dtheta));
            gl.glVertex2d(inner * Math.cos(theta), inner * Math.sin(theta));
            gl.glVertex2d(inner * Math.cos(dtheta), inner * Math.sin(dtheta));
        }
        gl.glEnd();

    }

    void drawTarget(GL gl, double radius) {
        int n = 5;
        int c = 0;
        float[][] colors = {{1.0f, 0.0f, 0.0f}, {1.0f, 1.0f, 1.0f}};
        double step = radius / (double) n;
        for (int i = 0; i < n; i++) {
            gl.glColor3f(colors[c][0], colors[c][1], colors[c][2]);
            drawCircle(gl, radius - i * step, radius - (i + 1) * step, 20);
            c = (c + 1) % colors.length;
        }
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

    void drawGrid(GL gl, float depth) {
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        //gl.glTranslatef(0.0f, 0.0f, -1.0f);
        gl.glScalef(screenAspect, 1.0f, 1.0f);
        gl.glBegin(gl.GL_LINES);
        int numLines = 5;
        float roomWidth = .5f;
        float roomHeight = .5f;
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

    void drawCube(GL gl, float x, float y, float z) {
        gl.glPushMatrix();
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glBegin(GL.GL_LINES);
        gl.glVertex3f(x, y, roomDepth);
        gl.glVertex3f(x, y, z);
        gl.glEnd();
        gl.glTranslatef(x, y, z);
        gl.glScalef(.01f, .01f, .01f);
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
            System.out.println("SPACE!");
            randomizeTargets();
            head.calibrate();
        } else if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
            System.exit(0);
        //System.exit(0);
        }
    }

    public void keyReleased(KeyEvent arg0) {
    }
}

