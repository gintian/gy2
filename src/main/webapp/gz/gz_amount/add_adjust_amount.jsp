<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm" %>

<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script type="text/javascript">
<!--
   var code="${croPayMentForm.code}";
   var year="${croPayMentForm.yearnum}";
   var setid="${croPayMentForm.isHasAdjustSet}";
function save()
{
<%int n=0;%>
  <logic:iterate id="element" name="croPayMentForm" property="fieldList" offset="0" indexId="index">
    <logic:equal name="element" property="itemtype" value="N">
     var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
     var deci="<bean:write name="element" property="decimal"/>";
     var mustfill="<bean:write name="element" property="mustfill"/>";
     var itemlength="<bean:write name="element" property="itemlength"/>";
     if(trim(a<%=n%>[0].value) !=''){
         var myReg =/^(-?\d+)(\.\d+)?$/
         if(!myReg.test(a<%=n%>[0].value)) 
		 {
		    alert("<bean:write  name="element" property="itemdesc"/>请输入数字！");
		    return;
		 }
		 if(deci=="0"&&a<%=n%>[0].value.indexOf(".")!=-1)
		 {
		      alert("<bean:write  name="element" property="itemdesc"/>请输入整数！");
		      return;
		 }
		 if(deci!='0')
		 {
		    var vv=a<%=n%>[0].value;
		    if(vv.indexOf(".")!=-1)
		       vv=vv.substring(0,vv.indexOf("."));
		    if(vv.length>parseInt(itemlength))
		    {
		         alert("<bean:write  name="element" property="itemdesc"/>值超出范围！");
		         return; 
		    }
		   var aa=a<%=n%>[0].value;
		   if(aa.indexOf(".")!=-1)
		   {
		      aa=aa.substring(aa.indexOf(".")+1);
		      if(aa.length>deci)
		      {
		         alert("<bean:write  name="element" property="itemdesc"/>小数值超出范围！");
		         return;
		      }
		   }
		 }
     }
     else{
        if(mustfill=='1')
        {
           alert("<bean:write  name="element" property="itemdesc"/>为必填项！");
           return;
        }
     }
    </logic:equal>
    <logic:equal name="element" property="itemtype" value="A">
     var mustfill="<bean:write name="element" property="mustfill"/>";
     var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
     if(mustfill=='1'&&trim(a<%=n%>[0].value)=='')
     {
         alert("<bean:write  name="element" property="itemdesc"/>为必填项！");
         return;
     }
    </logic:equal>
    <logic:equal name="element" property="itemtype" value="D">
    var mustfill="<bean:write name="element" property="mustfill"/>";
     var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
     if(mustfill=='1'&&trim(a<%=n%>[0].value)=='')
     {
         alert("<bean:write  name="element" property="itemdesc"/>为必填项！");
         return;
     }
    </logic:equal>
    <logic:equal name="element" property="itemtype" value="M">
    var mustfill="<bean:write name="element" property="mustfill"/>";
     var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
     if(mustfill=='1'&&trim(a<%=n%>[0].value)=='')
     {
         alert("<bean:write  name="element" property="itemdesc"/>为必填项！");
         return;
     }
    </logic:equal>
  <%n++;%>
  </logic:iterate>
croPayMentForm.action="/gz/gz_amount/adjust_amount_list.do?b_save=link&closew=1";
croPayMentForm.submit();
}

function closeW()
{
   croPayMentForm.action="/gz/gz_amount/adjust_amount_list.do?b_query=link&ocode="+code+"&oyear="+year+"&setid="+setid; 
   croPayMentForm.submit();
}
//-->
</script>
<html:form action="/gz/gz_amount/adjust_amount_list">

<table width="100%" border="0" cellspacing="0"    align="center" cellpadding="0" class="ListTable">
<tr>
<td class="RecordRow">
<div style='overflow:auto;width:100%;height:370'>
<table width="100%" border="0" cellspacing="0" style="margin-top:-1"   align="center" cellpadding="0" class="ListTable">
<tr>
<td colspan="2" class="TableRow" align="left">
			<html:hidden name="croPayMentForm" property="yearnum"/>
			<html:hidden name="croPayMentForm" property="code"/>
			<html:hidden name="croPayMentForm" property="isHasAdjustSet"/>
			<html:hidden name="croPayMentForm" property="optType"/>
<logic:equal value="new" name="croPayMentForm" property="optType">
新增调整记录
</logic:equal>
<logic:equal value="edit" name="croPayMentForm" property="optType">
修改调整记录
</logic:equal>
</td>
</tr>
<%int i=0; %>
<logic:iterate id="element" name="croPayMentForm" property="fieldList" offset="0" indexId="index">

<tr <logic:equal value="1" name="element" property="hidden"> style="display:none" </logic:equal>>

<td align="right" class="RecordRow" nowrap>
<input type="hidden" name="<%="fieldList["+index+"].itemid"%>" value="<bean:write name="element" property="itemid"/>"/>
<bean:write name="element" property="itemdesc"/>:
</td>
<td align="left" class='RecordRow' nowrap>
<logic:equal value="A" name="element" property="itemtype">
  <logic:equal value="0" name="element" property="codesetid">
  <input type="text" size="30" maxlength="<bean:write name="element" property="itemlength"/>" name="<%="fieldList["+i+"].value"%>" value="<bean:write name="element" property="value"/>" class="inputtext"/>
  </logic:equal>
 <logic:notEqual value="0" name="element" property="codesetid">
      <input type="text" size="30" name="<%="fieldList["+i+"].viewvalue"%>" value="<bean:write name="element" property="viewvalue"/>" readOnly class="inputtext"/>
      <img  onclick='openInputCodeDialog("<bean:write name="element" property="codesetid"/>","<%="fieldList["+i+"].viewvalue"%>");' src='/images/code.gif' border=0 align="absmiddle"/>
      <input type='hidden' value="<bean:write name="element" property="value"/>"  name=<%="fieldList["+i+"].value"%> />
 </logic:notEqual>
</logic:equal>
<logic:equal value="N" name="element" property="itemtype">
 <input type="text" size="30" class="inputtext" name="<%="fieldList["+i+"].value"%>" value="<bean:write name="element" property="value"/>" <logic:equal value="0" name="element" property="decimal"> maxlength="<bean:write name="element" property="itemlength"/>" </logic:equal>/>
</logic:equal>
<logic:equal value="D" name="element" property="itemtype">
 <input type="text" size="20" class="inputtext" name="<%="fieldList["+i+"].value"%>" value="<bean:write name="element" property="value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' readOnly/>
</logic:equal>
<logic:equal value="M" name="element" property="itemtype">
 <textarea name="<%="fieldList["+i+"].value"%>" rows='5'   cols='46'/><bean:write name="element" property="value"/></textarea>
</logic:equal>
<logic:equal value="1" name="element" property="mustfill">
<font color="red">*</font>
</logic:equal>
</td>
</tr>
<%i++; %>
</logic:iterate>
<tr><td colspan="2" class="RecordRow">&nbsp;</td></tr>
</table>
</div>
</td>
</tr>
<tr>
<td align="center" style="padding-top:3px;">
<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save();"/>
<input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="closeW();"/>
</td>
</tr>
</table>
</html:form>
<script type="text/javascript">
<!--
<%if(request.getParameter("closew")!=null&&request.getParameter("closew").equals("1")){%>
   croPayMentForm.action="/gz/gz_amount/adjust_amount_list.do?b_query=link&ocode="+code+"&oyear="+year+"&setid="+setid; 
   croPayMentForm.submit();
   <%}%>
//-->
</script>

