<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript">
 function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
  var date_desc;
  var div_id;
    function showObjectSelectBox(srcobj,id)
   {
      
          date_desc=srcobj;
          Element.show(id); 
          div_id=id;  
          var pos=getAbsPosition(srcobj);
	  with($(id))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
      
   }
   function setSelectValue(obj_select,id)
   {
      var values=obj_select.options[obj_select.selectedIndex].value;     
      var input=document.getElementById(id);      
      input.value=values;       
      event.srcElement.releaseCapture();     
   }
   //限制 考勤卡号长度 只能输入整数
function isNums(i_value){
    re=new RegExp("[^0-9]");
    var s;
    if(s=i_value.match(re)){
        return false;
     }
    return true;
}
function checkNuNS(obj){
 	if(!isNums(obj.value)){
 		obj.value='';
 		return;
 	}
}
</script>

<html:form action="/kq/options/machine/machine_location">
   <br><br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center" class="ftable">
   <tr height="20">
    <!--  <td width=10 valign="top" class="tableft"></td>
    <td width=130 align=center class="tabcenter">
     <bean:message key="kq.machine.message"/></td>
    <td width=10 valign="top" class="tabright"></td>
    <td valign="top" class="tabremain" width="500"></td>--> 
    <td  colspan="2" align=center class="TableRow">
     <bean:message key="kq.machine.message"/></td>             	      
   </tr>  
   <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.name"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(name)"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.type_id"/>&nbsp;
                  </td>
                  <td  align="left">
                   <hrms:optioncollection name="kqMachineForm" property="typelist" collection="list" />
	           <html:select name="kqMachineForm" property="machine.string(type_id)" size="1" >
                   <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                   </html:select> 
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    出入类型&nbsp;
                  </td>
                  <td  align="left">
                  
	            <html:select name="kqMachineForm" property="machine.string(inout_flag)" size="1" >                   
                    <html:option value="0">不限</html:option>
                     <html:option value="-1">出</html:option>
                    <html:option value="1">进</html:option>
                    </html:select> 
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.location"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(location)"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    机器号&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(machine_no)"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.port"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(port)" styleId="text" onclick="showObjectSelectBox(this,'object_panel');" onblur="Element.hide('object_panel');"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.baud_rate"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(baud_rate)" styleId="text1" onclick="showObjectSelectBox(this,'object_panel1');" onblur="Element.hide('object_panel1');"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.ip_address"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(ip_address)" onkeypress="event.returnValue=IsDigit();"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    考勤卡号长度&nbsp;
                  </td>
                  <td  align="left">
                  <html:text styleClass="inputtext" name="kqMachineForm" property="machine.string(card_len)" maxlength="10" onkeyup="checkNuNS(this)"/>  
                  </td>
                </tr>
                <tr>
                  <td width="30%" align="right">
                    <bean:message key="kq.machine.description"/>&nbsp;
                  </td>
                  <td  align="left">
                  <html:textarea name="kqMachineForm" property="machine.string(description)" cols="35" rows="4" styleClass="text5"/>  
                  </td>
                </tr>
     <tr>
        <td align="center" style="height:35px;border: none" colspan="2">
           <hrms:submit styleClass="mybutton" property="b_save">
            		   <bean:message key="button.save"/>
	   </hrms:submit>  
	    <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="history.back();" class="mybutton">						       
        </td>
     </tr>
   </table>
   <div id="object_panel">
         
   	<select name="object_com" multiple="multiple" size="4"  style="width:150" onchange="setSelectValue(this,'text');">    
		  <option value="COM1">&nbsp;COM1&nbsp;</option>
		  <option value="COM2">&nbsp;COM2&nbsp;</option>	 
		  <option value="COM3">&nbsp;COM3&nbsp;</option>
		  <option value="COM4">&nbsp;COM4&nbsp;</option>	    		    			    		    
    </select>
   </div>
   <div id="object_panel1">         
   	<select name="object_rate" multiple="multiple" size="6"  style="width:150" onchange="setSelectValue(this,'text1');">    
		  <option value="57600">&nbsp;57600&nbsp;</option>
		  <option value="38400">&nbsp;38400&nbsp;</option>	 
		  <option value="19200">&nbsp;19200&nbsp;</option>
		  <option value="9600">&nbsp;9600&nbsp;</option>	
		  <option value="4800">&nbsp;4800&nbsp;</option>
		  <option value="2400">&nbsp;2400&nbsp;</option>	    		    			    		    
    </select>
   </div>
</html:form>
<script language="javascript">
   Element.hide('object_panel');
   Element.hide('object_panel1');
</script>