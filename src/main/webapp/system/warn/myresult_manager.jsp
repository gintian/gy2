<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.transaction.sys.warn.ColumnBean"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
 <%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
%>

<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
<%int i = 0;%>
<script language="javaScript">
	function changedb(){
		var wid = document.warnConfigForm.wid.value;
		var v = document.warnConfigForm.dbPre.value;
		document.warnConfigForm.action="/system/warn/myresult_manager.do?b_query=link&warn_wid="+wid +"&dbpre="+v;
		document.warnConfigForm.submit();
	}
</script>
<hrms:themes></hrms:themes>
<html:form action="/system/warn/myresult_manager">
<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0">
	<tr height="25px;">
			<input type="hidden" name="wid" value="<bean:write name="warnConfigForm" property="wid" filter="true" />">
			<td class="" nowrap>
				人员库:
                 <html:select name="warnConfigForm" property="dbPre" size="1" onchange="changedb()">
                  <option value="ALL">全部</option>
                  <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                </html:select>    
				预警提示:
				<bean:write name="warnConfigForm" property="dynaBean.cmsg" filter="true" />
   </td>	
  </tr>
   <tr>
	 <td>
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable">
			     <tr>
			         <td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.org" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="label.title.dept" />
				&nbsp;
			   </td>
			   <td align="center" class="TableRow" nowrap>
				<bean:message key="tree.kkroot.kkdesc" />
				&nbsp;
		     	</td>
		     	<td align="center" class="TableRow" nowrap width="15%">
			    	<bean:message key="label.title.name" />
				&nbsp;
			    </td>

		       	<logic:iterate id="ColumnBean" name="warnConfigForm" property="columnList" indexId="index">
				   <td align="center" class="TableRow" nowrap>
				   	<bean:write name="ColumnBean" property="columndesc" filter="true" />
					&nbsp;
				   </td>
			      </logic:iterate>
			     </tr>
			     

		<hrms:paginationdb id="element" name="warnConfigForm" sql_str="warnConfigForm.strsql" table="" where_str="" columns="warnConfigForm.columns" page_id="pagination" pagerows="15" indexes="indexes">
			<%if (i % 2 == 0) {

			%>
			<tr class="trShallow">
				<%} else {%>
			</tr>
			<tr class="trDeep">
				<%}
			i++;

			%>
				  <td align="left" class="RecordRow" nowrap>  
					<!--bean:write  name="element" property="o1name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				  <td align="left" class="RecordRow" nowrap>  
					<!--bean:write name="element" property="o2name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				  <td align="left" class="RecordRow" nowrap>  
					<!--bean:write name="element" property="o3name" filter="true"/-->
					&nbsp;<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page" />
					<bean:write name="codeitem" property="codename" />
					&nbsp;
				</td>
				  <td align="left" class="RecordRow" nowrap>  
					<!--a href="/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="element" property="pre" filter="true"/>&a0100=<bean:write name="element" property="a0100" filter="true"/>&flag=notself&returnvalue=1"-->
					&nbsp;<bean:write name="element" property="a0101" filter="true" />
					&nbsp;
					<!--/a-->
				</td>


				<logic:iterate id="columnBean" name="warnConfigForm" property="columnList" indexId="index">
					<%String itemType = ((ColumnBean)columnBean).getColumnType();
					  String itemsetid=((ColumnBean)columnBean).getCodesetid();
						if(itemType.equalsIgnoreCase("D")||itemType.equalsIgnoreCase("N")){%>
						<td align="right" class="RecordRow" nowrap>
					<%}else{%>
						  <td align="left" class="RecordRow" nowrap>  
					<%}%>
					<%if(itemsetid!=null&&!itemsetid.equalsIgnoreCase("0")){ %>
					     <hrms:codetoname codeid="<%=itemsetid %>" name="element" codevalue="<%=((ColumnBean)columnBean).getColumnName().toLowerCase()%>" codeitem="codeitem" scope="page" />
					       &nbsp;<bean:write name="codeitem" property="codename" />
					<%}else{%>
					&nbsp;	<bean:write name="element" property="<%=((ColumnBean)columnBean).getColumnName().toLowerCase()%>" filter="true" />
					&nbsp;
					<%}%>
					</td>
				</logic:iterate>
			    </tr>
	        </hrms:paginationdb>
	   </table> 
	 </td>
  </tr>
  <tr>
	   <td>
	       <table width="100%" align="center" class="RecordRowP">
		       <tr>
			     <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			    </td>
			    <td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="warnConfigForm" property="pagination" nameId="warnConfigForm" scope="page">
					</hrms:paginationdblink>
			    </td>
		        </tr>
	            </table>
	   </td>
	</tr>
    </table> 
	<table width="50%" align="center">
		<tr>
			<td align="center">
				<html:button styleClass="mybutton" property="bc_btn1" onclick="window.location.replace('/system/warn/myinfo_all.do?b_query=link');">
					<bean:message key="button.return" />
				</html:button>
			</td>
		</tr>
	</table>

</html:form>

