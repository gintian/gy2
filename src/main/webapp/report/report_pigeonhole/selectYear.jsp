<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<html>

<HEAD>
<META HTTP-EQUIV='pragma' CONTENT='no-cache'> 
<TITLE>
<bean:message key="edit_report.reportDataMerger"/>
</TITLE>

</HEAD>

 <link href="/css/css1.css" rel="stylesheet" type="text/css">
 <script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript">
//无用代码谷歌下报错 wangbs 20190318
// var info=dialogArguments;  // 0:unitcode  1:tabid  2:narch


function submit_value()
{
	var a_object=eval("document.f1.reportType");
	var num=0;
	for(var i=0;i<a_object.length;i++)
	{
		if(a_object[i].checked==true)
			num=a_object[i].value;
	}
	var desc="";
	if(num==1)
		desc=REPORT_GENERAL;
	else if(num==2)
		desc=REPORT_YEAR;
	else if(num==3)
		desc=REPORT_HALFYEAR;
	else if(num==4)
		desc=REPORT_QUARTER;
	else if(num==5)
		desc=REPORT_MONTH;
	else if(num==6)
		desc=REPORT_WEEK;
	if(!confirm(REPORT_INFO52+" '"+desc+"'？"))
			return;

    //兼容谷歌、ie wanbgs 20190318
    if(parent.Ext){
        var operateTarget = parent.Ext.getCmp("formArchiveType");
        if(operateTarget){
            operateTarget.year_value = num;
            closeWindow();
        }else{
            returnValue=num;
            window.close();
        }
    }else{
        returnValue=num;
        window.close();
	}
}


function closeWindow()
{
    //兼容谷歌、ie wanbgs 20190318
    if(parent.Ext){
        if(parent.Ext.getCmp("formArchiveType")){
            parent.Ext.getCmp("formArchiveType").close();
        }else{
            window.close();
		}
    }else{
        window.close();
	}
}





</script>

<body   >

<base id="mybase" target="_self">
<hrms:themes/>
<form name='f1'  >
	<table  width="380px" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable" cellpadding="0">   
		        <tr valign="top">  
		         <td width="380px" valign="top">
						<fieldset align="left" style="height: 230px;width:390px;margin-left: 1px;">
    							 <legend ><bean:message key="report.pigeonholeType"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="30%" height="30" >
			                						<INPUT type='radio' name='reportType' checked value='1' ><bean:message key="report.pigeonhole.generalReport"/>
			                						
			                					</td>
			                				
		                					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='2' > <bean:message key="report.pigeonhole.yearReport"/>
		                      					</td>
		                      					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='3'  > <bean:message key="report.pigeonhole.halfyearReport"/>
		                      					</td>
		                      					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='4'  ><bean:message key="report.pigionhole.quarterReport"/>
		                      					</td>
		                      					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='5'  ><bean:message key="report.pigionhole.monthReport"/>
		                      					</td>                  					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='6'  ><bean:message key="report.pigionhole.weekReport"/>
		                      					</td>                  					
		                      				</tr>
		                      		
		                      			</table>
		                      		</fieldset>
		                  	</td>
		                  </tr>
		                  <tr><td align="center" style="padding-top:10px;">
	                  		<html:button  styleClass="mybutton" property="b_addfield" onclick="submit_value()" >
			            		     <bean:message key="button.ok" />
				            </html:button>
				            <html:button  styleClass="mybutton" property="b_addfield" onclick="closeWindow()" >
			            		     <bean:message key="button.cancel"/>
				            </html:button>
		                  
		                  </td></tr>
		                  
		                  
		                </table>


</form>

</body>
</html>
