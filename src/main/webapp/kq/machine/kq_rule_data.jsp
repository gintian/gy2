<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/numberS.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
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
</style>
<script type="text/javascript">

        function IsDigit() 
        { 
           return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
        } 
        function IsInputValue(textid) {	     
		event.cancelBubble = true;
		var fObj=document.getElementById(textid);		
		if(fObj.disabled==true)
		  return false;		
		if (!fObj) return;
		if(fObj.value=="")
		  fObj.value="0";		
		var cmd = event.srcElement.innerText=="5"?true:false;
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

  function go_edit()
   {
     if(confirm('确认保存吗？'))
     {
       var status=document.getElementById("status"); 
       var status_value="";      
       if(status.checked==true)
       {
          status_value="1";
       }else
       {
          status_value="0";
       }
       kqRuleDataForm.action="/kq/machine/kq_rule_data.do?b_edit=link&tran_flag=1&status_value="+status_value;      
       kqRuleDataForm.submit();
     }
   }
   function no_disabled()
   {
      var status=document.getElementById("status");      
      if(status.checked==true)
      {
         document.getElementById("machine_s").disabled=true;
         document.getElementById("machine_e").disabled=true;
      }else
      {
         document.getElementById("machine_s").disabled=false;
         document.getElementById("machine_e").disabled=false;
      }
   }
 </script>
<html:form action="/kq/machine/kq_rule_data">
<br>
<br>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
          <tr> 
            
    <td align="left" valign="top" nowrap> 
      <table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr height="20"> 
          <!--  <td width="10" valign="top" class="tableft"></td>
          <td width="180" align=center class="tabcenter"><bean:message key="kq.file.rule"/></td>
          <td width="10" valign="top" class="tabright"></td>
          <td valign="top" class="tabremain" width="300"></td>-->
          <td align=center class="TableRow"><bean:message key="kq.file.rule"/></td>
        </tr>
        <tr valign="top"> 
          <td > 
            <table width="100%" border="0" cellpadding="0" cellspacing="0" style="border-left:1px;border-right:1px;" class="ListTable">
              <tr align="center" valign="bottom"> 
                <td height="30" colspan="4" nowrap class="RecordRow" style="border-top:0px">
                    
                      <bean:write name="kqRuleDataForm" property="kq_rule_vo.string(rule_name)" filter="true"/>&nbsp;
                    </td>
              </tr>
              <tr> 
                <td width="16%" height="10"></td>
                <td width="37%" height="10"></td>
                <td width="36%" height="10"></td>
                <td width="11%" height="10">               
                </td>
              </tr>
              <tr> 
                <td nowrap class="RecordRow" height="25">&nbsp;<bean:message key="kq.rule.card"/></td>
                <td nowrap class="RecordRow" > 
                  <table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.from"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='card_s' property="kq_rule_vo.string(card_s)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="1" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('card_s');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('card_s');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left"><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
                <td colspan="2" nowrap class="RecordRow">
<table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.to"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='card_e' property="kq_rule_vo.string(card_e)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('card_e');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('card_e');">6</button></td></tr>
                        </table>
                      </td>
                      <td><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td nowrap class="RecordRow">&nbsp;<bean:message key="kq.rule.year"/> </td>
                <td nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.from"/>&nbsp;</td>
                      <td valign="middle">                      
                       <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='year_s' property="kq_rule_vo.string(year_s)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('year_s');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('year_s');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left"><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
                <td colspan="2" nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.to"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='year_e' property="kq_rule_vo.string(year_e)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('year_e');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('year_e');">6</button></td></tr>
                        </table>
                      </td>
                      <td><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td nowrap class="RecordRow">&nbsp;<bean:message key="kq.rule.hm"/> </td>
                <td nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.from"/>&nbsp;</td>
                       <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='md_s' property="kq_rule_vo.string(md_s)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('md_s');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('md_s');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left"><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
                <td colspan="2" nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.to"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='md_e' property="kq_rule_vo.string(md_e)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('md_e');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('md_e');">6</button></td></tr>
                        </table>
                      </td>
                      <td><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td nowrap class="RecordRow">&nbsp;<bean:message key="kq.rule.md"/> </td>
                <td nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.from"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='hm_s' property="kq_rule_vo.string(hm_s)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('hm_s');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('hm_s');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left"><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
                <td colspan="2" nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.to"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='hm_e' property="kq_rule_vo.string(hm_e)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('hm_e');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('hm_e');">6</button></td></tr>
                        </table>
                      </td>
                      <td><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td nowrap class="RecordRow">&nbsp;<bean:message key="kq.rule.machine"/> </td>
                <td nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.from"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm" styleId='machine_s' property="kq_rule_vo.string(machine_s)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('machine_s');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('machine_s');">6</button></td></tr>
                        </table>
                      </td>
                      <td align="left"><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
                <td colspan="2" nowrap class="RecordRow"><table width="50%" border="0" cellspacing="0" cellpadding="0">
                    <tr> 
                      <td align="right">&nbsp;<bean:message key="kq.rule.to"/>&nbsp;</td>
                      <td valign="middle"> 
                         <html:text styleClass="inputtext" name="kqRuleDataForm"  styleId='machine_e' property="kq_rule_vo.string(machine_e)" size="4"  onkeypress="event.returnValue=IsDigit();"/>&nbsp;                       
                      </td>
                      <td valign="middle" align="left">
                        <table border="0" cellspacing="2" cellpadding="0">
                          <tr><td><button id="1_up" class="m_arrow" onmouseup="IsInputValue('machine_e');">5</button></td></tr>
                          <tr><td><button id="1_down" class="m_arrow" onmouseup="IsInputValue('machine_e');">6</button></td></tr>
                        </table>
                      </td>
                      <td><bean:message key="kq.rule.place"/></td>
                    </tr>
                  </table></td>
              </tr>
              <tr> 
                <td colspan="4" nowrap class="RecordRow">&nbsp;<bean:message key="kq.machine.num.place"/> 
                <html:checkbox name="kqRuleDataForm" property="kq_rule_vo.string(status)" styleId='status' value="1" onclick="no_disabled();"/>&nbsp;
                 <bean:message key="kq.machine.num.fristline"/></td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
          </tr>
        </table>

        
<table  width="100%" align="center">
  <tr> 
    <td align="center" style="height:35px;">
      <hrms:priv func_id="270612">       
      <input type="button" name="b_next" value="<bean:message key="button.save"/>" class="mybutton" onclick="go_edit();"> 
      </hrms:priv>
       <logic:equal value="dxt" name="kqRuleDataForm" property="returnvalue">  
        <hrms:tipwizardbutton flag="workrest" target="il_body" formname="kqRuleDataForm"/> 
       </logic:equal>
       <!-- input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton" -->	
    </td>
  </tr>
</table>
</html:form>
<script language="javascript">
no_disabled();
</script>