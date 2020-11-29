
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm"%>
<html>

	<hrms:themes></hrms:themes>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script language="javascript">
   function sub()
  {
		
	var portal_id = getEncodeStr(trim(document.getElementsByName("addportal_id")[0].value));
	var portal_name =getEncodeStr(trim(document.getElementsByName("addportal_name")[0].value));
//	var colnum = getEncodeStr(trim(document.getElementsByName("colnum")[0].value));
  	var parentid = getEncodeStr(trim(document.getElementsByName("parentid")[0].value));
	var opt = '${portalMainForm.opt}';
		if(portal_id.length==0)
		{
			alert("编号不能为空");
			return;
		}
	 	if(portal_name.length==0)
		{
			alert("名称不能为空");
			return;
		}
		
	 	document.portalMainForm.action="/system/bos/portal/portalMain.do?b_findadd=add&portal_name="+portal_name+"&portal_id="+portal_id+"&parentid="+parentid+"&colnum=1";
		document.portalMainForm.submit();
  	}
  	
  	
  	<%
	
	 if(request.getParameter("b_findadd")==null)
 	 	{
		 	PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
			portalMainForm.setAddportal_id(""); 
			portalMainForm.setAddportal_name("");
			 portalMainForm.setColnum("");
	      
		}
	 if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){ %> 
	 	
	 	var portalflag="${portalMainForm.portalflag}";
	 	if(portalflag=='true')
	 	{
	 		alert("该门户分类编号已存在,请重新输入!");
	 		
	 	}

	<% }
	if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){
	 %>
	 	var portalflag="${portalMainForm.portalflag}";
	 	if(portalflag=='false')
	 	{
	var portal_name ="${portalMainForm.addportal_name}";
	var parentid ="${portalMainForm.parentid}";
	var portal_id="${portalMainForm.portalid}";
  		// document.portalMainForm.action="/system/bos/func/portalMain.do?b_SaveFunc=link&portal_name="+portal_name+"&portal_id="+portal_id+"&parentid="+parentid;
		//document.portalMainForm.submit();
  		 var portal_base_vo = new Object();
		    portal_base_vo.portal_name = portal_name;
		    portal_base_vo.portal_id = portal_id;
		   	portal_base_vo.parentid=parentid;
		    window.returnValue = portal_base_vo;
  	window.close();  	
  	}
<%}
	%>
  	
    function can(){
  window.close();
  }
   function checkComments(s){
    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）—|{}【】‘；：”“'。，、？%]");
    var rs = ""; 
    for (var i = 0; i < s.length; i++) { 
       rs = rs+s.substr(i, 1).replace(pattern, '');       
        } 
     return rs; 
    }
    function checkForm(){
     if(event.keyCode ==34){
        event.returnValue = false;
       }
     } 
   </script>
	<body>
		<html:form action="/system/bos/portal/portalMain">
			<html:hidden styleId="parentid" name="portalMainForm"
				property="parentid" />
			<table width="390" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow">
							<bean:message key="lable.portal.main.addportal" />
							&nbsp;
						</td>
					</tr>
				</thead>
				<tr>
					<td align="center" class="RecordRow" nowrap>
						<table border='0'>

							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.id" />
								</td>
								<td>
									<html:text name="portalMainForm" property="addportal_id" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.name" />
								</td>
								<td>
									<html:text name="portalMainForm" property="addportal_name" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
					
							
							

						</table>
					</td>
				</tr>

			</table>

			<table width="70%" align="center">
				<tr>
					<td align="center" height="35px;">

						<input type="button" name="b_add2"
							value="<bean:message key="lable.portal.main.return"/>"
							class="mybutton" onClick="sub()">


						<input type="button" value="<bean:message key="button.cancel"/>"
							class="mybutton" onclick="can();">

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
