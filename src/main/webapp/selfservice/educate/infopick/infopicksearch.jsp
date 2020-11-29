<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
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

<html:form action="/selfservice/educate/infopick/infopicksearch">
<br>
	 	
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
           <tr>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="column.select"/>&nbsp;
            </td>          
            <td align="center" class="TableRow" nowrap>
            	<hrms:fieldtoname name="infoPickForm" fieldname="R1910" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
            </td>      
            
	     <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="infoPickForm" fieldname="R1906" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	     <logic:iterate id="elementcol" name="infoPickForm" property="dynamicCol">
	    <%
	     		BusifieldBean busb=(BusifieldBean)elementcol;
	     		String str=busb.getItemid();
	     		str=str.toUpperCase();
	     		if(!str.equals("R1900")){
	     		
	     %>
	      <td align="center" class="TableRow" nowrap>
	     	
		<hrms:fieldtoname name="infoPickForm" fieldname="<%=str%>" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	   </td>  
	    <%
	    		}
	    %>
	    </logic:iterate>  
	      	   
	    <td align="center" class="TableRow" nowrap>
		<bean:message key="label.view"/>            	
	    </td>
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.addpickdetail"/>            	
	    </td>
          
	    
	    	    	    	    		        	        	        
           </tr>
   	  </thead>
 <hrms:extenditerate id="element" name="infoPickForm" property="infoPickForm.list" indexes="indexes"  pagination="infoPickForm.pagination" pageCount="10" scope="session">
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
             <td align="center" class="RecordRow" nowrap>
     		   <hrms:checkmultibox name="infoPickForm" property="infoPickForm.select" value="true" indexes="indexes"/>&nbsp;
	    </td>  
	    <td align="left" class="RecordRow" nowrap>
				<a href="/selfservice/educate/infopick/modifyinfopick.do?b_query=link&a_id=<bean:write name="element" property="r1901" filter="true"/>"><bean:write name="element" property="r1910" filter="true"/>&nbsp; </a>
	    	
	    </td>        
                               
           
	     <td align="left" class="RecordRow" nowrap>
                  <bean:write name="element" property="r1906" filter="true"/>&nbsp; 
	    </td>
	    <logic:iterate id="elementcol" name="infoPickForm" property="dynamicCol">
	    	    <%
	     		BusifieldBean busb=(BusifieldBean)elementcol;
	     		String str=busb.getItemid();
	     		str=str.toUpperCase();
	     		if(!str.equals("R1900")){
	     		%>
	    
	    
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
	  	  			RelatingcodeBean rcb=relatingFactory.getDisplayField(busb2);
	  	  			
	  	  			String str2=rcb.getCodedesc();
	  	  			String strcodeid2=rcb.getCodesetid();
	  	  			if(str2.equals("") || strcodeid2.equals(""))
	  	  			{
	  	  			out.println("&nbsp;");
	  	  			}
	  	  			else
	  	  			{
	  	  				out.println(doCodeBean.getCodeName(strcodeid2,voelement1.get(str2).toString()));
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
	  	  	<% } %>
	   	 </logic:iterate>  	
           	 </td>    	   		    
	    
	     <td align="center" class="RecordRow" nowrap>
	     	
            	<a href="/selfservice/educate/infopick/viewinfopick.do?b_query=link&a_id=<bean:write name="element" property="r1901" filter="true"/>"><img src="/images/view.gif" border=0></a>
	   
	    </td>
            <td align="center" class="RecordRow" nowrap>
            	
            	<a href="/selfservice/educate/infopick/addinfopickdetail.do?b_query=link&a_id=<bean:write name="element" property="r1901" filter="true"/>"><img src="/images/edit.gif" border=0></a>
	    	
	    </td>        
	    	  
            	    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="infoPickForm" property="infoPickForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="infoPickForm" property="infoPickForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="infoPickForm" property="infoPickForm.pagination.pages" filter="true" />
					页
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="infoPickForm" property="infoPickForm.pagination"
				nameId="infoPickForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>

<table  width="70%" align="center">
          <tr>
            <td align="center">
         	<hrms:submit styleClass="mybutton" property="b_add">
            		<bean:message key="button.insert"/>
	 	</hrms:submit>
         	<hrms:submit styleClass="mybutton" property="b_delete">
            		<bean:message key="button.delete"/>
	 	</hrms:submit>
         	
        
            </td>
          </tr>          
</table>

</html:form>

