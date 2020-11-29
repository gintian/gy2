
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
%>
<html>

	<hrms:themes></hrms:themes>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script LANGUAGE=javascript src="/system/bos/Portal/portalment.js"></script>
	<script language="javascript">
     function subSave()
  {
  	var portal_id = getEncodeStr(trim(document.getElementsByName("editportal_id")[0].value));
	var portal_name =getEncodeStr(trim(document.getElementsByName("editportal_name")[0].value));
	<%if("hcm".equals(userView.getBosflag())){%>
	var portal_icon = getEncodeStr(trim(document.getElementsByName("editcodeitemicon")[0].value));
	<%}else{%>
	var portal_icon = "";
	<%}%>
	var portal_url=getEncodeStr(trim(document.getElementsByName("editcodeitemurl")[0].value));
	//zhangh 2020-1-19 【56925】V77工具箱/平台接口/门户定制：非兼容IE浏览器编辑门户并保存，出现一个网页对话框
  	var preportal_id=getEncodeStr(trim(document.getElementsByName("precodeitemid")[0].value));
  	var opt = '${portalMainForm.opt}';
  	var hide = getEncodeStr(trim(document.getElementsByName("hide")[0].value));
  	var height = trim(document.getElementsByName("height")[0].value);
  	var heightarr = height.split("");
  	var priv = getEncodeStr(trim(document.getElementsByName("priv")[0].value));
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
		if(portal_url.length==0)
		{
			alert("链接地址不能为空");
			return;
		}
		if(height.length==0)
		{
			alert("高度不能为空");
			return;
		}
		for(var i =0;i<height.length;i++ )
		{
			if((heightarr[i].charCodeAt()<48)||(heightarr[i].charCodeAt()>57)){
				alert("高度只能为整数值");
				return;
			}
		}
		
 			document.portalMainForm.action="/system/bos/portal/portalMain.do?b_findedit=edit&portal_name="+$URL.encode(portal_name)+"&portal_id="+$URL.encode(portal_id)+"&preportal_id="+$URL.encode(preportal_id)+"&portal_icon="+$URL.encode(portal_icon)+"&portal_url="+$URL.encode(portal_url)+"&height="+height+"&hide="+$URL.encode(hide)+"&priv="+$URL.encode(priv)+"&opt="+$URL.encode(opt);

			document.portalMainForm.submit();
  
  	}
  	
  		<%  
  	 if(request.getParameter("b_findedit")==null)
 	 	{
		 		PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
		 	
			portalMainForm.setEditportal_id(portalMainForm.getCodeitemid()); 
			portalMainForm.setEditportal_name(portalMainForm.getCodeitemdesc());
			 portalMainForm.setEditcodeitemurl(portalMainForm.getCodeitemurl());
	        portalMainForm.setEditcodeitemicon(portalMainForm.getCodeitemicon());
	        portalMainForm.setEditcodeitemfunc_id(portalMainForm.getCodeitemfunc_id());
	        portalMainForm.setEditcodeitemtarget(portalMainForm.getCodeitemtarget());
		}
  	
  	
  
  	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){ %> 
	 	
	 	var portalflag="${portalMainForm.portalflag}";
	 	var precodeitemid="${portalMainForm.precodeitemid}";
	 	var editportal_id="${portalMainForm.editportal_id}";
	 	if(portalflag=='true'&&precodeitemid!=editportal_id)
	 	{
	 		alert("该菜单号id已存在,请重新输入!");
	 		
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
			<html:hidden styleId="preportal_id" name="portalMainForm"
				property="precodeitemid" />
			<table width="390" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow">
							<bean:message key="lable.portal.main.editpanel" />
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
									<html:text name="portalMainForm" property="editportal_id" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>

							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.name" />
								</td>
								<td>
									<html:text name="portalMainForm" property="editportal_name" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<%if("hcm".equals(userView.getBosflag())){%>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.icon" />
								</td>
								<td>
									<html:text name="portalMainForm" property="editcodeitemicon" styleClass="textColorWrite" style="width:250px;"/>
								</td>
							</tr>
							<%} else {%>
							<!-- tr>
                                <td align="right" height="30">
                                    <bean:message key="lable.portal.main.icon" />
                                </td>
                                <td>
                                    <html:text name="portalMainForm" property="editcodeitemicon" styleClass="textColorWrite" />
                                </td>
                            </tr> -->
							<%} %>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.url" />
								</td>
								<td>
									<html:text name="portalMainForm" property="editcodeitemurl" styleClass="textColorWrite" style="width:250px;"/>
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
								<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.panel.height" />
								</td>
								<td>
									<html:text name="portalMainForm" property="height" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.panel.priv" />
								</td>
								<td>
									<html:select name="portalMainForm" property="priv" style="width:250px;">
			 							<html:optionsCollection property="privlist" value="dataValue" label="dataName" />
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
