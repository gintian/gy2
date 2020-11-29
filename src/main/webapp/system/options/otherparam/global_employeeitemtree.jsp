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
    var currnode,codeitemid,codetext;
    currnode=Global.selectedItem;
    if(currnode==null||currnode.uid=='root')
    	 return;       
    codeitemid=currnode.uid;     
    codetext=currnode.text;   
    var thevo=new Object();
    thevo.codeitemid=codeitemid;
    thevo.codetext=codetext;
    // window.returnValue=thevo;
    parent.return_vo = thevo;
    if(parent.Ext.getCmp('global_employeeitemtree')){
        parent.Ext.getCmp('global_employeeitemtree').close();
	}
    // window.close();
}
function expchild(currnode){
	currnode.expand();

}
function winClose() {
    parent.return_vo = '';
    if(parent.Ext.getCmp('global_employeeitemtree')){
        parent.Ext.getCmp('global_employeeitemtree').close();
    }
}
</script>

<html:form action="/system/options/otherparam/global_employeeitemtree">
	<table width="290" border="0" cellspacing="0" align="left" cellpadding="0">

		<tr>
			<td align="left">
				<DIV id="treemenu" style="height: 330px;overflow: auto;border-style:solid ;border-width:1px">
					<SCRIPT LANGUAGE=javascript> 
					 Global.defaultradiolevel=2; 
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
			<button name="back" class="mybutton" onclick="winClose();"><bean:message key="button.close"/></button>
			</td>
			</tr>
	</table>
</html:form>
