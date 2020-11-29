//>>built
define("dojox/fx/style","dojo/_base/kernel,dojo/_base/lang,dojo/_base/fx,dojo/fx,./_base,dojo/_base/array,dojo/dom,dojo/dom-style,dojo/dom-class,dojo/_base/connect".split(","),function(i,j,n,k,g,o,l,t,m,p){i.experimental("dojox.fx.style");var q=function(a){return o.map(g._allowedProperties,function(b){return a[b]})},s=function(a,b,c){var a=l.byId(a),d=t.getComputedStyle(a),f=q(d);i[c?"addClass":"removeClass"](a,b);var e=q(d);i[c?"removeClass":"addClass"](a,b);var r={},h=0;o.forEach(g._allowedProperties,
function(a){f[h]!=e[h]&&(r[a]=parseInt(e[h]));h++});return r},k={addClass:function(a,b,c){var a=l.byId(a),d=function(a){return function(){m.add(a,b);a.style.cssText=e}}(a),f=s(a,b,!0),e=a.style.cssText,a=n.animateProperty(j.mixin({node:a,properties:f},c));p.connect(a,"onEnd",a,d);return a},removeClass:function(a,b,c){var a=l.byId(a),d=function(a){return function(){m.remove(a,b);a.style.cssText=e}}(a),f=s(a,b),e=a.style.cssText,a=n.animateProperty(j.mixin({node:a,properties:f},c));p.connect(a,"onEnd",
a,d);return a},toggleClass:function(a,b,c,d){"undefined"==typeof c&&(c=!m.contains(a,b));return g[c?"addClass":"removeClass"](a,b,d)},_allowedProperties:"width,height,left,top,backgroundColor,color,borderBottomWidth,borderTopWidth,borderLeftWidth,borderRightWidth,paddingLeft,paddingRight,paddingTop,paddingBottom,marginLeft,marginTop,marginRight,marginBottom,lineHeight,letterSpacing,fontSize".split(",")};j.mixin(g,k);return k});