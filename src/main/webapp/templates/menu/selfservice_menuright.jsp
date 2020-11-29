<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="/templates/menu/error.jsp"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.constant.SystemConfig" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.welcome.WelcomeForm"%>
<jsp:useBean id="welcomeForm" class="com.hjsj.hrms.actionform.welcome.WelcomeForm" scope="session" />

<%
	if(session.getAttribute("welcomeForm")==null)
	{
		%>
			<script>
			var target_url="/selfservice/lawbase/success.jsp";	window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=596,height=354'); 
			</script>
		<%
		return;
	}
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
           //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	
        hcmflag=userView.getBosflag(); 
	}

	int k=0;
	int j=0;
    String isturn=SystemConfig.getPropertyValue("Menutogglecollapse");
    WelcomeForm welcomeF = (WelcomeForm)session.getAttribute("welcomeForm");
    String homePageHotId=welcomeForm.getHomePageHotId();
 try
 {
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/leftmenu.css" type="text/css">
<SCRIPT language="JavaScript1.2"  src="/js/menu.js"></SCRIPT>
<SCRIPT language="JavaScript1.2"  src="/ajax/common.js"></SCRIPT>
<SCRIPT LANGUAGE="JavaScript">
   var  divHeight = window.screen.availHeight - window.screenTop -180;
   var flag=0;
   function turn()
   {
    <%if(isturn==null||!isturn.equals("false")){%>
	Element.hide('panel');
	    <%}%>
   }      
   
   
   function show(objname)
   {
	   	objname.style.display='block';
   }
   
   function hide(objname)
   {
       
	      	objname.style.display='none';
   }
    function showA(objname,desc)
   {
      if(desc!='')
	     document.getElementById(objname).style.display='block';
   }
   
   function hideA(objname,desc)
   {
       if(desc!='')
	    	document.getElementById(objname).style.display='none';
   }
   function sub(flag,index,fillflag,itemStatus,childIndex,itemName,id,selects,maxvalue,minvalue)
 {
    var objform=window.document.forms;
    var welcomeForm=objform[id];
    <%if(homePageHotId.equals("-1")){%>
    welcomeForm.target="il_right";
    <%}%>
             if(itemStatus=="0")
             {
               if(fillflag=="1")
              {
                var sum = 0;
                var textsum=0;
                for(var j=0;j<=childIndex;j++)
                {                  
                var radio = document.getElementById(index+"radio"+j);
                  if(radio)
                  {
                     if(radio.checked)
                     {
                         sum++;
                     }
                  }
                }
                if(sum==0)
                {
                    for(var j=0;j<=childIndex;j++)
                   {
                   var text = document.getElementById(index+"text"+j);
                  if(text)
                   {
                     if(text.value!="")
                     {
                         textsum++;
                     }
                   }
                 }
                }
               
                if(sum==0&&textsum==0)
                {
                      alert(itemName+"<bean:message key="lable.investigate_item.must"/>");
                      return;
                }
                }
                welcomeForm.action="/selfservice/welcome/investigate.do?b_add=add&questionFlag="+flag;
                welcomeForm.submit();
             }
             else if(itemStatus == "1")
             {
                if(fillflag=="1")
                {
                 var obj = document.getElementById("area"+index);
                 if(obj)
                 {
                     if(obj.value == "")
                     {
                         alert(itemName+"<bean:message key="lable.investigate_item.must"/>");
                         return;
                     }
                 }
                 }
                 welcomeForm.action="/selfservice/welcome/invquestion.do?b_add=add&questionFlag="+flag;
                 welcomeForm.submit();
             }
             else if(itemStatus =="2")
             { 
               if(fillflag=="1")
             {
                var sum = 0;
                var textsum=0;
                for(var j=0;j<=childIndex;j++)
                {
                  var radio = document.getElementById(index+"checkbox"+j);
                  if(radio)
                  {
                     if(radio.checked)
                     {
                         sum++;
                     }
                  }
                }
                if(sum==0)
                {
                    for(var j=0;j<=childIndex;j++)
                   {
                   var text = document.getElementById(index+"checktext"+j);
                  if(text)
                   {
                     if(text.value!="")
                     {
                         textsum++;
                     }
                   }
                 }
                }
                if(sum==0&&textsum==0)
                {
                
                      alert(itemName+"<bean:message key="lable.investigate_item.must"/>");
                      return;
                }
                }
                var boxsum=0;
                for(var j=0;j<=childIndex;j++)
                {
                  var radio = document.getElementById(index+"checkbox"+j);
                  if(radio)
                  {
                     if(radio.checked)
                     {
                         boxsum++;
                     }
                  }
                }
                if(selects=='1')
                {
                   if(maxvalue*1!=0&&boxsum>maxvalue*1)
                   {
                      alert(itemName+"最多选择"+maxvalue+"个!");
                      return;
                   }
                   if(minvalue*1!=0&&boxsum<minvalue*1)
                   {
                      alert(itemName+"最少选择"+minvalue+"个!");
                      return;
                   }
                }
                welcomeForm.action="/selfservice/welcome/investigateMult.do?b_add=add&questionFlag="+flag;
                welcomeForm.submit();
             }
              
 }
 function returnInfo(outparamters){
  
 }
 function lookResult(flag)
 {
   if(flag=='0')
   {
      welcomeForm.action="/selfservice/welcome/investigateMult.do?b_examine=examine";
      welcomeForm.submit();
   }
   else
   {
       welcomeForm.action="/selfservice/welcome/invquestion.do?b_examine=examine";
       welcomeForm.submit();
   }
 }
 function backHome(flag,home)
 {
     if(flag=='0')
     {
          welcomeForm.action="/selfservice/welcome/hot_topic.do?b_more=more&discriminateFlag=<%=(request.getParameter("discriminateFlag"))%>&home="+home;
          welcomeForm.submit();
     }
     else
     {
       if(home=='5'){
       		if("hcm"=='<%=hcmflag%>'){
       			document.location="/templates/index/hcm_portal.do?b_query=link";  
       		}else{
          		document.location="/templates/index/portal.do?b_query=link";      		
       		}
       }

     }
 }
</SCRIPT>
<style>
body.bodyRight {

	background-color: #DEEAF5;

	/*background-repeat: repeat;*/ 
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	line-height: 24px;
	color: #336699;
	margin:0 0 0 0;
}
.RecordRowinvestigate_b{
	border: inset 1px  #A8D1F1;
	BORDER-BOTTOM: #A8D1F1 1pt solid; 
	BORDER-LEFT:  #A8D1F1 1pt solid; 
	BORDER-RIGHT:  #A8D1F1 1pt solid; 
	BORDER-TOP:  0pt;
	font-size: 12px;
	height:22;
}
</style>
<hrms:themes />
	<%
   				int num=0;
   				int i=0;
   				%>

<logic:equal value="-1" name="welcomeForm" property="homePageHotId"> 				
<body   class="bodyRight"> 
<div id="panel" >

	
	            <%
	             
	             if(welcomeForm.getTopicList2().size()>0)
				 {
	            
				%>
             	 <table cellpadding=0 cellspacing=0 width="169" class="menu_table" >
           		 <tr>
            			 <td align="left" class="menu_title"  id="menuTitle2"><img src="/images/darrow.gif" border="0">问卷调查
            			 </td>
            		</tr>
            		
            	 <logic:iterate  id="element3" name="welcomeForm" property="topicList2" scope="session">
            		<tr class="trDeep">
		  		  		<td  background="/images/diao.gif" colspan="2" height="20" align="left">
		  		       		&nbsp;<img src="/images/forumme1.gif" border="0">&nbsp;<a href="/selfservice/infomanager/askinv/questionnaire.do?b_query=link&id=<bean:write name="element3" property="mdid" filter="true"/>&flag=<bean:write name="element3" property="flag" filter="true"/>&isClose=0&home=4&enteryType=1" target="_blank"> <bean:write name="element3" property="content" filter="true"/></a><br>  		
		  		 		 </td>
  					</tr>
  				 </logic:iterate>
              	 </table>
              	<%
              	}
              	
              	if(welcomeForm.getTrainEvaluateList().size()>0)
              	{
              	              	
              	%>
              	 <table cellpadding=0 cellspacing=0 width="169" class="menu_table" >
           		 <tr>
            			 <td align="left" class="menu_title"  id="menuTitle2"><img src="/images/darrow.gif" border="0">培训评估
             			&nbsp;&nbsp;&nbsp;&nbsp;
             
            			 </td>
            		</tr>
            		
            	 <logic:iterate  id="element3" name="welcomeForm" property="trainEvaluateList" scope="session">
            		<logic:equal name="element3" property="name" value="0">
	            		<tr class="trDeep">
			  		  		<td  background="/images/diao.gif" colspan="2" height="20" align="left">
			  		       		&nbsp;<img src="/images/forumme1.gif" border="0">&nbsp;<a href="/selfservice/infomanager/askinv/questionnaire.do?b_query=link&id=<bean:write name="element3" property="mdvalue" filter="true"/>&flag=2&isClose=0&home=4&enteryType=1" target="_blank"> <bean:write name="element3" property="desc" filter="true"/></a><br>  		
			  		 		 </td>
	  					</tr>
  					</logic:equal>
  					<logic:equal name="element3" property="name" value="1">
	            		<tr class="trDeep">
			  		  		<td  background="/images/diao.gif" colspan="2" height="20" align="left">
			  		       		&nbsp;<img src="/images/forumme1.gif" border="0">&nbsp;<a href="/train/evaluatingStencil.do?b_query=link&r3101=<bean:write name="element3" property="r3101" filter="true"/>&id=<bean:write name="element3" property="value" filter="true"/>&flag=2&type=<bean:write name="element3" property="type" filter="true"/>&isClose=0&home=4&enteryType=1" target="_blank"> <bean:write name="element3" property="desc" filter="true"/></a><br>  		
			  		 		 </td>
	  					</tr>
  					</logic:equal>
  					
  				 </logic:iterate>
              	 </table>
				<%
				}
			   if(welcomeForm.getDisplayContral().equals("0"))
				{
				%>
				<logic:equal value="-1" name="welcomeForm" property="homePageHotId"> 		
   <table cellpadding=0 cellspacing=0 width="169" class="menu_table" >
         <tr>
            			 <td align="left" class="menu_title"  id="menuTitle2"><img src="/images/darrow.gif" border="0">热点调查
            			 </td>
            		</tr>
    </table>	
   </logic:equal>
           		 
   				<% } 
   				%>
   					
  <logic:iterate  id="element2" name="welcomeForm" property="topicList" scope="session">     
        <%  num++;
        i++;
         %>
        
        <logic:notEqual name="element2" property="itemName" value="">
       
        	<logic:equal name="element2" property="itemStatus" value="1">
       		<table cellpadding=0 cellspacing=0 width="169" class="menu_table" style="margin-top:10px;">	
       			<form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/invquestion.do" method="post" target="il_body">
       			<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
       			<html:hidden name="welcomeForm" property="homePageHotId" value="-1"/>
       			<tr class="trDeep">
  		  		<td  background="/images/diao.gif" colspan="2" height="20"   align="left">
  		       		<img src="/images/forumme.gif">&nbsp;<bean:write name="element2" property="itemName" filter="true"/><logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br> 		
  		 		 </td>
  			</tr>
  			
  			<tr>
  				<td colspan='2' id='a<%=num%>' style='display:none' >
  				<bean:write name="element2" property="description" filter="false"/> 
  				</td>
  			</tr>
  			
       			<tr>
  	 	 		 <td colspan="2" align="left" class="RecordRowinvestigate" width="169" style="word-break:break-all">
  		      			 <textarea name="hotquestion" id="<%="area"+i%>" style="BORDER-BOTTOM: 0px solid; BORDER-LEFT: 0px solid; BORDER-RIGHT: 0px solid;overflow-y:hidden;"></textarea> 
  		 		 <br>
				 </td>
	 		</tr>
       			<tr>
	 	      		<td align="center" colspan="2" class="RecordRowinvestigate">
      				<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	             <input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">  
	 	                <hrms:priv func_id="1110"> 
	 	                
	 	 		  <hrms:submit styleClass="mybutton" property="b_examine">
            				<bean:message key="lable.welcoeminv.lookresult"/>
	 			  </hrms:submit>
	 			 
	 			   <!-- 
	 			   <input type="button" class="mybutton" value="<bean:message key="lable.welcoeminv.lookresult"/>" onclick="lookResult('1');"/>
	 			  -->
	 			</hrms:priv> 
	 			</td>
	 		</tr>
	 	
       			</form>
       	      </table>
        	</logic:equal>        	
       		<logic:notEqual name="element2" property="itemStatus" value="1">
       		   <logic:equal name="element2" property="itemStatus" value="0">
          		 <table cellpadding=0 cellspacing=0 width="169" class="menu_table" style="margin-top:10px;">
          			 <form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/investigate.do" method="post" target="il_body">
            			 <tr>
            		 			<td>
                        <html:hidden name="welcomeForm" property="homePageHotId" value="-1"/>
						<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
  						<table border="0" cellspacing="1"  align="center" cellpadding="1" width="100%" class="">
  						<tr class="trDeep">
  		 					 <td  background="/images/diao.gif" colspan="2" height="20" align="left" >
  		       						 <input type="hidden" name="id" value="<bean:write name="element2" property="id" filter="true"/>">
								<img src="/images/forumme.gif">&nbsp;
							
								  <bean:write name="element2" property="itemName" filter="true"/><logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br>  	
								 
  		 					 </td>
  						</tr>
  						<tr>
			  				<td colspan='2' id='a<%=num%>' style='display:none' >
			  				<bean:write name="element2" property="description" filter="false"/> 
			  				</td>
			  			</tr>
  						
  						
  	 					<tr>
  	 	 			 		<td colspan="2" align="left" class="RecordRowinvestigate" width="169" style="word-break:break-all">
  		 					 <%int h=0; %>
  		 					 <logic:iterate id="test" name="element2" property="pointList" >
  		   
 		    					<input type="radio" id="<bean:write name="test" property="pointid" filter="true"/>" name="hotcheck"  value="<bean:write name="test" property="pointid" filter="true"/>">
    		   						 <bean:write name="test" property="pointName" filter="true"/>
    		   						  <logic:equal name="test" property="describestatus" value="1">
    		   						 	  <input type="text" id="desc<bean:write name="test" property="pointid" filter="true"/>" class="TEXT4" name="<bean:write name="test" property="pointContext" filter="true"/>"/>
    		   						 </logic:equal>
                  					<br>
                  					<%h++; %>
  							 </logic:iterate>
  		 					 <br>
							 </td>
	 					</tr>
	 					<tr>
	 						<td align="center" colspan="2" class="RecordRowinvestigate">
      								<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	                            <input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">                   
	 	                                               <hrms:priv func_id="1110"> 
	 	                                               
	 	                                            
	 	  						<hrms:submit styleClass="mybutton" property="b_examine">
            								<bean:message key="lable.welcoeminv.lookresult"/>
	 							</hrms:submit>
	 							
	 							  <!-- 
	 							<input type="button" class="mybutton" value="<bean:message key="lable.welcoeminv.lookresult"/>" onclick="lookResult('1');"/>
	 						       -->
	 						       </hrms:priv>
	 						</td>
	 					</tr>
	 					</table>
            
           				</td>
            			</tr>
            			 </form> 
          		</table>
          	</logic:equal>
          	<logic:notEqual name="element2" property="itemStatus" value="0">
          		 <table cellpadding=0 cellspacing=0 width="169" class="menu_table" style="margin-top:10px;">
          			 <form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/investigateMult.do" method="post" target="il_body">
            			 <tr>
            		 			<td>
                         <html:hidden name="welcomeForm" property="homePageHotId" value="-1"/>
						<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
						<input type="hidden" name="id" value="<bean:write name="element2" property="id" filter="true"/>">
  						<table border="0" cellspacing="1"  align="center" cellpadding="1" width="100%" class="">
  						<tr class="trDeep">
  		 					 <td  background="/images/diao.gif" colspan="2" height="20" align="center" >
  		       						 
								<img src="/images/forumme.gif">&nbsp;  <bean:write name="element2" property="itemName" filter="true"/>
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
								
								<logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br>  		
  		 					 </td>
  						</tr>
  						<tr>
			  				<td colspan='2' id='a<%=num%>' style='display:none' >
			  				<bean:write name="element2" property="description" filter="false"/> 
			  				</td>
			  			</tr>
  						
  						
  						
  	 					<tr>
  	 	 			 		<td colspan="2" align="left" class="RecordRowinvestigate" width="169" style="word-break:break-all">
  	 	 			 		<%  int h=0; %>
  	 	 			 		 <logic:iterate id="test" name="element2" property="pointList" indexId="index">
  		        					<input type="checkbox" id="<bean:write name="test" property="pointid" filter="true"/>" name="hotmultcheck" value="<bean:write name="test" property="pointid" filter="true"/>">
    		   						 <bean:write name="test" property="pointName" filter="true"/>
    		   						 <logic:equal name="test" property="describestatus" value="1">
    		   						 	  <input type="text" id="desc<bean:write name="test" property="pointid" filter="true"/>"name="<bean:write name="test" property="pointContext" filter="true"/>"/>
    		   						 </logic:equal>
    		   					   	 <br>
    		   					   	 <%h++; %>
  							 </logic:iterate>
  		 					 <br>
							 </td>
	 					</tr>
	 					<tr>
	 						<td align="center" colspan="2" class="RecordRowinvestigate">
      								<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>','<bean:write name="element2" property="selects" filter="true"/>','<bean:write name="element2" property="maxvalue" filter="true"/>','<bean:write name="element2" property="minvalue" filter="true"/>');">
            						<input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>','<bean:write name="element2" property="selects" filter="true"/>','<bean:write name="element2" property="maxvalue" filter="true"/>','<bean:write name="element2" property="minvalue" filter="true"/>');">		
	 							
	 	                                               <hrms:priv func_id="1110"> 
	 	                                      
	 	  						<hrms:submit styleClass="mybutton" property="b_examine">
            								<bean:message key="lable.welcoeminv.lookresult"/>
	 							</hrms:submit>
	 							
	 							         <!-- 
	 							<input type="button" class="mybutton" value="<bean:message key="lable.welcoeminv.lookresult"/>" onclick="lookResult('0');"/>
	 						      -->
	 						       </hrms:priv>
	 						       
	 						        
	 						</td>
	 					</tr>
	 					</table>
            
           				</td>
            			</tr>
            			 </form> 
          		</table>
          	</logic:notEqual>
            </logic:notEqual>
	</logic:notEqual>
     </logic:iterate>
     </div>   
     </body>
     </logic:equal>  
     
     <logic:notEqual value="-1" name="welcomeForm" property="homePageHotId">
  
      <logic:iterate  id="element2" name="welcomeForm" property="topicList" scope="session">     
        <%  num++;
        i++;
         %>
        
        <logic:notEqual name="element2" property="itemName" value="">
       
        	<logic:equal name="element2" property="itemStatus" value="1">
       		<table align="center" cellpadding=0 cellspacing=0 width="80%" border="0" style="margin-top:10px;" >	
       			<form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/invquestion.do" method="post" target="il_body">
       			<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
       			<html:hidden name="welcomeForm" property="homePageHotId"/>
       			<html:hidden name="welcomeForm" property="home"/>
       			<html:hidden name="welcomeForm" property="enteryType"/>
       			<tr class="trDeep">
  		  		<td class="TableRow" colspan="2" height="20"   align="left">
  		       		<img src="/images/forumme.gif">&nbsp;<bean:write name="element2" property="itemName" filter="true"/><logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br> 		
  		 		 </td>
  			</tr>
  			
  			<tr id='a<%=num%>' style='display:none' >
  				<td colspan='2' class="RecordRowinvestigate_b common_border_color">
  				<bean:write name="element2" property="description" filter="false"/> 
  				</td>
  			</tr>
  			
       			<tr>
  	 	 		 <td colspan="2" align="left" class="RecordRowinvestigate_b common_border_color" width="100%" style="word-break:break-all">
  		      			 <textarea name="hotquestion" id="hotquestionid"  style="BORDER-BOTTOM: 0px solid;BORDER-TOP: 0px solid; BORDER-LEFT: 0px solid; BORDER-RIGHT: 0px solid;overflow-y:hidden;" cols="140" rows='11'></textarea>   
  		 		 <br>
				 </td>
	 		</tr>
       			<tr>
	 	      		<td align="center" colspan="2" style="padding-top:4px">
      				<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	            <input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	                <hrms:priv func_id="1110"> 
	 	                
	 	 		  <hrms:submit styleClass="mybutton" property="b_examine">
            				<bean:message key="lable.welcoeminv.lookresult"/>
	 			  </hrms:submit>
	 			   	</hrms:priv> 
	 			  <input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="backHome('<bean:write name="welcomeForm" property="enteryType"/>','<bean:write name="welcomeForm" property="home"/>');"/>
	
	 			</td>
	 		</tr>
	 	
       			</form>
       	      </table>
        	</logic:equal>        	
       		<logic:notEqual name="element2" property="itemStatus" value="1">
       		   <logic:equal name="element2" property="itemStatus" value="0">
          		 <table align="center" cellpadding=0 cellspacing=0 width="80%" border="0" style="margin-top:10px;">
          			 <form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/investigate.do" method="post" target="il_body">
            			 <tr>
            		 			<td>
                        <html:hidden name="welcomeForm" property="homePageHotId"/>
                        <html:hidden name="welcomeForm" property="home"/>
       		         	<html:hidden name="welcomeForm" property="enteryType"/>
						<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
  						<table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%" class="">
  						<tr class="trDeep">
  		 					 <td class="TableRow" colspan="2" height="20" align="left"  >
  		       						 <input type="hidden" name="id" value="<bean:write name="element2" property="id" filter="true"/>">
								<img src="/images/forumme.gif">&nbsp;
							
								  <bean:write name="element2" property="itemName" filter="true"/><logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br>  	
								 
  		 					 </td>
  						</tr>
  						<tr id='a<%=num%>' style='display:none'>
			  				<td colspan='2'  class="RecordRowinvestigate_b common_border_color">
			  				<bean:write name="element2" property="description" filter="false"/> 
			  				</td>
			  			</tr>
  						
  						
  	 					<tr>
  	 	 			 		<td colspan="2" align="left" class="RecordRowinvestigate_b common_border_color" width="100%" style="word-break:break-all">
  		 					 <%int h=0; %>
  		 					 <logic:iterate id="test" name="element2" property="pointList" >
  		   
 		    					<input type="radio" id="<bean:write name="test" property="pointid" filter="true"/>" name="hotcheck"  value="<bean:write name="test" property="pointid" filter="true"/>">
    		   						 <bean:write name="test" property="pointName" filter="true"/>
    		   						  <logic:equal name="test" property="describestatus" value="1">
    		   						 	  <input type="text" id="desc<bean:write name="test" property="pointid" filter="true"/>" class="TEXT4"name="<bean:write name="test" property="pointContext" filter="true"/>"/>
    		   						 </logic:equal>
                  					<br>
                  					<%h++; %>
  							 </logic:iterate>
  		 					 <br>
							 </td>
	 					</tr>
	 					<tr>
	 						<td align="center" colspan="2" style="padding-top:4px">
      								<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	                            <input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>');">
	 	                                               <hrms:priv func_id="1110"> 
	 	                                              
	 	                                        <hrms:submit styleClass="mybutton" property="b_examine">
            								<bean:message key="lable.welcoeminv.lookresult"/>
	 							</hrms:submit>
	 							 </hrms:priv>
	 							 <input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="backHome('<bean:write name="welcomeForm" property="enteryType"/>','<bean:write name="welcomeForm" property="home"/>');"/>
	 						      
	 						</td>
	 					</tr>
	 					</table>
            
           				</td>
            			</tr>
            			 </form> 
          		</table>
          	</logic:equal>
          	<logic:notEqual name="element2" property="itemStatus" value="0">
          		 <table align="center" cellpadding=0 cellspacing=0 width="80%" border="0" style="margin-top:10px;">
          			 <form name="welcomeForm" id=<%="form"+i%> action="/selfservice/welcome/investigateMult.do" method="post" target="il_body">
            			 <tr>
            		 			<td>
                        <html:hidden name="welcomeForm" property="homePageHotId"/>
                        <html:hidden name="welcomeForm" property="home"/>
       		        	<html:hidden name="welcomeForm" property="enteryType"/>
						<input type="hidden" name="itemid" value="<bean:write name="element2" property="itemid" filter="true"/>">
						<input type="hidden" name="id" value="<bean:write name="element2" property="id" filter="true"/>">
  						<table border="0" cellspacing="0"  align="center" cellpadding="0" width="100%" class="">
  						<tr class="trDeep">
  		 					 <td  class="TableRow" colspan="2" height="20" align="center"       >
  		       						 
								<img src="/images/forumme.gif">&nbsp;  <bean:write name="element2" property="itemName" filter="true"/>
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
								
								<logic:equal name="element2" property="fillflag" value="1"><font color="red">*</font></logic:equal><br>  		
  		 					 </td>
  						</tr>
  						<tr id='a<%=num%>' style='display:none'>
			  				<td colspan='2' class='RecordRowinvestigate_b common_border_color' >
			  				<bean:write name="element2" property="description" filter="false"/> 
			  			</tr>
  						
  						
  						
  	 					<tr>
  	 	 			 		<td colspan="2" align="left" class="RecordRowinvestigate_b common_border_color" width="100%" style="word-break:break-all">
  	 	 			 		<%  int h=0; %>
  	 	 			 		 <logic:iterate id="test" name="element2" property="pointList" indexId="index">
  		        					<input type="checkbox" id="<bean:write name="test" property="pointid" filter="true"/>" name="hotmultcheck" value="<bean:write name="test" property="pointid" filter="true"/>">
    		   						 <bean:write name="test" property="pointName" filter="true"/>
    		   						 <logic:equal name="test" property="describestatus" value="1">
    		   						 	  <input type="text" id="desc<bean:write name="test" property="pointid" filter="true"/>" name="<bean:write name="test" property="pointContext" filter="true"/>"/>
    		   						 </logic:equal>
    		   					   	 <br>
    		   					   	 <%h++; %>
  							 </logic:iterate>
  		 					 <br>
							 </td>
	 					</tr>
	 					<tr>
	 						<td align="center" colspan="2" style="padding-top:4px">
      								<input type="button" id="submitQuestion" class="mybutton" property="add" value="<bean:message key="lable.welcomeinv.sumbit"/>" onclick="sub('2','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>','<bean:write name="element2" property="selects" filter="true"/>','<bean:write name="element2" property="maxvalue" filter="true"/>','<bean:write name="element2" property="minvalue" filter="true"/>');">
            						<input type="button" id="saveQuestion" class="mybutton" property="save" value="<bean:message key="lable.welcomeinv.save"/>" onclick="sub('1','<%=i%>','<bean:write name="element2" property="fillflag"/>','<bean:write name="element2" property="itemStatus"/>','<%=h%>','<bean:write name="element2" property="itemName" filter="true"/>','<%=(i-1)%>','<bean:write name="element2" property="selects" filter="true"/>','<bean:write name="element2" property="maxvalue" filter="true"/>','<bean:write name="element2" property="minvalue" filter="true"/>');">		
	 							
	 	                                               <hrms:priv func_id="1110"> 
	 	                                               
	 	  						<hrms:submit styleClass="mybutton" property="b_examine">
            								<bean:message key="lable.welcoeminv.lookresult"/>
	 							</hrms:submit>
	 						        </hrms:priv>
	 						         <input type="button" class="mybutton" value="<bean:message key="button.return"/>" onclick="backHome('<bean:write name="welcomeForm" property="enteryType"/>','<bean:write name="welcomeForm" property="home"/>');"/>
	 						</td>
	 					</tr>
	 					</table>
            
           				</td>
            			</tr>
            			 </form> 
          		</table>
          	</logic:notEqual>
            </logic:notEqual>
	</logic:notEqual>
     </logic:iterate>
     
     
     
     
     
     </logic:notEqual>
	 <%
 }
catch(Exception ex)
{
}
	%>
   <script>
	<%
		ArrayList itemslist = welcomeF.getAnswerList();
		for(int m=0;m<itemslist.size();m++){
			String answerPoint=(String)itemslist.get(m);
			%>
	
			var answerPoint="<%=answerPoint%>";
			document.getElementById(answerPoint).checked="true";
	
		<%
		}
	%>
	
	<%
		ArrayList answerDesc = welcomeF.getAnswerDesc();
		for(int n=0;n<answerDesc.size();n++){
			String answer = (String)answerDesc.get(n);
			String [] answerArray = answer.split("`");
	%>
		var answer_point = "desc<%=answerArray[0] %>";
		var answer_desc = "<%=answerArray[1] %>";
		document.getElementById(answer_point).value=answer_desc;
		<%}%>
		
		<%
		ArrayList eassyDesc = welcomeF.getEssayDesc();
		for(int p=0;p<eassyDesc.size();p++){
			String eassy = (String)eassyDesc.get(p);
			String [] eassyArray = eassy.split("`");
	%>
		var eassy_point = "desc<%=eassyArray[0] %>";
		var eassy_desc = "<%=eassyArray[1] %>";
		eassy_desc=eassy_desc.replace(/<br>/g,"\r\n");
		if(document.getElementById(eassy_point)!=null)
			  document.getElementById(eassy_point).value=eassy_desc;
		else
			document.getElementById("hotquestionid").value=eassy_desc;
		<%}%>
		
		<%
			String state = welcomeF.getState();
		if("2".equals(state)){
		%>
		document.getElementById("submitQuestion").disabled=true;
		document.getElementById("saveQuestion").disabled=true;
		<%} %>
		
	</script>






                                                                              