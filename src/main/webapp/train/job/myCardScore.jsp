<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.ykcard.CardTagParamForm"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.hjsj.hrms.valueobject.ykcard.CardTagParamView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	String isMobile = request.getParameter("isMobile");
	String _return = request.getParameter("return");
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase();
	if (agent.indexOf("firefox") != -1)
		browser = "Firefox";
	else if (agent.indexOf("chrome") != -1)
		browser = "Chrome";
	else if (agent.indexOf("safari") != -1)
		browser = "Safari";
	CardTagParamForm cardtagparamform = (CardTagParamForm) session.getAttribute("cardTagParamForm");
	ArrayList yearlist = (ArrayList) cardtagparamform.getFormHM().get("yearlist");
	CardTagParamView cardparam = (CardTagParamView)cardtagparamform.getCardparam();
	String styear = (String) cardtagparamform.getFormHM().get("styear");
	String year = (String)cardtagparamform.getFormHM().get("year");
	if(year == null || year.equalsIgnoreCase(""))
		cardparam.setCyear(Integer.parseInt(styear));
%>
<%
	if ("1".equals(isMobile)) {
%>
<script type="text/javascript" src="/phone-app/jquery/jquery-3.5.1.min.js"></script>
<script type="text/javascript" src="/phone-app/jquery/rpc_command.js"></script>
<%
	}
%>
<style>
.mybuttons{
	border:1px solid #c5c5c5 ;
	padding:2px 4px 2px 4px ;
	background-color:#f9f9f9 ;
	font:12px/16px 微软雅黑, 宋体, tahoma, arial, verdana, sans-serif;
}
</style>
<script type="text/javascript">
function validatedate()
{
	<%if ("1".equals(isMobile)) {%>	
	var _queryflagtype=document.getElementById("queryflagtypeid");
	if(_queryflagtype.value==2){
		var str=document.getElementById("starttimeid").value;
		if(str.length>0&&!chechdate(str)){
			alert("请输入正确的日期!如:2010-1-25");
			return false;
		}
		str=document.getElementById("endtimeid").value;
		if(str.length>0&&!chechdate(str)){
			alert("请输入正确的日期!如:2010-1-25");
			return false;
		}
	}	
	<%}%>
	return true;
}

function chechdate(str){
		var reg = /^(\d{1,4})-(\d{1,2})-(\d{1,2})$/; //创建正则表达式校验时间对象
		var r = str.match(reg);
		if(r==null)
			return false;
		else
		{
			var d= new Date(r[1], --r[2],r[3]);
			if(d.getFullYear()!=r[1])
				return false;
			if(d.getMonth()!=r[2])
				return false;
			if(d.getDate()!=r[3])
				return false;
		}
		return true;
}
</script>
<body <%if (!"1".equals(isMobile)) {%>oncontextmenu="return false" ondragstart="return false" onselectstart ="return false" onselect="return false" oncopy="return false" onbeforecopy="return false" onmouseup="return false"<%}%>>         
<html:form action="/general/card/MyIntegral" >
	<input type="hidden" name="isMobile" value="<%=isMobile%>" />
	<input type="hidden" name="return" value="<%=_return%>" />
	<table>
				<tr>
					<td style="padding-left: 55px;" nowrap>
						<html:select name="cardTagParamForm" property="cardparam.cyear" styleId="year">
							<html:optionsCollection name="cardTagParamForm" property="formHM.yearlist" label="dataName" value="dataValue" />
						</html:select>
						<bean:message key="train.job.year"/>
						&nbsp;&nbsp;
						<input type="button" class="mybuttons" style="margin-bottom: 0px;" value='<bean:message key="button.query"/>' onclick="search();">
					</td>
				</tr>

			<tr width="100%">
			<td>
               <hrms:ykcard name="cardTagParamForm" property="cardparam" nid="${userView.a0100}" b0110="${cardTagParamForm.b0110}" nbase="${cardTagParamForm.pre}" tabid="${cardTagParamForm.tabid}" cardtype="${cardTagParamForm.cardtype}" disting_pt="javascript:screen.width" userpriv="selfinfo" havepriv="1" istype="0" infokind="1" fieldpurv="1" queryflag="0" isMobile="<%=isMobile %>"  browser="<%=browser %>" returnvalue="<%=_return %>" bizDate="${cardTagParamForm.bizDate}"/>
		    </td>
		</tr>
	</table>
	<%
		if ("1".equals(isMobile) && "0".equals(_return)) {
	%>
	   <input style="display: none" type="submit"  id="androidSubmit" value="ss">
	<%
		}
	%>
</html:form>
</body>
<script language='javascript'>
function showPDF(outparamters)
{
	<%if ("1".equals(isMobile)) {%>
	    var map=JSON.parse(outparamters);
		if(map.succeed){
			var url=map.url;
			window.location.target="_blank";
			window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+url;
		}	
	<%} else {%>
		var url=outparamters.getValue("url");
	    var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+url,"pdf");	
	<%}%>
}

function excecutePDF()
{
	<%if ("1".equals(isMobile)) {%>
        var map = new HashMap();
        var tab_id="${cardTagParamForm.tabid}";  
        if(tab_id=="")
        {
           tab_id="${re_tabid}";  
        } 
        map.put("nid","${userView.a0100}");
        map.put("b0110","${cardTagParamForm.b0110}");
        map.put("flag","${cardTagParamForm.flag}");
        map.put("tabid",tab_id);
        map.put("cardid","1");
        if(${cardTagParamForm.cardparam.queryflagtype}==1)
        {
           map.put("cyear","${cardTagParamForm.cardparam.cyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==3)
        {
           map.put("cyear","${cardTagParamForm.cardparam.csyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==4)
        {
           map.put("cyear","${cardTagParamForm.cardparam.cyyear}");
        }        
        map.put("userpriv","selfinfo");
        map.put("istype","0");        
        map.put("cmonth","${cardTagParamForm.cardparam.cmonth}");
        map.put("season","${cardTagParamForm.cardparam.season}");
        map.put("ctimes","${cardTagParamForm.cardparam.ctimes}");
        map.put("cdatestart","${cardTagParamForm.cardparam.cdatestart}");
		map.put("cdateend","${cardTagParamForm.cardparam.cdateend}");
		map.put("querytype","${cardTagParamForm.cardparam.queryflagtype}");
		map.put("infokind","1");
		map.put("userbase","${cardTagParamForm.userbase}");
	    map.put("pre","${cardTagParamForm.pre}");
		map.put("fieldpurv","1");
	    map.put("exce","PDF"); 
	    var platform=navigator.platform;
		map.put("platform",platform); 
	    Rpc({functionId:'07020100005',success:showPDF},map); 
	<%} else {%>
		var hashvo=new ParameterSet();
        var tab_id="${cardTagParamForm.tabid}";  
        if(tab_id=="")
        {
           tab_id="${re_tabid}";  
        } 
        hashvo.setValue("nid","${userView.a0100}");
        hashvo.setValue("b0110","${cardTagParamForm.b0110}");
        hashvo.setValue("flag","${cardTagParamForm.flag}");
        hashvo.setValue("tabid",tab_id);
        hashvo.setValue("cardid","1");
        if(${cardTagParamForm.cardparam.queryflagtype}==1)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.cyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==3)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.csyear}");
        }else if(${cardTagParamForm.cardparam.queryflagtype}==4)
        {
           hashvo.setValue("cyear","${cardTagParamForm.cardparam.cyyear}");
        }        
        hashvo.setValue("userpriv","selfinfo");
        hashvo.setValue("istype","0");        
        hashvo.setValue("cmonth","${cardTagParamForm.cardparam.cmonth}");
        hashvo.setValue("season","${cardTagParamForm.cardparam.season}");
        hashvo.setValue("ctimes","${cardTagParamForm.cardparam.ctimes}");
        hashvo.setValue("cdatestart","${cardTagParamForm.cardparam.cdatestart}");
		hashvo.setValue("cdateend","${cardTagParamForm.cardparam.cdateend}");
		hashvo.setValue("querytype","${cardTagParamForm.cardparam.queryflagtype}");
		hashvo.setValue("infokind","1");
		hashvo.setValue("userbase","${cardTagParamForm.userbase}");
	        hashvo.setValue("pre","${cardTagParamForm.pre}");
		hashvo.setValue("fieldpurv","1");
	    var In_paramters="exce=PDF";  
	    var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'07020100005'},hashvo);
	<%}%>
}
function changTabid(obj)
{
    var tabid=obj.value;
    var a0100="${cardTagParamForm.a0100}";
    var pre="${cardTagParamForm.pre}";
    var b0110="${cardTagParamForm.b0110}";
    var flag="${cardTagParamForm.flag}";  
    document.cardTagParamForm.pageid.value=0;
    document.cardTagParamForm.action="/ykcard/employeeselfcard.do?b_querypage=link&a0100="+a0100+"&flag="+flag+"&pre="+pre+"&b0110="+b0110+"&tabid="+tabid;
    document.cardTagParamForm.submit();
} 
function returnback(){
	document.cardTagParamForm.action="/phone-app/app/emolument.do?b_init=link&flag=infoself";
    document.cardTagParamForm.submit();
}

<%if ("1".equals(isMobile) && "0".equals(_return)) {%>
Android.setPer_year_list_str(document.getElementById('per_year_list_strid').value);
<%}%>

function doAndroidParam(queryflagtype,cyyear,cyear,cmonth,cdatestart,cdateend,csyear,season){
	document.getElementsByName("cardparam.queryflagtype")[0].value=queryflagtype;
	document.getElementsByName("cardparam.cyyear")[0].value=cyyear;
	document.getElementsByName("cardparam.cyear")[0].value=cyear;
	document.getElementsByName("cardparam.cmonth")[0].value=cmonth;
	document.getElementsByName("cardparam.cdatestart")[0].value=cdatestart;
	document.getElementsByName("cardparam.cdateend")[0].value=cdateend;
	document.getElementsByName("cardparam.csyear")[0].value=csyear;
	document.getElementsByName("cardparam.season")[0].value=season;
	document.getElementById("androidSubmit").click();
}
function search(){
	var year = document.getElementById("year").value;
	cardTagParamForm.action="/general/card/MyIntegral.do?b_showcard=link&year="+year;
	cardTagParamForm.submit();
}
</script>   
<%
	cardtagparamform.setBizDate("");
%>