<!DOCTYPE html> 
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<html>
  <head>
  <title></title>
	<style type="text/css">
	.x-panel-header-text-container-default {
		padding: 0px;
	}
	</style>
  </head>
  <%String r5000=request.getParameter("r5000"); 
  	String lessondesc = TrainCourseBo.getr5012(r5000);
  	lessondesc = lessondesc == null || lessondesc.length() < 1 ? "" : lessondesc;
  	lessondesc = lessondesc.replaceAll("\\n","<br>"); 
  %>
  <body style="width：100%; padding: 0;margin: 0;background-color: #FFFFFF;border-width: 1px;border-color: #c5c5c5;border-style: solid;">
	  <div id="panel" style="width：100%; border: 1 #c5c5c5 solid;margin: 0px;padding: 0px;">
		  <div style="height: 20px;padding: 5px 5px 0px 5px;"><img style="float: right;" onclick="closed();"
			   title="关闭" src="/train/resource/mylessons/images/close.png"/>
		  </div>
		  <div id="lessondescid" style="padding: 0px 5px 5px 5px; font-family:'微软雅黑'; font-size:12px;
		  		max-height: 500px;min-height:200px; height:auto;padding-left: 10;overflow: auto;">
		  		<table>
		  			<TR><TD style="word-wrap:break-word; word-break:break-all;display:block;width:100%;"><%=lessondesc %></TD></TR>
		  		</table>
		  		
		  </div>
	  </div>
  </body>
  <script type="text/javascript">
 	var view_panel = document.getElementById('panel');
    var winHeight =parent.document.body.clientHeight;
    view_panel.style.height=winHeight;
    document.getElementById("lessondescid").style.maxHeight=winHeight*0.8;
    window.parent.document.getElementById("lessondesc").height=document.getElementById("lessondescid").offsetHeight+29;
    document.getElementById("panel").style.height=document.getElementById("lessondescid").offsetHeight+25;
    document.getElementById("panel").style.border = '1 #c5c5c5 solid';

    function closed(){
    	window.parent.document.getElementById("desclesson").style.display="none";
		window.parent.document.getElementById("lessondesc").src="";
    }
  </script>
</html>
