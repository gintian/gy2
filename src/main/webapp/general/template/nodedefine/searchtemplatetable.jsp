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
<%int i=0;%>
<html:form action="/general/template/nodedefine/searchtemplatetable">
   <table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <thead>
       <tr class="TableRow">
           <td align="center" class="TableRow" width='10%' nowrap >
                <bean:message key="general.template.nodedefine.templateid"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow"  width='60%'  nowrap >
                <bean:message key="general.template.nodedefine.templatename"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" width='30%'  nowrap >
                <bean:message key="button.edit"/>&nbsp;           	
 	   </td>
      </tr>
    </thead>
      <hrms:paginationdb id="element" name="nodeDefineForm" sql_str="nodeDefineForm.strsql" table="" where_str="" columns="nodeDefineForm.columns" order_by="" page_id="pagination" pagerows="21" distinct="" keys="" indexes="indexes">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onclick='tr_onclick(this,"#F3F5FC");'>
          <%}
          else
          {%>
          <tr class="trDeep"  onclick='tr_onclick(this,"#E4F2FC");'  >
          <%
          }
          i++;          
          %> 
	    <td align="center" class="RecordRow" nowrap>
                 &nbsp;<bean:write name="element" property="tabid" filter="false"/>&nbsp;               
	    </td> 
	     <td align="left" class="RecordRow" nowrap>
                 &nbsp;<bean:write name="element" property="name" filter="false"/>&nbsp;               
	    </td> 
	    <td align="center" class="RecordRow" nowrap>
	    	 <hrms:priv func_id="33001022,33101203,32022,324010303,325010303,32142,37042,37142,37242,37342">	
            	<a href="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=<bean:write name="element" property="tabid" filter="false"/>&returnflag=0"><img src="/images/edit.gif" border=0></a>
	  		 </hrms:priv>
	  			&nbsp; 
	    </td>
        </tr>
      </hrms:paginationdb>
  <table>
  <table  width="90%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor"><bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="nodeDefineForm" property="pagination" nameId="nodeDefineForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
</table>
</html:form>

