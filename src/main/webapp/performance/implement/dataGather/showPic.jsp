<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript">
	if('${dataGatherForm.picSrc}'==''){
		alert('没有图像！');
		// window.close();
		winClose();
	}
	function winClose() {
		if(parent.parent.Ext.getCmp('showPic')){
			parent.parent.Ext.getCmp('showPic').close();
		}
	}
</script>

<html:form action="/performance/implement/dataGather">
	<TABLE cellSpacing=0 cellPadding=0 width="100%" border=0>
 	 <TBODY>
 		 <TR>
    		<TD background="">
    			<logic:notEqual name="dataGatherForm"  property="picSrc" value="">
                	<img  id ="pic" src="${dataGatherForm.picSrc}"  style='width:"${dataGatherForm.picWidth}";height:"${dataGatherForm.picHeight}"' />
                </logic:notEqual>
      </TD></TR></TBODY></TABLE>     

</html:form>