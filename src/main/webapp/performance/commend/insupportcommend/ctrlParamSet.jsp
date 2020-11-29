<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean" %>
 <html>
<head>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<hrms:themes />
<SCRIPT language="javascript">
<!--
function sub(){
<% int i=0;%>
<logic:iterate id="element" name="inSupportCommendForm" property="parameterSetList" indexId="index"> 
var a<%=i%>=document.getElementsByName("parameterSetList[<%=i%>].ctrl_param");
        if(a<%=i%>[0].value !=''){
          var myReg =/^(-?\d+)(\.\d+)?$/
		  if(!myReg.test(a<%=i%>[0].value)) 
		   {
		    alert("<bean:write  name="element" property="p0203"/>的最大推荐数请输入数字！");
		    return;
		   }
		}
<% i++;%>		
</logic:iterate>
inSupportCommendForm.action="/performance/commend/insupportcommend/ctrlParamSet.do?b_set=set&oper=2";
inSupportCommendForm.submit();
window.close();   	    

}
-->
</SCRIPT>
</head>

<body>
  <base id="mybase" target="_self">
 
<html:form action="/performance/commend/insupportcommend/ctrlParamSet">
<br><br>
<fieldset align="center" style="width:85%;">
<legend><bean:message key="label.commend.p_set"/></legend>
	<table width="85%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	   <tr height='20'>
            <td align="center" class="TableRow" width="60%" nowrap>
           <bean:message key="label.commend.i_name"/>
            </td>
           
            <td align="center" class="TableRow" width="40%" nowrap>
	         <bean:message key="label.commend.max"/>
	   		 </td>
         </tr> 
         </thead> 
 <logic:iterate id="element" name="inSupportCommendForm" property="parameterSetList" indexId="index"> 
           <tr height="20">
           <td align="left" width="60%" class="RecordRow" nowrap>
          <bean:write name="element" property="p0203"/><input type="hidden" name="<%="parameterSetList["+index+"].p0201"%>" value="<bean:write name="element" property="p0201"/>"/>
          </td>
          <td align="center" width="40%" class="framestyle" nowrap>
          <input type="text" name="<%="parameterSetList["+index+"].ctrl_param"%>" value="<bean:write name="element" property="ctrl_param"/>" /></td>
          </tr>
           

 </logic:iterate>
    
</table> 
</fieldset>
<table  width="70%" align="center">
          <tr>
            <td align="right"> 
              <button class="mybutton" value="" onclick="sub();"><bean:message key="button.save"/></button>
          </td>
           <td align="left" ><button class="mybutton"value="" onclick="window.close();"><bean:message key="button.close"/></button></td>
         </tr>
   </table>
    </html:form>
 </body>
</html>
