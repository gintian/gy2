<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page import="javax.servlet.ServletException"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<%
	int i=0;
	
	
%>

<html:form action="/selfservice/propose/upfilelist">
<table width="70%" style="margin-top:6px;" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
            <input type="checkbox" name="selbox" onclick="batch_select(this,'htmlFileListForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_list.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_list.createdate"/>&nbsp;
	    </td>
	     <td align="center" class="TableRow" nowrap>
		<bean:message key="lable.resource_plan.org_id"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.resource_listt.allowup"/>&nbsp;
	    </td>
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>
            	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="htmlFileListForm" property="htmlFileListForm.list" indexes="indexes"  pagination="htmlFileListForm.pagination" pageCount="10" scope="session">
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
     		   <hrms:checkmultibox name="htmlFileListForm" property="htmlFileListForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>            
            <td align="left" class="RecordRow" nowrap>
                   &nbsp;<bean:write name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(createdate)" filter="true"/>&nbsp;
	    </td>
	    <td align="left" class="RecordRow" nowrap>
                   <% 
                   RecordVo boardvo = (RecordVo)pageContext.getAttribute("element");
                   String unitCode = boardvo.getString("unitcode");
                   if(unitCode==null || unitCode.equals("")){
                   %>
                	   &nbsp;
                	   <bean:message key="jx.khplan.hjsj"/>
                	   &nbsp;
                	   <% 
                   }else{
                	   %>
                	   &nbsp;
                       <hrms:codetoname codeid="UN" name="element" codevalue="string(unitcode)" codeitem="codeitem" scope="page"/>     
   					<bean:write name="codeitem" property="codename" />
                       &nbsp; 
                       <% 
                   }
                   %> 
	    </td>
            <td align="center" class="RecordRow" nowrap>
            	    <logic:equal name="element" property="string(status)" value="1">
	    	   <bean:message key="datestyle.yes"/>
	    	   </logic:equal>
	    	   <logic:equal name="element" property="string(status)" value="0">
	    	   <bean:message key="datesytle.no"/>
	    	   </logic:equal>
	    	   
            	    
                   &nbsp;
	    </td>
           
            <td align="center" class="RecordRow" nowrap>
            <%
            RecordVo vo = (RecordVo)pageContext.getAttribute("element");
            String a_id = vo.getString("contentid");
             %>
            	<a href="/selfservice/propose/addhtmlfile.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a_id)%>"><img src="/images/edit.gif" border=0></a>
	    </td>
            	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>
<table  width="70%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
					<bean:write name="htmlFileListForm" property="htmlFileListForm.pagination.current" filter="true" />
				<bean:message key="label.page.sum" />
					<bean:write name="htmlFileListForm" property="htmlFileListForm.pagination.count" filter="true" />
				<bean:message key="label.page.row" />
					<bean:write name="htmlFileListForm" property="htmlFileListForm.pagination.pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>  
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right">
		          <hrms:paginationlink name="htmlFileListForm" property="htmlFileListForm.pagination"
				nameId="htmlFileListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
             <hrms:submit styleClass="mybutton" property="b_up">
            		<bean:message key="lable.fileup"/>
	 	</hrms:submit>
         	
         	<hrms:submit styleClass="mybutton" property="b_delete" onclick="return ifdel();">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
        
            </td>
          </tr>          
</table>

</html:form>
<script type="text/javascript">
<!--
function ifdel()
{	
	   var isSelected=false;
   	   for(var i=0;i<document.htmlFileListForm.elements.length;i++)
   	   {
   			if(document.htmlFileListForm.elements[i].type=='checkbox'&&document.htmlFileListForm.elements[i].name.length>24&&document.htmlFileListForm.elements[i].name.substring(0,24)=='htmlFileListForm.select[')
   			{
   				if(document.htmlFileListForm.elements[i].checked==true)
   				{
   					isSelected=true;
   					
   				}  				
   			}
   		}
   		
  		if(!isSelected)
  		{
  			alert(PLASE_SELECT_RECORD+"！");
  			return false ;
  		}else{
  			return ( confirm('确认删除选择的项目？') );
  		}		

}

//-->
</script>

