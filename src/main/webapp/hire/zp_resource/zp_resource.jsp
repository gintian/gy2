<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<%
   int i = 0;
%>

<html:form action="/hire/zp_resource/zp_resource">
      <br>
      <br>
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="label.zp_job.resource_id"/></td>
		 </tr> 
                  <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.resource_id"/></td>
                     <td align="left"  nowrap valign="center">
                         <hrms:importgeneraldata showColumn="name" valueColumn="plan_id" flag="true"  paraValue="1"
                             sql="select type_id,name from zp_resource_set where 1=? " collection="list" scope="page"/> 
            	             <html:select name="zpresourceForm" property="zpresourcevo.string(type_id)" size="1"> 
            	                 <html:options collection="list" property="dataValue" labelProperty="dataName"/> 
            	             </html:select>
                      </td>  
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.resname"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(name)" styleClass="text6"/>
                     </td>
                 </tr>    
                 <tr class="trShallow1">
                      <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.area"/></td>  
                      <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(area)" styleClass="text6"/>
                      </td> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.scope"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(scope)" styleClass="text6"/>
                     </td>
                 </tr>
                 <tr class="trDeep1"> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.charge"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(charge)" styleClass="text6"/>
                     </td> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.phone"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(phone)" styleClass="text6"/>
                     </td> 
                 </tr> 
                 <tr class="trShallow1"> 
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.linkman"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(linkman)" styleClass="text6"/>
                     </td>   
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.address"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(address)" styleClass="text6"/>
                     </td>
                </tr> 
                <tr class="trDeep1">  
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.postalcode"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(postalcode)" styleClass="text6"/>
                     </td>  
                     <td align="right" nowrap valign="center"><bean:message key="label.zp_resource.http"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:text name="zpresourceForm" property="zpresourcevo.string(http)" styleClass="text6"/>
                     </td>
                </tr>  
               <tr class="trDeep1">
                     <td align="right" nowrap valign="top"><bean:message key="label.zp_resource.description"/></td>
                     <td align="left"  nowrap valign="center" colspan="3">
                          <html:textarea name="zpresourceForm" property="zpresourcevo.string(description)" cols="61" rows="5" styleClass="text6"/>
                      </td>
                   </tr> 
                   
           <tr>
              <td align="center"  nowrap colspan="4">
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.zpresourceForm.target='_self';validate('R','zpresourcevo.string(name)','资源名称','RF','zpresourcevo.string(charge)','所需费用','R','zpresourcevo.string(area)','所在地区','R','zpresourcevo.string(scope)','影响范围','R','zpresourcevo.string(phone)','电话','R','zpresourcevo.string(linkman)','联系人','R','zpresourcevo.string(address)','通讯地址','R','zpresourcevo.string(postalcode)','邮政编码','R','zpresourcevo.string(http)','网址','R','zpresourcevo.string(description)','简介');return (document.returnValue && ifqrbc());"><bean:message key="button.save"/></hrms:submit>
	 	      <hrms:submit styleClass="mybutton" property="br_return"><bean:message key="button.return"/></hrms:submit>    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>               
        
 </table>
</html:form>
