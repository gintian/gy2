<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
function change(){
 	var tablename=document.getElementById("setname").value;
	var in_paramters="tablename="+tablename;
    var request=new Request({method:'post',asynchronous:false,parameters:in_paramters,onSuccess:showFieldList,functionId:'0520000002'});
}
function showFieldList(outparamters){
		var fieldlist=outparamters.getValue("fieldlist");
		AjaxBind.bind(projectForm.left_fields,fieldlist);
	}
function setscond(){
	var rights= document.getElementsByName("right_fields");
	if(rights[0].length<1){
		alert("<bean:message key='org.autostatic.mainp.select.item'/>");
		return;
	}
	setselectitem('right_fields');
    projectForm.action="/org/autostatic/mainp/setconditions.do?b_query=link";
    projectForm.submit();
}
</script>
<base id="mybase" target="_self">
<html:form action="/org/autostatic/mainp/statistics_conditions">
<br>
<table width="90%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
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
                      <hrms:fieldsetlist name="projectForm" usedflag="usedflag" domainflag="domainflag"  collection="setlist" scope="session"/>
                      <html:select name="projectForm" property="setname" size="1"  onchange="change();" style="width:100%" >
                           <html:options collection="setlist" property="dataValue" labelProperty="dataName"/>
                      </html:select>
                    </td>
                    </tr>
                   <tr>
                    <td align="center">
                      <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:209px;width:100%;font-size:9pt">
                     </select>
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
     	             <html:select name="projectForm" property="right_fields" multiple="multiple" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="selectedlist" value="dataValue" label="dataName"/>   		      
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
          	<input type="button" name="button1" value='<bean:message key="button.query.next"/>' Class="mybutton" onclick="setscond();">        	                    
          </td>
          </tr>
</table>
</html:form>
<script language="javascript">
   change();
</script>
