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
<html:form action="/kq/team/array/normal_array_data_add">

<%
int i=0;
%>
<div  class="fixedDiv2" style="height: 100%;border: none">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    	
    <tr>
       <td align="center" class="TableRow" nowrap>
        <input type="checkbox" name="aa" value="true" onclick="selAll()">
       </td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.class.name"/></td>
       <td align="center" class="TableRow" nowrap><bean:message key="kq.class.all.time"/></td>
   </tr>  
 
  <hrms:extenditerate id="element" name="kqClassArrayForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="10" scope="session">
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
               <input type="checkbox" name="checkbox" value='<bean:write  name="element" property="string(class_id)" filter="true"/>'>
            </td>  
            <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
            </td> 
            <td align="left" class="RecordRow" nowrap>
             <logic:notEqual name="element" property="string(onduty_1)" value="">  
                <logic:notEqual name="element" property="string(offduty_1)" value="">           
                   &nbsp;<bean:write  name="element" property="string(onduty_1)" filter="true"/>~
                   <bean:write  name="element" property="string(offduty_1)" filter="true"/>,
                </logic:notEqual>
             </logic:notEqual> 
             <logic:notEqual name="element" property="string(onduty_2)" value="">  
                <logic:notEqual name="element" property="string(offduty_2)" value="">           
                   <bean:write  name="element" property="string(onduty_2)" filter="true"/>~
                   <bean:write  name="element" property="string(offduty_2)" filter="true"/>,
                </logic:notEqual>
             </logic:notEqual> 
             <logic:notEqual name="element" property="string(onduty_3)" value="">  
                <logic:notEqual name="element" property="string(offduty_3)" value="">           
                   <bean:write  name="element" property="string(onduty_3)" filter="true"/>~
                   <bean:write  name="element" property="string(offduty_3)" filter="true"/>,
                </logic:notEqual>
             </logic:notEqual> 
             <logic:notEqual name="element" property="string(onduty_4)" value="">  
                <logic:notEqual name="element" property="string(offduty_4)" value="">           
                   <bean:write  name="element" property="string(onduty_4)" filter="true"/>~
                   <bean:write  name="element" property="string(offduty_4)" filter="true"/>,
                </logic:notEqual>
             </logic:notEqual> 
            </td>   
         </tr>
  </hrms:extenditerate>  
  <tr>
    <td colspan="3">
      <table width="100%" align="center" class="RecordRowP">
      <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="kqClassArrayForm" property="recordListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="kqClassArrayForm" property="recordListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="kqClassArrayForm" property="recordListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="kqClassArrayForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
      </tr> 
     </table>
    </td>
  </tr>  
  </table>
  
<table width="70%" align="center">
    <tr>
       <td width="60%" align="center"  nowrap>
         <input type="button" name="Submit2" value="保存" class="mybutton" onclick="getSelect();window.close();">&nbsp;
         <input type="button" name="Submit2" value="关闭" class="mybutton" onclick="window.close();">&nbsp;
       </td>
    </tr>
</table>
</div>
</html:form>