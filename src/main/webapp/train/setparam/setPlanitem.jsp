<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript"><!--
 function savefield()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert(SAME_ITEM_RESET_ITEM);
          		return false;
          	}
          }
        }       
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;          
      code_fields[i]=valueS;
    }     
    hashvo.setValue("subclass_value",code_fields);
    var request=new Request({method:'post',onSuccess:showSelect,functionId:'2020020120'},hashvo);
   }	
   function showSelect(outparamters)
   { 
   	 returnValue="aaaa";
	 window.close(); 
   }
   function savefieldOk()
  {  	  
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos.length!=0)
     {
        var code_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          code_fields[i]=valueS;
          for(var j=i+1;j<vos.length;j++)
          {
          	if(valueS.toUpperCase()==vos.options[j].value.toUpperCase())
          	{
          		alert(SAME_SUBSET_RE_SELECT);
          		return false;
          	}
          }
        }       
     }
    var code_fields=new Array();        
    for(var i=0;i<vos.length;i++)
    {
      var valueS=vos.options[i].value;          
      code_fields[i]=valueS;
    }         
    hashvo.setValue("subclass_value",code_fields); 
    var request=new Request({method:'post',onSuccess:showSelectOk,functionId:'2020020120'},hashvo);
   }
    function showSelectOk(outparamters)
   { 
		alert(RELATED_ITEM_SET_OK); 
		setParamForm.action="/train/setparam/setPlanitem.do?b_query=link";
		setParamForm.submit();
   }
   function closeOk()
   { 
		returnValue="ssss";
		window.close();
   }
	function retrunSans(){
		setParamForm.action="/train/setparam/project.do?b_query=link";
		setParamForm.target="il_body";
		setParamForm.submit();
	}
--></script>
<html:form action="/train/setparam/setPlanitem">
<br>
<br>
<table width="500" border="0" cellspacing="0"  align="center" cellpadding="0" >
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
            	<bean:message key="train.plan.train.details.item"/>
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap style="border-top: 0px;">
              <table border="0" cellpadding="0" cellspacing="0">
                <tr>
                 <td align="center"  width="41%">
                   <table align="center" width="100%">
                    <tr>
                    <td align="left">
		            	<bean:message key="gz.bankdisk.preparefield" />&nbsp;&nbsp;
                    </td>
                    </tr>                   
                   <tr>
                    <td align="center">
                      <hrms:optioncollection name="setParamForm" property="itemlist" collection="list"/> 
     	              <html:select property="left_fields" size="10" multiple="true"
							style="height:230px;width:100%;font-size:9pt"
							ondblclick="additem('left_fields','right_fields');removeitem('left_fields');">
							<html:options collection="list" property="dataValue"
								labelProperty="dataName" />
						</html:select>
                    </td>
                    
                    </tr>
                   
                   </table>
                </td>
               
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');removeitem('left_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="41%" align="center">
                 
                 
                 <table width="100%" >
                  <tr>
                  <td width="100%" align="left">
		            	<bean:message key="static.ytarget" />&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
                  <hrms:optioncollection name="setParamForm" property="selectlist" collection="selectedlist"/> 
     	           <html:select property="right_fields" size="10" multiple="true"
						style="height:230px;width:100%;font-size:9pt" styleId="right"
						ondblclick="additem('right_fields','left_fields');removeitem('right_fields');">
						<html:options collection="selectedlist" property="dataValue"
							labelProperty="dataName" />
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
          <td align="center" class="RecordRow" nowrap  colspan="3" style="height:35px;border-top: 0px;">
               <input type="button" name="btnreturn" value='<bean:message key="button.ok"/>' class="mybutton" onclick="savefieldOk();">
              <!-- <input type="button" name="breturn" value='<bean:message key="button.return"/>' class="mybutton" onclick="retrunSans();"> -->
          </td>
          </tr>
</table>
</html:form>
