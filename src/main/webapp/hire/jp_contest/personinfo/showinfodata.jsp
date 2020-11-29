<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.jp_contest.personinfo.ShowJpPersonForm" %>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>


<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<% int n = 0;%>
<script language="javascript">
   function selchange()
    {
      showJpPersonForm.action="/hire/jp_contest/personinfo/showinfodata.do?b_search=link";
      showJpPersonForm.submit();
   }
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("appstate","${showJpPersonForm.appstate}");
	hashvo.setValue("state","${showJpPersonForm.state}");
	hashvo.setValue("jp_station","${showJpPersonForm.jp_station}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'3970003004'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");
    url = decode(url);
    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
   }
</script>
<hrms:themes></hrms:themes>
<html:form action="/hire/jp_contest/personinfo/showinfodata">
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <br>
 
 <tr>
    <td align="left"  nowrap>
    	<bean:message key="hire.jp.personinfo.nijppost"/>
    	<html:select name="showJpPersonForm" property="jp_station" size="1" onchange="selchange()">
        	<html:optionsCollection property="stationlist" value="dataValue" label="dataName"/>
        </html:select>  
     	<bean:message key="column.sys.status"/>
        <bean:write name="showJpPersonForm" property="statestr"  filter="false"/>
         <bean:message key="button.app"/><bean:message key="column.sys.status"/>
        <bean:write name="showJpPersonForm" property="appstatestr"  filter="false"/>
	</td>         
   </tr>
</table>
   <br>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" >
		<thead>
			<tr>
				<logic:iterate id="info"    name="showJpPersonForm"  property="columns">   
	              <td align="center" class="TableRow" nowrap>
	                   <bean:write  name="info" filter="true"/>&nbsp;
	              </td>
	             </logic:iterate> 	
			</tr>
		</thead>
		<logic:equal name="showJpPersonForm" property="ye" value="1">
		<hrms:extenditerate id="element" name="showJpPersonForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="10" scope="session">
		<%	if(n%2==0){
        %>
             	<tr class="trShallow">            
        <%}
       	 	else
        	{%>
            	<tr class="trDeep">  
            <%}
            n++;
            %>
            	<logic:equal name="element" property="stateid" value="03">
            		<td align="center" class="RecordRow" nowrap>
			        	&nbsp;
			       </td>
            	</logic:equal>
            	<logic:notEqual name="element" property="stateid" value="03">
			       <td align="center" class="RecordRow" nowrap>
			        	<hrms:checkmultibox name="showJpPersonForm" property="roleListForm.select"  value="ture" indexes="indexes"/>&nbsp;
			       </td>
		       </logic:notEqual>
		        	
		       <%
		       	ShowJpPersonForm showJpPersonForm =(ShowJpPersonForm)session.getAttribute("showJpPersonForm");
		       	ArrayList columnlist = showJpPersonForm.getColumnlist();
		       	ArrayList typelist = showJpPersonForm.getTypelist();
		       	for(int i=0;i<columnlist.size();i++){
		       		String prop = columnlist.get(i).toString();
		       %>
		       <%
		       		if(typelist.get(i).equals("A")||typelist.get(i).equals("M")){
		       		%>
			       	<td align="left" class="RecordRow" nowrap>
			       		<bean:write name="element" property="<%=prop%>" filter="true"/>
			       	</td>
			   <%
			   		}else {
			   %>
			       	<td align="right" class="RecordRow" nowrap>
			       		<bean:write name="element" property="<%=prop%>" filter="true"/>
			       	</td>
			   <%
			   		}
			   }
			   %>
		       <td align="center" class="RecordRow" nowrap>
		       	<a href="/hire/jp_contest/personinfo/jpcard.do?b_search=link&userbase=<bean:write name='element' property='nbase' filter='true'/>&a0100=<bean:write name='element' property='a0100' filter='true'/>&inforkind=1&userpriv=${showJpPersonForm.userpriv}" target="_blank">
	            	<img src="/images/view.gif" border=0>
	           	</a>  
		       </td>
		       <td align="left" class="RecordRow" nowrap>
		       		<a href="/hire/jp_contest/personinfo/showinfodata.do?b_stuff=link&nbase=<bean:write name='element' property='nbase' filter='true'/>&a0100=<bean:write name='element' property='a0100' filter='true'/>&z0700=<bean:write name='element' property='zp_z0700' filter='true'/>  ">查阅</a>
		       </td>
		   </tr>
		</hrms:extenditerate>
		<table  width="100%" align="center" class="RecordRowP">
		  <tr>
		      <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
		     <bean:write name="showJpPersonForm" property="roleListForm.pagination.current" filter="true" />
		     <bean:message key="label.page.sum"/>
		     <bean:write name="showJpPersonForm" property="roleListForm.pagination.count" filter="true" />
		     <bean:message key="label.page.row"/>
		     <bean:write name="showJpPersonForm" property="roleListForm.pagination.pages" filter="true" />
		     <bean:message key="label.page.page"/>
		   </td>
		   <td  align="right" nowrap class="tdFontcolor">
		         <p align="right">
		         <hrms:paginationlink name="showJpPersonForm" property="roleListForm.pagination"
		                  nameId="roleListForm" propertyId="roleListProperty">
		         </hrms:paginationlink>
		   </td>
		  </tr>
		</table>
		</logic:equal>
    
</table>
<table  width="100%" align="center"  >
          <tr>
            <td align="left">
				<logic:notEmpty name="showJpPersonForm" property="templatelist">
				<bean:message key="hire.jp.personinfo.template"/>
		          	<html:select name="showJpPersonForm" property="template" size="1" >
		          		<html:optionsCollection property="templatelist" value="dataValue" label="dataName"/>
		          	</html:select>
				</logic:notEmpty>
         	    <hrms:submit styleClass="mybutton" property="b_approve">
            		<bean:message key="button.approve"/>
	 	    	</hrms:submit>    
	 	    	<hrms:submit styleClass="mybutton" property="b_reject">
            		<bean:message key="button.reject"/>
	 	    	</hrms:submit>  
	 	    	<input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
            </td>
          </tr>          
</table>
<logic:equal name="showJpPersonForm" property="ye" value="2">
<br>
	<table  width="100%" align="center"  >
		<tr>
			<td align="center" class="" nowrap>
				<font size=4><bean:message key="hire.jp.personinfo.info"/></font>
			</td>
		</tr>
	</table>
	</logic:equal>
</html:form>
