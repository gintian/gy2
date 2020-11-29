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
<hrms:themes />
<html>
<head>
<script language="javascript" src="/js/dict.js"></script>
<script type="text/javascript">
	function save(){
		<%int n=0;%> 
  		<logic:iterate id="element" name="budgetingForm" property="fieldList" indexId="index"> 
  		
  			   <logic:equal name="element" property="codeitemtype" value="N">
              		 var a<%=n%>=document.getElementsByName("fieldList[<%=n%>].num");
              		 if(a<%=n%>[0].value !=''&a<%=n%>[0].value !=null){
                 	 	var myReg =/^[0-9]*$/;//非负整数
		  				if(!myReg.test(a<%=n%>[0].value)) 
		  			 	{
		    				alert("<bean:write  name="element" property="codeitemdesc"/>人数"+GZ_BUDGET_SETPATRAMS01+"！");
		    				return;
		   				}
					}
              </logic:equal>
  		 	<%n++;%>
 		</logic:iterate>
 		 var vo=new Object();
	     vo.save="save";
	     window.returnValue=vo;

	budgetingForm.action="/gz/gz_budget/budgeting/budgeting_table.do?b_save_person=link";
	budgetingForm.submit();
	window.close();
	}

</script>
 
</head>

<body>
<% int m=0;%>  
<html:form action="/gz/gz_budget/budgeting/budgeting_table.do?b_save=link" >
 		<script language='javascript' >
			document.write("<div id=\"tbl-container\"  style='position:absolute;height:"+(document.body.clientHeight-50)+";width:100%'  >");
		</script>
 <fieldset width="90%">
	<legend>
		<bean:message key="gz.info.addmen"/>
 </legend>
 <br>							
	<table width="95%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
	<tr>
		<bean:message key="gz.budget.budgeting.add.person.num"/>:
	</tr>	
	 	   <tr class="list3" >
		      <td align="center" class="TableRow" nowrap>
		    	  <bean:message key="report.actuarial_report.person_sort"/>
		      </td>
		      
		      <td align="center" class="TableRow" nowrap>
		         <bean:message key="menu.gz.personnum"/>
		 	 </td>
		   </tr>	    

	<logic:iterate id="element" name="budgetingForm" property="fieldList" indexId="index">
	      <tr>
	      	<td class="RecordRow" align="right" nowrap>
	      		<SPAN><bean:write name="element" property="codeitemdesc"/>&nbsp;&nbsp;</SPAN>
	      	</td>
	      	<td class="RecordRow" align="center"  width="60"nowrap>
	      	<input type="text" name="<%="fieldList["+index+"].num"%>" value="<bean:write name="element" property="num"/>"  ><!-- !!!!! -->
	      	</td>
	      </tr>
	     <% m++;%>
    </logic:iterate>
    </table>
    </fieldset>
          </div> 
<script language='javascript' >
	document.write("<div id='container' style='position:relative;top:"+(document.body.clientHeight-50)+";width:100%'  >");
</script>
<table align="center">
    <tr>
    	<td align="center" colspan="2">
	    	<input type="button" class="mybutton" onclick="save()" value="确定"/>

	    	<input type="button" class="mybutton" onclick="window.close()"  value="取消"/>
	    </td>
    </tr>
</table>
</div>

</html:form>
</body>
</html>