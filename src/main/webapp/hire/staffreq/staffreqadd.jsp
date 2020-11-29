<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
 function validate1()
  {
          var tag=true;    
          var valueInputs=document.getElementsByName("zpgathervo.string(valid_date)");
          var dobj=valueInputs[0];
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  } 
     return tag;   
  }
  function change_pos()
   {
    hireManageForm.action="/hire/staffreq/staffreqadd.do?b_org=link&pretype=UN";
    hireManageForm.submit();
   }
</script>
<%
   int i = 0;
%>

<html:form action="/hire/staffreq/staffreqadd">
      <br>
      <br>
      <fieldset align="center" style="width:90%;">
      <table width="700" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
                <tr>
		 	<td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="hire.requirement.unit"/>(<bean:write name="hireManageForm" property="zpgathervo.string(gather_id)" />)</td>
		 </tr> 
		 <tr>
		    <td><html:hidden name="hireManageForm" property="gather_id_value" styleClass="text6"/></td>
		 </tr>
                  <tr class="trDeep1">

                     <logic:equal name="hireManageForm" property="managepriv" value="UM">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="hireManageForm" property="orgparentcode"/> 
                          <html:text name="hireManageForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                      </td>
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(dept_id)" styleClass="text6"/>  
                          <html:hidden name="hireManageForm" property="deptparentcode"/> 
                          <html:text name="hireManageForm" property="dept_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif"/>
                      </td>
                   </tr>
                   </logic:equal>
                   <logic:notEqual name="hireManageForm" property="managepriv" value="UM">
                     <logic:notEqual name="hireManageForm" property="managepriv" value="UN">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="hireManageForm" property="orgparentcode"/> 
                          <html:text name="hireManageForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif"/>
                      </td>
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(dept_id)" styleClass="text6"/>  
                          <html:hidden name="hireManageForm" property="deptparentcode"/> 
                          <html:text name="hireManageForm" property="dept_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_id_value","zpgathervo.string(dept_id)",hireManageForm.deptparentcode.value);'/>
                      </td>
                   </tr>
                     </logic:notEqual>
                     <logic:equal name="hireManageForm" property="managepriv" value="UN">
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.org_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(org_id)" styleClass="text6"/>
                          <html:hidden name="hireManageForm" property="orgparentcode"/> 
                          <html:text name="hireManageForm" property="org_id_value" readonly="true" styleClass="text6" onchange="change_pos();"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UN","org_id_value","zpgathervo.string(org_id)",hireManageForm.orgparentcode.value);'/>
                      </td>
                     
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.dept_id"/></td>
                     <td align="left"  nowrap valign="center">
                          <html:hidden name="hireManageForm" property="zpgathervo.string(dept_id)" styleClass="text6"/>  
                          <html:hidden name="hireManageForm" property="deptparentcode"/> 
                          <html:text name="hireManageForm" property="dept_id_value" readonly="true" styleClass="text6"/>
                          <img src="/images/code.gif" onclick='javascript:openInputCodeDialogOrg_1("UM","dept_id_value","zpgathervo.string(dept_id)",hireManageForm.deptparentcode.value);'/>
                      </td>
                   </tr>
                     </logic:equal>
                   </logic:notEqual>
                   <tr class="trDeep1">
                     <!--<td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.gather_id"/></td>
                     <td align="left"  nowrap valign="center">
                           <html:text name="hireManageForm" property="zpgathervo.string(gather_id)" disabled = "true"  styleClass="text6"/>
                      </td>  -->
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.valid_date"/></td>
                     <td align="left"  nowrap valign="center">
                         <html:text name="hireManageForm" property="zpgathervo.string(valid_date)" styleClass="text6"/>
                      </td>
                     <td align="right" nowrap valign="center"><bean:message key="lable.hiremanage.gather_type"/></td>
                      <td align="left"  nowrap valign="center" >
                          <logic:equal name="hireManageForm" property="gather_type" value="0">
                             <input type="text" name="gather_type0" value="<bean:message key="label.hiremanage.gather_type0"/>"  readonly="readonly" class="text6">
                          </logic:equal>
                           <logic:equal name="hireManageForm" property="gather_type" value="1">
                               <input type="text" name="gather_type1" value="<bean:message key="label.hiremanage.gather_type1"/>"  readonly="readonly" class="text6">
                          </logic:equal>
                       </td>
                      <!--    <td align="left"  nowrap valign="center" colspan="4">
                        <html:select name="hireManageForm" property="zpgathervo.string(gather_type)">
                           <html:option value="0"><bean:message key="label.hiremanage.gather_type0"/></html:option>
                           <html:option value="1"><bean:message key="label.hiremanage.gather_type1"/></html:option>
                        </html:select>   
                      </td> -->   
                   </tr>                    
                   
           <tr>
              <td align="center"  nowrap colspan="4">                		
	 	     <hrms:submit styleClass="mybutton" property="b_save" onclick="document.hireManageForm.target='_self';validate('R','org_id_value','单位名称','R','dept_id_value','部门名称','R','zpgathervo.string(valid_date)','生效日期');return (document.returnValue && validate1() && ifqrbc());">
	 	        <bean:message key="button.save"/>
	 	     </hrms:submit>	 	    
             </td>
         </tr>                                                        
          
          </table>     
        </td>
      </tr>   
            
      <tr class="list3">
            <td align="center" colspan="2">
		&nbsp           
            </td>
          </tr>   

          <tr>
          <table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		 <bean:message key="column.select"/>&nbsp;
            </td>         
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.pos_id"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.amount"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.type"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.hiremanage.reason"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td> 	    		        	        	        
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="hireManageForm" property="gatherPosForm.list" indexes="indexes"  pagination="gatherPosForm.pagination" pageCount="10" scope="session">
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
            <td align="center" class="RecordRow" nowrap>
	   	 <hrms:checkmultibox name="hireManageForm" property="gatherPosForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>           
         
            <td align="left" class="RecordRow" nowrap>
               <hrms:codetoname codeid="@K" name="element" codevalue="string(pos_id)" codeitem="codeitem" scope="page"/>  	      
               <bean:write name="codeitem" property="codename" />&nbsp;
	    </td>
            <td align="right" class="RecordRow" nowrap>
                    <bean:write  name="element" property="string(amount)" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
              <logic:equal name="element" property="string(type)" value="01">
                    <bean:message key="label.hiremanage.type1"/>&nbsp;
              </logic:equal>
               <logic:notEqual name="element" property="string(type)" value="01">
                      <bean:message key="label.hiremanage.type2"/>&nbsp;
                 </logic:notEqual>       
	    </td> 	                
            <td align="left" class="RecordRow" nowrap width="100" style="word-break:break-all">
                    <bean:write  name="element" property="string(reason)" filter="false"/>&nbsp;
	    </td>
	   <td align="center" class="RecordRow" nowrap>
	     <logic:equal name="hireManageForm" property="flag" value="1">
	       <logic:equal name="hireManageForm" property="flag_mid" value="1">
		<a href="/hire/staffreq/add_pos.do?b_query=link&a_id=<bean:write name="element" property="string(gather_id)" filter="true"/>&pos_id=<bean:write name="element" property="string(pos_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	
	       </logic:equal>     
	     </logic:equal>
	     <logic:notEqual name="hireManageForm" property="flag" value="1">
		<a href="/hire/staffreq/add_pos.do?b_query=link&a_id=<bean:write name="element" property="string(gather_id)" filter="true"/>&pos_id=<bean:write name="element" property="string(pos_id)" filter="true"/>"><img src="/images/edit.gif" border=0></a>             	   
	     </logic:notEqual>
	    </td>	   	    
               		        	        	        
          </tr>
        </hrms:extenditerate>
   	    
     </table>
   
     <table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
					<bean:write name="hireManageForm" property="gatherPosForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="hireManageForm" property="gatherPosForm.pagination"
				nameId="gatherPosForm" propertyId="gatherPosProperty">
				</hrms:paginationlink>
			</td>
		</tr>
    </table>

     <table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="hireManageForm" property="flag" value="1">
              <logic:equal name="hireManageForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_pos_add" disabled = "true">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos_delete" disabled = "true">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	      </logic:equal>
	      <logic:notEqual name="hireManageForm" property="flag_mid" value="0">
         	<hrms:submit styleClass="mybutton" property="b_pos_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	      </logic:notEqual>
	    </logic:equal> 
	    <logic:notEqual name="hireManageForm" property="flag" value="1">
         	<hrms:submit styleClass="mybutton" property="b_pos_add" >
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
	 	<hrms:submit styleClass="mybutton" property="b_pos_delete" onclick="return ifdel()">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
	    </logic:notEqual>	
         	
        
            </td>
          </tr>          
    </table>
 </fieldset>  
    </tr>             
    <tr>
       <td width="100%" align="center">
       <br>
       <br>
           <logic:equal name="hireManageForm" property="flag" value="1">
                   <logic:equal name="hireManageForm" property="flag_mid" value="0">
         	      <hrms:submit styleClass="mybutton" property="b_submit" disabled = "true">
            	          <bean:message key="lable.welcomeinv.sumbit"/>
	 	     </hrms:submit>
	           </logic:equal>
	 	   <logic:notEqual name="hireManageForm" property="flag_mid" value="0">
         	     <hrms:submit styleClass="mybutton" property="b_submit" onclick = "return ifqrtj()">
            		<bean:message key="lable.welcomeinv.sumbit"/>
	 	     </hrms:submit>
	           </logic:notEqual>	
	        </logic:equal> 
	        <logic:notEqual name="hireManageForm" property="flag" value="1">
         	    <hrms:submit styleClass="mybutton" property="b_submit" onclick = "return ifqrtj()">
            		<bean:message key="lable.welcomeinv.sumbit"/>
	 	    </hrms:submit>
	        </logic:notEqual>
	         <hrms:submit styleClass="mybutton" property="b_return"><bean:message key="button.return"/></hrms:submit>    
       </td>
    </tr>
 </table>

</html:form>
