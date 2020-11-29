<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
                com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.module.recruitment.position.actionform.PositionForm"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<script language="JavaScript" src="../../../module/recruitment/position/positionList/position.js"></script>
<script language="JavaScript" src="../../../components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="../../../components/fileupload/FileUpLoad.js"></script>
<script language="JavaScript" src="../../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script language="JavaScript" src="../../../module/recruitment/js/utils.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<link href="../../../module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
<%
String i = "ss";
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  PositionForm positionForm = (PositionForm)session.getAttribute("positionForm");
  ArrayList queryList = positionForm.getQueryList();
  HashMap hm = (HashMap)positionForm.getFormHM().get("requestPamaHM");
  String searchStr = positionForm.getSearchStr();
  int queryListSize = queryList.size()==0?queryList.size():queryList.size()-1;
  request.setAttribute("queryListSize",String.valueOf(queryListSize));
  if(userView.hasTheFunction("3110107"))
  {
	  request.setAttribute("showPublicPlan",true);
  }
  String from = positionForm.getFrom();
  String title = "";
  if(from.length()>0){
	 	title="推荐其他职位";
    }else{
	 	title="招聘职位";
	}
  request.setAttribute("title",title);
  
//默认查询指标
String defaultQuery = (String)positionForm.getFormHM().get("defaultQuery");
//备选查询指标
String optionalQuery = (String)positionForm.getFormHM().get("optionalQuery");
//保存默认查询模板
Object hasTheFunction = positionForm.getFormHM().get("hasTheFunction");
//已发布批次
String batchQuery = (String)positionForm.getFormHM().get("batchQuery");
//审批状态
String appStatus = (String)positionForm.getFormHM().get("appStatus");
%>
<script type="text/javascript">
Global.dataList = [{dataValue:'01',dataName:'起草'},{dataValue:'09',dataName:'暂停'},{dataValue:'04',dataName:'已发布'},{dataValue:'06',dataName:'结束'}];
var pageDesc="${positionForm.pageDesc}";
var a0100s='${positionForm.a0100s}';
var tableObj = undefined;
var from = "${positionForm.from}";
var max_count = "${positionForm.max_count}";
Ext.onReady(function(){
	var changeList = "${positionForm.changeIds}";
	if(changeList.length>0){
		var list = changeList.split(",");
		for(var num =0;num<list.length;num++){
			var name = list[num].substring(0,1);
			var index = list[num].substring(1,2);
			var elem=document.getElementById(list[num]);
			if("A"==name){
				Global.positionPlan[0]=index;
			}else if("B"==name){
				Global.positionPlan[1]=index;
			}else if("C"==name){
				Global.positionPlan[2]=index;
	        }
	        if(elem!=null)
	        {
				elem.style.textDecoration="underline";
		    }
		}
	}
    
	
   <hrms:tableFactory  jsObjName="tableObj" title="${title}" constantName="recruitment/positionList"  formName="positionForm" columnProperty="positionColumn"  orderbyProperty="ordersql"
        subModuleId="zp_position_191130_00001" itemKeyFunctionId="ZP0000002085"  sqlProperty="strsql" currentPage="${positionForm.pageNum}" pagesize="${positionForm.pagesize}" showPublicPlan="${showPublicPlan}"
             isScheme="true"  fieldAnalyse="true"  isColumnFilter="true" schemePosition="title" schemeSaveCallback="Global.pageLode">
         <hrms:buttonTag buttonsProperty="buttonList" />
   </hrms:tableFactory> 
   tableObj.setBorderLayoutRegion("center");
   //查询方案中插入职位
   var obj = {
			type:"A",
			itemid:"z0351",
			itemdesc:"职位",
			codesetid:"0",
			codesource:"",
			ctrltype:"",
			nmodule:"",
			codesetValid:true
		};
   Ext.each(tableObj.toolBar.items.items,function(name){
	   if(name.id=="tableObj_querybox")
   		Ext.Array.splice(name.fieldsArray,0,0,obj);
   });
         //tableObj.renderTo("table1");
  var title = "";
   var height = 20;
   if(from.length>0)
	   height = 0;
  /*if(from.length>0){
 	 title="推荐其他职位";
  }else{
 	 title="招聘职位";
	  
  }*/

   var myPanel = Ext.widget('panel',{
	   height:height,
	   border:false,
	   html:"<div id='topPanel'></div>"
	});
   tableObj.insertItem(myPanel,0);
   <% if(from.length()<=0){%>
   //公共查询控件
   Ext.require("EHR.commonQuery.CommonQuery",function(){
    var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
        subModuleId:'zp_position_191130_00001',
        ctrltype:'3',
        nmodule:'7',
        defaultQueryFields: <%=defaultQuery%>,
        optionalQueryFields: <%=optionalQuery%>,
        beforeFieldRender:function(field){
        	if(field.itemid=='Z0103'){
        		field.codeData=<%=batchQuery%>;
        	}
        	if(field.itemid=='z0319'){
        		field.codeData=<%=appStatus%>;
        	}
        },
         doQuery:function(items){
         	var map = new HashMap();
         	map.put("items", items);
         	map.put("type", "3");
         	map.put("subModuleId", "zp_position_191130_00001");
         	Rpc( {
         		functionId : 'ZP0000002081',
         		success : function(){
         			var store = Ext.data.StoreManager.lookup('tableObj_dataStore');
         			store.load();
         			store.loadPage(1);
         		}
         	}, map);
        	 
         },
         scope:Global,
         fieldPubSetable:<%=hasTheFunction %>
    	});
    tableObj.insertItem(commonQuery,0);
    });
   <%}%>
    Ext.widget('viewport',{
    	layout:'fit',
    	id:'tableObj',
    	padding:"0 5 0 5",
    	style:'backgroundColor:white',
    	items:/*[{
    			  xtype:'panel',title:title,
    			  html:"<div id='topPanel'></div>",
    			  region:'north',height:height,border:false
    			},*/
    		   [tableObj.getMainPanel()]/*]*/
    });   
    function getMenu(z0301){
    
	    var addPos = {text:CREATE_NEW_POSITION,handler:function(){
	    	var hashvo = new HashMap();
			hashvo.put("z0301", z0301);
			//debugger;
			
			Rpc({
				functionId : 'ZP0000002091',
				async : false,
				success : Global.showWin
			}, hashvo);
		}};
    	return addPos;
    }
    tableObj.tablePanel.addListener('cellcontextmenu',
    		function( view, td, cellIndex, record, tr, rowIndex, e){
    	var z0301 = record.data.z0301_e;
    	var contextmenu = view.contextmenu;
    	var codesetid = e.position.column.config.codesetid;
    	if(contextmenu&&codesetid!="0"){
    		if(contextmenu.items.length==2 ){
    			contextmenu.remove(1);
    		}
    		
			<%if(userView.isSuper_admin()||userView.hasTheFunction("3110116")){%>
				contextmenu.add(getMenu(z0301));
			<%}%>
    	}
    	else{
    		<%if(userView.isSuper_admin()||userView.hasTheFunction("3110116")){%>
    			contextmenu = Ext.create('Ext.menu.Menu', {items:getMenu(z0301)});
    			<%}%>
    	}
    	contextmenu.showAt(e.getXY());
    	});
    document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
    document.getElementById('funcDiv').style.display="block";
    
<% if(searchStr!=null && !searchStr.equals("")){ %>
var searchStr = '<%=searchStr %>'; 
var scheme = searchStr.split(",");
if(from == "resumeCenter" || from == "process" || from == "talents"  
		|| from == "process`resumeInfo" || from == "resumeInfo") 
	return;
	
if("0,0,0"==searchStr){
	Global.searchAll();
}else{
	if(scheme[0]!='0'){
    	Global.tosearch(scheme[0],'0','A');
	}
	if(scheme[1]!='0'){
    	Global.tosearch(scheme[1],'1','B');
	}
	if(scheme[2]!='0'){
    	Global.tosearch(scheme[2],'2','C');
	}
}
	
<% } %>
});
</script>
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
.searchicon{border:0px solid;vertical-align:middle;cursor: pointer;width: 22px;height: 22px;}
.img-zoom-pos {zoom:0.9;margin-top:2px;margin-left:3px;margin-right:3px;cursor: pointer;}
.border-width-none {border:0px solid;line-height:22px;letter-spacing:0px;text-align:left;word-spacing:0px}
.x-window-default {padding: 0;border-width: 1px;border-style: solid;background-color: white}
</style>
<body >
	<form id="form1" action="/recruitment/position/position.do">
	<div id="funcDiv" style="display:none;margin-bottom:0px;">
	  
		<logic:equal value="" name="positionForm" property="from">
			<div style="float:left;margin-top:2px;margin-left:3px;width:99%;margin-bottom:0px">
				<div style="float:left;margin-right: -5px;">查询方案：</div>
				
			<logic:iterate id="element" name="positionForm" property="queryList" indexId="indexid">
			
			    <logic:iterate id="elem" name="element" >
					<div style="float:left;margin-left: 10px;">
						<logic:equal value="C1" property="id" name="elem">
							<a id="<bean:write name="elem" property="id" />" name="<bean:write name="elem" property="type" />" style="text-decoration: underline;color:green;" onclick="<bean:write name="elem" property="jsMethod" />" href="javascript:void(0)" >
								<bean:write name="elem" property="name" />
							</a>
						</logic:equal>
						<logic:notEqual value="C1" property="id" name="elem">
							<a id="<bean:write name="elem" property="id" />" name="<bean:write name="elem" property="type" />" onclick="<bean:write name="elem" property="jsMethod" />" href="javascript:void(0)" >
								<bean:write name="elem" property="name" />
							</a>
						</logic:notEqual>
					</div>
				</logic:iterate>
				<logic:notEqual  name="indexid"  value="${queryListSize}">
				    <div name="rule" style="float: left;margin-left: 10px; color: gray">|</div>
				</logic:notEqual>
						
			</logic:iterate>
            </div>
    </logic:equal>
			<div id="table1" style="width: 99%;float:left;margin-left:5px"  >
			</div>

</div>
		</form>
	</body>
</html>
