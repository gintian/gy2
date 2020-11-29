<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.dao.RecordVo"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	int i=0;
%>
<html:form action="/selfservice/param/friend">
<br>
<table width="70%" border="0" cellspacing="0"  align="center" cellpadding="0">
 <tr>
  <td> 
    <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap> 
            <input type="checkbox" name="selbox" onclick="batch_select(this,'friendForm.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;	    
            </td>           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="selfservice.param.friend.url"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="selfservice.param.friend.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="label.edit"/>            	
	    </td>	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="friendForm" property="friendForm.list" indexes="indexes"  pagination="friendForm.pagination" pageCount="10" scope="session">
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
            	   
     		  	 <hrms:checkmultibox name="friendForm" property="friendForm.select" value="true" indexes="indexes"/>&nbsp;
	    	   	
	    </td>            
            <td align="left" class="RecordRow" wrap>
                    &nbsp;<bean:write name="element" property="string(url)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" nowrap>
                    &nbsp;<bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
	    </td>
	    <%
	    	RecordVo vo = (RecordVo)pageContext.getAttribute("element");
	    	String a_id = vo.getString("site_id");
	    %>
            <td align="center" class="RecordRow" nowrap>
              	<a href="/selfservice/param/addfriend.do?b_query=link&encryptParam=<%=PubFunc.encrypt("a_id="+a_id)%>"><img src="/images/edit.gif" border=0></a>
	    	
	    </td>
	       	    		        	        	        
          </tr>
        </hrms:extenditerate>
       </table>
  </td>
 </tr>
 <tr>
  <td> 
     <table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="friendForm" property="friendForm.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="friendForm" property="friendForm.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="friendForm" property="friendForm.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="friendForm" property="friendForm.pagination"
				nameId="friendForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
   </table>
  </td>
 </tr>
</table>


<table  width="70%" align="center">
          <tr>
            <td align="center" height="35px;">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
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
   	   for(var i=0;i<document.friendForm.elements.length;i++)
   	   {
   			if(document.friendForm.elements[i].type=='checkbox'&&document.friendForm.elements[i].name.length>18&&document.friendForm.elements[i].name.substring(0,18)=='friendForm.select[')
   			{
   				if(document.friendForm.elements[i].checked==true)
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
