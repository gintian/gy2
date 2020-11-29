 <%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.ArrayList,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.EncryptLockClient,
				 com.hjsj.hrms.actionform.performance.WorkPlanViewForm,
				 com.hrms.frame.codec.SafeCode"%>
<%@page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%

		WorkPlanViewForm workPlanViewForm=(WorkPlanViewForm)session.getAttribute("workPlanViewForm");
		String pendingCode = workPlanViewForm.getPendingCode();
		String doneFlag = workPlanViewForm.getDoneFlag();
		String optPlan = workPlanViewForm.getOptPlan();
		String a0100 = workPlanViewForm.getA0100();
		String a0100_Encrpt=PubFunc.encrypt(a0100);
		String p0115 = workPlanViewForm.getP0115();
		String a0101 = workPlanViewForm.getA0101();
		String planP0100 = workPlanViewForm.getPlanP0100();
		String nbase = workPlanViewForm.getNbase();
		String startime = workPlanViewForm.getStartime();
		String log_type = workPlanViewForm.getLog_type();
		String dailyPlan_attachment = workPlanViewForm.getDailyPlan_attachment();
		String dailySumm_attachment = workPlanViewForm.getDailySumm_attachment();
		String sp_level = workPlanViewForm.getSp_level();
		if(sp_level==null)
			sp_level = "";
		String userStatus = workPlanViewForm.getUserStatus();
		String record_grade = workPlanViewForm.getRecord_grade();
		if(record_grade==null)
			record_grade = "";
		int columns = 10;
		
		ArrayList editContentList = workPlanViewForm.getEditContentList(); // 取得p01表中，可编辑的指标
		String editListClass1 = "TableRow_rnull";
		String editListClass2 = "TableRow_lnull";
		String editListClass3 = "RecordRow_rnull";
		String editListClass4 = "RecordRow_leftnull";
		
   		ArrayList baseInfoList = workPlanViewForm.getBaseInfoList();
   		int size=baseInfoList.size();
   		
   		String url_p = SystemConfig.getServerURL(request);
   		String userName = null;
		// <CARDSTYLE>A人员,B单位,K职位,R培训,P绩效</CARDSTYLE><TEMPLATEID>考核模板号</TEMPLATEID><PLANID>考核计划号</PLANID>
		//   <SUPER_USER>全权，同管理员</SUPER_USER> 
		String dataflag=SafeCode.encode("<CARDSTYLE>A</CARDSTYLE><SUPER_USER>1</SUPER_USER>"); // 转码加密
		String objid=SafeCode.encode("<NBASE>"+nbase+"</NBASE><ID>"+a0100+"</ID><NAME>"+a0101+"</NAME><BIZDATE>"+startime+"</BIZDATE>");
   		UserView userView = (UserView)session.getAttribute(WebConstant.userView);   		
   		if(userView != null)
   		{
   			userName = userView.getUserName();  
	  	}
	  	EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
   		String license = lockclient.getLicenseCount();
   		int version = userView.getVersion();
   		if(license.equals("0"))
        	version = 100+version;
   		int usedday = lockclient.getUseddays();
   		//workType =1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结=7个人日报=8团队日报=9个人周报=10团队周报
   		String workType=workPlanViewForm.getWorkType();
   		String button_desc="";
   		if("1".equals(workType))
   			button_desc="年汇总";
   		else if("5".equals(workType))
   			button_desc="月汇总";
   		else if("3".equals(workType))
   			button_desc="季汇总";
   		else if("9".equals(workType))
   			button_desc="周汇总";
%>

<link href="/performance/workplan/workplanview/workplanviewcss.css" rel="stylesheet" type="text/css">
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript">AxManager.writeCard();</script>
<SCRIPT LANGUAGE=javascript src="/performance/workplan/workplanview/workplanview.js"></SCRIPT>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<style>
<!--

-->
</style>
<script type="text/javascript">
<!--

function initCard()
{
	var rl = document.getElementById("hostname").href;     
    var aurl = rl;//tomcat路径
    var DBType = "${workPlanViewForm.dbType}";//1：mssql，2：oracle，3：DB2
    var UserName="<%=userName%>";   //登陆用户名     
    var obj = document.getElementById('CardPreview1');
    if(obj == null)
    	return false;
       
    obj.SetURL(aurl);
    obj.SetDBType(DBType);
    obj.SetUserName(UserName);
    obj.SetHrpVersion("<%=version%>");
    obj.SetTrialDays("<%=usedday%>","30");
}

function showPrintCard()
{   
  	var tab_id = "${workPlanViewForm.print_id}"; 
  	if(tab_id==null || tab_id.length<=0 || tab_id=="-1")
  	{
    	alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO02);
        return false;
  	}  
  	var obj = document.getElementById('CardPreview1');  
  	if(obj==null)
  	{
      	alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO03);
      	return false;
  	}
  	obj.SetCardID("${workPlanViewForm.print_id}");
  	obj.SetDataFlag("<%=dataflag%>");
    obj.SetNBASE("<%=nbase%>");   
  	obj.ClearObjs();   	     
  	obj.AddObjId("<%=objid%>");
  	try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
  	obj.ShowCardModal();
}

function openwin(url)
{
   window.open(url,"_blank","left=0,top=0,width="+(screen.availWidth-10)+",height="+(screen.availHeight-40)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
}

function validateValue(flag)
{
	if(flag==2) // 报批的时候验证
	{
		var workLength = "${workPlanViewForm.workLength}"; // 工作纪实录入的文字的最小字数
		var log_type = document.getElementById("log_type").value;		
		var allStr = "";	
		if(log_type!=null && log_type=='1')
		{
			for(var i=1;i<=maxRow;i++)
			{
				var everyStr="";
				var everyRowArr=document.getElementsByName("name_"+i);
				for(var j=0;j<everyRowArr.length;j++)
				{
					everyStr+=everyRowArr[j].value;				
				}
				allStr+=everyStr;
			}
			if(trimStr(allStr)=="")
			{
				alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO09);
				return;
			}
			if(trimStr(allStr).length<workLength)
			{
				alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO10+workLength+PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO11);
				return;
			}
		}else
		{
			allStr=document.getElementById("SummaryStrid").value;
			if(trimStr(allStr)=="")
			{
				alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO12);
				return;
			}
			if(trimStr(allStr).length<workLength)
			{
				alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO13+workLength+PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO11);
				return;
			}
		}
	}			
    <% int m=0;  %>
    <logic:iterate id="editList" name="workPlanViewForm" property="editContentList" indexId="editIndex" offset="0">
    
    	if(flag==2) // 报批的时候验证
		{
	    	var tt<%=m%>=document.getElementsByName("editContentList[<%=m%>].value");
	    	var mustwrite = "<bean:write  name="editList" property="mustwrite"/>";
	    	if(tt<%=m%>[0].value=='' && mustwrite=='true')
	    	{
	    		alert("<bean:write  name="editList" property="itemdesc"/>"+" "+THIS_IS_MUST_FILL+"！");
				return;
	    	}
    	}
    	    
	    <logic:equal value="N" name="editList" property="itemtype">
	        var a<%=m%>=document.getElementsByName("editContentList[<%=m%>].value");
			if(a<%=m%>[0].value!='')
			{
				var myReg =/^(-?\d+)(\.\d+)?$/
				if(!myReg.test(a<%=m%>[0].value)) 
				{
					alert("<bean:write  name="editList" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
					return;
				}
			}
	    </logic:equal>
	 
	    <logic:equal value="D" name="editList" property="itemtype">
	       var a<%=m%>=document.getElementsByName("editContentList[<%=m%>].value")
		   if(a<%=m%>[0].value!='')
		   {
			 if(!checkDateTime(a<%=m%>[0].value)) 
			 {
				alert("<bean:write  name="editList" property="itemdesc"/>"+PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO14);
				return;
			 }
			var year=a<%=m%>[0].value.substring(0,4);
			var month=a<%=m%>[0].value.substring(5,7);
			var day=a<%=m%>[0].value.substring(8,10);
			if(!isValidDate(day, month, year))
			{
			    alert("<bean:write  name="editList" property="itemdesc"/>"+PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO15);
			    return;
			}
		  }
	    </logic:equal>
	<%m++;%>
	</logic:iterate>
				
	if(flag==1)	
		savePlan("",flag,"${workPlanViewForm.target}","${workPlanViewForm.pendingCode}");
	else 
		appiary(flag);
}

// 报批
function appiary(flag)
{	
	var hashvo = new ParameterSet();
	hashvo.setValue("a0100","${workPlanViewForm.a0100}");
	hashvo.setValue("nbase","${workPlanViewForm.nbase}");
	hashvo.setValue("flag",flag);
	hashvo.setValue("sp_relation","${workPlanViewForm.sp_relation}");
	hashvo.setValue("sp_level","${workPlanViewForm.sp_level}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'90100170054'},hashvo);
}
function getSuperiorUser(outparamters)
{
	var flag = outparamters.getValue("flag");
	var curr_user = "";
	if(outparamters.getValue("outname").length==1)
	{
		curr_user = (outparamters.getValue("outname")[0].split(":"))[0];
		savePlan(curr_user,flag,"${workPlanViewForm.target}","${workPlanViewForm.pendingCode}");
		
	}else if(outparamters.getValue("outname").length>1)
	{
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       	if(return_vo!=null && return_vo.length>=0)
       	{
       		curr_user = return_vo;
       		savePlan(curr_user,flag,"${workPlanViewForm.target}","${workPlanViewForm.pendingCode}");
       	}
	}else
	{
		selectCurrUser(flag);
	}
}
//选择审批人
function selectCurrUser(flag)
{
        //var return_vo=select_org_emp_dialog(1,2,1,0,0,1);
        var return_vo = select_org_emp_dialog2_jh("1","2","1","0","0","1","");
        if(return_vo){
            var curr_user = return_vo.content;
	        if(curr_user==""){
	          alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO16);
	          return;
	        } 
	        savePlan(curr_user,flag,"${workPlanViewForm.target}","${workPlanViewForm.pendingCode}");
        }
}
//组织机构树如果显示人员，则先显示人员库
function select_org_emp_dialog2_jh(flag,selecttype,dbtype,priv,isfilter,loadtype,generalmessage)
{													//("1","1","0","1","0","1",generalmessage); 
	 var showSelfNode=0;
	 <%		 
        if(SystemConfig.getPropertyValue("clientName")!=null && SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("gjkhxt")) 
		{	
     %>
			showSelfNode=1;
	<%  }%>
	 if(dbtype!=1)
	 	dbtype=0;
	 if(priv!=0)
	    priv=1;
     var theurl="/system/logonuser/org_employ_tree.do?b_query=link`flag="+flag+"`showSelfNode="+showSelfNode+"`showDb=1`tabid="+2+"`selecttype="+selecttype+"`dbtype="+dbtype+
                "`priv="+priv + "`isfilter=" + isfilter+"`loadtype="+loadtype+"`generalmessage="+"可以输入“姓名”，“拼音简码”进行查询";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
      
     var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:300px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");
	 return return_vo;
}
// 引入计划内容
function intoPlanstr()
{
	var hashvo = new ParameterSet();
	hashvo.setValue("a0100","${workPlanViewForm.a0100}");
	hashvo.setValue("nbase","${workPlanViewForm.nbase}");
	hashvo.setValue("state","${workPlanViewForm.state}");
	hashvo.setValue("year_num","${workPlanViewForm.year_num}");
	hashvo.setValue("quarter_num","${workPlanViewForm.quarter_num}");
	hashvo.setValue("month_num","${workPlanViewForm.month_num}");
	hashvo.setValue("week_num","${workPlanViewForm.week_num}");	
	hashvo.setValue("day_num","${workPlanViewForm.day_num}");		
	var request = new Request({method:'post',asynchronous:false,onSuccess:showlist,functionId:'90100170055'},hashvo);
}
 
function showlist(outparamters)
{
	var content = outparamters.getValue("content");
	var opt=outparamters.getValue("opt"); //collectSummarize:月总结汇总周报   季度 总结汇总月总结 
	var str = getDecodeStr(content); 
	if(str==null || trim(str).length<=0)
	{ 
		if(opt!='collectSummarize')
			alert(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO17);
		else
			alert("无汇总内容");
	}
	document.getElementById("SummaryStrid").value = str+"\r\n"+document.getElementById("SummaryStrid").value;
}



// 批准
function approveValue(flag) // flag 1:批准 2:驳回
{
    
    if(flag==1){
      if(!confirm(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO18)){
          return;
      }  
    }
    if(flag==2){
      if(!confirm(PERFORMANCE_WORKPLAN_WORKPLANTEAM_INFO19)){
          return;
      }
    }
    var pendingCode="<%=pendingCode%>";
    var isBack=1;
    if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
      document.getElementById("doneFlag").value="1";
      isBack=0;
    }
    workPlanViewForm.action="/performance/workplan/workplanview/workplan_view_list.do?b_approve=approve&flag="+flag+"&isBack="+isBack; 
	workPlanViewForm.submit();		
}

// 返回
function returnBack()
{	
    
	document.workPlanViewForm.action="${workPlanViewForm.returnURL}"; 	    
	document.workPlanViewForm.target="${workPlanViewForm.target}";				 		
	document.workPlanViewForm.submit();
}
//-->
</script>
<html:form action="/performance/workplan/workplanview/workplan_view_list">
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<html:hidden property="workType" name="workPlanViewForm"/>
<html:hidden property="state" name="workPlanViewForm"/>
<html:hidden property="p0100" name="workPlanViewForm"/>
<html:hidden property="log_type" name="workPlanViewForm"/>
<html:hidden property="month_num" name="workPlanViewForm"/>
<html:hidden property="year_num" name="workPlanViewForm"/>
<html:hidden property="quarter_num" name="workPlanViewForm"/>
<html:hidden property="month" name="workPlanViewForm"/>
<html:hidden property="week_num" name="workPlanViewForm"/>
<html:hidden property="day_num" name="workPlanViewForm"/>
<html:hidden property="doneFlag" name="workPlanViewForm"/>

<table width="980" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<tr>
		<td width="100%" align="center">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<tr>
				<td align="center" colspan="4" class="TableRow" width="100%" style="height:30px" nowrap>
					&nbsp;<bean:write name="workPlanViewForm" property="checkCycleStr"/>
				</td>
			</tr>			
			<%int i=0; %>
			<logic:iterate id="baseInfo" name="workPlanViewForm" property="baseInfoList" indexId="index">
			<%
			    if(i==0||i%2==0) {
			        if(i!=0){
			%>
				</tr>
				<%} %>
				<tr width="100%">
			<%} %>
					<td align="right" class="TableRow" width="20%" nowrap>
						<bean:write name="baseInfo" property="itemdesc"/>&nbsp;&nbsp;
					</td>
					<td align="left" class="RecordRow" width="30%" nowrap>
						&nbsp;&nbsp;<bean:write name="baseInfo" property="valuedesc"/>
					</td>
				<%if(i==(size-1)){ %>
				</tr>
			<%} %>
			<%i++; %>
			</logic:iterate>
			<logic:equal value="2" name="workPlanViewForm" property="log_type">
			
			<% if(planP0100!=null && planP0100.trim().length()>0){  %>	
				<tr>
					<td align="center" class="TableRow" width="20%" style="height:30px" nowrap>
						&nbsp;
					</td>
					<td align="left" colspan="3" class="TableRow" width="80%" style="height:30px" nowrap>
						<span id="showPlan" nowrap>
							&nbsp;<a href="###" onclick="Element.hide('showPlan');Element.show('hidePlan');Element.show('showPlanHtml');" ><bean:write name="workPlanViewForm" property="planCycleStr"/>&nbsp;[<bean:message key="lable.channel.visible"/>]</a>
						</span>
						<span id="hidePlan" nowrap>
							&nbsp;<a href="###" onclick="Element.hide('hidePlan');Element.show('showPlan');Element.hide('showPlanHtml');" ><bean:write name="workPlanViewForm" property="planCycleStr"/>&nbsp;[<bean:message key="lable.channel.hide"/>]</a>
						</span>
					</td>					
				</tr>
			<%} %>				
				<tr id="showPlanHtml">
					<td width="500" align="center" colspan="4" class="RecordRow_1">						
						<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
							<tr>
								<td width="30"></td>
								<td class="bookstyle" width="125" align="center">
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
								</td>
								<td width="395" align="center">
									<br>
									${workPlanViewForm.planHtml}
									<br>
								</td>
							</tr>
						</table>						
					</td>
				</tr>
				
				<logic:notEqual value="" name="workPlanViewForm" property="refer_name">		
				<tr>
					<td align="center" class="TableRow" width="20%" style="height:30px" nowrap>
						&nbsp;
					</td>
					<td align="left" colspan="3" class="TableRow" width="80%" style="height:30px" nowrap>
						<a href="###" onclick='openwin("/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=<%=nbase%>&a0100=<%=a0100_Encrpt%>&inforkind=1&tabid=${workPlanViewForm.refer_id}&multi_cards=-1&bizDate=${workPlanViewForm.startime}");'>
               				&nbsp;<bean:write name="workPlanViewForm" property="refer_name"/>
               			</a>						
					</td>
				</tr>
				</logic:notEqual>
			</logic:equal>
			</table>
		</td>
	</tr>
				
	<tr>
	<logic:equal value="1" name="workPlanViewForm" property="log_type">	
		<td width="500" align="center" class="RecordRow_1">			
			<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<tr>
					<td width="30"></td>
					<td class="bookstyle" width="125" align="center">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</td>
					<td width="395" align="center">
						<br>
						${workPlanViewForm.planHtml}
						<br>
					</td>
				</tr>
			</table>			
		</td>
	</logic:equal>
	<logic:equal value="2" name="workPlanViewForm" property="log_type">
		<td align="center" width="100%" class="RecordRow_1">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">	
				<tr>
  					<td width="194" class="TableRow_ws" align="right">
						<bean:message key='performance.workdiary.worksummary' />&nbsp;&nbsp;
					</td>
					<logic:equal value="1" name="workPlanViewForm" property="optPlan">						
						<td align="left" >
							<textarea name="workPlanViewForm" id="SummaryStrid" rows="25" cols="90"><bean:write name="workPlanViewForm" property="summaryStr"/></textarea>
						</td>
					</logic:equal>
  					<logic:notEqual value="1" name="workPlanViewForm" property="optPlan">
  						<td align="left" >
							<textarea name="workPlanViewForm" id="SummaryStrid" rows="25" cols="90" readonly="true"  ><bean:write name="workPlanViewForm" property="summaryStr"/></textarea>
						</td>
  					</logic:notEqual>
				</tr>
			</table>
		</td>				
	</logic:equal>
	</tr>
	
	<tr>
		<td align="center" width="100%">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
			<%int k = 0; %>
				<logic:iterate id="editList" name="workPlanViewForm" property="editContentList" indexId="editIndex" offset="0">	
				<%
					k++;
					if(k==editContentList.size())
					{
						editListClass1 = "TableRow_rbnull";
						editListClass2 = "TableRow_lbnull";
						editListClass3 = "RecordRow_bnull";
					}
				%>			
				<tr>
  					<td width="3%" class="<%=editListClass1%>" valign="top" align="left">
  						&nbsp;
  						<logic:equal value="M" name="editList" property="itemtype">	      					
	      					<span id="<%="editContentList["+editIndex+"].value"%>_idview">								
								<a href="###"><img src="/images/button_vert1.gif" onclick='hides("<%="editContentList["+editIndex+"].value"%>_idview","<%="editContentList["+editIndex+"].value"%>_idhide","<%="editContentList["+editIndex+"].value"%>_view");' border="0"></a>
							</span>
							<span id="<%="editContentList["+editIndex+"].value"%>_idhide" style="display:none">								
								<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("<%="editContentList["+editIndex+"].value"%>_idview","<%="editContentList["+editIndex+"].value"%>_idhide","<%="editContentList["+editIndex+"].value"%>_view");' border="0"></a>
							</span>							
	    				</logic:equal>	    				 						
  					</td>
  					<td width="17%" class="<%=editListClass2%>" align="right">  						
	      				<bean:write name="editList" property="itemdesc"/>
	      				&nbsp;	    										
  					</td>  					
  					<logic:equal value="1" name="workPlanViewForm" property="optPlan">
	  					<td align="left" class="<%=editListClass3%>">
	    					<logic:equal value="N" name="editList" property="itemtype">
	    						<input type="text" size="40" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" maxlength="<bean:write name="editList" property="itemlength"/>"/>
	    					</logic:equal>
	    					<logic:equal value="A" name="editList" property="itemtype">
	        					<logic:equal value="0" name="editList" property="codesetid">
	          						<input type="text" size="40" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" maxlength="<bean:write name="editList" property="itemlength"/>"/>
	       						</logic:equal>
	        					<logic:notEqual value="0" name="editList" property="codesetid">
	             					<input type="hidden" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" readonly />
	              					<input type="text"  size="40" name="<%="editContentList["+editIndex+"].viewvalue"%>" value="<bean:write name="editList" property="viewvalue"/>"/>
	              					<img  src="/images/code.gif" onclick='javascript:openInputCodeDialogText("<bean:write  name="editList" property="codesetid"/>","<%="editContentList["+editIndex+"].viewvalue"%>","<%="editContentList["+editIndex+"].value"%>");'/>
	        					</logic:notEqual>
	    					</logic:equal>
	    					<logic:equal value="D" name="editList" property="itemtype">
	        					<input type="text" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' size="40"/>
	    					</logic:equal>
	    					<logic:equal value="M" name="editList" property="itemtype">
	    						<span id="<%="editContentList["+editIndex+"].value"%>_view">
	      						<textarea name="<%="editContentList["+editIndex+"].value"%>" rows="10" cols="90"><bean:write name="editList" property="value"/></textarea>
	    						</span>
	    					</logic:equal>
	  					</td>
	  					<td width="10%" class="<%=editListClass4%>" valign="top" align="left">
	  						<logic:equal value="true" name="editList" property="mustwrite">
								<font color='red'>*</font>
							</logic:equal>
	  					</td>
  					</logic:equal>
  					<logic:notEqual value="1" name="workPlanViewForm" property="optPlan">
	  					<td align="left" class="<%=editListClass3%>">
	    					<logic:equal value="N" name="editList" property="itemtype">
	    						<input type="text" size="40" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" maxlength="<bean:write name="editList" property="itemlength" />" readonly="true"  />
	    					</logic:equal>
	    					<logic:equal value="A" name="editList" property="itemtype">
	        					<logic:equal value="0" name="editList" property="codesetid">
	          						<input type="text" size="40" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" maxlength="<bean:write name="editList" property="itemlength" />" readonly="true"  />
	       						</logic:equal>
	        					<logic:notEqual value="0" name="editList" property="codesetid">
	             					<input type="hidden" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" readonly/>
	              					<input type="text"  size="40" name="<%="editContentList["+editIndex+"].viewvalue"%>" value="<bean:write name="editList" property="viewvalue" />"  readonly="true"  />
	              					<img  src="/images/code.gif" onclick='javascript:openInputCodeDialogText("<bean:write  name="editList" property="codesetid"/>","<%="editContentList["+editIndex+"].viewvalue"%>","<%="editContentList["+editIndex+"].value"%>");' disabled/>
	        					</logic:notEqual>
	    					</logic:equal>
	    					<logic:equal value="D" name="editList" property="itemtype">
	        					<input type="text" name="<%="editContentList["+editIndex+"].value"%>" value="<bean:write name="editList" property="value"/>" onclick='popUpCalendar(this,this, dateFormat,"","",true,false)' size="40"  readonly="true" disabled />
	    					</logic:equal>
	    					<logic:equal value="M" name="editList" property="itemtype">
	    						<span id="<%="editContentList["+editIndex+"].value"%>_view">
	      						<textarea name="<%="editContentList["+editIndex+"].value"%>" rows="10" cols="90" readonly="true"  ><bean:write name="editList" property="value" /></textarea>
	    						</span>
	    					</logic:equal>
	  					</td>
	  					<td width="10%" class="<%=editListClass4%>" valign="top" align="left">
	  						<logic:equal value="true" name="editList" property="mustwrite">
								<font color='red'>*</font>
							</logic:equal>
	  					</td>
  					</logic:notEqual>
				</tr>
				</logic:iterate>
			</table>
		</td>
	</tr>
	
	<%
		 if(((dailyPlan_attachment==null || dailyPlan_attachment.trim().length()<=0 || dailyPlan_attachment.equalsIgnoreCase("True")) && log_type.equalsIgnoreCase("1")) 
		 		|| ((dailySumm_attachment==null || dailySumm_attachment.trim().length()<=0 || dailySumm_attachment.equalsIgnoreCase("True")) && log_type.equalsIgnoreCase("2")))
		 {
	%>
	<tr>
		<td align="left" width="100%" class="RecordRow_lead">
			<table width="100%" border="0" cellspacing="0"  align="left" height="100%" cellpadding="0" class="ListTable">
				<tr>
					<td width="20" class="TableRow_wsnull" valign="top" align="left"> 
  						&nbsp;
						<!-- 2015/12/23 wangjl 附件默认收起 -->
						<span id="file_idview" style="display: none">
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("file_idview","file_idhide","file_id_view");' border="0"></a>
						</span>
						<span id="file_idhide" >
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("file_idview","file_idhide","file_id_view");' border="0"></a>
						</span>						
					</td>					
					<td width="174" class="TableRow_ws" align="right" >
						<bean:message key="label.zp_employ.uploadfile"/>
						&nbsp;					
					</td>
					<td align="left" >	
					<span id="file_id_view" style="display: none">
						<iframe src="/performance/workplan/workplanview/workplan_view_list.do?b_attach=query&p0100=${workPlanViewForm.p0100}" width="100%" height="150px" scrolling="auto" frameborder="0" name="main">
						
						</iframe> 
					</span>
					</td>
					
				</tr>
			</table>
		</td>
	</tr>
	<% }  %>
	
	<% if(record_grade.equalsIgnoreCase("true") && p0115!=null && p0115.trim().length()>0 && ((!p0115.equalsIgnoreCase("01") && !p0115.equalsIgnoreCase("02")) || (p0115.equalsIgnoreCase("02") && (optPlan.equalsIgnoreCase("3"))|| !userStatus.equals("0")  ))){  %>
	  <tr>
	  <!-- 2015/12/23 wangjl RecordRow换成RecordRow_nopadding解决对不齐的问题 -->
	   <td align="center" width="100%" class="RecordRow_nopadding">
	    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	      <tr>
	          <td width="194" class="TableRow_ws" align="right"> 						
			      				<bean:message key="hire.level"/>&nbsp;&nbsp;&nbsp;									
		  	  </td>
				  <% if(sp_level.equals("2")){ %>
				     <% if(optPlan.equalsIgnoreCase("3")){ %>
			  		 <td>
			  		   <hrms:optioncollection name="workPlanViewForm" property="recordGradeList" collection="list" />
										 <html:select name="workPlanViewForm" property="recordGradeName" size="1" >
								             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
								        </html:select>	        	
			  		 </td>
			  		 <% }  %>
			  		 <% if(!optPlan.equalsIgnoreCase("3")  && (!userStatus.equals("0") || (userStatus.equals("0") && p0115!=null && p0115.trim().length()>0 && (p0115.equalsIgnoreCase("07") || p0115.equalsIgnoreCase("03")) ))){  %>
			  		     <td>
			  		 		<hrms:optioncollection name="workPlanViewForm" property="recordGradeList" collection="list" />
										 <html:select name="workPlanViewForm" property="recordGradeName" size="1" disabled="true">
								             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
								        </html:select>	        	
			  		 	</td>
			  		 <% }  %>
				  <% }  %>
				  <% if(sp_level.equals("1")){ %>
				     <% if(optPlan.equalsIgnoreCase("3")){ %> 
			  		 <td>
			  		   <hrms:optioncollection name="workPlanViewForm" property="recordGradeList" collection="list" />
										 <html:select name="workPlanViewForm" property="recordGradeName" size="1" >
								             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
								        </html:select>	        	
			  		 </td>
			  		 <% }  %>
			  		 <% if(!optPlan.equalsIgnoreCase("3")){ %> 
			  		 <td>
			  		 <hrms:optioncollection name="workPlanViewForm" property="recordGradeList" collection="list" />
										 <html:select name="workPlanViewForm" property="recordGradeName" size="1" disabled="true">
								             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
								        </html:select>	        	
			  		 </td>
			  		 <% }  %>
				  <% }  %>
	     </tr>
	    </table>
	  </td>
	</tr>
	<% }  %>
	<% if(p0115!=null && p0115.trim().length()>0 && ((!p0115.equalsIgnoreCase("01") && !p0115.equalsIgnoreCase("02")) || (p0115.equalsIgnoreCase("02") && (optPlan.equalsIgnoreCase("3"))|| !userStatus.equals("0")  ))){  %>
	<%
	  LazyDynaBean ldbean = (LazyDynaBean)workPlanViewForm.getLeaderCommandsBean();
	  String spAdvice = (String)ldbean.get("value1");
	  if(!(userStatus.equals("0") && (spAdvice==null || spAdvice.equals("")))){ %>      	
	<tr>
		<td align="center" width="100%" class="RecordRow_nopadding">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<tr>
  					<td width="20" class="TableRow_wsnull" valign="top" align="left"> 
  						&nbsp;					
  						<span id="leaderCommandsBean_idview">							
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("leaderCommandsBean_idview","leaderCommandsBean_idhide","leaderCommandsBean_view");' border="0"></a>
						</span>
						<span id="leaderCommandsBean_idhide" style="display:none">							
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("leaderCommandsBean_idview","leaderCommandsBean_idhide","leaderCommandsBean_view");' border="0"></a>
						</span>						  					  						
  					</td>
  					<td width="174" class="TableRow_ws" align="right"> 
  						<bean:write name="workPlanViewForm" property="leaderCommandsBean.itemdesc"/>
						&nbsp;  					  						
  					</td>
  					<td align="left" >
  					    <% if((p0115.equalsIgnoreCase("07") && !userStatus.equals("0")) || optPlan.equals("3")){  
  						         columns = 7;}%>
  						<span id="leaderCommandsBean_view">
  						        <% if(!userStatus.equals("0") || (userStatus.equals("0") && p0115!=null && p0115.trim().length()>0 && !p0115.equalsIgnoreCase("01") && !p0115.equalsIgnoreCase("02") )){  %>
		  						   <textarea name="leaderCommandsBean.value1" rows="<%=columns %>" cols="90" readonly="true"><bean:write name="workPlanViewForm" property="leaderCommandsBean.value1"/></textarea>
			  					<% }  %>
			  					<% if((p0115.equalsIgnoreCase("07") && !userStatus.equals("0") && optPlan.equals("3")) || optPlan.equals("3")){ %>
			     				   <textarea name="leaderCommandsBean.value2" rows="<%=columns %>" cols="90" ><bean:write name="workPlanViewForm" property="leaderCommandsBean.value2"/></textarea>
			     				<% }  %>		
  						</span>
  					</td>
  				</tr>
			</table>
		</td>
	</tr>
	<% }  %>
	<% }  %>
	
	<tr>
		<td align="center" width="100%" class="RecordRow_nopadding">
			<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				<tr>
					<td width="194" align="right" class="TableRow_ws">
						<bean:message key="performance.workplan.workplanview.copyTo"/>
						&nbsp;
					</td>
					<td align="left" >
						<logic:equal value="1" name="workPlanViewForm" property="optPlan">
						&nbsp;
						<%--
							<input type="button" name="seCopy" value="抄送" class="mybutton" onclick='selectCopyTo("${workPlanViewForm.p0100}","${workPlanViewForm.workNbase}");'/>&nbsp;
						--%>	
							<input type="text" size="110" class="TEXT" id="ctn" name="codyToName" value="${workPlanViewForm.codyToName}" readonly="true"/>
							<input type="hidden" name="copyToStr" id="cts" value="${workPlanViewForm.copyToStr}"/>							
							<a href="###"><img src="/images/employee.gif" onclick='selectCopyTo("${workPlanViewForm.p0100}","${workPlanViewForm.workNbase}");' border="0"></a>							
						</logic:equal>
  						<logic:notEqual value="1" name="workPlanViewForm" property="optPlan">
  						&nbsp;
  							<input type="text" size="110" class="TEXT" id="ctn" name="codyToName" value="${workPlanViewForm.codyToName}"  readonly="true"  />
  							<%--<!-- 审批后用户可以选择抄送人  -->
  							<logic:equal value="03" name="workPlanViewForm" property="p0115">
  							<logic:equal value="0" name="workPlanViewForm" property="userStatus">
  							<input type="hidden" name="copyToStr" id="cts" value="${workPlanViewForm.copyToStr}"/>							
							<a href="###"><img src="/images/employee.gif" onclick='selectCopyTo("${workPlanViewForm.p0100}","${workPlanViewForm.workNbase}");' border="0"></a>
  							</logic:equal>
  							</logic:equal>
  						--%></logic:notEqual>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td align="center" class="RecordRow" style="padding-top:3px;padding-bottom:3px;">
			<logic:equal value="2" name="workPlanViewForm" property="log_type">
			<logic:equal value="1" name="workPlanViewForm" property="optPlan">
			<%
				if(button_desc.length()>0)
				{
					out.println("<input type='button'  value='"+button_desc+"' onclick='collectSummarize(\""+workType+"\")' class='mybutton'/>&nbsp;"); 
				}
			 %>
			</logic:equal>		
			</logic:equal>		
			
			
			<logic:equal value="1" name="workPlanViewForm" property="optPlan">
				<input type="button" name="bp" value="<bean:message key="button.appeal"/>" onclick="validateValue(2);" class="mybutton"/>
				&nbsp;
				<input type="button" name="sv" value="<bean:message key="button.save"/>" onclick="validateValue(1);" class="mybutton"/>
				&nbsp;
				<%-- 		
				<logic:equal value="2" name="workPlanViewForm" property="log_type">
					<input type="button" class="mybutton" value="<bean:message key='jx.param.intoplan' />" onClick="intoPlanstr();" />
					&nbsp;
				</logic:equal>
				--%>
			</logic:equal>			
			<logic:equal value="3" name="workPlanViewForm" property="optPlan">
				<input type="button" name="sv" value="<bean:message key="button.save"/>" onclick="validateValue(1);" class="mybutton"/>
				&nbsp;
				<% if(sp_level.equals("2")){ %>
				<logic:equal value="1" name="workPlanViewForm" property="userStatus">
				    <input type="button" name="bp" value="<bean:message key="button.appeal"/>" onclick="validateValue(2);" class="mybutton"/>
				&nbsp;
			    </logic:equal>
			    <% }  %>
			    <% if(sp_level.equals("2")){ %>
			    <logic:equal value="2" name="workPlanViewForm" property="userStatus">
			    <input type="button" name="pz" value="<bean:message key="button.approve"/>" onclick="approveValue(1);" class="mybutton"/>
			    &nbsp;
				</logic:equal>
				<% }  %>
				<% if(sp_level.equals("1")){ %>
				<input type="button" name="pz" value="<bean:message key="button.approve"/>" onclick="approveValue(1);" class="mybutton"/>
			    &nbsp;
				<% }  %>
				<input type="button" name="bh" value="<bean:message key="button.reject"/>" onclick="approveValue(2);" class="mybutton"/>
				&nbsp;
			</logic:equal>
			
			<% if(p0115!=null && p0115.trim().length()>0 && (p0115.equalsIgnoreCase("03"))){  %>	
				<input type='button' value='<bean:message key="button.print"/>'  class='mybutton' onclick='showPrintCard();'>  
				&nbsp; 
			<% }  %>
			<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null")) {%>
			 <input type="button" name="close" value="关闭" onclick="javascript:window.close();" class="mybutton"/>
			<% }else { %>   
			<!-- 2016/1/26 wangjl 全总领导没批之前可以撤回计划或总结  -->
			<!-- 是否用户 -->
			<logic:equal value="0" name="workPlanViewForm" property="userStatus">
			<!-- 报批 -->
			<logic:equal value="02" name="workPlanViewForm" property="p0115">    
			<input type="button" name="rc" value="<bean:message key="button.recall"/>" onclick="recall();" class="mybutton"/>   
			</logic:equal> 
			<!-- 批准 -->
			<logic:equal value="03" name="workPlanViewForm" property="p0115">
			<input type="button" name="cc" value="<bean:message key="performance.workplan.workplanview.copyTo"/>" onclick="copyTo();" class="mybutton"/>
			&nbsp;  
			</logic:equal> 
			</logic:equal>
			<input type="button" name="rb" value="<bean:message key="button.return"/>" onclick="returnBack();" class="mybutton"/>
			<% }  %>
		</td>
	</tr>
</table>

<input type="hidden" name="planContent" id="planContent_Str" value=""/>

<script type="text/javascript">
	init_row_max_num("${workPlanViewForm.helpScript}");
	<% if(request.getParameter("isBack")!=null && request.getParameter("isBack").equalsIgnoreCase("1")){%>
		document.workPlanViewForm.action="${workPlanViewForm.returnURL}"; 	    
		document.workPlanViewForm.target="${workPlanViewForm.target}";				 		
		document.workPlanViewForm.submit();
	<% }%>
	<% if(optPlan.equals("3") && pendingCode!=null && pendingCode!="" && !pendingCode.equals("null") && doneFlag!=null && doneFlag.equals("1")){%>
	     document.workPlanViewForm.sv.disabled=true; 
	     document.workPlanViewForm.bh.disabled=true;
	      <% if(sp_level.equals("2")){ %>
	         <% if(userStatus.equals("1")){ %>
	             document.workPlanViewForm.bp.disabled=true; 
	         <% }else if(userStatus.equals("2")){%>
	             document.workPlanViewForm.pz.disabled=true;
	         <% }%>
	      <% }else if(sp_level.equals("1")){%>
	         document.workPlanViewForm.pz.disabled=true;   
	      <% }%>
	<% }%>
	<% if(optPlan.equals("1") && pendingCode!=null && pendingCode!="" && !pendingCode.equals("null") && doneFlag!=null && doneFlag.equals("1")){%>
	   document.workPlanViewForm.bp.disabled=true;
	   document.workPlanViewForm.sv.disabled=true; 
	<% }%>
</script>

<script language="javascript">
	initCard();
	Element.hide('hidePlan');
	Element.hide('showPlanHtml');
	
	//log_type =1计划=2总结  //optPlan 对于计划和总结 =0查看，=1可填报，=2按钮置灰 =3审批  //workType =1个人年工作计划总结。=2团队年工作计划总结=3个人季度工作计划总结=4团队季度工作计划总结=5个人月工作计划总结=6团队月工作计划总结=7个人日报=8团队日报=9个人周报=10团队周报
	function collectSummarize(workType)
	{ 
		var hashvo = new ParameterSet();
		hashvo.setValue("a0100","${workPlanViewForm.a0100}");
		hashvo.setValue("nbase","${workPlanViewForm.nbase}");
		hashvo.setValue("state","${workPlanViewForm.state}");
		hashvo.setValue("year_num","${workPlanViewForm.year_num}");
		hashvo.setValue("quarter_num","${workPlanViewForm.quarter_num}");
		hashvo.setValue("month_num","${workPlanViewForm.month_num}");
		hashvo.setValue("week_num","${workPlanViewForm.week_num}");	
		hashvo.setValue("day_num","${workPlanViewForm.day_num}");		
		hashvo.setValue("opt","collectSummarize");
		hashvo.setValue("workType",workType);
		var request = new Request({method:'post',asynchronous:false,onSuccess:showlist,functionId:'90100170055'},hashvo);
	}
		
	
</script>
<!-- wangjl 全国总工会需要右键菜单 -->
<script type="text/javascript">
window.onload = function(){
document.oncontextmenu = function(e) {return true;}
}
</script>
</html:form>