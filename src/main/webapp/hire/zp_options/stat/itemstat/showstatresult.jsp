<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.zp_options.itemstat.HireStatForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/anychart/js/AnyChart.js"></script>
<script language="JavaScript" src="/ajax/basic.js"></script>
<script type="text/javascript">
<!--
function showpic(name){
	var allcheckbox=document.getElementsByTagName("INPUT");
	for(var i=0;i<allcheckbox.length;i++){
	if(allcheckbox[i].name==name)
		{
		if(allcheckbox[i].value=="on"){
		searchpic(name,"block");
		allcheckbox[i].value="false"
		}else{
		searchpic(name,"none");
		allcheckbox[i].value="on"
		}
		}
	}
	
}
function searchpic(name,command){
	var pic=$(name);
	pic[1].style.display=command;
}
function querytf(){
    var d1 = document.getElementById("editor1").value;
    var d2 = document.getElementById("editor2").value;
    if(trim(d1).length<=0||trim(d2).length<=0)
    {
        alert(APPOINT_COUNT_INTERVAL+"！");
        return;
    }
    if(checkDateTime(d1)&&checkDateTime(d2))
    {
    	hireStatForm.action="/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link";
    	hireStatForm.submit();
	}else{
	   alert(COUNT_TIME_FORMAT_WRONG+"!");
	}
}
function showpos(posvalue){
	
	if(posvalue.length>0)
	{
		var poslist=posvalue.split("/");
		document.getElementById("zp_fullname").value=poslist[1];
		hireStatForm.action="/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link&init=1&pos=menu&zp_pos_id="+poslist[0]+"&mark=no";
		hireStatForm.submit();
	}
	else{
		hireStatForm.action="/hire/zp_options/stat/itemstat/showstatresult.do?b_query=link&init=1&pos=menu&mark=no";
		hireStatForm.submit();
	}
}
//-->
</script>
<style>
<!--
.RecordRowLR {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}
-->
</style>
<%
int i=0;
int j=0;
List mylist=new ArrayList();
List statlist=new ArrayList();
HireStatForm hireStatForm=(HireStatForm)session.getAttribute("hireStatForm");
ArrayList retlist =(ArrayList)hireStatForm.getRetlist();
String schoolPosition=hireStatForm.getSchoolPsoition();
String columnName="e01a1.label";
if(schoolPosition!=null&&schoolPosition.length()>0)
    columnName="e01a1.major.label";
%>
<hrms:themes></hrms:themes>
<html:form action="/hire/zp_options/stat/itemstat/showstatresult"> 
<bean:define id="itemlists" name="hireStatForm" property="itemlist"/>
<logic:iterate id="elements" name="hireStatForm" property="itemlist">
	<bean:define id="fi" name="elements" type="com.hrms.hjsj.sys.FieldItem"/>
	<bean:define id="fidesc" name="fi" property="itemdesc"/>
	<bean:define id="fid" name="fi" property="itemid"/>
	<%mylist.add(fidesc);
	statlist.add(fid);
	
	%>
</logic:iterate>
<%
int len=statlist.size();
%>
<div style="padding:0 0 0 0px;">
<table align=center cellspacing="0" cellpadding="0" width="85%">
	
<tr>
<td>
<table  border="0" cellspacing="0" align="center" cellpadding="0"  width="100%">
<tr>
<td colspan="<%=len%>" class="TableRow"  style='border-bottom:0px;' nowrap>
	<bean:message key="hire.resume.report"/>&nbsp;&nbsp;<bean:write name="hireStatForm" property="zp_fullname"/>&nbsp;
	<html:hidden name="hireStatForm" property="zp_fullname"/>
</td>
</tr>
<tr>
<td colspan="<%=len%>" class="RecordRow" nowrap>
<table>
<tr>
<logic:equal value="menu" name="hireStatForm" property="pos">
<td>

<bean:message key="<%=columnName%>"/>&nbsp;&nbsp;<html:select name="hireStatForm" property="zp_pos_id" onchange='showpos(this.value)'>
			
			 
			 <html:optionsCollection property="zp_poslist" value="dataValue" label="dataName"/>
		</html:select>
</td>
<td>&nbsp;&nbsp;</td>
</logic:equal>
<td>		
<bean:message key="hire.count.time"/>&nbsp;
<bean:message key="label.from"/>
	<input type="text" name="startime"  extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor1"  dropDown="dropDownDate" value="${hireStatForm.startime}">
	<bean:message key="kq.init.tand"/>
	<input type="text" name="endtime"  extra="editor" style="width:100px;font-size:10pt;text-align:left" id="editor2"  dropDown="dropDownDate" value="${hireStatForm.endtime}">
</td>
<td>	<BUTTON name="tfquery" class="mybutton" onclick="querytf();"><bean:message key="hire.generate.chart"/></BUTTON></td>
</tr>
</table>
</td>
</tr>
<tr>
<td class="RecordRowLR"  colspan="<%=len%>" nowrap>
&nbsp;
<bean:message key="hire.count.type"/>&nbsp;&nbsp;
<logic:iterate id="el" name="hireStatForm" property="itemlist">

<bean:write name="el" property="itemdesc"/><input type="checkbox" name='<%=statlist.get(j).toString()%>' onclick='showpic(this.name)' value='false' checked='checked'/>&nbsp;

<%j++;%>
</logic:iterate>
</td>
</tr>
</table>

</td>
</tr>
<tr>
<td class="RecordRow" >

<table border='0' >
	<logic:iterate id="element" name="hireStatForm" property="retlist">
	<tr>
	<td  >
	
	<div id="<%=statlist.get(i).toString()%>"  style="display=block">
	<table>
		<tr>
		<td align="left" id='<%="pnl_"+i %>'>
		<% 
			ArrayList templist = (ArrayList)retlist.get(i);
			hireStatForm.setTempList(templist);
		%>
		
			<hrms:chart name="hireStatForm" title="<%=mylist.get(i).toString()%>" scope="session" legends="tempList" data=""  width="480" height="400" chart_type="20" chartpnl='<%="pnl_"+i %>'>
				</hrms:chart>
			</td>
		</tr>
		<tr>
			<td>
			<br/>
			</td>
		</tr>
		<tr>
			<td>
				<table  border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable" width="100%">
					<script type="text/javascript" language="javascript">
					var  sum=0;
					</script>
				<tr>
		
					<td align="left" class="TableRow" nowrap>
					<%=mylist.get(i).toString()%>
					</td>
		
				<logic:iterate id="statvalue" name="element">
					<td align="left" class="TableRow" nowrap>
					<bean:write name="statvalue" property="dataName"/>
					</td>
					</logic:iterate>
					<td align="left" class="TableRow" nowrap>
				<bean:message key="planar.stat.total"/>
				</td>
				</tr>
		
				<tr>
					<td class="RecordRow" nowrap>
				<bean:message key="hire.zp_option.weekly.apped"/>
					</td>
		
		<logic:iterate id="statvalue" name="element">
		<td class="RecordRow" nowrap>
		<bean:write name="statvalue" property="dataValue"/>
		<bean:define id="nnn" name="statvalue" property="dataValue"/>
		<script type="text/javascript" language="javascript">
			var nns="<%=nnn%>";
			if(nns!=""&&nns.length>0){
			var numbers=parseInt(nns);
			sum=sum+numbers;}
			</script>
		</td>
		</logic:iterate>
		<td class="RecordRow" nowrap>
		&nbsp;
		
		<script type="text/javascript" language="javascript">
		document.write(sum);
		</script>
		
		</td>
		</tr>
	
		<tr>
		<td class="RecordRow" nowrap>
		<bean:message key="jx.param.percent"/>
		</td>
		
		<logic:iterate id="statvalue" name="element">
		<td class="RecordRow" nowrap>
		
		<bean:define id="nnn" name="statvalue" property="dataValue"/>
		<script type="text/javascript" language="javascript">
			var nns="<%=nnn%>";
			if(nns!=""&&nns.length>0){
			var numbers=parseInt(nns);
			var percent=numbers/sum*100+"%";
			var nu=parseFloat(percent);
			document.write(nu.toFixed(2));
			}
			</script>
		</td>
		</logic:iterate>
		<td class="RecordRow" nowrap>
		&nbsp;--
		
		
		</script>
		
		</td>
		</tr>
	
		</table>
		</td>
		</tr>
		<tr>
		<td>
		<br/>
		</td>
		</tr>
		<%i++;%>
		</table>
		</div>
		</td>
		</tr>
	</logic:iterate>
</table>
	
	
</td></tr>	
	
	<tr>
	<td align="center">
	<logic:notEqual value="menu" name="hireStatForm" property="pos">
	<button class="mybutton" name="cls" onclick="window.close();" style="margin-top:5px;"><bean:message key="button.close"/></button>
	</logic:notEqual>
	</td>
	</tr>
</table>

	<table align="center" cellspacing="0" cellpadding="0">
	<tr>
	<td align="center">
	<logic:equal value="dxt" name="hireStatForm" property="returnflag">
	<hrms:tipwizardbutton flag="retain" target="il_body" formname="hireStatForm"/> 
	</logic:equal>
	</td>
	</tr>
	</table>
</div>
</html:form>

  	 


    