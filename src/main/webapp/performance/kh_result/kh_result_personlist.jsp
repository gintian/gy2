<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc" %>

<%
   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   boolean scoreStatus=false;
   	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
      scoreStatus=true;
   }
 %>
<script type="text/javascript">
<!--
function queryPerson(distinctionFlag,model,code,obj,isClose)
{
    var nbase;
    for(var i=0;i<obj.options.length;i++)
    {
        if(obj.options[i].selected)
            nbase=obj.options[i].value;
    }
    khResultForm.action="/performance/kh_result/kh_result_personlist.do?b_init=link&isClose="+isClose+"&opt=1&a_code="+code+"&model="+model+"&distinctionFlag="+distinctionFlag+"&nbase="+nbase;
    khResultForm.submit();
}
function returnTOWizard()
  {
     khResultForm.action="/templates/attestation/police/wizard.do?br_postwizard=link";
     khResultForm.target="il_body";
     khResultForm.submit();
  }
  function goPlanList(isCloseButton,distinctionFlag,a0100)
  {
      khResultForm.action="/performance/kh_result/kh_plan_list.do?b_init=link&opt=1&model=1&isClose="+isCloseButton+"&distinctionFlag="+distinctionFlag+"&a0100="+a0100;
      khResultForm.submit();
  }
//-->
</script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<% int i=0; %>
<html:form action="/performance/kh_result/kh_result_personlist">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<table width="80%"  border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:10px;">
<%--
<tr>
<td align="left" style="height:35px">   
  <font class="<%=tt3CssName%>"><bean:message key="label.query.dbpre"/></font>&nbsp;
  <html:select name="khResultForm" property="nbase" size="1" onchange="queryPerson('${khResultForm.distinctionFlag}','${khResultForm.model}','${khResultForm.code}',this,'${khResultForm.isCloseButton}');">
			<html:optionsCollection property="dbList" value="dataValue" label="dataName"/>
		    </html:select>&nbsp;
  
</td>
</tr>
--%>
<tr>
<td>
<table width="100%"  border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="b0110.label"/></font>
</td>
<%
	FieldItem fielditem = DataDictionary.getFieldItem("E0122");
%>	         			 	
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><%=fielditem.getItemdesc()%></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="e01a1.label"/></font>
</td>
<td align="center" class="TableRow" nowrap>
<font class="<%=tt4CssName%>"><bean:message key="hire.employActualize.name"/></font>
</td>
</tr>
</thead>
<%
int x=21;
if("hcm".equals(hcmflag)){
	x=18;
}else{ 
	x=21;
} %>
<hrms:paginationdb id="element" name="khResultForm" sql_str="khResultForm.selectSql" table="" where_str="khResultForm.whereSql"  order_by="khResultForm.orderSql" pagerows="<%=x %>" columns="${khResultForm.columns}" page_id="pagination">
		 <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" >
          <%}
          else
          {%>
          <tr class="trDeep" >
          <%
          }
          i++;          
          %>  
<td align="left" class="RecordRow" nowrap>
         <hrms:codetoname codeid="UN" name="element" codeitem="codeitem" codevalue="b0110" scope="page"/>
         &nbsp;<font class="<%=tt3CssName%>"><bean:write name="codeitem" property="codename"/></font>&nbsp;
         </td>
          <td align="left" class="RecordRow" width="25%" nowrap>
         <hrms:codetoname codeid="UM" name="element" codeitem="codeitem" codevalue="e0122" scope="page"/>
         &nbsp;<font class="<%=tt3CssName%>"><bean:write name="codeitem" property="codename"/></font>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         <hrms:codetoname codeid="@K" name="element" codeitem="codeitem" codevalue="e01a1" scope="page"/>
         &nbsp;<font class="<%=tt3CssName%>"><bean:write name="codeitem" property="codename"/></font>&nbsp;
         </td>
          <td align="left" class="RecordRow" width="25%" nowrap>
          <%if(scoreStatus){ %>
          
           <a href="/performance/kh_result/kh_result_muster.do?b_init=link&opt=yuangong&model=1&isClose=${khResultForm.isCloseButton}&distinctionFlag=${khResultForm.distinctionFlag}&object_id=<bean:write name="element" property="a0100"/>"> <font class="<%=tt3CssName%>">&nbsp;<bean:write name="element" property="a0101"/></font></a>
          <%}else{
            LazyDynaBean bean=(LazyDynaBean)pageContext.getAttribute("element");
             String mdoid=PubFunc.encryption(((String)bean.get("a0100")));
            
           %>
         <a href='javascript:goPlanList("${khResultForm.isCloseButton}","${khResultForm.distinctionFlag}","<%=mdoid%>");'> <font class="<%=tt3CssName%>">&nbsp;<bean:write name="element" property="a0101"/></font></a>
        <%} %>
         </td>
         </tr>
</hrms:paginationdb>
</table>
</td>
</tr>
<tr>
<td>
<table  width="100%" align="center" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="khResultForm" property="pagination" nameId="khResultForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</td>
</tr>
<tr>
<td align="left">

<logic:equal value="1" name="khResultForm" property="isCloseButton">
<input type="button" class="<%=buttonClass%>" value="<bean:message key="button.close"/>" onclick='window.close();'/>
</logic:equal>
<logic:equal value="poloicewizard" name="khResultForm" property="returnvalue">
                          <input type='button' name='b_save' value='返回' onclick='returnTOWizard();' class='mybutton'>
</logic:equal>	
</td>
</tr>
</table>
</html:form>