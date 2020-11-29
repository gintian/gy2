<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,
				java.util.*,
				com.hjsj.hrms.actionform.gz.gz_accounting.voucher.VoucherForm,
				org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<html>
<head>
 <%
 	VoucherForm voucherForm=(VoucherForm)session.getAttribute("voucherForm"); 
 	String _code=voucherForm.get_code();
 	String type=voucherForm.getType();  //1:财务凭证  2:按月汇总
 	ArrayList headList=voucherForm.getHeadList(); 
 	String dbilltimes=voucherForm.getDbilltimes(); // 发放次数
 	ArrayList dbilltimesList=voucherForm.getDbilltimesList(); // 发放次数集合
 	LazyDynaBean abean=null;
 	String clientName = SystemConfig.getPropertyValue("clientName").toUpperCase();
 	String isCwBz = SystemConfig.getPropertyValue("isCwBz").toUpperCase();
 	boolean isVisibleExport= false;
 	boolean isShowCwBz = false;
 	if(clientName.equalsIgnoreCase("tdk"))
 	{
 	   isVisibleExport=true;
 	}
 	if(null != isCwBz &&
 		!"".equalsIgnoreCase(isCwBz.toString())){
 		isShowCwBz = true;
 	}
 	/** 
 	*  原来此界面有3个报送按钮，对应后台3个交易类，如果既有凭证又有月汇总的凭证种类，按钮是比较乱的， 	
 	*  而且出现选择凭证种类的界面也是无意义的，比较乱
 	*  现在改成两个报送按钮，一种是出现选择凭证种类的窗口，一种不出现选择凭证种类的,默认出现选择界面 
 	*  还是由员参数isShowCwBz控制开启
 	*/
 	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 	
  %>
 
 
<title>
</title>
</head>

<style>

div#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}


.RecordRow_br {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 0pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22px;
}
</style>
<link href="/performance/evaluation/locked-column-new.css" rel="stylesheet" type="text/css"> 
<script language='javascript' >
function search()
{
	document.voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query";
	document.voucherForm.submit();

}
var retvo;
function executeData()
{
	var thecodeurl="/gz/gz_accounting/voucher/financial_voucher.do?br_setParam=query`_code=<%=_code%>`type=<%=type%>"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	retvo = window.showModalDialog(iframe_url, null, 
		        "dialogWidth:450px; dialogHeight:310px;resizable:no;center:yes;scroll:no;status:no");		
	if(retvo)
	{
		 var hashVo=new ParameterSet();
		 hashVo.setValue("type","<%=type%>");
		 hashVo.setValue("year",retvo[0]);
		 hashVo.setValue("month",retvo[1]); 
		 hashVo.setValue("count",retvo[2]);
		 hashVo.setValue("voucher_date",retvo[3]);
		 hashVo.setValue("deptcode",retvo[4]);
		 hashVo.setValue("flag",retvo[5]);
		 hashVo.setValue("voucher_id",document.getElementsByName("voucher_id")[0].value);
		 controlButton(true);
		 var request=new Request({asynchronous:false,method:'post',onSuccess:successFunc,onFailure:fail,functionId:'3020072014'},hashVo);			
		    
	}
}


function successFunc(outparamters)
{
	var info=outparamters.getValue("info");
	var type=outparamters.getValue("type");
	var oper="1";
	if(info=='2' && type=='1')
	{
		if(!confirm('已为单位部门生成过相应发放月份的凭证数据，是否继续？'))
		{
				 controlButton(false);
				return;
		}
	}
	if(info=='2' && type=='2')
	{
	  if(confirm("部分薪资汇总数据已存在，是否需要覆盖？"))
	  {
	     oper="2";
	  }else{
	    oper="1";
	  }
	}
	var flag=outparamters.getValue("flag");
	var year=outparamters.getValue("year");
	var month=outparamters.getValue("month");
	var count=outparamters.getValue("count");
	var voucher_date=outparamters.getValue("voucher_date");
	var deptcode=outparamters.getValue("deptcode");
 	
 	var hashVo=new HashMap();
 	hashVo.put("oper",oper);
	hashVo.put("type",type);
	hashVo.put("flag",flag);
    hashVo.put("year",year);
	hashVo.put("month",month);
	hashVo.put("count",count);
	hashVo.put("voucher_date",voucher_date);
	hashVo.put("deptcode",deptcode);
	hashVo.put("voucher_id",document.getElementsByName("voucher_id")[0].value);
	var waitInfo=eval("wait");	
	waitInfo.style.display="block";
	//var request=new Request({method:'post',asynchronous:true,onSuccess:successFunc2,onFailure:fail,functionId:'3020072015'},hashVo);
	Rpc({functionId:'3020072015',async:true,success:successFunc2},hashVo);//xiegh 20170524 bug:22817  add: 薪资发放，财务凭证，点击生成，给出的提示 能否提示谁与谁重复，方便核查  
}
function fail(){
	controlButton(false);
}


function controlButton(flag)
{
	if(document.getElementsByName("create")!=null&&document.getElementsByName("create").length>0)
	  document.getElementsByName("create")[0].disabled=flag;
	if(document.getElementsByName("notice")!=null&&document.getElementsByName("notice").length>0)
	  document.getElementsByName("notice")[0].disabled=flag;
	if(document.getElementsByName("export")!=null&&document.getElementsByName("export").length>0)
	  document.getElementsByName("export")[0].disabled=flag;
	var type="${voucherForm.type}";
	if(document.getElementsByName("delete")!=null&&document.getElementsByName("delete").length>0)
    	document.getElementsByName("delete")[0].disabled=flag;

}

function successFunc2(outparamters)
{
	var res=Ext.decode(outparamters.responseText);
	if(res.succeed){
		var url=res.url;
		if(url){
			if(confirm("凭证涉及人员的唯一指标值有重复数据，详情请见Excel！")){
				var waitInfo=eval("wait");	
				waitInfo.style.display="none";
				controlButton(false);
				window.location.target="_blank";
				window.location.href="/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true";
			}
		}else{ 
			/* bug: 28265  生成凭证后定位到生成的计提日期和voucher_id上的界面上*/
			   var voucher_id = document.getElementsByName("voucher_id")[0].value;
		       document.voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query&timeInfo="+retvo[0]+"-"+retvo[1]+"&deptcode="+retvo[4]+"&voucher_id="+voucher_id;
			   document.voucherForm.submit();
		}
	}else{
		controlButton(false);
		var waitInfo=eval("wait");	
	    waitInfo.style.display="none";
	    alert(res.message);
	}
}
function deleteData()
{	
	var str="";
	 var type="${voucherForm.type}";
	 var voucher_id="${voucherForm.voucher_id}";
	for(var i=0;i<document.voucherForm.elements.length;i++)
	{
		if(document.voucherForm.elements[i].type=="checkbox")
		{					
			var ff = voucherForm.elements[i].name.substring(0,26);						
			if(document.voucherForm.elements[i].checked==true && ff=='voucherInfoListform.select')
			{
				str+=document.voucherForm.elements[i+1].value+"/";
			}
		}
	}
	if(str.length==0)
	{
		alert("请选择记录，再进行操作！");
		return;
	}
	else
	{		
		if (confirm('确认要删除吗？'))
		{				
			var hashvo=new ParameterSet();
	  		hashvo.setValue("deletestr",str);	
	  		hashvo.setValue("type",type);	
	  		hashvo.setValue("voucher_id",voucher_id);
	  		var request=new Request({asynchronous:false,onSuccess:delDataValue_ok,functionId:'3020072016'},hashvo); 												
		}
	}			
}
function delDataValue_ok(outparameters)
{    		
   	var msg = getDecodeStr(outparameters.getValue("msg"));
   	var type=outparameters.getValue("type");
   	if(type=='1'){
	   	if(msg=='error')
	   		alert("只有起草及失败状态的记录允许删除！");
	}else{
	    alert("删除成功！");
	}
    voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query";
	voucherForm.submit();
   		  
}
function queryHKBankData()
{
	var voucher_id="${voucherForm.voucher_id}";	
	var timeInfo = document.getElementById("timeInfo");
	if (timeInfo.value == 'all') 
	{
		alert("请选择具体月份再查询！");
		return;
	}
	<%
		if(dbilltimesList!=null && dbilltimesList.size()>2 && (dbilltimes!=null && dbilltimes.trim().length()>0 && dbilltimes.equalsIgnoreCase("all"))){
	%>
		alert("请选择具体发放次数再查询！");
		return;
	<%
		}
	%>
	
	var hashvo=new ParameterSet();	
	hashvo.setValue("voucher_id",voucher_id);
	hashvo.setValue("timeInfo",timeInfo.value);
	hashvo.setValue("dbilltimes","${voucherForm.dbilltimes}");
	var request=new Request({method:'post',asynchronous:false,onSuccess:show,functionId:'3020072120'},hashvo);
}

function show(outparamters)
{
	var flag=outparamters.getValue("flag");
	var msgStr=getDecodeStr(outparamters.getValue("msgStr"));
		
	alert(msgStr);
	if(flag=='1')
	{
		window.location.href = window.location.href;
	}
}

function sendNXYPData() {
	var voucherid = "${voucherForm.voucher_id}";
	var theArr = new Array(voucherid);//bug36541 add by xiegh 
	var timeInfo = document.getElementById("timeInfo");
	if (timeInfo.value == 'all') {
		alert("请选择具体月份！");
		return;
	}
	
	var thecodeurl="/gz/gz_accounting/voucher/financial_voucher.do?br_searchvoucher=link"; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	var retvo= window.showModalDialog(iframe_url, theArr, 
		        "dialogWidth:500px; dialogHeight:450px;resizable:no;center:yes;scroll:yes;status:no");	
		       	
	if(retvo)
	{			
		var hashvo=new ParameterSet();
		hashvo.setValue("pzIds",getEncodeStr(retvo));
		hashvo.setValue("timeInfo",timeInfo.value);
		hashvo.setValue("dbilltimes","${voucherForm.dbilltimes}");		
		var request=new Request({asynchronous:false,onSuccess:sendNXYPDataValue_ok,functionId:'3020072018'},hashvo); 												
	}
}

// 汉口银行报送功能  也是标准报送功能
function sendCWBZData() 
{
	var voucher_id = "${voucherForm.voucher_id}";
	var timeInfo = document.getElementById("timeInfo");
	if (timeInfo.value == 'all') 
	{
		alert("请选择具体月份再报送！");
		return;
	}
	<%
		if(dbilltimesList!=null && dbilltimesList.size()>2 && (dbilltimes!=null && dbilltimes.trim().length()>0 && dbilltimes.equalsIgnoreCase("all"))){
	%>
		alert("请选择具体发放次数再报送！");
		return;
	<%
		}
	%>
	
	if (confirm('确认要报送吗？'))
	{			
		var hashvo=new ParameterSet();
//		hashvo.setValue("pzIds",getEncodeStr(retvo));
		hashvo.setValue("pzIds",voucher_id);
		hashvo.setValue("timeInfo",timeInfo.value);	
		hashvo.setValue("dbilltimes","${voucherForm.dbilltimes}");	
		var request=new Request({asynchronous:false,onSuccess:sendNXYPDataValue_ok,functionId:'3020072118'},hashvo); 												
	}
}

function sendNXYPDataValue_ok (outparameters) {
		var msg = getDecodeStr(outparameters.getValue("erroStr"));
		if (msg!=""){
		  alert(msg);
		}
		voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query";
		voucherForm.submit();
}
function send(type)
{
  var timeInfo=document.getElementById("timeInfo").value;
  if(timeInfo=='all'){
     alert("不能选择全部进行报送，请选择具体月份！");
     return;
  }
  var hashvo=new ParameterSet();
  hashvo.setValue("type",type);	
  hashvo.setValue("time",timeInfo);
  hashvo.setValue("pn_id",document.getElementsByName("voucher_id")[0].value);
  hashvo.setValue("status",document.getElementsByName("status")[0].value);
  hashvo.setValue("a_code","${voucherForm._code}");
  var request=new Request({asynchronous:false,onSuccess:send_ok,functionId:'3020072017'},hashvo); 	
}
function send_ok(outparameters){
 var flag=outparameters.getValue("flag");
 if(flag=='3'){
   alert("报送数据失败，请检查WebService接口是否正确！");
   return;
 }else if(flag=='1'){
   alert("没有可报送的数据！");
   return;
 }else if(flag=='4'){
	   alert("发送失败！");
	   return;
 }else{
   alert("数据报送成功！");
   voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query";
	voucherForm.submit();
 }

}
function exportData(type){
 var timeInfo=document.getElementById("timeInfo").value;
  if(timeInfo=='all'){
     alert("不能选择全部进行导出，请选择具体月份！");
     return;
  }
  var fileType="1,0";
  var clientName="<%=clientName%>";
  if(clientName!='TDK'){
     var retvo= window.showModalDialog("/gz/gz_accounting/voucher/financial_voucher.do?br_select=select", null, 
		        "dialogWidth:450px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");	
	 if(retvo==null)
	   	return;
	 else{
	 	fileType=retvo.fileType;
	 }
  }
  var hashvo=new ParameterSet();
  hashvo.setValue("type",type);	
  hashvo.setValue("time",timeInfo);
  hashvo.setValue("pn_id",document.getElementsByName("voucher_id")[0].value);
  hashvo.setValue("status",document.getElementsByName("status")[0].value);
  hashvo.setValue("a_code","${voucherForm._code}");
  hashvo.setValue("dbilltimes","${voucherForm.dbilltimes}");
  hashvo.setValue("fileType",fileType);
  var request=new Request({asynchronous:false,onSuccess:export_ok,functionId:'3020072019'},hashvo); 	
}
function export_ok(outparameters){
    var fileName=outparameters.getValue("fileName");
    var fileType=outparameters.getValue("fileType");
    var ext="txt";
    if(fileType=='2')
       ext="xls";
	fileName = getDecodeStr(fileName);
	var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true", ext);
	voucherForm.action="/gz/gz_accounting/voucher/financial_voucher.do?b_query=query";
    voucherForm.submit();
			
}
</script>


<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<body>
<html:form action="/gz/gz_accounting/voucher/financial_voucher">
<%if("hl".equals(hcmflag)){ %>
<table width="100%"><tr><td>
<%}else{ %>
<table width="100%" style="margin-top:-5px;"><tr><td>
<%} %>

<table><tr>
<td>
<hrms:priv func_id="3241701">
<button name="create" Class="mybutton"  onclick='executeData()'  /><bean:message key='gz.voucher.execute'/></button>
</hrms:priv>

<hrms:priv func_id="3241702">
<%if(!isVisibleExport){ %>
	<%if(isShowCwBz){ %>
	   <button name="notice" Class="mybutton"  onclick='sendCWBZData()' /><bean:message key='gz.voucher.appeal'/></button>
	<%}else{%>
	 	<logic:equal value="1" name="voucherForm" property="type">
			<button name="notice" Class="mybutton"  onclick='sendNXYPData()' /><bean:message key='gz.voucher.appeal'/></button>
	 	</logic:equal>
	 	 <logic:equal value="2" name="voucherForm" property="type">
			<button name="notice" Class="mybutton"   onclick='send("${voucherForm.type}")' /><bean:message key='gz.voucher.appeal'/></button>
		</logic:equal>
	 <%}%>
	 <%String cwpzquery=SystemConfig.getPropertyValue("cwpzquery");// 获得配置文件信息 
	 if("true".equalsIgnoreCase(cwpzquery)){%>
		 <logic:equal value="1" name="voucherForm" property="type">
		 <button name="notice" Class="mybutton"  onclick='queryHKBankData()' />查询</button>
		 </logic:equal>
	 <%} %>
 <%}%>
</hrms:priv>

<hrms:priv func_id="3241704">
<button name="export" Class="mybutton"   onclick='exportData("${voucherForm.type}")' /><bean:message key='button.export'/></button>
</hrms:priv>

<hrms:priv func_id="3241703">
<button name="delete" Class="mybutton"   onclick='deleteData()' /><bean:message key='reportcyclelist.del'/></button>
</hrms:priv>
 </td>

<td width='75' align='right'  ><bean:message key='gz.voucher.name'/>&nbsp;</td><%--xiegh 20170526 bug:28040 --%>
<td>
<html:select name="voucherForm" property="voucher_id"   onchange="search()"   size="1">
	<html:optionsCollection property="voucherList" value="dataValue" label="dataName"/>
</html:select>  
</td>
<td width='35' align='right'  ><bean:message key='gz.acount.month'/></td>
<td>
<html:select name="voucherForm" property="timeInfo"   onchange="search()"   size="1" styleId="timeInfo">
	<html:optionsCollection property="timeList" value="dataValue" label="dataName"/>
</html:select>  
</td>
<td width='45' align='right'  ><bean:message key='hire.jp.pos.state'/>&nbsp;</td>
<td>
<html:select name="voucherForm" property="status"   onchange="search()"   size="1">
								                              <html:optionsCollection property="statusList" value="dataValue" label="dataName"/>
</html:select>  
</td>
<td width='70' align='right' >发放次数</td>
<td>
<html:select name="voucherForm" property="dbilltimes"   onchange="search()"   size="1">
								                              <html:optionsCollection property="dbilltimesList" value="dataValue" label="dataName"/>
</html:select>  
</td>
</tr></table>


</td></tr>
<tr><td>



<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-100)+";width:99%'  >");
</script> 



<table id='tbl'  width="100%" cellspacing="0"  >
   	  <thead>
        <tr>
         <td align="center" class="tableRow cell_locked2"  style="border-top:none;border-left:none;border-right:none;position:relative;top:expression(this.offsetParent.scrollTop);" nowrap width="10">
		   <input type="checkbox" name="selbox" onclick="batch_select(this,'voucherInfoListform.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
	     </td> 
	   
         <% for(int i=0;i<headList.size();i++) 
            {
         		abean=(LazyDynaBean)headList.get(i);
         		String itemname=(String)abean.get("itemname");
         		String itemid=(String)abean.get("itemid");
         		if(itemid.equalsIgnoreCase("id"))
         	    	continue;
         		String class_name="header_locked";
         		if(i==0)
         			class_name="cell_locked2";
         %>
         
         <td align="center" class="<%=class_name%> tableRow" style="border-top:none;border-right:none;" nowrap >
		   &nbsp;&nbsp;<%=itemname%>&nbsp;&nbsp;
	     </td>          
		 <%
		 	}
		  %>
		</tr>
		</thead>
		
	    <hrms:extenditerate id="element" name="voucherForm" property="voucherInfoListform.list" indexes="indexes"  pagination="voucherInfoListform.pagination" pageCount="${voucherForm.pagerows}" scope="session">
         
          <tr  class="trShallow"   >
          
         <td align="center" class="RecordRow t_cell_locked" style="border-top:none;border-left:none;border-right:none;" nowrap>
         
   		<hrms:checkmultibox name="voucherForm" property="voucherInfoListform.select" value="true" indexes="indexes"/>&nbsp;
   		<Input type='hidden' value='<bean:write name="element" property="id" filter="true"/>' />
	    </td>
	
         <% for(int i=0;i<headList.size();i++) 
            {
         		abean=(LazyDynaBean)headList.get(i);
         		String itemid=(String)abean.get("itemid");
         		if(itemid.equalsIgnoreCase("id"))
         	    	continue;
         		String itemtype=(String)abean.get("itemtype");
         		String null_str_l="";
         		String null_str_r="&nbsp;&nbsp;";
         		String align="left";
         		if(itemtype.equalsIgnoreCase("N")||itemtype.equalsIgnoreCase("I"))
         		{
         			align="right";
         			null_str_l="&nbsp;&nbsp;";
         			null_str_r="";
         		}
         		String class_name="RecordRow";
         		if(i==0)
         			class_name="t_cell_locked";
         		else if(i==1)
         			class_name="RecordRow_br";
         		 
         %>
			 <td align="<%=align%>" class="<%=class_name%> RecordRow" style="border-top:none;border-right:none;"  nowrap >
			 <%=null_str_l%>&nbsp;<bean:write  name="element" property="<%=itemid%>" filter="true"/>&nbsp;<%=null_str_r%>
			 </td>
		
		<%  } %>
		  </tr>
		
		</hrms:extenditerate>
		
		
</table>

</div>
<script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-58)+";width:99%'  >");
	</script>
	<table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" align="left" class="tdFontcolor">
				<bean:message key='label.page.serial'/>
				<bean:write name="voucherForm"
					property="voucherInfoListform.pagination.current" filter="true" />
				<bean:message key='label.page.sum'/>
				<bean:write name="voucherForm"
					property="voucherInfoListform.pagination.count" filter="true" />
				<bean:message key='label.page.row'/>
				<bean:write name="voucherForm"
					property="voucherInfoListform.pagination.pages" filter="true" />
				<bean:message key='label.page.page'/>
				&nbsp;&nbsp; 每页显示<html:text property="pagerows" styleId="pagenum" name="voucherForm" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:search();">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="voucherForm"
						property="voucherInfoListform.pagination" nameId="voucherInfoListform"
						propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
</div>
 <div id='wait' style='position:absolute;top:150;left:350;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					<bean:message key="org.autostatic.mainp.calculation.wait"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
</td></tr>
</html:form>
</body>

</html>