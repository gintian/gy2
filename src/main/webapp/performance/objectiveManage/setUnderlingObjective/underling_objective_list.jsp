<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm" %>
<%@ page import="
				com.hrms.hjsj.sys.DataDictionary,
				com.hrms.hjsj.sys.FieldItem,
				com.hrms.struts.constant.SystemConfig,com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant" %>
				
<%
	String isEpmLoginFlag="0";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	  hcmflag=userView.getBosflag();
	}   

   String tt4CssName="ttNomal4";
   String tt3CssName="ttNomal3";
   String buttonClass="mybutton";
   boolean flag2=true;
   if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt"))
   {
      tt4CssName="tt4";
      tt3CssName="tt3";
      buttonClass="mybuttonBig";
      flag2=false;
   }
     String editDesc="设定";
    if(SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("bjpt"))
    {
      editDesc="制订";
    }
   boolean flag=true;
   if(SystemConfig.getPropertyValue("isVisibleUN").equalsIgnoreCase("false"))
   {
     flag=false;
   }
SetUnderlingObjectiveForm setUnderlingObjectiveForm = (SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
String returnflag=setUnderlingObjectiveForm.getReturnflag();
	if(returnflag==null)
		returnflag="menu";
String url_extends="";
	if(returnflag.equals("10"))
		url_extends="&returnflag=10";
	else
		url_extends="&returnflag=menu";		
		
String target="mil_body";
String entranceType=setUnderlingObjectiveForm.getEntranceType();
String lt="&zglt=0";
if(!entranceType.equals("0"))
{
      target="_self";
      lt="&zglt=3";
}
 
	    String url_p=SystemConfig.getServerURL(request);
      %>
<script type="text/javascript">
<!--
var pa="-1";
var a0100="<%=userView.getA0100()%>";
function query()
{
   setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&opt=2";
   setUnderlingObjectiveForm.submit();
}

//返回 中国联通用
function returnWel()
 {
     window.parent.location.href="/templates/attestation/unicom/performance.do?b_query=link";
 }
function unberlingObjective(opt,planid,a0100,level)
{
   if(pa =="-1"||pa!=planid+a0100)
   {
      pa=planid+a0100;
      var entranceType="${setUnderlingObjectiveForm.entranceType}";
      objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query<%=lt%><%=url_extends%>&entranceType="+entranceType+"&body_id="+level+"&model=3&opt="+opt+"&planid="+planid+"&object_id="+a0100;
      objectCardForm.submit();
   }
    else
   {
     window.setTimeout('setPAValue()',2000);   
   }
}
// 目标卡已批或驳回也让调整 JinChunhai 2013.03.26
function unberlingObjective2(opt,planid,a0100,level)
{
   if(pa =="-1"||pa!=planid+a0100)
   {
      pa=planid+a0100;
      var entranceType="${setUnderlingObjectiveForm.entranceType}";
      objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query<%=lt%><%=url_extends%>&entranceType="+entranceType+"&body_id="+level+"&model=7&opt="+opt+"&planid="+planid+"&object_id="+a0100;
      objectCardForm.submit();
   }
    else
   {
     window.setTimeout('setPAValue()',2000);   
   }
}

function searchReject(object_id,plan_id)
{
    var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=1`type=2`plan_id="+plan_id+"`object_id="+object_id; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	
	// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
	if(/msie/i.test(navigator.userAgent)){
		var retvo= window.showModalDialog(iframe_url, null, "dialogWidth:600px; dialogHeight:465px;resizable:no;center:yes;scroll:yes;status:no");
		return ;
	} else {
		function openWin(){
		    Ext.create("Ext.window.Window",{
		    	id:'searchreject_win',
		    	width:600,
		    	height:480,
		    	title:'退回',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	closeAction:'destroy',
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
		    }).show();
		}
		
		if(typeof window.Ext == 'undefined'){
			insertFile("/ext/ext6/resources/ext-theme.css","css", function(){
				insertFile("/ext/ext6/ext-all.js","js" ,openWin);
			});
			
		} else {
			openWin();
		}
	}
}
function searchReject_close(){
	Ext.getCmp('searchreject_win').close();
}
function setPAValue()
{
   pa="-1";
}
function allSelect(obj)
{
   var arr=document.getElementsByName("oid");
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
        if(obj.checked)
            arr[i].checked=true;
        else
            arr[i].checked=false;
      }
   }
}
function report()
{
   var arr=document.getElementsByName("oid");
   var lev=document.getElementsByName("alevel");
   var num=0;
   var ids="";
   if(arr)
   {
      for(var i=0;i<arr.length;i++)
      {
         if(arr[i].checked)
         {
            num++;
            ids+=","+arr[i].value+"/"+lev[i].value;
         }
      }
   }
   if(num==0)
   {
     alert("请选择要上报的考核对象！");
     return;
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("planid","${setUnderlingObjectiveForm.plan_id}");
    hashVo.setValue("record",ids.substring(1));
    hashVo.setValue("model","3");
    hashVo.setValue("url_p",document.getElementById("hostname").href);
	var request=new Request({method:'post',asynchronous:false,onSuccess:export_ok,functionId:'30200710257'},hashVo);			
	
   
}
function export_ok(outparameters)
{
   var info=getDecodeStr(outparameters.getValue("info"));
   if(info=='1')
   {
      alert("统一报批/批准成功！");
      setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&opt=2";
      setUnderlingObjectiveForm.submit();
   }
   else
   {
      alert(info);
      return;
   }
}

function goback(returnflag)
{
    var isEpmLoginFlag = "<%=isEpmLoginFlag %>";
    if(isEpmLoginFlag=="1"){
           window.location='/templates/index/subportal.do?b_query=link';
    }else{
		if(returnflag=="8"){
			if('<%=hcmflag%>'=="hcm"){
 	      		 window.parent.location='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		window.parent.location='/templates/index/portal.do?b_query=link';      		
       		}
			
		}
		else if(returnflag=="10"){
		   window.parent.location='/general/template/matterList.do?b_query=link';
		}
	}
}

function bacthSp()
{
   var records=document.getElementsByName("oid");
   var bacthdata=document.getElementsByName("bacthdata");
   var num=0;
   var selectRecords="";
   var spable=0;
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
            num++;
            selectRecords+="/"+bacthdata[i].value;
            var t=bacthdata[i].value.split("`");
            var sp_flag=t[2];
			var curruser=t[3];
			var status=t[5];
			if(sp_flag=='02'&&curruser==a0100&&status!='5')
			{
			    spable++;
			}
         }
      }
   }
   if(num==0)
   {
      alert("请选择记录!");
      return;
   }
   if(spable==0)
   {
     alert("没有可审批数据!");
     return;
   }
    if(!confirm("确认执行批量审批?"))
      return;
   var isTargetCardTemp="${setUnderlingObjectiveForm.isTargetCardTemp}";
   var isEmail="0";
   if(isTargetCardTemp=="true") {
	   if(confirm("是否发送邮件?"))
	        isEmail="1";
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("ids",selectRecords.substring(1)); 
    hashVo.setValue("isEmail",isEmail);
    hashVo.setValue("url_p",document.getElementById("hostname").href);
	var request=new Request({method:'post',asynchronous:false,onSuccess:bacth_ok,functionId:'90100170020'},hashVo);			
}
function bacth_ok(outparameters)
{
  var info = outparameters.getValue("info");
  if(info=='0')
  {
    alert("批量审批成功！");
     setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&opt=2";
      setUnderlingObjectiveForm.submit();
	
  }
  else if(info=='1')
  {
    alert("没有可审批数据！");
    return;
  }else {
      alert(getDecodeStr(info));
       return;
  }
}
// 批量引入上期目标卡
function bacthImportPreCard(){
    var records=document.getElementsByName("oid");
    var bacthdata=document.getElementsByName("bacthdata");
    var eflags = document.getElementsByName("eflag");
    var currsp = document.getElementsByName("currsp");
    var num=0;
    var impotable=0;
    var object_ids = new Array();
    if(records)
    {
        for(var i=0;i<records.length;i++)
        {
            if(records[i].checked)
            {
                num++;
                var eflagvalue = eflags[i].value;
                var currspvalue = currsp[i].value;
                var t=bacthdata[i].value.split("`");
                var sp_flag=t[2];
                var objectId = t[1];
                //查看
                if (eflagvalue==6){
                    if(sp_flag=="01" && currspvalue){
                        object_ids.push(objectId);
                        impotable++;
                    }
                }else if (eflagvalue == 7 || eflagvalue == 8){
                    //=7 制定;=8 制定（调整）
                    if(currspvalue){
                        object_ids.push(objectId);
                        impotable++;
                    }
                }
            }
        }
    }
    if(num==0)
    {
        alert("请选择记录!");
        return;
    }
    if (impotable ==0){
        alert("没有要引入上期"+KH_OBJECTIVE_LABLE+"的考核对象!");
        return;
    }
    var hashvo=new ParameterSet();
    hashvo.setValue("object_ids",object_ids.join(","));
    hashvo.setValue("plan_id","${setUnderlingObjectiveForm.plan_id}");
    hashvo.setValue("opt","0");
    var request=new Request({method:'post',asynchronous:false,onSuccess:bacthImportPreCard2,functionId:'21111111116'},hashvo);
}

function bacthImportPreCard2(outparamters)
{
    var object_ids=outparamters.getValue("object_ids") ;
    var plan_id=outparamters.getValue("plan_id")
    if(object_ids.length>0)
    {
        if(confirm("您确定要引入上期"+KH_OBJECTIVE_LABLE+"吗？此操作会将当前"+KH_OBJECTIVE_LABLE+"中的数据清除!"))
        {
            if(confirm('为避免误操作带来严重后果，请再次确认!'))
            {
                var hashvo=new ParameterSet();
                hashvo.setValue("object_ids",object_ids);
                hashvo.setValue("plan_id",plan_id);
                hashvo.setValue("opt","1");
                var request=new Request({method:'post',asynchronous:false,onSuccess:importSuccess,functionId:'21111111116'},hashvo);
            }
        }
    }
    else
        alert("上期"+KH_OBJECTIVE_LABLE+"无合适的数据引入!");
}

function importSuccess(outparamters)
{
    var object_ids=getDecodeStr(outparamters.getValue("object_ids"));
    if(object_ids.length>0)
    {
        setUnderlingObjectiveForm.action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init&opt=2";
        setUnderlingObjectiveForm.submit();
    }
    else
        alert("上期目标卡无合适的数据引入!");
}
//导出目标卡
function downLoadTarget()
{	
/***
	var records=document.getElementsByName("oid");	
	var plan_id="${setUnderlingObjectiveForm.plan_id}";	
    var num=0;
    var selectTargetCalcItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{     		
	    	if(records[i].checked)
	        {
	        	num++;
	            selectTargetCalcItemts+="/"+records[i].value+"-"+plan_id;
	        }	        
      	}
   	}  	
   	if(num==0)
   	{
      	alert("请选择记录！");
      	return;
   	} 
 	var hashvo=new ParameterSet();     
	hashvo.setValue("records",selectTargetCalcItemts.substring(1));
//	hashvo.setValue("plan_id","${setUnderlingObjectiveForm.plan_id}");
	hashvo.setValue("model",'3');
	hashvo.setValue("body_id",'1');
	hashvo.setValue("underOpt",'1');
	hashvo.setValue("logo",'2');
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashvo);	
	***/
   
   var records=document.getElementsByName("oid");
   var bacthdata=document.getElementsByName("bacthdata");
   var plan_id="${setUnderlingObjectiveForm.plan_id}";	
   var num=0;
   var selectTargetCalcItemts="";
   
   var opt="";
   var body_id="";
   var  obj1 = document.getElementsByName("optflag");
   var  obj2 = document.getElementsByName("levelflag");
   if(records)
   {
      for(var i=0;i<records.length;i++)
      {
         if(records[i].checked)
         {
        	num++;
        	var t=bacthdata[i].value.split("`");
            var _obj_id=t[1];
            var _plan_id=t[0];
            selectTargetCalcItemts+="/"+_obj_id+"-"+_plan_id;
            opt+=obj1[i].value+"`";
            body_id+=obj2[i].value+"`";
            
         }
      }
   }
   if(num==0)
   {
      alert("请选择记录！");
      return;
   }
    var hashVo=new ParameterSet();
    hashVo.setValue("records",selectTargetCalcItemts.substring(1));
    hashVo.setValue("model",'3');/// 1:团对 2:我的目标 3:目标制订 4.目标评估 5.目标结果 6:目标执行情况 7:目标卡代制订 8:评分调整
	hashVo.setValue("body_id",body_id);
	hashVo.setValue("underOpt",opt);
	hashVo.setValue("logo",'2');// 1:我的目标；2:员工目标；3:目标评分；4:目标执行情况；5:团队绩效
	var In_parameters="opt=1";
	var request=new Request({method:'post',asynchronous:false,parameters:In_parameters,onSuccess:export_ok,functionId:'9028000405'},hashVo);	   	  	
}
function export_ok(outparameters)
{
	var fileName=outparameters.getValue("fileName");
//	var win=open("/servlet/DisplayOleContent?filename="+fileName,"excel");
	//20/3/6 xus vfs改造
  	var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");	
}

//-->
</script>
<html>
 <link href="/performance/objectiveManage/objectiveCard/objectiveCard.css" rel="stylesheet" type="text/css">
<head><title>title</title></head>
<body>
<base id="mybase" target="_self">
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td>
<html:form action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="left">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-bottom:5px;">
<td align="left" style="height:20px">   
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<html:hidden property="a0100" name="setUnderlingObjectiveForm"/>
<html:hidden property="plan_id" name="setUnderlingObjectiveForm"/>
<html:hidden property="posid" name="setUnderlingObjectiveForm"/>
<font class="<%=tt3CssName%>">目标卡<bean:message key="org.performance.status"/></font>:
<html:select name="setUnderlingObjectiveForm" property="status" size="1" onchange="query();">
			<html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
		    </html:select>
&nbsp;&nbsp;
<font class="<%=tt3CssName%>">部门</font>:
<html:select name="setUnderlingObjectiveForm" property="deptid" size="1" onchange="query();">
			<html:optionsCollection property="deptList" value="dataValue" label="dataName"/>
		    </html:select>
</td>
</table>
</td>
</tr>
<tr>
<td class="RecordRow" valign="top">
<div style='overflow:auto;width:100%;height:340'>
<table width="100%" border="0" cellspacing="0"  style="margin-top:-1" align="center" cellpadding="0" class="ListTable">
<thead>
<tr>

<td class="TableRow" align="center">
  <input type="checkbox" name="as" value="0" onclick="allSelect(this);" title="全选"/>
</td>
<%if(flag){ %>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="b0110.label"/></font>&nbsp;
  </td>
  <%} %>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><%
	         							FieldItem fielditem = DataDictionary.getFieldItem("E0122");
	         						%>	         
			 						<%=fielditem.getItemdesc()%></font>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="e01a1.label"/></font>&nbsp;
  </td>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="hire.employActualize.name"/></font>&nbsp;
  </td>
  <%if(flag2){ %>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="org.performance.status"/></font>&nbsp;
  </td>
  <%} %>
  <td align="center" class="TableRow" nowrap>
  &nbsp;<font class="<%=tt4CssName%>"><bean:message key="kh.field.opt"/></font>&nbsp;
  </td>
</tr>
</thead>
<% int j=0; %>
 <hrms:extenditerate id="element" name="setUnderlingObjectiveForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="${setUnderlingObjectiveForm.pagerows}" scope="session">
 <%if(j%2==0){ %>
	     <tr class="trShallow" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%} else { %>
	     <tr class="trDeep" onClick="javascript:tr_onclick(this,'#E4F2FC')">
	     <%}%>
	     <input type="hidden" name="optflag" value="<bean:write name="element" property="opt"/> ">
	     <input type="hidden" name="levelflag" value="<bean:write name="element" property="level"/> ">
         <input type="hidden" name="eflag" value="<bean:write name="element" property="flag"/> ">
         <input type="hidden" name="currsp" value="<bean:write name="element" property="currsp"/> ">
	     <%if(!flag2){ %>
	     <td class="RecordRow" align="center">
           <logic:equal value="1" name="element" property="currsp">
             <input type="checkbox" name="oid" value="<bean:write name="element" property="a0100"/>"/>
             <input type="hidden" name="alevel" value="<bean:write name="element" property="level"/>"/>
             <input type="hidden" name="bacthdata" value="<bean:write name="element" property="bacthdata"/>"/>
           </logic:equal>
         </td>
         <%}else{ %>
             <td class="RecordRow" align="center">
             <input type="checkbox" name="oid" value="<bean:write name="element" property="a0100"/>"/>
             <input type="hidden" name="alevel" value="<bean:write name="element" property="level"/>"/>
             <input type="hidden" name="bacthdata" value="<bean:write name="element" property="bacthdata"/>"/>
         </td>
         <%} %>
	     <%if(flag){ %>
	     <td align="left" class="RecordRow" nowrap>
	     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="b0110"/></font>&nbsp;
	     </td>
	     <%} %>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="e0122"/></font>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="e01a1"/></font>&nbsp;
	     </td>
	      <td align="left" class="RecordRow" nowrap>
	     &nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="a0101"/></font>&nbsp;
	     </td>
	     <%if(flag2){ %>
	      <td align="left" class="RecordRow" nowrap>
	       <logic:equal value="1" property="isReject" name="element">
	       <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>');">&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="sp_flag"/></font>&nbsp;</a>
	       </logic:equal>
	        <logic:equal value="0" property="isReject" name="element">
	      <logic:equal value="07" property="sp" name="element">
	      <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>');">&nbsp;<font class="<%=tt3CssName%>"><bean:write name="element" property="sp_flag"/></font>&nbsp;</a>
	      </logic:equal>
	      <logic:notEqual value="07" property="sp" name="element">&nbsp;<font class="<%=tt3CssName%>">
	       <logic:notEqual value="03" property="sp" name="element">
	      <bean:write name="element" property="sp_flag"/>
	      </logic:notEqual>
	      <logic:equal value="03" property="sp" name="element">
	        <logic:equal value="07" property="trace_flag" name="element">
	            <a href="javascript:searchReject('<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="mdplanid"/>');"><bean:write name="element" property="tsp"/></a>
	          </logic:equal>
	           <logic:notEqual value="07" property="trace_flag" name="element">
	            <bean:write name="element" property="tsp"/>
	          </logic:notEqual>
	      </logic:equal>
	      </font>&nbsp;</logic:notEqual>
	      </logic:equal>
	     </td>
	     <%} %>
	      <td align="center" class="RecordRow" nowrap>
	    <logic:equal value="6" name="element" property="flag">
	       <logic:equal value="1" name="element" property="currsp">
	       	    <a href="javascript:unberlingObjective('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="mdelement" property="a0100"/>','<bean:write name="element" property="level"/>');">
	       	    <font class="<%=tt3CssName%>">
	       	    <logic:equal value="01" property="sp" name="element">
	       	    <%=editDesc%>
	       	    </logic:equal>
	       	    <logic:equal value="02"  property="sp" name="element">
	       	    审核
	       	    </logic:equal>
	       	    </font>
	       	    </a>
	      </logic:equal>	
	      <logic:notEqual value="1" name="element" property="currsp">
	       	  <logic:equal value="2" name="element" property="currsp">
	      <a href="javascript:unberlingObjective('3','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">评价</font></a>
	      </logic:equal>
	      <logic:notEqual value="2" name="element" property="currsp">
	       <a href="javascript:unberlingObjective('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	        </logic:notEqual>
	      	  <logic:equal value="1" name="element" property="cardEdit">
	      		  <a href="javascript:unberlingObjective2('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">调整</font></a>	      	
	      	  </logic:equal>
	      </logic:notEqual>
	    </logic:equal>
	    <logic:equal value="7" name="element" property="flag">
	     <logic:equal value="1" name="element" property="currsp">
	       	    <a href="javascript:unberlingObjective('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>"> <logic:equal value="01" property="sp" name="element">
	       	     <%=editDesc%>
	       	    </logic:equal>
	       	    <logic:equal value="02"  property="sp" name="element">
	       	    审核
	       	    </logic:equal></font></a>
	      </logic:equal>	
	      <logic:notEqual value="1" name="element" property="currsp">
	      
	       <logic:equal value="2" name="element" property="currsp">
	      <a href="javascript:unberlingObjective('3','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">评价</font></a>
	      </logic:equal>
	      <logic:notEqual value="2" name="element" property="currsp">
	       <a href="javascript:unberlingObjective('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	        </logic:notEqual>
	      </logic:notEqual>
	    </logic:equal>
	    <logic:equal value="8" name="element" property="flag">
	               <logic:equal value="1" name="element" property="currsp">
	       	    <a href="javascript:unberlingObjective('1','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>"> <logic:equal value="01" property="sp" name="element">
	       	     <%=editDesc%>
	       	    </logic:equal>
	       	    <logic:equal value="02"  property="sp" name="element">
	       	    审核
	       	    </logic:equal></font></a>
	      </logic:equal>	
	      <logic:notEqual value="1" name="element" property="currsp">
	      <logic:equal value="2" name="element" property="currsp">
	      <a href="javascript:unberlingObjective('3','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">评价</font></a>
	      </logic:equal>
	      <logic:notEqual value="2" name="element" property="currsp">
	       <a href="javascript:unberlingObjective('0','<bean:write name="element" property="mdplanid"/>','<bean:write name="element" property="mda0100"/>','<bean:write name="element" property="level"/>');"><font class="<%=tt3CssName%>">查看</font></a>
	        </logic:notEqual>
	      </logic:notEqual>
	        </logic:equal>
			<logic:equal value="1" name="element" property="currsp">
			<img src="/images/new0.gif" border="0"/>
			</logic:equal>			  
	        <logic:notEqual value="1" name="element" property="currsp">
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			</logic:notEqual>	
	     </td>	     
	     </tr>
	     <% j++; %>
	     </hrms:extenditerate>
</table>
</div>
</td>
</tr>
<tr>
<td class="PersonRecordRow common_border_color">
<table  width="100%" align="center" >

    <tr>
        <td valign="bottom" class="tdFontcolor" nowrap>
            <bean:message key="label.page.serial"/>
            <bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.current" filter="true" />
            <bean:message key="label.page.sum"/>
            <bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.count" filter="true" />
            <bean:message key="label.page.row"/>
            <bean:write name="setUnderlingObjectiveForm" property="personListForm.pagination.pages" filter="true" />
            <bean:message key="label.page.page"/>&nbsp;&nbsp;
            每页显示<html:text property="pagerows" name="setUnderlingObjectiveForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:query();">刷新</a>
        </td>
        <td align="right" nowrap class="tdFontcolor">
            <p align="right">
                <hrms:paginationlink name="setUnderlingObjectiveForm" property="personListForm.pagination" nameId="personListForm" propertyId="personListProperty">
                </hrms:paginationlink>
        </td>
    </tr>
</table>
</td>
</tr>
<tr>
</tr>

<tr>

  <td align='left' style="padding-top:5px">
  
  <hrms:priv func_id="06070309">
  	  <input type="button" name="downLoad" class="<%=buttonClass%>" value="<bean:message key="info.appleal.state13"/>" onclick="downLoadTarget();"/>			
  </hrms:priv>
  
  <%if(!flag2){ %>
  <input type='button' name='allsubmit' class="<%=buttonClass%>" value="统一报批/批准" onclick="report();"/>
  <%} %>
  <input type='button' name='bac' class="<%=buttonClass%>" value="批量审批" onclick="bacthSp();"/>
  <input type='button' name='bac' class="<%=buttonClass%>" value="批量引入上期目标卡" onclick="bacthImportPreCard();"/>
  <logic:notEqual value="0" name="setUnderlingObjectiveForm" property="entranceType">
     <input type='button' name='clo' class="<%=buttonClass%>" value="<bean:message key="button.close"/>" onclick="window.close();"/>
 </logic:notEqual>
  <logic:equal value="0" name="setUnderlingObjectiveForm" property="entranceType">
  <% if(returnflag.equals("8")||returnflag.equals("10")){

	out.println("<input type='button' name='export' class='"+buttonClass+"' value='返回' onclick='goback(\""+returnflag+"\")' />");

	}else if(returnflag.equals("20")){
    	out.println("<input type='button' name='return' class='"+buttonClass+"' value='返回' onclick='returnWel();'/>");
    }
 %>
  </logic:equal>
  </td>
</tr>

</table>
</html:form>
</td>
</tr>
<tr>
<td>
<html:form action="/performance/objectiveManage/objectiveCard">
<input type="hidden" name="returnURL" value="/performance/objectiveManage/setUnderlingObjective/underling_objective_list.do?b_init=init2&entranceType=${setUnderlingObjectiveForm.entranceType}&opt=3&posid=${setUnderlingObjectiveForm.posid}&plan_id=${setUnderlingObjectiveForm.plan_id}&a0100=${setUnderlingObjectiveForm.a0100}&status=${setUnderlingObjectiveForm.status}&deptid=${setUnderlingObjectiveForm.deptid}"/>
<input type="hidden" name="target" value="<%=target%>"/>
</html:form>
</td></tr>
</table>
</body>
</html>