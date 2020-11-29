<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedDiv1{
	overflow:hidden; 
	height:30px;
	width:640px;
	line-height:30px; 
	BORDER: #94B6E6 1pt solid; 
	background-color:#f4f7f7;
	font-weight:bold;
}
.fixedDiv2 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:640px;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: none; 
}
</style>
<hrms:themes></hrms:themes>
<script language="javascript">
function abolish(){
	var temp=selectcheckeditem();
	if(temp.length>0){
		if(confirm("确认要撤销吗？")){
		   var hashvo=new ParameterSet();          
	       hashvo.setValue("selected", temp);
	       var request=new Request({method:'post',onSuccess:afterabolish,functionId:'1010010066'},hashvo);
		}
	}
}
function afterabolish(outparamters){
	var msg=outparamters.getValue("msg");
	if("ok"==msg){
		var roleid=outparamters.getValue("roleid");
		roledetailForm.action="/system/security/roledetail.do?b_detailed=link&roleid="+roleid;
		roledetailForm.submit();
	}
}
 function selectcheckeditem()
   {
    var a=0;
	var selectid=new Array();	

	    for(var i=0;i<document.roledetailForm.elements.length;i++)
	    {			
		   if(document.roledetailForm.elements[i].type=='checkbox'&&document.roledetailForm.elements[i].name!="selbox")
		   {	
		     
		       if(document.roledetailForm.elements[i].checked==true)
		       {
			      selectid[a++]=document.roledetailForm.elements[i].value;						
		       }
		  }
	   }
	
	if(selectid.length==0)
	{
		alert(REPORT_INFO9+"!");
		return '';
	}
	return selectid;	
 } 
 function excExcel(){
 	    var hashvo=new ParameterSet();
		hashvo.setValue("roleid",'${roledetailForm.roleid }');
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnExportOk1,functionId:'1010010077'},hashvo);
 }
 function returnExportOk1(outparameters){
		var outName=outparameters.getValue("outName");
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
}
</script>
<html:form action="/system/security/roledetail">
<!-- 
<table width="640" class="TableRow" border="0" cellpadding="0" cellspacing="0">
<tr height="20">
    <!-- td width=10 valign="top" class="tableft"></td>
    <td width=200 align=center class="tabcenter" id="topic">&nbsp;${roledetailForm.rolename }&nbsp;</td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="490"></td>  
    <td align="left" >&nbsp;${roledetailForm.rolename }&nbsp;</td>             	      
</tr> 
</table>
 -->
<div class="fixedDiv1">
&nbsp;${roledetailForm.rolename }&nbsp;
</div>
<div class="fixedDiv2" style="border-top:none;width:640px;"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top: -1">
   	  <thead>
           <tr>
        <td align="center" class="TableRow" nowrap style="border-left:none;">
		<input type="checkbox" name="selbox" onclick="batch_select(this,'detailListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.role.detail.type"/>&nbsp;
	    </td>	    
        <td align="center" class="TableRow" nowrap style="border-right:0;">
		<bean:message key="label.role.detail.name"/>&nbsp;
	    </td>
           </tr>
   	  </thead>
   	  <hrms:extenditerate id="element" name="roledetailForm" property="detailListForm.list" indexes="indexes"  pagination="detailListForm.pagination" pageCount="${roledetailForm.pagerows}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap style="border-left:none;">
             <bean:define id="staff_id" name='element' property='staff_id'/> 
             <bean:define id="role_id" name='element' property='role_id'/>    	
    		 <hrms:checkmultibox name="roledetailForm" property="detailListForm.select" value="${role_id}`${staff_id}" indexes="indexes"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" 	style="word-break:break-all;"  nowrap>
                   &nbsp;<bean:write name="element" property="detailtype" filter="true"/>       	   	             	            	              	              	            	               	             	             	             	             	             	             	               
	    </td>
        <td align="left" class="RecordRow" 	style="word-break:break-all;border-right:0;"  nowrap>
                    &nbsp;<bean:write  name="element" property="detailname" filter="true"/>
	    </td>
          </tr>
      </hrms:extenditerate>
        
</table>
</div>
<table width="100%" cellpadding="0" cellspacing="0" border="0"><tr><td>
<div class="fixedDiv1" style="border-top:0px;background-color:white;">
<table  width="100%" align="left" >
		<tr>
		    <td valign="bottom" >
		            <hrms:paginationtag name="roledetailForm" pagerows="${roledetailForm.pagerows}" property="detailListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	        <td  align="right" nowrap >
				 <p align="right"><hrms:paginationlink name="roledetailForm" property="detailListForm.pagination" nameId="detailListForm">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
</td></tr>
<tr><td>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
        <td align="center"  nowrap height="35px;">
        	 <html:button property="b_abolish" styleClass="mybutton" onclick="abolish();">&nbsp;<bean:message key='button.abolish'/>&nbsp;</html:button>&nbsp;&nbsp;
        	 <html:button property="b_abolish" styleClass="mybutton" onclick="excExcel();">&nbsp;<bean:message key='report.actuarial_report.exportExcel'/>&nbsp;</html:button>&nbsp;&nbsp;
            <html:button property="b_close" styleClass="mybutton" onclick="top.window.close();">&nbsp;<bean:message key='button.close'/>&nbsp;</html:button>
        </td>
   </tr> 
</table>
</td></tr></table>
</html:form>
