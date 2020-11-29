<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page
	import="com.hrms.struts.valueobject.UserView,com.hrms.hjsj.utils.Sql_switcher,
	com.hjsj.hrms.actionform.report.edit_report.EditReportForm,java.util.ArrayList,com.hrms.struts.taglib.CommonData,
	com.hjsj.hrms.actionform.report.auto_fill_report.ReportOptionForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%
	UserView userView = (UserView) request.getSession().getAttribute(
			WebConstant.userView);
	String userName = userView.getUserName();
	String name = userView.getUserFullName();
	String userId = userView.getUserId();
	EditReportForm editReportForm = (EditReportForm) session
			.getAttribute("editReportForm");
	String status = editReportForm.getStatus();

	String bosflag = "";
	if (userView != null) {
		bosflag = userView.getBosflag();
	}
	/**
	String aurl = (String) request.getServerName();
	String port = request.getServerPort() + "";
	String prl = request.getProtocol();
	int idx = prl.indexOf("/");
	prl = prl.substring(0, idx);
	String url_s = prl + "://" + aurl + ":" + port;
	**/
	String url_s=SystemConfig.getCsClientServerURL(request);
	//注意：给插件的数据库类型按实际类型传
	String dbtype = String.valueOf(Sql_switcher.searchDbServerFlag());
	String fields = userView.getFieldpriv().toString();
	String tables = userView.getTablepriv().toString();
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
	String license=lockclient.getLicenseCount();
	int version=userView.getVersion();
	if(license.equals("0"))
	version=100+version;
	int usedday=lockclient.getUseddays();
	String returnvalue =	editReportForm.getReturnflag();
	ReportOptionForm reportOptionForm=(ReportOptionForm)session.getAttribute("reportOptionForm");
	String updateflag="0";
   if(reportOptionForm!=null&&reportOptionForm.getUpdateflag()!=null){
       updateflag = reportOptionForm.getUpdateflag();
   
   }
   //add by wangchaoqun on 2014-9-25 begin
   //反查（revertData()）
   String encryptParam = PubFunc.encrypt("pageNum=1&tabid=" + editReportForm.getTabid() + 
		   "&username=" + editReportForm.getUsername1() + "&scopeid=" + editReportForm.getScopeid());
   //参数设置（parameterSet2()）
   String encryptParam1 = PubFunc.encrypt("tabid=" + editReportForm.getTabid() + "&status=" + request.getParameter("status") +
   "&operateObject=1&code=" + editReportForm.getUnitcode() + "&obj1=" + editReportForm.getObj1() + "&username="
   + editReportForm.getUsername1());
   //批量导出（print()）
   String encryptParam2 = PubFunc.encrypt("sortId=-1&print=1&tabid=" + editReportForm.getTabid() + "&username="
		   + editReportForm.getUsername1() + "&unitcode=" + editReportForm.getUnitcode() + "&obj1=" + 
		   editReportForm.getObj1() + "&operateObject=1&selfUnitcode=" + editReportForm.getSelfUnitcode());
   //总校验（reportAllValidate()）
   String encryptParam3 = PubFunc.encrypt("sortId=-1&print=5&checkFlag=1&obj1=" + editReportForm.getObj1()
		   + "&username=" + editReportForm.getUsername1());
   //审批（shenpi()）、驳回（bohui()）
   String encryptParam4 = PubFunc.encrypt("tabid=" + editReportForm.getTabid() + "&unitcode1=" + editReportForm.getUnitcode1());
   //add by wangchaoqun on 2014-9-26 end
%>
<head>
<meta http-equiv="X-UA-Compatible" content="IE=9; IE=8; IE=7">
</head>
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script language="javascript" src="/js/page_options.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT>
<!-- <script language="JavaScript" src="/js/meizzDate.js"></script> -->
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript" src="/report/edit_report/editReport.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
    var tabid="${editReportForm.tabid}";
	var rows="${editReportForm.rows}";
	var cols="${editReportForm.cols}";
	var param_str="${editReportForm.param_str}";
	var status='<%=(request.getParameter("status"))%>';
	var reverseFlag="";
	var selfType="${editReportForm.narch}"   //报表类型
	var selfUnitcode="${editReportForm.selfUnitcode}";
	var unitcode="${editReportForm.unitcode}";
	var userName="${editReportForm.userName}";
	var scopeid="${editReportForm.scopeid}";
	var use_scope_cond="${editReportForm.use_scope_cond}";
	var scopeownerunitid ="${editReportForm.scopeownerunitid}";
	var username = "${editReportForm.username}";
	var username1 = "${editReportForm.username1}";
	var unitcode1 = "${editReportForm.unitcode1}";
	var treport_ctrl = "${editReportForm.treport_ctrl}";
	var isApproveflag = "${editReportForm.isApproveflag}";
	var obj1 = "${editReportForm.obj1}";
	var isPrint = "${editReportForm.isPrint}";
	var encryptParam = '<%=encryptParam %>';
	var encryptParam1 = '<%=encryptParam1 %>';
	var encryptParam2 = '<%=encryptParam2 %>';
	var encryptParam3 = '<%=encryptParam3 %>';
	var encryptParam4 = '<%=encryptParam4 %>';
	  function reportdataanalyse(){
		var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid);
	    if(scopeid!=""&&scopeid!="0"){
	    hashvo.setValue("unitcode" ,scopeownerunitid);
	    }else{
	    hashvo.setValue("unitcode" ,selfUnitcode);
	    }
	   	var In_paramters="flag=1"; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:reportdataanalyseresult,functionId:'03020000025'},hashvo);			
	  }
		
	  function reportdataanalyseresult(outparamters){
		   var info = outparamters.getValue("info");
		   if(info == "true"){
		   <logic:equal name="editReportForm" property="use_scope_cond" value="1" >
			 editReportForm.action="/report/edit_report/reportanalyse.do?b_query2=link&tabid=" + tabid + "&username="+$URL.encode(username1)+"&obj1="+obj1+"&code="+selfUnitcode+"&scopeid="+scopeid;
			   editReportForm.submit();
			</logic:equal>	
			<logic:notEqual name="editReportForm" property="use_scope_cond" value="1" >
				editReportForm.action="/report/edit_report/reportanalyse.do?b_query=link&tabid=" + tabid + "&username="+$URL.encode(username1)+"&obj1="+obj1+"&code="+selfUnitcode+"&editOrreport=edit";
			   editReportForm.submit();
			</logic:notEqual>	
		  	  
		   }else{
		   	   alert(NOHISTORYDATA);
		   }
	  }
	  
	  function print() {
           newwindow=window.open('/report/auto_fill_report/reportlist.do?b_query=link&encryptParam=' + encryptParam2,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=100,resizable=no,width=800,height=600');
	       //newwindow=window.open('/servlet/Print?tabid=' + tabid + "&username=<%=userName%>&unitcode="+unitcode+"&operateObject=1&selfUnitcode=" + selfUnitcode,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,top=170,left=220,resizable=yes');
	  }
	  
	  function simplePrint() {
		  	//56045  V76报表管理：报表模块涉及到导出的（pdf、word、excel、zip等等）命名，请统一成： 登陆用户_相应信息
			var hashvo=new ParameterSet();
			var selectid=new Array();
			selectid[0]=tabid;
			hashvo.setValue("selectedlist",selectid);	
			hashvo.setValue("exportFashion",1);	
			hashvo.setValue("operateObject",1);	
			hashvo.setValue("unitcode",unitcode);
			hashvo.setValue("username",username1);			
			var request=new Request({method:'post',asynchronous:false,onSuccess:showView,functionId:'1010020207'},hashvo);			
	  }
	  function showView(outparamters)
		{	
			var path=outparamters.getValue("path");
			var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+path,"_self");
		}

	 function reportAllValidate(){
	       newwindow=window.open('/report/auto_fill_report/reportlist.do?b_query=link&encryptParam=' + encryptParam3 +'','','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,top=0,left=100,resizable=yes,width=800,height=700');
	 	
	 }
	 
	 
	 function exportExcel()
	 {
	 	var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("unitcode" ,unitcode);
	    hashvo.setValue("operateObject","1");
	    hashvo.setValue("username",username1);	    
		var request=new Request({method:'post',asynchronous:false,onSuccess:outFile,functionId:'03030000025'},hashvo);			
	 }
	 
	 
	 function outFile(outparamters)
	 {
	 	 
		 var outName=outparamters.getValue("outName");
		window.location.target="_blank";
		window.location.href = "/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;	//dml 2011-05-04
	 }
	 
	 function editReportStruct()
	 {
		 var hashvo=new ParameterSet();
	    hashvo.setValue("tabid",tabid);
	    hashvo.setValue("username1",username1);
		var request=new Request({method:'post',asynchronous:false,onSuccess:reportStrut,functionId:'03030000027'},hashvo);				 
	 }
	 
	 function reportStrut(outparamters)
	 {
	 	alert(MAINTAINSTRUCTURE+"！");
	 }
	 
	 
	 function goback()
	 {
	 	hrbreturn('report','il_body','editReportForm');
	 }
	 
	function csPrint()
	{
        var waitInfo=eval("wait");     
        waitInfo.style.display="block";
        if(!AxManager.setup("TJBP", "TJBPreview1", 0, 0, csPrint3, AxManager.tjbpkgName))
            return false;
        csPrint3();
	} 
	 var timecount=0;
	function csPrint3()
	{
		var obj = document.getElementById('TJBPreview1');  
		var aurl="<%=url_s%>";
		var DBType=<%=dbtype%>;
		var UserName="su";
		try{
			obj.SetURL(aurl);
			csPrint2();
		}catch(e1){
			timecount=timecount+1;
			if(timecount<20){
				setTimeout("csPrint3()",2000);
			}else{
				var waitInfo=eval("wait");	   
				waitInfo.style.display="none";
				alert("插件下载失败，请查看网速是否太慢或者插件被禁用！");
			}
		}
	}
	  
	function csPrint2()
	{
		var obj = document.getElementById('TJBPreview1');  
		var aurl="<%=url_s%>";
		var DBType=<%=dbtype%>;
		var UserName="su";
		obj.SetURL(aurl);
		obj.SetDBType(DBType);
		obj.SetUserName("<%=userName%>");
		obj.SetUserFullName("<%=name%>");
		obj.SetSuperUser(1);  // 1为超级用户,0非超级用户
		obj.SetUserMenuPriv("<%=fields%>");  // 指标权限, 逗号分隔, 空表示全权
		obj.SetUserTablePriv("<%=tables%>");  // 子集权限, 逗号分隔, 空表示全权
		obj.SetHrpVersion("<%=version%>");// 设置版本号40,43,50,+100表示试用版
		obj.SetTrialDays("<%=usedday%>","30");// 设置试用天数
		obj.SetReportType(0);  // 0:一般,1:综合表    
		obj.SetTableID(tabid);
		obj.SetUnitCode("<%=userName%>");  // 填报单位
		obj.SetParamType(0); // 0: BS编辑报表参数, 1: BS报表汇总参数(默认值)   
		var new_result=""
		for(var i=0;i<rows;i++)
		{
			var a_values='';
			for(var j=0;j<cols;j++)
			{
				var a_object=eval("document.editReportForm.a"+i+"_"+j);
				if(a_object.value==''||a_object.value==' ')
					a_values+=':0';
				else
					a_values+=':'+a_object.value;
			}
			  // new_result[i]=a_values.substring(1);
			new_result+="`"+a_values.substring(1);
		}
		var waitInfo=eval("wait");	   
		waitInfo.style.display="none";
		obj.SetCellValues(new_result.substring(1));
		try {
			obj.SetJSessionId(AxManager.getJSessionId()); 
		} catch(err) {}
		obj.ShowReportModal();
	}
    function showBat()
    {
        var waitInfo=eval("wait");     
        waitInfo.style.display="block";
        if(!AxManager.setup("TJBP", "TJBPreview1", 0, 0, showBat3, AxManager.tjbpkgName))
            return false;
		showBat3();   
	} 
    var return_vo;
	function showBat2(){
 		var waitInfo=eval("wait");	   
		waitInfo.style.display="none";
   		var theurl="/report/edit_report/printReport.do?b_query2=query&reptype=0";
  		var config = {
				width:744,
				height:450,
				title:'',
				theurl:theurl,
				id:'showBatId'
			}
			openWin(config);
  			Ext.getCmp("showBatId").addListener('close',function(){showWin(return_vo)});
	}
	function showWin(return_vo){
		if(return_vo){
		  	  var menu_table = return_vo.menu_table;
		      var aurl="<%=url_s%>";
		      var DBType=<%=dbtype%>;
		      var UserName="su"; 
		      var obj = document.getElementById('TJBPreview1');      
		      obj.SetURL(aurl);
		      obj.SetDBType(DBType);
		      obj.SetUserName("<%=userName%>");
		      obj.SetUserFullName("<%=name%>");
		      obj.SetSuperUser(1);  // 1为超级用户,0非超级用户
	     	  obj.SetUserMenuPriv("<%=fields%>");  // 指标权限, 逗号分隔, 空表示全权
	    	  obj.SetUserTablePriv("<%=tables%>");  // 子集权限, 逗号分隔, 空表示全权
	    	  obj.SetHrpVersion("<%=version%>");// 设置版本号40,43,50,+100表示试用版
	   		  obj.SetTrialDays("<%=usedday%>","30");// 设置试用天数
	          var reptype='0';
	          obj.SetReportType(0);  // 0:一般,1:综合表
	          obj.SetTableID(menu_table);  // 批量打印时，多个tabid逗号分隔
	     
	          obj.SetUnitCode(userName);  // 编辑报表
	          obj.SetParamType(reptype);  // 0: BS编辑报表参数/数据, 1: BS报表汇总-编辑报表参数/数据
	      
	         // 不需要传单元格数据，自动取值
	     	 // obj.SetCellValues();
	         try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
	     	 obj.ShowReportModal();
		  }
	}
	function showBat3()
	{
		var obj = document.getElementById('TJBPreview1');  
		var aurl="<%=url_s%>";
		var DBType=<%=dbtype%>;
		var UserName="su";
		try{
			obj.SetURL(aurl);
			showBat2();
		}catch(e1){
			timecount=timecount+1;
			if(timecount<20){
				setTimeout("showBat3()",2000);
			}else{
				var waitInfo=eval("wait");	   
				waitInfo.style.display="none";
				alert("插件下载失败，请查看网速是否太慢或者插件被禁用！");
			}
		}
	}	
	 

/*********************************************************
			
            剪贴板处理

**********************************************************/
			
	var bMouseDown=false;
	var bSelectCellMode=false;
	var startCell=null;  // 选中的input text
	var endCell=null;
	var rightmouseflag=null;
	var endCellFlag=null;
	var tyle=null;			//右键显示层的对象
	var expr_editor=null;//右键显示层的table对象	     
	function doformmousedown()
	{
		if(window.event.button!=2){
			var topy=document.body.scrollTop+event.clientY;
			var leftx=document.body.scrollLeft+event.clientX;
			//加上ie 9/8/7兼容模式下页面滚动后取不到光标正确的位置
			if(isCompatibleIE()){
				topy=document.documentElement.scrollTop+event.clientY;
				leftx=document.documentElement.scrollLeft+event.clientX;
			}
			if(rightmouseflag==1){
				var top= parseInt(tyle.top.replace("px",""));
				var left=  parseInt(tyle.left.replace("px",""));
				var width= parseInt( tyle.width.replace("px",""));
				var heigth= parseInt(tyle.height.replace("px",""));
				if(top<=topy&&topy<=heigth+top&&left<=leftx&&leftx<=left+width)//点击在这个范围不做处理
				{
					return;
				}else{
					if(tyle!=null)
						tyle.display="none";
				}
			}else{
				if(tyle!=null){
					var top=  parseInt(tyle.top.replace("px",""));
					var left=  parseInt(tyle.left.replace("px",""));
					var width= parseInt( tyle.width.replace("px",""));
					var heigth= parseInt(tyle.height.replace("px",""));
					
					if(top<=topy&&topy<=heigth+top&&left<=leftx&&leftx<=left+width)//点击在这个范围不做处理
					{
					
					}else{
						if(tyle!=null)
						tyle.display="none";
					}
				}
			
			}
			rightmouseflag=null;
			bMouseDown=true;
			var targ;
			var e = window.event;
			if (e.target) targ = e.target;
			else if (e.srcElement) targ = e.srcElement;
			if(targ!=null&&targ.name!=null){
				endCellFlag = targ;
			}else{
				endCellFlag =null;
			}
			if (targ.name!=null&&targ.name.substring(0,1)=="a"&&targ.name.substring(1,2)!="a")  // input标签
			{	
				if (!startCell){
					startCell=targ;
					startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
					startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
					var cell=eval("aa"+startRow+"_"+startCol);
					if(reverseFlag==''||reverseFlag==' '||reverseFlag!="a"+startRow+"_"+startCol){
						cell.style.border='2px solid blue';
					}
				}else{
					if(startCell!=null&&endCell!=null&&startCell.name!=endCell.name){
						showCellSelection(false);
						startCell=null;
						endCell=null;
						startCell=targ;
						startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
						startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
						var cell=eval("aa"+startRow+"_"+startCol);
						cell.style.border='2px solid blue';
					}
				}
			}
		}
	}
	function doformmouseup()
	{
		if(window.event.button!=2){
			if(rightmouseflag==1){
				return;
			}
			rightmouseflag=null;
			var targ;
			var e = window.event;
			
			if (e.target) targ = e.target;
			else if (e.srcElement) targ = e.srcElement;
			if(targ.name!=null&&targ.name.substring(0,1)=="a"&&targ.name.substring(1,2)!="a"){
			endCell=targ; 
			} 
			
			if(startCell!=null&&endCell!=null){
			
			var startCol=0;
			var startRow=0;
			var endCol=0;
			var endRow=0;
			
			if (startCell==null||startCell.name==null||endCell==null||endCell.name==null)
				return;
			
			startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
			startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
			endCol=parseInt(endCell.name.substring(endCell.name.indexOf("_")+1));
			endRow=parseInt(endCell.name.substring(1,endCell.name.indexOf("_")));
			
			var startCol2=startCol<endCol?startCol:endCol;
			var endCol2=startCol<endCol?endCol:startCol;
			var startRow2=startRow<endRow?startRow:endRow;
			var endRow2=startRow<endRow?endRow:startRow;
			
			for(var r=startRow2;r<=endRow2;r++)
			{
				for(var c=startCol2;c<=endCol2;c++)
				{
					if(reverseFlag!=''&&reverseFlag!=' '&&reverseFlag=="a"+r+"_"+c){
						continue;
					}
					var cell=eval("aa"+r+"_"+c);
					
					cell.style.border='2px solid blue';
				}
			}
			targ.style.cursor="default";  // 箭头
			}
		}
	}
	
        function showCellSelection(ShowOrHide)
        {
        	var startCol=0;
        	var startRow=0;
        	var endCol=0;
        	var endRow=0;
        	
        	if (startCell==null||startCell.name==null||endCell==null||endCell.name==null)
        	  return;
        	
        	startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
        	startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
        	endCol=parseInt(endCell.name.substring(endCell.name.indexOf("_")+1));
        	endRow=parseInt(endCell.name.substring(1,endCell.name.indexOf("_")));
        	
        	var startCol2=startCol<endCol?startCol:endCol;
        	var endCol2=startCol<endCol?endCol:startCol;
        	var startRow2=startRow<endRow?startRow:endRow;
        	var endRow2=startRow<endRow?endRow:startRow;
        	
      
        	for(var r=startRow2;r<=endRow2;r++)
        	{
	        	for(var c=startCol2;c<=endCol2;c++)
        		{
        			var cell=eval("aa"+r+"_"+c);
        			if(reverseFlag!=''&&reverseFlag!=' '&&reverseFlag=="a"+r+"_"+c){
        			continue;
        			}
        			if (ShowOrHide)
        				cell.style.border='2px solid blue';
        			else{
        			
        				cell.style.border='1px solid #000000';
        				if(cell.currentStyle){
        				cell.style.borderRightWidth=CellArray[r];
        			}
        			}
        		}
        	}
        }
	function pasteDataToSelection()  // 粘贴到选中区域
	{
		var startCol=0;
		var startRow=0;
		var endCol=0;
		var endRow=0;
		if (startCell==null||startCell.name==null||endCell==null||endCell.name==null)
			return;
		startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
		startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
		endCol=parseInt(endCell.name.substring(endCell.name.indexOf("_")+1));
		endRow=parseInt(endCell.name.substring(1,endCell.name.indexOf("_")));
		
		var startCol2=startCol<endCol?startCol:endCol;
		var endCol2=startCol<endCol?endCol:startCol;
		var startRow2=startRow<endRow?startRow:endRow;
		var endRow2=startRow<endRow?endRow:startRow;
		var s=clipboardData.getData("Text");
		if(!s)
			return;
		var lines=s.split("\n");
		for(var r=startRow2;r<=endRow2;r++)
		{
			var line=lines.shift();
			if (line!=null&&line!="")
			{
				var values=line.split("\t");
				for(var c=startCol2;c<=endCol2;c++)
				{
					var cellvalue = values.shift();
					
					if(!cellvalue){
						cellvalue ="";
					}
					var v=parseFloat(cellvalue);
					if (v!=null)
					{
						if(isNaN(v))
							v="";
						var cell=eval("document.editReportForm.a"+r+"_"+c);
						if(cell.readOnly){
							
						}else{
							cell.value=v;
						}
					}
				}
			}
		}
	}
        
	function pasteData(cell)  // 未选中多个单元格，直接粘贴
	{
		var startCol=0;
		var startRow=0;
		
		if (cell==null||cell.name==null)
		return;
		
		startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
		startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));

		var s=clipboardData.getData("Text");
		var lines=s.split("\n");

       	for(var r=startRow;r<=startRow+lines.length;r++)
       	{
       		var line=lines.shift();
       		if (line!=null&&line!="")
       		{
        		var values=line.split("\t");
	        	for(var c=startCol;c<=startCol+values.length;c++)
       			{
       				var v=parseFloat(values.shift());
       				if (v!=null&&!isNaN(v))
       				{
        				var cell=eval("document.editReportForm.a"+r+"_"+c);
     						cell.value=v;
   					}
       			}
       		}
		}
	}        
	function doformkeydown()
	{
	
		if (window.event.ctrlKey&&window.event.keyCode==86)  // Ctrl+V
		{ 
			if (startCell!=null&&endCell!=null)
			{
				window.event.keyCode=0;
				if(Ext.isIE){
					pasteDataToSelection();
					if(window.event.preventDefault){
						window.event.preventDefault();
						window.event.stopPropagation();
					}
					return false; 
				}
			}
		}
		if (window.event.ctrlKey&&window.event.keyCode==67)  // Ctrl+C
		{ 
			if (startCell!=null&&endCell!=null)
			{
				window.event.keyCode=0;
				if(Ext.isIE){
					copy();
					window.event.preventDefault();
					window.event.stopPropagation(); 
				}
				return false; 
			}
		}
	}	
         //屏蔽右键,实在没有办法的采用此办法,解决重复提交问题

	function changeBgColor(id)
	{
	   var e = document.getElementById(id);
	   e.style.backgroundColor="#98C2E8";
	  
	}
	function goBackBgColor(id)
	{
	   var e = document.getElementById(id);
	   e.style.backgroundColor="white";
	}
	function copy(){
		rightmouseflag=null;
		var startCol=0;
		var startRow=0;
		var endCol=0;
		var endRow=0;
		
		if (startCell==null||startCell.name==null||endCell==null||endCell.name==null){
			if(tyle!=null)
				tyle.display="none";
			return;
		}
		startCol=parseInt(startCell.name.substring(startCell.name.indexOf("_")+1));
		startRow=parseInt(startCell.name.substring(1,startCell.name.indexOf("_")));
		endCol=parseInt(endCell.name.substring(endCell.name.indexOf("_")+1));
		endRow=parseInt(endCell.name.substring(1,endCell.name.indexOf("_")));
		
		var startCol2=startCol<endCol?startCol:endCol;
		var endCol2=startCol<endCol?endCol:startCol;
		var startRow2=startRow<endRow?startRow:endRow;
		var endRow2=startRow<endRow?endRow:startRow;
        	
		var lines='';
		for(var r=startRow2;r<=endRow2;r++)
		{
			var line='';
			for(var c=startCol2;c<=endCol2;c++)
			{
				var cell=eval("document.editReportForm.a"+r+"_"+c);
				line+=cell.value+"\t";
			//line+=cell.createTextRange().text+"\t";
			}
			if(line.length>0)
				line=line.substring(0,line.length-1);
			lines+=line+"\n";
		}
		if(lines.length>0)
			lines=lines.substring(0,lines.length-1);
		clipboardData.setData("Text",lines);
		if(tyle!=null)
			tyle.display="none";
	}  
	function parse(){
		rightmouseflag=null;
		
		if (startCell!=null&&endCell!=null)
		{
			pasteDataToSelection();
		}
		if(tyle!=null)
			tyle.display="none";
	} 
	function scopeChange(scopeid2){
		if(scopeid2!=""){
			var hashvo=new ParameterSet();
			var selectid=new Array();
			selectid[0]= tabid;
			hashvo.setValue("selectid",selectid);
			hashvo.setValue("updateflag",<%=updateflag %>);			
			hashvo.setValue("scopeid",scopeid2);			
			var In_paramters="flag=1"; 		
			var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo22,functionId:'03010000002'},hashvo);
		}
	} 
	function returnInfo22(outparamters){
		var href = window.location.href;
		window.location.href=href;
	}	   
	 
</script>
<link href="/css/css1_report.css" rel="stylesheet" type="text/css">
<hrms:themes />
<!-- 编辑报表 xiaoyun 2014-6-25 start -->
<style>
.text {
    font-family:微软雅黑;
	BACKGROUND-COLOR:transparent;
	font-size: 12px;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
<!--
#idDIV{
	margin-left: -1px;
}	
/* .ListTable tr {
	line-height:14px;
} */
-->
<!-- 编辑报表 xiaoyun 2014-6-25 end -->
</style>
<body onmousedown="doformmousedown()">
	<form action="/report/edit_report/editReport" name="editReportForm"
		method="post" onmouseup="doformmouseup();"
		onkeydown="doformkeydown();">
		<table>
		<tr>
		<td width="800px">
		<div id="menu" style="width:100%"></div>
		</td>
		</tr>
		<tr>
		<td>
		${editReportForm.htmlCode}
		</td>
		</tr>
		</table>
		<div id="date_panel" style="background:#ffffff; groove black;top:0px;left:0px;width:105px;height:100px;display:none ;
		    border-right: activeborder 1px solid; border-top: activeborder 1px solid; border-left: activeborder 1px solid; border-bottom: activeborder 1px solid;
			filter:progid:DXImageTransform.Microsoft.dropshadow(OffX=2, OffY=2,
		Color='gray', Positive='true');opacity:90;
		     ">			  
			<table id="date_box" name="date_box" width="100%">  
			<tr id='b_2' onMouseOver="changeBgColor('b_2')" onMouseOut="goBackBgColor('b_2')" onclick="copy();" style="cursor:hand;">
				<td width="100%">
				&nbsp;&nbsp;&nbsp;复制
				</td>
			</tr> 
			<tr id='b_3' onMouseOver="changeBgColor('b_3')" onMouseOut="goBackBgColor('b_3')" onclick="parse();" style="cursor:hand;">
				<td width="100%">
				&nbsp;&nbsp;&nbsp;粘贴
				</td>
			</tr> 
			<tr><td style="color:#808695;font-size:10px;-webkit-transform:scale(0.8)">非ie粘贴剪切板内容请使用ctrl+v</td></tr>  
			</table>
		</div>
		<input type="hidden" name='tabid' value="${editReportForm.tabid}" />
		<input type="hidden" name='rows' value="${editReportForm.rows}" />
		<input type="hidden" name='cols' value="${editReportForm.cols}" />
		<input type="hidden" name='reportResultData' value="" />

	</form>

	<div id="TJBP">
	</div>
	<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在加载报表数据....</td>
          </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
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


<script language="javascript">
	var dropDownDate=createDropDown("dropDownDate");
	var __t=dropDownDate;
	__t.type="date";
	__t.tag="";
	_array_dropdown[_array_dropdown.length]=__t;
	initDropDown(__t);
	
	var CellArray = new Array(rows);	//恢复样式目前只恢复borderRightWidth
	var endcols=cols-1;
	var pageResult=new Array(rows);		//页面值得二维数组		
	for(var a=0;a<rows;a++)
	{
		var c_object =eval("aa"+a+"_"+endcols);
		if(c_object.currentStyle){
			CellArray[a]=c_object.currentStyle['borderRightWidth'];
		}
		var cols_ob=new Array(cols);
		for(var b=0;b<cols;b++)
		{	
			var a_object=eval("document.editReportForm.a"+a+"_"+b);
			
			if(a_object.value==''||a_object.value==' ')
				cols_ob[b]=0;
			else
				cols_ob[b]=a_object.value;
		}
		pageResult[a]=cols_ob;
	}
	function newButton(text,items){
		var button = Ext.create('Ext.Button', {
			    text: text,
			    border:0,
			    margin:'4 4 0 4',
			    menu:[{xtype: 'menu',width: 150,floating: false,items:items}]
			});
		return button;
	}
	var buttons = [];
	<%if(userView.hasTheFunction("29020")){%>
		var items = [];
		<%if(userView.hasTheFunction("29020701")&&"1".equals(editReportForm.getUse_scope_cond())){
		%>
		items.push({text: '设置统计口径',icon:"/images/img_y.gif",handler:function(){setStaticStatement();}});
		<%}
		if(userView.hasTheFunction("290201")&&editReportForm.getOperateObject().equals("1")
			&&!status.equals("1") && !status.equals("3")){%>
		items.push({text: '<bean:message key="button.save" />',icon:"/images/save_edit.gif",handler:function(){save();}});
		<%}
		if(userView.hasTheFunction("290203")&&!"0".equals(editReportForm.getExistunicode())
				&&!"1".equals(editReportForm.getUse_scope_cond())){
			if(editReportForm.getIsApproveflag().equals("4")||editReportForm.getIsApproveflag().equals("1")
				||editReportForm.getIsApproveflag().equals("3")){%>
		items.push({text: '<bean:message key="reportManager.appeal" />',icon:"/images/sb.gif",handler:function(){appeal_1();}});
		<%}else if(editReportForm.getIsApproveflag().equals("2")){%>
		items.push({text: '报批',icon:"/images/sb.gif",handler:function(){appeal_4();}});
		<%}}
		if(userView.hasTheFunction("290206")&&!status.equals("1")&&!status.equals("3")&&!status.equals("4")){%>
		items.push({text: '<bean:message key="reportManager.clearData" />',icon:"/images/quick_query.gif",handler:function(){clear();}});
		<%}
		if(userView.hasTheFunction("290204")){%>
		items.push({text: '<bean:message key="report.reportlist.reverse" />',icon:"/images/quick_query.gif",handler:function(){revertData();}});
		<%}
		if(userView.hasTheFunction("290202")){%>
		items.push({text: '<bean:message key="report.reportlist.editparam" />',icon:"/images/prop_ps.gif",handler:function(){parameterSet2();}});
		<%}
		if(!"false".equals(editReportForm.getReboundDescription())){%>
		items.push({text: '<bean:message key="report.reportlist.lookRejectCause" />',icon:"/images/hint.gif",handler:function(){description();}});
		<%}
		if(userView.hasTheFunction("290205")&&editReportForm.getIsPrint().equals("true")){%>
		 if(Ext.isIE && window.navigator.platform!="Win64"){//插件只支持ie32为浏览器
			items.push({text: '<bean:message key="reportcheck.title_yuyan" />',icon:"/images/print.gif",menu:{items:[{text:'<bean:message key="edit_report.sigleprint" />',handler:function(){csPrint();}},{text:'<bean:message key="reportcheck.batch.print" />',handler:function(){showBat();}}]}});
		 }
		<%}
		if(userView.hasTheFunction("290205")){%>
		items.push({text: '<bean:message key="button.export" />',icon:"/images/print.gif",menu:{items:[{text:'<bean:message key="edit_report.importexcel" />',handler:function(){exportExcel();}},{text:'<bean:message key="edit_report.importPDF" />',handler:function(){simplePrint();}},{text:'<bean:message key="edit_report.batchoutport" />',handler:function(){print();}}]}});
		<%}%>
		buttons.push(newButton('文件',items));
		<%}%>
		<%if(userView.hasTheFunction("29022")){%>
		var items = [];
		<%if(userView.hasTheFunction("290221")){%>
		items.push({text: '<bean:message key="reportlist.reportinnercheck" />',icon:"/images/check.gif",handler:function(){promptlyValidate();}});
		<%}
		if(userView.hasTheFunction("290222")){%>
		items.push({text: '<bean:message key="reportlist.reportspacecheck" />',icon:"/images/check.gif",handler:function(){reportValidate();}});
		<%}
		if(userView.hasTheFunction("290223")){%>
		items.push({text: '<bean:message key="report_collect.totalValidate" />',icon:"/images/check.gif",handler:function(){reportAllValidate();}});
		<%}%>
		buttons.push(newButton('<bean:message key="reportspacecheck.check" />',items));
		<%}%>
 		<%if(userView.hasTheFunction("29021")){%>
		var items = [];
 			<%if(userView.hasTheFunction("290211")){%>
 			items.push({text: '<bean:message key="edit_report.reportCalculate" />',icon:"/images/compute.gif",handler:function(){reportsCount(1);}});
 			<%}
 			if(userView.hasTheFunction("290212")){%>
 			items.push({text: '<bean:message key="edit_report.reportsCalculate" />',icon:"/images/compute.gif",handler:function(){reportsCount(2);}});
 			<%}
 			if(userView.hasTheFunction("290213")){%>
 			items.push({text: '<bean:message key="edit_report.totalCalculate" />',icon:"/images/check.gif",handler:function(){reportsCount(3);}});
 			<%}%>
 			buttons.push(newButton('<bean:message key="infor.menu.compute" />',items));
		<%}%>
 		<%if(userView.hasTheFunction("290323")){%>
			var items = [];
 			<%if(userView.hasTheFunction("290211")){
 			if(editReportForm.getIsUpapprove().equals("true")){%>
 			items.push({text: '批准',icon:"/images/link.gif",handler:function(){approve();}});
 			items.push({text: '驳回',icon:"/images/link.gif",handler:function(){returnApprove(1);}});
 			<%}else if(editReportForm.getIsUpapprove().equals("false")){%>
 			items.push({text: '驳回',icon:"/images/link.gif",handler:function(){returnApprove(1);}});
 			<%}else{%>
 			items.push({text: '审批意见',icon:"/images/link.gif",handler:function(){shenpi(1);}});
 			<%}}
 			if(userView.hasTheFunction("290212")&&!editReportForm.getIsUpapprove().equals("true")
					&&!editReportForm.getIsUpapprove().equals("false")){%>
 			items.push({text: '驳回原因',icon:"/images/link.gif",handler:function(){bohui(2);}});
 			<%}%>
 			buttons.push(newButton('<bean:message key="button.audit" />',items));
		<%}%>
 		<%if(userView.hasTheFunction("29023")){%>
		var items = [];
 			<%if(!"0".equals(editReportForm.getExistunicode())){
 			if(userView.hasTheFunction("290231")){%>
 			items.push({text: '<bean:message key="edit_report.reportDataMerger" />',icon:"/images/ac.gif",handler:function(){pigeonhole();}});
 			<%}
 			if(userView.hasTheFunction("290233")){%>
 			items.push({text: '<bean:message key="edit_report.reportDataAnalyse" />',icon:"/images/compute.gif",handler:function(){reportdataanalyse();}});
 			<%}}
 			if(userView.hasTheFunction("290232")){%>
 			items.push({text: '<bean:message key="edit_report.tableConstructMaintain" />',icon:"/images/sb.gif",handler:function(){editReportStruct();}});
 			<%}%>
 			buttons.push(newButton('<bean:message key="edit_report.reportAnalyse" />',items));
	<%}
	if (bosflag != null && returnvalue.equals("dxt")) {
	%>
		buttons.push({xtype: 'button',border:0,margin:'4 4 0 4',text: '<bean:message key="button.return" />',handler:function(){goback();}});
	<%
		}
	%>
	
	<%if(editReportForm.getObj1().equals("1")){ %>
	buttons.push({xtype: 'button',border:0,margin:'4 4 0 4',text: '<bean:message key="reportcheck.return" />',handler:function(){goback1(${editReportForm.returnType});}});
	<%} %>
	var data = [];
	var obj = new Object();
	obj.dataName ='空';
	obj.dataValue ='0';
	data.push(obj);
	<%ArrayList scopelist = editReportForm.getScopelist();
		for(int i=0;i<scopelist.size();i++){
			CommonData data = (CommonData)scopelist.get(i);
			String dataName=data.getDataName();
			String dataValue=data.getDataValue();
	%>
	var obj = new Object();
	obj.dataName ='<%=dataName%>';
	obj.dataValue ='<%=dataValue%>';
	data.push(obj);
	<%
		}
	%>
	<%if(userView.hasTheFunction("29020702")&&"1".equals(editReportForm.getUse_scope_cond())){%>
		buttons.push(
			{xtype:'combo',
			fieldLabel:'<bean:message key="report.static.statement"/>',
			labelSeparator:'：',
			labelWidth:76,
			labelAlign:'right',
			margin:'4 4 0 0',
			width:200,
			value:'<%=editReportForm.getScopeid()%>',
			displayField: 'dataName',
		    valueField: 'dataValue',
		    store:{xtype:'store',fields: ['dataName', 'dataValue'],data:data},
		    listeners: {
		    	'change':function(obj,newvalue){
		    			scopeChange(newvalue);
		    		}
		    	}
			});
	<%}%>
	Ext.create('Ext.panel.Panel', {
	    border:0,
	    height:30,
	    width:800,
	    renderTo: 'menu',
	    layout:'hbox',
	    items:buttons
	});
	
	window.onload=function(){
		if(Ext.isIE){
			var divs=document.getElementsByTagName("div")
			for(var i=0;i<divs.length;i++){
				if(divs[i].getAttribute("name")=="bottom_div_name"){
					divs[i].style.posTop=divs[i].style.posTop+2;
				}
			}
		}else{
			//非ie自己定义clipboardData,缺点没办法从页面外复制内容，但是可以复制表格
	    	function ClipBoardData()
	    	{
	    		
	    	}
			ClipBoardData.prototype.setData = function(name,value) {
				this[name]=value;
			}
			ClipBoardData.prototype.getData = function(name) {
				return this[name];
			}
			clipboardData = new ClipBoardData();
		}
		
	}
</script>


