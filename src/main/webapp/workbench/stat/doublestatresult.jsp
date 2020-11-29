<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	    target_url="/workbench/stat/statset.do?b_search=link&target=mil_body&isoneortwo=2";
    	   newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
       }
       

   function excecuteExcel()
   {
	var hashvo=new ParameterSet();	
	hashvo.setValue("userbase","${statForm.userbase}");
	hashvo.setValue("statid","${statForm.statid}");
	hashvo.setValue("querycond","${statForm.querycond}");
	hashvo.setValue("infokind","${statForm.infokind}");
	var curr = new Array();
	var a = new Array();
	<%
		StatForm statForm = (StatForm)session.getAttribute("statForm");
		String[] curr_id = (String[])statForm.getCurr_id();
		if(curr_id!=null)
		for(int i=0;i<curr_id.length;i++){
	%>
	
	a[<%=i%>] = "<%=curr_id[i]%>";
	<%}%>
	for(var i=0;i<a.length;i++){
		curr.push(a[i]);
	}
	hashvo.setValue("curr_id",curr);
	hashvo.setValue("preresult","${statForm.preresult}");
	hashvo.setValue("history","${statForm.history}");
	hashvo.setValue("result","${statForm.result}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showExcel,functionId:'02040001003'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");
       url=decode(url)
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url);
   }
   </SCRIPT> 

<hrms:themes></hrms:themes>
<html:form action="/workbench/stat/statshow">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
    <tr>
      <td align="center"  nowrap>
            <logic:notEqual name="statForm" property="isshowstatcond" value="1">
               <logic:equal name="statForm" property="infokind" value="1">
               <hrms:priv func_id="04010101">
                   <a href="javascript:statset()"><bean:message key="workbench.stat.statsettitle"/></a>       
               </hrms:priv>
               </logic:equal>
             </logic:notEqual>&nbsp;&nbsp;&nbsp;
       	   <bean:write name="statForm" property="snamedisplay" />&nbsp;(<bean:message key="workbench.stat.stattotalvalue"/><bean:write name="statForm" property="totalvalue" />)
      </td>                	    	    	    		        	        	        
   </tr>      
</table>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
           <td align="center" class="TableRow" nowrap>
               <!--<bean:write name="statForm" property="querycond"/> --> 
           </td> 
            <logic:iterate id="element" name="statForm" property="varraylist">
              <td align="center" class="TableRow" nowrap>
                 <bean:write name="element" property="legend"/>
              </td>   
             </logic:iterate>                      	    	    	    		        	        	        
           </tr>
   	  </thead>
   	  <logic:iterate id="element" name="statForm" property="harraylist" indexId="indexh">
   	  <tr>
           <td align="center" class="TableRow" nowrap>
               <bean:write name="element" property="legend"/> 
           </td> 
            <logic:iterate id="helement" name="statForm" property="varraylist" indexId="indexv">
              <td align="center" class="RecordRow" nowrap>
                <a href="/workbench/stat/statshow.do?b_double=link&querycond=${statForm.querycond}&v=${indexv}&h=${indexh}&flag=2">${statForm.statdoublevalues[indexv][indexh]}</a>
              </td> 
               </logic:iterate>          	    	    	    		        	        	        
           </tr> 
           </logic:iterate>    	         
</table>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1">
	<tr>
		<td  nowrap>
			<input type="button" name="b_excel" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="excecuteExcel();">
		</TD>
	</tr>
</table>
</html:form>
