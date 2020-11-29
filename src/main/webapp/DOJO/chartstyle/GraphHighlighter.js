/****************************************************************************
 **
 ** This file is part of yFiles AJAX 2.1.
 **
 ** yWorks proprietary/confidential. Use is subject to license terms.
 **
 ** Unauthorized redistribution of this file and reverse engineering are
 ** strictly forbidden.
 **
 ** Copyright (c) 2006 - 2013 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ***************************************************************************/
define(["dojo/_base/declare","yfiles/client/tiles/GraphHighlighter","dojox/gfx"],
  function(declare,GraphHighlighter,gfx) {
    return declare(null, {

  _defaultHighlighter : null,

  constructor : function() {
    this._defaultHighlighter = new GraphHighlighter();
  },

  drawNodeHighlight : function( canvas, highlightDiv, nodeBounds ) {

    var x = Math.round(nodeBounds.minX * canvas.zoom);
    var y = Math.round(nodeBounds.minY * canvas.zoom);
    var w = Math.round(nodeBounds.width() * canvas.zoom);
    var h = Math.round(nodeBounds.height() * canvas.zoom);

    var border = 3;
    var padding = 3;

    var left = (x - border - padding);
    var top = (y - border - padding);
    var width = w + padding*2+border*2;
    var height = h + padding*2+border*2;

    dojo.style(highlightDiv, {
      "left" : left + "px",
      "top" : top + "px",
      "width" : width + "px",
      "height" : height + "px",
      "display" : "block",
      "position" : "absolute"
    });

    if( dojo.isIE == 6 ) {
      // using a css border doesn't work in IE6
      // - use dojo.gfx to draw the selection border.
      dojo.style( highlightDiv, "margin", border + "px" );
      var surface = gfx.createSurface(highlightDiv, w+border*2, h+border*2);
      var rect3 = surface.createRect({x: border-2, y: border-2,width: width-(border+2)*2, height: height-(border+2)*2});
      rect3.setStroke( { color: "#ff6600", width: border } );
    } else {
      dojo.style( highlightDiv, "border", border + "px solid orange" );
    }

    var bgDiv = dojo.doc.createElement('div');
    dojo.style(bgDiv, {
      "opacity" : 0.1,
      "display" : "block",
      "position" : "absolute",
      "left" :padding + "px",
      "top" : padding + "px",
      "width" : w + "px",
      "height" : h  + "px",
      "backgroundColor" : "white"
    });
    highlightDiv.appendChild( bgDiv );

  },

  drawEdgeHighlight : function( canvas, parentDiv, controlPointIndex, controlPoints ) {
    this._defaultHighlighter.drawEdgeHighlight( parentDiv, controlPointIndex, controlPoints );
  }

});
});