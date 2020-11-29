<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc" %>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript">
	document.title="评估记录";
	function outContent(id){
		var content = document.getElementById(id).value;
		Tip(content,STICKY,true);
		return true;
	}
	
	function showQuestionnaire(id)
	{
	  if(""==id)
	  {
	     alert("此培训班未设置教师培训效果调查问卷！");
	     return;
	  }
    var target_url = "/selfservice/infomanager/askinv/searchendview.do?b_query=link&f=1&id=" + id    
    window.showModalDialog(target_url, "memoFld_win", 
                "dialogWidth:1200px; dialogHeight:1000px;resizable:yes;center:yes;scroll:yes;status:no");  
	}
	
	function showKhResult(classid,templetid)
	{
	  if(""==templetid)
    {
       alert("此培训班未设置教师培训评估模板！");
       return;
    }
    var target_url = "/train/evaluatingStencil.do?b_analyse=link&r3101=" + classid + "&templateid=" + templetid;    
    window.showModalDialog(target_url, "memoFld_win", 
                "dialogWidth:1200px; dialogHeight:1000px;resizable:yes;center:yes;scroll:yes;status:no");
	}
	
</script>
<style>
.div{
	width: 585px;
	height: 300px;
	border: 1px solid #C4D8EE;
	margin-bottom: 8px;
	overflow: auto;
	margin-right: 5px;
}

</style>
<hrms:themes/>
<html:form action="/train/trainCosts/trainAssess">
	<center>
		<div class="div common_border_color">
			<table border="0" cellspacing="0" align="center" cellpadding="0"
				class="ListTable" width="100%">
				<tr>
					<td align="center" width="25%" class="TableRow" style="border-top: 0px;border-left: 0px;padding:0,5,0,5px; " nowrap>培训班名称</td>
					<td align="center" width="50%" class="TableRow" style="border-top: 0px;border-right: 0px; padding:0,5,0,5px; " nowrap>资源评估结果</td>
					<logic:equal name="trainCostsForm" property="recTab" value="r04">
					  <td align="center" width="15%" class="TableRow" style="border-top: 0px;border-right: 0px; padding:0,5,0,5px; " nowrap>效果调查问卷</td>
					  <td align="center" width="10%" class="TableRow" style="border-top: 0px;border-right: 0px; padding:0,5,0,5px; " nowrap>考核量表</td>
					</logic:equal>
				</tr>
				<logic:iterate id="bean" name="trainCostsForm" property="assessList" indexId="i">
				<bean:define id="strvalue" name="bean" property="r3704" type="java.lang.String" />
					<tr>
						<td class="RecordRow" style="border-left: 0px;" nowrap>&nbsp;<bean:write name="bean" property="r3130" filter="false"/>&nbsp;</td>
						<td class="RecordRow noright" onmouseout="UnTip();" onmouseover="outContent('svalue${i }');" nowrap>
							<input type="hidden" id="svalue${i }" value="${strvalue }">
							&nbsp;<%=strvalue.length()>30?strvalue.substring(0,30)+"...":strvalue %>&nbsp;
						</td>
						<%
						  LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("bean");
						  String mdquestionnaire=PubFunc.encryption((String)abean.get("questionnaire"));
						 %>
						<logic:equal name="trainCostsForm" property="recTab" value="r04">
							<td class="RecordRow" align="center" nowrap>
							<logic:notEmpty name="bean" property="questionnaire">
								 <img src="/images/view.gif" BORDER="0" style="cursor:hand;" 
							    onclick='showQuestionnaire("<%=mdquestionnaire%>");' alt="效果调查问卷"/>						
							</logic:notEmpty>
							</td>
							<td class="RecordRow noright" style="border-left: 0px;" align="center" nowrap>
							<logic:notEmpty name="bean" property="template">
			              <img src="/images/view.gif" BORDER="0" style="cursor:hand;" 
			                onclick='showKhResult("<bean:write name="bean" property="classid" filter="false"/>","<bean:write name="bean" property="template" filter="false"/>");' alt="考核量表"/>            
				           	</logic:notEmpty>
				            </td>
            			</logic:equal>
					</tr>
				</logic:iterate>
			</table>
		</div>
		<input type="button" value="关闭" class="mybutton"
			onclick="javascript:window.close();" />
	</center>
</html:form>