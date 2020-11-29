<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm" %>
<script type="text/javascript">
<!--
function allSelect(obj)
{
  var arr=document.getElementsByName("salary");
  if(arr)
  {
     for(var i=0;i<arr.length;i++)
     {
       if(obj.checked)
          arr[i].checked=true;
       else
          arr[i].checked=false;
     }
  }
}
function save()
{
   var arr=document.getElementsByName("salary");
   var num=0;
   var ids="";
   if(arr)
   {
     for(var i=0;i<arr.length;i++)
     {
       if(arr[i].checked)
       { 
          ids+=","+arr[i].value;
          num++;
       } 
     }
   }
   if(num==0)
   {
      alert("请选择薪资类别!");
      return;
   }
   var hashvo=new ParameterSet();
   hashvo.setValue("itemid","<%=request.getParameter("itemid")%>");
   hashvo.setValue("salaryid",ids.substring(1));
   var request=new Request({method:'post',asynchronous:true,onSuccess:save_ok,functionId:'30200710256'},hashvo);
}
function save_ok(outparameters)
{
  var salaryid=outparameters.getValue("salaryid");
  window.returnValue=salaryid;
  window.close();
}
//-->
</script>
<style>
<!--
#scroll_box {
    border: 1px solid #eee;
    height: 355px;    
    width: 470px;            
    overflow: auto;            
    margin: 0 0 0 0;
}
-->
</style>

<html:form action="/gz/gz_amount/init_parameter_config">
 <div id="scroll_box" style="overflow:auto;width:390px;height:250px;">
 <table width="100%" id ="tt" align="center" border="0" cellspacing="0" cellpadding="0" class="ListTable">

 <thead>
    <tr class="fixedHeaderTr1">
   <td align="center" class="TableRow" style="border-left: none;">
 <input type="checkbox" name="alsel" value="0" onclick="allSelect(this);"/>
  </td>
  <td align="center" class="TableRow" >
编号
 </td>
 <td align="center" class="TableRow_left">
 薪资类别
 </td>
 </tr>
 </thead>

 <% int i=0; %>
<logic:iterate id="element" name="croPayMentForm" property="salarySetList" indexId="index">
<%if(i%2==0){ %>
	     <tr class="trShallow">
	     <%} else { %>
	     <tr class="trDeep">
	     <%}%>
	     <td align="center" class="RecordRow_right" nowrap>
	     <logic:equal value="0" name="element" property="isselect">
	     <input type="checkbox" name="salary" value="<bean:write name="element" property="id"/>" />
	     </logic:equal>
	      <logic:equal value="1" name="element" property="isselect">
	     <input type="checkbox" name="salary" value="<bean:write name="element" property="id"/>" checked/>
	     </logic:equal>
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<bean:write name="element" property="id"/>&nbsp;
	     </td>
	     <td align="left" class="RecordRow_left" nowrap>
	     &nbsp;<bean:write name="element" property="name"/>&nbsp;
	     </td>
        </tr>
<% i++; %>
</logic:iterate>



</table>
</div>
<table width="370px;">
<tr>
<td align="center">
<input type="button" class="mybutton" name="sav" value="<bean:message key="button.ok"/>" onclick="save();"/>
<input type="button" class="mybutton" name="add" value="<bean:message key="button.cancel"/>" onclick="window.close();"/>
</td>
</tr>
</table>
</html:form>
