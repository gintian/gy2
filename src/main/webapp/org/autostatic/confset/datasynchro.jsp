
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
	
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<hrms:themes></hrms:themes>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	if(userView != null){
	  css_url=userView.getCssurl();
	 
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
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

</style> 

<html:form action="/org/autostatic/confset/datasynchro"> 

<div id='wait' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
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
<table align="center" border="0" width="400" cellspacing="0" cellpadding="0" style="margin-top:50px;">
<tr><td width="100%">
	<table border="0" width="100%" cellspacing="0" align="left" cellpadding="0" class="ListTable1">		
	<bean:define id="confsetlist"  name="subsetConfsetForm"  property="confsetlist" />
		<tr>
			<td class="TableRow" nowrap>
				选择按（年|月）变化子集
			</td>
		</tr>
		<tr>
			<td class="RecordRow">
				<html:select styleId="selectconfset" multiple="true" name="subsetConfsetForm" property="subset" style="width:400;height:200" onchange="valueonchage();">
				<logic:iterate id="element" name="subsetConfsetForm" property="confsetlist">
				<logic:equal value="1"  name="element" property="changeflag">
					<html:option value="1-${element.fieldsetid}">[<bean:message key='columns.archive.month'/>]<bean:write name="element" property="customdesc"/></html:option>
				</logic:equal>
				<logic:notEqual value="1" name="element" property="changeflag" >
					<logic:equal value="2" name="element" property="changeflag" >
						<html:option value="2-${element.fieldsetid}">[<bean:message key='columns.archive.year'/>]<bean:write name="element" property="customdesc"/></html:option>
					</logic:equal>
				</logic:notEqual>
				</logic:iterate>
				</html:select>
			</td>
		</tr>
	</table>
</td></tr>
<tr><td>
	<table width="100%"  align="center" class="RecordRowP">
	<tr><td align="left">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr><td width="85" >
			<table width="50%" border="0" cellspacing="0" cellpadding="0">
	        	<tr> 
	          		<td align="right">&nbsp;</td>                                 
	          		<td valign="middle">
	          			<html:text name="subsetConfsetForm" property="yearnum" styleClass="text4" styleId="yearnum" onkeypress="event.returnValue=IsDigit();"  style="width:40"/>                     
	          		</td>
	          		<td valign="middle">&nbsp;</td>
	          		<td valign="middle" align="left">
		          			<table border="0" cellspacing="2" cellpadding="0" >
			      				<tr><td><button id="y_up" type="button" class="m_arrow" onclick="yincrease();">5</button></td></tr>
			      				<tr><td><button id="y_down" type="button" class="m_arrow" onclick="ysubtract();">6</button></td></tr>
		          			</table>
	          		</td>
	          		<td align="left"><bean:message key='datestyle.year'/></td>
	          	</tr>
	         </table> 
    	 </td>
    	 <td>
          	  <span id="months" >
	          <table width="50%" border="0" cellspacing="0" cellpadding="0" >
			  	<tr> 
	   		  		<td align="right">&nbsp;</td>
	   		  		<td valign="middle"> 
	       	  			<html:text name="subsetConfsetForm" property="monthnum" styleClass="text4" styleId="monthnum" onkeypress="event.returnValue=IsDigit();"  style="width:40"/>                     
	          		</td>
	          		<td valign="middle" align="left">
	          			<table border="0" cellspacing="2" cellpadding="0">
		      				<tr><td><button id="m_up" class="m_arrow" onclick="mincrease();">5</button></td></tr>
		      				<tr><td><button id="m_down" class="m_arrow" onclick="msubtract();">6</button></td></tr>
	          			</table>
	          		</td>
	          		<td align="left"><bean:message key='datestyle.month'/></td>
	          	</tr>
			  </table>
     	      </span>
		</td></tr>
		</table>
		</td></tr>
		</table>    
</td></tr>
<tr><td>
		<table width="100%" border="0" align="center" class="RecordRowP">
    	<tr align="center">
    		<td>&nbsp;&nbsp;<html:hidden name="subsetConfsetForm" property="view_scan"/>
    			<hrms:priv func_id="2306101">  
				<button name="confset" Class="mybutton" onclick="jumpconfset();">
				<bean:message key='org.autostatic.mainp.set.change.item'/></button>
				</hrms:priv>&nbsp;
				<button name="button_view" Class="mybutton" onclick="jumpdatascan();">
				<bean:message key='columns.archive.browse'/></button>
				 <hrms:tipwizardbutton flag="org" target="il_body" formname="subsetConfsetForm"/>  
			</td>
    	</tr>
    	</table>
</td></tr>
</table>

</html:form>

<script type="text/javascript"> 
valueonchage();
var yearnum=document.getElementById("yearnum").value; 
var yearset = parseInt(yearnum);

var monthnum=document.getElementById("monthnum").value;
var monthset = parseInt(monthnum);
function yincrease(){
	yearset = yearset+1;
	document.all.yearnum.value = yearset;
}
function ysubtract(){
	if(yearset<1991){
		document.all.yearnum.value = 1990;
	}else{
		yearset = yearset-1;
		document.all.yearnum.value = yearset;
	}
}


function mincrease()
{
	if(monthset>11){
		document.all.monthnum.value = monthset;
	}else{
		monthset = monthset+1;
		document.all.monthnum.value = monthset;
	}

}
function msubtract(){
	if(monthset<2){
		document.all.monthnum.value = 1;
	}else{
		monthset = monthset-1;
		document.all.monthnum.value = monthset;
	}
}



function valueonchage(){
	var valuesmonth = document.all.selectconfset.value;
	valuesmonth=valuesmonth.substring(0,1);
	if(valuesmonth==1){
		document.getElementById("months").style.display="block";
	}else{
		document.getElementById("months").style.display="none";
	}
}

function jumpconfset(){
    subsetConfsetForm.action="/org/autostatic/confset/subsetconfset.do?b_query=link&init=first";
    subsetConfsetForm.submit();
}
function jumpdatascan(){
	var monthvalue=document.getElementById("monthnum").value;
	if(monthvalue>12||monthvalue<1){
		alert(INPUT_CORRECT_MONTH);
		return;
	}
	var subset = document.getElementById("selectconfset").value;
    if(subset==undefined||trim(subset)==''){
		alert("请选择子集后再浏览！");
		return;
	}
	
	subsetConfsetForm.action="/org/autostatic/confset/datascan.do?b_scan=link&scan=view";
    subsetConfsetForm.submit();
}
function checkDate(){
	var  yearnum =  document.getElementById("yearnum").value;
	var valuesmonth = document.all.selectconfset.value;
	valuesmonth=valuesmonth.substring(0,1);
	var  monthnum = "";
	if(valuesmonth==1){
		monthnum = document.getElementById("monthnum").value;
		if(monthnum>12||monthnum<1){
			alert(INPUT_CORRECT_MONTH);
			return;
		}
	}else{
		monthnum = 0;
	}
	var  subset =  document.getElementById("subset").value;
	
	var hashvo=new ParameterSet();
    hashvo.setValue("yearnum",yearnum);
	hashvo.setValue("monthnum",monthnum);
	hashvo.setValue("fieldsetid",subset);
	var In_paramters="flag=1"; 
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:setscan,functionId:'1602010222'},hashvo);
	
}
function setscan(outparamters){
	var view_scan = document.getElementById("view_scan").value;
    var thecodeurl ="/org/autostatic/confset/setscandata.do?b_query=link&view_scan="+view_scan; 
    var return_vo= window.showModalDialog(thecodeurl, "", 
              "dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    
    if(return_vo!=null){
    	var checkdb = outparamters.getValue("checkdb");
    	var included = "";
		if(checkdb=='ok'){
 			var fieldsetid = document.getElementById("subset").value;
    		var thecodeurl="/org/autostatic/confset/included.do?b_included=link&fieldsetid="+fieldsetid; 
    		var popwin= window.showModalDialog(thecodeurl,"", 
        		"dialogWidth:400px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        	included = popwin;
   	 	}
    	
    	document.getElementById("view_scan").value = return_vo;
    	jindu();
    	subsetConfsetForm.action="/org/autostatic/confset/datascan.do?b_scan=link&scan=insert&included="+included;
    	subsetConfsetForm.submit();
    }
}
function jindu(){
	var x=document.body.scrollLeft+event.clientX-182;
    var y=document.body.scrollTop+event.clientY+25; 
	var waitInfo=eval("wait");
	waitInfo.style.top=y;
	waitInfo.style.left=x;
	waitInfo.style.display="block";
}	

function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}
function returnFirst(){
   		document.location="/general/tipwizard/tipwizard.do?br_orginfo=link";
	}
</script> 


  