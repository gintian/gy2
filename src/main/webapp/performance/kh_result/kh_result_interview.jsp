<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">

function browseInterview(id,template_id,object_id,plan_id,ins_id,taskid)
{
	if(template_id=='-1')
	{
	   var strurl="/performance/kh_result/kh_result_interview.do?b_browse=browse`ID="+id;
	     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
//	   var values= window.showModalDialog(iframe_url,null,
//			        "dialogWidth:650px; dialogHeight:5760px;resizable:yes;center:yes;scroll:yes;status:no");
	   var config = {
		    width:570,
		    height:490,
		    type:'3',
           title:'谈话记录',
           id:'kh_result_interview_window'
		}

		modalDialog.showModalDialogs(iframe_url,"browseInterview_win",config,"");
	}
	else
	{
	
	    //window.open("/general/template/edit_form.do?b_query=link&tabid="+template_id+"&ins_id="+ins_id+"&businessModel=2&sp_flag=2&returnflag="+template_id,"_ad","top=0,left=5,height="+(window.screen.height-70)+",width="+(window.screen.width-20));
	     var strurl="/general/template/edit_form.do?b_query=link`tabid="+template_id+"`taskid="+taskid+"`ins_id="+ins_id+"`businessModel=2`sp_flag=2`returnflag="+template_id;
	     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(strurl);
	     //var values= window.showModalDialog(iframe_url,null, 
		//	        "dialogWidth:"+(window.screen.width-20)+"px; dialogHeight:"+(window.screen.height-70)+"px;resizable:yes;center:yes;scroll:yes;status:no");
	    
	     var config = {
 		    width:(window.screen.width-20),
 		    height:(window.screen.height-70),
 		    type:'1'
 		 }

 		 modalDialog.showModalDialogs(iframe_url,"browseInterview_win",config,"");
	}
}

function closeWindow() {
    Ext.getCmp('kh_result_interview_window').close();
}
</script>
<html:form action="/performance/kh_result/kh_result_interview">
<table width="90%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
<thead>
<tr>
<td align="center" class="TableRow">
&nbsp;&nbsp;序号&nbsp;&nbsp;
</td>
<td align="center" class="TableRow">
&nbsp;&nbsp;计划名称&nbsp;&nbsp;
</td>
<td align="center" class="TableRow">
&nbsp;&nbsp;面谈人&nbsp;&nbsp;
</td>
<td align="center" class="TableRow">
&nbsp;&nbsp;面谈时间&nbsp;&nbsp;
</td>
<td align="center" class="TableRow">
&nbsp;&nbsp;面谈记录&nbsp;&nbsp;
</td>
</tr>
</thead>
<% int i=0; %>
<logic:iterate id="element" name="khResultForm" property="interviewList" indexId="index">
<tr>
<td align="center" class="RecordRow" nowrap>
<%=(i+1)%>
</td>
<td class="RecordRow" align="left" nowrap>
&nbsp;<bean:write name="element" property="name" filter="true"/>
</td>
<td class="RecordRow" align="left" nowrap>
&nbsp;<bean:write name="element" property="mainbodyid" filter="true"/>
</td>
<td class="RecordRow" align="left" nowrap>
&nbsp;<bean:write name="element" property="create_date" filter="true"/>
</td>
<td class="RecordRow" align="center" nowrap>
<a href='javascript:browseInterview("<bean:write name="element" property="id"/>","${khResultForm.templet_id}","<bean:write name="element" property="object_id"/>","<bean:write name="element" property="plan_id"/>","<bean:write name="element" property="ins_id"/>","<bean:write name="element" property="task_id"/>");'>查看</a>
</td>
</tr>
<% i++; %>
</logic:iterate>
</table>
</html:form>