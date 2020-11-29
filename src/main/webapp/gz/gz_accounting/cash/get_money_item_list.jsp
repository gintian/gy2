<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script language="Javascript" src="/gz/salary.js"></script>
<!-- 薪资发放/分钱清单/票额 样式美化 xiaoyun 2014-9-26 start -->
<style>
<!--
#scroll_box {
           border: 1px solid #eee;
           height: 280px;    
           width: 270px;            
           overflow: auto;            
           margin: 1em 1;
}
-->
</style>
<!-- 薪资发放/分钱清单/票额 样式美化 xiaoyun 2014-9-26 end -->
<html:form action="/gz/gz_accounting/cash/getMoneyItemList">
<%if("hl".equals(hcmflag)){ %>
<br>
<%}%>
<table width='340px;' border="0" cellspacing="1"  align="center" cellpadding="1">

<tr>
<td colspan="2" align="center">
<fieldset align="center">
<legend><bean:message key="gz.cash.ticketconfig"/></legend>
<div style="overflow:auto;width:300px;height:300px;" id="scroll_box"><!-- modify by xiaoyun 薪资发放/分钱清单/票额 样式美化 2014-9-26 -->
<table width='100%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
<thead>
           <tr>
            <td align="left" class="TableRow" style="border-top: none;border-left: none;" nowrap colspan="2" nowrap><!-- modify by xiaoyun 薪资发放/分钱清单/票额 样式美化 2014-9-26 -->
		<bean:message key="gz.cash.ticket"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <% int i=0;%>
   	  <logic:iterate id="element" name="cashListForm" property="moneyItemList" offset="0">
   	  <% if(i%2==0){%>
   	    <tr class="trShallow">
   	  <%}else{%>
   	   <tr class="trDeep">
   	  <%}%>
   	  <td width="10%" align="center" style="border-left: none;border-bottom: none;" class="RecordRow"  nowrap>
   	  <logic:equal name="element" property="isSelect" value="1">
   	  <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="itemid"/>" checked/>
   	  </logic:equal>
   	  <logic:equal name="element" property="isSelect" value="0">
   	   <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="itemid"/>"/>
   	  </logic:equal>
   	  </td>
   	  <td align="left" class="RecordRow" style="border-bottom: none;" nowrap>
   	  &nbsp;&nbsp;<bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;
   	  </td>
   	  </tr>
   	  <% i++;%>
   	  </logic:iterate>
   	  </table>
   	  </div>
   	  </fieldset>
   	  </td>
</tr>
</table>
<table width='100%' border="0" cellspacing="1"  align="center" cellpadding="1">
          <tr>
          <td align="right" nowrap>
              <html:button styleClass="mybutton" property="ok" onclick="cashlist_selectMoneyitem();">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button>
	      <td align="left">	
	      <input type="button" name="back" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">   
	     <html:hidden name="cashListForm" property="nmoneyid"/>     
          </td>
          </tr>  
          
</table>
</html:form>
