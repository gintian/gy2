<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/kq/kq.js"></script>
<script type="text/javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script type="text/javascript" src="/ext/ext-all.js"></script>
<link rel='stylesheet' href='/ext/resources/css/ext-all.css' type='text/css' />
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
	width: 42px;
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
   var kqempcal = "${baseNetSignInForm.kqempcal}";
   
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

   var ip_adr="";
  function save_makeup(z5)
   {
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
	  if(!isDate(makeup_date,"yyyy-MM-dd"))
       {
                alert("补签日期格式不正确,请输入正确的日期格式！\nyyyy-MM-dd");
                return false;
       }
	  if(trim($F('oper_cause'))=="")
	  {
	    alert("补刷原因不能为空!");
	    return false;
	  }else if(IsOverStrLength(trim($F('oper_cause')), 250))
      {
          alert("补刷卡原因字数不能超过250个英文字符或125个汉字！");
          return false;
      }
	  
	  var vo_obj= document.getElementById('makeup_time');
	  var cardTime = Ext.getCmp('cardtime_field').getValue();
      var cardTimeFormat = Ext.Date.format(cardTime,'H:i');

      if(cardTimeFormat=="")
      {
          alert("补刷时间不能为空！");
          return false;
      }
      if(!isDate(cardTimeFormat,"HH:mm"))
      {
               alert("补签时间格式不正确,请输入正确的时间格式！\nHH:mm");
               return false;
      }
      vo_obj.value=cardTimeFormat;
          
   	  ip_adr=getLocalIPAddressf();  	 
      if(ip_adr=="")
      {
          alert("无法得到客户端的计算机IP，请重新打开该页面，允许Active控件交互！");
          return false;
      }

      //保存
      if ('01'==z5) {
     	 if(confirm("确定补刷吗？"))
         {
             if(kqempcal == "1"){
            	 var obj = new ParameterSet();
                 obj.setValue("ip_adr",ip_adr);
                 obj.setValue("z5","01");
                 obj.setValue("makeup_date",document.getElementById('makeup_date').value);
                 obj.setValue("makeup_time",document.getElementById('makeup_time').value);
                 obj.setValue("oper_cause",getEncodeStr(document.getElementById('oper_cause').value));
                 obj.setValue("inout_flag","${baseNetSignInForm.inout_flag}");
                 
                 var request=new Request({method:'post',onSuccess:parent.empcal_me.cardClose,functionId:'15502110211'},obj);
             }else{
                 baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_makesave=link&ip_adr="+ip_adr+"&z5=01";
                 baseNetSignInForm.submit();
             }
         }
      } else { //报批
     	 var request=new Request({method:'post',asynchronous:false,onSuccess:setapprove,functionId:'15502110214'});
      }
   }    
  
 function setapprove(outparamters){
      
      var apppeo = outparamters.getValue("apppeo");
      if(apppeo.length <= 1){
        if(apppeo.length == 0){
            var app_account = "null";
            if(confirm("确定要报批吗？"))
              {
            	if(kqempcal == "1"){
                    var obj = new ParameterSet();
                    obj.setValue("ip_adr",ip_adr);
                    obj.setValue("z5","02");
                    obj.setValue("app_account",getEncodeStr(app_account));
                    obj.setValue("makeup_date",document.getElementById('makeup_date').value);
                    obj.setValue("makeup_time",document.getElementById('makeup_time').value);
                    obj.setValue("oper_cause",getEncodeStr(document.getElementById('oper_cause').value));
                    obj.setValue("inout_flag","${baseNetSignInForm.inout_flag}");
                    
                    var request=new Request({method:'post',onSuccess:parent.empcal_me.cardClose,functionId:'15502110211'},obj);
                }else{
                	baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_makesave=link&ip_adr="+ip_adr+"&z5=02&account="+getEncodeStr(app_account);
                	baseNetSignInForm.submit();
                }
              } 
        }else{
          var list = apppeo[0];
          var array = list.split(",");
          var app_account = array[2];
          
          if(confirm("确定要报批吗？"))
              {  
        	     if(kqempcal == "1"){
	                  var obj = new ParameterSet();
	                  obj.setValue("ip_adr",ip_adr);
	                  obj.setValue("z5","02");
	                  obj.setValue("app_account",getEncodeStr(app_account));
	                  obj.setValue("makeup_date",document.getElementById('makeup_date').value);
	                  obj.setValue("makeup_time",document.getElementById('makeup_time').value);
	                  obj.setValue("oper_cause",getEncodeStr(document.getElementById('oper_cause').value));
	                  obj.setValue("inout_flag","${baseNetSignInForm.inout_flag}");
	                  
	                  var request=new Request({method:'post',onSuccess:parent.empcal_me.cardClose,functionId:'15502110211'},obj);
                  }else{
	                 baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_makesave=link&ip_adr="+ip_adr+"&z5=02&account=" + getEncodeStr(app_account);
	                 baseNetSignInForm.submit();
                 }
              } 
        }
      }else{
        var dh="360px";
        var wh="360px";
        var target_url="";
        var return_vo;
        var str_app="";
        for(var i=0;i<apppeo.length;i++){
            
            var list = apppeo[i].split(",");
            for(var j=0;j<list.length;j++){
                str_app = str_app + list[j]+"`";
            }
            str_app = str_app + "~";
        }
        target_url = "/kq/kqself/card/carddata.do?b_selapp=link&str_app="+str_app;
        return_vo = window.showModalDialog(target_url,1,
        "dialogWidth:"+wh+";dialogHeight:"+dh+";resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        if(return_vo){
            var app_account = return_vo.app_account;
            if(confirm("确定要报批吗？"))
              {
            	if(kqempcal == "1"){
                    var obj = new ParameterSet();
                    obj.setValue("ip_adr",ip_adr);
                    obj.setValue("z5","02");
                    obj.setValue("app_account",getEncodeStr(app_account));
                    obj.setValue("makeup_date",document.getElementById('makeup_date').value);
                    obj.setValue("makeup_time",document.getElementById('makeup_time').value);
                    obj.setValue("oper_cause",getEncodeStr(document.getElementById('oper_cause').value));
                    obj.setValue("inout_flag","${baseNetSignInForm.inout_flag}");
                    
                    var request=new Request({method:'post',onSuccess:parent.empcal_me.cardClose,functionId:'15502110211'},obj);
                }else{
	                 baseNetSignInForm.action="/kq/kqself/card/carddata.do?b_makesave=link&ip_adr="+ip_adr+"&z5=02&account=" + getEncodeStr(app_account);
	                 baseNetSignInForm.submit();
                }
              } 
         }
      }
   } 

   function IsDigit() 
    { 
       return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
    } 
   
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
    		alert("获取IP地址失败!");
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
    <logic:notEqual name="baseNetSignInForm" property="kqempcal" value="1">
        <table width="60%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
    </logic:notEqual>
    <logic:equal name="baseNetSignInForm" property="kqempcal" value="1">
        <table width="90%" border="0" cellspacing="1" align="center" cellpadding="1" class="ListTable">
    </logic:equal>
		<thead>
			<tr>
				<td align="center" class="TableRow" colspan="2" nowrap style= "border-bottom:1">
					&nbsp;补签&nbsp;
				</td>
			</tr>
		</thead>
		<tr>
		      <td width="20%" height="30" align="center" class="RecordRow" nowrap style= "border-bottom:1" style="border-right:1" >
		        日期
		      </td>
		      <td  class="RecordRow" nowrap style= "border-bottom:1" >		        
		          <input type="text" name="makeup_date"  size="12" readonly="readonly" value="${baseNetSignInForm.makeup_date}" class="inputtext" style="width:100px;font-size:10pt;text-align:left" id="makeup_date"  onclick='getKqCalendarVar();popUpCalendar(this,this,weeks,feasts,turn_dates,week_dates,false);' onchange="rep_dateValue(this);">
		      </td>  
	        </tr>
	        <tr>
		      <td height="30" align="center" class="RecordRow" nowrap style= "border-bottom:1" style="border-right:1">
		        时间<html:hidden name="baseNetSignInForm" styleId="makeup_time" property="makeup_time" styleClass="text"/> 
		      </td>
		      <td  class="RecordRow" nowrap style= "border-bottom:1">
		          <div id="cardtime"></div>
		      </td>  
	        </tr>
	        <logic:equal name="baseNetSignInForm" property="isInout_flag" value="true">
	        <tr>
		      <td  height="30" align="center" class="RecordRow" style="border-bottom: none;border-right:none;" nowrap >
		        出入类型
		      </td>
		      <td  class="RecordRow"  style="border-bottom: none;" nowrap >
		        <html:select name="baseNetSignInForm" property="inout_flag" size="1" >
                      <html:option value="-1">出</html:option>
                      <html:option value="0">不限</html:option>
                      <html:option value="1">进</html:option>
                      </html:select>       
		      </td>  
	        </tr>
	        </logic:equal>
	        <tr>
		      <td  height="30" align="center" class="RecordRow" nowrap style="border-right:1">
		      	<table>
		      		<tr>
		      			<td>
		        			补刷原因
		      			</td>
		      		</tr>
		        </table>			    
		      </td>
		      <td  class="RecordRow" nowrap >
			      <span style="float:left">	
				      <logic:equal name="baseNetSignInForm" property="card_causation" value="">
							<textarea id="oper_cause" name="oper_cause" cols="45" rows="4" ></textarea>
						</logic:equal>
		               <logic:notEqual name="baseNetSignInForm" property="card_causation" value=""> 
				        <html:text styleClass="inputtext" name="baseNetSignInForm" property='oper_cause' size="20" styleId="oper_cause" readonly="true"/> 
		                <input type="hidden" name="oper_cause_hidden" value="1"> 
		                <img  src="/images/code.gif" onclick='javascript:openInputCodeDialogText("${baseNetSignInForm.card_causation}","oper_cause","oper_cause_hidden");'/>     
				       </logic:notEqual>
			       </span>
	           <logic:notEqual name="baseNetSignInForm" property="kqempcal" value="1">
			       <span style="float:center">
			       		<div id='setdiv' style="display:block"><font color="red" style="center">*</font></div>
			       </span>
		       </logic:notEqual>
		      </td>  
	        </tr>
	        <tr>
	        <td nowrap colspan="2" align="center" style="height:35px;">
	            <logic:notEqual name="baseNetSignInForm" property="kqempcal" value="1">
		     	    <input type="button" name="btnreturn" value='<bean:message key="button.save"/>' class="mybutton" onclick="save_makeup('01')">&nbsp;
		     	</logic:notEqual>
		     	<input type="button" name="btnreturn" value='<bean:message key="button.appeal"/>' class="mybutton" onclick="save_makeup('02')">&nbsp;
	     	    <logic:notEqual name="baseNetSignInForm" property="kqempcal" value="1">
		          <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="history.back();">&nbsp;
	            </logic:notEqual>
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
 createTimeField('cardtime_field', 'cardtime', document.getElementById('makeup_time').value);
 </script>



