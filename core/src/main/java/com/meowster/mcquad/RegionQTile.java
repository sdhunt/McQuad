/*
 * Copyright (c) 2014 Meowster.com
 */

package com.meowster.mcquad;

/**
* A quad tile composed of a region.
*
* @author Simon Hunt
*/
class RegionQTile extends AbsQuadTile {
    private final Region region;

    RegionQTile(Region region, Coord regionToQuadDelta) {
        this.region = region;
        int x = region.coord().x() + regionToQuadDelta.x();
        int z = region.coord().z() + regionToQuadDelta.z();
        coord = new Coord(x, z);
        image = TileUtils.generateImage(region);
    }

    @Override
    public void releaseResources() {
        super.releaseResources();
        region.releaseResources();
    }

    @Override
    public String toString() {
        return "RegionQTile{" +
                "region=" + region +
                ", coord=" + coord +
                '}';
    }
}
