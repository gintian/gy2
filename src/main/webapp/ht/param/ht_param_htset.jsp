<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="ht_param.js"></script>
<html:form action="/ht/param/ht_param_htset">
	<table width="60%" border="0" align="center" cellspacing="0"  cellpadding="0" class="ListTable">
		<tr>
		<td  nowrap colspan="2" align="center">&nbsp;</td>
	</tr>
		<tr>
			<td  class="RecordRow" nowrap>
				<bean:message key="ht.param.selhtset" />
			</td>
			<td  class="RecordRow" nowrap>
				<html:select name="contractParamForm" property="htSubSet" style="width:200" onchange="changeSet()">
					<html:optionsCollection property="empSubSet" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td  class="RecordRow" nowrap>
				<bean:message key="ht.param.selhtcode" />
			</td>
			<td  class="RecordRow" nowrap>
				<html:select name="contractParamForm" property="httype" style="width:200">
					<html:optionsCollection property="codeset" value="dataValue" label="dataName" />
				</html:select>
			</td>
		</tr>
		<tr>
			<td  class="RecordRow" nowrap colspan="2">
				<bean:message key="ht.param.htrelset" />
			</td>
		</tr>
	<tr>
		<td  class="RecordRow" nowrap colspan="2">
			<table>
                <tr>
                 	<td align="center"  width="46%">
                 		<table align="center" width="100%">
                   		 <tr>
                    		<td align="left">
                    			<bean:message key="ht.param.cansel"/>&nbsp;&nbsp;
                    		</td>
                   		 </tr>
                   		 <tr>
                   			 <td align="center">
                     			 <html:select name="contractParamForm" property="left_fields" multiple="true" style="height:200px;width:100%;font-size:9pt" ondblclick="additem2('left_fields','right_fields');">
                          			 <html:optionsCollection property="empSubSet" value="dataValue" label="dataName"/>
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
                
                
                <td width="46%" align="center">
                 
                 <table width="100%" cellspacing="0"  cellpadding="0">
                  <tr>
                  <td width="100%" align="left">
                     <bean:message key="lable.performance.selectedPerMainBody"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	             <html:select name="contractParamForm" property="right_fields" size="10" multiple="true" style="height:200px;width:100%;font-size:9pt" ondblclick="removeitem('right_fields');">
     	          		   	<html:optionsCollection property="htRelSubSet" value="dataValue" label="dataName"/>
                     </html:select>   
                  </td>  
                  </tr>
                  </table>             
                </td>   
		</td>
	</tr>
	</table>
</td>
	</tr>
	</table>
	<div style="margin-top: 5px" align="center">
		<input type="button"  value="<bean:message key='button.save'/>" onclick="saveHtSet();" Class="mybutton">
	</div>
		
</html:form>