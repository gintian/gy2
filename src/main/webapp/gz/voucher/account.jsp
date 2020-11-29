<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.voucher.VoucherForm,java.util.*,com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>

<script language="Javascript" src="/gz/salary.js"/></script>
<script language="javascript" src="/js/dict.js"></script> 
<%
	VoucherForm voucherForm=(VoucherForm)session.getAttribute("financial_voucherForm"); 
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>

<html>
<body >


<script language='javascript'>
var gsetname;
function new_account(tablename)
{
	financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_new=link&opt=1";
	financial_voucherForm.submit();
}
function setNewRecord(outparamters)
{
	var tablename,table,dataset,preno,bmainset;
    tablename="table"+gsetname;
    table=$(tablename);
    dataset=table.getDataset();
    record=dataset.getCurrent();
	dataset.insertRecord("before");	

}
function selectAccount(){
	var selectvalue = document.getElementById("cha").value;
	financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_select=link&selectvalue="+getEncodeStr(selectvalue);
	financial_voucherForm.submit();
}
function outExcel(){
	/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 start */
	/*var hashvo=new ParameterSet();
	var sql = financial_voucherForm.sql.value;
  	hashvo.setValue("sql",getEncodeStr(sql));*/
  	/* 安全问题 sql-in-url 财务凭证定义-设置 xiaoyun 2014-9-16 end */
	var request=new Request({method:'post',asynchronous:false,onSuccess:outsucc,functionId:'3020073006'},null);
}
function outsucc(outparamters){
	var fileName=outparamters.getValue("fileName");
	fileName = getDecodeStr(fileName);
	var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true","excel");
}
function del(){
var ids="";
var num=0;
var i_idArr=document.getElementsByName("i_idArray");
if(i_idArr==null||i_idArr.length==0){
alert("无记录!");
return;
}
for(var i=0;i<i_idArr.length;i++){
 if(i_idArr[i].checked){
 ids += ","+i_idArr[i].value;
 num++;
}
}
if(num==0){
alert("请选择要删除的科目!");
return;
}
if(num>0){
if(confirm("确认删除吗？")){
var hashVo=new ParameterSet();
  hashVo.setValue("deleteIds",ids);
  var In_parameters="flag=1";
  var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:add_ok,functionId:'3020073005'},hashVo);			
       		 
}else{
return;
}
}
}
function add_ok(outparamters){
financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_account=link";
financial_voucherForm.submit();
}
function exportExcel()
{
var theURL="/gz/voucher/financial_voucher.do?br_file=link`opt=0";
var iframe_url="/gz/voucher/iframe_account.jsp?src="+$URL.encode(theURL);
var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=400px;dialogHeight=210px;scroll:no;resizable=yes;status=no;");  
if(objlist==null)
return;
var obj=new Object();
obj.fresh=objlist.fresh;
if(obj.fresh=="1")
{
		financial_voucherForm.action="/gz/voucher/financial_voucher.do?b_account=link";
		financial_voucherForm.submit();
}
else
{    
}
}
function batch_select(obj)
{
   var arr=document.getElementsByName("i_idArray");
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
function back(){
    financial_voucherForm.action="/gz/voucher/searchvoucherdate.do?b_query=link&pn_id=";
    financial_voucherForm.target="mil_body";            
    financial_voucherForm.submit();
}
</script>

<html:form action="/gz/voucher/financial_voucher">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow" width='5%' nowrap>
	<input type="checkbox" name="selbox" onclick="batch_select(this);"  title='<bean:message key="label.query.selectall"/>'>    
</td>
<td align='center' class='TableRow' nowrap>科目编号</td>
<td align='center' class="TableRow" nowrap>科目名称</td>
<td align='center' class='TableRow' nowrap>科目级别</td>
<td align="center" class="TableRow" width='5%' nowrap><bean:message key="column.operation"/></td>
</tr>
</thead>
 <% int i=0; 
    int j=0;%>
 	 <hrms:extenditerate id="element" name="financial_voucherForm" property="voucherForm.list" indexes="indexes"  pagination="voucherForm.pagination" pageCount="21" scope="session">
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
			<input type="checkbox" name="i_idArray" value="<bean:write name="element" property="i_id"/>"/>
         </td>
         <td align="left" class="RecordRow" id='<bean:write name="element" property="i_id"/>' width="20%" nowrap>&nbsp;
            <bean:write name="element" property="ccode"/>&nbsp;
         </td>
         <td align="left" class="RecordRow" width="50%" nowrap>&nbsp;
            <bean:write name="element" property="ccode_name"/>&nbsp;
         </td>
         <td align="right" class="RecordRow" width="20%" nowrap>&nbsp;
            <bean:write name="element" property="igrade"/>&nbsp;
         </td>
         <td align="center" class="RecordRow" width='5%' nowrap>
    	<hrms:priv func_id="3240904">
    	 <a href="/gz/voucher/financial_voucher.do?b_up=link&i_id=<bean:write name="element" property="i_id"/>"><img src="/images/edit.gif" border='0'></a>
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
		   <bean:write name="financial_voucherForm" property="voucherForm.pagination.current" filter="true"/>
					<bean:message key="label.page.sum"/>
		   <bean:write name="financial_voucherForm" property="voucherForm.pagination.count" filter="true"/>
					<bean:message key="label.page.row"/>
		   <bean:write name="financial_voucherForm" property="voucherForm.pagination.pages" filter="true"/>
					<bean:message key="label.page.page"/>
		   </td>
		   <td align="right" class="tdFontcolor" nowrap>
		   <p align="right">
		   <hrms:paginationlink name="financial_voucherForm" property="voucherForm.pagination" nameId="voucherForm" >
		   </hrms:paginationlink>
		   </td>
		</tr> 
</table>
<table  width="80%" align="center">
<tr>
<td align="center">
<hrms:priv func_id="3240901">
<input type='button' name='new' value='<bean:message key="lable.tz_template.new"/>' class='mybutton' onclick='new_account();'>
</hrms:priv>
<hrms:priv func_id="3240902">
<input type='button' name='delete' value='<bean:message key="lable.tz_template.delete"/>' class='mybutton' onclick='del();'>
</hrms:priv>
<hrms:priv func_id="3240906">
<input type='button' name='export_out' value='导出' class='mybutton' onclick='outExcel();'/>
</hrms:priv>
<hrms:priv func_id="3240906">
<input type='button' name='export_in' value='导入' class='mybutton' onclick='exportExcel();'/>
</hrms:priv>
<input type='button' name='fanhui' value='返回' class='mybutton' onclick='back();'/>
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="financial_voucherForm"/> 
</td>
</tr>
</table>

</html:form>
</body>
</html>