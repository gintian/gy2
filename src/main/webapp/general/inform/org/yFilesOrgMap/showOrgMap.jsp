<%@page import="java.util.HashMap"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>

<%

%>

<hrms:themes />
<style type="text/css">
  @import "../../../../DOJO/dojo/resources/dojo.css";
  @import "../../../../DOJO/dijit/themes/claro/claro.css";
  @import "../../../../resources/style/yfiles-ajax.css";
</style>

<script>
    var dojoConfig = {
      isDebug: false,
      baseUrl: "../../../../DOJO/",
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
  <script data-dojo-config="async: true" src="../../../../DOJO/dojo/dojo.js"></script>
  <script src="../../../../DOJO/dojo/yfiles-ajax.js"></script>
<script type='text/javascript' src='/module/utils/js/template.js' ></script>
<body class="claro yfiles">
<html:form action="/general/inform/org/map/showyFilesOrgMap">
     <html:hidden name="orgMapForm" property="orgtype" styleId="orgtype"/>
   	 <html:hidden name="orgMapForm" property="backdate" styleId="backdate"/>
   	 <html:hidden name="orgMapForm" property="code" styleId="code"/>
   	 <html:hidden name="orgMapForm" property="kind" styleId="kind"/>
   	 <html:hidden name="orgMapForm" property="catalog_id" styleId="catalog_id"/>
   	 <html:hidden name="orgMapForm" property="ishistory" styleId="ishistory"/>
   	 <div style="position: absolute;top:10px;">
	   	 <table id="buttonTable">
	   	   <tr >
	   	      <td valign="middle"  nowrap="nowrap">
	   	      &nbsp;&nbsp;
	   	         <button class="mybutton" type="button" onclick="setOptions();" style="line-height: 20px"><bean:message key="button.orgmapset"/></button>
	   	         <button class="mybutton" type="button" id="exportB" style="line-height: 20px"><bean:message key="pos.report.relations.export"/></button>
	   	         <button class="mybutton" type="button" id="expandAll" style="line-height: 20px">展开所有下级</button>
	   	         <button class="mybutton" type="button" id="smaller" style="width: 30px;line-height: 20px" title="缩小">-</button>
	   	         <button class="mybutton" type="button" id="bigger" style="width: 30px;line-height: 20px" title="放大">+</button>
	   	         <button class="mybutton" type="button" id="reZoom" style="line-height: 20px" title="初始化缩放级别">1:1</button>
	   	         <logic:equal value="dxt" name="orgMapForm" property="returnvalue">
                   <input type="button" name="b_delete" value='<bean:message key="button.return"/>' class="mybutton" onclick="hrbreturn('org','2','orgMapForm');"> 
                 </logic:equal>
                 <!--  归档说明不需要显示
	   	         <logic:equal value="true" name="orgMapForm" property="ishistory">
	   	         &nbsp;&nbsp;&nbsp;
	   	         <button class="mybutton" id="showhistorydesc" onclick="explain()">归档说明</button> 
	   	         &nbsp;&nbsp;&nbsp;
	   	                           历史机构：${orgMapForm.catalog_name}
	   	         </logic:equal>
	   	         -->
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
	                     data-dojo-props="canvasId:'canvas',overviewId:'overview'" >
	                </div>
	          </div>
          </div>
   	 <table  width="100%" height="100%" border=0>
   	   <tr>
   	     <td height="40">&nbsp;</td>
   	   </tr>
   	   <tr>
   	      <td>
   	           <!-- data-dojo-type 是div关联那个js对象，相当于插件。  -->
		      <div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="canvas"  maxZoomForFitContent="1.0" 
		           data-dojo-props="nodeEvents:true,nodeLabelEvents:true,baseURL:'../../../..'" style="height: 100%;width: 100%;">
		     </div>
   	      </td>
   	   </tr>
   	 </table>
   	 <!--  加载等待 动态图 -->
   	 <div id="loading" style="display: none;z-index:10;position: absolute;">
       <img src="../../../../resources/style/loading.gif"></img>
     </div>
   	 <!-- 点击按钮后台操作时用于遮挡页面防止重复操作 -->
   	 <div id="coverDiv" style="position:absolute;display:none;background:black; filter:alpha(opacity:00);opacity:0;top:0px;left:0px; width:100%;height:100%;z-index: 100;"></div>
	       
  </html:form>
</body>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script>
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
        	
        	//初始化画布
          var initGraph = function() {
        		
        		//registry对象 用来 获取指定id的js对象
        	canvas = registry.byId('canvas');
            canvas.paintDetailThreshold = 3;
            canvas.zoomToPointRecognizer = function(evt) { return !evt.ctrlKey; };
            
            canvas._mouseWheeled = mouseWheel;
            
            var overview = registry.byId("overview");
            overview.setNoInteractionMode();
            
          
            on(canvas,"clickNodeLabel",clickLabel);
            on(canvas,"DblClickNodeLabel",dblClickLabel);
            
            //on(canvas,mouse.wheel,function(){alert();});
            
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
            
            //ajax
            xhr.post('/servlet/org/yfileschart/getOrgMapServlet/initializeGraph',{
            	handleAs : 'json',//返回值以什么格式处理，默认是字符串，这里自动转成json对象
                data : {//参数，request.parameter中获取
                		code:dom.byId("code").value,
                    	orgtype:dom.byId("orgtype").value,
                    	kind:dom.byId("kind").value,
                    	backdate:dom.byId("backdate").value,
                    	catalog_id:dom.byId("catalog_id").value,
                    	ishistory:dom.byId("ishistory").value,
                    	cwidth:dom.byId('canvas').offsetWidth,
                    	cheight:dom.byId('canvas').offsetHeight
                	}
              }).then(function(boundsAndShift, ioargs) {
                  canvas.setPath('OrgMapTree');
                  graphWidth = boundsAndShift.bounds.maxX;
      	          graphHeight = boundsAndShift.bounds.maxY;
      	          center = boundsAndShift.shift;
      	          
      	          //当节点将要加载之前执行的方法
	      	      canvas.onLoadTiles = function(){
      	        	  //zoom：设置初始比例。setCenter：设置中心点，可自定义
      	        	  if(zoomflag){canvas.zoom=1;canvas.setCenter(center);}
      	        	  //else{var event = window.event;if(event){alert(event.type);};}
      	          };
	      	      //当节点加载完毕后执行的方法
	      	      on(canvas, "TilesLoaded", onTilesLoaded);
                }
            );
            
          };

          
          var onTilesLoaded = function(){
        	  if(zoomflag){
        		  //初次加载后设置成false，以后都不执行。否则将每一步操作都将执行canvas.onLoadTiles，放大缩小移动功能就失效了
        		  zoomflag= false;
        		    //展开root节点
        			  toggleNode("","single");
        	   }
        	  
        	  
        	  doSomeThingElse();
          };
          
          var doSomeThingElse = function(){
        	  loadingStage('hidden');
        	  dom.byId('reZoom').disabled = false;
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
          
          //展开节点操作
          var toggleNode = function(nodeId,expandflag) { 
        	  
        	  loadingStage('show',expandflag);
        	  
        	    dojo.xhrPost({
        	      url:'/servlet/org/yfileschart/getOrgMapServlet/toggleNode',
        	      content : { id : nodeId,expandflag:expandflag },
        	      load: function(boundsAndShift, ioargs) {
        	    	  if('re' == boundsAndShift.state){
        	    		  loadingStage('hidden');
        	    		  return;
        	    	  }
        	    	  //后台更新graph对象后，这里刷新画布，bounds和shift是用来定位使画面刷新后不移动的
        	        canvas.refresh(boundsAndShift.bounds, boundsAndShift.shift);
        	        registry.byId('overview').setPath('OrgMapTree');
        	        graphWidth = boundsAndShift.bounds.maxX;
        	        graphHeight = boundsAndShift.bounds.maxY;
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
        	  dojo.xhrPost({
        	      url : '/servlet/org/yfileschart/getOrgMapServlet/getCodeitemid',
        	      content : {  id:nodeId }, 
        	      load: function(result, ioargs) {
        	    	  var orginfo = result.split("`");
        	    	  if(orginfo.length!=2)
        	    		  return;
        	    	  var url;
        	    	  if(orginfo[0].indexOf('@K') > -1)
        	    		  url = '/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id='+orginfo[0].substr(2)+'&infokind=3&dbname='+orginfo[1];
        	          else if(orginfo[0].indexOf('RY') > -1 || orginfo[0].indexOf('PJ') > -1) 
        	        	  
        	        	  url = '/workbench/browse/showselfinfo.do?b_search=link&personid='+orginfo[0].substr(2)+'&flag=notself&returnvalue=1000000&dbname='+orginfo[1];
        	          else
        	        	  url = '/general/inform/org/map/showorgmap.do?b_showinfo=link&org_id='+orginfo[0].substr(2)+'&infokind=2&dbname='+orginfo[1];
        	          
        	        	//  showModalDialog(url,'','dialogHeight:800;dialogWidth:600;center:yes;help:no;resizable:no;status:no;');
        	    	  //兼容非IE浏览器  改用open弹窗  wangb 20171123
        	          var top = (window.screen.availHeight-30-600)/2;//获得窗口的垂直位置;
      	        	  var left = (window.screen.availWidth-10-800)/2; //获得窗口的水平位置; 
      	    	  	  open(url,'','height=600,width=800,resizable=no,status=no,scrollbars=yes,top'+top+',left='+left);
        	      }
        	    });
          };
          
          var initButton = function(){
        	  on(dom.byId('exportB'),'click',exportPDF);
        	  on(dom.byId('expandAll'),'click',expandAll);
        	  on(dom.byId('expandAll'),'dblclick',function(){return;});
        	  on(dom.byId('bigger'),'click',decreaseCanvas);
        	  on(dom.byId('smaller'),'click',increasesCanvas);
        	  on(dom.byId('reZoom'),'click',function(){
        		  
        		  dom.byId('reZoom').disabled = 'true';
        		  if(graphWidth>600 || graphHeight>600){
        			  canvas.setZoom(1.0);
        			  dom.byId('reZoom').disabled = false;
        		  }else{
        			  zoomflag=true;
        			  canvas.setPath('OrgMapTree');
        		  }
        	  });
        	  
          };
          
          var exportPDF = function(){
        	  window.location.href="/servlet/org/yfileschart/getOrgMapServlet/download";
          };
          
          var expandAll = function(){
        	  var selectedNodes = selection.getNodes();
        	  if(selectedNodes.length>0){
        		  
        		  toggleNode(selectedNodes[0],'all');
        	  }else{
        		  alert("请选择要展开的机构！");
        	  }
          };
          
          var onShowNavigation = function(){
        	  registry.byId('overview').setPath('OrgMapTree');
          };
          
          var increasesCanvas = function(){
        	  if((graphWidth<600 && graphHeight<600) && canvas.zoom<=1)
        		  return;
        	  var zoom = canvas.zoom;
        	  canvas.increaseZoom(0.7);
        	  if(zoom == canvas.zoom && zoom>1){
        		  zoomflag=true;
			      canvas.setPath('OrgMapTree');
        	  }
          };
          
          var decreaseCanvas = function(){
        	canvas.decreaseZoom(0.7);
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
          
          ready(function() {
            parser.parse();
            initButton();
            initGraph();

            // 加载时，更换 div id=preview_titleBarNode 的 背景颜色 
            dojo.addClass("preview_titleBarNode","common_background_color");
          });
        }
      );
      
      function setOptions(){
    		orgMapForm.action="/general/inform/org/map/setyFilesOrgMap.do?b_search=link&ishistory="+orgMapForm.ishistory.value;
    		orgMapForm.submit();
      }
      
      function explain()
      {
         var target_url="/general/inform/org/map/searchhistoryorgmaps.do?b_explain=link&catalog_id=${orgMapForm.catalog_id}";
         var dw=480,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
         window.showModalDialog(target_url,1, 
              "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:480px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      };
      
      function loadingStage(type,expandflag){
    	  var loading = document.getElementById('loading');
    	  var canvasobj = document.getElementById('canvas');
    	  if(type=='show'){
    		  loading.style.top=canvasobj.offsetHeight/2;
    		  loading.style.left=canvasobj.offsetWidth/2;
    		  loading.style.display='block';
    		  if('all'==expandflag){
    		     document.getElementById('expandAll').disabled = 'true';
    		     //显示透明覆盖层
    		     document.getElementById('coverDiv').style.display='block';
    		  }
    	  }else{
    		  loading.style.display='none';
    		  document.getElementById('expandAll').disabled = false;
    		  //隐藏 覆盖层
    		  document.getElementById('coverDiv').style.display='none';
    	  }
      }
      
  </script>


  