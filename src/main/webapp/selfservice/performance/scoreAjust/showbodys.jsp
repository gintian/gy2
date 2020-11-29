<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<style>
.myfixedDiv
{ 
	overflow:auto; 
	/* height:expression(document.body.clientHeight-100);
	width:expression(document.body.clientWidth-10);  */
	BORDER-BOTTOM: #94B6E6 0pt solid; 
    BORDER-LEFT: #94B6E6 0pt solid; 
    BORDER-RIGHT: #94B6E6 0pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
</style>
<script>
	function closeWin(){
		if(window.showModalDialog){
			window.close();
		}else{
			parent.parent.Ext.getCmp("returnMarkWin").close();
		}
	}
	function set()
	{
		var mainBodyIDs=new Array();
  		var tablevos=document.getElementsByTagName("input");
  		var flag=false;
		for(var i=0;i<tablevos.length;i++)  
		{
	   		if(tablevos[i].type=="checkbox" && tablevos[i].checked==true && tablevos[i].name!='selbox')	    
	 		{
	    		var theVal = tablevos[i].value;	    	
	    		mainBodyIDs.push(theVal);
	    	}
   		}

   		if(mainBodyIDs.length>0)
   		{
   			if(confirm('您确定要退回此考核主体吗？'))
   			{
   				
   				if(window.showModalDialog){
   					window.returnValue=	mainBodyIDs;
   				}else{
   					parent.parent.returnMark_ok(mainBodyIDs);
   				}
   			}  			
   		}
   		else{  			
  			alert("请选择要退回的考核主体!");
  			return;  		
   		}
   		closeWin();
	}
</script>
<html:form action="/selfservice/performance/scoreAjust">	
	<table width="100%" border="0" cellspacing="1" align="center"
			cellpadding="1" class="ListTable">
			<tr>
				<td class="RecordRow" align="left">
					请选择要退回的考核主体：
				</td>
			</tr>
			<tr>
				<td nowrap class="RecordRow" style='border-top:0pt;'>
					<div id='myfixedDiv' style='padding-top:5px;' class="myfixedDiv">
						<table width="100%" border="0" cellspacing="0" align="center"
							cellpadding="0" class="ListTableF">
							<%
							int i = 0;
							%>

						<tr class="fixedHeaderTr">
								<td align="center" class="TableRow" nowrap>
									<input type="checkbox" name="selbox"
										onclick="batch_select(this, 'mainbodys');">
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="tree.unroot.undesc" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="label.codeitemid.um" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="label.codeitemid.kk" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="lable.performance.perMainBody" />
								</td>
								<td align="center" class="TableRow" nowrap>
									<bean:message key="jx.evaluation.evaluation" /><bean:message key="org.performance.status" />
								</td>
							</tr>

							<logic:iterate id="element" name="scoreAjustForm"
								property="mainBodyList">
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
									<td align="center" class="RecordRow" nowrap>
										<input type='checkbox' name='mainbodys<%=i%>'
											value='<bean:write name="element" property="mainbody_id" filter="true"/>' />
									</td>
									<td  class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="b0110" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="e0122" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="e01a1" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="a0101" filter="true" />
									</td>
									<td  class="RecordRow" nowrap>
										&nbsp;<bean:write name="element" property="status"
											filter="true" />
									</td>
								</tr>
							</logic:iterate>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<table width="100%" >
			<tr>
				<td style="height:35px" align="center">
					<input type='button' class="mybutton" name="b_ok"
						onclick='set()'
						value='<bean:message key="button.ok"/>' />
					<input type='button' class="mybutton" name="b_close"
						onclick='closeWin();'
						value='<bean:message key="button.cancel"/>' />
				

				</td>
			</tr>
		</table>
</html:form>
<script type="text/javascript">
var myfixedDiv = document.getElementById("myfixedDiv");
if(myfixedDiv){
	myfixedDiv.style.width=(document.body.clientWidth-40)+'px';
	myfixedDiv.style.height = (document.body.clientHeight-110)+'px';
}
	
</script>