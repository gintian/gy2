<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="java.util.*" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.achivement.AchievementTaskForm,
				 org.apache.commons.beanutils.LazyDynaBean,
				 com.hrms.struts.taglib.CommonData,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>
<%
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList selectedPointList=(ArrayList)achievementTaskForm.getSelectedPointList();
	RecordVo perTargetVo=(RecordVo)achievementTaskForm.getPerTargetVo();
	String object_type=(String)perTargetVo.getString("object_type");  // 1团队 2人员
%>	

<head>
</head>

<script LANGUAGE=javascript src="/js/function.js"></script> 
<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
    <script language='javascript' >
    function closeWin(){
		if(window.showModalDialog)
			parent.window.close();
		else
			parent.parent.Ext.getCmp("searchPersonnelWin").close();
	}
    </script>
<style>

div#a3 
{	    
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
	width:100%;
	height:320;
	overflow: auto;
}

.RecoRowConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}

.RecoTdConition 
{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	border-collapse:collapse; 
	height:22;
}

.TableRowConition 
{
	background-position : center left;
	font-size: 12px;  
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 0pt solid; 
	/*BORDER-TOP: #C4D8EE 1pt solid;*/
	height:22px;
	font-weight: bold;
	background-color:#f4f7f7;	
	/*
	color:#336699;
	*/
	valign:middle;
}
</style>
 <hrms:themes />
  <body>
  <html:form action="/performance/achivement/achivementTask" onsubmit="return false;">
  <div id='a1' >
  
   <table width='90%'   border="0" cellspacing="0"  id='a_table' align="center" cellpadding="1" class="ListTable ">
	<thead>
        <tr ><td align="left"   class="TableRow" nowrap>&nbsp;<bean:message key="label.gz.condfilter"/></td></tr>
    </thead>
    <tr class='trShallow' >
 		<td class='RecoRowConition common_border_color' align='center' >
			<table width='98%' >
				<tr>
					<td align='left' ><bean:message key="jx.eval.preparePoint"/></td>
					<td>&nbsp;</td>
					<td align='left' ><bean:message key="static.ytarget"/></td>
				</tr>
				<tr>
					<td>
							<!-- ie下选择框没有边框的问题  haosl update 2018-1-31  -->
							<div style='width:220px;height:256px;border:1px solid #A0A0A0;overflow:hidden'>
							<select style='font-size:9pt;width:224px;margin-left:-3px;margin-top:-3px;margin-bottom:-3px;' name='left_fields' ondblclick="additem('left_fields','right_fields');"  multiple="multiple" size="16">
	 	 						<% if(object_type.equals("2")){ %>
	 	 						<option value='a0101'><bean:message key="kq.emp.change.emp.a0101"/></option>
	 	 						<% }else{ %>
	 	 						<option value='a0101'><bean:message key="label.query.unit"/>/<bean:message key="lable.hiremanage.dept_id"/></option>
	 	 						<% } %>
	 	 						<option value='kh_cyle'><bean:message key="jx.khplan.khqujian"/></option>
	 	 						<%
	 	 							for(int i=0;i<selectedPointList.size();i++)
	 	 							{
	 	 								CommonData d=(CommonData)selectedPointList.get(i);
	 	 								out.println("<option value='"+d.getDataValue()+"' >"+d.getDataName()+"</option>");
	 	 							}
	 	 						 %>
	 	 					</select> 
	 	 					</div>
	 	 					<br>&nbsp;
					</td>
					<td align="center" style="padding-left:7px;">
						 <html:button  styleClass="mybutton" property="b_addfield" onclick="additem('left_fields','right_fields');">
	            		     <bean:message key="button.setfield.addfield"/> 
			            </html:button >
			            <br>
			            <br>
			            <html:button  styleClass="mybutton" property="b_delfield" onclick="removeitem('right_fields');">
		            		     <bean:message key="button.setfield.delfield"/>
			            </html:button >	<br>
					</td>
					<td>
						<!-- ie下选择框没有边框的问题  haosl update 2018-1-31  -->
						<div style='width:220px;height:256px;border:1px solid #A0A0A0;overflow:hidden'>
							<select style='font-size:9pt;width:224px;margin-left:-3px;margin-top:-3px;margin-bottom:-3px;' name='right_fields' ondblclick="removeitem('right_fields');"  multiple="multiple" size="16">
	 	 						
	 	 					</select>
						</div>
						 <br>&nbsp;
					</td>
				</tr>
				</table>
   		</td>
   	</tr>
   	<tr class='trShallow' >
 		<td class='RecoRowConition common_border_color' align='center' style="height:35px">
  				<Input type='button' value='<bean:message key="static.next"/>'  class="mybutton"  onclick='next()'  />
  				<Input type='button' value='<bean:message key="kq.register.kqduration.cancel"/>'  class="mybutton"  onclick='closeWin()'  />
   		</td>
   	</tr>
  </table>
   
   </div>
   <div id='a2' style='display:none' >
   
   </div>
  </html:form>
  
  
  <script language='javascript' >
  	var cycle='<%=(request.getParameter("cycle"))%>';
    var theyear='<%=(request.getParameter("theyear"))%>'
    
    	var aa=document.getElementsByTagName("input");
 	for(var i=0;i<aa.length;i++){
 		if(aa[i].type=="text"){
 			aa[i].className="inputtext";
 		}
 	}
  	
  
  </script>
  
  </body>
</html>
