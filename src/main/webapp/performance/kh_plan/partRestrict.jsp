<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>  
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<hrms:themes />
<script language="javascript" src="/performance/kh_plan/examPlan.js"></script>

<style>
	div#treemenu {
	BORDER-BOTTOM:#94B6E6 1px solid; 
	BORDER-LEFT: #94B6E6 1px solid; 
	BORDER-RIGHT: #94B6E6 1px solid; 
	BORDER-TOP: #94B6E6 1px solid; 
	width: 100%;
	height: 320px;
	overflow: auto;
	align:center;
	}
.Input_self{                                                                    
  font-size:   12px;                                              
  font-weight:   bold;                                                          
  background-color:   #FFFFFF;         
  letter-spacing:   1px;                      
  text-align:   right;                        
  height:   90%;                                    
  width:   100%;                                    
  border:   1px   solid   #94B6E6;           
  cursor:   hand;                                     
  } 
</style>

<script>	
	<%if(request.getParameter("br_save")!=null){%>
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
	<%}%>
	function save()
	{
		var fine_str='';
		var badly_str='';
		var isValue = document.forms[0].accordPVFlag[1].checked;
		
		if("${param.type}"=="fine")
		{
			<logic:iterate id="element" name="examPlanForm" property="fine_partRestrict" indexId="index">
				var value = document.getElementById('f_<bean:write name="element" property="point_id" />').value;
				if(value=='')
					value='-1';	
				if("${param.wholeEval}"=="false" && '<bean:write name="element" property="point_id" />'=='C_whole_grade')
				{
					fine_str=fine_str+'';
					document.getElementById('f_C_whole_grade').value='0';
				}
				else	
				{	
					fine_str=fine_str+(',<bean:write name="element" property="point_id" />'+'='+getBiliValue(value,isValue));
					document.getElementById('f_<bean:write name="element" property="point_id" />').value=getBiliValue(value,isValue);
				}
			</logic:iterate>			
		}
		else if("${param.type}"=="badly")
		{
			<logic:iterate id="element" name="examPlanForm" property="badly_partRestrict" indexId="index">
				var value = document.getElementById('b_<bean:write name="element" property="point_id" />').value;
				if(value=='')
					value='-1';		
				//alert('${param.wholeEval}:'+('${param.wholeEval}'=='false')+'  C_whole_grade:'+('<bean:write name="element" property="point_id" />'=='C_whole_grade'));		
				if("${param.wholeEval}"=="false" && '<bean:write name="element" property="point_id" />'=='C_whole_grade')
				{
					badly_str=badly_str+'';
					document.getElementById('b_C_whole_grade').value='0';
				}
				else
				{
					badly_str=badly_str+(',<bean:write name="element" property="point_id" />'+'='+getBiliValue(value,isValue));
					document.getElementById('b_<bean:write name="element" property="point_id" />').value=getBiliValue(value,isValue);
				}
			</logic:iterate>			
		}	
		examPlanForm.action="/performance/kh_plan/param_partRestrict.do?br_save=link&type=${param.type}&wholeEval=${param.wholeEval}&plan_id=${param.plan_id}&templId=${param.templId}&theStatus=${param.theStatus}";
		examPlanForm.submit();		
	}
    function closewindow()
    {
        if(window.showModalDialog) {
            parent.window.close();
        }else{
            window.open("about:blank","_top").close();
        }
    }
	function changeTitle(theFlag)
	{
		if("${param.type}"=="fine")
		{
			Element.hide('maxvalue');
			Element.hide('maxbili');
			if(document.forms[0].accordPVFlag[1].checked)
				Element.show('maxvalue');
			else
				Element.show('maxbili');	
		}
		if("${param.type}"=="badly")
		{
			Element.hide('minvalue');
			Element.hide('minbili');
			if(document.forms[0].accordPVFlag[1].checked)
				Element.show('minvalue');
			else
				Element.show('minbili');	
		}	
		if(theFlag==1)
		{
			var objs=document.getElementsByTagName("input");
			for(var i=0;i<objs.length;i++)  
			{
	  	 	 if(objs[i].type=="text")	    
	    		objs[i].value="";
   			}	
   		}	
	}

	function getBiliValue(strValue,isValue)
	{
		var strLen = strValue.length;
		if(isValue==false && strValue!='-1')
			return parseFloat(strValue)/100;
		return strValue;
	}
	function testNum(theObj)
	{
		if(theObj.value!='')
		{
			if(document.forms[0].accordPVFlag[0].checked && parseFloat(theObj.value)>100)
			{
				alert(PERCENT_LIMIT);
				theObj.value="";
				theObj.focus();
			}
		}
	}
	function IsDigit(obj) 
	{	
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if(document.forms[0].accordPVFlag[0].checked)//比例允许小数
			{
				if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
					return false;
				if((event.keyCode == 46) && (values.length==0))//首位是.
					return false;	
			}else //数值只能是整数
			{
				if(event.keyCode == 46)
					return false;	
			}
			return true;
		}
			return false;	
	}
</script>
<html:form action="/performance/kh_plan/param_partRestrict">
<table width="98%" border="0" align="center" >
	<tr>
		<td>				
	<div id="treemenu">
		<html:radio name="examPlanForm" property="accordPVFlag" value="2" onclick="changeTitle(1)"/>
			<bean:message key='jx.khplan.param1.title8' /> 
		<html:radio name="examPlanForm" property="accordPVFlag" value="1" onclick="changeTitle(1)"/>
			<bean:message key='jx.khplan.param1.title7' />		
		
		<table width="100%" border="0" align="center">
			<%
				    int i = 0;
				    int j = 0;
			%>
			<%if(request.getParameter("type").equalsIgnoreCase("fine")){ %>
			<tr id="fine_table">
				<td colspan="3">
					<table width="100%" border="0" cellspacing="0" align="center" style="border-width:0 1px 1px 0;border-style:solid;"
						cellpadding="0" class="ListTable0 common_border_color">
						<tr>
							<td class="TableRow" nowrap align="center" width="5%">

							</td>
							<td class="TableRow" nowrap align="center" width="80%">
								<bean:message key="menu.field" />
							</td>
							<td class="TableRow" nowrap align="center" width="15%">
								<span id='maxvalue'><bean:message
										key="jx.khplan.param1.title9" /> </span>
								<span id='maxbili'><bean:message
										key="jx.khplan.param1.title10" /> </span>	
							</td>
						</tr>
						<logic:iterate id="element" name="examPlanForm" property="fine_partRestrict" indexId="index">

							<%
								LazyDynaBean aabean=(LazyDynaBean)pageContext.getAttribute("element");
		     					String point_id=(String)aabean.get("point_id");
									    if (i % 2 == 0)
									    {
							%>
							<tr class="trShallow" id="fine_<%=point_id %>">
								<%
										    } else
										    {
								%>
							
							<tr class="trDeep" id="fine_<%=point_id %>">
								<%
								}
								%>
								<td align="center" class="RecordRow" nowrap>
									<%=++i%>
								</td>
								<td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="pointname" />
								</td>
								<td align="left" class="RecordRow" nowrap>										
									<input type="text"  class="Input_self common_border_color" id="f_<bean:write name="element" property="point_id" />" name="fine_partRestrict[<%=index %>].value"
										value="<bean:write  name="element" property="value"/>" onblur='isNumber(this);testNum(this);' onkeypress="event.returnValue=IsDigit(this);" >
								</td>
							</tr>
						</logic:iterate>
					</table>					
				</td>
			</tr>
			<%}else if(request.getParameter("type").equalsIgnoreCase("badly")){ %>
			<tr id="badly_table">
				<td colspan="3">
					<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTable" >
						<tr>
							<td class="TableRow" nowrap align="center" width="5%">

							</td>
							<td class="TableRow"  align="center" width="80%">
								<bean:message key="menu.field" />
							</td>
							<td class="TableRow" nowrap align="center" width="15%">
								<span id='minvalue'><bean:message
										key="jx.khplan.param1.title11" /> </span>
								<span id='minbili'><bean:message
										key="jx.khplan.param1.title12" /> </span>	
							</td>
						</tr>
						<logic:iterate id="element" name="examPlanForm"
							property="badly_partRestrict" indexId="index">
							<%
								LazyDynaBean aabean=(LazyDynaBean)pageContext.getAttribute("element");
		     					String point_id=(String)aabean.get("point_id");
									    if (j % 2 == 0)
									    {
							%>
							
							<tr class="trShallow" 	id="badly_<%=point_id %>">
								<%
										    } else
										    {
								%>
							
							<tr class="trDeep" 	id="badly_<%=point_id %>">
								<%
								}
								%>
								<td align="center" class="RecordRow" nowrap>
									<%=++j%>
								</td>
								<td align="left" class="RecordRow" nowrap>
									<bean:write name="element" property="pointname" />
								</td>
								<td align="left" class="RecordRow" nowrap>
									<input type="text"  class="Input_self common_border_color" id="b_<%=point_id %>" name = "badly_partRestrict[<%=index %>].value";
										value="<bean:write  name="element" property="value"/>" onblur='isNumber(this);testNum(this);' onkeypress="event.returnValue=IsDigit(this);" >
								</td>
							</tr>
						</logic:iterate>
					</table>
				</td>
			</tr>
			<%} %>
		</table>
	</div>
				</td>
		</tr>
	</table>
	<table width='100%'>
		<tr>
			<td align='center'>
				<%if(request.getParameter("theStatus")!=null && (request.getParameter("theStatus").equals("0") || request.getParameter("theStatus").equals("5"))){%>
				<input type='button'
					value='&nbsp;<bean:message key='button.ok' />&nbsp;'
					class="mybutton" onclick='save();'>
				&nbsp;
					<%}%>
				<input type='button' id="button_cancel"
					value='&nbsp;<bean:message key='button.cancel' />&nbsp;'
					onclick='closewindow();' class="mybutton">
			</td>
		</tr>
	</table>
	<script>
		/* 
		var sum = 0.0;
		var x=1;
		if("${param.type}"=="fine")
		{
			<logic:iterate id="element" name="examPlanForm" property="fine_partRestrict" indexId="index">					
				if(x==1)
				{
					var value = '<bean:write name="element" property="value" />';	
				 	if(value=='' || value=='-1')
			 			value='0';
			 		sum=parseFloat(value);		
				}
				x++;	
			</logic:iterate>
			if(sum>=1 || sum==0)
				document.forms[0].type[1].checked=true;
			else
				document.forms[0].type[0].checked=true;
		}
		else if("${param.type}"=="badly")
		{
			<logic:iterate id="element" name="examPlanForm" property="badly_partRestrict" indexId="index">					
				if(x==1)
				{
					var value = '<bean:write name="element" property="value" />';	
				 	if(value=='' || value=='-1')
			 			value='0';
			 		sum=parseFloat(value);		
				}
				x++;	
			</logic:iterate>
			if(sum>=1 || sum==0)
				document.forms[0].type[1].checked=true;
			else
				document.forms[0].type[0].checked=true;
		}
		*/
		changeTitle(0);
		
		if("${param.wholeEval}"=="true")
		{
			if(document.getElementById('fine_C_whole_grade')!=null)
			document.getElementById('fine_C_whole_grade').style.display='table-row';
			if(document.getElementById('badly_C_whole_grade')!=null)
			document.getElementById('badly_C_whole_grade').style.display='table-row';
		}
		else
		{
		
			if(document.getElementById('fine_C_whole_grade')!=null)
			document.getElementById('fine_C_whole_grade').style.display='none';
			if(document.getElementById('badly_C_whole_grade')!=null)
			document.getElementById('badly_C_whole_grade').style.display='none';
		}
			
		var isBiLi = document.forms[0].accordPVFlag[0].checked;
		if("${param.type}"=="fine")
		{
		<logic:iterate id="element" name="examPlanForm" property="fine_partRestrict" indexId="index">
			var value = '<bean:write name="element" property="value" />';
			if(value=='-1')
				document.getElementById('f_<bean:write name="element" property="point_id" />').value='';
			else if(isBiLi==true && value!='-1')
			{
				document.getElementById('f_<bean:write name="element" property="point_id" />').value=(''+parseFloat(value)*100);
			}
		</logic:iterate>
		}
		else if("${param.type}"=="badly")
		{
		<logic:iterate id="element" name="examPlanForm" property="badly_partRestrict" indexId="index">
			var value = '<bean:write name="element" property="value" />';
			if(value=='-1')
				document.getElementById('b_<bean:write name="element" property="point_id" />').value='';			
			else if(isBiLi==true)
			{			
				document.getElementById('b_<bean:write name="element" property="point_id" />').value=(''+parseFloat(value)*100);
			}
		</logic:iterate>
		}
	</script>
</html:form>


