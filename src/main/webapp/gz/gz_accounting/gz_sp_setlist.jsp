<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.hjsj.sys.VersionControl"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>				 
<%
    String noComparisonColumn="false";  //薪资发放不显示发放次数和变动比对列
	if(SystemConfig.getPropertyValue("noComparisonColumn")!=null&&SystemConfig.getPropertyValue("noComparisonColumn").equalsIgnoreCase("true"))
		noComparisonColumn="true";

  UserView userView =(UserView)session.getAttribute(WebConstant.userView);
   int versionFlag = 0;
		if (userView != null)
			versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		
VersionControl ver = new VersionControl();

	int i=0;
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html:form action="/gz/gz_accounting/gz_sp_setlist">
<br>
<table width='90%' align='center' ><tr><td>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
        <tr>
         <% if(noComparisonColumn.equals("false")){ %>
         <td align="center" class="TableRow" nowrap >
		   <bean:message key="report.number"/>
	     </td>          
	     <% } %>
         <td align="center" class="TableRow" nowrap >
           <logic:equal name="accountingForm" property="gz_module" value="0">
		     <bean:message key="label.gz.salarytype"/>
		   </logic:equal>		    
           <logic:equal name="accountingForm" property="gz_module" value="1">
		     <bean:message key="sys.res.ins_set"/>
		   </logic:equal>	
	     </td>         

         <td align="center" class="TableRow" nowrap >
			<bean:message key="kh.field.opt"/>
         </td>
           		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="accountingForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"   onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');" >
          <%}
          else
          {%>
          <tr class="trDeep"  onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');">
          <%
          }
          i++;          
          %>  
           <% if(noComparisonColumn.equals("false")){ %>
            <td align="left" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="salaryid" filter="true"/>
	   		</td>
	   	   <% } %>        
            <td align="left" class="RecordRow" nowrap>
              <a href="<bean:write name="element" property="collectPoint" filter="true"/>?b_query=link&ori=0&zjjt=1&returnflag=${accountingForm.returnvalue}&salaryid=<bean:write name="element" property="salaryid" filter="true"/>&gz_module=<bean:write name="element" property="gz_module" filter="true"/>">
				&nbsp;<bean:write name="element" property="cname" filter="true"/>
			  </a>
	    </td>
 
            <td align="center" class="RecordRow" nowrap>
				<a href="<bean:write name="element" property="collectPoint" filter="true"/>?b_query=link&ori=0&zjjt=1&returnflag=${accountingForm.returnvalue}&salaryid=<bean:write name="element" property="salaryid" filter="true"/>&gz_module=<bean:write name="element" property="gz_module" filter="true"/>"><bean:write name="element" property="isCurr_user" filter="true"/></a>
			</td>            
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="100%"  class='RecordRowP' align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="accountingForm" property="setlistform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="accountingForm" property="setlistform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="accountingForm" property="setlistform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="accountingForm" property="setlistform.pagination"
				nameId="setlistform" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  align="center" width="100%" ><tr><td align='center' >
<logic:equal name="accountingForm" property="gz_module" value="0">
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="accountingForm"/>
</logic:equal>
<logic:equal name="accountingForm" property="gz_module" value="1">
<hrms:tipwizardbutton flag="insurance" target="il_body" formname="accountingForm"/>
</logic:equal>
</td></tr></table>
</td></tr></table>

</html:form>

<script language='javascript' >
var flag=1;
function gz_collect(salaryid,gz_module)
{
	if(flag==1)
	{
		document.accountingForm.action="/gz/gz_accounting/gz_collect_orgtree.do?b_tree=link&salaryid="+salaryid+"&gz_module="+gz_module; 
		document.accountingForm.submit();
		flag=2;
	}
}


</script>
