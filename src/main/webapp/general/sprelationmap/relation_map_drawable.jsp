<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" src="/powerCharts/jquery.min.js"></script>
<script language="JavaScript" src="/powerCharts/FusionCharts.js"></script> 
<script type="text/javascript"    src="/powerCharts/FusionChartsExportComponent.js"></script> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script type="text/javascript" >

</script>
<%
String showQueryButton = request.getParameter("showQueryButton");//tianye add showQueryButton代表是从业务平台查看关系图需要提供查询功能 自助平台下我的团队关系图不提供查询功能（人员定位）
%>
 <script type="text/javascript">
 var width="${relationMapForm.trueWidth}";
 var height="${relationMapForm.trueHeight}";
 var menuConstantTop="${relationMapForm.menuConstantTop}";
 var menuConstantLeft="${relationMapForm.menuConstantLeft}";
 var clientH=document.body.clientHeight;
 //alert(width+"///"+height);
 </script>
 
 
<style>
<!--
.menuTd{
    border: inset 1px #F5F5F5;
	BORDER-BOTTOM: #F5F5F5 1pt solid; 
	BORDER-LEFT: #F5F5F5 0pt solid; 
	BORDER-RIGHT: #F5F5F5 0pt solid; 
	BORDER-TOP: #F5F5F5 0pt solid;
}
-->
</style>

 <body style="margin-top: 7px;">
<html:form action="/general/sprelationmap/relation_map_drawable">
<html:hidden property="xmlData"/>
<html:hidden property="currentNodeId"/>
<html:hidden property="nodeType"/>
<html:hidden property="clientWidth"/>
<html:hidden property="clientHeight"/>
<html:hidden property="trueWidth"/>
<html:hidden property="trueHeight"/>
<html:hidden property="a_code"/>
<span style="vertical-align: middle;"><input type="button" value="打印预演" class="mybutton" onclick="printBrowse()"/></span>
<%if("showQueryButton".equals(showQueryButton)){ %>
<input type="text" id="queryTreePersonName" name="a0101" class="text4" value="" size="atuo"  onkeyup="showDataSelectBoxBefore('queryTreePersonName');" />
<!-- tianye add 查询按钮 根据需求以后可以考虑它是否需要提供 不需要可以进行注释 功能已完成 -->
<span style="vertical-align: middle;"><input type="button" value="查询" class="mybutton" onclick="queryTreePerson()"/></span>
<%} %>
<!-- add end -->
<div id="date_panel" style="display:none; z-index:2" onmouseout="remove();">
		<select id="date_box" name="contenttype" multiple="multiple"  style="" size="10"  ondblclick="setSelectPerson();">
        </select>
</div>
<div id="chart1div" style="position:absolute;top:31px;left:0px;z-index:1;">  
        FusionCharts    
      </div>
      </html:form>
     <script type="text/javascript">
     
	  var currentRenderer = 'javascript';	 		  
	  FusionCharts.setCurrentRenderer(currentRenderer);
	  var  chartObj = new FusionCharts("/powerCharts/DragNode.swf","sampleChart",width,height,"0","1");
      
       //This way of get the relation data can not be used on weblogic server.
	   //chartObj.setDataURL("relation_map_xml.jsp");
       
       chartObj.setXMLData("${relationMapForm.xmlData}");
       chartObj.render("chart1div"); 
	   function reloadDataJS(myVar){
	 		var chartObj2 = getChartFromId("sampleChart");   
	 		var chart1div=document.getElementById("chart1div");
	        document.getElementById("xmlData").value=getEncodeStr(chartObj2.getXMLData());
	        var arr=myVar.split("^");
	        document.getElementById("currentNodeId").value=arr[0]; 
	        if(arr[3]=='parent'||arr[3]=='child')
	            document.getElementById("t1").style.display="none";
	        else
	            document.getElementById("t1").style.display="block";
	        document.getElementById("nodeType").value=arr[3];
	        with(document.getElementById("menupanel"))
       	 	{
         	 	 style.position="absolute";
	        	 style.posLeft=window.event.clientX+document.body.scrollLeft;
	        	 style.posTop=window.event.clientY+document.body.scrollTop;
	         	 style.width=150;
	         	 style.display="block";
       	 	}
		}
		function refreshColor(){
		    var chartObj2 = getChartFromId("sampleChart");   
	 		var chart1div=document.getElementById("chart1div");
	        document.getElementById("xmlData").value=getEncodeStr(chartObj2.getXMLData());
	        relationMapForm.action="/general/sprelationmap/relation_map_drawable.do?b_query=query&freshType=1";
            relationMapForm.submit();
		} 
		</script>

  		<div id="menupanel" style="position:absolute;top:50px;left:20px;height:20px;width:30px;border:1px;border-color:#c0c0c0;z-index:100;display:none;">
<table width="100%" style="background-color:#c0c0c0">
<tr><td align="right" class="menuTd"><a href="javascript:menuClose();" ><img src="/images/hire/18.gif" border="0"/></a></td></tr>
<tr height="25px" id="t1" onmouseover="changeBg('t1');" onmouseout="backBg('t1');" style="cursor:hand;display:block;" onclick="expendChild('<%=showQueryButton%>');"><td align="left" class="menuTd" >&nbsp;&nbsp;显示|隐藏下级节点</td></tr>
<tr height="25px" id="t2" onmouseover="changeBg('t2');" onmouseout="backBg('t2');" style="cursor:hand;display:block;" onclick="openInfoBase();"><td align="left" class="menuTd" >&nbsp;&nbsp;人员基本信息表</td></tr>
<tr><td align="left" >&nbsp;&nbsp;</td></tr>
</table>
</div>
<form name="myprintform" action="/general/sprelationmap/print_browse.do?b_init=link" method="post" target="mysearchframe">
<input type="hidden" name="printXmlData" id="printXmlData"/>
</form>

<iframe name="mysearchframe" style="display: none;"></iframe>
  </body>
