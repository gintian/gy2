<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.report.actuarial_report.edit_report.EditReport_actuaialForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.hjsj.sys.FieldItem,
				com.hrms.frame.dbstruct.Field,
				java.util.*" %>

<script language="JavaScript" src="/js/validateDate.js"></script>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<hrms:themes />
<style>
.fixedtab 
{ 
	overflow:auto; 
	height:expression(350);
	BORDER-BOTTOM: 1pt solid; 
    BORDER-LEFT: 1pt solid; 
    BORDER-RIGHT: 1pt solid; 
    BORDER-TOP: 1pt solid ; 	
}
</style>
<script type="text/javascript">
function save(oper)
{	
	
	   var hashvo=new ParameterSet();
	   var i=0;
	   <logic:iterate id="element"  name="editReport_actuaialForm" property="editlistU02" indexId="index"> 	   
	     <bean:define id="itemid" name="element" property="itemid"/>
	         var valueInputs=document.getElementsByName("<%="editlistU02["+index+"].value"%>");
             var dobj=valueInputs[0];
             hashvo.setValue("${itemid}",dobj.value);	
	     <logic:notEqual name="element" property="codesetid" value="0">
	         var valueInputs=document.getElementsByName("<%="editlistU02["+index+"].viewvalue"%>");
             var dobj=valueInputs[0];
             hashvo.setValue("${itemid}"+"view",dobj.value);
             i++;        
	     </logic:notEqual>	     
	   </logic:iterate>
	     hashvo.setValue("id","${editReport_actuaialForm.id}");	
	     hashvo.setValue("unitcode","${editReport_actuaialForm.unitcode}");	
	     hashvo.setValue("report_id","${editReport_actuaialForm.report_id}");
	     hashvo.setValue("kmethod","${editReport_actuaialForm.kmethod}");
//	     hashvo.setValue("olditemdesc","${editReport_actuaialForm.olditemdesc}");
	     hashvo.setValue("oper",oper);
	     hashvo.setValue("flag","${editReport_actuaialForm.flag}");
         var request=new Request({method:'post',asynchronous:false,onSuccess:reflag,functionId:'03060000207'},hashvo);
  
}
function reflag(outparamters)
{
    var flag= outparamters.getValue('flag');
    var oper=outparamters.getValue('oper');
    if(flag=="true")
    {
       alert("新增成功！");
       if(oper=='saveClose')
       {
	       var thevo=new Object();
		   thevo.flag="true";
		   window.returnValue=thevo;
		   window.close();
	   }
	   else
	   {
	   		var url=document.location.href;
	   		document.location.href=url;
	   
	   }
		
    }
    
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
	EditReport_actuaialForm form = (EditReport_actuaialForm)session.getAttribute("editReport_actuaialForm");
	int len = form.getEditlistU02().size();  
 %>
<body>
<html:form action="/report/actuarial_report/edit_report/editreportU02List">	
	 <html:hidden name="editReport_actuaialForm" property="id"/>  
	 <html:hidden name="editReport_actuaialForm" property="unitcode"/>  
	 <html:hidden name="editReport_actuaialForm" property="report_id"/>  
     <html:hidden name="editReport_actuaialForm" property="kmethod"/>  
<br/>
<table width='740px' height="430px" style="margin-top: -22px;margin-left: -17px;">
<tr valign="top"><td>&nbsp;&nbsp;</td><td valign="top">
			<input type='button' value='<bean:message key='button.save' />'	class="mybutton" onclick='save("saveClose");'>
			<input type='button' value='<bean:message key='button.savereturn' />'	class="mybutton" onclick='save("");'>						
			<input type="button" class="mybutton" value="<bean:message key='button.cancel'/>" onClick="qxFunc()">  							
 </td></tr>
<tr valign="top"><td>&nbsp;&nbsp;</td><td valign="top">
<div class='fixedtab' style='width:100%;height:390px; vertical-align: top;'  >

	
				
				<table width="100%" border="0" cellpadding="3" cellspacing="0"
					align="center" class="ListTableF" style="border-top:none;border-left:none;border-right:none;">
					<tr height="20">
						<td colspan="4" align="left" class="TableRow" style="border-top:none;border-left:none;border-right:none;">	
							&nbsp;&nbsp;
							<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_1">
							   表2-1离休人员
							</logic:equal>
							<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_2">
							   表2-2退休人员
							</logic:equal>
							<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_3">
							   表2-3内退人员
							</logic:equal>
							<logic:equal name="editReport_actuaialForm" property="report_id" value="U02_4">
							   表2-4遗属
							</logic:equal>
						</td>
					</tr>
					<tr class="trDeep">
					<%int i=0,j=0; %>
				    <logic:iterate  id="element" name="editReport_actuaialForm" property="editlistU02" indexId="index">
						<logic:equal name="element" property="visible" value="true">
						<%
						FieldItem item=(FieldItem)pageContext.getAttribute("element");
						Field field=item.cloneField();
						
						if(i==2){ 
						%>
							<%if(j%2 == 0){%>
						</tr><tr class="trShallow">
						<%}else{%>
						</tr><tr class="trDeep">
						<%}i=0;j++;} %>
							<logic:notEqual name="element" property="itemtype" value="M">
							    <%if(i%2==0){ %>
								<td align="right" class="RecordRow"  style="border-left:none;" nowrap>
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
												
													<html:text maxlength="${element.itemlength}" size="20" styleClass="textbox" 
															name="editReport_actuaialForm"  styleId="${element.itemid}" property='<%="editlistU02[" + index + "].value"%>' /> 																																
											</logic:notEqual>
										   <logic:equal name="element" property="itemtype" value="N">
												<logic:equal name="element" property="decimalwidth" value="0">
													<html:text maxlength="${element.itemlength}" size="20" styleClass="textbox" onkeypress="event.returnValue=IsDigit2(this);" onblur='isNumber(this);'
														name="editReport_actuaialForm"  styleId="${element.itemid}" property='<%="editlistU02[" + index + "].value"%>' /> 		  						
												</logic:equal>												
												<logic:notEqual name="element" property="decimalwidth" value="0">
													<html:text maxlength="${element.itemlength}" size="20" styleClass="textbox" onkeypress="event.returnValue=IsDigit(this);" onblur='isNumber(this);'
														name="editReport_actuaialForm"   styleId="${element.itemid}" property='<%="editlistU02[" + index + "].value"%>' /> 								
												</logic:notEqual>
											</logic:equal>
										</logic:notEqual>	
											<logic:equal name="element" property="itemtype" value="D">	
												<input type="text" name='<%="editlistU02[" + index + "].value"%>' maxlength="10" size="20"  id="${element.itemid}" extra="editor"  class="m_input"  style="font-size:10pt;text-align:left"
															   dropDown="dropDownDate" value="${element.value}"  onchange=" if(!validate(this,'${element.itemdesc}')) { this.value='';}">
											</logic:equal>	
									</logic:equal>									
									<logic:notEqual name="element" property="codesetid" value="0">									
									        <html:hidden name="editReport_actuaialForm" property='<%="editlistU02[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
											<html:text maxlength="0" size="20" styleClass="textbox" 
													name="editReport_actuaialForm"  property='<%="editlistU02[" + index + "].viewvalue"%>' onchange="fieldcode(this,2)"
													   styleId="${element.itemid}" />
			 								<img id='img${element.itemid}' src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="editlistU02[" + index + "].viewvalue"%>");' align="absmiddle"/>&nbsp;
			 						</logic:notEqual>									
											<%i++;
											if(field.isFillable())
												out.print("<font color='red'>*</font>");
											 %>
											
								</td>
								<%if(index<len-1) {	%>
								<logic:equal name="editReport_actuaialForm" property='<%="editlistU02[" + (index+1) + "].itemtype"%>' value="M">
									<%if(i<2){ %>
									<td align="left" class="RecordRow" nowrap></td>
									<td align="left" class="RecordRow" style="border-right:none;" nowrap></td>
									<%i++; }%>
									
								</logic:equal>
								<%} else if(index==len-1){%>
									<%if(i<2){ %>
									<td align="left" class="RecordRow" nowrap></td>
									<td align="left" class="RecordRow" style="border-right:none;" nowrap></td>
									<%i++; }%>		
								<%} %>
							</logic:notEqual>
							<logic:equal name="element" property="itemtype" value="M">
								<td align="right" class="RecordRow" nowrap  valign="top" >
									<bean:write name="element" property="itemdesc" filter="false" />
								</td>
								<td align="left" class="RecordRow" nowrap  colspan="3">
									<html:textarea name="editReport_actuaialForm"  styleId="${element.itemid}"
										property='<%="editlistU02[" + index + "].value"%>'
										cols="64" rows="4" styleClass="textboxMul"></html:textarea>
										<% 
										if(field.isFillable())
												out.print("<font color='red'>*</font>");
										 %>
										
								</td>
								<%i=2; %>
							</logic:equal>
						  </logic:equal>
						  <logic:notEqual name="element" property="visible" value="true">
						       <html:hidden name="editReport_actuaialForm" property='<%="editlistU02[" + index + "].value"%>' styleId="${element.itemid}_value"/>  
						       <html:hidden name="editReport_actuaialForm" property='<%="editlistU02[" + index + "].viewvalue"%>' styleId="${element.itemid}" />
						  </logic:notEqual>
						</logic:iterate>
					</tr>	
				</table>
			
</div>		
</td></tr></table>		
</html:form>
<script>
	
</script>
</body>