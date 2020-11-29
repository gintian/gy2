<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<script language="javascript">
	function ifAssign()
	{
		return ( confirm('确认指派任务吗？') );
	}
</script>
<html:form action="/workflow/webclient/monitortask">
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
            <tr>
            <td align="center" class="TableRow" nowrap>
				选择&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap>
				任务号&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
                任务名称&nbsp;
            </td>
            <td align="center" class="TableRow" nowrap>
                任务状态&nbsp;
            </td>             
            <td align="center" class="TableRow" nowrap>
                任务执行者&nbsp;
            </td>             
            <td align="center" class="TableRow" nowrap>
                任务开始时间&nbsp;
            </td>  
	        	        	        
            </tr>
          </thead>		
          <hrms:extenditerate id="element" name="workListForm" property="taskListForm.list" indexes="indexes"  pagination="taskListForm.pagination" pageCount="10" scope="session">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>  
            <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="workListForm" property="taskListForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>  
            <td align="right" class="RecordRow" nowrap>
                   <bean:write name="element" property="id" filter="true"/>&nbsp;
	        </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="name" filter="true"/>&nbsp;
            </td>
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="state" filter="true"/>&nbsp;
	    </td>
            <td align="left" class="RecordRow" nowrap>
                    <bean:write name="element" property="start_actorid" filter="true"/>&nbsp;
	    </td>  	                    
            <td align="left" class="RecordRow" nowrap>
                    <bean:write name="element" property="start_date" filter="true"/>&nbsp;
            </td>  
        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="workListForm" property="taskListForm.pagination.current" filter="true" />
					页

					共
					<bean:write name="workListForm" property="taskListForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="workListForm" property="taskListForm.pagination.pages" filter="true" />
					页
			</td>
	       <td  align="right" nowrap class="tdFontcolor">
		      <p align="right"><hrms:paginationlink name="workListForm" property="taskListForm.pagination"
				nameId="taskListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr class="list3">
            <td align="center" colspan="2">
    			<input type="submit" name="b_signal" value="提交" class="mybutton" onclick="iftqsp();">
    			<!--
    			<input type="submit" name="b_rollback" value="回退上一环节">
    			-->
    			<input type="submit" name="b_end" value="终止流程" class="mybutton" onclick="return ifend();">
    			<input type="submit" name="b_reassign" value="分派" class="mybutton" onclick="document.workListForm.target='_self';validate('RS','actor_id','指派人员');return (document.returnValue && ifAssign());">
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
