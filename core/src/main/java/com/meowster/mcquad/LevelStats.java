/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.Tracker;

/**
 * Encapsulates statistics about a level build.
 *
 * @author Simon Hunt
 */
public class LevelStats {
    private int zoom;
    private final Tracker tracker = new Tracker();
    private int blanks;

    /**
     * Sets the zoom level these stats are associated with.
     *
     * @param zoom the zoom level
     */
    void setZoom(int zoom) {
        this.zoom = zoom;
    }

    /**
     * Returns a one-line report of these stats
     *
     * @return the report line
     */
    public String reportLine() {
        return "Zoom Level " + zoom + ": #tiles = " + tracker.count() +
//                ", duration = " + tracker.duration() +
//                ", throughput = " + tracker.perSecond() +
                ", (blanks=" + blanks + ")";
    }

    @Override
    public String toString() {
        return reportLine();
    }

    /**
     * Starts measuring throughput of tile generation.
     */
    public void startTracker() {
        tracker.start();
    }

    /**
     * Stops measuring tile generation throughput.
     */
    public void stopTracker() {
        tracker.stop();
    }

    /**
     * Add the stats of the specified quad tile to the tracker for this level.
     *
     * @param tile the quad tile
     */
    public void addStats(QuadTile tile) {
        tracker.inc();
    }

    /**
     * Increments the blank count.
     */
    public void incBlanks() {
        blanks++;
    }
}
