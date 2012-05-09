package com.resba.catplanet.util;

/**
    Smoothes out the jumps in time do to poor timer accuracy.
    This is a simple algorithm that is slightly inaccurate (the
    smoothed time may be slightly ahead of real time) but gives
    better-looking results.
*/
public class TimeSmoothie {

    /**
        How often to recalc the frame rate
    */
    protected static final long FRAME_RATE_RECALC_PERIOD = 500;

    /**
        Don't allow the elapsed time between frames to be more than 100 ms
    */
    protected static final long MAX_ELAPSED_TIME = 100;

    /**
        Take the average of the last few samples during the last 100ms
    */
    protected static final long AVERAGE_PERIOD = 100;

    protected static final int NUM_SAMPLES_BITS = 6; // 64 samples
    protected static final int NUM_SAMPLES = 1 << NUM_SAMPLES_BITS;
    protected static final int NUM_SAMPLES_MASK = NUM_SAMPLES - 1;

    protected long[] samples;
    protected int numSamples = 0;
    protected int firstIndex = 0;

    // for calculating frame rate
    protected int numFrames = 0;
    protected long startTime;
    protected float frameRate;

    public TimeSmoothie() {
        samples = new long[NUM_SAMPLES];
    }


    /**
        Adds the specified time sample and returns the average
        of all the recorded time samples.
    */
    public long getTime(long elapsedTime) {
        addSample(elapsedTime);
        return getAverage();
    }


    /**
        Adds a time sample.
    */
    public void addSample(long elapsedTime) {
        numFrames++;

        // cap the time
        elapsedTime = Math.min(elapsedTime, MAX_ELAPSED_TIME);

        // add the sample to the list
        samples[(firstIndex + numSamples) & NUM_SAMPLES_MASK] =
            elapsedTime;
        if (numSamples == samples.length) {
            firstIndex = (firstIndex + 1) & NUM_SAMPLES_MASK;
        }
        else {
            numSamples++;
        }
    }


    /**
        Gets the average of the recorded time samples.
    */
    public long getAverage() {
        long sum = 0;
        for (int i=numSamples-1; i>=0; i--) {
            sum+=samples[(firstIndex + i) & NUM_SAMPLES_MASK];

            // if the average period is already reached, go ahead and return
            // the average.
            if (sum >= AVERAGE_PERIOD) {
                Math.round((double)sum / (numSamples-i));
            }
        }
        return Math.round((double)sum / numSamples);
    }


    /**
        Gets the frame rate (number of calls to getTime() or
        addSample() in real time). The frame rate is recalculated
        every 500ms.
    */
    public float getFrameRate() {
        long currTime = System.currentTimeMillis();

        // calculate the frame rate every 500 milliseconds
        if (currTime > startTime + FRAME_RATE_RECALC_PERIOD) {
            frameRate = (float)numFrames * 1000 /
                (currTime - startTime);
            startTime = currTime;
            numFrames = 0;
        }

        return frameRate;
    }
}
