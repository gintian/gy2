<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView" %>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
int ver=0;
String bosflag="";
String themes="default";
if(userView!=null){
	ver = userView.getVersion();
	bosflag = userView.getBosflag();
	/*xuj added at 2014-4-18 for hcm themes*/
    themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
}
%>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style type="text/css">
body {
	
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
    float: left;
	width: 40%;
	height: 20px;
    line-height:20px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted;
	BORDER-LEFT: #FFFFFF 0pt dotted;
	BORDER-RIGHT: #FFFFFF 0pt dotted;
	BORDER-TOP: #FFFFFF 0pt dotted;
}
</style>
 <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />

  <%} %>
<script type="text/javascript">
function setField(field_falg)
{
    var target_url="/general/deci/leader/param.do?b_setfeild=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
    /*
    var return_vo= window.showModalDialog(iframe_url,1,
        "dialogWidth:540px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById(field_falg);
	  var in_obj2=document.getElementById("photo_other_itemid");
	  var str=return_vo.mess;
	  var str2=return_vo.code;
	  in_obj.value=str;
	  in_obj2.value=str2;
    }else
    {
	  var in_obj=document.getElementById(field_falg);
    }
    */
    //改用ext 弹窗显示  wangb 20190319
    var win = Ext.create('Ext.window.Window',{
		id:'select_field',
		title:'请选择',
		width:560,
		height:410,
		resizable:false,
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody(),
		listeners:{
			'close':function(){
				if(this.return_vo!=null)
    			{
	  				var in_obj=document.getElementsByName(field_falg)[0];
	  				var in_obj2=document.getElementsByName("photo_other_itemid")[0];
	  				var str=this.return_vo.mess;
	  				var str2=this.return_vo.code;
	  				in_obj.value=str;
	  				in_obj2.value=str2;
    			}
    			else
    			{
	  				var in_obj=document.getElementsByName(field_falg)[0];
    			}
			}
		}
	});
}
function IsDigit()
{
    return ((event.keyCode >= 47) && (event.keyCode <= 57));
}
function validate()
{
  var lawrule_file_days_o=document.getElementById("lawrule_file_days");
  var num_per_page_o=document.getElementById("num_per_page");
  var lawrule_file_days=lawrule_file_days_o.value;
  var num_per_page=num_per_page_o.value;
  if(lawrule_file_days!="")
  {
     for(var i=0;i<lawrule_file_days.length;i++)
     {
         if(lawrule_file_days.charCodeAt(i)>128)
         {
            alert("输入的规章制度及知识最近天数仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  var announce_days = document.getElementById("announce_days").value;
  if(announce_days!="")
  {
     for(var i=0;i<announce_days.length;i++)
     {
         if(announce_days.charCodeAt(i)>128)
         {
            alert("输入的公告栏最近天数仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  if(num_per_page!="")
  {
     for(var i=0;i<num_per_page.length;i++)
     {
         if(num_per_page.charCodeAt(i)>128)
         {

            alert("输入的信息浏览每页显示记录数仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  var link_p_height_o=document.getElementById("link_p_height");
  var link_p_width_o=document.getElementById("link_p_width");
  var lastdays_o=document.getElementById("lastdays");
  var firstdays_o=document.getElementById("firstdays");
  var lastdays=lastdays_o.value;
  var firstdays=firstdays_o.value;
  var link_p_width=link_p_width_o.value;
  var link_p_height=link_p_height_o.value;
  var dairyinfolimit = document.getElementById("dairyinfolimit").value;
  if(link_p_width!="")
  {
     for(var i=0;i<link_p_width.length;i++)
     {
         if(link_p_width.charCodeAt(i)>128)
         {

            alert("输入的首页友情链接图片大小宽仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  if(lastdays!="")
  {

     for(var i=0;i<lastdays.length;i++)
     {
         if(lastdays.charCodeAt(i)>58)
         {

            alert("周报和月报提交期限只能为数字,请重新输入!");
            return false;
         }
     }
  }
   if(firstdays!="")
  {

     for(var i=0;i<firstdays.length;i++)
     {
         if(firstdays.charCodeAt(i)>58)
         {

            alert("周报和月报提交期限只能为数字,请重新输入!");
            return false;
         }
     }
  }
   if(dairyinfolimit!=""){
	   for(var i=0;i<dairyinfolimit.length;i++)
	     {
	         if(dairyinfolimit.charCodeAt(i)>58)
	         {

	            alert("周报和月报提交期限只能为数字,请重新输入!");
	            return false;
	         }
	     }
	   if(dairyinfolimit<0) {
		   alert("日报提交期限不能为负数,请重新输入!");
	       return false;
	   }
   }
   if(link_p_height!="")
  {
     for(var i=0;i<link_p_height.length;i++)
     {
         if(link_p_height.charCodeAt(i)>128)
         {

            alert("输入的首页友情链接图片大小高仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  var display_e0122_o=document.getElementById("display_e0122");
  var display_e0122= display_e0122_o.value;
  if(display_e0122!="")
  {
     for(var i=0;i<display_e0122.length;i++)
     {
         if(display_e0122.charCodeAt(i)>128)
         {

            alert("输入的部门显示包含参数仅只能为数字,请重新输入!");
            return false;
         }
     }
  }
  return true;
}
function checkBoxSel(obj) {
    if(obj.checked){
    	document.otherParamForm.condisk.value="1";
    }else{
    	document.otherParamForm.condisk.value="0";
    }
}
function checkBoxunits(obj) {
    if(obj.checked){
    	document.otherParamForm.units.value="1";
    }else{
    	document.otherParamForm.units.value="0";
    }
}
function checkBoxplace(obj) {
    if(obj.checked){
    	document.otherParamForm.place.value="1";
    }else{
    	document.otherParamForm.place.value="0";
    }
}
function getbasefield(view_name,hidden_name){
	var return_vo= window.showModalDialog("/system/options/otherparam/global_employeeitemtree.do?b_query=link&param=root&froms=db&name=usr&input=2", false,
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
   if(return_vo==null){
     return;
   }else
   {
     var codeitemid,codetext;
     codeitemid=return_vo.codeitemid;
     codetext=return_vo.codetext;
     if(codeitemid.length<=3||codeitemid=="root")
     {
        alert("请选择指标信息");
        return;
     }
     var oldInputs=document.getElementsByName(view_name);
     var oldobj=oldInputs[0];
     var hiddenInputs=document.getElementsByName(hidden_name);
     if(hiddenInputs!=null)
     {
    	hiddenobj=hiddenInputs[0];
    	hiddenobj.value=codeitemid;
     }
     oldobj.value=codetext;
   }
}
function gquerycond(){

	var gquery_conds=document.getElementsByName("gquery_cond")[0].value;
	var strUrl="/selfservice/param/otherparam.do?b_cond=link&gquery_conds="+gquery_conds;
	/*
	var return_vo= window.showModalDialog(strUrl, false,
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	var conds=return_vo.split("#");
    	document.getElementById("gquery_cond").value=conds[0];
    	document.getElementById("g_cond").value=replaceAll(conds[1],"、","\r\n");
    }
    */
    //改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
			id:'select_cond',
			title:'人员分类条件',
			width:520,
			height:350,
			resizable:false,
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+strUrl+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo!=null){
    					var conds=this.return_vo.split("#");
    					document.getElementsByName("gquery_cond")[0].value=conds[0];
    					document.getElementsByName("g_cond")[0].value=replaceAll(conds[1],"、","\r\n");
    				}
				}
			}
	});
}
function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0){
   		str = str.replace(sptr, sptr1);
	}
	return str;
}
this.fObj = null;
var time_r=0;
function setFocusObj(obj,time_vv)
 {
	this.fObj = obj;
	time_r=time_vv;
 }
function IsInputTimeValue()
 {
       event.cancelBubble = true;
       var fObj=this.fObj;
       if (!fObj) return;
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);
       var radix=parseInt(time_r,10)-1;
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }
       fObj.select();
  }
  function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function setDb(field_falg)
{
    var target_url="/system/options/param/set_sys_param.do?b_setdb=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(iframe_url,null,
        "dialogWidth:300px; dialogHeight:275px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
}
function describeNews(){
    var target_url="/selfservice/param/otherparam.do?b_describenews=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var dw=550,dh=320,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
    /*
    var return_vo= window.showModalDialog(iframe_url,null,
        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
    */
	//改用ext 弹窗显示  wangb 20190318
    var win = Ext.create('Ext.window.Window',{
		id:'person_desc', 
		title:'明星员工描述信息',
		width:dw+20,
		height:dh+40, 
		resizable:false,
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody()
	});
}
function query(){
	var cvalue = otherParamForm.complex_id.value;
	if(cvalue == "#"){
		//document.getElementsByName("btnreturn1")[02].disabled = true;
		document.getElementsByName("btnreturn2")[0].disabled = true;
	}else{
		//document.getElementsByName("btnreturn1")[0].disabled = false;
		document.getElementsByName("btnreturn2")[0].disabled = false;
	}
}
</script>

<html:form action="/selfservice/param/otherparam"  onsubmit="return validate();">
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow" style="margin-top:10px;">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" colspan="2" style="border-right:none;">
		<bean:message key="selfservice.param.otherparam.title"/>&nbsp;&nbsp;
            </td>
           </tr>
   	  </thead>
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="sys.label.empcard"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                       <html:select name="otherParamForm" property="emp_card_id" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:optionsCollection property="empcardlist" value="dataValue" label="dataName"/>
                      </html:select>&nbsp;
                    </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  人员信息录入格式
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="rownums">
                  <html:option value="0">单列</html:option>
                  <html:option value="1">双列</html:option>
                  </html:select>
                  </td>
                  </tr>
                   <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  机构信息浏览格式
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="org_browse_format">
                  <html:option value="0">单列</html:option>
                  <html:option value="1">双列</html:option>
                  </html:select>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  部门显示包含上
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <!--zhangh 2019-11-30部门显示包含上N级名称时，限制输入框只可以输入数字 -->
                      <html:text name="otherParamForm" property="display_e0122" styleId="display_e0122" size="5" maxlength="2"  onkeyup = "value=value.replace(/[^\d]/g,'')" styleClass="text4"/>
                      级名称
                  </td>
                  </tr>
                   <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  部门层级间分隔符
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:text name="otherParamForm" property="seprartor" styleId="seprartor" size="5" maxlength="2" styleClass="text4"/>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  员工信息浏览默认显示
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="browse_photo">
                  <html:option value="0">记录</html:option>
                  <html:option value="1">照片</html:option>
                  </html:select>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  员工信息浏览查询项默认
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="browse_search_state">
                  <html:option value="0">隐藏</html:option>
                  <html:option value="1">显示</html:option>
                  </html:select>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  拼音简码指标
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select name="otherParamForm" property="pinyin_field" size="1">
                  <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                  <html:optionsCollection property="pinyin_fieldlist" value="dataValue" label="dataName"/>
                  </html:select>
                  </td>
                  </tr>

                   <hrms:priv func_id="300158">
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  按信息分类浏览员工信息
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="infosort_browse">
                  <html:option value="0">否</html:option>
                  <html:option value="1">是</html:option>
                  </html:select>
                  </td>
                  </tr>
					</hrms:priv>
				<hrms:priv func_id="260111">
				<tr>
                  <td align="right"  width="36%" class="RecordRow">
                  常用花名册
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
	                  <html:select name="otherParamForm" property="common_roster" size="1">
		                  <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
		                  <html:optionsCollection property="common_rosterlist" value="dataValue" label="dataName"/>
	                  </html:select>
                  </td>
                </tr>
                </hrms:priv>
                <hrms:priv func_id="">
				<tr>
                  <td align="right" valign="top"  width="36%" class="RecordRow">
                  	人员分类条件
                  </td>
                  <td width="64%" align="left"  valign="middle" class="RecordRow" style="border-right:none;">
                  	<table cellpadding="0" cellspacing="0"><tr><td style="padding-bottom:1px;">
                  	  <input type="hidden" name="gquery_cond" value="${otherParamForm.gquery_cond }"/>
	                  <textarea name="g_cond" rows="4" cols="1" style="width: 260px;vertical-align: top;font-size: 12px;" readonly>${otherParamForm.g_cond }</textarea>
	                  </td>
	                  <td style="padding-left:5px;">
                  		<input type="button" value="设置" class="mybutton" onclick="gquerycond();"/>

                  	  </td></tr></table>
                  </td>

                </tr>
                </hrms:priv>
                   <hrms:priv func_id="030701">

                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  综合信息范围
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <hrms:optioncollection name="otherParamForm" property="syn_list" collection="list" />
	          <html:select name="otherParamForm" property="syn_bound" size="1">
                  <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select>
                  </td>
                  </tr>
                  </hrms:priv>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  组织机构名称
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:text name="otherParamForm" property="org_root_caption" size="20"  styleClass="text4"/>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  浏览子集时每页显示条数
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:text property="num_per_page"  maxlength="2" styleId="num_per_page" size="20"  styleClass="text4">

                  </html:text>
                  </td>
                  </tr>
            <hrms:priv func_id="0308,26063">
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  信息修改后是否需要审核
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="approveflag">
                  <html:option value="1">需要</html:option>
                  <html:option value="0">不需要</html:option>
                  </html:select>
                  </td>
                  </tr>
                  <!-- 2014-2-13 信息审核默认不直接入库
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  信息修改后是否直接进库
                  </td>
                  <td width="64%" align="left" class="RecordRow">
                  <html:select property="inputchinfor">
                  <html:option value="0">是</html:option>
                  <html:option value="1">否</html:option>
                  </html:select>
                  </td>
                  </tr>
                  -->
          </hrms:priv>
          <%

          if(ver<50){
          %>
                   <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  首页公告信息是否滚动
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:select property="welcome_marquee">
                  <html:option value="0">是</html:option>
                  <html:option value="1">否</html:option>
                  </html:select>
                  </td>
                  </tr>
                  <%} %>
    <!-- 53607 友情连接功能已去掉，此处不显示友情连接设置 guodd 2019-11-06
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  首页友情链接图片大小
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                    宽：<html:text property="link_p_width" styleId="link_p_width" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>pixels(像素)
                    高：<html:text property="link_p_height" styleId="link_p_height" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>pixels(像素)
                  </td>
                  </tr>
                  <tr>
                  -->
                  <td align="right"  width="36%" class="RecordRow">
                  人员照片大小
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                    宽：<html:text property="photo_w" styleId="link_p_width" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>pixels(像素)
                    高：<html:text property="photo_h" styleId="link_p_height" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>pixels(像素)
                  </td>
                  </tr>
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     上传照片文件大小
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:text name="otherParamForm" property="photo_maxsize" maxlength="6" size="6" onkeypress="event.returnValue=IsDigit();" styleClass="text4"/>
                    KB
                   </td>
                  </tr>
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     上传多媒体文件大小
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  <html:text name="otherParamForm" property="multimedia_maxsize" maxlength="6" size="6" onkeypress="event.returnValue=IsDigit();" styleClass="text4"/>
                    KB
                   </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                   人员统计范围
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                    <hrms:optioncollection name="otherParamForm" property="statlist" collection="list" />
	            <html:select name="otherParamForm" property="stat_id" size="1">
                      <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                    <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                    </html:select>
                  </td>
                  </tr>
                   <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  规章制度及知识最近
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:text property="lawrule_file_days" styleId="lawrule_file_days" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>
                  天为新文章
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  公告栏最近
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:text property="announce_days" styleId="announce_days" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>
                  天为新公告
                  </td>
                  </tr>
                    <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  登记表单元格字体自动适应大小
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:radio name="otherParamForm" property="ykcard_auto" value="1"/> 是
                   <html:radio name="otherParamForm" property="ykcard_auto" value="0"/> 否
                  </td>
                  </tr>

                    <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  报表上报判断直属单位是否上报
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:radio name="otherParamForm" property="subunitup" value="true"/> 是
                   <html:radio name="otherParamForm" property="subunitup" value="false"/> 否
                  </td>
                  </tr>


                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  报表上报需要校检
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:radio name="otherParamForm" property="updisk" value="true"/> 是
                   <html:radio name="otherParamForm" property="updisk" value="false"/> 否
                   <logic:equal name="otherParamForm" property="condisk" value="1">
                   		<input type="checkbox" name="condisks" onclick="checkBoxSel(this);" checked/> 是否包含下级
                   	</logic:equal>
                   	<logic:notEqual name="otherParamForm" property="condisk" value="1">
                   		<input type="checkbox" name="condisks" onclick="checkBoxSel(this);"/> 是否包含下级
                   	</logic:notEqual>
                   	<html:hidden name="otherParamForm" property="condisk"/>
                  </td>
                  </tr>
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  上级单位是否可以修改下级单位报表数据
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   <html:radio name="otherParamForm" property="editupdisk" value="true"/> 是
                   <html:radio name="otherParamForm" property="editupdisk" value="false"/> 否
                  </td>
                  </tr>

                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                  报表上报是否支持审批
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr>
					<td align="left" width="15%" nowrap="nowrap">
                   <html:radio name="otherParamForm" property="isApprove" value="false" onclick = "hides('sss')"/> 不需要审批
                   </td>
                   <td align="left" width="15%" nowrap="nowrap">
                   <html:radio name="otherParamForm" property="isApprove" value="true" onclick = "toggles('sss')"/> 需要审批
                   </td>
                   <td align="left" width="70%" style="border-right:none;">
						<div id = "sss">
                   			<hrms:optioncollection name="otherParamForm" property="approvelist" collection="list"/>
								<html:select name="otherParamForm" property="relation_id" onchange="" style="width:100">
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
						</div>
					</td>
					</tr>
					</table>
                  </td>
                  </tr>

                 <hrms:priv func_id="0105,0306,0610">
                  <tr>
                  <td align="right"  width="36%" class="RecordRow">
                	周报和月报提交期限
                  </td>
                  <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                  	本期末最后<html:text property="lastdays" styleId="lastdays" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>天
                  	&nbsp;&nbsp;&nbsp;&nbsp;
                  	下期初头<html:text property="firstdays" styleId="firstdays" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>天
                  </td>
                  </tr>
                  <tr>
                    <td align="right"  width="36%" class="RecordRow">
                         日报提交期限
                    </td>
                    <td width="64%" align="left" valign="middle" class="RecordRow" style="border-right:none;">
                      <table width="100%" border="0" cellspacing="0" cellpadding="0" style="margin-top:5px;margin-bottom:5px;">
                       <tr>
                       	<td width="100" align="left">
                  		延后<html:text name="otherParamForm" property="dairyinfolimit" styleId="dairyinfolimit" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"  styleClass="text4"/>天
                  	    </td>
                         <td valign="middle" align="left">
                           <table border="0" cellspacing="0"  align="left" valign="bottom" cellpadding="0" style="margin-left:5px;">
                             <tr>
		                       <td width="40" nowrap style="background-color:#FFFFFF">
		                         <div class="m_frameborder" nowrap>
		                         <input type="text" class="m_input" maxlength="2" name="limit_HH" value="${otherParamForm.limit_HH}" onblur="validateNum(this)" onfocus="setFocusObj(this,24);"><span style="width:3px;line-height:20px;color: #000000;float: left;text-align: center" >:</span><input type="text" onblur="validateNum(this)" class="m_input" maxlength="2" name="limit_MM" value="${otherParamForm.limit_MM}" onfocus="setFocusObj(this,60);">
		                         </div>
		                       </td>
		                       <td>
		                         <table border="0" cellspacing="2" cellpadding="0">
		                            <tr><td><button type="button" id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		                            <tr><td><button type="button" id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		                         </table>
                              </td>  
                              <td>
                               &nbsp;
                              </td>                   
                          </tr>
                         </table>
                        </td>
                       </tr>
                     </table>
                    </td>
                  </tr>
          		</hrms:priv> 
                  <!-- 
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="sys.label.orgcard"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow">
                       <html:select name="otherParamForm" property="org_card_id" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:optionsCollection property="orgcardlist" value="dataValue" label="dataName"/>
                      </html:select>&nbsp;	     
                    </td>  
                  </tr>       
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     <bean:message key="sys.label.poscard"/>&nbsp;
                   </td>
                   <td width="64%" align="left" class="RecordRow">
                       <html:select name="otherParamForm" property="pos_card_id" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:optionsCollection property="poscardlist" value="dataValue" label="dataName"/>
                      </html:select>&nbsp;	     
                    </td>  
                  </tr>   
                   -->  
                  <logic:notEqual name="otherParamForm" property="birthday_wid" value="null">
                  <tr>
                   <td align="right"  width="36%" class="RecordRow">
                     　　员工生日信息预警提示
                   </td>
                   <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                       <html:select name="otherParamForm" property="birthday_wid" size="1">
                          <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>
                          <html:optionsCollection property="warnlist" value="dataValue" label="dataName"/>
                      </html:select>&nbsp;	     
                    </td>  
                  </tr> 
                  </logic:notEqual>                                          
               <tr>
                  	<td align="right"  width="36%" class="RecordRow">
                  		人员信息必填项
                  	</td>
                  	<td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   		<logic:equal name="otherParamForm" property="units" value="1">
                   			<input type="checkbox" name="danwei" onclick="checkBoxunits(this);" checked/> 单位名称
                   		</logic:equal>
                   		<logic:notEqual name="otherParamForm" property="units" value="1">
                   			<input type="checkbox" name="danwei" onclick="checkBoxunits(this);"/> 单位名称
                   		</logic:notEqual>
                   		<html:hidden name="otherParamForm" property="units"/>
                   		<logic:equal name="otherParamForm" property="place" value="1">
                   			<input type="checkbox" name="zhiwei" onclick="checkBoxplace(this);" checked/> 岗位
                   		</logic:equal>
                   		<logic:notEqual name="otherParamForm" property="place" value="1">
                   			<input type="checkbox" name="zhiwei" onclick="checkBoxplace(this);"/> 岗位
                   		</logic:notEqual>
                   		<html:hidden name="otherParamForm" property="place"/>
                  	</td>
                </tr>
                <tr>
                  	<td align="right"  width="36%" class="RecordRow">
                  		人员信息照片浏览显示指标信息
                  	</td>
                  	<td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   		<html:text name="otherParamForm" property='photo_other_view'  styleClass="textColorWrite" style="width: 200px;"/>   
                   		<html:hidden name="otherParamForm" property="photo_other_itemid"/>
                   		<img src="/images/code.gif" align="absmiddle" onclick="setField('photo_other_view');" />        
                   		<!--<img src="/images/code.gif" onclick='javascript:getbasefield("photo_other_view","photo_other_itemid");' />-->            		
                  	</td>
                </tr>
                <tr>
                  	<td align="right"  width="36%" class="RecordRow">
                  		黑名单人员库
                  	</td>
                  	<td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   		<html:select name="otherParamForm" property="blacklist_per" size="1">   
                   		  <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>                                              
                          <html:optionsCollection property="dblist" value="dataValue" label="dataName"/>
                      </html:select>
                  	</td>
                </tr>
                <tr>
                  	<td align="right"  width="36%" class="RecordRow">
                  		黑名单人员对应指标
                  	</td>
                  	<td width="64%" align="left" class="RecordRow" style="border-right:none;">
                   		<html:select name="otherParamForm" property="blacklist_field" size="1">   
                   		  <html:option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</html:option>                       
                          <html:optionsCollection property="fieldlist" value="dataValue" label="dataName"/>
                      </html:select>
                  	</td>
                </tr>
                <tr>
                  	<td align="right"  width="36%" class="RecordRow">
                  		明星员工
                  	</td>
                  	<td width="64%" align="left" class="RecordRow" id="cpx" style="border-right:none;">
                   		<html:select name="otherParamForm" property="complex_id" size="1" onchange="query();">
                        	<option value="#">请选择...&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>
                        	<html:optionsCollection property="condlist" value="dataValue" label="dataName"/>
                        </html:select><!-- 
		           	&nbsp;<input type="button" name="btnreturn1" value='<bean:message key="leaderteam.leaderparam.dbsetting"/>' class="mybutton"  onclick="setDb('dbpre');">
		           	 -->&nbsp;<input type="button" name="btnreturn2" value='描述信息' class="mybutton"  onclick="describeNews();">&nbsp;
                  	</td>
                </tr>
                <tr>
                    <td align="right"  width="36%" class="RecordRow">
                        应用日志级别设置
                    </td>
                    <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                        <html:select property="loglevel">
                            <html:option value="TRACE">TRACE</html:option>
                            <html:option value="DEBUG">DEBUG</html:option>
                            <html:option value="INFO">INFO</html:option>
                            <html:option value="WARN">WARN</html:option>
                            <html:option value="ERROR">ERROR</html:option>
                            <html:option value="FATAL">FATAL</html:option>
                            <html:option value="OFF">OFF</html:option>
                        </html:select>
                    </td>
                </tr>
                <%if("hcm".equals(bosflag)&&false){ %>
                <tr>
                    <td align="right"  width="36%" class="RecordRow">
                                                                     系统皮肤
                    </td>
                    <td width="64%" align="left" class="RecordRow" style="border-right:none;">
                        <html:select name="otherParamForm" property="themes" size="1">
                            <option value="default" <logic:equal value="default" name="otherParamForm" property="themes">selected=selected</logic:equal>>默认</option>
                            <option value="gray" <logic:equal value="gray" name="otherParamForm" property="themes">selected=selected</logic:equal>>银光灰</option>
                            <option value="vista" <logic:equal value="vista" name="otherParamForm" property="themes">selected=selected</logic:equal>>天空蓝</option>
                        </html:select>
                        <img border="0" src="/images/view.gif" alt="预览" title="预览" style="vertical-align: bottom;">（修改皮肤后需要您注销重新登录）
                    </td>
                </tr>
                <%} %>
                <tr>
               <td align="center" class="RecordRow" nowrap style="height: 35px"  colspan="3">
               
                  <hrms:submit styleClass="mybutton" property="b_save">
                     <bean:message key="button.save"/>
	          </hrms:submit> 	         
              </td>
          </tr>   
</table>
</html:form>
<script language='javascript' >
	var isApprove = "${otherParamForm.isApprove}";
	if(isApprove=="false"){
		hides('sss');
	}else{
		toggles('sss')
	}
	query();
	
if(!getBrowseVersion()){//非IE浏览器样式修改   wangb  20190522 bug 48176
	var tables = document.getElementsByTagName('table')[0];
	var trs = tables.getElementsByTagName('tr');
	for(var i = 0 ; i < trs.length ; i++){
		var tds =trs[i].getElementsByTagName('td');
		if(!tds.length)
			continue;
		if(tds.length==1)
			tds[0].style.borderRight='';
		else
			tds[1].style.borderRight='';
			
	}
}else{
    document.getElementById('0_down').style.position='relative';
    document.getElementById('0_down').style.bottom='4px';
}

    //数字验证
    function validateNum(me){
        //修改校验数字的正则表达式
        var reg =/^[0-9]*$/ ;
        var f = reg.test(me.value);
        if ( !f ){
            me.value="";
            alert('请填写数字！');
            return;
        }
    }
</script>