<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript">
<% 
if(request.getParameter("b_save")!=null&&request.getParameter("b_save").equals("query")){
	
	out.print("var thevo=new Object(); thevo.flag='true'; thevo.objids='" + request.getParameter("objectIDs") + "'; thevo.order=' order by score desc';");
	out.print("if(window.showModalDialog){");
	out.print(" window.returnValue=thevo"); 
	out.print("}else{");
	out.print(" parent.window.opener.generateRemark_ok(thevo)");
	out.print("}");
	out.print(" parent.window.close();");
}
%>
function creatTemplate(template)
{
	if(template=='new')
	{
		var theurl="/performance/options/perParamList.do?b_query=link`from_eval=1";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
        //var vo= window.showModalDialog(iframe_url, 'template_win', 
		//	"dialogWidth:800px; dialogHeight:600px;resizable:yes;center:yes;scroll:yes;status:no;minimize:yes;maximize:yes;");
        var config = {
       	    width:800,
       	    height:600,
       	    type:'2',
       	    id:'creatTemplate_win'
       	}

       	modalDialog.showModalDialogs(iframe_url,"creatTemplate_win",config,creatTemplate_ok);
	}
}
function creatTemplate_ok() {
	evaluationForm.action="/performance/evaluation/dealWithBusiness/generateRemark.do?b_query=link&planid=${param.planid}";     
	evaluationForm.submit(); 
}
var objectIDs = "";
function generate(planid)
{
	var template=$F('remark');
  	var tablevos=document.getElementsByTagName("input");
		for(var i=0;i<tablevos.length;i++)  
		{
	  	  if(tablevos[i].type=="checkbox" && tablevos[i].name!='selbox' &&  tablevos[i].checked==true)	    
	 	  {
	    		objectIDs +="@"+tablevos[i].value;	    	
		  }
   		}	
   	if(objectIDs=="")
   	{
   		alert(KH_REMARK_INFO1);
   		return;
   	}
   	if(template=='none' || template=='new')
   	{
   		alert(KH_REMARK_INFO2);
   		return;
   	}
	document.evaluationForm.action="/performance/evaluation/dealWithBusiness/generateRemark.do?b_save=query&planid="+planid+"&objectIDs="+objectIDs.substring(1)+"&template="+template;
	document.evaluationForm.submit();
}
window.onload=function() {
	var fixed = document.getElementsByTagName("DIV");
	for (var i = 0; i < fixed.length; i++) {
		if (fixed[i].className.indexOf("myfixedDiv") > -1) {
			fixed[i].style.height = document.body.clientHeight-100;
			fixed[i].style.width = document.body.clientWidth-15;
		}
	}
}
</script>
<style>
.myfixedDiv
{ 
	overflow:auto; 
 	height:440px; 
 	width:530px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
 <hrms:themes />
<html:form action="/performance/evaluation/dealWithBusiness/generateRemark">
	<table  width="525px;" style="margin-left:-2px;">
		<tr>
			<td>
	<div class="myfixedDiv common_border_color">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<%
				int i = 0;
				%>

				<tr class="fixedHeaderTr">
					<td align="center" class="TableRow" nowrap width="40" style="border-top:0; border-left:0;">
						<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'obj');">
					</td>
			 <logic:equal name="evaluationForm" property="object_type"  value="1">  
         			<td align="center" class="TableRow" nowrap style="border-top:0; border-right:0;">
						<bean:message key="org.performance.unorum"/>    
					</td>
             </logic:equal>   
             <logic:equal name="evaluationForm" property="object_type"  value="2">  
					<td align="center" class="TableRow" nowrap style="border-top:0;">
						<bean:message key="tree.unroot.undesc" />
					</td>
					<td align="center" class="TableRow" nowrap style="border-top:0;">
						<bean:message key="tree.umroot.umdesc" />
					</td>
					<td align="center" class="TableRow" nowrap style="border-top:0;">
						<bean:message key="label.codeitemid.kk" />
					</td>
					<td align="center" class="TableRow" nowrap style="border-top:0; border-right:0;">
						<bean:message key="hire.employActualize.name" />
					</td>
			 </logic:equal>  	
			 <logic:equal name="evaluationForm" property="object_type"  value="3">  
					<td align="center" class="TableRow" nowrap style="border-top:0; border-right:0;">
						<bean:message key="tree.unroot.undesc" />
					</td>					
			 </logic:equal>  
			 <logic:equal name="evaluationForm" property="object_type"  value="4">  
					<td align="center" class="TableRow" nowrap style="border-top:0;">
						<bean:message key="tree.unroot.undesc" />
					</td>
					<td align="center" class="TableRow" nowrap style="border-top:0; border-right:0;">
						<bean:message key="tree.umroot.umdesc" />
					</td>					
			 </logic:equal>  	
				</tr>
			</thead>
			<logic:iterate id="element" name="evaluationForm"
				property="currentObjList">
				<%
						if (i % 2 == 0)
						{
				%>
				<tr class="trShallow">
					<%
							} else
							{
					%>
				
				<tr class="trDeep">
					<%
							}
							i++;
					%>
					<td align="center" class="RecordRow" nowrap style="border-left:0;">
						<input type='checkbox' name='obj<%=i%>'
							value='<bean:write name="element" property="object_id" filter="true"/>' />
					</td>
				<logic:equal name="evaluationForm" property="object_type" value="1">  
         			<td align="left" class="RecordRow" nowrap style="border-right:0;">
						&nbsp;<bean:write name="element" property="a0101" filter="true" />   
					</td>
                </logic:equal>   
                <logic:equal name="evaluationForm" property="object_type" value="2">  
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="b0110" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="e0122" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="e01a1" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap style="border-right:0;">
						&nbsp;<bean:write name="element" property="a0101" filter="true" />
					</td>
				</logic:equal>   
				 <logic:equal name="evaluationForm" property="object_type" value="3">  
					<td align="left" class="RecordRow" nowrap style="border-right:0;">
						&nbsp;<bean:write name="element" property="b0110" filter="true" />
					</td>
				</logic:equal> 	
			    <logic:equal name="evaluationForm" property="object_type" value="4">  
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="b0110" filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap style="border-right:0;">
						&nbsp;<bean:write name="element" property="e0122" filter="true" />
					</td>
				</logic:equal> 
				</tr>
			</logic:iterate>
		</table>
	</div>
		</td>
		</tr>
		</table>

	<table width='530px;' align='center'>
		<tr>
			<td align='left'>
				<bean:message key='jx.eval.remarkTemplate' />
				<html:select name="evaluationForm" property="remark" size="1"
						onchange="creatTemplate(this.value);">
						<html:option value="none">
							&nbsp;
						</html:option>
					
						<html:optionsCollection property="remarkTemplates" value="dataValue"
							label="dataName" />
								<html:option value="new">
							<bean:message key="lable.tz_template.new" />
						</html:option>
					</html:select>
					&nbsp;
				<input type='button'
					value='&nbsp;<bean:message key='hmuster.label.excecute' />&nbsp;'
					class="mybutton"
					onclick='generate("${param.planid}")'>

				<input type='button' id="button_goback"
					value='&nbsp;<bean:message key='button.cancel' />&nbsp;'
					onclick='parent.window.close();' class="mybutton">
			</td>
		</tr>
	</table>
</html:form>
