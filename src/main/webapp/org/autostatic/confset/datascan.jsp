
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>   
<hrms:themes></hrms:themes>
<%
	String autostaticdialog=SystemConfig.getPropertyValue("autostaticdialog");
	autostaticdialog= autostaticdialog!=null?autostaticdialog:"";
	pageContext.setAttribute("autostaticdialog",autostaticdialog);

	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
 %>  
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
<script type="text/javascript">
CheckBrowserCompatibility();
</script>
<html:form action="/org/autostatic/confset/datascan"> 

<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='savewait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					<bean:message key='org.autostatic.mainp.save.wait'/>
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
<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style common_background_color" height=24>
					<bean:message key='org.autostatic.mainp.calculation.wait'/>
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
<table  cellpadding="0" cellspacing="0"><tr><td>
<table><tr><td>
<hrms:menubar menu="menubar1" id="menubar1" container="tableContainer1">
  	<hrms:menuitem name="file" label="menu.file.label">
    	<hrms:menuitem name="data_output" label="general.inform.muster.output.excel" icon="" url="outExcel();" command="" function_id="2306107"/>
    	<hrms:menuitem name="tempOutput" label="button.download.template" icon="" url="outTemplateExcel();" command="" function_id="2306108"/>
    	<hrms:menuitem name="tempinput" label="import.tempData" icon="" url="inputExcel();" command="" function_id="2306109"/>
  	</hrms:menuitem>
  	<hrms:menuitem name="show" label="infor.menu.view">
      	<hrms:menuitem name="view_area " label="org.autostatic.confset.datascan.viewrange" icon="" url="viewarea()" command="" enabled="true" visible="true"/>
      	<hrms:menuitem name="show_hide" label="org.autostatic.confset.datascan.viewhide" icon="" url="viewhide()" command="" enabled="true" visible="true" function_id="2306106"/>
      	<hrms:menuitem name="show_hide" label="infor.menu.sortitem" icon="" url="sortItem()" command="" enabled="true" visible="true" function_id=""/>
 	</hrms:menuitem> 
  	<hrms:menuitem name="set" label="kh.field.config" >
    	<hrms:menuitem name="set_scan" label="org.autostatic.confset.datascan.setscan" icon="" url="setscan();" command="" function_id="2306104"/>
    	<hrms:menuitem name="set_project" label="org.autostatic.confset.datascan.setproject" icon="" url="setproject()" command="" function_id="2306105"/>
  	</hrms:menuitem>  
</hrms:menubar>
</td></tr></table>
</td>
<td>&nbsp;</td>
</table>
<hrms:dataset name="subsetConfsetForm" property="fieldlist" scope="session" setname="${subsetConfsetForm.tablename}" 
 setalias="position_set" readonly="false" editable="true" select="false" 
 sql="${subsetConfsetForm.selectsql}" pagerows="${subsetConfsetForm.pagerows}" buttons="bottom">      
	<hrms:commandbutton name="save" function_id="2306102" hint="gz.acount.determined.alert"  functionId="1602010223"  refresh="true" type="all-change" setname="${subsetConfsetForm.tablename}" >
	<bean:message key="button.save"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="add" function_id="2306103" onclick="checkDatejindu();"><bean:message key="infor.menu.compute"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="printbutton" function_id="2306107" onclick="outExcel();">
		<bean:message key="general.inform.muster.output.excel"/>
	</hrms:commandbutton>
	<hrms:commandbutton name="outpuntExcel" function_id="2306108" onclick="outTemplateExcel();">
		<bean:message key="button.download.template"/>
	</hrms:commandbutton> 
	<hrms:commandbutton name="inpuntExcel" function_id="2306109" onclick="inputExcel();">
		<bean:message key="import.tempData"/>
	</hrms:commandbutton>  
	<hrms:commandbutton name="returns" onclick="jumpconfset();">
		<bean:message key="button.return"/>
	</hrms:commandbutton>
</hrms:dataset>
<table border="0" cellspacing="0" cellpadding="0" height="20" width="400" id="tableContainer1" style="position:absolute;left:380px;top:32px;">
<tr>
	<td width="10">&nbsp;</td>	
	<logic:notEqual name="subsetConfsetForm" property="inforflag" value="0">
	<td width="120" valign="middle"><bean:message key="org.autostatic.confset.datascan.level"/>
		<html:select name="subsetConfsetForm" property="level" onchange="submitset();" style="width:80;">
			 <html:optionsCollection property="levellist" value="dataValue" label="dataName" />
		</html:select>
	</td>
	</logic:notEqual>
	<td width="70">
		<table width="50%" border="0" cellspacing="0" cellpadding="0">
		   <tr> 
		       <td align="right"></td>
		       <td valign="middle"> 
		          <html:text name="subsetConfsetForm" property="yearnum" styleClass="text4" onkeypress="event.returnValue=IsDigit();" style="height:20px;width:40;font-size:9pt"/>                      
		       </td>
		       <td valign="middle" align="left">
		          <table border="0" cellspacing="2" cellpadding="0" >
			      	<tr><td><button id="y_up" class="m_arrow" onclick="yincrease();">5</button></td></tr>
			      	<tr><td><button id="y_down" class="m_arrow" onclick="ysubtract();">6</button></td></tr>
		          </table>
		       </td>
		       <td align="left"><bean:message key="columns.archive.year"/></td>
		    </tr>
		 </table> 
	</td>
	<logic:notEqual name="subsetConfsetForm" property="monthnum" value="0">
	<td width="70">
	   <span id="months" >
	   <table width="50%" border="0" cellspacing="0" cellpadding="0" >
			<tr> 
		   		<td align="right"></td>
		   		<td valign="middle"> 
		       	 	<html:text name="subsetConfsetForm" property="monthnum" styleClass="text4" onkeypress="event.returnValue=IsDigit();" style="height:20px;width:40;font-size:9pt"/>                     
		        </td>
		        <td valign="middle" align="left">
		          <table border="0" cellspacing="2" cellpadding="0">
			      	<tr><td><button id="m_up" class="m_arrow" onclick="mincrease();">5</button></td></tr>
			      	<tr><td><button id="m_down" class="m_arrow" onclick="msubtract();">6</button></td></tr>
		          </table>
		        </td>
		        <td align="left"><bean:message key="columns.archive.month"/></td>
		    </tr>
		</table>
	    </span>
	</td>
	</logic:notEqual>
	<td width="120">&nbsp;</td>
	<td align="center">&nbsp;
		<html:hidden name="subsetConfsetForm" property="subset"/>
		<html:hidden name="subsetConfsetForm" property="hideitemid"/>
		<html:hidden name="subsetConfsetForm" property="areavalue"/>
		<html:hidden name="subsetConfsetForm" property="view_scan"/>
	</td>
 </tr>
</table>        
</html:form>
<script type="text/javascript">
function yincrease(){
	var yearnum=document.getElementById("yearnum").value;
	var check=yearnum.match(/^\+?[0-9]*[1-9][0-9]*$/);
	if(check!=null){
		var yearset = parseInt(yearnum);
		yearset = yearset+1;
		if(yearset<3000){
			document.all.yearnum.value = yearset;
			submitset();
		}
	}
}
function ysubtract(){
	var yearnum=document.getElementById("yearnum").value;
	var check=yearnum.match(/^\+?[0-9]*[1-9][0-9]*$/);
	if(check!=null){
		var yearset = parseInt(yearnum);
		if(yearset<1991){
			document.all.yearnum.value = 1990;
		}else{
			yearset = yearset-1;
			document.all.yearnum.value = yearset;
		}
		submitset();
	}
}

function mincrease(){
	var monthnum=document.getElementById("monthnum").value;
	var check=monthnum.match(/^\+?[0-9]*[1-9][0-9]*$/);
	
	if(check!=null){
		var monthset = parseInt(monthnum);
		if(monthset>11){
			document.all.monthnum.value = monthset;
		}else{
			monthset = monthset+1;
			document.all.monthnum.value = monthset;
			submitset();
		}
		
	}
}
function msubtract(){
	var monthnum=document.getElementById("monthnum").value;
	var check=monthnum.match(/^\+?[0-9]*[1-9][0-9]*$/);
	
	if(check!=null){
		var monthset = parseInt(monthnum);
		if(monthset<2){
			document.all.monthnum.value = 1;
		}else{
			monthset = monthset-1;
			document.all.monthnum.value = monthset;
			submitset();
		}
		
	}
}
function viewarea(){
    var subset = document.getElementById("subset").value;
    var thecodeurl ="/org/autostatic/confset/view_area.jsp?subset="+subset; 
    var height = 350;
    if(isIE6()){
    	height +=20;
    } 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:300px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	document.getElementById("areavalue").value=return_vo;
    	submitset();
    }
}
function viewhide(){
    var subset = document.getElementById("subset").value;
    var thecodeurl ="/org/autostatic/confset/view_hide.do?b_query=link&subset="+subset; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:350px; dialogHeight:380px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
   		submitset();
    }
}
function sortItem(){
    var subset = document.getElementById("subset").value;
    var thecodeurl ="/org/autostatic/confset/view_hide.do?b_sort=link&subset="+subset; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:320px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
    if(return_vo!=null){
   		submitset();
    }
}
function setproject(){
   var thecodeurl ="/org/autostatic/mainp/project.do?b_query=link`flag=1`param="; 
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   var height = 560;
   if(isIE6){
      height += 31;
   }
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:760px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
}

function submitset(){
   subsetConfsetForm.action="/org/autostatic/confset/datascan.do?b_reset=link";
   subsetConfsetForm.submit();
}
function setscan(){
	//var view_scan = document.getElementById("view_scan").value;
    var thecodeurl ="/org/autostatic/confset/setscandata.do?b_query=link&flag=datascan&view_scan="/*+view_scan*/; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:340px; dialogHeight:350px;resizable:no;center:yes;scroll:no;status:no");
    /*if(return_vo!=null){
    	//jindu();
    	document.getElementById("view_scan").value = return_vo;
    	checkDate();
    }*/
}
function outExcel(){
	var  yearnum =  document.getElementById("yearnum").value;
	
	var  monthnum = "${subsetConfsetForm.monthnum}";
	
	var  view_scan = document.getElementById("view_scan").value;
	var  hideitemid = document.getElementById("hideitemid").value;
	var  subset =  document.getElementById("subset").value;
	var  areavalue =  document.getElementById("areavalue").value;
	var  level = "${subsetConfsetForm.level}";
	var hashvo=new ParameterSet();
    hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("view_scan",view_scan);
	hashvo.setValue("hideitemid",hideitemid);
	hashvo.setValue("subset",subset);
	hashvo.setValue("areavalue",areavalue);
	hashvo.setValue("level",level);
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'1602010216'},hashvo);
		
}
function showfile(outparamters){
	var outName=outparamters.getValue("outName");
	//var name=outName.substring(0,outName.length-1)+".xls";
	window.location.target="_blank";
	window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
}
function outTemplateExcel(){
	var  yearnum =  document.getElementById("yearnum").value;
	var  monthnum = "${subsetConfsetForm.monthnum}";
	
	var  view_scan = document.getElementById("view_scan").value;
	var  hideitemid = document.getElementById("hideitemid").value;
	var  subset =  document.getElementById("subset").value;
	var  areavalue =  document.getElementById("areavalue").value;
	var  level = "${subsetConfsetForm.level}";
		
	var hashvo=new ParameterSet();
    hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("view_scan",view_scan);
	hashvo.setValue("hideitemid",hideitemid);
	hashvo.setValue("subset",subset);
	hashvo.setValue("areavalue",areavalue);
	hashvo.setValue("level",level);
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'03030000030'},hashvo);
		
}
function checkDate(){
	var  yearnum =  document.getElementById("yearnum").value;
	var  months = "${subsetConfsetForm.monthnum}";
	var  monthnum = "";
	var  subset =  document.getElementById("subset").value;
	if(months!=0){
		monthnum = document.getElementById("monthnum").value;
		if(monthnum<1||monthnum>12){
			alert(INPUT_CORRECT_MONTH);
			return;
		}
	}else{
		monthnum='0';
	}
	if(isNaN(yearnum)==true){
		alert(INPUT_CORRECT_YEAR);
		return;
	}
	if(isNaN(monthnum)==true){
		alert(INPUT_CORRECT_MONTH);
		return;
	}
	
	var hashvo=new ParameterSet();
   	hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("fieldsetid",subset);
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:count,functionId:'1602010222'},hashvo);
}
function checkDatejindu(){
	var  yearnum =  document.getElementById("yearnum").value;
	var  months = "${subsetConfsetForm.monthnum}";
	var  monthnum = "";
	var  subset =  document.getElementById("subset").value;
	if(months!=0){
		monthnum = document.getElementById("monthnum").value;
		if(monthnum<1||monthnum>12){
			alert(INPUT_CORRECT_MONTH);
			return;
		}
	}else{
		monthnum='0';
	}
	if(isNaN(yearnum)==true){
		alert(INPUT_CORRECT_YEAR);
		return;
	}
	if(isNaN(monthnum)==true){
		alert(INPUT_CORRECT_MONTH);
		return;
	}
	
	var hashvo=new ParameterSet();
   	hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("fieldsetid",subset);
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:count,functionId:'1602010222'},hashvo);
	
}
function count(outparamters){
	var checkdb = outparamters.getValue("checkdb");
	var included = "";
	var autostaticdialog='${autostaticdialog}';//设置编制管理中计算或统计汇总是否出现引入上期数据对话框 system.properties
	//alert(autostaticdialog);
	//alert(autostaticdialog!='false');
	if(checkdb=='ok'&&autostaticdialog!='false'){
 		var fieldsetid = document.getElementById("subset").value;
    	var thecodeurl="/org/autostatic/confset/included.do?b_included=link&fieldsetid="+fieldsetid; 
    	var width = 340;
    	var height = 350;
    	if(isIE6()){
    		width += 10;
    		height += 25;
    	} 
    	var popwin= window.showModalDialog(thecodeurl,"", 
        	"dialogWidth:"+width+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:yes;status:no");
        if(popwin!=null){
        	included = popwin;
        	jindu();
        	subsetConfsetForm.action="/org/autostatic/confset/datascan.do?b_reset=link&count=0k&included="+included;
    		subsetConfsetForm.submit();
        }
   }else{
   		jindu();
    	subsetConfsetForm.action="/org/autostatic/confset/datascan.do?b_reset=link&count=0k&included="+included;
   	 	subsetConfsetForm.submit();
    }
}
function inputCodeDialog(mytarget) {
    var codevalue,thecodeurl,target_name,hidden_name,hiddenobj;
    if(mytarget==null)
      return;
    var oldInputs=document.getElementsByName(mytarget);
    
    oldobj=oldInputs[0];
    //根据代码显示的对象名称查找代码值名称	
    target_name=oldobj.name;
    hidden_name=target_name+"el";
    var hiddenInputs=document.getElementsByName(hidden_name);
    if(hiddenInputs!=null)
    {
    	hiddenobj=hiddenInputs[0];
    	codevalue="";
    }
    var theArr=new Array(oldobj,hiddenobj); 
    thecodeurl="/org/autostatic/confset/level.jsp"; 
    var popwin= window.showModelessDialog(thecodeurl, theArr, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
}
function jindu(){
	//新加的，屏蔽整个页面不可操作
	document.all.ly.style.display="block";   
	document.all.ly.style.width=document.body.clientWidth;   
	document.all.ly.style.height=document.body.clientHeight; 
	
	var x=document.body.scrollLeft+event.clientX+150;
    var y=document.body.scrollTop+event.clientY+100; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function IsDigit() { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function jumpconfset(){
    subsetConfsetForm.action="/org/autostatic/confset/datasynchro.do?b_init=link";
    subsetConfsetForm.submit();
}
function inputExcel(){
    var thecodeurl ="/org/autostatic/confset/datascan.do?b_inExport=link`saveflag=search`tablename=${subsetConfsetForm.tablename}"; 
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
    var return_vo= window.showModalDialog(iframe_url, "", 
              "dialogWidth:400px; dialogHeight:200px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null){
    	submitset();
    }
}

function table${subsetConfsetForm.tablename}_B0110_onRefresh(cell,value,record){
	if(record!=null&&record!=""){	
		var values = record.getValue("B0110");
		var hashvo=new ParameterSet();
   		hashvo.setValue("b0110",values);
   		hashvo.setValue("cell",cell);
   		var In_paramters="flag=1"; 
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:rs_view,functionId:'1602010228'},hashvo);
		
	}
	function rs_view(outparamters){
		var rs_view=outparamters.getValue("rs_view");
		cell.innerHTML=rs_view;
	}
}

if('hcm'=='<%=bosflag%>'){
	document.getElementById("tableContainer1").style.top="42px";
}
</script> 


