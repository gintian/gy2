//>>built
define("dojox/sketch/Toolbar","dojo/_base/kernel,dojo/_base/lang,dojo/_base/declare,./Annotation,dijit/Toolbar,dijit/form/Button".split(","),function(c){c.getObject("sketch",!0,dojox);c.declare("dojox.sketch.ButtonGroup",null,{constructor:function(){this._childMaps={};this._children=[]},add:function(a){this._childMaps[a]=a.connect(a,"onActivate",c.hitch(this,"_resetGroup",a));this._children.push(a)},_resetGroup:function(a){c.forEach(this._children,function(b){a!=b&&b.attr&&b.attr("checked",!1)})}});
c.declare("dojox.sketch.Toolbar",dijit.Toolbar,{figure:null,plugins:null,postCreate:function(){this.inherited(arguments);this.shapeGroup=new dojox.sketch.ButtonGroup;if(!this.plugins)this.plugins="Lead,SingleArrow,DoubleArrow,Underline,Preexisting,Slider".split(",");this._plugins=[];c.forEach(this.plugins,function(a){var b=c.isString(a)?a:a.name,a=new dojox.sketch.tools[b](a.args||{});this._plugins.push(a);a.setToolbar(this);if(!this._defaultTool&&a.button)this._defaultTool=a},this)},setFigure:function(a){this.figure=
a;this.connect(a,"onLoad","reset");c.forEach(this._plugins,function(b){b.setFigure(a)})},destroy:function(){c.forEach(this._plugins,function(a){a.destroy()});this.inherited(arguments);delete this._defaultTool;delete this._plugins},addGroupItem:function(a,b){"toolsGroup"!=b?console.error("not supported group "+b):this.shapeGroup.add(a)},reset:function(){this._defaultTool.activate()},_setShape:function(a){if(this.figure.surface&&this.figure.hasSelections())for(var b=0;b<this.figure.selected.length;b++){var c=
this.figure.selected[b].serialize();this.figure.convert(this.figure.selected[b],a);this.figure.history.add(dojox.sketch.CommandTypes.Convert,this.figure.selected[b],c)}}});dojox.sketch.makeToolbar=function(a,b){var c=new dojox.sketch.Toolbar;c.setFigure(b);a.appendChild(c.domNode);return c};return dojox.sketch.Toolbar});