<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/constant.js"></SCRIPT>
<script language="javascript">
   function change()
   {
      setselectitem('right_fields');
      posFilterSetForm.action="/hire/zp_options/filtercond_field.do?b_addfield=link";
      posFilterSetForm.submit();
   }
   
   function next()
   {  		
   		var ss=eval('document.posFilterSetForm.right_fields');
		if(ss.options.length==0)
		{
		    alert(GENERAL_SELECT_ITEMNAME+"!");
		    return;		
		}
   	    setselectitem('right_fields');
   		posFilterSetForm.action="/hire/zp_options/filtercond_field.do?b_next=link";
        posFilterSetForm.submit();
   
   }
   
   
   function clearCond()
   {
   		if(confirm(CONFIRM_CLEAR_POSITION_CONDITION+"?"))
   		{
   			posFilterSetForm.action="/hire/zp_options/filtercond_field.do?b_clear=link";
 		    posFilterSetForm.submit();
   		}
   }
   
</script>
<html:form action="/hire/zp_options/filtercond_field">
  <br>
  <br>
  <br>   
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
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
                      <hrms:fieldsetlist name="posFilterSetForm" usedflag="usedflag" domainflag="domainflag"  collection="setlist" scope="session"/>
                      <html:select name="posFilterSetForm" property="setname" size="1"  onchange="change();" style="width:100%" >
                           <html:options collection="setlist" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   <tr>
                    <td align="center">
                      <hrms:fielditemlist  name="posFilterSetForm" usedflag="usedflag" setname="setname" collection="list" scope="session"/>
                      <html:select property="left_fields" multiple="true" style="height:209px;width:100%;font-size:9pt" ondblclick="additem('left_fields','right_fields');">
                           <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
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
                  <hrms:optioncollection name="posFilterSetForm" property="fieldlist" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">              
                <hrms:submit styleClass="mybutton" property="br_return">
            		      <bean:message key="button.query.pre"/>
	        </hrms:submit> 
	          <input type='button' class="mybutton" value="<bean:message key="button.query.next"/>" onclick="next()" />
	        	                     	                    
              <input type='button' class="mybutton" value="<bean:message key="hire.clear.condition"/>" onclick="clearCond()" />
	        	  
          </td>
          </tr>   
</table>
</html:form>
