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
	function openedit(node_id,nodename)
	{
	    var obj_vo=window.showModalDialog("/general/template/nodedefine/wf_node_define.do?b_edit=link&node_id=" + node_id + "&nodename=" + nodename,null,"dialogWidth=450px;dialogHeight=200px;status:no");   
	        if(obj_vo)
		{
		    var param=new Object();
		    param.acotrid=obj_vo.name;
		    param.actorname=obj_vo.fullname;		    
		    param.objecttype=obj_vo.objecttype;
		    param.nodename=obj_vo.nodename;
		    param.node_id=obj_vo.node_id;
		    param.tabid=obj_vo.tabid;
	       	    var hashvo=new ParameterSet();
	       	    hashvo.setValue("actor",param);
        	    var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010305'},hashvo); 
		}
	}
	function isSuccess(outparamters)
        {
	    nodeDefineForm.action="/general/template/nodedefine/wf_node_define.do?b_search=link&tabid=${nodeDefineForm.tabid}";
	    nodeDefineForm.submit();     
        }
        function openadd()
	{
	    var obj_vo=window.showModalDialog("/general/template/nodedefine/wf_node_define.do?b_add=link",null,"dialogWidth=450px;dialogHeight=200px;status:no");   
	        if(obj_vo)
		{
		    var param=new Object();
		    param.acotrid=obj_vo.name;
		    param.actorname=obj_vo.fullname;		    
		    param.objecttype=obj_vo.objecttype;
		    param.nodename=obj_vo.nodename;
		    param.node_id=obj_vo.node_id;
		    param.tabid=obj_vo.tabid;
	       	    var hashvo=new ParameterSet();
	       	    hashvo.setValue("actor",param);
        	    var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010307'},hashvo); 
		}
	}	
	function returntemplatelist()
	{
	    nodeDefineForm.action="/general/template/nodedefine/searchtemplatetable.do?b_search=link";
	    nodeDefineForm.submit();    
	}
	function returnywfl()
	{
	    nodeDefineForm.action="/general/operation/operationmaintence.do?b_query=link";
	    nodeDefineForm.target="il_body";
	    nodeDefineForm.submit();    
	}	
	function deletedd()
	{
	    var location=0;
	    var selected=0;
	    var len=document.nodeDefineForm.elements.length;   
	    for (var i=0;i<len;i++)
        {
         if (document.nodeDefineForm.elements[i].type=="checkbox"&&document.nodeDefineForm.elements[i].name!="selbox")
         {
            if(document.nodeDefineForm.elements[i].checked==true)
            {
               selected++;
            }
            location++;
         }
        }
        if(selected==0)
        {
          alert("请选择节点！");
          return false;
        }
        if(selected>=location)
        {
           alert("不能全部删除节点！");
           return false;
        }
	    nodeDefineForm.action="/general/template/nodedefine/wf_node_define.do?b_delete=link";
	    nodeDefineForm.submit(); 
	}		
//-->
</script>
<%int i=0;%>

<html:form action="/general/template/nodedefine/wf_node_define">
	<br>
   <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <thead>
       <tr class="TableRow">
          <td align="center" class="TableRow" nowrap >
              <input type="checkbox" name="selbox" onclick="batch_select(this,'nodeForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
 	   </td>
           <td align="center" class="TableRow" nowrap >
                <bean:message key="general.template.nodedefine.nodeid"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" nowrap >
                <bean:message key="general.template.nodedefine.nodename"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" nowrap >
                <bean:message key="general.template.nodedefine.nodeapprove"/>&nbsp;           	
 	   </td>
 	   <td align="center" class="TableRow" nowrap >
                <bean:message key="button.edit"/>&nbsp;           	
 	   </td>
 	
      </tr>
    </thead>
         <hrms:extenditerate id="element" name="nodeDefineForm" property="nodeForm.list" indexes="indexes"  pagination="nodeForm.pagination" pageCount="10" scope="session">
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
               <hrms:checkmultibox name="nodeDefineForm" property="nodeForm.select" value="true" indexes="indexes"/>&nbsp;
            </td>
             <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="node_id" filter="false"/>&nbsp;               
	    </td> 
	     <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="nodename" filter="false"/>&nbsp;               
	    </td> 
	    <td align="left" class="RecordRow" nowrap>
                <bean:write name="element" property="actorname" filter="false"/>&nbsp;               
	    </td> 
	    <td align="center" class="RecordRow" nowrap>
			<logic:equal name="nodeDefineForm" property="isOperate" value="1" > 	    
            	<img src="/images/edit.gif"  onclick="openedit('<bean:write name="element" property="node_id" filter="false"/>','<bean:write name="element" property="nodename" filter="false"/>');" style="cursor:hand"> 
	    	</logic:equal>
	    </td>            	    		        	        	        
          </tr>
        </hrms:extenditerate>
  <table> 
<table  width="80%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    		<bean:message key="label.page.serial"/>
					<bean:write name="nodeDefineForm" property="nodeForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="nodeDefineForm" property="nodeForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="nodeDefineForm" property="nodeForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="nodeDefineForm" property="nodeForm.pagination"
				nameId="nodeDefineForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="80%" align="center">
          <tr>
            <td width="100%" align="center">     
         <logic:equal name="nodeDefineForm" property="isOperate" value="1" > 
            <hrms:priv func_id="33001020,33101201,32020,32140,37040,37140,37240,37340,324010301,325010301">
	 	   <input type="button" name="returnbutton"  value="<bean:message key="button.insert"/>" class="mybutton" onclick="openadd();"> 
	 	   </hrms:priv>  
	 	   <input type="button" name="returnbutton"  value="<bean:message key="button.delete"/>" class="mybutton" onclick="deletedd();"> 
	           <hrms:submit styleClass="mybutton" property="b_order" function_id="33001023,33101204,32023,32143,37043,37143,37243,37343,324010304,325010304">
            		 <bean:message key="general.template.nodedefine.nodeorder" />
	 	   </hrms:submit> 
	 	  </logic:equal> 
	 	       <logic:equal name="nodeDefineForm" property="returnflag" value="1">
	 	         <input type="submit" name="br_returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" > 
	           </logic:equal>
	           <logic:equal name="nodeDefineForm" property="returnflag" value="0">
	 	         <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="javascript:returntemplatelist();"> 
	           </logic:equal>
	           
	     </td>
          </tr>          
 </table>
</html:form>

