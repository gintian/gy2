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
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<html>

<head> 

</head>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achivementTask.js"></script> 
<script language="JavaScript"src="../../../js/showModalDialog.js"></script> 

<style>

div#tbl_container 
{
	background-color: #FFFFFF;
	BORDER-BOTTOM:#94B6E6 0pt inset; 
	BORDER-COLLAPSE: collapse;
	BORDER-LEFT: #94B6E6 0pt inset; 
	BORDER-RIGHT: #94B6E6 0pt inset; 
	BORDER-TOP: #94B6E6 0pt inset; 
	width: 220px;
	height: 280px;
	overflow: auto;
}

</style>

<script language='javascript' >
   		 		
   		<%  
   		// 绩效所有模块都改为：超级用户也按操作单位优先的规则限制可操作范围    JinChunhai 2011.05.11
   		
   		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   		String operOrg =userView.getUnitIdByBusi("5"); // 操作单位 5: 绩效管理 6：培训管理 7：招聘管理
   		String b_suballocate = request.getParameter("b_suballocate");
   		%>
   		if("sub"=="<%=b_suballocate%>")
   		{
   			if(window.showModalDialog){//haosl 20170224 兼容非IE浏览器
	  			parent.window.returnValue = "1";
	  		}else{
	  		    if(window.allocateBigTask != "true")
	  		    	window.opener.allocateTask_callBack('1',${achievementTaskForm.target_id});
	  		    else
	  		        window.opener.allocateBigTask_callBack('1',${achievementTaskForm.target_id});
	  		    
	  		}
   			parent.window.close();
	  }
</script>
<body>
   <html:form action="/performance/achivement/achivementTask">
   		<table style="width:95%;">
   		<tr><td rowspan='2' >&nbsp;&nbsp;&nbsp;</td>
   			<td>
   				<fieldset align="center">
    			 	<legend ><bean:message key="jx.eval.khobj1"/></legend>
		            	<div align="left" id="tbl_container"  >
				        	<% if(operOrg.length() > 2){ %>
				              	<hrms:orgtree flag="${achievementTaskForm.tree_flag}" dbpre='Usr' viewunit="1" nmodule="5" loadtype="${achievementTaskForm.tree_loadtype}"  showroot="false"  selecttype='1' dbtype="0" priv="1" />
				              			
				            <% }else{ %>	
				              	<hrms:orgtree flag="${achievementTaskForm.tree_flag}" dbpre='Usr' loadtype="${achievementTaskForm.tree_loadtype}"  showroot="false"  selecttype='1' dbtype="0" priv="1" />
				           	<% } %>
				        </div>		                      			 		                      			
   				</fieldset>	  			
   			</td>
   			<td align="center">
   				 <html:button style='margin-right:0px;' styleClass="mybutton" property="b_addfield"  onclick="addObject('${achievementTaskForm.tree_flag}')"  >
            		   <bean:message key="button.setfield.addfield"/>
	            </html:button>
	            <br>
	            <br>
	           <html:button style='margin-right:0px;' styleClass="mybutton" property="b_delfield" onclick="delObject()"  >
            		   <bean:message key="button.setfield.delfield"/>
	            </html:button>	

   			</td>
   			<td align="center">
   			<fieldset align="center" style="width:100%;">
    							 <legend ><bean:message key="jx.eval.khobj2"/></legend>
   				<select name="right_fields" multiple="multiple" size="10"   style="height:280px;width:215px;font-size:9pt">
               </select>
   			</fieldset>
   			
   			</td>
   		</tr>
   		<tr><td colspan='3' style="height:35px">
   		<bean:message key="jx.khplan.cycle"/>: <html:select name="achievementTaskForm" styleId="cycle" property="cycle" size="1"  >
  					 <html:optionsCollection property="cycleList" value="dataValue" label="dataName"/>
				</html:select> 
   		</td></tr>
   		<tr><td align='center' colspan='4'>
   		<logic:equal  name="achievementTaskForm"  property="object_type"   value="2">
   			<Input type='button' value='<bean:message key="button.sys.cond"/>'  class="mybutton"  onclick='conditionselect()'  />
   		</logic:equal>
   			<Input type='button' value='<bean:message key="reporttypelist.confirm"/>'  class="mybutton"  onclick='subAllocate()'  />

			<Input type='button' value='<bean:message key="kq.register.kqduration.cancel"/>'  class="mybutton"  onclick='javascript:parent.window.close()'  />
   		</td></tr>   		
   	
   		</table>
   		
   </html:form>
  </body>
</html>
