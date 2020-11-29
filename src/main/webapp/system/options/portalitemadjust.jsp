<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<html:form action="/system/options/portalitemadjust">
<br>
<br>

<!--查询模板指标-->
<div id="first" style="filter:alpha(Opacity=100);display=block;">
<table width="55%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ftable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="2">
		<bean:message key="system.itemadjust"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td>
   	            <html:select name="portalTailorForm" property="right_fields" multiple="multiple" size="10" style="height:230px;width:100%;font-size:9pt">
                      <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                    </html:select>   	     
   	   </td>
   	    <td width="8%" align="center">
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
          <tr>
          <td align="center"  nowrap  colspan="2" style="height:35px">
              <html:submit styleClass="mybutton" property="b_save" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.save"/>
	      </html:submit> 	
	      <html:submit styleClass="mybutton" property="b_return">
            		      <bean:message key="button.return"/>
	      </html:submit>        
          </td>
          </tr>   
</table>
</div>
</html:form>
