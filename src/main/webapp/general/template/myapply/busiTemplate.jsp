<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function test()
	{
	  var setname="detail.templet_"+${templateForm.tabid};
	  alert(setname);
	  var dataset=eval(setname);
	  alert(dataset.getFieldCount());
	  alert(dataset.getCurrent());
	}
   function pageset_beforeTabChange(TabSettabSet, stringoldName, stringnewName)
   {
	  /* if(stringoldName=="")
	      return ;
	   if(confirm('是否切换？'))
	   {
	       return "y";  //有返回值时，则不切换
	   } */
	   if(stringoldName=="")
	      return ;
	   var saveBt = window.frames('detail').document.getElementById('buttonsave'); 
	   if(saveBt!=null)
	   		saveBt.fireEvent("onClick");
   } 
//-->
</script>
<hrms:themes />
<html:form action="/general/template/myapply/busiTemplate">
<hrms:tabset name="pageset" width="100%" height="100%" type="true"> 
    <logic:iterate id="pagebo"  name="templateForm"  property="pagelist" indexId="index">
	  <hrms:tab name="${pagebo.pageid}" label="${pagebo.title}" visible="true" url="/general/template/myapply/busiPage.do?b_query=link&pageno=${pagebo.pageid}&tabid=${pagebo.tabid}&sp_batch=${templateForm.sp_batch}">
      </hrms:tab>	
   </logic:iterate> 

</hrms:tabset>

</html:form>
