<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.Calendar"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<hrms:themes />
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/common.js"></script>
<script language="JavaScript" src="./gz_setinfor.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<style>
.mybutton{
    border:1px solid #c5c5c5;
    background:#f9f9f9;
    height:23px;
    line-height:20px;
    color:#414141;
    padding:0 10px 0 10px;
    width:70px;
}
</style>
<html:form action="/gz/gz_analyse/gz_setinfor">
<table width="100%" border="0">
  <tr> 
    <td height="150" colspan="2">
   <%--  <fieldset align="center">
	  <legend><bean:message key='kq.init.tscope'/></legend>  --%>
    <table width="99%" border="0" style="position:relative;bottom:10px;">
        <tr> 
          <td width="20%" height="25" id="radio_1"> 
          <html:radio name="gzReportForm" property="selecttime" onclick="disabled_radio('')" value="1"/>
          <bean:message key='gz.gz_analyse.gz_setinfor.all'/>
          </td>
          <td width="85%">&nbsp;</td>
        </tr>
        <tr> 
          <td height="15" id="radio_2">
          <html:radio name="gzReportForm" property="selecttime" onclick="disabled_radio('viewyear')" value="2"/>
          <bean:message key='gz.gz_analyse.gz_setinfor.year'/>
          </td>
          <td height="25"><div id="viewyear"></div></td>
        </tr>
        <tr> 
          <td height="25" id="radio_3">
          	<html:radio name="gzReportForm" property="selecttime" onclick="disabled_radio('viewmonth')" value="3"/>
          	<bean:message key='gz.gz_analyse.gz_setinfor.month'/>
          </td>
          <td height="25"><div id="viewmonth"></div></td>
        </tr>
        <tr> 
          <td height="25" id="radio_4">
          	<html:radio name="gzReportForm" property="selecttime" onclick="disabled_radio('viewnumber')" value="4"/>
          	<bean:message key='gz.gz_analyse.gz_setinfor.certain'/>
          </td>
          <td height="25"><div id="viewnumber"></div></td>
        </tr>
        <tr> 
          <td id="radio_5">
          	<html:radio name="gzReportForm" property="selecttime" onclick="disabled_radio('viewinterval')" value="5"/>
          	<bean:message key='gz.gz_analyse.gz_setinfor.interval'/>
          </td>
          <td><div id="viewinterval"></div></td>
        </tr>
      </table>
      <!-- </fieldset> -->
    </td>
  </tr>
  <tr> 
       <td height="25" style="padding-left: 12px;">
       	<span style="padding-left: 18px;">汇总</span>
        <select name="summary" style="width:246px;position:absolute;left: 100px;">
        	<option value="0">不汇总</option>
        	<option value="1">按人员汇总</option>
        	<option value="2">按人员及归属日期汇总</option>
        </select>
        </td>
  </tr>
  <tr>
        <td height="30" style="padding-left: 12px;">
         <span style="padding-left: 18px;">筛选</span>
	    <hrms:optioncollection name="gzReportForm" property="conditionslist" collection="list"/>
		<html:select name="gzReportForm" property="conditions" onchange="change(this,'${gzReportForm.tabid}')" style="width:246px;position:absolute;left: 100px;">
			<html:options collection="list" property="dataValue" labelProperty="dataName"/>
		</html:select>
	   </td>
  </tr>
  <tr>
  	<td rowspan="2" align="center" valign="bottom" style="padding-top: 15px;"> 
      <table width="100%" border="0" style="border-top:1px solid #cecece;position: absolute;left: -1px;width:450px;">
          <td height="40" style="width: 75px;padding-left: 101px;">
              <input name="reportData" type="button" id="reportData"
               onclick="openTable(1);"
                value="<bean:message key='hmuster.label.reGetData'/>" Class="mybutton">
          </td>
          <td height="40" style="width: 75px;"><input name="nextData" type="button" id="nextData" onclick="openTable(0);" 
           value="<bean:message key='gz.gz_analyse.gz_setinfor.lastdata'/>" Class="mybutton"></td>
          <td height="40"><input name="cancellation" type="button" id="cancellation" value="取&nbsp;&nbsp;&nbsp;&nbsp;消" Class="mybutton" onclick="setinfo_close();"></td>
      </table>
    </td>
  </tr>
  <%-- <tr> 
    <td width="50%" height="70" align="center"> 
    <fieldset align="center">
	  <legend><bean:message key='gz.gz_analyse.gz_setinfor.rows'/></legend>
      <table width="100%" border="0">
        <tr> 
          <td height="30" width="100">
         	<html:radio name="gzReportForm" property="isAutoCount" onclick="selectPageRow(1);" value="0"/>
         	<bean:message key='report.parse.body.isAutorow'/>
         </td>
         <td>&nbsp;</td>
        </tr>
        <tr> 
          <td height="30"> 
          	<html:radio name="gzReportForm" property="isAutoCount" onclick="selectPageRow(2);" value="1"/>
          	<bean:message key='hmuster.label.user_define'/>
          </td>
          <td>
          	<div id="pageRowsView" style="display:none">
          		<html:text name="gzReportForm" property="pageRows" onkeypress="event.returnValue=IsDigit();"  maxlength="3" size="3"/>
          	</div>
          </td>
        </tr>
      </table>
       </fieldset>
    </td>
    <td width="50%" align="center" height="70" >
    <fieldset align="center">
	  <legend><bean:message key='kq.report.print'/></legend>
     <table width="100" border="0">
        <tr> 
          <td height="30">
          	<input type="checkbox" name="zeroPrint">
          	<bean:message key='hmuster.label.zero_print'/>
          </td>
        </tr>
        <tr> 
          <td height="30">
          	<input type="checkbox" name="printGrid" checked>
          	<bean:message key='hmuster.label.print_compart'/>
          </td>
        </tr>
      </table>
      </fieldset>
      </td>
  </tr> --%>
</table> 
<html:hidden name="gzReportForm" property="gz_module"/>
<html:hidden name="gzReportForm" property="tabid"/>
<html:hidden name="gzReportForm" property="dbname"/>
<html:hidden name="gzReportForm" property="titlename"/>
<html:hidden name="gzReportForm" property="category"/>
<html:hidden name="gzReportForm" property="archive"/>
</html:form>
<script>
//置灰操作
function setDsiabled(id, flag) {
	var node = document.getElementById(id).childNodes;
	for(var i = 0; i < node.length; i++) {
		node[i].disabled = flag;
	}
}
var list = new Array();
var str=getSelectYear(0);
document.getElementById("viewyear").innerHTML=str;

str="";
str+=getSelectYear(0);
str+=getSelectMonth(0);
document.getElementById("viewmonth").innerHTML=str;

str="";
str+=getSelectYear(0);
str+=getSelectMonth(0);
str+=getSelectNumber();
document.getElementById("viewnumber").innerHTML=str;

str="";
str+=getSelectYear2();
str+=getSelectMonth(0);
str+=getSelectYear(0);
str+=getSelectMonth(1);
document.getElementById("viewinterval").innerHTML=str;

//第一次进来的时候只显示年，所以置灰其他的
setDsiabled("viewmonth", true);
setDsiabled("viewyear", true);
setDsiabled("viewnumber", true);
setDsiabled("viewinterval", true);

list.push("viewmonth");
list.push("viewyear");
list.push("viewnumber");
list.push("viewinterval");

function disabled_radio(id) {
	for(var i = 0; i < list.length; i++) {
		var list_ = list[i];
		if(list_ == id) {
			setDsiabled(id, false);
		}else {
			setDsiabled(list_, true);
		}
	}
}


</script>
