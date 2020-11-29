<%@ page contentType="text/html; charset=UTF-8" language="java"%>

<style type="text/css">
  @import "../../../../DOJO/dojo/resources/dojo.css";
  @import "../../../../DOJO/dijit/themes/claro/claro.css";
  @import "../../../../resources/style/yfiles-ajax.css";
</style>
<script language="javascript" src="/js/validate.js"></script>
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
<body>

<div data-dojo-type="yfiles/client/tiles/widget/GraphCanvas" id="canvas" 
		           data-dojo-props="nodeEvents:true,nodeLabelEvents:true,baseURL:'../../../..'" style="height: 100%;width: 100%;">
		     </div>

</body>

		     
		     
<script>
var option;
if(getBrowseVersion()){ //ie娴忚鍣ㄥ脊绐楄幏鍙栨暟鎹�   wangb 20180226 bug34817
	option = parent.window.dialogArguments;
}else{//闈瀒e娴忚鍣� open寮圭獥鑾峰彇鏁版嵁   wangb 20180226 bug34817
	option = parent.opener.option;
}


require(["dojo/ready",
         "dojo/parser",
         "dojo/on","dojo/dom","dijit/registry",
         "dojo/request/xhr",
         "yfiles/client/tiles/widget/GraphCanvas"],
		function(ready,parser,on,dom,registry,xhr){
	
	var zoomflag=true;
	
	    var viewGraph = function(){
	    	
	    	var canvas = registry.byId('canvas');
	    	canvas.onLoadTiles = function(){if(zoomflag){canvas.zoom=1;} };
	    	canvas.onTilesLoaded = function(){
	        	  if(zoomflag){
	        		  zoomflag= false;
	        	   }
	          };;
	    	xhr.post('/servlet/org/yfileschart/previewChartServlet',{
            	handleAs : 'json',
                data :option
              }).then(function(boundsAndShift, ioargs) {
                  canvas.setPath('PreviewChart');
                }
            );
	    };
	
	     ready(function(){
	         parser.parse();
	    	 viewGraph();
	     });
});

</script>		     