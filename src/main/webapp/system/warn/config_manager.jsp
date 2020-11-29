<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.warn.ConfigForm"%>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/function.js"></script>
<%int i = 0;%>
<style id="iframeCss">
	div{
		cursor:hand;font-size:12px;
	   }
	a{
	text-decoration:none;color:black;font-size:12px;
	}
	
	a.a1:active {
		color: #003100;
		text-decoration: none;
	}
	a.a1:hover {
		color: #FFCC00;
		text-decoration: none;
	}
	a.a1:visited {	
		text-decoration: none;
	}
	a.a1:link {
		color: #003100;
		text-decoration: none;
	}
</style>
<script language="javascript">
<!--
	function goback()
	{
	  document.warnConfigForm.action="/system/sys_param_panel.do";
	  document.warnConfigForm.submit();  
	}
	
function upItem(wid){
	var hashvo=new ParameterSet();        
    hashvo.setValue("wid", wid);
    hashvo.setValue("type", 'up');
    var request=new Request({method:'post',onSuccess:upItemview,functionId:'1010020314'},hashvo);
}

function upItemview(outparamters){
	warnConfigForm.action="/system/warn/config_manager.do?b_query3=link";
	warnConfigForm.submit();
}
function downItem(wid){
	var hashvo=new ParameterSet();             
    hashvo.setValue("wid", wid);
    hashvo.setValue("type", 'down');
    var request=new Request({method:'post',onSuccess:downItemview,functionId:'1010020314'},hashvo);
}

function downItemview(outparamters){
	warnConfigForm.action="/system/warn/config_manager.do?b_query3=link";
	warnConfigForm.submit();
}

function ondelete(){
	var j=0;
  		for(var i=0;i<document.warnConfigForm.elements.length;i++)
  		{
  			if(document.warnConfigForm.elements[i].type=='checkbox'&&document.warnConfigForm.elements[i].name!='selbox')
  			{
  				if(document.warnConfigForm.elements[i].checked==true)
  				{
  					j++;  
  				}
  				
  			}
  		}
  		if(j==0){
  			alert('请选择要删除的预警!');
  			return false;
  		}
	if(!confirm('确定要删除选择的预警吗?')){
		return false;
	}
}
//-->
</script>
<html:form action="/system/warn/config_manager">
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="RecordRow">
		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'pageListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.warn.wname" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.warn.domain" />
					&nbsp;
				</td>
				<!--
			<td align="center" class="TableRow" nowrap width="60%">
		<bean:message key="column.warn.csource"/>&nbsp; </td>
		-->
				<td align="center" class="TableRow" nowrap width="15%">
					<bean:message key="column.warn.cmsg" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.warn.ntype" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="column.warn.valid" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="label.edit" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
				<bean:message key="label.zp_exam.sort"/>             	
			</td> 
			</tr>
		</thead>
		<%
			ConfigForm warnConfigForm=(ConfigForm)session.getAttribute("warnConfigForm");
			int len=warnConfigForm.getPageListForm().getAllList().size();
			int pagerows = warnConfigForm.getPagerows();
			int curpage = warnConfigForm.getPageListForm().getPagination().getCurrent()-1;
			int remainder = len-(pagerows*curpage);
			len=remainder;
		 %>
		<hrms:extenditerate id="element" name="warnConfigForm" property="pageListForm.list" indexes="indexes" pagination="pageListForm.pagination" pageCount="${warnConfigForm.pagerows}" scope="session">
			<%if (i % 2 == 0) {

			%>
			<tr class="trShallow">
				<%} else {%>
			</tr>
			<tr class="trDeep">
				<%}
			i++;

		%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="warnConfigForm" property="pageListForm.select" value="true" indexes="indexes" />&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="wname" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="configDomainNames" filter="true" />
					&nbsp;
				</td>
				<!--
		<td align="left" class="RecordRow" wrap>
                    <bean:write  name="element" property="csource" filter="true"/>&nbsp;
		</td>
		-->
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="cmsg" filter="true" />
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="xmlResultFreq" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:notEqual name="element" property="valid" value="0">
						<bean:message key="column.sys.valid" />&nbsp;
     		        </logic:notEqual>
					<logic:equal name="element" property="valid" value="0">
						<font color=#FF0000> <bean:message key="column.sys.invalid" />&nbsp; </font>
					</logic:equal>
				</td>
				<%
					DynaBean bean = (DynaBean)pageContext.getAttribute("element");
					String wid = (String)bean.get("wid");
				 %>
				<td align="center" class="RecordRow" nowrap>
					<a href="/system/warn/config_maintenance.do?b_query=link&encryptParam=<%=PubFunc.encrypt("warn_wid="+wid)%>"> <img src="/images/edit.gif" border="0"></a>
				</td>
				<td align="left" class="RecordRow" width="50" nowrap>
                 	<%if(i!=1||curpage!=0){ %>
					&nbsp;<a href="javaScript:upItem('<bean:write name="element" property="wid" filter="true"/>');">
					<img src="/images/up01.gif" width="12" height="17" border=0></a> 
					<%}else{ %>
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					<%} %>
				    <%if(len==i){ %>
				    	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				    <%}else{ %>
					&nbsp;<a href="javaScript:downItem('<bean:write name="element" property="wid" filter="true"/>');">
					<img src="/images/down01.gif" width="12" height="17" border=0></a> 
					<%} %>
				</td> 
			</tr>
		</hrms:extenditerate>
		<tr>
			<td colspan="8">
				<table width="100%" align="center" class="">
					<tr>
						<td valign="bottom" class="tdFontcolor">
					            	<hrms:paginationtag name="warnConfigForm"
											pagerows="${warnConfigForm.pagerows}" property="pageListForm.pagination"
											scope="session" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationlink name="warnConfigForm" property="pageListForm.pagination" nameId="pageListForm"></hrms:paginationlink>
							</p>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<table width="50%" align="center" style="height:35px;">
		<tr>
			<td align="center">
				<hrms:submit styleClass="mybutton" property="b_add">
					<bean:message key="button.insert" />
				</hrms:submit>
				<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ondelete();">
					<bean:message key="button.delete" />
				</hrms:submit>
				<logic:equal name="warnConfigForm" property="edition" value="4">
					<input type="button" name="btnreturn" value='返回' onclick="goback();" class="mybutton">
				</logic:equal>
			</td>
		</tr>
	</table>

</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion() == 10){
	var pageSelect = document.getElementsByName('pageSelect')[0];
	pageSelect.setAttribute('onkeypress','checkNumber(this,event)');
}

</script>


