<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.evaluation.EvaluationForm,
				com.hrms.struts.constant.WebConstant,
				org.apache.commons.beanutils.LazyDynaBean"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>

<script language="JavaScript" src="evaluation.js"></script>
  <script language='javascript'>
  	var info=new Array();
  	if(window.showModalDialog){
  		info=window.parent.dialogArguments;
  	}else {
  		info=parent.parent.dialogArguments;
  	}
  	function save()
  	{
	    var hashvo=new ParameterSet();
		hashvo.setValue("planid",info[2]);
		hashvo.setValue("opt",info[3]);
		hashvo.setValue("object_id",info[4]);
		hashvo.setValue("evalRemark",getEncodeStr(document.getElementById('evalRemark').value));
		var request=new Request({method:'post',asynchronous:false,onSuccess:returnAdjust,functionId:'9024000009'},hashvo);
	            
  	}
  function returnAdjust(outparamters)
  {
		var info=getDecodeStr(outparamters.getValue("info"));
		
		var h = "/performance/evaluation/adjustGrade.jsp?action=modifyMemory";
		h += "&planid=" + dialogArguments[2];
		h += "&object_id=" + dialogArguments[4];
		h += "&info=" + $URL.encode(info.replace("&", "`"));
		
		window.location.href = h;
  }
  
  <%
  	// 修改了等级的同时修改EvaluationForm中考核对象集合对应对象的等级 lium
  	if ("modifyMemory".equals(request.getParameter("action"))) {
  		String planid = request.getParameter("planid");
  		String object_id = request.getParameter("object_id");
  		String info = request.getParameter("info");
  		info = info.replace("`", "&");
  		
  		EvaluationForm myForm = (EvaluationForm) session.getAttribute("evaluationForm");
  		List list = myForm.getSetlist();
  		if (list != null && list.size() > 0) {
  			for (int i = 0, len = list.size(); i < len; i++) {
  				LazyDynaBean bean = (LazyDynaBean) list.get(i);
  				if (bean == null) {
  					continue;
  				}
  				
  				if (object_id.equals(bean.get("object_id")) && info.indexOf("!") < 0) {
  					bean.set("desc", info.substring(0,info.lastIndexOf("&")));
  					break;
  				}
  			}
  		}
  %>
  		var rtValue = {
  			flag: "true",
  			info: "<%=info%>"
  		};
  		if(window.showModalDialog){
            parent.window.returnValue = rtValue;
  		}else {
  			parent.parent.adjustGrade_ok(rtValue);
  		}
  		close_adjust();
  <%
  	}
  %>
  
 function close_adjust() {
	if(!window.showModalDialog){
	  	var win = parent.parent.Ext.getCmp('adjustGrade_win');
 	  	if(win) {
  			win.close();
 	  	}
  	}
  	parent.window.close();
}
    </script>
<html:form action="/performance/evaluation/performanceEvaluation">
		<table border="0" cellspacing="0" align="center" cellpadding="2">
		<tr>
			<td nowrap>
			  <script language='javascript' >
    	document.write(info[1]);
    	
    </script>
		</td>
		</tr>
			<tr>

				<td align="center" nowrap>
					<html:textarea name="evaluationForm" styleId="evalRemark" onkeyup="if(ltrim(rtrim(this.value))!='') document.getElementById('okButton').disabled=false;"
						property="evalRemark" style="width:370;height:250"></html:textarea>
				</td>
			</tr>
		</table>
		<table border="0" cellspacing="0" align="center" cellpadding="2" width="50%" >
			<tr>
				<td align="right" id='aa'>
					<input type="button" class="mybutton" id="okButton" disabled="true"
						value="&nbsp;<bean:message key='button.ok' />&nbsp;"
						onClick="save();" />
				</td>
				<td align="left" id='bb'>
					<input type="button" class="mybutton"
						value="&nbsp;<bean:message key='button.close' />&nbsp;"
						onClick="close_adjust();">
				</td>
			</tr>
		</table>
</html:form>
 <script language='javascript'>
if(info[0]=='1'){
    document.getElementById('aa').style.display='none';
    document.getElementById('bb').align='center';
}
</script>	
	