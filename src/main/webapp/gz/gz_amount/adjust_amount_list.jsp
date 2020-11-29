<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm,com.hrms.hjsj.sys.FieldItem" %>
<% 
CroPayMentForm croPayMentForm = (CroPayMentForm)session.getAttribute("croPayMentForm");
ArrayList tableHeaderList = croPayMentForm.getTableHeaderList();
int colspan=tableHeaderList.size()+2;
%>
<script type="text/javascript">
<!--
function addOrEdit(opt,i9999)
{
    var code="${croPayMentForm.code}";
    var year="${croPayMentForm.yearnum}";
    var setid="${croPayMentForm.isHasAdjustSet}";
    var thecodeurl ="/gz/gz_amount/adjust_amount_list.do?b_add=link&ocode="+code+"&oyear="+year+"&setid="+setid+"&optType="+opt+"&i9999="+i9999; 
    croPayMentForm.action=thecodeurl;
    croPayMentForm.submit();
   // var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
   // var return_vo= window.showModalDialog(iframe_url, "","dialogWidth:700px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:yes");
   // if(return_vo)
   // {
   //    if(return_vo=='1')
    //   {
      //    croPayMentForm.action="/gz/gz_amount/adjust_amount_list.do?b_query=link&ocode="+code+"&oyear="+year+"&setid="+setid; 
       //   croPayMentForm.submit();
      // }
  // }
}
function allSel(obj)
{
   var arr=document.getElementsByName("allSelect");
   for(var i=0;i<arr.length;i++)
   {
       if(obj.checked)
           arr[i].checked=true;
        else
           arr[i].checked=false;
   }
}
function deleteRecords()
{
   var obj=document.getElementsByName("allSelect");
   var num=0;
   var ids="";
   for(var i=0;i<obj.length;i++)
   {
      if(obj[i].checked)
      {
         num++;
         ids+="~"+obj[i].value;
      }
   }
   if(num==0)
   {
      alert("请选择要删除的记录！");
      return;
   }
   if(confirm("确认删除？"))
   {
        var code="${croPayMentForm.code}";
        var year="${croPayMentForm.yearnum}";
        var setid="${croPayMentForm.isHasAdjustSet}";
        var hashvo=new ParameterSet();
    	hashvo.setValue("ids",ids.substring(1));
    	hashvo.setValue("setid",setid);
   		var request=new Request({asynchronous:false,onSuccess:checkadd,functionId:"3020080024"},hashvo);    
   }
}
function checkadd(outparameter)
{
  alert("删除成功！");
  var code="${croPayMentForm.code}";
  var year="${croPayMentForm.yearnum}";
  var setid="${croPayMentForm.isHasAdjustSet}";
  croPayMentForm.action="/gz/gz_amount/adjust_amount_list.do?b_query=link&ocode="+code+"&oyear="+year+"&setid="+setid; 
  croPayMentForm.submit();
}
//-->
</script>
<html:form action="/gz/gz_amount/adjust_amount_list">

<table width="785px;"  border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td align="left">
 <b> ${croPayMentForm.orgDesc}</b>
  </td></tr>
<tr>
<td class="RecordRow" valign="top" align="left">
<div style='overflow:auto;width:480px;height:310px;'>
<table width="99%" border="0" cellspacing="0"   style="margin-top:-1;border-left:0px;" align="left" cellpadding="0" class="ListTable">
<thead>

<tr>
<td align="center" class='TableRow'>
<input type="checkbox" name='all' onclick="allSel(this);"/>
</td>
<td align="center" class='TableRow' nowrap>
<bean:message key="kq.deration_details.edit"/>
</td>
<logic:iterate id="header" name="croPayMentForm" property="tableHeaderList">
<td align="center" class='TableRow' nowrap>
<bean:write name="header" property="itemdesc"/>
</td>
</logic:iterate>
</tr>
</thead>
<hrms:extenditerate id="element" name="croPayMentForm" property="adjustListform.list" indexes="indexes"  pagination="adjustListform.pagination" pageCount="15" scope="session">
<tr>
<td align="center" class="RecordRow">
<input type="checkbox" name="allSelect" value="<bean:write name="element" property="b0110"/>"/>
</td>
<td align="center" class="RecordRow">
<img src="/images/edit.gif" onclick='addOrEdit("edit","<bean:write name="element" property="i9999"/>");'>
</td>
<%for(int i=0;i<tableHeaderList.size();i++){ 
  FieldItem item = (FieldItem)tableHeaderList.get(i);
  String ss="left";
  if(item.getItemtype().equalsIgnoreCase("N"))
      ss="right";
%>
  <td  align="<%=ss%>" class='RecordRow'>
  &nbsp;<bean:write name="element" property="<%=item.getItemid().toLowerCase()%>"/>&nbsp;
  </td>
 
<%} %>
</tr>
</hrms:extenditerate>


</table>
</div>
</td>
</tr>
<tr>
<td>
<table  width='100%'  class='RecordRowP'  align='center'>
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="croPayMentForm" property="adjustListform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="croPayMentForm" property="adjustListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="croPayMentForm" property="adjustListform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="croPayMentForm" property="adjustListform.pagination"
				nameId="adjustListform" propertyId="adjustListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</td>
</tr>
<tr>
  <td width="90%" align="center" style="padding-top:3px;">
  <input type="button" name="new" class="mybutton" value="<bean:message key="button.insert"/>" onclick="addOrEdit('new','');"/>
  <input type="button" name="del" class="mybutton" value="<bean:message key="button.delete"/>" onclick="deleteRecords();"/>
   <input type="button" name="clo" class="mybutton" value="<bean:message key="button.close"/>" onclick="window.close();"/>
  </td>
</tr>

</table>
</html:form>