<%@page import="java.util.Map"%>
<%@ page import="java.util.HashMap"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<%
String showQueryButton = request.getParameter("showQueryButton");//tianye add showQueryButton代表是从业务平台查看关系图需要提供查询功能 自助平台下我的团队关系图不提供查询功能（人员定位）
%>
<style>
.transparent_class {  
      filter:alpha(opacity=75);  
      -moz-opacity:0.75;  
      -khtml-opacity: 0.75;  
      opacity: 0.75;  
}   
</style>
<hrms:themes />
<style type="text/css">
  @import "../../../DOJO/dojo/resources/dojo.css";
  @import "../../../DOJO/dijit/themes/claro/claro.css";
  @import "../../../resources/style/yfiles-ajax.css";
</style>
<script language="javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/powerCharts/jquery.min.js"></script>
<script language="JavaScript" src="/powerCharts/FusionCharts.js"></script> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script>
    var dojoConfig = {
      isDebug: false,
      baseUrl: "../../../DOJO/",
      tlmSiblingOfDojo: false,
      packages: [
        { name: "dojo", location: "dojo" },
        { name: "dijit", location: "dijit" },
        { name: "dojox", location: "dojox" },
        { name: "chartstyle", location: "chartstyle", main: "chartstyle" },
        { name: "yfiles", location: "yfiles", main: "yfiles" }
      ]
    };
    
  </script>
  <script data-dojo-config="async: true" src="../../../DOJO/dojo/dojo.js"></script>
  <script src="../../../DOJO/dojo/yfiles-ajax.js"></script>
  <body class="claro yfiles" style="overflow: hidden;">
  
  <html:form action="/general/sprelationmap/show_report_map">
   		<div style="position: absolute;top:10px;">
		   	 <table id="buttonTable" >
		   	   <tr >
		   	      <td valign="top"  nowrap="nowrap">&nbsp;&nbsp;
		   	      <%if("showQueryButton".equals(showQueryButton)){ %>
					  <%--在谷歌下会显示之前输入过的值 用autocomplete属性禁调改功能 wangbs 20190315--%>
<input type="text" id="queryTreePersonName" autocomplete="off" name="a0101" class="inputtext" style="height:23px;" value="" size="atuo"  onkeyup="showDataSelectBoxBefore('queryTreePersonName');" />
<!-- tianye add 查询按钮 根据需求以后可以考虑它是否需要提供 不需要可以进行注释 功能已完成 -->
<button class="mybutton" type="button"  onclick="queryTreePerson()" style="line-height: 20px"><bean:message key="button.query"/></button>
<%} %>
		   	         <button class="mybutton" type="button" style="line-height: 20px"  onclick="exportGraph()" style=""><bean:message key="pos.report.relations.export"/></button>
		   	         <button class="mybutton" type="button" id="smaller" style="width: 30px;line-height: 20px" title="缩小">-</button>
	   	         <button class="mybutton" type="button" id="bigger" style="width: 30px;line-height: 20px" title="放大">+</button>
	   	         <button class="mybutton" type="button" id="reZoom" style="line-height: 20px" title="初始化缩放级别">1:1</button>
		   	      </td>
		   	   </tr>
		   	 </table>
	
	   	 </div>
         <div data-dojo-type="dijit/TitlePane" title="缩略图" id="preview"
            data-dojo-props="region:'top',showTitle:true,toggleable:true,open:false"
            style="position:absolute; top:10px; right:10px; width: 320px; padding: 0,0,0,0px; z-index:100;">
	            
	          <div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="overview" style="height:150px;width:295px;"
	               data-dojo-props="tileSize:300,baseURL:'../../..'">
	                <div data-dojo-type="yfiles/client/tiles/widget/ViewPortMarker"
	                     data-dojo-props="canvasId:'canvas',overviewId:'overview'" style="filter：alpha(opacity=0)">
	                </div>
	          </div>
          </div>
  	
   	 <table  width="100%" height="100%" border=0>
   	   <tr>
   	     <td height="40" >&nbsp;</td>
   	   </tr>
   	   <tr>
   	      <td>
   	           <!-- data-dojo-type 是div关联那个js对象，相当于插件。  -->
		      <div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="canvas"  maxZoomForFitContent="1.0" 
		           data-dojo-props="nodeEvents:true,nodeLabelEvents:true,baseURL:'../../..'" style="height: 100%;width: 100%;">
		     </div>
   	      </td>
   	   </tr>
   	 </table>
   	 <div id="loading" style="display: none;z-index:10;position: absolute;">
       <img src="../../../resources/style/loading.gif"></img>
     </div>
     <!-- 点击按钮后台操作时用于遮挡页面防止重复操作 -->
   	 <div id="coverDiv" style="position:absolute;display:none;background:black; filter:alpha(opacity:00);opacity:0;top:0px;left:0px; width:100%;height:100%;z-index: 100;"></div>
   	 <!-- 信息提示框 -->
     <div id="hintInfoBox" onmouseover="javascript:this.style.display='none';" style="display:none;position: absolute;padding:5 10 5 10;z-index:20;border:1px D1D1D1 solid;background-color: EDEDED" class=" transparent_class">
     </div>
     
     <div id="date_panel" style="display:none; z-index:10" onmouseleave="remove();">
	 <%--onmouseout时间会冒泡，当鼠标移入其子元素时也触发改事件，在谷歌浏览器下就会显示不正常
	 同样的在谷歌下要提高div的显示优先级才能正常显示div触发remove()  wangbs 20190315--%>
     <%--<div id="date_panel" style="display:none; z-index:2" onmouseout="remove();">--%>
		<select id="date_box" name="contenttype" multiple="multiple"  style="" size="10"  ondblclick="setSelectPerson();">
        </select>
</div>

</html:form>
  <script>
  if(parent.frames['mil_menu'])
  	parent.frames['mil_menu'].graphselectid = undefined;
  
  require(//需要什么就加什么，require会自动加载这些js   最前面的dojo、dijit、yfiles是路径变量，具体值看上边dojoConfig的定义
	        [ "dojo/ready",
	          "dojo/parser",
	          "dojo/mouse",
	          "dojo/on","dojo/dom","dijit/registry",
	          "dojo/request/xhr","dojo/_base/lang",
	          "dijit/TitlePane","dijit/layout/ContentPane",
	          "yfiles/client/tiles/GraphSelection",
	          "chartstyle/GraphHighlighter",
	          "yfiles/client/tiles/widget/GraphCanvas",
	          "yfiles/client/tiles/widget/ViewPortMarker"],
	          //想要使用上边加载的js对象，必须将对象传进function里，如果下面不写，那就用不到了
	        function(ready, parser,mouse, on, dom, registry,xhr,lang,TitlePane,ContentPane,GraphSelection,GraphHighlighter) {
	        	//画布对象 （不是htmlElement元素，是js对象）
	        	var canvas;
	        	//节点选择对象
	        	var selection;
	        	//是否重制缩放级别为1：1标示。只有在第一次加载的时候才是true，以后都为false
	        	var zoomflag=true;
	        	//后台graph对象的长宽，用于输出
	        	var graphWidth,graphHeight;
	        	//后台graph对象的中心坐标，用于初始定位
	        	var center;
	        	var hintInfo;
	        	ready(function() {
	                parser.parse();
	                initButton();
	                initGraph();

	                // 加载时，更换 div id=preview_titleBarNode 的 背景颜色 
	                dojo.addClass("preview_titleBarNode","common_background_color");
	              });
	  
	        	
	        	var initButton = function(){
	        		on(dom.byId('bigger'),'click',decreaseCanvas);
		         	  on(dom.byId('smaller'),'click',increasesCanvas);
		         	  on(dom.byId('reZoom'),'click',function(){
		         		  dom.byId('reZoom').disabled = 'true';
		         		  if(graphWidth>600 || graphHeight>600){
		         			  canvas.setZoom(1.0);
		         			  dom.byId('reZoom').disabled = false;
		         		  }else{
		         			  zoomflag=true;
		         			  canvas.setPath('ReportMapTree');
		         			 dom.byId('reZoom').disabled = false;
		         		  }
		         	  });
	        	};
	        	
	           var initGraph = function(){
	        	  canvas = registry.byId('canvas');
	        	  
	        	  canvas.mouseWheelZoom= true;
	        	  canvas._mouseWheeled = mouseWheel;
	        	  on(canvas,"clickNodeLabel",clickLabel);
	        	  on(canvas,"DblClickNodeLabel",dblClickLabel);
	        	  on(canvas,"MouseOverNodeLabel",showHintInfo);
	              on(canvas,"MouseOutNodeLabel",hideHintInfo);
	        	  
	        	  var overview = registry.byId("overview");
	              overview.setNoInteractionMode();
	              var preview = registry.byId("preview");
	              preview.on("show",onShowNavigation);
	              
	            //为画布设置节点选中样式对象
	              canvas.setHighlighter(new GraphHighlighter());
	              selection = new GraphSelection(true, false);
	              
	              /**  on 对象是用来关联事件的          
	  		             当A对象执行类方法a() 时想一起执行 一个全局方法  another()时：on(A,"a",another);  
	  		             当A对象执行类方法a() 时想一起执行 B对象的类方法b()时：on(A,"a",lang.hitch(B, "b"));        
	               lang.hitch 是用来关联另一个对象的某个方法用的
	              **/
	            //当selection对象的addNode方法执行时，执行canvas对象的highlight方法
	              on(selection, "AddNode", lang.hitch(canvas, "highlight"));
	              on(selection, "RemoveNode", lang.hitch(canvas, "unhighlight"));
	              
	              
	        	   xhr.post('/servlet/org/reportmap/reportMapServlet/initializeGraph',{handleAs: 'json'}).then(function(boundsAndShift, ioargs) {
	                     canvas.setPath('ReportMapTree');
	                     graphWidth = boundsAndShift.bounds.maxX;
	         	         graphHeight = boundsAndShift.bounds.maxY;
	                     
	                     canvas.onLoadTiles = function(){
	                    	 if(zoomflag){canvas.zoom=1;}
	         	         };
	         	         //canvas.tileLoaded=function(){alert();};
	         	         on(canvas, "TilesLoaded", onTilesLoaded);
	         	         hintInfo = boundsAndShift.hintInfo;
	                   }
	               );
	        	   
	           };
	           
	           var onTilesLoaded = function(){
	         	  if(zoomflag){
	         		  //初次加载后设置成false，以后都不执行。否则将每一步操作都将执行canvas.onLoadTiles，放大缩小移动功能就失效了
	         		  zoomflag= false;
	         	   }
	         	 loadingStage('hidden');
	           };
	           
	           
	           var onShowNavigation = function(){
	         	  registry.byId('overview').setPath('ReportMapTree');
	           };
	           
	           var clickLabel = function(labelId){
	        	   var info = canvas.getHitTest().getLabelInfo(labelId);
	         	  if(info && info.labelIndex == 1) {
	         		 toggleNode(info.mainElementId);
	         	  }
	         	  else{
	         		  adjustSelection(info.mainElementId);
	         	  }
	           };
	           
	           var adjustSelection = function(node) {
	         	  var selectedNodes = selection.getNodes();
	               if (selectedNodes.length != 1
	                 || (selectedNodes[0] != node)) {
	                 
	                 var desc = hintInfo[node];
	                 if(desc){
	                	  selection.clear();
		                  selection.add(node);
		                  if(parent.frames['mil_menu'])
	                         parent.frames['mil_menu'].graphselectid = desc[1];
	                 }else{
	                	 if(parent.frames['mil_menu'])
	                	 	parent.frames['mil_menu'].graphselectid = desc;
	                 }
	               }
	             };
	           
	         //展开节点操作
	           var toggleNode = function(nodeId) { 
	        	   loadingStage('show');
	         	    dojo.xhrPost({
	         	      url:'/servlet/org/reportmap/reportMapServlet/toggleNode',
	         	      content : { nodeId : nodeId},
	         	      load: function(boundsAndShift, ioargs) {
	         	    	  if('re' == boundsAndShift.state){
	         	    		  loadingStage('hidden');
	         	    		  return;
	         	    	  }
	         	    	  
	         	    	 graphWidth = boundsAndShift.bounds.maxX;
	         	         graphHeight = boundsAndShift.bounds.maxY;
	         	    	  //后台更新graph对象后，这里刷新画布，bounds和shift是用来定位使画面刷新后不移动的
	         	        canvas.refresh(boundsAndShift.bounds, boundsAndShift.shift);
	         	        registry.byId('overview').setPath('ReportMapTree');
	         	       for(var o in boundsAndShift.hintInfo){
	         	    	   hintInfo[o] = boundsAndShift.hintInfo[o];
	         	       }
	         	      },
	         	      handleAs: 'json'
	         	    });
	           };
	           
	           
	           var dblClickLabel = function(labelId){
	         	  var info = canvas.getHitTest().getLabelInfo(labelId);
	         	  if(info && info.labelIndex > 1)
	         	     showOrgInfo(info.mainElementId);
	           };
	           var showOrgInfo = function(nodeId){
	        	   var desc = hintInfo[nodeId];
	        	   if(desc){
	        		   var objid = desc[1];
	        		   var url;
	         	    	  if(objid.indexOf('@K') > -1){
	         	    		  url = '/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id='+objid.substr(2)+'&infokind=3&dbname=';
	         	    		 showModalDialog(url,'','dialogHeight:800;dialogWidth:600;center:yes;help:no;resizable:no;status:no;');
	         	    	  }else if(objid.indexOf('UN') > -1 || objid.indexOf('UM') > -1){
	         	        	  url = '/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id='+objid.substr(2)+'&infokind=2&dbname=';
	         	        	 showModalDialog(url,'','dialogHeight:800;dialogWidth:600;center:yes;help:no;resizable:no;status:no;');
	         	    	  }else{
	         	        	 xhr.post('/servlet/org/reportmap/reportMapServlet/getCodeitemid',{
	         	        		 data:{
	         	        			 nodeId:nodeId
	         	        		 }
	         	        	 }).then(function(result){
	         	        		 if(!result)
	         	        			 return;
	         	        		 url = '/workbench/browse/showselfinfo.do?b_search=link&personid='+result.split("`")[1]+'&flag=notself&returnvalue=1000000&dbname='+result.split("`")[0];
	         	        		 showModalDialog(url,'','dialogHeight:800;dialogWidth:600;center:yes;help:no;resizable:no;status:no;');
	         	        		 
	         	        	 });
	         	          }
	        	   }
	        		   
	           };
	           
	           var increasesCanvas = function(){
	         	  if((graphWidth<600 && graphHeight<600) && canvas.zoom<=1)
	         		  return;
	         	  var zoom = canvas.zoom;
	         	  canvas.increaseZoom(0.7);
	         	  if(zoom == canvas.zoom && zoom>1){
	         		  zoomflag=true;
	 			      canvas.setPath('ReportMapTree');
	         	  }
	           };
	           
	           var decreaseCanvas = function(){
	         	canvas.decreaseZoom(0.7);
	           };
	           
	           
	           var showHintInfo = function(id,hitinfo,event){
	        	   var info = canvas.getHitTest().getLabelInfo(id);
	          	 
	          	 
	           	  var desc = hintInfo[info.mainElementId];
	           	  if(!desc)return;
	           	  var items = desc[0].split("`");
	           	  var html = "<table>";
	           	  for(var i=0;i<items.length;i++){
	           		  var arr = items[i].split(":");
	           		  if(arr.length<2)
	           			  continue;
	           		//<td align='right' style=\"border-bottom:1px gray dashed;border-right:1px gray dashed;padding-right:5px\" nowrap>"+arr[0]+"</td>
	           		  html+="<tr><td style=\"border-bottom:1px gray dashed;\" nowrap>"+arr[1]+"</td></tr>";
	           	  }
	                html+="</table>";
	           	 dom.byId("hintInfoBox").innerHTML=html;
	           	 
	           	 var nodeBounds = canvas.getHitTest().getBoundsForId(info.mainElementId);
	           	var x = Math.round(nodeBounds.minX * canvas.zoom);
	            var y = Math.round(nodeBounds.minY * canvas.zoom);
	            var w = Math.round(nodeBounds.width() * canvas.zoom);
	            var h = Math.round(nodeBounds.height() * canvas.zoom);

	            var left = (x+w);
	            var top = (y+h/2);

	            dojo.style(dom.byId("hintInfoBox"), {
	              "left" : left + "px",
	              "top" : top + "px",
	              "display" : "block",
	              "position" : "absolute"
	            });
	        	   
	            canvas.overlays.appendChild(dom.byId("hintInfoBox"));
	           };
	           var hideHintInfo = function(){
	        	   dom.byId("hintInfoBox").style.display="none";
	           };
	           
	           var mouseWheel = function(a){
	         	  dojo.stopEvent(a);
	           	if(this.mouseWheelZoom){
	           		var b=0;
	           		if(dojo.isFF)
	           		   b=-a.detail;
	           		else                          
	           		if("number"==typeof a.wheelDelta)
	           		   b=a.wheelDelta;b=0<b;

	           		if(!b && (graphWidth<600 && graphHeight<600) && this.zoom<=1){
	           			return;
	           		}
	           		
	           		if(this.zoomToPointRecognizer(a)){
	           			if(b=b?this.zoom*this.mouseWheelScrollFactor:this.zoom/this.mouseWheelScrollFactor,b=this._coerceZoom(b),b!=this.zoom){
	           				var a=this.toLocal(a.pageX,a.pageY),c=this.getWorldCoordinates(a.x,a.y);
	           				this.zoomTo(this.viewportLimiter.limitViewport(this,{x:c.x-a.x/b,y:c.y-a.y/b,width:this.width/b,height:this.height/b}))
	           			}
	           		}
	           		else 
	           		b?this.increaseZoom(this.mouseWheelScrollFactor):this.decreaseZoom(this.mouseWheelScrollFactor)
	           	}
	           };
  });
               var exportGraph = function(){
	         	  window.location.href="/servlet/org/reportmap/reportMapServlet/download";
	           };
	           
	           function loadingStage(type){
	         	  var loading = document.getElementById('loading');
	         	  var canvasobj = document.getElementById('canvas');
	         	  if(type=='show'){
	         		  loading.style.top=canvasobj.offsetHeight/2;
	         		  loading.style.left=canvasobj.offsetWidth/2;
	         		  loading.style.display='block';
	         		     //显示透明覆盖层
	         		  document.getElementById('coverDiv').style.display='block';
	         	  }else{
	         		  loading.style.display='none';
	         		  //隐藏 覆盖层
	         		  document.getElementById('coverDiv').style.display='none';
	         	  }
	           }
  </script>
    </body>