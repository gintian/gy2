
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm"%>
<html>

	<hrms:themes></hrms:themes>
    <script language="JavaScript" src="/js/validate.js"></script>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script LANGUAGE=javascript src="/system/bos/menu/menument.js"></script>
	<script language="javascript">
 function saveSort(){ 
	var sorting = selectTostr('sort_fields');
	document.menuMainForm.action="/system/bos/menu/menuMain.do?b_savepayMenu=save&sorting="+sorting;
	document.menuMainForm.submit();
}
<% 
	if(request.getParameter("b_savepayMenu")!=null&&request.getParameter("b_savepayMenu").equals("save")){
	 %>
		var sorting = '${menuMainForm.sorting}';
  		 var menu_base_vo = new Object();
		    menu_base_vo.menu_id = sorting;
		    window.returnValue = menu_base_vo;
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
<html:form action="/system/bos/menu/menuMain">
		<center>
<table width="390" height="100%" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr> 
    <td align="center" valign="top" height="250px;">
    	<html:hidden name="menuMainForm" property="menuid"/>
    	<fieldset style="width:100%;height:100%">
    	<legend><bean:message key='lable.main.sortitem'/></legend>
    	<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
    	<tr>
    		<td width="90%" align="center" valign="middle">
    			<html:select name="menuMainForm" property="sort_fields" multiple="multiple"  style="height:240px;width:95%;font-size:10pt">
                    <html:optionsCollection property="sortlist" value="dataValue" label="dataName"/>   		      
 		     	</html:select>
    		</td>
    		<td width="10%" align="center" valign="middle">
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
