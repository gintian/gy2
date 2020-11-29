<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.kq.team.ArrayGroupForm"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	int ver=lock.getVersion();
	ver=50;
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->

<script language="javascript">
  function submitADD()
  {
      arrayGroupForm.action="/kq/team/array_set/search_array_data.do?b_tran=link&save_flag=add";
      arrayGroupForm.submit();
  }
  function submitUp(group_id)
  {
     arrayGroupForm.action="/kq/team/array_set/search_array_data.do?b_tran=link&group_id="+group_id+"&save_flag=update";
     arrayGroupForm.submit();      
  }
  function submitDEL()
  {
  	var str="";
		for(var i=0;i<document.arrayGroupForm.elements.length;i++)
			{
				if(document.arrayGroupForm.elements[i].type=="checkbox")
				{
					if(document.arrayGroupForm.elements[i].checked==true)
					{
						if(document.arrayGroupForm.elements[i].name=="selbox")
							continue;
							str+=document.arrayGroupForm.elements[i].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert("请选择！");
				return;
			}else
			{
				if(confirm("确定要删除所选记录吗？"))
    {
      arrayGroupForm.action="/kq/team/array_set/search_array_data.do?b_delete=link";
      arrayGroupForm.submit();
    }
			}
  }
  function b_reload()
  {
      <%if(ver<=40){%>
         parent.mil_menu.b.document.location = "/kq/team/array/group_tree.jsp?id=${arrayGroupForm.unCodeitemid}";  
      <%}else {%>    
          var currnode=parent.mil_menu.document.getElementById("a");        
          if(currnode==null)
           return;
	      currnode.src="/kq/team/array/group_tree.jsp?id=${arrayGroupForm.unCodeitemid}";
      <%}%>
  }
  function goback()
  {
     arrayGroupForm.action="/kq/team/array/search_array_data.do?b_search=link&a_code=${arrayGroupForm.return_code}";
     arrayGroupForm.submit();
  }
  var checkflag = "false";
  function selAll()
   {
      var len=document.arrayGroupForm.elements.length;
       var i;
    if(checkflag == "false")
    {
        for (i=0;i<len;i++)
        {
         if (document.arrayGroupForm.elements[i].type=="checkbox")
          {
             
            document.arrayGroupForm.elements[i].checked=true;
          }
        }
        checkflag = "true";
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
<%
	int i=0;
	ArrayGroupForm arrayGroupForm = (ArrayGroupForm)session.getAttribute("arrayGroupForm");
	ArrayList codesetid = arrayGroupForm.getCodesetid();
%>
<html:form action="/kq/team/array_set/search_array_data">
<table border="0" cellspacing="0"  align="center" cellpadding="0" width="70%" >
 <tr>
   <td>   
   <html:hidden name="arrayGroupForm" property="a_code"/>     
    <td>
 </td>
 </tr>
 <tr>
   <td width="70%">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     <thead>
              <tr>      
               <td align="center" class="TableRow" nowrap>
		<input type="checkbox" name="aa" value="true" onclick="selAll()">&nbsp;
               </td>         	    
               <td align="center" class="TableRow" nowrap>
                    <bean:message key="kq.shift.group.name"/>&nbsp;
               </td>  
               <td align="center" class="TableRow" nowrap>
                   所属机构&nbsp;
               </td>            
               <td align="center" class="TableRow" nowrap>
		         分配人员&nbsp;
               </td> 
               <hrms:priv func_id="270713,0C3513" module_id="">
               <td align="center" class="TableRow" nowrap>
		<bean:message key="kq.shift.group.update.name"/>&nbsp;
               </td> 
               </hrms:priv>
   	     </thead>
   	      	     		  	 	 
          <hrms:extenditerate id="element" name="arrayGroupForm" property="recordListForm.list" indexes="indexes"  pagination="recordListForm.pagination" pageCount="20" scope="session">
	     <%
               if(i%2==0){ 
             %>
             <tr class="trShallow">
             <%
               }else{
             %>
             <tr class="trDeep">
             <%}
             %>
              <td align="center" class="RecordRow" nowrap>
               <hrms:checkmultibox name="arrayGroupForm" property="recordListForm.select" value="true" indexes="indexes"/>&nbsp;
              </td>  
               <td align="left" class="RecordRow" nowrap>              
                   &nbsp;<bean:write  name="element" property="string(name)" filter="true"/>&nbsp;
               </td>  
               <td align="left" class="RecordRow" nowrap>
               <%if(codesetid.get(i).equals("UN")){ %>
                  <hrms:codetoname codeid="UN" name="element" codevalue="string(org_id)" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
               <%}else if(codesetid.get(i).equals("UM")){ %>
                  <hrms:codetoname codeid="UM" name="element" codevalue="string(org_id)" codeitem="codeitem" scope="page"/> 
                   &nbsp;<bean:write name="codeitem" property="codename" />&nbsp;   
               <%} %>
               </td>              
               <td class="RecordRow" nowrap align="center">
               <hrms:priv func_id="270712,0C3512" module_id="">
                 <a href="/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=<bean:write name="element" property="string(group_id)" filter="true"/>">
                  <img src="/images/edit.gif" border="0">
                 </a> 
                </hrms:priv>  
               </td> 
                <hrms:priv func_id="270713,0C3513" module_id="">
                <td class="RecordRow" nowrap align="center">
                 <a href="###" onclick="submitUp('<bean:write name="element" property="string(group_id)" filter="true"/>');">
                  <img src="/images/edit.gif" border="0">
                 </a>
                 </td>  
                </hrms:priv> 
             <%i++;%>  
	     </tr>	     
          </hrms:extenditerate>
     </table>       
   </td>
 </tr>
 <tr>
    <td>
      <table  width="100%" class="RecordRowP" align="left">
      <tr>
       <td valign="bottom" class="tdFontcolor">第
          <bean:write name="arrayGroupForm" property="recordListForm.pagination.current" filter="true" />
          页
          共
          <bean:write name="arrayGroupForm" property="recordListForm.pagination.count" filter="true" />
          条
          共
          <bean:write name="arrayGroupForm" property="recordListForm.pagination.pages" filter="true" />
          页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationlink name="arrayGroupForm" property="recordListForm.pagination"
                   nameId="recordListForm">
           </hrms:paginationlink>
       </td>
      </tr>
    </table> 
    </td>
 </tr>
 <tr>
 <td align="center" style="height:35px;">
     <hrms:priv func_id="270710,0C3510" module_id="">	
       <input type="button" name="tt" value="<bean:message key="button.insert"/>"  class="mybutton" onclick="submitADD();">
     </hrms:priv>
     <hrms:priv func_id="270711,0C3511" module_id="">
     <input type="button" name="tdf" value="<bean:message key="button.delete"/>"  class="mybutton" onclick="submitDEL();">
     </hrms:priv> 
      <input type="button" name="btnreturn" value='<bean:message key="kq.emp.button.return"/>' onclick="goback();" class="mybutton">
 </td>
 </tr>
 </table>    
</html:form>
<script language="javascript">
	b_reload();
</script>