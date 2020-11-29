<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.transaction.train.BusifieldBean"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingFactory"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingcodeBean"%>
<%@ page import="org.apache.commons.beanutils.DynaBean"%>
<jsp:useBean id="doCodeBean" class="com.hjsj.hrms.transaction.train.DoCodeBean" scope="page"/>
<jsp:useBean id="relatingFactory" class="com.hjsj.hrms.transaction.train.RelatingFactory" scope="session"/>
<%
	int i=0;
	relatingFactory.getInstance();
%>
<html:form action="/selfservice/educate/infopick/viewinfopick">
		
    <center>  <h3><bean:message key="conlumn.infopick.detailinfo"/></h3>&nbsp;</center>
       		
          <table width="500" border="0" cellpadding="0"  cellspacing="0" class="ListTable" align="center">
          <thead>
         	 <tr>
         	 		
         	    		<logic:iterate id="elementcol" name="infoPickForm" property="dynamicColDetail">
	   					 <%
	     					BusifieldBean busb=(BusifieldBean)elementcol;
	     					String str=busb.getItemid();
	     					str=str.toUpperCase();
	    					 %>
	     					 <td align="center" class="TableRow" nowrap>
	     	
							<hrms:fieldtoname name="infoPickForm" fieldname="<%=str%>" fielditem="fielditem"/>
							<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    					</td>  
	   			 </logic:iterate>  		
    				
          	</tr>
          	 </thead>
         	 <tr>
           
           	 <hrms:extenditerate id="element" name="infoPickForm" property="pickInfolst" indexes="indexes"  pagination="infoPickForm.pagination" pageCount="10" scope="session">
          	
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
    				 
	   				 
	   				 <logic:iterate id="elementcol" name="infoPickForm" property="dynamicColDetail">
            	       			 <logic:equal name="elementcol" property="itemtype" value="N">
	  	  				<td align="right" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  			</logic:equal>
	  	  			<logic:notEqual name="elementcol" property="itemtype" value="N">
	  	  				<td align="left" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  			</logic:notEqual>
            	 			<logic:equal name="elementcol" property="codesetid" value="0">
	     				 <bean:write name="element" property="${elementcol.itemid}" filter="true"/>&nbsp; 
	     	  			</logic:equal>
	  	  			<logic:notEqual name="elementcol" property="codesetid" value="0">
	  	  				<logic:equal name="elementcol" property="codeflag" value="1">
	  	  		
	  	  			<%
	  	  			DynaBean voelement1=(DynaBean)element;
	  	  			BusifieldBean busb2=(BusifieldBean)elementcol;
	  	  			RelatingcodeBean rcb2=relatingFactory.getDisplayField(busb2);
	  	  			
	  	  			String str2=busb2.getItemid();
	  	  			String strcodeid2=rcb2.getCodesetid();
	  	  			if(str2.equals("") || strcodeid2.equals(""))
	  	  			{
	  	  			out.println("&nbsp;");
	  	  			}
	  	  			else
	  	  			{
	  	  			
	  	  			out.println(doCodeBean.getRelCodeName(rcb2,voelement1.get(str2).toString()));
	  	  			}
	  	  			
	  	  			%>
	  	  		
	  	  			</logic:equal>
	  	  			<logic:notEqual name="elementcol" property="codeflag" value="1">
	  	  		
	  	  			<%
	  	  			DynaBean voelement=(DynaBean)element;
	  	  			BusifieldBean busb1=(BusifieldBean)elementcol;
	     				String str1=busb1.getItemid();
	     				String strcodeid=busb1.getCodesetid();
	     				if(str1.equals("") || strcodeid.equals(""))
	     				{
	     				out.println("&nbsp;");
	     				}
	     				else
	     				{
	     					out.println(doCodeBean.getCodeName(strcodeid,voelement.get(str1).toString()));
	     				}
	     				
	  	  			%>
 	  		
	  	  		</logic:notEqual>
	  	  	</logic:notEqual>
	  	  	 </td>
	   	 </logic:iterate>  	   
    				
  				</tr>
  		  </hrms:extenditerate>
               
                  
         </table>
         <table align="center">                                                
          <tr class="list3">
            	<td align="center" colspan="14">
         		 	
         	<hrms:submit styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	</hrms:submit>            
            </td>
          </tr>          
      </table>
     
</html:form>
