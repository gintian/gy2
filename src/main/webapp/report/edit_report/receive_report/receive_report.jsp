<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.valueobject.UserView"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript" src="/js/constant.js"></script>
 <script language="JavaScript" src="/js/validate.js"></script>
<%
	UserView userview = null;
		String returnvalue =	request.getParameter("returnvalue")==null?"":request.getParameter("returnvalue");
		String editflag = request.getParameter("editflag")==null?"":request.getParameter("editflag");
		if(editflag!=null&&!editflag.equals("1"))
		userview = (UserView) request.getSession().getAttribute(
			WebConstant.userView);
%>

<script>

    function mysubmit() { 
        var fileEx = receiveReportForm.file.value;
		// 56005  V76报表管理：接受报盘，文件类型错误，上传时进度条和提示同时存在，点击提示的确定后还是提示的界面		
		//这里不需要重复验证
			/* var flag=validateUploadFilePath(fileEx);
			if(!flag){
				return;
			} */
        if(fileEx == ""){
           var ssss = eval("sss")
		ssss.style.display ="block";
  		var waitInfo=eval("wait");	   
		waitInfo.style.display="none";
        	alert(REPORT_INFO19+"!");
        	return false;
        }else{
        	if(fileEx.indexOf(".rpx") > -1 )
	        {
    		return true;
	        } else {
	        	  var ssss = eval("sss")
					ssss.style.display ="block";
  					var waitInfo=eval("wait");	   
					waitInfo.style.display="none";
	            alert(REPORT_INFO20+"！");
	            return false;
	        }
        }
       // if(fileEx.indexOf(".xml") > -1 || fileEx.indexOf(".dat") > -1)
        //{
      //      return true;
       // } else {
       //     alert("文件类型不正确，请选择xml或dat类型的文件！");
       //     return false;
      //  }
      //判断操作用户所负责的单位与上报盘里的单位是否一致
    
    }

 
</script>
<style>
.inputT{
    font-size: 12px;
    font-family:微软雅黑;
    height:24px;
    line-height:22px;
    border: 1px solid #C4D8EE;
    margin:0px; 
    padding:0;
    *padding:0 0 3px 1px;
}
.noBorder input{
outline:0;
filter:chroma(color=#000000);
}
</style>
<html>
	<link href="/css/css1.css" rel="stylesheet" type="text/css">
	<hrms:themes />
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<title>HRPWEB3</title>
	</head>
	
	<body>
		<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td valign="top">
				<%if(editflag!=null&&editflag.equals("1")) {%>
					<form name="receiveReportForm" method="post" action="/report/edit_report/receive_report.do?editflag=1&b_query2=2&returnvalue=<%=returnvalue %>" enctype="multipart/form-data" onsubmit="return mysubmit()">
				<%}else{ %>
					<form name="receiveReportForm" method="post" action="/report/edit_report/receive_report.do?returnvalue=<%=returnvalue %>" enctype="multipart/form-data" onsubmit="return mysubmit()">
				<%} %>		
						<input type="hidden" name="editflag" value=<%=editflag %> >
						<input type="hidden" name="b_query3" value="2" >
						<br>
						<br>
						<br>
						<table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
							<tr height="20">
								<!-- td width="10" valign="top" class="tableft"></td>
								<td width="130" align=center class="tabcenter">
									<bean:message key="reportManager.receiveReport" />
								</td>
								<td width="10" valign="top" class="tabright"></td>
								<td valign="top" class="tabremain" width="500"></td -->
								<td align="left" colspan="4" class="TableRow"><bean:message key="reportManager.receiveReport"/>&nbsp;</td> 
							</tr>
							<tr>
								<td colspan="4" class="framestyle3">

									<table border="0" cellpmoding="0" cellspacing="5" class="DetailTable" cellpadding="0" align="center">

										<tr>
											<td height="70">
												<input name="file" type="file" size="50" class="inputT">
											</td>
										</tr>
									</table>
								</td>
							</tr>


							<tr class="list3" align="center">
							
								<td colspan="4" id ="sss"  style="padding-top: 5px;" class="noBorder">
								<%if(editflag!=null&&editflag.equals("1")) {%>
								<input type="submit" name="b_query2"  onclick='valide()' value="<bean:message key="button.ok" />" class="mybutton" >
								<%}else{ %>
								<input type="submit" name="b_query" autofocus="" onclick='valide()' value="<bean:message key="button.ok" />" class="mybutton" >
								<%} %>
								 	<!-- 导航菜单进来增加返回按钮 xiaoyun 2014-5-19 start -->
								   <% 
								    if(userview!=null && userview.getBosflag()!=null && returnvalue.equals("dxt"))
									{
									%>
									<!-- <input type="button" name="b_delete" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('report','il_body','receiveReportForm')"> -->
									<!-- hrbreturn方法会使表单提交，此时返回的页面没有缓存，点击另一页面再返回时调用history.back()方法页面会提示网页已过期。所以此时用window.open()方法 -->
									<input type="button" name="b_delete" value="<bean:message key="button.return"/>" class="mybutton"  onclick="window.open('/general/tipwizard/tipwizard.do?br_report=link','il_body');"> 
									<%} %>
									<!-- 导航菜单进来增加返回按钮 xiaoyun 2014-5-19 end -->
								</td>
								
							</tr>
						</table>
					</form>
				</td>
			</tr>
		</table>
		<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>

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
	</body>
</html>
<script type="text/javascript">
function valide(){
var ssss = eval("sss")
ssss.style.display ="none";
  var waitInfo=eval("wait");	   
waitInfo.style.display="block";
 
}
 <%
  if(request.getParameter("b_query2")!=null&&request.getParameter("b_query2").equals("2"))
  {
  %>
    var waitInfo=eval("wait");	   
waitInfo.style.display="none";
  var editvalide= "${receiveReportForm.editvalide}";
  if(editvalide=="1"){
  if(confirm("操作用户所负责的单位与上报盘里的单位不一致！点击\"确定\"按钮将覆盖操作用户负责的报表")){
   receiveReportForm.action="/report/edit_report/receive_report.do?b_query2=1&editflag=1&returnvalue=<%=returnvalue %>";
   receiveReportForm.submit();
   var ssss = eval("sss")
ssss.style.display ="none";
  var waitInfo=eval("wait");	   
waitInfo.style.display="block";
  }
  }else{
  var clew= "${receiveReportForm.clew}";
    var waitInfo=eval("wait");	   
waitInfo.style.display="none";
  alert(clew);
  }
   <%
  }else{
%>
   var ssss = eval("sss")
ssss.style.display ="block";
 <%
  }
%>
</script>
