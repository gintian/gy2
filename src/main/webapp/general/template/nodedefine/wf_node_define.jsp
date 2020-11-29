<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<script type="text/javascript">
<!--
	function query()
	{
	    taskDeskForm.action="/general/template/ins_obj_list.do?b_query=link&sp_flag=${taskDeskForm.sp_flag}";
	    taskDeskForm.submit();     		
	}

	function selectobject()
	{
     	 var return_vo=select_org_emp_dialog(1,2,1,0);   
		 if(return_vo)
		 {
		    $('actorid').value=return_vo.content;
		    $('actorname').value=return_vo.title;
		    return true;
	 	}	
	 	else
	 		return false;
	}	
//-->
</script>
<html:form action="/general/template/nodedefine/wf_node_define">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <thead>
       <tr class="TableRow">
           <td align="center" class="TableRow" nowrap >
                <bean:message key="rsbd.wf.name"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" nowrap >
                <bean:message key="general.template.nodedefine.templatename"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" nowrap >
                <bean:message key="button.edit"/>&nbsp;           	
 	   </td>
      </tr>
    </thead>
     <hrms:paginationdb id="element" name="nodeDefineForm" sql_str="nodeDefineForm.strsql" table="" where_str="" columns="taskDeskForm.columns" order_by="" page_id="pagination" pagerows="21" distinct="" keys="" indexes="indexes">
	 <tr>
         </tr>
      </hrms:paginationdb>
  </table>
  <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    		<bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="taskDeskForm" property="pagination" nameId="taskDeskForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</html:form>

