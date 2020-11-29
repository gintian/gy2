<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.train.trainexam.question.type.QuestionTypeForm"%>

<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="javascript">
  function submitDEL()
  {
  	var len=document.questionTypeForm.elements.length;
       var uu;
       for (var i=0;i<len;i++)
       {
           if (document.questionTypeForm.elements[i].type=="checkbox")
           {
              if(document.questionTypeForm.elements[i].checked==true)
              {
                uu="dd";
                break;
               }
           }
       }
       if(uu!="dd")
       {
          alert(CHOISE_DELETE_NOT);
          return false;
       }
  	if(confirm(CONFIRMATION_DEL))
  	{
      questionTypeForm.action="/train/trainexam/question/type.do?b_delete=link";
      questionTypeForm.submit();  
    }
  }
  function submitUp(id)
  {
      questionTypeForm.action="/train/trainexam/question/type.do?b_edit=link&e_flag=up&type_id="+id;
      questionTypeForm.submit();  
  }
  function submitNew()
  {
      questionTypeForm.action="/train/trainexam/question/type.do?b_edit=link&e_flag=add";
      questionTypeForm.submit();  
  }
  
  function submitUpOrder(id)
  {
      questionTypeForm.action="/train/trainexam/question/type.do?b_order=link&e_flag=uporder&type_id=" + id;
      questionTypeForm.submit();  
  }
  
  function submitDownOrder(id)
  {
      questionTypeForm.action="/train/trainexam/question/type.do?b_order=link&e_flag=downorder&type_id=" + id;
      questionTypeForm.submit();  
  }
</script>
<%
	int i=0;
	QuestionTypeForm questionTypeForm=(QuestionTypeForm)session.getAttribute("questionTypeForm");
	int maxOrder = questionTypeForm.getMaxOrder();
%>

<html:form action="/train/trainexam/question/type"><br/><br/>
<table border="0" cellspacing="0"  align="center" cellpadding="0" width="400px" >
 <tr>
   <td width="100%">   
    <td>
 </td>
 <tr>
   <td width="100%">
      <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	     <thead>
              <tr>      
               <td align="center" class="TableRow" style="display:none;"  nowrap>
		         <input type="checkbox" name="selbox" onclick="batch_select(this,'pagination.select');" title='<bean:message key="label.query.selectall"/>'>&nbsp;
               </td>  
               <td align="center" class="TableRow" style="display:none;" nowrap>
		         <bean:message key="train.quesType.type_id"/>&nbsp;
               </td>  
               <td align="center" width="60%" class="TableRow" nowrap>
		         <bean:message key="train.quesType.type_name"/>&nbsp;
               </td>                    
               <td align="center" width="15%" class="TableRow" nowrap>
                  <bean:message key="train.quesType.ques_type"/>&nbsp;         
               </td>    
               <td align="center" class="TableRow" nowrap>
		          <bean:message key="button.edit"/>&nbsp;
               </td>     
               <td align="center" class="TableRow" width="12%" nowrap="true">
		           <bean:message key="train.quesType.norder"/>
               </td>                       
   	     </thead>
   	     <% pageContext.setAttribute("maxOrder",Integer.valueOf(maxOrder)); %> 	   	  	 	 
          <hrms:paginationdb id="element" name="questionTypeForm" 
            sql_str="questionTypeForm.sqlstr" table="" 
            where_str="questionTypeForm.where"
            columns="questionTypeForm.column" 
            order_by="order by nOrder" pagerows="19" page_id="pagination" indexes="indexes" >
            
	         <% if(i%2==0){ %>
             <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
             <%  }else{ %>
             <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
             <%}%>
             
              <td align="center" class="RecordRow" style="display:none;" nowrap>
				<hrms:checkmultibox name="questionTypeForm" property="pagination.select" value="true" indexes="indexes"/>&nbsp;
              </td>                 
              <td align="center" class="RecordRow" style="display:none;" nowrap>               
               <bean:write name="element" property="type_id" filter="true"/>
              </td>   
              <td align="left" class="RecordRow"  nowrap>               
               &nbsp<bean:write name="element" property="type_name" filter="true"/>
              </td>  
              <td align="center" class="RecordRow" nowrap>               
                <logic:equal name="element" property="ques_type" value="2">
                   <bean:message key="train.quesType.ques_type.objective"/>
                </logic:equal>
                <logic:equal name="element" property="ques_type" value="1">
                   <bean:message key="train.quesType.ques_type.subjective"/>
                </logic:equal>                
              </td>  
               <td class="RecordRow" nowrap align="center">
                 <a href="###" onclick="submitUp('<bean:write name="element" property="type_id" filter="true"/>');">
                  <img src="/images/edit.gif" alt="<bean:message key="button.edit" />" border="0">
                 </a> 
               </td> 
               <td class="RecordRow" align="center" width="50" nowrap>
                 <% if(i>0){ %>            
                 <a href="###" onclick="submitUpOrder('<bean:write name="element" property="type_id" filter="true"/>');">
                  <img src="/images/up01.gif" alt="<bean:message key="button.previous" />" border="0">
                 </a> 
                 <% } else {%>
                   &nbsp;&nbsp;
                 <% } %>
                 <logic:notEqual name="element" property="norder" value="${maxOrder}">                   
                   <a href="###" onclick="submitDownOrder('<bean:write name="element" property="type_id" filter="true"/>');">
                    <img src="/images/down01.gif" alt="<bean:message key="button.next" />" border="0" >
                   </a>
                 </logic:notEqual>
                 <logic:equal name="element" property="norder" value="${maxOrder}">
                 &nbsp;&nbsp;
                 </logic:equal>
               </td> 
             <%i++;%>  
	     </tr>	     
          </hrms:paginationdb>
     </table>
     <table width="100%" class="RecordRowP"  align="center">
      <tr>
       <td valign="bottom" class="tdFontcolor">
          第<bean:write name="pagination" property="current" filter="true" />页
          共<bean:write name="pagination" property="count" filter="true" />条
          共<bean:write name="pagination" property="pages" filter="true" />页
       </td>
       <td  align="right" nowrap class="tdFontcolor">
          <p align="right">
           <hrms:paginationdblink name="questionTypeForm" property="pagination" nameId="questionTypeForm" scope="page">
             </hrms:paginationdblink>
       </td>
      </tr>
    </table>
   </td>
 </tr>
 <tr>
 <td align="center" style="height:35px;display:none;">
     <input type="button" name="tt" value="<bean:message key="button.insert" />"  class="mybutton" onclick="submitNew();">
     <input type="button" name="tdf" value="<bean:message key="button.delete" />"  class="mybutton" onclick="submitDEL();">
     <hrms:tipwizardbutton flag="workrest" target="il_body" formname="questionTypeForm"/> 
     
 </td>
 </tr>
 </table>    
</html:form>