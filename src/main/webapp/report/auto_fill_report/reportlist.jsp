<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.frame.dao.RecordVo,
				 java.net.URLEncoder,
				 com.hrms.struts.constant.SystemConfig,
				 com.hjsj.hrms.actionform.report.auto_fill_report.ReportListForm,
				 com.hjsj.hrms.actionform.report.auto_fill_report.ReportOptionForm,
				 com.hjsj.hrms.utils.PubFunc"%>
<%
   ReportOptionForm reportOptionForm=(ReportOptionForm)session.getAttribute("reportOptionForm");
    String updateflag="0";
   if(reportOptionForm!=null&&reportOptionForm.getUpdateflag()!=null){
       updateflag = reportOptionForm.getUpdateflag();
   
   }
   ReportListForm reportListForm=(ReportListForm)session.getAttribute("reportListForm");	
   UserView userView = (UserView) request.getSession().getAttribute(WebConstant.userView);
   String print =reportListForm.getPrint();  //  request.getParameter("print");
   String printFlg = request.getParameter("printFlg");
   String path = request.getParameter("path");
   String operateObject = reportListForm.getOperateObject();
   String isCheck=reportListForm.getIsCheck();
   String a_print=reportListForm.getPrint();
   String checkUnitCode = reportListForm.getCheckUnitCode();
   String rowPage="20";
   if((print!=null&&print.equals("1"))||(a_print!=null&&a_print.equals("1")))
   		rowPage="18";
   request.setAttribute("rowPage",rowPage);
   String url="/system/home.do?b_query=link";
   String target="i_body";
   String tar=userView.getBosflag();
   if(tar=="hl4")
  	  target="il_body";
  String ver = "";
  String home = request.getParameter("home");
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
   HashMap customMap = reportListForm.getCustomMap();
String realurl = request.getSession().getServletContext().getRealPath("/system/options/customreport/html");
   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
   {
  	  realurl=session.getServletContext().getResource("/system/options/customreport").getPath();//.substring(0);
     if(realurl!=null){
      if(realurl.indexOf(':')!=-1)
  	  {
		 realurl=realurl.substring(1);   
   	  }
  	  else
   	  {
		 realurl=realurl.substring(0);      
   	  }
   	  realurl = realurl+"html/";
      int nlen=realurl.length();
  	  StringBuffer buf=new StringBuffer();
   	  buf.append(realurl);
  	  buf.setLength(nlen-1);
   	  realurl=buf.toString();
   	  }
   }

realurl = URLEncoder.encode(realurl);
String sortId =request.getParameter("sortId");
String checkFlag = request.getParameter("checkFlag");
   int i=0;
   //add by wangchaoqun on 2014-9-24 查阅表数据参数加密
   String encryptParam = PubFunc.encrypt("operateObject=" + operateObject + "&operates=1&menuflag=1&status=1" + "&checkFlag=" + checkFlag);
   //add by wangchaoqun on 2014-9-25 对获取报表类别（change()方法）参数加密
   String encryptParam1 = PubFunc.encrypt("print=" + reportListForm.getPrint() + "&checkFlag=" + checkFlag 
   		+"&username=" + reportListForm.getUsername1() + "&unitcode=" + reportListForm.getCheckUnitCode()
   		+ "&ver=" + ver + "&home=" + home);
%>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="javascript">
		var print1 = "${reportListForm.print}";
		var checkflag = "<%=checkFlag%>";
		var checkunitcode = "${reportListForm.checkUnitCode}";
		var username1 = "${reportListForm.username1}";
		//alert(checkflag);
		
		//全选功能
		function full(){ 
			//alert(reportListForm.sfull.checked);
  			for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox')
			   		{	
						document.forms[0].elements[i].checked =reportListForm.sfull.checked;
					}
				}
		}
 function valideselected()
  {
      var chklist,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return false ;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;	  
	     if(chklist[i].disabled)
	     	continue;
	     if( chklist[i].checked)
  	      return true;
  	     
	  } 
	  
	  return false;  
  } 
		
		//报表类别改变后的数据联动
		function change()
		{
			var showbuttons;
			if('1' == '<%=request.getParameter("showbuttons")%>')/*add by xiegh ondate 20180206 中水联科技股份 加参数 控制领导桌面取数按钮 */
				showbuttons = "1";
			reportListForm.action="/report/auto_fill_report/reportlist.do?&b_query=link&showbuttons="+showbuttons+"&sortId=" + reportListForm.sortId.value +"&encryptParam=<%=encryptParam1%>";
			reportListForm.submit();
		}
		
		/* 汇总校验 */
		function reportCollectCheck()
		{
			if(!valideselected()){
			alert("没有选中要校验报表！");
			return;
			}
			if(checkflag == "2"){
				reportListForm.action="/report/auto_fill_report/reportinnercheckresult.do?b_reportcollectcheck=link&checkunitcode="+checkunitcode+"&checkFlag="+checkflag+"&print="+print1;
			}else{
				reportListForm.action="/report/auto_fill_report/reportinnercheckresult.do?b_reportcollectcheck=link&checkFlag="+checkflag+"&print="+print1;
			}
			var obj=eval("reportListForm.b_reportinnercheck");			
			obj.disabled=true;
			var obj2=eval("reportListForm.b_reportspacecheck");
			obj2.disabled=true;
			var obj3=eval("reportListForm.b_collectcheck");
			obj3.disabled=true;
			reportListForm.submit();
		
		}
		
		
		
		function reportInnerCheck()
		{
		if(!valideselected()){
			alert("没有选中要校验报表！");
			return;
			}
			if(checkflag == "2"){
				reportListForm.action="/report/auto_fill_report/reportinnercheckresult.do?b_reportinnercheck=link&checkunitcode="+checkunitcode+"&checkFlag="+checkflag+"&print="+print1;
			}else{
				reportListForm.action="/report/auto_fill_report/reportinnercheckresult.do?b_reportinnercheck=link&checkFlag="+checkflag+"&print="+print1;
			}
			var obj=eval("reportListForm.b_reportinnercheck");			
			obj.disabled=true;
			var obj2=eval("reportListForm.b_reportspacecheck");
			obj2.disabled=true;
		//	var obj3=eval("reportListForm.b_collectcheck");
		//	obj3.disabled=true;
			reportListForm.submit();
		}
		
		function reportSpaceCheck()
		{
		if(!valideselected()){
			alert("没有选中要校验报表！");
			return;
			}
			if(checkflag == "2"){
				reportListForm.action="/report/auto_fill_report/reportspacecheckresult.do?b_reportspacecheck=link&checkunitcode="+checkunitcode+"&checkFlag="+checkflag+"&print="+print1;
			}else{
				reportListForm.action="/report/auto_fill_report/reportspacecheckresult.do?b_reportspacecheck=link&checkFlag="+checkflag+"&print="+print1;
			}
			
			var obj=eval("reportListForm.b_reportspacecheck");
			obj.disabled=true;
			var obj2=eval("reportListForm.b_reportinnercheck");
			obj2.disabled=true;
		//	var obj3=eval("reportListForm.b_collectcheck");
		//	obj3.disabled=true;
			
			reportListForm.submit();
		}
		
		function field_Result()
		{
			reportListForm.action="/report/auto_fill_report/field_result.do?b_field_result=link";
			reportListForm.submit();
		}
		
		function expr_Result()
		{
			reportListForm.action="/report/auto_fill_report/expr_result.do?b_expr_result=link";
			reportListForm.submit();
		}
		
		function returnInfo(outparamters)
		{
			
			//info 0:成功 1:指标没有构库  2.插入数据出错  3.批量取数错误			
			var info=outparamters.getValue("info");
			var waitInfo=eval("wait");			
			waitInfo.style.display="none";
			if(info!=null)
				Ext.showAlert(info);
		}
		
		
		
		
		function batchGetData()
		{		
			
			var a=0;
			var b=0;
			var selectid=new Array();
			var a_IDs=eval("document.forms[0].IDs");	
			var nums=0;		
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
		   		{		   			
		   			nums++;
		   		}
			}
			if(nums>1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].name!='selbox'&&document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs[b].value;						
						}
						b++;
					}
				}
			}
			if(nums==1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs.value;						
						}
					}
				}
			}
			
			if(selectid.length==0)
			{
				alert("请选择相关选项！");
				return ;
			}
			var updateflag = "<%=updateflag%>";
	//	if(confirm(REPORT_UPDATE_CELL)){
	//	updateflag="1";
	//	}
			var waitInfo=eval("wait");
			waitInfo.style.display="block";
			document.getElementById('marqueeid').setAttribute("behavior","scroll");
			
		
			var hashvo=new ParameterSet();
			hashvo.setValue("selectid",selectid);
			hashvo.setValue("updateflag",updateflag);	
			hashvo.setValue("home","${reportListForm.home}");
			hashvo.setValue("operateObject","${reportListForm.operateObject}");		
			var In_paramters="flag=1"; 		
			
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:returnInfo,functionId:'03010000002'},hashvo);
	
		}
		
		function print(flag) {
			var obj1=eval("reportListForm.b_print1");
			var obj2=eval("reportListForm.b_print2");
			obj1.disabled=true;
			obj2.disabled=true;
		//    reportListForm.action="/report/edit_report/reportPrint.do?b_query=link&exportFashion="+flag;
		//    reportListForm.target="_blank";
		//	reportListForm.submit();
		
			var a=0;
			var b=0;
			var selectid=new Array();
			var a_IDs=eval("document.forms[0].IDs");	
			var nums=0;		
			for(var i=0;i<document.forms[0].elements.length;i++)
			{			
				if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
		   		{		   			
		   			nums++;
		   		}
			}
			if(nums>1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].name!='selbox'&&document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs[b].value;						
						}
						b++;
					}
				}
			}
			if(nums==1)
			{
				for(var i=0;i<document.forms[0].elements.length;i++)
				{			
					if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			   		{	
						if(document.forms[0].elements[i].checked==true)
			   			{
			   				selectid[a++]=a_IDs.value;						
						}
					}
				}
			}
			
			if(selectid.length==0)
			{
				alert(REPORT_INFO9+"！");
				
				obj1.disabled=false;
				obj2.disabled=false;
				return ;
			}	
		
		
			
			var hashvo=new ParameterSet();
			hashvo.setValue("selectedlist",selectid);	
			hashvo.setValue("exportFashion",flag);	
			hashvo.setValue("operateObject","${reportListForm.operateObject}");	
			hashvo.setValue("unitcode","${reportListForm.unitcode}");
			hashvo.setValue("username","${reportListForm.username1}");			
			var request=new Request({method:'post',asynchronous:false,
				onFailure:function(out){
					var obj1=eval("reportListForm.b_print1");
					var obj2=eval("reportListForm.b_print2");
					obj1.disabled=false;
					obj2.disabled=false;
				},onSuccess:returnInfo2,functionId:'1010020207'},hashvo);
		}
		
		function returnInfo2(outparamters)
		{	
			var path=outparamters.getValue("path");
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+path,"_self");
			
			var obj1=eval("reportListForm.b_print1");
			var obj2=eval("reportListForm.b_print2");
			obj1.disabled=false;
			obj2.disabled=false;
		
		}
		
		
		
		function goback()
		{
			
			reportListForm.action="<%=url%>";
		    reportListForm.target="<%=target%>";
			reportListForm.submit();
		}
		function openwins(id){
			//officeviewer只支持ie兼容模式
			if(!isCompatibleIE()){
				alert('请使用IE兼容模式打开！')				
				return;
			}
	    var hashvo=new ParameterSet();
	    hashvo.setValue("ispriv","1");	
       	hashvo.setValue("id",id);
       	var u = "<%=realurl%>";
       		hashvo.setValue("realurl",u);
       		
     	var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        document.getElementById('marqueeid').setAttribute("behavior","scroll");
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'10100103413'},hashvo);
	   
	}
	
	function showSelect(outparamters) { 
     var waitInfo=eval("wait");	   
     waitInfo.style.display="none";
     var url = outparamters.getValue("url");
     var filename = outparamters.getValue("filename");
     url = url + "?filename=" +filename;
     var html = document.getElementById("htmlparam");
     html.value = getDecodeStr(outparamters.getValue("htmlparam"));
     window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
     
  	}
	function openwin(id){
        var url = id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
		window.open(iframe_url,"","dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;"); 
	}
	document.body.focus();
		
	</script>
<form name="reportListForm" method="post" action="/report/auto_fill_report/reportlist.do?print=<%=print %>&sortId=<%=sortId %>&b_query=link&checkFlag=<%=checkFlag %>&unitcode=<%=checkUnitCode %>&ver=<%=ver%>&home=<%=home%>">
<input id="htmlparam" type="hidden" name="html_param"/>
 <input id="reportlisthref" type="hidden" name="reportlisthref"   />
	<table width="75%" border="0" cellspacing="0" align="center" style="margin-top: 5px;" cellpadding="0" class="RecordRow">

		<thead>
			<tr height="25">
			<%if("-2".equals(sortId)){ %>
				<td colspan="5" valign="middle">
				<%}else{ %>
				<td colspan="4" valign="middle">
				<%} %>
					<bean:message key="report.reportlist.reportsort" />
					<hrms:importgeneraldata showColumn="name" valueColumn="tsortid" flag="true" paraValue="" sql="reportListForm.dbsql" collection="list" scope="page" />

					<html:select name="reportListForm" property="sortId" size="1" style="vertical-align: middle;" onchange="javascript:change()">
						<html:option value="-1">
							<bean:message key="report.reportlist.fullreport" />
						</html:option>
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
						<%if((customMap!=null&&"0".equals(customMap.get("-1")))&&("5".equals(ver)||"1".equals(home)||"4".equals(home)||"0".equals(checkFlag))) {%>
						<html:option value="-2">
							<bean:message key="report.reportlist.customreport" />
						</html:option>
						<%} %>
					</html:select>
					&nbsp;
				</td>
			</tr>
			<tr>
				<td align="center" class="TableRow" nowrap width="10%">
            <input type="checkbox" name="selbox" onclick="batch_select(this,'reportListForm.select');" title='<bean:message key="label.query.selectall"/>'>
				</td>
				<td align="center" class="TableRow" nowrap width="10%">
					<bean:message key="report.reportlist.reportid" />
					&nbsp;
				</td>
			
				<%if("-2".equals(sortId)){ 
				%>
					<td align="center" class="TableRow" nowrap width="45%">
					<bean:message key="report.reportlist.reportname" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap width="15%">
				<bean:message key="system.options.customreport.business.module" />
				</td>
				<% 
				}else{%>
					<td align="center" class="TableRow" nowrap width="60%">
					<bean:message key="report.reportlist.reportname" />
					&nbsp;
				</td>
				<%} %>
				<td align="center" class="TableRow" nowrap width="15%">
					<bean:message key="report.reportlist.reportquery" />
					&nbsp;
				</td>
			</tr>
		</thead>
		<hrms:extenditerate id="element" name="reportListForm" property="reportListForm.list" indexes="indexes" pagination="reportListForm.pagination" pageCount="${rowPage}" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow"   onclick='tr_onclick(this,"#F3F5FC");'   >
          <%}
          else
          {%>
          <tr class="trDeep" onclick='tr_onclick(this,"#E4F2FC");'>
          <%
          }
          i++; 
          String ctrollflag ="0";  
          RecordVo vo=(RecordVo)pageContext.getAttribute("element");   
          int status=vo.getInt("paper");  
 
          String tabid = vo.getString("tabid");
          %>  
				<td align="center" class="RecordRow" nowrap>
				<% if(print!=null&&print.equals("5")&&isCheck.equalsIgnoreCase("hidden")){
						
						
						if(status==-1||status==0||status==2)
						{
						%>
						<hrms:checkmultibox name="reportListForm" property="reportListForm.select" value="true" indexes="indexes" />	
						<% 
						}
						else
						{
						//System.out.println(print+":"+isCheck+":"+status);
						ctrollflag="1";
						%>
						<input type="checkbox"  disabled="false"  name="reportListForm.select[${indexes}]" value="true">	
						<%
						}
					}
					else
					{
					if(print!=null&&print.equals("5")&&isCheck.equalsIgnoreCase("show")){
					if(status==1)
						{
						ctrollflag="1";
						}
					}
					if(status==50){
				 %>
				 <input type="checkbox"  disabled='false'  name="reportListForm.select[${indexes}]" value="true">
				 <%}else{ %>
					<hrms:checkmultibox name="reportListForm" property="reportListForm.select" value="true" indexes="indexes" />	
				<% } } %>
				</td>

				<td align="left" class="RecordRow" nowrap>
					 &nbsp;<bean:write name="element" property="string(tabid)" filter="false" />
					&nbsp;
					<input type="hidden" name="IDs" value="<bean:write name="element" property="string(tabid)" filter="false"/>" />

				</td>
				<td align="left" class="RecordRow" wrap>
					 &nbsp;<bean:write name="element" property="string(name)" filter="false" />
					&nbsp;
				</td>
				<%if("-2".equals(sortId)){ 
				if(status==50){
				if(customMap!=null&&customMap.get(tabid)!=null){
					LazyDynaBean temp=(LazyDynaBean)customMap.get(tabid);
				%>
				<td align="center" class="RecordRow" nowrap>
				<%=temp.get("moduleid") %>
				</td>
				<%} 
				}
				}%>
				<td align="center" class="RecordRow" nowrap>
				<%if(status==50){
				if(customMap!=null&&customMap.get(tabid)!=null){
					LazyDynaBean temp=(LazyDynaBean)customMap.get(tabid);	 	        		
				        		String hzname=""+temp.get("name");
				        		if("0".equals(temp.get("report_type"))){
							    		if(".xls".equals(temp.get("ext"))||".xlsx".equals(temp.get("ext"))||".xlt".equals(temp.get("ext"))||".xltx".equals(temp.get("ext"))||".htm".equals(temp.get("ext"))||".html".equals(temp.get("ext")))
							    		{
				%>
							    			<a href='javascript:openwins(<%=temp.get("id") %>)'><img src="../../images/edit.gif" border=0></a>
							    	<%} 
							    		
							    	}else if("1".equals(temp.get("report_type"))){
							    	%>	
							    		<a href='javascript:openwin("/system/options/customreport.do?b_query2=query`operateObject=1`operates=1`code=<%=temp.get("link_tabid") %>`status=1")'><img src="../../images/edit.gif" border=0></a>
							    	<%	
							    	}else if("2".equals(temp.get("report_type"))){
							    	%>	
							    		<a href='javascript:openwin("/general/card/searchcard.do?b_query2=link`home=2`inforkind=<%=temp.get("flaga") %>`result=0`tableid=<%=temp.get("link_tabid") %>")'><img src="../../images/edit.gif" border=0></a>
							    	<%
							    	}else if("3".equals(temp.get("report_type"))){
							    		if(temp.get("module")!=null&&!"".equals(temp.get("module"))){
							    		%>
							    			<a href='javascript:openwin("/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=<%=temp.get("module") %>`a_inforkind=<%=temp.get("a_inforkind") %>`result=0`isGetData=1`operateMethod=direct`costID=<%=temp.get("link_tabid") %>")'><img src="../../images/edit.gif" border=0></a>
							    		<%
							    		}
							    	}else if("4".equals(temp.get("report_type"))){//添加简单名册报表显示 guodd 2018-03-29
								%>
										<a href='javascript:openwin("/components/dataview/dataview.jsp?reportid=<%=temp.get("id") %>")'><img src="../../images/edit.gif" border=0></a>
								<%} %>
				<%
				}
				 %>
				<%}else{ /*add by xiegh on date 20180206  bug34568 中水联科技股份有限公司 
							如果 showbuttons=1指 是通过领导桌面进入  不走权限控制 
							如果参数showbuttons为null代表从报表管理进入需走权限控制
							*/
					if(null!=request.getParameter("showbuttons") && "1".equals(request.getParameter("showbuttons"))){ 
						 if(!print.equals("1")&&!print.equals("5")){
								 String temp="";
								 if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
  											temp="&ver=5&home="+home+"";
						
						 %>
							<a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&home=1&flag=2&showbuttons=1&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/><%=temp%>")'><img src="../../images/edit.gif" border=0></a>
						<% }else{
						if(print.equals("1")){
						%>
						    <a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&showbuttons=1&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/>&print=1&ver=<%=ver %>&home=<%=home %>")'><img src="../../images/edit.gif" border=0></a>
						<%
						}else{
						 %>
						    <a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&showbuttons=1&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/>&ver=<%=ver %>&home=<%=home %>")'><img src="../../images/edit.gif" border=0></a>
						<% }}
						 }else if(!"0".equals(request.getParameter("showbuttons"))){ %>
							<hrms:priv func_id="290105">
								<% if(!print.equals("1")&&!print.equals("5")){
											 String temp="";
											 if(request.getParameter("ver")!=null&&request.getParameter("ver").equals("5"))
		   											temp="&ver=5&home="+home+"";
								
								 %>
									<a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&home=1&flag=2&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/><%=temp%>")'><img src="../../images/edit.gif" border=0></a>
								<% }else{
								if(print.equals("1")){
								%>
								    <a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/>&print=1&ver=<%=ver %>&home=<%=home %>")'><img src="../../images/edit.gif" border=0></a>
								<%
								}else{
								 %>
								    <a href='javascript:openreport("/report/edit_report/reportSettree.do?b_query2=query&encryptParam=<%=encryptParam %>&ctrollflag=<%=ctrollflag %>&code=<bean:write name="element" property="string(tabid)" filter="false"/>&ver=<%=ver %>&home=<%=home %>")'><img src="../../images/edit.gif" border=0></a>
								<% }} %>
								
							</hrms:priv>
						
						<%}} %>
					&nbsp;
				</td>
			</tr>
		</hrms:extenditerate>

	</table>
<div id='wait' style='position:absolute;top:40%;left:38%;display:none;'>
		<table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style"  height=24>
					<bean:message key="report.reportlist.reportqushu"/>
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee id="marqueeid" class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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

	<table width="75%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="reportListForm" property="reportListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="reportListForm" property="reportListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="reportListForm" property="reportListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right"><hrms:paginationlink name="reportListForm" property="reportListForm.pagination" nameId="reportListForm">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>

	<table width="60%" align="center" style="margin-top: 1px;">
		<tr>
			<td align="center">
			<%if(!"-2".equals(sortId)){ %>
				<logic:equal name="reportListForm" property="print" value="1">
					<input type="button" name="b_print1" value="<bean:message key="edit_report.outPDF"/>" class="mybutton" onclick="javascript:print(1)">
					&nbsp;
				    <input type="button" name="b_print2" value="<bean:message key="general.inform.muster.output.excel"/>" class="mybutton" style="margin-left: -10px;" onclick="javascript:print(2)">
				</logic:equal> 
				<%} %>
				<logic:equal name="reportListForm" property="print" value="5">
					<logic:equal name="reportListForm" property="isCheck" value="hidden">	
					<%if(!"-2".equals(sortId)){ %>					
						<hrms:priv func_id="290100">
							<input type="button" name="b_add" value="<bean:message key="reportlist.reportplqs"/>" class="mybutton" onclick="batchGetData()">
						</hrms:priv>
						<hrms:priv func_id="290101">
							<input type="button" name="b_expr_result" value="<bean:message key="reportlist.expranalyse"/>" class="mybutton" onclick="javascript:expr_Result()">
						</hrms:priv>
						<hrms:priv func_id="290102">
							<input type="button" name="b_field_result" value="<bean:message key="reportlist.fieldanalyse"/>" class="mybutton" onclick="javascript:field_Result()">
						</hrms:priv>
						<hrms:priv func_id="290103">
							<input type="button" name="b_reportinnercheck" value="<bean:message key="reportlist.reportinnercheck"/>" class="mybutton" onclick="javascript:reportInnerCheck()">
						</hrms:priv>
						<hrms:priv func_id="290104">
							<input type="button" name="b_reportspacecheck" value="<bean:message key="reportlist.reportspacecheck"/>" class="mybutton" onClick="javascript:reportSpaceCheck()">
						</hrms:priv>
						<%} %>
						<%if(ver!=null&&ver.equals("5")){ %>
						
						<%} else{
						if(home!=null&&!home.equals("")&&!home.equals("null")){
						}else{
						%>	
						<hrms:tipwizardbutton flag="report" target="il_body" formname="reportListForm"/>
						<%}} %>
								
					</logic:equal> 
						<%if(!"-2".equals(sortId)){ %>		
					<logic:equal name="reportListForm" property="isCheck" value="show">
						<input type="button" name="b_reportinnercheck" value="<bean:message key="reportlist.reportinnercheck"/>" class="mybutton" onclick="javascript:reportInnerCheck()">
						<input type="button" name="b_reportspacecheck" value="<bean:message key="reportlist.reportspacecheck"/>" class="mybutton" onClick="javascript:reportSpaceCheck()">
						<logic:equal name="reportListForm" property="checkFlag" value="2">
						<hrms:priv func_id="2903224">
						<input type="button" name="b_collectcheck" value="<bean:message key="report_collect.collectValidate"/>" class="mybutton" onClick="javascript:reportCollectCheck()"> 
						</hrms:priv>
						 </logic:equal>
						<input type="hidden" name="chackFlag"  value="${reportListForm.checkFlag}">
						<input type="hidden" name="checkUnitCode"  value="${reportListForm.checkUnitCode}">
					</logic:equal> 	
					<%} %>
				</logic:equal>
				
				<logic:notEqual name="reportListForm" property="print" value="1" >
					<logic:notEqual name="reportListForm" property="print" value="5" >
						<input type="button"  class="mybutton" value="<bean:message key="reportcheck.return"/>"  onclick='goback()' >
					</logic:notEqual>
					<logic:equal name="reportListForm" property="print" value="5" >
					<%if(ver!=null&&ver.equals("5")){ %>
						<input type="button"  class="mybutton" value="<bean:message key="reportcheck.return"/>"  onclick='goback()' >
						<%}else{ if(home!=null&&!home.equals("")&&!home.equals("null")){
						 %>
						 <input type="button"  class="mybutton" value="<bean:message key="reportcheck.return"/>"  onclick='goback()' >
						 <%}}
						 %>
					</logic:equal>
				</logic:notEqual>
				
			</td>
		</tr>
	</table>
	<script language="javascript">
	function openreport(url){
	var href = window.location.href;
	replaceAll(href,"&","`");
	href =$URL.encode(href); //
	/*liuy 2015-1-29 简化替换所有
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");
	href = href.replace("&","`");*/
	window.location.href = url+"&reportlisthref="+href;
	}
	function replaceAll(str, from, to){
		var idx = str.indexOf(from);
		while (idx > -1) {
			str = str.replace(from,to);
			idx = str.indexOf(from);
		}
		return str;
	}
	window.onload=function(){
		 var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串  
        var isIE = userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1; 
		if(isIE){
			var divs=document.getElementsByTagName("div")
			for(var i=0;i<divs.length;i++){
				if(divs[i].getAttribute("name")=="bottom_div_name"){
					divs[i].style.posTop=divs[i].style.posTop+2;
				}
			}
		}		
	}
	</script>
</form>

