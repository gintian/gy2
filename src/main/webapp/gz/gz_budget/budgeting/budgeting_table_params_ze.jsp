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
	function save(j){
		var zhuangtai="${budgetingForm.zhuangtai}";
		if(zhuangtai=="readonly"){
			return;
		}
		<%int n=0;%> 
  		<logic:iterate id="element" name="budgetingForm" property="fieldList" indexId="index"> 	
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
	                 	 	var myReg =/^(-?\d+)(\.\d+)?$/   //实数
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

	budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_save=link";
	budgetingForm.submit();
	}

</script>
 
</head>
<body>
<% int m=0;%>  
<html:form action="/gz/gz_budget/budgeting/budgeting_table.do?b_save=link" >
<bean:define id="zeItemid" name="budgetingForm" property="zeItemid" />
 <span>${budgetingForm.infoStr}</span> 
   		<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;left:10px;top:30px;height:"+(document.body.clientHeight-100)+";width:98%'  >");
 		</script> 
	<table width="40%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<logic:notEqual name="budgetingForm" property="status" value="ze">
	 	   <tr class="fixedHeaderTr">
		      <td align="center" class="TableRow" nowrap style="border-top:0px;">
		    	  <bean:message key="gz.budget.budgeting.params.name"/>
		      </td>
		      
		      <td align="center" class="TableRow" nowrap style="border-top:0px;">
		         <bean:message key="gz.budget.budgeting.params.value"/>
		 	 </td>
		   </tr>	    
	   </logic:notEqual>    
	   <logic:equal name="budgetingForm" property="status" value="ze">	  
	   		 <tr class="fixedHeaderTr">
		      <td align="center" class="TableRow" nowrap style="border-top:0px;">
		    	  <bean:message key="gz.budget.budgeting.ze.name"/>
		      </td>
		      
		      <td align="center" class="TableRow" nowrap style="border-top:0px;">
		         <bean:message key="gz.budget.budgeting.ze.value"/>
		 	 <br></td>
		   </tr>
	    </logic:equal>  

  
 <logic:equal name="budgetingForm" property="zhuangtai" value="write">
	<logic:iterate id="element" name="budgetingForm" property="fieldList" indexId="index">
	
		 <logic:equal name="element" property="priv" value="0">
			<input type="hidden" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  > <!--有必要，否则上面js获得不到对象  -->
	     </logic:equal> 
	     
		<logic:equal name="element" property="priv" value="1">
	      <tr>
	      	<td class="RecordRow" nowrap align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="150" nowrap>
	      	<input type="text" class="inputtext" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  disabled><!-- !!!!! -->
	      	</td>
	      </tr>
	     </logic:equal> 
	     
		 <logic:equal name="element" property="priv" value="2">
			 <logic:equal name="element" property="itemid" value='${zeItemid}' >
		      <tr>
		      	<td class="RecordRow" nowrap align="right">
		      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
		      	</td>
		      	<td class="RecordRow" align="center"  width="150" nowrap >
		      	<input type="text" class="inputtext" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"  />"  
		      	maxlength="<bean:write name="element" property="itemlength"/>"  disabled><!-- !!!!! -->
		      	</td>
		      </tr>
		     </logic:equal> 
		     <logic:notEqual name="element" property="itemid" value='${zeItemid}'>
		     <tr>
		      	<td class="RecordRow" nowrap align="right">
		      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
		      	</td>
		      	<td class="RecordRow" align="center"  width="150" nowrap >
		      	<input type="text" name="<%="fieldList["+index+"].value"%>" class="inputtext" value="<bean:write name="element" property="value"  />"  
		      	maxlength="<bean:write name="element" property="itemlength"/>"  ><!-- !!!!! -->
		      	</td>
		      </tr>
	     	</logic:notEqual>
	   </logic:equal> 
	     <% m++;%>
    </logic:iterate>

    </logic:equal> 
    
    
    
    
   <!-- 非起草、驳回状态  只读 -->
    
    <logic:equal name="budgetingForm" property="zhuangtai" value="readonly">

	   <logic:iterate id="element" name="budgetingForm" property="fieldList" indexId="index">
	
		 <logic:equal name="element" property="priv" value="0">
		
	     </logic:equal> 
	     
		<logic:equal name="element" property="priv" value="1">
	      <tr>
	      	<td class="RecordRow" nowrap align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100" nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  disabled>
	      	</td>
	      </tr>
	     </logic:equal> 
	     
		 <logic:equal name="element" property="priv" value="2" >
	      <tr>
	      	<td class="RecordRow" nowrap align="right">
	      		<SPAN><bean:write name="element" property="itemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="100" nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].value"%>" value="<bean:write name="element" property="value"/>"  disabled>
	      	</td>
	      </tr>
	     </logic:equal> 
    </logic:iterate>

    </logic:equal>
   </table> 
   </div>
<script language='javascript' >
	document.write("<div id='container' style='position:relative;top:"+(document.body.clientHeight-95)+";width:98%'  >");
</script>
   <table width="80%" border="0" cellspacing="0" align="center" cellpadding="0">
   	 <logic:equal name="budgetingForm" property="zhuangtai" value="write">
	   	<tr class="list3">
    		<td align="center">
				&nbsp;<input type="button" class="mybutton" value="<bean:message key="button.save"/>" onclick="save('<%=m%>')" />
	 	   </td>
	 	</tr>  
	 </logic:equal>	
   </table>
<div>
</html:form>
</body>
</html>