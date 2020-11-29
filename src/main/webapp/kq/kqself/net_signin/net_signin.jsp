<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>
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
<!--
.sample {
 font-family: arial black;font-size: 20px; font-weight: bold; cursor: hand;
}
-->
</style>
<hrms:priv func_id="0B4"> 
<script language="javascript">
   function netsingin(singin_flag)
   {
    var ip_addr=document.getElementById("ipaddr").value;   
    if(ip_addr==""||ip_addr=="undefined")
    {
        ip_addr=getLocalIPAddress();
    } 
    if(ip_addr=="")
    {
       alert("找不到本地IP，请在Internet选项中对ActiveX配置\r\n对未标记为可安全执行脚本的ActiveX控件初始化并执行-提示");
       return false;
    }
    var hashvo=new ParameterSet();			
    hashvo.setValue("singin_flag",singin_flag);	
    hashvo.setValue("ip_addr",ip_addr);
    var request=new Request({method:'post',asynchronous:false,onSuccess:showReturn,functionId:'15502110200'},hashvo);
   }
   function MusterInitData()
   {
	   var waitInfo=eval("signin_wait");	
	   waitInfo.style.display="none";
	   waitInfo=eval("signout_wait");	
	   waitInfo.style.display="none";
	   waitInfo=eval("signlate_wait");	
	   waitInfo.style.display="none";
   }
   function showReturn(outparamters)
   {
   	  return;//xujian 2009-12-8  去掉下面的签到结果页面
      var mess=outparamters.getValue("mess");
      var signflag=outparamters.getValue("signflag");
      if(signflag=="0")
      {
         showFlash('signin_wait',mess);   //早上 ok        
      }else if(signflag=="1")
      {
         showFlash('signout_wait',mess);   //与我无关
      }else if(signflag=="3")
      {
        showFlash('signlate_wait',mess);  //签到玻璃
      }else
      {
        showFlash('signoth_wait',mess);  //签退成功
        //alert(mess);;
      }      
      //alert(mess);
   }
   function goself()
   {
      baseNetSignInForm.action="/kq/kqself/net_signin/selfnetsignin.do?b_self=link";
      baseNetSignInForm.submit();
   }
   function goSearch()
   {
      baseNetSignInForm.action="/kq/kqself/net_signin/allnet_signin.do?b_search=link&action=allnet_signin_data.do&target=mil_body";
      baseNetSignInForm.submit();
   }
   function showFlash(name,mess)
   {
     var waitInfo=eval(name);	 
     var marquee1="<marquee width:100 height:70 scrollamount='5' class='sample' behavior='alternate'>";
     var marquee2="</marquee>";     
     var text=marquee1+mess+marquee2;     
     if(name=="signin_wait")
	 {
	    closediv("signout_wait");
	    closediv("signoth_wait");
	    closediv("signlate_wait");
	    var obj=document.getElementById("signin_mess");
	    if(obj!=null)
	      obj.innerHTML=text;
	    waitInfo.style.display="block"; 
	 }else if(name=="signout_wait")
	 {
	    closediv("signin_wait");
	    closediv("signoth_wait");
	    closediv("signlate_wait");
	    var obj=document.getElementById("signout_mess");
	    if(obj!=null)
	      obj.innerHTML=text;
	    waitInfo.style.display="block"; 
	 }else if(name=="signlate_wait")
	 {
	    closediv("signin_wait");
	    closediv("signoth_wait");
	    closediv("signout_wait");
	    var obj=document.getElementById("signlate_mess");	   
	    if(obj!=null)
	       obj.innerHTML=text; 
	    waitInfo.style.display="block"; 
	 }else if(name=="signoth_wait")
	 {
	 //有效刷卡时间范围外,签退无效
	     closediv("signin_wait");
	     closediv("signlate_wait");
	     closediv("signout_wait");
	     var obj=document.getElementById("signoth_mess");	   
	     if(obj!=null)
	       obj.innerHTML=text;
	     waitInfo.style.display="block";
	      
	 }
	 	
   }
   function closediv(name)
   {
     var waitInfo=eval(name);	    
	 waitInfo.style.display="none";	 
	 var div_obj=document.getElementById(name);
	 div_obj.innerHTML="";
	 if(name=="signin_wait")
	 {
	   var obj=document.getElementById("signin_mess");
	   if(obj!=null)
	     obj.stop(); 
	 }else if(name=="signout_wait")
	 {
	   var obj=document.getElementById("signout_mess");
	   if(obj!=null)
	     obj.stop(); 
	 }else if(name=="signlate_wait")
	 {
	   var obj=document.getElementById("signlate_mess");	   
	   if(obj!=null)
	      obj.stop(); 
	 }else if(name=="signoth_wait")
	 {
	    var obj=document.getElementById("signoth_mess");	   
	    if(obj!=null)
	    {
	      obj.stop(); 	           	      
	    }
	 }
   }
</script>
<html:form action="/kq/kqself/net_signin/net_signin" onsubmit="return validate()">
  <br>
  <br> <input type="hidden" name="txtIPAddr" id="ipaddr" value="">
   <table width="60%" border="0" cellspacing="" align="center" cellpadding="" class="ListTableF">
		<thead>
			<tr>
				<td align="left" class="TableRow" colspan="2" nowrap>
					&nbsp;&nbsp;&nbsp;网上签到说明&nbsp;
				</td>
			</tr>
		</thead>		
		<tr>
		      <td height="100" valign="top" class="RecordRow" nowrap >
		         <br>
		         说明:<br>
		         1.管理员设置了IP绑定，用户只能在自己的机器上签到、签退！否则无效！
		         <br><br><br><br>
		         <%= new java.util.Date().toLocaleString() %>	
		      </td>
		        
	        </tr>
	        <tr>
	         <td class="RecordRow" nowrap>	    
	               <!--    <hrms:priv func_id="0B401">      
		        <input type="button" name="btnreturn" value='签到' class="mybutton" onclick="netsingin('0')">&nbsp;
			 <input type="button" name="b_qt" class="mybutton" value="签退"  onclick="netsingin('1')">&nbsp;
			 </hrms:priv>-->
			 <input type="button" name="b_sgr" class="mybutton" value="个人明细" onclick="goself();">
			 <hrms:priv func_id="0B402"> 
			 <input type="button" name="b_sgr" class="mybutton" value="审阅" onclick="goSearch();">
		         </hrms:priv>
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
<div id='signin_wait' style='position:absolute;top:180;left:250;display:none;'>
  <table border="0" width="200" cellspacing="0" align="center" cellpadding="0" height="200">
     <tr>             
      <td style="" align=center>        
      <!--<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="signin" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="200" height="200">
          <param name="movie" value="/images/sign_in.swf">
          <param name="quality" value="high">
          <param name="wmode" value="transparent">
          <embed src="/images/sign_in.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="200" height="200">
          </embed>
      </object>-->
      <img src="/images/laugh.gif" border=0>
      <div align="center" id="signin_mess"></div>
      <div align="right"><a href="javascript:closediv('signin_wait')"><font color="red">关闭</font></a></div>  
      </td>
     </tr>       
  </table>
</div> 
<div id='signout_wait' style='position:absolute;top:180;left:250;display:none;'>
  <table border="0" width="200" cellspacing="0" align="center" cellpadding="0" height="200">
     <tr>             
      <td style="" align=center>          
      <!--<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="signout" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="200" height="200">
          <param name="movie" value="/images/sign_out.swf">
          <param name="quality" value="high">
          <param name="wmode" value="transparent">
          <embed src="/images/sign_out.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="200" height="200">
          </embed>
      </object>-->
      <img src="/images/laugh.gif" border=0>
      <div align="center" id="signout_mess"></div>
      <div align="right"><a href="javascript:closediv('signout_wait')"><font color="red">关闭</font></a></div>
      </td>
     </tr>       
  </table>
</div> 
<div id='signlate_wait' style='position:absolute;top:180;left:250;display:none;'>
  <table border="0" width="200" cellspacing="0" align="center" cellpadding="0" height="200">
     <tr>             
      <td style="" align=center>       
      <!--<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="signlate" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="200" height="200">
          <param name="movie" value="/images/sign_late.swf">
          <param name="quality" value="high">
          <param name="wmode" value="transparent">
          <embed src="/images/sign_late.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="200" height="200">
          </embed>
      </object>-->
      <img src="/images/weep.gif" border=0>
      <div align="center" id="signlate_mess"></div>
      <div align="right"><a href="javascript:closediv('signlate_wait')"><font color="red">关闭</font></a></div>  
      </td>
     </tr>       
  </table>
</div> 
<div id='signoth_wait' style='position:absolute;top:180;left:250;display:none;'>
  <table border="0" width="200" cellspacing="0" align="center" cellpadding="0" height="200">
     <tr>             
      <td style="" align=center>   
      <!--<object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" id="signoth" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="200" height="200">
          <param name="movie" value="/images/sign_oth.swf">
          <param name="quality" value="high">
          <param name="wmode" value="transparent">
          <embed src="/images/static.gif" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="200" height="200">
          </embed>
      </object>-->
      <img src="/images/weep.gif" border=0>
      <div align="center" id="signoth_mess"></div>
      <div align="right"><a href="javascript:closediv('signoth_wait')"><font color="red">关闭</font></a></div>  
      </td>
     </tr>       
  </table>
</div> 
<SCRIPT language=JScript event=OnObjectReady(objObject,objAsyncContext) for=foo>
   if(objObject.IPEnabled != null && objObject.IPEnabled != "undefined" && objObject.IPEnabled == true)
   {    
      if(objObject.IPEnabled && objObject.IPAddress(0) != null && objObject.IPAddress(0) != "undefined")
       {
          IPAddr = objObject.IPAddress(0);   
          var obj=document.getElementById("ipaddr");      
          obj.value=IPAddr+"";  
          <%
          String sign=(String)session.getServletContext().getAttribute("sign");           
          if(sign!=null&&sign.equalsIgnoreCase("in"))
          {
               session.getServletContext().setAttribute("sign","");
          %>
               netsingin('0'); 
          <%
          }else if(sign!=null&&sign.equalsIgnoreCase("out"))
          {
          %>
             netsingin('1');             
          <% 
             session.getServletContext().setAttribute("sign","");
          }
          %>          
       }       
  }
</SCRIPT>
<OBJECT id=locator classid=CLSID:76A64158-CB41-11D1-8B02-00600806D9B6 VIEWASTEXT></OBJECT>
<OBJECT id=foo classid=CLSID:75718C9A-F029-11d1-A1AC-00C04FB6C223></OBJECT>
<%
  
  session.getServletContext().setAttribute("sign","");
%>
<script language="javascript">
   MusterInitData();
   var service = locator.ConnectServer();   
   var IPAddr ;  
   service.Security_.ImpersonationLevel=3;
   service.InstancesOfAsync(foo, 'Win32_NetworkAdapterConfiguration');   
</script>
</hrms:priv>	
