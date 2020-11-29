<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm, 
				 com.hrms.hjsj.sys.FieldItem,
				 com.hjsj.hrms.utils.PubFunc,
				 java.util.ArrayList,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView,				 
				 com.hrms.frame.utility.AdminCode"%>
<%	
	String a0 = PubFunc.encryption("0");
	String a1 = PubFunc.encryption("1");
	String a2 = PubFunc.encryption("2");
	WorkdiaryForm myform=(WorkdiaryForm)session.getAttribute("workdiaryForm");
	String pendingCode = myform.getPendingCode();
	String doneFlag = myform.getDoneFlag();
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
	
	String state = "";
	if(request.getParameter("state") != null){
		state = PubFunc.decryption((String)request.getParameter("state"));
	}
	
%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/wz_tooltip.js"></script>
<link href='/ext/ext6/resources/ext-theme.css' rel='stylesheet' type='text/css'><link>
<script language="JavaScript" src="/ext/ext6/ext-all.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>

<style type="text/css"> 
.scroll_box {
    height: 200px;    
    /* width: 100%;  */           
    overflow: auto;            
   	/*BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ;*/
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid ;
    
}

</style>
<SCRIPT Language="JavaScript">
dateFormat='yyyy-mm-dd';
var pendingCode="<%=pendingCode%>";
var zxgflag = "${workdiaryForm.zxgflag}";
function addiary(obj){
	var state = "${workdiaryForm.state}";
	if(state=='0'){
		var message=checkinfo();
		if(message.length>1){
			alert(message);
			return;
		}
		//workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_add=link&save=add";
		workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_add=link&save=add&fileFlag1=1";
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}else{
		obj.setAttribute("disabled", true);//lis 设置按钮不可点
		//workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_adds=link&save=add";
		workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_adds=link&save=add&fileFlag1=1";
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}
	
}
function loaddiary(){
	var state = "${workdiaryForm.state}";
	
		var flag = "${workdiaryForm.flag}";
		var appflag = "${workdiaryForm.appflag}";
		
		//var fileFlag = "${workdiaryForm.fileFlag}";
		
		var picturefile = document.getElementById("picturefile");
		if(!validateUploadFilePath(picturefile.value)){
			picturefile.outerHTML=picturefile.outerHTML; 
			return;
		}
			
		var message=checkinfo();
		if(message.length>1){
			alert(message);
			return;
		}
		if(flag=="0")
			workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_load=link&save=addload&returnch=${workdiaryForm.returnch}&checkApp=${workdiaryForm.checkResion}&fileFlag=0";
		else
			workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_load=link&save=updateload&returnch=${workdiaryForm.returnch}&checkApp=${workdiaryForm.checkResion}&fileFlag=1";
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	
}
///员工写完日志后报批  首先找出要报的领导
function appiary1(saveflag){
	// 验证能否报批
	var checkApp = "${workdiaryForm.checkApp}";
	var checkResion = "${workdiaryForm.checkResion}";
	if (checkApp == "0") {
		alert(getDecodeStr(checkResion));
		return ;
	}
	/****验证必填备注项是否填写了内容****/
<logic:iterate id="info" name="workdiaryForm" property="fieldlist" indexId="index">	
			<bean:define id="fid" name="info" property="itemid"/>
			<bean:define id="ftype" name="info" property="itemtype"/>
			<bean:define id="flen" name="info" property="itemlength"/>
			<logic:notEqual value="p0100" name="fid">
			<logic:notEqual value="nbase" name="fid">
			<logic:notEqual value="p0115" name="fid">
			<logic:notEqual value="p0104" name="fid">
			<logic:notEqual value="p0106" name="fid">
			<logic:notEqual value="p0114" name="fid">
			<logic:notEqual value="p0113" name="fid">
			<logic:notEqual value="e0122" name="fid">
			<logic:notEqual value="e01a1" name="fid">
			<logic:notEqual value="b0100" name="fid">
			<logic:notEqual value="a0101" name="fid">

			<logic:equal value="M" name="ftype">
				<logic:equal name="info" property="fillable" value="true">
					if(document.getElementById("${fid}").value==null||trim(document.getElementById("${fid}").value)==""){
						alert("<bean:write name="info" property="itemdesc"/>为必填项!");
						return;
					}
				
				</logic:equal>

			</logic:equal>

			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>

</logic:iterate>
	var hashvo=new ParameterSet();
	hashvo.setValue("saveflag",saveflag);
	var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'9020010015'},hashvo);
}
///如果通过验证，就准备报批。根据是否有直管领导、有几个直管领导分情况。如果有两个及以上直管领导，报批时会弹出框让你选择报给谁
function getSuperiorUser(outparamters)
{
	document.getElementById("curr_user").value="";
	if(outparamters.getValue("outname").length==1)
	{
		document.getElementById("curr_user").value=(outparamters.getValue("outname")[0].split(":"))[0];
		appiary(outparamters.getValue("saveflag"));
		
	}
	else if(outparamters.getValue("outname").length>1)
	{
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       if(return_vo!=null && return_vo.length>=0)
       {
       		document.getElementById("curr_user").value=return_vo;
       		appiary(outparamters.getValue("saveflag"));
       }
	} 
	else
	{
		appiary(outparamters.getValue("saveflag"));
	}
}
function appiary(saveflag){
	
	var state = "${workdiaryForm.state}";///=0日报 =1周报 =2月报
	if(!confirm(APP_OK+"?")){
		return false;
	}
	
	if(state=='0')///如果是日报
	{
		var message=checkinfo();
		if(message.length>1)
		{
			alert(message);
			return;
		}
	    if(pendingCode!=null && pendingCode!="" && pendingCode!='null')
	    {
	        document.getElementById("doneFlag").value="1";
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_addfrominface=link&save="+saveflag;	 
	    }
	    else
	    {
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_add=link&save="+saveflag;
	    }
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}
	else///如果是周报
	{
	    if(pendingCode!=null && pendingCode!="" && pendingCode!='null')
	    {
	        document.getElementById("doneFlag").value="1";
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_addsfrominface=link&save="+saveflag;	 
	    }
	    else
	    {
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_adds=link&save="+saveflag;
	    }
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}
	
}
function updatediary(){
	var state = "${workdiaryForm.state}";
	if(state=='0'){
	    if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_addfrominface=link&save=update&fileFlag1=1";	 
	    }else{
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_add=link&save=update&fileFlag1=1";
	    }
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}else{
	    if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_addsfrominface=link&save=update&fileFlag1=1";
	    }else{
	        workdiaryForm.action="/performance/workdiary/myworkdiary.do?b_adds=link&save=update&fileFlag1=1";
	    }
		workdiaryForm.target="_self";
		workdiaryForm.method="post";
		workdiaryForm.enctype="multipart/form-data";
		workdiaryForm.submit();
	}
}
function returnback(state){
	if(state==0){
		workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_search=link&state=<%=a0 %>&zxgflag="+zxgflag;
		workdiaryForm.submit();
	}else if(state==1){
		workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a1 %>";
		workdiaryForm.submit();
	}else if(state==2){
		workdiaryForm.action="/performance/workdiary/myworkweekshow.do?b_query=link&state=<%=a2 %>";
		workdiaryForm.submit();
	}else if(state==3){
		workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_query=link&state=<%=a0 %>";
		workdiaryForm.submit();
	} else if(state==4){
		workdiaryForm.action="/templates/index/hcm_portal.do?b_query=link";
		workdiaryForm.submit();
	}
}
function checkinfo(){
	var message="";
	return "";
	var startime="${workdiaryForm.startime}";
	var endtime="${workdiaryForm.endtime}";
	if(startime.length<1){
		message+="<bean:message key='workdiary.info.startime'/>\n";
	}else{
		if(!checkTime(startime)){
			message+=STAR_TIME_INPUT_ERROR+"!\n";
		}
	}
	if(endtime.length<1){
		message=message+"<bean:message key='workdiary.info.endtime'/>\n";
	}else{
		if(!checkTime(endtime)){
			message+=END_TIME_INPUT_ERROR+"!\n";
		}
	}
	if(startime>endtime){
		message+=STARTIME_THAN_ENDTIME+"!\n";
	}


	return message;
}
function checkTime(times){
	//var result=times.match(/^(\d{1,4})(-|\/)(\d{1,2})\2(\d{1,2})$/);
 	//if(result==null) return false;
 	//var d= new Date(result[1], result[3]-1, result[4]);
 	//return (d.getFullYear()==result[1]&&(d.getMonth()+1)==result[3]&&d.getDate());
 	return true;
}
function hides(hide1,hiede2,hide3){
	//Element.hide(hide1);
	//Element.hide(hide3);
	//Element.toggle(hiede2);
	document.getElementById(hide1).style.display='none';
	document.getElementById(hide3).style.display='none';
	document.getElementById(hiede2).style.display='';
}
function toggles(toggles1,toggles2,toggles3){
	//Element.toggle(toggles1);
	//Element.toggle(toggles3);
	//Element.hide(toggles2);
	document.getElementById(toggles1).style.display='';
	document.getElementById(toggles3).style.display='';
	document.getElementById(toggles2).style.display='none';
}
function deleteFile(fileid,p0100){
	if(!confirm("确认删除？")){
		return;
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("fileid",fileid+",");
	var fileFlag="${workdiaryForm.fileFlag}";
	hashvo.setValue("fileFlag",fileFlag);
	hashvo.setValue("flag","56");
	hashvo.setValue("p0100",p0100);
	var request=new Request({method:'post',asynchronous:false,onSuccess:pageupload,functionId:'2020030041'},hashvo);
}
function pageupload(outparamters){
	var p0100="";
	p0100=outparamters.getValue("p0100");
	workdiaryForm.action="/performance/workdiary/myworkdiaryshow.do?b_add=link&query=update&timestr=${param.timestr}&p0100="+p0100+"&returnch=${workdiaryForm.returnch}";
	workdiaryForm.submit();
}
function uploadFile(fileid){
	var hashvo=new ParameterSet();
	hashvo.setValue("check","outfile");
	hashvo.setValue("fileid",fileid);
	var fileFlag="${workdiaryForm.fileFlag}";
	hashvo.setValue("fileFlag",fileFlag);
	hashvo.setValue("flag","56");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'2020030040'},hashvo);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
function IsDigit(obj) {
	if((event.keyCode >= 45) && (event.keyCode <= 57)){
		var values=obj.value;
		if((event.keyCode == 46) && (values.indexOf(".")!=-1))
			return false;
		if((event.keyCode == 45) && (values.indexOf("-")!=-1))
			return false;
		if((event.keyCode == 46) && (values.length==0))
			return false;	
	}else{
		return false;
	}
}
//输入整数
function isBigNumBer(obj) {
var checkOK = "-0123456789.";
 		var checkStr = obj.value;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return;
  		var count = 0;
  		var theIndex = 0;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		if(ch=='-')
    		{
    			count=count+1;
    			theIndex=i+1;
    		}
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			   allValid = false;
   			   break;
  		    }
    		if (ch == ".")
    		{
     			 allNum += ".";
     			 decPoints++;
  			 }
    	  else if (ch != ",")
      		allNum += ch;
  		}
  	if(count>1 || (count==1 && theIndex>1))
  			allValid=false;
  	if (decPoints > 1 || !allValid) 
  	{
  		alert(INPUT_NUMBER_VALUE+'!');
  		obj.value='0'; 
  	    obj.focus();
  	} 	
}
</SCRIPT>
<hrms:themes />
<body><center>
<html:form action="/performance/workdiary/myworkdiary" enctype="multipart/form-data">
<html:hidden property="doneFlag" name="workdiaryForm"/>
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

	<bean:define id="p0100" name="workdiaryForm" property="p01Vo_myself.string(p0100)"/>
	<html:hidden name="workdiaryForm" property="p01Vo_myself.string(p0100)" />
	<fieldset align="center" style="width:80%;">
	<legend><bean:message key="workdiary.message.edit.log"/></legend>
	<table width="650" border="0" cellspacing="0" style='margin:0 auto;' cellpadding="0">	
	<tr>
	 <td>
	  <table width="650" border="0" cellspacing="0" align="center" cellpadding="0">	
			<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
			<logic:notEqual value="no" name="workdiaryForm" property="perPlanTable">
			<tr>
				<td>我的目标卡</td>
				<td>
					${workdiaryForm.perPlanTable}
				</td>
			</tr>
			</logic:notEqual>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist" indexId="index">									
					<bean:define id="fid" name="info" property="itemid"/>					
					<logic:equal value="p0104" name="fid">
					<tr>
					<td>
						<bean:write name="info" property="itemdesc"/><!-- 起始时间 -->
						</td>	
						<td>
						<logic:equal value="0" name="workdiaryForm" property="state">
							<logic:equal value="1" name="workdiaryForm" property="appflag">
								<input type='text' name='startime' class='TEXT_NB inputtext'   size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.startime}" disabled="true" />
							</logic:equal>
							<logic:equal value="0" name="workdiaryForm" property="appflag">
								<input type='text' name='startime' class='TEXT_NB inputtext'   size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.startime}"  disabled="true"/>
							</logic:equal>
						</logic:equal>
						<logic:notEqual value="0" name="workdiaryForm" property="state">
							<input type='text' name='startime' class='TEXT_NB inputtext'   size='20' value="${workdiaryForm.startime}" disabled="true"/>
						</logic:notEqual>
						</td>
					</TR>
					</logic:equal>
			</logic:iterate>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist" indexId="index">	
					<bean:define id="fid" name="info" property="itemid"/>
					<logic:equal value="p0106" name="fid">
					<tr>
					<td >
						<bean:write name="info" property="itemdesc"/><!-- 终止时间 -->	
						</td>
						<td>
						<logic:equal value="0" name="workdiaryForm" property="state">
							<logic:equal value="0" name="workdiaryForm" property="appflag">
								<input type='text' name='endtime' class='TEXT_NB inputtext'   size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.endtime}" disabled="true"/>		
							</logic:equal>
							<logic:equal value="1" name="workdiaryForm" property="appflag">
								<input type='text' name='endtime' class='TEXT_NB inputtext'   size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.endtime}" disabled="true" />		
							</logic:equal>
						</logic:equal>
						<logic:notEqual value="0" name="workdiaryForm" property="state">
							<input type='text' name='endtime' class='TEXT_NB inputtext'   size='20' value="${workdiaryForm.endtime}" disabled="true"/>
						</logic:notEqual>
						</td>
					</TR>
					</logic:equal>
			</logic:iterate>
			<bean:define id="disa" name="workdiaryForm" property="dis"/>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist" indexId="index">	
			<bean:define id="fid" name="info" property="itemid"/>
			<bean:define id="ftype" name="info" property="itemtype"/>
			<bean:define id="flen" name="info" property="itemlength"/>
			<logic:notEqual value="p0100" name="fid">
			<logic:notEqual value="nbase" name="fid">
			<logic:notEqual value="p0115" name="fid">
			<logic:notEqual value="p0104" name="fid">
			<logic:notEqual value="p0106" name="fid">
			<logic:notEqual value="p0114" name="fid">
			<logic:notEqual value="p0113" name="fid">
			<logic:notEqual value="e0122" name="fid">
			<logic:notEqual value="e01a1" name="fid">
			<logic:notEqual value="b0100" name="fid">
			<logic:notEqual value="a0101" name="fid">
			<logic:equal value="A" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
		    <%		
		         
		      WorkdiaryForm workdiaryForm=(WorkdiaryForm)session.getAttribute("workdiaryForm");
		      RecordVo p01Vo_myself=(RecordVo)workdiaryForm.getP01Vo_myself();
		      
		      String abean=(String)pageContext.getAttribute("fid");
		      FieldItem abean2=(FieldItem)pageContext.getAttribute("info");
		      
		      String id=(String)abean2.getCodesetid();
		      

		      String levelDesc2=AdminCode.getCodeName(id,p01Vo_myself.getString(abean));
		  
		     %>
		    
		    
             <input type="text" name="${fid}_viewvalue" value="<%=levelDesc2%>" />
             <html:hidden name="workdiaryForm" property="p01Vo_myself.string(${fid})" />
             <logic:equal value="A" name="ftype">
             <%if(!id.equals("0")&&!id.equals("")){ %>
			 <img  src="/images/code.gif" align="absmiddle" onclick='javascript:openInputCodeDialogText("<bean:write name="info" property="codesetid"/>","${fid}_viewvalue","p01Vo_myself.string(${fid})");'/>&nbsp;
			 <%} %>
			 </logic:equal>	
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="D" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
			<html:text styleId="${fid}" name="workdiaryForm" property="p01Vo_myself.date(${fid})" maxlength="${flen}" disabled="${disa}" styleClass="inputtext"></html:text>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="N" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
			<html:text styleId="${fid}" name="workdiaryForm" property="p01Vo_myself.double(${fid})" maxlength="${flen}"  onblur="isBigNumBer(this);"
			disabled="${disa}" onkeypress="event.returnValue=IsDigit(this);" styleClass="inputtext"></html:text>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="M" name="ftype">
			<tr>
			<td colspan="2">
				<table width="100%" border="0">
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
							<span id="${fid}view">
							<a href='javascript:hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/>
							<logic:equal name="info" property="fillable" value="true"><font color="red">*</font></logic:equal>
							</a>
							<img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0">
							</span>
							<span id="${fid}hide" style="display: none;">
							<a href='javascript:toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/>
							<logic:equal name="info" property="fillable" value="true"><font color="red">*</font></logic:equal>
							</a>
							<img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0">
							</span>
						</td>
					</tr>
				</table>
				<span id="${fid}_view">
				<table width="100%" border="0">
					<tr>
						<td width="55">&nbsp;</td>
						<td style="word-break: break-all; word-wrap:break-word;">
							<html:textarea styleId="${fid}" name="workdiaryForm" property="p01Vo_myself.string(${fid})" readonly="${disa}" cols='80'  rows='20'></html:textarea>
						</td>
					</tr>
				</table>
				</span>
			</td>
			</tr>
			</logic:equal>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			<logic:notEqual value="0" name="workdiaryForm" property="flag">
			<logic:notEqual value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
			<logic:equal value="p0113" name="fid">
			<tr>
			<td colspan="2">
				<table width="100%" border="0">
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
							<span id="${fid}view">
							<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
							<span id="${fid}hide" style="display:none">
							<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
						</td>
					</tr>
				</table>
				<span id="${fid}_view">
				<table width="100%" border="0">
					<tr>
						<td width="55">&nbsp;</td>
						<td style="word-break: break-all; word-wrap:break-word;">
							<html:textarea name="workdiaryForm" property="p01Vo_myself.string(p0113)"  cols='80' rows='6' readonly="true"></html:textarea>
						</td>
					</tr>
				</table>
				</span>
			</td>
			</tr>
			</logic:equal>
			</logic:notEqual>
			</logic:notEqual>
			</logic:iterate>
			<tr>
			<td colspan="2">
				<table width="100%" border="0" >
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
						<!-- 2015/12/25 wangjl 附件默认收起 -->
							<span id="file_idview" style="display:none">
							<a href="###" onclick='hides("file_idview","file_idhide","file_id_view");'>附件</a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("file_idview","file_idhide","file_id_view");' border="0"></a>
							</span>
							<span id="file_idhide">
							<a href="###" onclick='toggles("file_idview","file_idhide","file_id_view");'>附件</a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("file_idview","file_idhide","file_id_view");' border="0"></a>
							</span>
						</td>
					</tr>
				</table>
				<span id="file_id_view" style="display:none">
				
				<table width="640" border="0" cellspacing="0" cellpadding="0" class="ListTable">
					<tr>
						<td width="55">&nbsp;</td>
						<td >
						<div class="scroll_box common_border_color">
						<table width="100%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTable">
						<tr class="fixedHeaderTr">
							<td align="center" class="TableRow_right common_background_color common_border_color" nowrap>
								附件名称
							</td>
							<td align="center" width="60" class="TableRow_left common_background_color common_border_color" nowrap>
								下载
							</td>
							<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<td align="center" width="60" class="TableRow_left common_background_color common_border_color" nowrap>
								操作
							</td>
							</logic:equal>
							<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<td align="center" width="60" class="TableRow_left common_background_color common_border_color" nowrap>
								操作
							</td>
							</logic:equal>
						</tr>
						<%int i=0;%>
						<hrms:paginationdb id="element" name="workdiaryForm"
							sql_str="select file_id,name,P0100" table="" where_str="from per_diary_file where P0100='${p0100}'"
							columns="file_id,name,P0100" page_id="pagination"
							pagerows="100"
							order_by="order by file_id">
							<bean:define id="file_id" name="element" property="file_id" />
							 <% if(i%2==0){%>
          					<tr class="trShallow">
          					<%}else{%>
          					<tr class="trDeep">
          					<%}i++;%>  
							<td align="center" class="RecordRow_right" nowrap>
								<bean:write name="element" property="name" filter="true" />
							</td>
							<td align="center" class="RecordRow" nowrap>
								<a style="cursor:hand;color:#0000FF" href="javascript:uploadFile('${file_id}');">下载</a>
							</td>
							<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<td align="center" class="RecordRow_left" nowrap>
								<a style="cursor:hand;color:#0000FF" href="javascript:deleteFile('${file_id}','${p0100}');">删除</a>
							</td>
							</logic:equal>
							<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<td align="center" class="RecordRow_left" nowrap>
								<a style="cursor:hand;color:#0000FF" href="javascript:deleteFile('${file_id}','${p0100}');">删除</a>
							</td>
							</logic:equal>
							</tr>
						</hrms:paginationdb>
					</table>
					</div>
					</td>
					</tr>
					<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
					<tr>
					<td>&nbsp;</td>
					<td>
					<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td class="tdFontcolor common_border_color" width="100">
							文件名称：
						</td>
						<td class="tdFontcolor common_border_color">
							<html:text name="workdiaryForm" property="filename" styleClass="textborder common_border_color" size="20"/>
						</td>
					</tr>
					<tr>
						<td class="tdFontcolor common_border_color" width="100">
							上传文件路径：
						</td>
						<td  nowrap class="tdFontcolor common_border_color">
							<html:file name="workdiaryForm" property="picturefile" onchange="loaddiary();" styleClass="textborder common_border_color" size="20"/>
						</td>
					</tr>
				</table>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
					<tr>
					<td>&nbsp;</td>
					<td>
					<table width="100%" class="RecordRowP" align="center">
					<tr>
						<td class="tdFontcolor" width="100">
							文件名称：
						</td>
						<td class="tdFontcolor">
							<html:text name="workdiaryForm" property="filename" styleClass="text6" size="20"/>
						</td>
					</tr>
					<tr>
						<td class="tdFontcolor" width="100">
							上传文件路径：
						</td>
						<td  nowrap class="tdFontcolor"><html:file name="workdiaryForm" property="picturefile" onchange="loaddiary();" styleClass="text6" size="20"/>
						</td>
					</tr>
				</table>
			</td>
			</tr>
			</logic:equal>
			</table>
			</span>
			</td>
			</tr>
			
			<tr>
			<td colspan="2">
				<table width="100%" border="0">
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
						<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<span id="personview">
							<a href="###" onclick='hides("personview","personhide","person_view");'>抄送人员</a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("personview","personhide","person_view");' border="0"></a>
							</span>
						</logic:equal>
						<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<span id="personhide" style="display:none">
							<a href="###" onclick='toggles("personview","personhide","person_view");'>抄送人员</a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("personview","personhide","person_view");' border="0"></a>
							</span>
						</logic:equal>
						<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<span id="personview">
							<a href="###" onclick='hides("personview","personhide","person_view");'>抄送人员</a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("personview","personhide","person_view");' border="0"></a>
							</span>
						</logic:equal>
						<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<span id="personhide" style="display:none">
							<a href="###" onclick='toggles("personview","personhide","person_view");'>抄送人员</a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("personview","personhide","person_view");' border="0"></a>
							</span>
						</logic:equal>
						</td>
					</tr>
				</table>
				<span id="person_view">
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable">
					<tr>
						<td width="55">&nbsp;</td>
						<td >
						<bean:define id="p0100" name="workdiaryForm" property="p01Vo_myself.string(p0100)"></bean:define>
						<logic:equal value="07" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<input type="button" value="抄送" class="mybutton" onclick="chaosong('<%=p0100 %>');">
							<html:text name="workdiaryForm" property="personname" styleClass="text" disabled="${disa}" size="60" readonly="true"/>
							<html:hidden name="workdiaryForm" property="personid"/>
						</logic:equal>
						<logic:equal value="01" name="workdiaryForm" property="p01Vo_myself.string(p0115)">
							<input type="button" value="抄送" class="mybutton" onclick="chaosong('<%=p0100 %>');">
							<html:text name="workdiaryForm" property="personname" styleClass="text" disabled="${disa}" size="60" readonly="true"/>
							<html:hidden name="workdiaryForm" property="personid"/>
						</logic:equal>
							
						</td>
					</tr>
				</table>
				</span>
			</td>
			</tr>
			<tr>
			<td colspan="2">&nbsp;</td>
			</tr>
	</table>
	</td>
  </tr>
</table>
</fieldset>
<table width="80%" border=0 align="center">
	<tr>
		<td align="center" style="height:20px;">
			<input type="hidden" name="curr_user" id="curr_user" />
			<logic:equal value="0" name="workdiaryForm" property="flag">
				<!-- 未填进入 -->
				<BUTTON name="app" class="mybutton"  onclick="appiary1('app');" >报批</BUTTON>
				<logic:equal value="0" name="workdiaryForm" property="fileFlag">
					<BUTTON name="add" class="mybutton"  onclick="addiary(this);" ><bean:message key="button.save"/></BUTTON>
				</logic:equal>
				<logic:equal value="1" name="workdiaryForm" property="fileFlag">
					<BUTTON id="edit" name="add" class="mybutton"  onclick="updatediary();" ><bean:message key="button.save"/></BUTTON>
				</logic:equal>
			</logic:equal>
			
			<logic:notEqual value="1" name="workdiaryForm" property="appflag">
				<logic:equal value="1" name="workdiaryForm" property="flag">
					<!-- 起草或驳回进入 -->
					<BUTTON id="baopi" name="app" class="mybutton"  onclick="appiary1('updateapp');" >报批</BUTTON>
					<logic:equal value="0" name="workdiaryForm" property="fileFlag">
						<BUTTON id="edit" name="add" class="mybutton"  onclick="updatediary();" ><bean:message key="button.save"/></BUTTON>
					</logic:equal>
					<logic:equal value="1" name="workdiaryForm" property="fileFlag">
						<BUTTON id="edit" name="add" class="mybutton"  onclick="updatediary();" ><bean:message key="button.save"/></BUTTON>
					</logic:equal>
				</logic:equal>
			</logic:notEqual>
			
			<logic:equal value="0" name="workdiaryForm" property="state">
				<logic:notEqual value="1" name="workdiaryForm" property="returnch">
					<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null") ) { %>
						<% }else { %> 
					<BUTTON name="returns" class="mybutton"  onclick="returnback(0);" ><bean:message key="button.return"/></BUTTON>
					<% }  %>
				</logic:notEqual>
				<logic:equal value="1" name="workdiaryForm" property="returnch">
					<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null") ) {
					%>
						<% }else { 
						if("0".equals(state)){
						%> 
						<BUTTON name="returns" class="mybutton"  onclick="returnback(3);" ><bean:message key="button.return"/></BUTTON>
						<% }else { %> 
					<!-- 【6622】我的任务中点开周报驳回信息后，点返回按钮返回的界面没有到首页，不对。 jingq upd 2015.02.04 -->
					<%-- <BUTTON name="returns" class="mybutton"  onclick="returnback(3);" ><bean:message key="button.return"/></BUTTON> --%>
					<BUTTON name="returns" class="mybutton"  onclick="returnback(4);" ><bean:message key="button.return"/></BUTTON>
					<% } %> 
					<% }  %>
				</logic:equal>
			</logic:equal>
			
			<logic:equal value="1" name="workdiaryForm" property="state">
				<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null") ) {%>
					<% }else { %> 
				<BUTTON name="returns" class="mybutton"  onclick="returnback(1);" ><bean:message key="button.return"/></BUTTON>
				<% }  %>
			</logic:equal>
				
			<logic:equal value="2" name="workdiaryForm" property="state">
				<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null") ) {%>
					<% }else { %> 
				<BUTTON name="returns" class="mybutton"  onclick="returnback(2);" ><bean:message key="button.return"/></BUTTON>
				<% }  %>
			</logic:equal>
			<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null") ) {%>
			 <input type="button" name="close" value="关闭" onclick="javascript:window.close();" class="mybutton"/>
			<% } %>	
		</td>
	</tr>
	</table>
<script language="javascript">
  <% if(pendingCode!=null && pendingCode!="" && !pendingCode.equals("null") && doneFlag!=null && doneFlag.equals("1")){%>
   document.getElementById("baopi").disabled=true; 
   document.getElementById("edit").disabled=true; 
<% }%>
</script>
</html:form></center>
</body>
<SCRIPT Language="JavaScript">
function Skip(){
	var chflag = "${param.returnch}";
	if(chflag!=null&&chflag.length>0)
		document.body.scrollTop=document.body.scrollHeight;
}
function perPlan(plan_id,a0100){
	//var theURL = "/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=5&model=2&opt=0&planid="+plan_id+"&object_id="+a0100;
	var theURL = "/performance/objectiveManage/objectiveCard.do?b_query=query`fromflag=rz`body_id=5`model=2`opt=0`planid="+plan_id+"`object_id="+a0100;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theURL);
	
	
	var return_vo = window.showModalDialog(iframe_url,"",
			"dialogWidth=800px;dialogHeight=500px;resizable=yes;scroll:yes;center:yes;status=no;");   	
}
function chaosong(p0100){
	var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating1=link`pri=0`chkflag=11`p0100="+p0100;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    if(/msie/i.test(navigator.userAgent)){
	    var return_vo= window.showModalDialog(iframe_url, 'trainClass_win2', "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
	    chaosong_ok(return_vo);
		return ;
	} else {
		var picker = new PersonPicker({
			multiple : true,
			text : "确定",
			titleText : "选择抄送人",
			isPrivExpression:false,
			callback :function(c){
				var idArray = [];
				for (var i = 0; i < c.length; i++) {
					var staffId = c[i].id;
					idArray.push(staffId);
				}
				chaosong_ok(idArray, '1');
			} 
		}, this);
		picker.open();	
	}
}
function chaosong_ok(return_vo, bencrypt){
	if(return_vo!=null){
		var recordstr = "";
		for(var i=0;i<return_vo.length;i++){
			if(return_vo[i]!=null&&return_vo[i].length>0){
				recordstr+=return_vo[i]+"`";
			}
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("personstr",recordstr);
		if(bencrypt){
			hashvo.setValue("bencrypt", bencrypt);
		}
		var request=new Request({method:'post',onSuccess:showResult,functionId:'9020010014'},hashvo);
	}
}
function showResult(outparamters){ 
   	 if(outparamters.getValue("result")=="success"){
   	 	document.getElementsByName("personname")[0].value=outparamters.getValue("personname");
   	 	document.getElementsByName("personid")[0].value=outparamters.getValue("personid");
   	 } 
}
if(!getBrowseVersion()){//兼容非IE浏览器样式问题   bug 34768 wangb 20180209
	var fieldset = document.getElementsByTagName('fieldset')[0]; //表单域 文字靠左显示
	fieldset.setAttribute('align','left');
	var inputtext1 = fieldset.getElementsByClassName('inputtext')[0]; //文本框 边框靠一起
	inputtext1.style.marginBottom='1px';
}
</script>
