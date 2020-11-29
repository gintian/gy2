<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.report.edit_report.EditReportForm,
                 com.hjsj.hrms.utils.PubFunc" %>
<%
    UserView userView = (UserView) request.getSession().getAttribute(
	WebConstant.userView);
	String username = userView.getUserName();
	
    String url="/system/home.do?b_query=link";
    String target="i_body";
    String tar=userView.getBosflag();
    if(tar=="hl4")
  	  target="il_body";
    String ver="";
    String home=request.getParameter("home");
    if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
    {
   		if("hcm".equals(tar)){
	   		url="/templates/index/hcm_portal.do?b_query=link";
   		}else{
   			url="/templates/index/portal.do?b_query=link";
   		}
   		target="il_body";
   		ver=request.getParameter("ver");
    }
    //add by wangchaoqun on 2014-9-25 begin
    EditReportForm editReportForm = (EditReportForm)session.getAttribute("editReportForm");
	String encryptParam = PubFunc.encrypt("pageNum=1&username=" + editReportForm.getUsername1() + "&tabid=" + editReportForm.getTabid());
    //add by wangchaoqun on 2014-9-25 end
%>
<script language="javascript" src="/js/page_options.js"></script>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
	<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
	<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
	<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
	<SCRIPT LANGUAGE=javascript>
	var isIE=(!!window.ActiveXObject || "ActiveXObject" in window);
	var tabid="${editReportForm.tabid}";
	var rows="${editReportForm.rows}";
	var cols="${editReportForm.cols}";
	var param_str="${editReportForm.param_str}";
	var status='<%=(request.getParameter("status"))%>';
	var reverseFlag="";
	var selfType="${editReportForm.narch}"   //报表类型
	var href = "${editReportForm.reportlisthref}"   //返回的url
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
	 //liuy 2015-1-22 bug6909 start
	function upload_picture(tabid,gridno,pathname){
   
		if(pathname==null)
		{
			alert(NOT_HAVE_RECORD);
			return;
		}    
	   pathname=getEncodeStr(pathname);
	    var thecodeurl ="/report/edit_report/pictureReport.do?b_query=link`tabid="+tabid+"`gridno="+gridno+"`pathname="+pathname+"`tablename=tpage"; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;     
	    var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:500px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");      
		//dataset.flushData();
		if(return_vo)
		{
	
			var img=document.getElementById(""+tabid+"_"+gridno);
			if(img==null)
				return;
			img.src=return_vo;
			pathname = return_vo.substring(return_vo.indexOf("filename=")+9);
			img.onclick =  function(){ 
	            upload_picture(tabid,gridno,pathname); 
	        }; 
		}
	}
	//liuy 2015-1-22 end
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
	  	document.editReportForm.action="<%=url%>";
	  	document.editReportForm.target='<%=target%>';
	  	document.editReportForm.submit();
	  }
	  function goback5()
	  {
				window.parent.location.href="/templates/index/hcm_mainpanel.do?b_query=link&module=34&menu_id=21&menu_target=il_menu&center_url="+'<%=PubFunc.encrypt("/general/inform/org/searchorgbrowse.do?b_query=link")%>'+"&center_target=il_body&menu_name=领导桌面&allname=";
	  }
	  
	  function goback2()
	  {
	    if(href.length>0){
	       while(href.indexOf("／")!=-1){
	           href = href.replace("／","/");
	       }
	       while(href.indexOf("？")!=-1){
	           href = href.replace("？","?");
	       }
	       while(href.indexOf("＝")!=-1){
	           href = href.replace("＝","=");
	       }
	       while(href.indexOf("＆")!=-1){
	           href = href.replace("＆","&");
	       }
	       document.editReportForm.action=href;
	   }else
	  	   document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print=5&ver=<%=ver%>&home=<%=home%>";
	  	    document.editReportForm.submit();
		//window.history.back(-1);
	  }
	   function goback4(print)
	  {
	   //add by wangchaoqun on 2014-9-19 begin
	   if(href.length>0){
	       while(href.indexOf("／")!=-1){
	           href = href.replace("／","/");
	       }
	       while(href.indexOf("？")!=-1){
	           href = href.replace("？","?");
	       }
	       while(href.indexOf("＝")!=-1){
	           href = href.replace("＝","=");
	       }
	       while(href.indexOf("＆")!=-1){
	           href = href.replace("＆","&");
	       }
	       document.editReportForm.action=href;
	   }
	   //add by wangchaoqun on 2014-9-25 end
	  else
	  	document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&print="+print;
	  	document.editReportForm.submit();
	  }
	  function goback3()
	  {
	  	<%
	  	 String temp="";
		 if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
   						temp="&ver=5";
	  	%>
	  	 if(href.length>0){
	  	    //add by wangchaoqun on 2014-9-26 begin
	  	     while(href.indexOf("／")!=-1){
	           href = href.replace("／","/");
	       }
	       while(href.indexOf("？")!=-1){
	           href = href.replace("？","?");
	       }
	       while(href.indexOf("＝")!=-1){
	           href = href.replace("＝","=");
	       }
	       while(href.indexOf("＆")!=-1){
	           href = href.replace("＆","&");
	       }
	       //add by wangchaoqun on 2014-9-26 end
	  	     document.editReportForm.action=href;
	  	 }
	  else
	  	document.editReportForm.action="/report/auto_fill_report/reportlist.do?b_query=link&sortId=-1&home=1<%=temp%>";
	  	document.editReportForm.submit();
	  
	  }
	var return_vo;
	function afreshGetData(){
		var url="/report/auto_fill_report/options.do?b_query2=link`operateObject=<%=request.getParameter("operateObject")%>`home=<%=request.getParameter("home")%>`code=<%=request.getParameter("code")%>";
		
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
		var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
		var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
		window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=600px,height=365px');
	}
	function returnVo(return_vo){
		  var dbpreStr = return_vo.dbpreStr;
	   	  document.getElementById("dbpreStr").value = dbpreStr;
	   	  var appdate = return_vo.appdate;
	   	  document.getElementById("appdate").value = appdate;
	   	  var start = return_vo.start;
	   	  document.getElementById("start").value = start;
	   	  //liuy 2015-2-13 end
	   	  document.editReportForm.action = window.location.href;  // 方式3 比方式2快1-2秒
	      document.editReportForm.submit();
	}
	/**谷歌不支持此写法*/ 
/*  function document.oncontextmenu() 
{
var e = window.event;
		
		if (e.target) targ = e.target;
		else if (e.srcElement) targ = e.srcElement;
			if (targ.name!=null&&targ.name.substring(0,1)=="a"&&targ.name.substring(1,2)!="a")  // input标签
			{
			
				setReverseID(targ.name);
			}


return false; 
}	  */
	 
/* 	window.document.oncontextmenu= function(){
		  var e = window.event;
		  if (e.target) targ = e.target;
		  	else if (e.srcElement) targ = e.srcElement;
		 if (targ.name!=null&&targ.name.substring(0,1)=="a"&&targ.name.substring(1,2)!="a")  // input标签
		  {
			setReverseID(targ.name);
		  }
		return false; 
	 }	   */

	     //设置反查标记
	function setReverseID(name)
	{

		var a_td;
		if(reverseFlag!=''&&reverseFlag!=' ')
		{
		    a_td=eval("a"+reverseFlag);
			a_td.style.border='1px solid #000000';
			
			var startRow=parseInt(reverseFlag.substring(1,reverseFlag.indexOf("_")));
			a_td.style.borderRightWidth=CellArray[startRow];
		}
		reverseFlag=name;
		a_td=eval("a"+reverseFlag);
		a_td.style.border='2px solid green'
	
	}
		//反查
	function revertData()
	{
		
		if(reverseFlag==''||reverseFlag==' ')
		{
			alert("请双击需要反查的单元格，再进行反查！");
			return;
		}
		var gridVo=eval("document.editReportForm."+reverseFlag);	
		if(gridVo.value==''||gridVo.value==' ')
			return;		
		// 选择人员库
		var dbpreStr = document.getElementById("dbpreStr").value;
		var appdateObj = document.getElementById("appdate");
		var  appdate='';
		if(appdateObj)//xiegh add  bug:30945 原因：起止时间有为空的情况，如果为空，则需在后台交易类中到数据库中查询
			appdate = appdateObj.value;
			
		var start='';
		var startObj = document.getElementById("start");
		if(startObj)
			start	=startObj.value;
			
		var reportTabid = '${editReportForm.tabid}';
		var username = '${editReportForm.username1}';
		newwindow = window.open('/report/edit_report/editReport.do?b_reverseFind=find&dbpreStr='+dbpreStr+'&appdate='+appdate+'&start='+start+'&gridName=' +reverseFlag+ "&count="+gridVo.value+"&encryptParam=<%=encryptParam%>",
		'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,width=530,height=550,resizable=no');			
		
//		iframe_url="/general/query/common/iframe_query.jsp?src=/report/report_analyse/reportanalyse.do?b_queryBase=find`isclose=0`reportTabid="+reportTabid+"`gridName="+reverseFlag;
// 		return_vo= window.showModalDialog(iframe_url, 'trainClass_win1', 
//      				"dialogWidth:450px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:yes");		
//		var	newwindow=window.open('/report/report_analyse/reportanalyse.do?b_queryBase=find','glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,width=400,height=400,resizable=yes');
		var info='';
		
	//	var strurl="/report/edit_report/editReport.do?b_reverseFind=find`pageNum=1`gridName=" +reverseFlag+ "`tabid="+tabid+"`count="+gridVo.value;		 
	//	var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
//		if(return_vo){
		
//		newwindow=window.open('/report/edit_report/editReport.do?b_reverseFind=find&pageNum=1&gridName=' +reverseFlag+ "&tabid="+tabid+"&count="+gridVo.value+"&dbname="+return_vo,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,width=400,height=400,resizable=yes');
//		}
	//	var year_value= window.showModelessDialog(iframe_url, info, "dialogWidth:400px; dialogHeight:300px;resizable:yes;center:yes;scroll:yes;status:yes");			
	}

	// 导出Excel 
	function exportExcel()
	{
		var hashvo = new ParameterSet();
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("unitcode" ,"${editReportForm.unitcode}");
	    hashvo.setValue("username" ,"${editReportForm.username1}");
	    //liuy 2015-1-28 6843：报表汇总/编辑报表/文件/批量导出：查阅48号表，导出excel，导出后查看数据都没有了 start
	    //hashvo.setValue("operateObject","1");
	    hashvo.setValue("operateObject","${editReportForm.operateObject}");
	    //liuy 2015-1-28 end    
		var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'03030000025'},hashvo);			
	}
	 	 
	function outFile(outparamters)
	{	 	 
		var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	
	}
	  
	</script>  
	<link href="/css/css1_report.css" rel="stylesheet" type="text/css">
<html:form action="/report/edit_report/editReport">
<%-- liuy 2015-2-13 6807：cs扫描库设置为本报表设置，bs自动取数/反查：对1号表取数后反查，反查不对 start --%>
<input type="hidden" id="dbpreStr" name="dbpreStr" value="${editReportForm.dbpreStr}" />
<input type="hidden" id="appdate" name="appdate" value="${editReportForm.appdate}" />
<input type="hidden" id="start" name="start" value="${editReportForm.start}" />
<%-- liuy 2015-2-13 end --%>
<!--start add by xiegh on date 20180201  -->
<% 
if("1".equals(request.getParameter("showbuttons"))){
%>
<table width="80%"  border='0' cellspacing='0'  align='center' cellpadding='1' style='position:absolute;top:7;left:4;height:30'> 
  <tr  valign='middle' align="left"> 
  <td> 
  
  <% 
   if((request.getParameter("print")==null||!request.getParameter("print").equals("1"))&&(!("1").equals(request.getParameter("checkFlag"))&&!("2").equals(request.getParameter("checkFlag")))){
  if(request.getParameter("ctrollflag")!=null&&!request.getParameter("ctrollflag").equals("1")){
  %>
<input type="button" name="b_add" value="<bean:message key="hmuster.label.reGetData"/>" class="mybutton" onclick="afreshGetData()">
  <%
  }
   %>
							<input type="button" name="b_query"
								value=" <bean:message key="report.reportlist.reverse"/> "
								onclick='revertData()' class="mybutton" >
								
<%}%>

	<input type="button" name="outExcel" value="<bean:message key="edit_report.importexcel"/>" class="mybutton" style="" onclick="exportExcel()">
<%-- 
/* 领导桌面在bi和hr界面不需要返回界面  */
if("hcm".equals(tar) && !"0".equals(request.getParameter("showreturn"))){%><!-- add by xiegh ondate 20180205 如果showbuttons=1且是通过领导桌面进入则是通过showreturn来控制返回按钮，否则是通过flag参数来控制 -->
		<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback5()" />
 &nbsp;</td> </tr>
</table>} --%>
<% } else if(!"0".equals(request.getParameter("showbuttons"))){ %><!-- showbuttons -->
<table width="80%"  border='0' cellspacing='0'  align='center' cellpadding='1' style='position:absolute;top:7;left:4;height:30'> 
  <tr  valign='middle' align="left"> 
  <td> 
  
  <% 
   if((request.getParameter("print")==null||!request.getParameter("print").equals("1"))&&(!("1").equals(request.getParameter("checkFlag"))&&!("2").equals(request.getParameter("checkFlag")))){
  if(request.getParameter("ctrollflag")!=null&&!request.getParameter("ctrollflag").equals("1")){
  %>
   <hrms:priv func_id="290106,290100">
<input type="button" name="b_add" value="<bean:message key="hmuster.label.reGetData"/>" class="mybutton" onclick="afreshGetData()">
</hrms:priv>
  <%
  }
   %>
 	<hrms:priv func_id='290107,290204'>
							<input type="button" name="b_query"
								value=" <bean:message key="report.reportlist.reverse"/> "
								onclick='revertData()' class="mybutton" >
								</hrms:priv>
								
<%}%>

<hrms:priv func_id="290108">
	<input type="button" name="outExcel" value="<bean:message key="edit_report.importexcel"/>" class="mybutton" style="" onclick="exportExcel()">
</hrms:priv>

<%}%> <%--  
if(!"bi".equals(tar)){/* 报表管理在bi界面不需要返回界面  */
if(request.getParameter("flag")==null){
	if(request.getParameter("print")!=null){
	%>
	<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback4('<%=request.getParameter("print") %>')" />
	<% 
	}else{
	if(request.getParameter("menuflag")!=null){
  %>
<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback2()" />
<%} }}else if(request.getParameter("flag").equals("2")){ %>
<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback3()" />
<% } %>
 &nbsp;</td> </tr>
</table>
<% } --%>

<%  

/* 领导桌面在bi和hr界面不需要返回界面   showreturn 参数单独控制返回按钮  showbuttons不在控制 wangb 20180717*/
if("hcm".equals(tar) && request.getParameter("showreturn")!=null&&!"0".equals(request.getParameter("showreturn"))){%><!-- add by xiegh ondate 20180205 如果showbuttons=1且是通过领导桌面进入则是通过showreturn来控制返回按钮，否则是通过flag参数来控制 -->
		<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback5()" />
 &nbsp;</td> </tr>
</table>
<%}else if(!"0".equals(request.getParameter("showbuttons"))){ 
	if(!"bi".equals(tar)){/* 报表管理在bi界面不需要返回界面  */
		if(request.getParameter("flag")==null){
			if(request.getParameter("print")!=null){
%>
				<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback4('<%=request.getParameter("print") %>')" />
		 <% }else{
				if(request.getParameter("menuflag")!=null){
  		 %>
					<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback2()" />
			  <%}%>
	      <%}%>
	  <%}else if(request.getParameter("flag").equals("2")){%>
	  		<input type='button' value=' <bean:message key="button.return"/> ' class="mybutton" onclick="goback3()" />
	  <%}%>
  <%}%>
   &nbsp;</td> </tr>
	</table>
<%}%>

<!--end add by xiegh on date 20180201  -->
<style>
#idDIV{
	margin-top: 5px;
	margin-left: -5px;
}
</style>
	${editReportForm.htmlCode}
	
	<input type="hidden" name='tabid' value="${editReportForm.tabid}" />	
	<input type="hidden" name='rows' value="${editReportForm.rows}" />
	<input type="hidden" name='cols' value="${editReportForm.cols}" />
	<input type="hidden" name='reportResultData' value='' />
	
</html:form>

<script language="javascript">
	

			//自动计算
			function autoAccount(iteName,npercent)
			{
		
			}
		
	  		var CellArray = new Array(rows);	//恢复样式目前只恢复borderRightWidth
	  		var endcols=cols-1;
	  		var pageResult=new Array(rows);		//页面值得二维数组		
			for(var a=0;a<rows;a++)
			{
				var c_object =eval("aa"+a+"_"+endcols);
				if(c_object.currentStyle){
				CellArray[a]=c_object.currentStyle['borderRightWidth'];
				}
		
			
			}
			


</script>


