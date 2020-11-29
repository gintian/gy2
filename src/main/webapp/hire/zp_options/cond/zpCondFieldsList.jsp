<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="../../js/constant.js"></script>
<script language="javascript">
	function changeFieldSet(){
	var v = zpCondTemplateConstantForm.fieldsetid.value;
  	var hashvo=new ParameterSet();
  	
  	
    hashvo.setValue("fieldsetid",v);
   	var In_paramters="flag=1"; 	
   	  
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'3960000001'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	//Element.hide('cid');

  	var fielditemlist=outparamters.getValue("zpFieldList");
	AjaxBind.bind(zpCondTemplateConstantForm.itemid,fielditemlist);
	
  }
function sub()
	{
		var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 returnValue=0;
	    	 window.close();
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
			rightFieldNames+=","+rightFields.options[i].text;
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			
			if(n>1)
			{
				alert(a_text+FIELD_NOT_REPEAT);
				return;
			}
		}
		
		var infos=new Array();
		infos[0]=rightFiledIDs.substring(1);
		infos[1]=rightFieldNames.substring(1);
   	    returnValue=infos;
	    window.close();
	}
	function add(){
	
	var rightFiledIDs="";
		var rightFieldNames="";
	
		var rightFields=$('right_fields')
		if(rightFields.options.length==0)
		{
			 returnValue=0;
	    	 window.history.back();
		}
		for(var i=0;i<rightFields.options.length;i++)
		{
			rightFiledIDs+=","+rightFields.options[i].value;
			rightFieldNames+=","+rightFields.options[i].text;
			
			var a_value=rightFields.options[i].value;
			var n=0;
			var a_text="";
			for(var j=0;j<rightFields.options.length;j++)
			{
				if(rightFields.options[j].value==a_value)
				{
					n++;
					a_text=rightFields.options[j].text;
				}
			}
			
			if(n>1)
			{
				alert(a_text+"指标不能重复！");
				return;
			}
		}
		var text =rightFieldNames.substring(1);
		var ids=rightFiledIDs.substring(1);
		var zptype="${zpCondTemplateConstantForm.zp_cond_template_type}";
		alert(zptype);
		if(zptype=='0')
		{
	    	zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_show=show&ids="+ids;
	    }
	    else
	    {
	        zpCondTemplateConstantForm.action="/hire/zp_options/cond/getZpCondFieldsList.do?b_complex=show&ids="+ids;
	    }
	    zpCondTemplateConstantForm.submit();
	}

	
</script>
<html:form action="/hire/zp_options/cond/getZpCondFieldsList">
<Br>

<table width='97%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
              <table>
                <tr>
                 <td align="center"  width="46%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
                        <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                    </td>
                    </tr>
                    <tr>
                      <td align="center">
			 <hrms:optioncollection name="zpCondTemplateConstantForm" property="zpFieldSetList" collection="list" />
		             <html:select name="zpCondTemplateConstantForm" property="fieldsetid" size="1" onchange="changeFieldSet();" style="width:100%">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <hrms:optioncollection name="zpCondTemplateConstantForm" property="zpFieldList" collection="list"/>
		              <html:select name="zpCondTemplateConstantForm" size="10" property="itemid" multiple="multiple" ondblclick="additem('itemid','right_fields');" style="height:209px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('itemid','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     
 		     <hrms:optioncollection name="zpCondTemplateConstantForm" property="selectedFieldsList" collection="list"/>
		              <html:select name="zpCondTemplateConstantForm" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:250px;width:100%;font-size:9pt">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>                                
                </tr>
              </table>             
            </td><html:hidden name="zpCondTemplateConstantForm" property="zp_cond_template_type"/>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
              <html:button styleClass="mybutton" property="b_next" onclick="add()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>
