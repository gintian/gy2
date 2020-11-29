<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hjsj.hrms.actionform.report.report_analyse.ReportAnalyseForm"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String appDate = userView.getAppdate().substring(0,4);//业务日期年
	ReportAnalyseForm reportAnalyseForm=(ReportAnalyseForm)session.getAttribute("reportAnalyseForm");
	ArrayList years2 = (ArrayList)reportAnalyseForm.getYears2();
%>
<link href="/css/css1_report.css" rel="stylesheet" type="text/css">

<script language="javascript" src="/js/page_options.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script language="javascript" src="/js/function.js"></script>
<script language="javascript" src="/js/meizzDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<!-- <script type="text/javascript" language="javascript" src="/anychart/js/AnyChart.js"></script> -->
<script type="text/javascript" language="javascript" src="/ajax/basic.js"></script>
<script type="text/javascript" language="javascript" src="../../echarts/echarts.min.js"></script>
<script type="text/javascript" language="javascript" src="../../echarts/shine.js"></script>

<script type="text/javascript">

	function pf_ChangeFocus(){
		key = window.event.keyCode;
		//0xD是16进制表示，这里是13，也就是回车键
		if ( key==0xD && event.srcElement.tagName!='TEXTAREA' && event.srcElement.type!='file'){
			window.event.keyCode=9;//转成tab
		}
		if ( key==116){//按F5刷新问题,重复提交问题
			window.event.returnValue=false;
		}   
		if ((window.event.ctrlKey)&&(key==82)){//屏蔽 Ctrl+R     
			window.event.returnValue=false;
		} 
	}

	var rows ='${reportAnalyseForm.rows}';
	var cols ='${reportAnalyseForm.cols}';
	tabid='${reportAnalyseForm.reportTabid}';
	var width = screen.width;
	var height = screen.height; 
	<!--改变年份ajax联动-->
	function yearChange(){
		var reportTypes="${reportAnalyseForm.reportTypes}";
		if(reportTypes!='6'){
			var hashvo=new ParameterSet();
		    hashvo.setValue("unitCode",ra.codeFlag.value);
		    hashvo.setValue("tabid",tabid);
		    hashvo.setValue("yearid",ra.reportYearid.value);    
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:yearChangeResult,functionId:'03040000005'},hashvo);			
		}else{
			ra.target="ril_body1";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&opt=year&code="
						+ra.codeFlag.value +"&tabid="+tabid ;
			ra.submit();	
		}
	}
	
	function yearChangeResult(outparamters){
		//格式：null$$1##2##2.5@1##3##2.5
		//     1##1次@2##2次$$1##2##2.5@1##3##2.5
		var info = outparamters.getValue("info");
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
		
		/*add by xiegh on 20180614 项目：38312 切换年份时 没有对图表进行刷新操作  */
		if(rowFlag=="false"){//没甲行时
			selectRowOrColumn("b0");
		}else{//有甲行时
			selectRowOrColumn("b1");
		}
	}
		
	function weekChange(){
		var hashvo=new ParameterSet();
	    hashvo.setValue("unitCode",ra.codeFlag.value);
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("yearid",ra.reportYearid.value);
	    hashvo.setValue("countid",ra.reportCount.value);
	    hashvo.setValue("weekid",ra.weekid.value);
	    hashvo.setValue("reportTypes","${reportAnalyseForm.reportTypes}");
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03040000007'},hashvo);			
	}
		
	<!--改变参数ajax联动-->
	function countChange(){
		var reportTypes="${reportAnalyseForm.reportTypes}";
		if(reportTypes!='6'){
			var hashvo=new ParameterSet();
		    hashvo.setValue("unitCode",ra.codeFlag.value);
		    hashvo.setValue("tabid",tabid);
		    hashvo.setValue("yearid",ra.reportYearid.value);
		    hashvo.setValue("countid",ra.reportCount.value);
		   	var In_paramters="flag=1"; 	
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03040000007'},hashvo);			
		}else{
			ra.target="ril_body1";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&opt=count&code="
						+ra.codeFlag.value +"&tabid="+tabid ;
			ra.submit();	
		}
	}
		
	function countChangeResult(outparamters){
		var info = outparamters.getValue("info");
		var rows=outparamters.getValue("rows");
		var cols=outparamters.getValue("cols");
		dbarray = info.split("@");
		for(var i=0;i<rows;i++){
			for(var j=0;j<cols;j++){
				var a_object=eval("document.ra.a"+i+"_"+j);
				if(a_object)
					a_object.value='';
			}
		}
	    for(var i = 0; i< dbarray.length ; i++){
			dba = dbarray[i].split("##");
			var a_object=eval("document.ra.a"+dba[0]+"_"+dba[1]);
			if(a_object){
				if(dba[2]!='0'){
					a_object.value=dba[2];
				}else{
					a_object.value='';
				}
			}
		}
	}
		
	var image_width = 0;
	var image_height = 0;
	function func(){
		if(document.body!=null){
			available_width=document.body.clientWidth;
		    available_height=document.body.clientHeight;
			
		    available_width = available_width*0.9;
	    	available_height = available_height*0.5;
	    	
	    	available_width = ""+available_width;
	    	available_height =""+available_height;
	    	
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}
			image_width = (available_width);
			image_height = (available_height);
		
			clearTimeout(val);
		}
	}

	var val="";
	function init(){
		var is = new Is();
		var available_width = 0;
		var available_height = 0; 
	    if(is.ns4||is.ns6) {
	        available_width=innerWidth;
	        available_height=innerHeight;
	    } else if(is.ie4||is.ie5||is.ieX) {
	    	if(document.body!=null){
				available_width=document.body.clientWidth;
		        available_height=document.body.clientHeight;
	    	}
	    }
	    if(document.body!=null&&(is.ie4 ||is.ie5||is.ns6|| is.ns4||is.ieX)) {
	    	available_width = available_width*0.9;
	    	available_height = available_height*0.5;
	    	
	    	available_width = ""+available_width;
	    	available_height =""+(available_height+35);
	    	if(available_width.indexOf('.')!=-1){
	    		available_width = available_width.substring(0,available_width.indexOf('.'));
	    	}
	    	if(available_height.indexOf('.')!=-1){
	    		available_height = available_height.substring(0,available_height.indexOf('.'));
	    	}
			image_width = available_width;
			image_height = available_height;
		}
		if(document.body==null&&(is.ie4 ||is.ie5||is.ns6|| is.ns4)) {
			val=setTimeout("func()",3000); 
		}
	}
		
	var selectsGrid = "";//选中的所有点
	var selectGrid = "";//选中的一个点
	function changeGrid( k,  j){
		init();
		var var_name="aa"+k+"_"+j;
		if(rowsSelected!=""){
 				var infoRow = rowsSelected.substr(rowsSelected.indexOf(",")+1,(rowsSelected.indexOf("/")-2));//截取第一个","到第一个"/"的值
 				if(rowFlag=="true"){
 					rowLength = (rowsSelected.split("/")).length;
 				}else{
 					rowLength = (rowsSelected.split("/")).length-1;
 				}
 				for(var i = 0;i < rowLength;i++){//(rowsSelected.split("/")).length的值是选中的一行的单元格的个数
 					if(rowFlag=="true"&&i==0){
 						continue;
 					}
 					var rowObject =eval("aa"+infoRow+"_"+i);
 					rowObject.style.background="#ffffff";
 				}
  			if(selectsGrid.indexOf(rowsSelected)!=-1){
  				selectsGrid=selectsGrid.replace(rowsSelected,"");//移除selectsGrid的上一次选中行
  				rowsSelected = "";
 				}
 			}else if(colsSelected!=""){
 				var infoRow = colsSelected.substr(0,colsSelected.indexOf(","));
 				if(colFlag=="true"){
 					colLength = (colsSelected.split("/")).length;
 				}else{
 					colLength = (colsSelected.split("/")).length-1;
 				}
 				for(var i = 0;i < colLength;i++){//(colsSelected.split("/")).length的值是选中的一列的单元格的个数
 					if(colFlag=="true"&&i==0){
 						continue;
 					}
 					var rowObject =eval("aa"+i+"_"+infoRow);
 					rowObject.style.background="#ffffff";
 				}
 				if(selectsGrid.indexOf(colsSelected)!=-1){
  				selectsGrid=selectsGrid.replace(colsSelected,"");//移除selectsGrid的上一次选中列
  				colsSelected = "";
				}
 			}
		if(selectGrid.indexOf("/"+j+","+k+"/")==-1){
		    if(selectGrid.length>2){
				var temp=selectsGrid.substring(1,selectGrid.length-1);
				var temps=temp.split("/");
			}
			var obj=document.getElementById(var_name);
			if(obj!=null){
				obj.style.background="yellow";
			}

			if(selectGrid.length==0){
				selectGrid="/"+j+","+k+"/";
				selectsGrid = selectGrid;		
			}else{
				selectGrid = selectGrid+j+","+k+"/";
				selectsGrid = selectGrid;
			}
		}else{
			var obj=document.getElementById(var_name);
			if(obj!=null){
				obj.style.background="#ffffff";
			}
			selectGrid = replaceAll(selectGrid,"/"+j+","+k+"/", "/");
			if(selectGrid=="/"){
				obj.style.background="yellow";
				selectGrid="/"+j+","+k+"/";
			}
			selectsGrid = selectGrid;
		}
		//开始回调函数
    	var hashvo=new ParameterSet();
		hashvo.setValue("code",ra.codeFlag.value);	
		hashvo.setValue("tabid",tabid);
		hashvo.setValue("rc",selectsGrid);
		hashvo.setValue("w",image_width);
		hashvo.setValue("h",image_height);
		hashvo.setValue("char_type",document.getElementById("char_type").value);
		hashvo.setValue("years",document.getElementById("years").value);
		hashvo.setValue("showFlag","1");
		hashvo.setValue("type","manager");
		hashvo.setValue("selectType","spot");
		/*add by xiegh on 20180614 项目：38312 切换年份时  点击行数据没有根据当前的年份对图表进行刷新  */
		hashvo.setValue("yearid",ra.reportYearid.value);
		//保存值
		document.getElementById("tabid").value = tabid;
		document.getElementById("selectsGrid").value = selectsGrid;
		document.getElementById("char_width").value = image_width;
		document.getElementById("char_height").value = image_height;
		document.getElementById("selectType").value = "spot";
	   	var request=new Request({method:'post',asynchronous:true,onSuccess:showChartFlag,functionId:'03040000010'},hashvo);

	}
		
	function replaceAll( str, from, to ) {
		var idx = str.indexOf( from );
			while ( idx > -1 ) {
			str = str.replace( from, to ); 
			idx = str.indexOf( from );
		}
		return str;
	}
		
			
	function load(){  
		init();
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
		
	var colsSelected = "";//选中的报表的一列的值
	var rowsSelected = "";//选中的报表的一行的值
	var colFlag = "${reportAnalyseForm.colFlag}";
	var rowFlag = "${reportAnalyseForm.rowFlag}";
	var colLength = "";
	var rowLength = "";
	function selectRowOrColumn(info){//选中整行或整列
		init();
  		if(info.substring(0,1)=='a')//选中一列
  		{
 			var colselected = "";//选中列的一个单元格的值
  			if(rowsSelected!=""){
  				var infoRow = rowsSelected.substr(rowsSelected.indexOf(",")+1,(rowsSelected.indexOf("/")-2));//截取第一个","到第一个"/"的值
  				if(rowFlag=="true"){
 					rowLength = (rowsSelected.split("/")).length;
 				}else{
 					rowLength = (rowsSelected.split("/")).length-1;
 				}
  				for(var i = 0;i < rowLength;i++){//(rowsSelected.split("/")).length的值是选中的一行的单元格的个数
  					if(rowFlag=="true"&&i==0){
 						continue;
 					}
  					var rowObject =eval("aa"+infoRow+"_"+i);
  					rowObject.style.background="#ffffff";
  				}
	  			if(selectsGrid.indexOf(rowsSelected)!=-1){
	  				selectsGrid=selectsGrid.replace(rowsSelected,"");//移除selectsGrid的上一次选中行
	  				rowsSelected = "";
  				}
  			}else if(colsSelected!=""){
  				var infoRow = colsSelected.substr(0,colsSelected.indexOf(","));
  				if(colFlag=="true"){
 					colLength = (colsSelected.split("/")).length;
 				}else{
 					colLength = (colsSelected.split("/")).length-1;
 				}
  				for(var i = 0;i < colLength;i++){//(colsSelected.split("/")).length的值是选中的一列的单元格的个数
  					if(colFlag=="true"&&i==0){
 						continue;
 					}
  					var rowObject =eval("aa"+i+"_"+infoRow);
  					rowObject.style.background="#ffffff";
  				}
  				if(selectsGrid.indexOf(colsSelected)!=-1){
	  				selectsGrid=selectsGrid.replace(colsSelected,"");//移除selectsGrid的上一次选中列
	  				colsSelected = "";
 				}
  			}else if (selectGrid!=""){
  				var list = [];
		    	list = selectGrid.split("/");
		    	var selectGridObject = "";
		    	var spot = "",//定义一个点
		    		row = "",//横坐标
		    		col = "";//纵坐标
		    	for(var i = 1;i<list.length-1;i++){
		    		spot = list[i];
		    		row = spot.substr(0,spot.indexOf(","));
		    		col = spot.substr(spot.indexOf(",")+1,spot.length);
			     	selectGridObject = eval("aa"+col+"_"+row);
			     	selectGridObject.style.background="#ffffff";
		     	}
		     	selectsGrid=selectsGrid.replace(selectGrid,"");//移除selectsGrid的选中的所有点
	  			selectGrid = "";
  			}
  			for(var i=0;i<rows;i++)
  			{
  				if(colFlag=="true"&&i==0){
 					continue;
 				}
  				var a_object=eval("aa"+i+"_"+info.substring(1));
  				colselected = info.substring(1)+","+i+"/";//选中列里的对象
  				if(selectsGrid.indexOf(colselected)==-1){//已经添加的对象与选中的对象对比，避免重复添加
  					selectsGrid.replace(colselected,"");
  					colsSelected=colsSelected+colselected;
  				}else{
  					colsSelected=colsSelected+colselected;//把列对象放到一个对象里面
  				}
  				a_object.style.background="yellow";//改变选中列的背景颜色为黄色
  			}
  			if(selectsGrid==""){
				selectsGrid = "/"+colsSelected;
  			}else{
	  			selectsGrid = selectsGrid+colsSelected;//将选中的列的值传到集合里
  			}
				
	    	var hashvo=new ParameterSet();
			hashvo.setValue("code",ra.codeFlag.value);	
			hashvo.setValue("tabid",tabid);
			hashvo.setValue("rc",selectsGrid);
			hashvo.setValue("w",image_width);
			hashvo.setValue("h",image_height);
			hashvo.setValue("char_type",document.getElementById("char_type").value);
			hashvo.setValue("years",document.getElementById("years").value);
			hashvo.setValue("showFlag","1");
			hashvo.setValue("type","manager");
			hashvo.setValue("selectType","cols");
			/*add by xiegh on 20180614 项目：38312 切换年份时  点击行数据没有根据当前的年份对图表进行刷新  */
			hashvo.setValue("yearid",ra.reportYearid.value);
			//保存值
			document.getElementById("tabid").value = tabid;
			document.getElementById("selectsGrid").value = selectsGrid;
			document.getElementById("char_width").value = image_width;
			document.getElementById("char_height").value = image_height;
			document.getElementById("selectType").value = "cols";	
		   	var request=new Request({method:'post',asynchronous:true,onSuccess:showChartFlag,functionId:'03040000010'},hashvo);
		   	
  		}else{//选中一行
  			var rowselected = "";//选中行的一个单元格的值
  			if(rowsSelected!=""){
  				var infoRow = rowsSelected.substr(rowsSelected.indexOf(",")+1,(rowsSelected.indexOf("/")-2));
  				if(rowFlag=="true"){
 					rowLength = (rowsSelected.split("/")).length;
 				}else{
 					rowLength = (rowsSelected.split("/")).length-1;
 				}
  				for(var i = 0;i < rowLength;i++){//(rowsSelected.split("/")).length的值是选中的一行的单元格的个数
  					if(rowFlag=="true"&&i==0){
 						continue;
 					}
  					var rowObject =eval("aa"+infoRow+"_"+i);
  					rowObject.style.background="#ffffff";
  				}
	  			if(selectsGrid.indexOf(rowsSelected)!=-1){
	  				selectsGrid=selectsGrid.replace(rowsSelected,"");//移除selectsGrid的上一次选中行
	  				rowsSelected = "";
  				}
  			}else if(colsSelected!=""){
  				var infoRow = colsSelected.substr(0,colsSelected.indexOf(","));
  				if(colFlag=="true"){
 					colLength = (colsSelected.split("/")).length;
 				}else{
 					colLength = (colsSelected.split("/")).length-1;
 				}
  				for(var i = 0;i < colLength;i++){//(colsSelected.split("/")).length的值是选中的一列的单元格的个数
  					if(colFlag=="true"&&i==0){
 						continue;
 					}
  					var rowObject =eval("aa"+i+"_"+infoRow);
  					rowObject.style.background="#ffffff";
  				}
  				if(selectsGrid.indexOf(colsSelected)!=-1){
	  				selectsGrid=selectsGrid.replace(colsSelected,"");//移除selectsGrid的上一次选中列
	  				colsSelected = "";
 				}
  			}else if (selectGrid!=""){
		    	var list = [];
		    	list = selectGrid.split("/");
		    	var selectGridObject = "";
		    	var spot = "",//定义一个点
		    		row = "",//横坐标
		    		col = "";//纵坐标
		    	for(var i = 1;i<list.length-1;i++){
		    		spot = list[i];
		    		row = spot.substr(0,spot.indexOf(","));
		    		col = spot.substr(spot.indexOf(",")+1,spot.length);
			     	selectGridObject = eval("aa"+col+"_"+row);
			     	selectGridObject.style.background="#ffffff";
		     	}
		     	selectsGrid=selectsGrid.replace(selectGrid,"");//移除selectsGrid的选中的所有点
	  			selectGrid = "";
  			}
  			for(var i=0;i<cols;i++)
  			{
  				if(rowFlag=="true"&&i==0){
 					continue;
 				}
  				var a_object=eval("aa"+info.substring(1)+"_"+i);
  				rowselected = i+","+info.substring(1)+"/";//选中的对象
  				if(selectsGrid.indexOf(rowselected)!=-1){//已经添加的对象与选中的对象对比，避免重复添加
  					selectsGrid.replace(rowselected,"");
  					rowsSelected=rowsSelected+rowselected;
  				}else{
  					rowsSelected=rowsSelected+rowselected;//把行对象放到一个对象里面
  				}
  				a_object.style.background="yellow";//改变选中行的背景颜色为黄色
  			}
  			if(selectsGrid==""){
				selectsGrid = "/"+rowsSelected;
  			}else{
  				selectsGrid = selectsGrid+rowsSelected;//将选中的行的值传到集合里
  			}
	    	var hashvo=new ParameterSet();
			hashvo.setValue("code",ra.codeFlag.value);	
			hashvo.setValue("tabid",tabid);
			hashvo.setValue("rc",selectsGrid);
			hashvo.setValue("w",image_width);
			hashvo.setValue("h",image_height);
			hashvo.setValue("char_type",document.getElementById("char_type").value);
			hashvo.setValue("years",document.getElementById("years").value);
			hashvo.setValue("showFlag","1");
			hashvo.setValue("type","manager");
			hashvo.setValue("selectType","rows");
			/*add by xiegh on 20180614 项目：38312 切换年份时  点击行数据没有根据当前的年份对图表进行刷新  */
			hashvo.setValue("yearid",ra.reportYearid.value);
			//保存值
			document.getElementById("tabid").value = tabid;
			document.getElementById("selectsGrid").value = selectsGrid;
			document.getElementById("char_width").value = image_width;
			document.getElementById("char_height").value = image_height;
			document.getElementById("selectType").value = "rows";
		   	var request=new Request({method:'post',asynchronous:true,onSuccess:showChartFlag,functionId:'03040000010'},hashvo);

  		}
	}
	  
	//var chart = new AnyChart('/anychart/swf/AnyChart.swf');
	function showChartFlag(outparamters){//显示报表折线图
	
		var showReport=outparamters.getValue("showReport");
		var width = outparamters.getValue("chartWidth");
		var height = outparamters.getValue("chartHeight");
		showReport = getDecodeStr(showReport);
		var reportDivObj=document.getElementById("showChart");
		if(reportDivObj){
			reportDivObj.style.width = width;
			reportDivObj.style.height = height;
			reportDivObj.style.display='block';
		}
		var chart;
		if(chart)
    		chart.dispose();
		
		chart =  echarts.init(reportDivObj,'shine');
		
		if(showReport){
    		showReport = showReport.replace(/option =/,'').replace(/(^\s*)|(\s*$)/g,"");
			showReport = showReport.substring(0,showReport.length-1);
		}
		var optionObj = eval('(' + showReport + ')');
		chart.clear();//先清空在重新渲染  wangb 20180717 bug 38905
		chart.setOption(optionObj);
	}
	//切换年份
	function setYears(obj){
		var arr = [];
		var years = obj.options[obj.selectedIndex].value;//显示value
		document.getElementById("years").value = years;
		document.getElementsByName("reportYearid")[0].options.length=0;
		<%for(int i = 0; i< years2.size(); i++){%>
		if(<%=i%> >= years){
			
		}else{
			var newopt=document.createElement("option");
			newopt.text="<%=years2.get(i)%>";
			newopt.value="<%=years2.get(i)%>";
			document.getElementsByName("reportYearid")[0].options.add(newopt,0);
		}
						
		<%}%>
	

		callAjax();
	}
	//切换图表类型
	function setChartType(obj){
		var char_type = obj.options[obj.selectedIndex].value;//显示value
		document.getElementById("char_type").value = char_type;
		callAjax();
	}
	//处理ie下不出折现问题
	var chartHeight=document.getElementById("char_height").value;
	//调用Ajax刷新前台页面
	function callAjax(){
		var hashvo=new ParameterSet();
		hashvo.setValue("tabid",document.getElementById("tabid").value);
		hashvo.setValue("rc",document.getElementById("selectsGrid").value);
		hashvo.setValue("w",document.getElementById("char_width").value);
		hashvo.setValue("h",document.getElementById("char_height").value);
		hashvo.setValue("char_type",document.getElementById("char_type").value);
		hashvo.setValue("years",document.getElementById("years").value);
		hashvo.setValue("type","manager");
		hashvo.setValue("selectType",document.getElementById("selectType").value);
		var request=new Request({method:'post',asynchronous:true,onSuccess:showChartFlag,functionId:'03040000010'},hashvo);
	}
</script>
<hrms:themes />
<logic:equal name="reportAnalyseForm" property="reportExist" value="yes">
<body onload="load()" onKeyDown="return pf_ChangeFocus(); ">
	<form name="ra" method="post" action="/report/report_analyse/reportanalyse.do">
	<input type="hidden" value="${reportAnalyseForm.reportTabid}" id="tabid"  />
	<input type="hidden" value="" id="selectsGrid" />
	<input type="hidden" value="" id="char_width" />
	<input type="hidden" value="" id="char_height" />
	<input type="hidden" value="" id="selectType" />
	<input type="hidden" value="${reportAnalyseForm.years}" id="years" />
	<input type="hidden" value="${reportAnalyseForm.char_type}" id="char_type" />
	<input type="hidden" value="${reportAnalyseForm.years2}" id="years2" />
	<table id="chartTable">
		<!-- 报表折线图分析 -->
		<tr>
			<td height="${reportAnalyseForm.chartHeight}">
				<table align="left" width="100%" height="100%">
					<tr>
						<td>
							<bean:message key="edit_report.year" />
							<hrms:optioncollection name="reportAnalyseForm" property="yearList" collection="ylist" scope="session" />
							<html:select name="reportAnalyseForm" property="years" size="1" styleId="aaa" onchange="setYears(this)">
								<html:options collection="ylist" property="dataValue" labelProperty="dataName" />
							</html:select>
							&nbsp;&nbsp;
							<bean:message key="report_collect.chartType" />
							<select name='char_type' onchange='setChartType(this)'  >
								<option value='11' <logic:equal name="reportAnalyseForm" property="char_type" value="11">selected</logic:equal> ><bean:message key="lable.performance.histogram" /></option>
								<option value='27' <logic:equal name="reportAnalyseForm" property="char_type" value="27">selected</logic:equal> ><bean:message key="lable.performance.graph" /></option>
							</select>
						</td>
					</tr>
					<tr>
						<td align="left" nowrap colspan="5">
							<div id="showChart"></div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	<div style="position:absolute;" id="reportDiv">
	<table align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
		<tr>
			<td style="padding-top:3px">
				<input type="hidden" name="codeFlag" value="<bean:write name="reportAnalyseForm" property="codeFlag" filter="true" />">
				<hrms:optioncollection name="reportAnalyseForm" property="reportList" collection="list" scope="session" />
				<html:select name="reportAnalyseForm" property="reportTabid" size="1"  style="display:none;">
					<html:options collection="list" property="dataValue" labelProperty="dataName" />
				</html:select>
				<logic:notEqual name="reportAnalyseForm" property="reportState" value="null">
					<bean:write name="reportAnalyseForm" property="reportState" filter="true" />
				</logic:notEqual>
				<logic:equal name="reportAnalyseForm" property="reportState" value="null">
					<bean:message key="edit_report.year" />
  				 		<hrms:optioncollection name="reportAnalyseForm" property="reportYearidList" collection="list" />
					<html:select name="reportAnalyseForm" property="reportYearid" size="1" onchange="javascript:yearChange()">
						<html:options collection="list" property="dataValue" labelProperty="dataName" />
					</html:select>
					<logic:notEqual name="reportAnalyseForm" property="reportCountInfo" value="null">
						<bean:write name="reportAnalyseForm" property="reportCountInfo" filter="true" />
					</logic:notEqual>
					<logic:notEqual name="reportAnalyseForm" property="reportCountInfo" value="null">
						<hrms:optioncollection name="reportAnalyseForm" property="reportCountList" collection="list" />
						<html:select name="reportAnalyseForm" property="reportCount" size="1" onchange="javascript:countChange()">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
					</logic:notEqual>
					<logic:equal name="reportAnalyseForm" property="reportTypes" value="6">
						<hrms:optioncollection name="reportAnalyseForm" property="reportWeekList" collection="list" />
						<html:select name="reportAnalyseForm" property="weekid" size="1" onchange="javascript:weekChange()">
							<html:options collection="list" property="dataValue" labelProperty="dataName" />
						</html:select>
					</logic:equal>
				</logic:equal>
			</td>
		</tr>
		<!-- 报表浏览分析 -->
		<tr>
			<td align="left" height="${reportAnalyseForm.reportHeight}">
				<bean:write name="reportAnalyseForm" property="reportHtml" filter="false" />
				<script language='javascript'>
				if(rowFlag=="false"){//没甲行时
					selectRowOrColumn("b0");
				}else{//有甲行时
					selectRowOrColumn("b1");
				}
		 		</script>
			</td>
		</tr>
	</table>	
	</div>
	</form>
</body>
</logic:equal>
<script language="javascript">
if(rows>1){
	var CellArray = new Array(rows);	//恢复样式目前只恢复borderRightWidth
	var endcols=cols-1;
	var pageResult=new Array(rows);		//页面值得二维数组		
	for(var a=0;a<rows;a++)
	{
		var c_object =eval("aa"+a+"_"+endcols);
		if(c_object.currentStyle){
			CellArray[a]=c_object.currentStyle['borderRightWidth'];
		}
	}
}
var chartTable = document.getElementById("chartTable");
chartTable.setAttribute("height",(Number(image_height)+15));
</script>
