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
	/*auto;border-style:inset ;
	border-width:2px*/
	}
   </style>
   <hrms:themes></hrms:themes>
<script>
	var obj = window.dialogArguments;
	// 初始化
	Global.checkvalue = obj.orglist;
	Global.checkboxScan = true;
	
	// 确认返回数据
	function getOrg() {
	    var thevo = new Object();
	    thevo.pid = obj.pid;
	    thevo.orglist = root.getSelected();
	    window.returnValue = thevo;
	    window.close();
	}
	
	// 清空
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
<html:form action="/kq/options/sign_point/setsign_point"> 
   <table width="100%" border="0" cellspacing="1"  align="center" style="margin-top: 10px">	         
         <tr>
           <td align="left" id="ftd">
                 <hrms:orgtree loadtype="0" selecttype="1" showroot="false" divStyle="height:330px;width:285px;overflow-x:auto;overflow-y:auto;" />
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
