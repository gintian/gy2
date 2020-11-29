<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<script type="text/javascript" src="/powerCharts/jquery.min.js"></script>
<script language="JavaScript" src="/powerCharts/FusionCharts.js"></script> 
<script type="text/javascript"    src="/powerCharts/FusionChartsExportComponent.js"></script> 
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script type="text/javascript">
 var width="${relationMapForm.trueWidth}";
 var height="${relationMapForm.trueHeight}";
 </script>

<body>
<hrms:themes/>
<html:form action="/general/sprelationmap/relation_map_drawable">
<input type="button" value="打印" style="position:absolute;top:0px;" class="mybutton" onclick="saveChart()"/>
<input type="button" value="关闭" style="position:absolute;left:50px;top:0px;" class="mybutton" onclick="window.close();">
<div id="chart1div" style="position:absolute;top:30px;left:0px;z-index:1;">  
        FusionCharts    
      </div>
      
      </html:form>
     <script type="text/javascript">
	    
	 function FC_Exported(objRtn){ 
      if (objRtn.statusCode=="1"){
         //alert("这个chart成功保存到服务端,这个文件可以从这个地址访问:" + objRtn.fileName);
         var index=objRtn.fileName.lastIndexOf("/");
         name=objRtn.fileName.substring(index+1);
         var ext=name.substring(name.lastIndexOf(".")+1);
         var win=open("/servlet/DisplayOleContent?fromflag=relationmap&filename="+name,ext);
      }else{
         alert("打印失败，失败原因: " + objRtn.statusMessage);
      }
     }
	 
	      var  chartObj = new FusionCharts({
           swfUrl: "/powerCharts/DragNode.swf",
           width: width, height: height,
           id: 'sampleChart', 
           dataFormat: FusionChartsDataFormats.XMLURL 
        });
	   chartObj.setDataURL("print_xml.jsp");    
       chartObj.render("chart1div"); 
		function saveChart()
		{
			var chartObj2 = getChartFromId("sampleChart");    
			if(chartObj2.hasRendered()){
			    chartObj2.exportChart({exportFormat: 'PDF'});
			   }else{
			   
			   }
		
		}
		
		</script>   
  	
  </body>

