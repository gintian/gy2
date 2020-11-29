<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="./ht_static.js"></script>
<script language="javascript" src="/js/validate.js"></script>

<html:form action="/ht/ctstatic/setFlds">
<div class="fixedDiv3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
            <td align="center" class="TableRow" nowrap  >
		      <bean:message key="label.query.selectfield"/>
            </td>            	        	        	        
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap >
		       <table>
                <tr>
                 	<td align="center"  width="42%">
                 		<table align="center" width="100%">
                   		 <tr>
                    		<td align="left">
                    			备选指标&nbsp;&nbsp;
                    		</td>
                   		 </tr>
                   		 <tr>
                   			 <td align="center">
                     			 <html:select name="stAnalysisForm" property="left_fields" multiple="true" style="height:350px;width:100%;font-size:9pt" ondblclick="additem2('left_fields','right_fields');">
                          			 <html:optionsCollection property="fieldsSet" value="dataValue" label="dataName"/>
                     			 </html:select>
                   			 </td>                    
                  		 </tr>                   
                   		</table>
                    </td>
	
				<td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_addfield" onclick="additem2('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
            		     <bean:message key="button.abolish"/>    
	           </html:button >	     
                </td>         
                
                
                <td width="42%" align="center">
                 
                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                     已选指标&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="stAnalysisForm" property="right_fields" size="10" multiple="true" style="height:350px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');" styleId="curr_id">
     	          		    <html:optionsCollection property="fieldsSel" value="dataValue" label="dataName"/>
                     </html:select>   
                  </td>  
                  </tr>
                  </table>             
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="mybutton" property="b_up" onclick="upItem($('curr_id'))">
            		     <bean:message key="button.previous"/> 
	           </html:button >
	           <br>
	           <br>
	           <html:button  styleClass="mybutton" property="b_down" onclick="downItem($('curr_id'))">
            		     <bean:message key="button.next"/>    
	           </html:button >	     
                </td>    
		</td>
	</tr>
	</table>
			</td>
	</tr>
	</table>
</div>
    <div class="fixedDiv3" style="margin-top:5px" align="center">
      <input type="button"  value="<bean:message key='button.save'/>" onclick="saveSetFlds();" Class="mybutton">
    </div>
</html:form>
