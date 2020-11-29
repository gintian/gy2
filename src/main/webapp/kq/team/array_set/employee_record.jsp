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
      var sb = false;
      for(var i=0;i<document.arrayGroupForm.elements.length;i++)
	{
	   if(document.arrayGroupForm.elements[i].type=="checkbox")
	   {
		    if(document.arrayGroupForm.elements[i].checked==true&&document.arrayGroupForm.elements[i].name!="aa")
		    {
		      var ov=document.arrayGroupForm.elements[i].value;
		      str=str+ov+",";
		      sb = true;
		    }
			
			
	   }
	}
  	if(!sb){
		alert("请选择人员！");
		return false;
    }	
	var thevo=new Object();
	if(str!="")
	{
	   thevo.flag="true";
	   thevo.str=str;
	}else
	{
	   thevo.flag="false";
	   thevo.str=str;
	}
	window.returnValue=thevo;
	window.close();
   }
   function change()
  {
      arrayGroupForm.action="/kq/team/array_group/load_emp_data_record.do?b_search=link";
      arrayGroupForm.submit();
  }
 var checkflag = "false";
 function selAll()
  {
      var len=document.arrayGroupForm.elements.length;
       var i;

    
  
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.arrayGroupForm.elements[i].type=="checkbox")
            {
              document.arrayGroupForm.elements[i].checked=true;
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.arrayGroupForm.elements[i].type=="checkbox")
          {
            document.arrayGroupForm.elements[i].checked=false;
          }
        }
        checkflag = "false";    
    }   
  } 
</script>
<html:form action="/kq/team/array_group/load_emp_data_record">
<%
int i=0;
%>
         <html:select name="arrayGroupForm" property="select_pre" size="1" onchange="change();">
                <html:optionsCollection property="kq_list" value="dataValue" label="dataName"/>	        
          </html:select> 
          
<div  class="fixedDiv2" style="height:400px;">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="">
    <tr class="fixedHeaderTr">
       <td class="TableRow" align="center" nowrap style="border-top: none;border-left: none;border-right: none;">
       <input type="checkbox" name="aa" value="true" onclick="selAll()">
       </td>
       <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap><bean:message key="hrms.b0110"/></td>  
       <td align="center" class="TableRow" style="border-top: none;border-right: none;" nowrap><bean:message key="hrmsNew.e0122"/></td>      
       <td align="center" class="TableRow" style="border-top: none;border-right: none;"nowrap><bean:message key="kq.shift.employee.name"/></td> 
   </tr>
    <hrms:paginationdb id="element" name="arrayGroupForm" sql_str="arrayGroupForm.sqlstr" table="" where_str="" columns="arrayGroupForm.column" order_by="order by b0110,e0122,a0100" page_id="pagination" pagerows="14" distinct="" keys="" indexes="indexes">
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
            <td align="center" class="RecordRow" style="border-top: none;border-right: none;border-left: none;" nowrap width="10%">               
               <input type="checkbox" name="checkbox" value='<bean:write  name="element" property="nbase" filter="true"/><bean:write  name="element" property="a0100" filter="true"/>'>
            </td>  
	    <td align="left" class="RecordRow" style="border-top: none;border-right: none;" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
	     <td align="left" class="RecordRow" style="border-top: none;border-right: none;" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          	&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;            
	    </td>
	    <td align="left" class="RecordRow" style="border-top: none;border-right: none;" nowrap>           
                  &nbsp;<bean:write name="element" property="a0101" filter="true"/>&nbsp;        	
	    </td>
	    </tr>
	                
	        <%i++;%>  
	     	    		        	        	        

    </hrms:paginationdb>
</table>
</div>
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
		          <p align="right"><hrms:paginationdblink name="arrayGroupForm" property="pagination" nameId="arrayGroupForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
    </table>
<table width="70%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>
         <input type="button" name="Submit2" value="<bean:message key="button.ok"/>"  class="mybutton" onclick="getSelect();">&nbsp;
         <input type="button" name="Submit2" value="<bean:message key="button.cancel"/>"  class="mybutton" onclick="window.close();">&nbsp;
       </td>
    </tr>    
   </table>
</html:form>
<script language="javascript">
hide_nbase_select('select_pre');
</script>