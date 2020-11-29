<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,com.hrms.hjsj.sys.DataDictionary,com.hrms.hjsj.sys.FieldItem,com.hrms.struts.constant.SystemConfig" %>
<html>
  <head>
   <%
		ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
		ArrayList mainBodyList=objectCardForm.getMainBodyList();
		String objectName=objectCardForm.getObjectName();
		Hashtable planParam=objectCardForm.getPlanParam();
		String desc="";
		FieldItem fielditem2=DataDictionary.getFieldItem("p0407");
		if(planParam.get("TaskNameDesc")!=null&&!((String)planParam.get("TaskNameDesc")).equals(""))
			{
				desc=(String)planParam.get("TaskNameDesc");
			}
			else{
	    		if(fielditem2==null||fielditem2.getItemdesc().trim().equalsIgnoreCase("任务内容"))
	    		{
		    		if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").equalsIgnoreCase("zglt")) //中国联通
		    			desc="工作目标";
		     		else
		     			desc=ResourceFactory.getProperty("jx.khplan.point");
		    	}
		    	else
		    		desc=fielditem2.getItemdesc();
			}
		int n=0;
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
		
   %>		
  </head>
<style>

 #tbl-container 
 {			 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 0pt solid;
	overflow:auto;
	height:400px;
	width:100% 			
 }
 #order
 {
 	float:right;
 }
 .TableRow_self {
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>
<hrms:themes />
<script language='javascript' >

function query()
{
   objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_scoreManual=query&sort_id=2";
   objectCardForm.submit();
} 

</script>
 
<body>
	<html:form action="/performance/objectiveManage/objectiveCard">

		<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

		<table width="570px" border="0" cellspacing="0"  align="center" cellpadding="0">
			<tr> 
				<td>
					<bean:message key="lable.performance.perObject"/>&nbsp;:&nbsp;<%= objectName %>  
					&nbsp;&nbsp;&nbsp;&nbsp;					
					
					<bean:message key="label.query.sortFashion"/>:
					<html:select name="objectCardForm"  onchange="query()"  styleId="sort" property="sort" size="1">
	  					<html:optionsCollection property="sortList" value="dataValue" label="dataName"/>
					</html:select>  
				</td>
			</tr>			
			<tr><td width='100%' style="border-top: 1px solid #8EC2E6;" class="common_border_color" >	
			<div id="tbl-container">					
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
					<thead>
        				<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">       	        	  
			
			 				<td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap style="border-left:0px;"><bean:message key="gz.formula.project"/></td>
			 				<td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><%=desc%></td>
			 				<td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><bean:message key="lable.performance.perMainBody"/></td>
			 				<td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap style="border-right:0px;"><bean:message key="kh.field.scorevalue"/></td>
			
						</tr>
	 				</thead>
	 	 
	 
	 				<%
						 for(int i=0;i<mainBodyList.size();i++)
						 {
							LazyDynaBean abean=(LazyDynaBean)mainBodyList.get(i);
					 		String itemdesc=(String)abean.get("itemdesc");
					 		String p0407=(String)abean.get("p0407");
					 		String a0101=(String)abean.get("a0101");
					 		Float score=(Float)abean.get("score");
					 		
							if(n%2==0)
						    {  
						    	out.println("<tr class='trShallow'>");   
						    }else{
						    	out.println("<tr class='trDeep'>");
							}		
								out.println("<td align='left' class='RecordRow' nowrap  style=\"border-left:0px;\">&nbsp;");
						 		out.print(itemdesc);
						  		out.print("</td>");
						  		
						  		out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(p0407);
						  		out.print("</td>");
						  		
						  		out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(a0101);
						  		out.print("</td>");
							
								out.println("<td align='right' class='RecordRow' nowrap style=\"border-right:0px;\">&nbsp;");
						 		out.print(score);
						  		out.print("</td>");
						  		
					  		out.print("</tr>");
					 	}   			        	 		           
					%>	
	 				
	  			</table>
	  		</div>
			</td></tr>	
		</table>	

		<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
			<tr>
				<td align="center" style="height:35px">				
<%--  				<input type="button" name="ok" class="mybutton" value="<bean:message key="button.ok"/>" onclick="window.close()"/>		    --%>					
					<input type="button" name="cancel" class="mybutton" value="&nbsp;<bean:message key="button.cancel"/>&nbsp;" onclick='window.close()'/>										
				</td>
			</tr>
		</table>
		
  	</html:form> 
  </body>
</html>
