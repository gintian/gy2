<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm" %>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy.mm.dd'</SCRIPT>
<% int tablesize=0; 
   int setsize=0;
ParameterForm parameterForm2=(ParameterForm)session.getAttribute("parameterForm2");
tablesize=Integer.parseInt(parameterForm2.getTableListSize());
setsize=Integer.parseInt(parameterForm2.getFieldSetListSize());
%>
<script type="text/javascript">
<!--
function allSelect()
{
   var value=document.getElementById("alls").checked;
   var table=document.getElementsByName("tables");
   var set=document.getElementsByName("setids");
   if(table)
   {
      for(var i=0;i<table.length;i++)
      {
           if(value)
           {
              table[i].checked=true;
           }
           else
           {
              table[i].checked=false;
           }
      }
  }
  if(set)
  {
      for(var i=0;i<set.length;i++)
      {
           if(value)
           {
              set[i].checked=true;
           }
           else
           {
              set[i].checked=false;
           }
      }
  }
}
function visibleTime(type)
{
   if(type=='0')
   {
      document.getElementById("sscop").readOnly=false;
       document.getElementById("escop").readOnly=false;
   }
   else
   {
      document.getElementById("sscop").readOnly=true;
       document.getElementById("escop").readOnly=true;
   }
}
function sub()
{
   if(confirm(CONFIRM_INIT_WILL_DELETE_ALLDATA+"！"))
   {
   var value=document.getElementById("alls").checked;
   var table=document.getElementsByName("tables");
   var set=document.getElementsByName("setids");
   var  tableStr="";
   var setStr="";
   var tnum=0;
   var snum=0;
   if(table)
   {
      for(var i=0;i<table.length;i++)
      {
           if(value)
           {
              tableStr+="/"+table[i].value;
           }
           else
           {
              if(table[i].checked)
              {
                 tableStr+="/"+table[i].value;
              }
           }
      }
  }
  if(set)
  {
      for(var i=0;i<set.length;i++)
      {
           if(value)
           {
              setStr+="/"+set[i].value;
           }
           else
           {
              if(set[i].checked)
              {
                 setStr+="/"+set[i].value;
              }
           }
      }
  }
  if(tableStr==''&&setStr=="")
  {
     alert(SELECT_TABLE_TO_INIT+"!");
     return;
  }
  var zero=document.getElementById("zero");
  var stime=document.getElementById("sscop").value;
  var etime=document.getElementById("escop").value;
  var type="1";
  if(zero.checked)
    type="0";
   if(type=='1'&&trim(stime).length<=0)
   {
      alert(SELECT_START_TIME+"！");
      return;
   }
     if(type=='1'&&trim(etime).length<=0)
   {
      alert(SELECT_END_TIME+"！");
      return;
   }
   if(type=='1')
   {
        var reg = /^(\d{4})((-|\.)(\d{1,2}))((-|\.)(\d{1,2}))$/;;
		if(!reg.test(stime))
		{
			alert(STARTTIME_FORMAT+"！");
			return;
		}
		if(!reg.test(etime))
		{
			alert(ENDTIME_FORMAT+"！");
			return;
		}
		var syear = stime.substring(0,4);
		var smonth=stime.substring(5,7);
		var sday=stime.substring(8);
		//if(!isValidDate(sday,smonth,syear))
		//{
		 //  alert(tableStr);
		 //  alert("起始时间的时间范围不正确,请注意年，月，日的有效性！");
		 //  return;
		//}
		var eyear = etime.substring(0,4);
		var emonth=etime.substring(5,7);
		var eday=etime.substring(8);
		//if(!isValidDate(eday, emonth, eyear))
		//{
		  // alert("结束时间的时间范围不正确,请注意年，月，日的有效性！");
		   //return;
		//}
		if(syear>eyear||(syear==eyear&&smonth>emonth)||(syear==eyear&&smonth==emonth&&sday>eday))
		{
		    alert(ENDTIME_LARGER_STARTTIME+"！");
		    return;
		}
   }
   var hashVo=new ParameterSet();
   hashVo.setValue("isAllDelete",value?"1":"0");
   hashVo.setValue("type",type);
   hashVo.setValue("tableStr",tableStr);
   hashVo.setValue("setStr",setStr);
   hashVo.setValue("stime",stime);
   hashVo.setValue("etime",etime);
   var request=new Request({method:'post',asynchronous:false,onSuccess:sub_ok,functionId:'3000000193'},hashVo);			
   }
}
function sub_ok(outparameters)
{
  var msg = outparameters.getValue("msg");
  if(msg=='0')
  {
     alert(DATA_INIT_SUCCESS+"！");
     return;
  }
  else
  {
     alert(DATA_INIT_FALITRUE);
     return;
  }
}
function checkSelect(Object){
	if(!Object.checked){
		document.getElementById("alls").checked=false;
	}
}
//-->
</script>
<html:form action="/hire/parameterSet/configureParameter/init_table_data">
<hrms:tabset name="pageset" width="80%" height="440" type="false"> 
 <hrms:tab name="tab1" label="hire.data.init" visible="true">
  <table width="100%"  height='100%'   align="center"> 
			<tr> <td  valign="top"  align='center'>
			<fieldset align="center">
			<legend><bean:message key="kq.init.select"/></legend>
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="DetailTable">

<tr>
<td>&nbsp;&nbsp;</td>
</tr>				
<tr>
<html:hidden name="parameterForm2" property="tableNames"/>
<td align="center" nowrap>

<fieldset align="center" style="width:90%">
<legend><bean:message key="hire.data.table"/></legend>
<div style="overflow:auto;width:380px;height:180px;" >
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
		<% int i=0; %>
		<logic:iterate id="element" name="parameterForm2" property="tableList" indexId="index">
		<% if(i==0&&i%2==0) {%>
		  <tr>
		<%} %>
		<td><input type="checkbox" name="tables" value="<bean:write name="element" property="table"/>" onclick="checkSelect(this);"/>&nbsp;
		<bean:write name="element" property="tablename"/></td>
		
		<% if((i+1)%2==0||i==(tablesize-1)){%>
		</tr>
		<%} %>
		<% i++; %>
		</logic:iterate>
		</table>
		</div>
</fieldset>

</td>
<td align="center" nowrap>
<fieldset align="center" style="width:90%">
<legend><bean:message key="hire.a01oth.set"/></legend>
<div style="overflow:auto;width:380px;height:180px;" >
		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
		<% int j=0; %>
		<logic:iterate id="element" name="parameterForm2" property="fieldSetList" indexId="index">
		<% if(j==0&&j%2==0) {%>
		  <tr>
		<%} %>
		<td><input type="checkbox" name="setids" value="<bean:write name="element" property="setid"/>" onclick="checkSelect(this);"/>&nbsp;
		<bean:write name="element" property="setdesc"/></td>
		
		<% if((j+1)%2==0||j==(setsize-1)){%>
		</tr>
		<%} %>
		<% j++; %>
		</logic:iterate>
		</table>
		</div>
</fieldset>
</td>
</tr>
<tr>
<td colspan="8" align="left">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input id="alls" type="checkbox" name="all" value="1" onclick="allSelect();"/><bean:message key="hire.alldata.initial"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center">
<fieldset>
<legend><bean:message key="kq.init.tscope"/></legend>
<div style="overflow:auto;width:760px;height:70px;">
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr>
	<td colspan="2">
	 <input type="radio" id="zero" name="timetype" value="0" onclick="visibleTime('1');" checked /><bean:message key="kq.init.allc"/>
	</td>
	</tr>
	<tr>
	<td>
	 <input type="radio" id="one" name="timetype" value="1" onclick="visibleTime('0');"><bean:message key="kq.init.tscope"/>&nbsp;&nbsp;
<bean:message key="label.from"/><input id="sscop" readOnly="true" type="text"  size="12" name="stime" extra="editor" id="editor2"  dropDown="dropDownDate"/>&nbsp;&nbsp;<bean:message key="label.to"/>
<input id="escop"  readOnly="true" type="text"  size="12" name="etime" extra="editor" id="editor2"  dropDown="dropDownDate"/>
	</td>
	</tr>
	<tr>
	<td>
	<bean:message key="hire.init.warn"/>&nbsp;&nbsp;
	</td>
	</tr>
	</table>
</div>
</fieldset>
</td>
</tr>
<tr>
	<td align="center"> <input type="button" class="mybutton" name="ok" value="<bean:message key="button.ok"/>" onclick="sub();"/></td>
	
</tr>
</table>
</hrms:tab>
</hrms:tabset>
</html:form>