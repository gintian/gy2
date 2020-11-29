<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
<style>
.RecordRow_self {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}

.RecordRow_self_l{
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_r {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #CCCCCC 0pt dotted; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_t {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #CCCCCC 0pt dotted;
	
}
.RecordRow_self_b {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #CCCCCC 0pt dotted; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}
.RecordRow_self_two {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #CCCCCC 0pt dotted; 
	BORDER-TOP: #94B6E6 1pt solid;
	
}

.ListTable_self {

    BACKGROUND-COLOR: #F7FAFF;
    BORDER-BOTTOM: medium none; 
    BORDER-COLLAPSE: collapse; 
    BORDER-LEFT: medium none; 
    BORDER-RIGHT: medium none; 
    BORDER-TOP: medium none; 
    
}
.RecordRow_self_t_l {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #CCCCCC 0pt dotted; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	
}
.RecordRow_self_t_r {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	
}

.TEXT {
	BACKGROUND-COLOR:transparent;
	
	BORDER-BOTTOM: medium none; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
.BackText {
	background-color: #FFFFFF;
}
</style>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/register/print_kqreport">
<script language=JavaScript> 
function change()
   {
      printKqInfoForm.action="/kq/register/historical/print_kqreport.do?b_view=link&report_id=${printKqInfoForm.report_id}&userbase=${printKqInfoForm.userbase}&code=${printKqInfoForm.code}&coursedate=${printKqInfoForm.coursedate}&kind=${printKqInfoForm.kind}";
      printKqInfoForm.submit();
   }
   function excecutePDF()
   {
    var sjelement = document.getElementById("sjelement"); //改为 制作日期
    if(sjelement!=null)
    {
    	sjelement=sjelement.value;
    	if(sjelement=="")
   		{
   			alert("制作日期不能为空!");
   			return;
   		}
   		if (!formatTime(sjelement))
 		{
    		alert("制作日期格式错误！");
    		return false;
 		}
    }else
    {
    	sjelement="#null";
    }
   	
   	var timeqd = document.getElementById("timeqd"); //改为 时间
   	if(timeqd!=null)
   	{
   		timeqd=timeqd.value;
   		if(timeqd=="")
   	    {
   		  alert("时间不能为空!");
   		  return false;
   	    }
   	}else
   	{
   		timeqd="#null";
   	}
	var hashvo=new ParameterSet();	
	hashvo.setValue("userbase","${printKqInfoForm.userbase}");	
	hashvo.setValue("code","${printKqInfoForm.code}");
	hashvo.setValue("kind","${printKqInfoForm.kind}");
	hashvo.setValue("coursedate","${printKqInfoForm.coursedate}");	
	hashvo.setValue("report_id","${printKqInfoForm.report_id}");
	hashvo.setValue("self_flag","${printKqInfoForm.self_flag}");
	hashvo.setValue("whereIN","${printKqInfoForm.wherestr_s}");
	hashvo.setValue("sjelement",sjelement);	
	hashvo.setValue("timeqd",timeqd);
	var dbty ="${printKqInfoForm.dbtype}";
	hashvo.setValue("dbty",dbty);
	var In_paramters="exce=PDF";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'15301110244'},hashvo);
	
}

function exportExcel()
   {
    var sjelement = document.getElementById("sjelement"); //改为 制作日期
    if(sjelement!=null)
    {
    	sjelement=sjelement.value;
    	if(sjelement=="")
   		{
   			alert("制作日期不能为空!");
   			return;
   		}
   		if (!formatTime(sjelement))
 		{
    		alert("制作日期格式错误！");
    		return false;
 		}
    }else
    {
    	sjelement="#null";
    }
   	
   	var timeqd = document.getElementById("timeqd"); //改为 时间
   	if(timeqd!=null)
   	{
   		timeqd=timeqd.value;
   		if(timeqd=="")
   	    {
   		  alert("时间不能为空!");
   		  return false;
   	    }
   	}else
   	{
   		timeqd="#null";
   	}
	var hashvo=new ParameterSet();	
	hashvo.setValue("userbase","${printKqInfoForm.userbase}");	
	hashvo.setValue("code","${printKqInfoForm.code}");
	hashvo.setValue("kind","${printKqInfoForm.kind}");
	hashvo.setValue("coursedate","${printKqInfoForm.coursedate}");	
	hashvo.setValue("report_id","${printKqInfoForm.report_id}");
	hashvo.setValue("self_flag","${printKqInfoForm.self_flag}");
	hashvo.setValue("whereIN","${printKqInfoForm.wherestr_s}");
	hashvo.setValue("sjelement",sjelement);	
	hashvo.setValue("timeqd",timeqd);
	var dbty ="${printKqInfoForm.dbtype}";
	hashvo.setValue("dbty",dbty);	
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'15301110243'},hashvo);
	
}

function showFieldList(outparamters)
{
	var url=outparamters.getValue("url");	
	window.location.target = "mil_body";
	window.location.href = "/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true";
}

function formatTime(str)
{

  var   r   =   str.match(/^(\d{1,4})(.|\/)(\d{1,2})\2(\d{1,2})$/);     
  if(r==null) return   false;     
  var  d=  new  Date(r[1],   r[3]-1,   r[4]);     
  return  (d.getFullYear()==r[1]&&(d.getMonth()+1)==r[3]&&d.getDate()==r[4]);   
}
function excecuteEXCEL()
{
	var hashvo=new ParameterSet();
	hashvo.setValue("userbase","${printKqInfoForm.userbase}");	
	hashvo.setValue("code","${printKqInfoForm.code}");
	hashvo.setValue("kind","${printKqInfoForm.kind}");
	hashvo.setValue("coursedate","${printKqInfoForm.coursedate}");	
	hashvo.setValue("report_id","${printKqInfoForm.report_id}");
	hashvo.setValue("self_flag","${printKqInfoForm.self_flag}");
	hashvo.setValue("whereIN","${printKqInfoForm.wherestr_s}");
	var In_paramters="exce=EXCEL";
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showFieldList,functionId:'15301110083'},hashvo);
		
}

function pagepar()
{
     //printKqInfoForm.action="/kq/register/select_kqreportpar.do?b_par=link&kqflag=5&report_id=${printKqInfoForm.report_id}&userbase=${printKqInfoForm.userbase}&code=${printKqInfoForm.code}&coursedate=${printKqInfoForm.coursedate}&kind=${printKqInfoForm.kind}";
     //printKqInfoForm.submit();
}
function go_back(flag)
{
   printKqInfoForm.action="/kq/register/historical/dailybrowse.do?b_search=link&coursedate=${printKqInfoForm.coursedate}&action=dailybrowsedata.do&target=mil_body&a_inforkind=1&privtype=kq";
   printKqInfoForm.target="il_body";
   printKqInfoForm.submit();
}

function openwin(a0110,userbase,start_date,end_date)
{
	printKqInfoForm.action="/kq/register/historical/single_register.do?b_browse=link&rflag=05&code=&kind=2&userbase="+userbase+"&start_date="+start_date+"&end_date="+end_date+"&A0100="+a0110+"&marker=1";
	printKqInfoForm.submit();
}
//考勤薄
function openwintable(a0110,username,userbase,start_date,end_date)
{
	printKqInfoForm.action="/kq/register/historical/print_kqreport_unittable.do?b_viewtable=link&code=&kind=2&userbase="+userbase+"&start_date="+start_date+"&end_date="+end_date+"&A0100="+a0110+"&username="+$URL.encode(getEncodeStr(username))+"&fileunit=1";
	printKqInfoForm.submit();
}
 </script> 
      ${printKqInfoForm.tableHtml}&nbsp;&nbsp;
      ${printKqInfoForm.turnTableHtml}
      
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