<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>

<%
	  int i=0;
%>
<script type="text/javascript">
function to_delete()
{
	jingpinForm.action="/hire/jp_contest/apply/apply_jp_pos.do?b_delete=link";
	jingpinForm.submit(); 	
}
function to_apply()
{
	jingpinForm.action="/hire/jp_contest/apply/apply_pos.do?b_query=link";
	jingpinForm.submit(); 
}
function to_return()
{	
	jingpinForm.action="/hire/jp_contest/apply/apply_jp_pos.do?b_search=link";
	jingpinForm.submit(); 
}
function to_upload()
{
	var thecodeurl = "/hire/jp_contest/apply/saveuploadfile.do?br_upload=link";
		var values= window.showModalDialog(thecodeurl,null, 
		        "dialogWidth:400px; dialogHeight:210px;resizable:no;center:yes;scroll:yes;status:no");	
	if(values=="refresh")
	{
		var z0700 = ${jingpinForm.z0700};
		document.location.href="/hire/jp_contest/apply/apply_jp_pos.do?b_query=link&z0700="+z0700;
	}
}
function upload()
{
	 var fileEx = jingpinForm.uploadfile.value;
	 if(fileEx == "")
	 {
    	alert(SELECT_FIELD+"!");
    	return ;
     }
     var fso=new ActiveXObject("Scripting.FileSystemObject");   
	 if(!fso.FileExists(fileEx))
	 {
	     alert(FIELD_NOT_EXIST);
	     return;
	 }    
    document.getElementById("filepath").value=fileEx;
	jingpinForm.action="/hire/jp_contest/apply/saveuploadfile.do?b_query=link&z0700=${jingpinForm.z0700}";
	jingpinForm.submit();
	
}
function to_card()
{
	var theurl="/hire/jp_contest/personinfo/jpcard.do?b_search=link&userbase=${jingpinForm.nbase}&a0100=${jingpinForm.a0100}&inforkind=1&userpriv=${jingpinForm.userpriv}";
	window.open(theurl,"_blank","");
}
</script>
<hrms:themes></hrms:themes>
<form name="jingpinForm" method="post" action="/hire/jp_contest/apply/apply_jp_pos.do" enctype="multipart/form-data" >
<html:hidden name="jingpinForm" property="z0700"/>
<html:hidden name="jingpinForm" property="filepath"/>
<br>
<fieldset align="center" style="width:460;">
	<legend><bean:message key='tab.label.myapply'/>
		<logic:equal name="jingpinForm" property="applystate" value="07">
			<font color="red"><bean:message key="edit_report.status.dh"/></font>
		</logic:equal>
		<logic:equal name="jingpinForm" property="applystate" value="03">
			<font color="red"><bean:message key="label.hiremanage.status3"/>
		</logic:equal>
		<logic:equal name="jingpinForm" property="applystate" value="01">
			<font color="red"><bean:message key="label.hiremanage.status1"/>
		</logic:equal>
	</legend>
<table border="0" cellspacing="1" align="center" cellpadding="1"  width="380" class="ListTable">	

	<tr class="trShallow1">
		<!--
		<td height="20"  class="RecordRow"  nowrap>
	
				&nbsp;&nbsp;<bean:message key="hire.jp.apply.apllypos" />&nbsp;:
				<logic:equal  name="jingpinForm" property="pos_parent" value="" >
	        		&nbsp;<bean:write name="jingpinForm" property="postion"/>
	        	</logic:equal>
	        	<logic:notEqual name="jingpinForm" property="pos_parent" value="">
		        	&nbsp;<bean:write name="jingpinForm" property="pos_parent"/>的
		        	<bean:write name="jingpinForm" property="postion"/>       		
				</logic:notEqual>
				  
		</td>
		-->
		<td height="20"  align="center"  class="RecordRow" width="20%"  nowrap>	
				&nbsp;<bean:message key="hire.jp.apply.apllypos" />&nbsp;							  
		</td>
		<td height="20"  align="left"  class="RecordRow" width="80%" nowrap>

				<logic:equal  name="jingpinForm" property="pos_parent" value="" >
	        		&nbsp;&nbsp;&nbsp;<bean:write name="jingpinForm" property="postion"/>
	        	</logic:equal>
	        	<logic:notEqual name="jingpinForm" property="pos_parent" value="">
		        	&nbsp;&nbsp;&nbsp;<bean:write name="jingpinForm" property="pos_parent"/><bean:message key="hire.jp.apply.of"/>
		        	<bean:write name="jingpinForm" property="postion"/>       		
				</logic:notEqual>
				  
		</td>
	</tr>
	
	<tr class="trShallow1">
		<td height="20"  align="center"  class="RecordRow"  nowrap>
			&nbsp;<bean:message key="hire.jp.apply.datum" />&nbsp;				
		</td>
		<td height="20"  align="left"  class="RecordRow"  nowrap>
			&nbsp;&nbsp;
				<logic:notEqual name="jingpinForm" property="applystate" value="03">
				<html:file name="jingpinForm" property="uploadfile" styleClass="text6"/>
				&nbsp;&nbsp;
				<a href="javascript:upload();"><bean:message key="hire.jp.apply.upload"/></a>
				<!--
				<html:button styleClass="mybutton" property="apply" onclick="upload();">
			  		<bean:message key="button.return"/>
				</html:button>	 
				<img id="new" src="/images/lawadd.gif"  onclick="upload();">   	 
				-->    		
				</logic:notEqual>				
		</td>		
	</tr>
	
	<tr class="trShallow1">
		<td align="center" width="20%" class="RecordRow"  nowrap>
			<bean:message key="lable.select"/>					
		</td>
		<td align="center" width="80%" class="RecordRow"  nowrap>
			<bean:message key="hire.jp.apply.datumname"/>		
		</td>
	</tr>
	  <hrms:extenditerate id="element" name="jingpinForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="30" scope="session">
	  <%if(i%2==0){%>
	  <tr class="trShallow">
	  <%}else{%>
	  <tr class="trDeep">
	  <%}%>
		  	<td align="center" class="RecordRow" nowrap>
			<hrms:checkmultibox name="jingpinForm" property="recordListForm.select"  value="ture" indexes="indexes"/>&nbsp;
		  	</td>
	  	<td align="left" class="RecordRow"  nowrap>
			<bean:write  name="element" property="name" filter="true"/>&nbsp;	
	  	</td>
	  </tr>
	  <%i++;%>
	  </hrms:extenditerate>
</table>
<table align="center"  width="380">
	<tr align="left">
		<td align="left" >
			<logic:equal  name="jingpinForm" property="applystate" value="" >
        		<html:button styleClass="mybutton" property="apply" onclick="to_apply();">
			  		<bean:message key="button.aplly"/>
				</html:button>	  
        	</logic:equal>
			<logic:equal  name="jingpinForm" property="applystate" value="01" >
        		<html:button styleClass="mybutton" property="apply" onclick="to_apply();">
			  		<bean:message key="button.aplly"/>
				</html:button>	  
        	</logic:equal>
        	<logic:equal  name="jingpinForm" property="applystate" value="07" >
        		<html:button styleClass="mybutton" property="apply" onclick="to_apply();">
			  		<bean:message key="button.aplly"/>
				</html:button>	  
        	</logic:equal>
			
			<logic:notEqual name="jingpinForm" property="applystate" value="03">
	        	<html:button styleClass="mybutton" property="apply" onclick="to_delete();">
			  		<bean:message key="button.delete"/>
				</html:button>	  		
			</logic:notEqual>
			
			<logic:notEqual name="jingpinForm" property="applystate" value="03">
	        	<html:button styleClass="mybutton" property="apply" onclick="to_card();">
			  		<bean:message key="hire.jp.apply.template"/>
				</html:button>	   		
			</logic:notEqual>					  
			
			<logic:equal  name="jingpinForm" property="returnflag" value="true" > 
				<html:button styleClass="mybutton" property="apply" onclick="to_return();">
			  		<bean:message key="button.return"/>
				</html:button>	 
			</logic:equal>

		</td>
	</tr>
	
</table>
</fieldset>
</form>

  	 


    