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
	 	winclose();
	 }
	 function showSelect(outparamters)
	 {
	 	 var mess=outparamters.getValue("mess");        
         var thevo=new Object();
		 thevo.mess=mess;
		 if(parent.parent.Ext){
	  	 	var win = parent.parent.Ext.getCmp('select_db');
	  	 	win.return_vo =ab;
	  	 	win.close();
	  	 }else{
			 window.returnValue=thevo;
			 window.close(); 
	  	 }
	 	
	 } 
function save()
{
  sysParamForm.action = "/system/options/param/set_sys_param.do?b_savedb=link&rflag=1";
  sysParamForm.taget="_self";
  sysParamForm.submit();
  
}
//关闭弹窗方法  wangb 20190319
function winclose(){
	if(parent.parent.Ext){
		parent.parent.Ext.getCmp('select_db').close();
		return;
	}
	window.close();
}
   <%String rflag = (String)request.getParameter("rflag");
		if(rflag!=null&&rflag.equals("1")){
	  %>
	  var ab = new Object();
	  if(parent.parent.Ext){
	  	 var win = parent.parent.Ext.getCmp('select_db');
	  	 win.return_vo =ab;
	  	 win.close();
	  }else{
	  	 window.returnValue = ab;
	     window.close();
	  }
	  
	  <%}%>
</script>
<html:form action="/system/options/param/set_sys_param">
<table width="290" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <tr>
	   	  <td width="100%" align="left"  nowrap>
	   	  <!-- 【7533】系统管理人员库设置，出现滚动条,  jingq add 2015.02.10 -->
	   	  <div style="width:100%;height:180px;overflow:auto;">
	   	  	<table >
	   	  		<logic:iterate id="db" name="sysParamForm" property="dbprelist">
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
	      <td align="center" nowrap style="height: 35px;border-top:#E5E5E5 1pt solid">
	      		<input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" save();">
	     		<input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick="winclose();">
	      </td>
      </tr>
</table>
</html:form>