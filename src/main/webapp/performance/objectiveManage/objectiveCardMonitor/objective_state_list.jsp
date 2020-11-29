<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.constant.SystemConfig,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>

<%
	    String tt4CssName="ttNomal4";
	    String tt3CssName="ttNomal3";
	    String buttonClass="mybutton";
	    if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
	    {
	       tt4CssName="tt4";
	       tt3CssName="tt3";
	       buttonClass="mybuttonBig";
	    }
%>
<%  
		SetUnderlingObjectiveForm setUnderlingObjectiveForm=(SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
		String levelstr=setUnderlingObjectiveForm.getLevel();
		
		String sqlStr=setUnderlingObjectiveForm.getSqlStr();	
		String checkCycle=setUnderlingObjectiveForm.getCheckCycle();	     //  考核周期
		String convertPageEntry=setUnderlingObjectiveForm.getConvertPageEntry();  // 转换页面入口： =1为正常的从 "目标卡状态页面" 进入， =2为从 "MBO目标设定及审批统计表" 页面进入， =3为从 "MBO目标总结考评进度统计表" 页面进入 
		ArrayList mboTableList=(ArrayList)setUnderlingObjectiveForm.getMboTableList();	// =2从 "MBO目标设定及审批统计表" 页面进入  页面信息
		String code=setUnderlingObjectiveForm.getOrgItemid();
		
		int level=1;
		if(levelstr!=null&&!levelstr.equals(""))
		{
		   level=Integer.parseInt(levelstr);
		}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/js/validateDate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../../../js/showModalDialog.js"></script>

<style>

.TableRow_self 
{
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>
<hrms:themes />
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script type="text/javascript">

function checkClcye()
{
	var checkCycle = document.getElementById("checkCycle").value;
	if(checkCycle=='7')
	{
		Element.show('datepnl');
		Element.hide('changeCycles');
		Element.hide('noYearCycle');
		document.getElementById("editor1").value='';
		document.getElementById("editor2").value='';
		return;
	}


    setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=3";
    setUnderlingObjectiveForm.submit();
}

function showPage()
{
	var checkCycle = document.getElementById("checkCycle").value;
		
	if(checkCycle=='7')
	{
		var startTime = document.getElementById("editor1").value;
		var endTime = document.getElementById("editor2").value;
				
		if(trim(startTime)!='')
	    {	
	    	 if(!validate_self(document.getElementById("editor1"),'起始日期'))
	    	 	return false;    	
	    }
	    if(trim(endTime)!='')
	    {
	    	if(!validate_self(document.getElementById("editor2"),'结束日期'))
	    	 	return false;
	   	}	
		
		document.getElementById("startDate").value=startTime;	
		document.getElementById("endDate").value=endTime;	
		
	    if(startTime!='' && endTime!='')
	    {
	    	if(startTime>endTime)	
	    	{
	    		alert(KHPLAN_INFO1);
	    	    return;
	    	}
	    }
	}
	
    setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=2";
    setUnderlingObjectiveForm.submit();
}

function validate_self(obj,aitemdesc)
{		
	var dd=true;
	var itemdesc="";
	if(aitemdesc==null||aitemdesc==undefined)
		itemdesc="日期";
	else 
		itemdesc=aitemdesc;

	if(trim(obj.value).length!=0)
	{						
		var myReg =/^(-?\d+)(\.\d+)?$/
		if(IsOverStrLength(obj.value,10))
		{
			alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
			return false;
		}
		else
		{
			if(trim(obj.value).length!=10)
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			var year=obj.value.substring(0,4);
			var month=obj.value.substring(5,7);
			var day=obj.value.substring(8,10);
			if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
			{
				alert(itemdesc+" 格式不正确,正确格式为yyyy-mm-dd ！");
				return false;
			}
			if(year<1900||year>2100)
			{
				alert(itemdesc+" 年范围为1900~2100！");
				return false;
			}
							 	
			if(!isValidDate(day, month, year))
			{
				alert(itemdesc+"错误，无效时间！");
				return false;
			}
		}
	}
	return dd
}

//返回展现 =1为正常的从 "目标卡状态页面" 进入，
function backObjectCard(obj)
{
	document.setUnderlingObjectiveForm.convertPageEntry.value=obj;
	setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=1";
//	setUnderlingObjectiveForm.target="mil_body";
   	setUnderlingObjectiveForm.submit();	
}

// 导出Excel
function ecport()
{
 	var hashVo=new ParameterSet();
   	hashVo.setValue("itemid","<%=code%>");
   	hashVo.setValue("entry","<%=convertPageEntry%>");
   	hashVo.setValue("whl","<%=sqlStr%>");
    var request=new Request({method:'post',asynchronous:false,onSuccess:sucess,functionId:'9028000317'},hashVo);			
}
function sucess(outparameters)
{
 	var outname=outparameters.getValue("name");
  	window.location.target="_blank";
  	//xus 20/4/28 vfs 改造
//	window.location.href = "/servlet/DisplayOleContent?filename="+outname;
  	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outname;
}

<!--
function query()
{
   setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=2";
   setUnderlingObjectiveForm.submit();
}
function detail(object_id)
{
  var plan_id="${setUnderlingObjectiveForm.p_id}";
  var thecodeurl="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_detail=link`plan_id="+plan_id+"`object_id="+object_id; 
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  //var retvo= window.showModalDialog(iframe_url, null, 
	//				        "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:no;status:no");
  //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
  /*     var iTop = (window.screen.availHeight - 30 - 430) / 2;  //获得窗口的垂直位置
       var iLeft = (window.screen.availWidth - 10 - 600) / 2; //获得窗口的水平位置
  window.open(iframe_url,"","width=600px,height=430px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  */
  var config = {
      width:600,
      height:430,
      title:'审批流程',
      id:'show_detail_window'
  }
  modalDialog.showModalDialogs(iframe_url,"show_detail_window",config);
}
function initCardState(opt)
{

  var desc="";
  if(opt==2)
  	  desc="状态";
  var obj = document.getElementsByName("select");
  var num=0;
  var object_id="";
  var planStatus="${setUnderlingObjectiveForm.planStatus}";
  if(planStatus=='-1')
     return;
  if(planStatus=='6'||planStatus=='7'||planStatus=='4')
  {
    alert("处于启动、评估、结束状态的计划,不允许对考核对象的"+KH_OBJECTIVE_LABLE+"进行"+desc+"初始化!");
    return;
  }
  for(var i=0;i<obj.length;i++)
  {
   if(obj[i].checked)
   {
     object_id+="/"+obj[i].value;
     num++;
   }
  }
  if(num==0)
  {
    alert("请选择要"+desc+"初始化的考核对象！");
    return;
  }
 if(confirm("您确定对所选考核对象的"+KH_OBJECTIVE_LABLE+desc+"进行初始化吗?"))
 {
   var hashVo=new ParameterSet();
   hashVo.setValue("ids",object_id.substring(1));
   hashVo.setValue("plan_id","${setUnderlingObjectiveForm.p_id}");
   hashVo.setValue("opt",opt);  // 1:初始化 2:状态初始化
   var request=new Request({method:'post',asynchronous:false,onSuccess:save_ok,functionId:'9028000307'},hashVo);			
 }
}
function save_ok(outparameters)
{
  query();
}
function clearUp()
{
  var obj = document.getElementsByName("select");
  var all=document.getElementById("all");
  if(obj)
  {
     for(var i=0;i<obj.length;i++)
     {
     if(all.checked)
     {
       obj[i].checked=true;
     }
     else
     {
       obj[i].checked=false;
     }
    }
  }
}
function dp()
{
var obj = document.getElementsByName("select");
var spf=document.getElementsByName("spf");
var sp="";
var num=0;
var object_id="";
for(var i=0;i<obj.length;i++)
{
 if(obj[i].checked)
 {
   sp=spf[i].value;
   object_id=obj[i].value;
   num++;
 }
}

if(num==0)
{
 alert("请选择要代批的考核对象！");
 return;
}
if(num>1)
{
  alert("对不起，代批操作一次只能针对一个考核对象！");
  return;
}
 var planStatus="${setUnderlingObjectiveForm.planStatus}";
  if(planStatus=='-1')
     return;
  if(planStatus=='6'||planStatus=='7'||planStatus=='4'||planStatus=='5')
  {
    alert("处于启动、评估、暂停、结束状态的计划,不允许对考核对象的"+KH_OBJECTIVE_LABLE+"进行代批!");
    return;
  }
  if(sp!='02')
{
    alert("只能代批已"+KH_PLAN_ASSIGN+"状态的考核对象！");
    return;
}
  var plan_id="${setUnderlingObjectiveForm.p_id}";
  var thecodeurl="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?br_agent=link`plan_id="+plan_id+"`object_id="+object_id; 
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
//  var retvo= window.showModalDialog(iframe_url, null, 
//					        "dialogWidth:700px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no");			
//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
  var iTop = (window.screen.availHeight - 30 - 400) / 2;  //获得窗口的垂直位置
  var iLeft = (window.screen.availWidth - 10 - 700) / 2; //获得窗口的水平位置 
  window.open(iframe_url,"","width=700px,height=400px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
  /*
  if(retvo)
  {
    if(retvo=='1')
    {
      setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=2";
      setUnderlingObjectiveForm.submit();
    }
  }
  */
 //window.open(thecodeurl,'_blank');
}
/*open 弹窗页面  用来 替换showModalDialog弹窗返回值  的方法      wangb 20171208   */
function openValue(retvo){
	if(retvo=='1')
    {
      setUnderlingObjectiveForm.action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_init=link&opt=2";
      setUnderlingObjectiveForm.submit();
    }
}

function sendMail(object_id,to_a0100)
{
 
	 var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=1`plan_id=${setUnderlingObjectiveForm.p_id}`object_id="+object_id+"`to_a0100="+to_a0100;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   // window.open(url,"_aa","hotkeys=0,menubar=0,height=470,width=700");
   // window.showModalDialog(iframe_url,"","dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=no;status=no;");  
	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
	  var iTop = (window.screen.availHeight - 30 - 470) / 2;  //获得窗口的垂直位置
	  var iLeft = (window.screen.availWidth - 10 - 700) / 2; //获得窗口的水平位置 
	  window.open(iframe_url,"","width=700px,height=470px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
}

// 发送通知
function sendMessages()
{
	var spf = document.getElementsByName("spf");
	var obj = document.getElementsByName("select");
	var mobj = document.getElementsByName("mainSelect");
  	var num = 0;
  	var object_ids = "";
  	var mainbody_ids = "";
 	for(var i=0;i<obj.length;i++)
  	{
   		if(obj[i].checked)
   		{
   		//	alert(spf[i].value);
   			if(spf[i].value!='03')
   			{
   				if(mobj[i].value!=null && trim(mobj[i].value).length>0)   				
   					mainbody_ids+="/"+mobj[i].value;   				
   				else   				
   					mainbody_ids+="/"+obj[i].value;   				
   			}else
   				mainbody_ids+="/";   			
     		object_ids+="/"+obj[i].value;
     		num++;
   		}
  	}
  	if(num==0)
  	{
    	alert("请选择要发送通知的对象！");
    	return;
  	}  
 //	alert(object_ids+"--------------"+mainbody_ids);
 
	 var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=1`logo=1`plan_id=${setUnderlingObjectiveForm.p_id}`object_id="+object_ids+"`to_a0100="+mainbody_ids;
	 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   // window.open(url,"_aa","hotkeys=0,menubar=0,height=470,width=700");
   // window.showModalDialog(iframe_url,"","dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=no;status=no;");
   //兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20171205
       var iTop = (window.screen.availHeight - 30 - 470) / 2;  //获得窗口的垂直位置
       var iLeft = (window.screen.availWidth - 10 - 700) / 2; //获得窗口的水平位置 
   window.open(iframe_url,"","width=700px,height=470px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);
}

//-->
</script>
<html:form action="/performance/objectiveManage/objectiveCardMonitor/objective_state_list">

<html:hidden name="setUnderlingObjectiveForm" property="convertPageEntry" styleId="convertPageEntry" />
<html:hidden name="setUnderlingObjectiveForm" property="startDate" styleId="startDate" />
<html:hidden name="setUnderlingObjectiveForm" property="endDate" styleId="endDate" />
	
<%
	if(convertPageEntry==null || convertPageEntry.trim().length()<=0 || convertPageEntry.equalsIgnoreCase("1")) // 转换页面入口： =1为正常的从 "目标卡状态页面" 进入， 
	{	
%>
<%if("hl".equals(hcmflag)){ %>
	<br>
<%} %>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
	<td align="left">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<td align="left" style="height:20px">
	 <font class='<%=tt3CssName%>'><bean:message key="lable.performance.perPlan"/>:</font>
	<html:select name="setUnderlingObjectiveForm" property="p_id" size="1" onchange="query();">
				<html:optionsCollection property="planList" value="dataValue" label="dataName"/>
			    </html:select>
	 <font class='<%=tt3CssName%>'><bean:message key="org.performance.status"/>:</font>
	<html:select name="setUnderlingObjectiveForm" property="status" size="1" onchange="query();">
				<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
			    </html:select>
			 
			    <html:hidden property="itemid" name="setUnderlingObjectiveForm"/>          			
	 <font class='<%=tt3CssName%>'>考核对象名</font>:&nbsp;<html:text size="5" property="a0101" name="setUnderlingObjectiveForm" styleClass="inputtext"></html:text>
	 &nbsp;
	 <input type="button" name="q" class="<%=buttonClass%>" value="查 询" onclick="query();"/>
	  <hrms:priv func_id="06070601"> 
	 <input type="button" name="d" class="<%=buttonClass%>" value="代 批" onclick="dp();"/>
	 </hrms:priv>
	  <hrms:priv func_id="06070602"> 
	  <input type="button" name="init" class="<%=buttonClass%>" value="初始化" onclick="initCardState(1);"/>
	  </hrms:priv>
	  <hrms:priv func_id="06070605"> 
	  <input type="button" name="init2" class="<%=buttonClass%>" value="状态初始化" onclick="initCardState(2);"/>
	  </hrms:priv>
	  <input type="button" name="sendMessage" class="<%=buttonClass%>" value="发送通知" onclick="sendMessages();"/>
	</td>
	</table>
	</td>
	</tr>
	<tr>
	<td>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
	<thead>
	<tr>
	 <td align="center" class="TableRow common_border_color" nowrap>
	  <input type="checkbox" name="seq" id='all' value="1" onclick="clearUp();"/>
	  </td>
	  <td align="center" class="TableRow common_border_color" nowrap>
	  &nbsp; <font class='<%=tt4CssName%>'><bean:message key="lable.performance.perObject"/></font>&nbsp;
	  </td>
	  <td align="center" class="TableRow common_border_color" nowrap>
	  &nbsp;<font class='<%=tt4CssName%>'><bean:message key="label.zp_resource.status"/></font>&nbsp;
	  </td>
	  <td align="center" class="TableRow common_border_color" nowrap>
	  &nbsp;<font class='<%=tt4CssName%>'><bean:message key="label.performance.reportdate"/></font>&nbsp;
	  </td>
	  <logic:iterate id="cloumn" name="setUnderlingObjectiveForm" property="leaderList" offset="0">
	      <td align="center" class="TableRow complex_border_color" nowrap>
	      <font class='<%=tt4CssName%>'><bean:write name="cloumn" property="sp"/></font>
	      </td>
	       <td align="center" class="TableRow complex_border_color" nowrap>
	      <font class='<%=tt4CssName%> '><bean:write name="cloumn" property="spd"/></font>
	      </td>
	 </logic:iterate>
	</tr>
	</thead>
	<% int j=0; %>
	 <hrms:extenditerate id="element" name="setUnderlingObjectiveForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="15" scope="session">
	 <%if(j%2==0){ %>
		     <tr class="trShallow">
		     <%} else { %>
		     <tr class="trDeep">
		     <%}%>
		       <td align="center" class="RecordRow" nowrap>
		    <input type="checkbox" name="select" value="<bean:write name="element" property="object_id"/>"/>
		    <input type="hidden" name="mainSelect" value="<bean:write name="element" property="mainbody_id"/>"/>
		    <input type="hidden" name="spf" value="<bean:write name="element" property="flag"/>"/>
		     </td>
		     
		     <td align="left" class="RecordRow" nowrap>
		     <a href="javascript:detail('<bean:write name="element" property="object_id"/>');">
		     &nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="a0101"/></font>&nbsp;</a>
		     </td>
		      <td align="left" class="RecordRow" nowrap>
		     &nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="sp_flag"/></font>&nbsp;
		     
		     <logic:equal name="element" property="flag" value="01"><img src='/images/mail2.gif'  onclick='sendMail("<bean:write name="element" property="object_id"/>","<bean:write name="element" property="object_id"/>")' /></logic:equal>
		     <logic:equal name="element" property="flag" value="07"><img src='/images/mail2.gif'  onclick='sendMail("<bean:write name="element" property="object_id"/>","<bean:write name="element" property="object_id"/>")' /></logic:equal>
		     
		     
		     </td>
		      <td align="left" class="RecordRow" nowrap>
		     &nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="report_date"/></font>&nbsp;
		     </td>
		     <% for(int i=1;i<=level;i++)
		        {
		        String date=String.valueOf(i)+"date";
		        String xx=String.valueOf(i);
		     %>
		        <td align="left" class="RecordRow" nowrap>
		         &nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="<%=xx%>" filter="false"  /></font>&nbsp;
		        </td>
		        <td align="left" class="RecordRow" nowrap>
		       &nbsp;<font class='<%=tt3CssName%>'><bean:write name="element" property="<%=date%>"/></font>&nbsp;
		        </td>
		     <% }%>
		     </tr>
		     <% j++; %>
		     </hrms:extenditerate>
	</table>
	</td>
	</tr>
	<td align="center">
	<table  width="100%" align="center" class="RecordRowP">
			<tr>
			   <td valign="bottom" class="tdFontcolor" nowrap>
			            <bean:message key="label.page.serial"/>
			   ${setUnderlingObjectiveForm.personListForm.pagination.current}
						<bean:message key="label.page.sum"/>
			   ${setUnderlingObjectiveForm.personListForm.pagination.count}
						<bean:message key="label.page.row"/>
			   ${setUnderlingObjectiveForm.personListForm.pagination.pages}
						<bean:message key="label.page.page"/>
			   </td>
			   <td align="right" class="tdFontcolor" nowrap>
			   <p align="right">
	            <hrms:paginationlink name="setUnderlingObjectiveForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
			   </hrms:paginationlink>
			   </p>
			   </td>
			</tr> 
	</table>
	</td> 
	<tr>
	</tr>
	</table>
<%
	}else if((convertPageEntry.equalsIgnoreCase("2")) || (convertPageEntry.equalsIgnoreCase("3")))   //  =2为从 "MBO目标设定及审批统计表" 页面进入， 
	{	
%>
	<%if("hl".equals(hcmflag)){ %>
	<br>
<%} %>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">		
		<% if(convertPageEntry.equalsIgnoreCase("2")){ %>
			<tr><td align="center">   
				<strong><font size='4'><bean:message key="jx.selfHelp.lookTargetCardType"/></font></strong>
			</td></tr>
		<% }else{ %>
			<tr><td align="center">   
				<strong><font size='4'><bean:message key="jx.selfHelp.lookLastTargetCardType"/></font></strong>
			</td></tr>
		<% } %>	
	</table>		
	<%if("hl".equals(hcmflag)){ %>
	<br>
<%} %>
	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
		<tr><td style="height:35px">   
			<bean:message key="jx.khplan.cycle"/>: 
						
			<html:select name="setUnderlingObjectiveForm"  onchange="checkClcye()" styleId="checkCycle" property="checkCycle" size="1" style="width:70px">
  				<html:optionsCollection property="checkCycleList" value="dataValue" label="dataName"/>
			</html:select>
			
			<span id="datepnl">							
				<bean:message key="label.from" /> 						
					<input type="text" name="start_date" value="${setUnderlingObjectiveForm.startDate}" extra="editor"
						style="width:100px;font-size:10pt;text-align:left" id="editor1" dropDown="dropDownDate"> 	
						
				<bean:message key="label.to" />					
					<input type="text" name="end_date" value="${setUnderlingObjectiveForm.endDate}" extra="editor"
						style="width:100px;font-size:10pt;text-align:left" id="editor2" dropDown="dropDownDate">  													
			</span>	
			
			
			&nbsp;
			<span id="changeCycles">	
			<html:select name="setUnderlingObjectiveForm" styleId="changeCycle" property="changeCycle" size="1" >
  				<html:optionsCollection property="changeCycleList" value="dataValue" label="dataName"/>
			</html:select>	(年)
			</span>				
			<%
				if((checkCycle.trim().length()>0) && !(checkCycle.equalsIgnoreCase("all")) && !(checkCycle.equalsIgnoreCase("0")))
				{
			%>
				&nbsp;
				<html:select name="setUnderlingObjectiveForm" styleId="noYearCycle" property="noYearCycle" size="1" >
	  				<html:optionsCollection property="noYearCycleList" value="dataValue" label="dataName"/>
				</html:select>			
			<%
				}
			%>
			&nbsp;	
			<hrms:priv> 
		    	<input type="button" name="check" class="mybutton" value="查询" onclick="showPage();"/>
		  	</hrms:priv>
		  	<hrms:priv> 
		    	<input type="button" name="outExcel" class="mybutton" value="导出Excel" onclick="ecport();"/>
		  	</hrms:priv>
		  	<hrms:priv> 
		    	<input type="button" name="back" class="mybutton" value="返回" onclick="backObjectCard('1');"/>
		  	</hrms:priv>
		</td></tr>						
		
		<tr><td width='100%' style=""  class="common_border_color">		
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
			<thead>
		        <tr >		        	 		        	  
					
					 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="tree.unroot.undesc"/></td>
					 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performance.perPlan"/></td>
					 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performance.persionNumList"/></td>
					 
					 <% if(convertPageEntry.equalsIgnoreCase("2")){ %>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="label.hiremanage.status1"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="performance.spflag.ybp"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="performance.spflag.ybl"/></td>
					 <% }else{ %>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.wpf"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.nowpingscore"/></td>
						 <td align="center" style='color:black' class="TableRow_self common_border_color" nowrap><bean:message key="lable.performnace.havepingscore"/></td>
					 <% } %>
				</tr>
			 </thead>
		 
			 <% int n=0; %>
			 <hrms:extenditerate id="fazerElement" name="setUnderlingObjectiveForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="15" scope="session">
			   <%
				    if(n%2==0)
				    {
			   %>
				    <tr class="trShallow" >
			   <%   }
				    else
				    {
			   %>
				    <tr class="trDeep" >
			   <%
				    }
				    n++;          
			   %>  
			   			   			   			   
			   			<td align="left" class="RecordRow" nowrap>&nbsp;
							<hrms:codetoname codeid="UN" name="fazerElement" codevalue="b0110" codeitem="codeitem_un" scope="page" />
							<hrms:codetoname codeid="UM" name="fazerElement" codevalue="b0110" codeitem="codeitem_um" scope="page" />
							<logic:notEqual name="fazerElement" property="b0110" value="HJSJ">
								<bean:write name="codeitem_un" property="codename" />
								<bean:write name="codeitem_um" property="codename" />
							</logic:notEqual>
							<logic:equal name="fazerElement" property="b0110" value="HJSJ">
								<bean:message key="jx.khplan.hjsj" />
							</logic:equal>&nbsp;
						</td>			   
				        			        				        
				        <td align='left' class='RecordRow' nowrap>&nbsp;&nbsp;<bean:write name="fazerElement" property="plan_name" filter="true"/></td>
				        				        				        				        
				        <logic:iterate  id="subElement" name="fazerElement" property="numList">
				        
			 				<td align='right' class='RecordRow' nowrap><bean:write name="subElement" property="num" filter="true"/>&nbsp;&nbsp;</td>				        	
			 
				        </logic:iterate>  
				        
			 		</tr>
			  </hrms:extenditerate>	  
	  	</table>
		</td></tr>	
	</table>	

	<table width="90%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial"/>
				<bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
				<bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
				<bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page"/>
			</td>
		    <td align="right" nowrap class="tdFontcolor">
				<p align="right">
				<hrms:paginationlink name="setUnderlingObjectiveForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
	</table>
	
	<script>
		var checkCycle = document.getElementById('checkCycle').value;	
		
		if(checkCycle!='7')
		{
			Element.hide('datepnl');
		}else
		{
			Element.show('datepnl');
			Element.hide('changeCycles');
			Element.hide('noYearCycle');
		}			
		
	</script>

<%
	}	
%>	
<script type="text/javascript">
if(!getBrowseVersion()){//兼容非IE浏览器   页面样式   wangb 20171208 
	var form = document.getElementsByName('setUnderlingObjectiveForm');
	var table2 = form[0].getElementsByTagName('table')[1];//按钮和表格重叠  添加下外边距
	table2.style.marginBottom = '1px';
}
</script>

</html:form>