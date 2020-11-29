<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	
	
	/**填充花名册指标和排序指标*/
	function filloutData()
	{		
	    setselectitem('right_fields');
	}
	
</script>
<base id="mybase" target="_self">
<html:form action="/kq/feast_manage/select/selectfiled">
<!--查询指标-->
<div id="first" style="filter:alpha(Opacity=100);display=block;margin-top:5px;">
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		&nbsp;<bean:message key="label.query.selectfield"/>&nbsp;
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
                         <html:select name="feastForm" property="left_fields" multiple="multiple" size="10" ondblclick="additem('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="selectfieldlist" value="dataValue" label="dataName"/>   		      
 		          </html:select>
                       </td>
                    </tr>
                   </table>
                </td>
               
                <td width="4%" align="center">
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
 		     <html:select name="feastForm" property="right_fields" multiple="multiple" size="10" ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
                           <html:optionsCollection property="selectedlist" value="dataValue" label="dataName"/>   		      
 		     </html:select>
                  </td>
                  </tr>
                  </table>             
                </td>
                
                <td width="4%" align="center">
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
            
          <tr>
          <td align="center" class="RecordRow" nowrap style="height:35px;">
          
               <hrms:submit styleClass="mybutton"  property="b_next" onclick="filloutData();">
            		      <bean:message key="button.query.next"/>
	       </hrms:submit>
	       <input type="button" name="btnreturn" value='<bean:message key="button.close"/>' onclick="history.back();window.close();" class="mybutton">						      	      	       
          </td>
          </tr>   
</table>
</div>
</html:form>
<script language="javascript">

</script>