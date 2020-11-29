<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
   function change()
   {
      setselectitem('right_fields');
      leaderParamForm.action="/general/deci/leader/param.do?b_addfeild=link";
      leaderParamForm.submit();
   }
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
     hashvo.setValue("field_falg","${leaderParamForm.field_falg}");   
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'05603000009'},hashvo);
   }	
   function showSelect(outparamters)
   { 
        var mess=outparamters.getValue("mess");        
        var thevo=new Object();
		thevo.mess=mess;
		
		if(parent.parent.Ext && parent.parent.Ext.getCmp('select_rname'))
   			parent.parent.Ext.getCmp('select_rname').return_vo = thevo;
   		else
			window.returnValue=thevo;
	
	//window.close();
		winclose(); 
   }  
   function winclose(){
   		if(parent.parent.Ext && parent.parent.Ext.getCmp('select_rname'))
   			parent.parent.Ext.getCmp('select_rname').close();
   		else
   			window.close();
   }
</script>
<html:form action="/general/deci/leader/param">
<table width="530" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3">
		<bean:message key="leaderteam.setname.chosiceunitinstancetable"/>&nbsp;&nbsp;
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
                      <hrms:optioncollection name="leaderParamForm" property="rnamelist" collection="list"/> 
     	              <html:select property="left_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  ondblclick="additem('left_fields','right_fields');">
                        <html:options collection="list"  property="dataValue" labelProperty="dataName"/>
                     </html:select> 
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
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
                  <hrms:optioncollection name="leaderParamForm" property="selectrname" collection="selectedlist"/> 
     	             <html:select property="right_fields" size="10" multiple="true" style="height:230px;width:100%;font-size:9pt"  styleId="right" ondblclick="removeitem('right_fields');">
                        <html:options collection="selectedlist"  property="dataValue" labelProperty="dataName"/>
                     </html:select>   
                  </td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
							<html:button styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));">
								<bean:message key="button.previous" />
							</html:button>
							<html:button styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:30px;">
								<bean:message key="button.next" />
							</html:button>
						</td>                              
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height: 35px;">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick=" savefield();">
	     <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' class="mybutton" onclick=" winclose();">
          </td>
          </tr>
</table>
</html:form>
