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
	 var makeup_time=up_hh+":"+up_mm;
	 var ip_addr="";
	 //补签到，如果IP不绑定则不走取IP的控件
	 if($F('net_sign_check_ip')!="0")
	 {
	 	ip_addr=getLocalIPAddressf();
	 }	
	 //var ip_addr=document.getElementById("ipaddr").value;   	 
     //if(ip_addr==""||ip_addr=="undefined")
     //{
        //ip_addr=getLocalIPAddress();
     //}
     var leng= $F('oper_cause');
     if(leng.length>50)
     {
     	alert("补刷原因长度不能大于50个字节！");
     	return false;
     }
	 hashvo.setValue("oper_cause",$F('oper_cause'));
	 hashvo.setValue("singin_flag",singin_flag);
	 hashvo.setValue("makeup_date",makeup_date);
	 hashvo.setValue("z1str","申请日期");
	 hashvo.setValue("makeup_time",makeup_time);	
	 hashvo.setValue("ip_addr",ip_addr);
	 var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15502110203'},hashvo);
   }
   function showReturn(outparamters)
   {
      	var mess= getDecodeStr(outparamters.getValue("mess"));
      	alert(mess);
   } 
   function IsDigit() 
    { 
       return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
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
            baseNetSignInForm.action="/kq/kqself/net_signin/selfnetsignin.do?b_self=link";
            baseNetSignInForm.submit();
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
<html:form action="/kq/kqself/net_signin/net_signin">
  <br>
  <br>
  <html:hidden name="baseNetSignInForm" property="net_sign_check_ip"/> 
   <table width="60%" border="0" cellspacing="" align="center" cellpadding="" class="ListTableF">
		<thead>
			<tr>
				<td align="left" class="TableRow" colspan="2" nowrap>
					&nbsp;&nbsp;&nbsp;个人补签申请&nbsp;<input type="hidden" name="txtIPAddr" id="ipaddr" value="">
				</td>
			</tr>
		</thead>		
		<tr>
		      <td width="20%" height="30" align="center" class="RecordRow" nowrap >
		        申请日期
		      </td>
		      <td  class="RecordRow" nowrap >		        
		          <html:text name="baseNetSignInForm" property='makeup_date' styleId="makeup_date" size="20" maxlength="20"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' styleClass="TEXT4"/>
		      </td>  
	        </tr>
	        <tr>
		      <td height="30" align="center" class="RecordRow" nowrap >
		        申请时间
		      </td>
		      <td  class="RecordRow" nowrap >
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
	        <hrms:priv func_id="0B401,0B405">
	        <tr>
		      <td  height="30" class="RecordRow" nowrap >
		        
		      </td>
		      <td  class="RecordRow" nowrap >
		      <hrms:priv func_id="0B401">
		        <html:radio name="baseNetSignInForm" property="singin_flag" value="0"/>补签到
		      </hrms:priv>
		      <hrms:priv func_id="0B405">
		        <html:radio name="baseNetSignInForm" property="singin_flag" value="1"/>补签退	
		      </hrms:priv>	       
		      </td>  
	        </tr>
	        </hrms:priv>
	        <tr>
		      <td  height="30" align="center" class="RecordRow" nowrap style="padding: 5px" >
		        补刷原因
		      </td>
		      <td  class="RecordRow" nowrap >
		       <logic:equal name="baseNetSignInForm" property="card_causation" value="">
		            <textarea name="oper_cause" cols="45" rows="4"
						onpropertychange="splitLeng();"></textarea>
               </logic:equal>
               <logic:notEqual name="baseNetSignInForm" property="card_causation" value=""> 
		        <html:text styleClass="inputtext" name="baseNetSignInForm" property='oper_cause' size="20"   readonly="true"/> 
                <input type="hidden" name="oper_cause_hidden" value="1"> 
                <img  src="/images/code.gif" onclick='javascript:openInputCodeDialogText("${baseNetSignInForm.card_causation}","oper_cause","oper_cause_hidden");'/>     
		       </logic:notEqual>
		      </td>  
	        </tr>
	        <tr>
	         <td align="center" nowrap="nowrap" colspan="2" style="padding: 5px">
		     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="save_makeup()">
		      <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="gback();">&nbsp;
		   </td>
		</tr>
		
	</table>
</html:form>
 <div id='axc' style='display:none'/>

<SCRIPT language=JScript event=OnObjectReady(objObject,objAsyncContext) for=foo>
   if(objObject.IPEnabled != null && objObject.IPEnabled != "undefined" && objObject.IPEnabled == true)
   {    
      if(objObject.IPEnabled && objObject.IPAddress(0) != null && objObject.IPAddress(0) != "undefined")
       {
          IPAddr = objObject.IPAddress(0);   
          var obj=document.getElementById("ipaddr");      
          obj.value=IPAddr+"";  
       }       
  }
</SCRIPT>
<OBJECT id=locator classid=CLSID:76A64158-CB41-11D1-8B02-00600806D9B6 VIEWASTEXT></OBJECT>
<OBJECT id=foo classid=CLSID:75718C9A-F029-11d1-A1AC-00C04FB6C223></OBJECT>
<script language="javascript">  
   var service = locator.ConnectServer();   
   var IPAddr ;  
   service.Security_.ImpersonationLevel=3;
   service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration');   
</script>

 <script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
 <script type="text/javascript">
 function InitAx()
 {
     if(!AxManager.setup("axc", "SetIE", 0, 0, InitAx, AxManager.setIEName))
           return;
 }

 InitAx();
 </script>


