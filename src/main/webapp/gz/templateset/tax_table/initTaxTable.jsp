<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<!--
function newTax(){
  var newName=prompt("请输入税率表名称:","");
  if(newName==''||newName==null)
  return;
  if(newName.length>100)
  {
    alert("您输入的税率表名称超出长度范围!");
    return;
  }
  var hashVo=new ParameterSet();
  hashVo.setValue("description",getEncodeStr(newName));
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:add_ok,functionId:'3020020002'},hashVo);			
       		 
}
function add_ok(outparamters){
taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_rename=rename";
taxDetailTableForm.submit();
}
function del(){
var ids="";
var num=0;
var taxidArr=document.getElementsByName("taxidArray");
if(taxidArr==null||taxidArr.length==0){
alert("无记录!");
return;
}
for(var i=0;i<taxidArr.length;i++){
 if(taxidArr[i].checked){
 ids += ","+taxidArr[i].value;
 num++;
}
}
if(num==0){
alert("请选择要删除的税率表!");
return;
}
if(num>0){
if(confirm("确认删除吗？")){
var hashVo=new ParameterSet();
  hashVo.setValue("deleteIds",ids);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:add_ok,functionId:'3020020003'},hashVo);			
       		 
}else{
return;
}
}
}
function rename(){
var id="";
var num=0;
var size=${taxDetailTableForm.size};
if(size ==0){
alert("无记录!");
return;
}
var taxidArr=document.getElementsByName("taxidArray");
for(var i=0;i<taxidArr.length;i++){
 if(taxidArr[i].checked){
 id=taxidArr[i].value;
 num++;
}
}
if(num==0){
alert("请选择要重命名的税率表!");
return;
}
if(num>1){
alert("一次只能为一个税率表重命名!");
return;
}
var ele=document.getElementById(id);
var description=prompt("请输入新的税率表名称:",ele.innerHTML);
if(description =='' || description ==null){
  return;
 }
else{
var hashVo=new ParameterSet();
  hashVo.setValue("description",getEncodeStr(description));
  hashVo.setValue("taxid",id);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:add_ok,functionId:'3020020004'},hashVo);			
  }     		 
}
function saveAs(){
var id="";
var num=0;
var size=${taxDetailTableForm.size};
if(parseInt(size)==0){
alert("无记录!");
return;
}
var taxidArr=document.getElementsByName("taxidArray");
for(var i=0;i<taxidArr.length;i++){
 if(taxidArr[i].checked){
 id=taxidArr[i].value;
 num++;
}
}
if(num==0){
alert("请选择税率表!");
return;
}
if(num>1){
alert("只能选择一个税率表!");
return;
}
var ele=document.getElementById(id);
var description=prompt("请输入新的税率表名称:",ele.innerText);
if(description ==null){
return;
}
if(description ==''){
alert("请输入税率表名称！");
  return;
  }
var hashVo=new ParameterSet();
  hashVo.setValue("description",getEncodeStr(description));
  hashVo.setValue("taxid",id);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:save_as_ok,functionId:'3020020008'},hashVo);			
    		 

}
function save_as_ok(outparamters){
alert("操作执行成功!");
taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_init=init";
taxDetailTableForm.submit();
}

function exportTaxExcel() {
    var id="";
	var num=0;
	var size=${taxDetailTableForm.size};
	if(parseInt(size)==0){
	return;
	}
	var taxidArr=document.getElementsByName("taxidArray");
	for(var i=0;i<taxidArr.length;i++){
	 if(taxidArr[i].checked){
	 id+=","+taxidArr[i].value;
	 num++;
	}
	}
	if(num==0){
	alert("请选择要导出的税率表!");
	return;
	}
	var hashVo=new ParameterSet();
    hashVo.setValue("taxid",id.substring(1));
    var In_parameters="flag=1";
    var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_tax_ok,functionId:'3020020009'},hashVo);			   	
}
function export_tax_ok(outparameters)
{
var outName=outparameters.getValue("outName");
outName = getDecodeStr(outName);
var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}
function exportinTaxExcel()
{
//   var theURL="/gz/templateset/tax_table/initTaxTable.do?br_query=link`opt=0";
     taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?br_query=link";
     taxDetailTableForm.submit();
/**var iframe_url="/gz/templateset/tax_table/iframe_tax.jsp?src="+theURL;
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=350px;dialogHeight=180px;resizable=yes;scroll=no;status=no;");  
if(objlist==null)
return;
var obj=new Object();
obj.fresh=objlist.fresh;
if(obj.fresh=="1")
{
     taxDetailTableForm.action="/gz/templateset/tax_table/initTaxTable.do?b_init=init";
     taxDetailTableForm.submit();
}
else
{    
}*/

}
function allSelectCheck(obj)
{
   var arr=document.getElementsByName("taxidArray");
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
//-->
</script>
<html:form action="/gz/templateset/tax_table/initTaxTable">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow" width='5%' nowrap><input type="checkbox" name="allselect" onclick="allSelectCheck(this);"/></td>
<td align='center' class='TableRow' nowrap><bean:message key="gz.columns.name"/></td>
<td align='center' class="TableRow" nowrap><bean:message key="gz.self.tax.basedata"/></td>
<td align='center' class='TableRow' nowrap><bean:message key="gz.columns.taxmode"/></td>
<td align="center" class="TableRow" width='5%' nowrap><bean:message key="column.operation"/></td>
</tr>
</thead>
 <% int i=0; 
    int j=0;%>
 	 <hrms:extenditerate id="element" name="taxDetailTableForm" property="taxListForm.list" indexes="indexes"  pagination="taxListForm.pagination" pageCount="10" scope="session">
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
        <input type="checkbox" name="taxidArray" value="<bean:write name="element" property="taxid"/>"/>
         </td>
               <td align="left" class="RecordRow" id='<bean:write name="element" property="taxid"/>' width="30%" nowrap>
         <bean:write name="element" property="description"/>
         </td>
         <td align="right" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="k_base"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="25%" nowrap>
         &nbsp;<bean:write name="element" property="param"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" width='5%' nowrap>
    	<hrms:priv func_id="3240904">
    	 <a href="/gz/templateset/tax_table/initTaxDetailTable.do?b_init=init&taxid=<bean:write name="element" property="taxid"/>"><img src="/images/edit.gif" border='0'></a>
        </hrms:priv>
         </td>
            </tr>
            <%j++;%>		    
	</hrms:extenditerate> 
    </table>
    <table  width="80%" align="center" class="RecordRowP">
		<tr>
		   <td valign="bottom" class="tdFontcolor" nowrap>
		            <bean:message key="label.page.serial"/>
		   <bean:write name="taxDetailTableForm" property="taxListForm.pagination.current" filter="true"/>
					<bean:message key="label.page.sum"/>
		   <bean:write name="taxDetailTableForm" property="taxListForm.pagination.count" filter="true"/>
					<bean:message key="label.page.row"/>
		   <bean:write name="taxDetailTableForm" property="taxListForm.pagination.pages" filter="true"/>
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="taxDetailTableForm" property="taxListForm.pagination" nameId="taxListForm" propertyId="taxListProperty">
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
<table  width="80%" align="center">
<tr>
<td align="center">
<hrms:priv func_id="3240901">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='newTax();'>
</hrms:priv>
<hrms:priv func_id="3240902">
<input type='button' name='delete' value='<bean:message key="lable.tz_template.delete"/>' class='mybutton' onclick='del();'>
</hrms:priv>
<hrms:priv func_id="3240903">
<input type='button' name='other_save' value='<bean:message key="button.other_save"/>' class='mybutton' onclick='saveAs();'>
</hrms:priv>
<hrms:priv func_id="3240905">
<input type='button' name='name' value='<bean:message key="button.rename"/>' class='mybutton' onclick='rename();'>
</hrms:priv>
<hrms:priv func_id="3240906">
<input type='button' name='export_out' value='导出' class='mybutton' onclick='exportTaxExcel();'/>
</hrms:priv>
<hrms:priv func_id="3240906">
<input type='button' name='export_in' value='导入' class='mybutton' onclick='exportinTaxExcel();'/>
</hrms:priv>
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="taxDetailTableForm"/> 
<input type='hidden' name='dir' value=''/>
</td>
</tr>
</table>

</html:form>