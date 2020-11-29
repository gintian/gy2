<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hjsj.hrms.actionform.performance.achivement.AchievementTaskForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<%
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList targetColumnList=(ArrayList)achievementTaskForm.getTargetColumnList();
	ArrayList pointClassList=(ArrayList)achievementTaskForm.getPointClassList();
	ArrayList selectedPointList=(ArrayList)achievementTaskForm.getSelectedPointList();
	String root_url=(String)achievementTaskForm.getRoot_url();
	String opt=request.getParameter("opt");
	if(opt==null)
	 	opt="edit";
	 
	int nameIndex=0;
	for(int i=0;i<targetColumnList.size();i++)
	{
		LazyDynaBean abean=(LazyDynaBean)targetColumnList.get(i);
		String itemid=(String)abean.get("itemid");
		if(itemid.equalsIgnoreCase("name"))
		{
			nameIndex=i;
			break;
		}
	}
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
	
 %>				 
				 
<html>
  <head>


  </head>
  <script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
  <script language='javascript'>
    function closeWin(){
    	if(window.showModalDialog){
    		parent.window.close();
    	}else{
    		parent.parent.Ext.getCmp("newTargetWin").close();
    	}
	}
  <%String callbackFunc = request.getParameter("callbackFunc");%>
  var nameIndex=<%=nameIndex%>
  var callbackFunc = '<%=callbackFunc%>';
  <%  
  		
   		if(request.getParameter("b_saveTarget")!=null&&request.getParameter("b_saveTarget").equals("save"))
   		{
   			String callbackFunc_sub = request.getParameter("callbackFunc_sub");
   	%>
	   		if(window.showModalDialog){
				parent.window.returnValue="<%=root_url%>";
			}else{
				eval(<%=callbackFunc_sub%>)('<%=root_url%>');
			}
			closeWin();
 	<%}%>
  
  
  
  
 
  
  function next()
  {
	  var obj=$('pageset');
	  if(obj.setSelectedTab){
	 	 obj.setSelectedTab("tab2");
	  }else{
		  $('#tabset_pageset').tabs('select', 1);
	  }
  }
  
  function pre()
  {
  	var obj=$('pageset');
  	 if(obj.setSelectedTab){
	 	 obj.setSelectedTab("tab1");
	  }else{
		  $('#tabset_pageset').tabs('select', 0);
	  }
  }
  
  
  </script>
  <body>
   <html:form action="/performance/achivement/achivementTask">
<%if("hl".equals(hcmflag)){ %>
<br>
<%} %>

   <hrms:tabset name="pageset" width="730" height="400" type="false"> 
	
	<hrms:tab name="tab1" label="label.gz.cond" visible="true">
	 <table width="100%"  height='100%' align="center"> 
	 	<tr> <td class="framestyle" valign="top"><Br><br>
		    <fieldset align="center" style="width:90%;">
    							 <br>
		                      			<table width="100%"  border="0" cellpmoding="0" cellspacing="0"  class="DetailTable"  cellpadding="0">                   	
		                      				
			                					<% 
			                					String cycle_value="";
			                					
			                					LazyDynaBean abean=null;
			                					for(int i=0;i<targetColumnList.size();i++)
			                					{
			                						abean=(LazyDynaBean)targetColumnList.get(i);
			                						String flag=(String)abean.get("flag");
			                						String itemid=(String)abean.get("itemid");
			                						String type=(String)abean.get("type");
			                						String value=(String)abean.get("value");
			                						String desc=(String)abean.get("desc");
			                						if(flag.equals("1"))
			                						{
			                						 
			                						  if(itemid.equals("cycle"))
			                						  {
			                						     out.println("<tr><td align='right' height='40' >"+desc+":</td>");
			                						     out.println("<td>");
			                						  	 cycle_value=value;
			                						  	 out.print("<select  name='targetColumnList["+i+"].value' ");
			                						  	 if(opt.equals("edit"))
			                						  			out.print(" disabled ");
			                						  	 out.print(" >");
			                						  	 	out.print("<option "+(value.equals("0")?"selected":"")+" value='0'>"+ResourceFactory.getProperty("jx.khplan.yeardu")+"</option>");
			                						  	 	out.print("<option "+(value.equals("1")?"selected":"")+" value='1'>"+ResourceFactory.getProperty("jx.khplan.halfyear")+"</option>");
			                						  	 	out.print("<option "+(value.equals("2")?"selected":"")+" value='2'>"+ResourceFactory.getProperty("jx.khplan.quarter")+"</option>");
			                						  	 	out.print("<option "+(value.equals("3")?"selected":"")+" value='3'>"+ResourceFactory.getProperty("jx.khplan.monthdu")+"</option>");
			                						  	 out.print("</select>");
			                						  }
			                						  else if(itemid.equalsIgnoreCase("object_type"))
			                						  {
			                						  	    out.println("<tr  ><td align='right' height='30' >"+desc+":</td>");
			                						        out.println("<td>");
			                						  		out.println("<select ");
			                						  		if(opt.equals("edit"))
			                						  			out.print(" disabled ");
			                						  		out.print(" name='targetColumnList["+i+"].value'>");
			                						  		out.print("<option "+(value.equals("1")?"selected":"")+" value='1'>"+ResourceFactory.getProperty("jx.khplan.team")+"</option>");
			                						  		out.print("<option "+(value.equals("2")?"selected":"")+" value='2'>"+ResourceFactory.getProperty("kjg.title.personnel")+"</option>");
			                						  		out.print("</select>");
			                						  
			                						  } 
			                						  else
				                					  {
				                					 	 out.println("<tr><td align='right' height='30' >"+desc+":</td>");
			                						     out.println("<td>");
				                					  	 out.print("<input type='text' name='targetColumnList["+i+"].value'  ");
				                					  	 if(itemid.equalsIgnoreCase("theyear")&&opt.equals("edit"))
				                					  	   out.print(" disabled ");
				                					  	 out.print("   value='"+value+"'   /> ");
			                						  }
			                						  out.println("</td></tr>");
			                						}
			                					}
			                					%>
			                					
			                				
			                			</table><br>
		     </fieldset>
		     
		     
		     <table width="80%"  align="center" >
				<tr><td align='center' >
					 <Input type='button' value='<bean:message key="static.next"/>'  class="mybutton"  onclick='next()'  />
					 <Input type='button' value='<bean:message key="kq.register.kqduration.cancel"/>'  class="mybutton"  onclick='closeWin()'  />
				</td></tr>
		  
		  	</table>
		   
		</td></tr>
	 </table>
	</hrms:tab>
	
	<hrms:tab name="tab2" label="指标" visible="true">
	 <table width="100%"  height='100%' align="center"> 
	 	<tr> <td class="framestyle" valign="top">
	 	
	 		<table width='90%' height='100%'   >
	 			<tr> <td rowspan='2' >&nbsp;</td> <td valign='top' colspan='3' > 
	 	 			<bean:message key="kh.field.class"/> :	<select name='classid'  onchange='selectPoint(this)' style="width:225px;font-size:9pt"  >
	 	 						<option value=''></option>
	 	 						<%
	 	 							for(int i=0;i<pointClassList.size();i++)
	 	 							{
	 	 								CommonData d=(CommonData)pointClassList.get(i);
	 	 								out.println("<option value='"+d.getDataValue()+"' >"+d.getDataName()+"</option>");
	 	 							}
	 	 						 %>
	 	 					</select> 
	 	 		  </td></tr>
	 	 		  <tr><td>
 	 		  			<!-- ie下选择框没有边框的问题  haosl update 2018-1-31  -->
						<div style='width:300px;height:273px;border:1px solid #A0A0A0;overflow:hidden'>
		 	 			<select name="left_fields" multiple="multiple" ondblclick="tagetRepeat();" style="margin-top:-3px;margin-left:-3px;height:280px;width:304px;overflow-x:auto;font-size:9pt">
	                   </select>
	                   </div>
		 	 	  </td>
		 	 	  <td width="8%" align="center">  
		            <html:button  styleClass="mybutton" property="b_addfield" onclick="tagetRepeat();">
	            		     <bean:message key="button.setfield.addfield"/> 
		            </html:button >
		            <br>
		            <br>
		            <html:button  styleClass="mybutton" property="b_delfield" onclick="additem('right_fields','left_fields');removeitem('right_fields');">
	            		     <bean:message key="button.setfield.delfield"/>    
		            </html:button >	
                </td>     
	 	 		<td  align="left">
     	          <div style='width:300px;height:273px;border:1px solid #A0A0A0;overflow:hidden'>
 		    	  <select name="right_fields" multiple="multiple" size="10"  ondblclick="additem('right_fields','left_fields');removeitem('right_fields');" style=";margin-top:-3px;margin-left:-3px;height:280px;width:304px;overflow-x:auto;font-size:9pt">
                  <%
                  	for(int i=0;i<selectedPointList.size();i++)
                  	{
                  		CommonData d=(CommonData)selectedPointList.get(i);
	 	 				out.println("<option value='"+d.getDataValue()+"' >&nbsp;"+d.getDataName()+"</option>");
                  	}
                   %>
                  </select>       
                  </div>
                </td>
	 	 		</tr>
	 	 		<tr><td colspan='4' align='center' >   
	 	 				<Input type='button' value='<bean:message key="static.back"/>'  class="mybutton"  onclick='pre()'  />
						<Input type='button' value='<bean:message key="kq.register.kqduration.ok"/>'  class="mybutton"  onclick='sub()'  />
	 	 		
	 	 		  </td></tr>
	 	 		</table>
	 	</td></tr>
	 </table>
	 </hrms:tab>
	
	
	
	
	</hrms:tabset>
	
	
 	 <% 
			                					LazyDynaBean aabean=null;
			                					for(int i=0;i<targetColumnList.size();i++)
			                					{
			                						aabean=(LazyDynaBean)targetColumnList.get(i);
			                						String flag=(String)aabean.get("flag");
			                						String itemid=(String)aabean.get("itemid");
			                						String type=(String)aabean.get("type");
			                						String value=(String)aabean.get("value");
			                						String desc=(String)aabean.get("desc");
			                						if(flag.equals("0"))
			                						{
			                						  out.println("<input type='hidden' name='targetColumnList["+i+"].value'    value='"+value+"'   />");
			                						}
			                					}
	%>
  
  </html:form>
   <script type="text/javascript">
 	var aa=document.getElementsByTagName("input");
 	for(var i=0;i<aa.length;i++){
 		if(aa[i].type=="text"){
 			aa[i].className="inputtext";
 		}
 	}
 </script> 
  </body>
</html>
