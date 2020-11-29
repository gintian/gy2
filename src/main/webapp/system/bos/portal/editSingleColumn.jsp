
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm"%>
<html>

	<hrms:themes></hrms:themes>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script LANGUAGE=javascript src="/system/bos/Portal/portalment.js"></script>
	<script language="javascript">
     function subSave()
  {
  	//var portal_id = getEncodeStr(trim(document.getElementsByName("editportal_id")[0].value));
  	var portal_id = $URL.encode(getEncodeStr(trim(document.getElementsByName("preportal_id")[0].value)));
	var portal_name =$URL.encode(getEncodeStr(trim(document.getElementsByName("editportal_name")[0].value)));
  	var hide=$URL.encode(getEncodeStr(trim(document.getElementsByName("hide")[0].value)));
  	var preportal_id=$URL.encode(getEncodeStr(trim(document.getElementsByName("preportal_id")[0].value)));
   	var colnum ='${portalMainForm.colnum}';
  	var opt = '${portalMainForm.opt}';
//	if(columnsuper=="true"){
//	alert("该节点的子节点数超过设置的列数");
//	return;
//	}
	 	if(portal_name.length==0)
		{
			alert("名称不能为空");
			return;
		}
		colnum = getEncodeStr(colnum);
	 		document.portalMainForm.action="/system/bos/portal/portalMain.do?b_findedit=edit&portal_name="+portal_name+"&portal_id="+portal_id+"&preportal_id="+preportal_id+"&opt="+opt+"&hide="+hide;
			document.portalMainForm.submit();
  
  	}
  	
  		<%  
  	 if(request.getParameter("b_findedit")==null)
 	 	{
		 		PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
		 	
			portalMainForm.setEditportal_id(portalMainForm.getCodeitemid()); 
			portalMainForm.setEditportal_name(portalMainForm.getCodeitemdesc());
			 portalMainForm.setEditcodeitemurl(portalMainForm.getColnum());
	       
		}
  	
  	
  
  	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){ %> 
	 	
	 	var portalflag="${portalMainForm.portalflag}";
	 	var precodeitemid="${portalMainForm.precodeitemid}";
	 	var editportal_id="${portalMainForm.editportal_id}";
	 	if(portalflag=='true'&&precodeitemid!=editportal_id)
	 	{
	 		alert("该门户分类id已存在,请重新输入!");
	 		
	 	}

	<% }
	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){
	 %>
	 	var portalflag="${portalMainForm.portalflag}";
	 	var precodeitemid="${portalMainForm.precodeitemid}";
	 	var editportal_id="${portalMainForm.editportal_id}";
	 	if(portalflag=='true'&&precodeitemid==editportal_id||portalflag=='false')
	{
  		 var portal_base_vo = new Object();
		    portal_base_vo.portal_id = editportal_id;
		    window.returnValue = portal_base_vo;
  	window.close();  	
  	}
<%}
	%>
  	
  	
  	
  	
    function can(){
  window.close();
  }
    function checkComments(s){
    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\]<>/?~！@#￥……&*（）—|{}【】‘；：”“'。，、？%]");
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
			<html:hidden styleId="preportal_id" name="portalMainForm"
				property="precodeitemid" />
			<table width="390" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow">
							<bean:message key="lable.portal.main.editcolumn" />
							&nbsp;
						</td>
					</tr>
				</thead>
				<tr>
					<td align="center" class="RecordRow" nowrap>
						<table border='0'>


							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.name" />
								</td>
								<td>
									<html:text name="portalMainForm" property="editportal_name" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.panel.hide" />
								</td>
								<td>
									<html:select name="portalMainForm" property="hide" style="width:250px;">
			 							<html:optionsCollection property="hidelist" value="dataValue" label="dataName" />
									</html:select> 
								</td>
							</tr>

						</table>
					</td>
				</tr>

			</table>

			<table width="70%" align="center">
				<tr>
					<td align="center" height="35px;">

						<button extra="mybutton" id="clo1" onclick="subSave()"
							allowPushDown="false" down="false">
							<bean:message key="lable.portal.main.save" />
						</button>

						<input type="button" value="<bean:message key="button.cancel"/>"
							class="mybutton" onclick="can();">

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
