<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String manager=userView.getManagePrivCodeValue();
%>
<script type="text/javascript">
<!--
    var startrec=null;
	var params=window.dialogArguments;
	var dataset=params[0];    
	function find(currrec)
	{
	  var finditem=$F('finditem');
	  var value=$F('find_value');
	  var findarr=new Array(finditem);
	  var findvalue=new Array(value);
	  var record=dataset.find(findarr,findvalue,currrec);
	  if(record)
	  {
		dataset.setRecord(record);
		startrec=record.getNextRecord();
	  }
	}
	
	function setValue()
	{
		$('find_value').value=$F('find_viewvalue');
	}


//-->
</script>
<html:form action="/general/muster/find">
  <br>
  <br>
  <br>   
  <table width="500" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!--  <td width="10" valign="top" class="tableft"></td>
       		<td width="130" align=center class="tabcenter"><bean:message key="label.find.title"/></td>
       		<td width="10" valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td> --> 
       		<td  align=center class="TableRow"><bean:message key="label.find.title"/></td>            	      
          </tr> 
          <tr>
            <td  class="framestyle9">
               <br>
               <table border="0" cellpmoding="0" cellspacing="5"  class="DetailTable"  cellpadding="0" align="center" >     
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.find.item"/>:</td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:select name="findForm" property="finditem" size="1">
                                 <html:optionsCollection property="list" value="dataValue" label="dataName"/>
                               </html:select>&nbsp;
                          </td>
                      </tr>
                      <tr>
                	      <td align="right" nowrap class="tdFontcolor"><bean:message key="label.find.value"/>:</td>
                	      <td align="left" nowrap class="tdFontcolor">
                               <html:hidden name="findForm" property="find_value"/>                               
                               <html:text name="findForm" property="find_viewvalue" styleClass="TEXT4" onchange="setValue();"/>
                                                          
                          </td>                                 
                      </tr>                    
	           </table>	            	
          </td>
          </tr>           
          <tr class="list3">
            <td align="center" style="height:35px;">
               <html:button styleClass="mybutton"  property="b_first" onclick="find();">
                    <bean:message key="label.find.first"/>
	           </html:button>
               <html:button styleClass="mybutton"  property="b_next" onclick="find(startrec);">
                    <bean:message key="label.find.next"/>
	           </html:button>	                       
               <html:button styleClass="mybutton"  property="b_close" onclick="window.close();">
                    <bean:message key="button.close"/>
	           </html:button>
            </td>
          </tr>  
  </table>
 
</html:form>
