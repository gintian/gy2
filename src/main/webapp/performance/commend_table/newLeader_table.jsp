<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.performance.commend_table.CommendTableForm,com.hrms.hjsj.sys.FieldItem"%>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<link href="/performance/commend_table/commend.css" rel="stylesheet" type="text/css">
<%  
  CommendTableForm  commendTableForm = (CommendTableForm)session.getAttribute("commendTableForm");
  int size=commendTableForm.getFieldList()==null?1:(commendTableForm.getFieldList().size()+1);
  ArrayList fieldlist=commendTableForm.getFieldList();
  
 %>
<script type="text/javascript">
<!--
function save(status,num)
{
   var record="";
   var count=0;
   for(var i=0;i<num;i++)
   {
      var a0100 = document.getElementById(i+"a0100").value;
      var arr = document.getElementsByName(i+"rad");
      var radiov = "0";
      for(var j=0;j<arr.length;j++)
      {
         if(arr[j].checked)
         {
            radiov = arr[j].value;
            break;
         }
      }
      if(radiov!='0')
      {
           count++;
           record+="/"+a0100+"`"+radiov;
      }
   }
   if(count!=num&&status=='1')
   {
      alert("所有人员必须都做出选择才可以提交！");
      return;
   }
   if(count>0)
   {
       if(status=='1')
       {
         if(!confirm("确认提交，提交后不可更改！"))
         {
            return;
         }
       }
       var hashvo=new ParameterSet();
       hashvo.setValue("record",getEncodeStr(record.substring(1)));
	   hashvo.setValue("status",status);
	   var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'3020080031'},hashvo);	
       
   }
}
function returnInfo(outparameter)
{
   var status = outparameter.getValue("status");
   if(status=='1')
   {
      alert("提交成功！");
      document.getElementById("trButton").style.display="none";
      document.getElementById("trButton2").style.display="block";
   }
   else{
     alert("保存成功！");
   }
}
function searchResult()
{
   commendTableForm.action="/performance/commend_table/leadership_members.do?br_tree=search";
   commendTableForm.submit();
}
//-->
</script>
<html:form action="/performance/commend_table/newLeader_table">
<br>
<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<tr>
<td class="TableRowCommend" align="center" colspan="<%=size+4%>">
 <font class='tt5'> 新选拔任用领导人员民主评议表</font>
</td>
</tr>
<tr>
<td class="TableRowCommend" align="center" colspan="<%=size%>">
<font class='tt4Noweight'> 被测评领导人员基本信息</font>
</td>
<td  class="TableRowCommend" align="center" colspan="4"><font class='tt4Noweight'>您对任用该领导人员的看法 </font>
</td>
</tr>

<tr>
<td class="TableRowCommend" align="center">
<font class='tt4Noweight'> 姓名</font>
</td>
<logic:iterate id="data" name="commendTableForm" property="fieldList" indexId="index">
<td class="TableRowCommend" align="center" nowrap>
<font class='tt4Noweight'> <bean:write name="data" property="itemdesc"/></font>
</td>
</logic:iterate>
<td class="TableRowCommend" align="center">
<font class='tt4Noweight'> 满意</font>
</td>
<td class="TableRowCommend" align="center">
<font class='tt4Noweight'> 基本满意</font>
</td>
<td class="TableRowCommend" align="center">
<font class='tt4Noweight'> 不满意</font>
</td>
<td class="TableRowCommend" align="center">
<font class='tt4Noweight'> 不了解</font>
</td>
</tr>
<% int j=0; %>
<logic:iterate id="element" property="newLeaderList" name="commendTableForm" indexId="index">
<tr>
<td class="RecordRow" align="center" nowrap>
<font class='tt6'><bean:write name="element" property="a0101"/>
</td>
<%
for(int i=0;i<fieldlist.size();i++)
{
  FieldItem item = (FieldItem)fieldlist.get(i);
%>
<td class="RecordRow" align="center">
<font class='tt6'><bean:write name="element" property="<%=item.getItemid().toLowerCase()%>"/>
</td>
<%
} %>
<td class="RecordRow" align="center">
<input type="hidden" name="dd" id="<%=j+"a0100"%>" value="<bean:write name="element" property="a0100"/>"/>
<input type="radio" name="<%=j+"rad"%>" value="1" <logic:equal name="element" property="status" value="1"> checked</logic:equal>/>
</td>
<td class="RecordRow" align="center">
<input type="radio" name="<%=j+"rad"%>" value="2" <logic:equal name="element" property="status" value="2"> checked</logic:equal>/>
</td>
<td class="RecordRow" align="center">
<input type="radio" name="<%=j+"rad"%>" value="3" <logic:equal name="element" property="status" value="3"> checked</logic:equal>/>
</td>
<td class="RecordRow" align="center">
<input type="radio" name="<%=j+"rad"%>" value="4" <logic:equal name="element" property="status" value="4"> checked</logic:equal>/>
</td>
<%j++; %>
</tr>
</logic:iterate>

<tr id="trButton">
<td align="center" colspan="<%=size+4%>">
<logic:equal value="0" name="commendTableForm" property="newLeaderStatus">
<input type="button" name="ss" class='mybuttonBig' value="完 成" onclick="save('0','<%=j%>');"/>
<input type="button" name="tj" class='mybuttonBig' value="提 交" onclick="save('1','<%=j%>');"/>
</logic:equal>
<input type="button" name="cc" class='mybuttonBig' value="<bean:message key="button.close"/>" onclick="window.close();"/>
<input type="button" value="查看结果" class="mybuttonBig" onclick="searchResult();"/>
</td>
</tr>
<tr id="trButton2" style="display=none">
<td align="center" colspan="<%=size+4%>">
<input type="button" name="cc" class='mybuttonBig' value="<bean:message key="button.close"/>" onclick="window.close();"/>
<input type="button" value="查看结果" class="mybuttonBig" onclick="searchResult();"/>
</td>
</tr>
</table>
</html:form>