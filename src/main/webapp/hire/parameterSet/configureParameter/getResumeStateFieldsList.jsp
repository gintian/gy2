<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/js/constant.js"></script>
<script language="javascript">
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
		if(rightFields.options.length>1){
		alert(RESUMESTATE_NOT_MUTISELECT);
		return;
		}
		for(var i=0;i<rightFields.options.length;i++){
		  rightFiledIDs+=","+rightFields.options[i].value;
		  rightFieldNames+=","+rightFields.options[i].text;
		}	
			
		
		
		var infos=new Array();
		infos[0]=rightFiledIDs.substring(1);
		infos[1]=rightFieldNames.substring(1);
   	    returnValue=infos;
	    window.close();
	}
	
</script>
<html:form action="/hire/parameterSet/configureParameter/getResumeStateFieldsList">
<Br>

<table width='97%' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
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
			 <hrms:optioncollection name="parameterForm2" property="resumeStateFieldsSetList" collection="list" />
		             <html:select name="parameterForm2" property="codesetid" size="1" style="width:100%">
		             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                      </td>
                    </tr>
                   <tr>
                       <td align="center">
                         <hrms:optioncollection name="parameterForm2" property="resumeStateFieldsList" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="codeitemid" multiple="multiple" ondblclick="additem('codeitemid','right_fields');" style="height:209px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>		
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('codeitemid','right_fields');">
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
 		     <hrms:optioncollection name="parameterForm2" property="selectedRSFieldsList" collection="list"/>
		              <html:select name="parameterForm2" size="10" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
		              <html:options collection="list" property="dataValue" labelProperty="dataName"/>
		        </html:select>	
 		    
                  </td>
                  </tr>
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
          <td align="center" class="RecordRow" nowrap>
              <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		      <bean:message key="reporttypelist.confirm"/>
	      </html:button> 	       
          </td>
          </tr>   
</table>

</html:form>

