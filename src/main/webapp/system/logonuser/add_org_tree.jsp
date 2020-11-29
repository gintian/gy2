<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<script LANGUAGE=javascript src="/js/xtree.js"></script>

<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<style type="text/css">
	#treemenu {  
		height: 300px;overflow: 
	}
</style>
<hrms:themes></hrms:themes>
<script>
	var obj = undefined;
	/*当窗口时window.open打开时，没有dialogArguments，调用父界面获取参数 guodd 2019-03-20*/
	if(window.showModalDialog){
		obj = window.dialogArguments;
	}else{
		obj = window.opener.paramObj;
	}
	Global.checkvalue = obj.orgIdList;
	Global.checkValueTitle = obj.orgTitleList;
	Global.checkboxScan = true;
	function getOrg() {
		var thevo = new Object();
		// 选中的机构编码
		thevo.orgIdList = root.getSelected();
		// 选中的机构名称
		thevo.orgTitleList = root.getSelectedTitle();
		
		/*兼容模式窗体和window.open方式回传值  guodd 2019-03-20*/
		if(window.showModalDialog){
			window.returnValue = thevo;
		}else{
			window.opener.selectOrgSuccess(thevo);
		}
		window.close();
	}

	function bClear() {
		if (window.confirm('<bean:message key="button.affirm"/><bean:message key="system.sms.alle"/>?')) {
			Global.checkvalue = ",";
			var root = Global.selectedItem;
			while (root.uid != "root") {
				root = root.root();
			}
			root.allClear();
		} else {
			return false;
		}
	}
</script>
<body style="overflow:hidden;">
<html:form action="/system/logonuser/add_edit_user"> 
   <table width="98%" border="0" cellspacing="1"  align="center" >
	   <tr>
	   		<td align="left" id="ftd">
				<hrms:orgtree loadtype="1" selecttype="1" showroot="false" divStyle="height:330px;width:100%;overflow-x:auto;overflow-y:auto;" />
			</td>
		</tr>   
	    <tr>
	        <td align="center" colspan="2" height="35px">
	        	<input type="button" class="mybutton" value="<bean:message key="button.ok"/>" onclick="getOrg();"/>
	        	<input type="button" class="mybutton" value="<bean:message key="system.sms.alle"/>" onclick="bClear();"/>
	        	<html:button styleClass="mybutton" property="br_return" onclick="window.close();">
	        		<bean:message key="button.close"/>
	        	</html:button>            
	        </td>         
	    </tr>        
   </table>
</html:form>
</body>