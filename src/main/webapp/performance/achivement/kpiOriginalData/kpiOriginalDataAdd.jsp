<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView"%>
<hrms:themes />
<%
	    String css_url = "/css/css1.css";
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    if (userView != null)
	    {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
		    css_url = "/css/css1.css";
	    }
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	    
	   
//	    String status = (String) request.getParameter("status");
//	    System.out.println("cycle:"+cycle+"theyear:"+theyear+"themonth:"+themonth+"thequarter:"+thequarter+"start_date:"+start_date+"end_date:"+end_date );
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>

		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
		<hrms:themes />
	</HEAD>
	
<script language="javascript">	

function showLeftTime()
{
 	var now=new Date();
// 	myDate.getFullYear();
 	var year=now.getFullYear();
 	var month=now.getMonth();
 	var day=now.getDate();
 	var hours=now.getHours();
 	var minutes=now.getMinutes();
 	var seconds=now.getSeconds();
	document.getElementById('theyear').value=year;
}

function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}

function save()
{	
	var cycle=document.getElementById('cycle').value;
	var theyear=document.getElementById('theyear').value;
	var themonth=document.getElementById('themonth').value;
	var thequarter=document.getElementById('thequarter').value;	
	var thehalfyear=document.getElementById('thehalfyear').value;		
		    
	var thevo=new Object();
    thevo.flag="true";
    thevo.cycle=cycle;
    thevo.theyear=theyear;
    thevo.themonth=themonth;
    thevo.thequarter=thequarter;
    thevo.thehalfyear=thehalfyear;
    window.opener.newBuiltTarget_callBack(thevo);//haosl 20170227 兼容ff chrome..
	window.close();
}

function hiderow()
{
	document.getElementById('khyeardu').style.display='none';
	document.getElementById('khhalfyeardu').style.display='none';
	document.getElementById('khquarterdu').style.display='none';	
	document.getElementById('khmonth').style.display='none';		 			
}	

function afterSelect()
{
	var cycle = document.getElementById('cycle').value;
	hiderow();
	if(cycle=='0')
	{			
		document.getElementById('khyeardu').style.display='block';	
	}			
	if(cycle=='1')		
	{
		document.getElementById('khyeardu').style.display='block';
		document.getElementById('khhalfyeardu').style.display='block';	
	}
	if(cycle=='2')		
	{
		document.getElementById('khyeardu').style.display='block';
		document.getElementById('khquarterdu').style.display='block';	
	}	
	if(cycle=='3')		
	{			
		document.getElementById('khyeardu').style.display='block';
		document.getElementById('khmonth').style.display='block';	
	}				 	
}
	
function mincrease(obj_name) 
{
	var objs =document.getElementsByName(obj_name);      
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)>0)
		obj.value = (parseInt(obj.value)+1)+'';
}
function msubtract(obj_name) 
{
    var objs =document.getElementsByName(obj_name);      
  	if(objs==null)
  		return false;
  	var obj=objs[0];
  	if(parseInt(obj.value)>0)
		obj.value = (parseInt(obj.value)-1)+'';
}
		
</script>
	<body>
			<table border="0" cellspacing="0" align="center" cellpadding="2" width="340px;">
			<%if("hl".equals(hcmflag)){ %>	
			<tr>
				<td height='10' nowrap>
					&nbsp;
				</td>
			</tr>
			<%} %>
			<tr>
				<td align="center" nowrap>
					<fieldset align="center" style="width:340;">
						<legend>
								请设置考核周期
						</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="2">
					<tr style="display:block">
						<td align="right" nowrap>
							<bean:message key='jx.khplan.cycle' />
							
						</td>
						<td align="left" nowrap>
							<select id="cycle" size="1" onchange="afterSelect()"
								style="width:100px">
								<option value="0">
									<bean:message key='jx.khplan.yeardu' />
								</option>
								<option value="1">
									<bean:message key='jx.khplan.halfyear' />
								</option>
								<option value="2">
									<bean:message key='jx.khplan.quarter' />
								</option>
								<option value="3">
									<bean:message key='jx.khplan.monthdu' />
								</option>								
							</select>
						</td>
					</tr>
					<tr id="khyeardu" style="display:none">
						<td align="right">
		             		<bean:message key="jx.khplan.khyeardu"/>
		             		
		             	</td>
						<td align="left" nowrap>
							<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
		             			 <tr>		             				 
		              				 <td align="left"> 
		                				 <div class="m_frameborder">
		                 					<input type="text"  name="theyear" readonly="true" id="theyear" class="m_input inputtext" maxlength="15" size="10" style="width:100px">
		              					</div>
		            				 </td>
		         					<td>
		             				  <table border="0" cellspacing="2" cellpadding="0">
		                					<tr><td><button id="0_up" class="m_arrow" onmouseup="mincrease('theyear');">5</button></td></tr>
		                					<tr><td><button id="0_down" class="m_arrow" onmouseup="msubtract('theyear');">6</button></td></tr>
		            				  </table>
		          					</td>
		            			</tr>
		      				</table> 
						 </td>
					</tr>
					<tr id="khmonth" style="display:none">
						<td align="right" nowrap>
							<bean:message key='jx.khplan.khmonth' />
							:&nbsp;
						</td>
						<td align="left" nowrap>
							<select id="themonth" size="1" style="width:100px">
								<option value="01">
									<bean:message key='date.month.january' />
								</option>
								<option value="02">
									<bean:message key='date.month.february' />
								</option>
								<option value="03">
									<bean:message key='date.month.march' />
								</option>
								<option value="04">
									<bean:message key='date.month.april' />
								</option>
								<option value="05">
									<bean:message key='date.month.may' />
								</option>
								<option value="06">
									<bean:message key='date.month.june' />
								</option>
								<option value="07">
									<bean:message key='date.month.july' />
								</option>
								<option value="08">
									<bean:message key='date.month.auguest' />
								</option>
								<option value="09">
									<bean:message key='date.month.september' />
								</option>
								<option value="10">
									<bean:message key='date.month.october' />
								</option>
								<option value="11">
									<bean:message key='date.month.november' />
								</option>
								<option value="12">
									<bean:message key='date.month.december' />
								</option>
							</select>
						</td>
					</tr>
					<tr id="khhalfyeardu" style="display:none">
						<td align="right" nowrap>
							<bean:message key='jx.khplan.khhalfyeardu' />
							:&nbsp;
						</td>
						<td align="left" nowrap>
							<select id="thehalfyear" size="1" style="width:100px">
								<option value="01">
									<bean:message key='report.pigeonhole.uphalfyear' />
								</option>
								<option value="02">
									<bean:message key='report.pigeonhole.downhalfyear' />
								</option>
							</select>
						</td>
					</tr>
					<tr id="khquarterdu" style="display:none">
						<td align="right" nowrap>
							<bean:message key='jx.khplan.khquarterdu' />
							:&nbsp;
						</td>
						<td align="left" nowrap>
							<select id="thequarter" size="1" style="width:100px">
								<option value="01">
									<bean:message key='report.pigionhole.oneQuarter' />
								</option>
								<option value="02">
									<bean:message key='report.pigionhole.twoQuarter' />
								</option>
								<option value="03">
									<bean:message key='report.pigionhole.threeQuarter' />
								</option>
								<option value="04">
									<bean:message key='report.pigionhole.fourQuarter' />
								</option>
							</select>
						</td>					
					</tr>
					
				</table>
			</fieldset>
		</td>
	</tr>
</table>			
			
<table width="100%">
	<tr>
		<td align="center" style="height:20px">  					
			<input type="button" class="mybutton" value="<bean:message key='button.ok' />" onClick="save();" />				
			<input type="button" class="mybutton" value="<bean:message key='button.cancel' />" onClick="window.close();">
		</td>
	</tr>
</table>

<script>	
	
	showLeftTime();
	afterSelect();
		
</script>

	</body>
</html>


