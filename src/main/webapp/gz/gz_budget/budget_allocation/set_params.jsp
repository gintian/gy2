<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<style>
#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}

</style>
<html>
<head>
<script language="javascript" src="/js/dict.js"></script>

<script type="text/javascript">

	<%if(request.getParameter("isClose")!=null&&request.getParameter("isClose").equals("1")){%>
	    window.close();
	<%}%>
	function save(j){

		<%int n=0;%> 
  		<logic:iterate id="element" name="budgetAllocationParamsForm" property="fieldList" indexId="index"> 
  		
  			   <logic:equal name="element" property="itemtype" value="N">
           		  <logic:equal name="element" property="decimalwidth" value="0">
              		 var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
              		 if(a<%=n%>[0].value !=''&a<%=n%>[0].value !=null){
                 	 	var myReg =/^[0-9]*$/;//非负整数
		  				if(!myReg.test(a<%=n%>[0].value)) 
		  			 	{
		    				alert("<bean:write  name="element" property="itemdesc"/>"+GZ_BUDGET_SETPATRAMS01+"！");
		    				return;
		   				}
					}
                 </logic:equal>
                 
               	 <logic:equal name="element" property="decimalwidth" value="2">
              		 var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
              		 if(a<%=n%>[0].value !=''){
                 	 	var myReg =/^(-?\d+)(\.\d+)?$/
		  				if(!myReg.test(a<%=n%>[0].value)) 
		  			 	{
		    				alert("<bean:write  name="element" property="itemdesc"/>"+GZ_BUDGET_SETPATRAMS02+"！");
		    				return;
		   				}
					}
                 </logic:equal>
                 
             </logic:equal>
             
             
	<logic:equal name="element" property="itemtype" value="A">
	
	
	
	</logic:equal>
	
   <logic:equal name="element" property="itemtype" value="D">
		 var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].value");
  		 if(a<%=n%>[0].value !=''){
			if(!checkDateTime(a<%=n%>[0].value)) 
			 	{
	 				alert("<bean:write  name="element" property="itemdesc"/>"+GZ_BUDGET_SETPATRAMS03+"！");
	 				return;
				}
			}
	
	</logic:equal>        
    
   <logic:equal name="element" property="itemtype" value="M">
	
	
	
	</logic:equal>        
            
            
            
  		
  		 	<%n++;%>
 		</logic:iterate>
	
	
	
	budgetAllocationParamsForm.action="/gz/gz_budget/budget_allocation/budget_allocation/setParams.do?b_save=link&&isClose=1";
	budgetAllocationParamsForm.submit();
	}
	
</script>
</head>
<body>
<% int m=0;%>  
<html:form action="/gz/gz_budget/budget_allocation/budget_allocation/setParams.do?b_save=link&&isClose=1" >
  		<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;height:"+(document.body.clientHeight-50)+";width:385px;'  >");
 		</script> 
	<table width="85%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
  <thead>
   <tr class="fixedHeaderTr" >
     <td align="center" class="TableRow" nowrap style="border-top:0px;">
    	<bean:message key="gz.budget.budget_allocation.params.name"/>
     </td>
      
     <td align="center" class="TableRow" nowrap style="border-top:0px;">
       <bean:message key="gz.budget.budget_allocation.params.value"/>
 	</td>
  </tr>	
  </thead>
 <logic:equal name="budgetAllocationParamsForm" property="zhuangtai" value="write">
	<logic:iterate id="element" name="budgetAllocationParamsForm" property="fieldList" indexId="index">
	<bean:define id="itemlength" name="element" property="itemlength"/>
		    <bean:define id="decimalwidth" name="element" property="decimalwidth"/>

		 <logic:equal name="element" property="priv" value="0">
			<input type="hidden" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  > <!--有必要，否则上面js获得不到对象  -->
	     </logic:equal> 
	     
		<logic:equal name="element" property="priv" value="1">
	      <tr >
	      	<td class="RecordRow" nowrap align="right">
	      		<SPAN>&nbsp;&nbsp;<bean:write name="element" property="itemdesc"/></SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100"nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  disabled class="inputtext"><!-- !!!!! -->
	      	</td>
	      </tr>
	     </logic:equal> 
	     
		 <logic:equal name="element" property="priv" value="2">
	      <tr>
	      	<td class="RecordRow" nowrap align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100" nowrap >
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"  />"  
		       	maxlength="<bean:write name="element" property="maxlength"/>"   class="inputtext"><!-- !!!!! -->
	      	</td>
	      </tr>
	     </logic:equal> 
	     <% m++;%>
    </logic:iterate>
    </logic:equal> 
    
    
    
    
   <!-- 非起草、驳回状态  只读 -->
    
    <logic:equal name="budgetAllocationParamsForm" property="zhuangtai" value="readonly">

	   <logic:iterate id="element" name="budgetAllocationParamsForm" property="fieldList" indexId="index">
			<input type="hidden" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  >
		 <logic:equal name="element" property="priv" value="0">
		
	     </logic:equal> 
	     
		<logic:equal name="element" property="priv" value="1">
	      <tr>
	      	<td class="RecordRow" nowrap  align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100"nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"   disabled class="inputtext">
	      	</td>
	      </tr>
	     </logic:equal> 
	     
		 <logic:equal name="element" property="priv" value="2">
	      <tr>
	      	<td class="RecordRow" nowrap  align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/> &nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100"nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  disabled class="inputtext">
	      	</td>
	      </tr>
	     </logic:equal> 
    </logic:iterate>

    </logic:equal>
   </table> 
      </div> 
<script language='javascript' >
	document.write("<div id='container' style='position:relative;top:"+(document.body.clientHeight-50)+";width:100%'  >");
</script> 
 <logic:equal name="budgetAllocationParamsForm" property="zhuangtai" value="write">
  <table align="center">
    	<tr class="list3">
    		<td>
				<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save('<%=m%>')" />
	 	    </td>
	 	    <td>
	 	   		<input type="button" class="mybutton" value="<bean:message key="button.cancel"/>" onclick="window.close()" /> 
	 	    </td>
	 	
 </table>
</logic:equal>
</div>
</html:form>
</body>
</html>