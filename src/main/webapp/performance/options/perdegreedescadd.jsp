<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.actionform.performance.options.PerDegreedescForm" %>
<script language="javascript" src="/js/common.js"></script>
<script language="javascript">
<% 
if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("close")){
	out.print("closeWin();");
}
%>

	function closeWin(){
		var thevo=new Object();
		thevo.flag="true";
		if(window.showModalDialog){
            parent.window.returnValue=thevo;
		}else {
	 		parent.window.opener.perde_add_ok(thevo);
		}
		parent.window.close();
	}
	
	function save()
	{
		var name = document.getElementById("itemname").value;	
		if(ltrim(rtrim(name)) == "")
		{
			alert("<bean:message key='jx.paramset.info1'/>");
		 	return;
		}
		if((document.getElementById("topscore")!=null) && (document.getElementById("bottomscore")!=null))
		{
			var topscore = document.getElementById("topscore").value;
			var bottomscore = document.getElementById("bottomscore").value;
			if(topscore!='' && bottomscore!='')
			{
				if(parseFloat(topscore)<parseFloat(bottomscore))
				{
					alert("<bean:message key='jx.paramset.info2'/>");
					return;
				}
			}
		}
		perDegreedescForm.action="/performance/options/perDegreedescAdd.do?b_save=link&opt=close"; 
		perDegreedescForm.target="_self";
		perDegreedescForm.submit();
	}		
	function isNumber(theData)
	{
  		var checkOK = "0123456789.";
 		var checkStr = theData;
  		var allValid = true;
  		var decPoints = 0;
  		var allNum = "";
  		if (checkStr=="")
  			return true;
  		for (i = 0;  i < checkStr.length;  i++)
	    {
    		ch = checkStr.charAt(i);
    		for (j = 0;  j < checkOK.length;  j++)
     	    if (ch == checkOK.charAt(j))
       			 break;
    		if (j == checkOK.length)
   		    {
  			    allValid = false;
   			    break;
  		    }
    		if (ch == ".")
    		{
     			allNum += ".";
     			decPoints++;
  			}
    	    else if (ch != ",")
      			allNum += ch;
  		}
 	 if (!allValid)
    	return false;
  	if (decPoints > 1) 
  	  return false;    
  	return true;
}

function IsDigit2(obj) 
{
		if((event.keyCode >= 46) && (event.keyCode <= 57) && event.keyCode!=47)
		{
			var values=obj.value;
			if((event.keyCode == 46) && (values.indexOf(".")!=-1))//有两个.
				return false;
			if((event.keyCode == 46) && (values.length==0))//首位是.
				return false;	
			return true;
		}
			return false;	
}	 
</script>
<%
		PerDegreedescForm myForm=(PerDegreedescForm)session.getAttribute("perDegreedescForm");	
		String flag = (String)myForm.getFlag();
		String isFirstOrLas=(String)myForm.getIsForL();
%>
<body>
<html:form action="/performance/options/perDegreedescAdd">
<table border="0" cellspacing="0" align="center" cellpadding="0">

			<tr>
						<td align="center" nowrap>
							<fieldset align="center" style="width:440;">
							<legend>
								等级项目维护	
							</legend>
		<table border="0" cellspacing="2" align="left" cellpadding="5">
			<input type="hidden" name="perDegreedescForm" property="perdegreedescvo.string(id)" />
			<input type="hidden" name="degreeId" value="${perDegreedescForm.degreeId}" />
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.degreeproname' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="itemname" property="perdegreedescvo.string(itemname)"/>
				</td>
			</tr>
			<%if(!flag.equals("4") && !flag.equals("5")) {%>
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.xishu' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="itemname" onkeypress="event.returnValue=IsDigit2(this);"  property="perdegreedescvo.string(xishu)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('系数为数值类型！');this.value='';this.focus();}"/>
				</td>
			</tr>
			<%} %>
			<tr>
				<td align="right" nowrap >
					<bean:message key='gz.columns.desc' />
				</td>
				<td align="left" nowrap >
					<html:textarea name="perDegreedescForm" styleId="itemdesc" property="perdegreedescvo.string(itemdesc)" cols="30" rows="6"></html:textarea>
				</td>
			</tr>
			<%if(flag.equals("0") || flag.equals("4") || flag.equals("5") || (flag.equals("2") && isFirstOrLas.equals("1") )|| (flag.equals("3") && isFirstOrLas.equals("0"))) {%>
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.markup' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="topscore" onkeypress="event.returnValue=IsDigit2(this);"  property="perdegreedescvo.string(topscore)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('分值上限为数值类型');this.value='';this.focus();}"/>
				</td>
			</tr>
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.markdown' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="bottomscore" onkeypress="event.returnValue=IsDigit2(this);"  property="perdegreedescvo.string(bottomscore)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('分值下限为数值类型');this.value='';this.focus();}"/>
				</td>
			</tr>
			<%} %>
			<%if(flag.equals("1")) {%>
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.bili' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="percentvalue" onkeypress="event.returnValue=IsDigit(this);"  property="perdegreedescvo.string(percentvalue)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('比例为数值类型');this.value='';this.focus();}"/>%
				</td>
			</tr>
			<%} %>
			<%if(flag.equals("2")) {%>
			<logic:equal name="perDegreedescForm" property="isForL" value="0">
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.bili' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="percentvalue" onkeypress="event.returnValue=IsDigit(this);"  property="perdegreedescvo.string(percentvalue)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('比例为数值类型');this.value='';this.focus();}"/>%
				</td>
			</tr>
			</logic:equal>
			<%} %>
			<%if(flag.equals("3")) {%>
			<logic:equal name="perDegreedescForm" property="isForL" value="1">
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.bili' />
				</td>
				<td align="left" nowrap >
					<html:text name="perDegreedescForm" styleId="percentvalue" onkeypress="event.returnValue=IsDigit(this);"  property="perdegreedescvo.string(percentvalue)" onblur="if(this.value!='' && !isNumber(this.value)){ alert('比例为数值类型');this.value='';this.focus();}"/>%
				</td>
			</tr>
			</logic:equal>
			<%} %>
			
			<logic:equal name="perDegreeForm" property="busitype" value="0">
			<logic:equal name="perDegreedescForm" property="itemNo" value="1">
			<tr>
				<td align="right" nowrap >
					<bean:message key='jx.param.limit' />
				</td>
				<td align="left" nowrap >
						<html:text name="perDegreedescForm" styleId="strict" property="perdegreedescvo.string(strict)"  onblur="if(this.value!='' && !isNumber(this.value)){ alert('限制为数值类型');this.value='';this.focus();}" /><%if(flag.equals("0") || flag.equals("2")) {%>%<%}%>
				</td>
			</tr>
			</logic:equal>
			</logic:equal>
			
		</table>
	</fieldset>
				</td>
			</tr>
			</table>
	<table border="0" cellspacing="0" align="center" cellpadding="5" width="100%">
		<tr>
			<td align="center" colspan="2">
				<input type="button" class="mybutton" value="<bean:message key='button.save' />" onClick="save();" />
				<input type="button" class="mybutton" value="<bean:message key='button.cancel' />" onClick="parent.window.close();">
			</td>
		</tr>
	</table>
</html:form>
</body>
<script type="text/javascript">
var aa=document.getElementsByTagName("input");
for(var i=0;i<aa.length;i++){
	if(aa[i].type=="text"){
		aa[i].className="inputtext";
	}
}
</script>
