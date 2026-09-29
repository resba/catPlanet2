package org.resba.catplanet.test;

import java.awt.*;
import javax.swing.ImageIcon;

import org.resba.catplanet.graphics.ScreenManager;


/**
    Simple abstract class used for testing. Subclasses should
    implement the draw() method.
*/
public abstract class GameCore {

    protected static final int FONT_SIZE = 12;

    /** Frames per second the render loop aims for. */
    protected static final int TARGET_FPS = 60;

    private static final long TARGET_FRAME_NS = 1_000_000_000L / TARGET_FPS;

    private volatile boolean isRunning;
    protected ScreenManager screen;


    /**
        Signals the game loop that it's time to quit
    */
    public void stop() {
        isRunning = false;
    }


    /**
        Calls init() and gameLoop()
    */
    public void run() {
        try {
            init();
            gameLoop();
        }catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            if (screen != null) {
                screen.restoreScreen();
            }
            lazilyExit();
        }
    }


    /**
        Exits the VM from a daemon thread. The daemon thread waits
        2 seconds then calls System.exit(0). Since the VM should
        exit when only daemon threads are running, this makes sure
        System.exit(0) is only called if neccesary. It's neccesary
        if the Java Sound system is running.
    */
    public void lazilyExit() {
        Thread thread = new Thread() {
            public void run() {
                // first, wait for the VM exit on its own.
                try {
                    Thread.sleep(2000);
                }
                catch (InterruptedException ex) { }
                // system is still running, so force an exit
                System.exit(0);
            }
        };
        thread.setDaemon(true);
        thread.start();
    }


    /**
        Creates the window and initialises objects.
    */
    public void init() {
        screen = new ScreenManager();

        Window window = screen.getFullScreenWindow();
        window.setFont(new Font("Dialog", Font.PLAIN, FONT_SIZE));
        window.setBackground(Color.black);
        window.setForeground(Color.white);
        screen.setTitle("Cat Planet Cat Planet: More Cat per Planet than the leading Planet.");
        screen.setupBuffering();
        isRunning = true;
    }


    public Image loadImage(String fileName) {
        return new ImageIcon(fileName).getImage();
    }


    /**
        Runs through the game loop until stop() is called.

        <p>Uses the monotonic nanosecond clock and paces frames to
        TARGET_FPS, sleeping only for whatever is left of the frame.
        Sub-millisecond remainders are carried over so the elapsed
        time handed to update() adds up to real time.
    */
    public void gameLoop() {
        long lastTime = System.nanoTime();

        while (isRunning) {
            long frameStart = System.nanoTime();
            long elapsedTime = (frameStart - lastTime) / 1_000_000L;
            lastTime += elapsedTime * 1_000_000L;

            // update
            update(elapsedTime);

            // draw the screen
            Graphics2D g = screen.getGraphics();
            if (g != null) {
                try {
                    draw(g);
                }
                finally {
                    g.dispose();
                }
                screen.update();
            }

            // pace to the target frame rate
            long sleepNs = TARGET_FRAME_NS - (System.nanoTime() - frameStart);
            if (sleepNs > 0) {
                try {
                    Thread.sleep(sleepNs / 1_000_000L,
                        (int)(sleepNs % 1_000_000L));
                }
                catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }


    /**
        Updates the state of the game/animation based on the
        amount of elapsed time that has passed.
    */
    public void update(long elapsedTime) {
        // do nothing
    }


    /**
        Draws to the screen. Subclasses must override this
        method.
    */
    public abstract void draw(Graphics2D g);
}
