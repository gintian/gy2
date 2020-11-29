<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript">
 function IsDigit() 
  { 
    return ((event.keyCode >= 46) && (event.keyCode <= 57)); 
  } 
  var date_desc;
  var div_id;
    function showObjectSelectBox(srcobj,id)
   {
      
          date_desc=srcobj;
          Element.show(id); 
          div_id=id;  
          var pos=getAbsPosition(srcobj);
	  with($(id))
	  {
	        style.position="absolute";
    		style.posLeft=pos[0]-1;
		style.posTop=pos[1]-1+srcobj.offsetHeight;
		style.width=(srcobj.offsetWidth<150)?150:srcobj.offsetWidth+1;
          }                 
      
   }
   function setSelectValue(obj_select,id)
   {
      var values=obj_select.options[obj_select.selectedIndex].value;     
      var input=document.getElementById(id);      
      input.value=values;       
      event.srcElement.releaseCapture();     
   }
   
   function goBack()
   {
      questionTypeForm.action="/train/trainexam/question/type.do?b_query=link&amp;returnvalue=";
      questionTypeForm.submit(); 
   }
</script>
<hrms:themes />
<html:form action="/train/trainexam/question/type">
   <br><br>
  <table width="400" border="0" cellpadding="1" cellspacing="0" align="center" class="ftable">
   <tr height="20">
     <td  colspan="2" align=center class="TableRow">
       <bean:message key="train.quesType"/>
     </td>             	      
   </tr>  
   <tr>
     <td width="30%" align="right">
       <bean:message key="train.quesType.type_name"/>&nbsp;
     </td>
     <td  align="left">
       <html:text name="questionTypeForm" property="quesType.string(type_name)" styleClass="textColorWrite"/>  
     </td>
   </tr>
      
     <tr>
         <td width="30%" align="right">
           <bean:message key="train.quesType.ques_type"/>&nbsp;
         </td>
         <td align="left">   
           <logic:greaterThan name="questionTypeForm" property="quesType.string(type_id)" value="6">             
	           <html:select name="questionTypeForm" property="quesType.string(ques_type)" size="1" >                   
               <html:option value="2">
                 <bean:message key="train.quesType.ques_type.objective"/>
               </html:option>
               <html:option value="1">
                 <bean:message key="train.quesType.ques_type.subjective"/>
               </html:option>
             </html:select>
           </logic:greaterThan>
           <logic:lessThan name="questionTypeForm" property="quesType.string(type_id)" value="7">
             <logic:equal name="questionTypeForm" property="quesType.string(ques_type)" value="2">
               <bean:message key="train.quesType.ques_type.objective"/>
             </logic:equal>
             <logic:equal name="questionTypeForm" property="quesType.string(ques_type)" value="1">
               <bean:message key="train.quesType.ques_type.subjective"/>
             </logic:equal>
           </logic:lessThan>
         </td>
     </tr>
     
     <tr>
        <td align="center" style="height:35px;" colspan="2">
           <hrms:submit styleClass="mybutton" property="b_save">
             <bean:message key="button.save"/>
	       </hrms:submit>
	       <input type="button" name="btnreturn" value='<bean:message key="button.return"/>' 
	         onclick="goBack();" class="mybutton">	
        </td>
     </tr>
   </table>
</html:form>