<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.sys.options.otherparam.SysOthParamForm"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="../../js/validate.js"></script>
<script language="JavaScript" src="../../js/validateDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.report.auto_fill_report.ReportOptionForm,				 
				 com.hrms.hjsj.sys.DataDictionary"%>
<% 
	
	ReportOptionForm reportOptionForm=(ReportOptionForm)session.getAttribute("reportOptionForm");	
	ArrayList dbprelist = reportOptionForm.getDbprelist(); // 授权人员库
		
%>

<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*
function document.oncontextmenu() 
{ 
  	return false; 
} 
*/
</script>
   <link href="../../css/css1.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="../../ajax/skin.css"></link>
<hrms:themes></hrms:themes>
<script language="javascript" src="../../ajax/constant.js"></script>
<script language="javascript" src="../../ajax/basic.js"></script>
<script language="javascript" src="../../ajax/common.js"></script>
<script language="javascript" src="../../ajax/control.js"></script>
<script language="javascript" src="../../ajax/dataset.js"></script>
<script language="javascript" src="../../ajax/editor.js"></script>
<script language="javascript" src="../../ajax/dropdown.js"></script>
<script language="javascript" src="../../ajax/table.js"></script>
<script language="javascript" src="../../ajax/menu.js"></script>
<script language="javascript" src="../../ajax/tree.js"></script>
<script language="javascript" src="../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../ajax/command.js"></script>
<script language="javascript" src="../../ajax/format.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    	var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="../../ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

<script language="javascript">
 /*变量声明部分*/ 
  var start1;//用于判断-号出现的位置
  var i;//用于判断字符串中'-'号的出现位置,定义的循环变量
  var chkyear;//用于截取年
  var chkyearinteger;
  var chkmonths;//用于截取月
  var chkmonthsinteger;
  var chkdays;//用于截取日
  var chkdaysinteger;
  var chk1;//用于按位判断输入的年,月,日是否为整数
  var chk2;
  var mon=new Array(12);/*声明一个日期天数的数组*/
  mon[0]=31;
  mon[1]=28;
  mon[2]=31;
  mon[3]=30;
  mon[4]=31;
  mon[5]=30;
  mon[6]=31;
  mon[7]=31;
  mon[8]=30;
  mon[9]=31;
  mon[10]=30;
  mon[11]=31;
  
  function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}
  
  
  //输入的字符串为空时,提示错误
  function check(aa){ 
   var btnok = document.getElementById("btnok");
   if(document.getElementsByName("startdate")[0].value == "")
   {
	    alert("起始日期不能为空！");
	    document.getElementsByName("startdate")[0].focus();
	    return false;
   }
   if(!validate(document.getElementsByName("startdate")[0],"起始日期"))
   		return;
   if(aa.value == "")
   {
    alert(REPORT_INFO1+"！");
    aa.focus();
    return false;
   }
   
   aa.value=replaceAll(aa.value,'.','-')	
   
   //输入的字符串不为固定格式10个字符时,提示错误
   if(aa.value.length != 10)
   {
    alert(REPORT_INFO2+"！");
    aa.focus();
    return false;
   }
   //输入的字符串为10个字符时,按项判断是否符合规定的格式
   else
   {
    if(aa.value.substring(4,5)!='-')
   {
    alert(REPORT_INFO3);
    aa.focus();
    return false;
   }
   if(aa.value.substring(7,8)!='-')
   {
    alert(REPORT_INFO4);
    aa.focus();
    return false;
   }
    for(i=0;i<6;i++)
    {
     start1=aa.value.substring(i,i+1);
     if(start1=='-' && i!=4)
     {
      alert(REPORT_INFO5);
      aa.focus();
      return false;
     }
     else
     {
      continue;
     }
    }
    for(i=6;i<=9;i++)
    {
     start1=aa.value.substring(i,i+1);
     if(start1=='-'&& i!=7)
     {
      alert(REPORT_INFO5);
      aa.focus();
      return false;
     }
     else
     {
      continue;
     }
    }
    
    
    //按位判断每位是否为整数
    for(i=0;i<=9;i++)
    {
     chk1=aa.value.substring(i,i+1);
     if(chk1=='-')
     {
     continue;
     }
     chk2=parseInt(chk1,10);
     if(!(chk2>=0&&chk2<=9))
     {
      alert(chk1+REPORT_NONUMBER+"！");
      aa.focus();
      return false;
     } 
    }
    //判断年是否符合条件
    chkyear=aa.value.substring(0,4);
    chkyearinteger=parseInt(chkyear,10);
    if(!(chkyearinteger>=1900&&chkyearinteger<=2100))
    {
     alert(REPORT_INFO6+"!");
     aa.focus();
     return false;
    }
    
    //根据年设2月份的日期
    if(chkyearinteger%100==0||chkyearinteger%4==0)
    {
    mon[1]=29;
    }
    else
    {
    mon[1]=28;
    }
    //判断月是否符合条件
    chkmonths=aa.value.substring(5,7);
    chkmonthsinteger=parseInt(chkmonths,10);
    if(!(chkmonthsinteger>=1&&chkmonthsinteger<=12))
    {
     alert(REPORT_INFO7+"!");
     aa.focus();
     return false;
    }
    //判断日期是否符合条件
    chkdays=aa.value.substring(8,10);
    chkdaysinteger=parseInt(chkdays,10);
    switch(chkmonths)
    {
     case "01":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[0]))
        {
         alert("1"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "02":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[1]))
        {
         alert("2"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
    
     case "03":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[2]))
        {
         alert("3"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "04":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[3]))
        {
         alert("4"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "05":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[4]))
        {
         alert("5"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "06":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[5]))
        {
         alert("6"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
    
     case "07":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[6]))
        {
         alert("7"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "08":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[7]))
        {
         alert("8"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     
     case "09":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[8]))
        {
         alert("9"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "10":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[9]))
        {
         alert("10"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "11":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[10]))
        {
         alert("11"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     case "12":if(!(chkdaysinteger>0&&chkdaysinteger<=mon[11]))
        {
         alert("12"+REPORT_INFO8+"!");
         aa.focus();
         return false;
        }
        break;
     }//日期判断结束
     if(document.getElementsByName("startdate")[0].value>aa.value)
     {
  	    alert(REPORT_INFO75);
  	    document.getElementsByName("startdate")[0].focus();
  	  	aa.focus();
  	    return false;
     }
	    if (document.reportOptionForm.checkbox.checked){
			document.reportOptionForm.setResult.value="1";
		}else{
			document.reportOptionForm.setResult.value="0";
		}
		 if (document.reportOptionForm.checkboxupdateflag[0].checked){
			document.reportOptionForm.updateflag.value="1";
		}else{
			document.reportOptionForm.updateflag.value="0";
		}
		btnok.disabled = "true";
		reportOptionForm.action="/report/auto_fill_report/options.do?b_update2=update2&code=<%=request.getParameter("code")%>&home=<%=request.getParameter("home")%>&operateObject=<%=request.getParameter("operateObject")%>&updateflag="+document.reportOptionForm.updateflag.value;
   		reportOptionForm.submit();
   		
	

   } 
    
}	
  var isIE=(!!window.ActiveXObject || "ActiveXObject" in window);  
function returnInfo(outparamters){
		var waitInfo=eval("wait");	   
		 waitInfo.style.display="none";
		 window.returnValue="ok";
		 //liuy 2015-2-13 6807：cs扫描库设置为本报表设置，bs自动取数/反查：对1号表取数后反查，反查不对 start
		 var vo=new Object();
		 var dbpreStr = outparamters.getValue("dbpreStr");
		 vo.dbpreStr = dbpreStr;
		 var appdate = outparamters.getValue("appdate");
		 vo.appdate = appdate;
		 var start = outparamters.getValue("start");
		 vo.start = start;
		 window.returnValue=vo;
		 //liuy 2015-2-13 end
		 var info = outparamters.getValue("info");
		 alert(info);
		  if(parent.opener){
				parent.opener.returnVo(vo);
				parent.window.close(); 
			}else{
				window.close(); 
			}
			  
}
function returnInfo2(outparamters){
var waitInfo=eval("wait");	   
waitInfo.style.display="none";
var info = outparamters.getValue("info");
alert(info);
 window.returnValue="ok";
}
	
</script>
<style>
<!--

-->
</style>
<body onKeyDown="return pf_ChangeFocus();" style="height: 180px;overflow: hidden;">
	<!-- 
	<table width="100%" height="90%" align="center" style="margin-top: 20px;" border="0" cellpadding="0" cellspacing="0">
		<tr>  
			<td valign="middle" align="center">
	 -->		
	 <div style="height:280px; margin: 20 12;">
				<form name="reportOptionForm" method="post" action="/report/auto_fill_report/options.do">
					<fieldset align="center" style="margin-left: 3px;width: 535px;height: 230px;">
					<legend ><bean:message key="options.title"/></legend>
					<div style="height: 65px;width: 255px;overflow: auto;margin-left:140px !important; margin-bottom: 3px;" class="fixedDiv2">
					<table border="0" cellspacing="0"  align="center" cellpadding="0">
						<% int i =0; %>
						<hrms:extenditerate id="element" name="reportOptionForm" property="dbNameListForm.list"   indexes="indexes"  pagination="dbNameListForm.pagination" pageCount="120" scope="session">
							<%if(i%2==0){ %>
						<tr >
							<td>
								<logic:equal name="element" property="string(flag)" value="1">
									<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select"  value="false" indexes="indexes"/>&nbsp;
								</logic:equal>  
								<logic:equal name="element" property="string(flag)" value="0">
									<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select" value="true" indexes="indexes"/>&nbsp;
								</logic:equal> 
									<bean:write name="element" property="string(dbname)" filter="false"/>&nbsp;&nbsp;&nbsp;							
							</td>	
							<%}else{ %>
							<td>
								<logic:equal name="element" property="string(flag)" value="1">
									<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select"  value="false" indexes="indexes"/>&nbsp;
								</logic:equal>  
								<logic:equal name="element" property="string(flag)" value="0">
									<hrms:checkmultibox name="reportOptionForm" property="dbNameListForm.select" value="true" indexes="indexes"/>&nbsp;
								</logic:equal> 
									<bean:write name="element" property="string(dbname)" filter="false"/>&nbsp;&nbsp;
							</td>	
						</tr>
							<%}i++; %>
						</hrms:extenditerate>
						<%if(i%2!=0) {%>
					<td>
					</td>
					</tr>
					<%} %>
					</table>
					</div>
					<table border="0" cellspacing="0"  align="center" cellpadding="0">	
						<tr height="25" >  
							<td colspan="2" >起始日期
							<input  type="text" name="startdate" extra="editor"  id="editor4"  
							dropDown="dropDownDate"  value="<bean:write name='reportOptionForm' property='startdate'/>">
							</td>
						</tr> 
						<tr height="30">  
							<td colspan="2"><bean:message key="options.enddate"/>
							<input  type="text" name="appDate" extra="editor"  id="editor4"  
							dropDown="dropDownDate"  value="<bean:write name='reportOptionForm' property='appDate'/>">
							</td>
						</tr>  
					    <tr height="30">  
							<td colspan="2">
								<logic:equal name="reportOptionForm" property="result" value="1">
								<input type="checkbox" name="checkbox" checked>&nbsp;
								</logic:equal>  
								<logic:equal name="reportOptionForm" property="result" value="0">
								<input type="checkbox" name="checkbox" >&nbsp;
								</logic:equal> 
								<input type="hidden" name="setResult">
								<bean:message key="options.scanqueryresult"/>
							</td>
						 </tr>
						    <tr height="30">  
							<td colspan="2">
								<logic:equal name="reportOptionForm" property="updateflag" value="1">
								<input type="radio" name="checkboxupdateflag" checked >&nbsp;
								<bean:message key="options.condition.getnumber"/>
								</logic:equal>
								<logic:equal name="reportOptionForm" property="updateflag" value="0">
								<input type="radio" name="checkboxupdateflag"  >&nbsp;
								<bean:message key="options.condition.getnumber"/>
								</logic:equal>  
							</td>
							</tr>
							<tr height="30">
							<td colspan="2">	
								<logic:equal name="reportOptionForm" property="updateflag" value="0">
								<input type="radio" name="checkboxupdateflag"  checked>&nbsp;
								<bean:message key="options.all.getnumber"/>
								</logic:equal> 
								<logic:equal name="reportOptionForm" property="updateflag" value="1">
								<input type="radio" name="checkboxupdateflag"  >&nbsp;
								<bean:message key="options.all.getnumber"/>
								</logic:equal> 
								<input type="hidden" name="updateflag">
							</td>
						 </tr>  
				</table>
				</fieldset>
				<table align='center' style="margin-top: 6px; ">
				<tr>
						<td align="center" >
							<input type="button" id="btnok" name="b_update" value="<bean:message key='button.ok'/>" class="mybutton" onClick="check(reportOptionForm.appDate)">     
							<input type="reset" value="<bean:message key='options.reset'/>" class="mybutton" style="margin-left: -2px;">
							<hrms:tipwizardbutton flag="report" target="il_body" formname="reportOptionForm"/> 
						</td>
				</tr>
				</table>
			</form>
		</div>
		 
		<!-- 
		</td>
		</tr>
	</table>
		 -->
	
	<div id='wait' style='position:absolute;top:60;left:60;display:none; width: 80%;'>
		<table border="1" width="80%" cellspacing="0" cellpadding="4" class="table_style" height="87px" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="80%" scrollamount="5" scrolldelay="10">
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
	
</body>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);

</script>
<script language="javascript">
  initDocument();
  <%
	 if(request.getParameter("b_update2")!=null&&request.getParameter("b_update2").equals("update2")){ %> 
	         var btnok = document.getElementById("btnok");
	        btnok.disabled = "true";
	        var waitInfo=eval("wait");	   
            waitInfo.style.display="block";
	 		var hashvo=new ParameterSet();
   		    var selectid=new Array();
	 		selectid[0]= "<%=request.getParameter("code")%>";
	  		var updateflag= "<%=request.getParameter("updateflag")%>";
			hashvo.setValue("home","${reportOptionForm.home}");
			hashvo.setValue("operateObject","${reportOptionForm.operateObject}");
			hashvo.setValue("selectid",selectid);
			hashvo.setValue("updateflag",updateflag);
			hashvo.setValue("appdate","${reportOptionForm.appDate}");
			hashvo.setValue("startdate","${reportOptionForm.startdate}");
			hashvo.setValue("dbprelist","${reportOptionForm.dbpreStr}");
			var In_paramters="flag=1";
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,onFailure:returnInfo2,functionId:'03010000002'},hashvo);
   

	<% }%>
</script>
