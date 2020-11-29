<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_system.kh_field.KhFieldForm"%>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="javascript" src="/js/dict.js"></script> 
<html>
<%
		String b0110=request.getParameter("a_code");
		if(b0110==null || b0110.trim().length()<=0)
			b0110="";
		String flag="";
		if(b0110.endsWith("UN")){
			flag="0";
		}else{
			flag="1";
		}
		KhFieldForm KFF=(KhFieldForm)session.getAttribute("khFieldForm");
		String info=KFF.getInfo();
 %>
<head>
<style type="text/css"> 

.selectPre{
	position:absolute;
    left:392px;
    top:34px;
    z-index: 10;
}

</style>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript">
	function createpoint(){
		var code='<%=b0110%>';
		var thecodeurl="/performance/kh_system/kh_field/init_grade_template.do?br_new=new`unitcode="+'<%=b0110%>'; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		var points= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");	
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
	function is_ok(outparamters){
		var unitcode =outparamters.getValue("unitcode");
   		khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+unitcode+"&flag=1";
   		khFieldForm.submit();
	}
	function deletep2(code){
		if(code=='1'){
			var orgpoint="${khFieldForm.orgpoint}";
			
	     		khpid="${khFieldForm.khpid}";
				var tablename="table${khFieldForm.tablename}"
		     	table=$(tablename);
		     	dataset=table.getDataset();
		     	var record=dataset.getFirstRecord();
		     	var selectID="";
		     	var points="";
		     	var i9999="";
		     	while(record){
		     		if(record.getValue("select")){
		     			points+=","+record.getValue(khpid);
		     			i9999+=","+record.getValue("i9999");
		     		}
		     		record=record.getNextRecord(khpid);
		     	}
		     	if(trim(points).length<=0){
		     		alert("请选择删除项！");
		     		return;
		     	}
		     if(confirm("您确认要清除当前所选行吗？")){
		     	var hashvo=new ParameterSet();
				hashvo.setValue("khpid",khpid);
				hashvo.setValue("orgpoint",orgpoint);
				hashvo.setValue("unitcode",'<%=b0110%>');
				hashvo.setValue("delpoints",points);
				hashvo.setValue("dflag",code);
				hashvo.setValue("i9999",i9999);
				var In_paramters="flag=1"; 
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok,functionId:'9021001085'},hashvo); 
	     	}
		}
		if(code=='2'){
				var hashvo=new ParameterSet();
				hashvo.setValue("orgpoint",orgpoint);
				hashvo.setValue("unitcode",'<%=b0110%>');
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_okk,functionId:'9021001089'},hashvo); 
		}
		if(code=='3'){
			if(confirm("您确认要清除当前及下级机构指标吗？")){
			var orgpoint="${khFieldForm.orgpoint}";
				var hashvo=new ParameterSet();
				hashvo.setValue("orgpoint",orgpoint);
				hashvo.setValue("unitcode",'<%=b0110%>');
				hashvo.setValue("dflag",code);
				var In_paramters="flag=1"; 
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok,functionId:'9021001085'},hashvo); 
			}
		}
	}
	function is_okk(outparamters){
		var parameters=outparamters.getValue("parameters");
		var code="2";
		if(parameters=='no'){
			if(confirm("您确认要清除当前机构指标吗？")){
				var orgpoint="${khFieldForm.orgpoint}";
					var hashvo=new ParameterSet();
					hashvo.setValue("orgpoint",orgpoint);
					hashvo.setValue("unitcode",'<%=b0110%>');
					hashvo.setValue("dflag",code);
					var In_paramters="flag=1"; 
					var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok3,functionId:'9021001085'},hashvo); 
				}
		}
		if(parameters=='ok'){
			var thecodeurl="/performance/kh_system/kh_field/init_grade_template.do?b_del=del`unitcode="+'<%=b0110%>'; 
				var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
				var returnVo= window.showModalDialog(iframe_url,"", 
			        "dialogWidth:450px; dialogHeight:360px;resizable:no;center:yes;scroll:yes;status:no");	
			   if(returnVo=='ok')  {   
					if(confirm("您确认要清除当前机构指标吗？")){
					var orgpoint="${khFieldForm.orgpoint}";
						var hashvo=new ParameterSet();
						hashvo.setValue("orgpoint",orgpoint);
						hashvo.setValue("unitcode",'<%=b0110%>');
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
   		khFieldForm.submit();
	}
	
	function gobackto(){
		window.close();
	}
	function setpage(){
		var theurl = "/performance/kh_system/kh_field/init_grade_template.do?b_add=add`aflag=1";
   		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
    	var return_vo= window.showModalDialog(iframe_url, arguments, 
        "dialogWidth:500px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no");
        if(return_vo=="ok"){
         	khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+'<%=b0110%>'+"&flag=0";
   			khFieldForm.submit();
        }
	}
	function copypoint(cclag){
		if(cclag=='1'){
			khpid="${khFieldForm.khpid}";
				var tablename="table${khFieldForm.tablename}"
		     	table=$(tablename);
		     	dataset=table.getDataset();
		     	var record=dataset.getFirstRecord();
		     	var selectID="";
		     	var points="";
		     	points='<%=b0110%>';
		     	while(record){
		     		if(record.getValue("select")){
		     			points+=","+record.getValue("i9999");
		     		}
		     		record=record.getNextRecord(khpid);
		     	}
		     	var temp=points.split(",");
		     	if(temp.length==1){
		     		alert("请复制考核指标！");
		     		return;
		     	}
		     	parent.mil_menu.copypoints=points;
		}
		if(cclag=='2'){
			var tablename="table${khFieldForm.tablename}"
		     	table=$(tablename);
		     	dataset=table.getDataset();
		     	var n=0;
		     	var record=dataset.getFirstRecord();
		     	if(record){
		     		n++;
		     	}
		     	if(n>0){
		     	
		     	}else{
		     		alert("所选机构下无指标！");
		     		return;
		     	}
		     	var record=dataset.getFirstRecord();
		     	if(record){
					parent.mil_menu.copyorg='<%=b0110%>';
					alert("复制当前选中组织单元成功！");
					return;
		     	}else{
		     		parent.mil_menu.copyorg='<%=b0110%>';
		     		alert("复制当前选中组织单元成功！");
		     		return;
		     	}
			
		}
	}
	function pastepoint(lflag){
		if(lflag=='1'){
				var copypoints=parent.mil_menu.copypoints;
				if(copypoints==null||copypoints.length==0){
					alert("请选择复制指标！");
					return;
				}
				var unticode='<%=b0110%>';
				if(unticode==copypoints.split(",")[0]){
					alert("不能在同一页面进行复制粘贴！");
					return;
				}
				var hashvo=new ParameterSet();
				hashvo.setValue("copypoints",copypoints);
				hashvo.setValue("unticode",unticode);
				hashvo.setValue("plag",lflag);
				var In_paramters="flag=1"; 
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok22,functionId:'9021001090'},hashvo); 
		}
		if(lflag=='2'){
			var copyorg=parent.mil_menu.copyorg;
			if(copyorg==null||copyorg.length==0){
					alert("请选择复制机构！");
					return;
			}
				var unticode='<%=b0110%>';
				var hashvo=new ParameterSet();
				hashvo.setValue("copyorg",copyorg);
				hashvo.setValue("unticode",unticode);
				hashvo.setValue("plag",lflag);
				var In_paramters="flag=1"; 
				var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok22,functionId:'9021001090'},hashvo); 
		}
	}
	function is_ok22(outparamters){
		var lflag=outparamters.getValue("plag");
		var unticode='<%=b0110%>';
		if(lflag=='2'){
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
		if(lflag=='1'){
			var unitname=outparamters.getValue("unitname");
			var copypoints=outparamters.getValue("copypoints");
			if(confirm("您确认要粘贴给机构["+unitname+"]吗？")){
					var hashvo=new ParameterSet();
					hashvo.setValue("copypoints",copypoints);
					hashvo.setValue("unticode",unticode);
					hashvo.setValue("plag",lflag);
					var In_paramters="flag=1"; 
					var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:is_ok2,functionId:'9021001087'},hashvo); 
				}
		}
	}
	
	function is_ok2(outparamters){
		///parent.mil_menu.copypoints="";
		///parent.mil_menu.copyorg="";
		var unitcode =outparamters.getValue("unitcode");
   		khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+unitcode+"&flag=1";
   		khFieldForm.submit();
	}
</script>
<title>Insert title here</title>
</head>
<body>
<html:form action="/performance/kh_system/kh_field/init_grade_template">
<table>
<tr>
<td align="left">
	<%=info %>
</td>
</tr>

</table>
<hrms:dataset name="khFieldForm" property="fieldlist" scope="session" setname="${khFieldForm.tablename}"  setalias="data_table" readonly="false" rowlock="true"    editable="true" select="true" sql="${khFieldForm.sql}" buttons="movefirst,prevpage,nextpage,movelast">
		  <hrms:commandbutton name="apply" onclick="deletep2('1');" function_id="326020103"   refresh="true" type="selected" setname="${positionDemandForm.tablename}"  >
	     	删除
	     </hrms:commandbutton>
	     <hrms:commandbutton name="save"   functionId="9021001086"  refresh="true" type="all-change" setname="${khFieldForm.tablename}"  function_id="326020105"  >
	          <bean:message key="button.save"/>
	     </hrms:commandbutton>  
</hrms:dataset>

</html:form>
<script type="text/javascript">
<%
	UserView userView = (UserView) session.getAttribute(WebConstant.userView);
	if ("hcm".equalsIgnoreCase(userView.getBosflag())) {
%>
		document.getElementById("tableOrgPointTable").style.marginLeft = "3";
<%
	}
%>
</script>
</body>
</html>