<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
 <%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script language="Javascript" src="/gz/salary.js"></script>
<script language="Javascript" src="/js/function.js"></script>
<script language="Javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript">
<!--
<% 
	String name=(String)request.getParameter("inputname");
	String opt=(String)request.getParameter("opt");
%>

var  dmlbank_id="";
<%
	if(opt!=null&&(opt.equalsIgnoreCase("fin")||opt.equalsIgnoreCase("ret")||opt.equalsIgnoreCase("del")||opt.equalsIgnoreCase("init"))){
	%>
	dmlbank_id="${bankDiskForm.bank_id}";
	<%
	}
%>
function isclose(isclose)
{
  if(parseInt(isclose)==1)
   {
        var obj= new Object();
        obj.isclose=isclose;
        obj.bank_id='${bankDiskForm.bank_id}';
        returnValue=obj;
        window.close();
        
   }
}
function bankdisk_sortField(bank_id)
{	
	if(dmlbank_id==null||trim(dmlbank_id).length==0){
		alert("请先增加银行待发数据标志！");
		return;
	}
	var salaryid="${bankDiskForm.salaryid}";
   var thecodeurl="/gz/gz_accounting/bankdisk/sort_template_field.do?b_init=link`close=1`bank_id="+bank_id+"`salaryid="+salaryid; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
   if(isIE6() ){
      var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:400px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");	
   }else{
      var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:400px; dialogHeight:340px;resizable:no;center:yes;scroll:no;status:no");	
   }		
	if(retvo)
	{
	  var obj=new Object();
	  obj.rightField=retvo.rightField;
	  bankDiskForm.rightFields.value=obj.rightField;
	  var priv;
	  var rd=document.getElementsByName("attributeflag");
	  var rd1=document.getElementsByName("attributeflag1");
	  if(rd1[0].checked==true){
	   	priv='0';
	  }
	  if(rd[0].checked==true){
		priv='1';
	  }
	  var name=document.getElementsByName("bank_name")[0];
	  var str=PLEASE_INPUT_BANKNAME;
      bankDiskForm.action="/gz/gz_accountingt/bankdisk/addBankItem.do?b_init=init&inputname="+str+"&priv="+priv+"&bankname="+getEncodeStr(name.value)+"&opt=ret";
      bankDiskForm.submit();
	}	
}
function changeFocus(code){
			var rd=document.getElementsByName("attributeflag");
			var rd1=document.getElementsByName("attributeflag1");
			if(code==1){
				rd[0].checked=true;
				rd1[0].checked=false;
			}else{
				rd[0].checked=false;
				rd1[0].checked=true;
			}
		}
//-->
var opt='<%=opt%>';
</script>

<html:form action="/gz/gz_accountingt/bankdisk/editBankTemplate">
<%if("hl".equals(hcmflag)){ %>
<br>
<%}%>
<table width="770px" border="0" cellspacing="0"  align="center" cellpadding="0">

		  <tr>
        <td>
       <table width='97%' border="0" cellspacing="0" align="center"
		cellpadding="0" >
			<tr>
			<td align="left"  rowspan='2' nowrap>
				&nbsp;&nbsp;
				请输入银行名称:	<input type="text" name="bank_name" size="35"
					value="${bankDiskForm.bank_name}" class="inputtext"/>	
			
			<% 
				if(opt==null||opt.equalsIgnoreCase("add")){
			 %>
			 <input type="radio" name="attributeflag" onclick="changeFocus(1);"  checked>&nbsp;
								<bean:message key="label.gz.private" />
								
			<input type="radio" name="attributeflag1" onclick="changeFocus(2);">&nbsp;
								<bean:message key="label.gz.public" />
			 <%}else{ %>
			<input type="radio" name="attributeflag" onclick="changeFocus(1);" <logic:equal name="bankDiskForm" property="scope" value="1"> checked</logic:equal>>&nbsp;
								<bean:message key="label.gz.private" />
								
			<input type="radio" name="attributeflag1" onclick="changeFocus(2);" <logic:equal name="bankDiskForm" property="scope" value="0"> checked</logic:equal>>&nbsp;
								<bean:message key="label.gz.public" />
								<%} %>
			</td>
		</tr>
					
		
		
</table>
<br>
        </td>
        </tr>
       
        <tr>
        <td>
        <fieldset align="center">
        <legend><bean:message key="gz.bankdisk.configure"/></legend>

        <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
        <thead>
         <TR>
        <td align="center" colspan="7">
         <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
         <td style="padding-top:2px;padding-bottom:2px;" align="left">
       <input type="button" value="<bean:message key="kq.emp.button.add"/>" name="new" class="mybutton" onclick='bankdisk_add("${bankDiskForm.code}","${bankDiskForm.bank_id}","${bankDiskForm.tableName}","${bankDiskForm.salaryid}");'/>
       <input type="button" value="<bean:message key="kh.field.delete"/>" name="new" class="mybutton" onclick='bankdisk_delTabRows("${bankDiskForm.bank_id}");'>
       <input type="button" value="<bean:message key="menu.gz.sortitem"/>" name="sort" class="mybutton" onclick='bankdisk_sortField("${bankDiskForm.bank_id}");'>
       </td>
       </tr>
       </table>
       </td>
        </TR>
        <tr>
        <td align="left" class="TableRow" colspan='7' nowrap>
       <bean:message key="gz.bankdisk.content"/>
        </td>
        </tr>
        </thead>
        <tr>
        <td><!-- 【6169】薪资管理：银行报盘，新增银行报盘页面问题。   jingq upd 2014.12.22 -->
        <div style="overflow:auto;height:200px;border:1px solid;border-top:none;border-bottom:none;" class="common_border_color">
         <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"  id="dataTable" class="ListTable1" >
       <thead>
        <tr>
        <td align="center" class="TableRow" style='border-top:0;border-left:none;' nowrap>
        <bean:message key="lable.select"/>
        </td>
        <td align="center" class="TableRow" style='border-top:0' nowrap>
      <bean:message key="gz.bankdisk.projectname"/>
        </td>
        <td align="center" class="TableRow" style='border-top:0' nowrap>
     <bean:message key="gz.bankdisk.datasource"/>
        </td>
        <td align="center" class="TableRow" style='border-top:0' nowrap>
    <bean:message key="gz.bankdisk.datatype"/>
        </td>
        <td align="center" class="TableRow" style='border-top:0' nowrap>
   <bean:message key="gz.bankdisk.outputlength"/>
        </td>
        <td align="center" class="TableRow" style='border-top:0;' nowrap>
  <bean:message key="gz.bankdisk.dataformat"/>
        </td>
        </tr>
        </thead>
        <%int i=0;%>
        <logic:iterate id="element" name="bankDiskForm" property="selectedFieldList" indexId="index">
         <%if(i%2==0){ %>
	     <tr class="trShallow">
          <%} else { %>
	     <tr class="trDeep">
	      <% }
	      %>
       <td align="center" width="3%" class="RecordRow" style="border-left:none;" nowrap>
       <input type="checkbox" name="itemidArray" value="<bean:write name="element" property="itemid"/>">
       </td>
          
         <td align="left" width="15%" class="RecordRow" nowrap>
         <input type="text" name="<%="selectedFieldList["+index+"].itemdesc"%>"  size="20" value="<bean:write name="element" property="itemdesc"/>" class="inputtext">
        </td>
         <td align="left" width="5%" class="RecordRow" nowrap>
         <bean:write name="element" property="itemid"/>
          <input type="hidden" name="<%="selectedFieldList["+index+"].itemid"%>" value="<bean:write name="element" property="itemid"/>">
        </td>
         <td align="left" width="5%" class="RecordRow" nowrap>
         <bean:write name="element" property="itemtype"/>
          <input type="hidden" name="<%="selectedFieldList["+index+"].item_type"%>" value="<bean:write name="element" property="item_type"/>">
        </td>
         <td align="right" width="5%" class="RecordRow" nowrap>
          <input type="text" style='text-align:right' name="<%="selectedFieldList["+index+"].itemlength"%>" size="8" value="<bean:write name="element" property="itemlength"/>"  class="inputtext">
        </td>
         <td align="right" width="10%" class="RecordRow" style="border-right:none;" nowrap>
         <input type="text" style='text-align:right' name="<%="selectedFieldList["+index+"].format"%>" size="15" value="<bean:write name="element" property="format"/>" class="inputtext">
        </td>
        </tr>
        <% i++;%>
        </logic:iterate>
        
        </table>
        </div>
        </td>
       </tr>
      
       </table>
       </fieldset>
        </td>
        </tr>

        <TR>
        <td>
                <br>
        <fieldset align="center">
        <legend><bean:message key="gz.bankdisk.bankcheck"/></legend>
        
        <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
         <tr>
        <td>
          <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
          <tr>
          <td>
          <input type="button" value="<bean:message key="kq.emp.button.add"/>" name="ok" class="mybutton" onclick='bankdisk_addInput("${bankDiskForm.code}","${bankDiskForm.bank_id}","${bankDiskForm.tableName}","${bankDiskForm.salaryid}");'>
           <input type="button" value="<bean:message key="kh.field.delete"/>" name="back" class="mybutton" onclick="delInput();" >
          </td>
          </tr>
        </table>
       
        </td>
        </tr>
        <tr>
        <td>
        <div style="overflow:auto;width:760px;height:100;align:center;vertical-align: middle;" >
         <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
        <tr class="trShallow">
        <td align="left" width="90%" class="RecordRow" colspan="5" nowrap>
        <logic:equal name="bankDiskForm" property="bankCheck" value="1">
        <input type="radio" name="bankCheck" value="1" checked><bean:message key="gz.bankdisk.firstrow"/>
        </logic:equal>
        <logic:notEqual name="bankDiskForm" property="bankCheck" value="1">
        <input type="radio" name="bankCheck" value="1"><bean:message key="gz.bankdisk.firstrow"/>
        </logic:notEqual>
        <logic:equal name="bankDiskForm" property="bankCheck" value="2">
         <input type="radio" name="bankCheck" value="2" checked><bean:message key="gz.bankdisk.lastrow"/>
         </logic:equal>
         <logic:notEqual name="bankDiskForm" property="bankCheck" value="2">
              <input type="radio" name="bankCheck" value="2"><bean:message key="gz.bankdisk.lastrow"/>
              </logic:notEqual>
        <logic:equal name="bankDiskForm" property="bankCheck" value="0">
         <input type="radio" name="bankCheck" value="0" checked><bean:message key="gz.bankdisk.no"/>
         </logic:equal>
         <logic:notEqual name="bankDiskForm" property="bankCheck" value="0">
          <input type="radio" name="bankCheck" value="0"><bean:message key="gz.bankdisk.no"/>
          </logic:notEqual>
        </td>
        </tr>
        <tr>
        <TD>
    	<div class="complex_border_color" style="height: 60px;overflow: auto;padding: 2px;width: 100%;vertical-align: middle;">
          <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" id="inputTable" class="ListTable">
	        <tr class="trDeep" valign="middle">
	                
		        
	        </tr>
        </table>
        </div>
        </TD>
        </tr>
        </table>
        </div>
        </td>
        </tr>
       
        </td>
        </TR>
       
        </table>
          </fieldset>
        <table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0">
        <tr>
         <td align="center" colspan="6" nowrap height="35px;">
         <input type="button" value="<bean:message key="button.ok"/>" name="ok" class="mybutton" onclick="bankdisk_sub();">
       <input type="button" value="<bean:message key="button.close"/>" name="back" class="mybutton" onclick='window.close();'>
       <input type="hidden" name="bank_id" value="${bankDiskForm.bank_id}">
        <input type="hidden" name="salaryid" value="${bankDiskForm.salaryid}">
        <input type="hidden" name="code" value="${bankDiskForm.code}">
        <input type="hidden" name="tableName" value="${bankDiskForm.tableName}">
        <input type="hidden" name="bankFormatValue" value="">
        <input type="hidden" name="rightFields" value=""> 
        <input type="hidden" name="scope" value="${bankDiskForm.scope}"> 
         </td>
        </tr>
        
        </table>

</html:form>
<script type="text/javascript">
<!--
isclose("${bankDiskForm.isclose}");
initBankFormat("inputTable","${bankDiskForm.bankFormat}");
//-->
</script>