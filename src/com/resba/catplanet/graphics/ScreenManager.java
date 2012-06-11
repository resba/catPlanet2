package com.resba.catplanet.graphics;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import javax.swing.*;

import com.resba.catplanet.catmonitor.CatMonitor;

/**
    The ScreenManager class manages initializing and displaying
    full screen graphics modes.
*/
public class ScreenManager {

    private GraphicsDevice device;
    private JFrame j;
    

    /**
        Creates a new ScreenManager object.
    */
    public ScreenManager() {
        GraphicsEnvironment environment =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        j = new JFrame();
    }


    /**
        Returns a list of compatible display modes for the
        default device on the system.
    */
    public DisplayMode[] getCompatibleDisplayModes() {
        return device.getDisplayModes();
    }


    /**
        Returns the first compatible mode in a list of modes.
        Returns null if no modes are compatible.
    */
    public DisplayMode findFirstCompatibleMode(
        DisplayMode modes[])
    {
        DisplayMode goodModes[] = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            for (int j = 0; j < goodModes.length; j++) {
                if (displayModesMatch(modes[i], goodModes[j])) {
                    return modes[i];
                }
            }

        }

        return null;
    }


    /**
        Determines if two display modes "match". Two display
        modes match if they have the same resolution, bit depth,
        and refresh rate. The bit depth is ignored if one of the
        modes has a bit depth of DisplayMode.BIT_DEPTH_MULTI.
        Likewise, the refresh rate is ignored if one of the
        modes has a refresh rate of
        DisplayMode.REFRESH_RATE_UNKNOWN.
    */
    public boolean displayModesMatch(DisplayMode mode1,
        DisplayMode mode2)

    {
        if (mode1.getWidth() != mode2.getWidth() ||
            mode1.getHeight() != mode2.getHeight())
        {
            return false;
        }

        if (mode1.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            mode2.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
            mode1.getBitDepth() != mode2.getBitDepth())
        {
            return false;
        }

        if (mode1.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            mode2.getRefreshRate() !=
            DisplayMode.REFRESH_RATE_UNKNOWN &&
            mode1.getRefreshRate() != mode2.getRefreshRate())
         {
             return false;
         }

         return true;
    }


    /**
        Gets the graphics context for the display. The
        ScreenManager uses double buffering, so applications must
        call update() to show any graphics drawn.
        <p>
        The application must dispose of the graphics object.
    */
    public Graphics2D getGraphics() {
        Window window = j;
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            return (Graphics2D)strategy.getDrawGraphics();
        }
        else {
            return null;
        }
    }


    /**
        Updates the display.
    */
    public void update() {
        //System.out.println("Update in Screen Manager Called.");

        // Sync the display on some systems.
        // (on Linux, this fixes event queue problems)
        Window window = j;
        if (window != null) {
            BufferStrategy strategy = window.getBufferStrategy();
            if (!strategy.contentsLost()) {
                strategy.show();
            }
        }
    }


    /**
        Returns the window currently used in full screen mode.
        Returns null if the device is not in full screen mode.
    */
    public JFrame getFullScreenWindow() {
        j.pack();
        j.setVisible(true);
        j.setSize(800, 600);
        return j;
    }

    public void setupBuffering(){
        j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        j.setIgnoreRepaint(true);
        j.setResizable(false);
        try {
            EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    j.createBufferStrategy(2);
                }
            });
        }
        catch (InterruptedException ex) {
            // ignore
        }
        catch (InvocationTargetException  ex) {
            // ignore
        }
    }

    /**
        Returns the width of the window currently used in full
        screen mode. Returns 0 if the device is not in full
        screen mode.
    */
    public int getWidth() {
        Window window = j;
        if (window != null) {
            return window.getWidth();
        }
        else {
            return 800;
        }
    }


    /**
        Returns the height of the window currently used in full
        screen mode. Returns 0 if the device is not in full
        screen mode.
    */
    public int getHeight() {
        Window window = j;
        if (window != null) {
            return window.getHeight();
        }
        else {
            return 800;
        }
    }


    /**
        Restores the screen's display mode.
    */
    public void restoreScreen() {
        Window window = j;
        if (window != null) {
            window.dispose();
        }
        window.setSize(800, 800);
    }


    /**
        Creates an image compatible with the current display.
    */
    public BufferedImage createCompatibleImage(int w, int h,
        int transparancy)
    {
        Window window = j;
        if (window != null) {
            GraphicsConfiguration gc =
                window.getGraphicsConfiguration();
            return gc.createCompatibleImage(w, h, transparancy);
        }
        return null;
    }
}
