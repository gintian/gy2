<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.hjsj.utils.Sql_switcher,
com.hjsj.hrms.actionform.report.edit_report.EditReportForm,
com.hjsj.hrms.actionform.report.auto_fill_report.ReportOptionForm,
com.hjsj.hrms.utils.PubFunc"%>
<script language=javascript src="/js/validate.js"></script>     
<script language=javascript src="/js/function.js"></script> 

<%
	EditReportForm editReportForm = (EditReportForm) session.getAttribute("editReportForm");
    String reverseSql = PubFunc.encrypt(editReportForm.getReverseSql());   //add by wangchaoqun on 2014-9-29
%>

<style>
.reportDiv 
{ 
	height:90%;
	width:100%;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    margin:0px 5px -1px 4px;
}
#excel{
	margin-left: 200px;
}
</style>

<script language='javascript'>
var reverseSql='<%=reverseSql %>';
var setMap_str="${editReportForm.setMap_str}";
var fieldItem_str="${editReportForm.fieldItem_str}";
var username1 = "${editReportForm.username1}";
var flag = "${editReportForm.flag}";
function change()
{
	
	editReportForm.action="/report/edit_report/editReport.do?b_reverseFind=find&pageNum="+editReportForm.pageNum.value+"&gridName=<%=request.getParameter("gridName")%>&tabid=<%=request.getParameter("tabid")%>&count=<%=request.getParameter("count")%>";
	editReportForm.submit();

}



function exportReverseExcel()
{
	 
	    var hashvo=new ParameterSet();
	    hashvo.setValue("tabid","${editReportForm.tabid}");
	    hashvo.setValue("unitcode" ,"${editReportForm.unitcode}");
	    hashvo.setValue("operateObject","5");	
	    hashvo.setValue("reverseSql",reverseSql);    
	    hashvo.setValue("setMap_str",setMap_str);
	    hashvo.setValue("fieldItem_str",fieldItem_str);
	    hashvo.setValue("username",username1);
	    hashvo.setValue("scanMode","${editReportForm.scanMode}");
		var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'03030000025'},hashvo);		
}
	

function outFile(outparamters) {
	 	 
		 var outName=outparamters.getValue("outName");
		 window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	
 }
function showOrgContext(codeitemid)
{
   var hashvo=new ParameterSet();
   hashvo.setValue("codeitemid",codeitemid);	
   var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'0401004003'},hashvo);
}
function getContext(outparamters)
{
    code=outparamters.getValue("codeitemid");
	kind=outparamters.getValue("kind");
	orgtype=outparamters.getValue("orgtype");
	parentid=outparamters.getValue("parentid");
	codesetid=outparamters.getValue("codesetid");
}
function editorg()
{
window.location.target="_blank";
   window.location.href="/workbench/orginfo/editorginfodata.do?b_search=link&code="+code+"&kind="+kind+"&orgtype="+orgtype+"&parentid="+parentid+"&returnvalue=scan&scantype=scan&return_codeid=${orgInfoForm.code}&edittype=update&isself=0";
   
}		  

</script>
<html>
<body>
<html:form action="/report/edit_report/editReport" style="width:98%">
<%if(editReportForm.getFlag().equals("1")){%>
  <%=editReportForm.getReverseHtml() %>
<%}else if(editReportForm.getFlag().equals("0")){ %>
  <table width="300" border="0" style="padding-left: 10px;" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow">&nbsp;信息提示&nbsp;</td>
          </tr> 
                    <tr >
              	      <td align="left" valign="middle" nowrap style="height:120">如果该单元格定义了计算公式或定义统计（取值）方法为统计非个数时，则不支持反查！</td>
                    </tr> 
 
                    <tr >
                      <td align="center" style="height:35">
              		<input type="button" name="btnreturn" value="返回" onclick="self.close();" class="mybutton">
                      </td>
                    </tr>   
          
  </table> 

<%} else if(editReportForm.getFlag().equals("2")){ %>
  <table width="500" border="0" style="margin-right: 5px;" cellpadding="0" cellspacing="0" align="center" class="ftable">
          <tr height="20">
       		<td align="left" class="TableRow">&nbsp;信息提示&nbsp;</td>
          </tr> 
                    <tr >
              	      <td align="left" valign="middle" nowrap style="height:120">编号行列不支持反查！</td>
                    </tr> 
 
                    <tr >
                      <td align="center" style="height:35">
              		<input type="button" name="btnreturn" value="返回" onclick="self.close();" class="mybutton">
                      </td>
                    </tr>   
          
  </table> 

<%} %>

</html:form>
</body>
</html>

<script language='javascript'>
	var flag=${editReportForm.flag};
	if(flag==3)
		self.close();
	
	if(document.getElementsByClassName('reportDiv')[0])
		document.getElementsByClassName('reportDiv')[0].parentNode.parentNode.style.overflow="hidden";
		
	document.body.focus();
		
</script>
