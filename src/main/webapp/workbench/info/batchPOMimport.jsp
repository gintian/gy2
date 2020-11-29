<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView!=null){
     bosflag = userView.getBosflag();
    }
%>
<html>
	<head>

	</head>
	<script language='javascript'>
	function goback()
	{
		/* selfInfoForm.action="/workbench/info/showinfodata.do?b_search=link";
      	 selfInfoForm.submit();*/
      	 window.location.href="/workbench/info/showinfodata.do?b_search=link";
	}
    function batchPOMimport()
    {
    
    	 var fileEx = selfInfoForm.file.value;
        if(fileEx == ""){
        	alert("请选择需导入的文件!");
        	return ;
        }
        <logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia"><!--zgd 2014-5-16 批量导入照片时，前台页面报错-->
	       //var multimediaSortFlag=document.getElementById('multimediaSortFlag').value;
	       var multimediaSortFlag=document.getElementsByName('multimediaSortFlag')[0].value;//非IE浏览器 name 属性不能通过获取id方式获取    bug 35129 wangb 20180302
		 	if(multimediaSortFlag.length==0){
		 		alert("没有多媒体分类权限，不能上传！");
		 		return;
		 	}
	 	</logic:equal>
       	flag=true;
		var temp=fileEx;
		while(flag)
    	{
	    	temp=temp.substring(temp.indexOf(".")+1)
	    	if(temp.indexOf(".")==-1)
	    		flag=false;
    	}
    	if(temp.toLowerCase()=='zip')
    	{
	    	<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
		    	var multimediaName =document.getElementsByName("multimediaName")[0];
		    	if(multimediaName.value==""){
		    		if(!confirm("未填写名称！系统将自动填写？")){
		    		return;
		    		}  
		    	}
	    	</logic:equal>
    		selfInfoForm.target="nil_body";
    		document.selfInfoForm.action="/workbench/info/showinfodata.do?b_batchPOMimport=link";//POM(photo or multimedia)
  			document.selfInfoForm.method="post";
	      	document.selfInfoForm.enctype="multipart/form-data";
  			document.selfInfoForm.submit();
  			//【4764】员工管理-信息维护-记录录入-批量导入照片，没有看到进度显示  jingq add 2014.11.10
  			var waitInfo = document.getElementById("wait");
  			waitInfo.style.display = "block";
  			//【5445】连续多次点击导入后，提示导入2条记录，实际上导入了4条记录，不对。 jingq add 2014.11.27
  			document.getElementsByName("b_update")[0].disabled = true;
    	}
    	else
    	{
    		alert('<bean:message key="workbench.browse.import.constraintZip"/>');
    	}
    }
    
    
  </script>
  <hrms:themes />
	<body>
		<form name="selfInfoForm" method="post"
			action="/workbench/info/showinfodata.do" enctype="multipart/form-data">
			<%if("hcm".equals(bosflag)){ %>
				<table border="0" align="center" style="width: 700" cellspacing="0" cellpadding="0">
			<%}else{ %>
				<table border="0" align="center" cellspacing="0" cellpadding="0" style="width: 700;margin-top: 8px">
			<%} %>
				<tr>
					<td >
						<fieldset>
							<legend>
								<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
									多媒体<bean:message key="workbench.browse.namingrules"/>
								</logic:equal>
								<logic:equal name = "selfInfoForm" property="batchImportType" value="photo">
									照片<bean:message key="workbench.browse.namingrules"/>
								</logic:equal>
							</legend>
					<logic:equal name = "selfInfoForm" property="batchImportType" value="photo">
							<table border="0"  align="center" width="90%" >
								<tr>
									<td align="right" width="30%">
										<bean:message key="column.law_base.filename"/>
									</td>
									<td align="left" width="70%">
										<logic:notEmpty name="selfInfoForm" property="infoFieldList" >
											<hrms:optioncollection name="selfInfoForm" property="infoFieldList" collection="list"/>
												<html:select name="selfInfoForm" property="ruleItemid"  style="width:300">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
												</html:select>
										</logic:notEmpty>
									</td>
								</tr>
							</table>
					</logic:equal>
					<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
						<logic:notEqual name="selfInfoForm" property="multimedia_file_flag" value="1">
							<table border="0"  align="center" width="90%" >
								<tr>
									<td align="right" width="30%">
										<bean:message key="column.law_base.filename"/>
									</td>
									<td align="left" width="70%">
										<logic:notEmpty name="selfInfoForm" property="infoFieldList" >
											<hrms:optioncollection name="selfInfoForm" property="infoFieldList" collection="list"/>
												<html:select name="selfInfoForm" property="ruleItemid"  style="width:300">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
												</html:select>
										</logic:notEmpty>
									</td>
								</tr>
							</table>
						</logic:notEqual>
						<logic:equal name="selfInfoForm" property="multimedia_file_flag" value="1">
							<table border="0"  align="center" width="90%" >
								<tr>
									<td align="right" width="30%">
										目标子集
									</td>
									<td align="left" width="70%">
											<hrms:optioncollection name="selfInfoForm" property="multimediaFilelist" collection="list"/>
												<html:select name="selfInfoForm" property="mulSetid"  style="width:150" onchange="changeFieldSet();">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
												</html:select>
									</td>
								</tr>
								<tr>
									<td align="right" width="30%">
										<bean:message key="column.law_base.filename"/>
									</td>
									<td align="left" width="70%" >
										<table border="0" cellpmoding="0" cellspacing="0"  cellpadding="0"  >
											<tr>
												<td align="left" >
													<logic:notEmpty name="selfInfoForm" property="infoFieldList" >
													<hrms:optioncollection name="selfInfoForm" property="infoFieldList" collection="list"/>
														<html:select name="selfInfoForm" property="ruleItemid"  style="width:150">
																<html:options collection="list" property="dataValue" labelProperty="dataName" />
														</html:select>
													</logic:notEmpty>
												</td>
												<td id="mulItemflag">
													_<hrms:optioncollection name="selfInfoForm" property="mulFileItemlist" collection="list" />
													<html:select name="selfInfoForm" property="mulItemid"  style="width:150;">
														<html:options collection="list" property="dataValue" labelProperty="dataName" />
													</html:select>
												</td>
												<td valign="bottom">
													_(文件名称).(扩展名)
												</td>
											</tr>
										</table>
									</td>
								</tr>
								<tr>
									<td align="right" width="30%">
										例如
									</td>
									<td align="left" width="70%">
										：110203196702041234_2016-07-01_硕士学位证.jpg
									</td>
								</tr>
							</table>
						</logic:equal>
					</logic:equal>
						</fieldset>
					</td>
					
				</tr>
				<tr>
					<td >
						<fieldset>
							<legend>
								<logic:equal name = "selfInfoForm" property="batchImportType" value="photo">
								<bean:message key="workbench.browse.selectphotopackage"/>
								</logic:equal>
								<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
								<bean:message key="workbench.browse.selectmultimediapackage"/>
								</logic:equal>
							</legend>
							<table border="0"  align="center" width="90%" >
								<tr>
									<td align="right" width="30%">
										<bean:message key="menu.file"/>
									</td>
									<td align="left" width="70%">
										<input type="file" name="file"  class="text6" style="width:300;height: 25px;">
									</td>
								</tr>
								
								<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
								<tr >
									<td align="right">
										<bean:message key="label.sys.codename"/>								
									</td>
									<td align="left">
										<html:text name="selfInfoForm" property="multimediaName" style="width:300" styleClass="inputtext"/>
									</td>
								</tr>
								<tr>
									<td align="right">
										<bean:message key="hire.file.classification"/>
									</td>
									<td align="left">
										<hrms:optioncollection name="selfInfoForm" property="infoSetList" collection="list"/>
											<html:select name="selfInfoForm" property="multimediaSortFlag"  style="width:300">
												<html:options collection="list" property="dataValue" labelProperty="dataName" />
											</html:select>
									</td>
								</tr>
								</logic:equal>
								<tr>
								    <td align="right">
									<bean:message key="label.description"/>
									</td>
									<td> (1)&nbsp;<bean:message key="workbench.browse.import.constraintZip"/> </td>
								</tr>
								<tr>
								<td> </td>
								<logic:equal name = "selfInfoForm" property="batchImportType" value="photo">
									
									<td align="left">
										(2)&nbsp;<bean:message key="workbench.info.photodescription"/>
										<br/>
									<logic:notEqual name = "selfInfoForm" property="photo_maxsize" value="0">
										(3)&nbsp;单张照片大小必须限制在${selfInfoForm.photo_maxsize }KB以内，否则不允许上传。
										</logic:notEqual>
									</td>
								</logic:equal>
									<logic:equal name = "selfInfoForm" property="batchImportType" value="multimedia">
										<logic:notEqual name = "selfInfoForm" property="multimedia_maxsize" value="0">
										<td>
										(2)&nbsp;单个多媒体的文件大小必须限制在${selfInfoForm.multimedia_maxsize }KB以内，否则不允许上传。
										</td>
										</logic:notEqual>
									</logic:equal>
								</tr>
							</table>
						 </fieldset>
					</td>
				 </tr>
				</table>
				<table border="0" cellspacing="0" align="center" cellpadding="0" style="width: 700px;" >
						<tr><td height="5px"></td></tr>
						<tr>
							<td align="center">
								<input type="button" name="b_update" value="<bean:message key='menu.gz.import'/>" class="mybutton"
									onClick="batchPOMimport()">
							    <input type="button" name="b_update" value="<bean:message key='button.return'/>" class="mybutton"
									onClick="goback();">
							</td>
						</tr>
				</table>
				<!-- 【4764】员工管理-信息维护-记录录入-批量导入照片，没有看到进度显示  jingq add 2014.11.10 -->
				<div id='wait' style='position:absolute;top:70;left:350;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					正在导入，请稍候...
				</td>
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
		</form>
	</body>
</html>
	<script language='javascript'>
    function changeFieldSet(){
    	var v = "";
        if(selfInfoForm.mulSetid){
        	v = selfInfoForm.mulSetid.value;
        }
      	var hashvo=new ParameterSet();
        hashvo.setValue("selectSetid",v);
       	 	
    	var request=new Request({method:'post',asynchronous:false,
    		onSuccess:resultChangeFieldSet,functionId:'0201001094'},hashvo);					
      }
      
      function resultChangeFieldSet(outparamters){
      	init();
      	var mulFileItemlist=outparamters.getValue("mulFileItemlist");
      	if(selfInfoForm.mulItemid){
	    	AjaxBind.bind(selfInfoForm.mulItemid,mulFileItemlist);
      	}
      }
      function init(){
    	  var value = "";
          if(selfInfoForm.mulSetid){
          		value = selfInfoForm.mulSetid.value;
          }
		  if(value == "A01"){
			  if(document.getElementById("mulItemflag")){
			  	document.getElementById("mulItemflag").style.display="none";
		      }
		  }else{
			  if(document.getElementById("mulItemflag")){
			  	document.getElementById("mulItemflag").style.display="block";
	      	}
		  }
      }
      init();
      if(!getBrowseVersion()){//兼容非IE浏览器样式 修改   wangb  20180206  bug 34450
	  	var file = document.getElementsByClassName('text6')[0]; //下载文件框 高度调整
	  	file.style.height = '26px';
	  }
  </script>