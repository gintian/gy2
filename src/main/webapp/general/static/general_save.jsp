<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="javascript">
	
	function getSelectItemValue(listbox)
	{
		if(listbox==null)
		   return;
		for(i=0;i<listbox.options.length;i++)
                {
                    if(listbox.options[i].selected)
                    {
    	               staticFieldForm.titles.value=listbox.options[i].text;
    	               staticFieldForm.hvalue.value=listbox.options[i].value;
    	               break;
                    }
                }		
	}
	
	function deleteSName(listbox)
	{
	        var id="";
	        var bflag=false;
		for(i=0;i<listbox.options.length;i++)
                {
                    if(listbox.options[i].selected)
                    {
    	               id=listbox.options[i].value;
    	               bflag=true;
    	               break;
                    }
                }
         if(bflag)
          {
	   		     var pars="id="+id;
   	  	     var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:deleteSuccess,functionId:'05301010006'});
                
           }
	}
	
	function deleteSuccess(outparamters)
	{
		for(i=0;i<$('selectstitle').options.length;i++)
                {
                    if($('selectstitle').options[i].selected)
                    {
    	               $('selectstitle').options.remove(i);
    	               break;
                    }
                }
	}
	function checkT(){
		var titles=$F('titles');
		if(titles.indexOf("\‘")>-1||titles.indexOf("\”")>-1||titles.indexOf("\'")>-1||titles.indexOf("\"")>-1)
	  {	
	       		alert("统计条件名称不能包含\’或\"或\’或\”");
	       		return false;
	  }
	}
	
</script>
<hrms:themes />
<html:form action="/general/static/general_save">
     <br>
     <table border=0 align="center"><tr><td>
      <fieldset style="width:400px;">
 <legend ><bean:message key="static.save"/></legend>
      <table width="400px;" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
            <td colspan="4">
               <table width="55%" border="0" cellspacing="1"  align="center" cellpadding="1" bordercolor = "#223377"> 
               
                 <tr class="trDeep1">
                     <td align="right" nowrap valign="center"><bean:message key="static.nsave"/></td>
                     <td align="left"  nowrap valign="center">
            				<html:text name="staticFieldForm" value="${staticFieldForm.titles}" property="titles" maxlength="10" size="20" />
            				<html:hidden property="infor_Flag"/>
            				<html:hidden property="hvalue"/>
            				<html:hidden name="staticFieldForm" property="find" />
            				<html:hidden name="staticFieldForm"  property="history"  />
            				<html:hidden name="staticFieldForm" property="result" />
                     </td>
                 </tr>
                  
                 <tr >
                     <td align="left"  nowrap valign="center" colspan="4">
		                <html:select name="staticFieldForm" property="selectstitle" size="1" multiple="multiple" style="height:280px;width:100%;font-size:9pt" ondblclick="getSelectItemValue(this);">
                  	  <html:optionsCollection property="snamelist" value="dataValue" label="dataName"/> 
                       </html:select>                
                     </td>
                 </tr>    
                  </table>
           
                </td>
              </tr> 
          </table>   
      </fieldset>
      </td></tr>
      <tr><td>
     <table style="width:400px;">
          <tr>
            <td align="center">
                  <html:button styleClass="mybutton" property="br_delete" onclick="deleteSName($('selectstitle'));"> 
                       <bean:message key="button.delete"/>
	          </html:button>
         	  <html:submit styleClass="mybutton" property="b_save"  onclick="return checkT();">
                     <bean:message key="button.save"/>
	          </html:submit>
	          <html:cancel styleClass="mybutton" property="br_close">
	          	<bean:message key="button.return"/>
	          </html:cancel>
            </td>
          </tr>          
    </table>
    </td></tr>
    </table>
</html:form>

