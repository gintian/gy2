<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	 function savefield()
	 {
	 	//var dbname = document.getElementById("choice").value;
	 	var hashvo=new ParameterSet();
	 	var dblist = document.getElementsByName("dbstr");
	 	//var dbtrue=new Array();
	 	var mess="";
	 	for(var i=0;i<dblist.length;i++)
	 	{
	 		if(dblist[i].checked==true){
	 			//dbtrue[i] = "true";
	 		//else
	 			//dbtrue[i] = "false";
	 			mess+=dblist[i].value+',';
	 		}
	 	}
	 	mess=mess.substring(0,mess.length-1);
	 	var thevo=new Object();
		thevo.mess=mess;
         var extWin = parent.parent.Ext.getCmp('ps_parameter');
         if(extWin){
             extWin.msg = thevo ? thevo : '';
             extWin.close();
         }
         //换用Ext window后之前的代码不适用了  wangbs 2019年3月12日10:40:21
         // if(navigator.appName.indexOf("Microsoft")!= -1){
		// 	window.returnValue=thevo;
		// }else {
		// 	top.returnValue=thevo;
		// }
		// top.close();
	 	//hashvo.setValue("dbtrue",dbtrue);
	 	//hashvo.setValue("dbname",dbname);
	 	//var request=new Request({method:'post',onSuccess:showSelect,functionId:'18010000016'},hashvo);
	 }
	 function showSelect(outparamters)
	 {
		 var mess=outparamters.getValue("mess");        
         var thevo=new Object();
		 thevo.mess=mess;
		 window.returnValue=thevo;
		 window.close(); 
	 } 
</script>
<html:form action="/pos/posparameter/ps_parameter">
<table width="290" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <tr><!-- 【7758】组织机构/编制管理，编制参数设置界面，人员库设置界面，出现了双滚动条了，不对。 jingq upd 2015.03.02 -->
	   	  <td align="left" nowrap>
	   	  <div style="height:165px;width:100%;overflow:auto;">
	   	  	    <table border=0>
	           		<logic:iterate id="db" name="posCodeParameterForm" property="dbprelist">
	           		<tr>
	           			<td nowrap>&nbsp;
	           				<logic:notEqual name="db" property="dbname" value="">
				           		
				           		<input type="checkbox" name="dbstr" value='<bean:write name="db" property="pre" />' <logic:equal name="db" property="choose" value="1">checked </logic:equal> />
				           		<bean:write name="db" property="dbname" />
			           		</logic:notEqual>
			           		<logic:equal name="db" property="dbname" value="">
			           			&nbsp;
			           		</logic:equal>
		           		</td>
		           	</tr>
	           		</logic:iterate>
	           	</table>
	      	</div>
	      </td>
      </tr>
      <tr>
	      <td align="center" style="height: 35px;" nowrap>
	      		<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefield();">
	     		<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="parent.parent.Ext.getCmp('ps_parameter').close();">
	      </td>
      </tr>
       
</table>
</html:form>