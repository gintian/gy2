<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function ret(){
moneyStyleForm.action="/gz/templateset/moneystyle/initMoneyStyle.do?b_init=init";
moneyStyleForm.submit();
}
function del(){
if(confirm("确定要删除吗?")){
var ids ="";
var num =0;
var val=moneyStyleForm.nstyleid.value;
var nitemid=document.getElementsByName("nstyleidArray");
for(var i=0;i<nitemid.length;i++){
if(nitemid[i].checked){
ids+="#"+nitemid[i].value;
num++;
}
}
if(num==0){
alert("请选择要删除的记录");
return;
}
var hashVo=new ParameterSet();
hashVo.setValue("selectID",ids);
hashVo.setValue("styleid",val);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'3020040006'},hashVo);			
}

else{
return;
}
}
function refresh(outparameters)
{
 var styleid=outparameters.getValue("styleid");
moneyStyleForm.action="/gz/templateset/moneystyle/initMoneyStyleDetail.do?b_init=init&nstyleid="+styleid;
moneyStyleForm.submit();
}
function add(){
moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyleDetail.do?b_add=add&opt=add";
moneyStyleForm.submit();
}
function selectAll(obj)
{
   var arr=document.getElementsByName("nstyleidArray");
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
          if(obj.checked)
          {
             arr[i].checked=true;
          }
          else
          {
            arr[i].checked=false;
          }
      }
   }
}
//-->
</script>

<html:form action="/gz/templateset/moneystyle/initMoneyStyleDetail">
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" id='tab' class="ListTable">
<thead>
<tr>
<td align='center' class='TableRow' nowrap><input type="checkbox" name='allSelect' onclick="selectAll(this);"/></td>
<td align='center' class='TableRow' nowrap>货币名称</td>
<td align='center' class='TableRow' nowrap>面值</td>
<td align='center' class='TableRow' nowrap>操作</td>

<input type='hidden' name='nstyleid' value='${moneyStyleForm.nstyleid}'/>
</tr>
</thead>
<% int i=0;%>
<hrms:paginationdb id="element" name="moneyStyleForm" sql_str="${moneyStyleForm.sql}" fromdict="1" where_str="${moneyStyleForm.where_sql}" columns="${moneyStyleForm.columns}" order_by="" page_id="pagination" pagerows="10" indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          %>
          <td align="center" class='RecordRow' nowrap>
        <html:multibox property="nstyleidArray"><bean:write name="element" property="nitemid"/></html:multibox>
         </td>
          <td align='left' class='RecordRow' nowrap>
         &nbsp;<bean:write name='element' property='cname'/>&nbsp;
         </td>
          <td align='right' class='RecordRow' nowrap>
         &nbsp;<bean:write name='element' property='nitemid'/>&nbsp;
         </td>
         <td align='center' class='RecordRow' nowrap>
        <a href="/gz/templateset/moneystyle/newMoneyStyleDetail.do?b_add=add&opt=edit&nitemid=<bean:write name="element" property="nitemid"/>"><img src="/images/edit.gif" border='0'></a>
         </td>
          
  </tr>
  <% i++;%>
	</hrms:paginationdb>
<tr>
<td colspan="4" class="RecordRow">
	   <table  width="80%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="hmuster.label.d"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="hmuster.label.paper"/>
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="count" filter="true" />
					条
					<bean:message key="hmuster.label.total"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="hmuster.label.paper"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="moneyStyleForm" property="pagination" nameId="moneyStyleForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   	  
</table> 
</td>
</tr>
</table>

<table width="80%" align="center">
<tr>
<td align="center">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='add();'/>
<input type='button' name='delete' value='<bean:message key="lable.tz_template.delete"/>' class='mybutton' onclick='del();'/>
<input type='button' name='name' value='<bean:message key="button.return"/>' class='mybutton' onclick='ret();'>
</td>
</tr>
</table>

</html:form>