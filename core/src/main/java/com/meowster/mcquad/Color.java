/*
 * Copyright (c) 2014-2016 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.util.StringUtils;

/**
 * Our notion of color, encapsulating the ubiquitous alpha-red-green-blue
 * (ARGB) color model, with 8 bits per component.
 *
 * @author Simon Hunt
 */
class Color {
    private static final int FF = 0xff;
    private static final int BYTE_LEN = 8;
    private static final int MAX_BYTE = 255;

    /**
     * Totally transparent.
     */
    public static final Color TRANSPARENT = new Color(0x0);

    /**
     * The color black.
     */
    public static final Color BLACK = new Color(0xff000000);

    /**
     * The color red.
     */
    public static final Color RED = new Color(0xffff0000);

    /**
     * The color green.
     */
    public static final Color GREEN = new Color(0xff00ff00);

    /**
     * The color blue.
     */
    public static final Color BLUE = new Color(0xff0000ff);

    /**
     * The color yellow.
     */
    public static final Color YELLOW = new Color(0xffffff00);

    /**
     * The color magenta.
     */
    public static final Color MAGENTA = new Color(0xffff00ff);

    /**
     * The color cyan.
     */
    public static final Color CYAN = new Color(0xff00ffff);

    /**
     * The color white.
     */
    public static final Color WHITE = new Color(0xffffffff);


    private final int raw;
    private final int alpha;
    private final int red;
    private final int green;
    private final int blue;

    /**
     * Creates a color instance from the given int value, where the color
     * components are encoded as: {@code 0xAARRGGBB}.
     *
     * @param val the encoded value
     */
    public Color(int val) {
        int tmp = val;
        raw = tmp;
        blue = tmp & FF;
        tmp >>= BYTE_LEN;
        green = tmp & FF;
        tmp >>= BYTE_LEN;
        red = tmp & FF;
        tmp >>= BYTE_LEN;
        alpha = tmp & FF;
    }

    /**
     * Creates a color instance from the given hex string
     * where the format is: {@code "0xAARRGGBB"}.
     *
     * @param hexString the hex representation of the color
     */
    public Color(String hexString) {
        this((int) StringUtils.hexAsLong(hexString));
    }

    /**
     * Creates a color instance from the given component values. Note that
     * the values are "clamped" to the range 0 .. 255; that is to say, values
     * less than 0 will be treated as 0, and values more than 255 will be
     * treated as 255.
     *
     * @param alpha the alpha (opacity) component
     * @param red   the red component
     * @param green the green component
     * @param blue  the blue component
     */
    public Color(int alpha, int red, int green, int blue) {
        this.alpha = clampByte(alpha);
        this.red = clampByte(red);
        this.green = clampByte(green);
        this.blue = clampByte(blue);
        raw = computeRaw();
    }

    private int clampByte(int value) {
        return value < 0 ? 0 : (value > MAX_BYTE ? MAX_BYTE : value);
    }

    private int computeRaw() {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    @Override
    public String toString() {
        return "Color{" +
                "a=" + alpha +
                ",r=" + red +
                ",g=" + green +
                ",b=" + blue +
                '}';
    }

    /**
     * Returns the alpha component. Can be thought of as the "opacity", that
     * is, the amount of opaqueness (from 0 .. 255).
     *
     * @return the alpha component
     */
    public int alpha() {
        return alpha;
    }

    /**
     * Returns the transparency. Can be thought of as the opposite of the
     * "opacity" or {@link #alpha()}. The amount of transparency  is
     * from 0 .. 255.
     *
     * @return the transparency
     */
    public int transparency() {
        return FF - alpha;
    }

    /**
     * Returns the red component (from 0 .. 255).
     *
     * @return the red component
     */
    public int red() {
        return red;
    }

    /**
     * Returns the green component (from 0 .. 255).
     *
     * @return the green component
     */
    public int green() {
        return green;
    }

    /**
     * Returns the blue component (from 0 .. 255).
     *
     * @return the blue component
     */
    public int blue() {
        return blue;
    }

    /**
     * Returns the color as a hex string of the form {@code "0xAARRGGBB"}.
     *
     * @return the color as a hex string
     */
    public String hex() {
        return StringUtils.asHex(raw);
    }

    /**
     * Returns the color encoded in an int value: {@code 0xAARRGGBB}.
     *
     * @return the color as an int
     */
    public int toInt() {
        return raw;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Color color = (Color) o;
        return raw == color.raw;
    }

    @Override
    public int hashCode() {
        return raw;
    }

    /**
     * Returns the color resulting from shading "this" color by the specified
     * amount.
     *
     * @param amount amount of shading
     * @return the resulting shaded color
     */
    public Color shade(int amount) {
        return new Color(alpha, red + amount, green + amount, blue + amount);
    }

    /**
     * Returns the color resulting from overlaying "this" color with
     * the specified color.
     *
     * @param overlayColor the overlay color
     * @return the resulting combined color
     */
    public Color overlay(Color overlayColor) {
        final OverlayFilter filter = new OverlayFilter(overlayColor);
        return new Color(
                filter.combinedAlpha(alpha),
                filter.combinedRgb(overlayColor.red, red),
                filter.combinedRgb(overlayColor.green, green),
                filter.combinedRgb(overlayColor.blue, blue)
        );
    }

    /**
     * Returns the color resulting from starting with "this" color, and
     * repeatedly overlaying the given color the specified number of times.
     * Note that invoking {@link #overlay(com.meowster.mcquad.Color)} is
     * equivalent to invoking this method with a repeat count of 1.
     *
     * @param overlayColor the overlay color
     * @param repeatCount  the repeat count
     * @return the resulting combined color
     */
    public Color overlay(Color overlayColor, int repeatCount) {
        Color result = this;
        for (int i = 0; i < repeatCount; i++)
            result = result.overlay(overlayColor);
        return result;
    }

    /**
     * Returns the color resulting from "de-multiplying" this color by its
     * alpha component.
     *
     * @return the alpha-demultiplied color
     */
    public Color demultiplyAlpha() {
        return alpha == 0 ? TRANSPARENT : new Color(
                alpha,
                demultAlpha(red),
                demultAlpha(green),
                demultAlpha(blue)
        );
    }

    private int demultAlpha(int component) {
        return component * MAX_BYTE / alpha;
    }

    /**
     * Returns the color resulting from "this" color multiplied by the
     * specified color.
     *
     * @param multiplier the color by which to multiply
     * @return the resulting combined color
     */
    public Color multiply(Color multiplier) {
        return new Color(
                multiply(alpha, multiplier.alpha),
                multiply(red, multiplier.red),
                multiply(green, multiplier.green),
                multiply(blue, multiplier.blue)
        );
    }

    /**
     * Returns the color resulting from "this" color multiplied by the
     * specified color (but ignoring the multiplier's alpha component).
     *
     * @param multiplier the color by which to multiply
     * @return the resulting combined color
     */
    public Color multiplySolid(Color multiplier) {
        return new Color(
                alpha,
                multiply(red, multiplier.red),
                multiply(green, multiplier.green),
                multiply(blue, multiplier.blue)
        );
    }

    private int multiply(int component, int multComponent) {
        return component * multComponent / MAX_BYTE;
    }

    /**
     * Private helper class to encapsulate overlay filtering behavior.
     */
    private static class OverlayFilter {
        private final int opacity;
        private final int transparency;

        OverlayFilter(Color overlay) {
            opacity = overlay.alpha();
            transparency = overlay.transparency();
        }

        int combinedAlpha(int alpha) {
            return opacity + showing(alpha) / MAX_BYTE;
        }

        int combinedRgb(int overlayComponent, int component) {
            return (occluded(overlayComponent) + showing(component)) / MAX_BYTE;
        }

        private int showing(int component) {
            return component * transparency;
        }

        private int occluded(int component) {
            return component * opacity;
        }
    }

    /**
     * Treats the input integers as ARGB color values; computes and returns
     * the average color.
     *
     * @param argb the color values to average
     * @return the computed average color
     */
    public static Color averageColor(int... argb) {
        if (argb == null || argb.length == 0)
            return TRANSPARENT;
        if (argb.length == 1)
            return new Color(argb[0]);

        int a = 0;
        int r = 0;
        int g = 0;
        int b = 0;

        for (int c : argb) {
            Color color = new Color(c);
            a += color.alpha;
            r += color.red;
            g += color.green;
            b += color.blue;
        }
        final int n = argb.length;
        return new Color(a / n, r / n, g / n, b / n);
    }
}
