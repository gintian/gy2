<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript">
	function ifAssign()
	{
		return ( confirm('确认指派任务吗？') );
	}
</script>

<html:form action="/workflow/webclient/signal_endofstate">
      <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr class="TableRow">
    	      <td align="center" nowrap colspan="4">文书审阅</td>
          </tr>      
          <tr >
    	      <td align="right" nowrap>原始文书:</td>
    	      <td align="left" nowrap >
		<IFRAME name="nodeForm" SRC='<bean:write name="workListForm" property="view_eform_path" filter="true"/>?b_viewprocess=link&<bean:write name="workListForm" property="eform_param" filter="true"/>' TITLE='原始文书' width='100%' height='400'  frameborder="0">	
		</IFRAME>     	      
              </td>
          </tr>
          <tr >
    	      <td align="right" nowrap>各环节处理意见:</td>
    	      <td align="left"  nowrap >
      			<table width="100%" border="0" cellspacing="0" class="mainbackground">
    	      	  <hrms:extenditerate id="element" name="workListForm" property="advice_list.list" indexes="indexes"  pagination="advice_list.pagination" pageCount="50" scope="session">
                  <tr bordercolor="#C9C9C9" class="list3">
                  <td align="left" class="textclass" nowrap>
                  	<a href="/workflow/webclient/view_advice.do?b_view_advice=link&a_advice_id=<bean:write name="element" property="string(advice_id)" filter="true"/>"><bean:write name="element" property="string(actor_id)" filter="true"/></a>
      	          </td>		        	        	        
                 </tr>
                 </hrms:extenditerate>
      			</table>    	      
             </td>
          </tr>
          <tr >
    	      <td align="right" nowrap>处理意见:</td>
    	      <td align="left"  nowrap>
                  <hrms:importgeneraldata showColumn="name" valueColumn="advice_type" flag="true" paraValue="" 
                   sql="select advice_type,name from t_bpm_advice_type order by advice_type" collection="list" scope="session"/>
                  <html:select property="advice_type" size="1">
                            <html:option value="#">请选择...</html:option>
                            <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select>&nbsp;
              </td>
          </tr>                    
          <tr >
    	      <td align="right" nowrap>批示内容:</td>
    	      <td align="left"  nowrap>
    	      	<html:textarea property="advice" cols="100" rows="8"/>
              </td>
          </tr>
          <tr >
            <td align="center" colspan="2">
    			<input type="submit" name="b_signal" value="提交" class="mybutton" onclick="document.workListForm.target='_self';validate('RS','advice_type','处理意见');return (document.returnValue && iftqsp());">
    			<!--
    			<input type="submit" name="b_rollback" value="回退上一环节">
    			-->
    			<input type="submit" name="b_end" value="终止流程" class="mybutton" onclick="return ifend();">
    			<input type="submit" name="b_reassign" value="分派" class="mybutton" onclick="document.workListForm.target='_self';validate('RS','advice_type','处理意见','RS','actor_id','指派人员');return (document.returnValue && ifAssign());">
                  <hrms:importgeneraldata showColumn="fullname" valueColumn="username" flag="true" paraValue="" 
                   sql="select username,fullname from operuser where roleid=0" collection="list" scope="session"/>
                  <html:select property="actor_id" size="1">
                            <html:option value="#">请选择...</html:option>
                            <html:options collection="list" property="dataValue" labelProperty="dataName"/>
                  </html:select>&nbsp;
   			     			    			   			
            </td>
          </tr>          
      </table>
 
</html:form>
