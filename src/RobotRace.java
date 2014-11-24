
import static javax.media.opengl.GL2.*;
import robotrace.Base;
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
public class RobotRace extends Base
{

    private float rotation;

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

    /**
     * Constructs this robot race by initializing robots, camera, track, and
     * terrain.
     */
    public RobotRace()
    {
        
        // Create a new array of four robots
        robots = new Robot[4];

        // Initialize robot 0
        robots[0] = new Robot(Material.GOLD, new Vector(0, -15, 0));

        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new Vector(0, -5, 0));

        // Initialize robot 2
        robots[2] = new Robot(Material.WOOD, new Vector(0, 5, 0));

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new Vector(0, 15, 0));

        // Initialize the camera
        camera = new Camera();

        // Initialize the race track
        raceTrack = new RaceTrack();

        // Initialize the terrain
        terrain = new Terrain();
    }

    /**
     * Called upon the start of the application. Primarily used to configure
     * OpenGL.
     */
    @Override
    public void initialize()
    {
        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Anti-aliasing can be enabled by uncommenting the following 4 lines.
        // This can however cause problems on some graphics cards.
        //gl.glEnable(GL_LINE_SMOOTH);
        //gl.glEnable(GL_POLYGON_SMOOTH);
        //gl.glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        //gl.glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);
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
    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView()
    {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        // Set the perspective.
        // Modify this to meet the requirements in the assignment.
        glu.gluPerspective(gs.vWidth, (float) gs.w / (float) gs.h, 0.01 * gs.vDist, 10 * gs.vDist);

        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode
        camera.update(gs.camMode);

        //Get coordinate values for camera
        double x = gs.vDist * Math.cos(gs.theta) * Math.sin(-gs.phi); //r*cos(theta)*sin(phi)
        double y = gs.vDist * Math.sin(gs.theta) * Math.sin(gs.phi);
        double z = gs.vDist * Math.cos(gs.phi);

        camera.eye = new Vector(x, y, z);
        camera.up = new Vector(0, 0, Math.cos(gs.phi + (0.5 * Math.PI)));

        glu.gluLookAt(camera.eye.x(), camera.eye.y(), camera.eye.z(),
                camera.center.x(), camera.center.y(), camera.center.z(),
                camera.up.x(), camera.up.y(), camera.up.z());
    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene()
    {
        double x = gs.cnt.x()/10;
        double y = gs.cnt.y()/10;
        double z = gs.cnt.z()/10;
        
        Vector newPosition = new Vector(-x, -y, z);
        robots[0].move(newPosition);
        
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
        if (gs.showAxes)
        {
            drawAxisFrame();
        }

        // Draw the robots
        for (Robot r : robots)
        {
            r.draw(true);
        }

        // Draw race track
        raceTrack.draw(gs.trackNr);

        // Draw terrain
        terrain.draw();

        gl.glColor3f(0f, 0f, 0f);

        // Unit box around origin.
        glut.glutWireCube(1f);

        // Move in x-direction.
        gl.glTranslatef(2f, 0f, 0f);

        // Rotate 30 degrees, around z-axis.
        gl.glRotatef(rotation, 0f, 0f, 1f);

        // Scale in z-direction.
        gl.glScalef(1f, 1f, 2f);

        // Translated, rotated, scaled box.
        glut.glutWireCube(1f);
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame()
    {
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
     * Materials that can be used for the robots.
     */
    public enum Material
    {

        /**
         * Gold material properties. Modify the default values to make it look
         * like gold.
         */
        GOLD(
                new float[]
                {
                    0.8f, 0.8f, 0.8f, 1.0f
                },
                new float[]
                {
                    0.0f, 0.0f, 0.0f, 1.0f
                }),
        /**
         * Silver material properties. Modify the default values to make it look
         * like silver.
         */
        SILVER(
                new float[]
                {
                    0.8f, 0.8f, 0.8f, 1.0f
                },
                new float[]
                {
                    0.0f, 0.0f, 0.0f, 1.0f
                }),
        /**
         * Wood material properties. Modify the default values to make it look
         * like wood.
         */
        WOOD(
                new float[]
                {
                    0.8f, 0.8f, 0.8f, 1.0f
                },
                new float[]
                {
                    0.0f, 0.0f, 0.0f, 1.0f
                }),
        /**
         * Orange material properties. Modify the default values to make it look
         * like orange.
         */
        ORANGE(
                new float[]
                {
                    0.8f, 0.8f, 0.8f, 1.0f
                },
                new float[]
                {
                    0.0f, 0.0f, 0.0f, 1.0f
                });

        /**
         * The diffuse RGBA reflectance of the material.
         */
        float[] diffuse;

        /**
         * The specular RGBA reflectance of the material.
         */
        float[] specular;

        /**
         * Constructs a new material with diffuse and specular properties.
         */
        private Material(float[] diffuse, float[] specular)
        {
            this.diffuse = diffuse;
            this.specular = specular;
        }
    }

    /**
     * Represents a Robot, to be implemented according to the Assignments.
     */
    private class Robot
    {

        // <editor-fold defaultstate="collapsed" desc="Limb Classes">
        private abstract class Limb
        {

            protected Vector localOrigin;
            protected Robot robot;

            public double[] rotationXYZ;

            public Limb(Vector localOrigin, Robot robot)
            {
                this.robot = robot;
                this.localOrigin = localOrigin;
                this.rotationXYZ = new double[]
                {
                    0, 0, 0
                };
            }

            public void draw()
            {
                gl.glPushMatrix();

                gl.glTranslated(localOrigin.x(), localOrigin.y(), localOrigin.z());
                gl.glRotated(rotationXYZ[0], 1, 0, 0);
                gl.glRotated(rotationXYZ[1], 0, 1, 0);
                gl.glRotated(rotationXYZ[2], 0, 0, 1);

                if (this.robot.drawStickFigure)
                {
                    drawStickFigure();
                } else
                {
                    drawSolid();
                }

                drawChildLimbs();

                gl.glPopMatrix();
            }

            public abstract void drawStickFigure();

            public abstract void drawSolid();

            public abstract void drawChildLimbs();
        }

        private class Torso extends Limb
        {

            public Head head;
            public UpperLeg foreLegLeft;
            public UpperLeg foreLegRight;
            public UpperLeg hindLegLeft;
            public UpperLeg hindLegRight;
            
            private final double legsOffsetX = 2;
            private final double legsOffsetY = 1.4;

            public Torso(Robot robot)
            {
                super(new Vector(0, 0, 11), robot);
                
                this.head = new Head(new Vector(4.4,0,0), robot);
                this.foreLegLeft = new UpperLeg(new Vector(legsOffsetX, legsOffsetY, 0), robot);
                this.foreLegRight = new UpperLeg(new Vector(legsOffsetX, -legsOffsetY, 0), robot);
                this.hindLegLeft = new UpperLeg(new Vector(-legsOffsetX, legsOffsetY, 0), robot);
                this.hindLegRight = new UpperLeg(new Vector(-legsOffsetX, -legsOffsetY, 0), robot);
            }

            @Override
            public void drawStickFigure()
            {
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
            public void drawSolid()
            {

            }

            @Override
            public void drawChildLimbs()
            {
                this.head.draw();
                this.foreLegLeft.draw();
                this.hindLegLeft.draw();
                this.foreLegRight.draw();
                this.hindLegRight.draw();
            }
        }

        private class UpperLeg extends Limb
        {
            public LowerLeg lowerLeg;

            public UpperLeg(Vector localOrigin, Robot robot)
            {
                super(localOrigin, robot);
                this.lowerLeg = new LowerLeg(new Vector(0, 0, -7.6), robot);
            }

            @Override
            public void drawStickFigure()
            {
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(0f, 0f, -7.6f);
                gl.glEnd();
                gl.glFlush();
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
            }

            @Override
            public void drawSolid()
            {

            }

            @Override
            public void drawChildLimbs()
            {
                this.lowerLeg.draw();
            }

        }

        public class LowerLeg extends Limb
        {
            public Foot foot;
            
            public LowerLeg(Vector localOrigin, Robot robot)
            {
                super(localOrigin, robot);
                this.foot = new Foot(new Vector(0, 0, -3.4f), robot);
            }

            @Override
            public void drawStickFigure()
            {
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(0f, 0f, -3.4f);
                gl.glEnd();
                gl.glFlush();
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
            }

            @Override
            public void drawSolid()
            {

            }

            @Override
            public void drawChildLimbs()
            {
                this.foot.draw();
            }

        }

        public class Foot extends Limb
        {

            public Foot(Vector localOrigin, Robot robot)
            {
                super(localOrigin, robot);
            }

            @Override
            public void drawStickFigure()
            {
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
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
            public void drawSolid()
            {
                
            }

            @Override
            public void drawChildLimbs()
            {

            }

        }

        public class Head extends Limb
        {

            public Head(Vector localOrigin, Robot robot)
            {
                super(localOrigin, robot);
            }

            @Override
            public void drawStickFigure()
            {
                gl.glColor3f(1, 0, 0);
                glut.glutWireSphere(0.3, 5, 5);
                gl.glColor3f(0, 0, 0);
                gl.glBegin(GL_LINES);
                gl.glVertex3f(0, 0, 0);
                gl.glVertex3f(3.4f, 0f, 0f);
                gl.glEnd();
                gl.glFlush();
            }

            @Override
            public void drawSolid()
            {
                
            }

            @Override
            public void drawChildLimbs()
            {

            }

        }
        // </editor-fold>
        
        private final Limb rootLimb;
        /**
         * The material from which this robot is built.
         */
        
        protected Vector startPosition;
        protected Vector position;
        
        private final Material material;

        public final boolean drawStickFigure = true;

        /**
         * Constructs the robot with initial parameters.
         */
        public Robot(Material material, Vector startPosition)
        {
            this.material = material;
            this.startPosition = startPosition;
            this.position = startPosition;

            rootLimb = new Torso(this);
            // code goes here ...
        }
        
        public void move(Vector offset) {
            this.position = startPosition.add(offset);
        }

        /**
         * Draws this robot (as a {@code stickfigure} if specified).
         */
        public void draw(boolean stickFigure)
        {
            gl.glPushMatrix();
            gl.glTranslated(position.x(), position.y(), position.z());
            rootLimb.draw();
            gl.glPopMatrix();

            // code goes here ...
//            if (stickFigure)
//            {
//                gl.glColor3f(0, 0, 0);
//                gl.glPushMatrix();
//
//                //Draw Torso
//                gl.glTranslatef(-2.25f, 0f, 5f);
//                gl.glBegin(GL_LINES);
//                gl.glVertex3f(0, 0, 0);
//                gl.glVertex3f(5f, 0f, 0f);
//                gl.glEnd();
//                gl.glFlush();
//
//                //Draw Head
//                gl.glPopMatrix();
//            }
        }
    }

    /**
     * Implementation of a camera with a position and orientation.
     */
    private class Camera
    {

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
        public void update(int mode)
        {
            robots[0].toString();

            // Helicopter mode
            if (1 == mode)
            {
                setHelicopterMode();

                // Motor cycle mode
            } else if (2 == mode)
            {
                setMotorCycleMode();

                // First person mode
            } else if (3 == mode)
            {
                setFirstPersonMode();

                // Auto mode
            } else if (4 == mode)
            {
                // code goes here...

                // Default mode
            } else
            {
                setDefaultMode();
            }
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * camera's default mode.
         */
        private void setDefaultMode()
        {
            // code goes here ...
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * helicopter mode.
         */
        private void setHelicopterMode()
        {
            // code goes here ...
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * motorcycle mode.
         */
        private void setMotorCycleMode()
        {
            // code goes here ...
        }

        /**
         * Computes {@code eye}, {@code center}, and {@code up}, based on the
         * first person mode.
         */
        private void setFirstPersonMode()
        {
            // code goes here ...
        }

    }

    /**
     * Implementation of a race track that is made from Bezier segments.
     */
    private class RaceTrack
    {

        /**
         * Array with control points for the O-track.
         */
        private Vector[] controlPointsOTrack;

        /**
         * Array with control points for the L-track.
         */
        private Vector[] controlPointsLTrack;

        /**
         * Array with control points for the C-track.
         */
        private Vector[] controlPointsCTrack;

        /**
         * Array with control points for the custom track.
         */
        private Vector[] controlPointsCustomTrack;

        /**
         * Constructs the race track, sets up display lists.
         */
        public RaceTrack()
        {
            // code goes here ...
        }

        /**
         * Draws this track, based on the selected track number.
         */
        public void draw(int trackNr)
        {

            // The test track is selected
            if (0 == trackNr)
            {
                // code goes here ...

                // The O-track is selected
            } else if (1 == trackNr)
            {
                // code goes here ...

                // The L-track is selected
            } else if (2 == trackNr)
            {
                // code goes here ...

                // The C-track is selected
            } else if (3 == trackNr)
            {
                // code goes here ...

                // The custom track is selected
            } else if (4 == trackNr)
            {
                // code goes here ...

            }
        }

        /**
         * Returns the position of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getPoint(double t)
        {
            return Vector.O; // <- code goes here
        }

        /**
         * Returns the tangent of the curve at 0 <= {@code t} <= 1.
         */
        public Vector getTangent(double t)
        {
            return Vector.O; // <- code goes here
        }

    }

    /**
     * Implementation of the terrain.
     */
    private class Terrain
    {

        /**
         * Can be used to set up a display list.
         */
        public Terrain()
        {
            // code goes here ...
        }

        /**
         * Draws the terrain.
         */
        public void draw()
        {
            // code goes here ...
        }

        /**
         * Computes the elevation of the terrain at ({@code x}, {@code y}).
         */
        public float heightAt(float x, float y)
        {
            return 0; // <- code goes here
        }
    }

    /**
     * Main program execution body, delegates to an instance of the RobotRace
     * implementation.
     */
    public static void main(String args[])
    {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }

}
