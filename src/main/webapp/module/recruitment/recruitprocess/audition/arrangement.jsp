<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.module.recruitment.recruitprocess.actionform.ArrangementInfoForm"%>
<%@ page import="java.util.HashMap" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<meta http-equiv="X-UA-Compatible" content="IE=7;IE=8;IE=9;">
<script language="JavaScript" src="/components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="/module/recruitment/recruitprocess/audition/arrangement.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>
<script language="JavaScript" src="/ext/adapter/jquery/jquery.js"></script>
<script language="JavaScript" src="/components/dateTimeSelector/dateTimeSelector.js"></script>
<link href="/ext/ext6/resources/ext-theme.css" rel="stylesheet" type="text/css"/>
<link href="/components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"/>
<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
  <head>
  <style type="text/css">
    .x-body{color:#555;}
    .hj-zm-msap-four TABLE TR{line-height: 22px;}
   <%-- .x-form-text-wrap-default {border:none}--%>
    .x-webkit .x-form-text{height: calc(100% + 2px)}
    .x-ie8 .x-form-text-default {min-height: 22px}
    .x-html-editor-input {border: 1px solid #b5b8c8}
  </style>
    <base href="<%=basePath%>">
    
    <title>面试安排</title>
<script type="text/javascript">
$(window).resize(function() { 
	var width = $(this).width();    
	var height = $(this).height();
	if(width<1050) {
		document.body.style.width="1100";
	}
});

Ext.onReady(function(){
	Ext.widget('datetimefield',{
		id:'arrangeDate',
		renderTo:'dateId',
		width:150,
		height:23,
		format:'Y-m-d H:i',
		value:'<bean:write name='arrangementInfoForm' property='arrangementInfo.Z0509'/>',
		listeners: {
			'change': function(obj, newValue, oldValue, eOpts ){
				Global.replaceMethodDate(newValue);
			}
    	}});
	
	var map = new HashMap();
	map.put("sub_module", '7');
	map.put("nModule", "20");
	map.put("a0100s","<bean:write name='arrangementInfoForm' property='resumeInfo.a0100'/>");
	//map.put("b0110", 'UN0101');
	map.put("z0301","<bean:write name='arrangementInfoForm' property='resumeInfo.z0301'/>");
	map.put("c0102", "<bean:write name='arrangementInfoForm' property='emailInfo.c0102s'/>");
	Rpc( {
		functionId : 'ZP0000002000',
		success :Global.emailPanel
	}, map);
    var areas = document.getElementById("content");
    if(areas!=null)
    {
	    if ("oninput" in areas) { // W3C标准浏览器
	    	areas.oninput = adapt.bind(adapt.adaptTextareaHeight, null, areas);
	    } else { // IE
	        function adapt4IE(t) {
	            t.style.height = (t.scrollHeight > adapt.minHeight ? t.scrollHeight : adapt.minHeight) + "px";
	        }
	        areas.onpropertychange = adapt.bind(adapt4IE, null, areas);
	        areas.onkeyup = adapt.bind(adapt4IE, null, areas);
	    }
    }

    adapt.adaptTextareaHeight();
    var str = '<bean:write name="arrangementInfoForm" property="arrangementInfo.Z0529"/>';
    if(str==1)
    {
    	$("#sendEmailDiv").show();
    }
    Ext.widget('viewport',{
        layout:'border',
        padding:"0 5 0 5",
        style:'backgroundColor:white',
        items:[{
                  xtype:'panel',
                  id:'view_panel',
                  title:"<div id='headPanel'></div>",
                  html:"<div id='topPanel'></div>",
                  region:'north',border:false
                }]
    });
    Ext.get("view_panel-headingEl").destroy();
    document.getElementById('headPanel').appendChild(document.getElementById('header'));
    document.getElementById('header').style.display="block";
    document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
    document.getElementById('funcDiv').style.display="block";
    var view_panel = Ext.getCmp('view_panel');
    view_panel.setAutoScroll(true);
    var winHeight =parent.document.body.clientHeight;
    view_panel.setHeight(winHeight);

})
 var adapt = adapt || {
         minHeight: 150,
         adaptTextareaHeight: function(t) { // 文本域高度自适应
             var areas = [];
             
             if (t) { // 指定对某一个文本域自适应
                 areas[0] = t;
             } else { // 对所有的文本域自适应
                 areas = document.getElementsByTagName("textarea");
             }

             var _area = "content";
             
             for (var i = 0; i < areas.length; i++) {
                 // 不对总结之外的文本域做修改
                 if (_area.indexOf(areas[i].id) < 0 || !areas[i].id) {continue;}
                 
                 var btw = adapt.style(areas[i]).borderTopWidth;
                 var bbw = adapt.style(areas[i]).borderBottomWidth;
                 
                 var iBtw = parseInt(btw.substring(0, btw.length - 2)) || 0;
                 var iBbw = parseInt(bbw.substring(0, bbw.length - 2)) || 0;
                 
                 areas[i].style.height = adapt.minHeight + "px";
                 
                 var adaptHeight = areas[i].scrollHeight + iBtw + iBbw;
                 adaptHeight = adaptHeight < adapt.minHeight ? adapt.minHeight : adaptHeight;
                 
                 areas[i].style.height = adaptHeight + "px";
             }
         },
         style: function(elmt) { // 获取元素计算后的样式
             if (elmt.currentStyle) {
                 return elmt.currentStyle;
             } else {
                 return window.getComputedStyle(elmt);
             }
         },
         bind: function(fn, thisObj) { // 创建闭包环境,用于参数传递
             if (!fn || typeof fn !== "function") {return null;}
             
             var args = [];
             if (arguments[2]) {
                 for (var i = 2; i < arguments.length; i++) {
                     args[args.length] = arguments[i];
                 }
             }
             
             return (function() {
                 fn.apply(thisObj, args);
             });
         }
         
     };
</script>
  </head>
  <body> 
  <div id="bodyWidth">
  <input type="hidden" id="link_id" value="<bean:write name='arrangementInfoForm' property='resumeInfo.link_id'/>"/>
  <input type="hidden" id="node_id" value="<bean:write name='arrangementInfoForm' property='resumeInfo.node_id'/>"/>
  <input type="hidden" id="page" value="<bean:write name='arrangementInfoForm' property='resumeInfo.page'/>"/>
  <input type="hidden" id="z0381" value="<bean:write name='arrangementInfoForm' property='resumeInfo.z0381'/>"/>
  <input type="hidden" id="phoneNum" value="<bean:write name='arrangementInfoForm' property='resumeInfo.c0104'/>"/>
  <input type="hidden" id="flag" value="<bean:write name='arrangementInfoForm' property='resumeInfo.flag'/>"/>
  <input type="hidden" id="resume_flag" value="<bean:write name='arrangementInfoForm' property='resumeInfo.resume_flag'/>"/>
  <input type="hidden" id="resume_name" value="<bean:write name='arrangementInfoForm' property='resumeInfo.resume_name'/>"/>
  
  <div  class="hj-wzm-xq-all" id="header" style="display: none;">
	    <div  class="hj-zm-msap-one">
	        <p style="padding-right: 20px;font-size: 12px;color: black;">
	            <input type="hidden" id="nbase" value="<bean:write name='arrangementInfoForm' property='resumeInfo.nbase'/>"/>
	            <input type="hidden" id="a0100" value="<bean:write name='arrangementInfoForm' property='resumeInfo.a0100'/>"/>
	            <input type="hidden" id="a0101" value="<bean:write name='arrangementInfoForm' property='resumeInfo.a0101'/>"/>
	            <bean:write name='arrangementInfoForm' property='resumeInfo.a0101'/>
	        </p>
	        <p style="font-size: 12px;color: black;padding-right: 20px;">
	            <input type="hidden" id="z0301" value="<bean:write name='arrangementInfoForm' property='resumeInfo.z0301'/>"/>
	            <input type="hidden" id="link_id" value="<bean:write name='arrangementInfoForm' property='resumeInfo.link_id'/>"/>
	            <input type="hidden" id="z0351" value="<bean:write name='arrangementInfoForm' property='resumeInfo.z0351'/>"/>
	            <input type="hidden" id="z0325" value="<bean:write name='arrangementInfoForm' property='resumeInfo.z0325'/>"/>
	            <bean:write name='arrangementInfoForm' property='resumeInfo.z0351'/>
	        </p>
	        <p style="font-size: 12px;color: black;padding-right: 40px;">
	        <bean:write name='arrangementInfoForm' property='resumeInfo.c0102'/>  <bean:write name='arrangementInfoForm' property='resumeInfo.c0104'/>
	        </p>
	       <div style="float:right;">
  	        <logic:equal value="wu" name='arrangementInfoForm' property='resumeInfo.nextNum'>
	       		<a onclick="Global.submitArrangement()" >提交面试安排</a>
	        </logic:equal>
	       
	        <logic:notEqual value="wu" name='arrangementInfoForm' property='resumeInfo.nextNum'>
				<a onclick="Global.continueToArrange('<bean:write name='arrangementInfoForm' property='resumeInfo.nextNum'/>')">提交并安排下一候选人</a>
				<a onclick="Global.just('<bean:write name='arrangementInfoForm' property='resumeInfo.nextNum'/>')" >安排下一候选人</a>
	        </logic:notEqual>
	        <a onclick="Global.goBack();">取消</a>
	        </div>
	    </div>
	    </div>
    <div class="hj-wzm-xq-all" id="funcDiv" style="display: none;">
        <div class="hj-zm-msap-all">
            <div class="hj-zm-msap-two">
                <h2><span>面试安排人：<bean:write name='arrangementInfoForm' property='resumeInfo.userName'/></span>面试信息</h2>
                <table width="800" border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td height="30" width="60" valign="middle"><font color='red'>*</font>&nbsp;面试时间</td>
                    <td width="180" height="30" align="left" valign="top">
                    <div style="width: 180px;height: 30px;" id="dateId"></div>
                    </td>
                    <td height="30" width="60" valign="middle"><font color='red'>*</font>&nbsp;面试地址</td>
                    <td height="30" width="248" align="left" valign="top"><input type="text" class="hj-zm-msap-msdz" style="height: 24px" onblur="Global.replaceMethodAddress(this)"  id="arrangAddress" value="<bean:write name='arrangementInfoForm' property='arrangementInfo.Z0503'/>"/></td>
                  </tr>
                </table>
            </div>
            <div class="bh-space"></div>
          <div class="hj-zm-msap-three">
            <h2>面试官</h2>
              <table width="100%" border="0" cellpadding="0" cellspacing="0" id="schedule">
                <tr>
                  <th width="5%" style="border-right:none;">序号</th>
                  <th width="400px" colspan="2" style="border-right:none;" id="arrangementes">面试官</th>
                  <th width="30%" style="border-right:none;">面试时间</th>
                  <th width="15%" style="border-right:none;">面试地点</th>
                  <th width="10%">操作</th>
                </tr>
                <logic:notEqual value=""  property="interviewerInfoList" name="arrangementInfoForm">
                <logic:iterate id="interviewerList" property="interviewerInfoList" name="arrangementInfoForm">
                        <tr>    
			                <td style="border-right:none;border-top:none;" id="<bean:write property="Group_number" name="interviewerList"/>"><bean:write property="Group_number" name="interviewerList"/></td> 
			                <td style="border-right:none;border-top:none;width:25%;" id="td<bean:write property="Group_number" name="interviewerList"/>">&nbsp;
			                    <logic:iterate  id="element" property="interviewerInfo"  name="interviewerList">
				                    <logic:notEmpty property="NbaseA0100" name="element">
		                                <dl style='float:left;'>
		                                    <dt onmouseover="Global.onMouseover(this)" onmouseleave="Global.onMouseleave(this)"><img src="<bean:write property="photoPath" name="element"/>"  class="img-middle" width="32px" height="32px;" /><img style="display:none;width: 20px; height: 20px;float:left;" class="deletePic"
		                                     onclick="Global.removePerson(this,'<bean:write property="Group_number" name="interviewerList"/>','<bean:write property="NbaseA0100" name="element"/>','<bean:write property="Phone_number" name="element"/>','<bean:write property="email" name="element"/>','<bean:write property="userName" name="element"/>')" src="/workplan/image/remove.png" >
		                                     </dt>
		                                    <dd><bean:write property="userName" name="element"/>&nbsp;<bean:write property="Phone_number" name="element"/></dd><br>
		                                    <dd><bean:write property="email" name="element"/></dd>
		                                </dl>
				                    </logic:notEmpty>
			                    </logic:iterate>
                            </td>
			                <td style="border-right:none;border-top:none;border-left:none;">
			                    <font color='red'>*</font>&nbsp;<a style="vertical-align: middle;" onclick="Global.addInterviewer(this,<bean:write property="Group_number" name="interviewerList"/>)">添加面试官</a>
			                </td>
			                <td style="border-right:none;border-top:none;" id="time<bean:write property="Group_number" name="interviewerList"/>">
			                     <bean:define id="start_time" name="interviewerList" property="start_time"/>
			                     <bean:define id="End_time" name="interviewerList" property="End_time"/>
			                     <%
			                      String biginhou = start_time.toString().split(":")[0];
			                      String biginmin = start_time.toString().split(":")[1];
			                      String endhou = End_time.toString().split(":")[0];
                                  String endmin = End_time.toString().split(":")[1];
                                  String[] hou={"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"};
                                  String[] min={"00","05","10","15","20","25","30","35","40","45","50","55"};
			                     %>
			                     <select id="beginhou<bean:write property="Group_number" name="interviewerList"/>"   onchange="Global.onchangeTiem(this,'<bean:write property="Group_number" name="interviewerList"/>')">
			                         <%
			                         for(int i=0;i<hou.length;i++)
			                         {
			                        	 if(biginhou.equalsIgnoreCase(hou[i]))
			                        	 {
			                        		 %>
			                        		   <option selected="selected" value="<%=hou[i] %>"><%=hou[i] %></option>
			                        		 <%
			                        	 }else{
			                        		 %>
			                        		  <option value="<%=hou[i] %>"><%=hou[i] %></option>
			                        		 <%
			                        	 }
			                         }
			                         %>
			                     </select>:
			                     <select id="beginmin<bean:write property="Group_number" name="interviewerList"/>"   onchange="Global.onchangeTiem(this,'<bean:write property="Group_number" name="interviewerList"/>')">
                                      <%
                                     for(int i=0;i<min.length;i++)
                                     {
                                         if(biginmin.equalsIgnoreCase(min[i]))
                                         {
                                             %>
                                               <option selected="selected" value="<%=min[i] %>"><%=min[i] %></option>
                                             <%
                                         }else{
                                             %>
                                             <option value="<%=min[i] %>"><%=min[i] %></option>
                                            <%
                                        }
                                     }
                                     %>
                                 </select>&nbsp;-&nbsp;
                                 <select id="endhou<bean:write property="Group_number" name="interviewerList"/>"   onchange="Global.onchangeTiem(this,'<bean:write property="Group_number" name="interviewerList"/>')">
                                      <%
                                     for(int i=0;i<hou.length;i++)
                                     {
                                         if(endhou.equalsIgnoreCase(hou[i]))
                                         {
                                             %>
                                               <option selected="selected" value="<%=hou[i] %>"><%=hou[i] %></option>
                                             <%
                                         }else{
                                             %>
                                             <option value="<%=hou[i] %>"><%=hou[i] %></option>
                                            <%
                                        }
                                     }
                                     %>
                                 </select>:
                                 <select id="endmin<bean:write property="Group_number" name="interviewerList"/>"   onchange="Global.onchangeTiem(this,'<bean:write property="Group_number" name="interviewerList"/>')">
                                      <%
                                     for(int i=0;i<min.length;i++)
                                     {
                                         if(endmin.equalsIgnoreCase(min[i]))
                                         {
                                             %>
                                               <option selected="selected" value="<%=min[i] %>"><%=min[i] %></option>
                                             <%
                                         }else{
                                             %>
                                             <option value="<%=min[i] %>"><%=min[i] %></option>
                                            <%
                                        }
                                     }
                                     %>
                                 </select>
			                </td>
			                <td style="border-right:none;line-height:20px;border-top:none;">
				                <font color='red'>*</font>&nbsp;<input type="text" id="address<bean:write property="Group_number" name="interviewerList"/>" class="hj-wzm-msap-dd" value="<bean:write name="interviewerList" property="address"/>"/>
		                        <input type="hidden" id="beginTime<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write name="interviewerList" property="start_time"/>"/>
		                        <input type="hidden" id="endTime<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write name="interviewerList" property="End_time"/>"/>
				                <input type="hidden" id="userNo<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write property="NbaseA0100s" name="interviewerList"/>">
				                <input type="hidden" id="c0104<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write property="Phone_numbers" name="interviewerList"/>"/>
				                <input type="hidden" id="userEmail<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write property="emails" name="interviewerList"/>"/>
				                <input type="hidden" id="userName<bean:write property="Group_number" name="interviewerList"/>" value="<bean:write property="userNames" name="interviewerList"/>"/>
			                </td>
			                <td style="border-top:none;"><a onclick="Global.deleteCurrentRow(this)">删除</a></td>
		                </tr>
                </logic:iterate>
                </logic:notEqual>
              </table>
              <div colspan="5" align="center" style="height: 30px;padding-top:5px;border: 1px solid #c5c5c5;width: 100%;border-top: none;"><a  onclick="Global.addNewTr(this)">
                  <img src="/module/recruitment/image/add.png" style="width: 20px;height: 20px;"  class="img-middle"/>
                  </a></div>
           <p>
           <input id="examinerMail" type="checkbox" value="yes" /><a href="javascript:void(0);"><font color='grey'>给面试官发送邮件通知</font></a>
          	</p>
           </div>
          <div class="hj-zm-msap-four">
            <h2>候选人</h2>
            <div id="panel"></div>
              <table width="800">
                <tr>
                  <td style="padding-left: 8px;padding-top: 8px;width:200px">
		             <input name="candidateMail" type="checkbox" id="candidateMail" onclick="Global.sendEmailDiv()"  />给候选人发送邮件通知
                  </td>
                  <td style="padding-left: 8px;padding-top: 8px;width:200px">
	                   <input id="candidateText" type="checkbox" onclick="Global.sendEmailDiv()" />给候选人发送短信通知
                   </td>
                  <td style="padding-left: 8px;padding-top: 8px;">
		             <input name="feedBack" type="checkbox" id="feedBack" onclick="Global.sendEmailDiv()" />反馈信息
                  </td>
                </tr>
                <tr>
                    <td style="padding-left: 8px;" colspan="3">
                        <div id="sendEmailDiv">
                        <table>
                       	    <tr id="EmailAddress">
			                  <td>邮箱地址&nbsp;&nbsp;</td>
			                  <td><input type="text" id="c0102" class="hj-ms-four-yjbt" style='color:#333;width:100%'/></td>
			                </tr>
			                <tr  id="Template">
			                  <td>通知模板&nbsp;&nbsp;</td>
			                  <td><div id="combo"></div></td>
			                </tr>
			                <tr id="TemplateTitle">
			                  <td>通知标题&nbsp;&nbsp;</td>
			                  <td><input type="text" id="title"  class="hj-ms-four-yjbt" style='color:#333;width:100%'/></td>
			                </tr>
			                <tr  id="Templatebody">
			                  <td>通知正文&nbsp;&nbsp;</td>
			                  <td id="contentPanel" border=""></td>
			                </tr>
			                <tr id='filetr'>
			                  <td>邮件附件&nbsp;&nbsp;</td>
			                  <td id='filePanel' height=100 width=400></td>
			                </tr>
                        </table>
                        </div>
                    </td>
                </tr>
              </table>
            </div>
        </div>
    </div>
    </div>
</body>
</html>

