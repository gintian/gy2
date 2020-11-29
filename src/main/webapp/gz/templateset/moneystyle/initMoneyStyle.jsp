<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
function newMoneyStyle()
{
moneyStyleForm.action="/gz/templateset/moneystyle/newMoneyStyle.do?b_init=init&opt=add";
moneyStyleForm.submit();
}
function del(){
var ids ="";
var num =0;
var nstyleid=document.getElementsByName("nstyleidArray");
for(var i=0;i<nstyleid.length;i++){
if(nstyleid[i].checked){
ids+="#"+nstyleid[i].value;
num++;
}
}
if(num==0){
alert("请选择要删除的记录");
return;
}
if(confirm("确定删除吗")){

var hashVo=new ParameterSet();
hashVo.setValue("selectID",ids);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:refresh,functionId:'3020040004'},hashVo);			
}       		 
}
function refresh(outparameters)
{
   moneyStyleForm.action="/gz/templateset/moneystyle/initMoneyStyle.do?b_init=init";
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
<html:form action="/gz/templateset/moneystyle/initMoneyStyle">
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow" width='5%' nowrap><input type="checkbox" name='allSelect' onclick="selectAll(this);"/></td>
<td align='center' class='TableRow' nowrap>名称</td>
<td align='center' class="TableRow" nowrap>符号</td>
<td align='center' class='TableRow' nowrap>单位</td>
<td align="center" class="TableRow" width='20%' nowrap>汇率</td>
<td align='center' class='TableRow' width='10%' nowrap>编辑</td>
<td align='center' class='TableRow' width='10%' nowrap>货币维护</td>


</tr>
</thead>
 <% int i=0; %>
 	 <hrms:extenditerate id="element" name="moneyStyleForm" property="moneyListForm.list" indexes="indexes"  pagination="moneyListForm.pagination" pageCount="10" scope="session">
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
          i++;          
          %>
              <td align="center" class="RecordRow" width="5%" nowrap>
        <html:multibox property="nstyleidArray"><bean:write name="element" property="nstyleid"/></html:multibox>
         </td>
               <td align="left" class="RecordRow" width="30%" nowrap>
         <bean:write name="element" property="cname"/>
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="ctoken"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="cunit"/>&nbsp;
         </td>
         <td align="right" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="nratio"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" width='5%' nowrap>
         <a href="/gz/templateset/moneystyle/newMoneyStyle.do?b_init=init&opt=edit&nstyleid=<bean:write name="element" property="nstyleid"/>">
    <img src="/images/edit.gif" border='0'></a>
         </td>
         <td align="center" class="RecordRow" width='5%' nowrap>
         <a href="/gz/templateset/moneystyle/initMoneyStyleDetail.do?b_init=init&nstyleid=<bean:write name="element" property="nstyleid"/>">
    <img src="/images/edit.gif" border='0'>
    </a>
         </td>	
         </tr>    
	</hrms:extenditerate> 
<tr>
<td colspan="7" class="RecordROw">
    <table  width="100%" align="center">
		<tr>
		   <td valign="bottom" class="tdFontolor" nowrap>第
		   <bean:write name="moneyStyleForm" property="moneyListForm.pagination.current" filter="true"/>
		   页
		   共
		   <bean:write name="moneyStyleForm" property="moneyListForm.pagination.count" filter="true"/>
		   条
		   共
		   <bean:write name="moneyStyleForm" property="moneyListForm.pagination.pages" filter="true"/>
		   页
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="moneyStyleForm" property="moneyListForm.pagination" nameId="moneyListForm" propertyId="moneyListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
</td>
</tr>
</table>
<table  width="80%" align="center">
<tr>
<td align="center">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='newMoneyStyle();'>
<input type='button' name='delete' value='<bean:message key="lable.tz_template.delete"/>' class='mybutton' onclick='del();'>
<input type='button' name='name' value='<bean:message key="button.return"/>' class='mybutton' onclick='window.location.href="/gz/templateset/gz_templatelist.do?b_query=link"'>
</td>
</tr>
</table>

</html:form>