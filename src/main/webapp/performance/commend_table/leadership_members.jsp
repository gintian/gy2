<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.performance.commend_table.CommendTableForm"%>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<link href="/performance/commend_table/commend.css" rel="stylesheet" type="text/css">
<%  
  CommendTableForm  commendTableForm = (CommendTableForm)session.getAttribute("commendTableForm");
  int size=commendTableForm.getLeaderShipList()==null?0:commendTableForm.getLeaderShipList().size()-1;
 %>
<script type="text/javascript">
<!--
function save(status)
{
   var arr=document.getElementsByName("q3");
   var value="";
   var threeNum=0;
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
         if(arr[i].checked)
         {
            value+="`"+arr[i].value;
            threeNum++;
         }
      }
   }
   if(arr==null||arr.length==0)
        threeNum++;
   if(value.length>0)
       value=value.substring(1);
   var one = document.getElementsByName("questionOne");
   var oneNum=0;
   for(var o=0;o<one.length;o++)
   {
      if(one[o].checked)
          oneNum++;
   }
   var two=document.getElementsByName("questionTwo");
   var twoNum=0;
   for(var o=0;o<two.length;o++)
   {
      if(two[o].checked)
         twoNum++;
   }
   var five = document.getElementsByName("questionFive");
   var fiveNum=0;
   for(var o=0;o<five.length;o++)
   {
      if(five[o].checked)
         fiveNum++;
   }
   var four = document.getElementsByName("questionFour");
   var fourNum=0;
   for(var o=0;o<four.length;o++)
   {
       if(four[o].checked)
       {
          fourNum++;
       }
   }
   if(oneNum==0||twoNum==0||threeNum==0||fourNum==0||fiveNum==0)
   {
       alert("所有题目都必须选择!");
       return;
   }
   if(status=='1')
   {
       if(!confirm("确认提交吗？\r\n提交后不可更改！"))
            return;
   }
   document.getElementById("questionThree").value=value;
   commendTableForm.action="/performance/commend_table/leadership_members.do?b_save=save&status="+status;
   commendTableForm.submit();
   
}
function searchResult()
{
   commendTableForm.action="/performance/commend_table/leadership_members.do?br_tree=search";
   commendTableForm.submit();
}
//-->
</script>
<html:form action="/performance/commend_table/leadership_members">
<br>
<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
<tr>
<td class="TableRowCommend" align="center">
 <font class='tt5'> 班子职数配备意见表</font>
</td>
</tr>
<tr>
<td class="TableRowCommend" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"<font class='tt4'>合理确定并从严掌握领导班子职数，优化领导班子结构，不断提高管理效率</font>"<font class='tt4Noweight'>是中
央对央企管理人员队伍建设的基本要求。请从有利于分公司发展出发，填答以下问题：</font>
</td>
</tr>
<tr>
<td class="TableRowCommend" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'> 一、您认为从满足省分公司发展角度出发，您所在省分公司领导班子的合理职数应为几人（请点选您认为合适的答案）:</font>
</td>
</tr>
<tr>
<td class="RecordRow" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt6'><html:radio name="commendTableForm" property="questionOne" value="3"/>［3人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionOne" value="4"/>［4人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionOne" value="5"/>［5人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionOne" value="6"/>［6人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionOne" value="7"/>［7人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionOne" value="8"/>［8人］
</font>
</td>
</tr>
<tr>
<td class="TableRowCommend" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'> 二、根据您认为的合理职数，可从现班子人员中选任几人、另外补充几人（请点选您认为合适的答案）:</font>
</td>
</tr>
<tr>
<td class="RecordRow" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>现班子成员中选任:</font>
&nbsp;<font class='tt6'><html:radio name="commendTableForm" property="questionTwo" value="3"/>［3人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionTwo" value="4"/>［4人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionTwo" value="5"/>［5人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionTwo" value="6"/>［6人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionTwo" value="7"/>［7人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionTwo" value="8"/>［8人］</font>
</td>
</tr>
<tr>
<td class="RecordRow" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>另</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>外</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>补</font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>充:</font>
&nbsp;<font class='tt6'><html:radio name="commendTableForm" property="questionFive" value="3"/>［3人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFive" value="4"/>［4人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFive" value="5"/>［5人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFive" value="6"/>［6人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFive" value="7"/>［7人］
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFive" value="8"/>［8人］</font>
</td>
</tr>
<tr>
<td class='TableRowCommend' align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>三、上述从现班子产生的人员具体是哪些（请点选您认为合适的人员） :</font>
</td>
</tr>
<%int i=0; %>
<tr>
<td class="RecordRow" align="left">
<table width="100%">
<logic:iterate id="element" property="leaderShipList" name="commendTableForm" indexId="index">
<%if(i==0){ %>
<tr>
<%} %>
<%if((i!=0&&i%5==0)){ %>
</tr>
<tr>
<%} %>
<td>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt6'>
<input type="checkbox" name="q3" value="<bean:write name="element" property="record"/>" <logic:equal value="1" name="element" property="checked">checked</logic:equal>/>
<bean:write name="element" property="a0101"/></font></td>
<%if(i==size){ %>
</tr>
<%} %>
<%i++;%>
</logic:iterate>
</table>
<html:hidden name="commendTableForm" property="questionThree"/>
<html:hidden name="commendTableForm" property="isLeader"/>
</td>
</tr>
<tr>
<td class='TableRowCommend' align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt4Noweight'>四、从专业结构考虑，您认为需要补充的班子成员应当擅长（请点击您认为合适的答案）：</font>
</td>
</tr>
<tr>
<td class="RecordRow" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<font class='tt6'><html:radio name="commendTableForm" property="questionFour" value="3"/>A．市场经营和客户服务
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFour" value="4"/>B．建设运维IT支撑
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<html:radio name="commendTableForm" property="questionFour" value="5"/>C．综合财务等</font>
</td>
</tr>
<tr>
<td class="RecordRow" align="center">
<logic:equal value="0" name="commendTableForm" property="status">
<input type="button" name="ss" class='mybuttonBig' value="完 成" onclick="save('0');"/>
<input type="button" name="tj" class='mybuttonBig' value="提 交" onclick="save('1');"/>
</logic:equal>
<input type="button" name="cc" class='mybuttonBig' value="<bean:message key="button.close"/>" onclick="window.close();"/>
</td>
</tr>
</table>
</html:form>