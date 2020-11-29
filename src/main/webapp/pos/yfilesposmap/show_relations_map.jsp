<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<hrms:themes />
<link rel="stylesheet" type="text/css" href="../../DOJO/dojo/resources/dojo.css"></link>
<link rel="stylesheet" type="text/css" href="../../DOJO/dijit/themes/claro/claro.css"></link>
<link rel="stylesheet" type="text/css" href="../../resources/style/yfiles-ajax.css"></link>
<style>
.transparent_class {  
      filter:alpha(opacity=75);  
      -moz-opacity:0.75;  
      -khtml-opacity: 0.75;  
      opacity: 0.75;  
}   
</style>
<script>
    var dojoConfig = {
      isDebug: false,
      baseUrl: "../../DOJO/",
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
  <script data-dojo-config="async: true" src="../../DOJO/dojo/dojo.js"></script>
  <script src="../../DOJO/dojo/yfiles-ajax.js"></script>
  
<body class="claro yfiles">
   <html:form action="/general/inform/org/map/showorgmap.do"> 
   <a id="aid" target="_blank" style="display:none"></a>
   	 <html:hidden name="orgMapForm" property="code" styleId="code"/>
   	 <html:hidden name="orgMapForm" property="constant" styleId="constant"/>
   	 <html:hidden name="orgMapForm" property="report_relations" styleId="report_relations"/>
     <div style="position: absolute;top:10px;z-index:1;">
	   	 <table id="buttonTable" border=0>
	   	   <tr>
	   	      <td nowrap="nowrap" valign="bottom">
	   	      &nbsp;&nbsp;
	   	         <button class="mybutton" type="button" onclick="setOptions();"><bean:message key="button.orgmapset"/></button>
	   	         <button class="mybutton" type="button" id="exportB"><bean:message key="pos.report.relations.export"/></button>
	   	         <button class="mybutton" type="button" id="expandAll">展开所有下级</button>
	   	         <button class="mybutton" type="button" id="smaller" style="width: 30px;" title="缩小">-</button>
	   	         <button class="mybutton" type="button" id="bigger" style="width: 30px;" title="放大">+</button>
	   	         <button class="mybutton" type="button" id="reZoom" title="初始化缩放级别">1:1</button>
	   	         <logic:equal value="dxt" name="orgMapForm" property="returnvalue">
                   <input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="hrbreturn('org','2','orgMapForm');"> 
                 </logic:equal>
	   	      </td>
	   	   </tr>
	   	 </table>
   	    
   	 </div>
   	  <div data-dojo-type="dijit/TitlePane" title="缩略图" id="preview"
                data-dojo-props="region:'top',showTitle:true,toggleable:true,open:false"
                style="position:absolute; top:10px; right:10px; width: 320px; padding: 0,0,0,0px; z-index:100;">
                
                  <div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="overview" style="height:150px;width:295px;"
                       data-dojo-props="tileSize:300,baseURL:'../../../..'">
                        <div data-dojo-type="yfiles/client/tiles/widget/ViewPortMarker"
                             data-dojo-props="canvasId:'canvas',overviewId:'overview'" style="filter：alpha(opacity=0)"></div>
          </div>
     </div>
   	 
<table width="100%" height="100%" border=0>
       <tr>
   	     <td height="40">&nbsp;</td>
   	   </tr>
   	   <tr>
   	      <td>
		      <div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="canvas" 
		           data-dojo-props="nodeEvents:true,nodeLabelEvents:true,baseURL:'../..'" style="height: 100%;width: 100%;">
		     </div>
   	      </td>
   	   </tr>
   	 </table>
   	 <div id="loading" style="display: none;z-index:10;position: absolute;">
       <img src="../../../../resources/style/loading.gif"></img>
     </div>
     
     <div id="posUpInfo" onmouseover="javascript:this.style.display='none';" style="display:none;position: absolute;padding:5 10 5 10;z-index:20;border:1px D1D1D1 solid;background-color: EDEDED" class=" transparent_class">
     </div>
</html:form>

</body>
  <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
  <script>
    require([ "dojo/ready",
	          "dojo/parser",
	          "dojo/on","dojo/dom","dijit/registry",
	          "dojo/request/xhr","dojo/_base/lang",
	          "dijit/TitlePane","dijit/layout/ContentPane",
	          "yfiles/client/tiles/GraphSelection",
	          "chartstyle/GraphHighlighter",
	          "yfiles/client/tiles/widget/GraphCanvas",
	          "yfiles/client/tiles/widget/ViewPortMarker"],
	          function(ready,parser,on,dom,registry,xhr,lang,TitlePane,ContentPane,GraphSelection,GraphHighlighter){
    	
    	var canvas,selection;
    	var zoomflag=true;
    	var graphWidth,graphHeight;
    	var center;
    	var posUpJson;
    	var initGraph = function(){
    		
    		canvas = registry.byId('canvas');
            canvas.paintDetailThreshold = 3;
            canvas.zoomToPointRecognizer = function(evt) { return !evt.ctrlKey; };
            
            canvas._mouseWheeled = mouseWheel;
            
            var overview = registry.byId("overview");
            overview.setNoInteractionMode();
            
            
            on(canvas,"clickNodeLabel",clickLabel);
            on(canvas,"DblClickNodeLabel",dblClickLabel);
            on(canvas,"MouseOverNodeLabel",showPosUpInfo);
            on(canvas,"MouseOutNodeLabel",hidePosUpInfo);
            
			var preview = registry.byId("preview");
            
            
            preview.on("show",onShowNavigation);
            
            
            
            canvas.setHighlighter(new GraphHighlighter());
            selection = new GraphSelection(true, false);
            on(selection, "AddNode", lang.hitch(canvas, "highlight"));
            on(selection, "RemoveNode", lang.hitch(canvas, "unhighlight"));
            
    		dojo.xhrPost({
    		      url : '/servlet/duty/relation/getPosRelations/initializeGraph',
    		      content : {
    	                       code:dom.byId("code").value,
    	                       constant:dom.byId("constant").value,
    	                       cwidth:dom.byId('canvas').offsetWidth,
    	                    	cheight:dom.byId('canvas').offsetHeight
    		                },
    		    	          
    		      load: function(boundsAndShift, ioargs) {
    		        canvas.setPath('PosRelationMap');
    		        center = boundsAndShift.shift;
    		        graphWidth = boundsAndShift.bounds.maxX;
        	        graphHeight = boundsAndShift.bounds.maxY;
        	        posUpJson = boundsAndShift.posUpJson;
        	      //当节点将要加载之前执行的方法
  	      	      canvas.onLoadTiles = function(){
        	        	 //zoom：设置初始比例。setCenter：设置中心点，可自定义
        	        	  if(zoomflag){canvas.zoom=1;canvas.setCenter(center);} 
        	        	  };
  	      	      //当节点加载完毕后执行的方法
  	      	      canvas.onTilesLoaded = onTilesLoaded;
    		      },
    		      handleAs: 'json'
    		    });
    	};
    	
    	var onTilesLoaded = function(){
      	  if(zoomflag){
      		  //初次加载后设置成false，以后都不执行。否则将每一步操作都将执行canvas.onLoadTiles，放大缩小移动功能就失效了
      		  zoomflag= false;
      		    //展开root节点
      		    loadingStage('show');
      			  toggleNode("","single");
      	   }
      	  
      	   loadingStage('hidden');
        };
        
        
        var adjustSelection = function(node) {
      	  var selectedNodes = selection.getNodes();
            if (selectedNodes.length != 1
              || (selectedNodes[0] != node)) {
              selection.clear();
              selection.add(node);
            }
          };
        
        
        var clickLabel = function(labelId){
      	  
      	  var info = canvas.getHitTest().getLabelInfo(labelId);
      	  
      	  if(info && info.labelIndex == 1) {
      		 toggleNode(info.mainElementId,'single');
      	  }else{
      		  adjustSelection(info.mainElementId);
      	  }

        };
    	
        var dblClickLabel = function(labelId){
      	  var info = canvas.getHitTest().getLabelInfo(labelId);
      	  if(info && info.labelIndex > 1)
      	     showOrgInfo(info.mainElementId);
        };
        
        
    	//展开节点操作
        var toggleNode = function(nodeId,expandflag) {
    		
        	loadingStage('show',expandflag);
        	
      	    dojo.xhrPost({
      	      url:'/servlet/duty/relation/getPosRelations/toggleNode',
      	      content : { id : nodeId,expandflag:expandflag },
      	      load: function(boundsAndShift, ioargs) {
      	    	  //后台更新graph对象后，这里刷新画布，bounds和shift是用来定位使画面刷新后不移动的
      	        canvas.refresh(boundsAndShift.bounds, boundsAndShift.shift);
      	        registry.byId('overview').setPath('PosRelationMap');
      	        graphWidth = boundsAndShift.bounds.maxX;
      	        graphHeight = boundsAndShift.bounds.maxY;
      	        posUpJson = boundsAndShift.posUpJson;
      	      },
      	      handleAs: 'json'
      	    });
        };
    	
    	
    	var initButton = function(){
      	  on(dom.byId('exportB'),'click',exportPDF);
      	  on(dom.byId('expandAll'),'click',expandAll);
      	  on(dom.byId('bigger'),'click',decreaseCanvas);
      	  on(dom.byId('smaller'),'click',increasesCanvas);
      	  on(dom.byId('reZoom'),'click',function(){
      		  
      		  if(graphWidth>600 || graphHeight>600)
  			      canvas.setZoom(1.0);
	  		  else{
	  			  zoomflag=true;
	  			canvas.setPath('PosRelationMap');
	  		  }
      		  
      	  });
      	  
        };
    	
        var expandAll = function(){
      	  var selectedNodes = selection.getNodes();
      	  if(selectedNodes.length>0)
      		  toggleNode(selectedNodes[0],'all');
      	  else{
	  		  alert("请选择要展开的机构！");
	  	  }
        };
        
        var onShowNavigation = function(){
      	  registry.byId('overview').setPath('PosRelationMap');
        };
        
        var increasesCanvas = function(){
        	if((graphWidth<600 && graphHeight<600) && canvas.zoom<=1)
      		  return;
      	  canvas.increaseZoom(0.7);
        };
        
        var decreaseCanvas = function(){
      	canvas.decreaseZoom(0.7);
        };
    	
        var exportPDF = function(){
      	  //var name;
      	  //xhr.post('/servlet/duty/relation/getPosRelations/download',{}).then(function(param){
      		  
  		//	  window.location.href = '/downloadGraph/' + "orgMap.jpg?path="+param+"&format=jpg&x=0&y=0&w="+graphWidth+"&h="+graphHeight+"&z=1";
  		  //});
      		  
      	  window.location.href = '/servlet/duty/relation/getPosRelations/download';
        };
        
        
        var showPosUpInfo = function(id,hitinfo,event){
        	 var info = canvas.getHitTest().getLabelInfo(id);
        	 //if(info && info.labelIndex == 1){
        		 //hidePosUpInfo();
        		// return;
        	 //}
        	 
        	 
         	  var posupinfo = posUpJson[info.mainElementId];
         	  if(!posupinfo)return;
         	  var U_M = posupinfo.split(",");
         	  var html = "<table><tr><td nowrap style=\"padding-bottom:6px;\">"+U_M[0]+"</td></tr>";
         	  if(U_M.length>1)
         		  html+="<tr><td nowrap style=\"border-top:1px gray solid;padding-top:8px;\">"+U_M[1]+"</td></tr>";
              html+="</table>";
         	 dom.byId("posUpInfo").innerHTML=html;
         	 
         	 
         	 var nodeBounds = canvas.getHitTest().getBoundsForId(info.mainElementId);
         	 var x = Math.round(nodeBounds.minX * canvas.zoom);
	            var y = Math.round(nodeBounds.minY * canvas.zoom);
	            var w = Math.round(nodeBounds.width() * canvas.zoom);
	            var h = Math.round(nodeBounds.height() * canvas.zoom);

	            var left = (x+w);
	            var top = (y+h/2);

	            dojo.style(dom.byId("posUpInfo"), {
	              "left" : left + "px",
	              "top" : top + "px",
	              "display" : "block",
	              "position" : "absolute"
	            });
	            canvas.overlays.appendChild(dom.byId("posUpInfo"));
        };
        
        
        var hidePosUpInfo = function(){
        	dom.byId("posUpInfo").style.display="none";
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
        
        
    	ready(function(){
    		parser.parse();
    		initButton();
    		initGraph();
    		// 加载时，更换 div id=preview_titleBarNode 的 背景颜色 
            dojo.addClass("preview_titleBarNode","common_background_color");
            //
    	});
    });
  
    var showOrgInfo = function(nodeId){
  	  dojo.xhrPost({
  	      url : '/servlet/duty/relation/getPosRelations/getCodeitemid',
  	      content : {  id:nodeId },
  	      load: function(result, ioargs) {
  	    	  
  	    		var  url = '/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id='+result.substr(2)+'&infokind=3&dbname=';
  	          
  	        	  showModalDialog(url,'','dialogHeight:800;dialogWidth:600;center:yes;help:no;resizable:no;status:no;');
  	      }
  	    });
    };
    
    function setOptions(){
		orgMapForm.action="/pos/posreport/pos_relation_parameter.do?b_search=link&report_relations=yes";
		orgMapForm.submit();
  }
    
    
    function loadingStage(type,expandflag){
  	  var loading = document.getElementById('loading');
  	  var canvasobj = document.getElementById('canvas');
  	  if(type=='show'){
  		  loading.style.top=canvasobj.offsetHeight/2;
  		  loading.style.left=canvasobj.offsetWidth/2;
  		  loading.style.display='block';
  		  if('all'==expandflag)
  		     document.getElementById('expandAll').disabled = 'true';
  	  }else{
  		  loading.style.display='none';
  		  document.getElementById('expandAll').disabled = false;
  	  }
    }
    var codepath;
    function reloadTree(){
    	
    	if(parent.frames['mil_menu'].reloadflag){
	    	codepath = parent.frames['mil_menu'].getExpandPath();
	    	parent.frames['mil_menu'].reloadTree();
	    	setTimeout('goon()',1000);
	    	
    	}
    }
    
    function goon(){parent.frames['mil_menu'].expandTree(codepath);};
    
    reloadTree();
  </script>
  
  
