<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	 function savefield()
	 {
	 	var dbname = document.getElementById("choice").value;
	 	var hashvo=new ParameterSet();
	 	var dblist = document.getElementsByName("dbstr");
	 	var dbtrue=new Array();
	 	for(var i=0;i<dblist.length;i++)
	 	{
	 		if(dblist[i].checked==true)
	 			dbtrue[i] = "true";
	 		else
	 			dbtrue[i] = "false";
	 	}
	 	hashvo.setValue("dbtrue",dbtrue);
	 	hashvo.setValue("dbname",dbname);
	 	var request=new Request({method:'post',onSuccess:showSelect,functionId:'05603000018'},hashvo);
	 	//window.close();
	 }
	 function showSelect(outparamters)
	 {
	 	 var mess=outparamters.getValue("mess");        
         var thevo=new Object();
		 thevo.mess=mess;
		 
		 if(parent.parent.Ext && parent.parent.Ext.getCmp('org_setDb')){
		 	 var extWin = parent.parent.Ext.getCmp('org_setDb');
			 extWin.msg = thevo;
			 extWin.close();
		 }else if(parent.parent.Ext &&parent.parent.Ext.getCmp('select_db')){
		 	 var win = parent.parent.Ext.getCmp('select_db');
		 	 win.return_vo = thevo;
		 	 win.close();
		 }else {
		 	parent.window.returnValue=thevo;
	 		window.close();
		 }
	 	
	 } 
	 
	 function winclose(){
	 	if(parent.parent.Ext &&parent.parent.Ext.getCmp('org_setDb'))
	 		 parent.parent.Ext.getCmp('org_setDb').close();
	 	else if(parent.parent.Ext &&parent.parent.Ext.getCmp('select_db'))
	 		 parent.parent.Ext.getCmp('select_db').close();
	 }
</script>
<html:form action="/general/deci/leader/param">
<html:hidden styleId="choice" name="leaderParamForm" property="field_falg" />

<table width="300" style="height: 50px" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <tr style="height: 50px;">
	   	  <td width="100%" align="left" nowrap >
			  <div style="height: 190px ;overflow-y: auto;">
	   	  	<table >
	           		<logic:iterate id="db" name="leaderParamForm" property="dbprelist">
				<tr>
					<td nowrap>&nbsp;
						<logic:notEqual name="db" property="dbname" value="">
							<input type="checkbox" name="dbstr" value='<bean:write name="db" property="pre" />' <bean:write name="db" property="check" /> >
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
	      <td align="center" nowrap  colspan="3" style="height: 35px; border-top: none;display: block; line-height: 36px;">
	      		<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefield();">
	     		<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="winclose();">
	      </td>
      </tr>
</table>
</html:form>