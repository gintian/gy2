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
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.frame.dao.RecordVo,
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<html>
  <head>
	<script LANGUAGE=javascript src="/js/function.js"></script>
	<script language="JavaScript"src="../../../js/showModalDialog.js"></script> 
 	<script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
 	<script language="JavaScript"src="../../../module/utils/js/template.js"></script>
  <%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  	String themes = "";
  	if(userView!=null)
  		themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList targetDataList=(ArrayList)achievementTaskForm.getTargetDataList();
	ArrayList selectedPointList=(ArrayList)achievementTaskForm.getSelectedPointList();
	RecordVo  perTargetVo=(RecordVo)achievementTaskForm.getPerTargetVo();
	String pointDesc="";
	for(int j=0;j<selectedPointList.size();j++){
			 			CommonData d=(CommonData)selectedPointList.get(j);
			 			pointDesc+=","+d.getDataValue();
	}
	if(pointDesc.length()>0)
		pointDesc=pointDesc.substring(1);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}
 %>
</head>
 <style>
 .TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
}
 /*
.TableRow_self {
   background-image:url(/images/tableHeader5x.jpg);
	background-repeat:repeat;
	background-position : center left;
	BACKGROUND-COLOR: #94B6E6; 
	font-size: 12px;  
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	height:25;
	font-weight: bold;	
	valign:middle;
}*/
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
 <script type="text/javascript">
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
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mc{height:23px !important;border:1px solid "+borderColors+";background-color:"+bgColor+" !important;}","menu_ms_bg");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-ml{display:none;background:none !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-mr{display:none;background:none !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small{border-color:"+borderColors+" !important;background-color:"+bgColor+" !important;}","menu1");
 Ext.util.CSS.createStyleSheet(".x-btn-wrap-default-toolbar-small{background-color:"+bgColor+" !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-inner-default-toolbar-small{padding:2px 4px !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-bc{height:0px !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-tc{height:0px !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-br{padding-right:0px !important;}","");
 Ext.util.CSS.createStyleSheet(".x-btn-default-toolbar-small-tr{padding-right:0px !important;}","");
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
	                    newTarget('1');
	                }
	            }),
	            new Ext.menu.Item({
	                text: "删除",
	                icon:"/images/del.gif",
	                handler:function(){
	                   delTarget('${achievementTaskForm.target_id}');
	                } 
	            }),
	            new Ext.menu.Item({
	                text: "修改",
	                icon:'/images/edit.gif',
	                handler:function(){
	                    editTarget('${achievementTaskForm.target_id}','1')//
	                }
	            }),
	            new Ext.menu.Item({
	                text: "下载模板",
	                icon:'/images/export.gif',
	                handler: function(){
	                    downloadTemp('${achievementTaskForm.target_id}')//
	                }
	            }),
	            new Ext.menu.Item({
	                text: "导入数据",
	                icon:'/images/import.gif',
	                handler: function(){
	                    importdata('${achievementTaskForm.target_id}')//
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
	                    allocateTask('${achievementTaskForm.target_id}')//
	                }
	            }),
	            new Ext.menu.Item({
	                text: "撤销任务",
	                icon:"/images/add_del.gif",
	                handler: function(){
	                    delTask()
	                }
	            }),
	            new Ext.menu.Item({
	                text: "指标排序",
	                icon:'/images/sort.gif',
	                handler: function(){
	                    setPointSort('${achievementTaskForm.target_id}')//
	                }
	            }),
	            new Ext.menu.Item({
	                text: "批量修改",
	                icon:'/images/edit.gif',
	                handler: function(){
	                    batch_update('${achievementTaskForm.target_id}')//
	                }
	            }),
	            new Ext.menu.Item({
	                text: "查询",
	                icon:'/images/view.gif',
	                handler: function(){
	                    searchPersonnel()
	                }
	            })
	        ]
	    }); 
	    var toolbar = Ext.create("Ext.Toolbar", {
	        renderTo: "toolbars",
	        width: 200,
	        margin:'0 -2 -4 -1',
	        border:false,
	        items:[{
		        text: "任务书",
		        menu: menu1,
		        style:'color:#c5c5c5',
		        height:24
		    },{
		        text: "任务分解",
		        menu: menu2,
		        style:'color:#c5c5c5',
		        height:24
		    }]
	    });
	});
</script>
  <script language='javascript' >
  var pointDescs="<%=pointDesc%>";
  var sql_whl2="${achievementTaskForm.sql_whl2}";
  //人员查询
  function searchPersonnel(cycle,theyear)	
  {
  		var theurl="/performance/achivement/achivementTask.do?br_searchPersonnel=show`";
  		<%if(perTargetVo!= null){%>
  			theurl+="cycle=<%=(perTargetVo.getInt("cycle"))%>`theyear=<%=(perTargetVo.getString("theyear"))%>";
		<%}%>
    	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theurl);
  		var config = {
  		      	width:550,
  		      	height:480,
  		      	title:'人员查询',
  		      	id:'searchPersonnelWin'
  	    	}
  	   		modalDialog.showModalDialogs(iframe_url,'template_win',config,function(retvo){
	  	   		if(retvo!=null&&retvo!='undefined'&&retvo.length>0)
	  	  		{
	  	  			document.achievementTaskForm.sql_whl.value=retvo;
	  	  			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&target_id=${achievementTaskForm.target_id}";
	  		  		document.achievementTaskForm.submit();
	  	  		}
  	   		});
  }
  function searchPersonnel_ok(retvo){
	  if(retvo!=null&&retvo!='undefined'&&retvo.length>0)
  		{
  			document.achievementTaskForm.sql_whl.value=retvo;
  			document.achievementTaskForm.action="/performance/achivement/achivementTask.do?b_search=query1&target_id=${achievementTaskForm.target_id}";
	  		document.achievementTaskForm.submit();
  		}
  }
  var down_height=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-100+"px";
  var down_height1=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-121+"px";
  </script>
  <style>
	div#tbl-container {
	    
		BORDER-BOTTOM:#94B6E6 1pt solid; 
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid; 
		
	}

</style>
<hrms:themes />
  <body>
  <html:form action="/performance/achivement/achivementTask">
 <%if("hl".equals(hcmflag)){ %>
  <table width='100%'>
<%}else{ %>
  <table width='100%' style="margin-top:-5px">
<%} %> 
  
	<tr><td>
		<div id="toolbars"/>
	</td></tr>

	<tr><td>
    <input type='button' class='button' id="clo" onclick="newTarget('1')" value='<bean:message key="menu.gz.new"/>'></input>
    <input type='button' class='button' id="clo" onclick="saveData()" value='<bean:message key="options.save"/>'></input>
	<input type='button' class='button' id="cl1" onclick='allocateTask("${achievementTaskForm.target_id}")' value='<bean:message key="per.achivement.assignTask"/>'></input>
	<input type='button' class='button' id="clo" onclick="delTask()" value='<bean:message key="per.achivement.removeTask"/>'></input>
	
	&nbsp;&nbsp;&nbsp;&nbsp;
	<bean:message key="jx.khplan.cycle"/>: <html:select name="achievementTaskForm"  onchange="changeCycle('${achievementTaskForm.target_id}')"  styleId="cycle" property="cycle" size="1"  >
  					 <html:optionsCollection property="cycleList" value="dataValue" label="dataName"/>
				</html:select> 
	
	
	</td></tr>
	<tr><td width='100%'   >
	<script language='javascript' >
			document.write("<div id=\"tbl-container\"  style='overflow:auto;height:<%if("hl".equals(hcmflag)){ %>"+down_height+"<%}else{ %>"+down_height1+"<%} %>;width:100%'  >");
	</script>
	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0"   style="margin-top:-1px"  class="ListTable">
	<thead>
        <tr>
        	 <td align="center"  width='3%' class="TableRow_right common_background_color common_border_color" nowrap>
        	 &nbsp;<input type="checkbox" name="selbox" onclick="batch_select(this,'ids');" >&nbsp;
        	 </td>
			 <%
			 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");
			  	if(perTargetVo.getString("object_type").equals("2"))
			  	{
				 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("b0110.label")+"</td>");
			 	 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+fielditem.getItemdesc()+"</td>");
			 	 	out.print(" <td align='center' style='color:black'   class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("hire.employActualize.name")+"</td>");
			 	}
			 	else
			  	{
			 	 	out.print(" <td align='center' style='color:black'    class='TableRow_self common_border_color' >"+ResourceFactory.getProperty("label.query.unit")+"/"+fielditem.getItemdesc()+"</td>");
			  	}
			  %>
			
			 <td align="center" style='color:black'    class="TableRow_self common_border_color" nowrap><bean:message key="jx.khplan.khqujian"/></td>
			
			
			
			 <%
			
			  for(int i=0;i<selectedPointList.size();i++){
			 		CommonData d=(CommonData)selectedPointList.get(i);
			 		out.print(" <td align='center' style='color:black'    class='TableRow_self common_border_color' >"+d.getDataName()+"</td>");
			 	}
			 
			 int n=0;
			  %>
		</tr>
	 </thead>
 
	  <hrms:extenditerate id="element" name="achievementTaskForm" property="targetDataListform.list" indexes="indexes"  pagination="targetDataListform.pagination" pageCount="50" scope="session">
	    <%
		          if(n%2==0)
		          {
		          %>
		          <tr class="trShallow"  onClick="javascript:tr_onclick(this,'#F3F5FC')"  >
		          <%}
		          else
		          {%>
		          <tr class="trDeep"  onClick="javascript:tr_onclick(this,'#E4F2FC')"   >
		          <%
		          }
		          n++;          
		          %>  
		          <td align="center" class="RecordRow_right " nowrap>
	               <input  type='checkbox' name='ids'  value='<bean:write name="element" property="object_id" filter="true"/>/<bean:write name="element" property="kh_cyle" filter="true"/>'  />
		          </td>
		          <%
		           if(perTargetVo.getString("object_type").equals("2")){
		           %>
		           <td align='left' width='80px' class='RecordRow' nowrap>&nbsp;<bean:write name="element" property="b0110" filter="true"/></td>
		           <td align='left' width='80px' class='RecordRow' nowrap>&nbsp;<bean:write name="element" property="e0122" filter="true"/></td>
		          <% 
		           }
		           %>
		           <td align='left' width='130px' class='RecordRow' nowrap>
		           &nbsp;		           
		           <a href="javascript:editBig('<bean:write name="element" property="object_id" filter="true"/>','<bean:write name="element" property="target_id" filter="true"/>','<bean:write name="element" property="nbase" filter="true"/>','<bean:write name="element" property="object_type" filter="true"/>');">
						<bean:write name="element" property="a0101" filter="true"/>
		           </a>
		           </td>
		           <td align='left' width='100px' class='RecordRow' nowrap>&nbsp;<bean:write name="element" property="cycle_str" filter="true"/></td>
		           <%
		           	 for(int j=0;j<selectedPointList.size();j++){
			 			CommonData d=(CommonData)selectedPointList.get(j);
	 					out.println("<td  width='100px' align='center' class='RecordRow' nowrap>");
	 					LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
	 					String index=(String)abean.get("index");
	 					
	 					String _value=(String)abean.get(d.getDataValue());
	 					if(_value.equals("no"))
	 					{
	 						out.print("<hr style='color:black size:1px' width='20'/>");
	 						out.print("<input type='hidden' value='"+(String)abean.get(d.getDataValue())+"' name='targetDataList["+index+"]."+d.getDataValue()+"'/>");
	 					}
	 					else
		 					out.print("<input type='text' value='"+(String)abean.get(d.getDataValue())+"' onkeydown='if (event.keyCode==37) go_left(this);if (event.keyCode==39) go_right(this);if (event.keyCode==38) go_up(this);if (event.keyCode==40) go_down(this);'        onblur='checkValue(this)'  name='targetDataList["+index+"]."+d.getDataValue()+"'  class='TEXT_NB' size='8'  />");
	 					out.print("</td>");
	 				}
		           
		            %>
	 			 </tr>
	  </hrms:extenditerate>
	  </table>
	</div>
	
	<table  width='100%'  class='RecordRowP'  align='center'>
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="achievementTaskForm" property="targetDataListform.pagination.current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="achievementTaskForm" property="targetDataListform.pagination.count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="achievementTaskForm" property="targetDataListform.pagination.pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	        <td valign="bottom" nowrap class="tdFontcolor" style="text-align:right">
					<hrms:paginationlink name="achievementTaskForm" property="targetDataListform.pagination" nameId="targetDataListform">
					</hrms:paginationlink>
			</td>
		</tr>
   </table>
	
	
	
	
	</td></tr>
	
</table>
  
  <Input type='hidden' name='sql_whl'  value='' />
  <input type='hidden' name='selectedIds'  value='' />
 
 
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
