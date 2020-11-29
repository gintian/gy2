
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.func.FunctionMainForm"%>
<html>

	<hrms:themes></hrms:themes>
    <script language="JavaScript" src="/js/validate.js"></script>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script LANGUAGE=javascript src="/system/bos/func/funcment.js"></script>
	<script language="javascript">
 function saveSort(){ 
	var ctrl_ver = '<%=request.getParameter("ctrl_ver")%>';
	ctrl_ver = ctrl_ver=='null'?'':ctrl_ver;
	var sorting = selectTostr('sort_fields');
	document.functionMainForm.action="/system/bos/func/functionMain.do?b_savepayFunc=save&sorting="+sorting+"&ctrl_ver="+ctrl_ver;
	document.functionMainForm.submit();
}
<% 
	if(request.getParameter("b_savepayFunc")!=null&&request.getParameter("b_savepayFunc").equals("save")){
	 %>
		var sorting = '${functionMainForm.sorting}';
  		 var func_base_vo = new Object();
		    func_base_vo.func_id = sorting;
		    window.returnValue = func_base_vo;
  	window.close();  	
  	
<%}
	%>
function selectTostr(listbox){
  var vos,right_vo,i,str='';
  vos= document.getElementsByName(listbox);
  if(vos==null || vos[0].length==0){
  	return;  	
 	vos[0].options[0].selected=false;

  }
  //设为要可选状态
  right_vo=vos[0];  
  for(i=0;i<right_vo.options.length;i++){
	str += right_vo.options[i].value+",";
  }
  return str;  	
}	
   </script>
<body>
<html:form action="/system/bos/func/functionMain">
		<center>
<table width="390" height="100%" border="0" align="center" cellspacing="0" cellpadding="0" >
  <tr> 
    <td align="center" valign="top" height="250px;">
    	<html:hidden name="functionMainForm" property="functionid"/>
    	<fieldset style="width:100%;height:100%;">
    	<legend><bean:message key='lable.main.sortitem'/></legend>
    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
    	<tr>
    		<td width="90%" align="center"><!-- 删除ondblclick="removeitem('sort_fields');"，jingq upd 2014.09.29 -->
    			<html:select name="functionMainForm" property="sort_fields" multiple="multiple"  style="height:240px;width:95%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td width="10%" valign="middle" align="center">
	    		<html:button  styleClass="mybutton" property="b_up" onclick="upItem($('sort_fields'));">
            		<bean:message key="button.previous"/> 
	           	</html:button >
				<html:button  styleClass="mybutton" property="b_down" onclick="downItem($('sort_fields'));" style="margin-top:30px;">
            		<bean:message key="button.next"/>    
	           	</html:button >	 
    		</td>
    	</tr>
    	</table>
    	 </fieldset>
    </td>
  </tr>
  <tr>
    <td align="center">
    	<input type="button" value="<bean:message key='button.ok'/>" onclick="saveSort();" Class="mybutton">
    	<input type="button" value="<bean:message key='button.cancel'/>" onclick="window.close();" Class="mybutton">
    </td>
  </tr>
</table>
</center>
</html:form>

	</body>
</html>
