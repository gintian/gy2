<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link href="../../css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes/>
<html>
  <head>
   <title>dfads</title>

  </head>
  <script language="JavaScript" src="/js/validate.js"></script>
  <script language='javascript'>
	function add()
	{
		var value="";
		var value2="";
		for(var i=0;i<document.searchReportUnitForm.elements.length;i++)
		{
			if(document.searchReportUnitForm.elements[i].type=='checkbox'&&document.searchReportUnitForm.elements[i].name !="selbox")
			{
				if(document.searchReportUnitForm.elements[i].checked==false)
				{
					value+=document.searchReportUnitForm.elements[i].value+",";
				}
				else
					value2+=document.searchReportUnitForm.elements[i].value+",";
			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("value",value); 
		hashvo.setValue("value2",value2);
	    hashvo.setValue("unitcode",'<%=(request.getParameter("unitcode"))%>');
	    hashvo.setValue("analysereportflag",'${searchReportUnitForm.analysereportflag}');
	    var request=new Request({method:'post',asynchronous:false,onSuccess:returnInfo,functionId:'0305000025'},hashvo);			
	}

	 function returnInfo()
	 {
		var reportWin = parent.Ext.getCmp("setReportWin");
		if(!reportWin)
		   	window.close();
		else
			reportWin.close();
	 }

  </script>
  <style type="text/css">
	.gray {	
		background:  #00FFFF;
		font-size: 12px;
		color: #000000;
	}
	#scroll_box {
	          
	           height: 340px;    
	           width: 430px;            
	           overflow: auto;            
	           margin: 1em 0;
	       }
	.RecordRow {
		height: 100%;
	}
 </style>
  <body>
   <form name="searchReportUnitForm" style="margin-top:-10px" method="post" action="/report/org_maintenance/reporttypelist.do">
   		<br>
   		<fieldset align="center" style="width:94%;margin:auto;">
		 <legend align="left" style="margin-left:10px;margin-top:-10px;background-color: white;padding:4px"><bean:message key="report.editReport" /></legend>	
   			
   			 <table width="100%" border="0">
		        <tr> 
		          <td height="325" valign="top">
		          <div id="scroll_box">
   			
					<table width="90%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
						<thead>
							<tr>
								<td align="center" class="TableRow" nowrap>
								 <input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="kq.report.id" />
									
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="report.reportlist.reportname2" />
								</td>
							</tr>
						 </thead>
	   					<logic:iterate id="element" name="searchReportUnitForm" property="reportList" >
	   						<tr class="trShallow">
	   								<td align="center" class="RecordRow" nowrap>
									<logic:equal name="element" property="flag" value="1">
										<input type='checkbox' name='id' checked  value='<bean:write name="element" property="tabid" filter="false" />' />
									</logic:equal>
									<logic:equal name="element" property="flag" value="0">
										<input type='checkbox' name='id' value='<bean:write name="element" property="tabid" filter="false" />' />				
									</logic:equal>
									</td>
		   							
		   							
		   							<td align="left" class="RecordRow" nowrap>
		   							&nbsp;<bean:write name="element" property="tabid" filter="false" />&nbsp;
		   							</td>
		   							<td align="left" class="RecordRow" >
		   							&nbsp;<bean:write name="element" property="name" filter="false" />
		   							</td>
		   					</tr>
		   				</logic:iterate>
	   				</table>
   
   				</div>
		  </td>
        </tr>
     
	</table>
	
   </fieldset>
 
	<table width="70%" align="center">
		<tr>
			<td align="center" nowrap colspan="4">
			<input type="button" value="<bean:message key='reporttypelist.confirm'/>" class="mybutton" onClick="add()">
			<input type="button"  value="<bean:message key='button.cancel'/>" onClick="returnInfo();" class="mybutton">
		  </td>
		</tr>
  </table>
   </form>
  </body>
</html>
