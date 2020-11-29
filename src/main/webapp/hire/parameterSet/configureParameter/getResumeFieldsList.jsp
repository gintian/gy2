<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/js/constant.js"></script>
<script language="javascript">
function changeFieldSet(){
	var v = parameterForm2.fieldsetid.value;
  	var hashvo=new ParameterSet();
  	
  	
    hashvo.setValue("fieldsetid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:resultChangeFieldSet,functionId:'3000000196'},hashvo);					
  }
  
  function resultChangeFieldSet(outparamters){
  	//Element.hide('cid');
  	var fielditemlist=outparamters.getValue("resumeFieldsList");
	AjaxBind.bind(parameterForm2.itemid,fielditemlist);
	
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
	
</script>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<html:form action="/hire/parameterSet/configureParameter/getResumeFieldsList">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>

<table width='530px' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
   	   <td class="RecordRow" align="center" width="100%" nowrap>
   	   <table width="100%">
   	   <tr>
            <td width="100%" align="center" nowrap>
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
			 <hrms:optioncollection name="parameterForm2" property="resumeFieldsSetList" collection="list" />
		             <html:select name="parameterForm2" property="fieldsetid" size="1" onchange="changeFieldSet();" style="width:100%">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                     <hrms:optioncollection name="parameterForm2" property="resumeFieldsList" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="itemid" multiple="multiple" ondblclick="additem('itemid','right_fields');" style="height:209px;width:100%;font-size:9pt">
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
 		     
 		        <hrms:optioncollection name="parameterForm2" property="selectedResumeFieldsList" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
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
            </td>
            </tr>
            </table>
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap style="padding-top:5px;padding-bottom:3px;">
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>
