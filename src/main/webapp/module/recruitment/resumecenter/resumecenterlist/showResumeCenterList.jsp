<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.module.recruitment.resumecenter.actionform.ResumeCenterForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,com.hrms.struts.constant.SystemConfig" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String url_p=SystemConfig.getCsClientServerURL(request)+"/"; //tomcat路径
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
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
ResumeCenterForm resumeCenterForm=(ResumeCenterForm)session.getAttribute("resumeCenterForm"); 
String from=request.getParameter("from")!=null?request.getParameter("from"):"";
String title = "";
boolean boo=(Boolean)resumeCenterForm.getFormHM().get("othModule");
//默认查询指标
String defaultQuery = (String)resumeCenterForm.getFormHM().get("defaultQuery");
//备选查询指标
String optionalQuery = (String)resumeCenterForm.getFormHM().get("optionalQuery");
//保存默认查询模板
Object hasTheFunction = resumeCenterForm.getFormHM().get("hasTheFunction");
//已发布批次
String batchQuery = (String)resumeCenterForm.getFormHM().get("batchQuery");

String exceptItems = (String)resumeCenterForm.getFormHM().get("exceptItems");
String jsonStr = (String)resumeCenterForm.getFormHM().get("jsonStr");
if(from.equals("resumeCenter"))
{
	request.setAttribute("subModuleId","zp_resume_191130_00001");
	if(userView.hasTheFunction("3110207"))
	{
		  request.setAttribute("showPublicPlan",true);
	}
	title="简历中心";
}else{
	request.setAttribute("subModuleId","zp_talent_191130_00001");
	if(userView.hasTheFunction("3110307"))
	{
		  request.setAttribute("showPublicPlan",true);
	}
	title="人才库";
}
request.setAttribute("title",title);
String schemeValues = (String)resumeCenterForm.getSchemeValues();
String pageDesc = (String)resumeCenterForm.getPageDesc();
%>

<link href="/module/recruitment/css/style.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>
<script type="text/javascript" src="/module/recruitment/js/feedback.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="javascript" src="../../module/recruitment/recruitment_resource_zh_CN.js"></script>
<script type="text/javascript" src="/module/recruitment/resumecenter/resumecenterlist/resumeCenter.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<script>
Global.exceptItems = <%=exceptItems%>;
Global.jsonStr = <%=jsonStr%>;
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
    function searchResume(value,meta,record,rowIndex){
        var resumeid = record.data.a0100_e;
        var zp_pos_id = record.data.z0301_e;
        var nbase = record.data.nbase_e; 
    	var name = record.data.a0101;
    	var resumeNum = record.data.resumenum.split("`")[0];
    	var isTalents = "";
    	if("resumeCenter"==Global.fromModule)
    	{
    		isTalents = record.data.istalents.split("`")[0];
    		if(isTalents>0)
    		{
    			isTalents=" <img title='已加入人才库' src='/module/recruitment/image/resume/isTalents.png'>";
        	}else{
        		isTalents="";
            }
        }
        if(resumeNum>1)
        {
        	/* resumeNum=" <img title='申请职位数量' src='/module/recruitment/image/resume/"+resumeNum+".png'>"; */
        	resumeNum = "<span title='申请职位数量' style='display:inline-block;border-radius:2px;color:#3686c7;margin:0px 5px;padding:1px 3px;border:1px solid #529fe5;'>"+resumeNum+"</span>"; 
        }else
        {
        	resumeNum="";
        }
    	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
    	var current = datastore.currentPage;
    	var pagesize = datastore.pageSize;
    	var schemeValues = '';
	    for(var i=0; i<Global.schemeArr.length; i++){
	        if(i!=Global.schemeArr.length-1)
	            schemeValues += Global.schemeArr[i] + ",";
	        else
	            schemeValues += Global.schemeArr[i]; 
	    }
	    var href = "/recruitment/resumecenter/searchresume.do?b_search=link&resumeid="+resumeid+"&zp_pos_id="+zp_pos_id+"&current="+current+"&pagesize="+pagesize+"&rowindex="+rowIndex+"&nbase="+nbase+"&schemeValues="+schemeValues+"&from="+Global.fromModule;
    	return '<a id='+resumeid+' href="javascript:void(0)" onclick="qureyResume(\''+nbase+'\',\''+resumeid+'\',\''+zp_pos_id+'\',\''+Global.fromModule+'\',\''+current+'\',\''+pagesize+'\',\''+rowIndex+'\',\''+schemeValues+'\')" target="_self">'+name+'</a>'+isTalents;
    }
    function qureyResume(nbase,a0100,zp_pos_id,from,current,pagesize,rowindex,schemeValues)
    {
        if(zp_pos_id=="2iIeo7kAcbU@3HJD@")
        {
        	zp_pos_id = "";
        }
    	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'ResumeTemplateUL': '/module/recruitment/resumecenter/resumecenterlist',
				'ResumeUL': '/module/recruitment/resumecenter/resumecenterlist',
				'SYSF.FileUpLoad':'/components/fileupload'
			}
		});
	    Ext.require('ResumeTemplateUL.resumeInfoTop', function(){
			Ext.create("ResumeTemplateUL.resumeInfoTop", {nbase:nbase,a0100:a0100,zp_pos_id:zp_pos_id,from:from,current:current,pagesize:pagesize,rowindex:rowindex,schemeValues:schemeValues});
		});
    }
    function searchPosition(value,meta,record){
        var zp_pos_id = record.data.z0301_e;
        var z0319 = record.data.z0319;
        if(!!z0319&&z0319.indexOf("`")!=-1){
            var s = z0319.split("`");
            z0319 = s[0];
        }
        var z0381 = record.data.z0381_e;
        var nbase = record.data.nbase_e; 
    	var name = record.data.z0351;
    	var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
    	var schemeValues = '';
	    for(var i=0; i<Global.schemeArr.length; i++){
	        if(i!=Global.schemeArr.length-1)
	            schemeValues += Global.schemeArr[i] + ",";
	        else
	            schemeValues += Global.schemeArr[i]; 
	    }
	    var href = "/recruitment/position/position.do?b_search=link&z0301="+getEncodeStr(zp_pos_id)+"&z0319="+z0319+"&z0381="+z0381+"&sign=1&pageNum="+datastore.currentPage+"&searchStr="+schemeValues+"&pagesize="+datastore.pageSize+"&from="+Global.fromModule;
    	return '<a id='+zp_pos_id+' href="javascript:void(0)" onclick="setDisabled(\''+zp_pos_id+'\',\''+href+'\')" target="_self">'+name+'</a>';
    }
    function sendEmail(value,meta,record){
        var email = record.data.c0102;
        return '<a href="mailto:'+email+'">'+email+'</a>';
    }
    //截取日期长度
    function subDate(value,meta,record){
        var date = record.data.recdate;
        if(date && date.length>10)
            date = date.substring(0,10);
        return date;
    }
   	var tablegrid = undefined;
   	
    Ext.onReady(function(){
    	Ext.Loader.setConfig({
			enabled: true,
			paths: {
				'SYSF':'../../../../components/fileupload',
				'NoticePath': '../../../module/system/viewboard'
			}
		});
    	if("resumeCenter"==Global.fromModule)
    	{
    		if(Ext.isIE){
    			Global.initCard();
    	   	}
    	<hrms:tableFactory  jsObjName="tablegrid" sqlProperty="sqlstr"  title="${title}" constantName="${resumeCenterForm.constantxml}" 
    		subModuleId="${subModuleId}"  isColumnFilter="true" fieldAnalyse="true" columnProperty="groupcolumns" formName="resumeCenterForm" currentPage="${resumeCenterForm.current}"  showPublicPlan="${showPublicPlan}" pagesize="${resumeCenterForm.pagesize}" 
    			itemKeyFunctionId="ZP0000002130"  isScheme="true" schemePosition="title"  schemeSaveCallback="Global.schemeSave" >//schemeSaveCallback 栏目设置后的回调函数
    	<hrms:buttonTag buttonsProperty="buttonList" />
    	</hrms:tableFactory>    
        }else{
    	<hrms:tableFactory  jsObjName="tablegrid" sqlProperty="sqlstr"  title="${title}" constantName="${resumeCenterForm.constantxml}" 
    		subModuleId="${subModuleId}"  isColumnFilter="true" fieldAnalyse="true" columnProperty="groupcolumns" formName="resumeCenterForm" currentPage="${resumeCenterForm.current}"  showPublicPlan="${showPublicPlan}" pagesize="${resumeCenterForm.pagesize}" 
    			itemKeyFunctionId="ZP0000002130"  isScheme="true" schemePosition="title" >
    	<hrms:buttonTag buttonsProperty="buttonList" />
    	</hrms:tableFactory>    
        	
        }
    	tablegrid.setBorderLayoutRegion("center");
    	//tablegrid.renderTo("tablegrid"); 
  	var myPanel = Ext.widget('panel',{
  	  // title:Global.fromName,
	   height:20,
	   border:false,
	   html:"<div id='toolbarPanel'></div>"
	});
  	tablegrid.insertItem(myPanel,0);
  	Ext.widget('viewport',{
    	layout:'fit',
    	id:'tablegrid_viewport',
    	padding:"0 5 0 5",
    	style:'backgroundColor:white',
    	items:tablegrid.getMainPanel()
   	});    
  	document.getElementById('toolbarPanel').appendChild(document.getElementById('funcDiv'));
    document.getElementById('funcDiv').style.display="block";
        	 
    //公共查询控件
    Ext.require("EHR.commonQuery.CommonQuery",function(){
          var commonQuery = Ext.create("EHR.commonQuery.CommonQuery",{
              subModuleId:Global.fromModule,
              defaultQueryFields:<%=defaultQuery%>,
              fieldsFunctionId:'ZP0000002110',
              ctrltype:'3',
              nmodule:'7',
              beforeFieldRender:function(field){
              	if(field.itemid=='Z0103'){
              		field.codeData=<%=batchQuery%>;
              	}
              },
               doQuery:function(items){
           	   var schemeValues = '';
           	   for ( var i = 0; i < Global.schemeArr.length; i++) {
           			if (i != Global.schemeArr.length - 1)
           				schemeValues += Global.schemeArr[i] + ",";
           			else
           				schemeValues += Global.schemeArr[i];
           		}
               	var map = new HashMap();
               	map.put("items", items);
               	map.put("schemeValues", schemeValues);
               	map.put("from", Global.fromModule);
               	Rpc( {
               		functionId : 'ZP0000002101',
               		success : function(){
               			var datastore = Ext.data.StoreManager.lookup('tablegrid_dataStore');
               			datastore.load();
               			datastore.loadPage(1);
               		}
               	}, map);
              	 
               },
               scope:Global,
               fieldPubSetable:<%=hasTheFunction%>
          });
          tablegrid.insertItem(commonQuery,0);
          
      });
        /*   //将上传组件渲染到importResumeId
     	if(!Ext.isEmpty(Ext.get("importResumeId")))
     		Global.importResume(); */
     	//非ie32位浏览器不能调用打印登记表控件
	    if(!Ext.isIE || window.navigator.platform=="Win64"){
	    	if(Ext.getCmp("printAXId"))
	     		Ext.getCmp("printAXId").destroy();
		}
    });
</script>
<style>
.border-width-none {border:0px solid;line-height:22px;letter-spacing:0px;text-align:left;word-spacing:0px}
.img-zoom-pos {zoom:0.9;margin:2px 1px 0 1px;}
.x-resizable-handle-west {width: 0px;height: 100%;left: 0;top: 0}
</style>
<body>
<div id='chajiana' ></div>  
<div id="funcDiv" style="display:none;margin-bottom: 0px;">    
    <div id="searchbar" style="float:left;margin-top:2px;margin-left:3px;width:99%;margin-bottom: 0px" >
	    <div name="employ" style="float:left;">
	   	    <logic:equal name="resumeCenterForm" property="from" value="resumeCenter" > 
	         应聘情况：
	        </logic:equal>
	        <logic:equal name="resumeCenterForm" property="from" value="talents" > 
        	投递时间：
          	</logic:equal>
	    </div>
	    <div name="href" style="float:left;margin-left:5px;">
	       <a id="all" name="all" href="javascript:void(0);" onclick="Global.searchAll();Global.schemeSearch();" > 全部 </a>
        </div>
	    <%int i = 0; %>
	    <logic:iterate id="scheme"    name="resumeCenterForm"  property="queryscheme">
	        <logic:notEqual name="scheme" property="regional" value="<%=String.valueOf(i) %>">
	            <%i++; %><div name="rule" style="float: left;margin-left: 10px; color: gray">|</div>
	        </logic:notEqual>
	        <div name="href" style="float:left;margin-left:10px;">
            <a id="<bean:write  name="scheme" property="id" filter="true"/>" 
              name="<bean:write  name="scheme" property="size" filter="true"/>"
              href="javascript:void(0);" 
              onclick="<bean:write  name="scheme" property="event" filter="true"/>;Global.schemeSearch();" >
                <bean:write  name="scheme" property="name" filter="true"/>
            </a>
            </div>
	    </logic:iterate>
    </div>
    </div>
	<div id="tablegrid" style="float:left;width:99%;margin-left:5px;">
	</div>
</div>
</body>
<script>
    Global.fromModule='<%=from%>';
    if(Global.fromModule==="resumeCenter")
        Global.fromName="简历中心";
    else if(Global.fromModule==="talents")
        Global.fromName="人才库";
   	Global.pageDesc = '<%=pageDesc %>';
   	<% if("0,0,0".equals(schemeValues)){%>
   		Global.searchAll();
   	<%}else if(schemeValues!=null && !schemeValues.equals("")&&!boo){ %>
   	    var schemeValues = '<%=schemeValues %>';
   	    var scheme = schemeValues.split(",");
   	    for(var i=0; i<scheme.length; i++){
   	        if(scheme[i]!='0')
   	            Global.setSchemeArr(i,scheme[i]);
   	    }
   	<% } %>
   	var tablegrid_obj = Ext.getDom("tablegrid"); 
   	var winHeight =parent.document.body.clientHeight-72;
   	tablegrid_obj.style.height = winHeight + "px";
</script>
