<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.kh_system.kh_field.KhFieldForm"%>
<%@ page import="com.hjsj.hrms.interfaces.hire.OrganizationByXml,com.hrms.frame.codec.SafeCode"%>
<%@ page import="com.hrms.frame.utility.CodeItem"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<%
 
	String action="/performance/kh_system/kh_field/init_grade_template.do?b_query=query";
	
	if(request.getParameter("b_add")!=null&&request.getParameter("b_add").equals("add1"))
	{
		 KhFieldForm khFieldForm=(KhFieldForm)session.getAttribute("khFieldForm"); 
	     action="/performance/kh_system/kh_field/init_grade_template.do?b_load=load&a_code="+khFieldForm.getUnitcode();
  
	}


 %>

<style>
<!--
.AutoTable{
   BORDER-BOTTOM: medium none; BORDER-COLLAPSE: collapse; 
   BORDER-LEFT: medium none; BORDER-RIGHT: medium none; BORDER-TOP: medium none; 
   TABLE-LAYOUT:fixed;   
   word-break:break-all;
}
-->
</style>
<script type="text/javascript">
	function refr(lag){
		if(lag=='1'){
			var obj=document.getElementById('aa');
			var pointid=document.getElementById("aa1").value;
			var khpointname=document.getElementById("aa2").value;
			var pointset;
			for(var i=0;i<obj.options.length;i++){
				 if(obj.options[i].selected){
					pointset=obj.options[i].value;
					break;
				 }
			}
			var points=pointset+","+pointid+","+khpointname;
			var In_paramters="flag=1"; 	
			var hashvo=new ParameterSet();
			hashvo.setValue("kpoints",points);
			hashvo.setValue("lag",lag);
			var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:change_ok,functionId:'9021001080'},hashvo);
		}
		if(lag=='2'){
			var orgpointset=document.getElementById("aa").value;
			var khpointname=document.getElementById("aa2").value;
			var obj =document.getElementById('aa1');
			var pointid;
			var name;
			for(var i=0;i<obj.options.length;i++){
				if(obj.options[i].selected){
					pointid=obj.options[i].value;
					break;
				}
			}
			var points=orgpointset+","+pointid+","+khpointname;
			var In_paramters="flag=1"; 	
			var hashvo=new ParameterSet();
			hashvo.setValue("kpoints",points);
			hashvo.setValue("lag",lag);
			var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:change_ok,functionId:'9021001080'},hashvo);
		}
		if(lag=='3'){
			var orgpointset=document.getElementById("aa").value;
			var pointid=document.getElementById("aa1").value;
			var obj =document.getElementById('aa2');
			var khpointname;
			var name;
			for(var i=0;i<obj.options.length;i++){
				if(obj.options[i].selected){
					khpointname=obj.options[i].value;
					break;
				}
			}
			var points=orgpointset+","+pointid+","+khpointname;
			var In_paramters="flag=1"; 	
			var hashvo=new ParameterSet();
			hashvo.setValue("kpoints",points);
			hashvo.setValue("lag",lag);
			var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:change_ok,functionId:'9021001080'},hashvo);
		}
	}
	function change_ok(outparamters){
		var innerhtml=outparamters.getValue("innerhtml");
		var lag=outparamters.getValue("klag");
		if(lag=='1'){
			var fielditemlist=outparamters.getValue("khpidlist");
			var fielditemlist1=outparamters.getValue("khpnamelist");
			var fielditemlist2=outparamters.getValue("alllist");
			AjaxBind.bind(khFieldForm.khpid,fielditemlist);
			AjaxBind.bind(khFieldForm.khpname,fielditemlist1);
		}
		var _str=getDecodeStr(innerhtml);
		var op=document.getElementById('date_panel');
		op.innerHTML=""; 
		op.innerHTML=_str; 
		Element.show('date_panel');  
	}
	
	
	
	
	function sure(){
		var showmenus="";
		var orgpointset=document.getElementById("aa").value;
		var khpointid=document.getElementById("aa1").value;
		var khpointname=document.getElementById("aa2").value;
		var hashvo=new ParameterSet();
		var allitems=document.getElementsByName("allitems");
		for(var i=0;i<allitems.length;i++){
			if(allitems[i].checked){
				showmenus+=allitems[i].value+",";
			}
		}
		
		if(!(orgpointset=="-1"&&khpointname=="-1"&&khpointid=="-1")){
			if(orgpointset=="-1"){
				alert("请选择考核指标子集！");
				return;
			}
			if(khpointname=="-1"){
				alert("请选择指标名称！");
				return;
			}
			if(khpointid=="-1"){
				alert("请选择指标编号！");
				return;
			}
			if(khpointid==khpointname){
				alert("指标编号和指标名称不能相同！");
				return;
			}
		}
		hashvo.setValue("showmenus",showmenus);
		hashvo.setValue("orgpoint",orgpointset);
		hashvo.setValue("khpid",khpointid);
		hashvo.setValue("khpname",khpointname);
		var In_paramters="flag=1"; 	
		var request=new Request({asynchronous:false,parameters:In_paramters,onSuccess:save_ok,functionId:'9021001081'},hashvo);
		
	}
	function save_ok(){
	//	khFieldForm.action="/performance/kh_system/kh_field/init_grade_template.do?b_query=query";
		var orgpointset=document.getElementById("aa").value;
		var khpointid=document.getElementById("aa1").value;
		var khpointname=document.getElementById("aa2").value;
		if(!(orgpointset=="-1"&&khpointname=="-1"&&khpointid=="-1")){
	   		khFieldForm.action="<%=action%>";
	   		khFieldForm.submit();
		}else{
			alert("初始化成功！");
		}
	}
	function concel(){
		window.history.back();
	}
	var orgpoint="${khFieldForm.orgpoint}";
	var khpid="${khFieldForm.khpid}";
	var khpname="${khFieldForm.khpname}";
	<%String aflag=request.getParameter("aflag");
		if(aflag!=null&&aflag.equalsIgnoreCase("0")){
	%>
	if(orgpoint.length>0){
		document.location ="/performance/kh_system/kh_field/init_grade_template.do?b_query=query"; 
	}else{

		
    }
	
	<%}%>
</script>
<html:form action="/performance/kh_system/kh_field/init_grade_template">
<div style='height:480px;width:100%; overflow: auto; padding-top:5px;' id="c_panel" >
<table width="600px"  border="0" align='center' class="ListTable">
  <tr>
	<td class="TableRow" align="left" width="100%" colspan="2">
		&nbsp;&nbsp;设置&nbsp;&nbsp;
	</td>
  </tr>

<tr>
  			 <td align="left" width="50%" class="RecordRow">
				&nbsp;&nbsp;考核指标子集：&nbsp;&nbsp;
			</td>
   			<td align="left" width="50%" class="RecordRow">
  						<html:select styleId="aa" name="khFieldForm" property="orgpoint" style="width:80%;" onchange="refr('1');">
  							
  							<html:option value="-1">请选择</html:option>
							<html:optionsCollection property="pointList" value="dataValue" label="dataName" />
						</html:select>
			</td>
  </tr>

  <tr>
  					<td align="left" width="50%" class="RecordRow">
							&nbsp;&nbsp;"指标编号"指标：&nbsp;&nbsp;
					</td>
   					<td align="left" width="50%" class="RecordRow">
	  					<html:select styleId="aa1" name="khFieldForm" property="khpid" style="width:80%;" onchange="refr('2');">
									<html:optionsCollection property="khpidlist" value="dataValue" label="dataName" />
						</html:select>
					</td>
  				</tr>
  
    <tr>
  		
  				<td align="left" width="50%" class="RecordRow">
					&nbsp;&nbsp;"指标名称"指标：&nbsp;&nbsp;
				</td>
	   				<td align="left" width="50%" class="RecordRow">
	  						<html:select styleId="aa2" name="khFieldForm" property="khpname" style="width:80%;" onchange="refr('3');">
									<html:optionsCollection property="khpnamelist" value="dataValue" label="dataName" />
							</html:select>
					</td>
  				</tr>
<tr>
	<td align="center" width="100%"  colspan="2" id="date_panel"> 
		<fieldset align="center" style="width:100%">
	  		<legend>其他显示指标</legend>
			<div style='height:280;width:100%; overflow: auto;' >
			${khFieldForm.innerhtml}
			</div>
	  </fieldset>
	</td>
</tr>
<tr>
<td align="center" colspan="2">
<input type="button" name="new" class="mybutton" style="margin-top:5px;" value="确定" onclick="sure();"/>

<% if(request.getParameter("b_add")!=null&&request.getParameter("b_add").equals("add1")){ %>
&nbsp;&nbsp;
&nbsp;&nbsp;
<!-- 
<input type="button" name="new" class="mybutton" value="取消" onclick="concel();"/>
 -->
<% } %>

</td>
</tr>
</table>
</div>
  <script language="javascript">
   Element.show('date_panel');
  
</script>
</html:form>

