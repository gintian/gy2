<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_amount.CroPayMentForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
<!--
 <%
     CroPayMentForm croPayMentForm = (CroPayMentForm)session.getAttribute("croPayMentForm");
     String returnflag=croPayMentForm.getReturnflag();
     if(returnflag==null)
     	returnflag="";
     String name=croPayMentForm.getGz_grossname();
     String ctrl_peroid=croPayMentForm.getCtrl_peroid();
     String time=croPayMentForm.getYearnum();
     String setid = croPayMentForm.getFieldsetid();
     String spflagid = croPayMentForm.getSpflagid();
     UserView userView=(UserView)session.getAttribute(WebConstant.userView);
     String yearnum=croPayMentForm.getYearnum();
     String isHasAdjustSet=croPayMentForm.getIsHasAdjustSet();
     String filtervalue=croPayMentForm.getFiltervalue();
     String fcVisible=croPayMentForm.getFcVisible();
     String fc_flag=croPayMentForm.getFc_flag();
     if(name!=null&&!name.equals("")&&(ctrl_peroid.equals("1")||ctrl_peroid.equals("2")))
     {
           out.println("function table"+name+"_b0110a_onRefresh(cell,value,record)");
           out.println("{");
           out.println("if(record!=null)");
           if(fc_flag!=null&&fc_flag.length()!=0){
            	out.print("cell.innerHTML=\"<a href='/gz/gz_amount/gropayment.do?b_detail=detail&b0110=\"+record.getString(\"b0110a\")+\"&"+name.toLowerCase()+"z1=\"+record.getString(\""+name+"z1\")");
           }else{
           		 out.print("cell.innerHTML=\"<a href='/gz/gz_amount/gropayment.do?b_detail=detail&b0110=\"+record.getString(\"b0110a\")");
           }
          
           if(ctrl_peroid.equals("2"))
           {
             out.print("+\"&season=\"+record.getString(\"season\")");
           }
           out.print("+\"&year="+yearnum+"&filter="+filtervalue+"\"");
           out.print("+\"' target='_self'>"+"明  细"+"</a>\";");
           out.println("}");
     }
 %>
 
 function collectData()
 {
 	
 	if(confirm("确认逐层汇总工资总额吗?"))
 	{
 		document.croPayMentForm.action="/gz/gz_amount/gropayment.do?b_collect=collect";
 		document.croPayMentForm.submit();
 	}
 }
 function query()
 {
     croPayMentForm.action = "/gz/gz_amount/gropayment.do?b_query=link&opt=2";
     croPayMentForm.submit();
 }
 
function inputExcel(){
	/* 安全问题 文件下载 薪资总额-导入数据 xiaoyun 2014-9-13 start */
    //var thecodeurl ="/gz/gz_amount/gropayment.do?b_inexport=link`saveflag=search";
    var thecodeurl ="/gz/gz_amount/gropayment.do?b_inexport=link`saveflag=search`isclose=link";
  	/* 安全问题 文件下载 薪资总额-导入数据 xiaoyun 2014-9-13 end */
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:500px; dialogHeight:200px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	query();
    }
}
function countformula(setid,year)
{
/*   // 屏蔽老的计算方法
    var thecodeurl ="/gz/gz_amount/countformula.do?b_query=link`setid="+setid+"`year="+year; 
   // window.open(thecodeurl,"_blank");
    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
    var return_vo= window.showModalDialog(iframe_url, "","dialogWidth:400px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:yes");
    if(return_vo)
    {
       if(return_vo == "ok")
       {
           query();
       }
    }
*/

	// 现采用新的薪资总额计算方式  JinChunhai 2012.11.30
	if(confirm("总额参数中设置的实发项目、剩余项目即使定义了计算公式，也不能计算。并且计算只对起草和驳回的记录生效！是否继续？"))
	{
		jindu1();
        var hashvo = new ParameterSet();
	    hashvo.setValue("setid","${croPayMentForm.fieldsetid}");	
        hashvo.setValue("year","${croPayMentForm.yearnum}");
        hashvo.setValue("codeitemid","${croPayMentForm.codeitemid}");
        hashvo.setValue("filtervalue","${croPayMentForm.filtervalue}");
        hashvo.setValue("spType","${croPayMentForm.spType}");
	    var request=new Request({method:'post',asynchronous:true,onSuccess:computSuccess,functionId:'3020080286'},hashvo);	
	}else
	{
	   	return;
	}

}
function jindu1(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait1");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}
function jindu2(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=(window.screen.width-700)/2;
    var y=(window.screen.height-500)/2; 
	var waitInfo=eval("wait2");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="";
}
function computSuccess(outparamters)
{
	var check = outparamters.getValue("msg");
	if(check=='1')
	{
		document.all.ly.style.display="none"; 
		var waitInfo=eval("wait1");
		waitInfo.style.display="none";
		alert("计算完成！");
		query();
	}else
	{
		alert(FORMULA_ERROR_CHECK_COND);
	}
}

function returnFlowPhoto()
{
     croPayMentForm.action="/general/tipwizard/tipwizard.do?br_compensation=link";
     croPayMentForm.target="il_body";
     croPayMentForm.submit();
}
function adjustAmount()
{
    var code="${croPayMentForm.code}";
    if(trim(code).length==2||trim(code).length==1||trim(code).length==0)
    {
       alert("请选择一个单位或者部门进行调整！");
       return;
    }
    var year="${croPayMentForm.yearnum}";
    var setid="${croPayMentForm.isHasAdjustSet}";
    var thecodeurl ="/gz/gz_amount/adjust_amount_list.do?b_query=link`ocode="+code+"`oyear="+year+"`setid="+setid; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url, "","dialogWidth:800px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
   
}
function introduceNext(){
	var codeitemid=document.getElementById("codeitemid").value;
    if(codeitemid==null||trim(codeitemid).length<=0)
    {
       alert("请选择一个单位或者部门进行引入操作！");
       return;
    }  
	var iframe_url="/gz/gz_amount/AddNext.jsp";
	var return_vo= window.showModalDialog(iframe_url, "","dialogWidth:300px; dialogHeight:130px;resizable:no;center:yes;scroll:no;status:no");
	if(return_vo=="2"){
		return;
	}
    	jindu2();
 		var hashvo=new ParameterSet();
 		hashvo.setValue("codeitemid",codeitemid);
 		hashvo.setValue("flag",return_vo);
 		hashvo.setValue("viewUnit",document.getElementById("viewUnit").value);
 		hashvo.setValue("fieldsetid","${croPayMentForm.fieldsetid}");
 		hashvo.setValue("filtervalue","${croPayMentForm.filtervalue}");
 		hashvo.setValue("yearnum","${croPayMentForm.yearnum}");
 		var request=new Request({method:'post',asynchronous:true,onSuccess:introduceok,functionId:'3020080033'},hashvo);
}
function introduceok(outparamters){
	var waitInfo=eval("wait2");
	waitInfo.style.display="none";
	
	 croPayMentForm.action="/gz/gz_amount/gropayment.do?b_query=link";
     croPayMentForm.submit();
}

function isEdit()
{
	var _dataset=${croPayMentForm.gz_grossname};
	var record=_dataset.firstUnit; 
	while (record) 
	{
		if (record.getValue("select") && (record.recordState == "insert" ||
			record.recordState == "modify" ||
			record.recordState == "delete")) {
			return true;
		}
		record=record.nextUnit;
	}
	return false;
}

//报批前先提示数据有变动
function appeal_gz_amount() {
	if(isEdit()) {
   		alert("数据有变动，请先保存！");
   	}else if(confirm("确定报批吗？")){
   		var _appeal=new UpdateCommand("appeal");
   		_appeal.setAction("/ajax/ajaxService");
   		_appeal.setFunctionId("3020080091");
   		//__DatasetInfo_SELECTED ： command.js中的selected
   		var datainfo_ = _appeal.addDatasetInfo(${croPayMentForm.gz_grossname},__DatasetInfo_SELECTED);
   		//成功后是否刷新界面
   		datainfo_.setFlushDataOnSuccess(true);
   		_appeal.execute();
   	}
}

//每次做完修改操作，将当前的表格置为modify
function ${croPayMentForm.gz_grossname}_afterChange(dataset,field)
{
  	var field_name=field.getName();
  	
  	if(field_name!='select') {
  		dataset.getCurrent().recordState="modify";
  	}
	  	
}
//-->
</script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">

.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.tabletoglle{
	border-top:#A9A9A9 1px solid;
	border-bottom:#A9A9A9 1px solid;
	border-left:#A9A9A9 1px solid;
	border-right:#A9A9A9 1px solid;
}
</style>
<hrms:themes />
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在计算,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
</div>
<div id='wait2' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在引入,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
						<table cellspacing="1" cellpadding="0">
							<tr height=8>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
								<td bgcolor=#3399FF width=8></td>
								<td></td>
							</tr>
						</table>
					</marquee>
				</td>
			</tr>
		</table>
</div>
<body onload='resetleft()'  >
<!-- 薪酬管理/薪酬总额界面中，在每页显示？条记录处输入新值后，点击刷新按钮界面如下图有两个组织机构 xiaoyun 2014-10-27 start -->
<html:form action="/gz/gz_amount/gropayment.do?b_query=link">
<!-- 薪酬管理/薪酬总额界面中，在每页显示？条记录处输入新值后，点击刷新按钮界面如下图有两个组织机构 xiaoyun 2014-10-27 end -->
<logic:equal value="1" name="croPayMentForm" property="hasParam">

<p align="CENTER">
薪资总额参数未定义
<hrms:tipwizardbutton flag="compensation" target="il_body" formname="croPayMentForm"/> 
</p>
</logic:equal>
<logic:equal value="0" name="croPayMentForm" property="hasParam">
<table width="180" border="0" style="margin-top:-5px;">
<html:hidden name="croPayMentForm" property="fieldsetid"/>
      <html:hidden name="croPayMentForm" property="codeitemid"/>
      <html:hidden name="croPayMentForm" property="ctrl_peroid"/> 
      <html:hidden name="croPayMentForm" property="cascadingctrl"/>
      <html:hidden name="croPayMentForm" property="viewUnit"/>
      <html:hidden name="croPayMentForm" property="ctrlAmountField"/>
      <html:hidden name="croPayMentForm" property="isCanCreate"/>

   <html:hidden name="croPayMentForm" property="code"/>
   <html:hidden name="croPayMentForm" property="ctrl_type"/>
    <tr>
      <td colspan="2" align="left">   
      <table>
      <tr>
      <td>
      <bean:message key="gz.acount.annual"/>&nbsp;
      	<html:text name="croPayMentForm" property="yearnum" onkeypress="event.returnValue=IsDigit();" style="width:100;padding-top:0px;vertical-align:middle;" styleClass="inputtext"/>
      </td>
      <td valign="middle" align="left">
		<table border="0" cellspacing="2" cellpadding="0" >
			<tr><td><button id="y_up" class="m_arrow" onclick="yincrease();">5</button></td></tr>
			<tr><td><button id="y_down" class="m_arrow" onclick="ysubtract();">6</button></td></tr>
		</table>
	  </td>
	  <td align="left">
	  <logic:notEqual value="1" name="croPayMentForm" property="ctrl_peroid">
	  <logic:equal value="0" name="croPayMentForm" property="ctrl_peroid">
	  <bean:message key="gz.acount.month"/>
	  </logic:equal>
	   <logic:equal value="2" name="croPayMentForm" property="ctrl_peroid">
	    <bean:message key="jx.khplan.quarter"/>
	  </logic:equal>
	  
	  <html:select name="croPayMentForm" property="filtervalue" style="width:100;vertical-align:middle;" onchange="query();">
			 				<html:optionsCollection property="filterList" value="dataValue" label="dataName" />
						</html:select>
		</logic:notEqual>
	  </td>
	  <%if(spflagid!=null && spflagid.trim().length()>0){%>
	  <td align="left">&nbsp;	  
	  		<bean:message key="jx.khplan.spstatus"/>&nbsp;
	  		<html:select name="croPayMentForm" property="spType" onchange="query();" style="vertical-align:middle;">
			 	<html:optionsCollection property="spTypeList" value="dataValue" label="dataName" />
			</html:select>		
	  </td>
	  <%}%>
	  </tr>
	  </table>
	  </td>	  
	  
	  
	  
	  
	  
	  	  
   </tr>  				
<tr>
<td colspan="2">
	<hrms:priv func_id="3240513">
		<hrms:menubar menu="menu1" id="menubar1" container="" visible="false">
			 <hrms:menuitem name="m1" label="label.gz.variable" icon="" url="setVariable()"  function_id="32405131" />
			 <hrms:menuitem name="m2" label="label.gz.formula" icon="" url="setFormula()"  function_id="32405132" /> 
		</hrms:menubar>
		
	 
	</hrms:priv>
<bean:define id="sqls" value="${croPayMentForm.sqlstr}"/> 
<hrms:dataset name="croPayMentForm" property="fieldlist" scope="session" setname="${croPayMentForm.gz_grossname}" pagerows="${croPayMentForm.pagerows}" setalias="position_set" readonly="false" rowlock="true"  rowlockfield="<%=spflagid%>"  rowlockvalues=",01,07,09," editable="true" select="true" sql="${croPayMentForm.sqlstr}" buttons="bottom">      
	<hrms:commandbutton name="add" onclick="addgross();"  function_id="3240501"    ><bean:message key="kq.emp.button.add"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="deleterecord" hint="gz.acount.determined.del" functionId="3020080003"  function_id="3240502"  refresh="true" type="selected" setname="${croPayMentForm.gz_grossname}">
		<bean:message key="button.delete"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="save" hint="org.orgpre.orgpretable.baocunok"  functionId="3020080004"  function_id="3240503"   refresh="true" type="all-change" setname="${croPayMentForm.gz_grossname}" >
	    <bean:message key="button.save"/>
	</hrms:commandbutton> 
	
	<hrms:commandbutton name="appeal" refresh="true" function_id="3240512"  type="selected" setname="${croPayMentForm.gz_grossname}" onclick="appeal_gz_amount();">
		<bean:message key="button.appeal"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="release" hint="org.orgpre.orgpretable.approvalok" functionId="3020080012"  refresh="true"   function_id="3240505"  type="selected" setname="${croPayMentForm.gz_grossname}">
		<bean:message key="button.approve"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="suspended" hint="org.orgpre.orgpretable.bohuiok" functionId="3020080005"  refresh="true"  function_id="3240504"  type="selected" setname="${croPayMentForm.gz_grossname}">
		<bean:message key="button.reject"/>
	</hrms:commandbutton>
	<%if(fcVisible.equalsIgnoreCase("1")) {%>
		 <hrms:commandbutton name="usave" hint="org.orgpre.orgpretable.fengcunok" functionId="3020080089"  refresh="true"   function_id="3240511"  type="selected" setname="${croPayMentForm.gz_grossname}">
			<bean:message key="kq.deration_details.usave"/>
		</hrms:commandbutton> 
		<hrms:commandbutton name="dsave" hint="org.orgpre.orgpretable.jiefengok" functionId="3020080090"  refresh="true"   function_id="3240510"  type="selected" setname="${croPayMentForm.gz_grossname}">
			<bean:message key="kq.deration_details.dsave"/>
		</hrms:commandbutton>
		<%} %>
	<%if(!filtervalue.equalsIgnoreCase("0")) {%>
	<hrms:commandbutton name="compute" hint="" functionId="" onclick="countformula('${croPayMentForm.fieldsetid}','${croPayMentForm.yearnum}');" refresh="false"   function_id="3240509"  type="all" setname="${croPayMentForm.gz_grossname}">
		<bean:message key="infor.menu.compute"/>
	</hrms:commandbutton> 
	<%} %>
	<hrms:commandbutton name="inp" onclick="introduceNext();"  function_id="3240514">
		引用上期总额
	</hrms:commandbutton>
	<hrms:commandbutton name="derived" onclick="outExcel('1');"  function_id="3240506">
		<bean:message key="button.export"/>Excel
	</hrms:commandbutton>
	<hrms:commandbutton name="outputexc" onclick="outExcel('2');"  function_id="3240508">
		<bean:message key="button.download.template"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="inputexc" onclick="inputExcel();"  function_id="3240507">
		<bean:message key="import.tempData"/>
	</hrms:commandbutton> 
	<%if(isHasAdjustSet!=null && !isHasAdjustSet.equals("-1")){ %>
	<hrms:commandbutton name="adjust" onclick="adjustAmount();" >
		<bean:message key="adjust.record"/>
	</hrms:commandbutton> 
	<%} %>
	
	 
		<hrms:commandbutton name="setVar" menuid="menu1" function_id="3240513">
		<bean:message key="menu.gz.options"/>
		</hrms:commandbutton>  
	
	
	<%if(returnflag.equals("dxt")){ %>
	<hrms:commandbutton name="retvn" onclick="returnFlowPhoto();">
		<bean:message key="button.return"/>
	</hrms:commandbutton> 
<%} %>
</hrms:dataset>
</td>
</tr>
</table>

</logic:equal>
<script language="javascript">
 


function yincrease(){
    var yearnum = document.getElementById("yearnum").value; 
    var yearset = parseInt(yearnum);
	yearset = yearset+1;
	document.getElementById("yearnum").value = yearset;
	croPayMentForm.action="/gz/gz_amount/gropayment.do?b_query=link&opt=second";
    croPayMentForm.submit();
}
function ysubtract(){
    var yearnum = document.getElementById("yearnum").value; 
    var yearset = parseInt(yearnum);
	if(yearset<1991){
		document.getElementById("yearnum").value = 1990;
	}else{
		yearset = yearset-1;
		document.getElementById("yearnum").value = yearset;
	}
	croPayMentForm.action="/gz/gz_amount/gropayment.do?b_query=link&opt=second";
    croPayMentForm.submit();
}
function addgross(){
  	var codeitemid=document.getElementById("codeitemid").value;
    if(codeitemid==null||trim(codeitemid).length<=0)
    {
       alert("请选择一个单位或者部门进行添加操作！");
       return;
    }   
    var isCanCreate = document.getElementById("isCanCreate").value;
    if(isCanCreate=='0')
    {
       alert("薪资管理/参数设置/薪资总额参数，启用了“启用总额控制指标”参数，当单位/部门信息集中该指标值为“否”、“空”或无主集记录时，不允许增加总额记录！");
       return;
    }
    var thecodeurl ="/gz/gz_amount/addgross.jsp"; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:550px; dialogHeight:280px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
    	var hashvo=new ParameterSet();
    	hashvo.setValue("times",return_vo);
    
    	hashvo.setValue("codeitemid",codeitemid);
    	var fieldsetid=document.getElementById("fieldsetid").value;
    	hashvo.setValue("fieldsetid",fieldsetid);
    	var code = document.getElementById("code").value;
    	hashvo.setValue("code",code);
    	var ctrl_peroid  = document.getElementById("ctrl_peroid").value;
    	hashvo.setValue("ctrl_peroid",ctrl_peroid);
    	hashvo.setValue("cascadingctrl",document.getElementById("cascadingctrl").value);
    	hashvo.setValue("viewUnit",document.getElementById("viewUnit").value);
   		var request=new Request({asynchronous:false,onSuccess:checkadd,functionId:"3020080002"},hashvo);
    }
}
function checkadd(outparamters){
	var expre = outparamters.getValue("info");
	var exprelist = outparamters.getValue("list");
	var dw=380,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	if(expre!="ok"){
 		 var arguments=exprelist;
	     var return_vo= window.showModalDialog("/gz/gz_accounting/sp_process1.jsp",arguments, 
	     "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
	}
	croPayMentForm.action="/gz/gz_amount/gropayment.do?b_query=link&opt=second";
    croPayMentForm.submit();
}
function outExcel(flag){
	var  years =  document.getElementById("yearnum").value;	
	var codeitemid=document.getElementById("codeitemid").value;
	var fieldsetid=document.getElementById("fieldsetid").value;
	var ctrl_type=document.getElementById("ctrl_type").value;	
	var cascadingctrl=document.getElementById("cascadingctrl").value;
	var viewUnit=document.getElementById("viewUnit").value;
	var hashvo=new ParameterSet();
	hashvo.setValue("yearnum",years);
	hashvo.setValue("codeitemid",codeitemid);
	hashvo.setValue("fieldsetid",fieldsetid);
	hashvo.setValue("ctrl_type",ctrl_type);
	hashvo.setValue("cascadingctrl",cascadingctrl);
	hashvo.setValue("viewUnit",viewUnit);
	hashvo.setValue("filtervalue","${croPayMentForm.filtervalue}");
	hashvo.setValue("spType","${croPayMentForm.spType}");
	var In_paramters="flag="+flag; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'3020080006'},hashvo);
		
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	outName=getDecodeStr(outName);
    var win=open("/servlet/vfsservlet?fileid="+outName+"&fromjavafolder=true","excel");
}
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function setVariable()
{
	var str="/gz/tempvar/viewtempvar.do?b_query=link`state=-1`type=1`nflag=4`isAddTempVar=0";
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(str);
  	if(isIE6()){
  	  	var values= window.showModalDialog(iframe_url,window, 
		        "dialogWidth:930px; dialogHeight:600px;resizable:yes;center:yes;scroll:no;status:no");
  	}else{
  	  	var values= window.showModalDialog(iframe_url,window, 
		        "dialogWidth:890px; dialogHeight:550px;resizable:yes;center:yes;scroll:no;status:no");
  	}
    ///tempvarForm.action="/gz/tempvar/viewtempvar.do?b_query=link&state=-1&type=1&nflag=4";
    ///tempvarForm.submit();
}
function setFormula()
{
	var str="/gz/formula/viewformula.do?b_query=link`salaryid=-1";
  	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(str);
  	var values= window.showModalDialog(iframe_url,window, 
		        "dialogWidth:850px; dialogHeight:520px;resizable:yes;center:yes;scroll:yes;status:no");
	///tempvarForm.action="/gz/formula/viewformula.do?b_query=link&salaryid=-1";
    ///tempvarForm.submit();
}
function resetleft()
   {     
   		<%if(returnflag.equals("dxt")){ %>
   		a_obj=document.getElementById('buttonretvn')
   		<%}else if(isHasAdjustSet!=null && !isHasAdjustSet.equals("-1")){%>
   		a_obj=document.getElementById('buttonadjust')
   		<%}else{%>
   		a_obj=document.getElementById('buttoninputexc')
   		<%}%>
   		if(a_obj)
   		{
	   		oRect=a_obj.getBoundingClientRect();   
	   		var a_value=oRect.right;
	   		var selobj=document.getElementById('sel');
	   		if(selobj!=null)
	        	selobj.style.left=a_value+5;
	    }
   }

</script>
</html:form>
</body>