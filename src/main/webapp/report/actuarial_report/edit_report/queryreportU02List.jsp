<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page
	import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.sys.FieldItem,com.hrms.frame.dbstruct.Field,java.util.*"%>

<script language="JavaScript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<style>
.fixedtab {
	overflow: auto;
	height: expression(350);
	BORDER-BOTTOM: #94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
}
.queryreportU02ListTable{
	width:expression(document.body.clientWidth-10);
}
</style>
<hrms:themes />
<script type="text/javascript">
function query()
{	
	
	   var hashvo=new ParameterSet();
	   var i=0;
	   var j=0;
	   <logic:iterate id="element"  name="editReport_actuaialForm" property="editlistU02" indexId="index"> 	   
	     <bean:define id="itemid" name="element" property="itemid"/>
	         var valueInputs=document.getElementsByName("<%="editlistU02[" + index + "].value"%>");
             var dobj=valueInputs[0];
             hashvo.setValue("${itemid}",dobj.value);	
	     <logic:notEqual name="element" property="codesetid" value="0">
	         var valueInputs=document.getElementsByName("<%="editlistU02[" + index + "].viewvalue"%>");
             var dobj=valueInputs[0];
             hashvo.setValue("${itemid}"+"view",dobj.value);
             i++;        
	     </logic:notEqual>	
	     	<logic:equal name="element" property="itemtype" value="D">
         
             var valueInputs=document.getElementsByName("<%="editlistU02[" + index + "].value"%>");
             var dobj=valueInputs[0];
             hashvo.setValue("${itemid}"+"view",dobj.value);
             var valueInputs=document.getElementsByName("<%="editlistU02[" + index + "].value2"%>");
             var dobj=valueInputs[0];
              hashvo.setValue("${itemid}"+"view2",dobj.value);
            
	     	</logic:equal>  
	   </logic:iterate>
	 if(document.getElementsByName("like")[0].checked){
	   hashvo.setValue("like","1");
	 }else{
	   hashvo.setValue("like","0");
	 }
	   
	       hashvo.setValue("report_id","${editReport_actuaialForm.report_id}");
         var request=new Request({method:'post',asynchronous:false,onSuccess:reflag,functionId:'03060000235'},hashvo);
  
}
function reflag(outparamters)
{
    var subquerysql= outparamters.getValue('subquerysql');
   subquerysql= getEncodeStr(subquerysql);
		   window.returnValue=subquerysql;
		   window.close();
	    
}
function qxFunc()
{
   var thevo=new Object();
   thevo.flag="true";
   window.returnValue=thevo;
   window.close();
}
function IsDigit2(obj) 
{
		if((event.keyCode >47) && (event.keyCode <= 57))
			return true;
		else
			return false;	
}
//输入数值型
function IsDigit(obj) 
{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
}
function isNumber(obj)
{
  		var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		//alert(INPUT_NUMBER_VALUE+'!');
  		obj.value=''; 
  	    obj.focus();
  	}  	   
}
</script>
<%
	EditReport_actuaialForm form = (EditReport_actuaialForm) session
			.getAttribute("editReport_actuaialForm");
	int len = form.getEditlistU02().size();
%>
<body>
	<html:form
		action="/report/actuarial_report/edit_report/editreportU02List">
		<html:hidden name="editReport_actuaialForm" property="id" />
		<html:hidden name="editReport_actuaialForm" property="unitcode" />
		<html:hidden name="editReport_actuaialForm" property="report_id" />
		<html:hidden name="editReport_actuaialForm" property="kmethod" />
		<table width='95%' height="100%" class="queryreportU02ListTable">
			<tr>
				<td>
					<div class='fixedtab' style='width: 100%;height:100%'>
						<table width="100%" border="0" cellpadding="3" cellspacing="0"
							align="center" class="ListTableF" style="border-top:none;border-left:none;border-right:none;">
							<tr height="20">
								<td colspan="4" align="left" style="border-top:none;border-left:none;border-right:none;" class="TableRow">
									<logic:equal name="editReport_actuaialForm"
										property="report_id" value="U02_1">
							   表2-1离休人员
							</logic:equal>
									<logic:equal name="editReport_actuaialForm"
										property="report_id" value="U02_2">
							   表2-2退休人员
							</logic:equal>
									<logic:equal name="editReport_actuaialForm"
										property="report_id" value="U02_3">
							   表2-3内退人员
							</logic:equal>
									<logic:equal name="editReport_actuaialForm"
										property="report_id" value="U02_4">
							   表2-4遗属
							</logic:equal>
								</td>
							</tr>
							<tr class="trDeep">
								<%
									int i = 0, j = 0;
								%>
								<logic:iterate id="element" name="editReport_actuaialForm"
									property="editlistU02" indexId="index">
									<logic:equal name="element" property="visible" value="true">
										<%
											FieldItem item = (FieldItem) pageContext
																		.getAttribute("element");
																Field field = item.cloneField();

																if (i == 2) {
										%>
										<%
											if (j % 2 == 0) {
										%>
									
							</tr>
							<tr class="trShallow">
								<%
									} else {
								%>
							</tr>
							<tr class="trDeep">
								<%
									}
															i = 0;
															j++;
														}
								%>
								<logic:notEqual name="element" property="itemtype" value="M">
								    <%if(i%2==0){ %>
									<td align="right" class="RecordRow" style="border-left:none;" nowrap>
										<bean:write name="element" property="itemdesc" filter="false" />
									</td>
									<%}else{ %>
									<td align="right" class="RecordRow" nowrap>
										<bean:write name="element" property="itemdesc" filter="false" />
									</td>
									<%} %>
									<td align="left" class="RecordRow" style="border-right:none;" nowrap>
										<logic:equal name="element" property="codesetid" value="0">
											<logic:notEqual name="element" property="itemtype" value="D">
												<logic:notEqual name="element" property="itemtype" value="N">

													<html:text maxlength="${element.itemlength}" size="20"
														styleClass="textbox" name="editReport_actuaialForm"
														styleId="${element.itemid}"
														property='<%="editlistU02["
														+ index + "].value"%>' />
												</logic:notEqual>
												<logic:equal name="element" property="itemtype" value="N">
													<logic:equal name="element" property="decimalwidth"
														value="0">
														<html:text maxlength="${element.itemlength}" size="20"
															styleClass="textbox"
															onkeypress="event.returnValue=IsDigit2(this);"
															onblur='isNumber(this);' name="editReport_actuaialForm"
															styleId="${element.itemid}"
															property='<%="editlistU02["
																	+ index
																	+ "].value"%>' />
													</logic:equal>
													<logic:notEqual name="element" property="decimalwidth"
														value="0">
														<html:text maxlength="${element.itemlength}" size="20"
															styleClass="textbox"
															onkeypress="event.returnValue=IsDigit(this);"
															onblur='isNumber(this);' name="editReport_actuaialForm"
															styleId="${element.itemid}"
															property='<%="editlistU02["
																	+ index
																	+ "].value"%>' />
													</logic:notEqual>
												</logic:equal>
											</logic:notEqual>
											<logic:equal name="element" property="itemtype" value="D">
												<input type="text"
													name='<%="editlistU02["
													+ index + "].value"%>'
													maxlength="10" size="20" id="${element.itemid}"
													extra="editor" class="m_input"
													style="font-size: 10pt; text-align: left"
													dropDown="dropDownDate" value="${element.value}"
													onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
														到&nbsp;<input type="text"
													name='<%="editlistU02["
													+ index + "].value2"%>'
													maxlength="10" size="20" id="${element.itemid}"
													extra="editor" class="m_input"
													style="font-size: 10pt; text-align: left"
													dropDown="dropDownDate" value="${element.value}"
													onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">

											</logic:equal>
										</logic:equal>
										<logic:notEqual name="element" property="codesetid" value="0">
											<html:hidden name="editReport_actuaialForm"
												property='<%="editlistU02[" + index
												+ "].value"%>'
												styleId="${element.itemid}_value" />
											<html:text maxlength="0" size="20" styleClass="textbox"
												name="editReport_actuaialForm"
												property='<%="editlistU02[" + index
												+ "].viewvalue"%>'
												onchange="fieldcode(this,2)" styleId="${element.itemid}" />
											<img id='img${element.itemid}' src="/images/code.gif"
												onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="editlistU02[" + index
												+ "].viewvalue"%>");'
												align="absmiddle" />&nbsp;
			 						</logic:notEqual>
										<%
											i++;
																	if (field.isFillable())
																		out.print("<font color='red'>*</font>");
										%>

									</td>
									<%
										if (index < len - 1) {
									%>
									<logic:equal name="editReport_actuaialForm"
										property='<%="editlistU02["
													+ (index + 1)
													+ "].itemtype"%>'
										value="M">
										<%
											if (i < 2) {
										%>
										<td align="left" class="RecordRow" nowrap></td>
										<td align="left" class="RecordRow" style="border-right:none;" nowrap></td>
										<%
											i++;
																			}
										%>

									</logic:equal>
									<%
										} else if (index == len - 1) {
									%>
									<%
										if (i < 2) {
									%>
									<td align="left" class="RecordRow" nowrap></td>
									<td align="left" class="RecordRow" style="border-right:none;" nowrap></td>
									<%
										i++;
																	}
									%>
									<%
										}
									%>
								</logic:notEqual>
								<logic:equal name="element" property="itemtype" value="M">
									<td align="right" class="RecordRow" nowrap valign="top">
										<bean:write name="element" property="itemdesc" filter="false" />
									</td>
									<td align="left" class="RecordRow" nowrap colspan="3">
										<html:textarea name="editReport_actuaialForm"
											styleId="${element.itemid}"
											property='<%="editlistU02[" + index
												+ "].value"%>'
											cols="64" rows="4" styleClass="textboxMul"></html:textarea>
										<%
											//										if(field.isFillable())
																	//												out.print("<font color='red'>*</font>");
										%>

									</td>
									<%
										i = 2;
									%>
								</logic:equal>
								</logic:equal>
								<logic:notEqual name="element" property="visible" value="true">
									<html:hidden name="editReport_actuaialForm"
										property='<%="editlistU02[" + index
										+ "].value"%>'
										styleId="${element.itemid}_value" />
									<html:hidden name="editReport_actuaialForm"
										property='<%="editlistU02[" + index
										+ "].viewvalue"%>'
										styleId="${element.itemid}" />
								</logic:notEqual>
								</logic:iterate>
							</tr>
							<tr height="20">
								<td colspan="4" align="left" width="5%">
									<html:checkbox name="editReport_actuaialForm" property="like"
										styleId="like">
										<bean:message key="label.query.like" />
									</html:checkbox>
								</td>
							</tr>
						</table>
					</div>
				</td>
			</tr>
			<tr>
				<td align="center" class="tdFontcolor" height="35px" nowrap>
					<hrms:submit styleClass="mybutton" onclick='query();'>
						<bean:message key="button.query" />
					</hrms:submit>
					<html:reset styleClass="mybutton" property="bc_clear">
						<bean:message key="button.clear" />
					</html:reset>
					<input type="button" name="br_return"
						value="<bean:message key="button.cancel"/>" class="mybutton"
						onclick="window.close();">
				</td>
			</tr>
		</table>
	</html:form>
	<script>
</script>
</body>