import com.jogamp.opengl.util.texture.Texture;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Random;
import static javax.media.opengl.GL.GL_REPEAT;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_WRAP_S;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.GL2GL3.GL_QUADS;
import static javax.media.opengl.GL2GL3.GL_TEXTURE_1D;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT1;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_POSITION;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;
import robotrace.Base;
import robotrace.Texture1D;
import robotrace.Vector;

/**
 * Handles all of the RobotRace graphics functionality, which should be extended
 * per the assignment.
 *
 * OpenGL functionality: - Basic commands are called via the gl object; -
 * Utility commands are called via the glu and glut objects;
 *
 * GlobalState: The gs object contains the GlobalState as described in the
 * assignment: - The camera viewpoint angles, phi and theta, are changed
 * interactively by holding the left mouse button and dragging; - The camera
 * view width, vWidth, is changed interactively by holding the right mouse
 * button and dragging upwards or downwards; - The center point can be moved up
 * and down by pressing the 'q' and 'z' keys, forwards and backwards with the
 * 'w' and 's' keys, and left and right with the 'a' and 'd' keys; - Other
 * settings are changed via the menus at the top of the screen.
 *
 * Textures: Place your "track.jpg", "brick.jpg", "head.jpg", and "torso.jpg"
 * files in the same folder as this file. These will then be loaded as the
 * texture objects track, bricks, head, and torso respectively. Be aware, these
 * objects are already defined and cannot be used for other purposes. The
 * texture objects can be used as follows:
 *
 * gl.glColor3f(1f, 1f, 1f); track.bind(gl); gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0); gl.glVertex3d(0, 0, 0); gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0); gl.glTexCoord2d(1, 1); gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1); gl.glVertex3d(0, 1, 0); gl.glEnd();
 *
 * Note that it is hard or impossible to texture objects drawn with GLUT. Either
 * define the primitives of the object yourself (as seen above) or add
 * additional textured primitives to the GLUT object.
 */
public class RobotRace extends Base {

    /**
     * Array of the four robots.
     */
    private final Robot[] robots;

    /**
     * Instance of the camera.
     */
    private final Camera camera;

    /**
     * Instance of the race track.
     */
    private final RaceTrack raceTrack;

    /**
     * Instance of the terrain.
     */
    private final Terrain terrain;

    private Texture1D terrainTexture;

    /**
     * Constructs this robot race by initializing robots, camera, track, and
     * terrain.
     */
    public RobotRace() {
        // Create a new array of four robots
        robots = new Robot[4];

        Random rand = new Random();

        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD, new Vector(0, 0, 0), rand);

        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new Vector(0, 0, 0), rand);

        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, new Vector(0, 0, 0), rand);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new Vector(0, 0, 0), rand);

        // Initialize the camera
        camera = new Camera();

        // Initialize the race track
        raceTrack = new RaceTrack();

        // Initialize the terrain
        terrain = new Terrain();
    }

    float Lightcolor1[]
            = {
                0.0f, 0.0f, 0.0f, 1f
            };
    float Ambientcolor1[]
            = {
                0.0f, 0.0f, 0.0f, 1
            };
    float Lightcolor2[]
            = {
                0.7f, 0.7f, 0.7f, 1f
            };
    float Ambientcolor2[]
            = {
                0.3f, 0.3f, 0.3f, 1
            };

    /**
     * Called upon the start of the application. Primarily used to configure
     * OpenGL.
     */
    @Override
    public void initialize() {

        //Initialize the shadeing model and two lights, one is an ambient light and the other will be placed near the camera.
        gl.glShadeModel(GL_SMOOTH);
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_LIGHT1);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, Lightcolor1, 0);
        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, Ambientcolor1, 0);
        gl.glLightfv(GL_LIGHT1, GL_DIFFUSE, Lightcolor2, 0);
        gl.glLightfv(GL_LIGHT1, GL_AMBIENT, Ambientcolor2, 0);
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Anti-aliasing can be enabled by uncommenting the following 4 lines.
        // This can however cause problems on some graphics cards.
        gl.glEnable(GL_LINE_SMOOTH);
        gl.glEnable(GL_POLYGON_SMOOTH);
//        gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
//        gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Normalize normals.
        gl.glEnable(GL_NORMALIZE);

        // Converts colors to materials when lighting is enabled.
        gl.glEnable(GL_COLOR_MATERIAL);
        gl.glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);

        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        // Try to load four textures, add more if you like.
        track = loadTexture("track.jpg");
        brick = loadTexture("brick.jpg");
        head = loadTexture("head.jpg");
        torso = loadTexture("torso.jpg");
        terrainTexture = new Texture1D(gl, new Vector[]{new Vector(0, 0, 1), new Vector(1, 0.95, 0.47), new Vector(0.247, 0.804, 0), new Vector(0.57, 0.294, 0)});
    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective(gs.vWidth, (float) gs.w / (float) gs.h, 0.05 * gs.vDist, 50 * gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        //This light is located near the camera
        float posLight2[]
                = {
                    -5, 5, 0, 1f
                };
        gl.glLightfv(GL_LIGHT1, GL_POSITION, posLight2, 0);

        // Update the view according to the camera mode
        camera.update(gs.camMode);

        //This light stays at the same location
        float posLight1[]
                = {
                    3, 2, 5, 1f
                };
        gl.glLightfv(GL_LIGHT0, GL_POSITION, posLight1, 0);

    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {
        double trackwidth = 6;
        double time = gs.tAnim; 


        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);

        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);

        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Draw the axis frame
        if (gs.showAxes) {
            drawAxisFrame();
        }

        // Draw the robots
        for (int i = 0; i < robots.length; i++) {
            Robot r = robots[i];

            r.walkAnim(time);

            r.move(raceTrack.getPoint(gs.trackNr, (float) time, i, trackwidth));

            r.drawStickFigure = gs.showStick;
            r.draw();

            Vector tangent = raceTrack.getTangent(gs.trackNr, time, i);
            double anglePhi = Math.atan2(tangent.y(), tangent.x());
            double angletheta =0.5*Math.PI-Math.asin(new Vector(tangent.x(), tangent.y(), 0).length()/tangent.length()); //assume going downhill 
            if (tangent.z() < 0.0001 && tangent.z()>-0.0001) { //if z is negletively small assume z == 0 to prevent weird twitches and funny rotations
                angletheta = 0;
            } else {if (tangent.z() >0 ) //incase the robot is moving downhill rotate in the other direction
            {
                angletheta = -angletheta;
            }
            }

            double[] rotationXYZ = new double[]{ //adjust rotation of individual robot
                0, angletheta, anglePhi // robots only rotate forwards, backwards or sideways so no x-direction tilts are needed
            };

            r.rotate(rotationXYZ);
        }

        // Draw race track
        raceTrack.draw(gs.trackNr);

        // Draw terrain
        terrain.draw();

        gl.glColor3f(0f, 0f, 0f);

        // Unit box around origin.
        glut.glutWireCube(1f);
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame() {
        //Draw the x-axis
        gl.glPushMatrix();
        gl.glColor3f(1f, 0f, 0f);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(1f, 0f, 0f);
        gl.glEnd();
        gl.glFlush();
        gl.glTranslatef(1f, 0, 0);
        gl.glRotatef(90, 0, 1, 0);
        glut.glutSolidCone(0.05, 0.1, 10, 1);
        gl.glPopMatrix();

        //Draw the y-axis
        gl.glPushMatrix();
        gl.glColor3f(0f, 1f, 0f);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0f, 1f, 0f);
        gl.glEnd();
        gl.glFlush();
        gl.glTranslatef(0, 1f, 0);
        gl.glRotatef(-90, 1, 0, 0);
        glut.glutSolidCone(0.05, 0.1, 10, 1);
        gl.glPopMatrix();

        //Draw the z-axis
        gl.glPushMatrix();
        gl.glColor3f(0f, 0f, 1f);
        gl.glBegin(GL_LINES);
        gl.glVertex3f(0, 0, 0);
        gl.glVertex3f(0f, 0f, 1f);
        gl.glEnd();
        gl.glFlush();
        gl.glTranslatef(0, 0, 1f);
        glut.glutSolidCone(0.05, 0.1, 10, 1);
        gl.glPopMatrix();

        //Draw the sphere in the origin
        gl.glColor3f(1f, 1f, 0f);
        glut.glutSolidSphere(0.1, 20, 20);

    }

    /**
     * Draws a cube consisting of 6 quads with size 1x1, the origin is in the
     * exact center of the cube
     *
     * @param texture
     * @param mappingCoordinates The order in which the coordinates should be
     * specified is: right, front, left, back, bottom, top
     */
    public void drawCube(Texture texture, QuadTexMappingCoordinates[] mappingCoordinates) {
        texture.bind(gl);

        gl.glBegin(GL_QUADS);

        //When looking along the X-axis, draw the right side
        gl.glTexCoord2d(mappingCoordinates[0].p1[0], mappingCoordinates[0].p1[1]);
        gl.glVertex3d(-0.5, -0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[0].p2[0], mappingCoordinates[0].p2[1]);
        gl.glVertex3d(0.5, -0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[0].p3[0], mappingCoordinates[0].p3[1]);
        gl.glVertex3d(0.5, -0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[0].p4[0], mappingCoordinates[0].p4[1]);
        gl.glVertex3d(-0.5, -0.5, 0.5);

        //When looking along the X-axis, draw the front side
        gl.glTexCoord2d(mappingCoordinates[1].p1[0], mappingCoordinates[1].p1[1]);
        gl.glVertex3d(0.5, -0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[1].p2[0], mappingCoordinates[1].p2[1]);
        gl.glVertex3d(0.5, 0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[1].p3[0], mappingCoordinates[1].p3[1]);
        gl.glVertex3d(0.5, 0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[1].p4[0], mappingCoordinates[1].p4[1]);
        gl.glVertex3d(0.5, -0.5, 0.5);

        //When looking along the X-axis, draw the left side
        gl.glTexCoord2d(mappingCoordinates[2].p1[0], mappingCoordinates[2].p1[1]);
        gl.glVertex3d(0.5, 0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[2].p2[0], mappingCoordinates[2].p2[1]);
        gl.glVertex3d(-0.5, 0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[2].p3[0], mappingCoordinates[2].p3[1]);
        gl.glVertex3d(-0.5, 0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[2].p4[0], mappingCoordinates[2].p4[1]);
        gl.glVertex3d(0.5, 0.5, 0.5);

        //When looking along the X-axis, draw the back side
        gl.glTexCoord2d(mappingCoordinates[3].p1[0], mappingCoordinates[3].p1[1]);
        gl.glVertex3d(-0.5, 0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[3].p2[0], mappingCoordinates[3].p2[1]);
        gl.glVertex3d(-0.5, 0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[3].p3[0], mappingCoordinates[3].p3[1]);
        gl.glVertex3d(-0.5, -0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[3].p4[0], mappingCoordinates[3].p4[1]);
        gl.glVertex3d(-0.5, -0.5, -0.5);

        //When looking along the X-axis, draw the bottom side
        gl.glTexCoord2d(mappingCoordinates[4].p1[0], mappingCoordinates[4].p1[1]);
        gl.glVertex3d(-0.5, -0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[4].p2[0], mappingCoordinates[4].p2[1]);
        gl.glVertex3d(0.5, -0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[4].p3[0], mappingCoordinates[4].p3[1]);
        gl.glVertex3d(0.5, 0.5, -0.5);
        gl.glTexCoord2d(mappingCoordinates[4].p4[0], mappingCoordinates[4].p4[1]);
        gl.glVertex3d(-0.5, 0.5, -0.5);

        //When looking along the X-axis, draw the top side
        gl.glTexCoord2d(mappingCoordinates[5].p1[0], mappingCoordinates[5].p1[1]);
        gl.glVertex3d(0.5, -0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[5].p2[0], mappingCoordinates[5].p2[1]);
        gl.glVertex3d(0.5, 0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[5].p3[0], mappingCoordinates[5].p3[1]);
        gl.glVertex3d(-0.5, 0.5, 0.5);
        gl.glTexCoord2d(mappingCoordinates[5].p4[0], mappingCoordinates[5].p4[1]);
        gl.glVertex3d(-0.5, -0.5, 0.5);

        gl.glEnd();
    }

    /**
     * Materials that can be used for the robots.
     */
    public enum Material {

        /**
         * Gold material properties.
         */
        GOLD(
                new float[]{
                    0.75f, 0.6f, 0.22f, 1.0f
                },
                new float[]{
                    0.63f, 0.55f, 0.36f, 1.0f
                },
                new float[]{
                    0.25f, 0.1995f, 0.0745f
                },
                0.6f
        ),
        /**
         * Silver material properties.
         */
        SILVER(
                new float[]{
                    0.5f, 0.5f, 0.5f, 1.0f
                },
                new float[]{
                    0.5f, 0.5f, 0.5f, 1.0f
                },
                new float[]{
                    0.1925f, 0.1925f, 0.1925f
                },
                0.5f
        ),
        /**
         * Wood material properties. Modify the default values to make it look
         * like wood.
         */
        WOOD(
                new float[]{
                    0.54f, 0.27f, 0.075f, 1.0f
                },
                new float[]{
                    0.1f, 0.05f, 0.05f, 1.0f
                },
                new float[]{
                    0.01f, 0.01f, 0.05f
                },
                0.1f
        ),
        /**
         * Orange material properties.
         */
        ORANGE(
                new float[]{
                    1f, 0.45f, 0f, 1.0f
                },
                new float[]{
                    0.05f, 0.05f, 0.05f, 1.0f
                },
                new float[]{
                    0.0f, 0.0f, 0.0f
                },
                0.0f
        );

        /**
         * The diffuse RGBA reflectance of the material.
         */
        float[] diffuse;

        /**
         * The specular RGBA reflectance of the material.
         */
        float[] specular;

        /**
         * The ambient RGBA relectance of the material
         */
        float[] ambient;

        /**
         * The materials' shininess
         */
        float shininess;

        Texture texture;

        /**
         * Constructs a new material with diffuse and specular properties.
         */
        private Material(float[] diffuse, float[] specular, float[] ambient, float shininess, Texture texture) {
            this.diffuse = diffuse;
            this.specular = specular;
            this.ambient = ambient;
            this.shininess = shininess;
            this.texture = texture;
        }

        private Material(float[] diffuse, float[] specular, float[] ambient, float shininess) {
            this(diffuse, specular, ambient, shininess, null);
        }
    }

    /**
     * Represents a Robot, to be implemented according to the Assignments.
     */
    private class Robot {

        /*
         * We chose to implement a hierarchichal model for the robot's limbs. A base class "Limb" holds all common info
         * associated with a robot's limb, like its location and rotation around its joint. Each limb class extends this base class.
         * the hierarchy is as follows:
         * - Torso (root)
         - Head
         - UpperLeg
         - LowerLeg
         - Foot
         - UpperLeg
         - LowerLeg
         - Foot
         - UpperLeg
         - LowerLeg
         - Foot
         - UpperLeg
         - LowerLeg
         - Foot
         */
        // <editor-fold defaultstate="collapsed" desc="Limb Classes">
        private abstract class Limb {

            /**
             * Each limb has a local origin, this is also the rotation point
             * when the limb moves
             */
            protected Vector localOrigin;

            /**
             * Each limb has a robot that it belongs to
             */
            protected Robot robot;

            /**
             * Represents the rotation of this limb around its joint (origin),
             * an array of doubles that contain, the rotation around the x, y,
             * and z azis respectively.
             */
            public double[] rotationXYZ;

            /**
             * Constructs a new Limb with the specified localorgin and parent
             * Robot
             *
             * @param localOrigin
             * @param robot
             */
            public Limb(Vector localOrigin, Robot robot) {
                this.robot = robot;
                this.localOrigin = localOrigin;
                this.rotationXYZ = new double[]{
                    0, 0, 0
                };
            }

            /**
             * Draws this limb
             */
            public void draw() {
                /*
                 Each limb is drawn according to the following precedure:
                 1 - Translate to the limbs local origin
                 2 - Rotate the limb according to its rotation
                 3 - Draw the limb's primitives using drawSolid() or drawStickFigure(). 
                 the implementations of these methods are located in the subclasses (Torso, Head, UpperLeg etc.)
                 */

                gl.glPushMatrix();

                //Each limb is drawn with respect to its local origin.
                gl.glTranslated(localOrigin.x(), localOrigin.y(), localOrigin.z());

                //Each limb is rotated around its local origin
                gl.glRotated(rotationXYZ[2], 0, 0, 1);
                gl.glRotated(rotationXYZ[0], 1, 0, 0);
                gl.glRotated(rotationXYZ[1], 0, 1, 0);

                float[] color = robot.material.diffuse;
                gl.glColor4f(color[0], color[1], color[2], color[3]);
                if (this.robot.drawStickFigure) {
                    drawStickFigure();
                } else {
                    drawSolid();
                }

                drawChildLimbs();

                gl.glPopMatrix();
            }

            /**
             * Draws this limb as a stick figure. Stick figures are drawn with
             * black lines as limbs and red spheres as joints
             */
            public abstract void drawStickFigure();

            /**
             * Draws this limb as a solid
             */
            public abstract void drawSolid();

            /**
             * Draw the child limbs of this limb
             */
            public abstract void drawChildLimbs();
        }

        /**
         * Class for the robot's Torso limb. The torso is the root limb for the
         * robot and is connected to the Head and the Upper legs.
         */
        private class Torso extends Limb {

            //The torso has a head, and the upper legs as it's childs
            public Head head;
            public UpperLeg foreLegLeft;
            public UpperLeg foreLegRight;
            public UpperLeg hindLegLeft;
            public UpperLeg hindLegRight;

            /**
             * The distance between the forelegs and hindlegs along the x-axis
             */
            private final double legsOffsetX = 2;

            /**
             * The distance between the left and right legs along the y-axis
             */
            private final double legsOffsetY = 1.4;

            public Torso(Robot robot) {
                super(new Vector(0, 0, 10.2), robot);

                this.head = new Head(new Vector(4.4, 0, 0), robot);
                this.foreLegLeft = new UpperLeg(new Vector(legsOffsetX, legsOffsetY, 0), robot);
                this.foreLegRight = new UpperLeg(new Vector(legsOffsetX, -legsOffsetY, 0), robot);
                this.hindLegLeft = new UpperLeg(new Vector(-legsOffsetX, legsOffsetY, 0), robot);
                this.hindLegRight = new UpperLeg(new Vector(-legsOffsetX, -legsOffsetY, 0), robot);
            }

            @Override
            public void drawStickFigure() {
                //Set the drawing color to black and draw the a line that runs from the neck 
                //to the back of the torso.
                gl.glColor3f(0, 0, 0);
                gl.glPushMatrix();
                gl.glTranslatef(-4.4f, 0f, 0f);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(8.8f, 0f, 0f);
                gl.glEnd();
                gl.glFlush();
                gl.glPopMatrix();

                //Draw shoulder and pelvis lines
                gl.glBegin(GL_LINES);
                gl.glVertex3d(legsOffsetX, 0, 0);
                gl.glVertex3d(legsOffsetX, legsOffsetY, 0);
                gl.glEnd();
                gl.glFlush();

                gl.glBegin(GL_LINES);
                gl.glVertex3d(legsOffsetX, 0, 0);
                gl.glVertex3d(legsOffsetX, -legsOffsetY, 0);
                gl.glEnd();
                gl.glFlush();

                gl.glBegin(GL_LINES);
                gl.glVertex3d(-legsOffsetX, 0, 0);
                gl.glVertex3d(-legsOffsetX, legsOffsetY, 0);
                gl.glEnd();
                gl.glFlush();

                gl.glBegin(GL_LINES);
                gl.glVertex3d(-legsOffsetX, 0, 0);
                gl.glVertex3d(-legsOffsetX, -legsOffsetY, 0);
                gl.glEnd();
                gl.glFlush();
            }

            @Override
            public void drawSolid() {
                //The torso of the robot consists of three blocks, one at the front of the torso, one in the middle and one at the rear.
                //Draw the middle block
                gl.glPushMatrix();
                gl.glTranslated(0, 0, 0.65);
                gl.glScaled(3.4, 3.4, 5.5);
                // <editor-fold defaultstate="collapsed" desc="Texture Coordinates For the middle cube">
                QuadTexMappingCoordinates[] mappingCoords = new QuadTexMappingCoordinates[6];
                //Right side:
                mappingCoords[0] = new QuadTexMappingCoordinates(0, 0.335975, 0.6640625, 0.335975, 0.6640625, 1, 0, 1);
                //Front side:
                mappingCoords[1] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Left side:
                mappingCoords[2] = new QuadTexMappingCoordinates(0.6640625, 0.335975, 0, 0.335975, 0, 1, 0.6640625, 1);
                //Back side:
                mappingCoords[3] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Bottom side:
                mappingCoords[4] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Top side:
                mappingCoords[5] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                // </editor-fold>
                drawCube(torso, mappingCoords);
                gl.glPopMatrix();

                //Draw the front block
                gl.glPushMatrix();
                gl.glTranslated(2.75, 0, -0.4);
                gl.glScaled(2.1, 2.1, 3.4);
                // <editor-fold defaultstate="collapsed" desc="Texture Coordinates For the front cube">
                mappingCoords = new QuadTexMappingCoordinates[6];
                //Right side:
                mappingCoords[0] = new QuadTexMappingCoordinates(0.6640625, 0.335975, 1, 0.335975, 1, 1, 0.6640625, 1);
                //Front side:
                mappingCoords[1] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Left side:
                mappingCoords[2] = new QuadTexMappingCoordinates(1, 0.335975, 0.6640625, 0.335975, 0.6640625, 1, 1, 1);
                //Back side:
                mappingCoords[3] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Bottom side:
                mappingCoords[4] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Top side:
                mappingCoords[5] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                // </editor-fold>                
                drawCube(torso, mappingCoords);
                gl.glPopMatrix();

                //Draw the rear block
                gl.glPushMatrix();
                gl.glTranslated(-2.75, 0, -0.4);
                gl.glScaled(2.1, 2.1, 3.4);
                // <editor-fold defaultstate="collapsed" desc="Texture Coordinates For the rear cube">
                mappingCoords = new QuadTexMappingCoordinates[6];
                //Right side:
                mappingCoords[0] = new QuadTexMappingCoordinates(1, 0.335975, 0.6640625, 0.335975, 0.6640625, 1, 1, 1);
                //Front side:
                mappingCoords[1] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Left side:
                mappingCoords[2] = new QuadTexMappingCoordinates(0.6640625, 0.335975, 1, 0.335975, 1, 1, 0.6640625, 1);
                //Back side:
                mappingCoords[3] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Bottom side:
                mappingCoords[4] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                //Top side:
                mappingCoords[5] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.335975, 0, 0.335975);
                // </editor-fold>   
                drawCube(torso, mappingCoords);
                gl.glPopMatrix();

                //Draw the neck
                gl.glPushMatrix();
                gl.glTranslated(3.8, 0, 0);
                gl.glRotated(90, 0, 1, 0);
                glut.glutSolidCylinder(0.95, 1.2, 10, 1);
                gl.glPopMatrix();

            }

            @Override
            public void drawChildLimbs() {
                //The torso has the head end four upper legs as child limbs they are drawn accrdingly.
                this.head.draw();
                this.foreLegLeft.draw();
                this.hindLegLeft.draw();
                this.foreLegRight.draw();
                this.hindLegRight.draw();
            }
        }

        /**
         * The properties of the robot's upper legs are defined in this class.
         * It has one child limb: LowerLeg
         */
        private class UpperLeg extends Limb {

            public LowerLeg lowerLeg;

            public UpperLeg(Vector localOrigin, Robot robot) {
                super(localOrigin, robot);
                this.lowerLeg = new LowerLeg(new Vector(0, 0, -6.6), robot);
            }

            @Override
            public void drawStickFigure() {
                //Draw the line representing the upperleg
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(0f, 0f, -6.6f);
                gl.glEnd();
                gl.glFlush();

                //Draw the joint sphere (shoulder or hip)
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
            }

            @Override
            public void drawSolid() {
                gl.glPushMatrix();
                gl.glTranslated(0, 0, -3.3);
                gl.glScaled(1, 0.5, 6.6);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
            }

            @Override
            public void drawChildLimbs() {
                this.lowerLeg.draw();
            }

        }

        /**
         * The properties of the robot's lower legs are defined in this class.
         * It has one child limb: Foot
         */
        public class LowerLeg extends Limb {

            public Foot foot;

            public LowerLeg(Vector localOrigin, Robot robot) {
                super(localOrigin, robot);
                this.foot = new Foot(new Vector(0, 0, -3.4f), robot);
            }

            @Override
            public void drawStickFigure() {
                //Draw the line representing the lowerleg.
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(0f, 0f, -3.4f);
                gl.glEnd();
                gl.glFlush();

                //Draw the oint sphere (knee)
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
            }

            @Override
            public void drawSolid() {
                //Draw the knee joint
                gl.glPushMatrix();
                gl.glTranslated(0, 0.3, 0);
                gl.glRotated(90, 1, 0, 0);
                glut.glutSolidCylinder(0.6, 0.6, 10, 1);
                gl.glPopMatrix();

                //Draw the lower leg
                gl.glPushMatrix();
                gl.glTranslated(0, 0, -1.7);
                gl.glScaled(1, 0.5, 3.4);
                glut.glutSolidCube(1);
                gl.glPopMatrix();
            }

            @Override
            public void drawChildLimbs() {
                this.foot.draw();
            }

        }

        /**
         * The properties of the robot's feet are defined in this class. It has
         * no child limbs
         */
        public class Foot extends Limb {

            public Foot(Vector localOrigin, Robot robot) {
                super(localOrigin, robot);
            }

            @Override
            public void drawStickFigure() {
                //Draw the joint sphere (ankle)
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);

                //Draw the line representing the foot
                gl.glColor3f(0, 0, 0);
                gl.glPushMatrix();
                gl.glTranslatef(-0.5f, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(1f, 0f, 0f);
                gl.glEnd();
                gl.glFlush();
                gl.glPopMatrix();
            }

            @Override
            public void drawSolid() {
                //Draw the foot, consisting of a large cylinder at the base and a smaller cylinder on top of that.
                gl.glPushMatrix();
                gl.glTranslated(0, 0, 0.6);
                glut.glutSolidCylinder(0.7, -0.8, 15, 1);
                gl.glTranslated(0, 0, -0.8);
                glut.glutSolidCylinder(1, -0.8, 15, 1);
                gl.glPopMatrix();
            }

            @Override
            public void drawChildLimbs() {
                //No child limbs
            }

        }

        /**
         * The robot's head. It has no child limbs.
         */
        public class Head extends Limb {

            public Head(Vector localOrigin, Robot robot) {
                super(localOrigin, robot);
            }

            @Override
            public void drawStickFigure() {
                //Draw the neck joint
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);

                //Draw the line representing the head
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(3.4f, 0f, 0f);
                gl.glEnd();
                gl.glFlush();
            }

            @Override
            public void drawSolid() {
                //Draw the head
                gl.glPushMatrix();
                gl.glTranslated(1.7, 0, 0);
                gl.glScaled(3.4, 2.1, 2.2);
                // <editor-fold defaultstate="collapsed" desc="Texture Coordinates For the middle cube">
                QuadTexMappingCoordinates[] mappingCoords = new QuadTexMappingCoordinates[6];
                //Right side:
                mappingCoords[0] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.421875, 0, 0.421875);
                //Front side:
                mappingCoords[1] = new QuadTexMappingCoordinates(0, 0.421875, 1, 0.421875, 1, 1, 0, 1);
                //Left side:
                mappingCoords[2] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.421875, 0, 0.421875);
                //Back side:
                mappingCoords[3] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.421875, 0, 0.421875);
                //Bottom side:
                mappingCoords[4] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.421875, 0, 0.421875);
                //Top side:
                mappingCoords[5] = new QuadTexMappingCoordinates(0, 0, 1, 0, 1, 0.421875, 0, 0.421875);
                // </editor-fold>
                drawCube(head, mappingCoords);
                gl.glPopMatrix();

                //Draw the guns
                gl.glPushMatrix();
                gl.glTranslated(0, 0.7, -1.2);
                gl.glRotated(90, 0, 1, 0);
                glut.glutSolidCylinder(0.15, 4.3, 6, 1);
                gl.glPopMatrix();

                gl.glPushMatrix();
                gl.glTranslated(0, -0.7, -1.2);
                gl.glRotated(90, 0, 1, 0);
                glut.glutSolidCylinder(0.15, 4.3, 6, 1);
                gl.glPopMatrix();
            }

            @Override
            public void drawChildLimbs() {
                //No child limbs
            }

        }
        // </editor-fold>

        /**
         * The root limb of the robot. this is the limb that is the root in the
         * limb hierarchy
         */
        public final Limb rootLimb;

        /**
         * Position where the robot is initially drawn.
         */
        protected Vector startPosition;
        protected Vector position;
        protected double rotate;
        public double distanceTraversed;
        public int trackpartcount = 0;

        /**
         * The material from which this robot is built.
         */
        private final Material material;

        /**
         * If true, draws the robot as a stick figure
         */
        public boolean drawStickFigure = true;

        public double speed;

        private final Random rand;

        /**
         * Constructs the robot with initial parameters.
         */
        public Robot(Material material, Vector startPosition, Random rand) {
            this.material = material;
            this.startPosition = startPosition;
            this.position = startPosition;
            this.distanceTraversed = 0;
            this.trackpartcount = 0;

            //Use a torso as the root limb
            rootLimb = new Torso(this);

            this.rand = rand;

            //The speed of the robot is gradually changed using a random number 
            this.speed = 20 + rand.nextDouble() * 2;
        }

        public void move(Vector offset) {
            this.position = startPosition.add(offset);
            this.speed = Math.abs(speed + (rand.nextDouble() - 0.5) / 200);
        }

        /**
         * Positions all of the robot's legs according to a certain time
         * @param t 
         */
        public void walkAnim(double t) {
            t *= speed / 8;

            //The animation cycle or each leg has a startingpoint that is
            //0.4 t higher than the previous leg        
            animateLeg(t, ((Torso) rootLimb).foreLegLeft);
            animateLeg(t + 0.4, ((Torso) rootLimb).hindLegRight);
            animateLeg(t + 0.8, ((Torso) rootLimb).foreLegRight);
            animateLeg(t + 1.2, ((Torso) rootLimb).hindLegLeft);
        }

        /**
         * Positions the given robotleg (by rotating around its local origin)
         * @param t Value of which the t%1 determines the position of the leg.
         * @param leg The leg to animate
         */
        public void animateLeg(double t, UpperLeg leg) {
            //The angle the legs can make (relative to the vector (0,0-1) from the local origin)
            double maxAngle = 35;
            //Multiplier for the time it takes to complete one animation cycle
            double period = 5;
            
            //The leg's animation is done by using a sine wave to manipulate the anlge at which a leg is located
            double sine = Math.sin((1 / period) * 2 * Math.PI * t);

            double newAngle = maxAngle * sine;
            
            //Rotate the upperleg
            leg.rotationXYZ = new double[]{
                0, newAngle, 0
            };

            //Rotate the lowerleg accordingly
            if (newAngle < 0) {
                leg.lowerLeg.rotationXYZ = new double[]{
                    0, -newAngle, 0
                };
            } else {
                leg.lowerLeg.rotationXYZ = new double[]{
                    0, 0.5 * newAngle, 0
                };
            }

        }

        public void rotate(double[] rotationXYZ) {
            this.rootLimb.rotationXYZ[0] = Math.toDegrees(rotationXYZ[0]);
            this.rootLimb.rotationXYZ[1] = Math.toDegrees(rotationXYZ[1]);
            this.rootLimb.rotationXYZ[2] = Math.toDegrees(rotationXYZ[2]);
        }

        /**
         * Draws this robot.
         */
        public void draw() {
            gl.glPushMatrix();
            gl.glMaterialfv(GL_FRONT, GL_SPECULAR, material.specular, 0);
            gl.glMaterialfv(GL_FRONT, GL_AMBIENT, material.ambient, 0);
            gl.glMaterialf(GL_FRONT, GL_SHININESS, material.shininess * 128);
            gl.glRotated(rotate, 0, 0, 1);
            //                                                                  gl.glRotated(rotate(raceTrack.getTangent(gs.tAnim/roundtime), raceTrack.getTangent((gs.tAnim-0.05)/roundtime)), 0, 0, 1);
            gl.glTranslated(position.x(), position.y(), position.z());
            rootLimb.draw();
            gl.glPopMatrix();

        }
    }

    /**
     * Implementation of a camera with a position and orientation.
     */
    private class Camera {

        int autoMode = 1;
        int oldTime = 0;

        /**
         * The position of the camera.
         */
        public Vector eye = new Vector(3f, 6f, 5f);

        /**
         * The point to which the camera is looking.
         */
        public Vector center = Vector.O;

        /**
         * The up vector.
         */
        public Vector up = Vector.Z;

        /**
         * Updates the camera viewpoint and direction based on the selected
         * camera mode.
         */
        public void update(int mode) {
            robots[0].toString();
            if (1 == mode) // Helicopter mode
            {
                setHelicopterMode();

                // Motor cycle mode
            } else if (2 == mode) {
                setMotorCycleMode();

                // First person mode
            } else if (3 == mode) {
                setFirstPersonMode();

                // Auto mode
            } else if (4 == mode) {
                if (autoMode % 4 == 0) {
                    autoMode = 1;
                }

                int time = (int) gs.tAnim;

                switch (autoMode) {
                    case 1:
                        setHelicopterMode();
                        break;
                    case 2:
                        setMotorCycleMode();
                        break;
                    case 3:
                        setFirstPersonMode();
                        break;
                }

                if (time != oldTime && time % 4 == 0) {
                    autoMode++;
                }

                oldTime = time;

            } else {
                setDefaultMode();
            }

            glu.gluLookAt(camera.eye.x(), camera.eye.y(), camera.eye.z(),
                    camera.center.x(), camera.center.y(), camera.center.z(),
                    camera.up.x(), camera.up.y(), camera.up.z());
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * camera's default mode.
         */
        private void setDefaultMode() {
            Vector centerPoint = new Vector(0, 0, 0);

            //Get coordinate values for camera
            float phi = gs.phi;
            double cosPhiForUp = Math.cos(phi + (0.5 * Math.PI));

            //To make sure that the image does not disappear when the camera is directly above the scene, 
            //we increase the value for phi when it is near 0.
            if (Math.abs(cosPhiForUp) <= 0.02) {
                phi += 0.025;
            }

            //Get the x, y and z values for the camera.
            double eyeX = gs.vDist * Math.cos(gs.theta) * Math.sin(-phi); //r*cos(theta)*sin(phi)
            double eyeY = gs.vDist * Math.sin(gs.theta) * Math.sin(phi);
            double eyeZ = gs.vDist * Math.cos(phi);

            up = new Vector(0, 0, Math.cos(phi + (0.5 * Math.PI)));

            //Move the camera to the new coordinates
            camera.eye = new Vector(eyeX, eyeY, eyeZ);
            camera.up = up;
            camera.center = centerPoint;
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * helicopter mode.
         */
        private void setHelicopterMode() {
            Vector pos0 = robots[0].position;
            Vector pos1 = robots[1].position;
            Vector pos2 = robots[2].position;
            Vector pos3 = robots[3].position;

            Vector total = pos0.add(pos1).add(pos2).add(pos3);
            double centerX = total.x() / 4;
            double centerY = total.y() / 4;
            double centerZ = total.z() / 4;

            Vector centerPoint = new Vector(centerX, centerY, centerZ);
            Vector eyePoint = centerPoint.add(new Vector(5, 5, 300));
            double eyeX = eyePoint.x();
            double eyeY = eyePoint.y();
            double eyeZ = eyePoint.z();
            up = new Vector(0, -1, 0);

            //Move the camera to the new coordinates
            camera.eye = new Vector(eyeX, eyeY, eyeZ);
            camera.up = up;
            camera.center = centerPoint;
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * motorcycle mode.
         */
        private void setMotorCycleMode() {
            //We use robots[0] for now
            int laneNr = 0;
            Robot robot = robots[laneNr];

            Vector alongTrack = raceTrack.getTangent(gs.trackNr, gs.tAnim / robot.speed % 1, laneNr);

            double normalX = -(alongTrack.y());
            double normalY = alongTrack.x();
            double normalZ = alongTrack.z();

            Vector normalUnit = new Vector(normalX, normalY, normalZ).normalized();
            Vector normal = robot.position.add(new Vector(100 * normalUnit.x(), 100 * normalUnit.y(), normalUnit.z()));

            up = normal.cross(alongTrack);
            up = new Vector(up.x(), up.y(), -up.z());

            //Move the camera to the new coordinates
            camera.eye = normal;
            camera.up = up;
            camera.center = robot.position;
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * first person mode.
         */
        private void setFirstPersonMode() {
            //We use robots[0] for now
            int laneNr = 0;
            Robot robot = robots[laneNr];

            Vector alongTrack = raceTrack.getTangent(gs.trackNr, gs.tAnim / robot.speed % 1, laneNr);

            double normalX = -(alongTrack.y());
            double normalY = alongTrack.x();
            double normalZ = alongTrack.z();

            Vector normalUnit = new Vector(normalX, normalY, normalZ).normalized();
            Vector normal = robot.position.add(new Vector(100 * normalUnit.x(), 100 * normalUnit.y(), normalUnit.z()));

            up = normal.cross(alongTrack).normalized();
            up = new Vector(up.x(), up.y(), -up.z());

            camera.center = up.scale(6).add(robot.position);
            Vector parallel = camera.center.subtract(alongTrack.normalized().scale(15));

            camera.eye = up.scale(2).add(parallel);
        }

    }

    /**
     * Implementation of a race track that is made from Bezier segments.
     */
    private class RaceTrack {

        /**
         * Array with control points for the O-track.
         */
        private double tracktime = 0; 
        private boolean calctrack = false; //this variable was used to make sure the addition within the trackpart calculation was only done ones
        private double[] trackpartdistanceOC = new double[2]; //originally created to make the tracklength calculation. Not deleted incase of a new need for trackcalculation
        private double[] trackpartdistanceO = {111.5677, 111.5677}; // calculated tracklength which is now inserted as a predifined vector. why make the program calculate it every time? 
        private Vector[][] controlPointsOTrack// every row is a new trackpart in which: startpoint, endpoint, 1st-controlpoint and 2nd controlpoint are given respectively
                = {
                    {
                        new Vector(-40, 0, 0), new Vector(40, 0, 0), new Vector(-40, 40, 0), new Vector(40, 40, 0)
                    },
                    {
                        new Vector(40, 0, 0), new Vector(-40, 0, 0), new Vector(40, -40, 0), new Vector(-40, -40, 0)
                    },
                    {
                        new Vector(-40, -3, 0), new Vector(-40, 3, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    }
                };
        private int[] BuildOTrack
                = {
                    2, 2, 0// are the commands used by findpoint. 0 is a line, 1 is a kwadratic Bézier curve and 2 is a cubic Bézier curve
                };
        /**
         * Array with control points for the L-track.
         */
        private double[] trackpartdistanceLC = new double[8];//originally created to make the tracklength calculation. Not deleted incase of a new need for trackcalculation
        private double[] trackpartdistanceL = {90, 83.6758, 15, 63.46, 70, 83.6758, 105, 74.6589};// calculated tracklength which is now inserted as a predifined vector. why make the program calculate it every time? 
        private Vector[][] controlPointsLTrack// every row is a new trackpart in which: startpoint, endpoint, 1st-controlpoint and 2nd controlpoint are given respectively
                = {
                    {
                        new Vector(0, -75, 0), new Vector(90, -75, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(90, -75, 0), new Vector(90, -15, 0), new Vector(120, -75, 0), new Vector(120, -15, 0)
                    },
                    {
                        new Vector(90, -15, 0), new Vector(75, -15, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(75, -15, 0), new Vector(30, 20, 0), new Vector(35, -15, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(30, 20, 0), new Vector(30, 90, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(30, 90, 0), new Vector(-30, 90, 0), new Vector(30, 120, 0), new Vector(-30, 120, 0)
                    },
                    {
                        new Vector(-30, 90, 0), new Vector(-30, -15, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(-30, -15, 0), new Vector(0, -75, 0), new Vector(-30, -75, 0), new Vector(0, 0, 0)
                    },
                    {
                        new Vector(-2, -75, 0), new Vector(2, -75, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    },};
        private int[] BuildLTrack
                = {
                    0, 2, 0, 1, 0, 2, 0, 1, 0// are the commands used by findpoint. 0 is a line, 1 is a kwadratic Bézier curve and 2 is a cubic Bézier curve
                };
        /**
         * Array with control points for the C-track.
         */
        private double[] trackpartdistanceCC = new double[4];//originally created to make the tracklength calculation. Not deleted incase of a new need for trackcalculation
        private double[] trackpartdistanceC = {41.838, 226.14, 41.838, 125.51};// calculated tracklength which is now inserted as a predifined vector. why make the program calculate it every time? 
        private Vector[][] controlPointsCTrack // every row is a new trackpart in which: startpoint, endpoint, 1st-controlpoint and 2nd controlpoint are given respectively
                = {
                    {
                        new Vector(30, 45, 0), new Vector(30, 75, 0), new Vector(45, 45, 0), new Vector(45, 75, 0)
                    },
                    {
                        new Vector(30, 75, 0), new Vector(30, -75, 0), new Vector(-60, 75, 0), new Vector(-60, -75, 0)
                    },
                    {
                        new Vector(30, -75, 0), new Vector(30, -45, 0), new Vector(45, -75, 0), new Vector(45, -45, 0)
                    },
                    {
                        new Vector(30, -45, 0), new Vector(30, 45, 0), new Vector(-15, -45, 0), new Vector(-15, 45, 0)
                    },
                    {
                        new Vector(28, 45, 0), new Vector(32, 45, 0), new Vector(0, 0, 0), new Vector(0, 0, 0)
                    }
                };
        private int[] BuildCTrack
                = {
                    2, 2, 2, 2, 0// are the commands used by findpoint. 0 is a line, 1 is a kwadratic Bézier curve and 2 is a cubic Bézier curve
                };
        /**
         * Array with control points for the custom track.
         */
        private double[] trackpartdistanceCustomC = new double[3];//originally created to make the tracklength calculation. Not deleted incase of a new need for trackcalculation
        private double[] trackpartdistanceCustom = {132.37,132.37,132.37,132.37};// calculated tracklength which is now inserted as a predifined vector. why make the program calculate it every time? 
        private Vector[][] controlPointsCustomTrack// every row is a new trackpart in which: startpoint, endpoint, 1st-controlpoint and 2nd controlpoint are given respectively
                = {
                    {
                        new Vector(0, 0, 0), new Vector(-90, 0, 30), new Vector(0, -47.5, 0), new Vector(-90, -47.5, 30)
                    },
                    {
                        new Vector(-90, 0, 30), new Vector(0, 0, 60), new Vector(-90, 47.5, 30), new Vector(0, 47.5, 60)
                    },
                    {
                        new Vector(0, 0, 60), new Vector(90, 0, 30), new Vector(0, -47.5, 60), new Vector(90, -47.5, 30)
                    },
                    {
                        new Vector(90,0 ,30 ),new Vector(0,0,0),new Vector(90,47.5,30),new Vector(0,47.5,0)
                    },
                    {
                        new Vector(0,2 ,0 ),new Vector(0,-2,0),new Vector(0,0,0),new Vector(0,0,0)
                    }
                        
                };
        private int[] BuildCustomTrack
                = {
                    2, 2, 2,2,0 // are the commands used by findpoint. 0 is a line, 1 is a kwadratic Bézier curve and 2 is a cubic Bézier curve
                };

        /**
         * Constructs the race track, sets up display lists.
         */
        public Vector[] findpoint(int Buildtype[], Vector Points[][], int j, double step, double segment) { // this function is used to get the right trackpart point
            Vector l1, l2;
            if (Buildtype[j] == 0) {
                l1 = straight(Points[j][0], Points[j][1], step);
                l2 = straight(Points[j][0], Points[j][1], step + segment);

            } else if (Buildtype[j] == 1) {
                l1 = bent_90(Points[j][0], Points[j][1], Points[j][2], step);
                l2 = bent_90(Points[j][0], Points[j][1], Points[j][2], step + segment);
            } else if (Buildtype[j] == 2) {
                l1 = bent_180(Points[j][0], Points[j][1], Points[j][2], Points[j][3], step);
                l2 = bent_180(Points[j][0], Points[j][1], Points[j][2], Points[j][3], step + segment);
            } else {
                l1 = Vector.O;
                l2 = Vector.O;
                System.out.println("Invallid Buildtype! made l1 == l2 == {0,0,0}" + " " + Buildtype[j]);

            }
            Vector[] back
                    = {
                        l1, l2
                    };
            return back;
        }

        public void trackprint(int i, Vector p, Vector q) {
            int textureX = 0;
            int textureY = 0;
            if (i % 2 != 0) {
                textureX = 1;
                textureY = 0;
            }
            gl.glTexCoord2d(textureX, textureY);
            gl.glVertex3d(p.x(), p.y(), p.z());
            if (i % 2 == 0) {
                textureX = 0;
                textureY = 1;
            } else {
                textureX = 1;
                textureY = 1;
            }
            gl.glTexCoord2d(textureX, textureY);
            gl.glVertex3d(q.x(), q.y(), q.z());
        }
// the following functions are used to create trackparts there are 3 different kinds:
        public Vector straight(Vector A, Vector B, Double t) { // actually a simple Bézier line, nothing special
            Vector point = A.scale(1 - t).add(B.scale(t));
            return point;
        }

        public Vector bent_90(Vector A, Vector B, Vector C, double t) { // Actually a kwadratic Bézier curve which will mostly used to make 90 degree turns
            Vector point = A.scale(Math.pow((1 - t), 2)).add(C.scale(2 * (1 - t) * t)).add(B.scale(Math.pow(t, 2)));
            return point;
        }

        public Vector bent_180(Vector A, Vector B, Vector C, Vector D, double t) { // Actually a cubic Bézier curve which will mostly be used to make 180 degree turns

            Vector point = A.scale(Math.pow(1 - t, 3)).add(C.scale(3 * Math.pow((1 - t), 2) * t)).add(D.scale(3 * (1 - t) * Math.pow(t, 2))).add(B.scale(Math.pow(t, 3)));
            return point;
        }

        public void tracklengthcalculator(int tracknr, double ll, int j) { // this calculator is used to find the lengths of individual trackparts which where filled in later by hand as final vectors to prevent a timing error in which the track calculation is not yet done before the track is used
            switch (tracknr) { 
                case 1: {
                    if (j < trackpartdistanceOC.length) {
                        if (j < trackpartdistanceOC.length) {
                            trackpartdistanceOC[j] += ll;
                            break;
                        }
                    }
                }
                case 2: {
                    if (j < trackpartdistanceLC.length) {
                        if (j < trackpartdistanceLC.length) {
                            trackpartdistanceLC[j] += ll;
                            break;
                        }
                    }
                }
                case 3: {
                    if (j < trackpartdistanceCC.length) {
                        if (j < trackpartdistanceCC.length) {
                            trackpartdistanceCC[j] += ll;
                            break;
                        }
                    }
                }
                case 4: {
                    if (j < trackpartdistanceCustomC.length) {
                        if (j < trackpartdistanceCustomC.length) {
                            trackpartdistanceCustomC[j] += ll;
                            break;
                        }
                    }
                }
            }
        }

        public void TrackConstructor(Vector[][] Points, int Buildtype[], Double trackwidth, int pos, int tracknr, boolean calc) {
            
// draw part of top side of the track
            track.bind(gl);
            gl.glBegin(GL_QUAD_STRIP);
            int times = Buildtype.length;
            for (int j = 0; j < times; j++) {//work through the trackparts individually at a time
                double steps = 100;
                double segment = 1 / steps;
                for (int i = 0; i < steps; i++) { // create the trackpart with 100 segments
                    double step = segment * i;
                    Vector[] beginandend = findpoint(Buildtype, Points, j, step, segment); // Findpoint returns 2 vectors. of which one is a bit further ahead
                    Vector l1 = beginandend[0];
                    Vector l2 = beginandend[1];

                    Vector ll = l2.subtract(l1);
                    if (calc == false) {
                        tracklengthcalculator(tracknr, ll.length(), j);
                    }
                    double s = Math.sqrt(Math.pow(ll.x(),2)+Math.pow(ll.y(), 2));
                    Vector p = (new Vector(ll.y() / s * trackwidth * (4 - pos), -ll.x() / s * trackwidth * (4 - pos), -ll.z())).add(l1);
                    Vector q = (new Vector(ll.y() / s * trackwidth * (3 - pos), -ll.x() / s * trackwidth * (3 - pos), -ll.z())).add(l2);

                    trackprint(i, p, q);
                }
            }
            gl.glEnd();
            
// Draw the inside of the track
            brick.bind(gl);
            gl.glBegin(GL_QUAD_STRIP);
            for (int j = 0; j < times; j++) {
                double stappen = 100;
                double segment = 1 / stappen;
                for (int i = 0; i < stappen; i++) {
                    double step = segment * i;
                    Vector[] beginandend = findpoint(Buildtype, Points, j, step, segment); // returns the next point on the curve and the point after that in a vector array
                    Vector l1 = beginandend[0];
                    Vector l2 = beginandend[1]; 
                    Vector ll = l2.subtract(l1); //B-A is the vector from A to B which will be used in calculating the tangent component
                    double s = Math.sqrt(Math.pow(ll.x(),2)+Math.pow(ll.y(), 2));
                    Vector p = (new Vector(-ll.y() / s * trackwidth, ll.x() / s * trackwidth, -ll.z())).add(l1);
                    Vector q = p.add(new Vector(0, 0, -3));
                    trackprint(i, p, q);
                }
            }
            gl.glEnd();
            
// Draw the uitside of the track
            brick.bind(gl);
            gl.glBegin(GL_QUAD_STRIP);
            for (int j = 0; j < times; j++) {
                double stappen = 100;
                double segment = 1 / stappen;
                for (int i = 0; i < stappen; i++) {
                    double step = segment * i;
                    Vector[] beginandend = findpoint(Buildtype, Points, j, step, segment);
                    Vector l1 = beginandend[0];
                    Vector l2 = beginandend[1];
                    Vector ll = l2.subtract(l1); //B-A is the vector from A to B which will be used in calculating the tangent component

                    double s = Math.sqrt(Math.pow(ll.x(),2)+Math.pow(ll.y(), 2));
                    Vector p = (new Vector(ll.y() / s * trackwidth * 3, -ll.x() / s * trackwidth * 3, -ll.z())).add(l1); // point 1 is calculated by adding the point l1 with a tangent component of with 3times the tracklength
                    Vector q = p.add(new Vector(0, 0, -3)); //point 2 is just 3 meters below the surface
                    trackprint(i, p, q);
                }
            }
            gl.glEnd();
        }

        public Vector robotpos(Vector[][] Points, int Buildtype[], int j, double trackwidth, int pos, double time) {

            Vector[] beginandend = findpoint(Buildtype, Points, j, time, 0.01); // Finds 2 positions 1st is the current position on the line and the second a position in a short future
            Vector L1 = beginandend[0];
            Vector L2 = beginandend[1];
            Vector LL = L2.subtract(L1); // L2-L1 gives the normal in that position

            double s = Math.sqrt(Math.pow(LL.x(),2)+Math.pow(LL.y(), 2)); // the track is horizontal from each bezierpoint so the z component is not used
            Vector p = (new Vector(LL.y() / s * trackwidth * (2.5 - pos), -LL.x() / s * trackwidth * (2.5 - pos), 0)).add(L1); // add the origional position with the appropriate width (in the tangent direction) to place the robot on track 
            return p;
        }

        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr) {            
// The test track is selected
            if (0 == trackNr) {
                //Set color
                double trackwidth = 6;

                float[][] colors
                        = {
                            Material.ORANGE.diffuse, Material.WOOD.diffuse, Material.SILVER.diffuse, Material.GOLD.diffuse
                        };

                for (int i = 0; i < 4; i++) {
                    gl.glColor3f(colors[3 - i][0], colors[3 - i][1], colors[3 - i][2]);
                    drawTestTrack(45 + (i * trackwidth), 70 + (i * trackwidth), trackwidth, 50);
                }
                
// The O-track is selected
            } else if (1 == trackNr) {
                double trackwidth = 6;
                float[][] colors
                        = {
                            Material.ORANGE.diffuse, Material.WOOD.diffuse, Material.SILVER.diffuse, Material.GOLD.diffuse
                        };
                for (int i = 0; i < 4; i++) {
                    gl.glColor3f(colors[3 - i][0], colors[3 - i][1], colors[3 - i][2]);// get the right collors with the right robottrack
                    TrackConstructor(controlPointsOTrack, BuildOTrack, trackwidth, i + 1, 1, calctrack); // Builds every individual lane with its own color
                    calctrack = true;
                }
                
// The L-track is selected
            } else if (2 == trackNr) {
                double trackwidth = 6;
                float[][] colors
                        = {
                            Material.ORANGE.diffuse, Material.WOOD.diffuse, Material.SILVER.diffuse, Material.GOLD.diffuse
                        };
                for (int i = 0; i < 4; i++) {
                    gl.glColor3f(colors[3 - i][0], colors[3 - i][1], colors[3 - i][2]);// get the right collors with the right robottrack
                    TrackConstructor(controlPointsLTrack, BuildLTrack, trackwidth, i + 1, 2, calctrack); // Builds every individual lane with its own color
                    calctrack = true;
                }               
// C track is selected
            } else if (3 == trackNr) {
                double trackwidth = 6;
                float[][] colors 
                        = {
                            Material.ORANGE.diffuse, Material.WOOD.diffuse, Material.SILVER.diffuse, Material.GOLD.diffuse
                        };
                for (int i = 0; i < 4; i++) {
                    gl.glColor3f(colors[3 - i][0], colors[3 - i][1], colors[3 - i][2]); // get the right collors with the right robottrack
                    TrackConstructor(controlPointsCTrack, BuildCTrack, trackwidth, i + 1, 3, calctrack); // Builds every individual lane with its own color
                    calctrack = true;
                }
// The custom track is selected
            } else if (4 == trackNr) {
                double trackwidth = 6;
                float[][] colors
                        = {
                            Material.ORANGE.diffuse, Material.WOOD.diffuse, Material.SILVER.diffuse, Material.GOLD.diffuse
                        };
                for (int i = 0; i < 4; i++) {
                    gl.glColor3f(colors[3 - i][0], colors[3 - i][1], colors[3 - i][2]);// get the right collors with the right robottrack
                    TrackConstructor(controlPointsCustomTrack, BuildCustomTrack, trackwidth, i + 1, 4, calctrack); // Builds every individual lane with its own color
                    calctrack = true;
                }
            }
        }

        private void drawTestTrack(double widthX, double widthY, double trackWidth, int segments) {
            track.bind(gl);
            gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            gl.glBegin(GL_QUADS);
            double segmentLength = (2 * Math.PI) / segments;
            double nextStartingX = 0;

            //Draw the top of the track
            for (int i = 0; i < segments + 1; i++) {
                double alpha = segmentLength * i;
                double nextAlpha = segmentLength * (i + 1);

                Vector p = new Vector((widthX - 3) * Math.cos(alpha), (widthY - 3) * Math.sin(alpha), 0);
                Vector q = p.add(new Vector(trackWidth * Math.cos(alpha), trackWidth * Math.sin(alpha), 0));

                double textureX = nextStartingX;
                double textureY = 0;

                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(p.x(), p.y(), p.z());

                textureY = 1;
                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(q.x(), q.y(), q.z());

                Vector p2 = new Vector((widthX - 3) * Math.cos(nextAlpha), (widthY - 3) * Math.sin(nextAlpha), 0);
                Vector q2 = p2.add(new Vector(trackWidth * Math.cos(nextAlpha), trackWidth * Math.sin(nextAlpha), 0));

                Vector segmentVector = q2.subtract(q);
                double xLength = segmentVector.length() / trackWidth;

                textureX = xLength;
                textureY = 1;
                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(q2.x(), q2.y(), q2.z());

                textureY = 0;
                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(p2.x(), p2.y(), p2.z());

                nextStartingX = xLength % 1;
            }
            gl.glEnd();

            //Draw the side of the track (inside)
            brick.bind(gl);
            gl.glBegin(GL_QUAD_STRIP);
            for (int i = 0; i < segments + 1; i++) {
                double alpha = segmentLength * i;
                Vector p = new Vector((widthX - 3) * Math.cos(alpha), (widthY - 3) * Math.sin(alpha), 0);
                Vector q = p.add(new Vector(0, 0, -20));

                int textureX = 0;
                int textureY = 0;

                if (i % 2 != 0) {
                    textureX = 1;
                    textureY = 0;
                }

                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(p.x(), p.y(), p.z());

                if (i % 2 == 0) {
                    textureX = 0;
                    textureY = 1;
                } else {
                    textureX = 1;
                    textureY = 1;
                }

                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(q.x(), q.y(), q.z());
            }
            gl.glEnd();

            //Draw the side of the track (outside)
            gl.glBegin(GL_QUAD_STRIP);
            for (int i = 0; i < segments + 1; i++) {
                double alpha = segmentLength * i;
                Vector p = new Vector((widthX - 3) * Math.cos(alpha), (widthY - 3) * Math.sin(alpha), 0);
                p = p.add(new Vector(trackWidth * Math.cos(alpha), trackWidth * Math.sin(alpha), 0));
                Vector q = p.add(new Vector(0, 0, -3));

                int textureX = 0;
                int textureY = 0;

                if (i % 2 != 0) {
                    textureX = 1;
                    textureY = 0;
                }

                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(p.x(), p.y(), p.z());

                if (i % 2 == 0) {
                    textureX = 0;
                    textureY = 1;
                } else {
                    textureX = 1;
                    textureY = 1;
                }

                gl.glTexCoord2d(textureX, textureY);
                gl.glVertex3d(q.x(), q.y(), q.z());
            }
            gl.glEnd();
        }

        /**
         * Returns the position of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getPoint(int trackNr, double t, int pos, double trackwidth) // t is time/speed
        {
            double shift = (int) pos * 6;
            Robot r = robots[pos];
            Vector position;
            switch (trackNr) { //sort by track
                case 0:
                    position = new Vector((45 + shift) * Math.cos(2 * Math.PI * t / r.speed), (shift + 70) * Math.sin(2 * Math.PI * t / r.speed), 1d);
                    return position; // <- code goes here
                case 1: // O track
                    if (r.trackpartcount > 1) // Prevents errors if switched from a larger track to a smaller one
                    {
                        r.trackpartcount = 0;
                    }
                    tracktime = (t * r.speed - r.distanceTraversed) / trackpartdistanceO[r.trackpartcount]; // tracktime goes from 0 to 1 on every track in every lap thanks to this formula
                    
                    if (tracktime > 1) { //reset tracktime when it goes out of its domain
                        tracktime = 0;
                        r.distanceTraversed += trackpartdistanceO[r.trackpartcount]; //distance traveled is updated
                        r.trackpartcount += 1; // next trackpart is choosen
                        if (r.trackpartcount > 1) { // if the next trackpart is not surposed to be used (all tracks have a small connecting piece of road that is not surposed to be used )
                            r.trackpartcount = 0; // the counter is reset
                        }

                    }

                    position = robotpos(controlPointsOTrack, BuildOTrack, r.trackpartcount, trackwidth, pos, tracktime); // gives the position on the specific time,track, trackpart and robotposition
                    return position;
                case 2: // L track
                { // works pretty much the same as the O-track case just some names and numbers are changed
                    if (r.trackpartcount > 7) // Prevents errors if switched from a larger track to a smaller one
                    {
                        r.trackpartcount = 0;
                    }

                    tracktime = (t * r.speed - r.distanceTraversed) / trackpartdistanceL[r.trackpartcount];

                    if (tracktime > 1) {
                        tracktime = 0;
                        r.distanceTraversed += trackpartdistanceL[r.trackpartcount];
                        r.trackpartcount += 1;
                        if (r.trackpartcount > 7) {
                            r.trackpartcount = 0;
                        }

                    }

                    position = robotpos(controlPointsLTrack, BuildLTrack, r.trackpartcount, trackwidth, pos, tracktime);
                    return position;
                }
                case 3: // C track
                { // works pretty much the same as the O-track case just some names and numbers are changed
                    if (r.trackpartcount > 3) // Prevents errors if switched from a larger track to a smaller one
                    {
                        r.trackpartcount = 0;
                    }
                    tracktime = (t * r.speed - r.distanceTraversed) / trackpartdistanceC[r.trackpartcount];

                    if (tracktime > 1) {
                        tracktime = 0;
                        r.distanceTraversed += trackpartdistanceC[r.trackpartcount];
                        r.trackpartcount += 1;
                        if (r.trackpartcount > 3) {
                            r.trackpartcount = 0;
                        }

                    }

                    position = robotpos(controlPointsCTrack, BuildCTrack, r.trackpartcount, trackwidth, pos, tracktime);
                    return position;
                }
                case 4: // Custom track
                { // works pretty much the same as the O-track case just some names and numbers are changed
                    if (r.trackpartcount > 3) // Prevents errors if switched from a larger track to a smaller one
                    {
                        r.trackpartcount = 0;
                    }
                    tracktime = (t * r.speed - r.distanceTraversed) / trackpartdistanceCustom[r.trackpartcount];

                    if (tracktime > 1) {
                        tracktime = 0;
                        r.distanceTraversed += trackpartdistanceCustom[r.trackpartcount];
                        r.trackpartcount += 1;
                        if (r.trackpartcount > 3) {
                            r.trackpartcount = 0;
                        }

                    }

                    position = robotpos(controlPointsCustomTrack, BuildCustomTrack, r.trackpartcount, trackwidth, pos, tracktime);
                    return position;
            }

            
            } return Vector.O;
        }

        /**
         * Returns the tangent of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getTangent(int trackNr, double t, int laneNr) {
            Vector point1 = getPoint(trackNr, t, laneNr, 0d); // takes current position
            Vector point2 = getPoint(trackNr, t + 0.001, laneNr, 0d); // takes position in a very recent future
            Vector tangent = point2.subtract(point1); // derive the tangent by simple subtraction
            return tangent;

        }

    }

    /**
     * Implementation of the terrain.
     */
    private class Terrain {

        /**
         * Draws the terrain.
         */
        public void draw() {
            //The number of segments of the terrain
            int segmentsX = 40;
            int segmentsY = 40;

            //dimensions of the terrain
            float widthX = 400f;
            float widthY = 400f;

            //dimensions of a segment
            float dx = widthX / segmentsX;
            float dy = widthY / segmentsY;

            //Factor used to determine how much 'mountains' the terrain contains
            float periodX = widthX / 10;
            float periodY = widthY / 10;

            //Height multiplier of the terrain
            float amplitude = 20;

            //Distance the complete terrain (except the water) is shifted down
            float shift = 0; //Not used right now

            gl.glPushMatrix();

            //Make sure the track is a bit elevated above the terrain
            gl.glTranslated(-widthX / 2, -widthY / 2, -5);
        
            gl.glDisable(GL_TEXTURE_2D);
            gl.glEnable(GL_TEXTURE_1D);
            
            //Bind the terrain texture
            terrainTexture.bind(gl);
            gl.glBegin(GL_QUADS);
            
            //Draw the surface
            for (int i = 0; i < segmentsX - 1; i++) {
                float x = i * dx;
                for (int j = 0; j < segmentsY - 1; j++) {
                    float y = j * dy;
                    float height = heightAt(x / periodX, y / periodY);
                    gl.glTexCoord1d((height / 2) + 0.5);
                    gl.glVertex3d(x, y, height * amplitude - shift);

                    height = heightAt((x + dx) / periodX, y / periodY);
                    gl.glTexCoord1d((height / 2) + 0.5);
                    gl.glVertex3d(x + dx, y, height * amplitude - shift);

                    height = heightAt((x + dx) / periodX, (y + dx) / periodY);
                    gl.glTexCoord1d((height / 2) + 0.5);
                    gl.glVertex3d(x + dx, y + dx, height * amplitude - shift);

                    height = heightAt(x / periodX, (y + dx) / periodY);
                    gl.glTexCoord1d((height / 2) + 0.5);
                    gl.glVertex3d(x, y + dx, height * amplitude - shift);
                }
            }
            gl.glEnd();
            gl.glDisable(GL_TEXTURE_1D);
            gl.glEnable(GL_TEXTURE_2D);

            //Draw the 'water' surface
            gl.glBegin(GL_QUADS);
            gl.glColor4d(155, 155, 155, 0.2);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(widthX, 0, 0);
            gl.glVertex3d(widthX, widthY, 0);
            gl.glVertex3d(0, widthY, 0);
            gl.glEnd();

            gl.glPopMatrix();
        }

        /**
         * Computes the elevation of the terrain at ({@code x}, {@code y}).
         */
        public float heightAt(float x, float y) {
            //Terrain as a function of two cosine waves
            return (float) (0.6 * Math.cos(0.3 * x + 0.3 * y) + 0.4 * Math.cos(x - 0.5 * y));
        }

    }

    /**
     * Main program execution body, delegates to an instance of the RobotRace
     * implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }

}
