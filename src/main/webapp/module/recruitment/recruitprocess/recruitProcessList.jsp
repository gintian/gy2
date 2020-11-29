<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.ArrayList"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.module.recruitment.recruitprocess.actionform.RecruitProcessForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="java.util.HashMap" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
RecruitProcessForm recruitProcessForm = (RecruitProcessForm)session.getAttribute("recruitProcessForm");
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
if(userView.hasTheFunction("3110701"))
{
	  request.setAttribute("showPublicPlan",true);
}
int stageListSize = recruitProcessForm.getStageList().size();
request.setAttribute("stageListSize",stageListSize);
int stageSize = recruitProcessForm.getProjectList().size();
request.setAttribute("stageSize",stageSize);
ArrayList projectList = (ArrayList)recruitProcessForm.getProjectList();
for(int i=0;i<projectList.size();i++)
{
	LazyDynaBean bean = (LazyDynaBean)projectList.get(i);
	String statusNo = (String)bean.get("status");
	if(statusNo.length()==2)
	{
		request.setAttribute("statusNo",statusNo);
	}
}
String emailItemId = (String)recruitProcessForm.getFormHM().get("emailItemId");
String url_p=SystemConfig.getServerURL(request)+"/"; //tomcat路径
String userViewName="";
if(userView!=null){
  userViewName=userView.getUserName();
}
String dbtype="1";
if(Sql_switcher.searchDbServer()== Constant.ORACEL)
{
  dbtype="2";
}
else if(Sql_switcher.searchDbServer()== Constant.DB2)
{
  dbtype="3";
}

String tableConfig = (String)recruitProcessForm.getFormHM().get("tableConfig");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<script language="javascript" src="../../general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="../../components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="../../components/personPicker/PersonPicker.js"></script>
<link href="../../components/personPicker/PersonPicker.css" rel="stylesheet" type="text/css"/>
<script src="../../components/fileupload/FileUpLoad.js" type="text/javascript"></script>
<script language="JavaScript" src="../../../ext/adapter/jquery/jquery.js"></script>
<script type="text/javascript" src="../../module/recruitment/js/custom.js"></script>
<script language="JavaScript" src="../../module/recruitment/recruitprocess/recruitProcessList.js"></script>
<script language="JavaScript" src="../../module/recruitment/js/invitationEvaluationPanel.js"></script>
<script language="JavaScript" src="../../module/recruitment/js/feedback.js"></script>
<script language="JavaScript" src="../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<link href="../../module/recruitment/css/style.css" rel="stylesheet" type="text/css" /> 
 <head>
 <style type="text/css">
 .x-window-default {padding: 0;border-width: 0px;border-style: none;background-color: white}
.toStage{width: 120px;height: 40px;float: left;padding-top: 10px;padding-left: 15px;}
.changeStatus{width: 120px;height: 40px;float: left;padding-top: 10px;padding-left: 15px;}
/*这个样式是为了解决什么问题？加上会影响到上传控件的边线 .x-window-body-default{border-style: none;} */
.img-zoom-pos {zoom:0.8;margin-top:4px;margin-left:3px;margin-right:3px;cursor: pointer;}
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
.hj-nmd-dl{margin: 8px 4px 0 4px;position:relative;line-height: 12px;}
dl dt{text-align: center}
.newDeletePic{
    position: absolute;
    top: 2px;
   	left: 42px;
    margin-top: -10px;
    margin-left: -10px;
    cursor: pointer;
}
 </style>
 <script type="text/javascript">
 Global.z0301='${recruitProcessForm.z0301}';
 Global.emailItemId='<%=emailItemId%>';
 Global.initCard = function ()
 {
       if(!AxManager.setup("chajiana", "CardPreview1", 0, 0, Global.initCard, AxManager.cardpkgName)){
         return false;
       }
       var aurl="<%=url_p%>";
       var DBType="<%=dbtype%>";
       var UserName="<%=userViewName%>";
       var obj = document.getElementById('CardPreview1');   
       var superUser="1";
       var menuPriv="";
       var tablePriv="";
       if(obj==null)
       {
    	  Ext.Msg.alert('提示信息', '没有下载打印控件，请设置IE重新下载！');
          return false;
       }
       obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
       obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
       obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
       obj.SetURL(aurl);
       obj.SetDBType(DBType);
       obj.SetUserName(UserName);
       obj.SetUserFullName("su");
 }	
 Ext.onReady(function(){
	 	if(Ext.isIE){
			Global.initCard();
	   	}
		var linkId = Ext.getDom("linkId");
		var nodeId = Ext.getDom("nodeId");
		if(linkId.value!=null)
		{
			   var stageA = Ext.getDom(linkId.value);
			   if(stageA!=null)
				    stageA.className="stage";
		}
		if(nodeId.value!=null)
        {
               var projectA = Ext.getDom(nodeId.value);
               if(projectA!=null){
	                    projectA.style.textDecoration="underline";
	               		projectA.style.color = "#ba2636";
                   }
        }
	    var tableLists = Ext.getDom("tablelists"); 
	    var functionDiv = Ext.getDom("functionDiv");
	    var tableList = Ext.getDom("tablelist");   
	    document.body.style.marginTop="0px";
        <%-- var tablelist = null;
	    var obj = Ext.decode('<%=tableConfig%>');
	    tablelist = new BuildTableObj(obj); --%>
	    window.tablelist = new BuildTableObj(<%=tableConfig%>);
	    tablelist.setBorderLayoutRegion("center");
	    Global.tableDom = tablelist.getMainPanel();
	    var saveButton = Ext.create('Ext.Button', {
	        text: "保存排名",
	        handler: Global.saveRank_num
	    });
	    tablelist.toolBar.insert(tablelist.toolBar.items.length-3,saveButton);
	    Global.bodyHtml = Ext.widget('viewport',{
	    	layout:'border',
	    	id:'tablelist_viewport',
	    	padding:"0 5 0 0",
	    	style:'backgroundColor:white',
	    	items:[{
		  			  xtype:'panel',
					  html:"<div id='leftPanel' style='height:100%;'></div>",
					  region:'west',width:170,border:false
					},{
					  itemId:'bodyDiv',
	    			  xtype:'panel',border:false,
	    			  layout:'border',region:'center',
	    			  items:[{
	    				  region:'north',border:false,
	    				  html:"<div id='topPanel'></div>",
	    				  height:25
	    			  },Global.tableDom]
	    			},
	    	       ]
	    });     
	    document.getElementById('topPanel').appendChild(document.getElementById('tablelists'));
	    document.getElementById('tablelists').style.display="block";
	    
	    document.getElementById('leftPanel').appendChild(document.getElementById('bo_left'));
	    document.getElementById('bo_left').style.display="block";
	    if("false"=='${recruitProcessForm.hasFlowLinkPriv}'){
	    	Ext.MessageBox.alert("提示信息","您无权操作此环节");
	    }
	  //非ie32位浏览器不能调用打印登记表控件
	    if(!Ext.isIE || window.navigator.platform=="Win64"){
	    	if(Ext.getCmp("printAXId"))
	     		Ext.getCmp("printAXId").destroy();
		}
	 });
//简历操作-转新阶段
 function toStage(function_str,link_id,node_id,a0100,name,c0102,flag){
	 var stageListSize = "${stageListSize}";
	 var stageHeight = Math.ceil(stageListSize/3)*40+240;
         var toStagePanel=Ext.widget("window",{
        	 modal:true,
             title:"转新阶段",
             shadow:false,
             resizable:false,
             layout:'vbox',
             collapsible:false,
             titleCollapse:true,
             renderTo:Ext.getBody(),
             bodyStyle:'background-color:white',
             //x:500,
             //y:50,
             width:400,
             height:stageHeight,
             frame:true,
             floating:true,//当设置floating为true时x,y项才有效  
             draggable:true,
             buttonAlign:'center',
             listeners:{
 		    	'close':function(){
 		    		Global.person = [];
	    	 }},
             items:[{xtype:'panel',
		    	width:'100%',
		    	border:false,
		    	height:Math.ceil(stageListSize/3)*40,
		    	html:" <logic:iterate id='element' name='recruitProcessForm' property='stageList'>" +
                "<div class='toStage'>"+
                "<input type='radio' style='margin-top:-3px' "+
                //如果勾选了	招聘环节必须按顺序进行  那么判断是否可以选中
                "<%
                    LazyDynaBean bean = (LazyDynaBean)element;
                    String link_id = (String)bean.get("link_id");
               	boolean flag = true;
                %>"+
                "<logic:equal value='1' property='skipFlag' name='recruitProcessForm' >"+
                "<logic:iterate id='skipelement' name='recruitProcessForm' property='skiplist'>" +
                "<%
                if(skipelement.equals(link_id)){
               	 flag = false;
                }
                %>"+
                "</logic:iterate>"+
                "<%
               	if(flag){
                %>"+
                " disabled='disabled'"+
                "<%}%>"+
                
                "</logic:equal> "+
                " <logic:equal value='${recruitProcessForm.next_linkId}' property='link_id' name='element' > checked </logic:equal>"+
                "id='<%=link_id%>' onclick=\"Global.showPerson('<%=link_id%>')\" name='link_ids' value='<bean:write name='element' property='link_id'/>/<bean:write name='element' property='node_id'/>'/>"+
                "<%if(!flag){%>"+
                "<font style='font-weight:bold'><bean:write name='element' property='custom_name'/></font>"+
                "<%}else{%>"+
                "<bean:write name='element' property='custom_name'/>"+
                "<%}%>"+
                //"<br><bean:write name='element' property='link_id'/>"+
                "</div>" +
                "</logic:iterate>"
                },{
         			xtype:'textarea',
         			id:'operationID',
         			labelWidth : 55,
         			labelStyle : 'padding-left:18px',
         			padding:'0 14 4 14',
         			width:'100%',
         			rows:3,
         			emptyText:'请填写意见'
         		},{xtype:'panel',
    		    	width:'100%',
    		    	border:false,
    		    	height:100,
    		    	padding:'0 10 0 0',
    		    	items:[{
    		    	    title: '通知对象',
    		    	    border:false,
    		    	    height:30,
    		    	    fullscreen: true,
    		    	    tools: [{
    		    	    	xtype: 'label',
    		    	    	margin:'0 6 0 0',
    		    	    	html:'添加：'
    		    	    },{
    		    	    	xtype: 'label',
    		    	    	margin:'0 6 0 0',
    		    	        html: '<a style="cursor:pointer" onmouseout="leave(this)" onmouseover="hover(this)">自助用户</a>',
    		    	        listeners: {
    		    	            click: {
    		    	                element: 'el',
    		    	                fn:function(){Global.openPicker(true)}
    		    	            }
    		    	        }
    		    	    },{
    		    	    	xtype: 'label',
    		    	        html: '<a style="cursor:pointer" onmouseout="leave(this)" onmouseover="hover(this)">业务用户</a>',
    		    	        listeners: {
    		    	            click: {
    		    	                element: 'el',
    		    	                fn:function(){Global.openPicker(false)}
    		    	            }
    		    	        }
    		    	    }]
    		    	}],
    		    	html:'<div id="personArea" class="hj-zm-xq-two" style="overflow-y:auto;padding-left:6px;margin-top:0px;width:380px;height:70px" ></div>',
    		    	listeners:{
    		    		'afterrender':function(){
    		    			Global.showPerson('${recruitProcessForm.next_linkId}');
    		    		}
    		    	}
    	    	}],
             buttons:[//底部按钮
                 {text:"确定",handler:function(){
                	 var link_ids = document.getElementsByName("link_ids");
                	    for(var i=0;i<link_ids.length;i++)
                	    {
                	        if(link_ids[i].checked)
                	        { 
                	        	Global.index++;
                	        	var values=link_ids[i].value.split('/');
                	        	Global.updateOperation(function_str,values[0],values[1],a0100,name,c0102);
                	        	toStagePanel.close();
                	        	return;
                	        } 
                	    }
                     }
                 },
                 {text:"关闭",handler:function(){
                	 if(flag == "passChoice"){
                		 document.recruitProcessForm.action="/recruitment/recruitprocess/recruitprocesslist.do?b_query=link&link_id="+link_id+"&node_id="+node_id;
                 		 document.recruitProcessForm.submit();
                	 }
                	 toStagePanel.close();
                	 }}
                 ] 
         });//Ext.getBody().mask();
         toStagePanel.show();
         if(Global.from=="resume"){
         	eval("var obj = Ext.getDom('"+window.frames['ifra'].Ext.getDom("nextLinkId").value+"')");
	        if(obj)
	            obj.checked=true;
         }
}

function leave(obj){
	obj.style.color="#549FE3"
}
function hover(obj){
	obj.style.color="#E39E19"
}
</script>
</head>
<body> 
<form action="/recruitment/recruitprocess/recruitprocesslist" method="post" name="recruitProcessForm">
<input type="hidden" value="<bean:write name="recruitProcessForm" property="linkId"/>" id="linkId">
<input type="hidden" value="<bean:write name="recruitProcessForm" property="nodeId"/>" id="nodeId">
<input type="hidden" value="<bean:write name="recruitProcessForm" property="z0301"/>" id="z0301">
<input type="hidden" value="<bean:write name="recruitProcessForm" property="infoBean.z0381"/>" id="z0381">
<input type="hidden" value="<bean:write name="recruitProcessForm" property="next_nodeId"/>" id="next_node">
</form>

     <div id="bo_left" class="hj-zm-cplc-bo-left" style="display:none;height:100%;">
         <div style="height: 30px;width: 100%;padding-top: 5px;border-bottom:1px #c5c5c5 solid;font-size:14px;font-weight:bold;margin-bottom:10px;" align="center">
         	招聘流程
         </div>
         <ul>
         <div class="hj-wzm-shuxian">
         <logic:iterate id="element" name="recruitProcessForm" property="stageList" indexId="i">
             <li>
             <logic:equal value="true" name="element" property="linkPriv">
             <a href="javascript:void(0);" class="hj-zm-cplc-bo-left-true" style="color:#FFF;cursor:default;<%if(0==i){ %> margin-top:0px; <%}%>" id="<bean:write name="element" property="link_id"/>">
             </logic:equal>
             <logic:equal value="false" name="element" property="linkPriv">
             <%--<img src="/module/recruitment/image/select.gif"  height="16" width="16" margin-left="-10px"/>
             --%><a href="javascript:void(0);" class="hj-zm-cplc-bo-left-false" style="color:#FFF;cursor:default;" id="<bean:write name="element" property="link_id"/>">
             </logic:equal>
             <label onclick="javascript:Global.queryStageList('<bean:write name="element" property="link_id"/>','<bean:write name="element" property="status"/>','<bean:write name="element" property="linkPriv"/>')">
             <bean:write name="element" property="custom_name"/> </label>
             <%--<logic:equal value="0" property="all_number" name="element">
                <div style="display:inline-block;">(<label onclick="javascript:Global.queryStageList('<bean:write name="element" property="link_id"/>','<bean:write name="element" property="node_id"/>','<bean:write name="element" property="linkPriv"/>')">0</label>)</div>
                </logic:equal>
             --%><logic:notEqual value="0" property="all_number" name="element">
                  <div style="display:inline-block;">【<label href="javascript:void(0);"  onclick="Global.queryProjectNum('<bean:write name="element" property="link_id"/>','<bean:write name="element" property="node_id"/>01','<bean:write name="element" property="linkPriv"/>')">
                 <bean:write name="element" property="new_number"/></label>
                 /<label href="javascript:void(0);"  onclick="Global.queryProjectNum('<bean:write name="element" property="link_id"/>','<bean:write name="element" property="node_id"/>','<bean:write name="element" property="linkPriv"/>')">
                 <bean:write name="element" property="all_number"/></label>】</div>
             </logic:notEqual>
             </a></li>
         </logic:iterate>
         </div>
         </ul>
         </div>
     <div style="float:left;padding-top:0px;display:none" id="tablelists" >
       <div style="FLOAT: left; PADDING-TOP: 5px;padding-right: 10px;">查询方案:</div>
       <logic:iterate id="element" name="recruitProcessForm" property="projectList">
           <div style="padding-right: 10px;float: left;padding-top: 5px;">
           <a onclick="javascript:Global.queryProjectList('<bean:write name="element" property="link_id"/>','<bean:write name="element" property="status"/>')" style="" href="javascript:void(0);" id="<bean:write name="element" property="status"/>" >
           <bean:write name="element" property="custom_name"/><bean:write name="element" property="all_number"/></a>
           <logic:equal name="element" property="custom_name" value="全部"><label style=" font-size: 12px;font-family:微软雅黑;color:#C5C5C5;">&nbsp;|</label></logic:equal>
           </div>
       </logic:iterate>&nbsp;&nbsp;&nbsp;&nbsp;
      
    </div>
 <div id='chajiana' ></div> 
</body>
<script type="text/javascript">
//屏蔽backspace
window.onload=function(){
	Ext.getDoc().on('keydown',function(e){  
	    if(e.getKey() == 8 && e.getTarget().type =='text' && !e.getTarget().readOnly){  
	           
	    }else if(e.getKey() == 8 && e.getTarget().type =='textarea' && !e.getTarget().readOnly){   
	       
	    }else if(e.getKey() == 8){  
	        e.preventDefault();  
	    }  
	});
	
}; 
</script>
</html>