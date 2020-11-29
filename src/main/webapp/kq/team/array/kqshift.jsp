<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.kq.team.KqShiftForm" %>
<% 
    KqShiftForm kqShiftForm = (KqShiftForm) session.getAttribute("kqShiftForm"); 
    String a_code = kqShiftForm.getA_code();
    a_code = PubFunc.decrypt(a_code);
    String code ="";
    if(a_code!=null&&a_code.length()>0)
     code =a_code.substring(0,2);
    String code_kind = request.getParameter("a_code");
    
    String selname = kqShiftForm.getSelect_name();
    if ("".equals(selname))
        selname = "请输入姓名、工号或考勤卡号";
 %>
<link rel="stylesheet" type="text/css" href="../../../ajax/skin.css"></link>
<script language="javascript" src="../../../ajax/constant.js"></script>
<script language="javascript" src="../../../ajax/basic.js"></script>
<script language="javascript" src="../../../ajax/common.js"></script>
<script language="javascript" src="../../../ajax/control.js"></script>
<script language="javascript" src="../../../ajax/dataset.js"></script>
<script language="javascript" src="../../../ajax/editor.js"></script>
<script language="javascript" src="../../../ajax/dropdown.js"></script>
<script language="javascript" src="../../../ajax/table.js"></script>
<script language="javascript" src="../../../ajax/menu.js"></script>
<script language="javascript" src="../../../ajax/tree.js"></script>
<script language="javascript" src="../../../ajax/pagepilot.js"></script>
<script language="javascript" src="../../../ajax/command.js"></script>
<script language="javascript" src="../../../ajax/format.js"></script>
<script language="javascript" src="../../../js/validate.js"></script>
<SCRIPT LANGUAGE=javascript src="../../../js/xtree.js"></SCRIPT>
<script type="text/javascript" src="../../../general/tipwizard/returnT.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="../../../js/popcalendar.js"></script>
<script type="text/javascript" src="../../../kq/kq.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<style type="text/css">
body {
	background-color: transparent;
	margin: 0px;
}

.m_frameborder {
	border: 1px solid;
	width: 40px;
	height: 18px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
}

.m_arrow {
	float: left;
	margin-top: 1px;
	<!--[if gte IE 9]>padding-top: 1px;<![endif]-->
	<!--[if lt IE 8]>paddint-top:0px;<![endif]-->
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}

.m_input {
	width: 13px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}

input_text {
	border: inset 1px #000000;
	BORDER-BOTTOM: #FFFFFF 0pt dotted;
	BORDER-LEFT: #FFFFFF 0pt dotted;
	BORDER-RIGHT: #FFFFFF 0pt dotted;
	BORDER-TOP: #FFFFFF 0pt dotted;
}
</style>
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
</script>
<style type="text/css">
<!--
.RecordRow3 {
	border: inset 1px #B9D2F5;
	BORDER-BOTTOM: #B9D2F5 1pt solid; 
	BORDER-LEFT: #B9D2F5 1pt solid; 
	BORDER-RIGHT: #B9D2F5 1pt solid; 
	BORDER-TOP: #B9D2F5 1pt solid;
	font-size: 12px;
	height:70;
}
.divStyle{
	border:1px solid #C4D8EE;overflow: auto;left:5;
	height:expression(document.body.clientHeight-75);
	width:expression(document.body.clientWidth-5);
}
-->
</style>
<script language="javascript">

	function reLoading(){
		var state="${kqShiftForm.state}";
		var selectShowBar = "${kqShiftForm.selectShowBar}";
		if("1"==state && selectShowBar=="1")
		{
			var target_searchbar = document.getElementById("search");
   			target_searchbar.style.display ="inline";
   			showOrNOFirstBar(target_searchbar.style.display);
		}
		var startTimeH = '${kqShiftForm.startTimesH}';
		var startTimesM = '${kqShiftForm.startTimesM}';
		var endTimesH = '${kqShiftForm.endTimesH}';
		var endTimesM = '${kqShiftForm.endTimesM}';
		var start_h = document.getElementById("start_h");
		var start_m = document.getElementById("start_m");
		var end_h = document.getElementById("end_h");
		var end_m = document.getElementById("end_m");
		if(startTimeH!=""&&startTimesM!=""&&endTimesH!=""&&endTimesM!="")
		{
			start_h.value = startTimeH;
			start_m.value = startTimesM;
			end_h.value = endTimesH;
			end_m.value = endTimesM;
			
		}else{
			start_h.value = "00";
			start_m.value = "00";
			end_h.value = "23";
			end_m.value = "59";
		}
    }
   function change(flag)
   {
   	   if(flag=="5"){
   	   	var dateBegin = document.getElementById("dateBegin").value;
    	var identities = document.getElementsByName("identity");
    	var startTimes = "";
    	var endTimes = "";
    	var checked = "";
    	var select_kqlist = "";
    	for(var i=0;i<identities.length;i++){
			if(identities[i].checked){
				checked = identities[i].value;
				if(checked==1){
					startTimes = document.getElementById("start_h").value+":"+document.getElementById("start_m").value+'';
					endTimes = document.getElementById("end_h").value+":"+document.getElementById("end_m").value+'';
					var checkType = "1";
					kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&dateBegin="+dateBegin+"&startTimes="+startTimes+"&endTimes="+endTimes+"&checkType="+checkType+"&selectShowBar=1";
				}else{
					select_kqlist =  document.getElementById("select_kqlist").value;
					var checkType = "2";
					kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&dateBegin="+dateBegin+"&select_kqlist="+select_kqlist+"&checkType="+checkType+"&selectShowBar=1";
				}
			}
		}
             kqShiftForm.submit();
   	   }else{
	   //当变更日期时，将周默认设为第一周，防止出现异常
	   if(flag==1&&document.getElementsByName("week_data")[0]!=undefined){
	  	 document.getElementsByName("week_data")[0].value = "第一周";
	   }
   		var pre = document.getElementsByName("select_pre");
   		var nbase = pre[0].value;
        var selectname = document.getElementsByName("select_name");
   		var actions="";
   		if(selectname[0].value.length>0)
   			actions="&select_flag=1";
      kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&a_code=${kqShiftForm.a_code}"+actions;
      kqShiftForm.submit();
      }
   } 
   function normal_shtif()
   { 
	 var code_kind = document.getElementById("a_code").value;
     if(code_kind == "null"){
         alert("请选择需要排班的对象！");
		return false;
     }

     if(code_kind=="GP"&&code_kind.length==2)
     {
       alert("请选择确定的班组！");
       return false;
     }
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/team/array/normal_array_data.do?b_normal=link`a_code=${kqShiftForm.a_code}`nbase=${kqShiftForm.nbase}`session_data=${kqShiftForm.session_data}`week_data=${kqShiftForm.week_data}`state=${kqShiftForm.state}";
     if($URL)
    	 target_url = $URL.encode(target_url);
     var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
     var return_vo= window.showModalDialog(iframe_url,window, 
        "dialogWidth:450px; dialogHeight:300px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(!return_vo)
	    return false;	   
      if(return_vo.flag=="true")
      {
         //var waitInfo=eval("wait");	   
	     //waitInfo.style.display="block";  	     
      }
   }
   function cycle_shtif()
   {
       var o=eval("document.kqShiftForm.a_code");
       var a_code=o.value;    
       var target_url;
       target_url="/kq/team/array/cycle_array_data.do?b_cycle=link`a_code=${kqShiftForm.a_code}`nbase=${kqShiftForm.nbase}`session_data=${kqShiftForm.session_data}";
       if($URL)
      	 target_url = $URL.encode(target_url);
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var return_vo= window.showModalDialog(iframe_url,window, 
        "dialogWidth:800px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
	    return false;	   
       if(return_vo.flag=="true")
       {
         //var waitInfo=eval("wait");	   
	     //waitInfo.style.display="block";  
	     //window.location.reload();
       }
  }
   function relief_shtif()
   {
       var o=eval("document.kqShiftForm.a_code");
       var a_code=o.value;    
       var target_url;
       target_url="/kq/team/array/relief_array_data.do?b_relief=link`a_code=${kqShiftForm.a_code}`nbase=${kqShiftForm.nbase}`session_data=${kqShiftForm.session_data}";
       if($URL)
      	 target_url = $URL.encode(target_url);
       var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
       var return_vo= window.showModalDialog(iframe_url,window, 
        "dialogWidth:700px; dialogHeight:485px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       if(!return_vo)
	    return false;	   
       if(return_vo.flag=="true")
       {
         //var waitInfo=eval("wait");	   
	     //waitInfo.style.display="block";  
       }
   }
   function unsteady_shtif()
   {
       kqShiftForm.action="/kq/team/array/unsteady_shift.do?b_query=link&action=unsteady_shift_data.do&target=mil_body&viewPost=kq";
       kqShiftForm.target="il_body";
       kqShiftForm.submit();
   }
   function groupSet()
   {
      kqShiftForm.action="/kq/team/array_set/search_array_data.do?b_search=link&action=search_array_data.do&target=mil_body&return_code=${kqShiftForm.a_code}&unCodeitemid=${kqShiftForm.unCodeitemid}";
      kqShiftForm.submit();
   }
  function show_state(s)
  {
     var state = s.options[s.selectedIndex].value;     
     kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&a_code=${kqShiftForm.a_code}&state="+state;
     kqShiftForm.submit();
  }
  function excecuteExcel()
   {
	var hashvo=new ParameterSet();		
	hashvo.setValue("a_code","${kqShiftForm.a_code}");
	hashvo.setValue("nbase","${kqShiftForm.nbase}");
	hashvo.setValue("grnbase","${kqShiftForm.grnbase}");
	hashvo.setValue("session_data","${kqShiftForm.session_data}");
	hashvo.setValue("week_data","${kqShiftForm.week_data}");
	hashvo.setValue("state","${kqShiftForm.state}");
	hashvo.setValue("select_name","${kqShiftForm.hidden_name}");
	hashvo.setValue("select_pre","${kqShiftForm.select_pre}");
	hashvo.setValue("select_flag","${kqShiftForm.select_flag}");
	hashvo.setValue("finsh","1");
	hashvo.setValue("decrypt_flag","1");
	
	hashvo.setValue("startTimes", "${kqShiftForm.startTimes}");
	hashvo.setValue("endTimes", "${kqShiftForm.endTimes}");
	hashvo.setValue("select_kqlist", "${kqShiftForm.select_kqlist}");
	hashvo.setValue("dateBegin", "${kqShiftForm.dateBegin}");
	hashvo.setValue("clicked", "${kqShiftForm.clicked}");
	hashvo.setValue("identity", "${kqShiftForm.identity}");
	hashvo.setValue("checkType", "${kqShiftForm.checkType}");
	hashvo.setValue("selectShowBar", "${kqShiftForm.selectShowBar}");
	
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15221000004'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	if($URL)
   	 url = $URL.encode(url);
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true";
   }
    function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }
   function viewAll()
   {
       Element.hide('datepnl');
       kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&select_flag=0";
       kqShiftForm.submit();
   }
   function selectflag()
   {
      var select_pre=$F('select_pre');
      if(select_pre==null||select_pre==""||select_pre=="0")
      {
         alert("请选择筛选人员库！");
         return false;
      }

      var select_name = document.getElementById("select_name").value;;
      //alert("select_name---"+select_name);
      if(select_name=="请输入姓名、工号或考勤卡号")
      {
    	  select_name="";
      }
      var nbase = "${kqShiftForm.nbase}";
      kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&select_flag=1&selectShowBar=&nbase="+nbase;
      kqShiftForm.submit();
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function show_noclass()
   {
      kqShiftForm.action="/kq/team/array/search_noarray.do?b_query=link&privtype=kq";
      kqShiftForm.target="il_body";
      kqShiftForm.submit();
   }
   function editClass(nbase,a_code,days,type)
   {
	  days = days.replace('.','-');
	  days = days.replace('.','-');
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      if(type==null){
      	type='2';
      }
      target_url="/kq/team/array/normal_array_data.do?b_normal=link`a_code="+a_code+"`nbase="+nbase+"`session_data="+days+"`isKqShift="+type+"";
      if($URL)
     	 target_url = $URL.encode(target_url);
      var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
      var return_vo= window.showModalDialog(iframe_url,window, 
        "dialogWidth:500px; dialogHeight:305px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      
      // 45956 没有返回值 直接刷新页面即可
      kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&select_flag=1&nbase="+nbase;
      kqShiftForm.submit();
      if(!return_vo)
	    return false;	   
   }
   function Excel_stencil()
   {
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;";
      var weekData = "${kqShiftForm.week_data}";
      if($URL)
    	  weekData = $URL.encode(weekData);      
      target_url="/kq/team/array/excel_shift_data.do?b_select=link&a_code=${kqShiftForm.a_code}&nbase=${kqShiftForm.nbase}&session_data=${kqShiftForm.session_data}&week_data=" + weekData + "&state=${kqShiftForm.state}";
      var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:320px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(!return_vo)
	    return false;	   
      if(return_vo.flag=="true")
      {
          var hashvo=new ParameterSet();			
	      hashvo.setValue("a_code",return_vo.a_code);
	      hashvo.setValue("nbase",return_vo.nbase);
	      hashvo.setValue("start_date",return_vo.start_date);
	      hashvo.setValue("end_date",return_vo.end_date);	      
	      var request=new Request({method:'post',asynchronous:false,onSuccess:downStencil,functionId:'15221000251'},hashvo);
      }
   }  
   function downStencil(outparamters)
   {
      var url=outparamters.getValue("excelfile");	
      if(url!=""){
          window.location.target="_blank";
	      window.location.href = "/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true";
      }
   }
   function input_Excel()
   {
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/team/array/excel_shift_data.do?br_input=link";
      var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
      var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:440px; dialogHeight:250px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(!return_vo)
	    return false;	   
      if(return_vo.flag=="true")
      {
         var waitInfo=eval("wait");	   
	     waitInfo.style.display="block";
	     location=location;
		 alert("文件上传成功！");
      }
   }
   function group_syn_shtif()
   {
      
          var target_url;
          var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
          target_url="/kq/team/array/syn_group_data.do?b_syndate=link&session_data=${kqShiftForm.session_data}&syn_uro=2";
          var return_vo= window.showModalDialog(target_url,1, 
         "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
          if(!return_vo)
	         return false;	   
	      if(return_vo.pick_type!='')
	      {
	         var waitInfo=eval("wait");	   
	         waitInfo.style.display="block";
	         kqShiftForm.action="/kq/team/array/syn_group_data.do?b_syngroup=link&a_code=${kqShiftForm.a_code}&syc_type="+return_vo.syc_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&session_data=${kqShiftForm.session_data}&grnbase=${kqShiftForm.grnbase}";
             kqShiftForm.submit();
	      }
   }
   function gr_syn_shtif()
   {
          var target_url;
          var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
          target_url="/kq/team/array/syn_group_data.do?b_syndate=link&session_data=${kqShiftForm.session_data}&syn_uro=1";
          var return_vo= window.showModalDialog(target_url,1, 
         "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
          if(!return_vo)
	         return false;	   
	      if(return_vo.pick_type!='')
	      {
	         var waitInfo=eval("wait");	   
	         waitInfo.style.display="block";
	         kqShiftForm.action="/kq/team/array/syn_group_data.do?b_syngroup=link&a_code=${kqShiftForm.a_code}&syc_type="+return_vo.syc_type+"&start_date="+return_vo.start+"&end_date="+return_vo.end+"&session_data=${kqShiftForm.session_data}&grnbase=${kqShiftForm.grnbase}";
             kqShiftForm.submit();
	      }
   }
   function searchBar()
   {
   		var target_searchbar = document.getElementById("search");
   		if(target_searchbar.style.display =="inline"){
   			target_searchbar.style.display ="none";
   			showOrNOFirstBar(target_searchbar.style.display);
   		}else{ 
   			target_searchbar.style.display ="inline";
   			showOrNOFirstBar(target_searchbar.style.display);
   		}
   		var dateBegin = document.getElementById("dateBegin");
   		if(dateBegin.value==""){
   		var d = new Date();
   		var year = d.getFullYear();
   		var month = d.getMonth()+1;
   		var date = d.getDate();
   		if(month<10)
   		month = "0"+month;
   		if(date<10)
   		date = "0"+date;
		var str = year+"."+month+"."+date;
		dateBegin.value=str;
		}
   }
   this.fObj = null;
   var time_r=0; 
   function setFocusObj(obj,time_vv) 
   {		
	this.fObj = obj;
	time_r=time_vv;		
   }
   function IsInputTimeValue() 
   {	     
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj) return;		
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);		
       var radix=parseInt(time_r,10)-1;				
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }	
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }			
       fObj.select();
    }
    function IsInputTimeValue() 
   {	     
       event.cancelBubble = true;
       var fObj=this.fObj;		
       if (!fObj) return;		
       var cmd = event.srcElement.innerText=="5"?true:false;
       if(fObj.value==""||fObj.value.lenght<=0)
	  fObj.value="0";
       var i = parseInt(fObj.value,10);		
       var radix=parseInt(time_r,10)-1;				
       if (i==radix&&cmd) {
           i = 0;
       } else if (i==0&&!cmd) {
	   i = radix;
       } else {
	   cmd?i++:i--;
       }	
       if(i==0)
       {
	  fObj.value = "00"
       }else if(i<10&&i>0)
       {
	  fObj.value="0"+i;
       }else{
	  fObj.value = i;
       }			
       fObj.select();
    }  
    function IsDigit() 
    { 
       return ((event.keyCode >= 48) && (event.keyCode <= 57)); 
    } 
    function checklist()
    {
    	var dateBegin = document.getElementById("dateBegin").value;
    	if(dateBegin==""){
    		alert(" 请输入当班日期！");
    		return;
    	}
    	//包括闰年也能教研的正则表达式
    	var ze = /^([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3}).(((0[13578]|1[02]).(0[1-9]|[12][0-9]|3[01]))|((0[469]|11).(0[1-9]|[12][0-9]|30))|(02.(0[1-9]|[1][0-9]|2[0-8])))$/;
    		if(!ze.test(dateBegin)){
    			document.getElementById("dateBegin").value = "";
    			alert("当班日期输入错误，请重新输入！（格式：YYYY.MM.DD）");
    			return;
    		}
    	
    	var identities = document.getElementsByName("identity");
    	var startTimes = "";
    	var endTimes = "";
    	var checked = "";
    	var select_kqlist = "";
    	for(var i=0;i<identities.length;i++){
			if(identities[i].checked){
				checked = identities[i].value;
				if(checked==1){
					startTimes = document.getElementById("start_h").value+":"+document.getElementById("start_m").value+'';
					endTimes = document.getElementById("end_h").value+":"+document.getElementById("end_m").value+'';
					if(document.getElementById("start_h").value>23){
						document.getElementById("start_h").value = "00";
						alert("起始时间格式不正确");
						return;
						}
					if(document.getElementById("end_h").value>23){
						document.getElementById("end_h").value = "23";
						alert("结束时间格式不正确");
						return;
						}
					if(document.getElementById("start_m").value>59){
						document.getElementById("start_m").value = "00";
						alert("起始时间格式不正确");
						return;
					}
					if(document.getElementById("end_m").value>59){
						document.getElementById("end_m").value = "59";
						alert("结束时间格式不正确");
						return;
					}
					if(document.getElementById("start_h").value>document.getElementById("end_h").value){
						alert("起始时间不能晚于结束时间");
						return;
					}
						else if(document.getElementById("start_h").value=document.getElementById("end_h").value){
								if(document.getElementById("start_m").value>document.getElementById("end_m").value){
									alert("起始时间不能晚于结束时间");
									return;	
								}
							}
					var checkType = "1";
					kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&dateBegin="+dateBegin+"&startTimes="+startTimes+"&endTimes="+endTimes+"&checkType="+checkType+"&selectShowBar=1";
				}else{
					select_kqlist =  document.getElementById("select_kqlist").value;
					var checkType = "2";
					kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&dateBegin="+dateBegin+"&select_kqlist="+select_kqlist+"&checkType="+checkType+"&selectShowBar=1";
				}
			}
		}
             kqShiftForm.submit();	
    }
     
	function showOrNOFirstBar(a){
		var dateSelect = document.getElementById("dateSelect");
		var weekSelect = document.getElementById("weekSelect");
		var searchAllData = document.getElementById("searchAllData");
		var chargeChange = document.getElementById("chargeChange");
		
		var display = "none"==a ? "inline" : "none";
		var changeText = "none"==a ? "谁当班?" : "返回查询 ";
		dateSelect.style.display = display;
		weekSelect.style.display = display;
		searchAllData.style.display = display;
		chargeChange.innerHTML = "[" + changeText +" ]";
	}

	function dofocus(){ 
		   var selectInput = document.getElementById("select_name");
		   if(selectInput.value=="请输入姓名、工号或考勤卡号"){ 
		       selectInput.value="";
		   }
		   selectInput.style.color = "black";
		} 
		function doblur(){ 
			var selectInput = document.getElementById("select_name"); 
			if(selectInput.value==''){ 
				selectInput.value="请输入姓名、工号或考勤卡号"; 
				selectInput.style.color = "gray";
			} else if(selectInput.value!="请输入姓名、工号或考勤卡号"){
				selectInput.style.color = "black";
			} 
		}
</script><hrms:themes /> <!-- 7.0css -->
<body onload="">
<html:form action="/kq/team/array/search_array_data">
<input type="hidden" name="a_code" id="a_code" value="<%=code_kind %>"/>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"> 
 <tr> 
  <td>
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
    <tr> 
     <td>
        <table border="0" cellspacing="0"  align="left" cellpadding="0">
            <tr>            
           <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body"> 
              <hrms:menuitem name="file" label="文件" function_id="27071,0C351,270700,0C3500,270701,0C3501,270702,0C3502,270703,0C3503,270720,0C3520,270721,0C3521,270704,0C3504">
                <hrms:menuitem name="mitem1" label="班组设置" icon="/images/write.gif"  url="javascript:groupSet();" command="" function_id="27071,0C351"/> 
                <hrms:menuitem name="mitem2" label="人员排班" icon="/images/add_del.gif" url="" function_id="27070,0C350" enabled="true" visible="true">
                  <hrms:menuitem name="mitem1" label="正常排班" icon="/images/quick_query.gif" url="javascript:normal_shtif();" function_id="270700,0C3500"/>       
                  <hrms:menuitem name="mitem2" label="周期排班" icon="/images/write.gif" url="javascript:cycle_shtif();" command="" function_id="270701,0C3501"/>
                  <hrms:menuitem name="mitem3" label="个人排班" icon="/images/sort.gif" url="javascript:relief_shtif();" function_id="270702,0C3502"/>       
                  <hrms:menuitem name="mitem4" label="不定排班" icon="/images/add_del.gif" url="javascript:unsteady_shtif();" function_id="270703,0C3503"/>    
                </hrms:menuitem>                  
             　　　　         <hrms:menuitem name="mitem3" label="Excel排班" icon="/images/sort.gif" url="" function_id="0C352,27072">  
             　　　　　        <hrms:menuitem name="mitem1" label="Excel模板" icon="/images/quick_query.gif" url="javascript:Excel_stencil();" function_id="270720,0C3520"/>       
                  　　                  <hrms:menuitem name="mitem2" label="导入Excel" icon="/images/write.gif" url="javascript:input_Excel();" command="" function_id="270721,0C3521"/>
                </hrms:menuitem>     
                 <hrms:menuitem name="mitem4" label="输出排班信息" icon="/images/link.gif" url="javascript:excecuteExcel();" function_id="270704,0C3504"/> 
                 <% if (code!=null&&code.equalsIgnoreCase("gp")) {%>
                    <hrms:menuitem name="mitem5" label="同步班组排班" icon="/images/add_del.gif" url="javascript:group_syn_shtif();" command="" function_id="27074,0C354"/>
                 <%} %>
                 <% if (code!=null&&code.equalsIgnoreCase("ep")) {%>
                 	<hrms:menuitem name="mitem5" label="同步个人排班" icon="/images/add_del.gif" url="javascript:gr_syn_shtif();" command="" function_id="27075,0C355"/>
                 <%} %>                                      
             　　              </hrms:menuitem> 
              
              <hrms:menuitem name="rec2" label="浏览" function_id="27076,0C356">    
                 <hrms:menuitem name="mitem1" label="未排班人员" icon="/images/add_del.gif" url="javascript:show_noclass();" function_id="27076,0C356"/>        
               </hrms:menuitem>
           </hrms:menubar>
           </td></tr></table>
        </td>
      </tr>
    </table>
       </td><%--     
    <td align= "left" nowrap>&nbsp;&nbsp;
        <bean:message key="kq.register.daily.menu"/>&nbsp;&nbsp;
         </td>  
     --%><td align="left" nowrap>&nbsp;&nbsp;
         <html:select name="kqShiftForm" property="state" size="1" onchange="show_state(this)">
           <logic:equal name="kqShiftForm" property="state" value="0">
             <option value="0" selected="selected">表格</option> 
           </logic:equal > 
           <logic:notEqual name="kqShiftForm" property="state" value="0">
             <option value="0">表格</option> 
           </logic:notEqual > 
           <logic:equal name="kqShiftForm" property="state" value="1">        
             <option value="1" selected="selected">记录</option>  
           </logic:equal > 
           <logic:notEqual name="kqShiftForm" property="state" value="1"> 
             <option value="1">记录</option>  
           </logic:notEqual > 
        </html:select>  
         
      </td>
      <td align= "left" nowrap>         
          &nbsp;
           <html:select name="kqShiftForm" property="select_pre" size="1">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
           </html:select>
            </td>     
    <td align= "left" nowrap>&nbsp;&nbsp;&nbsp;
    <div  id = "dateSelect" style="display:inline">
        <html:select name="kqShiftForm" property="session_data" size="0" onchange="javascript:change(1);">
        <html:optionsCollection property="sessionlist" value="dataValue" label="dataName"/>
        </html:select>
        <logic:equal name="kqShiftForm" property="state" value="1"> 
	        <html:select name="kqShiftForm" property="week_data"  size="0" onchange="javascript:change(2);">
	        <html:optionsCollection property="weeklist" value="dataValue" label="dataName"/>
	        </html:select> 
        </logic:equal>
        <html:hidden name="kqShiftForm" property="a_code" styleClass="text"/> 
         <html:hidden name="kqShiftForm" property="nbase" styleClass="text"/>
         <html:hidden name="kqShiftForm" property="grnbase" styleClass="text"/>
          </div>
      </td>  
        
    <td align= "left" nowrap>
    	 <div id = "weekSelect" style="display:inline">
          <input type="text" id="select_name" class="inputtext" name="select_name" value="<%= selname %>" onfocus="dofocus(this)" onblur="doblur(this)";  size="29" style="text-align:left;color:gray;height: 23px;margin-left: 9px ">&nbsp;
         </div>  
      </td>    
      <td>
      	<div id = "searchAllData" style="display: inline" nowrap>
           <button class="mybutton" onclick="javascript:selectflag();">查询</button>
           <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqShiftForm"/>   
        </div>
        &nbsp;
      </td>
   	  <td> 
   		<div id="search" style="display: none; width: 100%;float:left;" >
   		
   		<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   		<tr>
   		<td align= "left" nowrap>
   		当班日期 
   		<html:text name="kqShiftForm" property='dateBegin' value="${kqShiftForm.dateBegin}" size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);" styleClass="TEXT4"/>
		</td>
		<td>
		<table border="0" cellspacing="0" cellpadding="0" >
			<tr>
				<td align="left">
					<logic:equal property="identity" name="kqShiftForm" value="1" >	
					<input type="radio" name="identity" value="1"  checked="checked" />
					</logic:equal>
					<logic:equal property="identity" name="kqShiftForm" value="2" >
					<input type="radio" name="identity" value="1"  />
					</logic:equal> 
				</td >
				<td width="40" nowrap style="background-color: #FFFFFF";>
					<div class="m_frameborder inputtext">
						<input type="text" class="m_input" maxlength="2"
							name="intricacy_app_start_time_h" id="start_h"
							value="00"
							onfocus="setFocusObj(this,24);"
							onkeypress="event.returnValue=IsDigit();">
						<font color="#000000"><strong>:</strong> </font>
						<input type="text" class="m_input" maxlength="2"
							name="intricacy_app_start_time_m" id="start_m"
							value="00"
							onfocus="setFocusObj(this,60);"
							onkeypress="event.returnValue=IsDigit();">
					</div>
				</td>
				<td>
					<table border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td>
								<button id="2_up" class="m_arrow"
									onmouseup="IsInputTimeValue();">
									5
								</button>
							</td>
						</tr>
						<tr>
							<td>
								<button id="2_down" class="m_arrow"
									onmouseup="IsInputTimeValue();">
									6
								</button>
							</td>
						</tr>
					</table>
				</td>
				<td align="center">
			~
		</td>
		<td width="70">
			<table border="0" cellspacing="0" align="left"
				cellpadding="0">
				<tr>
					<td width="40" nowrap style="background-color: #FFFFFF";>
						<div class="m_frameborder inputtext">
							<input type="text" class="m_input" maxlength="2"
								name="intricacy_app_end_time_h" id="end_h"
								value="23"
								onfocus="setFocusObj(this,24);"
								onkeypress="event.returnValue=IsDigit();">
							<font color="#000000"><strong>:</strong> </font>
							<input type="text" class="m_input" maxlength="2"
								name="intricacy_app_end_time_m" id="end_m"
								value="59"
								onfocus="setFocusObj(this,60);"
								onkeypress="event.returnValue=IsDigit();">
						</div>
					</td>
					<td>
						<table border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td>
									<button id="2_up" class="m_arrow"
										onmouseup="IsInputTimeValue();">
										5
									</button>
								</td>
							</tr>
							<tr>
								<td>
									<button id="2_down" class="m_arrow"
										onmouseup="IsInputTimeValue();">
										6
									</button>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>

   			</td>
   			<td align="left">
   			 			<logic:equal property="identity" name="kqShiftForm" value="2" >	
						<input type="radio" name="identity" value="2"  checked="checked" />
						</logic:equal>
						<logic:equal property="identity" name="kqShiftForm" value="1" >
						<input type="radio" name="identity" value="2"/>
						</logic:equal>
   			</td>
   			<td align="right">
   				<html:select name="kqShiftForm" property="select_kqlist" size="1" value="${kqShiftForm.nameId}">
                	<html:optionsCollection property="bc_list" value="dataValue" label="dataName" />	        
           		</html:select>
   			</td>
   			<td align="center">
   				<button class="mybutton" onclick="javascript:checklist();">查询当班人</button>
   				<hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqShiftForm"/>   
   			</td>
			</tr>
		</table>
		</td>
		</tr>
		</table>	
    </div>
   	</td>
      <td>
		<logic:equal name="kqShiftForm" property="state" value="1"> 
			<a onclick="searchBar()" href="###" id = "chargeChange">[ 谁当班? ]</a>
        </logic:equal >
      </td> 
      <td nowrap>
      <logic:equal name="kqShiftForm" property="state" value="0"> 
      <logic:notEmpty name="kqShiftForm" property="code_mess">
      &nbsp;当前操作对象：<bean:write name="kqShiftForm" property="code_mess" />
      </logic:notEmpty>
      &nbsp;&nbsp;&nbsp;
       </logic:equal >
     </td>   

    </tr>
   
    </table>
  </td>
 </tr>
 <tr><td height="3px"></td></tr>
 <tr>
  <td width="100%">
      ${kqShiftForm.table_html}
  </td>
  </tr>  
  </table>
        
</html:form>
</body>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="tableRow" height=24>正在排班，请稍候...</td>
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
<script language="javascript">
 MusterInitData();
 hide_nbase_select('select_pre');
 doblur();
 reLoading();
</script>