<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.hire.employActualize.EmployResumeForm,
				 org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.FieldItem" %>
<%
EmployResumeForm employResumeForm=(EmployResumeForm)session.getAttribute("employResumeForm");	
ArrayList columnList=employResumeForm.getColumnList();
 %>
<script type="text/javascript">
<!--
function extendData()
{
var val=employResumeForm.score.value;
if(trim(val)=='')
{
   alert("分数值不能为空！");
   return;
}
   var myReg =/^(-?\d+)(\.\d+)?$/
	if(!myReg.test(val)) 
	{
	   alert("分数值请输入数字！");
	   return;
	}
   employResumeForm.action="/hire/employActualize/appointpassmark.do?b_init=link&opt=1";
   employResumeForm.submit();
}
//-->
</script>
<<hrms:themes></hrms:themes>
 <html:form action="/hire/employActualize/appointpassmark">
 <br>
 <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
 <td align="center" class="RecordRow">
  <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
  <tr>
  <td align="left">
     分数：<html:text name="employResumeForm" property="score"/>&nbsp;&nbsp;<input type="button" name="exd" class="mybutton" value="筛选" onclick="extendData();"/>
  </td>
  </tr>
  </table>
 </td>
 </tr>
 <tr>
 <td width="100%" class="RecordRow" align="left" valign="top">
  <div id="dataArea" style='overflow:auto;width:700px;height:360px'>
   <table width="100%" style="margin-top:-1" class="ListTable"  cellspacing="0"  align="center" cellpadding="0">
      <thead>
         <tr>
           <td align="center" class="TableRow" nowrap>
           单位名称
           </td>
             <td align="center" class="TableRow" nowrap>
           部门
           </td>
             <td align="center" class="TableRow" nowrap>
           职位名称
           </td>
            <td align="center" class="TableRow" nowrap>
           通过资格审查且过线人数
           </td>
           <%
                   for(int i=0;i<columnList.size();i++)
                   {
                      FieldItem item = (FieldItem)columnList.get(i);
                      %>
                       <td align="center" class="TableRow" nowrap>
                          <%=item.getItemdesc()%>
                       </td>
                      <%
                   }
            %>
         </tr>
      </thead>
      <%int j=0; %>
       <hrms:extenditerate id="element" name="employResumeForm" property="dataListForm.list" indexes="indexes"  pagination="dataListForm.pagination" pageCount="100" scope="session">
      <%if(j%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
	      <td align="left" class="RecordRow" nowrap>
	        &nbsp;<bean:write name="element" property="un"/>
	      </td>
	      <td align="left" class="RecordRow" nowrap>
	        &nbsp;<bean:write name="element" property="um"/>
	      </td>
	      <td align="left" class="RecordRow" nowrap>
	        &nbsp;<bean:write name="element" property="p"/>
	      </td>
	     <td align="right" class="RecordRow" nowrap>
	        <bean:write name="element" property="num"/>&nbsp;
	      </td>
	     
	        <%
                   for(int i=0;i<columnList.size();i++)
                   {
                      FieldItem item = (FieldItem)columnList.get(i);
                      if(item.getItemtype().equalsIgnoreCase("N"))
                      {
                      %>
                        <td align="right" class="RecordRow" nowrap>
	                        <bean:write name="element" property="<%=item.getItemid()%>"/>&nbsp;
	                    </td>
                      <%
                      }
                      else
                      {
                      %>
                        <td align="right" class="RecordRow" nowrap>
	                        &nbsp;<bean:write name="element" property="<%=item.getItemid()%>"/>
	                    </td>
                      <%
                      }
                   }
            %>
	     </tr>
	       <% j++; %>
	     </hrms:extenditerate>
   </table>
   </div>
 </td>
 </tr>
 <tr>
 <td align="center">
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   ${employResumeForm.dataListForm.pagination.current}
					<bean:message key="label.page.sum"/>
		   ${employResumeForm.dataListForm.pagination.count}
					<bean:message key="label.page.row"/>
		   ${employResumeForm.dataListForm.pagination.pages}
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
            <hrms:paginationlink name="employResumeForm" property="dataListForm.pagination" nameId="dataListForm" propertyId="dataListProperty">
		   </hrms:paginationlink>
		   </p>
		   </td>
		</tr> 
</table>
</td> 
 </tr>
 </table>
 </html:form>