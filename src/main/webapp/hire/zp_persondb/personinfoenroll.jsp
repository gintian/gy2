<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.ZppersondbForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
     String css_url="/css/css1.css";	
%>
 <logic:equal name="zppersondbForm" property="setname" value="A01">  
  <script language="javascript">
       parent.mil_menu.location.reload();
   </script>
 </logic:equal>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript">
  function validate()
  {
    var tag=true;    
     <logic:iterate  id="element"    name="zppersondbForm"  property="zpfieldlist" indexId="index"> 
         <logic:equal name="element" property="itemtype" value="D">   
          var valueInputs=document.getElementsByName("<%="zpfieldlist["+index+"].value"%>");
          var dobj=valueInputs[0];
          tag= checkDate(dobj) && tag;      
	  if(tag==false)
	  {
	    dobj.focus();
	    return false;
	  }
        </logic:equal> 
        <logic:equal name="element" property="itemtype" value="N"> 
           <logic:lessThan name="element" property="decimalwidth" value="1"> 
             var valueInputs=document.getElementsByName("<%="zpfieldlist["+index+"].value"%>");
             var dobj=valueInputs[0];
              tag=checkNUM1(dobj) &&  tag ;  
	      if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:lessThan>
	    <logic:greaterThan name="element" property="decimalwidth" value="0"> 
	     var valueInputs=document.getElementsByName("<%="zpfieldlist["+index+"].value"%>");
             var dobj=valueInputs[0];
             tag=checkNUM2(dobj,${element.itemlength},${element.decimalwidth}) &&  tag ;  
              if(tag==false)
	      {
	        dobj.focus();
	        return false;
	      }
	    </logic:greaterThan>
	</logic:equal>  
	 <logic:equal name="element" property="itemid" value="okuserpassword">
	    var valueInputs=document.getElementsByName("<%="zpfieldlist[1].value"%>");
            var dobj=valueInputs[0];  
            var svalueInputs=document.getElementsByName("<%="zpfieldlist["+index+"].value"%>");
            var sdobj=svalueInputs[0];                     
            tag=checkpassword(dobj,sdobj);
           if(tag==false)
           {
              dobj.focus();
              return false;
           }
       </logic:equal>
      </logic:iterate>    
      
     return tag;   
  }
  
  
  function goback()
  {
  		zppersondbForm.action="/templates/menu/hire_m_menu2.do?b_query=link&module=7";
  		zppersondbForm.target="i_body";
  		zppersondbForm.submit();
  	
  }
  
  
  
  
</script>
<%
	int i=0;
	int flag=0;

%>
<hrms:themes></hrms:themes>
<body>
<br>
<html:form action="/hire/zp_persondb/personinfoenroll" onsubmit="return validate()">
  <% ZppersondbForm zppersondbForm=(ZppersondbForm)session.getAttribute("zppersondbForm");
      if(zppersondbForm.getZpfieldlist().size()>0){%>  
<script language="javascript">
  function exeButtonAction(actionStr,target_str)
   {
     // alert(actionStr);
       target_url=actionStr;
       window.open(target_url,target_str); 
   }  
</script>
<table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <html:hidden name="zppersondbForm" property="userbase"/> 
 <html:hidden name="zppersondbForm" property="actiontype"/> 
 <html:hidden name="zppersondbForm" property="a0100"/> 
 <html:hidden name="zppersondbForm" property="i9999"/> 
    <tr width="100%" align="center">
    <td align="center" width="100%" colspan="4">    
      <bean:write  name="zppersondbForm" property="existusermessage"/>&nbsp;   
     </td>
    </tr>
<logic:iterate  id="element"    name="zppersondbForm"  property="zpfieldlist" indexId="index">  
    <logic:equal name="element" property="codesetid" value="0">
      <logic:equal name="element" property="itemtype" value="D">
        <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="top">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
         </td>
         <td align="left"  nowrap valign="top">
             &nbsp;<html:text   name="zppersondbForm" property="<%="zpfieldlist["+index+"].value"%>" styleClass="textColorWrite" maxlength="${element.itemlength}" /> &nbsp;&nbsp;&nbsp;&nbsp;  
         </td> 
        <%if(flag==0){%>           
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
      <logic:equal name="element" property="itemtype" value="A">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
         <td align="right" nowrap valign="top">        
            &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
         </td>
         <td align="left"  nowrap valign="top">
             <logic:equal name="element" property="itemid" value="userpassword">
               &nbsp;<html:password  name="zppersondbForm" property="<%="zpfieldlist["+index+"].value"%>" styleClass="textColorWrite" maxlength="${element.itemlength}" />&nbsp;&nbsp;&nbsp;&nbsp;   
              </logic:equal>
              <logic:equal name="element" property="itemid" value="okuserpassword">
                &nbsp;<html:password  name="zppersondbForm" property="<%="zpfieldlist["+index+"].value"%>" styleClass="textColorWrite" maxlength="${element.itemlength}" />&nbsp;&nbsp;&nbsp;&nbsp;   
              </logic:equal>
               <logic:notEqual name="element" property="itemid" value="okuserpassword">
                 <logic:notEqual name="element" property="itemid" value="userpassword">
                  &nbsp;<html:text  name="zppersondbForm" property="<%="zpfieldlist["+index+"].value"%>" styleClass="textColorWrite" maxlength="${element.itemlength}" />&nbsp;&nbsp;&nbsp;&nbsp;   
                 </logic:notEqual>
               </logic:notEqual>
         </td>
         <%if(flag==0){%>
           </tr>
       <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:equal> 
       <logic:equal name="element" property="itemtype" value="N">
          <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="top">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
           </td>
           <td align="left"  nowrap valign="top">
               &nbsp;<html:text  name="zppersondbForm" property='<%="zpfieldlist["+index+"].value"%>' styleClass="textColorWrite" maxlength="${element.itemlength + element.decimalwidth +1}" />  &nbsp;&nbsp;&nbsp;&nbsp; 
           </td>
         <%if(flag==0){%>
           </tr>
         <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
       </logic:equal>     
      <logic:equal name="element" property="itemtype" value="M">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;%>
                 <td align="right" nowrap valign="top">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
                 </td>
                 <td align="left"  nowrap valign="top"  colspan="3">
                  &nbsp;<html:textarea name="zppersondbForm" property='<%="zpfieldlist["+index+"].value"%>'  rows="3"  cols="66" styleClass="textColorWrite"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                 </td> 
             <%       
             }else{
               flag=0;%>               
              <td colspan="2">
              </td>
              </td>
               
             <%
            if(flag==0){
              if(i%2==0){
             %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>               
                <td align="right" nowrap valign="top">        
                   &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
                </td>
                <td align="left"  nowrap valign="top" colspan="3">
                  &nbsp;<html:textarea name="zppersondbForm" property='<%="zpfieldlist["+index+"].value"%>'  rows="3"  cols="66" styleClass="textColorWrite"/>&nbsp;&nbsp;&nbsp;&nbsp; 
                </td> 
               
               <%         
             }%>
         
           <%flag=0;%>
           
          </tr>
      </logic:equal>
    </logic:equal>
    <logic:notEqual name="element" property="codesetid" value="0">
           <%
          if(flag==0){
             if(i%2==0){
            %>
              <tr class="trShallow1">            
             <%}
             else
             {%>
               <tr class="trDeep1">  
             <%}
             i++;
             flag=1;          
             }else{
               flag=0;           
             }%>
           <td align="right" nowrap valign="top">        
              &nbsp;&nbsp;&nbsp;&nbsp;<bean:write  name="element" property="itemdesc"/>&nbsp;          
           </td>
           <td align="left"  nowrap valign="top">
              <html:hidden name="zppersondbForm" property='<%="zpfieldlist["+index+"].value"%>'/>  
              &nbsp;<html:text name="zppersondbForm" property='<%="zpfieldlist["+index+"].viewvalue"%>' readonly="true"  styleClass="textColorWrite" /> 
              <img  src="/images/code.gif" onclick='javascript:openInputCodeDialog("${element.codesetid}","<%="zpfieldlist["+index+"].viewvalue"%>");'/>&nbsp;
          </td>
         <%if(flag==0){%>
           </tr>
        <%}else{%>
            <logic:equal name="element" property="rowflag" value="${index}"> 
               <td colspan="2">
               </td>
               </tr>
            </logic:equal>
        <%}%> 
      </logic:notEqual>               
  
</logic:iterate> 





 <tr>
  
  <td align="center"  nowrap colspan="4">              
           <logic:equal name="zppersondbForm" property="setname" value="A01">
              <hrms:submit styleClass="mybutton"  property="b_save">
                     <bean:message key="button.save"/>
	         </hrms:submit> 
	    </logic:equal>
            <logic:notEqual name="zppersondbForm" property="setname" value="A01">
                  <hrms:submit styleClass="mybutton"  property="b_savesub">
                     <bean:message key="button.save"/>
	          </hrms:submit> 
           </logic:notEqual>   
            <logic:equal name="zppersondbForm" property="isHandWork" value="1">
	          		<input type='button' value="<bean:message key="button.return"/>" onclick="goback()"  class="mybutton" />
	   		 </logic:equal>
    
  </td>
 </tr>    
 </table> 
 <%}else{%>
 <table width="80%" border="0" cellspacing="1"  align="center" cellpadding="1">
 <br>
 <br>
 <tr>  
     <td align="center"  nowrap>
        <bean:message key="hire.zp_persondb.nofieldinfo"/>
     </td>  
 </tr>    
 </table>  
 <%}%>
</html:form>
</body>
