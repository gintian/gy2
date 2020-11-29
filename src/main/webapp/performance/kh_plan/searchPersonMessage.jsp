<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/performance/kh_plan/defineTargetItems.js"></script>
<style>
.fixedDiv_self
{ 
	overflow:auto; 
	height:300 ; 
	width:450; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.fixedHeaderTr 
{ 
    position:relative; 
    top:expression(this.offsetParent.scrollTop-1); 

}
</style>
<script>


   function getItems(elementName)
   {
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=items[i].value+',';
		}
		if(itemStr!='')
			itemStr=itemStr.substring(0,itemStr.length-1);
		return itemStr;
   }
	function ok(){
           var str = getItems("degrees");
		   var thevo=new Object();
		   thevo.degrees=str;
		   thevo.flag="true";
		   thevo.lockMGradeColumn=document.getElementById("lockMGradeColumn").checked;
        parent.window.returnValue=thevo;
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.top.opener.message_window_ok(thevo);
            window.open("about:blank","_top").close();
        }
	  }
   function closewindow()
   {
       if(window.showModalDialog) {
           parent.window.close();
       }else{
           window.open("about:blank","_top").close();
       }
   }
	  


</script>
<%
	int i=0;
%>
<html:form action="/performance/kh_plan/person_message">
<html:hidden name="examPlanForm" property="basicInfoItem" styleId="basicInfoItem"/>
	<table  border="0" align="center">
		<tr>
			<td>
				<div class="fixedDiv_self common_border_color" >
<table width="90%" border="0" style='BORDER-COLLAPSE: separate' cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
       <tr class="fixedHeaderTr">
         <td style='border-top: 0px;border-right: 0px;' align="center" class="TableRow" nowrap>
		  <input type="checkbox" name="selbox" onclick="batch_select(this, 'degrees');">
		 </td>
         <td style='border-top: 0px;' align="center" class="TableRow" nowrap >
		   <bean:message key="ht.param.empindex"/>
	     </td>                             
       </tr>
   	  </thead>
     <logic:iterate id="element" name="examPlanForm" property="messagelist">
	    <tr>   
	     <td style='border-top: 0px;border-right: 0px;' width="30" align="center" class="RecordRow">
		  <input name="degrees" type="checkbox" 
			value="<bean:write name="element" property="itemid" filter="true" />" 
			 <logic:notEqual name="element" property="select" value="0">checked</logic:notEqual>/>
		 </td>
		 <td style='border-top: 0px;' align="left" class="RecordRow" nowrap>&nbsp;&nbsp;		
			<bean:write name="element" property="itemdesc" filter="true" />
		 </td>
       </tr>
    </logic:iterate>
</table>
		</div>
	</td>
				</tr>
	</table>
<table  width="100%">
          <tr>
            <td  align="left">
            	<logic:notEqual name="examPlanForm" property="lockMGradeColumn" value="false"><input type="checkbox" id="lockMGradeColumn" checked /></logic:notEqual>
            	<logic:equal name="examPlanForm" property="lockMGradeColumn" value="false"><input type="checkbox" id="lockMGradeColumn" /></logic:equal>
           		 多人考评锁定指标列
            </td>
          </tr>          
</table>
<table  width="100%">
          <tr>
            <td  align="center">
         	<input type='button' class="mybutton" property="b_add"  onclick='ok()' value='<bean:message key="button.ok"/>'  />
            <input type='button' class="mybutton" property="b_delete"  onclick='closewindow()' value='<bean:message key="button.cancel"/>'  />
            </td>
          </tr>          
</table>
</html:form>