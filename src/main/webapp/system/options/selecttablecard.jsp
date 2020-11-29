<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language=JavaScript>   
   
  function saveStruts()
  {  	  
     
     var hashvo=new ParameterSet();          
     var vos= document.getElementById("right");       
     if(vos==null)
     {
       alert("已选指标项不能为空！");
       return false;
     }else
     {
        var right_fields=new Array();        
        for(var i=0;i<vos.length;i++)
        {
          var valueS=vos.options[i].value;          
          right_fields[i]=valueS;
        }       
     }
     hashvo.setValue("right_fields",right_fields);        
     hashvo.setValue("old_mysalarys","${cardConstantForm.old_mysalarys}");   
             	
     var request=new Request({method:'post',onSuccess:showSelect,functionId:'1010030009'},hashvo);
   }   
   function showSelect(outparamters)
   { 
     var types=outparamters.getValue("types");          
     if(types=="ok")
     {
        //alert("编辑成功");   
        //cardConstantForm.action="/ykcard/cardconstantset.do?b_cardset0=link";
        //cardConstantForm.submit();    
         cardConstantForm.action="/ykcard/cardconstantset.do?b_cardset1=link";
         cardConstantForm.submit();
     }else
     {
        alert("编辑失败");
     }     
   }     
   function nextStruts()
   {
      var vos= document.getElementById("right");       
      if(vos==null)
      {
         alert("已选指标项不能为空！");
         return false;
      }else{
          cardConstantForm.action="/ykcard/cardconstantset.do?b_cardset1=link";
          cardConstantForm.submit();
      }       
   }
   function goback()
   {
         cardConstantForm.action="/ykcard/recordconstantset.do?b_search=set";
         cardConstantForm.submit();
   }
   </script> 
<html:form action="/ykcard/cardconstantset">
<table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		选择子集
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
                        备选子集&nbsp;&nbsp;
                    </td>
                    </tr>                    
                   <tr>
                       <td align="center">
                         <html:select name="cardConstantForm" property="left_fields" multiple="multiple" size="10" ondblclick="additem2('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="employ_field_list" value="dataValue" label="dataName"/>   		      
 		        </html:select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="additem2('left_fields','right_fields');">
            		 <bean:message key="button.setfield.addfield"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	           </html:button>	     
                </td>         
                
                <td width="46%" align="center">
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     已选子集&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
 		     <html:select name="cardConstantForm" property="right_fields" styleId="right"  multiple="multiple" size="10"  style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="selected_field_List" value="dataValue" label="dataName"/>   		      
 		        </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
                   <html:button  styleClass="smallbutton" property="b_up" onclick="upItem($('right_fields'));">
            		     <bean:message key="button.previous"/> 
	           </html:button>
	           <html:button  styleClass="smallbutton" property="b_down" onclick="downItem($('right_fields'));" style="margin-top:30px;">
            		     <bean:message key="button.next"/>    
	           </html:button>	     
                </td>                                
                </tr>
              </table>             
            </td>
          </tr>
            
          <tr>
          <td align="center" class="RecordRow" nowrap style="height: 35px;">
             <input type="button" name="btnreturn" value='上一步' class="mybutton" onclick="goback();">
             <input type="button" name="btnreturn" value='下一步' class="mybutton" onclick=" saveStruts();">
	  </td>
          </tr>   
</table>
</html:form>
