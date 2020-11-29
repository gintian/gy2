<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.frame.dao.RecordVo" %>

<jsp:useBean id="lawbaseForm" class="com.hjsj.hrms.actionform.lawbase.LawBaseForm" scope="session" />
<script language="JavaScript" src="/js/function.js"></script>
<html>
	<SCRIPT LANGUAGE="javascript">
	    function checkValid() {	 
	        var  name= document.getElementsByName("law_base_vo.string(name)")[0]; 
		    if (trim(name.value) == "") {
		        alert("名称不能为空！");
		        name.focus();
		        return;
		    }
		    if(name.value.indexOf(",") > -1)
		    {
		    	alert("名称不能含有','号");
		    	name.focus();
		    	return;
		    }
		    if(name.value.indexOf("\"") > -1)
		    {
		    	alert("名称不能含有'\"'号");
		    	name.focus();
		    	return;
		    }
		    if(name.value.indexOf("\'") > -1)
		    {
		    	alert("名称不能含有'\''号");
		    	name.focus();
		    	return;
		    }
	        var description = document.getElementsByName("law_base_vo.string(description)")[0];
 		    if (trim(description.value) == "") {
		        alert("简述不能为空！");
		        description.focus();
		        return;
		    }	   
		    if (lawbaseForm.check.checked == true) {
		        lawbaseForm.check.value = "on";
		    } else {
		        lawbaseForm.check.value = "off";
		    }
		    lawbaseForm.check.checked = true;
		    var law_base_vo = new Object();
		    law_base_vo.name = name.value;
		    law_base_vo.description = description.value;
		    law_base_vo.check = lawbaseForm.check.value;
		    law_base_vo.up_base_id = document.getElementById("up_base_id").value;
		    law_base_vo.status = document.getElementById("status").value;
		    window.returnValue = law_base_vo;
		    window.close();
	    }
        function getArguments(up_base)
        {
    	    var up_node,base_id,val;
    	    var paraArray=window.dialogArguments;
    	    up_node = paraArray;
    	    if(up_node==null)
    	       return;
    	    base_id=up_node.uid;
    	    val=MM_findObj_(up_base);
    	    if(val==null)
    	        return;
    	        val.value=base_id;
    	
        }	
        function checkonchick(xname)
	    {	
		    var val = xname;
		   if(val.checked==true)
	       {
		       val.value="on";
			   <%
			       lawbaseForm.setCheck("on");
			   %>
		   } else {	
			   val.value="off";
			<%
			lawbaseForm.setCheck("");
			%>
						
    	   }
    	   
        }
        
        function checkLength(object){
     		var desc = object.value;
     		if ((null==desc)||(""==trim(desc)))
     			return;

     		if(IsOverStrLength(desc,250)){
     			alert(TRAIN_ROOM_MORE_LENGTH1+250+TRAIN_ROOM_MORE_LENGTH2+125+TRAIN_ROOM_MORE_LENGTH3);
     			object.focus();
     			object.value='';
     		}
     	}
        
    </SCRIPT>
	<base id="mybase" target="_self">
	<div class="fixedDiv3" style="padding: 5px 0 0 2;">
		<html:form action="/selfservice/lawbase/add_law_base">
			<html:hidden name="lawbaseForm" property="law_base_vo.string(up_base_id)" />
			<html:hidden styleId="up_base_id" name="lawbaseForm" property="law_base_vo.string(up_base_id)" />
			<html:hidden styleId="status" name="lawbaseForm" property="law_base_vo.string(status)" />
			<script>
              getArguments('law_base_vo.string(up_base_id)');	
            </script>
			<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
				<tr height="20">
					<td align="left" colspan="4" class="TableRow">
						<logic:equal name="lawbaseForm" property="basetype" value="4">
						  知识中心
						</logic:equal>
						<logic:equal name="lawbaseForm" property="basetype" value="1">
						 <bean:message key="label.law_base.base" />
						</logic:equal>
						<logic:equal name="lawbaseForm" property="basetype" value="5">
						 文件档案
						</logic:equal>
						</td>
				</tr>
				<tr>
					<td colspan="4" class="framestyle3 RecordRow">
						<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td align="right" nowrap valign="middle">
									<bean:message key="column.law_base.name" />&nbsp;
								</td>
								<td align="left" nowrap>
									<html:text styleClass="textColorWrite" name="lawbaseForm" property="law_base_vo.string(name)" size="51" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="framestyle3  RecordRow">
						<table border="0" cellpmoding="0" cellspacing="0" class="DetailTable" cellpadding="0">
							<tr class="list3">
								<td align="right" nowrap valign="middle">
									<bean:message key="column.law_base.description" />&nbsp;
								</td>
								<td align="left" nowrap>
									<html:textarea name="lawbaseForm" onchange="checkLength(this);" property="law_base_vo.string(description)" cols="60" rows="10" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="4" class="framestyle3 RecordRow">
						<logic:equal name="lawbaseForm" property="law_base_vo.string(status)" value="1">
							<input type="checkbox" name="check" checked onclick="checkonchick(this);">
							<bean:message key="column.law_base.status" />
						</logic:equal>
						<logic:notEqual name="lawbaseForm" property="law_base_vo.string(status)" value="1">
							<input type="checkbox" name="check" value="" onclick="checkonchick(this);">
							<bean:message key="column.law_base.status" />
						</logic:notEqual>
					</td>
				</tr>
				<tr class="list3" >
					<td align="center" colspan="4" style="height: 35;">
						<button Class="mybutton" name="b_save" onclick="checkValid()">
							<bean:message key="button.ok" />
						</button>
						<html:reset styleClass="mybutton" property="reset">
							<bean:message key="button.clear" />
						</html:reset>
						<html:button styleClass="mybutton" property="cancel" onclick="window.close();">
							<bean:message key="button.cancel" />
						</html:button>
					</td>
				</tr>
			</table>
		</html:form>
</div>
</html>
