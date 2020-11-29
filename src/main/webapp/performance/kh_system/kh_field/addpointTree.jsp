<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.AdminCode"%>
<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.frame.utility.CodeItem"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	}
	String codeitemdesc = "组织机构";
	String codeset = userView.getManagePrivCode();
	String codevalue = userView.getManagePrivCodeValue();
	String action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load";
	
	//  绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
//	if (userView.isSuper_admin() || userView.getGroupId().equals("1"))
//		codevalue = "-1";
//	else
	{
		//操作单位没有设置 走管理范围 
		if (userView.getUnitIdByBusi("5") == null || userView.getUnitIdByBusi("5").equals("") || userView.getUnitIdByBusi("5").equalsIgnoreCase("UN"))
		{
			codevalue = userView.getManagePrivCodeValue();
			codeset = userView.getManagePrivCode();	
			if ((codeset == null || codeset.trim().length() == 0) && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "";
			else if (codeset.length() != 0 && (codevalue == null || codevalue.trim().length() == 0))
				codevalue = "-1";
		} else
		{
			codevalue = userView.getUnitIdByBusi("5"); //.substring(2);
			if (codevalue.trim().length() == 3)
				codevalue = "-1";
			else if (codevalue.indexOf("`") == -1 && codevalue.trim().length() > 0)
				codevalue = codevalue.substring(2);
		}
	}
	String b0110=request.getParameter("a_code");
%>

<HTML>
	<HEAD>
	<script type="text/javascript">
	function initqnode(){
		var codeitemid="${khFieldForm.codeitemid}";
		var obj=root.childNodes[0];
		if(obj){
	    	selectedClass("treeItem-text-"+obj.id);
	     	obj.select();
	     }else{
	   		if(root){
	     for(var j=0;j<root.childNodes.length;j++)
				{
				obj.expand();
				var obj=root.childNodes[j];
					if(obj.uid==codeitemid)
					{
						var obj=root.childNodes[j];
						selectedClass("treeItem-text-"+obj.id);
	     				obj.select();
						break;
					}
				}
			}
	  }
	  }
		function createpoint(){
		var currnode=Global.selectedItem;
		if(currnode==null){
			alert("请选择单位或部门！");
			return;
		}
    	base_id=currnode.uid;
    	if(base_id=='root'){
    	    alert("请选择单位或部门！");
			return;
		}
		
		var code=base_id;
		var thecodeurl="/performance/kh_system/kh_field/init_grade_template.do?br_new=new`unitcode="+code; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		if(isIE6()){
			var points= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:470px; dialogHeight:390px;resizable:no;center:yes;scroll:no;status:no");
		}else{
			var points= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:no;status:no");
		}
			
		if(points){
			if(points=='undefined'||points=='')
			{			
		     return;
			}
			var In_paramters="flag=1"; 
			var hashvo=new ParameterSet();
			hashvo.setValue("unitcode",code);
			hashvo.setValue("points",points);
			var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok,functionId:'9021001084'},hashvo); 
		}
	}
		/**
	 * 判断当前浏览器是否为ie6
	 * 返回boolean 可直接用于判断 
	 * @returns {Boolean}
	 */
	function isIE6() 
	{ 
		if(navigator.appName == "Microsoft Internet Explorer") 
		{ 
			if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
			{ 
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	function is_ok(outparamters){
		var unitcode =outparamters.getValue("unitcode");
   		khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+unitcode+"&flag=1";
   		khFieldForm.target="mil_body";
   		khFieldForm.submit();
   		
   		
	}
	function deletep2(code){
		if(code=='2'){
				var hashvo=new ParameterSet();
				hashvo.setValue("orgpoint",orgpoint);
				hashvo.setValue("unitcode",'<%=b0110%>');
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_okk,functionId:'9021001089'},hashvo); 
		}
	}
	function confirdelete(){
		var currnode=Global.selectedItem;
			if(currnode==null){
				alert("请选择单位或部门！");
				return;
			}
	    	base_id=currnode.uid;
	    	if(base_id=='root'){
	    	    alert("请选择单位或部门！");
				return;
			}
		var thecodeurl="/performance/kh_system/kh_field/init_grade_template.do?br_del=confirm"; 
			var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl; 
		var qwe= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:450px; dialogHeight:200px;resizable:no;center:yes;scroll:no;status:no");	
		if(qwe!=null){
				
			var uni=base_id;
			if(qwe[0]!=null&&qwe[0]=='ok'){
				if(qwe[1]=='1'){
					if(confirm("您确认要清除当前及下级机构指标吗？")){
						var orgpoint="${khFieldForm.orgpoint}";
						var hashvo=new ParameterSet();
						hashvo.setValue("orgpoint",orgpoint);
						hashvo.setValue("unitcode",uni);
						hashvo.setValue("dflag","3");
						var In_paramters="flag=1"; 
						var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok,functionId:'9021001085'},hashvo); 
					}
				}else{
					var hashvo=new ParameterSet();
					hashvo.setValue("orgpoint",orgpoint);
					hashvo.setValue("unitcode",uni);
					var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_okk,functionId:'9021001089'},hashvo); 
				}
			}
		
		}
	}
	function is_okk(outparamters){
		var parameters=outparamters.getValue("parameters");
		var code="2";
		var currnode=Global.selectedItem;
			if(currnode==null){
				alert("请选择单位或部门！");
				return;
			}
	    	base_id=currnode.uid;
	    	if(base_id=='root'){
	    	    alert("请选择单位或部门！");
				return;
			}
		
			var uni=base_id;
		if(parameters=='no'){
			if(confirm("您确认要清除当前机构指标吗？")){
				var orgpoint="${khFieldForm.orgpoint}";
					var hashvo=new ParameterSet();
					hashvo.setValue("orgpoint",orgpoint);
					hashvo.setValue("unitcode",uni);
					hashvo.setValue("dflag","2");
					var In_paramters="flag=1"; 
					var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok3,functionId:'9021001085'},hashvo); 
				}
		}
		if(parameters=='ok'){
				var thecodeurl="/performance/kh_system/kh_field/init_grade_template.do?b_del=del`unitcode="+uni; 
				var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
				var returnVo= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");	
			   if(returnVo=='ok')  {   
					if(confirm("您确认要清除当前机构指标吗？")){
					var orgpoint="${khFieldForm.orgpoint}";
						var hashvo=new ParameterSet();
						hashvo.setValue("orgpoint",orgpoint);
						hashvo.setValue("unitcode",uni);
						hashvo.setValue("dflag",code);
						var In_paramters="flag=1"; 
						var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok3,functionId:'9021001085'},hashvo); 
					}
				}
			}
	}
	function is_ok3(outparamters){
		alert("清除成功！该机构自动继承上级机构指标！");
		parent.mil_menu.copypoints="";
		parent.mil_menu.copyorg="";
		var unitcode =outparamters.getValue("unitcode");
   		khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+unitcode+"&flag=1";
   		khFieldForm.target="mil_body";
   		khFieldForm.submit();
	}
	var selecorg=""
	function copywq(){
		if(parent.mil_body.copypoint)
		  parent.mil_body.copypoint(2);
		 else
		  	alert("当前页面不支持复制粘贴功能！");
			
	}
	function paste(){
		if(parent.mil_body.pastepoint)
			parent.mil_body.pastepoint(2)
		else
			alert("当前页面不支持复制粘贴功能！");
	}
	function is_ok22(outparamters){
		var lflag=outparamters.getValue("plag");
		if(selecorg==null||selecorg.length==0){
					alert("请选择复制机构！");
					return;
			}
				var currnode=Global.selectedItem;
			if(currnode==null){
				alert("请选择单位或部门！");
				return;
			}
	    	base_id=currnode.uid;
	    	if(base_id=='root'){
	    	    alert("请选择单位或部门！");
				return;
			}
		var unticode=base_id;
		var copyorg=outparamters.getValue("copyorg");
		var copyname=outparamters.getValue("copyname");
		var unitname=outparamters.getValue("unitname");
		if(confirm("您确认要将机构["+copyname+"]的所有指标粘贴给["+unitname+"]吗？")){
			var hashvo=new ParameterSet();
			hashvo.setValue("copyorg",copyorg);
			hashvo.setValue("unticode",unticode);
			hashvo.setValue("plag",lflag);
			var In_paramters="flag=1"; 
			var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok2,functionId:'9021001087'},hashvo); 
		}
	}
	function setpg(){
   		khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_add=add1&aflag=1";
   		khFieldForm.target="mil_body";
   		khFieldForm.submit();
	}
</script>
		<TITLE></TITLE>

		<link href="/css/xtree.css" rel="stylesheet" type="text/css">
		<hrms:themes />
		<script language="javascript" src="../../../ajax/constant.js"></script>
		<script language="javascript" src="../../../ajax/basic.js"></script>
		<script language="javascript" src="../../../ajax/common.js"></script>
		<script language="javascript" src="../../../ajax/control.js"></script>
		<script language="javascript" src="../../../ajax/dataset.js"></script>
		<script language="javascript" src="../../../ajax/editor.js"></script>
		<script language="javascript" src="../../../ajax/dropdown.js"></script>
		<script language="javascript" src="../../../ajax/table.js"></script>
		<script language="javascript" src="../../../ajax/menu.js"></script>
		<script language="javascript" src="../../../ajax/tree.js"></script>
		<script language="javascript" src="../../../ajax/pagepilot.js"></script>
		<script language="javascript" src="../../../ajax/command.js"></script>
		<script language="javascript" src="../../../ajax/format.js"></script>
		<script language="javascript" src="../../../js/validate.js"></script>
		<SCRIPT LANGUAGE=javascript src="../../../js/xtree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="../../../js/codetree.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="../../../js/constant.js"></SCRIPT>
		<SCRIPT LANGUAGE=javascript src="../../../js/hjsjUrlEncode.js"></SCRIPT>
		<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="../../../js/popcalendar.js"></script>
		<script type="text/javascript">
		</script>
	</HEAD>
	<body topmargin="0" leftmargin="0" style='margin-left:0px;margin-top:0px;'marginheight="0" marginwidth="0">
	<html:form action="/performance/kh_system/kh_field/init_grade_template"> 
		<table align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
		<script type="text/javascript">
		if (!!window.ActiveXObject || "ActiveXObject" in window){
			document.write('			<tr  class="toolbar"  style="padding-left:2px;width:expression(document.body.clientWidth);overflow: auto;" >');
			document.write('	<td valign="middle" align="left" >');
		}else{
			document.write('<tr  class="toolbar"  style="padding-left:2px;width:'+document.body.clientWidth+'px;overflow: auto;" >');
			document.write('	<td valign="middle" align="left" style="width:'+document.body.clientWidth+'px;">');
		}
		</script>
			
			
					 
					<INPUT type="hidden" id="flg">
						
					<hrms:priv func_id="326020102">
					<a href="###" onclick="createpoint();"><img src="/images/add.gif"  title="新增" border="0" align="middle"></a>               
					</hrms:priv>  
					<hrms:priv func_id="326020103">
                   <a href="###" onclick="confirdelete();"><img src="/images/del.gif" title="删除" border="0" align="middle"></a>  
                   </hrms:priv>  
					<hrms:priv func_id="326020104">  
  				   <a href="###" onclick="copywq();"><img src="/images/copy.gif " title="复制" border="0" align="middle"></a>  
  				   </hrms:priv>  
					<hrms:priv func_id="326020104"> 
  				     <a href="###" onclick="paste();"><img src="/images/past.gif " title="粘贴" border="0" align="middle"></a>   
  				     </hrms:priv>  
                     <hrms:priv func_id="326020101">
					<a href="###" onclick="setpg();"><img src="/images/sys_config.gif"  title="设置" border="0" align="middle"></a> 
					</hrms:priv>  
				</td>
			</tr>
			<tr>
				<td valign="top">
					<div id="treemenu" style="height: expression(document.body.clientHeight-25);width:expression(document.body.clientWidth);overflow-x:auto;overflow-y:auto;"></div>
				<br></td>

			</tr>
		</table>
		</html:form>
	<BODY>
	<script language="javascript">
</script>
</HTML>
	<script language="javascript">
	var m_sXMLFile="/performance/kh_system/kh_field/add_point_Tree.jsp?flag=0&a_code=" +$URL.encode("<%=codevalue%>") + "&init=1";
	var root=new xtreeItem("root","<%=codeitemdesc%>","<%=action%>","mil_body","<%=codeitemdesc%>","/images/root.gif",m_sXMLFile);
		root.setup(document.getElementById("treemenu"));	
	 	initDocument();
	 	initqnode();
	</script>