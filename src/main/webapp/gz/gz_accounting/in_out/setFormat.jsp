<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_accounting.AcountingForm" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<html>
  <head>
  <%
  AcountingForm accountingForm=(AcountingForm)session.getAttribute("accountingForm");
  String m_sql=accountingForm.getSql();
  
   %>
  </head>
  <style type="text/css">
	
	#scroll_box {
	           border: 0px solid #ccc;
	           height: 260px;    
	           width: 310px;            
	           overflow: auto;            
	           margin: 1em 0;
	       }
	</style>
  <script languge='javascript' >
  	function enter()
  	{
  		var obj=eval("document.accountingForm.a");
  		var value;
  		for(var i=0;i<obj.length;i++)
  		{
  			if(obj[i].checked==true)
  				value=obj[i].value;
  		}
  		
  		var itemids="";
  		for(var i=0;i<document.accountingForm.elements.length;i++)
  		{
  			if(document.accountingForm.elements[i].type=='checkbox')
  			{
  				if(document.accountingForm.elements[i].checked==true)
  				{
  					itemids+="/"+document.accountingForm.elements[i].value;
  				}
  			}
  		}
  		if(trim(itemids)=="")
  		{
  			alert("请选择导出的项目！");
  		//	window.close();
  			return;
  		}
  		
  		var hashvo=new ParameterSet(); 	
		hashvo.setValue("salaryid",'${accountingForm.salaryid}');
		hashvo.setValue("a_code",'<%=(request.getParameter("a_code"))%>');
		hashvo.setValue("condid",'${accountingForm.condid}');
		hashvo.setValue("order_by",getEncodeStr(accountingForm.order_by.value));
		hashvo.setValue("itemids",itemids);
		hashvo.setValue("filterWhl",getEncodeStr(accountingForm.filterWhl.value));
		hashvo.setValue("sp",'${accountingForm.sp}');
		hashvo.setValue("sql",getEncodeStr(accountingForm.sql.value));
		var In_paramters="flag="+value; 	
		var request=new Request({method:'post',asynchronous:false,
		parameters:In_paramters,onSuccess:showfile,functionId:'3020071008'},hashvo);
  		
  		
  	}
  	
  	//输出 EXCEL OR xml
    function showfile(outparamters)
	{
		var fileName=outparamters.getValue("fileName"); 
    	returnValue=fileName; 
		window.close();
	}
  	
  	
  
  	function selectAll()
  	{
  		for(var i=0;i<document.accountingForm.elements.length;i++)
  		{
  			if(document.accountingForm.elements[i].type=='checkbox')
  			{
  				document.accountingForm.elements[i].checked=true;
  			}
  		}
  	}
  
  	function unselect()
  	{
  		for(var i=0;i<document.accountingForm.elements.length;i++)
  		{
  			if(document.accountingForm.elements[i].type=='checkbox')
  			{
  				document.accountingForm.elements[i].checked=false;
  			}
  		}
  	}
  	
  </script>
  <body>
<html:form action="/gz/gz_accounting/in_out">
   <table width="100%" height='100%' align="center"> 
		<tr> <td  valign="top"><Br>
					
						<fieldset align="center" style="width:90%;">
    							 <legend >导出格式</legend>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				<tr>
			                					<td  height="25" >
			                					<input type='radio' name='a' value='1' checked >&nbsp;Excel格式
			                					</td>
			                					<td>
			                					<input type='radio' name='a' value='2' >&nbsp;xml格式
			                					</td>
			                				</tr>
		                      			</table>
		                 </fieldset>
		   </td></tr>
		   <tr><td align='left' > 
		   &nbsp;&nbsp;&nbsp;&nbsp;  请指定导出的项目
		   </td></tr>
		   <tr><td align='left' valign='top' >      
		    
		    <table   cellspacing="0"  cellpadding="0"  ><tr><td>&nbsp; &nbsp;&nbsp;&nbsp;</td><td  align='left' >
		    <div id="scroll_box" style="border:1px solid;">
		         <table border="0" cellspacing="0"  cellpadding="0" >
				   	  <thead>
				        <tr class="fixedHeaderTr">
				         <td width='60' align="center" class="TableRow" style="position:relative;top:expression(this.offsetParent.scrollTop);border-top:none;border-left: none;" nowrap>
						    <bean:message key="column.select"/>
				         </td>         
				         <td width='220'  align="center" class="TableRow" style="position:relative;top:expression(this.offsetParent.scrollTop);border-right: none;border-left: none;border-top:none;" nowrap >
						  <logic:equal  name="accountingForm" property="gz_module"  value="0">
						   薪资项目
						   </logic:equal>
						    <logic:equal  name="accountingForm" property="gz_module"  value="1">
						   保险项目
						   </logic:equal>
					     </td>    
		             	</tr>
		             </thead>  
 
		              
		             <% int i=0; 
		             %>
		             <logic:iterate   id="element" name="accountingForm" property="aimDataList"  >
			               <%
					          if(i%2==0)
					          {
					          %>
					          <tr class="trShallow">
					          <%}
					          else
					          {%>
					          <tr class="trDeep">
					          <%
					          }
					          i++;          
					          %>  
					             
		             	<td width='60' align="center" class="RecordRow" style="border-left: none;border-top:none;" nowrap>
		             		<input type='checkbox' name='itemid'  value='<bean:write name="element" property="itemid" filter="true"/>'  checked />
		             	</td>
		             	<td width='210'  align="center" class="RecordRow" style="border-right: none;border-left: none;border-top:none;" nowrap>
		             		<bean:write name="element" property="itemdesc" filter="true"/>
		             	</td>
		             </tr>
		             </logic:iterate>
 				
		         </table>    
		     </div>   
		      </td></tr></table>
		      
		            
		</td></tr>
		<tr><td align='center' valign='top' >
			<input type='button' value='全选' onclick='selectAll()'  class="mybutton"  />&nbsp;
			<input type='button' value='全撤' onclick='unselect()'  class="mybutton"  />&nbsp;
			<input type='button' value='确定' onclick='enter()'  class="mybutton"  />&nbsp;
			<input type='button' value='取消' onclick='window.close()'  class="mybutton"  />
		</td></tr>
	</table> 
	<html:hidden name="accountingForm" property="filterWhl" />
	<html:hidden name="accountingForm" property="order_by" />
	<html:hidden name="accountingForm" property="sp" />
	<input type='hidden' name='sql' value='<%=m_sql%>' />
</html:form>
  </body>
</html>
