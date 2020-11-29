
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>

  <script LANGUAGE=javascript src="/js/function.js"></script> 
  <%
  	UserView userView = (UserView) pageContext.getSession().getAttribute(
	WebConstant.userView);
   %>
  <script language="javascript">
 
   function sub()
  {
		var scopename =getEncodeStr(trim(document.getElementsByName("scopename")[0].value));
		var scopeownerunit =getEncodeStr(trim(document.getElementsByName("scopeownerunitid")[0].value));
		var scopeunits =getEncodeStr(trim(document.getElementsByName("scopeunitsids")[0].value));
		var scopeid =getEncodeStr(trim(document.getElementsByName("scopeid")[0].value));
	 	if(scopename.length==0)
		{
			alert(STATICSTATEMENTNAME+"!");
			return;
		}
		if(scopeownerunit.length==0)
		{
			alert(OWNERORGNAZITION+"!");
			return;
		}
	 	var hashvo=new ParameterSet();
			hashvo.setValue("scopename",scopename);
			hashvo.setValue("scopeownerunit",scopeownerunit);	
			hashvo.setValue("scopeunits",scopeunits);
			hashvo.setValue("scopeid",scopeid);			
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03020000044'},hashvo);
		
   }
 
  function returnInfo(outparamters){
 	var info=outparamters.getValue("info");
	  	if(info!="")
	  	{
	  var scopeobj = new Object();
		    scopeobj.info = outparamters.getValue("info");
		    scopeobj.scopeid = outparamters.getValue("scopeid");
		   	scopeobj.scopename=outparamters.getValue("scopename");
		   	scopeobj.displayid=outparamters.getValue("displayid");
	  	window.returnValue=scopeobj;
   		window.close();
	  	}else{
	  	alert(SAVEFAILED+"!");
	  	}
  
  }
  function can(){
  window.close();
  }
  function getorg()
	{
		var hashvo=new ParameterSet();
				var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnorg,functionId:'03020000073'},hashvo);
	
	}
function returnorg(outparamters){
	var flag=outparamters.getValue("flag"); 
		var ret_vo=select_org_emp_dialog_report(0,1,0,1,0,1,0,flag);
		if(ret_vo)
		{
			var	re=/,/g;
			var tmp=ret_vo.content;
			var str=tmp.replace(re,"`");
			$('scopeunits').value=ret_vo.title;
			$('scopeunitsids').value=str/*ret_vo.content*/;
		}
	}
	function getownerorg()
	{
	var hashvo=new ParameterSet();
				var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnownerorg,functionId:'03020000073'},hashvo);
	
	}
	function returnownerorg(outparamters){
	var flag=outparamters.getValue("flag"); 
		var ret_vo=select_org_emp_dialog_report(0,2,0,1,0,1,0,flag);
		if(ret_vo)
		{
			var	re=/,/g;
			var tmp=ret_vo.content;
			var str=tmp.replace(re,"`");
			$('scopeownerunit').value=ret_vo.title;
			$('scopeownerunitid').value=str/*ret_vo.content*/;
		}
	}
	function select_org_emp_dialog_report(flag,selecttype,dbtype,priv,isfilter,loadtype,level,viewunit)
{
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?flag="+flag+"&selecttype="+selecttype+"&dbtype="+dbtype+
                "&priv="+priv + "&isfilter=" + isfilter+"&loadtype="+loadtype+"&level="+level+"&viewunit="+viewunit;
     var return_vo= window.showModalDialog(theurl,1, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
   </script>
  <body >
  <html:form action="/report/edit_report/editReport/staticStatement">
  <html:hidden name="staticStatementForm" property="scopeid"/> 
  <br>
  <br>
  <br>
  <table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
          <thead>
            <tr> 
            <logic:equal name="staticStatementForm" property="scopeid" value="">
              <td align="left" class="TableRow"><bean:message key="button.new.add"/><bean:message key="report.static.statement"/>&nbsp; </td>
            </logic:equal>
            <logic:notEqual name="staticStatementForm" property="scopeid" value="">
              <td align="left" class="TableRow"><bean:message key="label.edit"/><bean:message key="report.static.statement"/>&nbsp; </td>
            </logic:notEqual>
            </tr>
          </thead>
          <tr>
           <td align="center" class="RecordRow" nowrap>
          		<table border='0' >
          	
                <tr>
                    <td align="right" height="30" ><bean:message key="report.static.name"/></td>
                    <td><html:text name="staticStatementForm" property="scopename" maxlength="20" styleClass="text" /></td>
                </tr> 
               <tr>
                    <td align="right" height="30" ><bean:message key="report.owner.unit"/></td>
                    <td><html:text name="staticStatementForm" property="scopeownerunit" maxlength="20" readonly="true" styleClass="text" />
                    	<img src="/images/code.gif" onclick="getownerorg();"/>   	      
                	      	<html:hidden name="staticStatementForm" property="scopeownerunitid"/> 
                    </td>
                </tr> 
                 <tr>
                    <td align="right" height="30" ><bean:message key="report.units"/></td>
                   <td align="left" nowrap >
                	      	<html:text name="staticStatementForm" property="scopeunits" size="20" maxlength="60" readonly="true" styleClass="text"/> 
                	      	<img src="/images/code.gif" onclick="getorg();"/>   	      
                	      	<html:hidden name="staticStatementForm" property="scopeunitsids"/>  
                          </td>
                </tr>      
		</table>
       </td>
	 </tr> 
    </table>
  	<table  width="70%" align="center">
          <tr>
            <td align="center"> 
            
              <input type="button" name="b_add2" value="<bean:message key="lable.func.main.return"/>" class="mybutton" onClick="sub()">
         	 
         	 
         	  <input type="button" value="<bean:message key="button.cancel"/>" class="mybutton" onclick="can();">
             
            </td>
          </tr>          
     </table> 
    </html:form>			                               
  </body>
</html>
