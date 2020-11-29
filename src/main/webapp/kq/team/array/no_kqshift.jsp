<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
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
<script type="text/javascript" src="/kq/kq.js"></script>
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

   function change()
   {
      kqShiftForm.action="/kq/team/array/search_array_data.do?b_search=link&a_code=${kqShiftForm.a_code}";
      kqShiftForm.submit();
   } 
   function normal_shtif()
   {
     var o=eval("document.kqShiftForm.a_code");
     var a_code=o.value; 
     var target_url;
     var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
     target_url="/kq/team/array/normal_array_data.do?b_normal=link&a_code=${kqShiftForm.a_code}&nbase=${kqShiftForm.nbase}&session_data=${kqShiftForm.session_data}";
     //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=auto,resizable=no,top=270,left=320,width=456,height=304');
     var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(!return_vo)
	    return false;	   
      if(return_vo.flag=="true")
      {
         var waitInfo=eval("wait");	   
	 waitInfo.style.display="block";  
      }
   }
   function cycle_shtif()
   {
       var o=eval("document.kqShiftForm.a_code");
       var a_code=o.value;    
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/team/array/cycle_array_data.do?b_cycle=link&a_code=${kqShiftForm.a_code}&nbase=${kqShiftForm.nbase}&session_data=${kqShiftForm.session_data}";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=556,height=406');
  }
   function relief_shtif()
   {
       var o=eval("document.kqShiftForm.a_code");
       var a_code=o.value;    
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       target_url="/kq/team/array/relief_array_data.do?b_relief=link&a_code=${kqShiftForm.a_code}&nbase=${kqShiftForm.nbase}&session_data=${kqShiftForm.session_data}";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=200,left=320,width=506,height=406');
       
   }
   function unsteady_shtif()
   {
       kqShiftForm.action="/kq/team/array/unsteady_shift.do?b_query=link&action=unsteady_shift_data.do&target=mil_body";
       kqShiftForm.target="il_body";
       kqShiftForm.submit();
   }
   function groupSet()
   {
      kqShiftForm.action="/kq/team/array_set/search_array_data.do?b_search=link&action=search_array_data.do&target=mil_body";
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
	hashvo.setValue("session_data","${kqShiftForm.session_data}");
	hashvo.setValue("state","${kqShiftForm.state}");
	var In_paramters="exce=excel";	
	hashvo.setValue("decrypt_flag","0");
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15221000004'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
	
   }
    function MusterInitData()
   {
	   var waitInfo=eval("wait");	
	   waitInfo.style.display="none";
	   hide_nbase_select('select_pre');
   }
   function viewAll()
   {
       Element.hide('datepnl');
       kqShiftForm.action="/kq/team/array/search_noarray_data.do?b_search=link&select_flag=0";
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
      var startdate = $F('start_date');
      var enddate = $F('end_date');
      if(startdate > enddate){
          alert("开始时间不能大于结束时间！");
		  return false;
      }
      kqShiftForm.action="/kq/team/array/search_noarray_data.do?b_search=link&select_flag=1";
      kqShiftForm.submit();
   }
   function quitRe()
   {
      window.location="/kq/register/daily_register.do?br_quit=link";
   }
   function show_class()
   {
      kqShiftForm.action="/kq/team/array/search_array.do?b_query=link&action=search_array_data.do&target=mil_body&privtype=kq&viewPost=kq";
      kqShiftForm.target="il_body";
      kqShiftForm.submit();
   }
   function kqshift()
   {
        var len=document.kqShiftForm.elements.length;
        var i;
        var checked=false;
        for (i=0;i<len;i++)
        {
         if (document.kqShiftForm.elements[i].type=="checkbox"&&document.kqShiftForm.elements[i].checked&&document.kqShiftForm.elements[i].name!="aa")
          {
             checked=true;
             break;
          }
        }
        if(!checked)
        {
           alert("请选择人员!");
           return false;
        }
        var target_url;
        var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
        target_url="/kq/team/array/normal_noarray_data.do?b_shiftq=link&session_data=${kqShiftForm.session_data}";
        var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
	       return false;	   
        if(return_vo.flag=="true")
        {
           var waitInfo=eval("wait");	   
	       waitInfo.style.display="block";  
           var par="start_date="+return_vo.start_date+"&end_date="+return_vo.end_date+"&selected_class="+return_vo.class_id;
           par=par+"&rest_postpone="+return_vo.rest_postpone+"&feast_postpone="+return_vo.feast_postpone;
           kqShiftForm.action="/kq/team/array/search_noarray_data.do?b_shifts=link&a_code=${kqShiftForm.a_code}&"+par;
           kqShiftForm.submit();
        }
   }
   function kqqueue()
   {
   		var len=document.kqShiftForm.elements.length;
        var i;
        var checked=false;
        for (i=0;i<len;i++)
        {
         if (document.kqShiftForm.elements[i].type=="checkbox"&&document.kqShiftForm.elements[i].checked&&document.kqShiftForm.elements[i].name!="aa")
          {
             checked=true;
             break;
          }
        }
        if(!checked)
        {
           alert("请选择人员!");
           return false;
        }
        var target_url;
        var winFeatures = "dialogHeight:300px; dialogLeft:250px;";
        target_url="/kq/team/department/department_noarray_data.do?b_shiftq=link&start_date=${kqShiftForm.start_date}&end_date=${kqShiftForm.end_date}";
        var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:440px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(!return_vo)
	       return false;
	    if(return_vo.flag=="true")
	    {
	       var waitInfo=eval("wait");	   
	       waitInfo.style.display="block";
           var par="startDate="+return_vo.start_date+"&endDate="+return_vo.end_date;
           kqShiftForm.action="/kq/team/array/search_noarray_data.do?b_inherit=link&a_code=${kqShiftForm.a_code}&"+par;
           kqShiftForm.submit();
           
           //selectflag();--wangmj 2013年4月24日17:17:42 注释  
	    }  		
   }
     var checkflag = "false";

 function selAll()
  {
      var len=document.kqShiftForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.kqShiftForm.elements[i].type=="checkbox")
            {
              document.kqShiftForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.kqShiftForm.elements[i].type=="checkbox")
          {
            document.kqShiftForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  } 
</script>
<html:form action="/kq/team/array/search_noarray_data">
<html:hidden name="kqShiftForm" property="a_code" styleClass="text"/> &nbsp;   
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" style="margin-top:-15px;">
 <tr style="padding-bottom: 5px">
  <td>
   <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
    
    <tr> 
     <td align= "left" nowrap>


      </td>  
      <td align= "left" nowrap>           
            <html:select name="kqShiftForm" property="select_pre" size="1" onchange="selectflag();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
            </html:select>&nbsp;
           <bean:message key="label.title.name"/> 
           <input type="text" name="select_name" class="inputtext" value="${kqShiftForm.select_name}" style="width:100px;font-size:10pt;text-align:left">
           <bean:message key="label.from"/>
   	  	 	<input type="text" name="start_date" class="inputtext" value="${kqShiftForm.start_date}" style="width:100px;font-size:10pt;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
   	  	 	<bean:message key="label.to"/>
   	  	 	<input type="text" name="end_date" class="inputtext"  value="${kqShiftForm.end_date}" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
   	  	   <input style="position:relative;left:5px;" class="mybutton" type="button" onclick="javascript:selectflag();" value="查询">
      </td>  
    </tr>
    </table>
  </td>
 </tr>
 <tr>
  <td width="100%">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     <thead>
              <tr>   
                <td align="center" class="TableRow" nowrap>
		 <input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
               </td>      	    
               <td align="center" class="TableRow" nowrap>
                   <bean:message key="label.dbase"/>
               </td>
                <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
	    <logic:notEqual value="1" name="kqShiftForm" property="isPost">
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;
	    </td>
	    </logic:notEqual>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="arrayGroupSelectForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />&nbsp;   
	    </td> 
               </tr>
   	     </thead>
   	     <%
   	       int i=0;
   	     %>
   	      <hrms:paginationdb id="element" name="kqShiftForm" sql_str="kqShiftForm.sql" table="" where_str="" columns="kqShiftForm.columns" order_by="order by b0110,e0122,a0000" page_id="pagination" pagerows="20" indexes="indexes">
   	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>  
                 <td align="center" class="RecordRow" nowrap>
                  <hrms:checkmultibox name="kqShiftForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
                </td>  
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="@@" name="element" codevalue="nbase" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page" />  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page" uplevel="${kqShiftForm.uplevel}"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                   
                 </td> 
                  <logic:notEqual value="1" name="kqShiftForm" property="isPost">
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td>  
                 </logic:notEqual>
                 <td align="left" class="RecordRow" nowrap>  
                 <bean:define id="a01001" name="element" property="a0100"/>
		         <bean:define id="nbase1" name="element" property="nbase"/>
		         <%
		         		//参数加密
		    		     String a0100 = PubFunc.encrypt(a01001.toString());
		    		     String nbase = PubFunc.encrypt(nbase1.toString());
		         %>   
                   <a href="/kq/team/array/sing_noarray_data.do?b_sing=link&a0100=<%=a0100 %>&dbase=<%=nbase %>&start_date=${kqShiftForm.start_date}&&end_date=${kqShiftForm.end_date}">           
                     &nbsp;<bean:write name="element" property="a0101" filter="true"/>
                     </a>
                 </td>
             </tr>
             <%
   	       i++;
   	     %>
   	     </hrms:paginationdb>
   	 </table>  
  </td>
  </tr>
   <tr>
     <td>
      <table  width="100%" class="RecordRowP" align="left">
       <tr>         
		<td width="40%" valign="bottom"  class="tdFontcolor" nowrap>
		<bean:message key="label.page.serial"/>
		    <bean:write name="pagination" property="current" filter="true" />
			<bean:message key="label.page.sum"/>
			<bean:write name="pagination" property="count" filter="true" />
			<bean:message key="label.page.row"/>
			<bean:write name="pagination" property="pages" filter="true" />
			<bean:message key="label.page.page"/>
		</td>
	        <td  width="60%" align="left" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="kqShiftForm" property="pagination" nameId="kqShiftForm" scope="page">
				</hrms:paginationdblink>
		</td>
	      </tr>
          </table> 
      </td>
    </tr>
   
    <tr>
       <td width="20%" valign="bottom" align="left"  class="tdFontcolor" nowrap style="padding-top:5px;">
          <input type="button" name="b_quit" value='排班' onclick="kqshift();" class="mybutton">
          <input type="button" name="b_quit" value='继承部门班组排班' onclick="kqqueue();" class="mybutton">
          <input type="button" name="b_return" value='<bean:message key="button.return"/>' onclick="javascript:show_class();" class="mybutton">     
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
<div id='wait' style='position:absolute;top:200;left:15;display:none;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="tableRow" height=24>正在排班，请稍候...</td>
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
 
</script>