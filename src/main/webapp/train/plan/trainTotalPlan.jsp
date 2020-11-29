<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>

<script language='javascript'>
 var orgid;
	function sub()
	{
		<% int n=0;  %>
		<logic:iterate  id="element"    name="trainMovementForm"  property="planFieldList" indexId="index"> 
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=n%>=document.getElementsByName("planFieldList[<%=n%>].value")
					if(a<%=n%>[0].value!='')
					{
					 var myReg =/^(-?\d+)(\.\d+)?$/
					 if(!myReg.test(a<%=n%>[0].value)) 
					 {
						alert("<bean:write  name="element" property="itemdesc"/>请输入数字！");
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
							alert("计划名称为必填项！");
							return;						
						}
					}
					
					if(a<%=n%>[0].value!='')
					{
						if(IsOverStrLength(a<%=n%>[0].value,<bean:write  name="element" property="itemlength"/>))
						{
							alert("<bean:write  name="element" property="itemdesc"/>超出了指定的长度范围");
							return;
						}
					}
				
				</logic:equal>
				</logic:equal>
				
		
		
			<% n++; %>
		</logic:iterate>
	    <%
	    String desc="b_save=save";
	    if(request.getParameter("operator")!=null&&request.getParameter("operator").equals("edit"))
	    	desc="b_editPlan=save";
	    %>
	    
	    
		document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?<%=desc%>";
	    document.trainMovementForm.submit();
	
	}
	function returnlist(){
		document.trainMovementForm.action="/train/plan/searchCreatPlanList.do?b_query=query";
	    document.trainMovementForm.submit();
	}
	function getOrgid(){
		orgid=document.getElementById("b0110_value").value;
	}
</script>
<style>
.notop{
	border-top: none;
}
</style>
<html:form action="/train/plan/searchCreatPlanList">
<%
	int i=0;
	int flag=0;
%>

 <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		  <td align="left" class="TableRow">&nbsp;<bean:message key="train.plan.new.train.plan"/>&nbsp;</td>         	      
          </tr> 
          <tr>
            <td>
<%
int nn=1;
%>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<logic:iterate  id="element"    name="trainMovementForm"  property="planFieldList" indexId="index"> 
		<%
		String className="RecordRow notop noleft noright"; 
		if(i%2==0)
		{
			
			
			if(flag!=0)
				out.print("</tr>");
			out.print("<tr>");
			nn++;
			
		}
		i++;
		flag++;
		if(i%2==0)
		    className="RecordRow notop noleft"; 
		%>
		   
	   <logic:equal name="element" property="itemtype" value="A">
				<logic:equal name="element" property="codesetid" value="0">
					<td align="right" class="RecordRow notop noright" nowrap>
						<bean:write  name="element" property="itemdesc"/>
					</td>
					
					<logic:equal name="element" property="itemid" value="r2501"> 
						<td align="left" class="<%=className%>" nowrap>
						&nbsp;<input class="textColorWrite" type="text" name="<%="planFieldList["+index+"].value"%>"   value="<bean:write  name="element" property="value"/>"  readonly="true"  maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
						</td>
					</logic:equal>
					<logic:notEqual name="element" property="itemid" value="r2501">
						<td align="left" class="<%=className%>" nowrap>
						&nbsp;<input class="textColorWrite" type="text" name="<%="planFieldList["+index+"].value"%>"   value="<bean:write  name="element" property="value"/>"   maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
						</td>
					</logic:notEqual>
					
				</logic:equal>
				<logic:notEqual name="element" property="codesetid" value="0">
					<td align="right" class="RecordRow notop noright" nowrap>
						<bean:write  name="element" property="itemdesc"/>
					</td>
					<td align="left" class="<%=className%>" nowrap>
					<logic:equal name="element" property="itemid" value="b0110">
                          <input type='hidden' name='<%="planFieldList["+index+"].value"%>' id="b0110_value" value="<bean:write  name="element" property="value"/>"   />  
						  &nbsp;<input type='text' class="textColorRead" name='<%="planFieldList["+index+"].viewvalue"%>'  value="<bean:write  name="element" property="viewvalue"/>"   readonly="true"   /> 
						<logic:notEqual name="element" property="codesetid" value="16">
							<img src="/images/code.gif" id='img<bean:write  name="element" property="itemid"/>' onclick='openInputCodeDialogOrgInputPos("<bean:write  name="element" property="codesetid"/>","<%="planFieldList[" + index + "].viewvalue"%>","${trainMovementForm.orgparentcode }","1");' align="absmiddle"/>		
						</logic:notEqual>          
                    </logic:equal>         
                    <logic:equal name="element" property="itemid" value="e0122">
							 <input type='hidden' name='<%="planFieldList["+index+"].value"%>'    value="<bean:write  name="element" property="value"/>"   />  
							 &nbsp;<input type='text' class="textColorRead" name='<%="planFieldList["+index+"].viewvalue"%>'  value="<bean:write  name="element" property="viewvalue"/>"   readonly="true"   /> 
						<logic:notEqual name="element" property="codesetid" value="16">
							<img src="/images/code.gif" id='imge0122' onclick='getOrgid();openInputCodeDialogOrgInputPos("<bean:write  name="element" property="codesetid"/>","<%="planFieldList[" + index + "].viewvalue"%>",orgid,"2");' align="absmiddle"/>		
						</logic:notEqual>          
                    </logic:equal> 
                    <logic:notEqual name="element" property="itemid" value="b0110">
	                    <logic:notEqual name="element" property="itemid" value="e0122">
		                    <input type='hidden' name='<%="planFieldList["+index+"].value"%>'    value="<bean:write  name="element" property="value"/>"   />  
					              &nbsp;<input type='text' class="textColorRead" name='<%="planFieldList["+index+"].viewvalue"%>'  value="<bean:write  name="element" property="viewvalue"/>"   readonly="true"   /> 
					        <logic:notEqual name="element" property="codesetid" value="16">
					              <img align="absmiddle" src="/images/code.gif" onclick='javascript:openInputCodeDialog("<bean:write  name="element" property="codesetid"/>","<%="planFieldList["+index+"].viewvalue"%>");'/>&nbsp;		
							</logic:notEqual>
						 </logic:notEqual> 
					 </logic:notEqual> 
                     </td>
				</logic:notEqual>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="D">
				<td align="right" class="RecordRow notop noright" nowrap>
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left" class="<%=className%>" nowrap>
				       
					&nbsp;<input class="textColorWrite" type="text"  name="<%="planFieldList["+index+"].value"%>"  value="<bean:write  name="element" property="value"/>"   onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' readOnly     maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
			    
			    </td>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="M">
				<td align="right" class="RecordRow notop noright" nowrap>
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left" class="<%=className%>" nowrap>
					&nbsp;<input class="textColorWrite" type="text"  name="<%="planFieldList["+index+"].value"%>"   maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
				</td>
		</logic:equal>
		<logic:equal name="element" property="itemtype" value="N">
				<td align="right" class="RecordRow notop noright" nowrap>
						<bean:write  name="element" property="itemdesc"/>
				</td>
				<td align="left" class="<%=className%>" nowrap>
					&nbsp;<input class="textColorWrite" type="text"  name="<%="planFieldList["+index+"].value"%>"  value="<bean:write  name="element" property="value"/>"    maxlength="<bean:write  name="element" property="itemlength"/>"   />&nbsp;&nbsp;&nbsp;&nbsp;   
				</td>
		</logic:equal>
	</logic:iterate>
	</tr>
</table>
</td>
</tr>      
</table>

<Input type='hidden' name='selectIDs' value="${trainMovementForm.selectIDs}"/>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
		<td align="left" style="padding-top: 5px;">
			<Input type='reset' name='reseta' value="<bean:message key='options.reset'/>" class="mybutton" >
			<Input type='button' name='buttona' value="<bean:message key='reporttypelist.confirm'/>" class="mybutton" onclick="sub()" >
			<Input type='button' name='button1a' value="<bean:message key='reporttypelist.cancel'/>" class="mybutton" onclick="returnlist();" >
		</td>
	</tr>
</table>
</html:form>