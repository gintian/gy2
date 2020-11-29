<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript" src="/js/common.js"></script>

<script type="text/javascript">

	function save()
	{
		var ids="";
  		var vos=document.getElementsByTagName("input");
		for(var i=0;i<vos.length;i++)  
		{
	  	  if(vos[i].type=="checkbox" && vos[i].name.substring(0,6)=='degree')	    
	 	  {
	 	  	if(vos[i].checked==true) 
	 	  		ids +="@1";
	 	  	else
	 	  		ids +="@0";	    	
		  }
   		}
   		var thevo=new Object();
		thevo.flag="true";
		thevo.degreeValues=ids.substring(1);
		thevo.num=$F('num');
		thevo.mode=$F('mode');
		thevo.value=$F('value');
		thevo.UMGrade=$F('UMGrade');
				
		thevo.grouped = $('grouped').value;		
/*		
		var grouped = $('grouped');
		if(grouped.checked==true)
			thevo.grouped='1';
		else
			thevo.grouped='0';
*/
				
		thevo.oper=$F('oper');
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
	 		parent.window.opener.height_add_ok(thevo);
		}
		parent.window.close();
	}
</script>

<html:form action="/performance/options/degreeHighSetAdd">
	<html:hidden name="perDegreeForm" property="num" />
<center><font size="4" ><bean:message key="jx.param.highsettitle" /></font></center>
	<table width="420px;" align="center" border="0" cellpadding="0"
		cellspacing="0" class="ListTable">
		<tr class="trShallow1">
			<td colspan='2' align="right" nowrap valign="top" class="RecordRow" nowrap>
				<bean:message key="kq.wizard.wise" />&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;&nbsp;<html:select name="perDegreeForm" styleId="mode"
					property="mode" size="1" style="width:150px">
					<html:option value="1">
						<bean:message key="jx.param.percent" />
					</html:option>
					<html:option value="2">
						<bean:message key="jx.param.empCount" />
					</html:option>
				</html:select>
			</td>
		</tr>
		<tr class="trDeep1">
			<td colspan='2'  align="right" nowrap valign="top" class="RecordRow" nowrap>
				<bean:message key="jx.param.oper" />&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;&nbsp;<html:select name="perDegreeForm" styleId="oper"
					property="oper" size="1" style="width:150px">
					<html:option value="1">
						<bean:message key="jx.param.noless" />
					</html:option>
					<html:option value="2">
						<bean:message key="jx.param.nomore" />
					</html:option>
				</html:select>
			</td>
		</tr>
		<tr class="trShallow1">
			<td colspan='2' align="right" nowrap valign="top" class="RecordRow" nowrap>
				<bean:message key="jx.param.value" />&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;&nbsp;<html:text maxlength="50"  styleClass="textbox"
					name="perDegreeForm" styleId="value" style="width:150px"
					property="value" onblur="if(this.value!='' && !isNum(this.value)){ alert('请输入整数！');this.value='';}"/>
			</td>
		</tr>
		<logic:notEqual name="perDegreeForm" property="itemCount" value="0">
			<tr class="trDeep1">
				<td rowspan="<bean:write name="perDegreeForm" property="itemCount" filter="true"/>"
					align="right" class="RecordRow" nowrap>
					<bean:message key="jx.param.degreeItem" />&nbsp;
				</td>
				<logic:iterate id="element" name="perDegreeForm" property="degrees">
					<bean:define id="nid" name="element" property="id" />
					<logic:equal name="element" property="bh" value="1">				
						<td align="right" nowrap valign="top" nowrap class="RecordRow">
							<bean:write name="element" property="itemname" filter="true" />&nbsp;
						</td>
						<td align="left" nowrap class="RecordRow">
							&nbsp;<input type="checkbox" name="degree${nid}" 
							<logic:equal name="element" property="value" value="1">checked="checked"</logic:equal>>
						</td>
					</logic:equal>
				</logic:iterate>
			</tr>	
			<% int i=0;%>		
			<logic:iterate id="element1" name="perDegreeForm" property="degrees">
				<bean:define id="mid" name="element1" property="id" />
				<logic:notEqual name="element1" property="bh" value="1">
					<%
					
					if (i % 2 == 0)
					{
			%>
			<tr class="trShallow1">
				<%
						} else
						{
				%>
			
			<tr class="trDeep1">
				<%
						}
						i++;
				%>
					
						<td align="right" nowrap valign="top" nowrap class="RecordRow">
							<bean:write name="element1" property="itemname" filter="true" />&nbsp;
						</td>
						<td align="left" nowrap class="RecordRow">
								&nbsp;<input type="checkbox" name="degree${mid}" 
							<logic:equal name="element1" property="value" value="1">checked="checked"</logic:equal>>
						</td>
					</tr>
				</logic:notEqual>
			</logic:iterate>
		</logic:notEqual>
		<tr class="trShallow1">
			<td colspan='2' align="right" nowrap valign="top" class="RecordRow" nowrap>
				<bean:message key="jx.param.deptGroup2" />&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;
				<html:select name="perDegreeForm" property="grouped" styleId="grouped" size="1" >
					<html:optionsCollection property="groupList" value="dataValue" label="dataName"/>
				</html:select>
			<!--
				&nbsp;<html:checkbox name="perDegreeForm" property="grouped" styleId="grouped" value="1" />
			-->
			</td>
		</tr>
		<tr class="trDeep1">
			<td colspan='2' align="right" nowrap valign="top" class="RecordRow" nowrap>
				<bean:message key="jx.param.org_grade" />&nbsp;
			</td>
			<td align="left" class="RecordRow" nowrap>
				&nbsp;
				<html:text maxlength="50" style="width:150px" styleClass="textbox" name="perDegreeForm" styleId="UMGrade"	property="UMGrade" />
			</td>
		</tr>
	</table>
	<table width="60%" align="center">
		<tr>		
			<td align="center">
				<input type='button' class="mybutton" property="b_ok"
					onclick='save()' value='<bean:message key="button.save"/>' />			
				<input type="button"
					value="<bean:message key='button.cancel'/>"
					onclick="parent.window.close();" Class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
<script type="text/javascript">
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>