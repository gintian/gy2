<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.implement.DataGatherForm
				 "%>
<html>

<style>

div#treemenu 
{
	BORDER-BOTTOM:#C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid; 
	width: 430px;
	height: 230px;
	overflow: auto;
}

</style>

<script language='javascript'>
		
function enter()
{
	var s =document.getElementById("vote").value;
			
	var sd = document.getElementById("aa").innerHTML;

	if(s.length==0)
	{
		alert(P_I_INFO7+"!");
	}
	var r =isNaN(s);
	if(r)
	{
		alert(P_I_INFO8+"!")
		return;
	}
	if(eval(s)<eval(sd))
	{
		alert(P_I_INFO9+"!");
		return;
    }
            
    if(s.length>0)
    {
    	var url = "/performance/implement/dataGather.do?b_vote=link&total="+s;
		window.location.href=url;
		return;
    }
          
}
		
</script>

	<body>
		<html:form action="/performance/implement/dataGather">
			<table width='430px;' align="center">
				<tr>
					<td align="left">
						<bean:message key="lable.performance.totalticketnum"/> &nbsp;
						<html:text property="vote" name="dataGatherForm" styleClass="inputtext"/>
					</td>
				</tr>
				<tr class="trShallow">
					<td width='100%' align="center">
						<div id='treemenu'>
							<table width="100%" border="0" cellspacing="0" align="center"
								cellpadding="0" class="ListTable">
								<thead>
									<tr>
										<td align="center" class="TableRow" style="border:0px;" width='155px' nowrap>
											
										</td>
										<td align="center" class="TableRow" style="border-top:0px;" width='120px' nowrap>
											<bean:message key="lable.welcome.invtextresult.ballot"/>
										</td>
										<td align="center" class="TableRow" style="border:0px;" width='120px' nowrap>
											<bean:message key="train.evaluationStencil.percent"/>
										</td>
									</tr>
								</thead>								
								<logic:iterate id="element" name="dataGatherForm"
									property="fullvoteList">
									<tr class="trDeep" nowrap>
										<td class="RecordRow" style="border-left:0px;" nowrap>
											&nbsp;<bean:write name="element" property="name" />
										</td>
										<td id="aa" align="right" class="RecordRow" nowrap>
											<bean:write name="element" property="vote" />
										</td>
										<td align="right" style="border-right:0px;" class="RecordRow" nowrap>
											<bean:write name="element" property="bl" />
										</td>
									</tr>
								</logic:iterate>
							</table>
						</div>
					</td>
				</tr>
				<tr>
					<td width='100%' valign='top' align="center" style="height:35px">  
						
						<input type='button' value='<bean:message key="kq.wizard.stas"/>' onclick='enter()'
							class="mybutton">
						<input type='button' value='<bean:message key="lable.welcomeboard.close"/>'
							onclick='javascript:window.close()' class="mybutton">
					</td>
				</tr>
			</table>
		</html:form>
	</body>
</html>
