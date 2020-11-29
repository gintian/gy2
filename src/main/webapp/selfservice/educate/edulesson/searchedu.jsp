<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.transaction.train.BusifieldBean"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingFactory"%>
<%@ page import="com.hjsj.hrms.transaction.train.RelatingcodeBean"%>
<%@ page import="com.hrms.frame.dao.RecordVo"%>
<jsp:useBean id="doCodeBean" class="com.hjsj.hrms.transaction.train.DoCodeBean" scope="page"/>
<jsp:useBean id="relatingFactory" class="com.hjsj.hrms.transaction.train.RelatingFactory" scope="session"/>
<%
	int i=0;
	relatingFactory.getInstance();
%>

<html:form action="/selfservice/educate/edulesson/searchedu">
 <br>
<table width="70%" border="0" cellspacing="1"  align="center" cellpadding="1" class="ListTable">
   	  <thead>
   	 
           <tr>
                  
            <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="eduForm" fieldname="R3130" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            
	     <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="eduForm" fieldname="R3110" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="eduForm" fieldname="R3115" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
           
	     <td align="center" class="TableRow" nowrap>
		<hrms:fieldtoname name="eduForm" fieldname="R3116" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>  
	    <logic:iterate id="elementcol" name="eduForm" property="dynamicCol">
	    <%
	     		BusifieldBean busb=(BusifieldBean)elementcol;
	     		String str=busb.getItemid();
	     		str=str.toUpperCase();
	     %>
	      <td align="center" class="TableRow" nowrap>
	     	
		<hrms:fieldtoname name="eduForm" fieldname="<%=str%>" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>  
	    </logic:iterate>  	    	    
           
            <td align="center" class="TableRow" nowrap>
		<bean:message key="conlumn.infopick.educate.edulesson.teacher"/>&nbsp;            	
	    </td>
	      
	    <td align="center" class="TableRow" nowrap>
	    	<hrms:fieldtoname name="eduForm" fieldname="R3126" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
	    	<hrms:fieldtoname name="eduForm" fieldname="R3128" fielditem="fielditem"/>
		<bean:write name="fielditem" property="dataValue"/>&nbsp;
	    </td>
            <td align="center" class="TableRow" nowrap>
	    	<bean:message key="conlumn.infopick.educate.edulesson.student"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
	    	<bean:message key="conlumn.infopick.educate.edulesson.infomation"/>&nbsp;
	    </td>
	    <td align="center" class="TableRow" nowrap>
	    	<bean:message key="conlumn.infopick.educate.edulesson.stulesson"/>&nbsp;
	    </td>	    	    		        	        	        
           </tr>
   	  </thead>
          <hrms:extenditerate id="element" name="eduForm" property="eduForm.list" indexes="indexes"  pagination="eduForm.pagination" pageCount="10" scope="session">
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
                  
            	<td align="left" class="RecordRow" nowrap>
                   	<bean:write name="element" property="string(r3130)" filter="true"/>&nbsp;
	    	</td>
                 
           	 <td align="right" class="RecordRow" nowrap>
                  	<bean:write name="element" property="string(r3110)" filter="true"/>&nbsp; 
	    	</td> 	                
            	<td align="left" class="RecordRow" nowrap>
            	 	<bean:write name="element" property="string(r3115)" filter="true"/>&nbsp;
            	</td>
	    	<td align="left" class="RecordRow" nowrap>
	    		<bean:write name="element" property="string(r3116)" filter="true"/>&nbsp; 
            	</td>
            	
            	 <logic:iterate id="elementcol" name="eduForm" property="dynamicCol">
            	       
            	       <logic:equal name="elementcol" property="itemtype" value="N">
	  	  				<td align="right" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  	</logic:equal>
	  	  	<logic:notEqual name="elementcol" property="itemtype" value="N">
	  	  				<td align="left" class="RecordRow" width="100" style="word-break:break-all" nowrap>
	  	  	</logic:notEqual>
            	 	<logic:equal name="elementcol" property="codesetid" value="0">
            	 	 <bean:write name="element" property="string(${elementcol.itemid})" filter="true"/>&nbsp; 
	     		</logic:equal>
	  	  	<logic:notEqual name="elementcol" property="codesetid" value="0">
	  	  		<logic:equal name="elementcol" property="codeflag" value="1">
	  	  		
	  	  		
	  	  		<%
	  	  			RecordVo voelement1=(RecordVo)element;
	  	  			BusifieldBean busb2=(BusifieldBean)elementcol;
	  	  			
	  	  			RelatingcodeBean rcb=relatingFactory.getDisplayField(busb2);
	  	  			
	  	  			String str2=busb2.getItemid();
	  	  			String strcodeid2=rcb.getCodesetid();
	  	  			if(str2.equals("") || strcodeid2.equals(""))
	  	  			{
	  	  			out.println("&nbsp;");
	  	  			}
	  	  			else
	  	  			{
	  	  			out.println(doCodeBean.getRelCodeName(rcb,voelement1.getString(str2)));
	  	  			
	  	  			}
	  	  			
	  	  		%>
	  	  		
	  	  		
	  	  		</logic:equal>
	  	  		<logic:notEqual name="elementcol" property="codeflag" value="1">
	  	  		
	  	  		<%
	  	  			RecordVo voelement=(RecordVo)element;
	  	  			BusifieldBean busb1=(BusifieldBean)elementcol;
	     				String str1=busb1.getItemid();
	     				String strcodeid=busb1.getCodesetid();
	     				if(str1.equals("") || strcodeid.equals(""))
	     				{
	     				out.println("&nbsp;");
	     				}
	     				else
	     				{
	     					out.println(doCodeBean.getCodeName(strcodeid,voelement.getString(str1)));
	     				}
	     				
	  	  		%>
	  	  				  	
	  	  		
	  	  		</logic:notEqual>
	  	  	</logic:notEqual>
	  	  	</td> 
	   	 </logic:iterate>  	
	        <td align="center" class="RecordRow" nowrap>
            		<a href="/selfservice/educate/edulesson/teacheredu.do?b_query=link&a_id=<bean:write name="element" property="string(r3101)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>
	      
	    	<td align="center" class="RecordRow" nowrap>
	    		<a href="/selfservice/educate/edulesson/eduplace.do?b_query=link&a_id=<bean:write name="element" property="string(r3126)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>
	    	<td align="center" class="RecordRow" nowrap>
	    		<a href="/selfservice/educate/edulesson/eduorg.do?b_query=link&a_id=<bean:write name="element" property="string(r3128)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>
             	<td align="center" class="RecordRow" nowrap>
	    		<a href="/selfservice/educate/edulesson/edustu.do?b_query=link&a_id=<bean:write name="element" property="string(r3101)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>
	    	<td align="center" class="RecordRow" nowrap>
	    		<a href="/selfservice/educate/edulesson/eduinfo.do?b_query=link&a_id=<bean:write name="element" property="string(r3101)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>
	    	<td align="center" class="RecordRow" nowrap>
	    		<a href="/selfservice/educate/edulesson/edulesson.do?b_query=link&a_id=<bean:write name="element" property="string(r3101)" filter="true"/>" ><img src="/images/view.gif" border=0></a>
	    	</td>		    	    		        	        	        
          </tr>
        </hrms:extenditerate>
        
</table>

<table  width="70%" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">第
					<bean:write name="eduForm" property="eduForm.pagination.current" filter="true" />
					页
					共
					<bean:write name="eduForm" property="eduForm.pagination.count" filter="true" />
					条
					共
					<bean:write name="eduForm" property="eduForm.pagination.pages" filter="true" />
					页
		    </td>
	            <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationlink name="eduForm" property="eduForm.pagination"
				nameId="eduForm" propertyId="roleListProperty">
				</hrms:paginationlink>
			</td>
		</tr>
</table>



</html:form>
