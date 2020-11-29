<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<%int i=0;%>
<script language="javascript">
function send(id)
{
	posRoleInfoForm.action = "/pos/roleinfo/pos_dept_post.do?b_up=link&codesetid="+id;
	posRoleInfoForm.submit();
}
function openword(id)
{
	var	iframe_url = "/pos/roleinfo/pos_dept_post.do?b_open=link&codesetid="+id;
	var return_vo= window.open(iframe_url, 'newwindow', 
        "height=300, width=500, top=0,left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no");
}
 function returnTO()
  {
     window.location="/templates/attestation/police/wizard.do?br_work_wizard=link";
  }
</script>

<html:form action="/pos/roleinfo/pos_dept_post">
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
  <tr>
    <td>
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           	 <td align="center" class="TableRow" nowrap>
                <bean:message key="column.sys.dept"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="tree.kkroot.kkdesc"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="lable.channel_detail.title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
             	<logic:equal value="L" name="posRoleInfoForm" property="modular">
             		<bean:message key="police.workinfo.lianzheng"/>&nbsp;
             	</logic:equal>
             	<logic:notEqual value="L" name="posRoleInfoForm" property="modular">
                	<bean:message key="hire.parameterSet.positionDescrible"/>&nbsp;
                </logic:notEqual>
             </td>                  
             <td align="center" class="TableRow" nowrap>
		  <bean:message key="hire.jp.apply.upload"/>            	
             </td>        
             <td align="center" class="TableRow" nowrap>
                <bean:message key="jx.khplan.createdate"/>&nbsp;
             </td>
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="posRoleInfoForm" property="rolelistForm.list" indexes="indexes"  pagination="rolelistForm.pagination" pageCount="${posRoleInfoForm.pagerows}" scope="session">
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
          <td align="left" class="RecordRow" nowrap>                
                <logic:notEqual name="element" property="codesetid" value="UM">
                &nbsp;	<hrms:codetoname codeid="UM" name="element" codevalue="parentid" codeitem="codeitem" scope="page" uplevel="${posRoleInfoForm.uplevel}"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
                </logic:notEqual>
            </td>
            <td align="left" class="RecordRow" nowrap>              
                <logic:notEqual name="element" property="codeitemid" value="@K">
                &nbsp;	 <hrms:codetoname codeid="@K" name="element" codevalue="codeitemid" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
                </logic:notEqual>
                <logic:notEqual name="element" property="codesetid" value="UN">
               &nbsp; 	<hrms:codetoname codeid="UN" name="element" codevalue="codeitemid" codeitem="codeitem" scope="page"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
                </logic:notEqual>
                <logic:notEqual name="element" property="codesetid" value="UM">
              &nbsp;  	<hrms:codetoname codeid="UM" name="element" codevalue="codeitemid" codeitem="codeitem" scope="page" uplevel="${posRoleInfoForm.uplevel}"/>         
           			<bean:write name="codeitem" property="codename" />&nbsp;
                </logic:notEqual>
                
            </td>
             <td align="left" class="RecordRow" nowrap>                
             &nbsp;   <bean:write  name="element" property="title" filter="true"/>&nbsp;
            </td>
           <td align="center" class="RecordRow" nowrap>
           		<logic:equal value="L" name="posRoleInfoForm" property="modular">
           			<hrms:priv func_id="570702">
	           			<logic:notEqual name="element" property="ext" value="0">
		           			<a href="/pos/roleinfo/pos_dept_post?usertable=k00&usernumber=<bean:write  name="element" property="codeitemid" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
		           		</logic:notEqual>
	           		</hrms:priv>
           		</logic:equal>
           		<logic:notEqual value="L" name="posRoleInfoForm" property="modular">
	           		<logic:notEqual name="element" property="ext" value="0">
	           			
	           			  <a href="/pos/police/open_police_book?tablename=k00&filetype=e01a1&filevalue=<bean:write  name="element" property="codeitemid" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>"><img src="/images/view.gif" border=0></a>
	           		  
	           		</logic:notEqual>
           		</logic:notEqual>
	   		</td>
	   		<td align="center" class="RecordRow" nowrap>
	   			<logic:equal value="L" name="posRoleInfoForm" property="modular">
	   				<hrms:priv func_id="570701">
	   					<a href="javascript:send('<bean:write  name="element" property="codeitemid" filter="true"/>')"  target="mil_body"><img src="/images/link.gif" border=0></a>
	   				</hrms:priv>
	   			</logic:equal>
	   			<logic:notEqual value="L" name="posRoleInfoForm" property="modular">                
		   			<hrms:priv func_id="52001"> 
		                <a href="javascript:send('<bean:write  name="element" property="codeitemid" filter="true"/>')"  target="mil_body"><img src="/images/link.gif" border=0></a>
		            </hrms:priv>
	            </logic:notEqual>
            </td>
            <td align="center" class="RecordRow" nowrap>                
               &nbsp; <bean:write  name="element" property="createtime" filter="true"/>&nbsp;
            </td>
                           	    		        	        	        
          </tr>
        </hrms:extenditerate>
         
</table>
    </td>
  </tr>
<tr>
  <td>
    <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="posRoleInfoForm" pagerows="${posRoleInfoForm.pagerows}" property="rolelistForm.pagination" scope="session" refresh="false"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="posRoleInfoForm" property="rolelistForm.pagination"
				nameId="rolelistForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
     </table>
  </td>
</tr>
<tr>
  <td>
    <table  width="100%" align="center" >
  <tr>
   <td align="center">
      <logic:equal value="wizard" name="posRoleInfoForm" property="returnvalue">
         <input type='button' name='b_save' value='返回' onclick='returnTO();' class='mybutton'>
      </logic:equal>
      
   </td>
  </tr>
</table>
  </td>
</tr>
</table>
</html:form>