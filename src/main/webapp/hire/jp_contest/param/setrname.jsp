<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   function savefield()
  {  	  
     
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos==null)
     {
       alert(ALREADY_CHOICE_IDENTIFIER_NOT_EMPTY);
       return false;
     }else
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
        }       
     }
     hashvo.setValue("code_fields",code_fields); 
     hashvo.setValue("field_falg","${engageParamForm.field_falg}");   
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'3970004005'},hashvo);
   }	
   function showSelect(outparamters)
   { 
        var mess=outparamters.getValue("mess");        
        var thevo=new Object();
	thevo.mess=mess;
	window.returnValue=thevo;
	window.close(); 
   }  
</script>
<html:form action="/hire/jp_contest/param/engageparam">
<table width="95%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="hire.jp.param.choicetemplettable"/>&nbsp;&nbsp;
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
                     <bean:message key="hire.jp.param.standbytable"/>&nbsp;&nbsp;
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                      <hrms:optioncollection name="engageParamForm" property="rnamelist" collection="list"/> 
     	              <html:select property="left_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  ondblclick="additem('left_fields','right_fields');">
                        <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
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
                     <bean:message key="hire.jp.param.alreadychoicetable"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <hrms:optioncollection name="engageParamForm" property="selectrname" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  styleId="right" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
							<html:button styleClass="mybutton" property="b_up" onclick="upItem($('right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<br>
							<br>
							<html:button styleClass="mybutton" property="b_down" onclick="downItem($('right_fields'));">
								<bean:message key="button.next" />
							</html:button>
						</td>                              
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" window.close();">
          </td>
          </tr>
</table>
</html:form>
