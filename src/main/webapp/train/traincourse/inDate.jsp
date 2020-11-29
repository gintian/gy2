<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String bosflag="";
	if(userView != null){	  
	  	bosflag=userView.getBosflag();
	  	bosflag=bosflag!=null?bosflag:"";                
	}
%>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/meizzDate.js"></script>
<% int i=0;
%>
<script language="JavaScript" >
   var checkflag = "false";
   function allSet()
   {
      var obj=document.getElementById("status");   
	  if( obj.checked==true)
	  {
	    checkflag = "false";
	    selAll();
	  }else
	  {
	     checkflag = "true";
	     selAll();
	  }	   
    }
  var checkflag = "false";

  function selAll()
  {
      var len=document.trainCourseForm.elements.length;
      var i;
     if(checkflag=="false")
     {
        for (i=0;i<len;i++)
        {
           if (document.trainCourseForm.elements[i].type=="checkbox")
            {
              document.trainCourseForm.elements[i].checked=true;
              if(document.trainCourseForm.elements[i].id!="status")
              {
                document.trainCourseForm.elements[i].disabled=true;
              }
            }
         }
        checkflag="true";
     }else
     {
        for (i=0;i<len;i++)
        {
          if (document.trainCourseForm.elements[i].type=="checkbox")
          {
            document.trainCourseForm.elements[i].checked=false;
            document.trainCourseForm.elements[i].disabled=false; 
          }
        }
        checkflag = "false";    
    } 
        
  } 
  function saveRe()
  {
       var len=document.trainCourseForm.elements.length;
       var i;
       var flag="false";
       var tabArr="";
       var status="0";
       for (i=0;i<len;i++)
       {
           if (document.trainCourseForm.elements[i].type=="checkbox")
           {
             if(document.trainCourseForm.elements[i].checked==true)
             {
                 flag="true";
                 tabArr+=document.trainCourseForm.elements[i].value+",";
             }
           }
      }
      var obj=document.getElementById("status");   
	  if( obj.checked==true){
	  	status="1";
	  }
      if(flag=="true")
      {
         if(confirm("您确定要删除所选表的数据吗！")){
           	var hashvo=new ParameterSet();
   			hashvo.setValue("checkinfor","1");
   			hashvo.setValue("tabArr",tabArr);
   			hashvo.setValue("status",status);
			var request=new Request({method:'post',asynchronous:false,functionId:'2020020224'},hashvo);
         	alert("数据初始化成功!");
         }
      }else
      {
         alert("请选择删除数据的业务表！");
         return false;
      }
  }
  function returnFirst(){
   	document.location= "/general/tipwizard/tipwizard.do?br_train=link";
  }
</script>
<hrms:themes/>
<html:form action="/train/traincourse/inDate" onsubmit="return validate()">
	<br>
<table width="480" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap>
		&nbsp;&nbsp;<bean:message key="train.Initialization"/>	
            </td>            	        	        	        
           </tr>
   	  </thead>
   	   <tr>
            <td width="100%" align="center" class="RecordRow" nowrap>
            <br>
            <table align="center" width="100%">
             <tr>
              <td>
               <fieldset align="center" style="width:95%;">
                  <legend ><bean:message key="kq.init.select"/> </legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                  <tr >
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R01" value="R01"/><bean:message key="train.r01"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R04" value="R04"/><bean:message key="train.R04"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R10" value="R10"/><bean:message key="train.R10"/>    
                   </td>
                   <td align="left" nowrap valign="middle">        
                      <input type="checkbox" name="R11" value="R11"/><bean:message key="train.R11"/>
                   </td>
                  </tr>
                  <tr >
                   <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R13" value="R13"/><bean:message key="train.R13"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R07" value="R07"/><bean:message key="train.R07"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R31" value="R31"/><bean:message key="train.R31"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                      <input type="checkbox" name="R41" value="R41"/><bean:message key="train.R41"/>
                   </td>
                  </tr>    
                  <tr >
                   <td align="left" nowrap valign="middle">        
                   <input type="checkbox" name="R40" value="R40"/><bean:message key="train.R40"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                   <input type="checkbox" name="R37" value="R37"/><bean:message key="train.R37"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                    <input type="checkbox" name="R25" value="R25"/><bean:message key="train.R25"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R45" value="R45"/><bean:message key="train.R45"/>
                   </td>	
                  </tr> 
                  <tr>
                   <td align="left" nowrap valign="middle">        
                     <input type="checkbox" name="R59" value="R59"/><bean:message key="train.R59"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                     <input type="checkbox" name="R61" value="R61"/><bean:message key="train.R61"/>
                   </td>
                    <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R16" value="R16"/><bean:message key="train.R16"/>
                   </td>
                    <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R19" value="R19"/><bean:message key="train.R19"/>
                   </td>
                                    
                  </tr>  
                  <tr>
                    <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R22" value="R22"/><bean:message key="train.R22"/>
                   </td> 
                    <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R28" value="R28"/><bean:message key="train.R28"/>
                   </td>	   
                    <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R47" value="R47"/><bean:message key="train.R47"/>
                   </td>
                   <td align="left" nowrap valign="middle">        
                       <input type="checkbox" name="R57" value="R57"/><bean:message key="train.R57"/>
                   </td>                                 
                  </tr>
                  
                  <hrms:priv module_id="39">
                  <tr>
	                   <td align="left" nowrap valign="middle">        
	                       <input type="checkbox" name="R50" value="R50"/><bean:message key="train.R50"/>
	                   </td>	
	                   <td align="left" nowrap valign="middle">        
	                       <input type="checkbox" name="R51" value="R51"/><bean:message key="train.R51"/>
	                   </td>
                       <td align="left" nowrap valign="middle">        
                           <input type="checkbox" name="Rsc" value="tr_selected_course,tr_selected_lesson"/><bean:message key="train.course.lesson"/>
                       </td>
                  </tr>
                  </hrms:priv>
                  <hrms:priv module_id="40">
	                  <tr>                   
		                   <td align="left" nowrap valign="middle">        
		                       <input type="checkbox" name="R53" value="R53"/><bean:message key="train.R53"/>
		                   </td>	
		                   <td align="left" nowrap valign="middle">        
	                       		<input type="checkbox" name="R52" value="R52"/><bean:message key="train.R52"/>
	                       </td>   
		                    <td align="left" nowrap valign="middle">        
		                       <input type="checkbox" name="R54" value="R54"/><bean:message key="train.R54"/>
		                   </td>
		                    <td align="left" nowrap valign="middle">        
		                       <input type="checkbox" name="R55" value="R55"/><bean:message key="train.R55"/>
		                   </td>
	                  </tr> 
	                  <tr>
		                  <td align="left" nowrap valign="middle">        
		                      <input type="checkbox" name="Rse" value="tr_selfexam_test,tr_exam_paper,tr_exam_answer,tr_lesson_paper"/><bean:message key="train.selfexam.exam"/>
		                  </td>  
	                  </tr>
                  </hrms:priv> 
                </table>       
	       </fieldset>
	       <!-- 
	       <fieldset align="center" style="width:97%;">
                  <legend >全选状态</legend>
                 <table border="0" cellspacing="0"  align="center" cellpadding="2" width="95%">
                  <tr>
        	    	<td align="left" nowrap valign="middle" height="40"> 
        	     	<input type="checkbox" name="all_init" value="1" onclick="allSet();" id="status"> 
        	    	培训管理所有数据全部归为初始状态           
                    </td>	                    
                  </tr>  
                </table>       
	       </fieldset>
	        -->
              </td>
             </tr>
             <tr>
              <td>
               <fieldset align="center" >
             <table border="0" cellspacing="0"  align="left" cellpadding="2" width="100%">
	             <tr>
		             <td align="left" nowrap valign="middle" >        
		                <input type="checkbox" name="all_init" value="1" onclick="allSet();" id="status"><bean:message key="button.all.select"/>
		             </td>
	             </tr>
	             <tr>
	              <td align="center" width="100%" height="30" valign="middle">
	                 <table border="0" cellspacing="0" cellpadding="2" width="95%">
	                   <tr>
	                    <td width="80%">
	                      &nbsp;&nbsp;
	                      <font color="black">
	                      	<bean:message key="train.explain"/>
	                      </font>
	                    </td>
	                    <td align="left">                    
		          
	                    </td>
	                   </tr>
		         </table>
	              </td>
	              </tr>
	              </table>
	               </fieldset>
            </table> 
          </td>
          <tr>
            <td align="center" height="40">
             	 		<input type="button" name="b_next" value="<bean:message key="button.ok"/>"  onclick="saveRe();" class="mybutton"> 
				 <%if(bosflag.equals("hl")){%>
				 <!-- //导向图没有做这个导向
				 <input type='button' class="mybutton" name="returnButton"
					onclick='returnFirst();'
					value='<bean:message key='reportcheck.return'/>' /> -->
				 <%} %>
            </td>            	        	        	        
           </tr>
 </table>	
</html:form>


