<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="java.util.*, org.apache.commons.beanutils.LazyDynaBean"%>

<html>
<head>

<% 
	EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
	
	String netHref=employPortalForm.getNetHref();
	String lftype=employPortalForm.getLfType();
	
	String gg_con="";
	String gg_title="";
	String gg_hasfile="";
	String gg_ext="";
	String gg_href="";
	String comid=request.getParameter("br_showBoardPage");  
	ArrayList boardlist=employPortalForm.getBoardlist();
	if(boardlist!=null){
		for(int i=0;i<boardlist.size();i++){
			LazyDynaBean bean=(LazyDynaBean)boardlist.get(i);
			String id=(String)bean.get("id");
			if(comid.equals(id)){
				gg_title=(String)bean.get("title");
				String down=(String)bean.get("down");
				gg_con =(String)bean.get("content");
				gg_hasfile=bean.get("hasfile")==null?null:(String)bean.get("hasfile");
				gg_ext=(String)bean.get("ext");
				gg_href=(String)bean.get("href");
				break;
			}
		}
	}
%>
<LINK href="../../css/hireNetStyle.css" type=text/css rel=stylesheet>
<link href="../../css/newHireStyle.css" type="text/css" rel="stylesheet">
<title><%=gg_title%></title>

</head>
<body>
<html:form action="/hire/hireNetPortal/search_zp_position">
	<TABLE align=center width="1000px" cellSpacing="0px" cellPadding="0px"  style="left:expression((document.body.clientWidth-1000)/2+'px'">
  		<TBODY>
  		<tr>
		<%if(lftype.equals("1")){ %>
			<td width="90%" >
			<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="1000">  
			<param name="movie" value="/images/hire_header.swf">  
			<param name="wmode" value="transparent">  
			<embed src="/images/hire_header.swf" width="1000"  type="application/x-shockwave-flash" />  
			</object>
			</td>
		<%}else {
		
			if(netHref!=null&&netHref.length()>0){ %>
				<td width="90%" ><a href="<%=netHref%>" target="_blank"><img src="/images/hire_header.gif" border="0"/></a></td>
			<%}else{ %>
				<td width="90%" height="auto"><img src='/images/hire_header.gif' border='0'/></td>
			<%} 
		}%>
  		</tr>
  		</TBODY>
	</TABLE>
	<TABLE align=center width="1000px" height="100%" cellSpacing="0px" cellPadding="0px"  style="left:expression((document.body.clientWidth-1000)/2+'px'">
  		<tr>
  		<td>
			<div class="tcenter" style="float:center;height:100%;border:0px solid #909296;border-top:0px;border-bottom:0px">
			<div style="margin-left:50">
			<br>
			<h3>通知公告</h3>
			</div>
			<div align="center">
									
				<table style='width:800px;height:100px;border:0px;align:left;font-size:12;margin-top:0px;margin-left:auto;margin-right:auto;padding-top:0px;padding-left:0px'>
					<tr>
					<td>
						<div align="center"><h3><%=gg_title%></h3></div>
						<%=gg_con%>
						
						<%if(gg_hasfile!=null&&gg_hasfile.trim().length()!=0&&gg_hasfile.trim().equalsIgnoreCase("true")){ %>
						<div align="left">
						<div><h4>相关附件：</h4></div>
							 		 &nbsp;&nbsp;<a href="<%=gg_href %>" style='margin-left:10px;margin-bottom:4px'><%=gg_title %></a>
						</div>
						<%} %>
					</td>
					</tr>
				</table>
				
			</div>
			            
			</div>
		</td>
	</tr>
	</TABLE>
			       
</html:form>           
</body>
</html>