<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.sys.bos.portal.PortalMainForm" %>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	int i=0;
%>

<style id=iframeCss>
div{
	cursor:hand;font-size:12px;
   }
a{
text-decoration:none;color:black;font-size:12px;
}

a.a1:active {
	color: #003100;
	text-decoration: none;
}
a.a1:hover {
	color: #FFCC00;
	text-decoration: none;
}
a.a1:visited {	
	text-decoration: none;
}
a.a1:link {
	color: #003100;
	text-decoration: none;
}
.fixedtab 
	{ 
		overflow:auto; 
		height:380;
		BORDER-BOTTOM: #94B6E6 1pt solid; 
	    BORDER-LEFT: #94B6E6 1pt solid; 
	    BORDER-RIGHT: #94B6E6 1pt solid; 
	    BORDER-TOP: #94B6E6 1pt solid ; 	
	}
</style>
<script language="javascript">

   function save()
   {
     
      var role_id="";
     
      var value="";
		var value2="";
		for(var i=0;i<document.portalMainForm.elements.length;i++)
		{
			if(document.portalMainForm.elements[i].type=='checkbox'&&document.portalMainForm.elements[i].name !="selbox")
			{
				if(document.portalMainForm.elements[i].checked==false)
				{
					value+=document.portalMainForm.elements[i].value+"`";
				}
				else
					value2+=document.portalMainForm.elements[i].value+"`";
			}
		}
      var thevo=new Object();
      if(value2!="")
        thevo.role_id=value2;
      else
        thevo.role_id="";
      window.returnValue=thevo;	       
	  window.close(); 
   }
   
   function selectAll()
   {
   		var len=document.portalMainForm.elements.length;
   		for (i=0;i<len;i++)
     	{
           	if (document.portalMainForm.elements[i].type=="checkbox")
            {
            	document.portalMainForm.elements[i].checked=true ;
            }
     	}
   }
   
   function deleteAll()
   {
   		var len = document.portalMainForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.portalMainForm.elements[i].type=="checkbox")
   				document.portalMainForm.elements[i].checked=false;
   		}
   }
</script>
<base target="_self"/>
<html:form action="/system/bos/portal/portalPriv">
<div class='fixedDiv2'>
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTableF">
   	  <thead>
           <tr>
           <td align="center" class="TableRow" nowrap>
								 <input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
								
							</td>          
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.desc"/>&nbsp;
	    </td>
   		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="portalMainForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="1000" scope="session">
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
            
            <logic:equal name="element" property="flag" value="1">
										<input type='checkbox' name='id' checked  value='<bean:write name="element" property="role_id" filter="false" />' />
									</logic:equal>
									<logic:equal name="element" property="flag" value="0">
										<input type='checkbox' name='id' value='<bean:write name="element" property="role_id" filter="false" />' />				
									</logic:equal>
                
            </td>            
            <td align="left" class="RecordRow" nowrap>
                   <bean:write name="element" property="role_name" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow" style="word-break:break-all;">
                    <bean:write  name="element" property="role_desc" filter="false"/>&nbsp;
            </td>
          </tr>
        </hrms:extenditerate>
        
</table>
</div>
<div class="fixedDiv3"> 
<table width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
				<hrms:paginationtag name="portalMainForm" pagerows="${portalMainForm.pagerows}" property="roleListForm.pagination" scope="session" refresh="false"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="portalMainForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
 </div>
 		   <br/>
<table  width="70%" align="center">
          <tr>
            <td align="center">	           
  	       <html:button styleClass="mybutton" property="b_save" onclick="save();">
            	   	<bean:message key="button.save"/>
  	       </html:button> 
              <html:button styleClass="mybutton" property="b_o_close" onclick="window.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>

                   	           	          	   	 	  
            </td>
          </tr>          
</table>

</html:form>
<script language="javascript">
 
</script>
