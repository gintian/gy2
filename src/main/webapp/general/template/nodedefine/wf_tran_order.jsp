<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>

<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
	function returnnodelist()
        {
	    nodeDefineForm.action="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${nodeDefineForm.tabid}";
	    nodeDefineForm.submit();     
        }
			
//-->
</script>
<html:form action="/general/template/nodedefine/wf_node_define">
<br>
<br>

<!--欲调整的流程定义顺序-->
<div id="first" style="display=block;">
<table width="65%" border="0" cellspacing="0"  align="center" cellpadding="0" class="RecordRow">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap colspan="3">
		<bean:message key="general.template.nodedefine.nodeorder"/>&nbsp;&nbsp;
            </td>            	        	        	        
           </tr>
   	  </thead>
   	  <tr>
   	   <td>
   	            <html:select name="nodeDefineForm" property="right_fields" multiple="multiple" size="10" style="height:230px;width:100%;font-size:9pt">
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
          <td align="center"  nowrap  colspan="3" style="height:35px;">
              <html:submit styleClass="mybutton" property="b_saveorder" onclick="setselectitem('right_fields');">
            		      <bean:message key="button.save"/>
	      </html:submit> 	
	     <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="javascript:returnnodelist();">        
          </td>
          </tr>   
</table>
</div>
</html:form>
