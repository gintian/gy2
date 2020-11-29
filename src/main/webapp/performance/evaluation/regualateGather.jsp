<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language="JavaScript" src="/performance/kh_plan/defineTargetItems.js"></script>
<style>
.fixedDiv_self
{ 
	overflow:auto; 
	height:300 ; 
	width:450; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}

</style>
<script>


   function getItems(elementName)
   {
		var items = document.getElementsByName(elementName);
		var itemStr='';
		for(var i=0;i<items.length;i++)
		{
			if(items[i].checked==true)
				itemStr+=items[i].value+',';
		}
		if(itemStr!='')
			itemStr=itemStr.substring(0,itemStr.length-1);
		return itemStr;
   }
	function ok(){
           var eva=document.getElementsByName("eva");
           var e_str="";
           if(eva)
			{
			   for(var i=0;i<eva.length;i++)
			   {
			      if(eva[i].checked)
			      {
			        e_str+=","+eva[i].value;
			      }
			   }
			}
		   var thevo=new Object();
		   thevo.degrees=e_str.substring(1);
		   thevo.flag="true";
		   window.returnValue=thevo;	
           window.close()
	  }
</script>
<%
	

	int i=0;
	
%>
<html:form action="/performance/evaluation/performanceEvaluation">
<%
	
	String filds=request.getParameter("filds");

	
%>
<br>
<br>
<br>
 <fieldset style="width:90%" align="center">
		          <legend><bean:message key="jx.evaluation.performanceMergeModeTrans"/></legend>
<br>
<table width='90%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	 <!--  <thead>
           <tr>
            <td align="left" class="TableRow" nowrap colspan="4">
		     <bean:message key="jx.evaluation.performanceMergeModeTrans"/>
            </td>            	        	        	        
           </tr>
   	  </thead> -->
   	  
   	   <tr >
	   	   <td align="center"  width=20%>
	   	   
		   	   <%
			    if(filds.indexOf("AVG")!=-1||filds.equals("")){
		       %>
		   	   <input type="checkbox" name="eva" value="ZAVG" checked/> 
		   	    <%
			    }else{
		       %>
		        <input type="checkbox" name="eva" value="ZAVG" /> 
		       	<%
			    }
		       %>
	   	   </td>
	   	    <td align="left"  width=30%>
	   	   		<bean:message key="jx.evaluation.jspZavg"/>
	   	   </td>
	   	   
	   	   <td align="center" width=20%>
	   	   
		   	   <%
			    if(filds.indexOf("SUM")!=-1){
		       %>
		       	<input type="checkbox" name="eva" value="ZSUM" checked/> 
		       <%
			    }else{
		       %>
		   	   	<input type="checkbox" name="eva" value="ZSUM" /> 
		   	   	<%
			    }
		       %>
	   	   </td>
	   	   <td align="left" width=30%>
	   	   		<bean:message key="jx.evaluation.jspZsum"/>
	   	   </td>
   	   </tr>
   	   
   	   
   	      <tr>
	   	   <td align="center"  width=20%>
		   	  &nbsp;&nbsp;&nbsp;&nbsp;
	   	   </td>
	   	   
	   	    <td align="left" width=30%>
	   	   		&nbsp;&nbsp;&nbsp;&nbsp;
	   	   </td>
	   	   
	   	   <td align="center"  width=20%>
		   	  &nbsp;&nbsp;&nbsp;&nbsp;
		   	   </td>
		   	   <td align="left"  width=30%>
		   &nbsp;&nbsp;&nbsp;&nbsp;
	   	   </td>
   	   </tr>

   	   <tr>
	   	   <td align="center"  width=20%>
		   	   
		   	    <%
			    if(filds.indexOf("MAX")!=-1){
		       %>
		   	   	<input type="checkbox" name="eva" value="ZMAX" checked/>
		   	   	<%
			    }else{
		       %> 
		       <input type="checkbox" name="eva" value="ZMAX" />
		        <%
			    }
		       %>
	   	   </td>
	   	   
	   	   <td align="left" width=30%>
	   	   		<bean:message key="jx.evaluation.jspZmax"/>
	   	   </td>
	   	   
	   	   <td align="center"  width=20%>
		   	   
		   	    <%
			    if(filds.indexOf("MIN")!=-1){
		       %>
		   	   	<input type="checkbox" name="eva" value="ZMIN" checked/> 
		   	   <%
			    }else{
		       %>
		       <input type="checkbox" name="eva" value="ZMIN" /> 
		        <%
			    }
		       %> 
		   	   </td>
		   	   <td align="left"  width=30%>
		   	   <bean:message key="jx.evaluation.jspZmin"/>
	   	   </td>
   	   </tr>
       </table>
       <br>
   	   </fieldset>
   	   
   	   <table width='90%' border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <BR>
   	   <tr >
          <td align="center">
         	<input type='button' class="mybutton" property="b_add"  onclick='ok()' value='<bean:message key="button.ok"/>'  />
            <input type='button' class="mybutton" property="b_delete"  onclick='window.close()' value='<bean:message key="button.cancel"/>'  />
          </td>
        </tr>
        </table>   

</html:form>

