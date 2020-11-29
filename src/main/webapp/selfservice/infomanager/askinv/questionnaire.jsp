<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.welcome.WelcomeForm,com.hjsj.hrms.actionform.askinv.QuestionnaireForm"%>

<%UserView userView = (UserView) session.getAttribute(WebConstant.userView); %>
<jsp:useBean id="questionnaireForm" class="com.hjsj.hrms.actionform.askinv.QuestionnaireForm" scope="session"/>
<%
	int i=0;
	QuestionnaireForm questionForm =(QuestionnaireForm)session.getAttribute("questionnaireForm");
	String columnCount = questionForm.getColumnCount();
%>

<style>
<!--
.RecordRow {
    border: inset 1px #C4D8EE;
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 0pt solid;
    font-size: 12px;
    height:22px;
}

.TableRow {
    background-position : center left;
    font-size: 12px;  
    BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid;
    height:22px;
    font-weight: bold;
    background-color:#f4f7f7;   
    /*
    color:#336699;
    */
    valign:middle;
}
-->
</style>
<hrms:themes />
<script>
function autoSelect(obj)
{
    document.getElementById(obj).checked=true;
}
	function doExamint(id)
	{
	var sql="/selfservice/infomanager/askinv/searchendview.do?b_query=link&id="+id+"&f=1";
	//location.parent.href="/selfservice/infomanager/askinv/searchendview.do?b_query=link&id="+id+"&f=1";
	window.open(sql,"");
	
	}
	function sub(flag,button)
	{
		if(button)
			button.disabled = true;
		
		document.getElementById('wait').style.display='block';
	      <logic:iterate id="element" name="questionnaireForm" property="topicList" indexId="index">
	      <logic:iterate id="element2" name="element" property="itemwhilelst">
	      var fillflag="<bean:write name="element2" property="fillflag"/>";
	      if(fillflag=="1")
	      {
	      <logic:equal name="element2" property="itemStatus" value="0">
	      <logic:iterate id="element3" name="element2" property="pointList">
	        var sel = document.getElementsByName("<bean:write name="element3" property="multitem" filter="true"/>");
	       var sum = 0;
	       var x=0;
	       for(var i=0;i<sel.length;i++)
	       {
	          if(sel[i].checked)
	          {
	             sum++;
	          }
	       }
	      <logic:equal name="element3" property="describestatus" value="1">
	      x=1;
	      var value=document.getElementsByName("<bean:write name="element3" property="pointContext" filter="true"/>");
	       if(sum==0&&value[0].value=='')
	      {
	    	   	document.getElementById('wait').style.display='none';
	    	   	button.disabled = false;
	          alert("<bean:write name="element2" property="itemName" filter="true"/>"+"<bean:message key="lable.investigate_item.must"/>");
	          return;
	      
	      }
	      	</logic:equal>
	      	if(x==0&&sum==0)
	      	{
	      		document.getElementById('wait').style.display='none';
	      		button.disabled = false;
	      	     alert("<bean:write name="element2" property="itemName" filter="true"/>"+"<bean:message key="lable.investigate_item.must"/>");
	          return;
	      	}
			</logic:iterate>
	      </logic:equal>

	      
	      <logic:equal name="element2" property="itemStatus" value="2">
	       var selects="<bean:write name="element2" property="selects"/>";
	       var maxvalue="<bean:write name="element2" property="maxvalue"/>";
	       var minvalue="<bean:write name="element2" property="minvalue"/>";
	       <logic:iterate id="element3" name="element2" property="pointList">
	       var sel = document.getElementsByName("<bean:write name="element3" property="multitem" filter="true"/>");
	       var sum = 0;
	       var x=0;
	       for(var i=0;i<sel.length;i++)
	       {
	          if(sel[i].checked)
	          {
	             sum++;
	          }
	       }
	      <logic:equal name="element3" property="describestatus" value="1">
	        x=1;
	       var value=document.getElementsByName("<bean:write name="element3" property="pointContext" filter="true"/>");
	       if(sum==0&&value[0].value=='')
	      {
	    	   	document.getElementById('wait').style.display='none';
	    	   	button.disabled = false;
	          alert("<bean:write name="element2" property="itemName" filter="true"/>"+"<bean:message key="lable.investigate_item.must"/>");
	          return;
	      
	      }
	      	</logic:equal>
	      	if(x==0&&sum==0)
	      	{
	      		document.getElementById('wait').style.display='none';
	      		button.disabled = false;
	      	    alert("<bean:write name="element2" property="itemName" filter="true"/>"+"<bean:message key="lable.investigate_item.must"/>");
	            return;
	      	}
	      	if(selects=='1')
	      	{
	      	     if(minvalue*1!=0&&sum<minvalue*1)
	      	     {
	      	    		document.getElementById('wait').style.display='none';
	      	    		button.disabled = false;
	      	        alert("<bean:write name="element2" property="itemName" filter="true"/>"+"最少选"+minvalue+"个!");
	      	        return;
	      	     }
	      	     if(maxvalue*1!=0&&sum>maxvalue*1)
	      	     {
	      	    		document.getElementById('wait').style.display='none';
	      	    		button.disabled = false;
	      	        alert("<bean:write name="element2" property="itemName" filter="true"/>"+"最多选"+maxvalue+"个!");
	      	        return;
	      	     }
	      	}
			</logic:iterate>
	      </logic:equal>

	      
	      
	      <logic:equal name="element2" property="itemStatus" value="1">
	      var value=document.getElementsByName("<bean:write name="element2" property="itemContext"/>");
	      if(value[0].value=='')
	      {
	    	  	document.getElementById('wait').style.display='none';
	    	  	button.disabled = false;
	          alert("<bean:write name="element2" property="itemName" filter="true"/>"+"<bean:message key="lable.investigate_item.must"/>");
	          return;
	      
	      }
	      
	      </logic:equal>
          }
          else
          {
              <logic:equal name="element2" property="itemStatus" value="0">
	      <logic:iterate id="element3" name="element2" property="pointList">
	        var sel = document.getElementsByName("<bean:write name="element3" property="multitem" filter="true"/>");
	       var sum = 0;
	       var x=0;
	       for(var i=0;i<sel.length;i++)
	       {
	          if(sel[i].checked)
	          {
	             sum++;
	          }
	       }
	      <logic:equal name="element3" property="describestatus" value="1">
	      	</logic:equal>
	      
			</logic:iterate>
	      </logic:equal>

	      
	      <logic:equal name="element2" property="itemStatus" value="2">
	       var selects="<bean:write name="element2" property="selects"/>";
	       var maxvalue="<bean:write name="element2" property="maxvalue"/>";
	       var minvalue="<bean:write name="element2" property="minvalue"/>";
	       <logic:iterate id="element3" name="element2" property="pointList">
	       var sel = document.getElementsByName("<bean:write name="element3" property="multitem" filter="true"/>");
	       var sum = 0;
	       var x=0;
	       for(var i=0;i<sel.length;i++)
	       {
	          if(sel[i].checked)
	          {
	             sum++;
	          }
	       }
	      <logic:equal name="element3" property="describestatus" value="1">
	       var value=document.getElementsByName("<bean:write name="element3" property="pointContext" filter="true"/>");
	      
	      	</logic:equal>
	      	
	      	if(selects=='1')
	      	{
	      	     if(minvalue*1!=0&&sum<minvalue*1)
	      	     {
	      	    		document.getElementById('wait').style.display='none';
	      	    		button.disabled = false;
	      	        alert("<bean:write name="element2" property="itemName" filter="true"/>"+"最少选"+minvalue+"个!");
	      	        return;
	      	     }
	      	     if(maxvalue*1!=0&&sum>maxvalue*1)
	      	     {
	      	    		document.getElementById('wait').style.display='none';
	      	    		button.disabled = false;
	      	        alert("<bean:write name="element2" property="itemName" filter="true"/>"+"最多选"+maxvalue+"个!");
	      	        return;
	      	     }
	      	}
			</logic:iterate>
	      </logic:equal>

	      
	      
	      <logic:equal name="element2" property="itemStatus" value="1">
	           
	      </logic:equal>
                  
             
          }
	      </logic:iterate>    
	      </logic:iterate>
	      questionnaireForm.action="/selfservice/infomanager/askinv/questionnaire.do?b_save=save&questionFlag="+flag;
	      questionnaireForm.submit();
	}
	function backHome(flag,msg,home)
 {
     if(flag=='0')
     {
         if(msg == "1") {
    	 	document.location="/train/evaluationdetails.do?b_query=link";
     	 } else {
       	 	document.location="/selfservice/welcome/hot_topic.do?b_more=more&home=5&ver=5&discriminateFlag=rese";
     	 }
     }
     else
     {
       if(home=='5'){
    	   var tar='<%=userView.getBosflag()%>';
    	   var status='<%=userView.getStatus()%>';
	       if(tar=="hl"){//6.0首页
	       		//if(status=="0")
	       		//{
		        	document.location="/templates/index/portal.do?b_query=link";
	       		//}else if(status=="4"){
			       // document.location="/general/tipwizard/tipwizard.do?br_selfinfo=link";
	       		//}
	       }else if(tar=="hcm"){//7.0首页
		        document.location="/templates/index/hcm_portal.do?b_query=link";
	       }
       }
     }
 }
</script>
<center>
	<table border="0" cellspacing="0"  align="center" cellpadding="0" width="800"  class="ListTableF" style="margin-top:10px;">
	<logic:iterate id="element" name="questionnaireForm" property="topicList" indexId="index">
		<form name="questionnaireForm" action="/selfservice/infomanager/askinv/questionnaire.do" method="post">
			<%
			int k=0;
			
			int count=((WelcomeForm)element).getItemwhilelst().size();			
			%>
			<input type="hidden" name="id" value="<bean:write name="element" property="id" filter="true"/>">
			<tr>
			<td class="TableRow"><strong>&nbsp;<bean:write name="element" property="name" filter="true"/></strong></td></tr>
			
			<logic:notEqual name="element" property="description" value="" >
				<tr><td class="RecordRow"><bean:write name="element" property="description" filter="false"/></td></tr>
				<tr>
			</logic:notEqual>
			
			<td width="100%" class="RecordRow" align="center" >
			<table>
				<tr>
			<logic:iterate id="element2" name="element" property="itemwhilelst">
			<% if(columnCount.equals("1")){ %>
			 <td valign="top" width="800" colspan="2">
			<% }else{ %>
			 <td valign="top" width="400" >
			<%} %>
				 
				<logic:equal name="element2" property="itemStatus" value="0">
				<% if(columnCount.equals("1")){ %>
			    <table width="800">
		     	<% }else{ %>
			     <table width="400">
		     	<%} %>
				
						<tr>
						<% if(columnCount.equals("1")){ %>
			            <td width="800" colspan="2">
		     	        <% }else{ %>
			            <td width="400">
		            	<%} %>
					<input type="hidden" name="itemEndView" value="<bean:write name="element2" property="itemid" filter="true"/>">
					<%
						++k;
					%>
					<strong><%=k%>.&nbsp;<bean:write name="element2" property="itemName" filter="true"/></strong><logic:equal name="element2" property="fillflag" value="1"><font color="red">&nbsp;*</font></logic:equal><br>
						</td>
						</tr>
					
				        <%int x=1; %>
						<logic:iterate id="element3" name="element2" property="pointList">
							<% if(columnCount.equals("1")){ 
							     if(x==1||x%2==1){
							%>
							   <tr>
							<%
						    	}
						  	}else{ 
						  	%>
							<tr>
							<%
							}
						     %>
							
							<td width="400" style="word-break:break-all">
							<input id="<bean:write name="element3" property="pointid" filter="true"/>" type="radio" name="<bean:write name="element3" property="multitem" filter="true"/>" value="<bean:write name="element3" property="pointid" filter="true"/>"/>
							<bean:write name="element3" property="pointName" filter="true"/>&nbsp;
							<logic:equal name="element3" property="describestatus" value="1">
								<%if(!columnCount.equals("1")){ %>
								<br>&nbsp;&nbsp;&nbsp;
								<%} %>

                            <input id="desc<bean:write name="element3" property="pointid" filter="true"/>"  onfocus="autoSelect('<bean:write name="element3" property="pointid" filter="true"/>')" type="text" class="text4" name="<bean:write name="element3" property="pointContext" filter="true"/>"/>
							</logic:equal>
							</td>
							<% if(columnCount.equals("1")){ 
							     if(x!=0&&x%2==0){
							%>
							   </tr>
							<%
						    	}
						  	}else{ 
						  	%>
							</tr>
							<%
							}
							 x++; %>
						</logic:iterate>
				
					</table><br>
				</logic:equal>
				<logic:equal name="element2" property="itemStatus" value="2">
							<% if(columnCount.equals("1")){ %>
			    <table width="800">
		     	<% }else{ %>
			     <table width="400">
		     	<%} %>
						<tr>
						<% if(columnCount.equals("1")){ %>
			            <td width="800" colspan="2">
		     	        <% }else{ %>
			            <td width="400">
		            	<%} %>
					<%
						++k;
					%>
					<strong><%=k%>.&nbsp;<bean:write name="element2" property="itemName" filter="true"/>
					<logic:equal value="1" name="element2" property="selects">
					<logic:equal value="0" name="element2" property="maxvalue">
					  <logic:equal value="0" name="element2" property="minvalue">
					  &nbsp;
					  </logic:equal>
					  <logic:notEqual value="0" name="element2" property="minvalue">
					  (最少选<bean:write name="element2" property="minvalue"/>个)
					  </logic:notEqual>
					</logic:equal>
					<logic:notEqual value="0" name="element2" property="maxvalue">
					(最多选<bean:write name="element2" property="maxvalue"/>个
					 <logic:equal value="0" name="element2" property="minvalue">
					  )&nbsp;
					  </logic:equal>
					   <logic:notEqual value="0" name="element2" property="minvalue">
					  ,最少选<bean:write name="element2" property="minvalue"/>个)
					  </logic:notEqual>
					</logic:notEqual>
					</logic:equal>
					<logic:equal name="element2" property="fillflag" value="1"><font color="red">&nbsp;*</font></logic:equal></strong><br>
				
						</td>
						</tr>
				        <% int x=1; %>
						<logic:iterate id="element3" name="element2" property="pointList">
							<% if(columnCount.equals("1")){ 
							     if(x==1||x%2==1){
							%>
							   <tr>
							<%
						    	}
						  	}else{ 
						  	%>
							<tr>
							<%
							}
						     %>
							<td width="400" style="word-break:break-all">
							<input id="<bean:write name="element3" property="pointid" filter="true"/>" type="checkbox" name="<bean:write name="element3" property="multitem" filter="true"/>" value="<bean:write name="element3" property="pointid" filter="true"/>"/>
							<bean:write name="element3" property="pointName" filter="true"/>&nbsp;
							<logic:equal name="element3" property="describestatus" value="1">
							<%if(!columnCount.equals("1")){ %>
								<br>
								&nbsp;&nbsp;&nbsp;
								<%} %>
								<input id="desc<bean:write name="element3" property="pointid" filter="true"/>" onfocus="autoSelect('<bean:write name="element3" property="pointid" filter="true"/>')" type="text" class="text4" name="<bean:write name="element3" property="pointContext" filter="true"/>"/>
							</logic:equal>
							</td>
							<% if(columnCount.equals("1")){ 
							     if(x!=0&&x%2==0){
							%>
							   </tr>
							<%
						    	}
						  	}else{ 
						  	%>
							</tr>
							<%
							}
							 x++; %>
						</logic:iterate>
				
					</table><br>
				</logic:equal>
				<logic:equal name="element2" property="itemStatus" value="1">
					<%
						++k;
					%>
					<strong><%=k%>.&nbsp;<bean:write name="element2" property="itemName" filter="true"/></strong><logic:equal name="element2" property="fillflag" value="1"><font color="red">&nbsp;*</font></logic:equal><br>
					    &nbsp;<textarea id="desc<bean:write name="element2" property="itemid" filter="true"/>" name="<bean:write name="element2" property="itemContext" filter="true"/>" cols="40" rows="7"></textarea> <br> 
				</logic:equal>	
				</td>
				<%
				if(columnCount.equals("1"))
				{
				%>
				<td width="350" >
				&nbsp;
				</td>
				</tr>
				<% if(k!=count){ %>
				<tr>
				<% 
				}
				}
				else
				{
				  if(k%2==0 && k!=count)
				  {
				%>
				</tr>
				<tr>
				<%
				    }
				    if(k%2==1 && k!=count)
				    {
				%>
				<td width="350" >
				&nbsp;
				</td>
				<%
				   }
			    	if(k==count && k%2==1)
				   {
				%>
				<td width="350">
				&nbsp;
				</td>
				<%
				 }
				}
				%>	
				
			</logic:iterate>
			</tr>
			</table>
			</td>
			</tr>
			<tr>
				<td align="center" class="RecordRow" style="height:35px;">
				
	 			<input type="button" id="submitQuestion"  name="dd" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub(2,this);" class="mybutton"/>
	 			
	 			<input type="button" id="saveQuestion"  name="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub(1,this);" class="mybutton"/>
	 			
	 			<html:reset styleClass="mybutton"  styleId="resetQuestion" property="reset">
	 			<bean:message key="button.clear"/>
	 			</html:reset>
	 			
	 			 <hrms:priv func_id="1110"> 
	 			<input type="button" class="mybutton" value="<bean:message key="lable.welcoeminv.lookresult"/>" onclick="doExamint('<bean:write name="element" property="mdid" filter="true"/>');">
	 			</hrms:priv>
	 			<logic:equal value="1" name="questionnaireForm" property="isClose">
	 				<input type="button" name="dgg" value="<bean:message key="button.return"/>" onclick="backHome('${questionnaireForm.enteryType}','${questionnaireForm.enteryFlag}','${questionnaireForm.home}');" class="mybutton"/>
	 			</logic:equal>
	 			</td>
	 		</tr>
	 	</form>
	</logic:iterate>
	
	 
 </table>
 
 <div id='wait' style='position:absolute;top:40%;left:40%;display:none;'>
	  <table border="1" width="300" cellspacing="0" cellpadding="4" style="border-collapse: collapse;" bgcolor="#FFFFFF" height="87" align="center">
	           <tr>
	             <td bgcolor="#f4f7f7" style="font-size:12px;color:#000000" height=24><bean:message key="leaderteam.leaderframe.waitingmessage"/></td>
	           </tr>
	           <tr>
	             <td style="font-size:12px;line-height:200%" align=center>
	               <marquee style="border:1px solid #000000" direction="right" width="300" scrollamount="5" scrolldelay="10" bgcolor="#FFFFFF">
	                 <table cellspacing="1" cellpadding="0">
	                   <tr height=8>
	                     <td bgcolor=#3399FF width=8></td>
	                         <td></td>
	                         <td bgcolor=#3399FF width=8></td>
	                         <td></td>
	                         <td bgcolor=#3399FF width=8></td>
	                         <td></td>
	                         <td bgcolor=#3399FF width=8></td>
	                         <td></td>
	                    </tr>
	                  </table>
	               </marquee>
	             </td>
	          </tr>
	        </table>
	</div>
	<%
	
	if(questionnaireForm.getMessage().equals(""))
	{
	}
	else
	{
	%>
	<script>
	alert("<%=questionnaireForm.getMessage()%>");
	window.close();
	</script>
	<%
		questionnaireForm.setMessage("");
		questionnaireForm.clearMessage();
	}
	%>
	<%
	if(questionnaireForm.getTopicList().size()<=0)
	{
	%>
	<script>
	alert('没有问卷!');
	window.close();
	</script>
	<%
	}
	%>
</center>	
<script>
	<%
		ArrayList itemslist = questionnaireForm.getAnswerList();
		for(int m=0;m<itemslist.size();m++){
			String answerPoint=(String)itemslist.get(m);
			%>
	
			var answer_Point="<%=answerPoint%>";
			document.getElementById(answer_Point).checked="true";
	
		<%
		}
	%>
	
	<%
		ArrayList answerDesc = questionnaireForm.getAnswerDesc();
		for(int n=0;n<answerDesc.size();n++){
			String answer = (String)answerDesc.get(n);
			String [] answerArray = answer.split("`");
	%>
		var answer_point = "desc<%=answerArray[0] %>";
		var answer_desc = "<%=answerArray[1] %>";
		document.getElementById(answer_point).value=answer_desc;
		<%}%>
		
		<%
		ArrayList eassyDesc = questionnaireForm.getEssayDesc();
		for(int p=0;p<eassyDesc.size();p++){
			String eassy = (String)eassyDesc.get(p);
			String [] eassyArray = eassy.split("`");
	%>
		var eassy_point = "desc<%=eassyArray[0] %>";
		var eassy_desc = "<%=eassyArray[1] %>";
		eassy_desc=eassy_desc.replace(/<br>/g,"\r\n");
		document.getElementById(eassy_point).value=eassy_desc;
		<%}%>
		
		<%
			String state = questionnaireForm.getState();
		if("2".equals(state)){
		%>
		document.getElementById("submitQuestion").disabled=true;
		document.getElementById("saveQuestion").disabled=true;
		document.getElementById("resetQuestion").disabled=true;
		<%} %>
		
	</script>