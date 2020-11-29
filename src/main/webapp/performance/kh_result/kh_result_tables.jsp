<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,
				 com.hjsj.hrms.actionform.performance.kh_result.KhResultForm" %>
				 
<%
        int i=0;
	    int k=0;
        KhResultForm khResultForm=(KhResultForm)session.getAttribute("khResultForm");
        LazyDynaBean bean = khResultForm.getPersonalInformation();
        int count=Integer.parseInt(khResultForm.getItemTotal());
        int a_total=khResultForm.getItemList().size();
        int total=khResultForm.getItemList().size();
        if(total==0)
            total=1;
        else if(total%2==0)
           total=total/2;
        else
           total=total/2+1;
          // String model = khResultForm.getModel();
          // System.out.println(model);
%>
 
<html:form action="/performance/kh_result/kh_result_tables">
<br>
<br>
<table align="left" border="0" width="90%" cellpmoding="0" cellspacing="0" cellpadding="0" class="ListTable">
	<tr align="center">
		<td class="RecordRow" width="5%"> 
	 		<font style="font-weight:bold">
	 			<logic:equal value="3" name="khResultForm" property="model">
	 				<bean:message key="lable.statistic.orginfo"/><!-- <p>单</p><p>位</p><p>信</p><p>息</p> -->
	 			</logic:equal>
                <logic:notEqual value="3" name="khResultForm" property="model">
                 	<logic:equal value="2" name="khResultForm" property="objecType">
                 		 <bean:message key="lable.statistic.selfinfo"/><!-- <p>个</p><p>人</p><p>信</p><p>息</p> -->
                 	</logic:equal>
                 	<logic:equal value="3" name="khResultForm" property="objecType">
                 		<bean:message key="lable.statistic.orginfo"/>
                 	</logic:equal>
                 	<logic:equal value="4" name="khResultForm" property="objecType">
                 		<bean:message key="lable.statistic.deptinfo"/>
                 	</logic:equal>
                  	<logic:equal value="1" name="khResultForm" property="objecType">
                 		<bean:message key="lable.statistic.teaminfo"/>
                 	</logic:equal>
                </logic:notEqual>
            </font>
	    </td>
	    <td width="45%" align="left" class="RecordRow" valign="top"><!-- 个人信息说明 -->
	 		<table>                        
        		<logic:equal value="2" name="khResultForm" property="objecType">  
      				<tr>      			
    		  			<td align="right">
		   					<hrms:fieldtoname name="khResultForm" fieldname="B0110" fielditem="fielditem"/>
	           				<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		  			<td align="left">
          	        		<bean:write name="khResultForm" property="personalInformation.b0110" filter="true"/>		
    	   	  			</td>
    				</tr>
    				<tr>
    		  			<td align="right">
 							<hrms:fieldtoname name="khResultForm" fieldname="E0122" fielditem="fielditem"/>
	    					<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		  			<td align="left">          	       
                  			<bean:write name="khResultForm" property="personalInformation.e0122" filter="true"/>
    		  			</td>
    				</tr>
    				<logic:notEqual value="3" name="khResultForm" property="model">
	    				<tr>
	    		  			<td align="right">
	 							<hrms:fieldtoname name="khResultForm" fieldname="A0101" fielditem="fielditem"/>
		    					<bean:write name="fielditem" property="dataValue" />:
	    		  			</td>
	    		  			<td align="left">
	    						<bean:write name="khResultForm" property="personalInformation.a0101" filter="true"/>
	    		  			</td>
	    				</tr>    
    				</logic:notEqual>	
    			</logic:equal>	
    			<logic:equal value="1" name="khResultForm" property="objecType">  
      				<tr>      			
    		  			<td align="right">
		   					<hrms:fieldtoname name="khResultForm" fieldname="B0110" fielditem="fielditem"/>
	           				<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		  			<td align="left">
          	        		<bean:write name="khResultForm" property="personalInformation.b0110" filter="true"/>		
    	   	  			</td>
    				</tr>
    				<tr>
    		  			<td align="right">
 							<hrms:fieldtoname name="khResultForm" fieldname="E0122" fielditem="fielditem"/>
	    					<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		  			<td align="left">         	       
                  			<bean:write name="khResultForm" property="personalInformation.e0122" filter="true"/>
    		  			</td>
    				</tr>
    			</logic:equal>		
    			<logic:equal value="3" name="khResultForm" property="objecType">  
      				<tr>      			
    		  			<td align="right">
		   					<hrms:fieldtoname name="khResultForm" fieldname="B0110" fielditem="fielditem"/>
	           				<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		  			<td align="left">
          	        		<bean:write name="khResultForm" property="personalInformation.b0110" filter="true"/>		
    	   	  			</td>
    				</tr>    		
    			</logic:equal>		
    			<logic:equal value="4" name="khResultForm" property="objecType">  
    				<tr>
    		  			<td align="right">
 							<hrms:fieldtoname name="khResultForm" fieldname="E0122" fielditem="fielditem"/>
	    					<bean:write name="fielditem" property="dataValue" />:
    		  			</td>
    		 		 	<td align="left">         	       
                  			<bean:write name="khResultForm" property="personalInformation.e0122" filter="true"/>
    		  			</td>
    				</tr>
    			</logic:equal>
    			<tr>
       		    	<td align="right">
    					<bean:message key="lable.statistic.colligategrade"/>:
    		    	</td>
    		    	<td align="left">
    					<bean:write name="khResultForm" property="personalInformation.score" filter="true"/>
    		    	</td>
    			</tr>
    			<tr>
       		    	<td align="right">
    					<bean:message key="lable.statistic.examinelevel"/>:
    		    	</td>
    		    	<td align="left">
    					<bean:write name="khResultForm" property="personalInformation.resultdesc" filter="true"/>
    		    	</td>
    			</tr>  
    		</table>  		                     
		</td>
		
		<logic:equal value="0" property="isShowEvaluationDescription" name="khResultForm">	
			<td colspan="2" class="RecordRow" width="50%" valign="top" >
	    	  	&nbsp;&nbsp;&nbsp;&nbsp;
	   		</td>
		</logic:equal>
		<logic:equal value="1" property="isShowEvaluationDescription" name="khResultForm">
			<td class="RecordRow" width="5%">
				<font style="font-weight:bold"> <bean:message key="lable.statistic.gradenote"/></font><!-- <p>测</p><p>评</p><p>说</p><p>明</p> -->
			</td>
			<td class="RecordRow" width="45%" valign="top">
				<table width='99%'>    	   		
   	 				<logic:iterate  id="element" name="khResultForm" property="evaluationDescription"  scope="session">
   	 					<tr>
   	 						<td align="left">
    							<bean:write name="element" property="bodyname" filter="true"/>:
    						</td>
    						<td align="right">
    							<bean:write name="element" property="count" filter="true"/>
    						</td>
    						<td align="right">
    	   						<bean:write name="element" property="score" filter="true"/>&nbsp;&nbsp;
    	   					</td>
    	   				</tr>
    	   			</logic:iterate>
    	   		</table> 
    	   	</td>
    	</logic:equal>  	
	</tr>
	<tr>
		<td rowspan="<%=total%>" class="RecordRow" align="center" width="5%">
        	<font style="font-weight:bold"><bean:message key="lable.statistic.examinegrade"/></font><!-- <p>测</p><p>评</p> <p>评</p><p>分</p> -->
      	</td>
      	<logic:iterate id="element" name="khResultForm" property="itemList" scope="session">       
    	<%
    		int j=0;
    	    ++i;
    	    if(i==1)
    	    {   	       
    	%>
    			<td class="RecordRow" width="45%" valign="top"> 
    	<%   		 
    		}
    		else if(i==2)
    		{   		
    	%>
    		 	<td class="RecordRow" colspan="2" width="50%" valign="top"> 		
    	<% 		
    		}
    		else if(count%2==0 &&  i==count-1)
    		{  		
    	%>
    		 	<td class="RecordRow" width="45%" valign="top"> 
    	<% 		
    		}
    		else if(count%2!=0 && i==count)
    		{
    			
    	%>  		
    		 	<td class="RecordRow" width="45%" valign="top"> 
    	<%	
    		}   		
    		else if(i!=2 && i%2!=0 && i!=count && i!=1 && i!=count-1)
    		{   		
    	%>
    		 	<td class="RecordRow" width="45%" valign="top"> 
    	<%   		
    		}		
    		else if(i==count && count%2==0)
    		{ 			
    	%>
    			<td class="RecordRow" colspan="2" width="50%" valign="top"> 		
    	<%  			
    		}
    		else if(i==count && count%2!=0)
    		{  		
    	%>
    			<td class="RecordRow" width="45%" valign="top"> 
    	<%	
    		}
    		 else 
    		{
    	%>
    			<td class="RecordRow" colspan="2" width="50%" valign="top"> 	
    	<%   		
    		}
    	%>      
       		<table border="0" cellspacing="0"  cellpadding="0" >
    			<tr>
    				<td align="center" valign="top">
    					<font style="font-weight:bold"> 
    						<bean:write name="element" property="itemname" filter="true"/>&nbsp;&nbsp;<bean:write name="element" property="score" filter="true"/>
    					</font>
    				</td>
    			</tr>
    			<tr>
    				<td>
    					&nbsp;
    				</td>
    			</tr>
    			<tr>
    				<td>
    					<logic:iterate id="pointelt" name="element" property="sublist">
    					<% j++; %>
    						<table border="0" cellspacing="0"  align="center" cellpadding="0">
    							<tr>
    								<td align="left"  width="300">
    									<table>
    										<tr>
    											<td>
    												<%=j%>.&nbsp;
    											</td>
    											<td>
    												<bean:write name="pointelt" property="pointname" filter="true"/>
    											</td>
    										</tr>
    									</table>
    								</td>
    								<td align="right" width="100">   						
 		   								<bean:write name="pointelt" property="score" filter="true"/>	    					
    								</td>
    							</tr>
    						</table>   		
    					</logic:iterate>
    				</td>
    			</tr>
    		</table>
      	<%
    		
    		if(count==1)
    		{
    			out.print("</td><td class=\"RecordRow\" colspan=\"2\" width=\"50%\">&nbsp;</td></tr>");
    		}
    		else
    		{
    			if(i%2==0)
    			{
    				if(i==count)
    				{
    					out.print("</td></tr>");
    				}
    				else
    				{
    					out.print("</td></tr><tr>");
    				}
    			}
    			else
    			{  
    			
    				if(count!=i)
    				{
    					out.print("</td>");
    				}
    				else
    				{
    				 	 out.print("</td><td class=\"RecordRow\" colspan=\"2\" width=\"50%\">&nbsp;</td></tr>");  //尾操作
    				}
    				
    			}
    		}
    	%>
      	</logic:iterate>

        	<logic:equal value="1" name="khResultForm" property="isShowVoteTd">
   				<tr> 
    				<td height="88" class="RecordRow" align="center">
         				<font style="font-weight:bold">   
         					<bean:message key="lable.statistic.votecalue"/>
         				</font>
      				</td>
    				<td colspan="3" class="RecordRow">&nbsp;&nbsp;&nbsp;
  					<logic:equal value="1" property="isShowWholeEval" name="khResultForm">
     					<bean:message key="lable.statistic.wholeeven"/>:
	    				<logic:iterate id="element" name="khResultForm" property="overallRating">
					    	<bean:write name="element" property="itemname" filter="true"/>:&nbsp;
					    	<bean:write name="element" property="vote" filter="true"/>票&nbsp;占
					    	<bean:write name="element" property="percent" filter="true"/>
					    	&nbsp;&nbsp;
	    				</logic:iterate>
	    				<br>
	    				<logic:iterate id="ordelement" name="khResultForm" property="overallRatingDetail">
	            			&nbsp;&nbsp;&nbsp;
	            			<bean:write name="ordelement" property="bodyname" filter="true"/>:
	    	    			<logic:iterate id="data" name="ordelement" property="sublist">
					    	      <bean:write name="data" property="itemname" filter="true"/>:&nbsp;
					    	      <bean:write name="data" property="vote" filter="true"/>票&nbsp;占
					    	      <bean:write name="data" property="percent" filter="true"/>
	    	    			</logic:iterate>
	    	  				<br>
	    				</logic:iterate>&nbsp;&nbsp;&nbsp;
    	  			</logic:equal>
    	  			<logic:equal value="1" property="isShowKnowDegree" name="khResultForm">
    					<bean:message key="lable.statistic.knowdegree"/>:
    					<logic:iterate id="element" name="khResultForm" property="understandingOf">
					    	<bean:write name="element" property="itemname" filter="true"/>:&nbsp;
					    	<bean:write name="element" property="vote" filter="true"/>票&nbsp;占
					    	<bean:write name="element" property="percent" filter="true"/>
					    	&nbsp;&nbsp;
				    	</logic:iterate>
    					<br>
    					<logic:iterate id="uodelement" name="khResultForm" property="understandingOfDetail">
			            	&nbsp;&nbsp;&nbsp;
			            	<bean:write name="uodelement" property="bodyname" filter="true"/>:
			    	    	<logic:iterate id="data" name="uodelement" property="sublist">
				    	      <bean:write name="data" property="itemname" filter="true"/>:&nbsp;
				    	      <bean:write name="data" property="vote" filter="true"/>票&nbsp;占
				    	      <bean:write name="data" property="percent" filter="true"/>
			    	    	</logic:iterate>
    	  					<br>
    					</logic:iterate>
    				</logic:equal>	
    				</td>
  				</tr>	  
  			</logic:equal>
		  
	</table>    
</html:form>