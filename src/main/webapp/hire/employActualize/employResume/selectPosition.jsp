<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<head>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>
	function next()
	{
		if(document.employResumeForm.right_fields.options.length==0)
		{
			alert(PLEASE_SELECT_POSITION+"！");
			return;
		}
	
		if(document.employResumeForm.right_fields.options.length>${employResumeForm.max_count})
		{
			alert(MOST_SELECT_THREE_POSITIONS1+${employResumeForm.max_count}+MOST_SELECT_THREE_POSITIONS2);
			return;
		}
		
		var z0301="";
		for(var i=0;i<document.employResumeForm.right_fields.options.length;i++)
		{
			for(var j=0;j<document.employResumeForm.right_fields.options.length;j++)
			{
				if(j!=i&&document.employResumeForm.right_fields.options[i].value==document.employResumeForm.right_fields.options[j].value)
				{
					alert(NO_RESELECT_SAME_POSITION+"！");
					return;
				}
			}
			z0301=z0301+"~"+document.employResumeForm.right_fields.options[i].value;
		}
		
		
		returnValue=z0301.substring(1);
		window.close();
	}
 /***********双击显示岗位需求细节  fromRecommend 来源于推荐岗位标识******/
 function showDetails(fields){
 	  var vos= document.getElementsByName(fields);
	  if(vos==null)
      return false;
      var z0301=vos[0].value;
      if(z0301==null||z0301=="")
      return false;
     
      window.showModalDialog("/hire/innerEmployNetPortal/initInnerEmployPos.do?b_posDesc=link&z0301="+z0301+"&fromRecommend=recommend",1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
 }

</script>
<TITLE>e-HR</TITLE>
</head>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  
<BODY>
<base id="mybase" target="_self">
<hrms:themes></hrms:themes>
<html:form action="/hire/employActualize/employResume"  >
<table width="890px" align="center" border="0" cellpadding="0" cellspacing="0" style="margin-top:0px;margin-left:-5px;">
  <tr>  
    <td valign="top" align="center" width="100%" > 
    <%
       if(bosflag!=null&&!bosflag.equals("hcm")){
    %>
    <br>
    <%
    }
    %> 
    
 
     <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	  <thead>
       <tr>
        <td align="center" class="TableRow" nowrap colspan="3"><bean:message key="hire.recommend.position"/> &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
   
        <td width="100%" align="center" class="RecordRow" nowrap colspan="3">
          <table width='100%' >
            <tr>
             <td align="center"  width="46%">
              <table width="100%">
                 <tr>
                  <td width="100%" align="left">
                      <bean:message key="hire.alternative.position"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" ondblclick="showDetails('left_fields');" style="height:230px;width:100%;font-size:9pt;overflow:auto;">
                   		<logic:iterate id="element" name="employResumeForm" property="posList"  offset="0"> 
                   			<option value='<bean:write name="element"  property='value'  filter="false"/>' title='<bean:write name="element" property='name' filter="false"/>'><bean:write name="element" property='name' filter="false"/></option>
                   		</logic:iterate>
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button >
	            <%
			      if(bosflag!=null&&!bosflag.equals("hcm")){
			    %>
			     <br>
			     <br>
			    <%
			    }
			    %> 
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:5px;">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button >	
                </td>         
                <td width="50%" align="center">

                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  
                     <bean:message key="hire.recommend.position"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	            
 		     		<select name="right_fields" multiple="multiple" size="10"  ondblclick="showDetails('right_fields');" style="height:230px;width:100%;font-size:9pt">
                     </select>            
 		                 
                   </td>
                  </tr>
                 </table>             
                </td>               
                </tr>
              </table>             
            </td>
            </tr>
          <tr>      
          <td align="center" class="" nowrap  colspan="3">
			<%
                if(bosflag!=null&&!bosflag.equals("hcm")){
		    %>
		    <br>
		    <%
		    }
		    %> 
           <input type='button'  value="<bean:message key="button.ok"/>"  class='mybutton'   onclick='next()' style="margin-top:5px" />
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>

</html:form>



</BODY>
</html>