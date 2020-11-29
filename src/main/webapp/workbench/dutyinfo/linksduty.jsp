<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hrms.hjsj.sys.ConstantParamter"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
   String orgId = request.getParameter("code");
   int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	
	String xml = "#";
	  if(ConstantParamter.getConstantVo("POS_STANDARD")!=null)
		  xml = ConstantParamter.getConstantVo("POS_STANDARD").getString("str_value");
	  RecordVo vo = ConstantParamter.getRealConstantVo("PS_C_CODE");
		  String codesetid = vo.getString("str_value");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'MyJsp.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
  <hrms:themes></hrms:themes>
  <link rel="stylesheet" href="/css/xtree.css" type="text/css">
  <link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<script language="javascript" src="/js/constant.js"></script>
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
	var webserver=<%=flag%>;
</script>
  
  <body>
  <% if(!xml.equalsIgnoreCase("#")){ %>
  <div id="link">
    <center>
    <table width=500>
       <tr>
         <td>
            <fieldset style="padding: 10px,10px,10px,20px;">
                <legend>说明</legend>
                <table border=0>
                   <tr><td >1.选择基准岗位如果在选中组织单元中不存在，则会自动构建，同时引入基准岗位下的信息；</td></tr>
                   <tr><td style="padding-top: 10px;">2.选择基准岗位如果在选中组织单元中存在，则会重新引入基准岗位下的信息。</td></tr>
                </table>
            </fieldset>
         </td>
       </tr>
       <tr>
	       <td style="padding-top: 10px;">
		       <div id="treemenu" style="border:1px solid #8EC2E6;height: 250px;padding: 10px,10px,10px,10px; overflow: auto;">
				      <script>
				          var root=new xtreeItem("root","岗位类别","","",'岗位类别',"/images/spread_all.gif","/servlet/sduty/getSdutyTree?target=&codesetid=<%=codesetid%>&codeitemid=&param=root");
				          root.setup(document.getElementById("treemenu"));
				      </script>
      		   </div>
	       </td>
	   </tr>
	   <tr>
	     <td>
	          <fieldset style="padding: 10px,10px,10px,20px;">
                <legend>引用基准岗位的组织单元</legend>
                <table border=0>
                  <tr>
                      <td>
                         &nbsp;&nbsp;&nbsp;&nbsp;<input type="radio" name="insertOrg" value="self" checked id="self_radio"> 当前选中组织单元&nbsp;&nbsp;
                         <input type="radio" name="insertOrg" value="all" id="all_dio"> 当前选中组织单元下所有部门
                      </td>
                   </tr></table>
             </fieldset>
         </td>
	   </tr>
	   <tr>
	       <td align="center" style="padding-top: 10px;">
	          <button id="querybutton"  class="mybutton" onclick="checkExist()"><bean:message key="button.ok"/></button>&nbsp;&nbsp;&nbsp;
	          <button id="cancelbutton" class="mybutton" onclick="parent.window.close();"><bean:message key="button.cancel"/></button>
	       </td>
	   </tr>
    </table>
    </center> 
    </div> 
    <div id="showdiv" style="position: absolute;top: 30%;left: 25%;display: none;z-index: 2;">
   <table class="ListTable" width="250" >
       <tr>
           <td class="TableRow" align="left" style=" padding-left: 10px;">提示</td>
       </tr>
       <tr>
           <td class="RecordRow"  style=" padding-left: 10px;padding-top:30px;padding-bottom: 30px; ">此岗位已存在，请选择导入方式：</td>
       </tr>
       <tr>
           <td class="RecordRow" align="center" style="padding-top:5px;padding-bottom: 5px; ">
               <button class="mybutton" onclick="linkduty('over');" title="岗位信息将被基准岗位信息覆盖">覆盖</button>&nbsp;&nbsp;
               <button class="mybutton" onclick="linkduty('union');" title="将在岗位子集中追加基准岗位子集记录">追加</button>&nbsp;&nbsp;
               <button class="mybutton" onclick="linkduty('cancel');">取消</button>
           </td>
       </tr>
   </table>
</div>
<% }else{ %>
<div id="exit" style="position: absolute;top: 30%;left: 25%; ">
  <center>
    <table class="ListTable"  width=250 >
       <tr>
           <td class="TableRow">提示</td>
       </tr>
       <tr>
         <td class="RecordRow"  align="center" style=" padding-left: 10px;padding-top:30px;padding-bottom: 30px; ">
               未设置 基准岗位-岗位 指标对应！
         </td>
       </tr>
    </table>
  </center>
</div>
<%} %>
	<!-- 添加等待处理界面提示 wangb 20170801 13833  -->
 	<div id='wait' style='position:absolute;top:150;left:120;display:none;'>
		<table border="1" width="100" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td  class="td_style"  height=24>
					正在处理，请稍候...
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
<script>
var hashvo=new ParameterSet();
     function checkExist(){ 
    	 var currnode;
    	    currnode=Global.selectedItem;
    	 // if(currnode.uid == "root"){
    	//	  alert("请选择岗位！");
    	//	  return;
    	//  }
    	  var insertOrgs = document.getElementsByName("insertOrg");
		  for(var i=0;i<insertOrgs.length;i++){
			  if(insertOrgs[i].checked == true)
				  hashvo.setValue("insertOrg",insertOrgs[i].value);
		  }
    	     hashvo.setValue("dutytype",currnode.title); //节点类型  1代表叶子节点 ；0代表岗位体系分类
    	     hashvo.setValue("orgid","<%=orgId%>");	
    	     hashvo.setValue("dutyid",currnode.uid);//其实就是h0100
    	     hashvo.setValue("dutydesc",currnode.text);
    	     hashvo.setValue("check","true");     
    	     var request=new Request({asynchronous:false,onSuccess:getContext,functionId:'18010000068'},hashvo);
    	  
    	    
     }
     
     function getContext(outparamters)
	  {
    	  hashvo.setValue("check","false");
    	  
    	   var linktag = outparamters.getValue("linktag");
    	   if(linktag=='1'){
    		   linkduty("over");
    	   }else{
    		   document.getElementById("showdiv").style.display="block";
    		   document.getElementById("querybutton").disabled="true";
    		   document.getElementById("cancelbutton").disabled="true";
    	   }
	  }
     function linkduty(str){
    	 
    	 document.getElementById("showdiv").style.display="none";
    	 document.getElementById("querybutton").disabled="";
		 document.getElementById("cancelbutton").disabled="";
		 
         if(str=='cancel'){
    		 return;
    	 }
         
         
    	 hashvo.setValue("sqltag",str);
    	 //后台处理时，前台显示正在处理 的页面  wangb 20170801 13833
    	 var waitInfo=eval("wait");	
		 waitInfo.style.display="block";
		 //改为异步请求 wangb  20170801 13833
    	 var request=new Request({/*asynchronous:false*/asynchronous:true,onSuccess:showmessage,functionId:'18010000068'},hashvo);
     }
     function showmessage(outparameters){
    	 var mess = outparameters.getValue("tag");
    	 parent.window.returnValue = mess;
    	 var waitInfo=eval("wait");	
		 waitInfo.style.display="none";
    	 switch(mess){
	    	 case '1': alert("请先设置岗位基本信息集指标对应！");break;
	    	 case '3': alert("导入岗位失败！"); /*window.close();*/parent.window.close(); break;
	    	 case '4': alert("岗位参数设置中\"所属岗位体系指标\"所设置指标不可设置为基准岗位对应指标！"); parent.window.close(); break;
	    	 case 'ok': alert("成功导入岗位！"); /*window.returnvalue = "ok";  window.close();*/ if(!getBrowseVersion() && parent.window.opener && parent.window.opener.adddutyReturn){parent.window.opener.adddutyReturn('ok'); parent.window.close();}else{parent.window.close();} break;
    	 }
     }
     function initLinkType(){
    	 hashvo.setValue("orgid",'<%=orgId %>');
    	 hashvo.setValue("checkChild","1");
    	 var request = new Request({asynchronous:false,onSuccess:initCheck,functionId:'18010000068'},hashvo);
    	 function initCheck(outparam){
    		 hashvo.setValue("checkChild",'0');
    		 var isHas = outparam.getValue("isHas");
    		 if(isHas == '0' && document.getElementById("all_dio") != null)
    			 document.getElementById("all_dio").disabled = true;
    	 }
     }
     initLinkType();
</script>
<script>

</script>
