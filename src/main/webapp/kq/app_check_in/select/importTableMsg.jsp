<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.kq.app_check_in.AppForm,com.hrms.struts.valueobject.PaginationForm,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc"%>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
AppForm appForm = (AppForm)session.getAttribute("appForm");
PaginationForm msgPageForm = appForm.getMsgPageForm();
ArrayList msglist=appForm.getMsglist();
String importMsg=appForm.getImportMsg();
String outName=appForm.getOutName();
int i=0;
int c=msgPageForm.getPagination().getCurrent();
int size=appForm.getPagerows();
%>
<script language="javascript" src="/js/validate.js"></script>
<html>
	<head>

	</head>
	<script language='javascript'>
    
    function showfile()
    {
    	<%if(outName!=null&&outName!=""){
    		%>
    		var outName='${appForm.outName}';
    	window.location.target="_blank";
    	window.location.href="/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true";
    	return false;
    	<% 	}%>
    }
    function turnBack(){
		window.location.href = "/kq/app_check_in/all_app_data.do?b_search=link";
    }
  </script>
  <% String status=request.getParameter("status");%>
	<body>
	<html:form action="/kq/app_check_in/all_app_data/importMsg.do" styleId="form1">
<div id="msgBox"  style='display: none;text-align:center;margin-top:5px;'  border=0 cellspacing=0  align=center cellpadding=0>
<div class="fixedDiv7 common_border_color" align=center  > 
<table  style='width:100%;text-align:center;'  border=0 cellspacing=0  align=center cellpadding=0 class=ListTable style=margin-top:0>
<thead>
	<tr class=fixedHeaderTr>
		<td align=center class=TableRow width=35 nowrap>
			<bean:message key='train.evaluationStencil.no'/>&nbsp;
		</td>
		<td align=center class=TableRow width="150" nowrap>位置&nbsp;
		</td>
		<td align=center class=TableRow nowrap><bean:message key='workbench.info.content.lebal'/>&nbsp;
		</td>
	</tr>
</thead>		
		<hrms:extenditerate id="element" name="appForm" property="msgPageForm.list" indexes="indexes"  pagination="msgPageForm.pagination" pageCount="${appForm.pagerows}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow_right" nowrap>
            <%=i+(c-1)*size%>
	    </td>
            <td align="left" class="RecordRow" 	style="word-break:break-all;" width="150"  nowrap>
                   &nbsp;<bean:write name="element" property="keyid"/>   	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
        <td align="left" class="RecordRow_left" 	style="word-break:break-all;"  nowrap>
                    <bean:write name="element" property="content" filter="false"/>  
	    </td>
          </tr>
      </hrms:extenditerate>
      
</table>
</div>

<div class="fixedDiv8" style="width:598px; margin-top:1px" id="page"> 
<table  width="100%" align="center" class="RecordRowP" cellpadding="0" cellspacing="0">
		<tr >
		    <td valign="bottom" class="tdFontcolor" >
		            <hrms:paginationtag name="appForm" pagerows="${appForm.pagerows}" property="msgPageForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td align="right" nowrap class="tdFontcolor" >
				 <p align="right">
				 <hrms:paginationlink name="appForm" property="msgPageForm.pagination" nameId="msgPageForm">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<div style="margin-top:5px;">
		<input type="button" name="b_abolish" value="<bean:message key='workbench.info.import.error.down.label'/>" class="mybutton"  onClick="javascript:showfile();">
        <input type="button" name="b_update" value="<bean:message key='button.return'/>" class="mybutton"  onClick="turnBack();"></div>
</div>
</html:form> 
	</body>
</html>


<script type="text/javascript">
function showMess(){
		<%
			if(importMsg!=null&&importMsg!=""&&(msglist==null||msglist.size()==0)){
		%>
			alert("<%=importMsg%>");
			turnBack();
		<% 
			appForm.setImportMsg(null);	
		}
			else if(importMsg!=null&&importMsg!=""&&msglist!=null&&msglist.size()>0){
		%>
			alert("<%=importMsg%>");
			document.getElementById("msgBox").style.display = "";
		<%
		  	appForm.setImportMsg(null);
			}else if((importMsg==null||importMsg=="")&&msglist!=null&&msglist.size()>0){
		%> 
		  	document.getElementById("msgBox").style.display = "";
		<%
			}else{
		%>
				turnBack();
		<%
			}
		%>
	}
		showMess();
</script>