<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/kq/kq.js"></script>
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
  function change()
  {
      kqClassArrayForm.action="/kq/team/array/cycle_shift_employee.do?b_employee=link&object_flag=0";
      kqClassArrayForm.submit();
  }
</script>
<base id="mybase" target="_self">
<html:form action="/kq/team/array/cycle_shift_employee">
<%
int i=0;
%>
<table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1">
  <thead>
     <tr height="25" >      
      <td align="left">  
         <html:select name="kqClassArrayForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select> 
       </td>
      </tr>
    </thead>
 </table>
 <div class="fixedDiv2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    	
    <tr>
       <td align="center" class="TableRow" style="border-top: none;border-left: none;" nowrap><input type="checkbox" name="aa" value="true" onclick="selAll()"></td>
       <td align="center" class="TableRow" style="border-top:none;" nowrap><bean:message key="kq.shift.employee.e0122"/></td>
       <logic:notEqual value="1" name="kqClassArrayForm" property="isPost">
       <td align="center" class="TableRow" style="border-top:none;" nowrap><bean:message key="kq.shift.employee.e01a1"/></td>
       </logic:notEqual>
       <td align="center" class="TableRow" style="border-top:none;border-right: none;" nowrap><bean:message key="kq.shift.employee.name"/></td> 
   </tr>
    <hrms:paginationdb id="element" name="kqClassArrayForm" sql_str="kqClassArrayForm.sql_str" table="" where_str="kqClassArrayForm.where_str" columns="kqClassArrayForm.column" order_by="order by b0110,e0122,e01a1,a0100,a0101" page_id="pagination" pagerows="12" distinct="" keys="" indexes="indexes">
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
            <td align="center" class="RecordRow" style="border-left:none;" nowrap>               
               <input type="checkbox" name="checkbox" value='<bean:write  name="element" property="nbase" filter="true"/>^<bean:write  name="element" property="a0100" filter="true"/>^<bean:write  name="element" property="a0101" filter="true"/>'>
            </td>  
		    <td align="left" class="RecordRow" nowrap>
	          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
	          	&nbsp;<bean:write name="codeitem" property="codename" />            
		    </td> 
		    <logic:notEqual value="1" name="kqClassArrayForm" property="isPost">
		    <td align="left" class="RecordRow" nowrap>           
	                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
	          	&nbsp;<bean:write name="codeitem" property="codename" />     
		    </td>
		    </logic:notEqual>
	
		    <td align="left" class="RecordRow" style="border-right:none;" nowrap>           
	            &nbsp;<bean:write name="element" property="a0101" filter="true"/>        	
		    </td>
	    </tr>
	                
	        <%i++;%>  
	     	    		        	        	        
    </hrms:paginationdb>
    </table>
    </div>    
 <div style="*width:expression(document.body.clientWidth-10);">
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
 </div>
    <table width="70%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>        
         <input type="button" name="Submit2" value="<bean:message key="button.ok"/>"  class="mybutton" onclick="getSelect();window.close();">&nbsp;
         <input type="button" name="Submit2" value="<bean:message key="button.cancel"/>"  class="mybutton" onclick="window.close();">&nbsp;
       </td>
    </tr>
</table>
</html:form>
<script language="javascript">
hide_nbase_select('select_pre');
</script>