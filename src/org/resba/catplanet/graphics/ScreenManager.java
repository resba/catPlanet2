package org.resba.catplanet.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;



/**
    The ScreenManager owns the game window and the double-buffered
    surface the game draws on.

    <p>The game renders into a fixed-size Canvas inside a JFrame rather
    than onto the frame itself, so the drawable area is exactly
    WIDTH x HEIGHT regardless of window decorations, and keyboard input
    is taken from that canvas.
*/
public class ScreenManager {

    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    private final JFrame frame;
    private final Canvas canvas;


    /**
        Creates the (still hidden) window and canvas.
    */
    public ScreenManager() {
        frame = new JFrame();
        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        canvas.setSize(WIDTH, HEIGHT);
        canvas.setIgnoreRepaint(true);
        canvas.setFocusable(true);
        canvas.setBackground(Color.black);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setIgnoreRepaint(true);
        frame.setLayout(new BorderLayout());
        frame.add(canvas, BorderLayout.CENTER);
        frame.pack();
        frame.setLocationRelativeTo(null);
    }


    /**
        Returns the game window. The name is historical: the game runs
        in a fixed-size window rather than exclusive full screen mode.
    */
    public JFrame getFullScreenWindow() {
        return frame;
    }


    /**
        The component that receives keyboard and mouse input.
    */
    public Component getInputComponent() {
        return canvas;
    }


    /**
        Shows the window and creates the double buffer.
    */
    public void setupBuffering(){
        Runnable setup = new Runnable() {
            public void run() {
                frame.setVisible(true);
                canvas.setFont(frame.getFont());
                canvas.setForeground(frame.getForeground());
                canvas.createBufferStrategy(2);
                canvas.requestFocusInWindow();
            }
        };
        if (EventQueue.isDispatchThread()) {
            setup.run();
            return;
        }
        try {
            EventQueue.invokeAndWait(setup);
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        catch (InvocationTargetException  ex) {
            throw new IllegalStateException("Could not create window", ex);
        }
    }


    /**
        Gets the graphics context for the display. The
        ScreenManager uses double buffering, so applications must
        call update() to show any graphics drawn.
        <p>
        The application must dispose of the graphics object.
    */
    public Graphics2D getGraphics() {
        BufferStrategy strategy = canvas.getBufferStrategy();
        if (strategy == null) {
            return null;
        }
        return (Graphics2D)strategy.getDrawGraphics();
    }


    /**
        Flips the buffer to show what was drawn since getGraphics().
    */
    public void update() {
        BufferStrategy strategy = canvas.getBufferStrategy();
        if (strategy != null && !strategy.contentsLost()) {
            strategy.show();
        }
        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Toolkit.getDefaultToolkit().sync();
    }


    /**
        Width of the drawable area in pixels.
    */
    public int getWidth() {
        return WIDTH;
    }


    /**
        Height of the drawable area in pixels.
    */
    public int getHeight() {
        return HEIGHT;
    }


    /**
        Closes the window.
    */
    public void restoreScreen() {
        frame.dispose();
    }

    public void setTitle(String title){
        frame.setTitle(title);
    }

    /**
        Creates an image compatible with the current display.
    */
    public BufferedImage createCompatibleImage(int w, int h,
        int transparancy)
    {
        GraphicsConfiguration gc = canvas.getGraphicsConfiguration();
        if (gc == null) {
            gc = frame.getGraphicsConfiguration();
        }
        return gc.createCompatibleImage(w, h, transparancy);
    }
}
