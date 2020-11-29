
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>


<link href="/css/css1_report.css" rel="stylesheet" type="text/css">

<script language="javascript" src="/js/page_options.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%@ page
	import="java.util.*,
	com.hjsj.hrms.actionform.report.report_analyse.ReportAnalyseForm,
	org.apache.commons.beanutils.LazyDynaBean,
	com.hrms.struts.valueobject.UserView,com.hrms.struts.constant.WebConstant,
	com.hjsj.hrms.utils.PubFunc"%>


<%
	ReportAnalyseForm reportAnalyseForm = (ReportAnalyseForm) session
			.getAttribute("reportAnalyseForm");
	String reportTypes = reportAnalyseForm.getReportTypes(); // =1，一般 =2，年 =3，半年 =4，季报 =5，月报 =6,周报
	
	UserView userview = (UserView) request.getSession().getAttribute(
			WebConstant.userView);
	//add by wangchaoqun on 2014-9-28 begin
	//反查（revertData()）
	String encryptParam = PubFunc.encrypt("pageNum=1&tabid=" + reportAnalyseForm.getReportTabid() + "&unitcode=" +
			reportAnalyseForm.getSelfUnitcode());
	//生成综合表（productIntegrateTable()）
	String encryptParam2 = PubFunc.encrypt("cols=" + reportAnalyseForm.getCols() + "&unitcode=" + reportAnalyseForm.getSelfUnitcode()
			+ "&tabid=" + reportAnalyseForm.getReportTabid() + "&reportTypes=" + reportAnalyseForm.getReportTypes() + "&flag=1");
	//add by wangchaoqun on 2014-9-28 end

%>

<script language="javascript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA' && event.srcElement.type!='file') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
//兼容谷歌浏览器 wangbs 20190319
document.onmousedown=function oncontextmenu (e)
{
var e = e||window.event;
		if(e.button==2){
		if (e.target) targ = e.target;
		else if (e.srcElement) targ = e.srcElement;
			
			if (targ.name!=null&&targ.name.substring(0,1)=="a"&&targ.name.substring(1,2)!="a")  // input标签
			{
			
				setReverseID(targ.name);
			}
		}

return true; 
}
		var rows ='${reportAnalyseForm.rows}';
		var cols ='${reportAnalyseForm.cols}';
		//alert(rows+"ddd"+cols);
		var selfUnitcode='${reportAnalyseForm.selfUnitcode}';
		var tabid='${reportAnalyseForm.reportTabid}';
		var width = screen.width;
		var height = screen.height; 
		var reverseFlag="";
		var encryptParam = '<%=encryptParam %>';
		var encryptParam2 = '<%=encryptParam2 %>';
		<!--改变报表刷新页面-->
		function tabidChange()
		{
			ra.target="ril_body1";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&code="
						+ra.codeFlag.value +"&tabid="+ra.reportTabid.value ;
			ra.submit();
		}
		
		<!--改变年份ajax联动-->
		function yearChange(){
			var reportTypes="${reportAnalyseForm.reportTypes}";
			if(reportTypes!='6')
			{
			
				var hashvo=new ParameterSet();
			    hashvo.setValue("unitCode",ra.codeFlag.value);
			    hashvo.setValue("tabid",ra.reportTabid.value);
			    hashvo.setValue("yearid",ra.reportYearid.value);    
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:yearChangeResult,functionId:'03040000005'},hashvo);			
			}
			else
			{
				ra.target="ril_body1";
				ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&opt=year&code="
							+ra.codeFlag.value +"&tabid="+ra.reportTabid.value ;
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
		
		
		function weekChange()
		{
				var hashvo=new ParameterSet();
			    hashvo.setValue("unitCode",ra.codeFlag.value);
			    hashvo.setValue("tabid",ra.reportTabid.value);
			    hashvo.setValue("yearid",ra.reportYearid.value);
			    hashvo.setValue("countid",ra.reportCount.value);
			    hashvo.setValue("weekid",ra.weekid.value);
			    hashvo.setValue("reportTypes","${reportAnalyseForm.reportTypes}");
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03040000007'},hashvo);			
		
		}
		
		
		
		<!--改变参数ajax联动-->
		function countChange(){
			//alert(ra.reportCount.value);
			var reportTypes="${reportAnalyseForm.reportTypes}";
			if(reportTypes!='6')
			{
				var hashvo=new ParameterSet();
			    hashvo.setValue("unitCode",ra.codeFlag.value);
			    hashvo.setValue("tabid",ra.reportTabid.value);
			    hashvo.setValue("yearid",ra.reportYearid.value);
			    hashvo.setValue("countid",ra.reportCount.value);
			   	var In_paramters="flag=1"; 	
				var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:countChangeResult,functionId:'03040000007'},hashvo);			
			}
			else
			{
				ra.target="ril_body1";
				ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&opt=count&code="
							+ra.codeFlag.value +"&tabid="+ra.reportTabid.value ;
				ra.submit();	
			
			
			}
		}
		
		function countChangeResult(outparamters){
			var info = outparamters.getValue("info");
			var rows=outparamters.getValue("rows");
			var cols=outparamters.getValue("cols");
			//alert(info);
			dbarray = info.split("@");
			
			for(var i=0;i<rows;i++)
			{
				for(var j=0;j<cols;j++)
				{
					var a_object=eval("document.ra.a"+i+"_"+j);
					if(a_object)
						a_object.value='';
				}
			}
			
		    for(var i = 0; i< dbarray.length ; i++){
				dba = dbarray[i].split("##");
				var a_object=eval("document.ra.a"+dba[0]+"_"+dba[1]);
				
				//alert(dba[0] + " " + dba[1] + " =" + dba[2]);
				if(a_object)
				{
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
		
		
		
		
		function func()
		{
			
			if(parent.ril_body2.document.body!=null)
			{
				available_width=parent.ril_body2.document.body.clientWidth;
			    available_height=parent.ril_body2.document.body.clientHeight;
				
				//----liuy 修改报表浏览分析图显示不全 2014-7-31 begin
			    available_width = available_width*0.9;
		    	available_height = available_height*0.9;
		    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 begin
		    	
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
			
				clearTimeout(val) 		
			}
		}

		var val;
		function init(){
			var flag =0;
			var is = new Is();
			var available_width = 0;
			var available_height = 250; 
			var dd = parent.ril_body2.document.body;
			if(is.ie){
		        if(parent.ril_body2.document.body!=null)
		    	{
					available_width=parent.ril_body2.document.body.clientWidth;
		        	available_height=parent.ril_body2.document.documentElement.clientHeight;
		    	}
			}
			else if(is.ns4||is.ns6) {
		        available_width=innerWidth;
		        if(parent.ril_body2.document.body!=null)
		    	{
		        	available_height=parent.ril_body2.document.body.clientHeight;
		    	}
		    } else if(is.ie4||is.ie5||is.ieX) {
		    	
		    	if(parent.ril_body2.document.body!=null)
		    	{
			      
			      available_width=parent.ril_body2.document.body.clientWidth;
			        available_height=parent.ril_body2.document.body.clientHeight;
			        if(parent.ril_body2.document.reportAnalyseForm.chartHeight.value!="120"){
			        flag=1;
			          image_width= parent.ril_body2.document.reportAnalyseForm.chartWidth.value;
			        image_height= parent.ril_body2.document.documentElement.chartHeight.value;
			     }
			     	
		    	}
		    	
		    }
		    if(parent.ril_body2.document.body!=null&&(is.ie4 ||is.ie5||is.ns6|| is.ns4||is.ieX)) {
		    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 begin
		    	available_width = available_width*0.9;
		    	available_height = available_height*0.9;
		    	
		    	available_width = ""+available_width;
		    	available_height =""+available_height;
		    	//----liuy 修改报表浏览分析图显示不全 2014-7-31 end
		    	if(available_width.indexOf('.')!=-1){
		    		available_width = available_width.substring(0,available_width.indexOf('.'));
		    	}
		    	if(available_height.indexOf('.')!=-1){
		    		available_height = available_height.substring(0,available_height.indexOf('.'));
		    	}
		//    	available_height=available_height*2.0;
		//    	available_height =""+available_height;
		//    	if(available_height.indexOf('.')!=-1){
		//    		available_height = available_height.substring(0,available_height.indexOf('.'));
		//    	}
				if(flag==1){
				
				}else{
				image_width = (available_width);
				image_height = (available_height);
				}
				
			}
			
			/* if(parent.ril_body2.document.body==null&&(is.ie4 ||is.ie5||is.ns6|| is.ns4)) {
					val=setTimeout("func()",3000); 
			} */
			
			
		}
		
		var selectGrid="";
			
		function changeGrid( i,  j){
			init();
				var columnflag = '${reportAnalyseForm.columnflag}';
			if(columnflag=="1")
			alert("该表的结构已修改，请维护表结构！");
			var unitcodes="";
			var var_name="aa"+i+"_"+j;
			if(parent.mil_menu.getShowFlag()=='2')
			{
				
				if(trim(parent.mil_menu.getSelected()).length==0)
				{
					alert(REPORT_INFO37+"!");
					return;
				}
				else
					unitcodes=parent.mil_menu.getSelected();
			
			}
			
			
			if(parent.mil_menu.getShowFlag()=='1')
			{
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
			}
			else
			{
				if(selectGrid.length>1)
				{
					var temp=selectGrid.split(",");
					var obj=document.getElementById("aa"+temp[1]+"_"+temp[0]);
					if(obj!=null)
						obj.style.background="#ffffff";
				}
				var obj=document.getElementById(var_name);
				if(obj!=null)
					obj.style.background="yellow";
				selectGrid=j+","+i
			}
			ra.target="ril_body2";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeGrid=link&code="
					+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&rc="+selectGrid+"&w="
					+image_width+"&h="+image_height+"&unitcodes="+unitcodes;
		   ra.submit();
	
		}
		
		function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
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
			
		function load(){  
			
			var currentReport = "${reportAnalyseForm.currentReport}";	
			var unitcodes="";
			for(var i = 0 ; i< document.ra.reportTabid.options.length; i++){
				if(document.ra.reportTabid.options[i].value==currentReport){
					document.ra.reportTabid.options[i].selected = true;
				}
			}
			init();
			parent.ril_body2.location.href ="/report/report_analyse/reportanalyse.do?b_changeFrame=link&w="
				+image_width+"&h="+image_height+"&code="
					+ra.codeFlag.value +"&tabid="+ra.reportTabid.value+"&rc="+selectGrid+"&unitcodes="+unitcodes;
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
		    this.ie     = (agent.indexOf("msie")       != -1||(!!window.ActiveXObject || "ActiveXObject" in window));
		    this.ie3    = (this.ie && (this.major      <   4));
		    this.ie4    = (this.ie && (this.major      ==  4) &&
		                  (agent.indexOf("msie 5.0")   == -1));
		    this.ie5    = (this.ie && (this.major      ==  4) &&
		                  (agent.indexOf("msie 5.0")   != -1));
		    this.ieX    = (this.ie && !this.ie3 && !this.ie4);
		}
	
	
		function changeSort()
		{
		   	var In_paramters="sortid="+ document.ra.reportSortID.value; 	
			var request=new Request({method:'post',asynchronous:true,parameters:In_paramters,onSuccess:changeSort0,functionId:'03040000014'});			
		
		}
	
		function changeSort0(outparamters)
		{
			//var tablist=outparamters.getValue("tablist");
		    //AjaxBind.bind(ra.reportTabid,tablist);
			var str=getDecodeStr(outparamters.getValue("str"))
			var temps=str.split("#~#");
			ra.reportTabid.length=0;
			for(var i=0;i<temps.length;i++)
			{
				var temp=temps[i].split("@#@");
				ra.reportTabid[i]=new  Option(temp[1],temp[0]);
			}
			ra.target="ril_body1";
			ra.action="/report/report_analyse/reportanalyse.do?b_changeTabid=link&code="
						+ra.codeFlag.value +"&tabid="+ra.reportTabid.value ;
			ra.submit();
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
			var reportTypes="${reportAnalyseForm.reportTypes}";
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
		
		function initGrid()
		{ 
			<logic:equal name="reportAnalyseForm" property="reportState" value="null">
			if(parent.mil_menu.getShowFlag()=='2'&&trim(parent.mil_menu.getSelected()).length>0)
			{
					changeGrid( 1,  1);
			}
			else if(parent.mil_menu.getShowFlag()=='1')
			{
					changeGrid( 1,  1);
			}
			else
			{
				ra.target="ril_body2";
			    ra.action="/report/report_analyse/reportanalyse.do?b_changeGrid=link";
		        ra.submit();
			}	
			</logic:equal>
			<logic:notEqual name="reportAnalyseForm" property="reportState" value="null">
				changeGrid( 1,  1);
			</logic:notEqual>
		}


		function collect()
		{
		    var url="/report/report_analyse/reportanalyse.do?b_collectData=collect&unitCode="+ra.codeFlag.value+"&tabid="+ra.reportTabid.value+"&yearid="+ra.reportYearid.value
			window.open(url,"_blank");
			
		}
		 var nums='-1,';
		  function selectRowOrColumn(info)
	  {
	  		//   a1   a:列  b:行
	  		//clearSelected(0);
	  		if(info.substring(0,1)=='a')
	  		{
	  			if(nums.indexOf(',b')!=-1)
	  			{
	  				alert("不能同时选择横纵列!");
	  				return;
	  			}
	  			for(var i=0;i<rows;i++)
	  			{
	  				var a_object=eval("aa"+i+"_"+info.substring(1));
	  				a_object.style.background="#2D86E8";
	  			    //	  a_object.style.border='thin solid blue'
	  			}	
	  		
	  		}
	  		else
	  		{	
	  			if(nums.indexOf(',a')!=-1)
	  			{
	  				alert("不能同时选择横纵列!");
	  				return;
	  			}
	  			for(var i=0;i<cols;i++)
	  			{
	  				var a_object=eval("aa"+info.substring(1)+"_"+i);
	  				a_object.style.background="#2D86E8";
	  				// a_object.style.border='thin solid blue'
	  			}	
	  		}
	  		if(nums.indexOf(','+info+',')==-1)
		  		nums+=info+',';
		  	
	  }
	    
	  //清除前一步所选的行或列的颜色
	  function clearSelected(info)
	  {
	  		if(nums.indexOf(','+info+',')!=-1)
	  		{
			  	if(info.substring(0,1)=='a')
			  	{
				  			for(var i=0;i<rows;i++)
				  			{
				  				var a_object=eval("aa"+i+"_"+info.substring(1));
				  				a_object.style.background="#ffffff";
				  			//	a_object.style.border='1px solid #000000';
				  			}	
			  	}
			  	else
			  	{
			  				for(var i=0;i<cols;i++)
				  			{
				  				var a_object=eval("aa"+info.substring(1)+"_"+i);
				  				a_object.style.background="#ffffff";
				  				//a_object.style.border='1px solid #000000';
				  			}	
			  	}
		  	}
		  	if(nums!='-1,')
	  		{
	  			nums=nums.replace(info+",",""); 	
	  		}
	  		//if(n=1)				//????
	  		//	nums=-1;
	  		
	  }
	  	   //生成综合表
	  function productIntegrateTable()
	  {
	  	if(nums=='-1,')
	  	{
	  		alert(REPORT_INFO38+"!");
	  		return;
	  	}
	  	  var yearid ="";
  		  if(ra.reportYearid!=null)
  		  yearid=ra.reportYearid.value;
  		  var reportCount = "";
  		  if(ra.reportCount!=null)
  		  reportCount=ra.reportCount.value;
  		  var weekid = "";
  		  if(ra.weekid!=null)
  		  weekid=ra.weekid.value;
  		 var theurl="/report/report_analyse/reportanalyse.do?b_selectTableTerm=search&nums="+nums+"&yearid="+yearid+"&reportCount="+reportCount+"&weekid="+weekid+"&width="+width+"&height="+height+"&encryptParam=" + encryptParam2;
  		
  		 window.open(theurl,"_blank","left=500,top=200,width=750px,height=460px,scrollbars=yes,toolbar=no,menubar=no,location=yes,resizable=yes,status=yes");
	  }
	   //设置反查标记
	function setReverseID(name)
	{

		var a_td;
		if(reverseFlag!=''&&reverseFlag!=' ')
		{
		    a_td=eval("a"+reverseFlag);
			a_td.style.border='1px solid #000000';
			
			var startRow=parseInt(reverseFlag.substring(1,reverseFlag.indexOf("_")));
			a_td.style.borderRightWidth=CellArray[startRow];
		}
		reverseFlag=name;
		a_td=eval("a"+reverseFlag);
		a_td.style.border='2px solid green'
	
	}
	var gridVo;
		//反查
	function revertData()
	{
		if(reverseFlag==''||reverseFlag==' ')
		{
			alert(REPORT_INFO66);
			return;
		}
        gridVo = eval("document.ra."+reverseFlag);
		if(gridVo.value==''||gridVo.value==' ')
			return;		
		var url="/report/report_analyse/reportanalyse.do?b_queryBase=find`isclose=0`gridName="+reverseFlag;
		//选择人员库
		iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);

        //报表浏览 反查 兼容谷歌浏览器 wangbs 20190319
		var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
        window.open(iframe_url,'','height=280, width=510,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}
	function revertDataReturnWin(return_vo) {
        if(return_vo){
            newwindow=window.open('/report/edit_report/editReport.do?b_reverseFind=find&gridName=' +reverseFlag+ "&count="+gridVo.value+"&dbname="+ return_vo + "&encryptParam=" + encryptParam,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=170,left=220,width=530,height=550,resizable=yes');
        }
    }
</script>
<hrms:themes />
<logic:equal name="reportAnalyseForm" property="reportExist" value="yes">

	<body onload="load()" onKeyDown="return pf_ChangeFocus(); ">
		<form name="ra" method="post" action="/report/report_analyse/reportanalyse.do" style="margin-top: 2px;">
			<table width="1200" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="margin-left: -11px;">
				<tr>
					<td>
						&nbsp;&nbsp;&nbsp;<bean:message key="report.reportlist.selectReportSet" />&nbsp;
						<hrms:optioncollection name="reportAnalyseForm" property="reportSortList" collection="list0" scope="session" />
						<html:select name="reportAnalyseForm" property="reportSortID" size="1" onchange="changeSort()">
							<html:options collection="list0" property="dataValue" labelProperty="dataName" />
						</html:select>
					</td>
				</tr>
				<tr>
					<td style="padding-top:3px">
						<input type="hidden" name="codeFlag" value="<bean:write name="reportAnalyseForm" property="codeFlag" filter="true" />">

						<logic:present name="reportAnalyseForm" property="reportList" scope="session">	
							&nbsp;&nbsp;&nbsp;<bean:message key="reportanalyst.selectreport" />&nbsp;
							<hrms:optioncollection name="reportAnalyseForm" property="reportList" collection="list" scope="session" />
							<html:select name="reportAnalyseForm" property="reportTabid" size="1" onchange="javascript:tabidChange()">
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
						</logic:present>



						<logic:notEqual name="reportAnalyseForm" property="reportState" value="null">
							<bean:write name="reportAnalyseForm" property="reportState" filter="true" />
						</logic:notEqual>


						<logic:equal name="reportAnalyseForm" property="reportState" value="null">

							<bean:message key="edit_report.year" />
	   				 		<hrms:optioncollection name="reportAnalyseForm" property="reportYearidList" collection="list" />
							<html:select name="reportAnalyseForm" property="reportYearid" size="1" onchange="javascript:yearChange()">
								<html:options collection="list" property="dataValue" labelProperty="dataName" />
							</html:select>
							<logic:notEqual name="reportAnalyseForm"
								property="reportCountInfo" value="null">
								<bean:write name="reportAnalyseForm" property="reportCountInfo"
									filter="true" />
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
						<logic:equal name="reportAnalyseForm" property="reportState" value="null">
							&nbsp;&nbsp;
							<%
								if (reportTypes.equals("3") || reportTypes.equals("4")|| reportTypes.equals("5")){
							%>
								<hrms:priv func_id='2904004'>
									<input type="button" name="b_export" value="归档数据汇总" onclick='collect()' class="mybutton">
								</hrms:priv>

							<%
								}
							%>
							<hrms:priv func_id='2904001'>
								<input type="button" name="b_query" value="<bean:message key="report.reportlist.reverse"/>" onclick='revertData()' class="mybutton" style="margin-left: -3px;">
							</hrms:priv>
							<hrms:priv func_id='2904002'>
								<input type="button" name="b_export" value="<bean:message key="general.inform.muster.output.excel"/>" onclick='exportExcel()' class="mybutton" style="margin-left: -3px;">
							</hrms:priv>
							<hrms:priv func_id='2904003'>
								<input type="button" name="b_export" value="<bean:message key="report_collect.executeTable"/>" onclick='productIntegrateTable()' class="mybutton" style="margin-left: -3px;">
							</hrms:priv>
							<html:select name="reportAnalyseForm" property="right_fields" size="1"  style="display:none">
							</html:select>
						</logic:equal>
						
						<logic:equal name="reportAnalyseForm" property="returnflag" value="dxt">
						     <!-- 导航菜单进入增加返回按钮 xiaoyun 2014-5-19 start -->
						     <% 
								if(userview.getBosflag()!=null){
							 %>
							 <!-- 导航菜单进入增加返回按钮 xiaoyun 2014-5-19 end -->
								 <input type="button" name="b_delete" value="<bean:message key="button.return"/>" class=mybutton  onclick="hrbreturn('report','il_body','ra')">
							 <%} %>
						</logic:equal>

					</td>
				</tr>
			</table>
			<table>
				<tr>
					<td align="left">
					<div style="margin-top: 30px">
						<bean:write name="reportAnalyseForm" property="reportHtml" filter="false" />
						<script language='javascript'>
						initGrid();
				 		</script>
				 	</div>
					</td>
				</tr>
			</table>
		</form>
				
	</body>

</logic:equal>
<logic:equal name="reportAnalyseForm" property="reportExist" value="no">
	<body>
		<table width="60%" align="center" border="0" cellpadding="0" cellspacing="0">
			<tr class="list3">
				<td>
					<table width="100%" border="0" cellpadding="4" cellspacing="1" class="mainbackground">
						<tr class="list3">
							<td align="center" nowrap>
								<bean:message key="label.information" />
							</td>
						</tr>
						<tr class="list3">
							<td align="left" wrap>
								<bean:message key="report.usernotreport" />
							</td>
						</tr>
					</table>
				</td>
			</tr>
		</table>
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

</script>



