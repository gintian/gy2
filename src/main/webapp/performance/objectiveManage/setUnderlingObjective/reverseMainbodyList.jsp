<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.objectiveManage.setUnderlingObjective.SetUnderlingObjectiveForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>
  <head>
   <% 
		SetUnderlingObjectiveForm setUnderlingObjectiveForm=(SetUnderlingObjectiveForm)session.getAttribute("setUnderlingObjectiveForm");
		ArrayList reverseList = (ArrayList)setUnderlingObjectiveForm.getReverseList();
		
		int n=0;
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
		
//		String object_id=request.getParameter("object_id");
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
	height:310px;
	width:100% 			
 }
 .TableRow_self {
	
	margin-left:auto;
	margin-right:auto;
	background-position : center;
	background-color:#f4f7f7;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	height:25px;
	font-weight: bold;	
	valign:middle;
}
</style>
<hrms:themes />
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
<script language='javascript' >

//全选
function selectAll()
{
	var records=document.getElementsByName("targetCalcItemt");
 	var allselect=document.getElementById("checkAll");
 	if(records)
 	{
     	for(var i=0;i<records.length;i++)
     	{
        	if(allselect.checked)       	
           		records[i].checked=true;        	
        	else       	
            	records[i].checked=false;       	
     	}
 	}
}

// 回顾催办
function sendMessageOrEmail()
{	 
	var records=document.getElementsByName("targetCalcItemt");
    var num=0;
    var selectItemts="";
    if(records)
    {
      	for(var i=0;i<records.length;i++)
      	{
         	if(records[i].checked)
         	{
            	num++;
            	selectItemts+=","+records[i].value;
         	}
      	}
   	}
   	if(num==0)
   	{
      	alert("请您选择记录！");
      	return;
   	} 
   	 	   
	var url="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_initMail=link`opt=3`plan_id="+'${setUnderlingObjectiveForm.plan_id}'+"`to_a0100="+selectItemts;
    	url+="`isAll=1`object_id=" + "<%=(request.getParameter("object_id"))%>";
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url)
    //window.showModalDialog(iframe_url,"","dialogWidth=700px;dialogHeight=470px;resizable=yes;scroll=yes;status=no;");
  	//兼容非IE浏览器 弹窗替换用 open弹窗  wangb 20180105
    var iTop = (window.screen.availHeight - 30 - 470) / 2;  //获得窗口的垂直位置
    var iLeft = (window.screen.availWidth - 10 - 700) / 2; //获得窗口的水平位置 
    window.open(iframe_url,"","width=700px,height=470px,resizable=no,scrollbars=no,status=no,left="+iLeft+",top="+iTop);  
}
</script>
 
<body>
	<html:form action="/performance/objectiveManage/setUnderlingObjective/underling_objective_list">
	
		<%if("hl".equals(hcmflag)){ %>
		<br>
		<%} %>

		<table width="660px" border="0" cellspacing="0"  align="center" cellpadding="0">
						
			<tr><td width='100%' style="border-top: 1px solid #8EC2E6;" class="common_border_color" >	
			<div id="tbl-container">					
				<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin:-1px;">
					<thead>
        				<tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">       	        	  
							<td align="center" style='color:black;border-left:0px;' class="TableRow_self common_border_color" nowrap>
			  			  		<input type="checkbox" name="check" id='checkAll' value="1" onclick='selectAll();' />
			  			  	</td>
			 				<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap>
			 					<bean:message key="b0110.label"/>
			 				</td>
			 				
			 				<%	FieldItem fielditem = DataDictionary.getFieldItem("E0122"); %>
			 				<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap>
			 					<%= fielditem.getItemdesc() %>
			 				</td>
			 				<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap>
			 					<bean:message key="e01a1.label"/>
			 				</td>
			 				<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap>
			 					<bean:message key="hire.employActualize.name"/>
			 				</td>
			 				<td align="center" style='color:black' class="TableRow_self common_border_color" nowrap>
			 					<bean:message key="lable.performance.perMainBodySort"/>
			 				</td>
			
						</tr>
	 				</thead>
	 	 
	 
	 				<%
						 for(int i=0;i<reverseList.size();i++)
						 {
							LazyDynaBean abean=(LazyDynaBean)reverseList.get(i);
							String b0110=(String)abean.get("b0110");
							String e0122=(String)abean.get("e0122");
							String e01a1=(String)abean.get("e01a1");
					 		String a0101=(String)abean.get("a0101");
					 		String bodyTypeName=(String)abean.get("bodyTypeName");
					 		String mainbody_id=(String)abean.get("mainbody_id");
					 		
							if(n%2==0)
						    {  
						    	out.println("<tr class='trShallow'>");   
						    }else{
						    	out.println("<tr class='trDeep'>");
							}
								out.println("<td align='center' class='RecordRow_right' width='5%' nowrap>");
						 		out.print("<input name='targetCalcItemt' type='checkbox' value="+mainbody_id+" />");
						  		out.print("</td>");
																
								out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(b0110);
						  		out.print("</td>");
						  		
						  		out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(e0122);
						  		out.print("</td>");
						  		
						  		out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(e01a1);
						  		out.print("</td>");
								
								out.println("<td align='left' class='RecordRow' nowrap>&nbsp;");
						 		out.print(a0101);
						  		out.print("</td>");
						  		
						  		out.println("<td align='left' class='RecordRow_left' nowrap>&nbsp;");
						 		out.print(bodyTypeName);
						  		out.print("</td>");
							
					  		out.print("</tr>");
					 	}   			        	 		           
					%>	
	 				
	  			</table>
	  		</div>
			</td></tr>	
		</table>	

		<table width="660px" border="0" cellspacing="0"  align="center" cellpadding="0">	
			<tr>
				<td align="center" style="height:35px">
				<%
					String opt=request.getParameter("opt"); // 已回顾:already  未回顾：noAlready
					if(opt!=null && opt.trim().length()>0 && opt.equalsIgnoreCase("noAlready"))
					{
				%>
					<hrms:priv func_id="06070702">				
						<input type="button" name="sendMessage" class="mybutton" value="<bean:message key="jx.performance.cardPerformCaseSendMessage"/>" onclick="sendMessageOrEmail();"/>							
					</hrms:priv>
				<%
					}
				%>
					<input type="button" name="cancel" class="mybutton" value="<bean:message key="button.cancel"/>" onclick='parent.window.close()'/>										
				</td>
			</tr>
		</table>
		
  	</html:form> 
  </body>
</html>

