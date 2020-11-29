<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="JavaScript" src="/js/function.js"></script>

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

</style>
<script language="JavaScript">

    function secBoard(n)
  {

    for(i=0;i<mainTable.tBodies.length;i++)
      mainTable.tBodies[i].style.display="none";
    mainTable.tBodies[n].style.display="block";
       
  }
  
    function disa()
	{
	     document.all.count.value="${kqDurationForm.count}";
	     document.all.count.disabled=true;
	     document.all.qqq.disabled=true;
	     document.all.month.disabled=true;
	     document.all.dat.disabled=true;
	     document.all.one_len.disabled=true;
	 }
	   function disb()
	{
	     document.all.count.value="12";
	     document.all.count.readonly=true;
	     document.all.count.disabled=true;
	     document.all.qqq.disabled=true;
	     document.all.month.disabled=true;
	     document.all.dat.disabled=true;
	     document.all.one_len.disabled=true;
	 }
	   function disc()
	{
	     document.all.count.value="12";
	     document.all.count.disabled=true;
	     document.all.qqq.disabled=false;
	     document.all.month.disabled=false;
	     document.all.dat.disabled=false;
	     document.all.one_len.disabled=false;
	 }
	 
  function disd()
	{
    	    document.all.count.value="12";
    	    document.all.count.disabled=false;
	    document.all.qqq.disabled=true;
	    document.all.month.disabled=true;
	    document.all.dat.disabled=true;
	    document.all.one_len.disabled=true;
	 }
  function IsDigit() 
  { 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
  } 
 function IsInputValue(textid) 
 {	     
        var v_radix=100;
        if(textid=="one_len")
          v_radix=7;
        else if(textid=="dat")
          v_radix=32;
        else if(textid=="month")
          v_radix=13;
	event.cancelBubble = true;
	var fObj=document.getElementById(textid);		
	if (!fObj) return;		
	var cmd = event.srcElement.innerText=="5"?true:false;		
	if(fObj.value=="")
	{
	   fObj.value="1";
	}else
	{
	   var i = parseInt(fObj.value,10);	   
	   if(i>v_radix)
	     i=v_radix-2;
	   if(textid=="one_len"&&i==4&&cmd)
	   {
	      i=5;
	   }else if(textid=="one_len"&&i==6&&!cmd)
	   {
	     i=5;
	   }
	   var radix = v_radix-1;		
	   if (i==radix&&cmd) {
		i = 1;
	   } else if (i==1&&!cmd) {
		i = radix;
	   } else {
		cmd?i++:i--;
	   }		
	   fObj.value = i;
	}
	fObj.select();
} 
function osave()
{	
	if(checkvalue())
	{
		document.kqDurationForm.target='_self';
		validate('R','duration.string(kq_duration)','名称');
		if (document.returnValue && ifqrbc())
		{
			kqDurationForm.action='/kq/options/add_batch_duration.do?b_osave=link';
			kqDurationForm.submit();
		}
	}
}
function oreturn()
{
	kqDurationForm.action='/kq/options/add_batch_duration.do?b_return=link';
	kqDurationForm.submit();
}
function IsDigits() 
{ 	     
	var keyCode = event.keyCode ? event.keyCode : event.which ? event.which : event.charCode;

    return ((keyCode >= 48) && (keyCode <= 57)); 
} 
function checkvalue()
{
	var obj = document.getElementById("count").value;
	if(!checkIsIntNum(obj))
	{
		alert("考勤期间数目需为整数！");
		return false;
	}
	return true;
}
</script>
  
<html:form action="/kq/options/add_batch_duration">
        <table width="50%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:60px;">
	<tr>
	   <td height="20" align="center" class="TableRow" nowrap colspan="4"><bean:message key="kq.deration_details.kqqj"/></td>
        </tr>

         <tr class="trDeep1">  
         	<logic:equal name="kqDurationForm" property="text" value="0" >          
           <td align="right" nowrap valign="center" class="RecordRow">        
              <bean:message key="kq.duration.year"/>     
           </td>
           <td align="left"  nowrap valign="center" colspan="3" class="RecordRow">
           	 <bean:write  name="kqDurationForm" property="kyear" filter="true"/>
           	</td>
           </logic:equal>
           <logic:equal name="kqDurationForm" property="text" value="1">
           	<td align="right" nowrap valign="center" class="RecordRow">        
              <bean:message key="kq.deration_details.kqnd"/>       
           </td>
           <td align="left"  nowrap valign="center" colspan="3" class="RecordRow">
           	  <html:text name="kqDurationForm" property="kyear" maxlength="4" styleClass="text"/>
           </td>
           	</logic:equal>
            
          </tr>
          <tr>
           <td align="right" nowrap valign="center" class="RecordRow">        
              <bean:message key="kq.duration.count"/>          
           </td>
          <td align="left"  nowrap valign="center" colspan="3" class="RecordRow">
           	<html:text  name="kqDurationForm" property="count" maxlength="2" styleId="count" styleClass="text"  onkeypress="event.returnValue=IsDigits();"/>
        
          </td>
        </tr>
   		   
    <TABLE align=center class=main_tab id=mainTable height=240 cellSpacing=0 cellPadding=0 width="50%" border=0 class="ListTable">
 	    <TBODY style="DISPLAY: block">
        <TR>
           <TD vAlign=top align=middle>
        	<table width="50%" border="0" cellspacing="1"  align="center" cellpadding="1">
 		       <tr>
             <td align="center"  nowrap colspan="4" style="height:35px;">
               <hrms:submit styleClass="mybutton" property="b_osave" onclick="document.kqDurationForm.target='_self';validate('R','duration.string(kq_duration)','名称');return (document.returnValue && ifqrbc());">
              		<bean:message key="button.save"/>
	          	</hrms:submit>
         	   <input type="button"  value="<bean:message key="button.sys.cond"/>" class="mybutton" onclick="secBoard(1);" >  
         	    <hrms:submit styleClass="mybutton" property="b_return">
              		<bean:message key="button.return"/>
	         	</hrms:submit>
	           
          </td>
         </tr>    
        </table> 
      </TBODY>
    <TBODY style="DISPLAY: none">
       <TR>
        <TD vAlign=top align=middle>
         <fieldset align="center" style="width:100%;border-top: 0px;">
          <table border="0" cellspacing="0"  align="center" cellpadding="2" >
          <tr>
           	<td align="left" nowrap valign="right">        
             <html:radio name="kqDurationForm" property="radio" value="1" onclick="disa();" /><bean:message key="kq.duration.samen"/>&nbsp;&nbsp;&nbsp;      
           </td>
         </tr>
         <tr>
         <td align="left" nowrap valign="right">        
            <html:radio name="kqDurationForm" property="radio" value="2" onclick="disb();"/><bean:message key="kq.duration.amonth"/>&nbsp;&nbsp;&nbsp;      
          </td>
         </tr>
         <tr>
          <td align="left" nowrap valign="right">        
            <html:radio name="kqDurationForm" property="radio" value="3" onclick="disc();" /><bean:message key="kq.duration.zdjj"/>&nbsp;&nbsp;&nbsp;      
          </td>
        </tr>
        <tr class="list3">
         <td align="left" nowrap valign="right">   
         <table  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td align="right">
                &nbsp;&nbsp;&nbsp;<bean:message key="kq.duration.smonth"/> 
                </td>
               <td>
                 <table border="0" cellspacing="0" cellpadding="0">
   	              <tr>
   	               <td align="left" valign="middle"> 
                         <html:text name="kqDurationForm" styleId='month'  property="month" size="3"  styleClass="inputtext" onkeypress="event.returnValue=IsDigit();" onfocus="this.blur()"/>&nbsp;                       
                      </td>
                       <td valign="middle" align="left">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('month');">5</button></td></tr>
                           <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('month');">6</button></td></tr>
                        </table>
                       </td>
                    </tr>   
   	           </table> 
               </td>
               <td>
                 <bean:message key="kq.duration.yue"/>
               </td>
              <tr>
           </table> 
           </td>
        </tr>
        <tr class="list3">
         <td align="left" nowrap valign="right"> 
         <table  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td align="right">
                &nbsp;&nbsp;&nbsp;<bean:message key="kq.duration.sdat"/>  
                </td>
               <td>
                 <table border="0" cellspacing="0" cellpadding="0">
   	              <tr>
   	               <td align="left" valign="middle"> 
                         <html:text name="kqDurationForm" styleId='dat'  property="dat" size="3"  styleClass="inputtext" 
                         onkeypress="event.returnValue=IsDigit();" onfocus="this.blur()"/>&nbsp;                       
                      </td>
                       <td valign="middle" align="left">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('dat');">5</button></td></tr>
                           <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('dat');">6</button></td></tr>
                        </table>
                       </td>
                    </tr>   
   	           </table> 
               </td>
               <td>
                 <bean:message key="kq.duration.dat"/> 
               </td>
              <tr>
           </table> 
           </td>
        </tr>
        <tr>
         <td align="right" nowrap >         
           <table  border="0" cellspacing="0" cellpadding="0">
             <tr>
               <td align="right">&nbsp;&nbsp; 
                <bean:message key="kq.duration.one_len"/>&nbsp;
                </td>
               <td>
                 <table border="0" cellspacing="0" cellpadding="0">
   	              <tr>
   	               <td align="left" valign="middle"> 
                         <html:text name="kqDurationForm" styleId='one_len'  property="one_len" size="3"  styleClass="inputtext" onkeypress="event.returnValue=IsDigit();" onfocus="this.blur()"/>&nbsp;                       
                      </td>
                       <td valign="middle" align="left">
                          <table border="0" cellspacing="2" cellpadding="0">
                            <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('one_len');">5</button></td></tr>
                           <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('one_len');">6</button></td></tr>
                        </table>
                       </td>
                    </tr>   
   	           </table> 
               </td>
               <td>
                 <bean:message key="kq.duration.entries"/> 
               </td>
              <tr>
           </table> 
            
         </td>
        </tr>
        <tr>
         <td  id="qqq" align="left" nowrap valign="right">        
          <input  type="checkbox" name="box" value="1"> <bean:message key="kq.duration.andy"/>  
         </td>
        </tr>
        <tr>
         <td align="left" nowrap valign="right">        
           <html:radio name="kqDurationForm" property="radio" value="4" onclick="disd();"/><bean:message key="kq.duration.define"/>&nbsp;&nbsp;&nbsp;      
         </td>
                 </tr>
       </table>
      </fieldset>
	   <table border="0" cellspacing="0"  align="center" cellpadding="2" >
		   <tr>
        <td align="center"  nowrap colspan="4">
        	
        		<input type='button' class="mybutton" 
						onclick='osave()'
						value='<bean:message key="button.save"/>' />
        	<!-- 
           <hrms:submit styleClass="mybutton" property="b_osave" onclick="document.kqDurationForm.target='_self';validate('R','duration.string(kq_duration)','名称');return (document.returnValue && ifqrbc());">
           	<bean:message key="button.save"/>
	       </hrms:submit>
	        -->
         	<input type="button"  value="<bean:message key="button.sys.cond"/>" class="mybutton" onclick="secBoard(0);">  
         		<!-- 
         	 <hrms:submit styleClass="mybutton" property="b_return">
            <bean:message key="button.return"/>
	       	</hrms:submit>
	       	    -->
	       	  	<input type='button' class="mybutton" 
						onclick='oreturn()'
						value='<bean:message key="button.return"/>' />  
	       
       </td>
      </tr>
		</table>
    </td>
   </tr>
  </TBODY>
  </table>
</table>
</html:form>
<script language="JavaScript">
disa();
</script>
