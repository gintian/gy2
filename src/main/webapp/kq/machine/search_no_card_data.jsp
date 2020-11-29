<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.kq.machine.KqCardDataForm"%>
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
<style type="text/css">
body {
	background-color: transparent;
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #D4D0C8;
	border-bottom: 1px inset #D4D0C8;
	width: 50px;
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
.myfixedDiv 
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
    margin-left:0px!important;
    margin-right:5px!important;/*解决ff能按距左右5px显示，宽度现对总宽度小10px*/
	width:expression(document.body.clientWidth-22);
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<script language="javascript">
   function change()
   {
      kqCardDataForm.action="/kq/machine/search_no_card_data.do?b_search=link&a_code=${kqCardDataForm.a_code}";
      kqCardDataForm.submit();
   }  
   function take_machinelist()
   {
      kqCardDataForm.action="/kq/machine/search_card_data.do?b_take=link";
      kqCardDataForm.submit();
   } 
   function machineTime()
   {
      
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/machine/search_card_data.do?b_time=link";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=406,height=274'); 
   } 
   function take_filelist()
   {
      kqCardDataForm.action="/kq/machine/search_card_data.do?b_file=link";
      kqCardDataForm.submit();
   } 
   function repair_card()
   {
       var target_url;
       var winFeatures = "dialogHeight:300px; dialogLeft:250px;"; 
       var a_code="${kqCardDataForm.a_code}";   
       var len=document.kqCardDataForm.elements.length;
       var i;
       var j=0;
       var str="";
       for (i=0;i<len;i++)
       {
         if (document.kqCardDataForm.elements[i].type=="checkbox"&&document.kqCardDataForm.elements[i].name!="aa")
         {
            if(document.kqCardDataForm.elements[i].checked==true)
            {
               var idv=(j+1)+"H";              
               str=str+document.getElementById(idv).value+",";
            }
            j++;
         }
       }   
       
       if((a_code==""||a_code=="UN")&&str=="")
       {
          alert("请选择单位，部门或人员！");
       }else
       {
          var nbase="${kqCardDataForm.nbase}";
          var work_date="${kqCardDataForm.cur_date}";
          var cur_session="${kqCardDataForm.cur_session}";
          var start_date = "${kqCardDataForm.start_date}";
          var end_date = "${kqCardDataForm.end_date}";
          for(var i=0;i<str.length;i++){
          	var intCode=str.charCodeAt(i)
          	if(intCode="`")
				str = str.replace(intCode, ":");
		  }
          target_url="/kq/machine/repair_card.do?b_query=link`noCardFlag=1`nbase="+nbase+"`a_code="+a_code+"`work_date="+work_date+"`cur_session="+cur_session+"`checkEm="+str+"`start_date="+start_date+"`end_date="+end_date;
          //newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=180,left=320,width=606,height=525',"_top");
          var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
          window.showModalDialog(iframe_url,window, 
        "dialogWidth:640px; dialogHeight:660px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
       }
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
        kqCardDataForm.action="/kq/machine/search_card_data.do?b_delete=link";
        kqCardDataForm.submit();
     }      
   }
   function excecuteExcel()
   {
	var hashvo=new ParameterSet();			
	hashvo.setValue("a_code","${kqCardDataForm.a_code}");
	hashvo.setValue("cur_date","${kqCardDataForm.cur_date}");
	hashvo.setValue("cur_session","${kqCardDataForm.cur_session}");
	var In_paramters="exce=excel";	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showExcel,functionId:'15211001111'},hashvo);
   }	
   function showExcel(outparamters)
   {
	var url=outparamters.getValue("excelfile");	
	var win=open("/servlet/vfsservlet?fileid="+url+"&fromjavafolder=true","excel");
	
   }
   function filtrate()
   {
       var target_url;
       var winFeatures = "dialogHeight:400px; dialogLeft:320px;"; 
       target_url="/kq/machine/search_card_data.do?b_filter=link";
       newwindow=window.open(target_url,'rr','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=250,width=406,height=274'); 
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
      kqCardDataForm.action="/kq/machine/search_card.do?b_query=link&action=search_card_data.do&target=mil_body&cur_flag="+cur_flag+"&viewPost=kq&datafrom="+datafrom+"&start_date=${kqCardDataForm.return_start_date}&end_date=${kqCardDataForm.return_end_date}&select_name=";
      kqCardDataForm.target="il_body";
      kqCardDataForm.submit();
    }    
    function select_no_card()
    {
        kqCardDataForm.action="/kq/machine/search_no_card.do?b_query=link&action=search_no_card_data.do&target=mil_body&privtype=kq"
        kqCardDataForm.target="il_body";
        kqCardDataForm.submit();       
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
</script>
 <% int i=0;
 //String flag = (String)session.getAttribute("flag");
 %>
 <script language="javascript">
	 <%
	 /**
	 if("1".equals(flag)){
	    KqCardDataForm kqCardDataForm = (KqCardDataForm) session.getAttribute("kqCardDataForm");
	    kqCardDataForm.setReturn_start_date(kqCardDataForm.getStart_date());
	    kqCardDataForm.setReturn_end_date(kqCardDataForm.getEnd_date());
	 	session.setAttribute("flag","2");
	 	}
	 **/
 %>
</script>
<html:form action="/kq/machine/search_no_card_data">
<table width="95%" style="margin-top: 6px; margin-left: 3px;">
 <tr>
  <td> 
     <table width="680px" border="0" cellspacing="0"  align="left" cellpadding="0">
       <tr>
         <td nowrap>           
         <html:select name="kqCardDataForm" property="select_pre" styleId="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select>   
          </td>          
          <td width="180px" align="left" nowrap > 
           <table border="0"  cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		   <td nowrap>		   
		   <input type="text" name="start_date" value="${kqCardDataForm.start_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor1" 
					onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' extra="editor" dataType="simpledate">
		   &nbsp;
		   </td>
		   <td width="40" nowrap style="background-color:#FFFFFF";> 
		     <div class="m_frameborder inputtext">
		      <input type="text" class="m_input" maxlength="2" name="start_hh" value="${kqCardDataForm.start_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="start_mm" value="${kqCardDataForm.start_mm}" onfocus="setFocusObj(this,60);">
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
             <bean:message key="label.to"/>
             &nbsp;
          </td>
          <td width="180px" align= "left" nowrap>             
             <table border="0" cellspacing="0"  align="left" valign="bottom" cellpadding="0">
             <tr>
		<td nowrap>
		  
		   <input type="text" name="end_date"  value="${kqCardDataForm.end_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="editor2" 
					onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' extra="editor" dataType="simpledate">
		   &nbsp;
		   </td>
		   <td width="40" nowrap style="background-color:#FFFFFF"> 
		     <div class="m_frameborder inputtext">
		      <input type="text" class="m_input" maxlength="2" name="end_hh" value="${kqCardDataForm.end_hh}" onfocus="setFocusObj(this,24);"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="end_mm" value="${kqCardDataForm.end_mm}" onfocus="setFocusObj(this,60);">
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
         <td align= "left" nowrap>姓名
          </td>
         <td align= "left" nowrap>
         <input type="text" name="select_name" value="${kqCardDataForm.select_name}" class="inputtext" style="width:100px;font-size:10pt;text-align:left">	
  &nbsp;&nbsp;
         <input type="button" name="br_return" value='查询' class="mybutton" onclick="change();">          
  </td>
        </tr>
       </table> 
  </td>  
 </tr>
 <tr>
  <td width="100%">
<div class="myfixedDiv">
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
      <thead>        
            <tr>   
               <td align="center" class="TableRow" style="border-top:none;border-left:none;" nowrap>
		         <input type="checkbox" name="aa" value="true" onclick="selAll()">
               </td>    	    
               <td align="center" class="TableRow" style="border-top: none;" nowrap>
                  人员库
               </td>
                 <td align="center" class="TableRow" style="border-top: none;" nowrap>
                <!-- 单位 --><bean:message key="b0110.label"/>
               </td>
                <td align="center" class="TableRow" style="border-top: none;"  nowrap>
                 <!-- 部门 --><bean:message key="e0122.label"/>
               </td>
               <logic:notEqual value="kq" name="kqCardDataForm" property="viewPost">
                <td align="center" class="TableRow" style="border-top: none;" nowrap>
                  <!-- 职位 --><bean:message key="e01a1.label"/>
               </td> 
               </logic:notEqual>
               <td align="center" class="TableRow" style="border-top: none;border-right:none;" nowrap>
                  姓名 
               </td>
               </tr> 
      </thead> 
      <hrms:paginationdb id="element" name="kqCardDataForm" sql_str="kqCardDataForm.sqlstr" table="" where_str="" columns="kqCardDataForm.column" order_by="kqCardDataForm.orderby" pagerows="18" page_id="pagination">
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
          <td align="center" class="RecordRow" style="border-left: none;" nowrap>   
                <hrms:checkmultibox name="kqCardDataForm" property="pagination.select" value="true" indexes="indexes"/>
                 <input type="hidden" name='<%=i+"H"%>' id='<%=i+"H"%>' value='<bean:write name="element" property="nbase" filter="true"/>`<bean:write name="element" property="a0100" filter="true"/>'>
           </td>
                <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="@@" name="element" codevalue="nbase" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td> 
                 <logic:notEqual value="kq" name="kqCardDataForm" property="viewPost">
                 <td align="left" class="RecordRow" nowrap>                      
                    <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
                    &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;                            
                 </td>  
                 </logic:notEqual>
                 <td align="left" class="RecordRow" style="border-right: none;" nowrap>                      
                    &nbsp;<bean:write name="element" property="a0101" filter="true"/>
            </td> 
          </tr>
        </hrms:paginationdb>
    </table>
    </div>
     <table  width="100%"  class="RecordRowP"  align="center">
       <tr>
          <td width="20%" valign="bottom"  class="tdFontcolor" nowrap>
             第<bean:write name="pagination" property="current" filter="true" />页
             共<bean:write name="pagination" property="count" filter="true" />条
             共<bean:write name="pagination" property="pages" filter="true" />页
	  </td>
	  <td  width="80%" align="right" nowrap >
	    <hrms:paginationdblink name="kqCardDataForm" property="pagination" nameId="kqCardDataForm" scope="page">
             </hrms:paginationdblink>
	  </td>	  
	</tr>	
     </table>
          <table width="100%"  align="left">
           <tr>
            <td>
             <input type="button"  class="mybutton"  onclick="javascript:repair_card();" value="补刷卡" />&nbsp;   
             <input type="button"  class="mybutton" onclick="javascript:query('1','0');" value="返回" /> 
               
            </td>
            </tr>
           </table>        
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