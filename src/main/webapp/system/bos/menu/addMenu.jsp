
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm"%>
<html>

	<hrms:themes></hrms:themes>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script language="javascript">
   function sub()
  {
		
	var menu_id = getEncodeStr(trim(document.getElementsByName("addmenu_id")[0].value));
	  	menu_id = $URL.encode(menu_id);
	var menu_name =getEncodeStr(trim(document.getElementsByName("addmenu_name")[0].value));
	  	menu_name = $URL.encode(menu_name);
	var menu_func_id=getEncodeStr(trim(document.getElementsByName("addcodeitemfunc_id")[0].value));
	  	menu_func_id = $URL.encode(menu_func_id);
	var menu_icon = getEncodeStr(trim(document.getElementsByName("addcodeitemicon")[0].value));
	  	menu_icon = $URL.encode(menu_icon);
	var menu_url=getEncodeStr(trim(document.getElementsByName("addcodeitemurl")[0].value));
	  	menu_url = $URL.encode(menu_url);
	var menu_target = getEncodeStr(trim(document.getElementsByName("addcodeitemtarget")[0].value));
	  	menu_target = $URL.encode(menu_target);
  	var parentid = getEncodeStr(trim(document.getElementsByName("parentid")[0].value));
	  	parentid = $URL.encode(parentid);
  	var menuhide = getEncodeStr(trim(document.getElementsByName("addmenuhide")[0].value));
	  	menuhide = $URL.encode(menuhide);
  	var validate=getEncodeStr(trim(document.getElementsByName("validate")[0].value));
	  	validate = $URL.encode(validate);
		if(menu_id.length==0)
		{
			alert("编号不能为空");
			return;
		}
	 	if(menu_name.length==0)
		{
			alert("名称不能为空");
			return;
		}
//		if(menu_func_id.length==0)
//		{
//			alert("授权功能号不能为空");
//			return;
//		}
//		if(menu_url.length==0)
//		{
//			alert("链接地址不能为空");
//			return;
//		}
//	 	if(menu_target.length==0)
//		{
//			alert("target不能为空");
//			return;
//		}

	 	document.menuMainForm.action="/system/bos/menu/menuMain.do?b_findadd=add&menu_name="+menu_name+"&menu_id="+menu_id+"&parentid="+parentid+"&menu_func_id="+menu_func_id+"&menu_icon="+menu_icon+"&menu_url="+menu_url+"&menu_target="+menu_target+"&menuhide="+menuhide+"&validate="+validate;
		document.menuMainForm.submit();
  	}
  	<%
	
	 if(request.getParameter("b_findadd")==null)
 	 	{
		 	MenuMainForm menuMainForm=(MenuMainForm)session.getAttribute("menuMainForm"); 
			menuMainForm.setAddmenu_id(""); 
			menuMainForm.setAddmenu_name("");
			 menuMainForm.setAddcodeitemurl("");
	        menuMainForm.setAddcodeitemicon("");
	        menuMainForm.setAddcodeitemfunc_id("");
	        menuMainForm.setAddcodeitemtarget("");
		}
	 if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){ %> 
	 	
	 	var menuflag="${menuMainForm.menuflag}";
	 	if(menuflag=='true')
	 	{
	 		alert("该菜单编号已存在,请重新输入!");
	 	}
	<% }
	if(request.getParameter("b_findadd")!=null&&request.getParameter("b_findadd").equals("add")){
	 %>
	 	var menuflag="${menuMainForm.menuflag}";
	 	if(menuflag=='false')
	 	{
	var menu_name ="${menuMainForm.addmenu_name}";
	var parentid ="${menuMainForm.parentid}";
	var menu_id="${menuMainForm.addmenu_id}";
  		// document.menuMainForm.action="/system/bos/func/menuMain.do?b_SaveFunc=link&menu_name="+menu_name+"&menu_id="+menu_id+"&parentid="+parentid;
		//document.menuMainForm.submit();
  		 var menu_base_vo = new Object();
		    menu_base_vo.menu_name = menu_name;
		    menu_base_vo.menu_id = menu_id;
		   	menu_base_vo.parentid=parentid;
		    window.returnValue = menu_base_vo;
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
		<html:form action="/system/bos/menu/menuMain">
			<html:hidden styleId="parentid" name="menuMainForm"
				property="parentid" />
			<table width="390" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow">
							<bean:message key="lable.menu.main.addmenu" />
							&nbsp;
						</td>
					</tr>
				</thead>
				<tr>
					<td align="center" class="RecordRow" nowrap>
						<table border='0'>

							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.id" />
								</td>
								<td>
									<html:text name="menuMainForm" property="addmenu_id" styleClass="textColorWrite" style="width:230px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.name" />
								</td>
								<td>
									<html:text name="menuMainForm" property="addmenu_name"  styleClass="textColorWrite" style="width:230px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.func_id" />
								</td>
								<td>
									<html:text name="menuMainForm" property="addcodeitemfunc_id" styleClass="textColorWrite" style="width:230px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.icon" />
								</td>
								<td>
									<html:text name="menuMainForm" property="addcodeitemicon" styleClass="textColorWrite" style="width:230px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.url" />
								</td>
								<td>
									<html:text name="menuMainForm" property="addcodeitemurl" styleClass="textColorWrite" style="width:230px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.target2" />
								</td>
								<td>
									<%
										String menuid = request.getParameter("menuid");
										menuid=menuid==null?"":menuid;
									if(menuid.startsWith("21")){ %>
										<html:text name="menuMainForm" property="addcodeitemtarget"
											value="i_body" styleClass="textColorWrite" style="width:230px;"/>
									<%}else{ %>
										<html:text name="menuMainForm" property="addcodeitemtarget"
											value="il_body" styleClass="textColorWrite" style="width:230px;"/>
									<%} %>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.panel.hide" />
								</td>
								<td>
										<html:select name="menuMainForm" property="addmenuhide" style="width:230px;">
			 							<html:optionsCollection property="menuhidelist" value="dataValue" label="dataName" />
									</html:select> 
								</td>
							</tr>
                            <!-- changxy 20160621 添加二次验证功能 -->
                            <tr>
                                <td align="right" height="30">
                                    <bean:message key="lable.menu.main.addvalidate" />
                               
                                </td>
                                <td>
                                    <html:select name="menuMainForm" property="validate" style="width:230px;">
                                    <html:optionsCollection property="validateList" value="dataValue" label="dataName" />
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
							value="<bean:message key="lable.menu.main.return"/>"
							class="mybutton" onClick="sub()">


						<input type="button" value="<bean:message key="button.cancel"/>"
							class="mybutton" onclick="can();">

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
