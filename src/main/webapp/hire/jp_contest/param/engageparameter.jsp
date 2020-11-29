<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language=JavaScript>   
function setField(field_falg)
{
    var target_url="/hire/jp_contest/param/engageparam.do?br_setfeild=link`field_falg="+field_falg;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(iframe_url,1, 
        "dialogWidth:540px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById(field_falg);  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById(field_falg);  
    }
}
function setRname()
{
    var target_url="/hire/jp_contest/param/engageparam.do?b_rname=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:540px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById('card');  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById("card");  
    }
}
function setApp()
{
    var target_url="/hire/jp_contest/param/engageparam.do?b_setappfile=link";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    var return_vo= window.showModalDialog(target_url,1, 
        "dialogWidth:540px; dialogHeight:390px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
    if(return_vo!=null)
    {
	  var in_obj=document.getElementById('appfile');  
	  in_obj.innerHTML=return_vo.mess;
    }else
    {
	  var in_obj=document.getElementById("appfile");  
    }
}
 function saveCode()
 {
    engageParamForm.action="/hire/jp_contest/param/engageparam.do?b_save=link";
    engageParamForm.submit();
 }
 function setTemplate()
	{
	    var id_vo= document.getElementById("template");
	    var select_id=id_vo.value;
	    
	    var t_url="/system/warn/config_maintenance.do?b_template=link&select_id="+select_id;
	       var return_vo= window.showModalDialog(t_url,'rr', 
            "dialogWidth:356px; dialogHeight:446px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
          if(!return_vo)
            return false;
         else
          {
             if(return_vo.flag=="true")
             {
                //var id_vo1= document.getElementById("template");
                engageParamForm.strTemplate.value=return_vo.content;
                //id_vo1.value=return_vo.content;
                var id_vo=document.getElementById("text_Template");   
                var text=return_vo.title;
                var ids=return_vo.content;
                if(text!=""&&text.length>0)
                {
                   var at=text.split(",");
                   var tabids=ids.split(",");
                   text="";
                   for(var i=0;i<at.length;i++)                   
                   {
                      if(tabids[i]!="")
                        text=text+tabids[i]+":"+at[i]+"\r\n";
                   }
                   id_vo.value=text;
                }else
                {
                   id_vo.value="";
                }                     
              
             }
          }     
	}
function IsDigit() 
{ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
} 
</script> 
<html:form action="/hire/jp_contest/param/engageparam">
<br>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		&nbsp;&nbsp;<bean:message key="hire.jp.param.jppostparamsetting"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>

   	   
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="hire.jp.param.applyjpposttarget"/>
   	   </td>
   	   <td width="60%" class="RecordRow" id="appfile" colspan="1">
   	   	<bean:write  name="engageParamForm" property="app_view_mess" filter="false"/>
       </td>     
           <td width="20%" class="RecordRow" colspan="1">
   	       <input type="button" name="btnreturn" value='<bean:message key="kh.field.config"/>' class="mybutton" onclick="setApp();">
           </td>       
          </tr>
          <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="hire.jp.param.jppostlist"/>
   	   </td>
   	   <td width="60%" class="RecordRow" id="attend" colspan="1">
   	   	<bean:write  name="engageParamForm" property="attent_view_mess" filter="false"/>
       </td>     
           <td width="20%" class="RecordRow" colspan="1">
   	       <input type="button" name="btnreturn" value='<bean:message key="kh.field.config"/>' class="mybutton" onclick="setField('attend');">
           </td>       
          </tr>  
          <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="sys.res.card"/>
   	   </td>
   	   <td width="60%" class="RecordRow" id="card" colspan="1">
   	   	<bean:write  name="engageParamForm" property="card_mess" filter="false"/>
       </td>     
           <td width="20%" class="RecordRow" colspan="1">
   	       <input type="button" name="btnreturn" value='<bean:message key="kh.field.config"/>' class="mybutton" onclick="setRname();">
           </td>       
          </tr> 
          <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="hire.jp.param.maxpostpositionnumber"/>
   	   </td>
   	   <td width="60%" class="RecordRow" id="position" colspan="2">
   	   		<html:text name="engageParamForm" property="maxpos" styleClass="textColorWrite" onkeypress="event.returnValue=IsDigit(this)" /> 个
       </td>     
            
        </tr>
        <tr>
   	   <td width="20%" class="RecordRow" height="35" align="right" nowrap>
   	     <bean:message key="system.operation.template"/>
   	   </td>
   	   <td width="60%" class="RecordRow" id="card" colspan="1">
   	   	<html:textarea name="engageParamForm" property="template" rows="6" cols="70" styleClass="text6" readonly="true" styleId="text_Template"/>
       </td>     
           <td width="20%" class="RecordRow" colspan="1">
   	       <input type="button" name="btnreturn" value='<bean:message key="kh.field.config"/>' class="mybutton" onclick="setTemplate()">
           </td>       
          </tr>
       <tr>
          <td align="center" class="RecordRow" nowrap colspan="3">
          <input type="button" name="btnreturn" value='<bean:message key="button.save"/>' class="mybutton" onclick=" saveCode();">
          </td>
      </tr>      
</table>
<input type="hidden" name="strTemplate" value="${engageParamForm.strTemplate}"/>
</html:form>
