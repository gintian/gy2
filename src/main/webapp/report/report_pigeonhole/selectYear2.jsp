<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*" %>
<html>

<HEAD>
<META HTTP-EQUIV='pragma' CONTENT='no-cache'> 
<TITLE>
报表数据归档
</TITLE>

</HEAD>

 <link href="/css/css1.css" rel="stylesheet" type="text/css">
 <script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript">
var info=parent.infos;  //  narch
function submit_value()
{
	var a_object=eval("document.f1.reportType");
	var num=0;
	for(var i=0;i<a_object.length;i++)
	{
		if(a_object[i].checked==true)
			num=a_object[i].value;
	}
	
	var a_value=new Array();
	a_value[0]=num;								//报表类型
	var a_select=eval("document.f1.year"+num);
	a_value[1]=a_select.value;			//年
	if(num>2)   
	{
		var a_count=eval("document.f1.count"+num);
		a_value[2]=a_count.value;			//次
	
	}
	if(num==6){
		var a_count=eval("document.f1.week6");
		a_value[3]=a_count.value;			//周
	}
	
	returnValue=a_value;
	parent.year_value=a_value;
	closeWindow();	
}


function closeWindow()
{	
	if(parent.Ext.getCmp("selectArchiveUnit")){
		parent.Ext.getCmp("selectArchiveUnit").close();
	}else
	    window.close();	

}


function showDescription()
{
	var a_object=eval("document.f1.reportType");
	var num=0;
	for(var i=0;i<a_object.length;i++)
	{
		if(a_object[i].checked==true)
			num=a_object[i].value;
	}
	for(var i=1;i<7;i++)
	{
		if(i!=num)
		{
			var a=eval("a"+i);	
			a.style.display="none"; 		
		}
	}
	var a=eval("a"+num);	
	a.style.display="block"; 	

}

function init()
{
	var a_object=eval("document.f1.reportType");
	for(var i=1;i<7;i++)
	{
		if(i!=info)
		{
			var a=eval("a"+i);	
			a.style.display="none"; 		
		}
		a_object[i-1].disabled=true;
	}
	var a=eval("a"+info);	
	a.style.display="block"; 	
	a_object[info-1].checked=true;

}

</script>
<%
	GregorianCalendar d=new GregorianCalendar();
	int current_year=d.get(Calendar.YEAR);
	int current_month=d.get(Calendar.MONTH)+1;                						
%>
<body onload='init()'  >

<base id="mybase" target="_self">
<hrms:themes />
<form name='f1'  >
	<table  width="390px" border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 
		        <tr>  
		         <td width="390px" valign="top">

						<fieldset align="left" style="width:390px;height: 230px;">
    							 <legend ><bean:message key="report.pigeonholeType"/></legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td width="30%" height="30" >
			                						<INPUT type='radio' name='reportType' value='1'    onclick='showDescription()'><bean:message key="report.pigeonhole.generalReport"/>
			                						
			                					</td>
			                					<td width='70%'>
			                						<div id='a1'>
			                						<select name='year1' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>		              
			                							<% } %>
			                						</select>&nbsp;<bean:message key="datestyle.year"/>
			                						</div>
			                					
			                					</td>
		                					
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='2' onclick='showDescription()' > <bean:message key="report.pigeonhole.yearReport"/>
		                      					</td>
		                      					<td width='70%' >
		                      						<div id='a2'>
		                      						<select name='year2' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>		              
			                							<% } %>
			                						</select>&nbsp;<bean:message key="datestyle.year"/>
			                						</div>
			                					
		                      					</td> 
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='3' onclick='showDescription()' > <bean:message key="report.pigeonhole.halfyearReport"/>
		                      					</td>
		                      					<td width='70%' >
		                      						<div id='a3'>
		                      				    	<select name='year3' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>		              
			                							<% } %>
			                						</select>&nbsp;<bean:message key="datestyle.year"/>
			                						&nbsp;
			                						<select name='count3' >		        
			                							<option value='1'  ><bean:message key="report.pigeonhole.uphalfyear"/>&nbsp;&nbsp;</option>		              
			                							<option value='2'  ><bean:message key="report.pigeonhole.downhalfyear"/>&nbsp;&nbsp;</option>
			                						</select>
			                						</div>
		                      					</td> 
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='4' onclick='showDescription()' ><bean:message key="report.pigionhole.quarterReport"/>
		                      					</td>
		                      					<td width='70%' >
		                      						<div id='a4'>
		                      						<select name='year4' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>		              
			                							<% } %>
			                						</select>&nbsp;<bean:message key="datestyle.year"/>
			                						&nbsp;
			                						<select name='count4' >		        
			                							<option value='1'  ><bean:message key="report.pigionhole.oneQuarter"/></option>		              
			                							<option value='2'  ><bean:message key="report.pigionhole.twoQuarter"/></option>
			                							<option value='3'  ><bean:message key="report.pigionhole.threeQuarter"/></option>
			                							<option value='4'  ><bean:message key="report.pigionhole.fourQuarter"/></option>
			                						</select>
			                						</div>
		                      					</td> 
		                      				</tr>
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='5' onclick='showDescription()' ><bean:message key="report.pigionhole.monthReport"/>
		                      					</td>
		                      					<td width='70%' >
		                      						<div id='a5'>
		                      						<select name='year5' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</option>		              
			                							<% } %>
			                						</select>&nbsp;<bean:message key="datestyle.year"/>
			                						&nbsp;
			                						<select name='count5' >		        
			                							<option value='1'  ><bean:message key="date.month.january"/>&nbsp;&nbsp;</option>		              
			                							<option value='2'  ><bean:message key="date.month.february"/>&nbsp;&nbsp;</option>
			                							<option value='3'  ><bean:message key="date.month.march"/>&nbsp;&nbsp;</option>
			                							<option value='4'  ><bean:message key="date.month.april"/>&nbsp;&nbsp;</option>
			                							<option value='5'  ><bean:message key="date.month.may"/>&nbsp;&nbsp;</option>
			                							<option value='6'  ><bean:message key="date.month.june"/>&nbsp;&nbsp;</option>
			                							<option value='7'  ><bean:message key="date.month.july"/>&nbsp;&nbsp;</option>
			                							<option value='8'  ><bean:message key="date.month.auguest"/>&nbsp;&nbsp;</option>
			                							<option value='9'  ><bean:message key="date.month.september"/>&nbsp;&nbsp;</option>
			                							<option value='10'  ><bean:message key="date.month.october"/>&nbsp;&nbsp;</option>
			                							<option value='11'  ><bean:message key="date.month.november"/>&nbsp;&nbsp;</option>
			                							<option value='12'  ><bean:message key="date.month.december"/>&nbsp;&nbsp;</option>
			                						</select>
			                						</div>
		                      					</td> 
		                      				</tr>
		                      		
		                      		
		                      				<tr>
		                      					<td width='30%' height="30" >
		                      					<INPUT type='radio' name='reportType' value='6' onclick='showDescription()' ><bean:message key="report.pigionhole.weekReport"/>
		                      					</td>
		                      					<td width='70%' >
		                      						<div id='a6'>
		                      						<select name='year6' >
			                							<% for(int i=current_year-10;i<current_year+10;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_year?"selected":" ")%>  ><%=i%></option>		              
			                							<% } %>
			                						</select><bean:message key="datestyle.year"/>
			                						
			                						<select name='count6' >		        
			                							<% for(int i=1;i<13;i++){	%>
			                									<option value='<%=i%>' <%=(i==current_month?"selected":" ")%>  ><%=i%></option>		              
			                							<% } %>
			                						</select><bean:message key="columns.archive.month"/>
			                						
			                						<select name='week6' >		        
			                							<option value='1'  ><bean:message key="performance.workdiary.one.week"/></option>		              
			                							<option value='2'  ><bean:message key="performance.workdiary.two.week"/></option>
			                							<option value='3'  ><bean:message key="performance.workdiary.three.week"/></option>
			                							<option value='4'  ><bean:message key="performance.workdiary.four.week"/></option>
			                							<option value='5'  ><bean:message key="performance.workdiary.five.week"/></option>
			                						</select>
			                						
			                						</div>
		                      					</td> 
		                      				</tr>
		                      			</table>
		                      		</fieldset>
		                  	</td>
		                  </tr>
		                  <tr><td align="center" style="padding-top:10px">
		                  <html:button  styleClass="mybutton" property="b_addfield" onclick="submit_value()" >
				            		     <bean:message key="button.ok"/>
					            </html:button>
					            <html:button  styleClass="mybutton" property="b_addfield" onclick="closeWindow()" >
				            		 <bean:message key="button.cancel"/>
					            </html:button>&nbsp;
		                  </td></tr>
		                </table>


</form>

</body>
</html>
