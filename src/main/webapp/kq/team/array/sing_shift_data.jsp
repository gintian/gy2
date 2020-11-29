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
-->
</style>
<script language="javascript">
   function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
   }  
   
   function editClass(nbase,a_code,days)
   {
      var target_url;
      var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
      target_url="/kq/team/array/normal_noarray_data.do?b_shiftq=link&session_data="+days+"&a_code="+a_code+"&nbase="+nbase;
      var return_vo= window.showModalDialog(target_url,1, 
      "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(!return_vo)
	      return false;	   
      if(!return_vo)
	    return false;	   
      if(return_vo.flag=="true")
      {
         var waitInfo=eval("wait");	   
	     waitInfo.style.display="block";  	    
         var par="start_date="+return_vo.start_date+"&end_date="+return_vo.end_date+"&selected_class="+return_vo.class_id;
         par=par+"&rest_postpone="+return_vo.rest_postpone+"&feast_postpone="+return_vo.feast_postpone;
         kqShiftForm.action="/kq/team/array/sing_noarray_data.do?b_shift=link&a0100="+a_code+"&dbase="+nbase+"&"+par;
         kqShiftForm.submit();
      }
   }
   function quitRe()
   {
       kqShiftForm.action="/kq/team/array/sing_noarray_data.do?br_return=link";
       kqShiftForm.submit();
   
   }
</script>
<html:form action="/kq/team/array/sing_noarray_data">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
 <td>
 <br>
   &nbsp;当前操作对象:<bean:write name="kqShiftForm" property="code_mess" />&nbsp;  &nbsp;&nbsp;<hrms:kqcourse/>
 </td>
 
 </tr>
 <tr> 
  <td>
   <table width="100%" border="0" cellspacing="1"  align="left" cellpadding="1">
    <tr> 
    <td align= "left" nowrap>&nbsp;&nbsp;             
        <html:hidden name="kqShiftForm" property="a_code" styleClass="text"/> 
         <html:hidden name="kqShiftForm" property="nbase" styleClass="text"/>
      </td>  
      <td align="left" nowrap>&nbsp;&nbsp;
      </td>
      <td align= "left" nowrap>         
          &nbsp;
           
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
  <tr>
  	<td>
  		&nbsp;
  	</td>
  </tr>
   <tr>
     <td valign="bottom" align="left"  class="tdFontcolor" nowrap>
          <input type="button" name="b_quit" value='<bean:message key="button.leave"/>' onclick="quitRe();" class="mybutton"> 
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
</script>