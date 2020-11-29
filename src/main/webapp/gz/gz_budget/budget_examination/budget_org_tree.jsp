<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.businessobject.report.tt_organization.TTorganization"%>
<%@ page import="com.hrms.frame.dao.RecordVo,
com.hjsj.hrms.actionform.report.org_maintenance.SearchReportUnitForm,
                 com.hjsj.hrms.utils.ResourceFactory"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_budget.BudgetExaminationForm,
                 com.hrms.frame.utility.AdminCode,
                 com.hjsj.hrms.businessobject.gz.gz_budget.budget_examination.BudgetExamBo" %>
<%@ page import="java.sql.*"%>
<%@ page import="com.hrms.frame.utility.AdminDb"%>
<%
	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
		css_url=userView.getCssurl();
		if(css_url==null||css_url.equals(""))
		css_url="/css/css1.css";
	}
	BudgetExaminationForm budgetForm = (BudgetExaminationForm)session.getAttribute("budgetExaminationForm");
	String Budget_id = budgetForm.getBudget_id();	
	String rootUnitcode =budgetForm.getRootUnitcode();
	
	String rootTitle = ResourceFactory.getProperty("tree.orgroot.orgdesc");
	String rootcode = "root";
	String rootText = null;
	String XMLFile = null;	 
	String rootAction = null;
	if(rootUnitcode!=null&&rootUnitcode.length()>0)	{
		String status=budgetForm.getRootunitstatus();
		if (!status.equals(""))  status ="("+status+")";
		rootcode="UN"+rootUnitcode;
		rootText = AdminCode.getCode("UN", rootUnitcode).getCodename()+status;		
		rootTitle =AdminCode.getCode("UN", rootUnitcode).getCodename();
		XMLFile = "budget_exam_org_tree_xml.jsp?topunit="+rootUnitcode;	 
		rootAction = "/gz/gz_budget/budget_examination.do?b_query=query&a_code=UN"+rootUnitcode;
	}
	else{
		rootText = ResourceFactory.getProperty("tree.orgroot.orgdesc");
		XMLFile = "budget_exam_org_tree_xml.jsp?topunit=";	 
		rootAction = "/gz/gz_budget/budget_examination.do?b_query=query&a_code=UN";
	}

%>
		
	
<HTML>
<HEAD>
	<link href="<%=css_url%>" rel="stylesheet" type="text/css">
	<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
	<hrms:themes />
	<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
	<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
	<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>   
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/dict.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>

 
	<SCRIPT LANGUAGE=javascript>
	 
	var ViewProperties=new ParameterSet();	
	function  opt(flag)   // flag:  1:批准   2：驳回  3:数据同步 4上报
	{
		var currnode=Global.selectedItem; 		
		var b0110= currnode.uid.substring(2);
		var budget_id='${budgetExaminationForm.budget_id}';		
		
		if(flag==3){
		    self.parent.parent.mil_body.jinduo();
			var hashVo=new ParameterSet(); 
			hashVo.setValue("budgetIdx",budget_id);  
			var request=new Request({method:'post',asynchronous:true,onSuccess:opt_ok3,functionId:'302001020304'},hashVo);		
		}
		else{
			if(flag==1)	{
			 	if(!confirm(APPROVAL_OK+"?"))
			 		return;
			}
			else if(flag==2){
			 	if(!confirm(DISMISSED_OK+"?"))
			 		return;
			}
			
			else if(flag==4){
			 	if(!confirm("确认上报"+"吗?"))
			 		return;
			}
		
			var hashVo=new ParameterSet();
			hashVo.setValue("b0110",b0110);
			hashVo.setValue("budget_id",budget_id);	
			hashVo.setValue("flag",flag);
			if(flag==1){ 
			  var request=new Request({method:'post',asynchronous:false,onSuccess:opt_ok1,functionId:'302001020303'},hashVo);	
			}	
			else if(flag==2){ 
			  var request=new Request({method:'post',asynchronous:false,onSuccess:opt_ok2,functionId:'302001020303'},hashVo);	
			}
			else if(flag==4){ 			
			  var request=new Request({method:'post',asynchronous:false,onSuccess:opt_ok4,functionId:'302001020303'},hashVo);	
			}
		}
		 
	}
	function validte(flag)   // flag:  1:批准   2：驳回   4上报
	{
		var currnode=Global.selectedItem; 		
		var b0110= currnode.uid.substring(2);
		var topunit=document.getElementById("topunit").value;
		var budget_id='${budgetExaminationForm.budget_id}';

		if(flag==1)	{
			if (currnode.uid==topunit) return;
		}
		else if(flag==2){
			if (currnode.uid==topunit) return;
		}
		else if(flag==4){
			if (currnode.uid=="root") return;
		}		

		var hashVo=new ParameterSet();
		hashVo.setValue("b0110",b0110);
		hashVo.setValue("budget_id",budget_id);	
		hashVo.setValue("flag",flag);
		var request=new Request({method:'post',asynchronous:false,onSuccess:validte_ok,functionId:'302001020306'},hashVo);	
 
	}
	
	function validte_ok(outparameters)	{ 
		var flag = outparameters.getValue("flag");
		var info = outparameters.getValue("info");
		if (info=="true"){
			opt(flag);
		}
		else {
			var str = outparameters.getValue("strError");
			if (str!=""){
				alert(str);			
			}
		}
	}
	
	
	function opt_ok1(outparameters)
	{ 
		var currnode=Global.selectedItem;  

		var dmobj=g_dm["_2303"]; // 已批
		var desc="";
		if(dmobj!=null&&dmobj!="undefined")
  			desc= "("+dmobj.V+")";
		currnode.setText(currnode.title +desc);

		
		if(currnode.load){
			while(currnode.childNodes.length){
				currnode.childNodes[0].remove();
			}
			currnode.load=true;
			currnode.loadChildren();
			currnode.reload(1);
		}
		currnode.select();
		//var href="/gz/gz_budget/budget_examination.do?b_query=query&a_code="+currnode.uid; 
		//parent.parent.mil_body.location=href; 
	}
	
	function opt_ok2(outparameters)
	{ 
		var currnode=Global.selectedItem;  

		var dmobj=g_dm["_2307"]; // 驳回
		var desc="";
		if(dmobj!=null&&dmobj!="undefined")
  			desc= "("+dmobj.V+")";
		currnode.setText(currnode.title +desc);		
		if(currnode.load){
			while(currnode.childNodes.length){
				currnode.childNodes[0].remove();
			}
			currnode.load=true;
			currnode.loadChildren();
			currnode.reload(1);
		}
		currnode.select();
	}
	
	function opt_ok3(outparameters)
	{ 
	   var info=getDecodeStr(outparameters.getValue("info"));
	   alert(info);
	   self.parent.parent.mil_body.closejindu();
	 
	}

	function opt_ok4(outparameters)
	{ 
		var currnode=Global.selectedItem;  
		var dmobj=g_dm["_2302"]; // 报批
		var desc="";
		if(dmobj!=null&&dmobj!="undefined")
  			desc= "("+dmobj.V+")";
		currnode.setText(currnode.title +desc);		
		if(currnode.load){
			while(currnode.childNodes.length){
				currnode.childNodes[0].remove();
			}
			currnode.load=true;
			currnode.loadChildren();
			currnode.reload(1);
		}
		currnode.select();
	}
	
	function summary(){
			var currnode=Global.selectedItem; 
			if (currnode.uid=="root") return;
			if(!confirm("确认汇总"+"吗?"))	return;
			var b0110= currnode.uid.substring(2);
			var budget_id='${budgetExaminationForm.budget_id}';
			var tab_id='${budgetExaminationForm.tab_id}';
			var hashVo=new ParameterSet();
			hashVo.setValue("b0110",b0110);
			hashVo.setValue("budget_id",budget_id);
			hashVo.setValue("tab_id",tab_id);		
			var request=new Request({method:'post',asynchronous:false,onSuccess:summary_ok,functionId:'302001020305'},hashVo);
	}
	function summary_ok(outparameters){
		alert("操作成功！");
		var currnode=Global.selectedItem;  
		var href="/gz/gz_budget/budget_examination.do?b_query=query&a_code="+currnode.uid; 
		parent.parent.mil_body.location=href; 
	}
	
	
	function release(flag){
			var budget_id='${budgetExaminationForm.budget_id}';
			var hashVo=new ParameterSet();
			hashVo.setValue("budget_id",budget_id);
			hashVo.setValue("flag",flag);
			var request=new Request({method:'post',asynchronous:false,onSuccess:ReleaseAndStop,functionId:'302001020307'},hashVo);
	}
	
	function ReleaseAndStop(outparameters){
			if(!confirm("确认发布"+"本次预算吗?"))	return;
			var budget_id='${budgetExaminationForm.budget_id}';
			var flag = outparameters.getValue("flag")
			var hashVo=new ParameterSet();
			hashVo.setValue("budget_id",budget_id); 
			hashVo.setValue("flag",flag);
			var request=new Request({method:'post',asynchronous:false,onSuccess:summary_ok,functionId:'302001020308'},hashVo);			
	}
	function Stop(flag){
			if(!confirm("确认暂停"+"本次预算吗?"))	return;
			var budget_id='${budgetExaminationForm.budget_id}';
			var hashVo=new ParameterSet();
			hashVo.setValue("budget_id",budget_id); 
			hashVo.setValue("flag",flag);
			var request=new Request({method:'post',asynchronous:false,onSuccess:summary_ok,functionId:'302001020308'},hashVo);			
	}
	function init1(){
			var flag = '${budgetExaminationForm.flag}';
			if(flag=="1"){
				toggles("div1");
				hides("div2");
			}else if(flag=="2"){
				toggles("div2");
				hides("div1");
			}
	       	
	}
	function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
	}
	function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
	} 
	function down(flag){
		var tabid = self.parent.a.Global.selectedItem;
		var tab_id = tabid.uid;
		if (tab_id=="root"||tab_id=="计提"||tab_id=="支出"){
			alert("请选择预算表!");
			return;
		}
	
		var budget_id = '${budgetExaminationForm.budget_id}';
		var b0110 = self.parent.b.Global.selectedItem;
		var bb0110 = b0110.uid;
		var hashvo=new ParameterSet();
     	hashvo.setValue("flag",flag);
     	hashvo.setValue("tab_id",tab_id);
     	hashvo.setValue("budgetid",budget_id);
     	hashvo.setValue("b0110",bb0110);
     	var request=new Request({asynchronous:false,onSuccess:download,functionId:'302001020221'},hashvo);	
	}
	function download(outparameters){
		var filename=outparameters.getValue("fileName");
		filename=getDecodeStr(filename);
	    var win=open("/servlet/vfsservlet?fileid="+filename+"&fromjavafolder=true","excel");
	}
	function en(){
		var href="/gz/gz_budget/budget_history.do?b_query=link"; 
		parent.parent.location=href; 
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
<hrms:themes />
<body >
<html:form action="/gz/gz_budget/budget_examination"> 
<input type ="hidden" id="topunit" value=<%=rootcode %> >
	<table align="left" width="800" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
		<tr align="left"  class="toolbar" style="padding-left:2px;">
				<td valign="middle" align="left">
					<div id="div1">
					<table align='left' ><tr>
					<td valign='top'>
					 <hrms:priv func_id="324200301">
					<a href="###" onclick="validte(1);"><img src="/images/sb.gif" alt="<bean:message key="button.approve"/>" border="0" align="middle"></a>               
                 	 </hrms:priv>
                 	</td> 
                 	<td valign='bottom' > 
                 	  <hrms:priv func_id="324200302">
                    <a href="###" onclick="validte(2);"><img src="/images/link.gif" alt="<bean:message key="button.reject"/>" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>

                    
                    <td valign='bottom' > 
                 	  <hrms:priv func_id="324200304">
                    <a href="###" onclick="summary()"><img src="/images/img_c.gif" alt="汇总" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>
                    
                    <td valign='bottom' > 
                 	  <hrms:priv func_id="324200305">
                    <a href="###" onclick="validte(4)"><img src="/images/img_wd.png" alt="上报" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>
                                        
                    <td valign='bottom' > 
                 	  <hrms:priv func_id="324200303">
                    <a href="###" onclick="return opt(3);"><img src="/images/deal.gif" alt="数据同步" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>
                    
                    <td valign='bottom' > 
                 	  <hrms:priv func_id="324200306">
                    <a href="###" onclick="release(5)"><img src="/images/img_px.png" alt="发布" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>
                    
                    <td valign='bottom' > 
                 	  <hrms:priv func_id="324200307">
                    <a href="###" onclick="Stop(6)"><img src="/images/stop_blue.png" alt="暂停" border="0" align="middle"></a>    
                      </hrms:priv>
                    </td>
                    </tr></table>                    
                    </div>
                    
                    <div id="div2">
                    <table align='left' ><tr>
                    <td valign='top'>
                  	  <hrms:priv func_id="324200308">
                    <a href="###" onclick="down(0)"><img src="/images/export.gif" alt="导出" border="0" align="middle"></a>    
                      </hrms:priv> 
                    </td>
                    <td valign='bottom' > 
                      <hrms:priv func_id="324200309">
                    <a href="###" onclick="down(1)"><img src="/images/img_e.gif" alt="批量导出" border="0" align="middle"></a>    
                      </hrms:priv> 
                    </td>
                    <td valign='bottom' > 
 
 					<!-- 删除返回按钮，太丑了 
                    <a href="###" onclick="en()"><img src="/images/fh2.gif" alt="返回" border="0" align="middle"></a>    
 					 -->
   
                    </td>
                    </tr> </table>                 	
                    </div>		
				</td>
					
			</tr>
	<tr>  
		<td valign="top" >
			
			<div id="treemenu"></div>
        
		</td>
	 
	</tr>
</table>	

</html:form>
</BODY>
</HTML>		
<SCRIPT LANGUAGE=javascript>
   init1();

    initDocument();

	var root=new xtreeItem("<%=rootcode%>","<%=rootText%>","<%=rootAction%>","mil_body","<%=rootTitle%>","/images/root.gif","<%=XMLFile%>");

	root.setup(document.getElementById("treemenu"));
    var rootunitdcode = '${budgetExaminationForm.rootUnitcode}';

	if (rootunitdcode==""){
	   if(root.getFirstChild())
	   {
	     root.getFirstChild().select();
	     selectedClass("treeItem-text-"+root.getFirstChild().id);
	   }
	}
	else {
       root.select();
       selectedClass("treeItem-text-"+root.id);
	}




</script>
