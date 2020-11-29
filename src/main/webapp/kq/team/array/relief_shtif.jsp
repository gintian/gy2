<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<script language="JavaScript" src="../../../js/popcalendar.js"></script>
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
<STYLE type=text/css>
TEXTAREA {
	SCROLLBAR-FACE-COLOR: #b5daff; SCROLLBAR-HIGHLIGHT-COLOR: #ffffff; SCROLLBAR-SHADOW-COLOR: #000000; SCROLLBAR-ARROW-COLOR: #0000ff; SCROLLBAR-BASE-COLOR: #6699ff; scrollbar-dark-shadow-color: #6699ff
}

SELECT {
	BORDER-RIGHT: #23365d 1px solid; BORDER-TOP: #97aad0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #3559a4 1px solid; BORDER-BOTTOM: #031333 1px solid
}
OPTION {
	BORDER-RIGHT: #23365d 1px solid; BORDER-TOP: #97aad0 1px solid; FONT-SIZE: 12px; BORDER-LEFT: #3559a4 1px solid; BORDER-BOTTOM: #031333 1px solid
}
TD {
	FONT-SIZE: 12px
}
.m_frameborder {
	BORDER-RIGHT: #ffffff 1px inset; 
	BORDER-TOP: #d4d0c8 1px inset; 
	FONT-SIZE: 10px; 
	OVERFLOW: hidden; 
	BORDER-LEFT: #d4d0c8 1px inset; 
	WIDTH: 25px; BORDER-BOTTOM: #ffffff 1px inset; 
	FONT-FAMILY: "Tahoma"; 
	HEIGHT: 19px; 
	BACKGROUND-COLOR: transparent; 
	TEXT-ALIGN: right
}
.m_arrow {
	PADDING-LEFT: 2px; 
	FONT-SIZE: 7px; 
	WIDTH: 16px; 
	CURSOR: default; 
	LINE-HEIGHT: 2px; 
	FONT-FAMILY: "Webdings"; 
	HEIGHT: 8px
}
.m_input {
	BORDER-RIGHT: black 0px solid; 
	BORDER-TOP: black 0px solid; 
	FONT-SIZE: 9px; BORDER-LEFT: 
	black 0px solid;
	WIDTH: 18px; 
	BORDER-BOTTOM: black 0px solid; 
	FONT-FAMILY: "Tahoma"; 
	HEIGHT: 14px; 
	TEXT-ALIGN: right
}
INPUT {
	BACKGROUND-COLOR: transparent
}
.TEXT12 {
	BORDER-RIGHT: none; 
	BORDER-TOP: none; 
	BORDER-LEFT: none; 
	BORDER-BOTTOM: none; 
	BACKGROUND-COLOR: #ffffff
}

.div2
{
 overflow:auto; 
 width: 200px;height: 230px;
 line-height:15px; 
 border:1px solid #C4D8EE;
}
</STYLE>
<script language="javascript">
   var outObject;
   var weeks="";
   var feasts ="";
   var turn_dates="";
   var week_dates="";
   var shift_class_list;
    function getKqCalendarVar()
   {
     var request=new Request({method:'post',asynchronous:false,onSuccess:setkqcalendar,functionId:'15388800008'});
   }
   function setkqcalendar(outparamters)
   {
      weeks=outparamters.getValue("weeks");  
      feasts=outparamters.getValue("feasts"); 
      turn_dates=outparamters.getValue("turn_dates"); 
      week_dates=outparamters.getValue("week_dates");  
   }
    function addSetListfield(shift_class_list,targetbox_id)
    {
       var list_vo,select_vo,vos,i;
       vos= shift_class_list;
       if(vos==null||vos.length<=0)
  	  return false;
       list_vo=vos;       
       vos= document.getElementsByName(targetbox_id);  
       if(vos==null)
  	  return false;
       select_vo=vos[0];
       for(var i=0;i<list_vo.length;i++)
       {
         var no = new Option();         
    	 no.value=list_vo[i].dataValue;
    	 no.text=list_vo[i].dataName;    	 
    	 if(validateSetField(select_vo,no.value))
    	 {
    	   
    	   select_vo.options[select_vo.options.length]=no;
    	 }
    	 
       }     
    }
    function deleteshift()
    {
       kqClassArrayForm.action="/kq/team/array/cycle_array_data.do?b_trans=link&clcye_flag=del";
       kqClassArrayForm.target="rr";
       kqClassArrayForm.submit();
    }
    function addShiftGroup()
    {
       
        var theurl="/kq/team/array/cycle_shift_group.do?b_group=link`object_flag=1";
        if($URL)
        	theurl = $URL.encode(theurl);
        var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
         var return_vo= window.showModalDialog(iframe_url,0, 
        "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        if(return_vo!=null&&return_vo.length>0)
        {
         
          var hashvo=new ParameterSet();
	  hashvo.setValue("object_str",return_vo);
	  hashvo.setValue("object_flag","1");	 
          var request=new Request({method:'post',asynchronous:true,onSuccess:addSetRightFields,functionId:'15221200006'},hashvo);
        }
    }
    function addShiftEmployee()
    {
         var obj=document.getElementById('r_f');
         var exist_emp="";
         for(var i=0;i<obj.options.length;i++)
         {
           exist_emp=exist_emp+"^"+obj.options[i].value;
         }
         var w = /[a-z]/i;
         if(exist_emp.search(w)!=-1)
         {
         	exist_emp="";
         }
         if(exist_emp.indexOf("`")==-1)
         {
         	exist_emp="";
         }
         var theurl="/kq/team/array/cycle_shift_employee.do?b_employee=link`object_flag=0`exist_emp="+exist_emp;
         if($URL)
         	theurl = $URL.encode(theurl);
         var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
         var return_vo= window.showModalDialog(iframe_url,0, 
        "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
        if(return_vo!=null&&return_vo.length>0)
        {
          var hashvo=new ParameterSet();
          hashvo.setValue("object_str",return_vo);
          hashvo.setValue("object_flag","0");	 
          var request=new Request({method:'post',asynchronous:true,onSuccess:addSetRightFields,functionId:'15221200006'},hashvo);
        }
    }
    function addSetRightFields(outparamters)
    {
       var object_list=outparamters.getValue("object_list");
       var object_flag=outparamters.getValue("selected_object");
       //AjaxBind.bind(kqClassArrayForm.left_fields,shift_class_list);
       var obj_selected=document.getElementsByName("selected_object");       
       if(obj_selected==null)
         return false;
       var obj_vos=obj_selected[0].value;       
       if(obj_vos==object_flag)
       {
          addSetListfield(object_list,'right_fields');
       }else
       {
          addSetNewListfield(object_list,'right_fields');
       }  
       obj_selected[0].value=object_flag;  
    }
     function addSetNewListfield(shift_class_list,targetbox_id)
    {
       var list_vo,select_vo,vos,i;
       vos= shift_class_list;
       if(vos==null||vos.length<=0)
  	  return false;
       list_vo=vos;       
       vos= document.getElementsByName(targetbox_id);  
       if(vos==null)
  	  return false;
       select_vo=vos[0];
       for(var i=select_vo.length-1;i>=0;i--)
       {
         select_vo.options[i]=null;
       }
       for(var i=0;i<list_vo.length;i++)
       {
         var no = new Option();         
    	 no.value=list_vo[i].dataValue;
    	 no.text=list_vo[i].dataName;
    	 select_vo.options[i]=no;
       }     
    }
    function validateSetField(select_vo,value)
    {
        for(var i=0;i<select_vo.length;i++)
       {
         var org_value=select_vo.options[i].value;         
         if(org_value==value)
         {
           return false
         }
       }
       return true;
    }
</script>
<script language="javascript">
var COLOR_INIT="#ffffff";
var COLOR_OVER="#e3e3Df";
var COLOR_SELECTED="#b0afab";
COLOR_OVER="#DDEAFE";
COLOR_SELECTED="#fff8d2";
var obj_tr;
function init_color(obj) { 
    for(var i=0;i<obj.length;i++){ obj[i].style.backgroundColor  =  COLOR_INIT;}
}

function creat_event(obj) {
    for(var i=0;i<obj.length;i++){
        obj[i].onmouseover  =  overdo;
        obj[i].onmouseout   =  outdo;
        obj[i].onclick      =  clickdo;
    }
}
function overdo() {
        if(this.style.backgroundColor!=COLOR_SELECTED)this.style.backgroundColor=COLOR_OVER;
}
function outdo() {
        if(this.style.backgroundColor!=COLOR_SELECTED)this.style.backgroundColor=COLOR_INIT;
}
function clickdo(obj) {
        init_color(obj_tr);
       
        this.style.backgroundColor=COLOR_SELECTED;
}

function init_event() {
        var table=document.getElementById('tbl');
        obj_tr=table.getElementsByTagName('tr');        
        init_color(obj_tr);
        creat_event(obj_tr);
}
function updiv(obj) {
    var n=0; 
       
    for(var i=0;i<obj.length;i++){ 
        if(obj[i].style.backgroundColor==COLOR_SELECTED){ change_tr(i-1,i); n+=1; return;}               
    }
    if(n==0) alert("请先选择一行");
}

function downdiv(obj) {
        var n=0; 
        for(var i=0;i<obj.length;i++){ 
                if(obj[i].style.backgroundColor==COLOR_SELECTED){ change_tr(i+1,i); n+=1; return;}               
    }
    if(n==0) alert("请先选择一行");
}
function deleteTR(obj)
{
   var n=0; 
        for(var i=0;i<obj.length;i++){ 
                if(obj[i].style.backgroundColor==COLOR_SELECTED)
                {
                 var table=document.getElementById('tbl');
                 table.deleteRow(i);  
                 n+=1;  
                 return;
                }
             
           }
        if(n==0) alert("请先选择一行");
}

function change_tr(x,y) {

        if(x<0||x>=obj_tr.length){ alert("已经到顶了！");return;}
        var obj_td_x=obj_tr[x].getElementsByTagName('td'); 
        var obj_td_y=obj_tr[y].getElementsByTagName('td'); 
    var a_1  =  obj_td_x[0].innerHTML;
    var a_2  =  obj_td_x[1].innerHTML;
    var a_3  =  obj_td_x[2].innerHTML;
    var b_1  =  obj_td_y[0].innerHTML;   
    var b_2  =  obj_td_y[1].innerHTML;  
    var b_3  =  obj_td_y[2].innerHTML;  
    //obj_tr[x].innerHTML=b; 
    //obj_tr[y].innerHTML=a;   
    obj_td_x[0].innerHTML=b_1;
    obj_td_x[1].innerHTML=b_2;
    obj_td_x[2].innerHTML=b_3;
    obj_td_y[0].innerHTML=a_1;   
    obj_td_y[1].innerHTML=a_2;
    obj_td_y[2].innerHTML=a_3;
    obj_tr[y].style.backgroundColor=COLOR_INIT;
    obj_tr[x].style.backgroundColor=COLOR_SELECTED;    
}

	
 function IsDigit() 
{ 
        return ((event.keyCode > 46) && (event.keyCode <= 57)); //天数是整数，过滤掉小数点
} 
function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if (!fObj) return;		
		var cmd = event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		var radix = parseInt(fObj.radix,10)-1;		
		if (i==radix&&cmd) {
			i = 0;
		} else if (i==0&&!cmd) {
			i = radix;
		} else {
			cmd?i++:i--;
		}		
		fObj.value = i;
		fObj.select();
}  
  function addBaseClass()
  {
      var theurl="/kq/team/array/cycle_array_data.do?b_employee=link'object_flag=0";
      if($URL)
          theurl = $URL.encode(theurl);
      var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
      var return_vo= window.showModalDialog(iframe_url,0, 
       "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
      if(return_vo!=null&&return_vo.length>0)
      {
          var hashvo=new ParameterSet();
          hashvo.setValue("object_str",return_vo);
          hashvo.setValue("object_flag","0");	 
          var request=new Request({method:'post',asynchronous:true,onSuccess:addSetRightFields,functionId:'15221200006'},hashvo);
      }
    }
function addShiftClass()
{
    var theurl="/kq/team/array/cycle_array_class.do?b_query=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
    var return_vo= window.showModalDialog(iframe_url,0, 
      "dialogWidth:500px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");
    if(return_vo!=null&&return_vo.length>0)
    {
        var hashvo=new ParameterSet();
        hashvo.setValue("addclass",return_vo);
        hashvo.setValue("type","relief");
        if(shift_class_list!=null&&shift_class_list.length>0)
        {
            hashvo.setValue("shift_class_list",shift_class_list);
        }	 
        var request=new Request({method:'post',asynchronous:true,onSuccess:addSetLeftList,functionId:'15221200003'},hashvo);
    }
}    
function addSetLeftList(outparamters)
{
  
  var shift_class_list=outparamters.getValue("shift_class_list");
  if(shift_class_list==null||shift_class_list.length<=0)
     return false;
  for(var s=0;s<shift_class_list.length;s++)
  {
       var table=document.getElementById('tbl');
       if(table==null)
  	  return false;
        var td_num=table.rows.length;
  	var rowCount=table.rows.length;	  
	var tRow = table.insertRow(rowCount);	
	var r_name=shift_class_list[s].dataName; 
	var r_id=shift_class_list[s].dataValue;	
	var hidd_id="<input type='hidden' name='relief_id' value="+r_id+">";
	var cell_0="<TD width='150' height='20'>&nbsp;"+r_name+""+hidd_id+"&nbsp;</TD>";
	var text_xl="BORDER-RIGHT: none;BORDER-TOP: none;BORDER-LEFT: none;BORDER-BOTTOM: none;BACKGROUND-COLOR: #ffffff;";
	var cell_1="<TD vAlign='center' align='right' width='50'><input type='text' radix='200' name='relief_day' id='text_"+td_num+"' size='4' maxlength='4' value='1' style='"+text_xl+"' onkeypress='event.returnValue=IsDigit();' onchange='checkvalue(this);'></TD>";
	var cell_2="<TD vAlign='center' width='20'><div><button class='m_arrow' onmouseup=IsInputValue('text_"+td_num+"');>5</button></div>";
  	cell_2=cell_2+"<div><button class='m_arrow' onmouseup=IsInputValue('text_"+td_num+"');>6</button></div></TD>";
	for (i=0;i<3;i++)
        { 
             var newCell=tRow.insertCell(i);
             
             switch (i) 
             { 
               case 0 : newCell.innerHTML=cell_0;newCell.width="150";newCell.height="25";break; 
               case 1 : newCell.innerHTML=cell_1; break; 
               case 2 : newCell.innerHTML=cell_2; break; 
             } 
         }
  }   	  
  init_event();    
} 
function checkvalue(obj){
	var value = obj.value;
	if(value == "0"){
		alert("循环天数需大于0！");
		document.getElementById(obj.id).value="";
	}
}
function reliefShift()
    {
       
       setselectitem('right_fields');
       var table=document.getElementById('tbl');
       var left_fields=table.getElementsByTagName('tr');        
       var right_fields=$F('right_fields');      
       if(left_fields.length==0)
       {
         alert("倒班的对应班次不能为空!");
         return false;
       }
       if(right_fields.length==0)
       {
         alert("排班对象不能为空!");
         return false;
       }
       var start_day=$F('start_date');
       var end_day=$F('end_date');
       if(start_day=="")
       {
          alert("开始日期时间不能为空！");
          return false;
       }
       if(end_day=="")
       {
          alert("结束日期时间不能为空！");
          return false;
       }
       if(!isDate(start_day,"yyyy-MM-dd"))
       {
           alert("开始日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           return false;
       }
       if(!isDate(end_day,"yyyy-MM-dd"))
       {
           alert("结束日期时间格式不正确,请输入正确的时间格式！\nyyyy-MM-dd");
           return false;
       }
         
        var c="起始时间不能大于终止时间！";           
        if(start_day>end_day)
        {
               alert(c);
               return false;
        }
        document.getElementById("shiftSubmit").disabled = "disabled";
        
        var hashvo=new ParameterSet();
  		hashvo.setValue("z1",start_day);
  		hashvo.setValue("z1str","开始日期");
  		hashvo.setValue("z3",end_day);
  		hashvo.setValue("z3str","结束日期");
  		hashvo.setValue("right_fields",right_fields);
   		var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
   		     
             
    }
    
     function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			kqClassArrayForm.action="/kq/team/array/relief_array_data.do?b_shift=link";
      //kqClassArrayForm.target="mil_body";
      	kqClassArrayForm.target="_self";
       kqClassArrayForm.submit();
       var thevo=new Object();
       thevo.flag="true";
       window.returnValue=thevo;
       //window.close(); 
       var waitInfo=eval("wait");	   
	     waitInfo.style.display="block"; 
   		} else {
   			alert(resultStr);
   			document.getElementById("shiftSubmit").disabled = "";
   		} 
   }
</script>
<html:form action="/kq/team/array/relief_array_data">
<div id='wait' style='position:absolute;top:40%;left:50%;display:none;margin-left:-200px; '>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style common_background_color" height=24>正排班,请不要刷新页面</td>
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
<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
	    &nbsp; <bean:message key="kq.shift.relief.shift"/>&nbsp;&nbsp;
		<html:hidden name="kqClassArrayForm" property="a_code" styleClass="text"/> 
                <html:hidden name="kqClassArrayForm" property="nbase" styleClass="text"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" valign="Top" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                       <td align="left">
			<table cellSpacing=0 cellPadding=0 width="100%" border=0> 
			<thead>
                         <tr>
                           <TD align="left" width="120" valign="top" height="20">&nbsp;&nbsp;<bean:message key="kq.shift.relief.name"/>&nbsp;</TD>
                           <TD align="left" width="50"  height="20"><bean:message key="kq.shift.relief.day"/></TD>
                         </tr>
                        </thead>
                       </table>
                      </td>
                   </tr>                    
                   <tr>
                       <td align="center">
                        <div id="tbl_container"  class="div2 common_border_color" >
						   <TABLE cellSpacing=0 cellPadding=0 width="100%" border=0 id="tbl">
                             <TBODY>     
                             
							</TBODY> 
                          </table>
						</div>
                       </td>
                    </tr>
			<tr>
                        <td align="center" style="padding-top: 5px">
                          <input type="button" class="mybutton" name="Submit" value="<bean:message key="kq.shift.cycle.add"/>" onClick="addShiftClass();">&nbsp;&nbsp;&nbsp;
                          <input type="button" class="mybutton" name="Submit2" value="<bean:message key="kq.shift.cycle.del"/>" onclick="deleteTR(obj_tr);">&nbsp;
                         </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <input type="button" name="b_addfield" value="<bean:message key="kq.shift.cycle.up"/>"  class="mybutton" onclick="updiv(obj_tr);">
	           <br>
	           <br>
	           <input type="button" name="b_delfield" value="<bean:message key="kq.shift.cycle.down"/>" class="mybutton" onclick="downdiv(obj_tr)">	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                        <td width="100%" align="left"> <bean:message key="kq.shift.cycle.object"/>&nbsp;&nbsp; </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <select name="right_fields" id="r_f"multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
                            
                     </select>
                  </td>
                  </tr>
	          <tr>
                    <td width="100%" align="center">                         
                        <!--<input type="button" name="Submit4" value="<bean:message key="kq.shift.cycle.add"/>" class="mybutton" onclick="showObjectSelectBox(this);" onblur="Element.hide('object_panel');" >-->
                        <hrms:priv func_id="27070201,0C350201">
                        <input type="button" name="Submit4" value="增加班组" class="mybutton" onclick="addShiftGroup();" >&nbsp;
                        </hrms:priv>
                        <hrms:priv func_id="27070202,0C350202">
                        <input type="button" name="Submit5" value="增加人员" class="mybutton" onclick="addShiftEmployee();" >&nbsp;
                        </hrms:priv>
                         <input type="button" name="Submit2" value="<bean:message key="kq.shift.cycle.del"/>" class="mybutton" onclick="removeitem('right_fields');">&nbsp;
                    </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
                  <input type="button" name="b_addfield" value="<bean:message key="kq.shift.cycle.up"/>"  class="mybutton" onclick="upItem($('right_fields'))">
	           <br>
	           <br>
	           <input type="button" name="b_delfield" value="<bean:message key="kq.shift.cycle.down"/>" class="mybutton" onclick="downItem($('right_fields'))">	
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
             <tr>
            <td align="center" class="RecordRow" nowrap><table width="95%" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td width="20%" align="right"><bean:message key="kq.shift.cycle.date"/>&nbsp;</td>
                  <td width="20%">
                  <html:text name="kqClassArrayForm" property='start_date'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
                  </td>
                  <td>&nbsp;&nbsp;<bean:message key="kq.shift.cycle.dateto"/></td>
                  <td>
                  <html:text name="kqClassArrayForm" property='end_date'  size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
                  </td>
                </tr>
              </table> </td>
          </tr>  
		   <tr>
            <td align="center" class="RecordRow" nowrap><table width="90%" border="0" cellpadding="0" cellspacing="0">
                <tr> 
                  <td width="20%" align="right"><bean:message key="kq.shift.rest_postpone"/>&nbsp;</td>
                  <td align="left"><html:multibox name="kqClassArrayForm" property="rest_postpone" value="1"/>
                  </td>
                  <td width="20%" align="right"><bean:message key="kq.shift.feast_postpone"/>&nbsp; </td>
                  <td align="left"> 
                    <html:multibox name="kqClassArrayForm" property="feast_postpone" value="1"/></td>
                </tr>
              </table> </td>
          </tr>  
          <tr>
          <td align="center" class="RecordRow" nowrap style="height:35px;border: none">
          <input id="shiftSubmit" type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="reliefShift();" class="mybutton">						      &nbsp;&nbsp;&nbsp;&nbsp;
               <input type="button" name="b_next" value="<bean:message key="button.cancel"/>" onclick="window.close();" class="mybutton">	      	       
          </td>
          </tr>   
</table>

    <div id="object_panel">
                       <html:hidden name="kqClassArrayForm" property="selected_object" styleClass="text"/> 
   			<select name="object_flag" multiple="multiple" size="2"  style="width:80" onchange="setSelectValue();">    
			    <option value="1">&nbsp;<bean:message key="kq.shift.cycle.group"/>&nbsp;</option>
			    <option value="0">&nbsp;<bean:message key="kq.shift.cycle.employee"/>&nbsp;</option>			    		    			    		    
                        </select>
     </div>


<script language='javascript'>
	Element.hide('object_panel');  

	
    var date_desc;	
   function showObjectSelectBox(srcobj)
   {
          date_desc=srcobj;
          Element.show('object_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('object_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
		var obj_vos=document.getElementsByName("object_flag");
		if(obj_vos!=null)
		{
		   var select_vos=obj_vos[0];
		   if(select_vos.options.length>0)
		   {
		    select_vos.options[0].selected=false;
		    select_vos.options[1].selected=false; 
		  }
		}
  	  
          }                 
       
   }
	

	function setSelectValue()
  	 {
	     if(date_desc)
	     {
	       var object_flag=$F('object_flag');
	       if(object_flag=="1")
	       {
	          addShiftGroup();
	          
	       }else if(object_flag=="0")
	       {
	         addShiftEmployee();
	       }
	       Element.hide('object_panel');   
	     }
     }
</script>
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
  init_event();

</script>