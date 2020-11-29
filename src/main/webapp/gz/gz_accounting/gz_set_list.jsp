<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView"%>
				 
<%
    String noComparisonColumn="false";  //薪资发放不显示发放次数和变动比对列
	if(SystemConfig.getPropertyValue("noComparisonColumn")!=null&&SystemConfig.getPropertyValue("noComparisonColumn").equalsIgnoreCase("true"))
		noComparisonColumn="true";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}

 %>				 
<script type="text/javascript">
<!--
function changeInfo(gz_module,salaryid,flow_flag)
{
   var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("fromflag","0"); 
	var request=new Request({method:'post',asynchronous:true,onSuccess:openInfoPage,functionId:'3020072010'},hashvo);
}
function openInfoPage(outparameters){
	var add=outparameters.getValue("add");
	var del=outparameters.getValue("del");
	var info=outparameters.getValue("info");
	var stop=outparameters.getValue("stop");
	var salaryid=outparameters.getValue("salaryid");
	var gz_module=outparameters.getValue("gz_module");
	var fromflag=outparameters.getValue("fromflag");
	var cname=getDecodeStr(outparameters.getValue("cname"));
	var flow_flag=outparameters.getValue("flow_flag");
	var error=outparameters.getValue("error");
	if(typeof(error)=='undefined'||error=='1')
		return;
	else if(add=='0' && del=='0' && info=='0' && stop=='0')
	{
	    alert(salaryid+"."+cname+"中没有新增，减少，或信息有变化的人员！");
	    return;
	}
	
	
	accountingForm.action="/gz/gz_accounting/change_list.do?b_query=link&flow_flag="+flow_flag+"&fromflag="+fromflag+"&add="+add+"&del="+del+"&info="+info+"&stop="+stop+"&salaryid="+salaryid+"&gz_module="+gz_module;
	accountingForm.submit();
}
//-->
</script>
<%
	int i=0;
	String flow_flag=request.getParameter("flow_flag");
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<html:form action="/gz/gz_accounting/gz_set_list">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<table align='center' width='90%' ><tr><td>

<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
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
		    <bean:message key="label.gz.appdate"/>
	     </td>
	     <% if(noComparisonColumn.equals("false")){ %>
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.count"/>
	     </td>
         <td align="center" class="TableRow" nowrap >
				<bean:message key="label.gz.changeinfo"/>
         </td> 
       
         
         <td align="center" class="TableRow" nowrap >
			<bean:message key="label.gz.operation"/>
         </td>
              <% } %>
            <!-- 
            <td align="center" class="TableRow">
				<bean:message key="label.gz.cond"/>
            </td>  
             -->                            		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="accountingForm" property="setlistform.list" indexes="indexes"  pagination="setlistform.pagination" pageCount="15" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"  onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');" >
          <%}
          else
          {%>
          <tr class="trDeep"  onmouseout="changTRColor(this,'');" onmouseover="changTRColor(this,'#FFF8D2');" >
          <%
          }
          i++;          
          %>  
           <% if(noComparisonColumn.equals("false")){ %>
            <td align="left" class="RecordRow" nowrap>
            &nbsp;<bean:write name="element" property="salaryid" filter="true"/>
	    </td>        
	    <%  } %>
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;
                   <a href="/gz/gz_accounting/gz_org_tree.do?b_query=link&zjjt=1&ff_bosdate=<bean:write  name="element" property="appdate" filter="true"/>&ff_count=<bean:write  name="element" property="count" filter="true"/>&salaryid=<bean:write name="element" property="salaryid" filter="true"/>">
                   <bean:write name="element" property="cname" filter="true"/>
                   </a>
	    </td>
         
            <td align="left" class="RecordRow">
                    &nbsp;<bean:write  name="element" property="appdate" filter="true"/>
            </td>
         <% if(noComparisonColumn.equals("false")){ %>    
        <td align="center" class="RecordRow" nowrap>
     		 &nbsp;<bean:write  name="element" property="count" filter="true"/>
	    </td>   
	     <td align="center" class="RecordRow" nowrap>
	     <logic:notEqual name="element" property="royalty_valid" value="1">
	      <hrms:priv func_id="3240201,3250201,3270201,3271201">	 	
			 <a href="javascript:changeInfo('<bean:write name="accountingForm" property="gz_module" filter="true"/>','<bean:write name="element" property="salaryid" filter="true"/>','<%=flow_flag%>');"><img src="/images/edit.gif" border=0></a>     		 
	   	  </hrms:priv>
	   	 </logic:notEqual>
	    </td> 
	  
	    
            <td align="center" class="RecordRow" nowrap>
				<a href="/gz/gz_accounting/gz_org_tree.do?b_query=link&zjjt=1&ff_bosdate=<bean:write  name="element" property="appdate" filter="true"/>&ff_count=<bean:write  name="element" property="count" filter="true"/>&salaryid=<bean:write name="element" property="salaryid" filter="true"/>"><img src="/images/edit.gif" border=0></a>
			</td>	
         <%  } %>    
	    <!-- 
            <td align="left" class="RecordRow" nowrap>
     		 <bean:write  name="element" property="domain" filter="true"/>            	
	    </td>
	         --> 	 	    	            
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
<!-- 
<hrms:priv func_id="324020503,325020503,327020503,327120503">
<OBJECT id="MusterPreview1"
	  classid="clsid:3343286A-FE6B-4A46-8CF5-D0C233752E0E"
	  codebase="/cs_deploy/axmusterpreview.cab#version=1,0,81,0"
	  width="0"
	  height="0"
	  align="center"
	  hspace="0"
	  vspace="0">
</OBJECT>
</hrms:priv>
 -->
</html:form>
