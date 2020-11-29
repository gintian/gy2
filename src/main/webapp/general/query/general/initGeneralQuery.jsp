<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<head>
 <link href="/css/css1.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language='javascript'>
	function next()
	{
		if(document.generalQueryForm.right_fields.options.length==0)
		{
			alert("请选择指标！");
			return;
		}
		setselectitem('right_fields');
		generalQueryForm.action='/general/query/general/generalQuery.do?b_next=next';
		generalQueryForm.submit();
	
	}


</script>
<TITLE>e-HR</TITLE>
</head>
<hrms:themes></hrms:themes>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
     if(bosflag!=null&&bosflag.equals("hcm")){
%>
<style>
.layouttable{
    margin-top:10px;
    margin-left:0px;
    margin-right:0px;
}
</style>
<%
}
%>
<BODY>
<base id="mybase" target="_self">

<html:form action="/general/query/general/generalQuery">
<table width="550px" align="center" border="0" cellpadding="0" cellspacing="0" class="layouttable">
  <tr>  
    <td valign="top" align="center"> 
    <%
        if(bosflag!=null&&!bosflag.equals("hcm")){
        
    %> 
    <br>
    <%
    }
    %>
     <table width="550px" border="0" cellspacing="0"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
       <tr>
        <td align="left" class="TableRow" nowrap colspan="3"><bean:message key="button.c.query"/> &nbsp;&nbsp;
         </td>            	        	        	        
        </tr>
   	  </thead>
   	   <tr>
   
        <td width="100%" colspan="3" align="center" class="RecordRow" nowrap>
          <table>
            <tr>
             <td align="center"  width="46%">
              <table width="100%">
                 <tr>
                  <td width="100%" align="left">
                      <bean:message key="selfservice.query.queryfield"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                
                <tr>
                 <td align="center">
                  <select name="left_fields" multiple="multiple" ondblclick="additem('left_fields','right_fields');" style="height:230px;width:100%;font-size:9pt">
                   </select>
                   </td>
                  </tr>
                 </table>
                </td>
               
               <td width="8%" align="center">  
	            <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
            		     <bean:message key="button.setfield.addfield"/> 
	            </html:button>
	            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');" style="margin-top:30px;">
            		     <bean:message key="button.setfield.delfield"/>    
	            </html:button>	
                </td>         
                <td width="46%" align="center">

                 <table width="100%">
                  <tr>
                  <td width="100%" align="left">
                  
                     <bean:message key="selfservice.query.queryfieldselected"/>&nbsp;&nbsp;
                  </td>
                  </tr>
                  <tr>
                  <td width="100%" align="left">
     	            
 		     		<select name="right_fields" multiple="multiple" size="10"  ondblclick="removeitem('right_fields');" style="height:230px;width:100%;font-size:9pt">
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
          <td align="center" class="RecordRow" nowrap  colspan="3">

	  			
            	<input type='button'  class="mybutton"  value='<bean:message key="button.query.next"/>' onclick='next()' style="margin-top:5px;margin-bottom:5px;"/>
	         
       
         </td>
        </tr>   
     </table>
   </td>
  </tr>
</table>

<input type='hidden' name='tableName'/>

</html:form>

<script language="JavaScript">
  var infos=dialogArguments; 
  init();
  
  function init()
  {
  	var a_select=eval('document.generalQueryForm.left_fields')
  	document.generalQueryForm.tableName.value=infos[0];
  	for(var i=0;i<infos[1].length;i++)
  	{
  		var oOption = document.createElement("OPTION");
		oOption.text=infos[1][i][1];
		oOption.value=infos[1][i][0]+'§§'+infos[1][i][1]+'§§'+infos[1][i][2]+'§§'+infos[1][i][3];
		a_select.add(oOption);
  	}
  }
</script>

</BODY>
</html>