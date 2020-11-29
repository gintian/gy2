<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>



<script LANGUAGE=javascript src="/js/xtree.js"></script>
<script LANGUAGE=javascript>
function getchose(){
   	var s=Global.selectedItem.getSelected();
   	window.returnValue= s;
   	window.close();
}
function expchild(currnode){
	currnode.expand();

}

</script>

<html:form action="/system/citemtree/citemtree">
	<table width="100%" border="0" cellspacing="1" align="left" cellpadding="1">
		<tr>
			<td align="left">
			<button name="update" class="mybutton" onclick="getchose();"><bean:message key="button.ok"/></button>&nbsp;
			<button name="back" class="mybutton" onclick="window.close();"><bean:message key="button.close"/></button>
			</td>
			</tr>
		<tr>
			<td align="left">
				<DIV id="treemenu">
					<SCRIPT LANGUAGE=javascript> 
              		 <bean:write name="citemTreeForm" property="treecode" filter="false"/> 
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
			<td align="left">
			<button name="update" class="mybutton" onclick="getchose();"><bean:message key="button.ok"/></button>&nbsp;
			<button name="back" class="mybutton" onclick="window.close();"><bean:message key="button.close"/></button>
			</td>
			</tr>
	</table>
</html:form>
