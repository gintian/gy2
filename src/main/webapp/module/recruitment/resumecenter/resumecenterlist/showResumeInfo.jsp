<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.module.recruitment.resumecenter.actionform.ResumeForm,com.hrms.hjsj.utils.Sql_switcher,com.hrms.hjsj.sys.Constant,com.hrms.struts.constant.SystemConfig" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="java.net.URLEncoder"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
UserView userView = (UserView) session.getAttribute(WebConstant.userView);
ResumeForm resumeForm=(ResumeForm)session.getAttribute("resumeForm");
int stageListSize = resumeForm.getStageList().size();
int length = resumeForm.getOthPos().size();
int length2 = resumeForm.getLastPos().size();
String nextResume = PubFunc.encrypt("resumeid=" + PubFunc.encrypt(resumeForm.getNextResumeid()) + "&zp_pos_id=" + PubFunc.encrypt(resumeForm.getNextZp_pos_id()) + "&nbase=" 
		+ PubFunc.encrypt(resumeForm.getNextNbase()) + "&current=" + resumeForm.getNextCurrent() + "&pagesize=" + resumeForm.getNextPagesize() + 
		"&rowindex=" + resumeForm.getNextRowindex() + "&schemeValues" + resumeForm.getSchemeValues() + "&from=resumeCenter");
String nextResume1 = PubFunc.encrypt("resumeid=" + PubFunc.encrypt(resumeForm.getNextResumeid()) + "&zp_pos_id=" + PubFunc.encrypt(resumeForm.getNextZp_pos_id()) + "&nbase=" 
		+ PubFunc.encrypt(resumeForm.getNextNbase()) + "&current=" + resumeForm.getNextCurrent() + "&pagesize=" + resumeForm.getNextPagesize() + 
		"&rowindex=" + resumeForm.getNextRowindex() + "&schemeValues" + resumeForm.getSchemeValues() + "&from=talents");
String resumeid1 = PubFunc.encrypt(resumeForm.getResumeid());
String zp_pos_id1 = resumeForm.getZp_pos_id();
String nbase = PubFunc.encrypt(resumeForm.getNbase());
String rootPath = resumeForm.getRootPath();
ArrayList projectList = (ArrayList)resumeForm.getProjectList();
for(int i=0;i<projectList.size();i++)
{
	LazyDynaBean bean = (LazyDynaBean)projectList.get(i);
	String statusNo = (String)bean.get("status");
	if(statusNo.length()==2)
	{
		request.setAttribute("statusNo",statusNo);
	}
}
String userViewName="";
if(userView!=null){
  userViewName=userView.getUserName();
}
String url_p=SystemConfig.getCsClientServerURL(request)+"/"; //tomcat路径
String dbtype="1";
if(Sql_switcher.searchDbServer()== Constant.ORACEL)
{
  dbtype="2";
}
else if(Sql_switcher.searchDbServer()== Constant.DB2)
{
  dbtype="3";
}
%>
<link rel="stylesheet" href="/module/recruitment/css/style.css" type="text/css" />
<link rel="stylesheet" href="/module/recruitment/css/stars.css" type="text/css" media="screen">
<script type="text/javascript" src="/module/recruitment/js/stars.js"></script>
<script type="text/javascript" src="/components/tableFactory/tableFactory.js"></script>
<script language="JavaScript" src="/components/personPicker/PersonPicker.js"></script>
<script language="JavaScript" src="/module/recruitment/js/invitationEvaluationPanel.js"></script>
<script type="text/javascript" src="/module/recruitment/resumecenter/resumecenterlist/resumeInfo.js"></script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>

<script src="/components/fileupload/FileUpLoad.js" type="text/javascript"></script>
<style>
body,ul,ol,li,p,h1,h2,h3,h4,h5,h6,form,fieldset,img,div,dl,dt,dd,span,table,tr,td{margin:0;padding:0; border:none;}
.addButton{
width:66px;
height:24px;
border:none;
margin-top:8px;
color:#FFF;
float:right;
background:#529FE5;
	}
.divFile{
white-space:nowrap;
overflow:hidden;
text-overflow:ellipsis;
width:200px;
float:left;
}
	/**
	*调整操作功能不能正常显示问题
	*/
.flex-container {
  display: -moz-box;  /* Firefox */ 
  display: -ms-flexbox;    /* IE10 */ 
  display: -webkit-box;    /* Safari */  
  display: -webkit-flex;    /* Chrome, WebKit */ 
  display: box;  
  display: flexbox;  
  display: flex;   
  width: 100%;  
  height: 100%;  
}

.hj-zm-hxr-all{width:99%;color:#666; background:#FFF;}
.hj-zm-hxr-three-yi{margin-top:0px}
.hj-zm-hxr-three{margin-top:0px;color: #333;}

.toStage{width: 120px;height: 40px;float: left;padding-top: 10px;padding-left: 15px;}
</style>
<script language="JavaScript">
    Global.resumeid = "<%=resumeid1 %>";
    Global.nbase = "<%=nbase %>";
    Global.username = "${resumeForm.username}";
    Global.email = "${resumeForm.email}";
    Global.from = "${resumeForm.from}";
    Global.current="${resumeForm.current }";
    Global.pagesize="${resumeForm.pagesize }";
    Global.schemeValues="${resumeForm.schemeValues }";
    Global.rowindex="${resumeForm.rowindex }";
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
    //简历操作-转新阶段
    function toStage(function_str,link_id,node_id,a0100,name,c0102,z0301,nbase){
    	var stageListSize = "<%=stageListSize%>";
        var stageHeight = Math.ceil(stageListSize/3)*40+140;
        
        var toStagePanel=Ext.widget("window",{
            modal:true,
            title:"转新阶段",
            region:'center',
            shadow:false,
            resizable:false,
            layout:'border',
            buttonAlign: 'center',
            collapsible:false,
            titleCollapse:true,
            renderTo:Ext.getBody(),
            bodyStyle:'background-color:white',
            width:400,
            height:stageHeight,
            border:0,
            frame:true,
            floating:true,//当设置floating为true时x,y项才有效  
            draggable:true,
            html:" <logic:iterate id='element' name='resumeForm' property='stageList'>" +
            "<div class='toStage'>"+
            "<input type='radio' "+
            //如果勾选了	招聘环节必须按顺序进行  那么判断是否可以选中
            "<%
                LazyDynaBean bean = (LazyDynaBean)element;
                String link_id = (String)bean.get("link_id");
           	boolean flag = true;
            %>"+
            "<logic:equal value='1' property='skipFlag' name='resumeForm' >"+
            "<logic:iterate id='skipelement' name='resumeForm' property='skiplist'>" +
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
            " <logic:equal value='${resumeForm.next_linkId}' property='link_id' name='element' > checked </logic:equal>"+
            " id='<%=link_id%>' name='link_ids' value='<bean:write name='element' property='link_id'/>/<bean:write name='element' property='node_id'/>'/>"+
            "<%if(!flag){%>"+
            "<font style='font-weight:bold'><bean:write name='element' property='custom_name'/></font>"+
            "<%}else{%>"+
            "<bean:write name='element' property='custom_name'/>"+
            "<%}%>"+
            //"<br><bean:write name='element' property='link_id'/>"+
            "</div>" +
            "</logic:iterate>",
            items:{
     			xtype:'textarea',
     			id:'operationID',
     			region : "south",
     			fieldLabel:'意见',
     			labelWidth : 55,
     			labelStyle : 'padding-left:18px',
     			padding:'0 15 4 0',
     			width:'100%',
     			rows:3,
     			emptyText:'请填写意见'
     		},
            buttons:[
                {text:"确定",handler:function(){
                    var link_ids = document.getElementsByName("link_ids");
                       for(var i=0;i<link_ids.length;i++)
                       {
                           if(link_ids[i].checked)
                           { 
                               var values=link_ids[i].value.split('/');
                               Global.updateOperation(function_str,values[0],values[1],a0100,name,c0102,z0301,nbase);
                               toStagePanel.close();
                           } 
                       }
                    }},
                {text:"关闭",handler:function(){toStagePanel.close();}}
                ] //底部按钮
        });//Ext.getBody().mask();
        toStagePanel.show();
        eval("var obj = Ext.getDom('"+Ext.getDom("nextLinkId").value+"')");
        if(obj)
            obj.checked=true;
   }
	  
</script>
<body onload="setGlobalPos();">
<form action="" method="post" name="resumeForm"></form>
<input id="zp_pos_id" type="hidden" value="<%=zp_pos_id1 %>" />
<input type="hidden" value="<bean:write  name="resumeForm" property="username" filter="true"/>" id="username"/>
<input type="hidden" value="<bean:write  name="resumeForm" property="node_flag"/>" id="node_flag" >
<input type="hidden" value="<bean:write  name="resumeForm" property="next_linkId"/>" id="nextLinkId" >
<input type="hidden" value="<bean:write  name="resumeForm" property="nextResumeid"/>" id="nextResumeid" >
<input type="hidden" value="<bean:write  name="resumeForm" property="nextNbase"/>" id="nextNbase" >
<input type="hidden" value="<%="null".equals(PubFunc.decrypt(resumeForm.getNextZp_pos_id()))?"":resumeForm.getNextZp_pos_id() %>" id="nextZp_pos_id" >
<input type="hidden" value="<bean:write  name="resumeForm" property="nextCurrent"/>" id="nextCurrent" >
<input type="hidden" value="<bean:write  name="resumeForm" property="nextPagesize"/>" id="nextPagesize" >
<input type="hidden" value="<bean:write  name="resumeForm" property="nextRowindex"/>" id="nextRowindex" >
<input type="hidden" value="<bean:write  name="resumeForm" property="lastResumeid"/>" id="lastResumeid" >
<input type="hidden" value="<bean:write  name="resumeForm" property="lastNbase"/>" id="lastNbase" >
<input type="hidden" value="<%="null".equals(PubFunc.decrypt(resumeForm.getLastZp_pos_id()))?"":resumeForm.getLastZp_pos_id() %>" id="lastZp_pos_id" >
<input type="hidden" value="<bean:write  name="resumeForm" property="lastCurrent"/>" id="lastCurrent" >
<input type="hidden" value="<bean:write  name="resumeForm" property="lastPagesize"/>" id="lastPagesize" >
<input type="hidden" value="<bean:write  name="resumeForm" property="lastRowindex"/>" id="lastRowindex" >
<div id="header" style="display:none;" class='hj-zm-hxr-three-right1'>
       <logic:equal value="null" name="resumeForm" property="operationList" > <font face="微软雅黑" style="font-weight:bold;">候选人简历</font>
        &nbsp;&nbsp;&nbsp;&nbsp;
 	<strong><label><bean:write  name="resumeForm" property="username" filter="true"/></label></strong>&nbsp;&nbsp;&nbsp;
 	<logic:notEqual name="resumeForm" property="recdate" value="">
   &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
    		<label id=recdate><bean:write  name="resumeForm" property="recdate" filter="true"/></label>&nbsp;更新
	</logic:notEqual>
	<logic:notEqual name="resumeForm" property="status" value="">
   &nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;
      <logic:equal name="resumeForm" property="status" value="0"><label id="status">未处理</label></logic:equal>
      <logic:equal name="resumeForm" property="status" value="1"><label id="status">接受</label></logic:equal>
      <logic:equal name="resumeForm" property="status" value="2"><label id="status">拒绝</label></logic:equal>
	</logic:notEqual>
	</logic:equal>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<logic:equal name="resumeForm" property="from" value="resumeCenter">
		<logic:notEqual name="resumeForm" property="node_flag" value="1">
		    <logic:iterate id="pos"    name="resumeForm"  property="lastPos">
	          <hrms:priv  func_id="3110203">
	          <a id="acceptPA" href="javascript:void(0)" >接受职位申请</a>
	          </hrms:priv>
	          <hrms:priv  func_id="3110204">
	          <a id="rejectPA" href="javascript:void(0)" >拒绝职位申请</a></hrms:priv>
			</logic:iterate>
			<hrms:priv  func_id="3110205">
	         <logic:equal name="resumeForm" property="isTalent" value="true">
	             <a id="turnTal" href="javascript:void(0)" onclick="" style="color:gray"">加入人才库</a>
	         </logic:equal>
	         <logic:notEqual name="resumeForm" property="isTalent" value="true">
	             <a id="turnTal" href="javascript:void(0)" onclick="Global.turnTalents()">加入人才库</a>
	         </logic:notEqual>
			</hrms:priv>
		</logic:notEqual>
             <logic:notEqual name="resumeForm" property="nextResumeid" value="">
                 <a id="nextResume" href="javascript:void(0)" onclick="setDisabled('nextResume','/recruitment/resumecenter/searchresume.do?b_search=link&encryptParam=<%=nextResume%>')" target="_self">处理下一份简历</a>
             </logic:notEqual>
             <hrms:priv func_id="3110206">
       <a onclick="Global.recommendOtherPosition(Global.resumeid,Global.nbase,Global.email)" href="javascript:void(0)" >推荐职位</a></hrms:priv>
          <a id="back" href="javascript:void(0)" onclick="setDisabled('back','/recruitment/resumecenter/searchresumecenter.do?b_search=link&current=${resumeForm.current }&pagesize=${resumeForm.pagesize }&schemeValues=${resumeForm.schemeValues }&from=resumeCenter&back=true')">返回</a>
    </logic:equal>
    <logic:equal name="resumeForm" property="from" value="talents">
   	 	<logic:notEqual name="resumeForm" property="node_flag" value="1">
	  		<logic:equal name="resumeForm" property="isMine" value="1">
	          <logic:iterate id="pos"    name="resumeForm"  property="lastPos">
		          <hrms:priv  func_id="3110203"><a id="acceptPA" href="javascript:void(0)" >接受职位申请</a></hrms:priv>
		          <hrms:priv  func_id="3110204"><a id="rejectPA" href="javascript:void(0)" >拒绝职位申请</a></hrms:priv>
	          </logic:iterate>
	       </logic:equal>
		</logic:notEqual>
        <logic:notEqual name="resumeForm" property="nextResumeid" value="">
            <a id="nextResume" href="javascript:void(0)" onclick="setDisabled('nextResume','/recruitment/resumecenter/searchresume.do?b_search=link&encryptParam=<%=nextResume1%>')" target="_self">处理下一份简历</a>
        </logic:notEqual>
        <hrms:priv func_id="3110306"><a onclick="Global.recommendOtherPosition(Global.resumeid,Global.nbase,Global.email)" href="javascript:void(0)" >推荐其他职位</a></hrms:priv>
        <a id="back" href="javascript:void(0)" onclick="setDisabled('back','/recruitment/resumecenter/searchresumecenter.do?b_search=link&current=${resumeForm.current }&pagesize=${resumeForm.pagesize }&&schemeValues=${resumeForm.schemeValues }&from=talents&back=true')">返回</a>
    </logic:equal>
	        <%int total = 0; %>
    <logic:equal name="resumeForm" property="from" value="process">
    <input type="hidden" value="<bean:write name="resumeForm" property="next_nodeId"/>" id="next_nodeId">
        <div style="margin-right: 30px;margin-right: 100px;width:10%;float:left"><label id="link_name"><bean:write property="infoBean.link_name" name="resumeForm"/></label>( <label id="node_name"><bean:write property="infoBean.resume_name" name="resumeForm"/></label> )</div>
	        <input type="hidden" value="<bean:write property="infoBean.resume_id" name="resumeForm"/>" id="node_id"/>
	        <input type="hidden" value="<bean:write property="infoBean.z0381" name="resumeForm"/>" id="z0381"/>
	        <input type="hidden" value="<bean:write property="infoBean.z0301" name="resumeForm"/>" id="z0301"/>
	        <input type="hidden" value="<bean:write property="infoBean.c0102" name="resumeForm"/>" id="c0102"/>
	        <input type="hidden" value="<bean:write property="infoBean.resume_flag" name="resumeForm"/>" id="resume_flag"/>
	        <input type="hidden" value="<bean:write property="infoBean.link_id" name="resumeForm"/>" id="link_id"/>
	        <input type="hidden" value="<bean:write property="infoBean.page" name="resumeForm"/>" id="page"/>
	        <input type="hidden" value="<bean:write property="infoBean.a0100" name="resumeForm"/>" id="a0100"/>
	        <%--人员简历信息中的操作功能--%>
	        <div style="display: inline;" id="tDiv">
	        <logic:notEqual value="null" name="resumeForm" property="operationList" >
	            <logic:iterate id="element" name="resumeForm" property="operationList">
	                <div style="display:inline;" id="temp<%=total %>"><a href="javascript:void(0);"  onclick="Global.operation('<bean:write property="function_str" name="element"/>',
	                '<bean:write property="link_id" name="element"/>','<bean:write property="infoBean.c0102" name="resumeForm"/>',
	                '<bean:write property="infoBean.z0301" name="resumeForm"/>','<bean:write property="custom_name" name="element"/>',
	                '<bean:write property="infoBean.nbase" name="resumeForm"/>','<bean:write property="infoBean.a0100" name="resumeForm"/>')">
	                <bean:write property="custom_name" name="element"/></a></div>
	                <%total++; %>
	            </logic:iterate>
	        </logic:notEqual>
		        <div style="display: inline;" id="showHide"><div style="display: inline;float:left" id="upSourceFile"></div>
		        <a href="javascript:void(0);" id="temps" onclick="Global.goBack('<bean:write property="infoBean.z0301" name="resumeForm"/>','<bean:write property="infoBean.z0381" name="resumeForm"/>',
		        '<bean:write property="infoBean.page" name="resumeForm"/>','<bean:write property="infoBean.link_id" name="resumeForm"/>',
		        '<bean:write property="infoBean.resume_id" name="resumeForm"/>')">返回</a></div>
	        <%--
	        <a href="javascript:void(0);"  id="upSourceFile" >上传文件</a>
	        --%>
	    </div>
    </logic:equal>
    </div>
<div id="funcDiv" style="display:none">
<div class="hj-wzm-xq-all">
    	<div class="hj-zm-hxr-all" id="resumeDiv">
            	<logic:equal name="resumeForm" property="from" value="123">
            <div class="hj-zm-hxr-two">
            <%if(length2>0){ %>
            	<p>
            	<%if(length>0){ %>
            	第一志愿职位：
            	<%} %>
            	<logic:iterate id="pos"    name="resumeForm"  property="lastPos">
            	    <input type="radio" id="<bean:write  name="pos" property="zp_pos_id_encry" filter="true"/>" name="posInfo" checked="checked" disabled="disabled"
            	    onclick="Global.applyPositionInfo('<bean:write  name="pos" property="zp_pos_id_encry" filter="true"/>','<bean:write  name="pos" property="position" filter="true"/>')">
	                <bean:write  name="pos" property="position" filter="true"/>
 					<logic:notEqual value="" name="pos" property="place">-</logic:notEqual>
	                <bean:write  name="pos" property="place" filter="true"/>
	                </input>
	                <input id="lastPosition" type="hidden" value='<bean:write  name="pos" property="position" filter="true"/>' />
		        </logic:iterate>
            	<br />
        <%if(length>0){ %>
                   其它志愿职位：
                   <%} %>
				<logic:iterate id="pos"    name="resumeForm"  property="othPos">
	                <input type="radio" id="<bean:write  name="pos" property="zp_pos_id_encry" filter="true"/>" name="posInfo" 
	                onclick="Global.applyPositionInfo('<bean:write  name="pos" property="zp_pos_id_encry" filter="true"/>','<bean:write  name="pos" property="position" filter="true"/>')"/>
	                <bean:write  name="pos" property="position" filter="true"/>
	                <logic:notEqual value="" name="pos" property="place">-</logic:notEqual>
	                <bean:write  name="pos" property="place" filter="true"/>
	                </input>&nbsp;&nbsp;
		        </logic:iterate>
                </p>
                <%} %>
            </div>
           </logic:equal>
           <div class="hj-zm-hxr-three">
            
            
              <div class="hj-zm-hxr-three-top" style="padding-bottom: 10px;">
                
                 
                 <div class="bh-clear"></div>
                <div class="hj-zm-hxr-three-yi">
                <h2><img id="a01img" src="/module/recruitment/image/jianhao.png" style="vertical-align:middle;padding-left:25px;"  onclick="Global.showOrCloseArea('a01')"/>&nbsp;<span style="vertical-align:middle;"><bean:message key="gz.report.baseinfomation"/></span></h2>
                <div id="a01" style="display:block;">
                <%
                ArrayList fieldSetList=resumeForm.getFieldSetList();	
				HashMap resumeBrowseSetMap=resumeForm.getResumeBrowseSetMap();
				HashMap setShowFieldMap=resumeForm.getSetShowFieldMap();
				String  zp_pos_id=resumeForm.getZp_pos_id();
				String resumeid=resumeForm.getResumeid();
				ArrayList a01InfoList=(ArrayList)resumeBrowseSetMap.get("a01");
				if(a01InfoList==null)
					a01InfoList=new ArrayList();
				out.print("<div style='float:left;width:70%;'>");
				out.print("<table width='100%' border='1' cellpadding='0' cellspacing='0' style='line-height:20px;margin-top:10px;padding-bottom:10px;padding-left:10px;'>");
				boolean newline = false;
	      		int count = 0;
	      		out.print("<tr><td colspan='2'  style='padding-left: 25px;'>");
	      		for(int i=0;i<a01InfoList.size();i++)
		      	{
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String value=((String)abean.get("value")).trim();
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		if("a0101".equalsIgnoreCase(itemid))
		      			out.print("<span>"+value+"</span><br/>");
		      	}
	      		out.print("</td></tr>");
	      		out.print("<tr><td colspan='2'  style='padding-left: 25px;'>");
				for(int i=0;i<a01InfoList.size();i++)
		      	{
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String value=((String)abean.get("value")).trim();
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		if("a0107".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			
			      			out.print(value);
			      			count++;
		      			}
		      		}
		      		if("a0127".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			
			      			out.print(value);
			      			count++;
		      			}
		      		}
		      		if("a0111".equalsIgnoreCase(itemid)){
		      			newline = true;
		      			if(StringUtils.isNotEmpty(value)) {
			      			if(count>0)
				      			out.print("&nbsp;&nbsp;|&nbsp;&nbsp;");
			      			count++;
			      			if(value != null && !"".equals(value)){
				      			String[] str = value.split("-");
				      			out.print(str[0]+"年"+str[1]+"月生");
			      			}
		      			}
		      		}
		      	}
				out.print("</td></tr>");
				int itemdescLen = 0;
				for(int i=0; i<a01InfoList.size(); i++){
					LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemdesc=(String)abean.get("itemdesc");
		      		if(itemdescLen < itemdesc.length())
		      			itemdescLen = itemdesc.length();
				}
				for(int i=1;i<fieldSetList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
					String setid=(String)abean.get("fieldSetId");
		      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
		      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
		      		if(dataList==null)
	                   {
	                       continue;
	                   }
	                      
	               for(int n=0;n<dataList.size();n++)
	               {
	               		 if(n!=0)
	               		 {
		                 }
	                     if(showFieldList!=null)
	                     {
	         				for(int s=0; s<showFieldList.size(); s++){
	         					LazyDynaBean abean1=(LazyDynaBean)showFieldList.get(s);
	         		      		String itemdesc=(String)abean1.get("itemdesc");
	         		      		if(itemdescLen < itemdesc.length())
	         		      			itemdescLen = itemdesc.length();
	         				} 
	                     }
	               }
		      	}
		      	for(int i=0;i<5&&i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
					String value=((String)abean.get("value")).trim();
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		
		      		if(itemid != null && "A0101,A0107,A0111,A0127".contains(itemid.toUpperCase()))
		      		    continue;
		      		out.print("<tr><td style='white-space: nowrap;' align='right' width='"+itemdescLen*20+"px'>"+itemdesc+"：</td>");
           			out.print("<td>"+value+"</td></tr>");
		      		 
		      	}
		      	for(int i=5;i<a01InfoList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)a01InfoList.get(i);
		      		String itemid = (String)abean.get("itemid");
		      		String itemdesc=(String)abean.get("itemdesc");
		      		String codesetid=(String)abean.get("codesetid");
		      		String viewvalue=(String)abean.get("viewvalue");
					String value=(String)abean.get("value");
		      		if(!codesetid.equals("0"))
		      			value=viewvalue;
		      		
		      		if(itemid != null && "A0101,A0107,A0111,A0127".contains(itemid.toUpperCase()))
		      		    continue;
		      		out.print("<tr><td style='white-space: nowrap;' align='right' width='"+itemdescLen*20+"px'>"+itemdesc+"：</td>");
                    out.print("<td>"+value+"</td></tr>");
	                
		      	}
		      	out.print("</table>");
		      	out.print("</div>");
		      	out.print("<div class='hj-zm-hxr-yi-right'>");
         		%>
        		<hrms:ole name="resumeForm" dbpre="${resumeForm.nbase}" a0100="resumeid" scope="session" height="120" width="85"/>
        		<%
        		out.print("</div>");
		      %>
		      </div>
		      </div>
		      <%
		      	for(int i=1;i<fieldSetList.size();i++)
		      	{
		      		LazyDynaBean abean=(LazyDynaBean)fieldSetList.get(i);
					String setid=(String)abean.get("fieldSetId");
		      		String setdesc=(String)abean.get("fieldSetDesc");
		      		ArrayList dataList=(ArrayList)resumeBrowseSetMap.get(setid.toLowerCase());
		      		ArrayList showFieldList=(ArrayList)setShowFieldMap.get(setid.toLowerCase());
	      			out.print("<div class='bh-clear'></div>");
	      			out.print("<div class='hj-zm-hxr-three-three'>");
		      %>
		      <h2><img id="<%=setid %>img" style="vertical-align:middle;padding-left:25px;"  src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('<%=setid %>')"/>&nbsp;<span style="vertical-align:middle;"><%=setdesc%></span></h2>
		      <div id="<%=setid %>" style="display:block;">
		      <%
                   if(dataList==null)
                   {
                       continue;
                   }
                      
               for(int n=0;n<dataList.size();n++)
               {
               		 if(n!=0)
               		 {
	                 }
                     LazyDynaBean a_bean=(LazyDynaBean)dataList.get(n);	
                     
                   if(showFieldList!=null)
                   {
                	   out.println("<table width='100%' border='1' cellpadding='0' cellspacing='0' style='line-height:20px;margin-top:10px;padding-bottom:10px;padding-left:10px;'>");
               		for(int j=0;j<showFieldList.size();j++)
               		{
               			LazyDynaBean aa_bean=(LazyDynaBean)showFieldList.get(j);
               			String itemid=(String)aa_bean.get("itemid");
               			String itemtype=(String)aa_bean.get("itemtype");
               			String itemdesc=(String)aa_bean.get("itemdesc");
               			String itemmemo=(String)aa_bean.get("itemmemo");
               			String value=(String)a_bean.get(itemid);
               		//	if(itemdesc.length()==2)
    		      	//		itemdesc="&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(0)+"&nbsp;&nbsp;&nbsp;"+itemdesc.charAt(1);
               			itemmemo = itemmemo.replace("\r\n","<br>");
               			if(value==null||value.equals(""))
               			   value="&nbsp;";
               			out.println("<tr style='text-align:right;vertical-align:top;'>");
               			out.print("<td width='"+itemdescLen*20+"px'>");
               			out.print(itemdesc+"：");
               			out.print("</td>");
               			out.print("<td align='left'>");
               			value=value.replace(" ","&nbsp;");
               			value=value.replace("\n","<br>");
	           			out.print(value);
	           			out.print("</td>");
	           			out.println("</tr>");
               		}
               		out.println("</table>");
          		   }
                   if(n!=dataList.size()-1)
                       out.println("<div style='border-bottom:1px #c5c5c5 dashed;'></div>");
                   
                }
       
              %>
               <%
               out.println("</div>");
			     }
			     %>
			     </div>
			     <logic:equal name="resumeForm" property="isAttach" value="1">
			     <div class='bh-clear'></div>
			     <div class='hj-zm-hxr-three-three'>
				 <h2 style="margin-top: 10px"><img id="uploadimg" style="vertical-align:middle;padding-left:25px;" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('upload')"/>&nbsp;<span style="vertical-align:middle;"><bean:message key="hire.resume.attach"/></span></h2>
                 <div id="upload" style="padding-left:40px;display:block;">
   				<TABLE  cellPadding=0 style="width:90%" align=center border=0 class="table" id="table">
    				<%
					String nodeid="";
					String createuser = "";
					String createtime = "";
					ArrayList uploadFIleList = resumeForm.getUploadFileList();
					for (int k = 0; k < uploadFIleList.size(); k++) {
						LazyDynaBean a_bean = (LazyDynaBean) uploadFIleList.get(k);
						String temnode = (String) a_bean.get("linkid");
						String title = (String) a_bean.get("title");//显示标题
						String createTime = (String) a_bean.get("createTime");//创建时间
						String createUser = (String) a_bean.get("createUser");//创建人
						String nodeName = (String) a_bean.get("nodename");//环节名称
						String imageUrl = (String) a_bean.get("imageUrl");//图片地址
						String id = (String) a_bean.get("id");//文件主键id
						String filePath = (String) a_bean.get("path");//文件绝对路径
						String filename = (String) a_bean.get("fileName");
						String encryptFileName = (String) a_bean.get("encryptFileName");
						String preview = (String)a_bean.get("preview");//是否可以预览
						//预览插件有问题，暂时去掉预览功能
						preview = "";
						String previews = (String)a_bean.get("previews");//是否有预览权限
						String seq = (String)a_bean.get("seq");//环节序号
						
						String tem = "";
						if("display:block;".equals(preview)&&"display:block;".equals(previews))
							tem = "";
						else
							tem = "display:none;";
						String download = (String)a_bean.get("download");//是否有下载权限
						String del = (String)a_bean.get("del");//是否有删除权限
						
						if(k==0){//个人简历
							out.println("<tr id='"+seq+"' name='newlink' style='padding-left:20px;'>");
							out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
							out.println("<span id='"+temnode+"'>"+nodeName+"</span>");
							out.println("<br/><div style='border-bottom:1px #c5c5c5 dashed;'></div><br/><div>"+title+"</div>");
							out.println("<ul id='"+id+"' name='aa'>");
							out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;width:300px;overflow:hidden\">");
							out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");

							if("display:none;".equalsIgnoreCase(download))
								out.println("<a><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
							else
								out.println("<a href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
							
							out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
										+(String) a_bean.get("fileSize")+ "&nbsp;&nbsp;");
							out.println("<a name='showfile' href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;");
							//out.println("<a href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"' style='"+download+"'>下载</a>&nbsp;&nbsp;");
							out.println("<a target='_Blank' href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"&displayfilename="+URLEncoder.encode(filename)+"' style='"+download+"'>下载</a>&nbsp;&nbsp;");
							out.println("<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></li></ul>");
						}else{
							if(temnode.equals(nodeid)&&createUser.equals(createuser)/* &&createTime.equals(createtime) */){//同一环节  同一人  上传时间相同
								out.println("<ul id='"+id+"' name='aa'>");
								out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;padding-top:8px;width:300px;overflow:hidden\">");
								out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");

								if("display:none;".equalsIgnoreCase(download))
									out.println("<a><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								else
									out.println("<a href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								
								out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+ (String) a_bean.get("fileSize")+"&nbsp;&nbsp; <a name='showfile' href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;");
								out.println("<a  target='_Blank' href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"&displayfilename="+URLEncoder.encode(filename)+"' style='"+download+"'>下载</a>&nbsp;&nbsp;");
								out.println("<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></li></ul>");
							}else if(!temnode.equals(nodeid)){//不是同一个环节
								out.println("</td></tr>");
								out.println("<tr  id='"+seq+"' name='newlink'>");
								out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
								out.println("<span id='"+temnode+"'>"+nodeName+"</span>");
								out.println("<br/><div style='border-bottom:1px #c5c5c5 dashed;'></div><br/><div>"+title+"</div>");
								out.println("<ul id='"+id+"' name='aa'>");
								out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;width:300px;overflow:hidden\">");
								out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");

								if("display:none;".equalsIgnoreCase(download))
									out.println("<a><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								else
									out.println("<a href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								
								out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile'  title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+(String) a_bean.get("fileSize")+ "&nbsp;&nbsp;<a name='showfile' href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;");
								out.println("<a target='_Blank' href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"&displayfilename="+URLEncoder.encode(filename)+"' style='"+download+"'>下载</a>&nbsp;&nbsp;");
								out.println("<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></li></ul>");
							}else{//同一个环节  上传人或上传日期不一致
								out.println("</td></tr>");
								out.println("<tr>");
								out.println("<td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+(String) a_bean.get("createUserName")+createTime+temnode+"'>");
								out.println("<br/>"+title);
								out.println("<ul id='"+id+"' name='aa'>");
								out.println("<li style=\"float: left;margin: 10px 5px 0px 5px;padding-top:8px;width:300px;overflow:hidden\">");
								out.println("<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">");

							    if("display:none;".equalsIgnoreCase(download))
									out.println("<a><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
								else
									out.println("<a href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"'><img align='left' width='32px' height='32px' src='"+imageUrl+"'/></a></li>");
									
								out.println("<li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\"><div class='divFile' title='"+filename+"'>"+ filename + "</div><div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"
											+ (String) a_bean.get("fileSize")+"&nbsp;&nbsp; <a name='showfile' href='/system/options/customreport/displayFile.jsp?filename="+encryptFileName+"&filepath="+filePath+"' style='margin-left:5px;display:inline;"+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;");
								out.println("<a target='_Blank' href='/servlet/vfsservlet?fromjavafolder=true&fileid="+filePath+"&displayfilename="+URLEncoder.encode(filename)+"' style='"+download+"'>下载</a>&nbsp;&nbsp;");
								out.println("<a href='javascript:void(0);' onclick='delFile(\""+id+"\",\""+filePath+"\")' style='"+del+"'>删除</a></div></li></ul></ul></li>");
							}
						}
						nodeid=temnode;
						createuser = createUser;
						createtime = createTime;
					}%>
				      
               	 </TABLE>
              	 </div>
              	 </div>
     			</logic:equal>
     			<div class="bh-clear"></div>
     			<div class='hj-zm-hxr-three-three'>
     			<h2 style="margin-top: 10px"><img id="evaluationimg" style="vertical-align:middle;padding-left:25px;"  src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('evaluation')"/>&nbsp;<span style="vertical-align:middle;">简历评价</span></h2>
                 <div id="evaluation" style="display:block;padding-left: 40px;">
					<table width="90%">
                 		<tr id="tr1">
                 			<td style="vertical-align: middle;" align="left" width="100px">
								<span>我的评价</span>
							</td>
                 			<td align="left" width="90%">
									<html:hidden styleId="score" name="resumeForm" property="evaluationBean.score" />
									<div style="float: left;width: 250px;"><span id="starlist" style="display: inline"></span></div>
									<div style="padding-right: 10px;padding-top: 3px;" id="textMsg">
										<span>&nbsp;<a href="javascript:void(0);" onclick="Global.ReEvaluation()" style="font-family: '微软雅黑';">重新评价</a></span>
									</div>
                 			</td>
                 		</tr>
						<tr id="tr2">
							<td style="margin-bottom: 5px" valign="top" align="left" width="100">&nbsp;</td>
							<td align="left" style="vertical-align: middle;">
								<div style="width: 100%" id="div_r">
									<div id="content_r" style="width: 100%;margin-top:8px;padding:0;word-break: break-all;word-wrap: break-word;">
										<%--<bean:write name="resumeForm" property="evaluationBean.content"/>
										--%><bean:define id="mycontent" name="resumeForm" property="evaluationBean.content"></bean:define>
										<%
											String content = (String)mycontent;
										%>
										<%=content %>
									</div>
								</div>
								<div style="width: 100%;" id="div_w">
									<div>
										<textarea id="addContent" rows="" cols="" style="width: 100%; height: 100px; margin-left: 0px; word-break: break-all; overflow: auto; margin-bottom: 10px;border:1px #c5c5c5 solid;"></textarea>
									</div>
									<div style="float: right;">
										<input type="button" class="addButton" value="发布评价" onclick="Global.addEvaluation()" style="cursor: pointer;" />
									</div>
								</div>
							</td>
						</tr>
						<logic:iterate id='element' name='resumeForm' property='evaluation'>
							<tr><td colspan="2"><p style='word-wrap:break-word;word-break:break-all;line-height:normal;'></p></td></tr>
							<tr>
								<td style="vertical-align: top;padding-top: 15px;" align="left" width="100px">
									<span style="color: #1b4a98;"><bean:write name="element" property="username"/></span>的评价
								</td>
								<td align="left" style="width: 700px;">
									<div style="width: 100%">
										<logic:equal value="0" name="element" property="score">
											<div style="width: 100%;padding-top: 15px;"><a href=" javascript:void(0);" onclick="Global.remind(this,'<bean:write name="element" property="nbase"/>','<bean:write name="element" property="a0100"/>'
											,'<bean:write name="element" property="nbase_object"/>','<bean:write name="element" property="a0100_object"/>','<bean:write property="infoBean.z0301" name="resumeForm"/>')">提醒<bean:write name="element" property="ta"/>评价</a></div>
										</logic:equal>
										<logic:notEqual value="0" name="element" property="score">
										<table width="100%" style="padding-top: 10px;">
											<tr>
												<td>
													<div class="main_div">
														<div showStart="" style="width:250px;">
															<span class="showStartScore"  onmouseout="return false"></span>
															<span class="startScore" style="display: none;"><bean:write name="element" property="score"/></span>
														</div>
													</div>
												</td>
											</tr>
											<tr>
												<td>
													<div id="content_r" style="width: 100%;padding:0;word-break: break-all;word-wrap: break-word;">
														<bean:define id="mycontent" name="element" property="content"></bean:define>
														<%
															content = (String)mycontent;
														%>
														<%=content %>
													</div>
												</td>
											</tr>
										</table>
										</logic:notEqual>
									</div>
								</td>
							</tr>
						</logic:iterate>
					</table>
                 </div>
                 </div>
                 <div class="bh-clear"></div>
                 <!-- 显示操作日志 20170410 -->
                 <div class='hj-zm-hxr-three-three'>
				 <h2 style="margin-top: 10px"><img id="logdivimg" style="vertical-align:middle;padding-left:25px;" src="/module/recruitment/image/jianhao.png" onclick="Global.showOrCloseArea('logdiv')">&nbsp;<span style="vertical-align:middle;">审批过程</span></h2>
				 <div id="logdiv" style="display:block;padding-left: 40px;"></div>
             	 </div>
				 <div class="bh-clear"></div>
             </div>   
        </div>
    </div>
    </div>
    </div>
    <div id='chajiana' ></div> 
</body>
<script type="text/javascript">
var ieflag = false;
Ext.getDom('resumeDiv').style.marginLeft="5px";
function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 } 
    var title = "";
Ext.onReady(function(){
	if(""==Ext.getDom("nextResumeid").value){
		var obj = parent.document.getElementById("window.frames['ifra'].Global.nextResume();");
		if(obj){
			if(Ext.isIE)			
				obj.removeNode(true);
			else
				obj.remove();
		}
	}
	if(""==Ext.getDom("lastResumeid").value){
		var obj = parent.document.getElementById("window.frames['ifra'].Global.lastResume();");
		if(obj){
			if(Ext.isIE)			
				obj.removeNode(true);
			else
				obj.remove();
		}
	}
	var map = new HashMap();
	map.put("a0100", "<%=resumeid1%>");
	map.put("z0301", "<%=zp_pos_id1%>");
	map.put("select","select");
	Global.searchOperationLog(map);
	if(Ext.isIE){
		Global.initCard();
		ieflag = true;
   	}
	if(!ieflag){
		var showfile = document.getElementsByName("showfile");
		for(var i=0;i<showfile.length;i++){
			showfile[i].style.display="none";
		}
	}
    Ext.widget('viewport',{
    	layout:'fit',
    	padding:"0 0 0 0",
    	style:'backgroundColor:white',
    	items:[{
    			  xtype:'panel',
    			  id:'view_panel',
    			  html:"<div id='topPanel'></div>",
    			  border:false
    			}]
    });
    document.getElementById('header').style.display="block";
    document.getElementById('topPanel').appendChild(document.getElementById('funcDiv'));
    document.getElementById('funcDiv').style.display="block";
    var view_panel = Ext.getCmp('view_panel');
    view_panel.setAutoScroll(true);
    var winHeight =parent.document.body.clientHeight;
    view_panel.setHeight(winHeight);
    
	var status = '<bean:write  name="resumeForm" property="status"/>';
	var acceptPA = document.getElementById("acceptPA");
	if(acceptPA!=null)
	{
		if(status=="1"||status=="2")
		{
			acceptPA.setAttribute("onclick","");
			acceptPA.setAttribute("style","color:gray");
		}else{
			acceptPA.setAttribute("onclick","Global.acceptPositionApply()");
		}
	}
	var rejectPA = document.getElementById("rejectPA");
	if(rejectPA!=null)
	{
		if(status=="1"||status=="2")
		{
			rejectPA.setAttribute("onclick","");
			rejectPA.setAttribute("style","color:gray");
		}else{
			rejectPA.setAttribute("onclick","Global.rejectPositionApply()");
		}
	}
    //判断是那个环节
    var nodeIdCmp = Ext.get("node_id");
    if (!Ext.isEmpty(nodeIdCmp)){
        var nodeId = nodeIdCmp.getValue().substring(0,2);
		if(nodeId=="04")
			title = "上传测评结果";
	    else if(nodeId=="05")
	    	title = "上传面试评价记录";
	    else if(nodeId=="06")
	    	title = "上传背景调查资料";
	    else if(nodeId=="07")
	    	title = "上传录用审批附件";
	    else if(nodeId=="09")
	    	title = "上传体检结果";
    
	    //Ext.get("upSourceFile").setHTML(title);
	    if(!Ext.isEmpty(title)){
		    var rootpath = "<%=rootPath%>";
			   Ext.create("SYSF.FileUpLoad",{
					renderTo:"upSourceFile",
					upLoadType:3,
					uploadUrl:rootpath,
					savePath:rootpath,
					buttonText:'<a href="javascript:void(0);">'+title+'</a>',
					fileSizeLimit:'20MB',
					fileExt:"*.doc;*.docx;*.xlsx;*.xls;*.rar;*.zip;*.ppt;*.jpg;*.jpeg;*.png;*.bmp;*.txt;*.wps;*.pptx;*.pdf",
					width:105,
					height:15,
					isTempFile:false,
		            VfsModules:VfsModulesEnum.ZP,
		            VfsFiletype:VfsFiletypeEnum.other,
		            VfsCategory:VfsCategoryEnum.other,
					success:uploadSuccess
				});
		  }
			
	    //停用setPosition();
		//获取所有ul下name为aa的元素
		var childCount = Ext.query("ul[name='aa']");
		//调整简历显示区域的高度
		if(childCount==0&&!Ext.isEmpty(Ext.get("upload")))
			Ext.get("upload").setStyle("padding-bottom","0px");
    }
//设置评价显示
	var score = '<bean:write name="resumeForm" property="evaluationBean.score"/>';
	if(score==0)
	{
		Ext.getDom('score').value="-1";
		Ext.getDom("div_r").style.display="none";
		Ext.getDom("div_w").style.display="block";
		Ext.getDom("textMsg").style.display="none";
	}else if(score==-1){
		Ext.getDom("tr1").style.display="none";
		Ext.getDom("tr2").style.display="none";
	}else{
		Ext.getDom("div_r").style.display="block";
		Ext.getDom("div_w").style.display="none";
	}
	
	<% if(userView == null || userView.getA0100() == null || userView.getA0100().equals("")) { %>
	Ext.getDom("tr1").style.display="none";
	Ext.getDom("tr2").style.display="none";
	<% } %>
		initstar('starlist');
	//渲染上传按钮
	window.parent.Ext.getDom("operatediv").style.display = "";
	window.parent.resume_me.uploadFile();
	
});	
/**
 * 设置返回和上传操作菜单位置
 */
function setPosition(){
	var target = Ext.getDom("upSourceFile");
	var reback = Ext.getDom("temps");
	//解决操作栏不按规定样式显示问题（采用绝对定位）
	if(<%=total%>==0){//没有操作工具栏
		target.style.position = "absolute";
		if(isIE())//IE 
			target.style.left = parseInt(Ext.getDom("headPanel").offsetLeft)+200+"px";
		else if(Ext.isSafari)//safari
			target.style.left =  parseInt(Ext.getDom("headPanel").offsetLeft)+200+"px";

		reback.style.position = "absolute";
		reback.style.left = parseInt(target.offsetLeft)+100+title.length+"px";
		Ext.get("showHide").hide();
	}else{
		Ext.get("view_panel_header").setHeight("50px");
		var last = Ext.getDom("temp<%=total-1%>");
		Ext.getDom("tDiv").style.display="inline";
		var name = last.innerHTML.substring(last.innerHTML.indexOf(">")+1,last.innerHTML.indexOf("</"));
		var t = last.offsetTop;
		var l = last.offsetLeft;

		reback.style.position = "absolute";
		if(Ext.isEmpty(title))
			reback.style.left = parseInt(l)+60+"px";
		else{
			target.style.position = "absolute";
			if(isIE())//IE 
				target.style.left = parseInt(l)-65+2*name.length+"px";
			else if(Ext.isSafari)//safari
				target.style.left = parseInt(l)+2*name.length+10+"px";
				
			target.style.top = "8px";
			reback.style.left = parseInt(target.offsetLeft)+100+title.length+"px";
		}
		reback.style.top = "8px";
	}
}
	uploadSuccess=function(list){
		var file_list = new Array();
		Ext.each(list,function(obj,index){
			file_list.push({"fileid":obj.fileid,"filename":obj.filename,"localname":obj.localname});
		});
		var linkid = Ext.getDom("link_id").value;
		var a0100 = Ext.getDom("a0100").value;
		var map = new HashMap();
		map.put("linkid",linkid);
		map.put("a0100",a0100);
		map.put("flag","1");
		map.put("nbase","<%=nbase %>");
		map.put("file_list",file_list);
		Rpc({
			functionId : 'ZP0000002363',
			async:false,
			success : judSuccess
		}, map);
	}
	function judSuccess(response){
		//Ext.getCmp('fileUp').close();
		var result = Ext.decode(response.responseText);
		var files = result.uploadFileList;
		if(!Ext.isEmpty(result.isOK)){
			var resId = "";
			var html = "";
			var tem = "display:none;";
			var download = "";
			Ext.each(files,function(file,index){
				download = file.download;
				/*if(file.preview=="display:block;"&&file.previews=="display:block;")
					tem = "";*/
				html = "<ul id='"+file.id+"' name='aa'><li style=\"float: left;margin: 10px 5px 0px 5px;padding-top:8px;width:300px;overflow:hidden\">"
						+"<ul style='margin-left:15px;padding-left:20px;display:flex;overflow:hidden;position:relative;'><li style=\"float: left;position:absolute\">"
						+"<a href='/DownLoadCourseware?url="+file.path+"'>"
						+"<img align='left' width='32px' height='32px' src='"+file.imageUrl+"'/></a></li><li style=\"float: left;margin-left:50px;margin-top:-2px;display:inline;\">"
						+"<div class='divFile'  title='"+file.fileName+"'>"+ file.fileName + "</div>"
						+"<div style='height:5px'><br></div><div style='float:left;display:inline;margin-top:5px;'>"+ file.fileSize+"&nbsp;&nbsp;"
						+" <a name='showfile' href='/system/options/customreport/displayFile.jsp?filename="+file.encryptFileName+"&filepath="+file.path+"'style='margin-left:5px;"
						+tem+"' target='_blank'>预览</a>&nbsp;&nbsp;<a href='/DownLoadCourseware?url="+file.path+"' style='"+download+"'>下载</a>&nbsp;&nbsp;"
						+"<a href='javascript:void(0);' onclick='delFile(\""+file.id+"\",\""+file.path+"\")' style='"+file.del+"'>删除</a></div></li></ul></ul></li>";

				resId = file.createUserName+file.createTime+file.linkid;//td的id，组成规则  a0100+createTime+linkid
				if(!Ext.isEmpty(Ext.get(resId))){//找到对应的td元素，说明是此人已在该环节上传过文件，即有对应的文件记录，此时直接在该td下追加ul即可
					Ext.get(resId).createChild(html);
				}else if(Ext.isEmpty(Ext.get(file.linkid))){//该环节尚未有简历评价附件
					html = "<tr id='"+file.seq+"'><td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+resId+"'><br/><span id='"+file.linkid+"'>"+file.nodename
							+"</span><br/><div style='border-bottom:1px #c5c5c5 dashed;'></div><br/><div>"
							+file.title+"</div>"+html+"</td></tr>";
				}else{//该环节已有简历附件，但暂无当前人员上传的附件
					html = "<tr><td class='hj_zhaopin_list_tab_titleone_1' width='10%' id='"+resId+"'><br/><div>"+file.title+"</div>"+html+"</td></tr>";
				}
				
				if(Ext.isEmpty(Ext.get(resId))){//处理该环节未有建立附件和已有简历附件但无当前人员上传的附件
					var allLinkTr = Ext.query("*[ name=newlink]");
					var i=0;
					if(allLinkTr.length>0){
						Ext.each(allLinkTr,function(tr){
							if(parseInt(tr.id)>parseInt(file.seq)){//已有附件环节序号大于新增环节序号，则插入到该元素之前
								Ext.get("table").createChild(html,Ext.get(tr.id));
							}else
								i += 1;
						});
						if(i == allLinkTr.length)//当前环节处于所有已有附件环节之后
							Ext.get("table").createChild(html);
					}else//没有已上传的简历附件
						Ext.get("table").createChild(html);
				}
			});
		}
	}
	//文件预览
	function displayFile(filename,filepath){
		window.location.href="/system/options/customreport/displayFile.jsp?filename="+filename+"&filepath="+filepath;
	}
	 /**
	    * 删除简历附件
	    */
	   function delFile(id,path){
		   Ext.Msg.confirm("提示信息","确认删除此文件吗?",function(res){
				if(res=="yes"){
				   	var map = new HashMap();
				   	map.put("id", id);
				   	map.put("path", path);
				   	Rpc( {
				   		functionId : 'ZP0000002362',
				   		async:false,
				   		success : delSuccess
				       }, map);
				}
		   });
		   }
		   function delSuccess(response){
		   	var result = Ext.decode(response.responseText);
		   	var isOK = result.isOK;
		   	if(!isOK)
		   		Ext.Msg.alert("提示信息","文件删除失败");
		   	else{
				var id = result.id;
				var count = Ext.get(id).parent().query("ul[name='aa']").length;//得到td下所有第一层的ul个数
				var span = Ext.get(id).parent().query("span").length;//获取td下span（用于判断当前操作tr是否为第一行带标题的tr）
				var prevTr = Ext.get(id).parent().parent().prev();
				var nextTr = Ext.get(id).parent().parent().next();
				if(count==1){
					if(span==0||Ext.isEmpty(nextTr))//当前操作节点不是第一行带标题节点(当前tr不包括标题、没有下一个兄弟节点tr)
						Ext.get(id).parent().remove();//删除td
					else{//操作的是第一行带标题的tr，此时若该环节下没有其他上传的文件，方可删除整个tr否则，得保留标题
						var nexSpan = nextTr.query("span").length;
						if(nexSpan==0){//说明该环节尚有其他已上传的文件
							Ext.get(id).prev().remove();//移除显示某人时间上传的附件
							Ext.get(id).remove();
						}
						else//表示下一个兄弟节点tr是另一个环节，从而说明当前操作的文件所在tr已是最后一个文件
							Ext.get(id).parent().remove();//删除td
					}
				}else{
					Ext.get(id).remove();//删除当前ul
				}
				if(Ext.get("upload").query("ul[name='aa']").length==0){//所有文件都删除后清空table元素
					Ext.get("table").remove();
					Ext.get("upload").createChild('<TABLE cellSpacing="15px" cellPadding=0 style="width:100%" align=center border=0 class="table" id="table"></TABLE>');
				}			
		   	}
	   }
</script>
