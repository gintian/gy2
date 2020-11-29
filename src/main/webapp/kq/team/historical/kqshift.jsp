<%@ page contentType="text/html; charset=UTF-8" language="java"%>
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
<script language="javascript" src="/kq/kq.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
	height:540px;
	width:1350px;
}
-->
</style>
<script language="javascript">
   function editClass(nbase,a_code,days)
   {}
   function change()
   {
      kqShiftForm.action="/kq/team/historical/search_array_data.do?b_search=link&a_code=${kqShiftForm.a_code}";
      kqShiftForm.submit();
   }
         function history(obj)
   {
   	if(obj.value=="0"){
   		  obj.options(0).selected = true;
	      kqShiftForm.action="/kq/team/history/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&privtype=kq";
	      kqShiftForm.target="il_body";
	      kqShiftForm.submit();
      }
   } 
  function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("a_code","${kqShiftForm.a_code}");
	hashvo.setValue("nbase","${kqShiftForm.nbase}");
	var session_data="${kqShiftForm.session_y}"+"-"+"${kqShiftForm.session_m}";	
	hashvo.setValue("session_data",session_data);	
	hashvo.setValue("state","${kqShiftForm.state}");
	hashvo.setValue("select_name","${kqShiftForm.select_name}");
	hashvo.setValue("select_pre","${kqShiftForm.select_pre}");
	hashvo.setValue("select_flag","${kqShiftForm.select_flag}");
	hashvo.setValue("finsh","1");
	var his = document.getElementById("his").value;
	hashvo.setValue("his",his);
	hashvo.setValue("decrypt_flag","0");
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15221000004'},hashvo);
   }	
   function showExcel(outparamters)
   {
	   var url=outparamters.getValue("excelfile");	
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
       kqShiftForm.action="/kq/team/historical/search_array_data.do?b_search=link&select_flag=0";
       kqShiftForm.submit();
   }
   function show_state(s)
   {
     var state = s.options[s.selectedIndex].value;     
     kqShiftForm.action="/kq/team/historical/search_array_data.do?b_search=link&a_code=${kqShiftForm.a_code}&state="+state;
     kqShiftForm.submit();
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   
   
   function Excel_stencil()
   {
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/team/array/excel_shift_data.do?b_select=link&a_code=${kqShiftForm.a_code}&nbase=${kqShiftForm.nbase}&session_data=${kqShiftForm.session_data}";
      var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
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
      if(url!="")
      {
        var win=open("/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true","excel");
      }
	    
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
      if(select_name=="请输入姓名、工号或考勤卡号")
      {
    	  select_name="";
      }
      kqShiftForm.action="/kq/team/historical/search_array_data.do?b_search=link&select_flag=1";
      kqShiftForm.submit();
   }

   function dofocus(inputObj){ 
		if(inputObj.value=="请输入姓名、工号或考勤卡号"){ 
			document.getElementById("select_name").style.color = "black";
			inputObj.value="";
		} 
		
	} 
	function doblur(inputObj){ 
		 
		if(inputObj.value==''){ 
			inputObj.value="请输入姓名、工号或考勤卡号"; 
			document.getElementById("select_name").style.color = "gray";
		}
		if(inputObj.value!="请输入姓名、工号或考勤卡号"){
			document.getElementById("select_name").style.color = "black";
		} 
	} 
</script><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/team/array/search_array_data">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr> 
  <td>
   <table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
    <tr> 
     <td>
        <table border="0" cellspacing="0"  align="left" cellpadding="0">
            <tr>            
           <td align="left">
           <table border="0" cellspacing="0"  align="left" cellpadding="0"><tr><td>
            <hrms:menubar menu="menu2" id="menubar1" target="mil_body"> 
              <hrms:menuitem name="file" label="文件">
                 <hrms:menuitem name="mitem4" label="输出排班信息" icon="/images/link.gif" url="javascript:excecuteExcel();" function_id=""/>                                         
             　　</hrms:menuitem> 
           </hrms:menubar>
           </td></tr></table>
        </td>
      </tr>
    </table>
    </td>  
    <td align= "left" nowrap>&nbsp;
		  <select id="his" onchange="history(this)">
           	<option value="1">归档数据</option>
           	<option value="0">封存数据</option>
           </select> 
     </td>    
    <td align= "left" nowrap>&nbsp;&nbsp;
        <bean:message key="kq.register.daily.menu"/>&nbsp;&nbsp;
        <html:select name="kqShiftForm" property="session_y" size="0" onchange="javascript:change();">
        <html:optionsCollection name="kqShiftForm" property="session_y_list" value="dataValue" label="dataName"/>
        </html:select> 
        <html:select name="kqShiftForm" property="session_m" size="0" onchange="javascript:change();">
        <html:optionsCollection name="kqShiftForm" property="duration_list" value="dataValue" label="dataName"/>
        </html:select>
        <html:hidden name="kqShiftForm" property="a_code" styleClass="text"/> 
         <html:hidden name="kqShiftForm" property="nbase" styleClass="text"/>
      </td>  
      <td align="left" nowrap>&nbsp;&nbsp;
         <html:select name="kqShiftForm" property="state" size="1" onchange="show_state(this)">
           <logic:equal name="kqShiftForm" property="state" value="0">
             <option value="0" selected>表格</option> 
           </logic:equal > 
           <logic:notEqual name="kqShiftForm" property="state" value="0">
             <option value="0">表格</option> 
           </logic:notEqual > 
           <logic:equal name="kqShiftForm" property="state" value="1">        
             <option value="1" selected>记录</option>  
           </logic:equal > 
           <logic:notEqual name="kqShiftForm" property="state" value="1"> 
             <option value="1">记录</option>  
           </logic:notEqual > 
        </html:select>  
         
      </td>
      <td align= "left" nowrap>         
          &nbsp;
           <html:select name="kqShiftForm" property="select_pre" size="1">
                <html:optionsCollection name="kqShiftForm" property="kq_list" value="dataValue" label="dataName"/>	        
           </html:select>&nbsp;
           <input type="text" id="select_name" class="inputtext" name="select_name" value="请输入姓名、工号或考勤卡号" onfocus="dofocus(this)" onblur="doblur(this)";  size="29" style="text-align:left;color:gray;height: 23px;margin-left: -2px ">&nbsp;
           <button extra="button" onclick="javascript:selectflag();">查询</button> 
      </td>
      <td nowrap>
      <logic:notEmpty name="kqShiftForm" property="code_mess">
      &nbsp;当前操作对象:<bean:write name="kqShiftForm" property="code_mess" />
      </logic:notEmpty>
      &nbsp;  &nbsp;&nbsp;
     </td>        
      <td>
       
      </td>
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td width="100%">
      ${kqShiftForm.table_html}
  </td>
  </tr>
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
             <td class="td_style common_background_color" height=24>正在做排班处理请稍候....</td>
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
</script>