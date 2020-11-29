<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<jsp:useBean id="statisticForm" class="com.hjsj.hrms.actionform.performance.StatisticForm" scope="session"/>
<%@ page import="com.hjsj.hrms.actionform.performance.StatisticForm,java.util.*" %>
<style>
TEXTAREA { overflow: auto; }
</style>
	
<%
	int i=0;
	int k=0;
	int count=Integer.parseInt(statisticForm.getItemTotalCount());
	StatisticForm aform=(StatisticForm)session.getAttribute("statisticForm");
	String isShowStatistic=aform.getIsShowStatistic();
	String labelIsPercent=aform.getShowtype();
	
	String browser = "MSIE";
	String agent = request.getHeader("user-agent").toLowerCase(); 
	if(agent.indexOf("firefox")!=-1)
		browser="Firefox";
	else if(agent.indexOf("chrome")!=-1)
		browser="Chrome";
	else if(agent.indexOf("safari")!=-1)
		browser="Safari";
%>
<br>

<script language='javascript' >
	function reShow(showType)
	{
		document.statisticForm.action="/selfservice/performance/statistic.do?b_search2=link&showtype="+showType+"&planNum=${statisticForm.planNum}&planFlag=2";
		//document.statisticForm.action="/selfservice/performance/leaderstatisticdata.do?b_search=query&showtype="+showType+"&planNum=${statisticForm.planNum}&planFlag=2";
		document.statisticForm.submit();
	}

</script>


<html:form action="/selfservice/performance/statistic">

<logic:notEqual name="statisticForm" property="planNum" value="">
<logic:equal name="statisticForm" property="drawingFlag" value="1">
<table>

<tr>
	<td align='center' >
	<Input type='radio' name='showType' value='0' <logic:equal  name="statisticForm" property="showtype"  value="0">checked</logic:equal>   onclick='reShow("0")' >按分值
	&nbsp;&nbsp;
	<Input type='radio' name='showType2' value='1'  <logic:equal  name="statisticForm" property="showtype"  value="1">checked</logic:equal>  onclick='reShow("1")' >按得分率

	</td>
</tr>
<tr>
	<td align='left' >&nbsp;&nbsp;
	<logic:equal  name="statisticForm" property="showtype"  value="0">(单位:分值)</logic:equal>
	<logic:equal  name="statisticForm" property="showtype"  value="1">(单位:百分比)</logic:equal>
	</td>
</tr>

<tr>
	
	<td>&nbsp;
		<hrms:chart name="statisticForm" title="" scope="session" legends="statisticDrawHm" data="" width="1100" height="600" chart_type="4"  labelIsPercent="<%=labelIsPercent%>"   chartParameter="chartParameter"  >
   		</hrms:chart>
   	</td>
</tr>
<tr><td>&nbsp;
	<table>
		<tr>
		<logic:iterate id="element" name="statisticForm" property="pointNotelst">
		<%
		k++;
		%>
		
		<td valign="top">&nbsp;<bean:write name="element" property="pointId" filter="true"/></td>
		<td width="300" valign="top"><bean:write name="element" property="pointName" filter="true"/></td>
		<%
		if(k%3==0)
		{
		%>
		</tr><tr>
		<%
		}
		%>
		</logic:iterate>
		<%
		if(k%3!=0 && k%3==1)
		{
		%>
		<td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr>
		<%
		}
		if(k%3!=0 && k%3==2)
		{
		%>
		<td>&nbsp;</td><td>&nbsp;</td></tr>
		<%
		}
		%>
		</table>
</td></tr>
</table>
</logic:equal>
<logic:equal name="statisticForm" property="drawingFlag" value="0">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
  <tr> 
    <td width="5%" class="RecordRow" height="177" align="center">
      <bean:message key="lable.statistic.selfinfo"/>
     </td>
    <td width="48%" valign="top" class="RecordRow"  <logic:notEqual name="statisticForm" property="showAppraiseExplain" value="true"> colspan='3' </logic:notEqual>  >&nbsp;<br>
    	<table>
    	        <tr>
    		  <td align="right">
		   <hrms:fieldtoname name="statisticForm" fieldname="B0110" fielditem="fielditem"/>
	           <bean:write name="fielditem" property="dataValue" />:
    		  </td>
    		  <td align="left">
          	        <hrms:codetoname codeid="UN" name="statisticForm" codevalue="result_bean.B0110" codeitem="codeitem"/>  	      
          	        <bean:write name="codeitem" property="codename" />&nbsp;    			
    	   	  </td>
    		</tr>
    		<tr>
    		  <td align="left">
 			<hrms:fieldtoname name="statisticForm" fieldname="E0122" fielditem="fielditem"/>
	    		<bean:write name="fielditem" property="dataValue" />:
    		  </td>
    		  <td align="left">
          	        <hrms:codetoname codeid="UM" name="statisticForm" codevalue="result_bean.E0122" codeitem="codeitem"/>  	      
          	        <bean:write name="codeitem" property="codename" />&nbsp; 
    		  </td>
    		</tr>
    		
    		<tr>
    		  <td align="left">
 			<hrms:fieldtoname name="statisticForm" fieldname="A0101" fielditem="fielditem"/>
	    		<bean:write name="fielditem" property="dataValue" />:
    		  </td>
    		  <td align="left">
    			<bean:write name="statisticForm" property="result_bean.A0101" filter="true"/>
    		  </td>
    		</tr>    		
    		<tr>
       		    <td align="right">
    			<bean:message key="lable.statistic.colligategrade"/>:
    		    </td>
    		    <td align="left">
    			<bean:write name="statisticForm" property="result_bean.score" filter="true"/>
    		    </td>
    		</tr>
    		<tr>
       		    <td align="right">
    			<bean:message key="lable.statistic.examinelevel"/>:
    		    </td>
    		    <td align="left">
    			<bean:write name="statisticForm" property="result_bean.resultdesc" filter="true"/>
    		    </td>
    		</tr>    		
    	</table>    
    </td>
    
    <logic:equal name="statisticForm" property="showAppraiseExplain" value="true">
    <td width="5%" class="RecordRow" align="center">
    	
            <bean:message key="lable.statistic.gradenote"/>
    	
    </td>
    <td width="42%" valign="top" class="RecordRow">
    &nbsp;<br>
  
    		<table width='100%' >
    		<tr>
   			 <td align="left">
    				<bean:message key="lable.statistic.thisgradenum"/>:
    			</td>
    			<td align="right">
    	   	 		<bean:write name="statisticForm" property="totalCount" filter="true"/>
    	   		</td>
    	   		<td align="right">
    	   		&nbsp;
    	   		</td>
    	   		</tr>
    	   		
   	 	<logic:iterate  id="element" name="statisticForm" property="examinelist"  scope="session">
   	 	<tr>
   	 		<td align="left">
    				<bean:write name="element" property="bodyName" filter="true"/>:
    			</td>
    			<td align="right">
    				<bean:write name="element" property="userCount" filter="true"/>
    			</td>
    			<td align="right">
    	   			<bean:write name="element" property="bodySetScore" filter="true"/>&nbsp;&nbsp;
    	   		</td>
    		</tr>
   		 </logic:iterate>
   	 
   		</table>
   		
   		
       </td>
       
       </logic:equal>
  </tr>
  <tr> 
    <td height="234" class="RecordRow" align="center">
           <bean:message key="lable.statistic.examinegrade"/>
      </td>
    <td colspan="3">
    <table width="100%" height="151" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
       <tr>
    	<logic:iterate id="element" name="statisticForm" property="itemwhilelist" scope="session">
    	        <%
    	        int j=0;
    	        ++i;
    	        if(i==1)
    	        {
    	       
    	        %>
    		<td valign="top" class="RecordRowleftTop0">
    		<%
    		 
    		}
    		else if(i==2)
    		{
    		
    		%>
    		<td valign="top" class="RecordRowTop0">
    		
    		<%
    		
    		}
    		else if(count%2==0 &&  i==count-1)
    		{
    		
    		%>
    		<td valign="top" class="RecordRowLeftBottomTest0">
    		<%
    		
    		}
    		else if(count%2!=0 && i==count)
    		{
    			if(isShowStatistic.equals("1"))
    			{
    		%>
    		
    			<td valign="top" class="RecordRowLeftBottomTest0">
    		
    		<%
    			}
    			else
    			{%>
    			<td valign="top" class="RecordRow">
    		<%	}
    		}
    		
    		else if(i!=2 && i%2!=0 && i!=count && i!=1 && i!=count-1)
    		{
    		
    		%>
    		<td valign="top" class="RecordRowLeft0">
    		<%
    		
    		}
    		
    		
    		else if(i==count && count%2==0)
    		{
    			
    		%>
    			<td valign="top" class="RecordRowBottom0">
    		<%
    			
    			
    		}
    		else if(i==count && count%2!=0)
    		{
    		
    		%>
    		<td valign="top" class="RecordRowLeftBottom0">	
    		<%
    		
    		
    		}
    		 else 
    		{
    		%>
    		<td valign="top" class="RecordRow">
    		<%
    		
    		}
    		%>
    		
    			<table border="0" cellspacing="0"  cellpadding="0" class="ListTable">
    			<tr>
    				<td class="tdFontcolor">
    				 <bean:write name="element" property="itemName" filter="true"/>&nbsp;&nbsp;<bean:write name="element" property="itemScore" filter="true"/>
    				</td>
    			</tr>
    			<tr>
    				<td>
    				&nbsp;
    				</td>
    			</tr>
    			<tr>
    				<td>
    				<logic:iterate id="pointelt" name="element" property="pointlist">
    				<%
    				  j++;
    				%>
    					<table border="0" cellspacing="0"  align="center" cellpadding="0">
    					<tr>
    						<td align="left"  width="300">
    							<table>
    							<tr>
    							<td>
    							<%=j%>.&nbsp;
    							</td>
    							<td>
    							<bean:write name="pointelt" property="pointName" filter="true"/>
    							</td>
    							</tr>
    							</table>
    						</td>
    						<td align="right" width="100">
    							<logic:equal name="statisticForm" property="GATIShowDegree" value="False">
 		   							<bean:write name="pointelt" property="score" filter="true"/>
    							</logic:equal>
    							<logic:equal name="statisticForm" property="GATIShowDegree" value="True">
	    							<bean:write name="pointelt" property="pointGrade" filter="true"/>
	    						</logic:equal>
    							&nbsp;
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
    			out.print("</td></tr>");
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
    					if(isShowStatistic.equals("1"))
    				 	 out.print("</td><td class=\"RecordRowBottom0\">&nbsp;</td></tr>");  //尾操作
    				 	else
    				 	 out.print("</td><td class=\"RecordRow\">&nbsp;</td></tr>");  //尾操作
    				}
    				
    			}
    		}
    		%>
    	</logic:iterate>
       <logic:equal name="statisticForm" property="flag" value="1">
       <td class="RecordRowTBLR0"> &nbsp;</td></tr>
       </logic:equal>
        
        
      </table></td>
  </tr>
  
  
  <logic:equal name="statisticForm" property="isShowStatistic" value="1">
  <tr> 
    <td height="88" class="RecordRow" align="center">
            <bean:message key="lable.statistic.votecalue"/>
      </td>
    <td colspan="3" class="RecordRow">&nbsp;&nbsp;&nbsp;
     	<bean:message key="lable.statistic.wholeeven"/>:
    	<logic:iterate id="element" name="statisticForm" property="elevellist">
    	<bean:write name="element" property="gradeName" filter="true"/>:&nbsp;
    	<bean:write name="element" property="gradeCount" filter="true"/>占
    	<bean:write name="element" property="gradePercent" filter="true"/>
    	&nbsp;&nbsp;
    	</logic:iterate>
    	<br>
    	<br>&nbsp;&nbsp;&nbsp;
    	<bean:message key="lable.statistic.knowdegree"/>:
    	<logic:iterate id="element" name="statisticForm" property="knowlist">
    	<bean:write name="element" property="knowName" filter="true"/>:&nbsp;
    	<bean:write name="element" property="knowCount" filter="true"/>占
    	<bean:write name="element" property="knowPercent" filter="true"/>
    	&nbsp;&nbsp;
    	</logic:iterate>
    	
    </td>
  </tr>
  </logic:equal>
  
  
  
</table>
</logic:equal>
</logic:notEqual>
<logic:equal name="statisticForm" property="drawingFlag" value="2">
	<br>	
	<br>
      <table width="570" border="0" cellpadding="0" cellspacing="0" align="center">
          <tr height="20">
       		<!-- <td width=10 valign="top" class="tableft"></td>
       		<td width=130 align=center class="tabcenter">&nbsp;考核评语&nbsp;</td>
       		<td width=10 valign="top" class="tabright"></td>
       		<td valign="top" class="tabremain" width="500"></td>  -->
       		<td  align=center class="TableRow">&nbsp;考核评语&nbsp;</td>             	      
          </tr> 
          <tr>
            <td  class="framestyle9">
            
               <table  width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">   
 	
 		
 		
		 		<tr>
		 			
		 			<td width="90%"  align='center' >
		 			<br>
		 			<TEXTAREA   name='desc' rows='35' cols='75' readonly type="_moz">
						
						</TEXTAREA>		
		 				
		 			
		 			</td>
		 		
		 		</tr>
 		
 		                                                 
        
      </table>


	
</logic:equal>

<logic:equal name="statisticForm" property="enrol_flag" value="5">

<hrms:ykcard name="statisticForm" property="cardparam" istype="3" nid="${statisticForm.nid}" tabid="${statisticForm.tabid}" cardtype="plan" disting_pt="javascript:screen.width" userpriv="noinfo" havepriv="1" queryflag="0" infokind="5" plan_id="${statisticForm.planNum}"  browser="<%=browser %>"/>
 
</logic:equal>

<br>

</center>

<logic:equal name="statisticForm" property="drawingFlag" value="2">	
<script language='javascript'>
	var a_desc=eval("statisticForm.desc");
	var context="${statisticForm.appraise}";
	var contexts=context.split('<br>')
	var a_t=''
	if(context.indexOf('<br>')==-1)
		a_t=context;	
	else
	{
		for(var i=0;i<contexts.length;i++)
		{
			a_t+=contexts[i]+'\n\r';
		}
	}
	a_desc.value=a_t;
	


</script>
</logic:equal>	
</html:form>