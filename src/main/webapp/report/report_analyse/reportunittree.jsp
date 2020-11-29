<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization,
				 com.hjsj.hrms.utils.ResourceFactory,com.hjsj.hrms.actionform.report.report_analyse.ReportAnalyseForm"%>
<%@ page import="com.hrms.frame.dao.RecordVo,com.hjsj.hrms.utils.PubFunc"%>

<%String css_url = "/css/css1.css";
			UserView userView = (UserView) session
					.getAttribute(WebConstant.userView);
			if (userView != null) {
				css_url = userView.getCssurl();
				if (css_url == null || css_url.equals(""))
					css_url = "/css/css1.css";
			}
	
		//	System.out.println("uc="+uc+"  "+userView.isSuper_admin());
			String backdate ="";
			String nodehead="";
				ReportAnalyseForm reportAnalyseForm=(ReportAnalyseForm)session.getAttribute("reportAnalyseForm");
				if(request.getParameter("backdate")!=null){
					backdate=(String)request.getParameter("backdate");
									
				}else{
					if(reportAnalyseForm!=null){
						 backdate = (String)reportAnalyseForm.getBackdate();
						 reportAnalyseForm.getFormHM().put("backdate","");
					}
					
				}
				request.setAttribute("backdate","");
			//获取当前用户对应的填报单位编码，生成SQL语句的where部分
			TTorganization ttorganization = new TTorganization();
			ttorganization.setValidedateflag("1");
			ttorganization.setBackdate(backdate);
			RecordVo selfVo =null;
			if(userView.getStatus()==4&&userView.getS_userName()!=null)
			{
					selfVo = ttorganization.getSelfUnit3(userView.getS_userName());
					
			}
			else{
			
				selfVo = ttorganization.getSelfUnit3(userView.getUserName());
				
				}
			
			String uc ="";
			if(selfVo!=null)
			 uc = selfVo.getString("unitcode");
			 if(backdate!=null&&backdate.length()>9){
			uc+="&backdate="+backdate;
			}
				String params = PubFunc.encrypt("where parentid='"
						+ selfVo.getString("unitcode")
						+ "' and unitcode <> parentid ");
				String action = "/report/report_analyse/reportanalyse.do?b_query=link&code="
						+ uc;
				nodehead = selfVo.getString("unitname");
				if (userView.isSuper_admin()) {
					nodehead = ResourceFactory.getProperty("report.appealUnit");
					params = PubFunc.encrypt("where parentid = unitcode");  //update by wangchaoqun on 2014-9-24
					action = "/report/report_analyse/reportanalyse.do?b_query=link";
					//action="";
				}
				
				String bosflag = "";
				if(userView != null)
				{
				  bosflag = userView.getBosflag();
				}	
%>
<HTML>
	<HEAD>
		<link href="<%=css_url%>" rel="stylesheet" type="text/css">
		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<hrms:themes />
		<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
		<script language="javascript" src="/js/constant.js"></script> 
		<SCRIPT LANGUAGE=javascript>
		var openerConfig;
		function backDate2(){
            openerConfig = ["_blank"];
		    var theUrl = "/report/report_analyse/reportunittree.do?br_backdate=link";
            var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
            var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
			//兼容谷歌 换用open wangbs 20190318
            window.open(theUrl,'_blank','height=360, width=470,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
			// var  backdate=showModalDialog('/report/report_analyse/reportunittree.do?br_backdate=link','_blank','dialogHeight:300px;dialogWidth:350px;scroll:no;center:yes;help:no;resizable:no;status:no;');
		}
		function formHistoryDateReturn(backdate){
            if(backdate&&backdate.length>9) {
                reportAnalyseForm.action="/report/report_analyse/reportunittree.do?b_query2=link&backdate="+backdate;
                reportAnalyseForm.target="il_body"
                reportAnalyseForm.submit();
            }else{
                return false;
            }
		}
		function initunitcode(){
			var unitcode="{reportAnalyseForm.unitcode}";
			var obj=root.childNodes[0];
		}
</SCRIPT>
<style> 
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>   
	</HEAD>
	<body topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
	<form name="reportAnalyseForm" method="post" action="/report/report_analyse/reportanalyse.do">
		<table border="0" id="tableID" cellpadding="0" cellspacing="0" class="mainbackground" style="width:100%;">
		<tr style="padding-right: 0px;padding-top: 0px;">
			<td>
					 <table border="0" cellpadding="0" cellspacing="0" width="100%">
            		  <tr valign="middle" class="toolbar" style="height: 40px" >
              		   <td style="padding-left: 10px;" width="100%">
							<a href="###" onclick="return backDate2();"><img src="/images/quick_query.gif" title="历史时点查询" border="0" align="middle"></a>
					   </td>
					  </tr>
					</table>               
       		</td>
		</tr>
			<tr>
				<td valign="top">
    				<div id="treemenu" style="overflow: auto;"></div>
				</td>
			</tr>
		</table>
	</form>
	<BODY>
</HTML>


<SCRIPT LANGUAGE=javascript>
    var showFlag="1";
	var m_sXMLFile= "report_unit_tree.jsp?params=<%=params%>&backdate=<%=backdate%>";
	var newwindow;
	var root=new xtreeItem("root","<%=nodehead%>","<%=action%>","ril_body1",REPORT_UNIT,"/images/unit.gif",m_sXMLFile);
	<%  if(request.getParameter("showFlag")==null){ %>
	<logic:equal name="reportAnalyseForm" property="showFlag" value="2">
		Global.defaultInput=1;
		Global.showroot=false;
		showFlag="2";
	</logic:equal>
	
	<% }else if(request.getParameter("showFlag").equals("2")){%>  
		Global.defaultInput=1;
		Global.showroot=false;
		showFlag="2";
	<% } %>

	root.setup(document.getElementById("treemenu"));
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	
	
	
	
	function getShowFlag()
	{
		return showFlag;
	}
	
	function refresh(flag)
	{
		
			document.location="/report/report_analyse/reportunittree.jsp?showFlag="+flag+"&backdate=<%=backdate%>";
	}
	
	
	function getSelected()
	{
		return root.getSelected();
	}

	function setWidth(){
    	var table = document.getElementById("tableID");
    	if(isCompatibleIE())
    		table.style.width = document.body.clientWidth + "px";
    	else
	    	table.style.width = "100%";
    		
    }
       
    setWidth();
    window.onresize=setWidth;
</SCRIPT>