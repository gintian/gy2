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
				 com.hrms.struts.constant.WebConstant,
				 com.hjsj.hrms.businessobject.sys.SysParamBo" %>
<html>
  <head>
   <%
   	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 	String themes = "";
 	if(userView!=null)
  		themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	AchievementTaskForm achievementTaskForm=(AchievementTaskForm)session.getAttribute("achievementTaskForm");
	ArrayList targetDataList=(ArrayList)achievementTaskForm.getTargetDataList();
	ArrayList selectedPointList=(ArrayList)achievementTaskForm.getSelectedPointList();
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
 
 </style>
 <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
 <script LANGUAGE=javascript src="/js/function.js"></script> 
 <script LANGUAGE=javascript src="/performance/achivement/achivementTask/achievement.js"></script> 
 <script language="JavaScript"src="../../../js/showModalDialog.js"></script> 
 <script language="JavaScript"src="../../../module/utils/js/template.js"></script>
  <script language='javascript' >
 
  var down_height=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-100+"px";
  var down_height1=(document.body.clientHeight==0?document.documentElement.clientHeight:document.body.clientHeight)-121+"px";
 var themes = "<%=themes %>";
 var bgColor="";
 var borderColors="";
 if(themes=="default"){
	 bgColor="#D7E4F4";
    borderColors = "#549CDA";
 }else if(themes=="gray"){
	 bgColor="#E5E5E4";
 	borderColors = "#BDBDBD";
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
	Ext.util.CSS.createStyleSheet(".x-btn-over .x-btn-default-toolbar-small-br{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-btn-over .x-btn-default-toolbar-small-bl{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-btn-over .x-btn-default-toolbar-small-tr{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-btn-over .x-btn-default-toolbar-small-tl{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-frame-tr{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-frame-bl{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-frame-tl{background-image:none !important;}","");	
	Ext.util.CSS.createStyleSheet(".x-frame-br{background-image:none !important;}","");	
	
Ext.onReady(function(){
    var menu1 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text: "新建",
                icon:"/images/prop_ps.gif",
                handler: function(){
                    newTarget();
                }
            }),
            new Ext.menu.Item({
                text: "删除",
                disabled:true,
                icon:"/images/del.gif",
                handler:function(){
                   delTarget('${achievementTaskForm.target_id}');
                } 
            }),
            new Ext.menu.Item({
                text: "修改",
                disabled:true,
                icon:'/images/edit.gif',
                handler:function(){
                    editTarget('${achievementTaskForm.target_id}','1')//
                }
            })
        ]
    }); 
    var menu2 = new Ext.menu.Menu({
        allowOtherMenus: false,
        items: [
            new Ext.menu.Item({
                text: "分配任务",
                disabled:true,
                icon:"/images/add_del.gif",
                handler: function(){
                    allocateTask('${achievementTaskForm.target_id}')//
                }
            }),
            new Ext.menu.Item({
                text: "撤销任务",
                disabled:true,
                icon:"/images/add_del.gif",
                handler: function(){
                    delTask()
                }
            }),
            new Ext.menu.Item({
                text: "指标排序",
                disabled:true,
                icon:'/images/sort.gif',
                handler: function(){
                    setPointSort('${achievementTaskForm.target_id}')//
                }
            }),
            new Ext.menu.Item({
                text: "批量修改",
                disabled:true,
                icon:'/images/edit.gif',
                handler: function(){
                    batch_update('${achievementTaskForm.target_id}')//
                }
            }),
            new Ext.menu.Item({
                text: "查询",
                disabled:true,
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
 
  </script>
  <style>
	div#tbl-container1 {
	    
		BORDER-BOTTOM:#94B6E6 1pt solid; 
		BORDER-COLLAPSE: collapse;
		BORDER-LEFT: #94B6E6 1pt solid; 
		BORDER-RIGHT: #94B6E6 1pt solid; 
		BORDER-TOP: #94B6E6 1pt solid; 
		width:100%;
		overflow: auto;
	}

</style>
  <body>
  <html:form action="/performance/achivement/achivementTask">
  
  <table width='100%'>
	<tr><td>
		<div id="toolbars"/>
	</td></tr>
	<tr><td>
    <input type='button' class='button' id="clo" onclick="newTarget()" value='<bean:message key="menu.gz.new"/>'></input>
    <input type='button' class='button' id="clo" onclick="saveData()" disabled value='<bean:message key="options.save"/>'></input>
	<input type='button' class='button' id="cl1" disabled onclick='allocateTask("${achievementTaskForm.target_id}")' value='<bean:message key="per.achivement.assignTask"/>'></input>
	<input type='button' class='button' id="clo" disabled onclick="delTask()" value='<bean:message key="per.achivement.removeTask"/>'></input>
	</td></tr>
	<tr><td width='100%'>
	
	
	<script language='javascript' >
			document.write("<div id=\"tbl-container\" class=\"tbl-container1 complex_border_color\" style='position:absolute;left:5;height:<%if("hl".equals(hcmflag)){ %>"+down_height+"<%}else{ %>"+down_height1+"<%} %>;width:98%'  >");
	</script>	
	&nbsp;&nbsp;
	
	</div>
	
	
	
	</td></tr>
	
</table>
  
 
 
  </html:form>
  </body>
</html>
