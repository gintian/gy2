<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>


<%// 在标题栏显示当前用户和日期 2004-5-10 
			String userName = null;
			String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			}
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script>
<hrms:themes></hrms:themes>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
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
<script language="javascript" src="/js/operationtree.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
  function delete_ok(outparamters)
  {
  	
     var currnode=Global.selectedItem;
     var preitem=currnode.getPreviousSibling();
     currnode.remove();
     
  }
  function delete_object()
  {
  if(ifdel())
  {
	 var currnode=Global.selectedItem;
	 if(currnode==null){
	 	return;
	 }
	 var uid=currnode.uid;
	 if(uid=="root"){
	  return;
	  }
	 var uids=uid.split("/");
     var hashvo=new ParameterSet(); 
     hashvo.setValue("operationid",uids[0]);  
     hashvo.setValue("operationcode",uids[1]);      
   　 var request=new Request({asynchronous:false,onSuccess:delete_ok,functionId:'1010070004'},hashvo);        
  }
  }
</script>
<html:form action="/general/operation/operationtree">
<!-- 暂时取消维护功能
<hrms:menubar menu="menu1" id="menubar1" container="tableContainer">
      <hrms:menuitem name="mitem3" label="button.insert" icon="" url="addObject();" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem4" label="button.edit" icon="" url="updateObject();" command="" enabled="true" visible="true"/>
      <hrms:menuitem name="mitem2" label="button.delete" icon="" url="delete_object();" command="" enabled="true" visible="true"/>
</hrms:menubar>
 -->
	<table id="tableContainer" width="100%" border="0" cellspacing="1" align="left" cellpadding="1">
		<tr>
			<td align="left">
				<DIV id="treemenu">
				<SCRIPT LANGUAGE=javascript> 
						
              		 <bean:write name="operationForm" property="treeStr" filter="false"/>    	
              		 var currnode=Global.selectedItem;
              		 root.openURL();	 	
                </SCRIPT>
				</DIV>
			</td>
	</table>
	
</html:form>
<script language="javascript">
  initDocument();
</script>
