<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
String home=(String)request.getParameter("home");
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String url="";
if(userView != null)
{
	url=userView.getBosflag();
}
%>

<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

  <SCRIPT LANGUAGE=javascript>
       
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();	
	hashvo.setValue("userbase","${historyStatForm.userbase}");
	hashvo.setValue("statid","${historyStatForm.statid}");
	hashvo.setValue("querycond","${historyStatForm.querycond}");
	hashvo.setValue("infokind","${historyStatForm.infokind}");
	var curr = new Array();
	var a = new Array();
	for(var i=0;i<a.length;i++){
		curr.push(a[i]);
	}
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'02040001003'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");
	   url=decode(url)
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
   }
	function doExcel()
   {

	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+${historyStatForm.filename});
   }
  
  function returnhome()
       {
         
            historyStatForm.action="/workbench/browse/history/showinfo.do?b_search=link&action=showinfodata.do&target=nil_body&userbase=usr";
            historyStatForm.target="il_body";
            historyStatForm.submit();
       }
  function changedate(val){
  			historyStatForm.action="/general/static/commonstatic/history/statshow.do?b_doubledata=data";
            historyStatForm.submit();
  }
</SCRIPT> 

<hrms:themes></hrms:themes>
<html:form action="/general/static/commonstatic/history/statshow">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
    <tr>
          <td align="left"  nowrap>
             
       	   历史时点
             <html:select property="backdates" name="historyStatForm" onchange="changedate(this.value);">
             	<html:optionsCollection property="backdateslist" value="dataValue" label="dataName"/>
             </html:select>
      </td>                	    	    	    		        	        	        
   </tr>      
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
      	${historyStatForm.html }
      	<!-- 
      		<tr >
      		<td rowspan="2" colspan="2" width="20%" class="TableRow"></td>
      		<td colspan="2" width="32%" align="center" class="TableRow">性别比例</td>
      		<td colspan="3" width="48%" align="center" class="TableRow">年龄分布</td>
      	</tr>
      	<tr>
      		<td align="center" class="TableRow">男</td>
      		<td align="center" class="TableRow">女</td>
      		<td align="center" class="TableRow">30岁以下</td>
      		<td align="center" class="TableRow">30~40岁</td>
      		<td align="center" class="TableRow">40以上</td>
      	</tr>
      	<tr>
      		<td rowspan="3" style="writing-mode:tb-rl" align="center" class="TableRow">年龄分布</td>
      		<td align="center" class="TableRow">30以下</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">10</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">40</td>
      	</tr>
      	<tr>
      		<td align="center" class="TableRow">30~40岁</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">10</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">40</td>
      	</tr>
      	<tr>
      		<td align="center" class="TableRow">40岁以上</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">10</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">40</td>
      	</tr>
      	<tr>
      		<td rowspan="2" style="writing-mode:tb-rl" align="center" class="TableRow">性别比例</td>
      		<td align="center" class="TableRow">男</td>
      		<td align="center" class="RecordRow">10</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">40</td>
      		<td align="center" class="RecordRow">80</td>
      	</tr>
      	<tr>
      		<td align="center" class="TableRow">女</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">10</td>
      		<td align="center" class="RecordRow">20</td>
      		<td align="center" class="RecordRow">30</td>
      		<td align="center" class="RecordRow">40</td>
      	</tr>
      	 -->
      </table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" height="35px">
	<TR>
		<TD nowrap>
			<input type="button" name="b_save" value="<bean:message key="report.actuarial_report.exportExcel"/>" class="mybutton" onclick="doExcel();">
			<input type="button" name="b_save" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnhome('')">
		 </TD>
	</TR>
</table>
</html:form>
