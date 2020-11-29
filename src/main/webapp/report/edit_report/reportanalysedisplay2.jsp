
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<link href="/css/css1_report.css" rel="stylesheet" type="text/css">

<script language="javascript" src="/js/page_options.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<script language="javascript">	
		//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题

 
function document._document_oncontextmenu() 
{
var e = window.event;
return false; 
}
		function collect()
		{
		    var url="/report/report_analyse/reportanalyse.do?b_collectData=collect&unitCode="+ra.codeFlag.value+"&tabid="+ra.reportTabid.value+"&yearid="+ra.reportYearid.value
			window.open(url,"_blank");
			
		}
		
		
		
		<!--改变年份ajax联动-->
		function yearChange(){
			var reportTypes="${editReportAnalyseForm.reportTypes}";
			if(reportTypes!='6')
			{
				var hashvo=new ParameterSet();
			    hashvo.setValue("unitCode",ra.codeFlag.value);
			    hashvo.setValue("tabid",ra.reportTabid.value);
			    hashvo.setValue("yearid",ra.reportYearid.value);
			    
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:yearChangeResult,functionId:'03020000019'},hashvo);			
			}
			else
			{
				ra.action="/report/edit_report/reportanalyse.do?b_query2=link&opt=year&tabid=${editReportAnalyseForm.reportTabid}&code=${editReportAnalyseForm.codeFlag}&scopeid="+ra.scopeid2.value;
			  	ra.target="mil_body";
			    ra.submit();
			}
		}
	
		
		function yearChangeResult(outparamters){
			
			//格式：null$$1##2##2.5@1##3##2.5
			//     1##1次@2##2次$$1##2##2.5@1##3##2.5
			
			var info = outparamters.getValue("info");
			//alert(info);
			arrays = info.split("$$");
			if(arrays[0] == "null"){
				dbarray = arrays[1].split("@");
				for(var i = 0; i< dbarray.length ; i++){
					dba = dbarray[i].split("##");
					var a_object=eval("document.ra.a"+dba[0]+"_"+dba[1]);
					if(dba[2]!='0'){
						a_object.value=dba[2];
					}else{
						a_object.value='';
					}
				}
				
			}else{
				
				var rc= eval("document.ra.reportCount"); 
				arrayrc = arrays[0].split("@");			
				for(var i=rc.options.length-1;i>=0;i--){
				   var no =rc.options[i];	
					rc.removeChild(no);
				}
				
				for(var i=0; i<arrayrc.length;i++){
					var noo = new Option();
					temp = arrayrc[i].split("##");
					noo.value=temp[1] ;
					noo.text= temp[0];
					rc.options[rc.options.length]=noo;
				}
				
				dbarray = arrays[1].split("@");
				for(var i = 0; i< dbarray.length ; i++){
					dba = dbarray[i].split("##");
					var a_object=eval("document.ra.a"+dba[0]+"_"+dba[1]);
					if(dba[2]!='0'){
						a_object.value=dba[2];
					}else{
						a_object.value='';
					}
				}
			}
			

		}
		
		<!--改变参数ajax联动-->
		function countChange(){
			//alert(ra.reportCount.value);
			var reportTypes="${editReportAnalyseForm.reportTypes}";
			if(reportTypes!='6')
			{
				var hashvo=new ParameterSet();
				
			    hashvo.setValue("unitCode",ra.codeFlag.value);
			    hashvo.setValue("tabid",ra.reportTabid.value);
			    hashvo.setValue("yearid",ra.reportYearid.value);
			    hashvo.setValue("countid",ra.reportCount.value);
			    
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03020000021'},hashvo);			
			}
			else
			{
				ra.action="/report/edit_report/reportanalyse.do?b_query2=link&opt=count&tabid=${editReportAnalyseForm.reportTabid}&code=${editReportAnalyseForm.codeFlag}&scopeid="+ra.scopeid2.value;
			  	ra.target="mil_body";
			    ra.submit();
			}
		}
		
		
		
		function weekChange()
		{
				var hashvo=new ParameterSet();
			    hashvo.setValue("unitCode","${editReportAnalyseForm.codeFlag}");
			    hashvo.setValue("tabid","${editReportAnalyseForm.reportTabid}");
			    hashvo.setValue("yearid",ra.reportYearid.value);
			    hashvo.setValue("countid",ra.reportCount.value);
			    hashvo.setValue("weekid",ra.weekid.value);
			    hashvo.setValue("reportTypes","${editReportAnalyseForm.reportTypes}");
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03040000007'},hashvo);			
		
		}
		
		
		function countChangeResult(outparamters){
			var info = outparamters.getValue("info");
			var rows=outparamters.getValue("rows");
			var cols=outparamters.getValue("cols");
			
			for(var i=0;i<rows;i++)
			{
				for(var j=0;j<cols;j++)
				{
					var a_object=eval("document.ra.a"+i+"_"+j);
					if(a_object)
						a_object.value='';
				}
			}
			
			//alert(info);
			dbarray = info.split("@");
			for(var i = 0; i< dbarray.length ; i++){
				dba = dbarray[i].split("##");
				var a_object=eval("document.ra.a"+dba[0]+"_"+dba[1]);
				
				//alert(dba[0] + " " + dba[1] + " =" + dba[2]);
				
				if(dba[2]!='0'){
					a_object.value=dba[2];
				}else{
					a_object.value='';
				}
			}
		}
			
		
		var selectGrid="";
			
		function changeGrid( i,  j){
			var var_name="aa"+i+"_"+j;
			var scopeids="";
				if(parent.ril_body2.document.editReportAnalyseForm.showFlag.value=="2"){
			if(trim(parent.ril_body2.document.editReportAnalyseForm.scopeids.value).length==0)
				{
					alert("请选择口径!");
					return;
				}
				else{
					scopeids=parent.ril_body2.document.editReportAnalyseForm.scopeids.value;
					document.ra.showFlag.value = "2";
					}
			}
			if(parent.ril_body2.document.editReportAnalyseForm.showFlag.value=="1"){
			document.ra.showFlag.value = "1";
				if(selectGrid.indexOf("/"+j+","+i+"/")==-1)
				{
				   if(selectGrid.length>2)
					{
						var temp=selectGrid.substring(1,selectGrid.length-1);
						var temps=temp.split("/");
//取消最多选择4个单元格		if(temps.length>3)
//						{
//							alert(REPORT_INFO30);
//							return;
//						}
					}
				
					
					var obj=document.getElementById(var_name);
					if(obj!=null)
						obj.style.background="yellow";
				
					if(selectGrid.length==0)
						selectGrid="/"+j+","+i+"/";				
					else
						selectGrid=selectGrid+j+","+i+"/";
				}
				else
				{
					var obj=document.getElementById(var_name);
					if(obj!=null)
						obj.style.background="#ffffff";
					selectGrid=replaceAll(selectGrid,"/"+j+","+i+"/", "/");
				}
			}else
			{
				if(selectGrid.length>1)
				{
					var temp=selectGrid.split(",");
					var obj=document.getElementById("aa"+temp[1].replace("/","")+"_"+temp[0].replace("/",""));
					if(obj!=null)
						obj.style.background="#ffffff";
				}
				var obj=document.getElementById(var_name);
				if(obj!=null)
					obj.style.background="yellow";
				selectGrid=j+","+i
			}
			
		
		/*	ra.target="ril_body2";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeGrid=link&code="
					+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&rc="+selectGrid+"&w="
					+image_width+"&h="+image_height+"&unitcodes="+unitcodes;
		   ra.submit();
		   */
		  
		   ra.target="ril_body2";
			ra.action="/report/edit_report/reportdisplay.do?b_changeGrid2=link&code="
				+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&rc="+selectGrid+"&scopeids="+scopeids+"&scopeid="+ra.scopeid2.value;
			ra.submit();
		}
		
		function changeGrid2( i,  j){
		var var_name="aa"+i+"_"+j;
			if(selectGrid.indexOf("/"+j+","+i+"/")==-1)
				{
				   if(selectGrid.length>2)
					{
						var temp=selectGrid.substring(1,selectGrid.length-1);
						var temps=temp.split("/");
//取消最多选择4个单元格		if(temps.length>3)
//						{
//							alert(REPORT_INFO30);
//							return;
//						}
					}
				
					
					var obj=document.getElementById(var_name);
					if(obj!=null)
						obj.style.background="yellow";
				
					if(selectGrid.length==0)
						selectGrid="/"+j+","+i+"/";				
					else
						selectGrid=selectGrid+j+","+i+"/";
				}
				else
				{
					var obj=document.getElementById(var_name);
					if(obj!=null)
						obj.style.background="#ffffff";
					selectGrid=replaceAll(selectGrid,"/"+j+","+i+"/", "/");
				}
			//	 ra.target="ril_body2";
			//ra.action="/report/edit_report/reportdisplay.do?b_changeGrid2=link&code="
			//	+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&rc="+selectGrid+"&scopeids=";
			//ra.submit();
			parent.ril_body2.location.href ="/report/edit_report/reportdisplay.do?b_changeGrid2=link&w="+wid+"&h="+hei+"&rc="+selectGrid+"&code="+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&scopeid="+ra.scopeid2.value;
		}
		
		
		function replaceAll( str, from, to ) {
		    var idx = str.indexOf( from );
		    while ( idx > -1 ) {
		        str = str.replace( from, to ); 
		        idx = str.indexOf( from );
		    }
		   
		    return str;
		}
		
		
		
		var wid,hei;
		function load(){  
		var columnflag = '${editReportAnalyseForm.columnflag}';
			if(columnflag=="1")
			alert("该表的结构已修改，请维护表结构！");	
			//页面重定向
			var is = new Is();
		var available_width = 750;
		var available_height = 250;
		var image_width = 750;
		var image_height = 120;
	    if(is.ns4||is.ns6) {
	        available_width=innerWidth;
	        available_height=innerHeight;
	    } else if(is.ie4||is.ie5||is.ieX) {
	        available_width=document.body.clientWidth;
	        available_height=document.body.clientHeight;
	    }
	    if(is.ie4 ||is.ie5||is.ieX||is.ns6|| is.ns4) {
	    	
	    	available_width = available_width*0.92;
	    	available_height = available_height*0.92;
	    	
	    	available_width = ""+available_width;
	    	available_height =""+available_height;
	    	
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}

		}
			image_width = (available_width);
			image_height = (available_height);
			 wid = image_width;
			 hei = image_height;
		var selectgrid2="";
			if(selectGrid.length==0){
			selectgrid2="/1,1/";	
			 }else
			 selectgrid2=selectGrid;
			//parent.ril_body2.location.href ="/report/edit_report/reportdisplay.do?b_changeFrame2=link&w="+wid+"&h="+hei+"&rc="+selectgrid2;
			 changeGrid2( 1,1);
		}
		
	function selectRowOrColumn(){
	}
	function clearSelected(){
	}	
	function Is() {
	    var agent   = navigator.userAgent.toLowerCase();
	    this.major  = parseInt(navigator.appVersion);
	    this.minor  = parseFloat(navigator.appVersion);
	    this.ns     = ((agent.indexOf('mozilla')   != -1) &&
	                  (agent.indexOf('spoofer')    == -1) &&
	                  (agent.indexOf('compatible') == -1) &&
	                  (agent.indexOf('opera')      == -1) &&
	                  (agent.indexOf('webtv')      == -1));
	    this.ns2    = (this.ns && (this.major      ==  2));
	    this.ns3    = (this.ns && (this.major      ==  3));
	    this.ns4    = (this.ns && (this.major      ==  4));
	    this.ns6    = (this.ns && (this.major      >=  5));
	    this.ie     = (agent.indexOf("msie")       != -1);
	    this.ie3    = (this.ie && (this.major      <   4));
	    this.ie4    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   == -1));
	    this.ie5    = (this.ie && (this.major      ==  4) &&
	                  (agent.indexOf("msie 5.0")   != -1));
	    this.ieX    = (this.ie && !this.ie3 && !this.ie4);
	}	
	<!--改变报表刷新页面-->
		function tabidChange()
		{
			ra.target="ril_body1";
			ra.action="/report/edit_report/reportdisplay.do?b_display2=link&tabid="+ra.reportchangeTabid.value ;
			ra.submit();
		}
	function scopeChange(){
		//alert(ra.scopeid.value);
		ra.target="ril_body1";
			ra.action="/report/edit_report/reportdisplay.do?b_display2=link&tabid="+ra.reportchangeTabid.value+"&scopeid="+ra.scopeid.value ;
			ra.submit();
   		}	
   			function resetSelectGrid(showFlag)
		{
			if(showFlag=="2"&&selectGrid.length>2)
			{
				selectGrid=selectGrid.substring(1,selectGrid.length-1);
				var grids=selectGrid.split("/");
				for(var i=0;i<grids.length;i++)
				{
					var temp=grids[i].split(",");
					var obj=document.getElementById("aa"+temp[1]+"_"+temp[0]);
					if(obj!=null)
						obj.style.background="#ffffff";
				}
			}
			else if(showFlag=="1"&&selectGrid.length>2)
			{
					var temp=selectGrid.split(",");
					var obj=document.getElementById("aa"+temp[1]+"_"+temp[0]);
					if(obj!=null)
						obj.style.background="#ffffff";
			}
			selectGrid="";
		}
		function exportExcel()
		{
			
			var hashvo=new ParameterSet();
			
		    hashvo.setValue("unitCode",ra.codeFlag.value);
		    hashvo.setValue("tabid",ra.reportTabid.value);
		    hashvo.setValue("yearid",ra.reportYearid.value);
		    if(ra.reportCount)
			    hashvo.setValue("countid",ra.reportCount.value);
			else 
				 hashvo.setValue("countid","");
				 hashvo.setValue("scopeid",ra.scopeid2.value);
			var reportTypes="${editReportAnalyseForm.reportTypes}";
			if(reportTypes=='6')
				  hashvo.setValue("weekid",ra.weekid.value);
			hashvo.setValue("reportTypes",reportTypes);
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'03040000013'},hashvo);			
		}
		
		function returnInfo(outparamters)
		{
			var outName=outparamters.getValue("outName");
		     window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	
		}
</script>
<body onload="load()">
<form name="ra" method="post" action="/report/edit_report/reportdisplay.do">
	<table width="850" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
		<tr>
			<td>
				<input type="hidden" name="codeFlag" value="<bean:write name="editReportAnalyseForm" property="codeFlag" filter="true" />">
				<input type="hidden" name="reportTabid" value="<bean:write name="editReportAnalyseForm" property="reportTabid" filter="true" />">
				<input type="hidden" name="showFlag" value="<bean:write name="editReportAnalyseForm" property="showFlag" filter="true" />">
				<input type="hidden" name="scopeid2" value="<bean:write name="editReportAnalyseForm" property="scopeid" filter="true" />">
				&nbsp;&nbsp;&nbsp;&nbsp;
				<logic:notEqual name="editReportAnalyseForm" property="reportState" value="null">
					<bean:write name="editReportAnalyseForm" property="reportState" filter="true" />
				</logic:notEqual>
				<logic:equal name="editReportAnalyseForm" property="reportState" value="null">			
	     			 <logic:equal name="editReportAnalyseForm" property="use_scope_cond" value="1" >
						<logic:present name="editReportAnalyseForm" property="reportList"
							scope="session">	
					&nbsp;&nbsp;<bean:message key="reportanalyst.selectreport" />：
					<hrms:optioncollection name="editReportAnalyseForm"
								property="reportList" collection="list" scope="session" />
							<html:select name="editReportAnalyseForm" property="reportchangeTabid"
								size="1" onchange="javascript:tabidChange()">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
						</logic:present>
						<logic:notEqual name="editReportAnalyseForm" property="use_scope_cond2" value="1" >
						统计口径：
							<hrms:optioncollection name="editReportAnalyseForm"
								property="scopelist" collection="list" />
							<html:select name="editReportAnalyseForm" property="scopeid"
								size="1" onchange="javascript:scopeChange()">
								<html:options collection="list" property="dataValue"
									labelProperty="dataName" />
							</html:select>
							</logic:notEqual>
				</logic:equal>
	     			 <bean:message key="edit_report.year"/>:
	   				 <hrms:optioncollection name="editReportAnalyseForm" property="reportYearidList" collection="list" />
						<html:select name="editReportAnalyseForm" property="reportYearid" size="1" onchange="javascript:yearChange()">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
						<logic:notEqual name="editReportAnalyseForm" property="reportCountInfo" value="null">
							<bean:write name="editReportAnalyseForm" property="reportCountInfo" filter="true" />
						</logic:notEqual>
						<logic:notEqual name="editReportAnalyseForm" property="reportCountInfo" value="null">
							<hrms:optioncollection name="editReportAnalyseForm" property="reportCountList" collection="list" />
							<html:select name="editReportAnalyseForm" property="reportCount" size="1" onchange="javascript:countChange()">
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
						</logic:notEqual>
						
						<logic:equal name="editReportAnalyseForm" property="reportTypes" value="6">
					   <hrms:optioncollection name="editReportAnalyseForm" property="reportWeekList" collection="list" />
							<html:select name="editReportAnalyseForm" property="weekid" size="1" onchange="javascript:weekChange()" >
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
					</logic:equal>
						<hrms:priv func_id='29023301'>
							<input type="button" name="b_export"
								value="<bean:message key="general.inform.muster.output.excel"/>"
								onclick='exportExcel()' class="mybutton">
						</hrms:priv>
					</logic:equal>
					
			
					
					
				</td>
			</tr>
			<tr>
				<td>
					<bean:write name="editReportAnalyseForm" property="reportHtml" filter="false" />
				</td>
				
			</tr>
		</table>
	</form>
</body>
