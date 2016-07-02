/*
 * Copyright (c) 2014-2016 Meowster.com
 * 
 * Main functionality of McQuad Zoomable Map web app.
 *
 * Author: Simon Hunt
 */
(function () {
    'use strict';

    // closure variables
    var zmap, $mast, $map, cfg,
        $heatmapTitle, $heatmapImage,
        $mcx, $mcz;


    function styleValue(el, name) {
        var raw = el.style(name);
        var nopx = raw.replace(/px$/, '');
        return Number(nopx);
    }

    function px(x) {
        return x + 'px';
    }

    function handleResize() {
        var windowW = window.innerWidth,
            windowH = window.innerHeight,
            mastP = styleValue($mast, 'padding-top'),
            mastH = styleValue($mast, 'height'),
            mastTall = mastH + mastP * 2,
            mapM = styleValue($map, 'margin-top'),
            mapB = styleValue($map, 'border-top-width'),
            mapP = styleValue($map, 'padding'),
            mapTall = windowH - mastTall - (mapM + mapB + mapP) * 2,
            mapW = windowW - (mapM + mapB) * 2;

        $map.style({
            width: px(mapW),
            height: px(mapTall)
        });
    }

    function setVersion(v) {
        d3.select('#mast-version').text('Version ' + v);
    }

    function resetZoom() {
        zmap.zoom(1);
        zmap.center({lat: 0, lon: 0});
        setXZ('--', '--');
    }

    function toggleHeatMap() {
        var div = d3.select('#heatmap');
        if (div.empty()) {
            createHeatMapDiv();
        } else {
            div.remove();
        }
    }

    function heatmapClick() {
        var ox = d3.event.offsetX,
            oy = d3.event.offsetY,
            rp = cfg.regionPixel,
            rx = Math.floor(ox / rp),
            rz = Math.floor(oy / rp);

        console.log('click:', rx, rz);

        // TODO: figure out how to project region coords onto lat/lon

        // zmap.zoom(cfg.baseZoom + 2.49);
        // zmap.center({lat:0, lon:0});
    }

    function toggleHeatType() {
        var reg = $heatmapTitle.text().startsWith("Region"),
            title = reg ? "Chunk Heat Map" : "Region Heat Map",
            img = reg ? 'aux/chunkmap/chunk-latest.png' : 'aux/heatmap/heat-latest.png';
        
        $heatmapTitle.text(title);
        $heatmapImage.attr('src', img);
        console.log("Switching to ", title);
    }
    
    function createHeatMapDiv() {
        var w = cfg.heatMapDiv,
            h = w + 32,
            offset = (w - cfg.heatMap) / 2,
            div = d3.select('body')
                .append('div')
                .attr('id', 'heatmap')
                .style('width', px(w))
                .style('height', px(h))
                .classed('centered', true);

        $heatmapTitle = div.append('h2')
            .style('width', px(w))
            .text('Region Heat Map')
            .on('click', toggleHeatType);

        $heatmapImage = div.append('img')
            .attr('src', 'aux/heatmap/heat-latest.png')
            .attr('width', cfg.heatMap)
            .attr('height', cfg.heatMap)
            .style('top', px(offset + 4))
            .style('left', px(offset))
            .on('click', heatmapClick);

        return div;
    }

    var divisor = 0.015625;

    function setXZ(x, z) {
        function trim(a) {
            var as = String(a);
            var i = as.indexOf('.');
            return i < 0 ? as : as.substring(0, i);
        }
        $mcx.text(trim(x));
        $mcz.text(trim(z));
    }

    function updateMousePosition() {
        var xy = zmap.mouse(d3.event),
            ll = zmap.pointLocation(xy),
            lc = zmap.locationCoordinate(ll),
            zpow = Math.pow(2, 9-lc.zoom),
            off = 256 / zpow,
            mcx = ((lc.column - off) / divisor) * zpow,
            mcz = ((lc.row - off) / divisor) * zpow;
        setXZ(mcx, mcz);
    }
    
    function initEventHandlers() {
        // show/hide the heat map
        d3.select('#heatmap-btn').on('click', toggleHeatMap);
        d3.select('#reset-zoom-btn').on('click', resetZoom);

        // handle window resize events...
        d3.select(window).on('resize', handleResize);
        handleResize();
        
        $map.on('mousemove', updateMousePosition);
    }

    function initZoomableMap() {
        var tileUrl = 'tiles/z{Z}/x{X}/t.{Y}.png',
            po = org.polymaps,
        // old style reference (not D3) since that is what polymaps wants...
            svg = document.getElementById('map').appendChild(po.svg('svg'));

        zmap = po.map()
            .container(svg)
            .center({'lat': 0.0, 'lon': 0.0})
            .zoom(1)
            .zoomRange([1, cfg.zoomLimit])
            .tileSize({x: 256, y: 256})
            .add(po.image().url(po.url(tileUrl)))
            .add(po.interact())
            .add(po.hash());
    }

    function init() {
        $mast = d3.select('#mast');
        $map = d3.select('#map');
        
        $mcx = d3.select('#mast-coords').select('i.mcx');
        $mcz = d3.select('#mast-coords').select('i.mcz');
        cfg = McQuad.cfg;

        setVersion(cfg.version);
        initZoomableMap();
        initEventHandlers();
    }

    McQuad.api = {
        init: init
    };
}());
