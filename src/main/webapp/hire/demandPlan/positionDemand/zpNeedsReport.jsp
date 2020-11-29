<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.demandPlan.PositionDemandForm"%>
<html>
<head>
<title>Insert title here</title>
</head>
<script type="text/javascript">
<!--
	function excecuteExcel()
   {
    var chanelcode=document.getElementById("zpchanel").value;
	var hashvo=new ParameterSet();			
	hashvo.setValue("lineFields","${positionDemandForm.lineFields}");
	hashvo.setValue("lieFields","${positionDemandForm.lieFields}");
	hashvo.setValue("resultFields","${positionDemandForm.resultFields}");
	hashvo.setValue("whl_sql","${positionDemandForm.whl_sql}");
	hashvo.setValue("zpchanel",chanelcode);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'3000000254'},hashvo);
   }	
   function showExcel(outparamters)
	 {
		 var outName=outparamters.getValue("outName");
		 outName = decode(outName);
		 var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	 }
	 function closeParent(){
	//	window.opener.close();
	}
	function chanchanel(){
		var waitInfo=eval("wait");
		waitInfo.style.display="block";
		var obj = document.getElementById("zpchanel");
		obj.readonly="true";
		positionDemandForm.action="/hire/demandPlan/positionDemand/getNeedFields.do?b_change=change&opt=change";
  		positionDemandForm.target="_self";	
  		positionDemandForm.submit();
	}
	<%
		PositionDemandForm positionDemandForm=(PositionDemandForm)session.getAttribute("positionDemandForm");//dml 2011-6-21 15:28:03
		String noexcel=positionDemandForm.getNoexcel();
		
	%>
//-->
</script>
<style>
	.fixedtab 
	{ 
		overflow:auto;
	}
	.t_cell_locked2 {
	<!--解决招聘管理/需求报批/文件/招聘需求汇总表 中缺线问题   引用样式为RecordRow  jingq upd 2014.08.27 -->
	  /*  background-image:url(/images/listtableheader_deep-8.jpg);*/
		/* background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 0pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 0pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		position: relative;
		z-index: 20; */
		border: inset 1px #C4D8EE;
		BORDER-BOTTOM: #C4D8EE 1pt solid; 
		BORDER-LEFT: #C4D8EE 1pt solid; 
		BORDER-RIGHT: #C4D8EE 1pt solid; 
		BORDER-TOP: #C4D8EE 1pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22px;
		padding:0 5px 0 5px;
	}
	.t_cell_locked {
		/* background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 0pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		position: relative;
		z-index: 10; */
		border: inset 1px #C4D8EE;
		BORDER-BOTTOM: #C4D8EE 1pt solid; 
		BORDER-LEFT: #C4D8EE 1pt solid; 
		BORDER-RIGHT: #C4D8EE 1pt solid; 
		BORDER-TOP: #C4D8EE 1pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22px;
		padding:0 5px 0 5px;
	}
	.t_cell_locked3 {
		/* background-repeat:repeat;
		background-position : center left;
		BACKGROUND-COLOR: #f4f7f7;  
		BORDER-BOTTOM: #94B6E6 0pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
		height:22;
		font-weight: bold;	
		valign:middle;
		COLOR : black;
		position: relative;
		z-index: 10; */
		border: inset 1px #C4D8EE;
		BORDER-BOTTOM: #C4D8EE 1pt solid; 
		BORDER-LEFT: #C4D8EE 1pt solid; 
		BORDER-RIGHT: #C4D8EE 1pt solid; 
		BORDER-TOP: #C4D8EE 1pt solid;
		font-size: 12px;
		border-collapse:collapse; 
		height:22px;
		padding:0 5px 0 5px;
	}
</style>
<hrms:themes></hrms:themes>
<html:form action="/hire/demandPlan/positionDemand/getNeedFields">
<body  style="overflow:auto;"  >
<br>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 招聘渠道:<hrms:optioncollection name="positionDemandForm" property="chanelList" collection="list" />
 							<html:select name="positionDemandForm" property="zpchanel" size="1" style="width:150px;" onchange="chanchanel();">
					             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
					        </html:select>&nbsp;&nbsp;&nbsp;
					        <%if(noexcel!=null&&noexcel.length()!=0){
		
							}else{ %>
					        <button id="excel" extra="button" allowPushDown="false"  onclick="excecuteExcel();" down="false"><bean:message key="goabroad.collect.educe.excel"/></button>
					        <% }%>
					        &nbsp;&nbsp;&nbsp;
					        <button  extra="button" allowPushDown="false"  onclick="window.close();" down="false">关闭</button>
 							
<div class='fixedtab' style='margin-left:20;margin-top:20;'  >
${positionDemandForm.reportHtml}
</div>
<br>
<br>
<br>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
	</div>

 </body>
 </html:form>
</html>