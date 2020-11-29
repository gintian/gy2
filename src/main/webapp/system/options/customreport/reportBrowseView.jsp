<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	
    String url="/system/home.do?b_query=link";
    String target="i_body";
    String tar=userView.getBosflag();
    if(tar=="hl4")
  	  target="il_body";
    
    if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
    {
   		url="/templates/index/portal.do?b_query=link";
   		target="il_body";
    }
	
%>
<script language="javascript" src="/js/page_options.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<SCRIPT LANGUAGE=javascript>
	var tabid=${customReportForm.tabid};
	var rows=${customReportForm.rows};
	var cols=${customReportForm.cols};
	var param_str="${customReportForm.param_str}";
	var status='<%=(request.getParameter("status"))%>';
	var reverseFlag="";
	var selfType="${customReportForm.narch}"   //报表类型

	function go_left(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    if(temp_str1!=0)
	    {
	    	var next_item;
	    	var next_item1=temp_str1-1;
	    	var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    	next_item1 = next_item + next_item1;
	    	var new_object=eval(next_item1);	    	
	    	if(new_object!=null&&new_object.type!='hidden')
	    		new_object.focus();
	    }
	  }
	  
	function go_right(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(post+1);
	    var next_item;
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm."+temp_str.substring(0,post+1);
	    next_item1 = next_item + next_item1;
	    var new_object=eval(next_item1);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();
	 
	  }
	  
	  
	function go_up(ite){
	    var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)-1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	  
	  
	function go_down(ite){
	   var temp_str = ite.name;
	    var post = temp_str.indexOf("_");
	    var temp_str1 = temp_str.substring(1,post);
	    var next_item1=parseInt(temp_str1)+1;
	    var next_item = "document.editReportForm.a"+next_item1+temp_str.substring(post);
	    var new_object=eval(next_item);
	    if(new_object!=null&&new_object.type!='hidden')
	    	new_object.focus();

	  }
	  
	  function setReverseID(name)
	  {
	  
	  }
	  
	  function check_data(iteName,npercent)
	  {
	  
	 
	  }
	  
	  function check_data2(iteName,intlen,npercent)
	  {
	  
	  }

	  

	  
	  //报表上报——1
	  function appeal_1()
	  {
	 
	  }
	  
	  function goback()
	  {
	  	// window.opener = null;
  		parent.window.close();
		  parent.parent.winClose();
	  }
	  
	  function goback2()
	  {
	  	document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print=5&checkFlag=0";
	  	document.editReportForm.submit();
	  }
	   function goback4(print)
	  {
	  	document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print="+print+"&checkFlag=0";
	  	document.editReportForm.submit();
	  }
	  function goback3()
	  {
	  	<%
	  	 String temp="";
		 if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
   						temp="&ver=5";
	  	%>
	  	document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&home=1<%=temp%>";
	  	document.editReportForm.submit();
	  
	  }

	</script>  
	<link href="/css/css1_report.css" rel="stylesheet" type="text/css">
<html:form action="/report/edit_report/editReport">	
<table   border='0' cellspacing='0'  align='center' cellpadding='1' style='position:absolute;top:1;left:10;height:30'> 
  <tr  valign='middle' align='center'> <td> 
<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback()" />
 &nbsp;</td> </tr>
</table>

	${customReportForm.htmlCode}
	
	<input type="hidden" name='tabid' value="${customReportForm.tabid}" />	
	<input type="hidden" name='rows' value="${customReportForm.rows}" />
	<input type="hidden" name='cols' value="${customReportForm.cols}" />
	<input type="hidden" name='reportResultData' value='' />
	
</html:form>
<style>

	.textCls{
		line-height:17px !important;
	}

</style>
<script language="javascript">
	

			//自动计算
			function autoAccount(iteName,npercent)
			{
		
			}
			if(!getBrowseVersion() || getBrowseVersion() =='10'){
				var p = document.getElementsByTagName("p");
				for(var i = 0 ; i < p.length ; i++){
					p[i].setAttribute("class","textCls");
				}
			}
		

</script>


