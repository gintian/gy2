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
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.valueobject.UserView,
				 com.hjsj.hrms.businessobject.sys.SysParamBo" %>
 
<html>
  <head>
   <%
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 	String themes = "";
 	if(userView!=null)
 		themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList objectCycleList=(ArrayList)achievementTaskForm.getObjectCycleList();	
	ArrayList objectPointList=(ArrayList)achievementTaskForm.getObjectPointList();	
	String codeitemdescs=(String)achievementTaskForm.getCodeitemdesc();
	int zbnumber=achievementTaskForm.getZbnumber();
	RecordVo  perTargetVo=(RecordVo)achievementTaskForm.getPerTargetVo();
	
	String pointDesc="";
	for(int j=0;j<objectPointList.size();j++){
			 			CommonData d=(CommonData)objectPointList.get(j);
			 			pointDesc+=","+d.getDataValue();
	}
	if(pointDesc.length()>0)
		pointDesc=pointDesc.substring(1);	
 %>		
  </head>
<style>
	.TableRow_self 
 	{
		background-position : center left;
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
	#tbl-container 
 	{			 
		BORDER-BOTTOM: #94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid;
 	}
</style>
 <hrms:themes />
 <style>
 	.TEXT_NB 
 	{ 
		BACKGROUND-COLOR:transparent;
		/*BORDER-BOTTOM: #94B6E6 1pt solid; */
		BORDER-LEFT: medium none !important; 
		BORDER-RIGHT: medium none !important; 
		BORDER-TOP: medium none !important;	
 	}
</style>
<script LANGUAGE=javascript src="/js/function.js"></script> 
<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
<script language="JavaScript"src="../../../js/showModalDialog.js"></script> 
<script language='javascript' >
	Ext.Loader.setConfig({
	    enabled: true,
	    paths: {
	        'SYSF':'/components/fileupload'
	    }
	});
	Ext.require('SYSF.FileUpLoad');
	var themes = "<%=themes %>";
	 var bgColor="";
	 var borderColors="";
	 if(themes=="default"){
	     Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{color:#414141 !important;}");
	 }
	 if(themes=="default" || themes=="gray"){
	     bgColor="#F9F9F9";
	     borderColors = "#C5C5C5";
	 }else if(themes=="green"){
	     bgColor="#E1FFBF";
	     borderColors = "#56B127";
	 }
	 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
	 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none;background:none;}","");
	 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none;background:none;}","");
	 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
	 Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
	 Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tr{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-frame-bl{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-frame-tl{background-image:none !important;}","");
	 Ext.util.CSS.createStyleSheet("#toolbars .x-frame-br{background-image:none !important;}","");
	Ext.onReady(function(){
	   var menu1 = new Ext.menu.Menu({
	       allowOtherMenus: false,
	       items: [
	           new Ext.menu.Item({
	               text: "新建",
	               icon:"/images/prop_ps.gif",
	               handler: function(){
	            	   newBigTaskTarget('2');
	               }
	           }),
	           new Ext.menu.Item({
	               text: "删除",
	               icon:"/images/del.gif",
	               handler:function(){
	                  delBigTarget('${achievementTaskForm.target_id}');
	               } 
	           }),
	           new Ext.menu.Item({
	               text: "修改",
	               icon:'/images/edit.gif',
	               handler:function(){
	                   editBigTarget('${achievementTaskForm.target_id}','2');
	               }
	           }),
	           new Ext.menu.Item({
	               text: "下载模板",
	               icon:'/images/export.gif',
	               handler: function(){
	            	   downloadTemp('${achievementTaskForm.target_id}');
	                   
	               }
	           }),
	           new Ext.menu.Item({
	               text: "导入数据",
	               icon:'/images/import.gif',
	               handler: function(){
	            	   importdata('${achievementTaskForm.target_id}');
	               }
	           })
	       ]
	   }); 
	   var menu2 = new Ext.menu.Menu({
	       allowOtherMenus: false,
	       items: [
	           new Ext.menu.Item({
	               text: "分配任务",
	               icon:"/images/add_del.gif",
	               handler: function(){
	            	   allocateBigTask('${achievementTaskForm.target_id}');
	               }
	           }),
	           new Ext.menu.Item({
	               text: "撤销任务",
	               icon:"/images/add_del.gif",
	               handler: function(){
	            	   delBigTask();
	               }
	           }),
	           new Ext.menu.Item({
	               text: "指标排序",
	               icon:'/images/sort.gif',
	               handler: function(){
	            	   setBigPointSort('${achievementTaskForm.target_id}');
	               }
	           }),
	           new Ext.menu.Item({
	               text: "批量修改",
	               icon:'/images/edit.gif',
	               handler: function(){
	            	   batchBigupdate('${achievementTaskForm.target_id}');
	               }
	           }),
	           new Ext.menu.Item({
	               text: "查询",
	               icon:'/images/view.gif',
	               handler: function(){
	            	   searchPersonnel();
	               }
	           })
	       ]
	   }); 
	   var toolbar = Ext.create("Ext.Toolbar", {
	       renderTo: "toolbars",
	       width: 200,
	       margin:0,
	       padding:0,
	       border:false,
	       items:[{
		        text: "任务书",
		        menu: menu1
		    },{
		        text: "任务分解",
		        menu: menu2
		    }]
	   });
	});
  var pointDescs="<%=pointDesc%>";
  var sql_whl2="${achievementTaskForm.sql_whl2}";
  //人员查询
  function searchPersonnel(cycle,theyear)	
  {
  		var theurl="/performance/achivement/achivementTask.do?br_searchPersonnel=show`cycle=<%=(perTargetVo.getInt("cycle"))%>`theyear=<%=(perTargetVo.getString("theyear"))%>";
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		var config = {
	      	width:653,
	      	height:462,
	      	title:'人员查询',
	      	id:'searchPersonnelWin'
    	}
   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
 	   		if(retvo!=null&&retvo!='undefined'&&retvo.length>0)
 	  		{
 	  			document.achievementTaskForm.sql_whl.value=retvo;
 	  			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query1&paramd=1&target_id=${achievementTaskForm.target_id}";
 		  		document.achievementTaskForm.submit();
 	  		}
   		});
  }
 
  function searchPersonnel_ok(retvo){
  		if(retvo!=null&&retvo!='undefined'&&retvo.length>0)
 		{
 			document.achievementTaskForm.sql_whl.value=retvo;
 			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_query=query1&paramd=1&target_id=${achievementTaskForm.target_id}";
	  		document.achievementTaskForm.submit();
 		}
  }
  var down_height=Ext.getBody().getViewSize().height-125;
</script>

<body>
<html:form action="/performance/achivement/achivementTask">	

<table width='100%'>
		<tr><td>
			<div id="toolbars"/>
		</td></tr>
	<tr><td>
    <input type="button" class='button' id="clo" onclick="newBigTaskTarget('2')"  allowPushDown="false" down="false" value='<bean:message key="menu.gz.new"/>'></input>  
    <input type="button" class='button' id="clo" onclick="saveBigData()"  allowPushDown="false" down="false" value='<bean:message key="options.save"/>'></input> 
	<input type="button" class='button' id="cl1" onclick='allocateBigTask("${achievementTaskForm.target_id}")'   allowPushDown="false" down="false" value='<bean:message key="per.achivement.assignTask"/>'></input>
	<input type="button" class='button' id="clo" onclick="delBigTask()"  allowPushDown="false" down="false" value='<bean:message key="per.achivement.removeTask"/>'></button>
	<input type="button" class='button' id="clo" onclick="returnBigData('${achievementTaskForm.target_id}')"  allowPushDown="false" down="false" value='<bean:message key="button.return"/>'></input>
	&nbsp;&nbsp;&nbsp;&nbsp;
	<bean:message key="jx.khplan.cycle"/>: <html:select name="achievementTaskForm"  onchange="changeBigCycle('${achievementTaskForm.target_id}')"  styleId="cycle" property="cycle" size="1"  >
  					 <html:optionsCollection property="cycleList" value="dataValue" label="dataName"/>
				</html:select> 		
	</td></tr>
	<tr><td>
	考核对象：<%= codeitemdescs %>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;指标数量：<%= zbnumber %>
	
	</td></tr>
	<tr><td width='100%'   >		
		<script language='javascript' >
				document.write("<div id=\"tbl-container\"  style='position:absolute;overflow:auto;height:"+down_height+"px;width:98%;border-top:0px;'  >");
		</script>		
	
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
		 <thead>
	        <tr style="position:relative;top:expression(this.offsetParent.scrollTop-1);">   
	        	<td align="center" style='color:black' class="TableRow_right common_background_color common_border_color" nowrap>
	        		<bean:message key="kh.field.field_n"/>\<bean:message key="jx.khplan.khqujian"/>
	        	</td> 			
				 <%
				
				  for(int i=0;i<objectCycleList.size();i++){
		       		LazyDynaBean abean=(LazyDynaBean)objectCycleList.get(i);
		       		String str=((String)abean.get("cycle_str"));			  
				 	out.print(" <td align='center'  style='color:black'    class='TableRow_left common_background_color common_border_color' >"+str+"</td>");
				 }			 
				 int n=0;
				 %>
			</tr>
		 </thead>
		 	 	 
		 <%
			 for(int j=0;j<objectPointList.size();j++){
				CommonData d=(CommonData)objectPointList.get(j);
				if(n%2==0)
			    {  
			    	out.println("<tr class='trShallow'>");   
			    }else{
			    	out.println("<tr class='trDeep'>");
				}		
				out.println("<td align='left' class='RecordRow_right' nowrap>&nbsp;");
		 		out.print(d.getDataName());
		  		out.print("</td>");
				
				for(int i=0;i<objectCycleList.size();i++){			 	
		 			out.println("<td align='center' class='RecordRow_left' nowrap>");
		 			LazyDynaBean abean=(LazyDynaBean)objectCycleList.get(i);
		 			String index=(String)abean.get("index");		 					
		 			
			 		out.print("<input type='text' value='"+(String)abean.get(d.getDataValue())+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);' onblur='checkValue(this)' name='objectCycleList["+index+"]."+d.getDataValue()+"' class='inputtext TEXT_NB' size='8'/>");
		 			out.print("</td>");			
				}
		  		out.print("</tr>");
		 	}   			        	 		           
		 %>	 
	</table>
	
</div>
	 
</td></tr>	        
</table>

	<Input type='hidden' name='sql_whl'  value='' />
    <input type='hidden' name='selectedIds'  value='' />

</html:form>
</body>
<script>
<% if(request.getParameter("b_saveBigTarget")!=null){%>
	
//	alert('保存成功');
//	goback();		
	
<%}%>
</script>
</html>