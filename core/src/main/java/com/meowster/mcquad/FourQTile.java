/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
* A quad tile generated from the composition of four other quad tiles.
*
* @author Simon Hunt
*/
class FourQTile extends AbsQuadTile {
    private final String tag;

    // note: we are guaranteed that at least one of the tiles exists
    FourQTile(int a, int b, QuadTile tl, QuadTile tr,
              QuadTile bl, QuadTile br) {
        coord = new Coord(a / 2, b / 2);
        tag = makeTag(tl, tr, bl, br);
        image = TileUtils.generateImage(tl, tr, bl, br);
    }

    private String makeTag(QuadTile tl, QuadTile tr, QuadTile bl, QuadTile br) {
        return "[" + tileTag(tl) + tileTag(tr) + tileTag(bl) + tileTag(br) + "]";
    }

    private String tileTag(QuadTile qt) {
        return qt==null ? "." : "#";
    }

    @Override
    public String toString() {
        return "FourQTile{" +
                "coord=" + coord +
                ", tag=" + tag +
                '}';
    }
}
