/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

import com.meowster.test.AbstractTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link com.meowster.mcquad.Color}.
 *
 * @author Simon Hunt
 */
public class ColorTest extends AbstractTest {

    private Color color;

    @Test(expected = NumberFormatException.class)
    public void badInput() {
        new Color("fred");
    }

    private void checkComponents(int a, int r, int g, int b, String hex) {
        assertEquals(AM_NEQ, a, color.alpha());
        assertEquals(AM_NEQ, r, color.red());
        assertEquals(AM_NEQ, g, color.green());
        assertEquals(AM_NEQ, b, color.blue());
        assertEquals(AM_NEQ, hex, color.hex());
    }

    @Test
    public void basic() {
        title("basic");
        color = new Color("0x00000000");
        print(color);
        checkComponents(0, 0, 0, 0, "0x0");
    }

    @Test
    public void red() {
        title("red");
        color = Color.RED;
        print(color);
        checkComponents(255, 255, 0, 0, "0xffff0000");
    }

    @Test
    public void green() {
        title("green");
        color = Color.GREEN;
        print(color);
        checkComponents(255, 0, 255, 0, "0xff00ff00");
    }

    @Test
    public void blue() {
        title("blue");
        color = Color.BLUE;
        print(color);
        checkComponents(255, 0, 0, 255, "0xff0000ff");
    }

    @Test
    public void randomColor1() {
        title("randomColor1");
        final int raw = 0xc0112233;
        final String hex = "0xc0112233";

        color = new Color(raw);
        print(color);
        assertEquals(AM_NEQ, 0xc0, color.alpha());
        assertEquals(AM_NEQ, 0x3f, color.transparency());
        assertEquals(AM_NEQ, 0x11, color.red());
        assertEquals(AM_NEQ, 0x22, color.green());
        assertEquals(AM_NEQ, 0x33, color.blue());
        assertEquals(AM_NEQ, 0x33, color.blue());
        assertEquals(AM_NEQ, raw, color.toInt());
        assertEquals(AM_NEQ, hex, color.hex());

        Color copy = new Color(hex);
        assertEquals(AM_NEQ, color, copy);
    }

    @Test
    public void differentConstructions() {
        title("differentConstructions");
        Color c1 = new Color("0xbbeeaadd");
        Color c2 = new Color(0xbbeeaadd);
        Color c3 = new Color(0xbb, 0xee, 0xaa, 0xdd);

        assertEquals(AM_NEQ, c1, c2);
        assertEquals(AM_NEQ, c2, c3);
        assertEquals(AM_NEQ, c1, c1);

        assertEquals(AM_NEQ, c1.toInt(), c2.toInt());
        assertEquals(AM_NEQ, c1.toInt(), c3.toInt());
    }

    @Test
    public void clampedByteConstruction() {
        title("clampedByteConstruction");
        int a = 128;
        int r = 3000;
        int g = 48;
        int b = -17;
        Color c = new Color(a, r, g, b);
        print(c);
        assertEquals(AM_NEQ, 0x80ff3000, c.toInt());
    }

    // ====================================================================

    @Test
    public void shade() {
        title("shade");
        checkShade(0, 0, 0);
        checkShade(0xff203040, 1, 0xff213141);
        checkShade(0x00a0b0c0, 0x52, 0x00f2ffff);
        checkShade(0xffbbeedd, -0x11, 0xffaaddcc);
    }

    private void checkShade(int color, int amt, int exp) {
        Color origColor = new Color(color);
        Color expColor = new Color(exp);
        Color actColor = origColor.shade(amt);
        print("shade(color:{}, amt:{}} => {}", origColor.hex(), amt, actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void overlay() {
        title("overlay");
        checkOverlay(0, 0, 0);
        //         1.0,30,40,50  0.25,128,128,128  1.0,54,62,69
        checkOverlay(0xff1e2832, 0x40808080,       0xff363e45);
        //         0.5,50,40,30  0.25,128,192,192  0.625,69,78,70
        checkOverlay(0x8032281e, 0x4080c0c0, 0x9f454e46);
    }

    private void checkOverlay(int color, int overlay, int exp) {
        Color origColor = new Color(color);
        Color overlayColor = new Color(overlay);
        Color expColor = new Color(exp);
        Color actColor = origColor.overlay(overlayColor);

        print("overlay(color:{}, overlay:{}) => {}",
                origColor.hex(), overlayColor.hex(), actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void repeatOverlay() {
        title("repeatOverlay");
        checkRepeatOverlay(0, 0, 4, 0);
        checkRepeatOverlay(0xff0000ff, 0x1000ff80, 1, 0xff0010f7);
        checkRepeatOverlay(0xff0000ff, 0x1000ff80, 2, 0xff001eef);
        checkRepeatOverlay(0xff0000ff, 0x1000ff80, 3, 0xff002ce8);
        checkRepeatOverlay(0xff0000ff, 0x1000ff80, 4, 0xff0039e1);
    }

    private void checkRepeatOverlay(int color, int overlay, int rep, int exp) {
        Color origColor = new Color(color);
        Color overlayColor = new Color(overlay);
        Color expColor = new Color(exp);
        Color actColor = origColor.overlay(overlayColor, rep);

        print("overlay(color:{}, overlay:{}, repeat:{}) => {}",
                origColor.hex(), overlayColor.hex(), rep, actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void demultiplyAlpha() {
        title("demultiplyAlpha");
        checkDemultiplyAlpha(0, 0);
        checkDemultiplyAlpha(0x80204060, 0x803f7fbf);  // div 0.5  = x2
        checkDemultiplyAlpha(0x40204060, 0x407fffff);  // div 0.25 = x4
        checkDemultiplyAlpha(0xff204060, 0xff204060);  // div 1.0  = x1
    }

    private void checkDemultiplyAlpha(int color, int exp) {
        Color origColor = new Color(color);
        Color expColor = new Color(exp);
        Color actColor = origColor.demultiplyAlpha();

        print("demultiplyAlpha({}) => {}", origColor.hex(), actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void multiply() {
        title("multiply");
        checkMultiply(0, 0, 0);
        checkMultiply(0x80ff0000, 0x80000080, 0x40000000);
        checkMultiply(0x80203040, 0x80403020, 0x40080908);
    }

    private void checkMultiply(int c1, int c2, int exp) {
        Color origColor = new Color(c1);
        Color multColor = new Color(c2);
        Color expColor = new Color(exp);
        Color actColor = origColor.multiply(multColor);

        print("multiply({}, {}) => {}", origColor.hex(), multColor.hex(),
                actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void multiplySolid() {
        title("multiplySolid");
        checkMultiplySolid(0, 0, 0);
        checkMultiplySolid(0x20444444, 0xff4080c0, 0x20112233);
    }

    private void checkMultiplySolid(int c1, int c2, int exp) {
        Color origColor = new Color(c1);
        Color multColor = new Color(c2);
        Color expColor = new Color(exp);
        Color actColor = origColor.multiplySolid(multColor);

        print("multiplySolid({}, {}) => {}", origColor.hex(), multColor.hex(),
                actColor.hex());
        assertEquals(AM_NEQ, expColor, actColor);
    }

    @Test
    public void averageEmpty() {
        title("averageEmpty");
        Color c = Color.averageColor();
        print(c);
        assertEquals(AM_NEQ, Color.TRANSPARENT, c);
    }

    @Test
    public void averageRed() {
        title("averageRed");
        Color c = Color.averageColor(Color.RED.toInt());
        print(c);
        assertEquals(AM_NEQ, Color.RED, c);
    }

    @Test
    public void averageRedBlue() {
        title("averageRedBlue");
        int r = Color.RED.toInt();
        int b = Color.BLUE.toInt();
        Color c = Color.averageColor(r, b);
        print(c);
        Color exp = new Color(0xff7f007f);
        assertEquals(AM_NEQ, exp, c);
    }


    @Test
    public void averageFour() {
        title("averageFour");
        int c1 = 0xff456689;
        int c2 = 0xff4577af;
        int c3 = 0xff4555a0;
        int c4 = 0xef4566a6;
        Color c = Color.averageColor(c1, c2, c3, c4);
        print(c);
        Color exp = new Color(0xfb45669f);
        assertEquals(AM_NEQ, exp, c);
    }
}
