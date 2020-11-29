<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<%@ page import="com.hjsj.hrms.actionform.kq.machine.DataAnalyseForm" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%
DataAnalyseForm daily=(DataAnalyseForm)session.getAttribute("dataAnalyseForm");
String lockedNumStr=daily.getLockedNum();
int lockedNum=Integer.parseInt(lockedNumStr);
String outSql = SafeCode.encode(daily.getStrSql() + " " + daily.getOrder());
%>
<link href="/kq/kq_tableLocked.css" rel="stylesheet" type="text/css">  

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
<script language="javascript" src="/js/validateDate.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/js/function.js"></script>
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
body {
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 41px;
	height: 22px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 12px;
	padding-top:2px;
}
.m_arrow {
	width: 16px;
	height: 8px;
	font-family: "Webdings";
	font-size: 7px;
	line-height: 2px;
	padding-left: 2px;
	cursor: default;
}
.m_input {
	width: 12px;
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
   var preProcess = "0/0";
   var analysing = false;
   var checkTimes = 0;

   function change()
   {

      //判断日期
      var dd = eval("document.dataAnalyseForm.start_date");
      var ks = dd.value;
      var jsd = eval("document.dataAnalyseForm.end_date");
      var js = jsd.value;
      if(!isDate(ks,"yyyy.MM.dd")){
          alert("起始日期格式错误,请输入正确的日期格式！\nyyyy.MM.dd");
          return false;
   	}
   	if(!isDate(js,"yyyy.MM.dd")){
  		alert("结束日期格式错误,请输入正确的日期格式！\nyyyy.MM.dd");
      	return false;
   	}
      ks=replaceAll(ks,"-",".");
      js=replaceAll(js,"-",".");
      if(ks>js)
      {
        alert(KQ_CHECK_TIME_HINT);
        return false;
      }
		
      //日期相同，判断时间
      if(ks==js)
      {
          var startHH = eval("document.dataAnalyseForm.start_hh");
          var intStartHH = parseInt(startHH.value);
          var endHH = eval("document.dataAnalyseForm.end_hh");
          var intEndHH = parseInt(endHH.value);
          if(intStartHH>intEndHH)
          {
              alert(KQ_CHECK_TIME_HINT);
              return false;
          }

          if(intStartHH==intEndHH)
          {
              var startMM = eval("document.dataAnalyseForm.start_mm");
              var intStartMM = parseInt(startMM.value);
              var endMM = eval("document.dataAnalyseForm.end_mm");
              var intEndMM = parseInt(endMM.value);
              if(intStartMM>intEndMM)
              {
                 alert(KQ_CHECK_TIME_HINT);
                 return false;
              }
          }               
      }       
      dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_search=link&select_flag=1";
      dataAnalyseForm.submit();
   }  
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	   hide_nbase_select('select_pre');
   }
   function delete_card()
   {
     if(confirm(KQ_CARDDATA_DEL_HINT))
     { 
        dataAnalyseForm.action="/kq/machine/search_card_data.do?b_delete=link";
        dataAnalyseForm.submit();
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
   function subq03()
   {
       var start_date=dataAnalyseForm.start_date.value;
       var end_date=dataAnalyseForm.end_date.value;
       var a_code="${dataAnalyseForm.a_code}";
       var nbase="${dataAnalyseForm.nbase}";
       var hashvo=new ParameterSet();
       hashvo.setValue("nbase",nbase);
       hashvo.setValue("a_code",a_code);
       hashvo.setValue("start_date",start_date);
       hashvo.setValue("end_date",end_date);
       hashvo.setValue("tran","0");       
       var request=new Request({method:'post',asynchronous:false,onSuccess:subQ031,functionId:'152110013124'},hashvo);   
   }
   function subQ031(outparamters)
   {
      var flag=outparamters.getValue("flag");
      if(flag=="1")
      {
         var cur_user=outparamters.getValue("cur_user");
         if(confirm("您所需要处理的人员正在被其他用户("+cur_user+")进行处理，是否继续进行确认操作！"))
         {
             enabledMenuitem(true);	 
             var waitInfo=eval("wait");	
	         waitInfo.style.display="block";
	         
	         setDetailInfo("");
             dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_submit=link";        
             dataAnalyseForm.submit();
         }
         return false;
      }else{
         var confirmMess = "";
         var hasTheCollect = outparamters.getValue("hasTheCollect");
         var hasTheCount = outparamters.getValue("hasTheCount");
         confirmMess = "确定将数据分析的结果导入到考勤日明细中?\n\n";
         if("1" == hasTheCollect && "0" == hasTheCount)
        	 confirmMess += "提示：导入后，将对员工日明细进行月汇总。"
         else if ("0" == hasTheCollect && "1" == hasTheCount)
			 confirmMess += "提示：导入后，将对员工日明细进行计算。";
         else if ("1" == hasTheCollect && "1" == hasTheCount)
        	 confirmMess += "提示：导入后，将对员工日明细进行计算和月汇总。";
         if(confirm(confirmMess))
         {
           enabledMenuitem(true);	 
           var waitInfo=eval("wait");	
	       waitInfo.style.display="block";
	       setDetailInfo("");
           dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_submit=link";        
           dataAnalyseForm.submit();
         }
      } 
       
   }
   function deleteQ()
   {
      dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_delete=link";
      dataAnalyseForm.submit();
   } 
   function analyseData()
   {
       var start_date=dataAnalyseForm.start_date.value;
       var end_date=dataAnalyseForm.end_date.value;
       var a_code="${dataAnalyseForm.a_code}";
       var nbase="${dataAnalyseForm.nbase}";
       var hashvo=new ParameterSet();
       hashvo.setValue("nbase",nbase);
       hashvo.setValue("a_code",a_code);
       hashvo.setValue("start_date",start_date);
       hashvo.setValue("end_date",end_date);
       hashvo.setValue("tran","0");       
       var request=new Request({method:'post',asynchronous:false,onSuccess:analyseData_1,functionId:'152110013124'},hashvo);   
   }
   function analyseData_1(outparamters)
   {
	  var flag=outparamters.getValue("flag");
      if(flag=="1")
      {
         var cur_user=outparamters.getValue("cur_user");
         alert("您所处理的人员正在被其他用户("+cur_user+")进行处理，请稍候操作！");
         return false;
      }
      var start_date=dataAnalyseForm.start_date.value;
      var end_date=dataAnalyseForm.end_date.value;
      if(start_date==""||end_date=="")
      {
         alert("开始时间或结束时间不能为空！");
         return false;
      }else
      {
         start_date=start_date.replace('-', ".");
         start_date=start_date.replace('-', ".");
         end_date=end_date.replace('-', ".");
         end_date=end_date.replace('-', ".");
      }      
		var target_url;      
		target_url="/kq/machine/analyse/analyse_result.do?br_selectdate=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,"app", 
		"dialogWidth:500px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo){
			dataAnalyseForm.start_date.value = return_vo.start_date;
			dataAnalyseForm.end_date.value = return_vo.end_date;
			var waitInfo=eval("wait");	
	    	waitInfo.style.display="block";
	    	
	    	enabledMenuitem(true);	 
	    	preProcess = "0/0"; 
	    	analysing = true;    
        	//dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_analyse=link";        
        	//dataAnalyseForm.submit();

        	var start_date=dataAnalyseForm.start_date.value;
	       var end_date=dataAnalyseForm.end_date.value;
	       var a_code="${dataAnalyseForm.a_code}";
	       var nbase="${dataAnalyseForm.nbase}";
	       var hashvo=new ParameterSet();
	       hashvo.setValue("nbase",nbase);
	       hashvo.setValue("a_code",a_code);
	       hashvo.setValue("start_date",start_date);
	       hashvo.setValue("end_date",end_date);
	       hashvo.setValue("isasync","1"); 
           var request=new Request({method:'post',asynchronous:false,functionId:'152110013115'},hashvo);   
        	

		}
     // if(confirm("确定要进行考勤数据处理吗？"))
      //{
      //  var mess="您要数据处理的业务时间日期为"+start_date+"至"+end_date+",请确认!";
     //  if(confirm(mess))
     //   {
           
      //  }        
     // }
   }
   function enabledMenuitem(_boolean)
   {
      var  bt=document.getElementById("br_query"); 
      if(bt)
          bt.disabled=_boolean;
      var menuitem=getMenuItem("mitem1");      
  	  if(menuitem)
  			menuitem.enabled=!_boolean;
	   menuitem=getMenuItem("mitem2");
  	  if(menuitem)
  			menuitem.enabled=!_boolean;	
  			var menuitem=getMenuItem("mitem3");      
  	  if(menuitem)
  			menuitem.enabled=!_boolean;
	   menuitem=getMenuItem("mitem4");
  	  if(menuitem)
  			menuitem.enabled=!_boolean;	
   }
   function specificAnalyseData()
   {
      var start_date=dataAnalyseForm.start_date.value;
      var end_date=dataAnalyseForm.end_date.value;
      var nbase=dataAnalyseForm.select_pre.value;
      var str="";
      for(var i=0;i<document.dataAnalyseForm.elements.length;i++)
	  {
		if(document.dataAnalyseForm.elements[i].type=="checkbox"&&document.dataAnalyseForm.elements[i].checked==true)
		{
				if(document.dataAnalyseForm.elements[i].name=="aa")
					continue;
				str+=document.dataAnalyseForm.elements[i+1].value+"/";
			
		}
	  }
       var hashvo=new ParameterSet();
       hashvo.setValue("nbase",nbase);
       hashvo.setValue("specdata",str);
       hashvo.setValue("start_date",start_date);
       hashvo.setValue("end_date",end_date);
       hashvo.setValue("tran","1");
       var request=new Request({method:'post',asynchronous:false,onSuccess:specificAnalyseData_1,functionId:'152110013124'},hashvo);   
   }
   function specificAnalyseData_1(outparamters)
   {
      var flag=outparamters.getValue("flag");
      if(flag=="1")
      {
         var cur_user=outparamters.getValue("cur_user");
         alert("您所处理的人员正在被其他用户("+cur_user+")进行处理，请稍后操作！");
         return false;
      }
      var start_date=dataAnalyseForm.start_date.value;
      var end_date=dataAnalyseForm.end_date.value;
      if(start_date==""||end_date=="")
      {
         alert("开始时间或结束时间不能为空！");
         return false;
      }else
      {
         start_date=start_date.replace('-', ".");
         start_date=start_date.replace('-', ".");
         end_date=end_date.replace('-', ".");
         end_date=end_date.replace('-', ".");
      }      
      var str="";
      for(var i=0;i<document.dataAnalyseForm.elements.length;i++)
	  {
		if(document.dataAnalyseForm.elements[i].type=="checkbox"&&document.dataAnalyseForm.elements[i].checked==true)
		{
				if(document.dataAnalyseForm.elements[i].name=="aa")
					continue;
				str+=document.dataAnalyseForm.elements[i+1].value+"/";
			
		}
	  }
      if(str.length==0)
	  {
		alert("请选择分析结果中的记录！");
		var obj=document.getElementsByName("specdata"); 
        obj[0].value="";
		return;
	  }
		var target_url="/kq/machine/analyse/analyse_result.do?br_selectdate=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
		var return_vo= window.showModalDialog(iframe_url,"app", 
			"dialogWidth:500px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no");
		if(return_vo){
			dataAnalyseForm.start_date.value = return_vo.start_date;
			dataAnalyseForm.end_date.value = return_vo.end_date;
			var obj=document.getElementsByName("specdata"); 
			obj[0].value=str;           
			var waitInfo=eval("wait");	
			waitInfo.style.display="block";	   
			setDetailInfo("");  
			dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_specanalyse=link";        
			dataAnalyseForm.submit();
		}else{
			return false;
		} 
	}
   function checkData(obj,nbase,a0100,q03z0)
   {
      var hashvo=new ParameterSet();
      hashvo.setValue("nbase",nbase);
      hashvo.setValue("a0100",a0100);
      hashvo.setValue("q03z0",q03z0);
      hashvo.setValue("type","result");
      hashvo.setValue("table","${dataAnalyseForm.analyseTempTab}");  
      if(obj.checked==true)
      {
          hashvo.setValue("flag","1");         
      }else
      {
         hashvo.setValue("flag","0");         
      }      
      var request=new Request({method:'post',asynchronous:false,functionId:'152110013116'},hashvo);   
   }
   function views(view,checked)
   {
      dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_search=link&oneSort="+view+"&checked="+checked;
      dataAnalyseForm.submit();
   } 
   function outdata()
   {
      var target_url;
      if (!isIE6())
          var winFeatures = "dialogWidth:440px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:no"; 
      else
    	  var winFeatures = "dialogWidth:440px; dialogHeight:420px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:no";
      
      target_url="/kq/machine/analyse/analyse_result.do?b_outfield=link";
      var return_vo= window.showModalDialog(target_url,1, winFeatures);
      if(!return_vo)
	    return false;
	  if(return_vo.flag=="true")
	  {
	      var hashvo=new ParameterSet();  	      
	      var mes=new Array(); 
	      for(var i=0;i<return_vo.fields.length;i++)
	      {
	        mes[i]=return_vo.fields[i];	        
	      }
	      hashvo.setValue("fields",mes); 
	      //hashvo.setValue("select_pre",$F('select_pre'));	      
	      //hashvo.setValue("start_date",$F('start_date'));
	      //hashvo.setValue("start_hh",$F('start_hh'));
	      //hashvo.setValue("select_type",$F('select_type'));
	      //hashvo.setValue("start_mm",$F('start_mm'));	      
	      //hashvo.setValue("end_date",$F('end_date'));
	      //hashvo.setValue("end_hh",$F('end_hh'));
	      //hashvo.setValue("end_mm",$F('end_mm'));
	      //hashvo.setValue("select_name",$F('select_name'));	      
	      //hashvo.setValue("a_code","${dataAnalyseForm.a_code}");
	      //hashvo.setValue("nbase","${dataAnalyseForm.nbase}");
	      hashvo.setValue("fAnalyseTempTab","${dataAnalyseForm.analyseTempTab}");
	      //hashvo.setValue("view","${dataAnalyseForm.view}");
	      hashvo.setValue("data","<%=outSql%>");     
	      var request=new Request({method:'post',onSuccess:showExcel,functionId:'152110013120'},hashvo);
	  }	   
   }
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true";
   }
    var checkflag = "false";
  function selAll()
   {
      var len=document.dataAnalyseForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.dataAnalyseForm.elements[i].type=="checkbox")
          {
             
            document.dataAnalyseForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
    }else
    {
        for (i=0;i<len;i++)
        {
          if (document.dataAnalyseForm.elements[i].type=="checkbox")
          {
             
            document.dataAnalyseForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }      
  }
  function patch()
  {
    var start_date = "${dataAnalyseForm.start_date}";
    var end_date = "${dataAnalyseForm.end_date}"
  	var str="";
	for(var i=0;i<document.dataAnalyseForm.elements.length;i++)
	{
		if(document.dataAnalyseForm.elements[i].type=="checkbox")
		{
			if(document.dataAnalyseForm.elements[i].checked==true)
			{
				if(document.dataAnalyseForm.elements[i].name=="aa")
					continue;
					//str+=document.dataAnalyseForm.elements[i].value+"/";
						str+=document.dataAnalyseForm.elements[i+1].value+"/";
			}
		}
	}
    if(str.length==0)
	{
		alert("请选择人员！");
		return;
	}else
	{
		//var target_url;
   		//var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
    	//target_url="/kq/machine/analyse/analyse_patch.do?b_patch=link`str="+str+"`start_date="+start_date+"`end_date="+end_date;
        //var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
        //var return_vo= window.showModalDialog(iframe_url,1, 
        //	"dialogWidth:606px; dialogHeight:575px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        var obj=document.getElementsByName("specdata"); 
		obj[0].value=str; 
        dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_patch=link&start_date="+start_date+"&end_date="+end_date;
        dataAnalyseForm.submit();
	}
  } 
</script><hrms:themes /> <!-- 7.0css -->
 <% int i=0;%>
<html:form action="/kq/machine/analyse/analyse_result">
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" style="margin-top:5px;">
 <tr>
  <td align="left"  nowrap> 
     <table width="100%" border="0" cellspacing="0" style="padding-bottom: 5px;" align="left" cellpadding="0">
       <tr>
          <td width="150" nowrap height="26">
          <table border="0" cellspacing="0"  align="left" cellpadding="0" style='position:relative'>
          <tr>
          <td>
           <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
           <hrms:menuitem name="file" label="文件" function_id="">
             <hrms:menuitem name="mitem1" label="数据处理" icon="/images/write.gif"  url="javascript:analyseData();" command="" function_id="2706202,0C38002"/> 
             <hrms:menuitem name="mitem2" label="个别数据处理" icon="/images/write.gif"  url="javascript:specificAnalyseData();" command="" function_id="2706203,0C38003"/>
             <hrms:menuitem name="mitem3" label="确认" icon="/images/add_del.gif" url="javascript:subq03();;" function_id="2706202,0C38002,2706203,0C38003"/>
             <hrms:menuitem name="mitem4" label="数据导出" icon="/images/write.gif" url="javascript:outdata();" function_id=""/>
            </hrms:menuitem>    
            <hrms:menuitem name="view" label="分类显示" function_id="">
             <logic:equal name="dataAnalyseForm" property="view" value="all">
	             <hrms:menuitem name="mitem201" label="全部" icon=""  checked="true" url="javascript:views('all','1');" command="" function_id=""/> 
	             <hrms:menuitem name="mitem204" label="异常" icon="" url="javascript:views('abnor','0');" function_id=""/>
	             <hrms:menuitem name="mitem202" label="正常" icon="" url="javascript:views('zc','0');" function_id=""/>
	             <hrms:menuitem name="mitem203" label="休息" icon="" url="javascript:views('xx','0');" function_id=""/>
	             <hrms:menuitem name="mitem205" label="旷工" icon="" url="javascript:views('kg','0');" function_id=""/>
	             <hrms:menuitem name="mitem206" label="迟到" icon="" url="javascript:views('cd','0');" function_id=""/>
	             <hrms:menuitem name="mitem207" label="早退" icon="" url="javascript:views('zt','0');" function_id=""/>
	             <hrms:menuitem name="mitem208" label="离岗" icon="" url="javascript:views('lg','0');" function_id=""/>
	             <hrms:menuitem name="mitem209" label="请假" icon="" url="javascript:views('qj','0');" function_id=""/>
	             <hrms:menuitem name="mitem210" label="公出" icon="" url="javascript:views('gc','0');" function_id=""/>
	             <hrms:menuitem name="mitem211" label="加班" icon="" url="javascript:views('jb','0');" function_id=""/>
             </logic:equal>
             <logic:notEqual name="dataAnalyseForm" property="view" value="all">
	             <hrms:menuitem name="mitem201" label="全部" icon="" url="javascript:views('all','0');" function_id=""/>
	             <logic:match value=",abnor," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem204" label="异常" icon="" url="javascript:views('abnor','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",abnor," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem204" label="异常" icon="" url="javascript:views('abnor','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",zc," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem202" label="正常" icon="" url="javascript:views('zc','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",zc," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem202" label="正常" icon="" url="javascript:views('zc','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",xx," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem203" label="休息" icon="" url="javascript:views('xx','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",xx," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem203" label="休息" icon="" url="javascript:views('xx','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",kg," name="dataAnalyseForm" property="view">
	             	 <hrms:menuitem name="mitem205" label="旷工" icon="" url="javascript:views('kg','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",kg," name="dataAnalyseForm" property="view">
	             	 <hrms:menuitem name="mitem205" label="旷工" icon="" url="javascript:views('kg','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",cd," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem206" label="迟到" icon="" url="javascript:views('cd','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",cd," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem206" label="迟到" icon="" url="javascript:views('cd','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",zt," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem207" label="早退" icon="" url="javascript:views('zt','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",zt," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem207" label="早退" icon="" url="javascript:views('zt','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",lg," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem208" label="离岗" icon="" url="javascript:views('lg','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",lg," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem208" label="离岗" icon="" url="javascript:views('lg','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",qj," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem209" label="请假" icon="" url="javascript:views('qj','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",qj," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem209" label="请假" icon="" url="javascript:views('qj','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",gc," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem210" label="公出" icon="" url="javascript:views('gc','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",gc," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem210" label="公出" icon="" url="javascript:views('gc','0');" function_id=""/>
	             </logic:notMatch>
	             <logic:match value=",jb," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem211" label="加班" icon="" url="javascript:views('jb','1');" function_id="" checked="true"/>
	             </logic:match>
	             <logic:notMatch value=",jb," name="dataAnalyseForm" property="view">
		             <hrms:menuitem name="mitem211" label="加班" icon="" url="javascript:views('jb','0');" function_id=""/>
	             </logic:notMatch>
             </logic:notEqual>

            </hrms:menuitem>
            <hrms:menuitem name="file" label="编辑" function_id="2706201,0C38001">
             <hrms:menuitem name="mitem31" label="刷卡补签" icon="/images/write.gif"  url="javascript:patch();" command="" function_id=""/> 
            </hrms:menuitem>   
            </hrms:menubar>
            </td>
            </tr>
           </table>           
          </td> 
          <td align="center" width="5" nowrap>          
             <html:hidden name="dataAnalyseForm" property="analyseTempTab" styleClass="text"/>
             <html:hidden name="dataAnalyseForm" property="specdata" styleClass="text"/>
             
          </td>         
          <td nowrap  align="center">    &nbsp;&nbsp;          
           <html:select name="dataAnalyseForm" property="select_pre" styleId="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
           </html:select>   
            &nbsp;&nbsp;
          </td>
          <td align="center" width="40" nowrap>
            范围&nbsp;<input type="hidden" name="dateValue" id="dateValue">
          </td>
          <td align="left" width="170" nowrap> 
           <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		   <td>		   
		   <input type="text" name="start_date" value="${dataAnalyseForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this); restoreDateValue(this,kq_duration)" extra="editor" dataType="simpledate">
		   </td>
		   <td width="40" nowrap style="background-color:#FFFFFF";> 
		     <div class="m_frameborder inputtext">
		      <input type="text" class="m_input" maxlength="2" name="start_hh" value="${dataAnalyseForm.start_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="start_mm" value="${dataAnalyseForm.start_mm}" onfocus="setFocusObj(this,60);">
		     </div>
		   </td>
		   <td>
		     <table border="0" cellspacing="2" cellpadding="0">
		       <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		         <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		     </table>
		 </td>
	      </tr>
           </table>
          </td>
          <td align= "middle" nowrap>
             -&nbsp;
          </td>
          <td align= "left" width="170" nowrap>             
	    
             <table border="0" cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		     <td>		   
		   <input type="text" name="end_date"  value="${dataAnalyseForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" onclick='saveCurrDateValue(this);getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);restoreDateValue(this,kq_duration)" extra="editor" dataType="simpledate">
		   </td>
		   <td width="40" nowrap style="background-color:#FFFFFF"> 
		     <div class="m_frameborder inputtext">
		      <input type="text" class="m_input" maxlength="2" name="end_hh" value="${dataAnalyseForm.end_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="end_mm" value="${dataAnalyseForm.end_mm}" onfocus="setFocusObj(this,60);">
		     </div>
		   </td>
		   <td>
		     <table border="0" cellspacing="2" cellpadding="0">
		       <tr><td><button id="0_up" class="m_arrow" onmouseup="IsInputTimeValue();">5</button></td></tr>
		         <tr><td><button id="0_down" class="m_arrow" onmouseup="IsInputTimeValue();">6</button></td></tr>
		     </table>
		 </td>
	      </tr>
           </table>               
         </td>
         <td align="center" width="100px" nowrap="nowrap">
                 按&nbsp;<html:select name="dataAnalyseForm" property="select_type"  size="1">
            	<html:option value="0"><bean:message key="label.title.name" /></html:option>                      
                <html:option value="1">工号</html:option>
                <html:option value="2">考勤卡号</html:option>
                </html:select>
          </td>
         <td align= "left" width="100" nowrap>            
         <input type="text" name="select_name" value="" class="inputtext" style="width:100px;font-size:10pt;text-align:left">	
  </td>
  <td align= "left" nowrap> &nbsp;
         <input type="button" name="br_query" value='查询' class="mybutton" onclick="change();">          
  </td>
        </tr>
       </table> 
  </td>  
 </tr>
 <tr>
  <td width="100%">
 <%
    int lock=0;
 %>
 <script language='javascript' >
		document.write("<div id=\"tbl-container\"  style='position:absolute;left:5;height:"+(document.body.clientHeight-120)+";width:100%'  >");
 </script> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF" >
      <thead>
         <tr>
            <td align="center" class="TableRow" nowrap>
				&nbsp;<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
            </td>  
            <logic:iterate id="element"    name="dataAnalyseForm"  property="fieldList" indexId="index"> 
              <logic:equal name="element" property="visible" value="true">
               <logic:equal name="element" property="itemtype" value="A">
                    <%if(i<lockedNum) {%>
                        <td align="center" class="TableRow"  style="border-top:none;" nowrap>
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}else{ %>
                        <td align="center" class="TableRow"  style="border-top:none;" nowrap>
                        <bean:write  name="element" property="itemdesc"/>&nbsp; 
                        </td>
                   <%}
                   i++;
                   %>
                 </logic:equal>
                 <logic:notEqual name="element" property="itemtype" value="A">
                   <td align="center" class="TableRow"  style="border-top:none;" nowrap>
                     <bean:write  name="element" property="itemdesc"/>&nbsp; 
                   </td>
                 </logic:notEqual>
              </logic:equal>
           </logic:iterate>                               	        
         </tr>         
      </thead> 
       <%i=0; %>
      <hrms:paginationdb id="element" name="dataAnalyseForm" sql_str="dataAnalyseForm.strSql" 
      table="" where_str="" columns="dataAnalyseForm.column" order_by="dataAnalyseForm.order" 
      pagerows="${dataAnalyseForm.pagerows}" distinct=""
       page_id="pagination" keys="nbase,a0100,q03z0">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++;  
          int  inNum=0;lock=0;
          LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");   
          String nbase=(String)abean.get("nbase");  
          String a0100=(String)abean.get("a0100");
          String q03z0=(String)abean.get("q03z0"); 
          pageContext.setAttribute("nbase",nbase);
          pageContext.setAttribute("a0100",a0100);
          pageContext.setAttribute("q03z0",q03z0);
          %>  
          <td align="center" class="RecordRow" nowrap> 
              &nbsp;<html:checkbox name="element" property="flag" value="1" onclick="checkData(this,'${nbase}','${a0100}','${q03z0}');"/>&nbsp;
              
              <input type="hidden" name="pageSize" value="'${nbase}','${a0100}','${q03z0}'" Id="dbsign"/>
           </td>
           <logic:iterate id="info" name="dataAnalyseForm"  property="fieldList">  
             <logic:equal name="info" property="visible" value="true">
                 <logic:equal name="info" property="itemtype" value="A">
                   <%if(lock<lockedNum) {%>
                         <td align="left" class="RecordRow" nowrap>
                       <%}else{ %>
                          <td align="left" class="RecordRow" nowrap>
                       <% }
                       lock++;
                   %>
                     <logic:notEqual name="info" property="codesetid" value="0">                          
                          <logic:equal name="info" property="codesetid" value="UM">
                          <hrms:codetoname codeid="UM" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${dataAnalyseForm.uplevel}"/>  	      
          	                 &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
                          </logic:equal>
                          <logic:notEqual name="info" property="codesetid" value="UM">
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp; 
                           </logic:notEqual>                             
                      </logic:notEqual>
                      <logic:equal name="info" property="codesetid" value="0">                         
                             <logic:notEqual name="info" property="itemid" value="inout_flag">  
                               &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                              </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="inout_flag">  
                               <logic:equal name="element" property="inout_flag" value="-1">
                                   出
                               </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="0">
                                   不限
                                </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="1">
                                   进
                               </logic:equal>
                              </logic:equal>                            
                      </logic:equal>
                     </td>
                 </logic:equal>
                 <logic:equal name="info" property="itemtype" value="D">
                       <%if(lock<lockedNum) {%>
                         <td align="left" class="RecordRow" nowrap>
                       <%}else{ %>
                          <td align="left" class="RecordRow"  nowrap>
                       <% }
                       lock++;
                       %>
                        <bean:write name="element" property="${info.itemid}" filter="false"/>&nbsp;   
                       </td>
                 </logic:equal>  
                 <logic:equal name="info" property="itemtype" value="N">
                   <td align="center" class="RecordRow" nowrap>&nbsp;   
                      <logic:notEqual name="element" property="${info.itemid}" value="0"> 
                        <bean:write name="element" property="${info.itemid}"/>
                      </logic:notEqual>
                    </td> 
                 </logic:equal>
             </logic:equal>
           </logic:iterate>             
          </tr>
        </hrms:paginationdb>
       <script language='javascript'>
	    document.write("</div>");
      </script>
    </table>
  </td>
 </tr> 
  <tr>
   <td>
   <script language='javascript' >
		document.write("<div id='page'  style='position:absolute;left:5;top:"+(document.body.clientHeight-74)+";width:100%;z-index:10;'>");
	</script>
     <table  width="100%" class="RecordRowP"  align="center" style="margin-bottom: 5px;">      
       <tr>
		    <td valign="bottom"   width="60%" class="tdFontcolor">
					<hrms:paginationtag name="dataAnalyseForm" pagerows="${dataAnalyseForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
			</td>
	        <td valign="bottom"  align="right" width="40%" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="dataAnalyseForm" property="pagination" nameId="dataAnalyseForm" scope="page">
				</hrms:paginationdblink>				
			</td>
		</tr>
       <tr>
     </table>
          <logic:equal value="dxt" name="dataAnalyseForm" property="returnvalue">  
           <hrms:tipwizardbutton flag="workrest" target="il_body" formname="dataAnalyseForm"/>
          </logic:equal>
     <script language='javascript' >
	    document.write("</div>");
      </script>
   </td>
 </tr>
</table>  
<table width="100%">
</table>
</html:form>
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
             <td class="td_style common_background_color" height=24>
                <span id="detail">正在进行数据处理，请稍候...</span>                
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
<script language="javascript">
 MusterInitData();

 function setDetailInfo(info) {
	 var detailInfo = eval("detail");
	 detailInfo.innerHTML = "正在进行数据处理" + info + "，请稍候...";

	 if (info == "")
		 analysing = false;
 }
 
 function checkAnalalyseProgress() {

	 var waitInfo=eval("wait");    
     if (waitInfo.style.display == "block" && analysing)
     {
         checkTimes++;

         //最初一段时间是在准备数据，记录数不准确，暂时延后1分钟再取数
         if (checkTimes > 1) {
			 var hashvo=new ParameterSet();
		     hashvo.setValue("tran","2");
		     var request=new Request({method:'post',asynchronous:false,onSuccess:analyseProgress,functionId:'152110013124'},hashvo);
         }
     }
     
	 setTimeout("checkAnalalyseProgress()",10000);
 }

 function analyseProgress(outparamters) {	 
	 var waitInfo=eval("wait");    
	 if (waitInfo.style.display == "block") {
		 
		 var flag = outparamters.getValue("flag");
		 if ("begin" == flag)
			 return;

		 if ("finished" == flag) {	      
			 change();
			 return;
		 }

		 if ("error" == flag) {
			 alert("数据处理发生错误：" + outparamters.getValue("error_info"));
			 change();
			 return;
		 }

		 setDetailInfo("【已处理数/总数 ：" + flag + "】");		 
		 
		 preProcess = flag;
	 }		 
 }

 setTimeout("checkAnalalyseProgress()",10000);
</script>