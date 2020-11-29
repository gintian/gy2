<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
         //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
%>
<%
	  int i=0;
%>
<script language="javascript">
    function exeButtonAction(actionStr,target_str)
   {
       target_url=actionStr;
       window.open(target_url,target_str); 
   }
   function deleteC()
   {
      if(ifdel())
      {
         dutyInfoForm.action="/workbench/dutyinfo/searchmediainfolist.do?b_delete=link";
         dutyInfoForm.submit();
      }
   }
</script>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">

<html:form action="/workbench/dutyinfo/searchmediainfolist">
<br>
<br>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <tr>
     <td align="center"  nowrap>
     <br>
        (<logic:equal name="dutyInfoForm" property="kind" value="0">
        	职位:
        </logic:equal>
         <logic:equal name="dutyInfoForm" property="kind" value="2">
         	单位：
         </logic:equal>
        <bean:write  name="dutyInfoForm" property="codeitemdesc" filter="true"/>&nbsp;
        )
     </td>
  </tr>
</table>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
         
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
              <bean:message key="column.select"/>&nbsp;
             </td>
           <!-- <td align="center" class="TableRow" nowrap>
               <bean:message key="conlumn.mediainfo.info_id"/>&nbsp;
             </td>-->            	
             <td align="center" class="TableRow" nowrap>
                   <bean:message key="info.appleal.statedesc"/>&nbsp;
              </td>        
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_title"/>&nbsp;
             </td>
             <td align="center" class="TableRow" nowrap>
                <bean:message key="conlumn.mediainfo.info_sort"/>&nbsp;
             </td>   
             <hrms:priv func_id="03050103,2606103"> 
           	   <td align="center" class="TableRow" nowrap>
		      <bean:message key="label.edit"/>            	
                   </td>  
	      </hrms:priv>         		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="dutyInfoForm" property="orgInfoForm.list" indexes="indexes"  pagination="orgInfoForm.pagination" pageCount="10" scope="session">
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
               <hrms:checkmultibox name="dutyInfoForm" property="orgInfoForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>     
             <td align="center" class="RecordRow" nowrap>
               <logic:equal name="element" property="state" value="0">
                  <bean:message key="info.appleal.state0"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="1">
                  <bean:message key="info.appleal.state1"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="2">
                  <bean:message key="info.appleal.state2"/>&nbsp;
               </logic:equal>
               <logic:equal name="element" property="state" value="3">
                  <bean:message key="info.appleal.state3"/>&nbsp;
               </logic:equal>
               <logic:notEqual name="element" property="state" value="0">
                 <logic:notEqual name="element" property="state" value="1">
                   <logic:notEqual name="element" property="state" value="2">
                      <logic:notEqual name="element" property="state" value="3">
                        <bean:message key="info.appleal.state0"/>&nbsp;
                      </logic:notEqual>
                    </logic:notEqual>
                  </logic:notEqual>
               </logic:notEqual>
            </td>
            <td align="left" class="RecordRow" nowrap>
            	<a href="/workbench/duty/showmediainfo?kind=<bean:write  name="dutyInfoForm" property="kind" filter="true"/>&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>" ><bean:write  name="element" property="title" filter="true"/></a>&nbsp;
            	<!-- 
            	<logic:equal name="dutyInfoForm" property="kind" value="0">
            		<a href="/workbench/duty/showmediainfo?kind=<bean:write  name="dutyInfoForm" property="kind" filter="true"/>&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>" ><bean:write  name="element" property="title" filter="true"/></a>&nbsp;
            	</logic:equal>        
            	<logic:notEqual name="dutyInfoForm" property="kind" value="0">        
               		<a href="/workbench/duty/showmediainfo?kind=<bean:write  name="dutyInfoForm" property="kind" filter="true"/>&usernumber=<bean:write  name="element" property="a0100" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>" ><bean:write  name="element" property="title" filter="true"/></a>&nbsp;
               	</logic:notEqual>
               	-->
            </td>
             <td align="left" class="RecordRow" nowrap>                
               <bean:write  name="element" property="flag" filter="true"/>&nbsp;
            </td>            
              <td align="center" class="RecordRow" nowrap>
              <hrms:priv func_id="01030103"> 
                 <a href="/workbench/dutyinfo/searchmediainfolist.do?br_update=link&kind=<bean:write  name="dutyInfoForm" property="kind" filter="true"/>&code=<bean:write  name="dutyInfoForm" property="code" filter="true"/>&i9999=<bean:write  name="element" property="i9999" filter="true"/>&filetitle=<bean:write  name="element" property="title" filter="true"/>"  target="mil_body"><img src="/images/edit.gif" border=0></a>
	          </hrms:priv> 
	           </td>
          </tr>
        </hrms:extenditerate>
         
</table>
<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="dutyInfoForm" property="orgInfoForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/> 
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="dutyInfoForm" property="orgInfoForm.pagination"
				nameId="orgInfoForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="left">
          <tr>
            <td align="center">
            	<hrms:submit styleClass="mybutton" property="br_add">
            		<bean:message key="button.insert"/>
	 	   		</hrms:submit>  
	 	       	<input type="button" name="b_delete" value='<bean:message key="button.delete"/>' onclick="deleteC();" class="mybutton"> 
	 	       	<logic:equal name="dutyInfoForm" property="kind" value="0">
	 	        	<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/dutyinfo/editorginfodata.do?b_search=link&orgtype=org&code=${dutyInfoForm.code}&kind=${dutyInfoForm.kind}','mil_body')"> 	 	
	 	        </logic:equal>
	 	        <logic:equal name="dutyInfoForm" property="kind" value="2">
         			<input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="exeButtonAction('/workbench/orginfo/editorginfodata.do?b_search=link&code=${dutyInfoForm.code}&kind=${dutyInfoForm.kind}&orgtype=org','mil_body')"> 	 	
         		</logic:equal>
	     </td>
          </tr>          
 </table>
</html:form>
