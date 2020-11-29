<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView,
				 com.hjsj.hrms.utils.PubFunc,
				 java.util.Calendar,
				 java.text.SimpleDateFormat,
				 com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<script language="javascript" src="/performance/kh_plan/examPlan.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<%
	    String css_url = "/css/css1.css";
	    UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	    if (userView != null)
	    {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
		    css_url = "/css/css1.css";
	    }
	    String cycle = (String) request.getParameter("cycle");
	    String theyear = (String) request.getParameter("theyear");
	    String themonth = (String) request.getParameter("themonth");
	    String thequarter = (String) request.getParameter("thequarter");
	    String start_date = (String) request.getParameter("start_date");
	    String end_date = (String) request.getParameter("end_date");
	    String status = (String) request.getParameter("status");
	    //System.out.println("cycle:"+cycle+"theyear:"+theyear+"themonth:"+themonth+"thequarter:"+thequarter+"start_date:"+start_date+"end_date:"+end_date );
	    
	    Calendar calendar = Calendar.getInstance();		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");     
		String nowDateTime=formatter.format(calendar.getTime());  // 得到当前时间
		
		calendar.add(Calendar.YEAR, -1);    //得到上一年
		String lastDateTime=formatter.format(calendar.getTime());  
%>
<HTML>
	<HEAD>
		<TITLE></TITLE>
		<hrms:themes />
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
	</HEAD>
<script language="javascript">	

function replaceAll(str, sptr, sptr1)
{
	while (str.indexOf(sptr) >= 0)
	{
   		str = str.replace(sptr, sptr1);
	}
	return str;
}
function windowclose() {
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        parent.parent.window.getCycle_Ok(null);
    }
}
function save()
{	
	var thequarter=document.getElementById('thequarter').value;
	var cycle=document.getElementById('cycle').value;
	var theyear=document.getElementById('theyear').value;
	var themonth=document.getElementById('themonth').value;
	var start_date=document.getElementById('start_date').value;
	var end_date=document.getElementById('end_date').value;
	var thehalfyear=document.getElementById('thehalfyear').value;
	if(cycle=='7' && (start_date=='' || end_date==''))		
	{
		alert("请设置考核区间！");
		return;	
	}	
	if(start_date!='' && end_date!='' && start_date > end_date)
	{
		alert("<bean:message key='jx.khplan.timeInfo'/>");
		return;
	}
	if(cycle=='2' && thequarter=='')		
	{
		alert("请设置考核的季度！");
		return;	
	}

	var thevo=new Object();
    thevo.flag="true";
    thevo.cycle=cycle;
    thevo.theyear=theyear;
    thevo.themonth=themonth;
    thevo.thequarter=thequarter;
    thevo.start_date=start_date;
    thevo.end_date=end_date;
    thevo.thehalfyear=thehalfyear;
    parent.window.returnValue=thevo;
    if(window.showModalDialog) {
        parent.window.close();
    }else{

        parent.parent.window.getCycle_Ok(thevo);
        Ext.getCmp('cycle_win').close();
    }
}
function hiderow()
{
	document.getElementById('khyeardu').style.display='none';
	document.getElementById('khhalfyeardu').style.display='none';
	document.getElementById('khquarterdu').style.display='none';	
	document.getElementById('khmonth').style.display='none';
	document.getElementById('khqujian').style.display='none';		
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
	if(cycle=='7')
	{		
		document.getElementById('khqujian').style.display='block';	
		<%if (StringUtils.isEmpty(start_date) || StringUtils.isEmpty(end_date)){
			start_date = lastDateTime;	
			end_date = nowDateTime;
		}%>
	}		 	
}	

</script>

	<body>
		<form>
			<table border="0" cellspacing="0" align="center" cellpadding="2">
			<tr>
				<td align="center" nowrap>
					<fieldset align="center" style="width: 280px;">
					<legend>
						考核计划周期设置
					</legend>
				<table border="0" cellspacing="0" align="center" cellpadding="2">
					<tr style="display: block">
						<td align="right" nowrap><bean:message key='jx.khplan.cycle' /> :&nbsp;
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
								<option value="7">
									<bean:message key='jx.khplan.indefinetime' />
								</option>
							</select>
						</td>
					</tr>
					<tr id="khyeardu" style="display:none">
						<td align="right">
		             		<bean:message key="jx.khplan.khyeardu"/> :&nbsp;
		             	</td>
						<td align="left">
							<table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
		             			 <tr>
		              				 <td align="left">
		                				 <div class="m_frameborder">
		                 					<input type="text"  name="theyear" readonly="true" id="theyear" class="inputtext" maxlength="15" size="10">
		              					</div>
		            				 </td>
		         					<td>
		             				  <table border="0" cellspacing="2" cellpadding="0" style="margin-top:-2px">
		                					<tr><td><button id="0_up" type="button" class="m_arrow" onmouseup="mincrease('theyear');">5</button></td></tr>
		                					<tr><td><button id="0_down" type="button" class="m_arrow" onmouseup="msubtract('theyear');">6</button></td></tr>
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
								<option value="1">
									<bean:message key='report.pigeonhole.uphalfyear' />
								</option>
								<option value="2">
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
					<tr id="khqujian" style="display:none">
						<td align="right" nowrap>
							<bean:message key='jx.khplan.khqujian' />
							:&nbsp;
						</td>
						<td align="left" nowrap>
							<input type="text" id="start_date" extra="editor"
								style="width:100px;font-size:10pt;text-align:left"
								dropDown="dropDownDate" class="inputtext">
							至
							<input type="text" id="end_date" extra="editor"
								style="width:100px;font-size:10pt;text-align:left"
								dropDown="dropDownDate" class="inputtext">
						</td>
					</tr>
				</table>
			</fieldset>
						</td>
			</tr>
			</table>

			<table width="100%">
				<tr>
					<td align="center" style="height:35px">
						<%
							    if (status.equals("0"))
							    {
						%>
						<input type="button" class="mybutton"
							value="<bean:message key='button.ok' />" onClick="save();" />
						<%
						}
						%>
						<input type="button" class="mybutton"
							value="<bean:message key='button.cancel' />"
							onClick="windowclose();">
					</td>
				</tr>
			</table>
			<script>
		document.getElementById('cycle').value='<%=cycle%>';
		document.getElementById('theyear').value='<%=theyear%>';
		document.getElementById('themonth').value='<%=themonth%>';
		document.getElementById('start_date').value=replaceAll('<%=start_date%>','.','-');
		document.getElementById('end_date').value=replaceAll('<%=end_date%>','.','-');
		if('<%=thequarter%>'=='1' || '<%=thequarter%>'=='2')
			document.getElementById('thehalfyear').value='<%=thequarter%>';
		else if('<%=thequarter%>'!='')
			document.getElementById('thequarter').value='<%=thequarter%>';
		afterSelect();
	</script>
		</form>
	</body>
</html>


