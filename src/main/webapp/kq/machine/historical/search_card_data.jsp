<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="/kq/kq.js"></script>
<style>
<!--
.divStyle{
	border:1px solid #C4D8EE;overflow: auto;left:5;
	height:532;
	width:expression(document.body.clientWidth-11);
}
.divStyle1{
	overflow: auto;left:5;
	width:expression(document.body.clientWidth-11);
}
-->
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
body {
	
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;
	font-family: "Tahoma";
	font-size: 6px;
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
	width: 15px;
	height: 15px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 11px;
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
   function change()
   {
      if(!validate(eval("document.kqCardDataForm.start_date"),"起始日期"))
      {
         return false;
      }
      if(!validate(eval("document.kqCardDataForm.end_date"),"结束日期"))
      {
         return false;
      }
      var dd = eval("document.kqCardDataForm.start_date");
   	  var ks = dd.value;
   	  var jsd=eval("document.kqCardDataForm.end_date");
   	  var js = jsd.value;
   	  ks=replaceAll(ks,"-",".");
   	  js=replaceAll(js,"-",".");
   	  if(ks>js)
   	  {
   	  	alert("结束时间不能大于开始时间！");
   	  	return false;
   	  }
      kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_query=link&a_code=${kqCardDataForm.a_code}";
      kqCardDataForm.submit();
   }  
   function take_machinelist()
   {
      kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_take=link";
      kqCardDataForm.submit();
   } 
   function machineTime()
   {
      
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/machine/historical/search_card_data.do?b_time=link";
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=406,height=274','_top'); 
       window.showModalDialog(target_url,1, 
        "dialogWidth:406px; dialogHeight:274px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
   } 
   function take_filelist()
   {
      kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_file=link";
      kqCardDataForm.submit();
   } 
   function repair_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       var a_code="${kqCardDataForm.a_code}";       
       var nbase="${kqCardDataForm.nbase}";
       var work_date="${kqCardDataForm.cur_date}";
       var cur_session="${kqCardDataForm.cur_session}";
       target_url="/kq/machine/historical/repair_card.do?b_query=link`nbase="+nbase+"`a_code="+a_code+"`work_date="+work_date+"`cur_session="+cur_session;
       //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=140,left=320,width=606,height=575','_top');
      var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
       window.showModalDialog(iframe_url,1, 
        "dialogWidth:606px; dialogHeight:575px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
   }   
   function dataAnalyse2()
   {
       var len=document.kqCardDataForm.elements.length;
       var i;
       for (i=0;i<len;i++)
       {
          if (document.kqCardDataForm.elements[i].type=="checkbox")
          {
            document.kqCardDataForm.elements[i].checked=false;
          }
       }
       kqCardDataForm.action="/kq/machine/historical/analyse/data_analyse.do?b_query=link&action=data_analyse_data.do&target=mil_body&privtype=kq";
	   kqCardDataForm.target="il_body";
	   kqCardDataForm.submit();
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
        kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_delete=link";
        kqCardDataForm.submit();
     }      
   }
   function excecuteExcel()
   {
    var dd = document.getElementById("select_pre").value;
    var sp_flag = document.getElementById("sp_flag").value;
    var into_flag = document.getElementById("into_flag").value;
	var hashvo=new ParameterSet();			
	hashvo.setValue("a_code","${kqCardDataForm.a_code}");
	hashvo.setValue("start_date","${kqCardDataForm.start_date}");
	hashvo.setValue("end_date","${kqCardDataForm.end_date}");
	hashvo.setValue("start_hh","${kqCardDataForm.start_hh}");
	hashvo.setValue("start_mm","${kqCardDataForm.start_mm}");
	hashvo.setValue("end_hh","${kqCardDataForm.end_hh}");
	hashvo.setValue("end_mm","${kqCardDataForm.end_mm}");
	hashvo.setValue("select_name","${kqCardDataForm.select_name}");
	hashvo.setValue("nbase","${kqCardDataForm.nbase}");
	hashvo.setValue("select_pre",dd);
	hashvo.setValue("sp_flag",sp_flag);
	hashvo.setValue("into_flag",into_flag);
	hashvo.setValue("datafrom","${kqCardDataForm.datafrom}");  //1:补卡 0：正常
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15301130023'},hashvo);
   }	
   function showExcel(outparamters)
   {
    MusterInitData();
	var url=outparamters.getValue("excelfile");	
	window.location.target="_blank";
	window.location.href = "/servlet/vfsservlet?fileid=" + url +"&fromjavafolder=true";
   }
   function filtrate()
   {
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/machine/historical/search_card_data.do?b_filter=link";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=250,width=406,height=374','_top'); 
       //var return_vo= window.showModalDialog(target_url,1, 
       // "dialogWidth:406px; dialogHeight:274px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
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
    function query(cur_flag,datafrom)
    {
      kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_query=link&a_code=${kqCardDataForm.a_code}&cur_flag="+cur_flag+"&datafrom="+datafrom;
      kqCardDataForm.submit();
    }  
    function select_no_card()
    {
        kqCardDataForm.action="/kq/machine/historical/search_no_card.do?b_query=link&action=search_no_card_data.do&target=mil_body"
        kqCardDataForm.target="il_body";
        kqCardDataForm.submit();       
    }  
    function approve(sp_flag)
    {
    	var str="";
		for(var i=0;i<document.kqCardDataForm.elements.length;i++)
			{
				if(document.kqCardDataForm.elements[i].type=="checkbox")
				{
					if(document.kqCardDataForm.elements[i].checked==true)
					{
						if(document.kqCardDataForm.elements[i].name=="selbox")
							continue;
							str+=document.kqCardDataForm.elements[i].value+"/";
					}
				}
			}
	   if(str.length==0)
	   {
	   		alert("请选择！");
			return;
	   }else
	   {
	   		var mess="";
       		if(sp_flag=="03")
         		mess="确认要批准已选择的人员？";
       		else if(sp_flag=="07")
         		mess="确认要驳回已选择的人员？";
       		else 
         		return false;
       		if(confirm(mess))
       		{
         		kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_approve=link&sp_action="+sp_flag;
         		kqCardDataForm.submit();
       		}
	   }
    } 
    function view_card(nbase,a0100,work_date,work_time)
    {
        var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/machine/historical/search_card_data.do?b_view=link&a0100="+a0100+"&nbase="+nbase+"&work_date="+work_date+"&work_time="+work_time;
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=250,width=406,height=400','_top'); 
    } 
    var checkflag = "false";
    function selAll()
    {
      var len=document.kqCardDataForm.elements.length;
       var i;
       
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.kqCardDataForm.elements[i].type=="checkbox")
            {
              document.kqCardDataForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.kqCardDataForm.elements[i].type=="checkbox")
          {
            document.kqCardDataForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    } 
        
  }   
  function syncCard()
  {
      var iframe_url="/kq/machine/historical/sync_tiem.jsp";
      var vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:506px; dialogHeight:350px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
      if(vo)
      {
         document.getElementById("start_date").value=vo.start_date;
         document.getElementById("start_hh").value=vo.start_hh;
         document.getElementById("start_mm").value=vo.start_mm;
         document.getElementById("end_date").value=vo.end_date;
         document.getElementById("end_hh").value=vo.end_hh;
         document.getElementById("end_mm").value=vo.end_mm;
         var waitInfo=eval("wait");	
	     waitInfo.style.display="block";
         kqCardDataForm.action="/kq/machine/historical/search_card_data.do?b_sync=link";
         kqCardDataForm.submit();
      }
  }
</script>
<hrms:themes /> <!-- 7.0css -->
 <% int i=0;%>
<html:form action="/kq/machine/historical/search_card_data">

<table width="100%" >
 <tr>
  <td> 
     <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
       <tr>
         <td width="200">
          <table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
           <tr>
            <td height="35">
            <hrms:menubar menu="menu1" id="menubar1" target="mil_body">
             <hrms:menuitem name="file" label="文件">       
             <hrms:menuitem name="mitem5" label="数据导出" icon="/images/sort.gif" url="javascript:excecuteExcel();" function_id="270603,0C3703"/>
             </hrms:menuitem>        
             </hrms:menubar>
            </td>
            </tr>
           </table>
          </td> 
          <td nowrap>&nbsp;           
           <html:select name="kqCardDataForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
           </html:select>   
           &nbsp;&nbsp;状态&nbsp;
           <select name="sp_flag" size="1" onchange="change();">
           
           <logic:equal name="kqCardDataForm" property="sp_flag" value="all">
              <option value="all" selected>全部</option>
           </logic:equal>
            <logic:notEqual name="kqCardDataForm" property="sp_flag" value="all">
             <option value="all">全部</option>
           </logic:notEqual>
           <logic:equal name="kqCardDataForm" property="sp_flag" value="02">
              <option value="02" selected>已报批</option>
           </logic:equal>
            <logic:notEqual name="kqCardDataForm" property="sp_flag" value="02">
              <option value="02">已报批</option>
           </logic:notEqual>
           <logic:equal name="kqCardDataForm" property="sp_flag" value="03">
              <option value="03" selected>批准</option>
           </logic:equal> 
            <logic:notEqual name="kqCardDataForm" property="sp_flag" value="03">
              <option value="03">批准</option>
           </logic:notEqual>
           <logic:equal name="kqCardDataForm" property="sp_flag" value="07"> 
              <option value="07" selected>驳回</option>
           </logic:equal>
            <logic:notEqual name="kqCardDataForm" property="sp_flag" value="07">
              <option value="07">驳回</option>
           </logic:notEqual>
           </select> 
          </td>
          <td align="right" nowrap>&nbsp;
            范围&nbsp;
          </td>
          <td align="left" nowrap> 
           <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		   <td><span style="vertical-align: middle;">		   
		   <input type="text" name="start_date" value="${kqCardDataForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;height:25px;text-align:left" id="editor1" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
		   </span>
		   </td>
		   <td width="40" nowrap style="background-color:#FFFFFF;"> 
		   <span style="vertical-align: middle;">
		     <div class="m_frameborder inputtext" style="margin-left:2px;height:25px;">
		      &nbsp;<input type="text" class="m_input" maxlength="2" name="start_hh" value="${kqCardDataForm.start_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="start_mm" value="${kqCardDataForm.start_mm}" onfocus="setFocusObj(this,60);">
		     </div>
		     	</span>
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
             -
          </td>
          <td align= "left" nowrap>             
	    
             <table border="0" cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		<td><span style="vertical-align: middle;">		   
		   <input type="text" name="end_date"  value="${kqCardDataForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);'>
		  </span> </td>
		   <td width="40" nowrap style="background-color:#FFFFFF"><span style="vertical-align: middle;"> 
		     <div class="m_frameborder inputtext" style="margin-left:2px;height:25px;">
		      <input type="text" class="m_input" maxlength="2" name="end_hh" value="${kqCardDataForm.end_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="end_mm" value="${kqCardDataForm.end_mm}" onfocus="setFocusObj(this,60);">
		     </div></span>
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
         
         <td align= "left" nowrap>&nbsp; 
             <%--<logic:equal name="kqCardDataForm" property="isInout_flag" value="true">--%>
                         出入类型&nbsp;
                      <html:select name="kqCardDataForm" property="into_flag" size="1" onchange="change();">
                      <html:option value="all">全部</html:option>  
                      <html:option value="-1">出</html:option>                      
                      <html:option value="1">进</html:option>
                      <html:option value="0">不限</html:option>
                      </html:select> 
             <%--</logic:equal>--%>
         </td>
         
         <td align= "left" nowrap>&nbsp;
          姓名&nbsp;
         <input type="text" name="select_name" value="" class="inputtext" style="width:100px;font-size:10pt;height:25px;text-align:left">	&nbsp;
  </td>
  <td align= "left" nowrap>
         <input type="button" name="br_return" value='查询' class="mybutton" onclick="change();">          
  </td>
        </tr>
       </table> 
  </td>  
 </tr>
 <tr>
  <td width="100%">
  <div class="divStyle common_border_color">
    <table width="100%"  cellspacing="0"  align="center" cellpadding="0" class="ListTableF" style="border: none;">
      <thead>
         <tr>
            <td align="center" class="TableRow" style="border-left:none;border-top:none;" nowrap>
            </td>  
            <logic:iterate id="element"    name="kqCardDataForm"  property="fielditemlist" indexId="index"> 
                <logic:equal name="element" property="visible" value="true">
                 <logic:notEqual name="element" property="itemid" value="e0122">
                 <logic:notEqual name="element" property="itemid" value="b0110">
                 <logic:notEqual name="element" property="itemid" value="a0101">
                 <logic:notEqual name="element" property="itemid" value="machine_no">
                 	<td align="center" class="TableRow" style="border-top:none;" nowrap>
                  		&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 	</td>
                 </logic:notEqual>
                 </logic:notEqual>
                 </logic:notEqual>
                 </logic:notEqual>
                 <logic:equal name="element" property="itemid" value="a0101">
                     <logic:notEqual name="kqCardDataForm" property="a0101zx" value="false">
                          <td align="center" class="TableRow" style="border-top:none;" nowrap>
                  		&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 	      </td> 		
                    </logic:notEqual>
                 </logic:equal>
                 <logic:equal name="element" property="itemid" value="b0110">
                     <logic:notEqual name="kqCardDataForm" property="b0110zx" value="false">
                          <td align="center" class="TableRow" style="border-top:none;" nowrap>
                  		&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 	      </td> 		
                    </logic:notEqual>
                 </logic:equal>
                 <logic:equal name="element" property="itemid" value="e0122">
                     <logic:notEqual name="kqCardDataForm" property="e0122zx" value="false">
                          <td align="center" class="TableRow" style="border-top:none;" nowrap>
                  		&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 	      </td> 		
                    </logic:notEqual>
                 </logic:equal>
                 <logic:equal name="element" property="itemid" value="machine_no">
                     <logic:notEqual name="kqCardDataForm" property="kqj" value="false">
                          <td align="center" class="TableRow" style="border-top:none;" nowrap>
                  		&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp; 
                 	      </td> 		
                    </logic:notEqual>
                 </logic:equal>
              </logic:equal>
           </logic:iterate>  
           <td align="center" class="TableRow" style="border-right:none;border-top:none;" nowrap>
	     <bean:message key="kq.strut.more"/>            	
          </td>                   	        
         </tr>         
      </thead>
      <hrms:paginationdb id="element" name="kqCardDataForm" sql_str="kqCardDataForm.sqlstr" table="" where_str="" columns="kqCardDataForm.column" order_by="kqCardDataForm.orderby" pagerows="${kqCardDataForm.pagerows}" page_id="pagination">
          <%
          if(i%2==0){ 
          %>
          <tr class="trShallow" onClick="javascript:tr_onclick(this,'')">
          <%
          }else{
          %>
          <tr class="trDeep" onClick="javascript:tr_onclick(this,'DDEAFE')">
          <%}i++;          
          %>  
           <%
             	 LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
             	 String id=(String)abean.get("work_time");   
             	 String z1 = (String)abean.get("work_date");         	   	                           
           %>
          <td align="center" class="RecordRow" style="border-left:none;" nowrap>
          		<hrms:kqdurationjudge startDate="<%=z1 %>">  
                <hrms:checkmultibox name="kqCardDataForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
           		</hrms:kqdurationjudge> 
           </td>
           <logic:iterate id="info" name="kqCardDataForm"  property="fielditemlist">  
             <logic:equal name="info" property="visible" value="true">
                 <logic:equal name="info" property="itemtype" value="A">
                     <logic:notEqual name="info" property="codesetid" value="0">
                     	<logic:notEqual name="info" property="itemid" value="e0122">
                     	 <logic:notEqual name="info" property="itemid" value="b0110">
                          <td align="left" class="RecordRow" nowrap>
                             <hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          </td>
                         </logic:notEqual>  
                        </logic:notEqual>
                        <logic:equal name="info" property="itemid" value="e0122">
                        	<logic:notEqual name="kqCardDataForm" property="e0122zx" value="false">
                        		<td align="left" class="RecordRow" nowrap>
                             	<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page" uplevel="${kqCardDataForm.uplevel}"/>  	      
                             	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          		</td>
                        	</logic:notEqual>
                        </logic:equal>
                        <logic:equal name="info" property="itemid" value="b0110">
                        	<logic:notEqual name="kqCardDataForm" property="b0110zx" value="false">
                        		<td align="left" class="RecordRow" nowrap>
                             	<hrms:codetoname codeid="${info.codesetid}" name="element" codevalue="${info.itemid}" codeitem="codeitem" scope="page"/>  	      
                             	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;  
                          		</td>
                        	</logic:notEqual>
                        </logic:equal>   
                      </logic:notEqual>
                      <logic:equal name="info" property="codesetid" value="0">
                             <logic:notEqual name="info" property="itemid" value="inout_flag">
                               <logic:notEqual name="info" property="itemid" value="a0101">
                                <logic:notEqual name="info" property="itemid" value="machine_no">
                                    <logic:notEqual name="info" property="itemid" value="iscommon">
                                		<td align="left" class="RecordRow" nowrap>
		                                    &nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
		                                </td>
                                	</logic:notEqual>
                                	<logic:equal name="info" property="itemid" value="iscommon">
                                		<td align="center" class="RecordRow" nowrap>
			                                <logic:equal name="element" property="${info.itemid}" value="0">
			                                   &nbsp;否&nbsp;
			                                </logic:equal>
			                                <logic:notEqual name="element" property="${info.itemid}" value="0">
			                                   &nbsp;是&nbsp;
			                                </logic:notEqual>
		                                </td>
                                	</logic:equal>
                                </logic:notEqual>  
                               </logic:notEqual>
                               <logic:equal name="info" property="itemid" value="a0101">
                               		<logic:notEqual name="kqCardDataForm" property="a0101zx" value="false">
                               			<td align="left" class="RecordRow" nowrap>
                               				&nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                               			</td>
                               		</logic:notEqual>
                               </logic:equal>
                               <logic:equal name="info" property="itemid" value="machine_no">
                               		<logic:notEqual name="kqCardDataForm" property="kqj" value="false">
                               			<td align="left" class="RecordRow" nowrap>
                               				&nbsp;<bean:write name="element" property="${info.itemid}"/>&nbsp;
                               			</td>
                               		</logic:notEqual>
                               </logic:equal>
                              </logic:notEqual>
                             <logic:equal name="info" property="itemid" value="inout_flag"> 
                                <td align="left" class="RecordRow" nowrap>
                               <logic:equal name="element" property="inout_flag" value="-1">
                                   &nbsp;出&nbsp;
                               </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="0">
                                   &nbsp;不限&nbsp;
                                </logic:equal>
                                <logic:equal name="element" property="inout_flag" value="1">
                                   &nbsp;进&nbsp;
                               </logic:equal>
                               </td>
                              </logic:equal> 
                      </logic:equal>
                 </logic:equal>
                 <logic:equal name="info" property="itemtype" value="D">
                       <td align="left" class="RecordRow" nowrap>
                           <bean:write name="element" property="${info.itemid}" />&nbsp;  
                       </td>
                    </logic:equal>  
                 <logic:equal name="info" property="itemtype" value="N">
                   <td align="center" class="RecordRow" nowrap> 
                      <bean:write name="element" property="${info.itemid}"/>
                    </td> 
                 </logic:equal>
             </logic:equal>
           </logic:iterate>
             <td align="center" class="RecordRow" style="border-right:none;" nowrap>
            					 <bean:define id="nbase1" name="element" property="nbase"/>
						         <bean:define id="a01001" name="element" property="a0100"/>
						         <bean:define id="work_date1" name="element" property="work_date"/>
						         <bean:define id="work_time1" name="element" property="work_time"/>
						         <%
						         	String nbase2 = PubFunc.encrypt(nbase1.toString());
						         	String a01002 = PubFunc.encrypt(a01001.toString());
						         	String work_date2 = PubFunc.encrypt(work_date1.toString());
						         	String work_time2 = PubFunc.encrypt(work_time1.toString());
						         %>  
                <a href="###" onclick="view_card('<%=nbase2 %>','<%=a01002 %>','<%=work_date2 %>','<%=work_time2 %>')"><img src="/images/view.gif" border=0></a>
            </td>  
          </tr>
        </hrms:paginationdb>
    </table>
    </div>
  </td>
 </tr> 
  <tr>
   <td>
   <div class="divStyle1">
     <table  width="100%" class="RecordRowP"  align="center">       
			<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="kqCardDataForm" pagerows="${kqCardDataForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td   align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="kqCardDataForm" property="pagination" nameId="kqCardDataForm" scope="page">
			</hrms:paginationdblink>
		</td>
		</tr>	
     </table>
     </div>
   </td>
 </tr>
 <tr>
       <td  valign="bottom"  class="tdFontcolor" nowrap style="height:35px;">   
		         <logic:equal value="dxt" name="kqCardDataForm" property="returnvalue">  
		           <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqCardDataForm"/>
		         </logic:equal> 
		 <bean:write name="kqCardDataForm" property="inout_str" filter="false"/>&nbsp;&nbsp;
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
  <table border="1" width="37%" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正在接收数据请稍候....</td>
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