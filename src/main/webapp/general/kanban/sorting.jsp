<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/gz/sort/sorting.js"></script>
<style type="text/css"> 
#dis_sort_table {
           border: 1px solid #eee;
           height: 230px;    
           width: 230px;            
           overflow: auto;            
           margin: 1em 1;
}
</style>
<html:form action="/general/kanban/kanban">
<table width='500' border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable" style="margin-top:5px;">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="3"  >
				<bean:message key="label.query.selectfield"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap style="border-top:none;">
              <table border="0">
              	<tr valign="bottom">
              		<td align="left" valign="bottom">
        	             <bean:message key="selfservice.query.queryfield"/>     
                    </td>
                    <td align="left" valign="bottom">   
                    </td>
                  	<td width="100%" align="left"  valign="bottom">
                   		<bean:message key="label.query.selectedsortfield"/>
                  	</td>
                  	<td width="100%" align="left" valign="bottom" >
                  	</td>
              	</tr>
                <tr>
                 <td align="center" valign="center"  width="44%">
                   <table align="center" width="100%">
	                    <tr>
	                       <td align="center">
	 		       		 		<html:select name="kanBanForm" property="orderid" multiple="multiple" ondblclick="addfield1();removeitem('orderid');"  style="height:230px;width:100%;font-size:9pt">
				 					<html:optionsCollection property="orderlist" value="dataValue" label="dataName" />
								</html:select> 
	                       </td>
	                    </tr>
                   </table>
                </td>
                <td width="8%" align="center">
                   <html:button  styleClass="smallbutton" property="b_addfield" onclick="addfield1();removeitem('orderid');">
            		     <bean:message key="button.setfield.addfield"/> 
		           </html:button >
	           	   <html:button  styleClass="smallbutton" property="b_delfield" onclick="deletefield1();" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	    	       </html:button >	     
                </td>         
                <td width="44%" align="center" >
                 	<table align="center"  width="100%" >
                  	<tr>
                  	<td width="100%"  >	                  
				       <div id="dis_sort_table">
				       	<table width="100%" border="0" class="ListTable1">
				       		<tr>
				       			<td class="TableRow" width="10%" align="left">&nbsp;</td>
				       			<td class="TableRow" width="65%" align="center"><bean:message key="field.label"/></td>
				       			<td class="TableRow" width="25%" align="center"><bean:message key="label.query.baseDesc"/><td>
				       		</tr>
				       	</table>
				       </div>    
                 	</td>
                  </tr>
                  </table>             
                </td>
                <td width="4%" align="center">
                    <html:button  styleClass="smallbutton" property="b_up" onclick="upSort1();">
            		     <bean:message key="button.previous"/> 
	           		</html:button >
	          		<html:button  styleClass="smallbutton" property="b_down" onclick="downSort1();" style="margin-top:30px;">
            		     <bean:message key="button.next"/>    
	           		</html:button >	     
                </td>                                
                </tr>
              </table>             
            </td>
            </tr>
          <tr>
          <td align="center" colspan="3" height="35px;">
                <html:button styleClass="mybutton" property="b_next" onclick="sub()">
            		<bean:message key="button.ok"/>
	      		</html:button> &nbsp;
	       		<html:button styleClass="mybutton" property="b_return" onclick="window.close();">
            		      <bean:message key="button.cancel"/>
	      		</html:button> 	       
          </td>
          </tr>   
</table>
<input type="hidden" name="sortitemid" id="sortitemid"> 
<html:hidden name="kanBanForm" property="sortitem" styleId="sortitem"/>
<script language="javascript">
defField1();
</script>
</html:form>


