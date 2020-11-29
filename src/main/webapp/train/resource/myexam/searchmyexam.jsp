<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.hjsj.hrms.actionform.train.trainexam.exam.MyExamForm"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.businessobject.train.trainexam.exam.mytest.MyTestBo" %>
<%
MyExamForm myExamForm = (MyExamForm)session.getAttribute("myExamForm");
HashMap map = myExamForm.getTimesmap();
UserView userView = (UserView) session.getAttribute(WebConstant.userView); %>
<style>
body{text-align: center;}
.myfixedDiv
{  
	overflow:auto; 
	height:expression(document.body.clientHeight-140);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
.noleft{
	border-left-width:0px;
}
.noright{
	border-right-width:0px;
}
.mytop{border-top: none;}
</style>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>
<script type="text/javascript">
<!--
	// 条件查询
	function exchange() {
		var form1 = document.getElementById("form1");
		form1.action = "/train/resource/myexam.do?b_query=link";
		form1.submit();
	}
	
	// enter键
	function keDownEv (event) {
		if (event.keyCode == 13) {
			exchange();
		}
	}
	
	// 考试,type 1为整版，2为单题
	function test(type,r5300,r5400) {
		map = new HashMap();
		map.put("type",type+"");
		map.put("r5300",r5300+"");
		map.put("r5400",r5400+"");
		Rpc({functionId:'2020030191',success:checkSucc},map);
		
	}
	
	
	function checkSucc(response) {
		var value=response.responseText;
		map=Ext.decode(value);
		if (getDecodeStr(map.biaozhi) == "ok") {
			var url = "";
			if (map.type == 1) {// 整版考试
				url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+map.r5300+"&exam_type=2&flag=2&returnId=2&paperState=0&plan_id="+map.r5400;
			} else {// 单题考试
				url = "/train/trainexam/paper/preview/paperspreview.do?b_single=link&r5300="+map.r5300+"&current=1&exam_type=2&flag=2&returnId=2&paperState=0&paper_id="+map.r5400;
			}
			
			//var form1 = document.getElementById("form1");
			//form1.action = url;
			//form1.submit();
			try {
			var newwindow = window.open(url,"myexam","channelmode=yes,fullscreen=yes,scrollbars=yes,resizable=no,location=no,toolbar=no,menubar=no,status=no");	
			}catch (e) {
			}	
			
		} else {
			alert(getDecodeStr(map.biaozhi));
			
		}
	}
	function reflash() {
		setTimeout("window.location.href='/train/resource/myexam.do?b_query=link';",1000);		
	}
	//详情
	function detail(id,r5300) {	
		var url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=2&flag=6&returnId=5&paper_id="+id;
		var form1 = document.getElementById("form1");
		form1.action = url;
		form1.submit();		
	}
	
	//浏览
	function detail2(id,r5300) {	
		var url = "/train/trainexam/paper/preview/paperspreview.do?b_query=link&r5300="+r5300+"&exam_type=2&flag=8&returnId=5&paper_id="+id;
		var form1 = document.getElementById("form1");
		form1.action = url;
		form1.submit();		
	}
//-->
  function goPortal()
  {
	  var type = "${myExamForm.type}";
	  if(type=="1"){
		  var tar='<%=userView.getBosflag()%>';
	       if(tar=="hl"){//6.0首页
		        document.location="/templates/index/portal.do?b_query=link";
	       }else if(tar=="hcm"){//7.0首页
		        document.location="/templates/index/hcm_portal.do?b_query=link";
	       }
	  }else
    	location.href = "/train/evaluationdetails.do?b_query=link";
  }
  function reexam(type,r5300,r5400) {
	 if(!confirm(TRAIN_TRAINEXAM_EXAM_REEXAM))
		 return;
	 map = new HashMap();
	 map.put("type",type+"");
	 map.put("r5300",r5300+"");
	 map.put("r5400",r5400+"");
	 Rpc({functionId:'2020030194',success:reexamSucc},map);
  }
  function reexamSucc(response) {
	var value=response.responseText;
	map=Ext.decode(value);
	var flag=map.flag;
	if("true"==flag){
		test(map.type,map.r5300,map.r5400);
	}else{
		alert(TRAIN_TRAINEXAM_EXAM_REEXAMERROR);
	}
  }
</script>
<html:form action="/train/resource/myexam.do?b_query=link" styleId="form1">
	<%
		int i = 0;
	%>	
	<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr>
		<td style="padding-bottom: 5px;">
					<span style="vertical-align: middle;">
						<bean:message key="train.examplan.status"/>&nbsp;<span style="vertical-align: middle">
						<html:select name="myExamForm" property="state" onchange="exchange()" >
							<html:optionsCollection name="myExamForm" property="stateList" value="dataValue" label="dataName" />
						</html:select>&nbsp;&nbsp;&nbsp;</span>
						<bean:message key="train.examplan.papershowstyle"/>&nbsp;<span style="vertical-align: middle;">
								<html:select name="myExamForm" property="responseType" onchange="exchange()">
									<html:optionsCollection property="responseTypeList" value="dataValue" label="dataName" />
								</html:select>&nbsp;&nbsp;&nbsp;</span>
						<bean:message key="train.examplan.name"/>&nbsp;
						<html:text name="myExamForm" styleClass="text4" property="planName" />&nbsp;&nbsp;
					</span>
					<span style="vertical-align: middle;">
						<input type="button" value="查询" onclick="exchange();" class="mybutton">
					</span>
		</td>
	</tr>
	<tr>
	<td>
	<div class="myfixedDiv">
	<table width="100%" border="0" cellspacing="0" align="center" style="border-collapse: collapse;"
		cellpadding="0">
		<thead>
			<tr class="fixedHeaderTr">
				<!-- <td align="center" width="5%" class="TableRow noleft">
					&nbsp;<input type="checkbox" name="checkall" alt='<bean:message key="label.query.selectall"/>' onclick="checkalls(this);"/>&nbsp;
				</td> -->
				<td align="center" class="TableRow noleft mytop" width="26%">
					&nbsp;<bean:message key="train.examplan.name"/>&nbsp;
				</td>
				<td align="center" width="20%" class="TableRow mytop">
					&nbsp;<bean:message key="train.examplan.begindate"/>&nbsp;
				</td>
				<td align="center" width="20%" class="TableRow mytop">
					&nbsp;<bean:message key="train.examplan.enddate"/>&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow mytop">
					&nbsp;<bean:message key="train.examplan.examtimelen"/>&nbsp;
				</td>
				<td align="center" width="8%" class="TableRow mytop">
					&nbsp;<bean:message key="train.examplan.papershowstyle"/>&nbsp;
				</td>
				<td align="center" width="5%" class="TableRow mytop">
					&nbsp;<bean:message key="train.examplan.status"/>&nbsp;
				</td>
				<td align="center" width="10%" class="TableRow noright mytop">
					&nbsp;<bean:message key="train.examplan.exam"/>&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:paginationdb id="element" name="myExamForm"
			sql_str="myExamForm.sql" table="" where_str="myExamForm.where"
			columns="myExamForm.cols" page_id="pagination"
			pagerows="${myExamForm.pagerows}" order_by="myExamForm.order" indexes="indexes" allmemo="1">
			
			<bean:define id="r5519" name="element" property="r5519"></bean:define>
			
			<%
				if (i % 2 == 0) {
			%>
			<tr class="trShallow"  onMouseOver="javascript:tr_onclick(this,'');">
				<%
					} else {
				%>
			
			<tr class="trDeep"  onMouseOver="javascript:tr_onclick(this,'');">
				<%
					}
								i++;
				%>
				<!--  <td align="center" class="RecordRow noleft" nowrap>					
					&nbsp;
					
					&nbsp;
				</td>-->
				<td class="RecordRow noleft"  align="center" style="word-break: break-all; word-wrap:break-word;padding:3px;">
					
					&nbsp;<bean:write name="element" property="r5401"/>&nbsp;
					
				</td>
				<td  align="center" class="RecordRow" >
					&nbsp;<bean:write name="element" property="r5405" />&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<bean:write name="element" property="r5406" />&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					
					&nbsp;<bean:write name="element" property="r5407" />&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:equal value="1" name="element" property="r5409">
						&nbsp;整版&nbsp;
					</logic:equal>
					<logic:equal value="2" name="element" property="r5409">
						&nbsp;单题&nbsp;
					</logic:equal>					
				</td>
				
				<td align="center" class="RecordRow" nowrap>
					<logic:equal value="04" name="element" property="r5411">
						&nbsp;已发布&nbsp;
					</logic:equal>
					<logic:equal value="05" name="element" property="r5411">
						&nbsp;启动&nbsp;
					</logic:equal>
					<logic:equal value="06" name="element" property="r5411">
						&nbsp;结束&nbsp;
					</logic:equal>					
				</td>
				
				<td align="center" class="RecordRow noright" nowrap>
					<bean:define id="r5400" name="element" property="r5400"></bean:define>
					<bean:define id="r5409" name="element" property="r5409"></bean:define>
					<bean:define id="r5300" name="element" property="r5300"></bean:define>
					<%MyTestBo bo = new MyTestBo(); 
					String pr5400 = SafeCode.encode(PubFunc.encrypt(r5400.toString()));
					String pr5300 = SafeCode.encode(PubFunc.encrypt(r5300.toString()));
					%>
					&nbsp;
						<logic:equal value="1" name="element" property="r5408">
							<logic:equal value="05" name="element" property="r5411">
								<logic:equal value="1" name="element" property="r5513">
							<% 
							if ("1".equals(bo.checkTime(r5400.toString())) || "2".equals(bo.checkTime(r5400.toString()))) {
										if(r5519.toString()==null || r5519.toString().length()<1)
										    r5519="0";
										int times = Integer.valueOf(map.get(r5400).toString());
									    if(times == -1 || times>Integer.valueOf(r5519.toString())){
									        %>
											<a href="###" onclick="reexam('<%=r5409.toString() %>','<%=pr5300 %>','<%=pr5400 %>')">
												<bean:message key="train.trainexam.exam.mytest.repeat"/>
											</a>
									        <%
									    }
							}%>
								</logic:equal>
								<logic:notEqual value="1" name="element" property="r5513">
									<%
										if ("1".equals(bo.checkTime(r5400.toString())) || "2".equals(bo.checkTime(r5400.toString()))) {
									%>
										
											<a href="###" onclick="test('<%=r5409.toString() %>','<%=pr5300 %>','<%=pr5400 %>')">
												<bean:message key="train.trainexam.exam.mytest.test"/>
											</a>
									<%} else { %>
										<logic:notEqual value="2" name="element" property="r5515">
										&nbsp;<a href="###" onclick="detail2('<%=pr5400 %>','<%=pr5300 %>')">
											<bean:message key="train.trainexam.exam.mytest.view"/>
										</a>&nbsp;
										</logic:notEqual>
									<%} %>
								</logic:notEqual>
							</logic:equal>
						
							<logic:equal value="2" name="element" property="r5515">
								&nbsp;<a href="###" onclick="detail('<%=pr5400 %>','<%=pr5300 %>')">
									<bean:message key="train.trainexam.exam.mytest.view"/>
								</a>&nbsp;
							</logic:equal>
							<logic:notEqual value="2" name="element" property="r5515">
								<logic:equal value="1" name="element" property="r5513">	
									&nbsp;<a href="###" onclick="detail2('<%=pr5400 %>','<%=pr5300 %>')">
										<bean:message key="train.trainexam.exam.mytest.view"/>
									</a>&nbsp;
								</logic:equal>
							</logic:notEqual>
						
					</logic:equal>
				</td>
			</tr>
		</hrms:paginationdb>
	</table>
	</div>
	</td>
	</tr>
	<tr>
			<td>
				<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<hrms:paginationtag name="myExamForm"
								pagerows="${myExamForm.pagerows}" property="pagination"
								scope="page" refresh="true"></hrms:paginationtag>
						</td>
						<td align="right" nowrap class="tdFontcolor">
							<p align="right">
								<hrms:paginationdblink name="myExamForm"
									property="pagination" nameId="myExamForm" scope="page">
								</hrms:paginationdblink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>	
	<br>
	<logic:equal name="myExamForm" property="home" value="5">
  <center><input type="button" value="返回"  class="mybutton" onclick="goPortal();"/></center>
  </logic:equal>
</html:form>
