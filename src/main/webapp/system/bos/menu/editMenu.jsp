
<%@ page language="java" contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.menu.MenuMainForm"%>
<html>

	<hrms:themes></hrms:themes>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script LANGUAGE=javascript src="/system/bos/menu/menument.js"></script>
	<script language="javascript">
     function subSave()
  {
  	var menu_id = $URL.encode(getEncodeStr(trim(document.getElementsByName("editmenu_id")[0].value)));
	var menu_name =$URL.encode(getEncodeStr(trim(document.getElementsByName("editmenu_name")[0].value)));
	var menu_func_id=$URL.encode(getEncodeStr(trim(document.getElementsByName("editcodeitemfunc_id")[0].value)));
	var menu_icon =$URL.encode( getEncodeStr(trim(document.getElementsByName("editcodeitemicon")[0].value)));
	var menu_url=$URL.encode(getEncodeStr(trim(document.getElementsByName("editcodeitemurl")[0].value)));
	var menu_target = $URL.encode(getEncodeStr(trim(document.getElementsByName("editcodeitemtarget")[0].value)));
  	var premenu_id=$URL.encode(getEncodeStr(trim(document.getElementsByName("premenu_id")[0].value)));
  	var menuhide =$URL.encode(getEncodeStr(trim(document.getElementsByName("editmenuhide")[0].value)));
    var validate=$URL.encode(getEncodeStr(trim(document.getElementsByName("validate")[0].value))); //添加
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
	 		document.menuMainForm.action="/system/bos/menu/menuMain.do?b_findedit=edit&menu_name="+menu_name+"&menu_id="+menu_id+"&premenu_id="+premenu_id+"&menu_func_id="+menu_func_id+"&menu_icon="+menu_icon+"&menu_url="+menu_url+"&menu_target="+menu_target+"&menuhide="+menuhide+"&validate="+validate;

			document.menuMainForm.submit();
  
  	}
  	
  		<%  
  	 if(request.getParameter("b_findedit")==null)
 	 	{
		 		MenuMainForm menuMainForm=(MenuMainForm)session.getAttribute("menuMainForm"); 
		 	
			menuMainForm.setEditmenu_id(menuMainForm.getCodeitemid()); 
			menuMainForm.setEditmenu_name(menuMainForm.getCodeitemdesc());
			 menuMainForm.setEditcodeitemurl(menuMainForm.getCodeitemurl());
	        menuMainForm.setEditcodeitemicon(menuMainForm.getCodeitemicon());
	        menuMainForm.setEditcodeitemfunc_id(menuMainForm.getCodeitemfunc_id());
	        menuMainForm.setEditcodeitemtarget(menuMainForm.getCodeitemtarget());
		}
  	
  	
  
  	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){ %> 
	 	
	 	var menuflag="${menuMainForm.menuflag}";
	 	var precodeitemid="${menuMainForm.precodeitemid}";
	 	var editmenu_id="${menuMainForm.editmenu_id}";
	 	if(menuflag=='true'&&precodeitemid!=editmenu_id)
	 	{
	 		alert("该菜单号id已存在,请重新输入!");
	 		
	 	}

	<% }
	if(request.getParameter("b_findedit")!=null&&request.getParameter("b_findedit").equals("edit")){
	 %>
	 	var menuflag="${menuMainForm.menuflag}";
	 	var precodeitemid="${menuMainForm.precodeitemid}";
	 	var editmenu_id="${menuMainForm.editmenu_id}";
	 	if(menuflag=='true'&&precodeitemid==editmenu_id||menuflag=='false')
	{
  		 var menu_base_vo = new Object();
		    menu_base_vo.menu_id = editmenu_id;
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
			<html:hidden styleId="premenu_id" name="menuMainForm"
				property="precodeitemid" />
			<table width="390" border="0" cellspacing="0" align="center"
				cellpadding="0" class="ListTable">
				<thead>
					<tr>
						<td align="left" class="TableRow">
							<bean:message key="lable.menu.main.editmenu" />
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
									<html:text name="menuMainForm" property="editmenu_id" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>

							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.name" />
								</td>
								<td>
									<html:text name="menuMainForm" property="editmenu_name" styleClass="textColorWrite" style="width:250px;" onkeyup="value=checkComments(this.value)"  onkeypress = "checkForm()"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.func_id" />
								</td>
								<td>
									<html:text name="menuMainForm" property="editcodeitemfunc_id" styleClass="textColorWrite" style="width:250px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.icon" />
								</td>
								<td>
									<html:text name="menuMainForm" property="editcodeitemicon" styleClass="textColorWrite" style="width:250px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.url" />
								</td>
								<td>
									<html:text name="menuMainForm" property="editcodeitemurl" styleClass="textColorWrite" style="width:250px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.menu.main.target2" />
								</td>
								<td>
									<html:text name="menuMainForm" property="editcodeitemtarget" styleClass="textColorWrite" style="width:250px;"/>
								</td>
							</tr>
							<tr>
								<td align="right" height="30">
									<bean:message key="lable.portal.panel.hide" />
								</td>
								<td>
										<html:select name="menuMainForm" property="editmenuhide" style="width:250px;">
			 							<html:optionsCollection property="menuhidelist" value="dataValue" label="dataName" />
									</html:select> 
								</td>
							</tr>
							<!-- changxy 二次验证 20160621 -->
                            <tr>
                                <td align="right" height="30">
                                    <bean:message key="lable.menu.main.editvalidate" />
                                  
                                </td>
                                <td>
                                <html:select name="menuMainForm" property="validate"  style="width:250px;" >
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

						<button extra="mybutton" id="clo1" onclick="subSave()"
							allowPushDown="false" down="false">
							<bean:message key="lable.menu.main.save" />
						</button>

						<input type="button" value="<bean:message key="button.cancel"/>"
							class="mybutton" onclick="can();">

					</td>
				</tr>
			</table>


		</html:form>
	</body>
</html>
