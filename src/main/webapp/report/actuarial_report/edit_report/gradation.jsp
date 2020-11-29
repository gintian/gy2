<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>


<script language="javascript">
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
   function base_shift()
   {
       var vo=document.getElementById("gradation");
       var value=vo.value;
       if(value=="")
       {
          if(!confirm("没有输入层级，将默认只显示当前单位！"))
          {
              return false;
          }
       }else
       {
          if(!confirm("确定单位层级显示 "+value+" 级吗？"))
           return false;
       }
       var thevo=new Object();
       thevo.flag="true";
       thevo.value=value;
       window.returnValue=thevo;
       window.close();
       
   }
   function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if (!fObj) return;		
		var cmd = event.srcElement.innerText=="5"?true:false;
		if(fObj.value==null||fObj.value.length<=0)
		  fObj.value=0;
		var i = parseInt(fObj.value,10);
		var radix = 200-1;		
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
</script>
<hrms:themes />
<html:form action="/report/actuarial_report/edit_report/searcheportU02List">
<br><br>
<table width="90%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		
          <td width="130" align=center class="tabcenter">单位显示层级</td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="250"></td>-->
       		 <td align=center class="TableRow">单位显示层级</td>             	      
          </tr> 
          <tr>
            <td class="framestyle9">
  <table width="100%" border="0" align="center"  cellpadding="0" cellspacing="5"  class="DetailTable" cellpmoding="0" >
        <tr>
            <td colspan="3" height="15">
              &nbsp;&nbsp;                   
            </td>
           </tr>
         <tr> 
          <td width="14" align="right" nowrap class="tdFontcolor"></td>
          <td width="100" align="left" nowrap class="tdFontcolor">显示上级层级:</td>
          <td align="left" nowrap class="tdFontcolor"> 
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
                     <tr>  
                      <td valign="middle" align="right"> 
                         <input type="text" name="gradation" id="gradation" size="4" maxlength="5" value="" onkeypress="event.returnValue=IsDigit();">&nbsp;&nbsp;                    
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('gradation');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('gradation');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left">层</td>
                      <td align="left" width='30%'>&nbsp; </td>
                    </tr>
                 </table>
           
           </td>
        </tr>  
        <tr>
            <td colspan="3" height="15">
              &nbsp;&nbsp;                   
            </td>
           </tr>      
      </table>	            	
     </td>
          </tr>
  <tr align="center" class="list3"> 
    <td height="35" align="center" > 
      <input type="button" name="b_shift" value="<bean:message key="button.ok"/>" class="mybutton" onclick="base_shift();">
               <input type="reset" name="bc_clear" value="<bean:message key="button.close"/>" class="mybutton" onclick="window.close();">
    </td>
          </tr>  
  </table>
</html:form>