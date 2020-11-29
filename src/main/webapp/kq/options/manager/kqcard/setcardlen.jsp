<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/function.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<style type="text/css">
body {
	background-color: transparent;
	margin:0px;
}
.m_frameborder {
	border-left: 1px inset #D4D0C8;
	border-top: 1px inset #D4D0C8;
	border-right: 1px inset #FFFFFF;
	border-bottom: 1px inset #FFFFFF;
	width: 40px;
	height: 19px;
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
	width: 18px;
	height: 14px;
	border: 0px solid black;
	font-family: "Tahoma";
	font-size: 9px;
	text-align: right;
}
input{
	background-color:transparent;
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

   function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
 function IsInputValue(textid) 
 {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if (!fObj) return;	
		var cmd = event.srcElement.innerText=="5"?true:false;
		var i = parseInt(fObj.value,10);
		if (isNaN(i))
			i = 0;

		var radix = 21-1;		
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
function saveCard()
{
       var fObj=document.getElementById("id_len");
       var len=fObj.value;
       if(len=="")
         len=0;
       if (!checkNUM1(fObj))
       {
          return;
       }
       
       if(parseInt(len)>20)
       {
          alert("卡号长度不得超过20位!");
          return;
       }
       
       kqCardForm.action="/kq/options/manager/cardlen.do?b_save=link";
       kqCardForm.submit();
       window.close(); 
}
</script>
<html:form action="/kq/options/manager/cardlen">
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" >
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
	     &nbsp;<bean:message key="kq.set.card.name"/>&nbsp;&nbsp;	    
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td width="100%" class="framestyle9">
   	     <table width="85%" border="0" cellspacing="1"  align="center" cellpadding="1"> 
   	       <tr>
   	         <td>
   	          &nbsp;&nbsp;
   	         </td>
   	         <td>   	         
   	         </td>  	        
   	       </tr>   	      
   	       <tr>
   	         <td height="30" width="30%">
   	          <bean:message key="kq.card.len"/>
   	         </td>
   	         <td align="left">
   	         <table border="0" cellspacing="0" cellpadding="0">
   	         <tr>
   	             <td align="left" valign="middle"> 
                         <html:text name="kqCardForm" styleId='id_len' 
                          property="id_len" size="4"  styleClass="text4" 
                         onkeypress="event.returnValue=IsDigit();"
                         onkeyup="checkNUM2(this,2,0);"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('id_len');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('id_len');">6</button></td></tr>
                        </table>
                      </td>
                   </tr>   
   	         </table>   	          
   	         </td>
   	       </tr>
   	     </table>
   	   </td>
   	  </tr>
   	  <tr>
   	   <td align="center"  nowrap style="height:35px;border:none"> 
   	     <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' onclick="saveCard();" class="mybutton">						      
             <input type="button" name="b_next" value="<bean:message key="button.cancel"/>" onclick="window.close();" class="mybutton">	      	       
   	   </td>
   	  </tr>
</table>
</div>
</html:form>
