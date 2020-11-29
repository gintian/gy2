
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%
 response.setHeader("Pragma","No-cache"); 
 response.setHeader("Cache-Control","no-store,no-cache"); 
 response.setHeader("Expires", "0"); 
 response.setDateHeader("Expires", 0);  %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript"
	src="/components/codeSelector/codeSelector.js"></script>
<script language="JavaScript"
	src="/components/extWidget/proxy/TransactionProxy.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript"
	src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>

<%@ page
	import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,
			     java.util.*,com.hrms.hjsj.sys.ResourceFactory"%>
<%@ page
	import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode"%>
<%@ page
	import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,
			     org.apache.commons.beanutils.LazyDynaBean,com.hrms.hjsj.utils.Sql_switcher,
			     com.hrms.struts.taglib.CommonData,com.hrms.hjsj.sys.Constant,
			     java.util.*,com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<style>
.input {
	width: 200px;
	float: left;
	background: url(../../images/hire/input_l.gif) no-repeat left;
	position: relative;
	white-space: nowrap;
	z-index: 1;
}
.pages {width:638px;margin-left: auto; margin-right: auto;}
.pages ul li { float:left; *float:left; height:30px;  line-height:30px; 
border:none; margin:0 10px 0 0; display:inline-block; *display:inline; *zoom:1;}
.pages ul li a{color:blue;}
.pages ul li a:visited {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
.pages ul li a:hover {
	font-size: 12px;
	color: #E39E19;
	text-decoration: none;
}
.ul_pages {color:blue;float:right;}
.index .gg .index_list ul li{
	font-size:12px !important;
}
</style>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Expires" CONTENT="0">
<meta http-equiv="Cache-Control" CONTENT="no-cache">
<meta http-equiv="Pragma" CONTENT="no-cache">


<%
  session.setAttribute("islogon",new Boolean("true"));
	String dbtype="1";
  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
  {
    dbtype="2";
  }
  else if(Sql_switcher.searchDbServer()== Constant.DB2)
  {
    dbtype="3";
  }
  EmployPortalForm employPortalForm2=(EmployPortalForm)session.getAttribute("employPortalForm");
  String isResumePerfection=employPortalForm2.getIsResumePerfection();
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   String userViewName="";
   String isHeadhunter ="";
   if(userView!=null){
       userViewName=userView.getUserName();
       isHeadhunter = (String)userView.getHm().get("isHeadhunter");
   }
   ArrayList conditionFieldList =  employPortalForm2.getConditionFieldList();
   HashMap hm = employPortalForm2.getFormHM();
   String channelName = (String)hm.get("channelName");
   
   Calendar c = Calendar.getInstance();
   int hour = c.get(Calendar.HOUR_OF_DAY); 
 %>
<style type="text/css">
.img-middle {
	vertical-align: middle;
}
</style>
<script language='javascript'>
	var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
	var a0100 = "${employPortalForm.a0100}";
	var cardid = "${employPortalForm.admissionCard}";
	var nbase = "${employPortalForm.dbName}";
	var msg =false;
	var sclength=window.screen.length;
	
	function showlist(code){
	
			var floor= document.getElementById("floor"+code);
			floor.style.display='block';
				var input=document.getElementById("input"+code)
				input.style.zIndex='10000'
			
			floor.focus();
	}
	function change(code,name,value,tt){
		var span=document.getElementById("span"+code);
		var floor= document.getElementById("floor"+code);
		var input=document.getElementById("input"+code);
		var img=document.getElementById("img2");
		var con=0;
		var ert=0;
		var has=false;
		if(name.length>=6){
			span.innerHTML =name.substring(0,7);
		}else{
			span.innerHTML =name;;
		}
			floor.style.display='none';
			var inpt=document.getElementsByName(tt)[0];
		inpt.value=value;
		}
	function hide(code){
			Element.hide("floor"+code);
			var input=document.getElementById("input"+code)
			input.style.zIndex='0';
		}
   function hide3(code){
			Element.hide("floor"+code);
			var input=document.getElementById("input"+code)
			input.style.zIndex='0';
		}
	function query(flag,id)
	{
		if(msg)
			return;
		
		if(flag==1){
			msg =true;
		<% int m=0;  %>
		<logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index"> 
		<% if(index<=2){ %>
				<logic:equal name="element" property="itemtype" value="D">
					var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value");
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/;
						 if(IsOverStrLength(a<%=m%>[0].value,10))
						 {
							 Ext.showAlert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
							 return;
						 }
						 else
						 {
						 	if(trim(a<%=m%>[0].value).length!=10)
						 	{
						 		Ext.showAlert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
							var year=a<%=m%>[0].value.substring(0,4);
							var month=a<%=m%>[0].value.substring(5,7);
							var day=a<%=m%>[0].value.substring(8,10);
							if(!myReg.test(year)||!myReg.test(month)||!myReg.test(day)) 
						 	{
								Ext.showAlert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 	if(year<1900||year>2100)
						 	{
						 		Ext.showAlert("<bean:write  name="element" property="itemdesc"/> "+YEAR_SCORPE+"！");
								 return;
						 	}
						 	
						 	if(!isValidDate(day, month, year))
						 	{
						 		Ext.showAlert("<bean:write  name="element" property="itemdesc"/> "+DATE_FORMAT_IS_NOT_RIGHT+"！");
								 return;
						 	}
						 }
					 }
				</logic:equal>	
				
							
				<logic:equal name="element" property="itemtype" value="N">
					var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value");
					if(trim(a<%=m%>[0].value).length!=0)
					{						
						 var myReg =/^(-?\d+)(\.\d+)?$/;
						 if(!myReg.test(a<%=m%>[0].value)) 
						 {
							 Ext.showAlert("<bean:write  name="element" property="itemdesc"/>"+PLEASE_INPUT_NUMBER+"！");
							return;
						 }
					 }
				</logic:equal>
				<logic:equal name="element" property="itemtype" value="A">
					<logic:equal name="element" property="codesetid" value="0">
						var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value");
						if(trim(a<%=m%>[0].value).length!=0)
						{
							if(IsOverStrLength(a<%=m%>[0].value,<bean:write  name="element" property="itemlength"/>))
							{
								Ext.showAlert("<bean:write  name="element" property="itemdesc"/>"+OVER_LENGTH_SCOPE);
								return;
							}
						}
					
					</logic:equal>
					<logic:notEqual name="element" property="codesetid" value="0">
						<logic:equal name="element" property="isMore" value="1">
						var a<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].value");
						var aa<%=m%>=document.getElementsByName("conditionFieldList[<%=m%>].viewvalue_view");
						if(trim(aa<%=m%>[0].value).length==0)
						{							
							a<%=m%>[0].value="";
						}
						</logic:equal>
					</logic:notEqual>
					
				</logic:equal>			
			<% m++;} %>	
		</logic:iterate>	
		<%if(request.getParameter("isAll")!=null){%>
		   
		    document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAll=<%=request.getParameter("isAll")%>&returnType=search";
		    document.employPortalForm.submit();
		<%}else{%>
	    	document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=search";
		    document.employPortalForm.submit();
		<%}%>
		}else{
			msg =true;
			var conditiontemp = new Array();
			<% int nm=0;  %>
			<logic:iterate  id="element"    name="employPortalForm"  property="conditionFieldList" indexId="index">
			<%
            LazyDynaBean bean = (LazyDynaBean)element;
            String itemid = (String)bean.get("itemid");
            String itemtype = (String)bean.get("itemtype");
            String codesetid = (String)bean.get("codesetid");
            String viewvalue = (String)bean.get("viewvalue");
            %>
            var a<%=nm%>=document.getElementsByName("conditionFieldList[<%=nm%>].value");
				var map = new HashMap();
				map.put("itemid",'<%=itemid%>');
				map.put("itemtype",'<%=itemtype%>');
				map.put("codesetid",'<%=codesetid%>');
				map.put("value",trim(a<%=nm%>[0].value));
				map.put("viewvalue",'<%=viewvalue%>');
					
				<% nm++; %>	
				conditiontemp.push(map);
			</logic:iterate>
			
				var hashMap = new HashMap();
				hashMap.put("hireChannel","${employPortalForm.hireChannel}");
				hashMap.put("isAllPosOk","1");
				hashMap.put("selunitcode",id);
				hashMap.put("conditiontemp",conditiontemp);
				Rpc({async:false,success:getMorePos,functionId:'90100170009'},hashMap);
		       
		}
	}

	function getMorePos(outparameters) {
		var result = Ext.decode(outparameters.responseText);
		var poslist=result.poslist;
		var unitid = result.unitid;
		var tableobj=document.getElementById(unitid);
		var deleterows = tableobj.rows.length;
		//表格中除了表头信息全部删掉
		for(var num = 1;num<deleterows;num++){
			tableobj.deleteRow(1);
		}
		var posFieldList = result.posFieldList;
		var moreImg=document.getElementById("more" + unitid);
		if(moreImg != null)
			moreImg.style.display="none";

		msg =false;
		<%
		String defautPosNum=employPortalForm2.getPositionNumber();
		int defPosNum=Integer.parseInt(defautPosNum);
		String returnType=request.getParameter("returnType");
		returnType = returnType == null ? "1" : returnType;
		
		%>
		for (var i = 0; i<poslist.length; i++) {
			var aPos = poslist[i];
			var posName=aPos.posName;
			var z0301=aPos.z0301;
			var z0311=aPos.z0311;
			var state=aPos.state;
			var isNewPos=aPos.isNewPos;
			var p="";
				
			if('${employPortalForm.hireChannel}'=="01"&&'${employPortalForm.hireMajor}' !="-1") {
				p = aPos.major == null ? "" : "&major=" + aPos.major;
			}
	
			var rowobj=tableobj.insertRow(tableobj.rows.length);
  		    var isApplyed = aPos.isApplyedPos;
			
			if(posFieldList != null && posFieldList.length > 0) {
				for (var m = 0; m < posFieldList.length; m++) {

		  		    var abean = posFieldList[m];
		  		    
		  		    var itemid = abean.itemid;
		  		    if(itemid == "z0321" || itemid == "z0311")
		  		       itemid=itemid+"Name";
		  		    
		  		    if(itemid == "z0311Name" && '${employPortalForm.hireChannel}' == "01" && '${employPortalForm.hireMajor}' != "-1")
		  		      itemid='${employPortalForm.hireMajor}';
		  		   
		  		    eval("var value = aPos."+itemid+";");
		  		   
		  		    if(value == null || value == "")
		  		       value="&nbsp;";

		  		    if(itemid == "z0351" || itemid == "z0311" || itemid == "ypljl" 
			  		    || itemid == '${employPortalForm.hireMajor}' || itemid == "z0311Name") {
		  		    	//已登录且已经申请该职位
						if(<%=StringUtils.isNotEmpty(userViewName)%> && "true" == isApplyed && itemid == "ypljl")								  		    	
			  		       value = "已申请";
						else
			  		       value="<a onclick=\"rediract('"+p+"','"+z0311+"','"+z0301+"','"+posName+"','','<%=returnType %>','"+unitid+"')\"> "+value+"</a>";
			  		}
		  		    
		  		    if (m==0) {
		  		        if (state == "1") { 
		  		            value+="<IMG border=0 src='/images/hot.gif' >"; 
		  		        }
		  		       
		  		        if (isNewPos == "1") {
		  		            value+="<IMG border=0 src='/images/new0.gif' />";
		  		        }
		  		    } 
		  			
		  		  	var cell = rowobj.insertCell(rowobj.cells.length);
		  			cell.style.height="36";
					cell.style.textAlign="center";
					cell.className ="hire_posTable_href";
					if (i%2 == 1)
					    cell.className = "table_line_single hire_posTable_href";
					
					cell.innerHTML= value;
				}
			} else {
				var z0333=aPos.z0333;
				var z0331=aPos.z0331;
				var z0329=aPos.z0329;
				var count=aPos.count;
				var z0313=aPos.z0313;
				if(!z0333||z0333=="")
				       z0333="&nbsp;";
				if(!z0329||z0329=="")
				      z0329="&nbsp;";
				if(!count||count=="")
				      count="&nbsp;";
				if(!z0313||z0313=="")
				    z0313="&nbsp;";
				
				var rowobj=tableobj.insertRow(tableobj.rows.length);
				var cell1=rowobj.insertCell(rowobj.cells.length);
				var cell2=rowobj.insertCell(rowobj.cells.length);
				var cell3=rowobj.insertCell(rowobj.cells.length);
				var cell4=rowobj.insertCell(rowobj.cells.length);
				var cell5=rowobj.insertCell(rowobj.cells.length);
	
	
				cell1.style.height="36";
				cell1.style.textAlign="center";
				cell2.style.height="36";
				cell2.style.textAlign="center";
				cell3.style.height="36";
				cell3.style.textAlign="center";
				cell4.style.height="36";
				cell4.style.textAlign="center";
				cell5.style.height="36";
				cell5.style.textAlign="center";
			    cell1.className = "hire_posTable_href";
				if (i%2 == 1) {
				    cell1.className = "table_line_single hire_posTable_href";
				    cell2.className = "table_line_single";
				    cell3.className = "table_line_single";
				    cell4.className = "table_line_single";
				    cell5.className = "table_line_single";			    
				}
				
				var rediract = "<a onclick=\"rediract('"+p+"','"+z0311+"','"+z0301+"','"+posName+"','','<%=returnType %>','"+unitid+"');\">";
				posName = "&nbsp;&nbsp;" + rediract +  posName;
	             
	             if(state=="1"){ 
	            	 posName += "<IMG border=0 src='/images/hot.gif'>";
	             }
	             if(isNewPos=="1") {
	                 posName += "<IMG border=0 src='/images/new0.gif'/>";
			     }
	             posName += "</a>";
	            
				cell1.innerHTML=posName;
				cell2.innerHTML=z0333;
				cell3.innerHTML=z0329;
				if('<%=SystemConfig.getPropertyValue("zp_visibletype")%>'!='1')
				    cell4.innerHTML=count+"&nbsp;";
	            else
	            	cell4.innerHTML=z0313+"&nbsp;";
	            
	            cell5.innerHTML=rediract + '<bean:message key="hire.column.applay"/>' + "</a>";
			}
		}
	}
	function showpage(id,title,hasfile){
		   var strurl="/hire/hireNetPortal/showpage.jsp?title="+getEncodeStr(title)+"&dml="+getEncodeStr(id)+"&hasfile="+getEncodeStr(hasfile);
			window.open(strurl,"blank","width="+window.screen.width+",height="+window.screen.height+"top=0,left=0,toolbar=no,menubar=no,scrollbars=yes,resizable=no,location=no,status=no");

	}
	function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;
      var DBType="<%=dbtype%>";
      var UserName="<%=userViewName%>";
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSuperUser(superUser);
      obj.SetUserMenuPriv(menuPriv);
      obj.SetUserTablePriv(tablePriv);
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("su");
}	
  function previewTableByActive()
  {
   var hashvo=new ParameterSet();
   hashvo.setValue("dbname","${employPortalForm.dbName}");   
   hashvo.setValue("inforkind","1"); 
   hashvo.setValue("flag","hire");
   hashvo.setValue("id","${employPortalForm.a0100}"); 
   var request=new Request({method:'post',onSuccess:showPrint,functionId:'07020100078'},hashvo);
  }


function showMore(){
	var ss=getCookie('hjsjpos');
	ss=getEncodeStr(ss);
	if(ss!=null&&ss.length!=0){
	var url="/hire/hireNetPortal/search_zp_position.do?b_query=link&z0301="+ss+"&returnType=resume&operate=init&hireChannel=${employPortalForm.hireChannel}&isShowAall=1";
	document.employPortalForm.action=url;
	window.setTimeout("document.employPortalForm.submit()",0);//采用延时加载是为了使ie6能够跳转
	
	}


}
 function finished() {      
           <% 
	if(request.getParameter("finished")!=null&&request.getParameter("finished").equals("1"))
	{
	if(isResumePerfection.equals("0")){
    %>
    Ext.Msg.show({
		title:"提示信息",
		message:"您的简历资料没有填写完整，请完善您的简历！",
		buttons: Ext.Msg.OK,
	    icon: Ext.Msg.INFO,
	    fn: function(btn) {
	    	if (btn === 'ok') {
            document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1";
            document.employPortalForm.submit();
	    	}
	    }
    });
    <%} else if(!"1".equals(isResumePerfection)){%>
    		var str = "<%=isResumePerfection%>";
    		var arr = str.split("-");
    		var message = "";
    		if(arr.length>2)
    			message = arr[1]+arr[2];
    		else
    			message = arr[1]+"必须填写！";
    		Ext.Msg.show({
    			title:"提示信息",
    			message:"您的简历资料没有填写完整，请完善您的简历！",
    			buttons: Ext.Msg.OK,
    		    icon: Ext.Msg.INFO,
    		    fn: function(btn) {
    		    	if (btn === 'ok') {
			    		document.employPortalForm.action="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID="+arr[0]+"&opt=1";
			            document.employPortalForm.submit();
    		    	}
    		    }
    		});
	<%}else {%>
		Ext.showAlert(YOUR_RESUME_SUBMIT_SUCCESS);
	
	<%}}%>
  }
 
finished();
//清空已输入内容
function clean_value(e,obj){
	 var event = e?e:window.event;
	 if(event.keyCode==8){
		 obj.value="";
		 var ojb_name = obj.name;
		 document.getElementsByName(ojb_name.split(".")[0]+".viewvalue_value")[0].value="";
		 document.getElementsByName(ojb_name.split(".")[0]+".value")[0].value="";
	 }
} 
</script>
</head>
<body onload="">
	<input type="hidden" id="hour" value="<%=hour%>" />
	<div id="ly"
		style="position: absolute; top: 0px; FILTER: alpha(opacity = 0); background-color: #FFF; z-index: 2; left: 0px; display: none;"></div>
	<div id='wait1'
		style='position: absolute; top: 285; left: 80; display: none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4"
			class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>激活邮件发送中,请稍候......</td>
			</tr>
			<tr>
				<td style="font-size: 12px; line-height: 200%" align=center><marquee
						class="marquee_style" direction="right" width="300"
						scrollamount="5" scrolldelay="10">
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
					</marquee></td>
			</tr>
		</table>
	</div>
	<div id="biaodan"></div>
	<div id='chajian'></div>
	<html:form action="/hire/hireNetPortal/search_zp_position">
		<%
			EmployPortalForm employPortalForm = (EmployPortalForm) session.getAttribute("employPortalForm");

				String hireMajorCode = employPortalForm.getHireMajorCode();
				String hireMajor = employPortalForm.getHireMajor();
				String isQueryCondition = employPortalForm.getIsQueryCondition();
				if (employPortalForm.getUserName() == null || employPortalForm.getUserName().equals("")) {
					employPortalForm.setA0100("");
				}
				String a0100 = employPortalForm.getA0100();
				if (a0100 == null || (a0100 != null && a0100.trim().length() == 0)) {//非登录状态查询时 清空cookies信息
		%>
		<script language='javascript'>
	   			var date1 = new Date(); 
				date1.setTime(date1.getTime() - 10000); 
				document.cookie ="hjsjpos=" + "555" + ";expires=" + date1.toGMTString();

	   			</script>
		<%
			Cookie[] ck = request.getCookies();
					if (ck != null) {
						for (int k = 0; k < ck.length; k++) {
							ck[k].setMaxAge(0);
							Cookie cookie = new Cookie(ck[k].getName(), null);
							response.addCookie(cookie);
						}
					}
				}
				String dbName = employPortalForm.getDbName();
				//ArrayList conditionFieldList=employPortalForm.getConditionFieldList();
				//zxj 20141231 对conditionFieldList中的数据进行安全过滤，防止跨站脚本等攻击
				for (int i = 0; i < conditionFieldList.size() && i<=2; i++) {
					LazyDynaBean abean = (LazyDynaBean) conditionFieldList.get(i);
					if (abean == null)
						continue;

					String itemid = (String) abean.get("itemid");
					abean.set("itemid", PubFunc.hireKeyWord_filter(itemid));

					String value = PubFunc.getReplaceStr((String) abean.get("value"));
					abean.set("value", PubFunc.hireKeyWord_filter(value));

					String viewvalue = PubFunc.getReplaceStr((String) abean.get("viewvalue"));
					abean.set("viewvalue", PubFunc.hireKeyWord_filter(viewvalue));

					String type = (String) abean.get("itemtype");
					abean.set("type", PubFunc.hireKeyWord_filter(type));

					String codesetid = (String) abean.get("codesetid");
					abean.set("codesetid", PubFunc.hireKeyWord_filter(codesetid));
				}

				String hirechannel = employPortalForm.getHireChannel();
				hirechannel = PubFunc.hireKeyWord_filter(hirechannel);

				String zpUnitCode = employPortalForm.getZpUnitCode();
				zpUnitCode = PubFunc.hireKeyWord_filter(zpUnitCode);

				String introduceType = employPortalForm.getIntroduceType();
				String unitIntroduce = employPortalForm.getUnitIntroduce();
				String introducelink = employPortalForm.getIntroducelink();
				String type = request.getParameter("returnType");
				String userName = employPortalForm.getUserName() == null ? "" : employPortalForm.getUserName();
				ArrayList unitList = employPortalForm.getUnitList();
				String admissionCard = employPortalForm.getAdmissionCard();
				String canprint = employPortalForm.getCanPrint();
				boolean hiddenColumn = false;//是否要隐藏掉特殊的列推荐人数和推荐
				if ("0".equals(isHeadhunter) && "headHire".equals(hirechannel)) {
					hiddenColumn = true;
				}
				LazyDynaBean kbean = null;
				String username = employPortalForm.getUserName() == null ? "" : employPortalForm.getUserName();
				ArrayList kll = new ArrayList();
				int hi = 0;
				int lis = 0;
				if (unitList != null && unitList.size() > 0) {
					kbean = (LazyDynaBean) unitList.get(0);
					for (int k = 0; k < unitList.size(); k++) {
						kbean = (LazyDynaBean) unitList.get(0);
						kll = (ArrayList) kbean.get("list");
						lis += lis + kll.size() + 1;
					}

					if (a0100 == null || (a0100 != null && a0100.trim().length() == 0)) {

						hi = lis * 30 + 500;
						if (lis <= 7) {
							hi = 7 * 30 + 500;
						}

					} else {
						if (username.length() <= 4) {
							hi = lis * 30 + 615;;
							if (lis <= 7) {
								hi = 7 * 30 + 615;
							}
						} else {
							hi = lis * 30 + 615;;
							if (lis <= 7) {
								hi = 7 * 30 + 615;
							}
						}

					}
				} else {
					if (a0100 == null || (a0100 != null && a0100.trim().length() == 0))
						hi = 7 * 30 + 500;
					else {
						hi = 7 * 30 + 615;
					}
				}
				if (type == null)
					type = "1";
		%>
		<%
			String aurl = (String) request.getServerName();
				String port = request.getServerPort() + "";
				String prl = request.getScheme();
				String url_p = prl + "://" + aurl + ":" + port;
		%>
		<html:hidden name="employPortalForm" property="isDefinitionActive" />
		<html:hidden name="employPortalForm" property="hireChannel" />
		<html:hidden name="employPortalForm" property="zpUnitCode" />
		<a href="<%=url_p%>" style="display: none" id="hostname">for vpn</a>
		<%
			if (userName.length() == 0
						|| (a0100 == null || (a0100 != null && a0100.trim().length() == 0) || a0100.equals(""))) {
		%>
		<div id="bodyid" class="body">

			<div class="tcenter" id='tc'>
				<div class="center_bg" id='cms_pnl'>
					<div class="left">
						<div class="login">
							<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
							<div class="dl">
								<table width="197" border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td>&nbsp;</td>
									</tr>
									<tr>

										<td>&nbsp;&nbsp;
											<div class="input_bg">
												<span> <%
 	if (hirechannel.equals("headHire")) {
 %> <bean:message key="hire.zp_persondb.username" /> <%
 	} else {
 %> <bean:message key="hire.out.user.email" /> <%
 	}
 %>
												</span> <input class="s_input" id="loginName" type="text" onkeydown="KeyDown()"
													name="loginName"
													value='<bean:write name="employPortalForm" property="loginName"  />' />
											</div>
										</td>
									</tr>
									<tr>
										<td><div class="input_bg">
												<span>密&nbsp;&nbsp; 码</span><input class="s_input"
													id="password" type="password" onkeydown="KeyDown()" AUTOCOMPLETE="off"
													name="password"
													value='<bean:write name="employPortalForm" property="password" />'
													name="password" />
											</div></td>
									</tr>
									<tr>
										<td>
											<div class="input_bg">
												<span>验证码</span><input class="s_input" id="validatecode"
													type="text" onkeydown="KeyDown()"  value="" name="validatecode"  />
											</div>
										</td>
									</tr>

									<tr>
										<td align="left"
											style="padding: 2px 0 2px 0; border-bottom: dotted 1px #c6c6c6;">
											<img align="absMiddle"
											src="/servlet/vaildataCode?channel=0&codelen=4"
											id="vaildataCode"> <img align="absMiddle"
											src="/images/refresh.png" height="15" width="15" title="换一张"
											onclick="validataCodeReload()"> <img align="absMiddle"
											style="cursor: pointer;" src="/images/hire/dl.gif" title="登录"
											onclick='hireloginvalidate(0);' />&nbsp;
										</td>
									</tr>
									<tr>
										<td><span> <%
 	if ("headHire".equals(hirechannel)) {//猎头招聘
 %> <a
												href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>忘记密码</a>
												<%
													} else {
												%> <logic:equal value="0" name="employPortalForm"
													property="isDefinitinn">
													<a href='javascript:T_BUTTON();'>注册</a>
												</logic:equal> <logic:equal value="1" name="employPortalForm"
													property="isDefinitinn">
													<a href='javascript:TR_BUTTON();'>注册</a>
												</logic:equal> <logic:equal value="1" name="employPortalForm"
													property="acountBeActived">
						                    |<a href="javascript:hireloginvalidate(1);">激活</a>
												</logic:equal> |<a
												href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>忘记密码</a>
												<logic:equal value="true" name="employPortalForm"
													property="accountFlag">
											|<a href='javascript:getZpAccounts();'>忘记帐号</a>
												</logic:equal> <%
 	}
 %>
										</span></td>
									</tr>
								</table>
							</div>
						</div>
						<script language="javascript">
                    var txt1=document.getElementById("loginName");
						if(txt1.value==" ")
 							txt1.value="";
                    </script>
						<%
							} else {
						%>
						<div class="body">
							<div class="tcenter" id='tc'>
								<div class="center_bg" id='cms_pnl'>
									<div class="left">
										<div class="login">
											<div class="dl_1">
												<div class="we">
													<b><bean:message key="hire.welcome.you" />, <%
														if (userName.length() > 6) {
													%>
													</b><b> <%
 	}
 %> ${employPortalForm.userName}
													</b>
													<bean:message key="hire.welcome.you.hint" />
												</div>
												<ul class="dl_list">
													<%
														if (isHeadhunter != null && isHeadhunter.equals("1")) {//进来的用户是猎头身份
													%>
													<li><a
														href="/hire/hireNetPortal/recommend_resume.do?b_recommendResume=query"><bean:message
																key="hire.out.resume.recommend" /></a></li>
													<!-- 推荐简历 -->
													<!--  <li><a href="###"><bean:message key="hire.out.position.recommend"/></a></li><!-- 推荐岗位 -->
													<li><a
														href="/hire/hireNetPortal/search_zp_position.do?b_query=link&returnType=headHunter&hireChannel=headHire"><bean:message
																key="hire.out.position.employment" /></a></li>
													<!-- 招聘岗位 -->
													<li><a
														href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message
																key="label.banner.changepwd" /></a></li>
													<!--修改密码 -->
													<%
														} else {
													%>
													<li><a
														href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")'><bean:message
																key="hire.browse.resume" /></a></li>
													<!-- 已经通过后台处理过这个,可能看到其他人的信息的问题,通过后台得到的这个相关信息 -->
													<!-- linbz 20160506   屏蔽打印简历功能 -->
													<!--
		            	         		<logic:notEqual name="employPortalForm" property="previewTableId" value="#">
		            	         		<logic:equal name="employPortalForm" property="canPrint" value="1">
		            	      			<li><a href='javascript:ysmethod("<bean:write name="employPortalForm" property="previewTableId"/>")'>打印简历</a></li>
		            	      	 		</logic:equal>
		            	      	 		</logic:notEqual>
		            	      	 	-->
													<logic:equal value="true" name="employPortalForm"
														property="canPrintExamno">
														<li><a href='javascript:printExamNo()'><bean:message
																	key="hire.print.examcard" /></a></li>
													</logic:equal>
													<logic:equal value="1" name="employPortalForm"
														property="canQueryScore">
														<li><a href='javascript:showCard("","")'><bean:message
																	key="hire.query.score" /></a></li>
													</logic:equal>
													<li><a
														href="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1"><bean:message
																key="hire.my.resume" /></a></li>
													<!-- 我的简历 -->
													<li><a href="javascript:void(0);"
														onclick='hasresume();'
														<%if (type != null && type.length() != 0 && type.equalsIgnoreCase("resume")) {%>
														class="els" <%}%>><bean:message
																key="hire.browsed.position" /></a></li>
													<li><a
														href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message
																key="hire.apply.position" /></a></li>
													<!-- 应聘职位 -->
													<li><a
														href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message
																key="label.banner.changepwd" /></a></li>
													<!-- 修改密码 -->

													<logic:equal value="2" name="employPortalForm"
														property="isapply">
														<li><a target="_blank"
															href="http://jtm.51chinahrd.com/jtmhongjin/User_Quesmain.aspx?username=${employPortalForm.hdtusername}&cer=<bean:write name="employPortalForm" property="cer"/>&pid=<bean:write name="employPortalForm" property="a0100"/>&jobid=<bean:write name="employPortalForm" property="jobid"/>&name=<bean:write name="employPortalForm" property="userName"/>&jobtypeid=<bean:write name="employPortalForm" property="jobid"/>&jobname=<bean:write name="employPortalForm" property="jobname"/>">我的测评</a></li>
													</logic:equal>
													<logic:equal value="1" name="employPortalForm"
														property="isDefinitionActive">
														<logic:equal value="1" name="employPortalForm"
															property="activeValue">
															<li><a
																href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>关闭简历</a></li>
														</logic:equal>
														<logic:equal value="2" name="employPortalForm"
															property="activeValue">
															<li><a
																href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>激活简历</a></li>
														</logic:equal>
													</logic:equal>
													<%
														}
													%>
													<li><a href="javascript:exit()">退出登录</a></li>
												</ul>
											</div>
										</div>
										<%
											}
										%>
										<div class="muen">
											<h2>&nbsp;&nbsp;&nbsp;&nbsp;招聘单位</h2>
											<!-- 这里表现一致不用修改 -->
											<logic:iterate id="unit" name="employPortalForm"
												property="unitList" indexId="index">
												<logic:equal value="<%=zpUnitCode%>" name="unit"
													property="codeitemid">
													<div class="firstDiv">
														<table>
															<tr>
																<td align="left" valign="middle"><img
																	src="/images/tree_collapse.gif" border="0"
																	id="<bean:write name="unit" property="id_img"/>"
																	style="cursor: hand"
																	onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");' /><a
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font
																		class="firstFont"><bean:write name="unit"
																				property="codeitemdesc" /></font> </a></td>
															</tr>
														</table>
													</div>
													<ul class="col">
														<logic:iterate id="UnitSub" name="unit" property="list"
															indexId="indexid">
															<logic:equal value="<%=zpUnitCode%>" name="UnitSub"
																property="codeitemid">
																<li id="<bean:write name="UnitSub" property="id_r"/>"><a
																	class="one"
																	title='<bean:write name="UnitSub" property="altdesc"/>'
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write
																			name="UnitSub" property="codeitemdesc" /></a></li>
															</logic:equal>
															<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub"
																property="codeitemid">
																<li id="<bean:write name="UnitSub" property="id_r"/>"><a
																	title='<bean:write name="UnitSub" property="altdesc"/>'
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write
																			name="UnitSub" property="codeitemdesc" /></a></li>
															</logic:notEqual>
														</logic:iterate>
													</ul>
												</logic:equal>
												<logic:notEqual value="<%=zpUnitCode%>" name="unit"
													property="codeitemid">
													<div class="firstDiv">
														<table>
															<tr>
																<td align="left" valign="middle"><img
																	src="/images/tree_collapse.gif" border="0"
																	id="<bean:write name="unit" property="id_img"/>"
																	style="cursor: hand"
																	onclick='changeDisplay("<bean:write name="unit" property="codeitemid"/>","<bean:write name="unit" property="count"/>");' /><a
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="unit" property="codeitemid"/>"><font
																		class="firstFont"><bean:write name="unit"
																				property="codeitemdesc" /></font> </a></td>
															</tr>
														</table>
													</div>
													<ul class="col">
														<logic:iterate id="UnitSub" name="unit" property="list"
															indexId="indexid">
															<logic:equal value="<%=zpUnitCode%>" name="UnitSub"
																property="codeitemid">
																<li id="<bean:write name="UnitSub" property="id_r"/>"><a
																	class="one"
																	title='<bean:write name="UnitSub" property="altdesc"/>'
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write
																			name="UnitSub" property="codeitemdesc" /></a></li>
															</logic:equal>
															<logic:notEqual value="<%=zpUnitCode%>" name="UnitSub"
																property="codeitemid">
																<li id="<bean:write name="UnitSub" property="id_r"/>"><a
																	title='<bean:write name="UnitSub" property="altdesc"/>'
																	href="/hire/hireNetPortal/search_zp_position.do?b_query=link&isAllPos=1&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=<bean:write name="UnitSub" property="codeitemid"/>"><bean:write
																			name="UnitSub" property="codeitemdesc" /></a></li>
															</logic:notEqual>
														</logic:iterate>
													</ul>
												</logic:notEqual>

											</logic:iterate>

										</div>
										<!-- promptContent左侧招聘单位下的说明信息 -->
										<div class="promt" id="board">
											${employPortalForm.promptContent}</div>
									</div>
									<!-- 右侧“社会/校园/猎头  招聘” -->
									<%
										if (hirechannel.equals("headHire")) {//暂时显示成这样,等做成图片后换 上来 
												out.println(" <div class=\"right3\"  id='rg'  >");
											} else {
									%>
									<div class="right4" id='rg'>
										<h2><%=channelName%></h2>
										<%
											}
										%>
										<!-- 猎头招聘未登录时不显示职位搜索 -->
										<%
											if ("headHire".equals(hirechannel) && (userName.length() == 0
														|| (a0100 == null || (a0100 != null && a0100.trim().length() == 0) || a0100.equals("")))) {//如果是猎头招聘 ,并且没有登录
												} else {
										%>
										<%
											if (conditionFieldList != null && conditionFieldList.size() > 0) {
										%>
										<div class="search">
											<h3>职位搜索</h3>
											<div class="xia">
												<%
													//人民大学职称要求为多选 z03a2职称要求，z0390职称要求隐藏代码
																String title_Requirements = SystemConfig.getPropertyValue("title_Requirements");
																String z03a2 = "";
																String z0390 = "";
																if (StringUtils.isNotEmpty(title_Requirements) && title_Requirements.split(":").length == 2) {
																	z03a2 = title_Requirements.split(":")[0];
																	z0390 = title_Requirements.split(":")[1];
																}
																String codevalue = "";//为了解决层级型代码选项出现后导致排列错格的问题 hidden也会作为一个页面元素占一个位置
																String selecnum = "";
																boolean isprint = false;
																for (int i = 0; i < conditionFieldList.size() && i<=2; i++) {
																	out.print("<span style='margin-right:2px;'>");
																	LazyDynaBean abean = (LazyDynaBean) conditionFieldList.get(i);
																	String itemid = (String) abean.get("itemid");
																	String itemtype = (String) abean.get("itemtype");
																	String codesetid = (String) abean.get("codesetid");
																	if ("Z0385".equalsIgnoreCase(itemid))
																		codesetid = "35";
																	else if (z03a2.equalsIgnoreCase(itemid))
																		codesetid = "DL";
																	String isMore = (String) abean.get("isMore");
																	String itemdesc = (String) abean.get("itemdesc");
																	String value = (String) abean.get("value");
																	String viewvalue = (String) abean.get("viewvalue");
																	String viewvalue_view = StringUtils.isEmpty((String) abean.get("viewvalue_view"))
																			? ""
																			: (String) abean.get("viewvalue_view");
																	out.print("" + itemdesc + "</span>");
																	if (itemtype.equals("A")) {
																		if (codesetid.equals("0")) {//是非代码类
																			if (itemid.equalsIgnoreCase(hireMajor)
																					&& !(hireMajorCode == null || hireMajorCode.equals("-1"))) {//是招聘专业指标
																				out.print("<div class=\"input_bg2\" style='width:95px;padding:0 0 0 0;'>");
																				out.print("<input  class='TEXT' type='text' name='conditionFieldList[" + i
																						+ "].viewvalue_view' value='" + viewvalue_view
																						+ "'  size='10'  style='width:90px;height:20px;line-height:20px;padding-left:5px;'/>");
																				out.print("</div>");
																				out.print("<span  style='width:0px;margin-left:-2px;'>"
																						+ "<input type='hidden' name='conditionFieldList[" + i
																						+ "].viewvalue_value'/>"
																						+ "<img style='float:right;margin-top:-1px;' class='img-middle'  src='/module/recruitment/image/xiala2.png' plugin='codeselector' codesetid='"
																						+ hireMajorCode + "' ctrltype='0' inputname='conditionFieldList[" + i
																						+ "].viewvalue_view'  afterfunc='dealRes(\"" + i
																						+ "\",\"conditionFieldList\")'/>" + "</span>");
																				isprint = true;
																				selecnum = selecnum + i + "@" + value + "`";
																			} else
																				out.println(
																						"<div class=\"search_input_bg\" style='width:110px;'><input name=\"conditionFieldList["
																								+ i + "].value\"  class='textbox' type=\"text\" value=\""
																								+ value + "\" size='18' /></div>");
																		} else {
																			if ("0".equals(isMore) && false) {
																				ArrayList options = (ArrayList) abean.get("options");
																				out.print("<div class='input' id='input" + i + "'");
																				out.print(" style='width:122px'>");
																				out.print(
																						"<img  src='../../images/hire/input_l.gif' width='100%' height='100%' style='position:absolute;z-index:-999'/>");
																				out.print(" <div class='floor' style='outline:none' tabindex=\"0\" id='floor"
																						+ i + "' onblur=\" hide3('" + i + "');\"> ");
																				String selected = "";
																				String selectedvalue = "";
																				out.println("<a  onclick=\"javascript:change(" + i + ",'" + "全部" + "','" + ""
																						+ "','conditionFieldList[" + i
																						+ "].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
																				out.print("  全部" + "</a><br>");

																				for (int n = 0; n < options.size(); n++) {
																					LazyDynaBean a_bean = (LazyDynaBean) options.get(n);
																					String avalue = (String) a_bean.get("value");
																					String aname = (String) a_bean.get("name");
																					out.println("<a  onclick=\"javascript:change(" + i + ",'" + aname + "','"
																							+ avalue + "','conditionFieldList[" + i
																							+ "].value');\" style='FONT-SIZE: 12px;font-family: 微软黑体;color:black;cursor:hand'>");
																					if (avalue.equals(value)) {
																						selected = aname;
																						selectedvalue = avalue;
																						if (selected.length() >= 7) {
																							selected = selected.substring(0, 7);
																						}
																					}
																					out.print(" " + aname + "</a>");
																					if (n != options.size() - 1) {
																						out.print("<br>");
																					}
																				}
																				if (selected.trim().length() == 0) {
																					selected = "全部";
																				}
																				out.print("<input type='hidden' name='conditionFieldList[" + i
																						+ "].value' value='" + selectedvalue + "'/>");
																				out.print("</div><span id='spank" + i + "' class='img'>");
																				out.print("<a href='javascript:void(0);' onclick='showlist(" + i
																						+ ");'><img src='/images/hire/xia.gif'/></a>");
																				out.print(
																						" </span><span style='overflow:overflow;padding-right:0px;margin-right:-2px;margin-left:1.9px' FONT-SIZE: 12px;font-family: 微软黑体;color:black; id='span"
																								+ i + "'>");
																				out.print(selected + " </span></div>");
																			} else {
																				out.print("<div class=\"input_bg2\" style='width:95px;padding:0 0 0 0;'>");
																				out.print(
																						"<input class='TEXT' type='text' onkeydown='clean_value(event,this)' autocomplete='off' name='conditionFieldList["
																								+ i + "].viewvalue_view' value='" + viewvalue_view
																								+ "'  size='10'  style='width:90px;height:20px;line-height:20px;padding-left:5px;'/>");
																				out.print("</div>");
																				out.print("<span  style='width:0px;margin-left:-2px;'>");
																				out.print(
																						"<img style='float:right;margin-top:-1px;' class='img-middle' src='/module/recruitment/image/xiala2.png' plugin='codeselector' isHideTip = 'true' codesetid='"
																								+ codesetid + "' inputname='conditionFieldList[" + i
																								+ "].viewvalue_view' ");
																				if ("un".equalsIgnoreCase(codesetid) || "um".equalsIgnoreCase(codesetid)
																						|| "@K".equalsIgnoreCase(codesetid))
																					out.print(" ctrltype='0' ");
																				//人大要求快速查询只显示有职位的单位
																				if ("un".equalsIgnoreCase(codesetid))
																					out.print(" codesource='GetZPOrganization' ");
																				if ("z0385".equalsIgnoreCase(itemid) || z03a2.equalsIgnoreCase(itemid))
																					out.print(" multiple='true' ");
																				out.print(" afterfunc='dealRes(\"" + i + "\",\"conditionFieldList\")'/>");
																				out.print("<input type='hidden' name='conditionFieldList[" + i
																						+ "].viewvalue_value'/>");
																				out.print("</span>");
																				isprint = true;
																				selecnum = selecnum + i + "@" + value + "`";
																			}
																		}
																	} else if (itemtype.equals("D")) {
																		out.println(
																				"<div class=\"input_bg1\" style='width:110px;'><input  name='conditionFieldList["
																						+ i
																						+ "].value' class='TEXT' type='text' style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;'   size='15' value='"
																						+ value
																						+ "'  onclick='popUpCalendar(this,this, dateFormat,\"\",\"\",true,false)'/></div>");

																	} else if (itemtype.equals("N")) {
																		out.println(
																				"<div class=\"input_bg1\" style='width:110px;'><input  name=\"conditionFieldList["
																						+ i
																						+ "].value\" style='width:100px;FONT-SIZE: 12px;font-family: 微软黑体;color:black;  class='TEXT' type=\"text\"  size='15'/></div>");
																	}
																}
												%>
												<a href="javascript:query(1);" style="margin-left: 10px"
													id="img2"><img src="/images/hire/sarch.gif" /></a>
												<!-- 搜索图标 -->
												<%
													if (isprint) {
																	if (null != selecnum && selecnum.indexOf("`") != -1 && selecnum.indexOf("@") != -1) {
																		String[] temp = selecnum.split("`");
																		for (int t = 0; t < temp.length; t++) {
																			String tt = temp[t];
																			if (tt.trim().length() == 0)
																				continue;
																			String[] gg = tt.split("@");
																			if (gg.length == 1) {
																				out.println("<input type='text' style='display:none;' name='conditionFieldList["
																						+ gg[0] + "].value' value=''  />&nbsp;");
																			} else {
																				out.println("<input type='text' style='display:none;' name='conditionFieldList["
																						+ gg[0] + "].value' value='" + gg[1] + "'  />&nbsp;");
																			}

																		}
																	}

																}
												%>
											</div>
										</div>
										<%
											}
												}
										%>
										<!-- 如果是岗位搜索或者已浏览简历不显示公告和单位介绍       等待添加招聘岗位中不显示公告和单位介绍 -->
										<%
											if (type != null && type.length() != 0 && (type.equalsIgnoreCase("resume")
														|| type.equalsIgnoreCase("search") || type.equalsIgnoreCase("headHunter"))) {
												} else {
										%>
										<logic:equal value="1" name="employPortalForm"
											property="isAllPos">
											<logic:equal value="1" property='hasMessage'
												name="employPortalForm">
												<div class="jj">
													<h2>
														<span>最新公告</span>
													</h2>
													<div >
													<div class='board_inner' style='line-height:20px;' id="ficont">
														<ul>
															<% 
															ArrayList boardlist=employPortalForm.getPageBoardList();
															int pageNum = employPortalForm.getPageNum();
															int pageCount = employPortalForm.getPageCount();
															if(boardlist!=null){
																for(int i=0;i<boardlist.size();i++){
																	LazyDynaBean bean=(LazyDynaBean)boardlist.get(i);
																	String id=(String)bean.get("id");
																	String down=(String)bean.get("down");
																	String hasfile=bean.get("hasfile")==null?null:(String)bean.get("hasfile");
																	String title=(String)bean.get("title");
																	String content=(String)bean.get("content");
																	String ext=(String)bean.get("ext");
																	String href=(String)bean.get("href");
																	%>
																	<li class="els">
																	<%
																	if(content!=null){
																	%>
																		<a href="./showBoardPage.jsp?br_showBoardPage=<%=id %>" target="_blank"  style="cursor:pointer"><%=title %></a>
																	<%}else{ %>
																		<%=title %>
																	<%}
																	
																	if(hasfile!=null&&hasfile.trim().length()!=0&&hasfile.trim().equalsIgnoreCase("true")){ %>
																		<a href='<%=href%>' style='margin-left:10px;margin-bottom:4px'><img src="/images/board_attach.gif" alt="附件下载" /></a>
																	<%}%>
																	</li>
																<%}
															}%>
																	 
														</ul>
													</div>
													<div class="pages">
														<ul class="ul_pages">
															<li id="previous"><a>上一页</a></li>
															<li id="next"><a>下一页</a></li>
															<li>当前第<span class="color" id="pageId"><%=pageNum %></span><span>页</span></li>
															<li>共<span class="color"><%=pageCount %></span><span>页</span></li>
														</ul>
													</div>
													</div>
												</div>
												<%
													if (boardlist != null) {
														for (int i = 0; i < boardlist.size(); i++) {
															LazyDynaBean bean = (LazyDynaBean) boardlist.get(i);
															String title = (String) bean.get("title");
															String id = (String) bean.get("id");
															String down = (String) bean.get("down");
															String content = (String) bean.get("content");
															if (content == null) {
																content = "";
															}
												%>
												<div class="jj"
													style="margin-top: 0px; display: none; padding-right: 0px"
													id='<%=id%>dmliner'>
													<h2>
														<span>最新公告</span><img src='/images/hire/18.gif'
															style="cursor: pointer" onclick="hideandshow('<%=id%>')">
													</h2>
													<div class="cont"
														style="display: block; overflow: auto; float: left; text-align: left; background: #fff; height: auto; width: 663px; padding-left: 15px; margin-left: 0px">
														<%=content%>
													</div>
												</div>
												<%
													}
												}
												%>
											</logic:equal>
											<%
												if (unitIntroduce != null && unitIntroduce.trim().length() > 0) {
											%>
											<div class="jj">
												<h2>
													<span>单位介绍</span>
												</h2>
												<div class="nb" style="">
													<%
														if (request.getParameter("allintro") != null
																				&& request.getParameter("allintro").equals("1")) {
													%>
													<div class='units_introduce_more'>
														${employPortalForm.unitIntroduce}</div>
													<%
														} else {
													%>
													<div class='units_introduce'>
														${employPortalForm.unitIntroduce}</div>
													<%
														}
													%>
												</div>
												<div class="nr" style="">
													<%
														if (introduceType != null && !introduceType.equals("-1")
																				&& unitIntroduce.trim().length() > 0) {
													%>
													<%
														if (introduceType.equals("0")) {
													%>
													<span><a
														href='javascript:openwindow("<%=introducelink%>")'><img
															src="/images/hire/more.gif" /></a></span>
													<%
														} else if (introduceType.equals("1")) {
													%>
													<%
														if (request.getParameter("allintro") != null
																						&& request.getParameter("allintro").equals("1")) {
													%>
													<span><a
														href='/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=${employPortalForm.zpUnitCode}&allintro=0'><img
															src="/images/hire/collect.gif" /></a></span>
													<%
														} else {
													%>
													<span><a
														href='/hire/hireNetPortal/search_zp_position.do?b_query=link&operate=init&hireChannel=${employPortalForm.hireChannel}&zpUnitCode=${employPortalForm.zpUnitCode}&allintro=1'><img
															src="/images/hire/more.gif" /></a></span>
													<%
														}
																			}
													%>
													<%
														}
													%>
												</div>
											</div>

											<%
												}
											%>
										</logic:equal>
										<%
											}
												if ("headHire".equals(hirechannel) && (userName.length() == 0
														|| (a0100 == null || (a0100 != null && a0100.trim().length() == 0) || a0100.equals("")))) {
													//猎头招聘未登录不显示招聘职位列表
												} else if ("headHire".equals(hirechannel) && (isHeadhunter == null || isHeadhunter.equals("0"))) {
													//非猎头登录查看猎头招聘不显示招聘职位列表 	        	
												} else {
													if ((type != null && type.length() != 0
															&& (type.equalsIgnoreCase("resume") || type.equalsIgnoreCase("search")))
															|| (unitIntroduce == null || unitIntroduce.trim().length() == 0)) {
										%>
										<div class="jj zw">
											<%
												} else {
											%>
											<div class="jj zw" style="padding-top: 0px">
												<%
													}
												%>
												<h3>
													<span> <%
 	if (type != null && type.length() != 0 && type.equalsIgnoreCase("resume")) {//这里暂时还没有kao考虑猎头招聘的推荐岗位的界面显示列表的情况
 %>已浏览<%
 	} else {
 %>招聘<%
 	}
 %>职位
													</span>
												</h3>
												<div class="nr">
													<%
														if (type != null && type.length() != 0 && type.equalsIgnoreCase("search")) { //岗位搜索 

																	String positionNumber = employPortalForm.getPositionNumber();

																	String hireChannel = employPortalForm.getHireChannel();
																	ArrayList posFieldList = employPortalForm.getPosFieldList();
																	String selunit = employPortalForm.getSelunit();
																	int pcount = Integer.parseInt(positionNumber);
																	if (request.getParameter("unitCode") == null) {
																		ArrayList sunitList = employPortalForm.getSunitlist();
																		int n = 0;
																		for (Iterator t = sunitList.iterator(); t.hasNext();) {
																			LazyDynaBean aBean = (LazyDynaBean) t.next();
																			String unitName = (String) aBean.get("name");
																			String id = (String) aBean.get("id");
																			ArrayList posList = (ArrayList) aBean.get("list");
																			String content = (String) aBean.get("content");
																			String contentType = (String) aBean.get("contentType");
													%>
													<!-- 每一个单位创建一个table,每一个岗位创建一个tr -->
													<table cellSpacing=0 cellPadding=1 width="100%"
														align="center" style='margin-top: 10px' border=0>
														<tbody>
															<tr align="left">
																<!-- 输出每个单位的名字 -->
																<td colspan="5" class="hj_zhaopin_h1"><%=unitName%>
																	<%
																		if (n != 0)
																			out.print("<br>");
																		n++;
																	%></td>
															</tr>
															<!-- 然后挨着输出该单位的需求职位 -->
															<tr>
																<td width="100%">
																	<table id="<%=id%>" width="100%" border="0"
																		cellspacing="0" cellpadding="0" class="table">
																		<tr align="center">
																			<%
																				if (posFieldList != null && posFieldList.size() > 0) {//所有前台要显示的字段 (输出表头)
																					for (int y = 0; y < posFieldList.size(); y++) {
																						LazyDynaBean abean = (LazyDynaBean) posFieldList.get(y);
																						String itemid = (String) abean.get("itemid");
																						String itemdesc = (String) abean.get("itemdesc");
																						if (hiddenColumn && (itemid.equals("tjjl") || itemid.equals("tjrsl"))) {
																							continue;//非猎头人员查看猎头招聘的信息将推荐人数和推荐简历隐藏
																						}
																						if (itemid.equalsIgnoreCase("Z0351")) {
																							itemdesc = ResourceFactory.getProperty("hire.out.employ.position");
																			%>
																			<td width="20%" class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				} else {
																			%>
																			<td class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				}
																					}
																			%>

																			<%
																				} else {
																			%>
																			<td width="40%" class='table_line_title'><B>
																					<!-- 如果没配置外网显示指标,显示默认的指标 --> <bean:message
																						key="hire.out.employ.position" />
																			</b></td>
																			<td width="15%" class='table_line_title'><b><bean:message
																						key="lable.zp_plan_detail.domain" /></b></td>
																			<!-- 工作地点 -->
																			<td width="15%" class='table_line_title'><b><bean:message
																						key="label.zp_release_pos.valid_date" /></b></td>
																			<!-- 发布日期 -->
																			<%
																				if (!hiddenColumn) {//非猎头人员查看猎头招聘的信息将推荐人数和推荐简历隐藏
																					if (hirechannel.equals("headHire")) {
																			%>
																			<!--推荐人数 -->
																			<td width="15%" class='table_line_title'><b>
																					<bean:message
																						key="hire.out.headhunter.recommend.count" />
																			</b></td>
																			<%
																				} else if (!SystemConfig.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td width="15%" class='table_line_title'><b>
																					<bean:message key="hire.applay.personcount" />
																			</b></td>
																			<!--应聘人数 -->
																			<%
																				} else {
																			%>
																			<td width="10%" class='table_line_title'><b>需求人数</b></td>
																			<%
																				}

																				if (hirechannel.equals("headHire")) {
																			%>
																			<td width="10%" class='table_line_title'><b>
																					<bean:message key="hire.out.headhunter.recommend" />
																			</b></td>
																			<!-- 推荐 -->
																			<%
																				} else {
																			%>
																			<td width="10%" class='table_line_title'><b>
																					<bean:message key="hire.column.applay" />
																			</b></td>
																			<!-- 应聘 -->
																			<%
																				}
																										}
																									}
																			%>
																		</tr>
																		<%
																			int colspan = 5;
																			if (posFieldList != null && posFieldList.size() > 0) {
																				colspan = posFieldList.size();;
																				for (int i = 0; i < posList.size(); i++)//每一个招聘岗位的相关信息 
																				{
																					if ((i > (pcount - 1) && request.getParameter("isAllPosOk") == null)
																							|| (i > (pcount - 1) && request.getParameter("isAllPosOk") != null)) {
																						if (selunit != null && selunit.indexOf("," + id + ",") != -1) {//如果超过了显示的个数，但是这个是要查询的招聘职位信息,那么也要显示出来 

																						} else
																							break;
																					}
																					out.println("<tr>");
																					LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																					String isApplyedPos = (String) bean.get("isApplyedPos");

																					String state = (String) bean.get("state");
																					String isNewPos = (String) bean.get("isNewPos");
																					String z0301 = (String) bean.get("z0301");
																					String z0311 = (String) bean.get("z0311");//想办法处理掉z0311
																					String p = "";
																					if (hireChannel.equals("01") && !hireMajor.equals("-1"))//如果是校园招聘,获取校园招聘专业指标
																					{
																						//posName=(String)bean.get(hireMajor.toLowerCase());
																						p = bean.get("major") == null ? "" : "&major=" + (String) bean.get("major");
																					}
																					String posName = (String) bean.get("Z0351");
																					for (int y = 0; y < posFieldList.size(); y++) {
																						LazyDynaBean abean = (LazyDynaBean) posFieldList.get(y);
																						String itemid = ((String) abean.get("itemid")).toLowerCase();
																						String itemtype = (String) abean.get("itemtype");
																						String value = (String) bean.get(itemid);
																						if (value == null || value.equals(""))
																							value = "&nbsp;";
																						if (hiddenColumn && (itemid.equals("tjjl") || itemid.equals("tjrsl"))) {
																							continue;
																						}
																						String z0321Name = (String) bean.get("z0321Name");
																						if (itemid.equalsIgnoreCase("z0321")) {
																							value = z0321Name;
																						}
																						if (itemid.equalsIgnoreCase("Z0351") || itemid.equalsIgnoreCase("ypljl")
																								|| itemid.equalsIgnoreCase(hireMajor)) {
																							//已登录且已经申请该职位
																							if (!StringUtils.isEmpty(userViewName) && "true".equals(isApplyedPos)
																									&& itemid.equalsIgnoreCase("ypljl"))
																								value = "已申请";
																							else
																								value = "<a onclick=\"rediract('" + p + "','" + z0311 + "','"
																										+ z0301 + "','" + SafeCode.encode(posName) + "','"
																										+ unitName + "','" + type + "','" + id
																										+ "')\"> " + value + "</a>";
																						}
																						if (itemid.equalsIgnoreCase("tjjl")) {
																							value = "<a onclick=\"recommendForposition('" + z0301 + "','"
																									+ SafeCode.encode(posName)
																									+ "')\"> " + value + "</a>";
																						}
																						String align = "align=\"center\"";
																		%>
																		<%
																			if (i % 2 == 0) {
																		%>
																		<td height="36" <%=align%> class='hire_posTable_href'><%=value%></td>
																		<%
																			} else {
																		%>
																		<td height="36" <%=align%> class='table_line_single hire_posTable_href'><%=value%></td>
																		<%
																			}
																				}
																				out.println("</tr>");
																			}
																		} else {//如果没有指定招聘外网显示的字段，就显示默认的
																			for (int i = 0; i < posList.size(); i++) {
																				/**&&request.getParameter("isAllPosOk")==null 分段显示全部信息**/
																				if (i > (pcount - 1)) {

																					if (selunit != null && selunit.indexOf("," + id + ",") != -1) {

																					} else
																						break;
																				}
																				LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																				String isApplyedPos = (String) bean.get("isApplyedPos");

																				String posName = (String) bean.get("posName");//职位名称 
																				String z0333 = (String) bean.get("z0333");//工作地点 
																				String Z0331 = (String) bean.get("z0331");//结束日期
																				String Z0329 = (String) bean.get("opentime");//发布日期    原为有效开始日期 z0329
																				String z0301 = (String) bean.get("z0301");//序号
																				String z0311 = (String) bean.get("z0311");//需求岗位
																				String state = (String) bean.get("state");
																				String isNewPos = (String) bean.get("isNewPos");
																				String count = (String) bean.get("count");
																				String z0313 = (String) bean.get("z0313");
																				if (z0333 == null || z0333.trim().equals(""))
																					z0333 = "&nbsp;";
																				if (Z0329 == null || Z0329.trim().equals(""))
																					Z0329 = "&nbsp;";
																				if (count == null || count.trim().equals(""))
																					count = "&nbsp;";
																				if (z0313 == null || z0313.trim().equals(""))
																					z0313 = "&nbsp;";
																				String p = "";
																		%>
																		<tr>
																			<%
																				if (i % 2 == 0) {
																			%><!--不同的行显示不同的背景色  -->
																			<td align="center" height='36' class='hire_posTable_href'>&nbsp;&nbsp;
																			<a onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"> 
																			<%=posName%>
																			</a>
																			</td>

																			<td height="20" align="center" height='36'><%=z0333%></td>

																			<td align="center" height='36'><%=Z0329%></td>
																			<%
																				if (!hiddenColumn) {//如果是普通用户查看猎头招聘则要隐藏这两列的信息
																					if (hirechannel.equals("headHire") || !SystemConfig
																							.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td style="TEXT-AlIGN: center" height='36'><%=count%>&nbsp;</td>
																			<%
																				} else {
																			%>
																			<td style="TEXT-AlIGN: center" height='36'><%=z0313%>&nbsp;</td>
																			<%
																				}
																			%>

																			<td style="text-align: center" height='36' class='hire_posTable_href'>
																				<%
																					if (hirechannel.equals("headHire")) {
																				%> <a onclick="recommendForposition('<%=z0301%>','<%=SafeCode.encode(posName)%>');">
																				 <bean:message key="hire.out.headhunter.recommend" /> 
																				 <%
																				 	} else {
																				 %> <a
																					onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																					> 
																					<bean:message key="hire.column.applay" /> <%
																					 	}
																					 %>
																				</a>
																			</td>
																			<%
																				}
																			} else {
																			%>
																			<td align="center" class='table_line_single hire_posTable_href'	height='36'>&nbsp;&nbsp; 
																			<a
																			onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																			> <%=posName%>
																			</a>
																			</td>
																			<td height="20" align="center" class='table_line_single'><%=z0333%></td>
																			<td align="center" class='table_line_single'><%=Z0329%></td>
																			<%
																				if (!hiddenColumn) {//普通应聘者查看猎头招聘的信息则隐藏推荐人数和推荐人两列 
																					if (hirechannel.equals("headHire") || !SystemConfig
																							.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td style="TEXT-AlIGN: center"
																				class='table_line_single'><%=count%>&nbsp;</td>
																			<%
																				} else {
																			%>
																			<td style="TEXT-AlIGN: center"
																				class='table_line_single'><%=z0313%>&nbsp;</td>
																			<%
																				}
																			%>
																			<td style="text-align: center"
																				class='table_line_single hire_posTable_href'>
																				<%
																					if (hirechannel.equals("headHire")) {
																				%> 
																				<a
																				onclick="recommendForposition('<%=z0301%>','<%=SafeCode.encode(posName)%>');"
																				> 
																				<bean:message key="hire.out.headhunter.recommend" /> <%
																				 	} else {
																				 %> <a
																					onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																					> 
																					<bean:message key="hire.column.applay" /> 
																					<%
																					 	}
																					 %>
																				</a>
																			</td>
																			<%
																				}
																											}
																			%>
																		</tr>

																		<%
																			}
																								}
																		%>
																	</table>
																</td>
															</tr>
															<%
																if ((posList.size() > (pcount) && request.getParameter("isAllPosOk") == null)
																		|| (posList.size() > (pcount) && request.getParameter("isAllPosOk") != null)) {
																	if (selunit != null && selunit.indexOf("," + id + ",") != -1) {

																	} else {
															%>

															<tr id="more<%=id%>">
																<td height="20" style="TEXT-ALIGN: right" width="715px">
																	<a onclick="query(2,'<%=id%>');return false;" href="javascript:void(0);">
																		<img src="/images/hire/more.gif" border="0" />
																	</a>
																</td>
															</tr>
															<%
																}
																					}
															%>

														</tbody>
													</table>

													<%
														}
																	}
													%>
													<%
														} else if (type != null && type.length() != 0 && type.equalsIgnoreCase("resume")) {
													%><!-- 已经浏览的岗位 这个界面只有社会招聘和校园招聘才会展现出来,猎头招聘不会看到-->

													<table width='100%' border="0" cellpadding="0"	cellspacing="0">

														<logic:equal value="1" name="employPortalForm"
															property="isHasNewDate">
															<!-- 如果有最新岗位 -->
															<tr height="30px">
																<td align='left' width='100%' class="hire_newPos_class3">
																	&nbsp;&nbsp;全部 <input type="radio" name="isAllPos"
																	value="1" onclick='more_q("<%=zpUnitCode%>","1");'
																	<logic:equal value="1" name="employPortalForm" property="isAllPos">checked</logic:equal> />
																	&nbsp;&nbsp; 最新职位 <input type="radio" name="isAllPos"
																	value="0" onclick='more_q("<%=zpUnitCode%>","0");'
																	<logic:equal value="0" name="employPortalForm" property="isAllPos">checked</logic:equal> />
																</td>
															</tr>
														</logic:equal>

														<tr>
															<td width='100%'>
																<%
																	String positionNumber = employPortalForm.getPositionNumber();//外网每个单位下显示职位条数

																	String hireChannel = employPortalForm.getHireChannel();
																	ArrayList posFieldList = employPortalForm.getPosFieldList();
																	ArrayList posList = employPortalForm.getZpPosList();
																	int pcount = Integer.parseInt(positionNumber);
																	String isAllPosOk = request.getParameter("isAllPosOk");
																	LazyDynaBean abean = (posList == null || posList.size() == 0)
																			? null : (LazyDynaBean) posList.get(0);
																	String unitName = "unitName";
																%>

																<table width="0" border="0" cellspacing="0"
																	cellpadding="0" class="table">
																	<tr>
																		<!-- 表头行 -->
																		<%
																			int colspan = 5;
																			if (posFieldList != null && posFieldList.size() > 0) {//如果指定了外网显示的字段 
																				colspan = posFieldList.size();
																				int width = 10;
																				if (posFieldList.size() < 5)
																					width = 15;
																				for (int y = 0; y < posFieldList.size(); y++) {
																					LazyDynaBean bbean = (LazyDynaBean) posFieldList.get(y);
																					String itemid = (String) bbean.get("itemid");
																					String itemdesc = (String) bbean.get("itemdesc");
																					String styleCl = "hj_zhaopin_list_tab_titleone";
																					if (y == 0)
																						styleCl = "hj_zhaopin_list_tab_titleone_1";
																					if (itemid.equalsIgnoreCase("Z0351")) {
																						itemdesc = ResourceFactory.getProperty("hire.out.employ.position");
																		%>
																		<td width="20%" class='table_line_title'><b><%=itemdesc%></b></td>
																		<%
																			} else {
																		%>
																		<td class='table_line_title'><b><%=itemdesc%></b></td>
																		<%
																			}
																							}
																		%>
																		<%
																			} else {//没有的话显示默认的
																		%>
																		<td height=25px class="hire_posTable_head" width="40%">
																			<b> <bean:message key="hire.out.employ.position" />
																		</b>
																		</td>

																		<td height="25" class="hire_posTable_head" width="15%"><b>
																				<bean:message key="lable.zp_plan_detail.domain" />
																		</b></td>

																		<td height="25" class="hire_posTable_head" width="15%"><b>
																				<bean:message key="label.zp_release_pos.valid_date" />
																		</b></td>

																		<%
																			if (hirechannel.equals("headHire")) {
																		%>
																		<td height="25" class="hire_posTable_head" width="15%"><b>
																				<bean:message
																					key="hire.out.headhunter.recommend.count" />
																		</b></td>
																		<%
																			} else if (!SystemConfig.getPropertyValue("zp_visibletype").equals("1")) {
																		%>
																		<td height="25" class="hire_posTable_head" width="15%"><b>
																				<bean:message key="hire.applay.personcount" />
																		</b></td>
																		<%
																			} else {
																		%>
																		<td height="25" class="hire_posTable_head" width="10%"><b>需求人数</b></td>
																		<%
																			}
																		%>

																		<%
																			if (hirechannel.equals("headHire")) {
																		%>
																		<td height="25" class="hire_posTable_head" width="10%"><b><bean:message
																					key="hire.out.headhunter.recommend" /></b></td>
																		<%
																			} else {
																		%>
																		<td height="25" class="hire_posTable_head" width="10%"><b><bean:message
																					key="hire.column.applay" /></b></td>
																		<%
																			}
																		%>
																		<%
																			}
																		%>
																	</tr>
																	<!-- 开始输出数据行 -->
																	<%
																		if (posList == null)//如果没有数据行,将数据行的数据List置为new ArrayList
																			posList = new ArrayList();
																		if (posFieldList != null && posFieldList.size() > 0) {
																			for (int i = 0; i < posList.size(); i++) {

																				out.println("<tr>");
																				LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																				//是否已申请职位
																				String isApplyedPos = (String) bean.get("isApplyedPos");

																				String state = (String) bean.get("state");
																				String z0301 = (String) bean.get("z0301");
																				String z0311 = (String) bean.get("z0311");//想办法处理掉z0311
																				String p = "";
																				if (hireChannel.equals("01") && !hireMajor.equals("-1")) {
																					p = bean.get("major") == null ? "" : "&major=" + (String) bean.get("major");
																				}
																				if ((i > (pcount - 1) && request.getParameter("isShowAall") == null)) {
																					break;
																				}
																				String posName = (String) bean.get("Z0351");
																				String isNewPos = (String) bean.get("isNewPos");
																				for (int y = 0; y < posFieldList.size(); y++) {
																					LazyDynaBean bbean = (LazyDynaBean) posFieldList.get(y);
																					String itemid = ((String) bbean.get("itemid")).toLowerCase();
																					String itemtype = (String) bbean.get("itemtype");

																					String value = (String) bean.get(itemid);
																					String z0321Name = (String) bean.get("z0321Name");
																					if (itemid.equalsIgnoreCase("z0321")) {
																						value = z0321Name;
																					}
																					if (value == null || value.equals(""))
																						value = "&nbsp;";
																					if (itemid.equalsIgnoreCase("Z0351") || itemid.equalsIgnoreCase("ypljl")
																							|| itemid.equalsIgnoreCase("tjjl") || itemid.equalsIgnoreCase(hireMajor)) {
																						//已登录且已经申请该职位
																						if (!StringUtils.isEmpty(userViewName) && "true".equals(isApplyedPos)
																								&& itemid.equalsIgnoreCase("ypljl"))
																							value = "已申请";
																						else
																							value = "<a onclick=\"rediract('" + p + "','" + z0311 + "','" + z0301
																									+ "','" + SafeCode.encode(posName) + "','" + unitName + "','" + type
																									+ "');\"  class='hire_posTable_href'> " + value + "</a>";
																					}
																					String align = "align=\"center\"";
																					if (itemtype.equalsIgnoreCase("N"))
																						align = "align=\"right\"";
																					String styleClass = "hj_zhaopin_list_tab_titletwo";
																					if (y == 0)
																						styleClass = "hj_zhaopin_list_tab_titletwo_1";
																	%>
																	<%
																		if (i % 2 == 0) {
																	%>
																	<%
																		if (itemtype.equalsIgnoreCase("N")) {
																	%>
																	<td height="36" <%=align%>
																		class="hire_posTable_body1_1">&nbsp;<%=value%>&nbsp;&nbsp;&nbsp;&nbsp;
																	</td>
																	<%
																		} else {
																	%>
																	<td height="36" <%=align%>
																		class="hire_posTable_body1_1">&nbsp;<%=value%>&nbsp;
																	</td>
																	<%
																		}
																	%>
																	<%
																		} else {
																	%>
																	<%
																		if (itemtype.equalsIgnoreCase("N")) {
																	%>
																	<td height="36" <%=align%>
																		class="hire_posTable_body2_2">&nbsp;<%=value%>&nbsp;&nbsp;&nbsp;&nbsp;
																	</td>
																	<%
																		} else {
																	%>
																	<td height="36" <%=align%>
																		class="hire_posTable_body2_2">&nbsp;<%=value%>&nbsp;
																	</td>
																	<%
																		}
																	%>

																	<%
																		}
																		}
																		out.println("</tr>");
																	}
																} else {//展现默认的字段 
																	for (int i = 0; i < posList.size(); i++) {

																		LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																		String isApplyedPos = (String) bean.get("isApplyedPos");

																		String posName = (String) bean.get("posName");
																		String z0333 = (String) bean.get("z0333");
																		String Z0331 = (String) bean.get("z0331");
																		String Z0329 = (String) bean.get("opentime");//发布日期    原为有效开始日期 z0329
																		String z0301 = (String) bean.get("z0301");
																		String z0311 = (String) bean.get("z0311");//想办法处理掉z0311
																		String state = (String) bean.get("state");
																		String count = (String) bean.get("count");
																		String z0313 = (String) bean.get("z0313");
																		String isNewPos = (String) bean.get("isNewPos");
																		if ((i > (pcount - 1) && request.getParameter("isShowAall") == null)) {
																			break;
																		}
																		if (z0333 == null || z0333.trim().equals(""))
																			z0333 = "&nbsp;";
																		if (Z0329 == null || Z0329.trim().equals(""))
																			Z0329 = "&nbsp;";
																		if (count == null || count.trim().equals(""))
																			count = "&nbsp;";
																		if (z0313 == null || z0313.trim().equals(""))
																			z0313 = "&nbsp;";
																		String p = "";
																		if (hireChannel.equals("01") && !hireMajor.equals("-1")) {
																			posName = (String) bean.get(hireMajor.toLowerCase());
																			p = bean.get("major") == null ? "" : "&major=" + (String) bean.get("major");
																		}

																		String styleClass = "hj_zhaopin_list_tab_titletwo";
																	%>
																	<%
																		if (i % 2 == 0) {
																	%>
																	<tr>
																		<td height="36" class="hire_posTable_body1" nowrap>
																			&nbsp;&nbsp; <a
																			onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>');"
																			class="hire_posTable_href"> <%=posName%> <%
																	 %>

																		</a>
																		</td>
																		<td height="36" class="hire_posTable_body1"><%=z0333%></td>
																		<td height="36" class="hire_posTable_body1"><%=Z0329%></td>
																		<%
																			if (hirechannel.equals("headHire")
																											|| !SystemConfig.getPropertyValue("zp_visibletype").equals("1")) {
																		%>
																		<td height="36" class="hire_posTable_body1"><%=count%>&nbsp;</td>
																		<%
																			} else {
																		%>
																		<td height="36" class="hire_posTable_body1"><%=z0313%>&nbsp;</td>
																		<%
																			}
																		%>
																		<td height="36" class="hire_posTable_body1"><a
																			onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>');"
																			class="hire_posTable_href"> <%
 	if (hirechannel.equals("headHire")) {
 %> <bean:message
																					key="hire.out.headhunter.recommend" /> <%
 	} else {
 %> <bean:message key="hire.column.applay" />
																				<%
																					}
																				%>
																		</a></td>
																	</tr>
																	<%
																		} else {
																	%>
																	<tr>
																		<td height="36" class="hire_posTable_body2">
																			&nbsp;&nbsp; <a
																			onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>');"
																			class="hire_posTable_href"> <%=posName%> <%
																	 %>
																		</a>
																		</td>
																		<td height="36" class="hire_posTable_body2"><%=z0333%></td>
																		<td height="36" class="hire_posTable_body2"><%=Z0329%></td>
																		<%
																			if (hirechannel.equals("headHire")
																											|| !SystemConfig.getPropertyValue("zp_visibletype").equals("1")) {
																		%>
																		<td height="36" class="hire_posTable_body2"><%=count%>&nbsp;</td>
																		<%
																			} else {
																		%>
																		<td height="36" class="hire_posTable_body2"><%=z0313%>&nbsp;</td>
																		<%
																			}
																		%>
																		<td height="36" class="hire_posTable_body2"><a
																			onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>');"
																			class="hire_posTable_href"> <%
 	if (hirechannel.equals("headHire")) {
 %> <bean:message
																					key="hire.out.headhunter.recommend" /> <%
 	} else {
 %> <bean:message key="hire.column.applay" />
																				<%
																					}
																				%>
																		</a></td>
																	</tr>
																	<%
																		}
																	%>
																	<%
																		}
																					}
																	%>
																</table>
															</td>
														</tr>
														<%
															if ((posList.size() > (pcount) && request.getParameter("isShowAall") == null))//猜测isShowAall有值得话  代表点击显示更多
																		{
														%>

														<tr>
															<td height="20" style="TEXT-ALIGN: right" width="715px">
																<br /> <a href="javascript:showMore()"><img
																	src="/images/hire/more.gif" border="0" /></a>
															</td>
														</tr>
														<%
															}
														%>
													</table>


													<%
														} else {//开始处理默认进来的情况：不是查询岗位  也不是 已浏览岗位
													%>
													<%
														String positionNumber = employPortalForm.getPositionNumber();
														String hireChannel = employPortalForm.getHireChannel();
														ArrayList posFieldList = employPortalForm.getPosFieldList();
														String selunit = employPortalForm.getSelunit();
														int pcount = Integer.parseInt(positionNumber);
														if (request.getParameter("unitCode") == null)//当点击more的时候会传递more所对应单位的部门过来 
														{
															ArrayList zpPosList = employPortalForm.getZpPosList();
															int n = 0;
															for (Iterator t = zpPosList.iterator(); t.hasNext();) {
																LazyDynaBean aBean = (LazyDynaBean) t.next();
																String unitName = (String) aBean.get("name");
																String id = (String) aBean.get("id");//z0321 代表的是招聘单位的代码值 
																ArrayList posList = (ArrayList) aBean.get("list");
																String content = (String) aBean.get("content");
																String contentType = (String) aBean.get("contentType");
													%>
													<table cellSpacing=0 cellPadding=1 width="100%"
														align="center" style='margin-top: 10px' border=0>
														<tbody>
															<tr align="left">
																<td colspan="5" class="hj_zhaopin_h1"><%=unitName%>
																	<%
																		if (n != 0)
																			out.print("<br>");
																		n++;
																	%></td>
															</tr>
															<tr>
																<td width="100%">
																	<table id="<%=id%>" width="100%" border="0"
																		cellspacing="0" style='table-layout: fixed'
																		cellpadding="0" class="table">
																		<tr align="center">
																			<%
																				if (posFieldList != null && posFieldList.size() > 0) {//设置了招聘外网显示指标 ,生成表头
																				int width = 15;
																				if (posFieldList.size() < 5)
																					width = 15;
																				for (int y = 0; y < posFieldList.size(); y++) {
																					LazyDynaBean abean = (LazyDynaBean) posFieldList.get(y);
																					String itemid = (String) abean.get("itemid");
																					String itemdesc = (String) abean.get("itemdesc");
																					if (hiddenColumn && (itemid.equals("tjjl") || itemid.equals("tjrsl"))) {
																						continue;
																					}
																					if (itemid.equalsIgnoreCase("Z0351")) {
																						itemdesc = ResourceFactory.getProperty("hire.out.employ.position");
																			%>
																			<td width="20%" class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				} else if (itemid.equalsIgnoreCase("ypljl")) {//应聘
																			%>
																			<td class='table_line_title' width="55px"><b><%=itemdesc%></b></td>
																			<%
																				} else if (itemid.equalsIgnoreCase("Z0357")) {//职位类别
																			%>
																			<td width="25%" class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				} else if (itemid.equalsIgnoreCase("Z0315")) {//招聘人数
																			%>
																			<td width="10%" class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				} else {
																			%>
																			<td width="<%=100 / (posFieldList.size()) - 0.5%>%"
																				class='table_line_title'><b><%=itemdesc%></b></td>
																			<%
																				}
																										}
																			%>
																			<%
																				} else {//没有外网显示指标,用默认的指标生成表头
																			%>
																			<td width="40%" class='table_line_title'><B><bean:message
																						key="hire.out.employ.position" /></B></td>
																			<td width="15%" class='table_line_title'><b><bean:message
																						key="lable.zp_plan_detail.domain" /></b></td>
																			<td width="15%" class='table_line_title'><b><bean:message
																						key="label.zp_release_pos.valid_date" /></b></td>
																			<%
																				if (!hiddenColumn) {//如果普通应聘者查看猎头招聘则隐藏掉推荐人数和推荐两列 
																					if (hirechannel.equals("headHire")) {
																			%>
																			<td width="15%" class='table_line_title'><b>
																					<bean:message
																						key="hire.out.headhunter.recommend.count" />
																			</b></td>
																			<%
																				} else if (!SystemConfig.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td width="15%" class='table_line_title'><b>
																					<bean:message key="hire.applay.personcount" />
																			</b></td>
																			<%
																				} else {
																			%>
																			<td width="10%" class='table_line_title'><b>需求人数</b></td>
																			<%
																				}
																			%>
																			<td width="10%" class='table_line_title'><b>
																					<%
																						if (hirechannel.equals("headHire")) {
																					%> <bean:message
																						key="hire.out.headhunter.recommend" /> <%
 	} else {
 %> <bean:message key="hire.column.applay" />
																					<%
																						}
																					%>
																			</b></td>
																			<%
																				}
																									}
																			%>
																		</tr>
																		<%
																			int colspan = 5;
																			if (posFieldList != null && posFieldList.size() > 0) {//展现外网显示指标 
																				colspan = posFieldList.size();;
																				for (int i = 0; i < posList.size(); i++) {
																					if ((i > (pcount - 1) && request.getParameter("isAllPosOk") == null)
																							|| (i > (pcount - 1) && request.getParameter("isAllPosOk") != null)) {
																						if (selunit != null && selunit.indexOf("," + id + ",") != -1) {
	
																						} else
																							break;
																					}
																					out.println("<tr>");
																					LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																					String isApplyedPos = (String) bean.get("isApplyedPos");
	
																					String state = (String) bean.get("state");
																					String isNewPos = (String) bean.get("isNewPos");
																					String z0301 = (String) bean.get("z0301");
																					String z0311 = (String) bean.get("z0311");//想办法处理掉z0311为空的情况 
																					String posName = (String) bean.get("z0351");//显示职位的信息
																					String p = "";
																					if (hireChannel.equals("01") && !hireMajor.equals("-1")) {
																						p = bean.get("major") == null ? "" : "&major=" + (String) bean.get("major");
																					}
																					for (int y = 0; y < posFieldList.size(); y++) {
																						LazyDynaBean abean = (LazyDynaBean) posFieldList.get(y);
																						String itemid = ((String) abean.get("itemid")).toLowerCase();
																						String itemtype = (String) abean.get("itemtype");
																						String value = (String) bean.get(itemid);
																						String z0321Name = (String) bean.get("z0321Name");
																						if (itemid.equalsIgnoreCase("z0321")) {
																							value = z0321Name;
																						}
																						if (value == null || value.equals(""))
																							value = "&nbsp;";
																						if (hiddenColumn && (itemid.equals("tjjl") || itemid.equals("tjrsl"))) {
																							continue;//如果是普通应聘人员查看猎头招聘信息则隐藏推荐人数和推荐两列
																						}
	
																						//专业列添加超链接
																						if (itemid.equalsIgnoreCase("z0351") || itemid.equalsIgnoreCase("ypljl")
																								|| itemid.equalsIgnoreCase(hireMajor)) {
																							//已登录且已经申请该职位
																							if (!StringUtils.isEmpty(userViewName) && "true".equals(isApplyedPos)
																									&& itemid.equalsIgnoreCase("ypljl"))
																								value = "已申请";
																							else
																								value = "<a onclick=\"rediract('" + p + "','" + z0311 + "','"
																										+ z0301 + "','" + SafeCode.encode(posName) + "','"
																										+ unitName + "','" + type + "','" + id
																										+ "')\"> " + value + "</a>";
																						}
	
																						if (itemid.equalsIgnoreCase("tjjl")) {//id即z0321
																							value = "<a onclick=\"recommendForposition('" + z0301 + "','"
																									+ SafeCode.encode(posName)
																									+ "')\"> " + value + "</a>";
																						}
																						String align = "align=\"center\"";
																						if (itemtype.equalsIgnoreCase("N"))
																							align = "align=\"center\"";
																		%>
																		<%
																			if (i % 2 == 0) {
																		%>
																		<td height="36" <%=align%> class='hire_posTable_href'><%=value%></td>
																		<%
																			} else {
																		%>
																		<td height="36" <%=align%> class='table_line_single hire_posTable_href'><%=value%></td>
																		<%
																			}
																					}
																					out.println("</tr>");
																				}
																			} else {//未设置招聘外网展现指标采用默认的 
																				if (posList != null && posList.size() > 0) {
																					for (int i = 0; i < posList.size(); i++) {
																						if (i > (pcount - 1) && request.getParameter("isAllPosOk") == null) {

																							if (selunit != null && selunit.indexOf("," + id + ",") != -1) {

																							} else
																								break;
																						}
																						LazyDynaBean bean = (LazyDynaBean) posList.get(i);
																						String isApplyedPos = (String) bean.get("isApplyedPos");

																						String posName = (String) bean.get("posName");//显示职位的信息 
																						String z0333 = (String) bean.get("z0333");
																						String Z0331 = (String) bean.get("z0331");
																						String Z0329 = (String) bean.get("opentime");//发布日期    原为有效开始日期 z0329
																						String z0301 = (String) bean.get("z0301");
																						String z0311 = (String) bean.get("z0311");//想办法处理掉z0311的问题 
																						String state = (String) bean.get("state");
																						String isNewPos = (String) bean.get("isNewPos");
																						String count = (String) bean.get("count");
																						String z0313 = (String) bean.get("z0313");
																						if (z0333 == null || z0333.trim().equals(""))
																							z0333 = "&nbsp;";
																						if (Z0329 == null || Z0329.trim().equals(""))
																							Z0329 = "&nbsp;";
																						if (count == null || count.trim().equals(""))
																							count = "&nbsp;";
																						if (z0313 == null || z0313.trim().equals(""))
																							z0313 = "&nbsp;";
																						String p = "";
																		%>
																		<tr>
																			<%
																				if (i % 2 == 0) {
																			%>
																			<td align="center" height='36'>&nbsp;&nbsp; <a
																				onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																				class='hire_posTable_href'> <%=posName%> <!-- 职位的描述 -->

																					<%
																						//if(state.equals("1")){ out.print("<IMG border=0 src='/images/hot.gif' >");  }
																					%><!-- 热点职位(最新程序中先屏蔽掉) -->
																					<%
																						//if(isNewPos.equals("1")){ out.print("<IMG border=0 src='/images/new0.gif' />");  }
																					%><!-- 新职位(最新程序中先屏蔽掉)-->
																			</a>
																			</td>

																			<td height="20" align="center" height='36'><%=z0333%></td>

																			<td align="center" height='36'><%=Z0329%></td>

																			<%
																				if (!hiddenColumn) {
																					if (hirechannel.equals("headHire") || !SystemConfig
																							.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td style="TEXT-AlIGN: center" height='36'><%=count%>&nbsp;</td>
																			<%
																				} else {
																			%>
																			<td style="TEXT-AlIGN: center" height='36'><%=z0313%>&nbsp;</td>
																			<%
																				}
																			%>

																			<td style="text-align: center" height='36'>
																				<%
																					if (hirechannel.equals("headHire")) {
																				%> <a
																				onclick="recommendForposition('<%=z0301%>','<%=SafeCode.encode(posName)%>');"
																				class='hire_posTable_href'> <bean:message
																						key="hire.out.headhunter.recommend" /> <%
 	} else {
 %> <a
																					onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																					class='hire_posTable_href'> <bean:message
																							key="hire.column.applay" /> <%
 	}
 %>
																				</a>
																			</td>

																			<%
																				}
																			} else {
																			%>
																			<td align="center" class='table_line_single'
																				height='36'>&nbsp;&nbsp; <a
																				onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																				class='hire_posTable_href'> <%=posName%> <%
																			 %>
																			</a>
																			</td>

																			<td height="20" align="center"
																				class='table_line_single'><%=z0333%></td>

																			<td align="center" class='table_line_single'><%=Z0329%></td>

																			<%
																				if (!hiddenColumn) {
																					if (hirechannel.equals("headHire") || !SystemConfig
																							.getPropertyValue("zp_visibletype").equals("1")) {
																			%>
																			<td style="TEXT-AlIGN: center"
																				class='table_line_single'><%=count%>&nbsp;</td>
																			<%
																				} else {
																			%>
																			<td style="TEXT-AlIGN: center"
																				class='table_line_single'><%=z0313%>&nbsp;</td>
																			<%
																				}
																			%>

																			<td style="text-align: center"
																				class='table_line_single'>
																				<%
																					if (hirechannel.equals("headHire")) {
																				%> <a
																				onclick="recommendForposition('<%=z0301%>','<%=SafeCode.encode(posName)%>');"
																				class='hire_posTable_href'> <bean:message
																						key="hire.out.headhunter.recommend" /> <%
 	} else {
 %> <a
																					onclick="rediract('<%=p%>','<%=z0311%>','<%=z0301%>','<%=SafeCode.encode(posName)%>','<%=unitName%>','<%=type%>','<%=id%>');"
																					class='hire_posTable_href'> <bean:message
																							key="hire.column.applay" /> <%
 	}
 %>
																				</a>
																			</td>
																			<%
																				}
																												}
																			%>
																		</tr>

																		<%
																			}
																									}

																								}
																		%>
																	</table>
																</td>

															</tr>
															<%
																if (posList != null && posList.size() > 0) {
																	if ((posList.size() > (pcount) && request.getParameter("isAllPosOk") == null)
																			|| (posList.size() > (pcount)
																					&& request.getParameter("isAllPosOk") != null)) {
																		if (selunit != null && selunit.indexOf("," + id + ",") != -1) {

																		} else {
															%>

															<tr id="more<%=id%>">
																<td height="20" style="TEXT-ALIGN: right" width="715px">
																	<a onclick="query(2,'<%=id%>');return false;"
																	href="javascript:void(0);"><img
																		src="/images/hire/more.gif" border="0" /></a>
																</td>
															</tr>
															<%
																}
																						}

																					}
															%>

														</tbody>
													</table>

													<%
														}
																	}
													%>


													<%
														}
													%>
												</div>
												<%
													} //输出招聘职位列表end
												%>
												<%
													if (type != null && type.length() != 0 && type.equalsIgnoreCase("resume")) {
												%>
												<div class='footer' style='height: 0px;'>
													&nbsp;&nbsp;</div>
												<%
													}
												%>
											</div>
											<%
												if (type != null && type.length() != 0
															&& (type.equalsIgnoreCase("resume") || type.equalsIgnoreCase("search"))) {

													} else {//滚动显示已经应聘人员的相关信息 

														ArrayList runHeaderList = employPortalForm.getRunHeaderList();
														if (runHeaderList.size() > 0) {
											%>
											<div class="jj zw">
												<h3>
													<span>应聘简历</span>
												</h3>
												<div class="nr">

													<table width="0" border="0" cellspacing="0" cellpadding="0"
														class="table">
														<tr>
															<logic:iterate id="header" name="employPortalForm"
																property="runHeaderList" indexId="index">
																<td height="25" class="hire_applyTable_head"
																	width='50px'><b> <bean:write name="header"
																			property="itemdesc" /></b></td>
															</logic:iterate>
														</tr>
													</table>
													<table width="100%" align="left" class="table">
														<tr>
															<td align="left"></td>
														</tr>
														<tr>
														<td align="center"><marquee scrolldelay="350" width='100%' height="230" direction="up" onmouseover='this.stop()' onmouseout='this.start()'>
														<table width='100%' border="0" cellspacing="0" width='100%' cellpadding="0">
															<%
																int i = 0;
															%>
															<logic:iterate id="data" name="employPortalForm" property="runDataList" indexId="index">
															<tr>
																<%
																for (int xx = 0; xx < runHeaderList.size(); xx++) {
																	LazyDynaBean bean = (LazyDynaBean) runHeaderList.get(xx);
																	String itemid = ((String) bean.get("itemid")).toLowerCase();
																	if (i % 2 == 0) {
																		%>
																		<td align="center" width="50px"	class="hire_applyTable_body1">
																		<%
																	} else {
																		%>
																		<td align="center" width="50px" class="hire_applyTable_body2">
																		<%
																	}
																	%> 
																	<logic:equal value="" name="data" property="<%=itemid%>">
																	&nbsp;
																	</logic:equal> 
																	<logic:notEqual value="" name="data" property="<%=itemid%>">
																	<bean:write name="data" property="<%=itemid%>" />
																	</logic:notEqual>
																	</td> <%
																}
																%>
																
															</tr>
															<%
															i++;
															%>
															</logic:iterate>
														</table>
														</marquee></td>
														</tr>
													</table>
												</div>
											</div>
											<%
												}
											%>
											<%
												}
											%>

										</div>

										<div class='footer' style='height: 0px;'>&nbsp;&nbsp;</div>
									</div>

								</div>

							</div>
			<script language='javascript'>
			<%String ll = (String) session.getAttribute("hasLogin");
			if (ll != null && ll.equalsIgnoreCase("true")) {
			
			} else {%>
			<!--hasLongin();  -->
			<%session.setAttribute("hasLogin", "true");
			}%>
			initCard();
			rr();       
			function rr() {
				<%if (request.getParameter("active") != null && request.getParameter("active").equals("1")) {%>  
				Ext.showAlert("帐号注册成功，已将激活帐号邮件发送到注册邮箱，请到注册邮箱中激活帐号！");
				<%}%>
			       
			}
			
			</script>
	</html:form>

</body>
<script language='javascript'>
JQuery("#previous").on("click",function(){
	var map = new HashMap();
	map.put("hireChannel","${employPortalForm.hireChannel}");
	map.put("pageNum",parseInt(JQuery("#pageId").html()));
	map.put("operation","previous");
	Rpc({functionId : 'ZP0000002657',success : changeContent}, map);
});
JQuery("#next").on("click",function(){
	var map = new HashMap();
	map.put("hireChannel","${employPortalForm.hireChannel}");
	map.put("pageNum",parseInt(JQuery("#pageId").html()));
	map.put("operation","next");
	Rpc({functionId : 'ZP0000002657',success : changeContent}, map);
});
function changeContent(outparamters){
	var param = Ext.decode(outparamters.responseText);
	var boardlist = param.pageBoardList;
	JQuery("#ficont ul:eq(0)").remove();
	JQuery("#pageId").html(param.pageNum);
	var $div = JQuery("#ficont");
	var $ul=JQuery("<ul></ul>"); 
	for(var i = 0;i<boardlist.length;i++){
		var obj = boardlist[i];
		var $li = JQuery("<li class='els'></li>");
		var a1 = JQuery("<a style='cursor:text'>"+obj.title+"</a>");
		//公告内容地址
		if(obj.content){
			a1 = JQuery("<a href='./showBoardPage.jsp?br_showBoardPage="+obj.id+"' style='cursor:pointer' target='_blank'>"+obj.title+"</a>");
			$li.append(a1);
		}else{
			$li.text(obj.title);
		}
		
		//公告附件地址
		if(obj.hasfile=="true"){
			var a2 = JQuery("<a href='"+obj.href+"' style='margin-left:10px;margin-bottom:4px'><img src='/images/board_attach.gif' alt='附件下载' /></a>");
			$li.append(a2);
		}
		$ul.append($li);
		$div.append($ul);
	}
}

var minh = document.documentElement.clientHeight-260;
//var h = document.getElementById('tc').scrollHeight-85;
document.getElementById('rg').style.cssText="margin-bottom: 30px;min-height:"+minh+"px;";//height:"+h+"px;";
function dealRes(i,name){
	document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value = replaceAll(document.getElementsByName(name+"["+i+"].viewvalue_view")[0].value, "|", ",");
	document.getElementsByName(name+"["+i+"].value")[0].value = document.getElementsByName(name+"["+i+"].viewvalue_value")[0].value;
}
//必须含有body元素才可屏蔽backspace
document.getElementsByTagName("body")[0].onkeydown =function(e){            
       //获取事件对象
       var event = e?e:window.event;
	var elem = event.relatedTarget || event.srcElement || event.target ||event.currentTarget;   
       if(event.keyCode==8){//判断按键为backSpace键  
		//获取按键按下时光标做指向的element  
           var elem = event.srcElement || event.target;   
           //判断是否需要阻止按下键盘的事件默认传递  
           var name = elem.nodeName;  
           if(name!='INPUT' && name!='TEXTAREA')
           	return _stopIt(event);  
           
           var type_e = elem.type.toUpperCase();  
           if(name=='INPUT' && (type_e!='TEXT' && type_e!='TEXTAREA' && type_e!='PASSWORD' && type_e!='FILE'))
           	return _stopIt(event);
           
           if(name=='INPUT' && (elem.readOnly==true || elem.disabled ==true)) 
           	return _stopIt(event);  
       }  
   }
function _stopIt(e){  
	if(e.returnValue){  
		e.returnValue = false ;  
    }  
    if(e.preventDefault ){  
        e.preventDefault();  
    }                 
    return false;
}
<%EmployPortalForm employPortalForm = (EmployPortalForm) session.getAttribute("employPortalForm");
			String info = employPortalForm.getValidateInfo();
			if (info != null && !info.equals("")) {%>
			setTimeout(function(){Ext.showAlert('<%=info%>');},1000);

<%employPortalForm.setValidateInfo("");	}%>
</script>
</html>
