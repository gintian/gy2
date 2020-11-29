<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script language="javascript">
   function getSelect()
   {
      var str="";
      for(var i=0;i<document.kqClassArrayForm.elements.length;i++)
	{
	   if(document.kqClassArrayForm.elements[i].type=="checkbox")
	   {
		    if(document.kqClassArrayForm.elements[i].checked==true&&document.kqClassArrayForm.elements[i].name!="aa")
		    {
		      var ov=document.kqClassArrayForm.elements[i].value;
		      str=str+ov+"`";
		    }
			
			
	   }
	}	
	window.returnValue=str;
   }
   var checkflag = "false";
   function selAll()
  {
      var len=document.kqClassArrayForm.elements.length;
      var i;
      if(checkflag=="false")
      {
        for (i=0;i<len;i++)
        {
           if (document.kqClassArrayForm.elements[i].type=="checkbox")
            {
              document.kqClassArrayForm.elements[i].checked=true;
            }
         }
        checkflag="true";
      }else
      {
        for (i=0;i<len;i++)
        {
          if (document.kqClassArrayForm.elements[i].type=="checkbox")
          {
            document.kqClassArrayForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
     }   
  } 
</script>
<base id="mybase" target="_self">
<html:form action="/kq/team/array/cycle_shift_group">
<%
int i=0;
%>
<div  class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    	
    <tr>
       <td align="center" class="TableRow" nowrap>
          <input type="checkbox" name="aa" value="true" onclick="selAll()">
       </td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq_shift.group.name"/></td> 
       <td align="center" class="TableRow" nowrap><bean:message key="kq_shift.group.org"/></td>       
   </tr>
    <hrms:paginationdb id="element" name="kqClassArrayForm" sql_str="kqClassArrayForm.sql_str" table="" where_str="kqClassArrayForm.where_str" columns="kqClassArrayForm.column" order_by="" page_id="pagination" pagerows="10" distinct="" keys="" indexes="indexes">
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
               <input type="checkbox" name="checkbox" value='<bean:write  name="element" property="group_id" filter="true"/>^<bean:write name="element" property="name" filter="true"/>'>
            </td>  
	    <td align="left" class="RecordRow" nowrap>           
                  &nbsp;<bean:write name="element" property="name" filter="true"/>        	
	    </td>
	     <td align="left" class="RecordRow" nowrap>
	     <div style="position:relative;margin-left:5px;">
	        <hrms:codetoname codeid="UN" name="element" codevalue="org_id" codeitem="codeitem" scope="page"/>
          	${codeitem.codename}
          	<hrms:codetoname codeid="UM" name="element" codevalue="org_id" codeitem="codeitem" scope="page"/>  	      
          	${codeitem.codename}
	        <hrms:codetoname codeid="@K" name="element" codevalue="org_id" codeitem="codeitem" scope="page"/>  	      
          	${codeitem.codename}
	    </div>
	    </td>
	    </tr>
	                
	        <%i++;%>  
    </hrms:paginationdb>
    <tr>     	    		        	        	        
     <td colspan="3">
       <table  width="100%" class="RecordRowP">
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
		          <p align="right"><hrms:paginationdblink name="kqClassArrayForm" property="pagination" nameId="kqClassArrayForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    </table>
	 </td>
	</tr>
    </table>
   
		
    <table width="70%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>
         <input type="button" name="Submit2" value="<bean:message key="button.ok"/>" onclick="getSelect();window.close();" class="mybutton">&nbsp;
         <input type="button" name="Submit2" value="<bean:message key="button.cancel"/>" onclick="window.close();" class="mybutton">&nbsp;
       </td>
    </tr>
</table>
</div>
</html:form>