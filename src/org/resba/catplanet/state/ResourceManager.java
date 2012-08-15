package org.resba.catplanet.state;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.sound.midi.Sequence;

import org.resba.catplanet.sound.*;



/**
    The ResourceManager class loads resources like images and
    sounds.
*/
public class ResourceManager {

    private GraphicsConfiguration gc;
    private SoundManager soundManager;
    private MidiPlayer midiPlayer;

    /**
        Creates a new ResourceManager with the specified
        GraphicsConfiguration.
    */
    public ResourceManager(GraphicsConfiguration gc,
        SoundManager soundManager, MidiPlayer midiPlayer)
    {
        this.gc = gc;
        this.soundManager = soundManager;
        this.midiPlayer = midiPlayer;

        try {
            java.util.Enumeration e = getClass().getClassLoader().getResources("com.brackeen.javagamebook.state.ResourceManager");
            while (e.hasMoreElements()) {
                System.out.println(e.nextElement());
            }

        }
        catch (IOException ex) {

        }

    }


    /**
        Gets an image from the images/ directory.
    */
    public Image loadImage(String name) {
        String filename = "images/" + name;
        return new ImageIcon(getResource(filename)).getImage();
    }


    public Image getMirrorImage(Image image) {
        return getScaledImage(image, -1, 1);
    }


    public Image getFlippedImage(Image image) {
        return getScaledImage(image, 1, -1);
    }


    private Image getScaledImage(Image image, float x, float y) {

        // set up the transform
        AffineTransform transform = new AffineTransform();
        transform.scale(x, y);
        transform.translate(
            (x-1) * image.getWidth(null) / 2,
            (y-1) * image.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image newImage = gc.createCompatibleImage(
            image.getWidth(null),
            image.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D g = (Graphics2D)newImage.getGraphics();
        g.drawImage(image, transform, null);
        g.dispose();

        return newImage;
    }


    public URL getResource(String filename) {
        return getClass().getClassLoader().getResource(filename);
    }

    public InputStream getResourceAsStream(String filename) {
        return getClass().getClassLoader().
            getResourceAsStream(filename);
    }


    public Sound loadSound(String name) {
        return soundManager.getSound(getResourceAsStream(name));
    }


    public Sequence loadSequence(String name) {
        return midiPlayer.getSequence(getResourceAsStream(name));
    }


}
