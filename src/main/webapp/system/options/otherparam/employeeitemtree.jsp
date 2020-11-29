<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>



<script LANGUAGE=javascript src="/js/xtree.js"></script>
<hrms:themes></hrms:themes>
<script LANGUAGE=javascript>
function getchose(){
   	var s=Global.selectedItem.getSelected();
   	if(parent.Ext){ //ext 弹窗回调   wangb 20190319
		var win = parent.Ext.getCmp('select_field');
		win.return_vo = s;
		win.close();
	}else{
	   	window.returnValue= s;   	
   		window.close();
	}
}
function expchild(currnode){
	currnode.expand();

}
//关闭弹窗方法   wangb 20190319
function winclose(){
	if(parent.Ext){
		parent.Ext.getCmp('select_field').close();
		return;
	}
	window.close();		
}
</script>

<html:form action="/system/options/otherparam/employeeitemtree">
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0">

		<tr>
			<td align="left">
				<DIV id="treemenu" style="height: 330px;width:290px;overflow: auto;border-style:solid ;border-width:1px">
					<SCRIPT LANGUAGE=javascript> 
					 Global.cascade=true;
              		 <bean:write name="sysOthParamForm" property="treecode" filter="false"/>
              		        
              		    	var sel=Global.selectedItem.getSelected();	
              		    	var selstr=sel.split(",");
              		    	var currnode=Global.selectedItem;
              		    	for(var i=currnode.childNodes.length-1;i>=0;i--){
              		    		var cuid=currnode.childNodes[i].uid;
              		    		for(var j=selstr.length-1;j>=0;j--){
              		    		var selstrs=selstr[j];
              		    		if(cuid==selstrs){
              		    		currnode.childNodes[i].expand();
              		    		}
              		    		}
              		    	}
              		    	
              		    	 	
             	</SCRIPT>
				</DIV>
			</td>
			</tr>
			<tr>
			<td align="center" height="35px;">
			<button name="update" class="mybutton" onclick="getchose();"><bean:message key="button.ok"/></button>&nbsp;
			<button name="back" class="mybutton" onclick="winclose();"><bean:message key="button.close"/></button>
			</td>
			</tr>
	</table>
</html:form>
<script>
	if(getBrowseVersion()==10 || !getBrowseVersion()){//非ie兼容模式下 样式修改 wangb 20190323
		var treemenu = document.getElementById('treemenu');
		treemenu.style.whiteSpace = 'nowrap';
	}
</script>