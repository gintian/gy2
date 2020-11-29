	<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.sys.customreport.CustomReportForm"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.net.URLEncoder"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="../../../../module/utils/js/template.js"></script>
<%CustomReportForm form=(CustomReportForm)session.getAttribute("customReportForm"); 
String realurl = request.getSession().getServletContext().getRealPath("/system/options/customreport/html");
   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
   {
  	  realurl=session.getServletContext().getResource("/system/options/customreport").getPath();//.substring(0);
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

realurl = URLEncoder.encode(realurl);
%>
<script type="text/javascript">
<!--
	function add() {
		customReportForm.action = "/system/options/customreport.do?b_add=link";
		customReportForm.submit();
	}
	
	function change() {		
		customReportForm.action="/system/options/customreport.do?b_search=link&encryptParam=<%=PubFunc.encrypt("toFirst=yes")%>";
		customReportForm.submit();
	}
	
	function deleted() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
		if (confirm("您确定要删除所选记录？")) {		
			customReportForm.action="/system/options/customreport.do?b_delete=link";
			customReportForm.submit();
		}
	}
	
	function publish() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
		if (confirm("您确定要删除所选记录？")) {		
			customReportForm.action="/system/options/customreport.do?b_delete=link";
			customReportForm.submit();
		}
	}
	
	function release() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
			
		customReportForm.action="/system/options/customreport.do?b_releaseorpause=link&encryptParam=<%=PubFunc.encrypt("flag=1")%>";
		customReportForm.submit();

	}
	
	function pause() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
			
		customReportForm.action="/system/options/customreport.do?b_releaseorpause=link&encryptParam=<%=PubFunc.encrypt("flag=0")%>";
		customReportForm.submit();

	}
	
	function checkSelect() {
		check = false;
		input = document.getElementsByTagName("input");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "selbox") {
				check =true;
				break;
			} 	
		}
		return check;
	}
	
	function power() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
		var input = document.getElementsByTagName("input");
		var num = 0;
		var ids = "";
		for (i = 0; i < input.length; i++) {
			var obj = input[i];
			if (obj.type == "checkbox" && obj.name != "selbox") {
				if (obj.checked == true) {
					num++;
					var checkname = obj.name;
					var start = checkname.indexOf("[");
					var end = checkname.indexOf("]");
					var idname = checkname.substr(start+1,end-start-1);
					ids = document.getElementById(idname).value;
				}
			}
		}
		var dat = new Date();
		var url = "/system/options/customreport.do?b_power=link&num="+num+"&tabid="+ids+"&isself=0&isShowFullName=0&time="+dat.getTime();
// 		var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
// 		var return_vo= window.showModalDialog(url,0,
//         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
// //        window.alert(return_vo.title+"----"+return_vo.content);
// 		if (return_vo != null) {
// 			customReportForm.action="/system/options/customreport.do?b_priv=link&num="+num+"&privuser="+getEncodeStr(return_vo.content);
// 			customReportForm.submit();
// 		}
        return_vo ='';
        var theUrl = url;
        Ext.create('Ext.window.Window', {
            id:'power',
            height: 430,
            width: 300,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
            renderTo:Ext.getBody(),
            listeners:{
                'close':function () {
                    if (return_vo) {
                        customReportForm.action = "/system/options/customreport.do?b_priv=link&num=" + num + "&privuser=" + $URL.encode(getEncodeStr(return_vo.content));
                        customReportForm.submit();
                    }
                }}

        }).show();
	}
	
	// 按角色授权
	function juesepower() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
		var input = document.getElementsByTagName("input");
		var num = 0;
		var ids = "";
		for (i = 0; i < input.length; i++) {
			var obj = input[i];
			if (obj.type == "checkbox" && obj.name != "selbox") {
				if (obj.checked == true) {
					num++;
					var checkname = obj.name;
					var start = checkname.indexOf("[");
					var end = checkname.indexOf("]");
					var idname = checkname.substr(start+1,end-start-1);
					ids = ids +","+ document.getElementById(idname).value;
				}
			}
		}
		
		if (ids.length > 0) {
			ids = ids.substr(1); 
		}
		var dat = new Date();
		var target_url;
     	var winFeatures = "dialogHeight:400px; dialogLeft:320px;";
     	target_url="/system/options/customreport/addjuese.do?b_addjuese=link`tabid="+ids+"`num="+num+"`time="+dat.getTime();
     	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url)+"&ti="+dat.getTime();
     	// var dw=600,dh=550,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     	// var return_vo= window.showModalDialog(iframe_url,1,
         // "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
     	// if(!return_vo)
			// return false;
     	// //应用设置、数据交换、报表关联、角色授权、保存 出现err.msgRouter.(MRMapping.xml)readXmlError   jingq upd 2014.09.29
	    // //customReportForm.action ="/system/options/customreport.do?b_savejuese=link&tabid="+ids+"&a_base_ids="+return_vo.role_id+"&num="+num;
	    // customReportForm.action ="/system/options/customreport/addjuese.do?b_savejuese=link&tabid="+ids+"&a_base_ids="+return_vo.role_id+"&num="+num;
     	// customReportForm.submit();
		var width ="";
		var height = "";
		if(getBrowseVersion()=='10'){
			width = 615;
			height = 595;
		}else{
			width = 607;
			height = 570;
		}
        return_vo ='';
        var theUrl = iframe_url;
        Ext.create('Ext.window.Window', {
            id:'juesepower',
            height: height,
            width: width,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
            renderTo:Ext.getBody(),
            listeners:{
                'close':function () {
                    if (return_vo) {
                        customReportForm.action ="/system/options/customreport.do?b_savejuese=link&tabid="+ids+"&a_base_ids="+return_vo.role_id+"&num="+num;
                        customReportForm.action ="/system/options/customreport/addjuese.do?b_savejuese=link&tabid="+ids+"&a_base_ids="+return_vo.role_id+"&num="+num;
                        customReportForm.submit();
                    }
                }}

        }).show();
	}
	
	function openwin(id){
	    //var hashvo=new ParameterSet();
	    //hashvo.setValue("ispriv","1");	
       	//hashvo.setValue("id",id);
     	//var waitInfo=eval("wait");	   
        //waitInfo.style.display="block";
        //var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'10100103413'},hashvo);
        var url = id;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
	  // window.showModalDialog(iframe_url,"","dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;location=no;toolbar=no;");
        var theUrl = iframe_url;
        Ext.create('Ext.window.Window', {
            id:'reportOutput',
            height: window.screen.height-200,
            width: window.screen.width-200,
            resizable:false,
            modal:true,
            autoScroll:false,
            autoShow:true,
            html:'<iframe style="background-color:#ffffff " frameborder="0" scrolling="no" height="100%" width="100%" src="'+theUrl+'"></iframe>',
            renderTo:Ext.getBody()
        }).show();
	   
	}
	function openwins(id){
	    var hashvo=new ParameterSet();
	    hashvo.setValue("ispriv","1");	
       	hashvo.setValue("id",id);
       	var u = document.getElementById("realurl").value;
       		hashvo.setValue("realurl",u);
     	var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'10100103413'},hashvo);
	   
	}
	function openSimpleReportWins(id){
		window.open("/components/dataview/dataview.jsp?reportid="+id,"_blank","left=0,top=0,width="+window.clientWidth+",height="+window.clientHeight+",scrollbars=yes,toolbar=no,menubar=no,location=yes,resizable=yes,status=no");
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
	//打开人员花名册
	function showOpenMusterOne(tabid){
	  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
	  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link&nFlag=3&a_inforkind=1&result=0&isGetData=1&operateMethod=direct&costID="+tabid;     	  
	  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+thecodeurl;
	  var return_vo= window.showModalDialog(iframe_url,theArr, 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
	}
	
	//打开机构花名册
	function showOpenMusterTwo(tabid) {
	  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
	  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=21`a_inforkind=2`result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
	  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+thecodeurl;
	  var return_vo= window.showModalDialog(iframe_url,theArr, 
	              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
  
	}
	
	//打开职位花名册
	function showOpenMusterThree(tabid) {
	  var theArr=new Array(focus_obj_node.parent.text,focus_obj_node.text); 
	  var thecodeurl ="/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=41`a_inforkind=3&result=0`isGetData=1`operateMethod=direct`costID="+tabid;     	  
	  var iframe_url="/gz/gz_analyse/gz_analyse_iframe.jsp?src="+thecodeurl;
	  var return_vo= window.showModalDialog(iframe_url,theArr,  "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;"); 
	}
	
	function openprint(id ,type)
	{
	   if(type=="0")//自定义表
	   {
	      var hashvo=new ParameterSet();
	      hashvo.setValue("id",id);	
	      hashvo.setValue("ispriv","1");
	      var request=new Request({method:'post',asynchronous:false,onSuccess:showReport,functionId:'10100103411'},hashvo);
	   }
	}
	function showReport(outparamters)
   {
      var ext=outparamters.getValue("ext");
	  var filename=outparamters.getValue("filename");	
	  if(ext.indexOf('xls')!=-1 ||ext.indexOf('xlt')!=-1)
	  {
	     customReportForm.action="/servlet/DisplayExcelCustomReport?filename="+filename;
	     customReportForm.submit();
	  }
	  
	}
	
	// 下载模板文件和sql条件
	function downloadReport(id,type) {
		var hashvo=new ParameterSet();
	    hashvo.setValue("id",id);	
	    hashvo.setValue("type",type);
	    var request=new Request({method:'post',asynchronous:false,onSuccess:showdownloadfile,functionId:'10100103412'},hashvo);
	}
	
	function showdownloadfile(outparamters) {
		var filename=outparamters.getValue("filename");
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+filename);
	}

	function winClose() {//报表输出关闭函数
		// parent.return_vo = '';
		if (Ext.getCmp('reportOutput')) {
			Ext.getCmp('reportOutput').close();
		}
	}
		
//-->
</script>
<%int i = 0;
int m = 5;
%>
<hrms:priv func_id="3001G06">
	<%m++; %>
</hrms:priv>
<hrms:priv func_id="3001G07">
	<%m++; %>
</hrms:priv>
<hrms:priv func_id="3001G08">
	<%m++; %>
</hrms:priv>
<html:form action="/system/options/customreport">
	<input id="htmlparam" type="hidden" name="html_param"/>
	<input type="hidden" id="realurl" name="realurl" value="<%=realurl %>"/>
	<input type="hidden" id="ids" name="ids"/>
<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
 <tr>
  <td>
      <table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr align="center" nowrap >
			<td align="left" nowrap style="padding-bottom:5px;" colspan="<%=m %>">
				&nbsp;<bean:message key="system.options.customreport.business.module" />				
				<html:select name="customReportForm" property="businessModuleValue" size="1" onchange="change()">
					<html:optionsCollection property="businessModuleList" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>		
		<tr>
			<td align="center" class="TableRow" width="4%" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'customReportForm.select');" title='<bean:message key="label.query.selectall"/>'>	    
			</td>
			<td align="center" class="TableRow" width="8%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.number" />
			</td>
			<td align="center" class="TableRow" width="30%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.reporttablename" />
			</td>
			<td align="center" class="TableRow" width="14%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.release.state" />
			</td>
			<hrms:priv func_id="3001G06">
			<td align="center" class="TableRow" width="10%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.edite" />
			</td>
			</hrms:priv>
			<hrms:priv func_id="3001G07">
			<td align="center" class="TableRow" width="12%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.downmodel" />
			</td>
			</hrms:priv>
			<hrms:priv func_id="3001G08">
			<td align="center" class="TableRow" width="12%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.downsql" />
			</td>
			</hrms:priv>
			<td align="center" class="TableRow" width="10%" nowrap>
				<bean:message key="system.options.customreport.tableinfo.reportexport" />
			</td>
		</tr>
		<hrms:extenditerate id="element" name="customReportForm" property="customReportForm.list" indexes="indexes" pagination="customReportForm.pagination" pageCount="10" scope="session">
			<%if (i % 2 == 0) {%>
			<tr class="trShallow">
			<%} else {%>
			<tr class="trDeep">
			<%
			}
				i++;
			%>
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="customReportForm" property="customReportForm.select" value="true" indexes="indexes"/>					
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="string(id)" filter="true" />
					<input type="hidden" id="<bean:write name="indexes"/>" name="<bean:write name="indexes"/>" value="<bean:write name="element" property="string(id)" filter="true" />"/>
					&nbsp;
				</td>
				<td align="left" class="RecordRow" nowrap>
					&nbsp;
					<bean:write name="element" property="string(name)" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<logic:equal name="element" property="string(flag)" value="1">
						<bean:message key="system.options.customreport.tableinfo.hasrelease" />
					</logic:equal>
					<logic:equal name="element" property="string(flag)" value="0">
						<bean:message key="system.options.customreport.tableinfo.norelease" />
					</logic:equal>
					&nbsp;
				</td>
				<hrms:priv func_id="3001G06">
				<%
					RecordVo vo = (RecordVo)pageContext.getAttribute("element");
					String id = vo.getString("id");
				 %>
				<td align="center" class="RecordRow" nowrap>
					<a href="/system/options/customreport.do?b_edit=link&encryptParam=<%=PubFunc.encrypt("id="+id)%>"><img src="/images/edit.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.edite" />"/></a>
				</td>
				</hrms:priv>
				<hrms:priv func_id="3001G07">
				<td align="center" class="RecordRow" nowrap>
					<logic:empty name="element" property="string(ext)">
					&nbsp;
					</logic:empty>
					<logic:notEmpty name="element" property="string(ext)">
						<a href="javascript:downloadReport('<bean:write name="element" property="string(id)" filter="true" />','<bean:write name="element" property="string(ext)" filter="true" />')""><img src="/images/down01.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.downmodel" />"/></a>
					</logic:notEmpty>
				</td>
				</hrms:priv>
				<hrms:priv func_id="3001G08">
				<td align="center" class="RecordRow" nowrap>
					<logic:empty name="element" property="string(sqlfile)">
					&nbsp;
					</logic:empty>
					<logic:notEmpty name="element" property="string(sqlfile)">
						<a href="javascript:downloadReport('<bean:write name="element" property="string(id)" filter="true" />','xml')"><img src="/images/down01.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.downsql" />"/></a>
					</logic:notEmpty>
				</td>
				</hrms:priv>
				<td align="center" class="RecordRow" nowrap>
					<% 	String module = "";
						HashMap map = form.getHMap();
						module = (String) map.get(((RecordVo)element).getInt("report_type")+":"+((RecordVo)element).getInt("link_tabid"));
						String flaga = "";
						if (((RecordVo)element).getInt("report_type") == 2) {
							HashMap fmap = form.getFMap();
							flaga = (String) fmap.get(((RecordVo)element).getInt("report_type")+":"+((RecordVo)element).getInt("link_tabid"));
							if("A".equals(flaga))
		    					flaga="1";
		    				else if("B".equals(flaga))
		    					flaga="2";
		    				else if("K".equals(flaga))
		    					flaga="4";
	    				}
					 %>
					<logic:equal name="element" property="string(report_type)" value="0">
						<logic:equal name="element" property="string(ext)" value=".xls">
							<!--<a href="javascript:openprint('<bean:write name="element" property="string(id)" filter="true"/>','<bean:write name="element" property="string(report_type)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>-->
							  <a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".xlsx">
							<!--<a href="javascript:openprint('<bean:write name="element" property="string(id)" filter="true"/>','<bean:write name="element" property="string(report_type)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>-->
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".xlt">
							<!--<a href="javascript:openprint('<bean:write name="element" property="string(id)" filter="true"/>','<bean:write name="element" property="string(report_type)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>-->
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".xltx">
							<!--<a href="javascript:openprint('<bean:write name="element" property="string(id)" filter="true"/>','<bean:write name="element" property="string(report_type)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>-->
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".htm">
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".html">
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
						<logic:equal name="element" property="string(ext)" value=".mht">
							<a href="javascript:openwins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:equal>
					</logic:equal>
					<logic:equal name="element" property="string(report_type)" value="1">
						<a href="javascript:;" onclick="openwin('/system/options/customreport.do?b_query2=query`operateObject=1`operates=1`code=<bean:write name="element" property="string(link_tabid)" filter="true"/>`status=1')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
					</logic:equal>
					<logic:equal name="element" property="string(report_type)" value="2">					
						<a href="javascript:;" onclick="openwin('/general/card/searchcard.do?b_query2=link`home=2`inforkind=<%=flaga %>`result=0`tableid=<bean:write name="element" property="string(link_tabid)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>						
					</logic:equal>
					<logic:equal name="element" property="string(report_type)" value="3">
						<%
						if (module != null){
						if (module.equals("3")) {%>
							<a href="javascript:;" onclick="openwin('/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=3`a_inforkind=1`result=0`isGetData=1`operateMethod=direct`costID=<bean:write name="element" property="string(link_tabid)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						<%}else if(module.equals("21")) {%>
							<a href="javascript:;" onclick="openwin('/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=21`a_inforkind=2`result=0`isGetData=1`operateMethod=direct`costID=<bean:write name="element" property="string(link_tabid)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						<%} else if(module.equals("41")) { %>
							<a href="javascript:;" onclick="openwin('/general/muster/hmuster/select_muster_name.do?b_custom=link`nFlag=41`a_inforkind=3`result=0`isGetData=1`operateMethod=direct`costID=<bean:write name="element" property="string(link_tabid)" filter="true"/>')"><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						<%} 
						}
						%>
					</logic:equal>
					<logic:equal name="element" property="string(report_type)" value="4">
						<logic:notEmpty name="element" property="string(sqlfile)">
							<a href="javascript:openSimpleReportWins('<bean:write name="element" property="string(id)" filter="true"/>')"/><img src="/images/compute.gif" border="0" alt="<bean:message key="system.options.customreport.tableinfo.reportexport" />"/></a>
						</logic:notEmpty>
					</logic:equal>
					
				</td>
			</tr>
		</hrms:extenditerate>
	</table>
  </td>
  </tr>
  <tr>
  <td>
     <table width="100%" align="center" class="RecordRowP">
		<tr>
			<td valign="bottom" class="tdFontcolor">
		         <bean:message key="label.page.serial"/>
				<bean:write name="customReportForm" property="customReportForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
				<bean:write name="customReportForm" property="customReportForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
				<bean:write name="customReportForm" property="customReportForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>   
			</td>
			<td align="right" nowrap class="tdFontcolor">

				<p align="right">
					<hrms:paginationlink name="customReportForm" property="customReportForm.pagination" nameId="customReportForm" propertyId="roleListProperty">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
  </td>
  </tr>
</table>
	
	
	<table width="85%" align="center">
		<tr>
			<td align="center" height="35px;">
				<hrms:priv func_id="3001G01">
				<input type="button" name="b_add" value="<bean:message key="button.insert"/>" class="mybutton" onclick="add()"/>	
				</hrms:priv>
				<hrms:priv func_id="3001G02">
				<input type="button" name="b_release" value="<bean:message key="system.options.customreport.button.release"/>" class="mybutton" onclick="release()"/>
				</hrms:priv>
				<hrms:priv func_id="3001G03">
				<input type="button" name="b_pause" value="<bean:message key="system.options.customreport.button.pause"/>" class="mybutton" onclick="pause()"/>
				</hrms:priv>
				<hrms:priv func_id="3001G04">
				<input type="button" name="b_power" value="<bean:message key="system.options.customreport.button.power"/>" class="mybutton" onclick="power()"/>				
				</hrms:priv>
				<hrms:priv func_id="3001G09">
				<input type="button" name="b_delete" value="<bean:message key="system.options.customreport.button.juesepower" />" class="mybutton" onclick="juesepower()"/>
				</hrms:priv>
				<hrms:priv func_id="3001G05">
				<input type="button" name="b_delete" value="<bean:message key="button.delete" />" class="mybutton" onclick="deleted()"/>
				</hrms:priv>
			</td>
		</tr>
	</table>
	<div id='wait' style='position:absolute;top:200;left:450;display:none;'>
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
</html:form>

