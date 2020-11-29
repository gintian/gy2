
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
	<script language="javascript">
   function sub()
  {
		
	var portal_id = getEncodeStr(trim(document.getElementsByName("addportal_id")[0].value));
	var portal_name =getEncodeStr(trim(document.getElementsByName("addportal_name")[0].value));
	<%if("hcm".equals(userView.getBosflag())){%>
	var portal_icon = getEncodeStr(trim(document.getElementsByName("addcodeitemicon")[0].value));
    <%}else{%>
    var portal_icon = "";
    <%}%>
    var portal_url=getEncodeStr(trim(document.getElementsByName("addcodeitemurl")[0].value));
  	var parentid = getEncodeStr(trim(document.getElementsByName("parentid")[0].value));
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
		
	 	document.portalMainForm.action="/system/bos/portal/portalMain.do?b_findadd=add&portal_name="+portal_name+"&portal_id="+portal_id+"&parentid="+parentid+"&height="+height+"&portal_icon="+portal_icon+"&portal_url="+portal_url+"&hide="+hide+"&priv="+priv+"&opt="+opt;
		document.portalMainForm.submit();
  	}
  	
  	
  	<%
	
	 if(request.getParameter("b_findadd")==null)
 	 	{
		 	PortalMainForm portalMainForm=(PortalMainForm)session.getAttribute("portalMainForm"); 
			portalMainForm.setAddportal_id(""); 
			portalMainForm.setAddportal_name("");
			 portalMainForm.setAddcodeitemurl("");
	        portalMainForm.setAddcodeitemicon("");
	        portalMainForm.setAddcodeitemfunc_id("");
	        portalMainForm.setAddcodeitemtarget("");
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
	var portal_id="${portalMainForm.addportal_id}";
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
							<bean:message key="lable.portal.main.addpanel" />
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
							<%if("hcm".equals(userView.getBosflag())){%>
                            <tr>
                                <td align="right" height="30">
                                    <bean:message key="lable.portal.main.icon" />
                                </td>
                                <td>
                                    <html:text name="portalMainForm" property="addcodeitemicon" styleClass="textColorWrite" style="width:250px;"/>
                                </td>
                            </tr>
                            <%} else {%>
							<!-- tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.main.icon" />
								</td>
								<td>
									<html:text name="portalMainForm" property="addcodeitemicon" />
								</td>
							</tr -->
						      <%} %>
						      <tr>
                                <td align="right" height="30">
                                    <bean:message key="lable.portal.main.url" />
                                </td>
                                <td>
                                    <html:text name="portalMainForm" property="addcodeitemurl" styleClass="textColorWrite" style="width:250px;"/>
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
