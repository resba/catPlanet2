package org.resba.catplanet.tilegame;

import java.awt.Graphics2D;
import java.util.logging.*;
import javax.sound.sampled.AudioFormat;

import org.resba.catplanet.input.InputManager;
import org.resba.catplanet.sound.MidiPlayer;
import org.resba.catplanet.sound.SoundManager;
import org.resba.catplanet.state.*;
import org.resba.catplanet.test.GameCore;



/**
    GameManager manages all parts of the game.
*/
public class GameManager extends GameCore {

    static final Logger log = Logger.getLogger("org.resba.catplanet.tilegame");

    /**
        Length of one simulation step in ms. Rendering runs at whatever
        rate the display allows; the world always advances in these
        fixed steps so physics feels identical on every machine.
    */
    public static final long STEP_MS = 5;

    /**
        Never simulate more than this much time per frame (a hitch or a
        debugger pause should not turn into a huge catch-up).
    */
    private static final long MAX_FRAME_MS = 100;

    public static void main(String[] args) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat PLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);


    private MidiPlayer midiPlayer;
    private SoundManager soundManager;
    private ResourceManager resourceManager;
    private InputManager inputManager;
    private GameStateManager gameStateManager;
    private long accumulator;

    public void init() {
        log.setLevel(Level.INFO);

        log.info("init sound manager");
        soundManager = new SoundManager(PLAYBACK_FORMAT, 8);

        log.info("init midi player");
        midiPlayer = new MidiPlayer();

        log.info("init gamecore");
        super.init();

        log.info("init input manager");
        inputManager = new InputManager(screen.getInputComponent());
        inputManager.setCursor(InputManager.INVISIBLE_CURSOR);


        log.info("init resource manager");
        resourceManager = new CatPlanetResourceManager(
            screen.getFullScreenWindow().getGraphicsConfiguration(),
            soundManager, midiPlayer);


        log.info("init game states");
        gameStateManager = new GameStateManager(inputManager,
            resourceManager.loadImage("splash.png"));
        gameStateManager.addState(new MainGameState(
            soundManager, midiPlayer,
            screen.getWidth(), screen.getHeight()));
        gameStateManager.addState(
            new SplashGameState("gamesplash.png"));

        // load resources (in separate thread)
        Thread loader = new Thread(() -> {
            log.info("loading resources");
            gameStateManager.loadAllResources(resourceManager);
            log.info("done loading resources. splash has been notified.");
            gameStateManager.setState("Splash");
        }, "resource-loader");
        loader.setDaemon(true);
        loader.start();
    }


    /**
        Closes any resources used by the GameManager.
    */
    public void stop() {
        log.info("stopping game");
        super.stop();
        log.info("closing midi player");
        midiPlayer.close();
        log.info("closing sound manager");
        soundManager.close();
    }

    public void update(long elapsedTime) {
        if (gameStateManager.isDone()) {
            stop();
            return;
        }
        accumulator += Math.min(elapsedTime, MAX_FRAME_MS);
        while (accumulator >= STEP_MS && !gameStateManager.isDone()) {
            gameStateManager.update(STEP_MS);
            accumulator -= STEP_MS;
        }
    }

    public void draw(Graphics2D g) {
        gameStateManager.draw(g);
    }

}
