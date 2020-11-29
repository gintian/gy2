<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<style type="text/css">
.RecordRowC {
	border: inset 1px #94B6E6;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}
</style>
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
	width: 40px;
	height: 20px;
	background-color: transparent;
	overflow: hidden;
	text-align: right;	
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
	width: 18px;
	height: 14px;
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
.unnamed2 {
	border: 1px solid #666666;
	background-color: #FFFFFF;
}
</style>
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
   var flag_biaozhi = 0;
  function save_makeup()
   {
	var hashvo=new ParameterSet();	
	var singin_flag="";
	var _singin_flag = document.getElementsByName("singin_flag");
	if(_singin_flag.length==0){
		alert("很抱歉您没有补签申请权限，不能补签！");
		return;
	}
	for(var i=0;i<_singin_flag.length;i++)
	{
		if(_singin_flag[i].checked)
		{
			singin_flag=_singin_flag[i].value;
		}
	}
	if(singin_flag==""){
		alert("请您选择补签申请类型！");
		return;
	}
	var makeup_date_o=document.getElementById("makeup_date"); 
	if(makeup_date_o.value=="")
	 {
	   alert("补签日期不能为空");
	   makeup_date_o.focus();
	   return false;
	 }
	 var makeup_date=makeup_date_o.value;
	 makeup_date=makeup_date.replace(".","-");
	 makeup_date=makeup_date.replace(".","-");	
	 if(!checkDateTime(makeup_date))
	 {
	    alert("补签日期格式不正确!");
	    return false;
	 }
	 var up_hh_o=document.getElementById("up_hh"); 
	 var up_hh=up_hh_o.value;	
	 if(up_hh_o.value=="")
	 {
	    alert("补签小时不能为空!");
	    up_hh_o.focus();
	    return false;
	 }else
	 {
	    if(up_hh.length == 1){
	    	up_hh = "0" + up_hh;
	    	document.getElementById("up_hh").value = up_hh
	    }
	    var i = parseInt(up_hh,10);
	    if(i>=24)
	    {
	       alert("补签小时数值不能大于或等于24!");
	       up_hh_o.focus();
	       return false;
	    }
	 }  
	 var up_mm_o=document.getElementById("up_mm"); 
	 var up_mm=up_mm_o.value;
	 if(up_mm_o.value=="")
	 {
	    alert("补签分钟不能为空!");
	    up_mm_o.focus();
	    return false;
	 }else
	 {
	    var i = parseInt(up_mm,10);
	    if(i>=60)
	    {
	       alert("补签小时数值不能大于或等于60!");
	       up_mm_o.focus();
	       return false;
	    }
	 } 
	 if(!isDate(up_hh+":"+up_mm,"HH:mm"))
     {
                alert("补签时间格式不正确,请输入正确的时间格式！\nHH:mm");
                return false;
     }
     if(!isDate(makeup_date,"yyyy-MM-dd"))
       {
                alert("补签日期格式不正确,请输入正确的日期格式！\nyyyy-MM-dd");
                return false;
       }
	 if($F('oper_cause')=="")
	  {
	    alert("补刷原因不能为空!");
	    return false;
	  }
	 var ip_addr=getLocalIPAddressf();
	 var makeup_time=up_hh+":"+up_mm;		
	 hashvo.setValue("oper_cause",$F('oper_cause'));
	 hashvo.setValue("singin_flag",singin_flag);
	 hashvo.setValue("makeup_date",makeup_date);
	 hashvo.setValue("makeup_time",makeup_time);	
	 hashvo.setValue("a0100","${netSigninForm.a0100sign}");
	 hashvo.setValue("nbase","${netSigninForm.dbsign}");
	 hashvo.setValue("ip_addr",ip_addr);
	 hashvo.setValue("z1",makeup_date);
	 hashvo.setValue("z1str","补签日期");
	 var request=new Request({asynchronous:false,onSuccess:returnResult,functionId:'1510010055'},hashvo);
	 
	 if (flag_biaozhi == 1) {
	 	var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15221400010'},hashvo);
	 }
   }
   
    function returnResult(outparamters) {
		var resultStr = outparamters.getValue("resultStr");
		resultStr = getDecodeStr(resultStr)
   		if (resultStr == "ok") {
   			flag_biaozhi = 1;
   		} else {
   			flag_biaozhi = 0;
   			alert(resultStr);
   		} 
   }
   function showReturn(outparamters)
   {
      var mess=outparamters.getValue("mess");
      alert(mess);
      netSigninForm.action="/kq/machine/netsignin/signinlist.do?b_self=link";
      netSigninForm.submit();
   } 
   function IsDigit() 
    { 
       return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    }  
</script>
<script language="javascript">  
	this.fObj = null;
	var time_r=0;
	function setFocusObj(obj,time_vv) {		
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
         function gback()
         {
            netSigninForm.action="/kq/machine/netsignin/signinlist.do?b_self=link";
            netSigninForm.submit();
         }
   /**取得本地机器ip地址*/
function getLocalIPAddressf()
{
    var obj = null;
    var rslt = "";   
    try
    {
        obj=document.getElementById('SetIE');
        	rslt = obj.GetIP();
        obj = null;
    }
    catch(e)
    {
    	//异常发生
    }
    return rslt;
}
	function splitLeng(){
		var o = document.getElementById('oper_cause');
		var str = o.value;
		if(str.length <= 25){ 
			return; 
		}
		var strMaxLeng = 50;
		var tempStr = "";
		var strLeng = 0;
		
		for(var i = 0;i < str.length;i++){
			if(str.charCodeAt(i)>255) {
				strLeng += 2;
			}else{
				strLeng++;
			}
			if(strLeng > 50){
				o.value = tempStr;
			}else{
				tempStr += str.charAt(i);
			}
		}
	}
</script>
<html:form action="/kq/machine/netsignin/makeupsign">
  <br>
  <br>
  <html:hidden styleId="dbsign" name="netSigninForm" property="dbsign"/>
  <html:hidden styleId="a0100sign" name="netSigninForm" property="a0100sign"/>
   <table width="60%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" colspan="2" nowrap style= "border-bottom:1">
					&nbsp;&nbsp;&nbsp;补签申请&nbsp;
				</td>
			</tr>
		</thead>		
		<tr>
		     	<td width="20%" height="30" align="center" class="RecordRow" nowrap style= "border-bottom:1" style="border-right:1" >
		        申请日期
		      </td>
		    <td  class="RecordRow" nowrap style= "border-bottom:1" >			        
		          <html:text name="netSigninForm" property='makeup_date' styleId="makeup_date" size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
		      </td>  
	        </tr>
	        <tr>
	           <td height="30" align="center" class="RecordRow" nowrap style= "border-bottom:1" style="border-right:1">
		        申请时间
		      </td>
		     	      <td  class="RecordRow" nowrap style= "border-bottom:1">
		        <table border="0" cellspacing="0" cellpadding="0">
		                 <tr>
		                   <td width="40" nowrap> 
		                    <div <div class="m_frameborder inputtext">
		                    <input type="text" class="m_input" maxlength="2" name="up_hh" id="hh" value="00" onfocus="setFocusObj(this,24);" onkeypress="event.returnValue=IsDigit();"><font color="#000000"><strong>:</strong></font><input type="text" class="m_input" maxlength="2" name="up_mm" id="mm" value="00" onfocus="setFocusObj(this,60);" onkeypress="event.returnValue=IsDigit();">
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
	        </tr>
	        <hrms:priv func_id="0C3460,0C3461">
	        <tr>
		    <td height="30" align="center" class="RecordRow" nowrap style= "border-bottom:1" style="border-right:1">
		        出入类型
		      </td>
		    <td  class="RecordRow" nowrap style= "border-bottom:1">
		      <hrms:priv func_id="0C3460">
		        <html:radio name="netSigninForm" property="singin_flag" value="0"/>补签到
		        </hrms:priv>
		        <hrms:priv func_id="0C3461">
		        <html:radio name="netSigninForm" property="singin_flag" value="1"/>补签退		
		        </hrms:priv>       
		      </td>  
	        </tr>
	        </hrms:priv>
	        <tr>
		   <td  height="30" align="center" class="RecordRow" nowrap style="border-right:1">
		        补刷原因
		      </td>
  			<td  class="RecordRow" nowrap >
		       <logic:equal name="netSigninForm" property="card_causation" value="">
                         <textarea name="oper_cause" cols="45" rows="4"
						onpropertychange="splitLeng();"></textarea>
               </logic:equal>
               <logic:notEqual name="netSigninForm" property="card_causation" value=""> 
		        <html:text name="netSigninForm" property='oper_cause' size="20"   readonly="true"/> 
                <input type="hidden" name="oper_cause_hidden" value="1"> 
                <img  src="/images/code.gif" onclick='javascript:openInputCodeDialogText("${netSigninForm.card_causation}","oper_cause","oper_cause_hidden");'/>     
		       </logic:notEqual>
		      </td>  
	        </tr>
	        <tr>
	         <td nowrap align="center" colspan="2">
		     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="save_makeup()">&nbsp;
		      <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="gback();">&nbsp;
		   </td>
		</tr>
		
	</table>
</html:form>
 <div id='axc' style='display:none'/>
 
 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }
 InitAx();
 </script>

