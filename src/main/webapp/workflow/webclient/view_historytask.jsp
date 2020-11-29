<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>


<html:form action="/workflow/webclient/view_historytask">
      <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr>
    	      <td align="center" nowrap colspan="2" class="RecordRow">文书审阅</td>
          </tr>      
          <tr >
    	      <td width="20%" align="right" nowrap class="RecordRow">原始文书:</td>
    	      <td align="left" nowrap class="RecordRow">
				<IFRAME name="nodeForm" SRC='<bean:write name="workListForm" property="view_eform_path" filter="true"/>?b_viewprocess=link&<bean:write name="workListForm" property="eform_param" filter="true"/>' TITLE='原始文书' width='100%' height='400'  frameborder="0">	
				</IFRAME>	      
              </td>
          </tr>
          <tr >
    	      <td align="right" wrap class="RecordRow">各环节处理意见:</td>
    	      <td align="left"  nowrap class="RecordRow">
      			<table width="100%" border="0" cellspacing="0" >
    	      	  <hrms:extenditerate id="element" name="workListForm" property="advice_list.list" indexes="indexes"  pagination="advice_list.pagination" pageCount="50" scope="session">
                  <tr >
                  <td align="left" class="textclass" nowrap>
                  	<a href="/workflow/webclient/view_advice.do?b_view_advice=link&a_advice_id=<bean:write name="element" property="string(advice_id)" filter="true"/>"><bean:write name="element" property="string(actor_id)" filter="true"/></a>
      	          </td>		        	        	        
                 </tr>
                 </hrms:extenditerate>
      			</table>    	      
             </td>
          </tr>
          <tr >
    	      <td align="right" wrap class="RecordRow">处理意见:</td>
    	      <td align="left"  nowrap class="RecordRow">
				<hrms:codetoname codeid="YY" name="workListForm" codevalue="advice_vo.string(advice_type)" codeitem="codeitem"/>&nbsp;  	      
				<bean:write name="codeitem" property="codename" />&nbsp;				

              </td>
          </tr>                    
          <tr >
    	      <td align="right" wrap class="RecordRow">批示内容:</td>
    	      <td align="left"  wrap class="RecordRow">
				<bean:write name="workListForm" property="advice_vo.string(advice_value)" filter="true"/>&nbsp;
              </td>
          </tr>
          <tr >
          	<td align="center" colspan="2">
    			<input type="button" name="br_return" value="返回" class="mybutton" onclick="history.back();">          	
          	</td>
          </tr>
     </table>
 
</html:form>
