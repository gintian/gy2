<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>移动助手</title>
	 <link rel="stylesheet" href="../css/chart.css" type="text/css">
	 <script type="text/javascript" src="../jquery/jquery-3.5.1.min.js"></script>
	 <script type="text/javascript" src="../jquery/rpc_command.js"></script>	
	 <script type="text/javascript" src="js/highcharts.js"></script> 
	 <script type="text/javascript" src="../jquery/highchartsset.js"></script>
	 
<script type="text/javascript">
Highcharts.visualize = function(options) {
	var chart = new Highcharts.Chart(options);
}
function getChartValue(id,username)
{
    var map = new HashMap();    
    map.put("statid", id);
    map.put("nbase", username);    
   　Rpc({functionId:'9101000012',success:viewchar},map);	
}
var charttype="${sphoneForm.charttype}";			
function viewchar(obj) 
{			
    var map=JSON.parse(obj);
    var snamedisplay=map.snamedisplay;
    var legendlist=map.legendlist;
    var options;
    if(charttype=="1")
       options=getColumnChart(legendlist,snamedisplay);  
    else if(charttype=="2") 
       options=getPieChart(legendlist,snamedisplay);  
    else if(charttype=="3")
       options=getLineChart(legendlist,snamedisplay);  
	Highcharts.visualize(options);
}
</script>
</head>
<body>

<div class="ui-top">
<html:form action="/phone-app/app/statchart">
   <div class='ui-header'>
         <a href="/phone-app/app/statlist.do?b_query=link&statid=" class="btu btn-left">
         <span class="icon icon-forward"></span>
         <span class="btn-inner">返回</span> 
         </a>
		   <span class="ui-title"><bean:write name="sphoneForm" property="snamedisplay"/></span>
		 <a href="javascript:showset()" class="btu btn-right">
		 <span class="icon icon-start"></span>
		 <span class="btn-inner">选项</span>
		 </a>
   </div>
   <div id="container" style="position:absolute;width:95%;height:85%;left:5;margin: 0 auto;"></div>   
<script type="text/javascript">   
  getChartValue("${sphoneForm.statid}","${sphoneForm.nbase}");
  function showset()
  {
     sphoneForm.action="/phone-app/app/statchart.do?b_set=link&returnvalue=chart";
	 sphoneForm.submit(); 
  }
</script>
</html:form>
</div>
</body>
</html>
