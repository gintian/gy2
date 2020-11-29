<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hjsj.hrms.actionform.general.statics.StaticForm" %>
<script language="javascript">
  function save()
  {
    staticForm.action="/general/static/singlestatic/showspecial.do?b_save=link";
    staticForm.submit();
    window.close();
  }
  function winhref1(url,target,a0100)
  {
   if(a0100=="")
      return false;   
	newwin=window.open(url+"&a0100="+a0100,target,"toolbar=no,location=no,directories=0,status=no,menubar=no,scrollbars=no,resizable=no","true");
  }    
</script>
<html:form action="/general/static/singlestatic/showspecial">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<logic:equal name="staticForm" property="infor_Flag" value="1"> 
  <td align="center" class="TableRow" nowrap>
         人员库&nbsp;          	
  </td>
  </logic:equal>
<logic:notEqual name="staticForm" property="infor_Flag" value="3">
  <td align="center" class="TableRow" nowrap>
         <hrms:fieldtoname name="staticForm" fieldname="B0110" fielditem="fielditem"/>
	 <bean:write name="fielditem" property="dataValue" />&nbsp;
  </td>
  </logic:notEqual>
  <logic:equal name="staticForm" property="infor_Flag" value="3">  
  <td align="center" class="TableRow" nowrap>
         <hrms:fieldtoname name="staticForm" fieldname="E01A1" fielditem="fielditem"/>
	 <bean:write name="fielditem" property="dataValue" />&nbsp;
  </td>
  </logic:equal>
  <logic:equal name="staticForm" property="infor_Flag" value="1"> 
  <td align="center" class="TableRow" nowrap>
         <hrms:fieldtoname name="staticForm" fieldname="A0101" fielditem="fielditem"/>
	 <bean:write name="fielditem" property="dataValue" />&nbsp;          	
  </td>
  </logic:equal>
  <logic:equal name="staticForm" property="select" value="2"> 
  <td align="center" class="TableRow" nowrap id="compute">
	  <bean:write name="staticForm"  property="itemdesc"/>[<bean:message key="kq.formula.min"/>]           	
  </td>
  </logic:equal>
  <logic:equal name="staticForm" property="select" value="3"> 
  <td align="center" class="TableRow" nowrap id="compute">
	  <bean:write name="staticForm"  property="itemdesc"/>[<bean:message key="kq.formula.max"/>]           	
  </td>
  </logic:equal>
</tr>
</thead>
<%
   StaticForm staticForm=(StaticForm)session.getAttribute("staticForm");
   String formatstr=staticForm.getFormatstr();
 %>
<hrms:paginationdb id="element" name="staticForm" sql_str="staticForm.strsql" table="" where_str="staticForm.where_str" columns="staticForm.columns" pagerows="10" page_id="pagination">
<tr>
	<logic:equal name="staticForm" property="infor_Flag" value="1"> 
  <td align="left" class="RecordRow" nowrap>
  	<bean:define id="db" name="element" property="db"></bean:define>
         &nbsp;<%=com.hrms.frame.utility.AdminCode.getCodeName("@@",(String)pageContext.getAttribute("db")) %>&nbsp;         	
  </td>
  </logic:equal>
    <logic:notEqual name="staticForm" property="infor_Flag" value="3">
      <td align="left" class="RecordRow" nowrap>&nbsp;
         <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
         <bean:write name="codeitem" property="codename" />
         <hrms:codetoname codeid="UM" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
         <bean:write name="codeitem" property="codename" />         
      </td>
    </logic:notEqual>
      <logic:equal name="staticForm" property="infor_Flag" value="3">
        <td align="left" class="RecordRow" nowrap>&nbsp;
           <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
           <bean:write name="codeitem" property="codename" />
        </td>
      </logic:equal>
      <logic:equal name="staticForm" property="infor_Flag" value="1"> 
      <td align="left" class="RecordRow" nowrap>&nbsp;
         <a onclick="javascript:winhref1('/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="element" property="db" filter="true"/>&flag=notself&returnvalue=100001','_blank','<bean:write name="element" property="b" filter="true"/>');" href="javascript:void(0);"  target="">
         <bean:write name="element" property="a0101" filter="false"/></a>&nbsp;                 
      </td>
      </logic:equal>
      <td align="right" class="RecordRow" nowrap>      
        <%  
          if(formatstr!=null&&formatstr.length()>0)
          {
             LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element"); 
             String value=(String)abean.get("a");                     
             if(value!=null&&value.length()>0&&formatstr.indexOf("#")!=-1)
             {
                 pageContext.setAttribute("floatval",Float.valueOf(value));
                 %>
                    <bean:write name="floatval"  format="<%=formatstr%>" />&nbsp;
                 <% 
             }
           }else{
         %>     
            <bean:write name="element"  property="a" filter="false" />&nbsp;     
         <%}%>            
      </td>
  
</tr>
</hrms:paginationdb> 
	    
</table>
<table  width="70%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="staticForm" property="pagination" nameId="staticForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center">
            <logic:equal name="staticForm" property="infor_Flag" value="1"></logic:equal>
         	  <input type="button" name="b_save" value="<bean:message key="button.save"/>" class="mybutton" onclick="save();">
         	
         	<input type="button" name="b_save" value="<bean:message key="button.close"/>" class="mybutton" onclick="javascript:window.close();">
            </td>
          </tr>          
</table>
</html:form>
