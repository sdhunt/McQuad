/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.util;

import com.meowster.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link Tracker}.
 *
 * @author Simon Hunt
 */
public class TrackerTest extends AbstractTest {

    /**
     * Timepiece support class to allow us to increment time at our leisure.
     */
    private static class TimePiece {
        private long nowMs = 0;

        private long now() {
            return nowMs;
        }

        private void advanceTime(long ms) {
            nowMs += ms;
        }
    }

    /**
     * Mock tracker class so we can control the start and stop times.
     */
    private static class TestTracker extends Tracker {
        private final TimePiece time = new TimePiece();

        @Override
        protected long now() {
            return time.now();
        }
    }

    private Tracker t;

    private void advanceMs(long ms) {
        ((TestTracker) t).time.advanceTime(ms);
    }

    @Before
    public void setUp() {
        t = new TestTracker();
    }


    @Test
    public void basic() {
        title("basic");
        print(t);
        assertEquals(AM_UXS, 0, t.count());
        assertEquals(AM_NEQ, "n/a", t.perSecond());
    }

    @Test
    public void twoItemsPerSec() {
        title("twoItemsPerSec");
        t.start();
        t.inc();
        t.inc();
        advanceMs(1000);
        t.stop();
        print(t);
        assertEquals(AM_UXS, 2, t.count());
        assertEquals(AM_NEQ, "2.00/s", t.perSecond());
    }

    @Test(expected = IllegalStateException.class)
    public void startTwice() {
        t.start();
        t.start();
    }

    @Test(expected = IllegalStateException.class)
    public void incWhenNotStarted() {
        t.inc();
    }

    @Test(expected = IllegalStateException.class)
    public void incWhenStopped() {
        t.start();
        t.stop();
        t.inc();
    }

    @Test(expected = IllegalStateException.class)
    public void stopWhenNotStarted() {
        t.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void stopWhenStopped() {
        t.start();
        t.stop();
        t.stop();
    }

    @Test
    public void zeroDuration() {
        title("zeroDuration");
        t.start();
        t.stop();
        print(t);
        assertEquals(AM_NEQ, "0.00/s", t.perSecond());
    }

    @Test
    public void increment() {
        title("increment");
        t.start();
        t.inc();
        t.inc();
        assertEquals(AM_UXS, 2, t.count());
        t.inc();
        advanceMs(10);
        t.stop();
        print(t);
        assertEquals(AM_NEQ, "300.00/s", t.perSecond());
    }

    @Test
    public void incrementAmount() {
        title("incrementAmount");
        t.start();
        t.inc();
        assertEquals(AM_UXS, 1, t.count());
        advanceMs(1000);
        t.inc(14);
        t.stop();
        print(t);
        assertEquals(AM_UXS, 15, t.count());
        assertEquals(AM_NEQ, "15.00/s", t.perSecond());
    }

    @Test(expected = IllegalArgumentException.class)
    public void lessThanOne() {
        t.inc(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void lessThanOneAgain() {
        t.inc(-5);
    }

}
