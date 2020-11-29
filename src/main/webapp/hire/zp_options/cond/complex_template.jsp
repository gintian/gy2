<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../js/constant.js"></script>
<script type="text/javascript">
<!--
function saveTemplate()
{
  
}
function insertText(strtxt)
{
   var expr_editor=$("expression");
   expr_editor.focus();
   var element = document.selection;
   if (element!=null) 
   {
    var rge = element.createRange();
		if (rge!=null)	
	        rge.text=strtxt;
   }
}
var date_desc;
function setSelectValue()
   {
     if(date_desc)
     {
       date_desc.value=$F('date_box');
       Element.hide('date_panel');   
     }
   }
   
  function showDateSelectBox(srcobj)
   {
       //if(event.button==2)
       //{
          date_desc=srcobj;
          Element.show('date_panel');   
          var pos=getAbsPosition(srcobj);
	  with($('date_panel'))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
       //}
   }
   function check()
{
    var code=window.event.keyCode;
    var ret=true;
    if(code==8||code==46)
    {
    }
   else if(97<=code&&code<=105)
   {
      
   }else if(48<=code&&code<=57)
   {
   }
   else
   { 
        if((window.event.shiftKey)&&(code==48||code==49||code==57||code==56||code==187))
        {
        }
        else
        {
           window.event.returnValue=false;
        }
     
   }
}
function ctrlKey()
{
    var code =window.event.keyCode;
    if((window.event.shiftKey)&&code==222)
    {
        window.event.returnValue=false;
    }
}
function checkExpr(type,size)
{
   <%int t=0;%> 
    var arr=new Array();
  <logic:iterate id="element" name="zpCondTemplateConstantForm" property="factorlist" indexId="index"> 
	   	      	    
             <logic:equal name="element" property="fieldtype" value="N">
               var a<%=t%>=document.getElementsByName("factorlist[<%=t%>].value");
               if(a<%=t%>[0].value !=''){
                  var myReg =/^(-?\d+)(\.\d+)?$/
	        	  if(!myReg.test(a<%=t%>[0].value)) 
	        	   {
	            	    alert("<bean:write  name="element" property="hz"/>"+GZ_BANKDISK_INFO4+"!");
	            	    return;
	         	   }
         		}
		</logic:equal>
         var a<%=t%>=document.getElementsByName("factorlist[<%=t%>].value");
         var obj=new Object();
         obj.value=a<%=t%>[0].value;
         var bb= document.getElementsByName("factorlist[<%=t%>].oper")[0]; 
         for(var i=0;i<bb.options.length;i++)
         {
            if(bb.options[i].selected){
               obj.oper=bb.options[i].value;
               break;
            }
         } 
         obj.fieldname="<bean:write  name="element" property="fieldname"/>";
         obj.log="";
        arr[<%=t%>]=obj;    
        
	         
        <%t++;%>
 </logic:iterate>
    var expr = zpCondTemplateConstantForm.expression.value;
    var hashvo=new ParameterSet();
    hashvo.setValue("expr",expr);
    hashvo.setValue("size",size);
    hashvo.setValue("type",type);
    hashvo.setValue("arr",arr);
    hashvo.setValue("fromflag","hire");
   	var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:"3020100021"},hashvo);
} 
function check_ok(outparameters)
{
   var type=outparameters.getValue("type");
   var info=outparameters.getValue("info");
   var oldname=zpCondTemplateConstantForm.templateName.value;
   if(info=='0')
   {
      var name=window.prompt(PLEASE_INPUT_TEMPLATE_NAME,oldname);
      if(name==null&&oldname!=null)
         name=oldname;
      if(trim(name)=='')
      {
         alert(TEMPLATE_NAME_IS_NOT_EMPTY);
         return;
      }
      zpCondTemplateConstantForm.templateName.value=name;
      zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_save=save";
      zpCondTemplateConstantForm.submit();
      window.returnValue="ture";
   }
   else
   {
     alert(info);
      return;
   }
}

//-->
</script>
<html:form action="/hire/zp_options/cond/getZpCondFieldsList">
<table width="60%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
<thead>
<tr height="20">
       		<td colspan='4' align='left' class='TableRow_lrt'>
       		 <bean:message key="hire.complex.template"/>
       		 </td>  
       		</tr>
<tr >
<td class="TableRow" align="center"><bean:message key="gz.bankdisk.sequencenumber"/></td><td class="TableRow" align="center"><bean:message key="gz.bankdisk.queryfield"/></td><td align="center" class="TableRow"><bean:message key="gz.bankdisk.relationcharacter"/></td><td align="center" class="TableRow"><bean:message key="gz.bankdisk.queryvalue"/></td>
</tr>
</thead>
<% int i=0;%>
<logic:iterate id="element" name="zpCondTemplateConstantForm" property="factorlist" indexId="index">
<tr>
 <td align="center" class="RecordRow" nowrap >
          <%=i+1%>
 </td>
 <td align="center" id='<%="hz"+i%>' class="RecordRow" nowrap >
     <bean:write name="element" property="hz"/>                       
  </td> 
<td align="center" class="RecordRow" nowrap >
     <html:select name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].oper"%>' size="1">
  <html:optionsCollection property="operlist" value="dataValue" label="dataName"/>                                   
  </html:select>
    </td>  
    <logic:equal name="element" property="fieldtype" value="D">
                            <td align="left" class="RecordRow" nowrap> 
 <html:text name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].value"%>'  size="20"  maxlength="${element.itemlen}" ondblclick="showDateSelectBox(this);" onblur="Element.hide('date_panel');" onkeydown="ctrlKey();" style="width:200px;" styleClass="text4"/>         
				</td>                                        
                   </logic:equal>
                    <!--字符型 -->                                                    
                          <logic:equal name="element" property="fieldtype" value="A">
                            <td align="left" class="RecordRow" nowrap>
                              <logic:notEqual name="element" property="codeid" value="0">
                              <html:hidden name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].value"%>'/>
                                 <html:text name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].hzvalue"%>'  size="20"  maxlength="${element.itemlen}" onchange="fieldcode(this,1)" onkeydown="ctrlKey();" style="width:200px;" styleClass="text4"/>         
                                <img src="/images/code.gif" onclick="openCondCodeDialog('<bean:write name="element" property="codeid"/>','<%="factorlist["+i+"].hzvalue"%>');" align="absmiddle"/>&nbsp;&nbsp;
                                
                              </logic:notEqual>               
                              <logic:equal name="element" property="codeid" value="0">
                              <html:text name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].value"%>' size="20" maxlength='${element.itemlen}' onkeydown="ctrlKey();" style="width:200px;" styleClass="text4"/>                                 
                              </logic:equal>                               
                            </td>                           
                          </logic:equal>
                           <!--数据值-->                            
                          <logic:equal name="element" property="fieldtype" value="N">
                            <td align="left"  class="RecordRow" nowrap>    
                            <html:text name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].value"%>' size="20" maxlength='${element.itemlen}' onkeydown="ctrlKey();" style="width:200px;" styleClass="text4"/>                      
                            </td>                           
                          </logic:equal>    
                          <!--备注型--> 
                          <logic:equal name="element" property="fieldtype" value="M">
                            <td align="left" class="RecordRow" nowrap>    
                            <html:text name="zpCondTemplateConstantForm" property='<%="factorlist["+i+"].value"%>' size="20" maxlength="${element.itemlen}" onkeydown="ctrlKey();" style="width:200px;" styleClass="text4"/>                      
                            </td>                           
                          </logic:equal>                           
  </tr>                                          
   <%i++;%>  
</logic:iterate>

<tr>
<td colspan="4" align="left" class="RecordRow" nowrap>
<bean:message key="gz.bankdisk.factorexpression"/>
</td></tr>
<tr>
<td align="center" colspan="4" class="RecordRow" nowrap>
<html:textarea name="zpCondTemplateConstantForm" property="expression" rows="5" cols="60" onkeydown="check();"></html:textarea>
</td>
</tr>
<tr>
<td colspan="4" align="left" class="RecordRow" style="padding-top:3px;padding-bottom:3px;" nowrap>
<input type="button" value="<bean:message key="gz.bankdisk.moveover"/>" name="" class="mybutton" onclick="insertText('*');"/>
&nbsp;<input type="button" value="<bean:message key="gz.bankdisk.or"/>" name="" class="mybutton" onclick="insertText('+');"/>
&nbsp;<input type="button" value="<bean:message key="gz.bankdisk.not"/>" name="" class="mybutton" onclick="insertText('!');"/>
&nbsp;<input type="button" value="(" name="" class="mybutton" onclick="insertText('(');"/>
&nbsp;<input type="button" value=")" name="" class="mybutton" onclick="insertText(')');"/>
</td>
</tr>
<tr>
<td colspan="4" align="center" class="RecordRow" style="padding-top:5px;padding-bottom:5px;">
<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.query.pre"/>
	         </hrms:submit>  
<input type="button" class="mybutton" name="save" value="<bean:message key="button.save"/>" onclick="checkExpr('1','<%=i%>');"/>
<input type="hidden" name="rightFields" value=""/>
<html:hidden name="zpCondTemplateConstantForm" property="zp_cond_template_type"/>
<html:hidden name="zpCondTemplateConstantForm" property="templateName"/>
</td>
</tr>
</table>
          <div id="date_panel">
   			<select name="date_box" multiple="multiple" size="10"  style="width:200" onchange="setSelectValue();">    
			    <option value="$YRS[10]"><bean:message key="gz.bankdisk.yearlimit"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentyear"/>"><bean:message key="gz.bankdisk.currentyear"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentmonth"/>"><bean:message key="gz.bankdisk.currentmonth"/></option>
			    <option value="<bean:message key="gz.bankdisk.currentday"/>"><bean:message key="gz.bankdisk.currentday"/></option>					    
			    <option value="<bean:message key="gz.bankdisk.today"/>"><bean:message key="gz.bankdisk.today"/></option>
			    <option value="<bean:message key="gz.bankdisk.stopdate"/>"><bean:message key="gz.bankdisk.stopdate"/></option>
                <option value="1992.4.12">1992.4.12</option>	
                <option value="1992.4">1992.4</option>	
                <option value="1992">1992</option>			    
			    <option value="????.??.12">????.??.12</option>
			    <option value="????.4.12">????.4.12</option>
			    <option value="????.4">????.4</option>			    			    		    
                        </select>
                    </div>
</html:form>
<script type="text/javascript">
<!--
 Element.hide('date_panel');
//-->
</script>
