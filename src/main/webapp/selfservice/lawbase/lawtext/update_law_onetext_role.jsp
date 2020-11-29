<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
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
.fixedHeaderTr{
 	border:1px solid #C4D8EE;
}
.myleft
{
	border-left: none;
}
.mytop
{
	border-top: none;
}
.myright
{
 	border-right:none; 
}
</style>

<script language="javascript">
   function selectAll()
   {
   		var len=document.lawbaseForm.elements.length;
   		for (i=0;i<len;i++)
     	{
           	if (document.lawbaseForm.elements[i].type=="checkbox")
            {
            	document.lawbaseForm.elements[i].checked=true ;
            }
     	}
   }
   
   function deleteAll()
   {
   		var len = document.lawbaseForm.elements.length;
   		for(i=0;i<len;i++)
   		{
   			if(document.lawbaseForm.elements[i].type=="checkbox")
   				document.lawbaseForm.elements[i].checked=false;
   		}
   }
   function validate(){
       var len = document.lawbaseForm.elements.length;
       var isCorrect = false;
       for(var i =0;i<len;i++){
           if(document.lawbaseForm.elements[i].type == "checkbox"){
               if(document.lawbaseForm.elements[i].checked == true && document.lawbaseForm.elements[i].name != "select"){
               	   isCorrect = true;
               }
           }
       }
       if(isCorrect){
	   	   if(confirm("确定清除授权？"))
	   	       return true;
	   	   return false;
       }else{
		   alert("请选择需要清除的授权！");	
       }
   }
</script>
<base target="_self"/>
<html:form action="/selfservice/lawbase/update_law_onetext_role" onsubmit="return validate();">
<html:hidden name="lawbaseForm" property="a_id" styleClass="text"/>
<div class="fixedDiv2"> 
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
   	  <thead>
           <tr class="fixedHeaderTr">        
           <td align="center" class="TableRow myleft mytop" width="30" style="border-left: none;"  nowrap>
		<input type=checkbox name=selbox onclick="batch_select(this,'roleListForm.select');" title=<bean:message key='label.query.selectall' />  width=35>&nbsp;
            </td>          
            <td align="center" class="TableRow myleft mytop" nowrap>
		<bean:message key="column.name"/>&nbsp;
	    </td>
            <td align="center" class="TableRow myleft mytop myright" style="border-right: none;" nowrap>
		<bean:message key="column.desc"/>&nbsp;
	    </td>
   		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="lawbaseForm" property="roleListForm.list" indexes="indexes"  pagination="roleListForm.pagination" pageCount="${lawbaseForm.pagerows}" scope="session">
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
           <td align="center" class="RecordRow myleft mytop" nowrap>
                 <hrms:checkmultibox name="lawbaseForm" property="roleListForm.select" value='<%=i+""%>' indexes="indexes"/>&nbsp;
                 <html:hidden name="element" property="string(role_id)" styleId='<%=i+"_h"%>' styleClass="text"/>
            </td>               
            <td align="left" class="RecordRow myleft mytop" nowrap>
                   <bean:write name="element" property="string(role_name)" filter="true"/>&nbsp;
	    </td>
         
            <td align="left" class="RecordRow myleft mytop myright" style="word-break:break-all;border-right: none;">
                    <bean:write  name="element" property="string(role_desc)" filter="false"/>&nbsp;
            </td>
          </tr>
        </hrms:extenditerate>
        
</table>
</div>
<div class="fixedDiv3"> 
<table  width="100%" align="center" class="RecordRowP">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		    	<hrms:paginationtag name="lawbaseForm" pagerows="${lawbaseForm.pagerows}" property="roleListForm.pagination" scope="session" refresh="true"></hrms:paginationtag>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="lawbaseForm" property="roleListForm.pagination"
				nameId="roleListForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>
</div>
<table  width="70%" align="center">
          <tr>   
          	<td  align="center">          	
           <html:submit styleClass="mybutton" property="b_update">
            	   	清除权限
  	       </html:submit> 
  	      
           <html:button styleClass="mybutton" property="b_o_close" onclick="window.close();">
            	   	<bean:message key="button.close"/>
  	       </html:button>   	
    
                   	           	          	   	 	  
            </td>
          </tr>          
</table>

</html:form>