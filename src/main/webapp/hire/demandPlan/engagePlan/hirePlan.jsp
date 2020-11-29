<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>

<script language='javascript'>

	function sub()
	{
		<% int n=0;  %>
		<logic:iterate  id="element"    name="engagePlanForm"  property="planFieldList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=n%>=document.getElementsByName("planFieldList[<%=n%>].value")
					if(a<%=n%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=n%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
						return;
					 }
					 if(a<%=n%>[0].value<0)
					 {
					 	alert("<bean:write  name="element" property="itemdesc"/>"+INPUT_POSITIVE_VALUE+"！");
					 	return;
					 }
					 
					 }
				</logic:equal>
				
				<logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					var a<%=n%>=document.getElementsByName("planFieldList[<%=n%>].value")
					var item<%=n%>='<bean:write  name="element" property="itemid"/>';
					if(item<%=n%>=='r2502')
					{
						if(a<%=n%>[0].value=='')
						{
							alert(PLANNAME_IS_MUST_FILL+"！");
							return;						
						}
					}
					
					if(a<%=n%>[0].value!='')
					{
						if(IsOverStrLength(a<%=n%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
							return;
						}
					}
				
				</logic:equal>
				</logic:equal>
				
		
		
			<% n++; %>
		</logic:iterate>
		
	    var origin='<%=(request.getParameter("origin"))%>';
		if(origin=='a')
			engagePlanForm.action="/hire/demandPlan/engagePlan.do?b_add=add&origin=a&operateType=<%=(request.getParameter("operateType"))%>";	
		else
			engagePlanForm.action="/hire/demandPlan/engagePlan.do?b_add2=add2&origin=b";
		engagePlanForm.submit();
	
	}
	
	
	function goback()
	{
		var origin='<%=(request.getParameter("origin"))%>';
		if(origin=='a')
		{
			engagePlanForm.action="/hire/demandPlan/positionDemand/positionDemandTree.do?b_query2=link&model=2";
			engagePlanForm.submit();
		}
		else
			history.go(-1)
	
	}
    function setTPinput(){
	    var InputObject=document.getElementsByTagName("input");
	    for(var i=0;i<InputObject.length;i++){
	        var InputType=InputObject[i].getAttribute("type");
	        if(InputType!=null&&(InputType=="text"||InputType=="password")){
	            InputObject[i].className=" "+"TEXT4";
	        }
	    }
    }
</script>
<style>
<!--
.TableRow_b {
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
-->
</style>
<hrms:themes></hrms:themes>
<body onload="setTPinput()">
<html:form action="/hire/demandPlan/engagePlan">
<%
	int i=0;
	int flag=0;
%>
<br><br>

<% String className="trShallow"; 
   String className2="trDeep";
%>



 <table width="700" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:42px;">
          <tr height="20">
       		<td class="TableRow_b common_background_color common_border_color">
       		<%if(request.getParameter("editopt")!=null&&request.getParameter("editopt").equals("1")){%>
       		&nbsp;<bean:message key="hire.edit.hireplan"/>
       		<%}else{ %>
       		&nbsp;<bean:message key="hire.new.hireplan"/>
       		<%} %>
       		</td>              	      
          </tr> 
          <tr>
            <td  class="framestyle"  >
            	<br>



<table width="90%" border="0" cellspacing="2"  align="center" cellpadding="2">
	<logic:iterate  id="element"    name="engagePlanForm"  property="planFieldList" indexId="index"> 
		<%
		if(i%2==0)
		{
			if(flag!=0)
				out.print("</tr>");
			out.print("<tr>");
		}
		i++;
		flag++;
		%>
		   
	   <logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					<td align="right" class="<%=className2%>" nowrap valign="center">
						<bean:write  name="element" property="itemdesc"/>
					</td>
					
					<logic:equal name="element" property="operator" value="0">
						<td align="left"  class="<%=className%>"  nowrap valign="center">
						&nbsp;<input type="text" name="<%="planFieldList["+index+"].value"%>"   value="<bean:write  name="element" property="value"/>"  disabled="true"  maxlength="<bean:write  name="element" property="itemlength"/>"   />
						</td>
					</logic:equal>
					<logic:notEqual name="element" property="operator" value="0">
						<td align="left"  class="<%=className%>"  nowrap valign="center">
						&nbsp;<input type="text" name="<%="planFieldList["+index+"].value"%>"   value="<bean:write  name="element" property="value"/>"   maxlength="<bean:write  name="element" property="itemlength"/>"   />
						   <logic:equal name="element" property="itemid" value="z0103">
						   <span style="color: red;">*</span>
						   </logic:equal>
						</td>
					</logic:notEqual>
					
				</logic:equal>
				<logic:notEqual name="element" property="codesetid" value="0">
					<td align="right" class="<%=className2%>" nowrap valign="center">
						<bean:write  name="element" property="itemdesc"/>
					</td>
					<td align="left"  class="<%=className%>"  nowrap valign="center">
						 <input type='hidden' name='<%="planFieldList["+index+"].value"%>'    value="<bean:write  name="element" property="value"/>"   />  
			              &nbsp;<input type='text' name='<%="planFieldList["+index+"].viewvalue"%>'  value="<bean:write  name="element" property="viewvalue"/>" 
			             <logic:equal name="element" property="operator" value="0"> 
			             	disabled 
			             </logic:equal>
			             <logic:equal name="element" property="operator" value="1">
			             	readonly="true"   /> 
			              <span/><img  src="/images/code.gif" style="margin:0,0,-2,0; position:relative;top:5px;" onclick='javascript:openInputCodeDialog("<bean:write  name="element" property="codesetid"/>","<%="planFieldList["+index+"].viewvalue"%>");'/></span/>		
					     </logic:equal>
					     <logic:equal name="element" property="itemid" value="z0105">
						   <span style="color: red;">*</span>
						 </logic:equal>
						 <logic:equal name="element" property="itemid" value="z0127">
						   <span style="color: red;">*</span>
						 </logic:equal>
					</td>
				</logic:notEqual>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="D">
				<td align="right"  class="<%=className2%>"  nowrap valign="center">
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left"  class="<%=className%>"  nowrap valign="center">
				       
					&nbsp;<input type="text"  name="<%="planFieldList["+index+"].value"%>"  value="<bean:write  name="element" property="value"/>"
					 <logic:equal name="element" property="operator" value="1">  onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' readOnly </logic:equal> 
					 <logic:equal name="element" property="operator" value="0">  disabled </logic:equal> 
					   maxlength="<bean:write  name="element" property="itemlength"/>"   />
			         <logic:equal name="element" property="itemid" value="z0107">
						 <span style="color: red;">*</span>
					 </logic:equal>
					 <logic:equal name="element" property="itemid" value="z0109">
						 <span style="color: red;">*</span>
					 </logic:equal>
			    </td>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="M">
				<td align="right"  class="<%=className2%>"  nowrap valign="center">
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left"  class="<%=className%>"  nowrap valign="center">
					&nbsp;<input type="text"  name="<%="planFieldList["+index+"].value"%>"   maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
				</td>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="N">
				<td align="right"  class="<%=className2%>"  nowrap valign="center">
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left"  class="<%=className%>"  nowrap valign="center">
					&nbsp;<input type="text"  name="<%="planFieldList["+index+"].value"%>"  value="<bean:write  name="element" property="value"/>"    maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
				</td>
		</logic:equal>
		
		
	</logic:iterate>
	</tr>
</table>
<br>&nbsp;
</td></tr></table>


<%
	String selectID="";
	if(request.getParameter("selectID")!=null)
		selectID=request.getParameter("selectID");
	
%>
<Input type='hidden' name='selectID' value='<%=selectID%>'/>
	<br>
			
			<table  width="700"  border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:-12px;">
				<tr>
					<td align="center">
		<Input type='button' name='a' value="<bean:message key="button.ok"/>" class="mybutton" onclick="sub()">			
		<Input type='reset' name='a' value="<bean:message key="button.clear"/>" class="mybutton" >
		<Input type='button' name='a' value="<bean:message key="button.return"/>" class="mybutton" onclick="goback()">
					</td>
				</tr>
			</table>
			<br>

			 </td>
          </tr>      
              
      </table>

</html:form>
</body>