<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%
   String buttonClass="mybutton";
   String tt3CssName="ttNomal3";
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      buttonClass="mybuttonBig";
      tt3CssName="tt3";
   }
 %>
<html>
  <head>
  <script language="javascript" src="/js/function.js"></script>
    <script language='javascript' >
    function sub()
    {
    	 if(trim(document.objectCardForm.myView.value).length==0)
    	 {
    	 	alert("请填写内容!");
    	 	return;
    	 }
    	 document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_subReview=review&isRead=<%=(request.getParameter("isRead"))%>&p0400=<%=(request.getParameter("p0400"))%>";
  		 document.objectCardForm.submit();
    }
    
    
    <% if(request.getParameter("b_subReview")!=null&&request.getParameter("b_subReview").equalsIgnoreCase("review")){
    		out.print("alert('提交成功!')");
    }
    	String str="任务回顾";
    	if(request.getParameter("p0400").equalsIgnoreCase("-1"))	
    		str="总结回顾";
     %>
    
    function query()
    {
       // var strurl="/performance/objectiveManage/objectiveCard.do?b_review=review`isRead="+isRead+"`p0400="+P0400;
        document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_review=review&isRead=<%=(request.getParameter("isRead"))%>&p0400=<%=(request.getParameter("p0400"))%>";
  		document.objectCardForm.submit();
    }
    // 关闭页面
    function closePage(){
    	// 页面以Ext.window展现的情况要关闭Ext.window；否则关闭页面 chent 20171226 add
    	if(parent && parent.parent && parent.parent.Ext && parent.parent.Ext.getCmp('review_win')){
    		parent.parent.Ext.getCmp('review_win').close();
    	} else {
	    	parent.window.close()
    	}
    }
    </script>
     
  </head>
  <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
  <body>
  <html:form action="/performance/objectiveManage/objectiveCard">
  
   <br>
   <table width="95%"  height="95%" border="0" cellspacing="0"    align="center" cellpadding="0" class="ListTableF">
   	 <tr class="trDeep"> <td align="left" class="TableRow" nowrap><font class='<%=tt3CssName%>'>回顾信息</font></td></tr>
     <tr class="trShallow"><td align="left"  style="padding-top:3px;padding-bottom:3px"  class="RecordRow" nowrap>
     	&nbsp;填写人：
     <html:select name="objectCardForm" property="txid" size="1" onchange="query();" style="width:60px;">
			<html:optionsCollection property="txList" value="dataValue" label="dataName"/>
		    </html:select>
     	<br>
     	<html:textarea name="objectCardForm"   cols="93" rows="14"  property="otherView" readonly="true" ></html:textarea>
     </td></tr>
    <tr class="trDeep"> <td align="left" class="TableRow" nowrap><font class='<%=tt3CssName%>'><%=(str)%></font></td></tr>
  	 <tr class="trShallow"><td style='padding:3px;' align="left" class="RecordRow" nowrap>
  	 	<html:textarea name="objectCardForm"   cols="93" rows="14" property="myView"></html:textarea>
  	 </td></tr>
  	  <tr   ><td align="center"  class="RecordRow" nowrap>
     <table width="100%" border="0" cellspacing="0"    align="center" cellpadding="0">
     <tr style="padding-top:3px;padding-bottom:3px;"><td align="left" width="40%">&nbsp;
		    </td>
		    <td align="left" width="60%">
     	<% if(request.getParameter("isRead")==null||request.getParameter("isRead").equals("0")) {%>
     	<Input type='button' value='提 交' class="<%=buttonClass%>" onclick='sub()'  />
     	<% } %>
     	&nbsp;<Input type='button' value='关 闭' onclick='javascript:closePage();'  class="<%=buttonClass%>"   />
     	&nbsp;
     	</td>
     	</tr>
     	</table>
    </td></tr>
  	 
   </table>
   
   </html:form>
  </body>
</html>
