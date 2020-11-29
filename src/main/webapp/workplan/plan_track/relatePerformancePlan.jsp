<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="com.hrms.frame.codec.SafeCode"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<style id=iframeCss>
#scroll_box {
    border: 0px solid #eee;
    height: 270px;    
    width: 100%;            
    overflow: auto;            
    margin: 0;
}
.fixedHeaderTr{
    position:static !important;
}
</style >
<html>
	<head>
		<title></title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<script language="JavaScript" src="/workplan/js/workplan_hr.js"></script>
		<script language="javascript" src="/workplan/js/relateplan.js"></script>
		<link rel="stylesheet" href="/css/css1.css" type="text/css">
		 <script language="JavaScript"src="/module/utils/js/template.js"></script>
	</head>
    <hrms:themes></hrms:themes>
	<script language='javascript' >
	var curTr= null;
	var oldTr_c="";
	var curPlan_id= "";
	function tronclick(obj)
	{/*
	    var objtr=document.getElementById(obj.id);
	    if(curTr!=null)
	        curTr.style.backgroundColor="";
	    curTr=objtr;
	    oldTr_c="FFF8D2";
	    curTr.style.backgroundColor='FFF8D2';      
	    Ext.get(obj.id).style.backgroundColor='FFF8D2';      
	    */
	    var ob=document.getElementById("a_table");
	    var j=ob.rows.length;
	    for(var i=1;i<j;i++)
	    {
	        ob.rows[i].className="trShallow";
	    }
	    var objtr=document.getElementById(obj.id);
	    objtr.className="selectedBackGroud";
	    curPlan_id=objtr.cells[0].innerHTML;   
	    
	}
	
	
	
	function ok()
	{
	    if (curPlan_id==""){
	      alert("请选择需要关联的计划！");
	      return;
	    }
    
        var retvo=new Object(); 
        retvo.success=1;
        retvo.plan_id=curPlan_id;
       
        parent.retvo=retvo;
       //关闭窗口
      var selectplanWin = parent.Ext.getCmp("selectplan");
       if(selectplanWin)
    	   selectplanWin.close();
	}
	   
	 
	</script>
		<%
		    String plantype=(String)request.getParameter("plantype");
		    String periodtype = (String) request.getParameter("periodtype");
		    String periodyear = (String) request.getParameter("periodyear");
		    String periodmonth = (String) request.getParameter("periodmonth");
		    String periodweek = (String) request.getParameter("periodweek");

		    periodtype = (periodtype != null) ? periodtype : "";
		    periodyear = (periodyear != null) ? periodyear : "";
		    periodmonth = (periodmonth != null) ? periodmonth : "";
		    periodweek = (periodweek != null) ? periodweek : "";		   
		    plantype=(plantype!=null)?plantype:"";
		
		%>

		<body >
        <input id="plantype" type="hidden" value="<%=plantype%>">
        <input id="periodtype" type="hidden" value="<%=periodtype%>">
        <input id="periodyear" type="hidden" value="<%=periodyear%>">
        <input id="periodmonth" type="hidden" value="<%=periodmonth%>">        
        <input id="periodweek" type="hidden" value="<%=periodweek%>">  
        <input id="plantype" type="hidden" value="<%=plantype %>">
		<table align="center" width="100%" style="">
			<tr>
				<td>
				<fieldset align="center" style="width: 97%; height: 300px">
					<legend>
						关联考核计划
					</legend>
					<table width="100%" height="100%" border="0">
						<tr >
							<td align="center" valign="top">
								<div id="scroll_box" style="width: 100%;">
									<TABLE id="a_table" class="ListTable" cellSpacing=0
										cellPadding=0 width="100%" align=center
										border=0>
											<TR class="fixedHeaderTr" >
												<TD class="TableRow" noWrap align="center">
													计划编号
												</TD>
												<TD class="TableRow" noWrap align="center">
													名称
												</TD>
												<TD class="TableRow" noWrap align="center">
													考核时间区间
												</TD>
												<TD class="TableRow" noWrap align="center">
													状态
												</TD>
												<TD class="TableRow" noWrap align="center">
													考核对象类型
												</TD>
											</TR>

											<TR>
												<TD class="RecordRow" style="BORDER-TOP: 0px" colspan="5"
													noWrap align="left">
													<a href="###" onclick="addPlan()"> 创建新计划……</a>

												</TD>
											</TR>
									</TABLE>
								</div>
							</TD>
						</TR>
					</TABLE>
				</fieldset>
				</td>
			</tr>
			<tr height="30px;">
				<td>
					<table width="100%" style="margin-left: -1px;">
						<tr>
							<td align="center">
								<button name="compute" Class="mybutton" onclick="ok();">
									<bean:message key="button.ok" />
								</button>
								  <button name="cancel" Class="mybutton" onclick="closeWin()"><bean:message key="button.cancel"/></button>

							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
		</body>

<script language='javascript'>
  initRelateForm();
  function closeWin(){
	  parent.Ext.getCmp("selectplan").destroy();
  }
</script>
</html>
