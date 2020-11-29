<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.valueobject.common.FieldItemView"%>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/general/inform/emp_main.js"></script>

<style>
<!--
.fixedDiv3
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-52);
	width:expression(document.body.clientWidth); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
    margin-bottom: -10px;
    margin-right: 0px;
}
-->
</style>
<script>
	function setPhoto(){
	window.frames['ole'].document.getElementById("picturefile").click();
}
function save0(dbname)
{
	//alert(document.getElementById('a0177'));
	if(null != document.getElementById('a0177')){
		if('${mInformForm.isTestBirthday}'=='1')
		{
			
	       if(!testIdCard())
	      		 return;
	    } 
		<% int m=0; %>
		<logic:iterate  id="element1"    name="mInformForm"  property="fieldslist" indexId="index"> 
		<%
			FieldItemView abean1=(FieldItemView)pageContext.getAttribute("element1");
		    boolean isFillable=abean1.isFillable();	
		%>		
		
			var aa<%=m%>=document.getElementsByName("fieldslist[<%=m%>].value")
			if(<%=isFillable%>)
			{
				if(aa<%=m%>[0].value=="")
				{
					alert("<bean:write  name="element1" property="itemdesc"/>"+THIS_IS_MUST_FILL+"！");
					return;						
				}
			}		
			<% m++; %>
		</logic:iterate>
		save(dbname);
	}else{
		window.close();
	}
}
</script>
<hrms:themes></hrms:themes>
<body onbeforeunload="closeWin();">
<html:form action="/general/inform/emp_add">
	<html:hidden name="mInformForm" property="orgparentcode" />
	<html:hidden name="mInformForm" property="deptparentcode" />
    <html:hidden name="mInformForm" property="posparentcode" />   
	<input type='button' value='&nbsp;<bean:message key='button.save' />&nbsp;'	class="mybutton" onclick="save0('${mInformForm.dbname}');">
	<input type='button' value='&nbsp;<bean:message key='button.close' />&nbsp;' class="mybutton" onclick='closeWin();'>
	<%
	int i = 1, j = 1;  
	String classname="RecordRow noleft noright";
	%>
	<div class="fixedDiv3 common_border_color">
	<table width="100%" align="center" border="0" cellpadding="1" cellspacing="0">
		<tr class="trShallow1">
			<logic:iterate id="element" name="mInformForm" property="fieldslist" indexId="index">
									<%
								FieldItemView abean = (FieldItemView) pageContext
														.getAttribute("element");
												boolean isFillable1 = abean.isFillable();
							%>				
				<td align="right" class="<%=classname %>" style="border-top: none;" nowrap>
					<bean:write name="element" property="itemdesc" filter="true" />
			</td>
				<td align="left" class="RecordRow noright" style="border-top: none;" nowrap>
				
					<logic:equal name="element" property="itemtype" value="A">
						<logic:equal name="element" property="codesetid" value="0">
							<logic:equal name="element" property="itemid" value="a0177">
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="text4" name="mInformForm"
									styleId="${element.itemid}" onblur="calcuBirthday(this)"
									property='<%="fieldslist[" + index + "].value"%>'/>									
							 </logic:equal>	
							 <logic:notEqual name="element" property="itemid" value="a0177">
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="text4" name="mInformForm"
									styleId="${element.itemid}"
									property='<%="fieldslist[" + index + "].value"%>' />									
							 </logic:notEqual>	
						</logic:equal>
						<logic:notEqual name="element" property="codesetid" value="0">
							<logic:equal name="element" property="itemid" value="b0110">
								<html:hidden name="mInformForm"
									property='<%="fieldslist[" + index + "].value"%>'
									styleId='b0110_value' onchange="changepos('UN',this)" />
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="text4" styleId="b0110" name="mInformForm"
									property='<%="fieldslist[" + index + "].viewvalue"%>'
									onchange="fieldcode(this,2)" />
								<img src="/images/code.gif" id='img${element.itemid}'
									onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>",mInformForm.orgparentcode.value,"1");'
									align="absmiddle" />&nbsp;	
						</logic:equal>
							<logic:equal name="element" property="itemid" value="e0122">
								<html:hidden name="mInformForm"
									property='<%="fieldslist[" + index + "].value"%>'
									styleId='e0122_value' onchange="changepos('UM',this)" />
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="text4" styleId="e0122" name="mInformForm"
									property='<%="fieldslist[" + index + "].viewvalue"%>'
									onchange="fieldcode(this,2)" />
								<img src="/images/code.gif" id='img${element.itemid}'
									onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>",mInformForm.deptparentcode.value,"2");'
									align="absmiddle" />&nbsp;	
						</logic:equal>
						<logic:equal name="element" property="itemid" value="e01a1">
								<html:hidden name="mInformForm"
									property='<%="fieldslist[" + index + "].value"%>'
									styleId='e01a1_value' onchange="changepos('@k',this)" />
								<html:text maxlength="${element.itemlength}" size="17"
									styleClass="text4" styleId="e01a1" name="mInformForm"
									property='<%="fieldslist[" + index + "].viewvalue"%>'
									onchange="fieldcode(this,2)" />
								<img src="/images/code.gif" id='img${element.itemid}'
									onclick='openInputCodeDialogOrgInputPos("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>",mInformForm.posparentcode.value,"2");'
									align="absmiddle" />&nbsp;	
						</logic:equal>
							<logic:notEqual name="element" property="itemid" value="b0110">
								<logic:notEqual name="element" property="itemid" value="e0122">
									<logic:notEqual name="element" property="itemid" value="e01a1">
									<html:hidden name="mInformForm"
										property='<%="fieldslist[" + index + "].value"%>'
										styleId="${element.itemid}_value" />
									<html:text maxlength="${element.itemlength}" size="17"
										styleClass="text4" name="mInformForm"
										property='<%="fieldslist[" + index + "].viewvalue"%>'
										onchange="fieldcode(this,2)" styleId="${element.itemid}" />
									<img id='img${element.itemid}' src="/images/code.gif"
										onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="fieldslist[" + index + "].viewvalue"%>");'
										align="absmiddle" />&nbsp;
								</logic:notEqual>
			 				</logic:notEqual>
							</logic:notEqual>
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="N">
						<logic:equal name="element" property="decimalwidth" value="0">
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="text4"
								onkeypress="event.returnValue=IsDigit2(this);"
								onblur='isNumber(this);' name="mInformForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' />
						</logic:equal>
						<logic:notEqual name="element" property="decimalwidth" value="0">
							<html:text maxlength="${element.itemlength}" size="17"
								styleClass="text4"
								onkeypress="event.returnValue=IsDigit(this);"
								onblur='isNumber(this);' name="mInformForm"
								styleId="${element.itemid}"
								property='<%="fieldslist[" + index + "].value"%>' />
						</logic:notEqual>
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="D">		
						<input type="text" name='<%="fieldslist[" + index + "].value"%>' maxlength="${element.itemlength}" size="17"  id="${element.itemid}" extra="editor"  class="text4"  style="font-size:10pt;text-align:left"
							dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}"  onblur="if(!validate(this,'${element.itemdesc}')) { this.value='';}">				
					</logic:equal>
					<logic:equal name="element" property="itemtype" value="M">
						<html:textarea name="mInformForm" styleId="${element.itemid}"
										property='<%="fieldslist[" + index + "].value"%>'
										cols="20" rows="3" styleClass="textarea"></html:textarea>
					</logic:equal>
										  <%
  	if (isFillable1) {
  %> &nbsp;<font color='red'>*</font>&nbsp;<%
 	}
 %>	
				</td>
					<%
							    j++;	
						    if (i == 1 && j == 3)
						    {
						    	j++;
				%>		
                    <td id="photo" rowspan="5" colspan="2"  align="center" class="RecordRow noright" style="border-top: none;" nowrap >                    	
                    		<iframe id="ole" name="ole"  width="85" height="120" frameborder="0" scrolling="no"  src="/general/inform/emp/view/displaypicture.do?b_query2=link&a0100=${mInformForm.a0100}&dbname=${mInformForm.dbname}"></iframe> 		                
                    </td>

					<%	
					}		
						   if (i ==1 && j==4 || i>5 && j % 3==0 || i>1 && i<6 && j%2==0)
						    {   
						       classname="RecordRow noleft noright";
						    	if (i % 2 == 0)
								{
				%>			
		</tr>  
		<tr class="trShallow1">
				<%
								} else{
				%>
			</tr>  
		<tr class="trDeep1">
			<%
								}
								i++;			
					    } else{
					        classname="RecordRow noright";
					    }
			%>
			</logic:iterate>
		</tr>
	</table>
	</div>
</html:form>
</body>
<script>
	parent.frames['a'].location="/general/inform/get_data_table.do?b_menu=link";
	//设置只读的文本框
	<logic:iterate  id="element1" name="mInformForm" property="readOnlyFlds" indexId="index">
		var itemid = '<bean:write name="element1" property="itemid" filter="true" />';
		var codesetid = '<bean:write name="element1" property="codesetid" filter="true" />';
		obj = $(itemid);
		obj.readOnly="true";
		obj.className="textColorRead";
		var imgid = 'img'+itemid;
		obj = $(imgid);		
		if(typeof(obj)=="object" && codesetid!='0'&& typeof(obj.style)!='undefined')
		{
			obj.style.display="none";
		}			
	</logic:iterate>
</script>