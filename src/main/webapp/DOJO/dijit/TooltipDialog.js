//>>built
require({cache:{"url:dijit/templates/TooltipDialog.html":'<div role="presentation" tabIndex="-1">\n\t<div class="dijitTooltipContainer" role="presentation">\n\t\t<div class ="dijitTooltipContents dijitTooltipFocusNode" data-dojo-attach-point="containerNode" role="dialog"></div>\n\t</div>\n\t<div class="dijitTooltipConnector" role="presentation" data-dojo-attach-point="connectorNode"></div>\n</div>\n'}});
define("dijit/TooltipDialog","dojo/_base/declare,dojo/dom-class,dojo/_base/event,dojo/keys,dojo/_base/lang,./focus,./layout/ContentPane,./_DialogMixin,./form/_FormMixin,./_TemplatedMixin,dojo/text!./templates/TooltipDialog.html,./main".split(","),function(g,h,d,c,n,e,i,j,k,l,m){return g("dijit.TooltipDialog",[i,l,k,j],{title:"",doLayout:!1,autofocus:!0,baseClass:"dijitTooltipDialog",_firstFocusItem:null,_lastFocusItem:null,templateString:m,_setTitleAttr:function(a){this.containerNode.title=a;this._set("title",
a)},postCreate:function(){this.inherited(arguments);this.connect(this.containerNode,"onkeypress","_onKey")},orient:function(a,b,c){a={"MR-ML":"dijitTooltipRight","ML-MR":"dijitTooltipLeft","TM-BM":"dijitTooltipAbove","BM-TM":"dijitTooltipBelow","BL-TL":"dijitTooltipBelow dijitTooltipABLeft","TL-BL":"dijitTooltipAbove dijitTooltipABLeft","BR-TR":"dijitTooltipBelow dijitTooltipABRight","TR-BR":"dijitTooltipAbove dijitTooltipABRight","BR-BL":"dijitTooltipRight","BL-BR":"dijitTooltipLeft"}[b+"-"+c];h.replace(this.domNode,
a,this._currentOrientClass||"");this._currentOrientClass=a},focus:function(){this._getFocusItems(this.containerNode);e.focus(this._firstFocusItem)},onOpen:function(a){this.orient(this.domNode,a.aroundCorner,a.corner);var b=a.aroundNodePos;if("M"==a.corner.charAt(0)&&"M"==a.aroundCorner.charAt(0))this.connectorNode.style.top=b.y+(b.h-this.connectorNode.offsetHeight>>1)-a.y+"px",this.connectorNode.style.left="";else if("M"==a.corner.charAt(1)&&"M"==a.aroundCorner.charAt(1))this.connectorNode.style.left=
b.x+(b.w-this.connectorNode.offsetWidth>>1)-a.x+"px";this._onShow()},onClose:function(){this.onHide()},_onKey:function(a){var b=a.target;a.charOrCode===c.TAB&&this._getFocusItems(this.containerNode);var f=this._firstFocusItem==this._lastFocusItem;a.charOrCode==c.ESCAPE?(this.defer("onCancel"),d.stop(a)):b==this._firstFocusItem&&a.shiftKey&&a.charOrCode===c.TAB?(f||e.focus(this._lastFocusItem),d.stop(a)):b==this._lastFocusItem&&a.charOrCode===c.TAB&&!a.shiftKey?(f||e.focus(this._firstFocusItem),d.stop(a)):
a.charOrCode===c.TAB&&a.stopPropagation()}})});