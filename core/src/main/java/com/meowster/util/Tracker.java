/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.util;

/**
 * A simple throughput tracker. Use something like this:
 * <pre>
 *     Tracker tracker = new Tracker();
 *     tracker.start();
 *     ...
 *     ... some loop construct ...
 *         ... do one piece of work ...
 *         tracker.inc();
 *     ... end of loop ...
 *     tracker.stop();
 *     System.out.println(tracker);
 * </pre>
 *
 * @author Simon Hunt
 */
public class Tracker {

    private static final String E_STARTED = "Tracker already started";
    private static final String E_STOPPED = "Tracker already stopped";
    private static final String E_NOT_RUNNING = "Tracker clock not running";

    private static final String NA = "n/a";
    private static final String ZERO = "0.00/s";
    private static final String E_LESS_THAN_ONE = "amount must be 1 or more";

    private long startedAt;
    private long stoppedAt;
    private boolean started = false;
    private boolean stopped = false;

    private long count;

    @Override
    public String toString() {
        return "Tracker{" +
                "started=" + started +
                ", stopped=" + stopped +
                ", count=" + count +
                ", duration=" + (stoppedAt - startedAt) + "ms" +
                ", \"" + perSecond() +
                "\"}";
    }

    /**
     * Returns the current time in milliseconds (epoch time).
     * This implementation uses {@link System#currentTimeMillis()}.
     *
     * @return the current time in milliseconds
     */
    protected long now() {
        return System.currentTimeMillis();
    }

    /**
     * Starts the tracker clock.
     */
    public void start() {
        if (started)
            throw new IllegalStateException(E_STARTED);
        started = true;
        startedAt = now();
    }

    /**
     * Increments the tracker counter by 1.
     *
     * @throws IllegalStateException if the tracker clock is not running
     */
    public void inc() {
        inc(1);
    }

    /**
     * Increments the tracker counter by the specified amount.
     *
     * @param amount the amount by which to increment the counter
     * @throws IllegalArgumentException if amount is &lt; 1
     * @throws IllegalStateException    if the tracker clock is not running
     */
    public void inc(long amount) {
        if (amount < 1)
            throw new IllegalArgumentException(E_LESS_THAN_ONE);
        if (!started || stopped)
            throw new IllegalStateException(E_NOT_RUNNING);
        count += amount;
    }

    /**
     * Freezes the tracker clock.
     *
     * @throws IllegalStateException if the tracker clock is not running or if
     *                               we have already stopped
     */
    public void stop() {
        if (!started)
            throw new IllegalStateException(E_NOT_RUNNING);
        if (stopped)
            throw new IllegalStateException(E_STOPPED);

        stopped = true;
        stoppedAt = now();
    }

    /**
     * Returns the tracker count.
     *
     * @return the count
     */
    public long count() {
        return count;
    }

    /**
     * Calculates and returns the throughput. That is, the counter divided
     * by the duration. If the tracker has not been stopped, "n/a" will be
     * returned.
     *
     * @return the throughput result
     */
    public String perSecond() {
        if (!stopped)
            return NA;

        long durationMs = stoppedAt - startedAt;
        if (durationMs == 0)
            return ZERO;

        double durationSecs = durationMs / 1000.0;
        double rate = count / durationSecs;
        String result = String.format("%.02f", rate);
        return result + "/s";
    }

    /**
     * Returns the duration, in milliseconds.
     *
     * @return the duration
     */
    public String duration() {
        return (stoppedAt - startedAt) + "ms";
    }
}
