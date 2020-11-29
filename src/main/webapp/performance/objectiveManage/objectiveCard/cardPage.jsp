<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Frameset//EN" "http://www.w3.org/TR/html4/frameset.dtd">
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>

<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.performance.objectiveManage.ObjectCardForm,
				org.apache.commons.beanutils.LazyDynaBean,
				com.hrms.frame.dao.RecordVo,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.SystemConfig,
				com.hrms.hjsj.sys.Constant,
				com.hrms.struts.valueobject.UserView,
				com.hrms.struts.constant.WebConstant,
				com.hjsj.hrms.utils.PubFunc"
				%>
<%
	String isEpmLoginFlag="0";	
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  isEpmLoginFlag=(String)userView.getHm().get("isEpmLoginFlag"); 
	  isEpmLoginFlag = (isEpmLoginFlag==null||isEpmLoginFlag.equals(""))?"0":isEpmLoginFlag;
	  hcmflag=userView.getBosflag();
	}
	String a0 = PubFunc.encryption("0");
	
	
	ObjectCardForm objectCardForm=(ObjectCardForm)session.getAttribute("objectCardForm");
	String returnflag=objectCardForm.getReturnflag();
	if(returnflag==null)
		returnflag="";
	String url_extends="";
	if("8,10".contains(returnflag.trim())) // 需考虑returnflag=8的情况 lium
		url_extends="&returnflag=" + returnflag.trim();
	else
		url_extends="&returnflag=";
	
	String scoreManual=objectCardForm.getScoreManual();
	String objectSpFlag=objectCardForm.getObjectSpFlag();
	String importPositionField=objectCardForm.getImportPositionField();
	String importDeptField=objectCardForm.getImportDeptField();
	String body_id=objectCardForm.getBody_id();
	String un_functionary=objectCardForm.getUn_functionary();
	String currappuser=objectCardForm.getCurrappuser();
	String entranceType=objectCardForm.getEntranceType();
	String processing_state_all=objectCardForm.getProcessing_state_all();
	String grading_auto_saving=SystemConfig.getPropertyValue("grading_auto_saving");
	if(!grading_auto_saving.equals(""))
	{
	    if(grading_auto_saving.toLowerCase().endsWith("h"))
	    {
	        grading_auto_saving=Integer.parseInt(grading_auto_saving.substring(0,grading_auto_saving.length()-1))*60*60+"";
	    }else if(grading_auto_saving.toLowerCase().endsWith("m")){
	        grading_auto_saving=Integer.parseInt(grading_auto_saving.substring(0,grading_auto_saving.length()-1))*60+"";
	    }else {
	       if(grading_auto_saving.toLowerCase().endsWith("s")){
	         grading_auto_saving=grading_auto_saving.substring(0,grading_auto_saving.length()-1);
	       }   
	    }
	}
	String a0100=userView.getA0100();
	String userFullName=userView.getUserFullName();
	String object_id=objectCardForm.getObject_id();
	String isRejectFunc=objectCardForm.getIsRejectFunc();
	String clientName=objectCardForm.getClientName();
	ArrayList tlist = objectCardForm.getTabList()==null?new ArrayList():objectCardForm.getTabList();
	String appealObjectStr= objectCardForm.getAppealObjectStr();
	String realSpFlag=objectCardForm.getRealSpFlag();
    int height=tlist.size()*10;
	String seqCondition=objectCardForm.getSeqCondition();
	int currentlevel=0;
	if(body_id.equalsIgnoreCase("1"))
			currentlevel=1;
	if(body_id.equalsIgnoreCase("0"))
			currentlevel=2;
	if(body_id.equalsIgnoreCase("-1"))
			currentlevel=3;
	if(body_id.equalsIgnoreCase("-2"))
			currentlevel=4;
	
	String isShowHistoryTask=objectCardForm.getIsShowHistoryTask();
	
	
	String opt=objectCardForm.getOpt();
	Hashtable planParam=objectCardForm.getPlanParam();
	String scoreflag=(String)planParam.get("scoreflag");  //=2混合，=1标度(默认值=混合)
	String EvalOutLimitStdScore=(String)planParam.get("EvalOutLimitStdScore");  //评分时得分不受标准分限制True, False, 默认为 False;都加
	String targetMakeSeries=(String)planParam.get("targetMakeSeries");
	int level=1;
    if(targetMakeSeries!=null&&!targetMakeSeries.equals(""))
    {
       level=Integer.parseInt(targetMakeSeries);
    }
    String SpByBodySeq="False";
	if(planParam.get("SpByBodySeq")!=null)
		SpByBodySeq=(String)planParam.get("SpByBodySeq");
	String taskAdjustNeedNew=(String)planParam.get("taskAdjustNeedNew");
	String EvalCanNewPoint=(String)planParam.get("EvalCanNewPoint");
	String allowSeeLowerGrade=(String)planParam.get("allowSeeLowerGrade");
	String isAdjustPoint=objectCardForm.getIsAdjustPoint();
	String TargetCollectItem=(String)planParam.get("TargetCollectItem");
	String targetTraceEnabled=objectCardForm.getTargetTraceEnabled();
	String targetCollectItem2=objectCardForm.getTargetCollectItem();
	String model=objectCardForm.getModel();
	String mainbodyScoreStatus=objectCardForm.getMainbodyScoreStatus();
	String planStatus=objectCardForm.getPlanStatus();
	RecordVo per_objectVo=objectCardForm.getPer_objectVo();
	RecordVo per_planVo=objectCardForm.getPer_planVo();
	String returnURL=objectCardForm.getReturnURL();
	ArrayList adjustBeforePointList=objectCardForm.getAdjustBeforePointList();
	String allowLeadAdjustCard=objectCardForm.getAllowLeadAdjustCard();
	String workDiaryButton_html=objectCardForm.getWorkDiaryButton_html();
	String aurl = (String)request.getServerName();
	String trHeight="40";
	String divTop="45";
	if("hcm".equals(hcmflag)){
		divTop="57";
	}
	String divHeight="0";
	String buttonClass="mybutton";
	String importPre="info.appleal.state15";
	String report="info.appleal.state1";
	String pz="info.appleal.state8";
	String bp="info.appleal.state7";
	String bh="info.appleal.state10";
	int value=1;
	if(clientName.equals("zglt"))
	{
	     trHeight="62";
	     divTop="68";
	     divHeight="15";
	     buttonClass="mybuttonBig";
	     pz="info.appleal.state3";
	     bp="info.appleal.state1";
	    // bh="button.reject";
	     value=2;
	}
	if(clientName.equalsIgnoreCase("bjpt"))
	{
	   importPre="info.appleal.state16";
	   report="info.appleal.state7";
	} 
	int plan_Status=Integer.parseInt(planStatus);
	int txw=Integer.parseInt(objectCardForm.getTxw()); 
	String url_p=SystemConfig.getServerURL(request);
	  
	String creatCard_mail=objectCardForm.getCreatCard_mail();
	String evaluateCard_mail=objectCardForm.getEvaluateCard_mail();
	String isApprove=objectCardForm.getIsApprove();
	String grade_template_id_str=objectCardForm.getGrade_template_id_str();
	String isAllowAppealTrancePoint=objectCardForm.getIsAllowAppealTrancePoint();  //是否允许报批跟踪指标
	String includeOperateCloumn=objectCardForm.getIncludeOperateCloumn(); //是否包含操作列 0不包含 1包含
	String isAllowApproveTrancePoint=objectCardForm.getIsAllowApproveTrancePoint();
	String noApproveTargetCanScore = objectCardForm.getNoApproveTargetCanScore();
	ArrayList mainbodylist=objectCardForm.getMainbodylist();
	String objectCardGradeMembersJson=objectCardForm.getObjectCardGradeMembersJson();
	
	  String dbtype="1";
	  if(Sql_switcher.searchDbServer()== Constant.ORACEL)
	  {
	    dbtype="2";
	  }
	  else if(Sql_switcher.searchDbServer()== Constant.DB2)
	  {
	    dbtype="3";
	  }
	EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int version=userView.getVersion();
   if(license.equals("0"))
        version=100+version;
   int usedday=lockclient.getUseddays();
   String planid=objectCardForm.getPlanid();
   String objectid=objectCardForm.getObject_id();
   planid=PubFunc.encryption(planid);
   objectid=PubFunc.encryption(objectid);
   String planDescription=objectCardForm.getPlanDescription();
   
   returnURL=PubFunc.hireKeyWord_filter_reback(returnURL);
   String userName = userView.getUserName();
%>


<head>
		<!-- 
		已无此js文件       
        <script language="JavaScript" src="/js/pergrade.js"></script>
        -->
		<script type="text/javascript" src="../../../components/personPicker/PersonPicker.js"></script>
		<SCRIPT LANGUAGE=javascript src="/performance/objectiveManage/objectiveCard/objectiveCard.js"></SCRIPT>
		<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
		<script language="JavaScript" src="/js/wz_tooltip.js"></script>
		<script language="JavaScript" src="/js/function.js"></script>
		<script language="javascript" src="/ajax/command.js"></script>
		<script language="javascript" src="/ajax/constant.js"></script>
		<script language="javascript" src="/js/constant.js"></script>
        <script language="javascript" src="../../../components/js/resource_zh_CN.js"></script>
</head>



<script language="JavaScript">
var ViewProperties=new ParameterSet();
 var EvalOutLimitStdScore='<%=(EvalOutLimitStdScore.toLowerCase())%>'
 var old_itemid="";
 var old_p0400="";
 var old_obj;
 var old_class="";
 var status="${objectCardForm.status}"    //　０:分值模版  1:权重模版
 var perPointNoGrade="${objectCardForm.perPointNoGrade}"  //目标卡中引入的绩效指标是否设置了标度  0：  1：没有设置标度
 var noGradeItem="${objectCardForm.noGradeItem}"
 var isEntireysub="${objectCardForm.isEntireysub}"        //提交是否必填
 var aclientHeight=document.documentElement.clientHeight //document.body.clientHeight 在ie9下得不到值，可能跟<!DOCTYPE>的类型有关系
 var url_p="<%=url_p%>";
 var model="${objectCardForm.model}";
 var body_id="${objectCardForm.body_id}";
 var opt="${objectCardForm.opt}";
 var plan_id="${objectCardForm.planid}";
 var object_id="${objectCardForm.object_id}";
 var mdplan_id="${objectCardForm.mdplanid}";
 var mdobject_id="${objectCardForm.mdobject_id}";
 var IVersion=getBrowseVersion();
 var un_functionary="${objectCardForm.un_functionary}";
 var pendingCode="${objectCardForm.pendingCode}";
 var creatCard_mail="<%=(creatCard_mail.toLowerCase())%>";
 var evaluateCard_mail="<%=(evaluateCard_mail.toLowerCase())%>";
 var currentlevel=<%=currentlevel%>;
 var targetMakeSeries=<%=targetMakeSeries%>;
 var scoreflag='<%=scoreflag%>';
 var grade_template_id_str='<%=grade_template_id_str%>';
 var seqCondition='<%=seqCondition%>';
 var grading_auto_saving='<%=grading_auto_saving%>';

//下载目标卡模板
function downLoadTarget(model)
{
	var onlyPram = '${objectCardForm.onlyField}';	
	if(onlyPram==null || onlyPram.length<=0)
	{
		alert('系统没有指定唯一性指标,不能下载模板!');
		return;
	}

 	var hashvo=new ParameterSet();     
	hashvo.setValue("object_id",'${objectCardForm.mdobject_id}');
	hashvo.setValue("plan_id",'${objectCardForm.mdplanid}');
	hashvo.setValue("model",model);
	hashvo.setValue("body_id",'${objectCardForm.body_id}');
	hashvo.setValue("opt",'1');
	//下载模板时不导出评价人和签批人
	hashvo.setValue("searchOrBatch",'serch');
	
	var request=new Request({method:'post',asynchronous:false,onSuccess:showfile,functionId:'9028000288'},hashvo);    	  	
}
function showfile(outparamters)
{
  	var outName=outparamters.getValue("outName");
	outName = decode(outName);
	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
//导入目标卡
function importTarget(model)
{
	var onlyPram = '${objectCardForm.onlyField}';	
	if(onlyPram==null || onlyPram.length<=0)
	{
		alert('系统没有指定唯一性指标,不能导入!');
		return;
	}
	var target_url="/performance/objectiveManage/objectiveCard.do?br_import=link";
 	var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
	/* var return_vo= window.showModalDialog(iframe_url, "importExcel", 
	              "dialogWidth:550px; dialogHeight:260px;resizable:no;center:yes;scroll:no;status:no");	 */
	
	 // 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
    Ext.create("Ext.window.Window",{
    	id:'import_win',
    	width:550,
    	height:260,
    	title:'导入目标',
    	resizable:false,
    	modal:true,
    	autoScroll:true,
    	renderTo:Ext.getBody(),
    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>"
    }).show();
}
// 导入后回调。多浏览器兼容修改
function importTarget_ok(flag){
	if(flag) {
		Ext.getCmp('import_win').close();
		alert('导入成功!');
		var planid="<%=planid%>";//加密的
		var a0100="<%=objectid%>";//加密的
		var body_id="${objectCardForm.body_id}";
		var url="/performance/objectiveManage/objectiveCard.do?b_query=query&zglt=0<%=url_extends%>&entranceType=0&showHistoryTask=0&model="+model+"&opt=1&planid="+planid+"&object_id="+a0100+"&body_id="+body_id;
		document.objectCardForm.action=url;
		document.objectCardForm.submit();
	}
	
}

//删除目标任务(调整)
function editAdjustPoint(p0400,opt)
{
	
		var width=610;  
		var height=310;
		if(opt=='2'||opt=='3')
		{
			height=430;
		/*	if(opt=='2')
			{
				height+=<%=(adjustBeforePointList.size()*30)%>
			}*/
		}
		var infos="";
		var thecodeurl="/performance/objectiveManage/objectiveCard.do?b_intEdit=query`operate="+opt+"`p0400="+p0400; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		
		// 多浏览器兼容showModalDialog改为Ext.window形式 chent 20171226 add
		if(/msie/i.test(navigator.userAgent)){
			var returnValue= window.showModalDialog(iframe_url, infos, "dialogWidth:"+width+"px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:no;status:no");
			document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
			return ;
		} else {
		    Ext.create("Ext.window.Window",{
		    	id:'editadjustpoint_win',
	        	width:610,
	        	height:460,
	        	title:'调整/删除任务',
	        	resizable:false,
	        	modal:true,
	        	autoScroll:true,
	        	renderTo:Ext.getBody(),
	        	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+iframe_url+"'></iframe>",
	        	listeners:{
	        		'close':function(){
						document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
	        		}
	        	}
	        }).show();
		}
}
function editadjustpointWinClose() {
	Ext.getCmp('editadjustpoint_win').close();
}
function auto_saveScore()
{
   subScore("1",'<%=objectid%>','<%=planid%>',body_id,"4","1");
}
function importSuccess(outparamters)
{
		var info=getDecodeStr(outparamters.getValue("info"));
		if(info=='1')
		{
			document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=1&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
		}
		else
			alert("上期目标卡无合适的数据引入!");
}


  function returnOk(outparamters)
 {
	var info=getDecodeStr(outparamters.getValue("info"));
	var rurl="<%=returnURL%>";
	var operator=getDecodeStr(outparamters.getValue("operator"));
	if(info.length!=0)
	{
		alert(info);
		if(operator=='2')
		    document.getElementById("appeal").disabled=false;
	}
	else 
	{
	
		if(operator=='2')  //上报目标卡
		{
			<%if(clientName.equals("zglt")){%>
			   alert(APPEALSUCCESS2+"!");//交办成功//普天
			<%}else{%>
			   alert(APPEALSUCCESS1+"!");//交办成功//普天
			<%}%>
			if(rurl==null||rurl=='')
			    document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&fromopt=self&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=0&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
			else
			  go_back();
		}
		if(operator=='3')  // 3.批准目标卡
		{
		<%if(clientName.equals("zglt")){%>
		    alert(RATIFYSUCCESS2+"!");
		<%}else{%>
			alert(RATIFYSUCCESS+"!");
		<%}%>
		if(rurl==null||rurl=='')
			document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&fromopt=self&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=0&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
	    else
			go_back();
			
		}
		if(operator=='1')  
		{
			var __score=outparamters.getValue("_score");
			if(document.getElementById("_score"))
				document.getElementById("_score").innerHTML=__score;
			alert(Template_SAVESUCCESS+"!");
		}
		if(operator=='4') // 4.驳回
		{
		    <%if(clientName.equals("zglt")){%>
		       alert(REJECTSUCCESS+"!")
		    <%}else{%>
		      	alert(REJECTSUCCESS2+"!")
			<%}%>
			if(rurl==null||rurl=='')
		    	document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&fromopt=self&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=0&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";	
			else
		    	go_back();
		}
		if(operator=='5')
		{
			alert(INSPECTSUCCESS+"!");
		}
	}	
 }
 
 
 ///performance/objectiveManage/objectiveCard.do?b_query=query&model=1&opt=1&planid=518&object_id=010201&body_id=1
  function returnSign(outparamters)
  {
   	//	var url=document.location.href;
   	//	document.location.reload();
   		document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&model=${objectCardForm.model}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}&body_id=${objectCardForm.body_id}";
  }

  //返回主页面
  function go_back()
  {
        var isEpmLoginFlag = "<%=isEpmLoginFlag %>";
 
	  	    document.objectCardForm.action="<%=returnURL%>";
	  	     <% if(returnflag.equals("8")){ %>
	 	    if(isEpmLoginFlag=="1"){
		           parent.location='/templates/index/subportal.do?b_query=link';
		    }else{
		        if("hcm"=='<%=hcmflag%>'){
	        		document.objectCardForm.action="/templates/index/hcm_portal.do?b_query=link";	
	       	    	document.objectCardForm.target="il_body";     		
       			}else{
	        		document.objectCardForm.action="/templates/index/portal.do?b_query=link";	
	       	    	document.objectCardForm.target="il_body"; 		
       			}
		    

		    }
	  	    <% 
	  	    }
	  		else if(returnflag.equals("10")&&(opt.equals("1")||(request.getParameter("fromopt")!=null&&request.getParameter("fromopt").equalsIgnoreCase("self"))||objectCardForm.getReturnURL().trim().length()==0))
	  	    {
	  	    %>
	  	    document.objectCardForm.action="/general/template/matterList.do?b_query=link";	
	  	    document.objectCardForm.target="il_body";
	  	    
	  	    <%
	  	  	}
	  	    else if(!returnflag.equals("10")){ %>
	 		document.objectCardForm.target="${objectCardForm.target}";
	 		<% } %>
	 		
	 		document.objectCardForm.submit();
  }

 
 var j=0; 
function printP(planid,objectid,tabid)
{
  if(j==0)
  {
    if(!AxManager.setup("print", "CardPreview1", 0, 0, null, AxManager.cardpkgName))
        return;
  }
  var card= document.getElementById('CardPreview1'); 
  var cardobj = isLoad(card);
  if(cardobj==true){
  	printCard(planid,objectid,tabid);
  	j++;
  }else{
     var timer = setInterval(function(){ 
     j++;
  	var obj= document.getElementById('CardPreview1');  
  	var _obj = isLoad(obj);
 	if(_obj==true){ 		
  		printCard(planid,objectid,tabid);	
  		clearInterval(timer);
  	}else if(j==5){
  		alert("插件加载失败！");  		
  		j=0;
  		clearInterval(timer);
  	}    
  	},2000);
  }		  
}
function isLoad(obj){
	var flag = true;
	try{
		obj.SetNBASE("USR");
	}catch(e){
		flag = false;
	}
	return flag;
}
function printCard(planid,objectid,tabid){
		  initCard();
		  var arr=tabid.split(",");
		  var num=0;
		  var id="";
		  for(var i=0;i<arr.length;i++)
		  {
		    if(arr[i]==null||trim(arr[i]).length==0)
		       continue;
		    else
		       num++;
		  }
		  if(num>1)
		  {
		       Element.show('date_panel0');   
		       var expr_editor=$('date_box');
		       expr_editor.focus();
		       var tyle=document.getElementById('date_panel0').style;
		       tyle.position="absolute";
		       tyle.top=(event.y-<%=height%>-30)+"px";
		       tyle.left=(event.x)+"px";
		   		
		  }
		  else
		  {
		    id=replaceAllStr(tabid,",","");
		  }
		 if(id=='')
		    return;
		  var hashVo=new ParameterSet();
		  hashVo.setValue("id",id);
		  hashVo.setValue("plan_id",planid);
		  hashVo.setValue("a0100",objectid);
		var In_parameters="opt=1";
		var request=new Request({method:'post',asynchronous:false,onSuccess:showPrint,functionId:'9028000412'},hashVo);	
}
function addItem(planid,objectid)
{
   Element.hide('date_panel0');
   var hashVo=new ParameterSet();
   var obj = document.getElementById("selectBOX");
   var id="";
   for(var i=0;i<obj.options.length;i++)
   {
     if(obj.options[i].selected)
        id=obj.options[i].value;
   }
  hashVo.setValue("id",id);
  hashVo.setValue("plan_id",planid);
  hashVo.setValue("a0100",objectid);
//var In_parameters="opt=1";
var request=new Request({method:'post',asynchronous:false,onSuccess:showPrint,functionId:'9028000412'},hashVo);			
		  
}
function showPrint(outparamters)
{
   var dataflag=outparamters.getValue("d"); 
   var objid=outparamters.getValue("o"); 
   var tabid=outparamters.getValue("tabid"); 
   var obj = document.getElementById('CardPreview1');    
   if(obj==null)
   {
      alert("没有下载打印控件，请设置IE重新下载！");
      return false;
   }

   obj.SetCardID(tabid);
   obj.SetDataFlag(dataflag);
   obj.SetNBASE("USR");
   obj.ClearObjs();   
   obj.AddObjId(objid);
   try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
   obj.ShowCardModal();
   
}
function initCard()
{
      var rl = document.getElementById("hostname").href;     
      var aurl=rl;//tomcat路径
      var DBType="<%=dbtype%>";//1：mssql，2：oracle，3：DB2
      var UserName="<%=userName%>";   //登陆用户名暂时用su     
      var obj = document.getElementById('CardPreview1');   
      var superUser="1";
      var menuPriv="";
      var tablePriv="";
      if(obj==null)
      {
         return false;
      }
      obj.SetSelfInfo("1");//自助用户
      obj.SetSuperUser(superUser);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(menuPriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablePriv);  // 子集权限, 逗号分隔, 空表示全权         
      obj.SetURL(aurl);
      obj.SetDBType(DBType);
      obj.SetUserName(UserName);
      obj.SetUserFullName("su");
      obj.SetHrpVersion("<%=version%>");
      obj.SetTrialDays("<%=usedday%>","30");
}
  
  

function replaceAllStr(str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	    return str;
}  
function detail(object_id,plan_id)
{
  var thecodeurl="/performance/objectiveManage/objectiveCardMonitor/objective_state_list.do?b_detail=link`plan_id="+plan_id+"`object_id="+object_id; 
  var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
  var retvo= window.showModalDialog(iframe_url, null, 
					        "dialogWidth:600px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");			
  
}  
function untread()
{
    var isSendEmail=document.getElementsByName("isSendEmail");
    var xx="-1";
    if(isSendEmail[0])
    {
       if(isSendEmail[0].checked)
           xx='1';
    }
    var hashVo=new ParameterSet();
    hashVo.setValue("plan_id","${objectCardForm.planid}");
    hashVo.setValue("object_id","${objectCardForm.object_id}");
    hashVo.setValue("body_id","${objectCardForm.body_id}");
    hashVo.setValue("type","1");
    hashVo.setValue("isSendMail",xx);
    hashVo.setValue("url_p","<%=url_p%>");
    var request=new Request({method:'post',asynchronous:false,onSuccess:showPerson,functionId:'30200710251'},hashVo);			
} 
function showPerson(outparameters)
{
  var msg = outparameters.getValue("msg");
  if(msg=='2')
  {
     alert("没有可退回的记录!");
     return;
  }
  else if(msg=='0')
  {
       var list=outparameters.getValue("list");
       if(list.length==0)
       {
          alert("没有可退回的记录!");
          return;
       }
       //personSelectOK
       var arguments=new Array(); 
       for(var i=0;i<list.length;i++)
      {
         arguments[i]=list[i].dataValue+"`"+list[i].dataName;
      }
	    var strurl="/performance/objectiveManage/objectiveCard.do?br_share=share";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl;
	    if(!window.showModalDialog){
	        window.dialogArguments = arguments;
        }
	    var config = {
	    	id:'reject_win',
	        width:400,
            height:300,
            type:'1',
            dialogArguments:arguments
        }
	    modalDialog.showModalDialogs(iframe_url,arguments,config,myuntread_ok);
  }
  else if(msg=='1')
  {
    if(confirm("确定执行退回操作？"))
    {
       var hashVo=new ParameterSet();
       var a0100=outparameters.getValue("a0100");
       var isSendEmail=document.getElementsByName("isSendEmail");
       var xx="-1";
       if(isSendEmail[0])
       {
          if(isSendEmail[0].checked)
              xx='1';
       }  
       hashVo.setValue("plan_id","${objectCardForm.planid}");
       hashVo.setValue("object_id","${objectCardForm.object_id}");
       hashVo.setValue("body_id","${objectCardForm.body_id}");
       hashVo.setValue("type","2");
       hashVo.setValue("ids",a0100);
       hashVo.setValue("isSendMail",xx);
       hashVo.setValue("url_p","<%=url_p%>");
       var request=new Request({method:'post',asynchronous:false,onSuccess:untreadSU,functionId:'30200710251'},hashVo);			
    }
  }
}
function myuntread_ok(flag){
    if(flag)
    {
        var isSendEmail=document.getElementsByName("isSendEmail");
        var xx="-1";
        if(isSendEmail[0])
        {
            if(isSendEmail[0].checked)
                xx='1';
        }
        var ids=flag.ids;
        var reason=flag.reason;
        Ext.showConfirm("确定执行退回操作？",function (btnflag) {
            if(btnflag == "yes"){
                var hashVo=new ParameterSet();
                hashVo.setValue("plan_id","${objectCardForm.planid}");
                hashVo.setValue("object_id","${objectCardForm.object_id}");
                hashVo.setValue("body_id","${objectCardForm.body_id}");
                hashVo.setValue("type","2");
                hashVo.setValue("ids",ids.substring(1));
                hashVo.setValue("reason",reason);
                hashVo.setValue("isSendMail",xx);
                hashVo.setValue("url_p","<%=url_p%>");
                var request=new Request({method:'post',asynchronous:false,onSuccess:untreadSU,functionId:'30200710251'},hashVo);
            }
        });
    }
}
function personSelectOK()
{
    var checkElem=document.getElementsByName("selectPersonC");
    var ids="";
    for(var i=0;i<checkElem.length;i++)
    {
       if(checkElem[i].checked)
          ids+="/"+checkElem[i].value;
    }
    if(ids=='')
    {
       alert("请选择要退回的人员！");
       return;
    }
    Element.hide('person_panel0')
   var isSendEmail=document.getElementsByName("isSendEmail");
   var xx="-1";
    if(isSendEmail[0])
    {
       if(isSendEmail[0].checked)
           xx='1';
    }  
     if(confirm("确定执行退回操作？"))
     {
       var hashVo=new ParameterSet();
       hashVo.setValue("plan_id","${objectCardForm.planid}");
       hashVo.setValue("object_id","${objectCardForm.object_id}");
       hashVo.setValue("body_id","${objectCardForm.body_id}");
       hashVo.setValue("type","2");
       hashVo.setValue("ids",ids.substring(1));
       hashVo.setValue("isSendMail",xx);
       hashVo.setValue("url_p","<%=url_p%>");
       var request=new Request({method:'post',asynchronous:false,onSuccess:untreadSU,functionId:'30200710251'},hashVo);			
     }
}
 function untreadSU(outparameters)
 {
    Ext.showAlert("操作成功！",function () {
        var opt="${objectCardForm.opt}";
        if(opt=='2')
            opt='0';
        document.location="/performance/objectiveManage/objectiveCard.do?b_query=query&model=${objectCardForm.model}&opt="+opt+"&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}&body_id=${objectCardForm.body_id}";
    });
 }
 function exportObjectiveCard()
{
    var hashVo=new ParameterSet();
    hashVo.setValue("plan_id","<%=planid%>");
    hashVo.setValue("object_id","<%=objectid%>");
    hashVo.setValue("body_id","${objectCardForm.body_id}");
    hashVo.setValue("model",model);
    hashVo.setValue("opt",opt);
	var request=new Request({method:'post',asynchronous:false,onSuccess:export_ok,functionId:'30200710254'},hashVo);			
		  
}
function export_ok(outparameters)
{
  var fileName=outparameters.getValue("fileName"); 
//var win=open("/servlet/DisplayOleContent?filename="+fileName,"excel");
  //20/3/6 xus vfs改造
	fileName = decode(fileName);
  var win=open("/servlet/vfsservlet?fileid="+fileName+"&fromjavafolder=true");
}
function closewindow()
{
	//history.back();
	window.close();
}
function goback()
{

   document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=${objectCardForm.body_id}&returnflag=${objectCardForm.returnflag}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
   document.objectCardForm.submit();
}
function showHistoryTask(flag)
{
	document.objectCardForm.isShowHistoryTask.value=flag;
	<%if(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz")){%>
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&fromflag=rz&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
	<%}else{%>
	document.objectCardForm.action="/performance/objectiveManage/objectiveCard.do?b_query=query&body_id=${objectCardForm.body_id}&model=${objectCardForm.model}&opt=${objectCardForm.opt}&planid=${objectCardForm.mdplanid}&object_id=${objectCardForm.mdobject_id}";
	<%}%>
	document.objectCardForm.submit();
}
function click(){ 

if(event.button==2){ 
	alert( '右键已屏蔽 !'); 
} 
} 
<!--document.onmousedown=click-->
function designateTask(planid,objectid)
{
    var returnUrl="/performance/objectiveManage/objectiveCard.do?b_query=query`body_id=${objectCardForm.body_id}`returnflag=${objectCardForm.returnflag}`opt=${objectCardForm.opt}`planid=${objectCardForm.mdplanid}`object_id=${objectCardForm.mdobject_id}";
    document.location="/performance/objectiveManage/designateTask.do?b_init=link&plan_id="+planid+"&objectid="+objectid+"&returnURL="+$URL.encode(returnUrl);
}
function My(){
	// document.objectCardForm.action="/performance/workdiary/myworkdiaryshow.do?b_search=link&state='+<%=a0 %>+'";
	// document.objectCardForm.submit();
		var info='';
		var thecodeurl="/performance/workdiary/myworkdiaryshow.do?b_search=link`state='+<%=a0 %>+'`zxgflag=1";	
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		var win= window.showModalDialog(iframe_url,info, 
		       "dialogWidth:1000px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:no");
		       if(win!=null){
		       
		        }
}
//计划说明
	function planDescript(planid)
	{
		var thecodeurl="/performance/objectiveManage/myObjective/my_objective_list.do?b_reject=link`opt=2`plan_id="+planid; 
		var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
		/*var retvo= window.showModalDialog(iframe_url, null,
						        "dialogWidth:600px; dialogHeight:510px;resizable:no;center:yes;scroll:yes;status:no");*/
        var config = {
            width:600,
            height:410,
            type:'2'
        }
        modalDialog.showModalDialogs(iframe_url,'planDescriptwin',config);
	}
	function hiddenText(obj,name,str,temp,_temp){
		 var tab=document.getElementById(name);
		 var tab1=document.getElementById(str);
		 var tab2=document.getElementById(temp);
		 var tab3=document.getElementById(_temp);
		 if(tab){
		 	if(obj.checked){
		 		tab.style.display='block';	
		 		if(tab2){
		 			tab2.src='/images/expand_pm.gif';
		 		}		 		
		 		tab1.style.display='none';	
		 		if(tab3){
			 		tab3.src='/images/collapse_pm.gif';	 		
		 		}
		 	}
		 }
	}
</script>
<script type="text/javascript">

//if(IVersion==8){
 // document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard_8.css\" rel=\"stylesheet\" type=\"text/css\">");
//}
//else{
//  document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/objectiveCard.css\" rel=\"stylesheet\" type=\"text/css\">");
//}
document.writeln("<link href=\"/performance/objectiveManage/objectiveCard/cardPage.css\" rel=\"stylesheet\" type=\"text/css\">");
//-->
	/** 禁用鼠标滚轮 **/
	function stop_onmousewheel(){
		for(var i=0;i<document.objectCardForm.getElementsByTagName('select').length;i++){
			document.objectCardForm.getElementsByTagName('select')[i].onmousewheel = function (){
			return false;}
		}
	}
</script>
<hrms:themes />
<style>
.table_class{
border: 1px solid  #5383E5; 
}

.table_td {
	
	BORDER-BOTTOM: #0066cc 1pt dotted; 
	BORDER-LEFT: #0066cc 0pt dotted; 
	BORDER-RIGHT: #0066cc 0pt dotted; 
	BORDER-TOP: #0066cc 0pt dotted;
}
</style>
<html>
<div id="options"  onmouseleave="closeDiv()"  >


</div>
<%if(userView.hasTheFunction("06070304")||userView.hasTheFunction("06070204")||userView.hasTheFunction("06070405")||userView.hasTheFunction("06070109")){ %>
<div id='print' style="display:none"></div>
<%} %>

<body  onload="initCard();stop_onmousewheel()" >
<html:form action="/performance/objectiveManage/objectiveCard">
<html:hidden name="objectCardForm" property="returnURL"/>
<table width="99%" cellspacing="0" cellpadding="0" style="margin-left:5px"><tr height="<%=trHeight%>"><td align="left">
${objectCardForm.desc}</td>
</tr>
<tr><td>
<script language='javascript' >
		document.write("<div id=\"tbl-container\"  class=\"framestyle0\" style='height:"+(aclientHeight-200-<%=divHeight%>)+"px;'  >");
</script>
<table border='0' cellspacing="0"  align="left" cellpadding="0" class="ListTable" style="BORDER-COLLAPSE: separate">
<tr>
<td>
${objectCardForm.cardHtml} 
</td>
</tr>

<%if(!clientName.equals("zglt")){ %>
<tr>
<td>
<table width="70%" align="left" border='0'>

<tr>
<td>
<hrms:priv func_id="06070218">
<%if((model.equals("2"))&&(body_id.equals("5")||body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2")||SpByBodySeq.equalsIgnoreCase("true"))){ %>
<input type="radio" name="type" value="0" onclick="hiddenText(this,'spd','pfd','but','but1');"/>审批过程&nbsp;<a href="javascript:displayIMG1('0','spd','but');"><img id="but" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070116">
<%if((model.equals("1"))&&(body_id.equals("5")||body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2")||SpByBodySeq.equalsIgnoreCase("true"))){ %>
<input type="radio" name="type" value="0" onclick="hiddenText(this,'spd','pfd','but','but1');"/>审批过程&nbsp;<a href="javascript:displayIMG1('0','spd','but');"><img id="but" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070302">
<%if((model.equals("3"))&&(body_id.equals("5")||body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2")||SpByBodySeq.equalsIgnoreCase("true"))){ %>
<input type="radio" name="type" value="0" onclick="hiddenText(this,'spd','pfd','but','but1');"/>审批过程&nbsp;<a href="javascript:displayIMG1('0','spd','but');"><img id="but" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070410">
<%if(model.equals("4")&&objectCardForm.getPfopinion().length()>0){ %>
<input type="radio" name="type" value="1" onclick="hiddenText(this,'pfd','spd','but1','but');"/>评分过程&nbsp;<a href="javascript:displayIMG1('1','pfd','but1');"><img id="but1" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070311">
<%if(model.equals("3")&&objectCardForm.getPfopinion().length()>0){ %>
<input type="radio" name="type" value="1" onclick="hiddenText(this,'pfd','spd','but1','but');"/>评分过程&nbsp;<a href="javascript:displayIMG1('1','pfd','but1');"><img id="but1" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070119">
<%if(model.equals("1")&&objectCardForm.getPfopinion().length()>0){ %>
<input type="radio" name="type" value="1" onclick="hiddenText(this,'pfd','spd','but1','but');"/>评分过程&nbsp;<a href="javascript:displayIMG1('1','pfd','but1');"><img id="but1" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>

<hrms:priv func_id="06070219">
<%if(model.equals("2")&&objectCardForm.getPfopinion().length()>0){ %>
<input type="radio" name="type" value="1" onclick="hiddenText(this,'pfd','spd','but1','but');"/>评分过程&nbsp;<a href="javascript:displayIMG1('1','pfd','but1');"><img id="but1" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
<%} %>
</hrms:priv>
</td>
</tr>
<tr>
<td>

<table id="spd" style="display:none;" width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
 <tr><td align="left">
 <textarea name="spflow" rows="10" cols="95"  readonly="readonly" >${objectCardForm.sp_flow}</textarea>
	  						
 </td></tr>
</table>


</td>
</tr>
<tr>
<td>
<table id="pfd" style="display:none;" width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
 <tr><td align="left">
 <textarea name="spflow" rows="10" cols="95"  readonly="readonly" >${objectCardForm.pfopinion}</textarea>
	  						
 </td></tr>
</table>
</td>
</tr>

</table>
</td>
</tr>
<%} %>


<%-- 2013.11.09 pjf --%>
<%  if(SystemConfig.getPropertyValue("clientName")!=null&&SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("hkyh")) { %>
<%if(planDescription!=null&&planDescription.length()>0){%>
<tr>
<td>
<table width="70%" align="left" border='0'>
<tr>
<td>
计划说明&nbsp;<a href="javascript:displayIMG('planDesc','but2');"><img id="but2" src="/images/expand_pm.gif" border="0" style="cursor:hand"/></a>
</td>
</tr>
<tr>
<td>
<table id="planDesc" style="display=block" width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
 <tr><td align="left">
 <textarea name="planDesc" rows="10" cols="95"  readonly="readonly" >${objectCardForm.planDescription}</textarea>
	  						
 </td></tr>
</table>
</td>
</tr>
<tr>
<td>
</td>
</tr>
</table>
</td>
</tr>
<%} %>
<%} %>
</table>
<script language='javascript' >
		document.write("</div>");
</script>
</td>
</tr>
<tr><td>
<script language='javascript' >
		document.write("<div id=\"aa\"  style='margin-top:5px;'>");
</script>
<!-- 填表说明 zzk -->
<%  if(SystemConfig.getPropertyValue("clientName")==null || !SystemConfig.getPropertyValue("clientName").trim().equalsIgnoreCase("hkyh")) { %>
<%if(planDescription!=null&&planDescription.length()>0){%>
 <input type="button" name="b_descript" value="<bean:message key='lable.performance.fillDeclare'/>" onclick="planDescript('<%=planid%>')" class="mybutton"  />
 <%} %>
 <%} %>
<% if(request.getParameter("confirm")==null||!request.getParameter("confirm").equals("1")){ %>

<%
if((plan_Status==6||plan_Status==4)&&((model.equals("4")||(model.equals("1")&&opt.equals("2"))||(model.equals("2")&&opt.equals("2")))&&!mainbodyScoreStatus.equals("2")&&(EvalCanNewPoint.equalsIgnoreCase("true")&&scoreflag.equals("4"))&&objectSpFlag.equals("03")))
{
 %>
 <hrms:priv func_id="06070402">
    <input type='button' value='<bean:message key="lable.tz_template.new"/>' onclick='newEvalCanNewPointPoint()' class="<%=buttonClass%>" />	
 </hrms:priv>
 <%if((plan_Status==6||plan_Status==4)&&targetTraceEnabled.equalsIgnoreCase("true")&&((TargetCollectItem!=null&&!TargetCollectItem.equals("")))||targetCollectItem2!=null&&!targetCollectItem2.equals("")){ %>
 <input type="button" class="<%=buttonClass%>" value="数据采集" onclick='collectData("${objectCardForm.a_code}","${objectCardForm.planid}","${objectCardForm.object_id}","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'/>
 <%}
 } %>
<% if(!opt.equals("0")){ %>
<logic:equal name='objectCardForm' property='objectSpFlag'  value="01">
	<% if(model.equals("7")||body_id.equals("5")||allowLeadAdjustCard.equalsIgnoreCase("True")){ %>
	<hrms:priv func_id="06070205,06070103,06070305"><!-- 任务目标 -->
	<input type='button' value='<bean:message key="label.performance.task.target"/>' onclick='newPoint()' class="<%=buttonClass%>" />	
	</hrms:priv>
	<hrms:priv func_id="06070306,06070206,06070104"><!-- 引用绩效指标 -->
	<input type='button' value='<bean:message key="label.performance.importPerPoint"/>' onclick='importPoint("${objectCardForm.object_id}","${objectCardForm.plan_objectType}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
	</hrms:priv>
	<%if(importPositionField.equalsIgnoreCase("true")){ %>
	<hrms:priv func_id="06070101,06070203,06070303">															
	  <input type="button" value="<bean:message key="label.performance.importpositionfield"/>" class="<%=buttonClass%>" onclick='importPositionField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	
	<%}%>
	<%if(importDeptField.equalsIgnoreCase("true")){ %> 
	<hrms:priv func_id="06070116,06070216,06070311">	
	    <input type="button" value="引入部门工作任务" class="<%=buttonClass%>" onclick='importDeptField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<% }
	} 
	%>
	
	
<%  if((a0100.equals(object_id))||a0100.equals(un_functionary)||allowLeadAdjustCard.equalsIgnoreCase("True")||model.equals("7")){ 
		   if(isAdjustPoint.equalsIgnoreCase("False")&&processing_state_all.equals("0")&&(model.equals("1")||model.equals("2")||model.equals("3")||model.equals("7")))
		   {
		%>
		<hrms:priv func_id="06070202,06070102,06070310">
		<input type='button' id='importPre'  value="<bean:message key="<%=importPre%>"/>" onclick='importPreCard("${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</hrms:priv>
		<% } %>
<%   } %>
<logic:equal name='objectCardForm' property='objectSpFlag'  value="01">
	<logic:equal name='objectCardForm' property='model'  value="1">
		<hrms:priv func_id="06070117">
			<input type="button" name="downLoad" class="<%=buttonClass%>" value="<bean:message key="button.download.template"/>" onclick="downLoadTarget('1');"/>			
		</hrms:priv>

		<hrms:priv func_id="06070118">
			<input type="button" name="importData" class="<%=buttonClass%>" value="<bean:message key="button.import"/><bean:message key="lable.menu.main.target"/>" onclick="importTarget('1');"/>
		</hrms:priv>
	</logic:equal>
	<logic:equal name='objectCardForm' property='model'  value="2">
		<hrms:priv func_id="06070216">
			<input type="button" name="downLoad" class="<%=buttonClass%>" value="<bean:message key="button.download.template"/>" onclick="downLoadTarget('2');"/>			
		</hrms:priv>

		<hrms:priv func_id="06070217">
			<input type="button" name="importData" class="<%=buttonClass%>" value="<bean:message key="button.import"/><bean:message key="lable.menu.main.target"/>" onclick="importTarget('2');"/>
		</hrms:priv>
	</logic:equal>
</logic:equal>
<%
if((model.equals("1") || model.equals("2"))&&opt.equals("1") && appealObjectStr.length()>0){ %>
		<html:hidden name="objectCardForm" property="appealObjectStr"/>
		<hrms:priv func_id="06070212,06070110">
			<input type='button' id='importLeaderTarget'  value='<bean:message key="jx.selfmytarget.importLeaderTarget"/>'  onclick='importLeaderTargetCard("${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</hrms:priv>					

		<hrms:priv func_id="06070213,06070111">
			<input type='button' id='lookLeaderTarget'  value='<bean:message key="jx.selfmytarget.lookLeaderTarget"/>'  onclick='lookLeaderTargetCard("${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</hrms:priv>
		
	<% }
//	if(!(planParam.get("EvalCanNewPoint")!=null&&((String)planParam.get("EvalCanNewPoint")).equalsIgnoreCase("true")&&scoreflag.equals("4")))  //dengcan 解决bug:0020240
	if(!model.equals("7"))
	{
		if((((String)planParam.get("taskAdjustNeedNew")).equalsIgnoreCase("True")||isAdjustPoint.equalsIgnoreCase("False"))&&(a0100.equals(object_id)||a0100.equals(un_functionary)||(allowLeadAdjustCard.equalsIgnoreCase("True")&&isAdjustPoint.equalsIgnoreCase("False")))){ %>
		<input type='button' value='<bean:message key="button.temporary.save"/>' onclick='saveTaskValue(1,"${objectCardForm.body_id}","${objectCardForm.status}","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		<% } 
	}
	
	if(((a0100.equals(object_id))||a0100.equals(un_functionary)||allowLeadAdjustCard.equalsIgnoreCase("True"))&&!model.equals("7")){
	%>
	
		<% 
		  //if(allowLeadAdjustCard.equalsIgnoreCase("True")&&isApprove.equals("1")){ 
		  if(isApprove.equals("1")){ //2013-08-03 允许领导批准目标卡 与是否可以出现批准按钮没有关系
		%>
		<input type='button' id='appeal'  value='<bean:message key="<%=pz%>"/>' onclick='approveCard("${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" /><!-- 办理 -->
		<% }else{ %>
		<input type='button' id='appeal'  value="<bean:message key="<%=bp%>"/>" onclick='appealCard(event,"${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" /><!-- 交办 -->
		<% } %>
		
	<% } %>
	
	
	
	
</logic:equal>
<% if(!model.equals("7")){ %>
<logic:equal name='objectCardForm' property='objectSpFlag'  value="02">
	<% if(!body_id.equals("5")&&currappuser.equalsIgnoreCase(a0100)){ %>
	<% 	if(allowLeadAdjustCard.equalsIgnoreCase("True")) // &&(!a0100.equalsIgnoreCase(object_id)))
		{
	%>
	 <hrms:priv func_id="06070205,06070103,06070305">
		<input type='button' value='<bean:message key="lable.tz_template.new"/>' onclick='newPoint()' class="<%=buttonClass%>" />	
	    </hrms:priv>
	    <hrms:priv func_id="06070306,06070206,06070104">
	    <input type='button' value='<bean:message key="label.performance.importPerPoint"/>' onclick='importPoint("${objectCardForm.object_id}","${objectCardForm.plan_objectType}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
	   </hrms:priv>
	    <%if(importPositionField.equalsIgnoreCase("true")){ %>
	      <hrms:priv func_id="06070101,06070203,06070303">
	      <input type="button" value="<bean:message key="label.performance.importpositionfield"/>" class="<%=buttonClass%>" onclick='importPositionField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	      </hrms:priv>
	      <%} %>
	<%if(importDeptField.equalsIgnoreCase("true")){ %> 
	<hrms:priv func_id="06070116,06070216,06070311">	
	    <input type="button" value="引入部门工作任务" class="<%=buttonClass%>" onclick='importDeptField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<% }
	} 
	%>
	
	
		<logic:equal name='objectCardForm' property='isApprove'  value="0">
		<input type='button' id='appeal'  value='<bean:message key="<%=bp%>"/>' onclick='appealCard(event,"${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</logic:equal>
		
		<logic:equal name='objectCardForm' property='isApprove'  value="1">
		<input type='button' id='appeal'  value='<bean:message key="<%=pz%>"/>' onclick='approveCard("${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</logic:equal>
		
		<input type='button' value='<bean:message key="<%=bh%>"/>' onclick='reject("${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
	<% } %>
</logic:equal>
<% } %>
<logic:equal name='objectCardForm' property='objectSpFlag'  value="07">
	<% if((body_id.equals("5")&&(object_id.equalsIgnoreCase(a0100)||un_functionary.equalsIgnoreCase(a0100)))||model.equals("7")){ %>
	<hrms:priv func_id="06070205,06070103,06070305">
	<input type='button' value='<bean:message key="lable.tz_template.new"/>' onclick='newPoint()' class="<%=buttonClass%>" />	
	</hrms:priv>
	<hrms:priv func_id="06070306,06070206,06070104">
	<input type='button' value='<bean:message key="label.performance.importPerPoint"/>' onclick='importPoint("${objectCardForm.object_id}","${objectCardForm.plan_objectType}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
	</hrms:priv>
	<%if(importPositionField.equalsIgnoreCase("true")){ %>
	<hrms:priv func_id="06070101,06070203,06070303">
	  <input type="button" value="<bean:message key="label.performance.importpositionfield"/>" class="<%=buttonClass%>" onclick='importPositionField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<%} %>
	<%if(importDeptField.equalsIgnoreCase("true")){ %> 
	<hrms:priv func_id="06070116,06070216,06070311">	
	    <input type="button" value="引入部门工作任务" class="<%=buttonClass%>" onclick='importDeptField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<% }
	%>
	
	<%
if((model.equals("1") || model.equals("2"))&&opt.equals("1") && appealObjectStr.length()>0){ %>
		<html:hidden name="objectCardForm" property="appealObjectStr"/>
		<hrms:priv func_id="06070212,06070110">
			<input type='button' id='importLeaderTarget'  value='<bean:message key="jx.selfmytarget.importLeaderTarget"/>'  onclick='importLeaderTargetCard("${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</hrms:priv>
		 
		<hrms:priv func_id="06070213,06070111">
			<input type='button' id='lookLeaderTarget'  value='<bean:message key="jx.selfmytarget.lookLeaderTarget"/>'  onclick='lookLeaderTargetCard("${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
		</hrms:priv>
		
<%}  if(!model.equals("7")){ %>
<!-- 
	<input type='button' value='<bean:message key="options.save"/>' onclick='saveTaskValue(1,"${objectCardForm.body_id}","${objectCardForm.status}","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
 -->
	<input type='button' id='appeal'  value='<bean:message key="<%=bp%>"/>' onclick='appealCard(event,"${objectCardForm.body_id}","<%=a0100%>","${objectCardForm.object_id}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
<%  } %>
	<% } %>
</logic:equal>
<logic:equal name='objectCardForm' property='objectSpFlag'  value="03">
	<% 
	if(((String)planParam.get("TargetAllowAdjustAfterApprove")).equalsIgnoreCase("True")){
	if(opt.equals("1") && ((model.equals("2")&&object_id.equalsIgnoreCase(a0100)) || (model.equals("1")&&un_functionary.equalsIgnoreCase(a0100)) || (model.equals("7")))){ %>
		<hrms:priv func_id="06070205,06070103,06070305">
		<input type='button' value='<bean:message key="lable.tz_template.new"/>' onclick='newPoint()' class="<%=buttonClass%>" />	
   	   </hrms:priv>
   	   <hrms:priv func_id="06070306,06070206,06070104">
   	    <input type='button' value='<bean:message key="label.performance.importPerPoint"/>' onclick='importPoint("${objectCardForm.object_id}","${objectCardForm.plan_objectType}","${objectCardForm.planid}")' class="<%=buttonClass%>" />
   	   </hrms:priv>
   	    <%if(importPositionField.equalsIgnoreCase("true")){ %>
	<hrms:priv func_id="06070101,06070203,06070303">
	  <input type="button" value="<bean:message key="label.performance.importpositionfield"/>" class="<%=buttonClass%>" onclick='importPositionField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<% }%>
   	 <%if(importDeptField.equalsIgnoreCase("true")){ %> 
   	 <hrms:priv func_id="06070116,06070216,06070311">	
	    <input type="button" value="引入部门工作任务" class="<%=buttonClass%>" onclick='importDeptField("<%=planid %>","<%=objectid %>","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");'>
	</hrms:priv>
	<% }
	} }
	%>
	
</logic:equal>
	

<% if((model.equals("4")||model.equals("2")||model.equals("1"))&&opt.equals("2")/*&&!mainbodyScoreStatus.equals("2")*/){ 
   if(!mainbodyScoreStatus.equals("2"))
   {
%>
	<%-- 参与评分却只有确认权限的主体提供“同意”按钮,其他的情况走原先的程序 by 刘蒙 --%>
	<logic:equal name="objectCardForm" property="planBodyOpt" value="1">
		<input type="button" class="<%=buttonClass %>" value="<bean:message key="label.agree"/>"
			onclick='agree("${objectCardForm.planid}", "<%=object_id%>")' id="agreeBtn" />
	</logic:equal>
	<logic:notEqual name="objectCardForm" property="planBodyOpt" value="1">
		<input type='button'  class="<%=buttonClass%>"  value="<bean:message key="info.appleal.state14"/>" onclick='subScore("1","<%=objectid%>","<%=planid%>","${objectCardForm.body_id}","4","2")' class="<%=buttonClass%>" />
		<%if(clientName.equalsIgnoreCase("zglt")){ %>
		<input type='button' class="<%=buttonClass%>"  value='打分完成' onclick='subScore("8","<%=objectid%>","<%=planid%>","${objectCardForm.body_id}","4","2")' class="<%=buttonClass%>" />
		<%}else{ 
		//田野添加 判断是否是国网，不是就显现提交按钮
		if(SystemConfig.getPropertyValue("clientName")==null || !"gwyjy".equalsIgnoreCase(SystemConfig.getPropertyValue("clientName")))
		{	 
		%>
		<input type='button' value='提交' onclick='subScore("2","<%=objectid%>","<%=planid%>","${objectCardForm.body_id}","4","2")' class="<%=buttonClass%>" />
		<%
			}	
		} %>
	</logic:notEqual>
<%if((plan_Status==6||plan_Status==4)&&(model.equals("4")||(model.equals("1")))&&(((body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2"))&&allowSeeLowerGrade.equalsIgnoreCase("true"))||isRejectFunc.equals("1"))&&!mainbodyScoreStatus.equals("2")&&opt.equals("2")){ 
%>
	<%-- 同意结束后屏蔽退回按钮 by 刘蒙 --%>
	<input type='button' id="returnBtn" value='退回' onclick="untread();" class="<%=buttonClass%>" />
<%}%>
 <hrms:priv func_id="06070404,06070209,06070107">
  <input type="button" class="<%=buttonClass%>" onclick="exportObjectiveCard();" value="导出打分表"/>
  </hrms:priv>
<% }} 

}
%>

<% if((model.equals("4")||model.equals("2"))&&opt.equals("0")){ %>
<hrms:priv func_id="06070404,06070209,06070107">
	<input type="button" class="<%=buttonClass%>" onclick="exportObjectiveCard();" value="导出打分表"/>
</hrms:priv>
<% } %>

${objectCardForm.personalComment}

<logic:equal name='objectCardForm' property='objectSpFlag'  value="03">
 <%
 // if(mainbodyScoreStatus.equals("2"))
if(!model.equals("7")){ 
  if((plan_Status>=4&&plan_Status!=8)||(model.equals("6"))||(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz")))
  { %>
  <hrms:priv func_id="06070105,06070207,06070307,06070403">
 <input type='button' value="<bean:message key="info.appleal.state18"/>" onclick="review('-1','1')" class="<%=buttonClass%>" />
  </hrms:priv>
 <% }else{ %>
  <hrms:priv func_id="06070105,06070207,06070307,06070403">
 <input type='button' value="<bean:message key="info.appleal.state18"/>" onclick="review('-1','0')" class="<%=buttonClass%>" />
 </hrms:priv>
 <% } 

} 
 
 %>
</logic:equal>
<% 
if(objectCardForm.getTxw().equals("1")){
if(model.equals("3")||model.equals("1")||model.equals("4")){
	//if(plan_Status>=4&&plan_Status!=8&&plan_Status!=5){
	 out.println(workDiaryButton_html);
	// }
}
}else{
if(model.equals("2")||model.equals("3")||model.equals("1")||model.equals("4")){
	 out.println(workDiaryButton_html);
}
}%>
<!-- 非ie下屏蔽打印功能 -->
<!--[if IE]> 
<logic:equal value="1" name="objectCardForm" property="isCard">
<%
if(model.equals("2")){ %>
<hrms:priv func_id="06070204">
<input type="button" class="<%=buttonClass%>" name="print" value="打 印" onclick='printP("${objectCardForm.mdplanid}","${objectCardForm.mdobject_id}","${objectCardForm.tabIDs}");'/>
</hrms:priv>
<% }else if(model.equals("3")){ %>
<hrms:priv func_id="06070304">
<input type="button" class="<%=buttonClass%>" name="print" value="打 印" onclick='printP("${objectCardForm.mdplanid}","${objectCardForm.mdobject_id}","${objectCardForm.tabIDs}");'/>
</hrms:priv>
<%} else if(model.equals("1")){%>
<hrms:priv func_id="06070109">
<input type="button" class="<%=buttonClass%>" name="print" value="打 印" onclick='printP("${objectCardForm.mdplanid}","${objectCardForm.mdobject_id}","${objectCardForm.tabIDs}");'/>
</hrms:priv>
<%}else if(model.equals("4")){ %>
<hrms:priv func_id="06070405">
<input type="button" class="<%=buttonClass%>" name="print" value="打 印" onclick='printP("${objectCardForm.mdplanid}","${objectCardForm.mdobject_id}","${objectCardForm.tabIDs}");'/>
</hrms:priv>
<%} %>
</logic:equal>
<![endif]-->

<%if((body_id.equals("5")||body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2"))){ %>
<%if(clientName.equals("zglt")){ %>
 <input type="button" class="<%=buttonClass%>" name="flow" value="历史日志" onclick='detail("${objectCardForm.object_id}","${objectCardForm.planid}");'/>
<%} %>
<%}%>
<%if((plan_Status==6||plan_Status==4)&&(model.equals("4")||(model.equals("1")))&&(((body_id.equals("0")||body_id.equals("1")||body_id.equals("-1")||body_id.equals("-2"))&&allowSeeLowerGrade.equalsIgnoreCase("true"))||isRejectFunc.equals("1"))&&!mainbodyScoreStatus.equals("2")&&opt.equals("2")){ 
%>
	<%-- 在原有条件下判断：确认（opt=1）时，屏蔽复制按钮 by 刘蒙 --%>
	<logic:notEqual name="objectCardForm" property="planBodyOpt" value="1">
		<input id="copy" type="button" value="复制" class='<%=buttonClass%>'
			onclick='copyScore("${objectCardForm.planid}","${objectCardForm.object_id}","${objectCardForm.model}","${objectCardForm.opt}","${objectCardForm.body_id}");' />
	</logic:notEqual>
<%}%>
<%if((scoreManual.equalsIgnoreCase("true")) && (model.equals("1") || model.equals("4")) && (opt.equals("2") || mainbodyScoreStatus.equals("2"))){ %>  
<hrms:priv func_id="06070113,06070407">  
	<input type='button' value='<bean:message key="button.scoreManual"/>' onclick='scoreManual();' class="<%=buttonClass%>" />
</hrms:priv>
<%} 
 
if(plan_Status==8||plan_Status==5||((plan_Status==4||plan_Status==6)&&(planParam.get("NoShowTargetAdjustHistory")==null||((String)planParam.get("NoShowTargetAdjustHistory")).equalsIgnoreCase("False")))){
	 
	if(taskAdjustNeedNew.equalsIgnoreCase("True")&&isAdjustPoint.equalsIgnoreCase("True")&&!model.equals("6"))
	{
		if(isShowHistoryTask.equals("0"))
		{
%>
	<input type="button"     value="查看历史" class="<%=buttonClass%>" onclick="showHistoryTask('1')" />

<%	
		}
		else
		{
%>
	<input type="button"     value="隐藏历史" class="<%=buttonClass%>" onclick="showHistoryTask('0')" />
<%
		}
	}
}
%>
<%if(plan_Status!=7&&((model.equals("2")&&(realSpFlag.equals("03")))||(model.equals("1")&&un_functionary.equalsIgnoreCase(a0100)&&(realSpFlag.equals("03"))))&&(request.getParameter("fromflag")==null||!request.getParameter("fromflag").equals("rz"))){
 %>
<hrms:priv func_id="06070114,06070214">  
<input type='button' value='任务下达' onclick='designateTask("${objectCardForm.planid}","${objectCardForm.object_id}");' class="<%=buttonClass%>" />
</hrms:priv>
<%} %>
 <%if(!model.equals("7")&&(!returnflag.equals("8")&&!returnflag.equals("10")&&(returnURL==null||returnURL.trim().length()==0||entranceType.equals("1")||entranceType.equals("2")||entranceType.equals("3")))){ 
	if(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz"))
	{
%>

<input type='button' value='<bean:message key="button.close"/>' onclick='javascript:parent.window.close()' class="<%=buttonClass%>" />

<%
	}
	else
	{
 %>

<input type='button' value='<bean:message key="button.close"/>' onclick='javascript:window.close()' class="<%=buttonClass%>" />

<%
	}
 }else{ %>
<%if(clientName.equalsIgnoreCase("zglt")){ %>
<input type='button' value='返回上页' onclick='go_back()' class="<%=buttonClass%>" />
<%}
else if(!model.equals("7")&&(!returnflag.equals("8")&&!returnflag.equals("10")&&(returnURL==null||returnURL.trim().length()==0||(request.getParameter("pendingCode")!=null&&!request.getParameter("pendingCode").equals(""))||(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz"))))){%>
      
      <input type='button' value='关闭' onclick='closewindow()' class="mybutton" class="<%=buttonClass%>"/>
      <%}else{ 
      	if(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz"))
      	{
      %>
      <input type='button' value='<bean:message key="button.close"/>' onclick='javascript:parent.window.close()' class="<%=buttonClass%>" />
      <% 	
      	}
      	else
      	{
      %>
      <%if(objectCardForm.getTxw().equals("1")){
      	if(model.equals("2")){ %>
      <input type='button' value='我的日志' onclick='My()' class="<%=buttonClass%>" />
      <%} 
      	}%>
		<% String bosflg = userView.getBosflag();
		if(!"bi".equalsIgnoreCase(bosflg)) {%>
	     	 <input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='go_back()' class="<%=buttonClass%>" />
	 	<%}%>
		
<% 
		}
	}

 }
}
else
{
%>
<input type='button' value='确 认' onclick='confirmCard()' class="<%=buttonClass%>" />

<% 
}
if((plan_Status==6||plan_Status==4||plan_Status==8)&&objectSpFlag.equalsIgnoreCase("03")&&!model.equals("6")&&!model.equals("7")&&(request.getParameter("fromflag")==null||!request.getParameter("fromflag").equals("rz"))){
	if(isAllowAppealTrancePoint.equalsIgnoreCase("true")&&isAllowApproveTrancePoint.equalsIgnoreCase("false")&&"1".equals(includeOperateCloumn)){ %>
		<input type="button" title='报批跟踪指标' value="<bean:message key="<%=report%>"/>" class="<%=buttonClass%>" onclick='appealTracePoint("02")' />
	<% }
	if(isAllowApproveTrancePoint.equalsIgnoreCase("true")){
		if(per_objectVo!=null&&!per_objectVo.getString("trace_sp_flag").equals("03") && !per_objectVo.getString("trace_sp_flag").equals("07")){
	%>
		<input type="button"  title='批准跟踪指标'  value="<bean:message key="info.appleal.state3"/>" class="<%=buttonClass%>" onclick='appealTracePoint("03")' />
	<%
		}
		if(per_objectVo!=null&&per_objectVo.getString("trace_sp_flag").equals("02")){
	%>
		<input type="button"  title='退回跟踪指标'  value="<bean:message key="button.rejeect2"/>" class="<%=buttonClass%>" onclick='appealTracePoint("07")' />
	<% 	
		}
	}

}



%>
<script language='javascript' >
<%

 if(!objectSpFlag.equals("03")&&!model.equals("7")&&opt.equals("1")){%>
if(creatCard_mail=='true')
{
	document.write("&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='isSendEmail'  value='0'  />发送邮件提醒");

}
<% } %>

<% if((model.equals("4")||model.equals("2")||model.equals("1"))&&opt.equals("2")&&!mainbodyScoreStatus.equals("2")){
		if((body_id.equals("-1")||body_id.equals("-2")||body_id.equals("0")||body_id.equals("1")||body_id.equals("5"))||seqCondition.equalsIgnoreCase("true")){

 %>
	       if(currentlevel<targetMakeSeries||seqCondition.toLowerCase()=="true")
	       {
	       	 if(evaluateCard_mail=='true')
		     {
			    document.write("&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='isSendEmail'  value='0'  />发送邮件提醒");
		     }
	       }
<%		}
	}
 %>
</script>

</div>
</td>
</tr>
</table>
<script language='javascript' >
		document.write("<div id=\"bb\"  style='position:absolute;top:"+(aclientHeight-30)+";display:none'  >");
</script>
<%if(!returnflag.equals("8")&&!returnflag.equals("10")&&(returnURL==null||returnURL.trim().length()==0||request.getParameter("pendingCode")!=null||(request.getParameter("fromflag")!=null&&request.getParameter("fromflag").equals("rz")))){%>
      <input type='button' value='关闭' onclick='closewindow()' class="<%=buttonClass%>" />
      <%}else{ 
      if(clientName==null||(clientName!=null&&!clientName.equals("zglt"))){
      %>
	<input type='button' value='<bean:message key="kq.search_feast.back"/>' onclick='go_back()' class="<%=buttonClass%>" />
<%		}

}

 %>
</div>

<input type='hidden' value=''  name='importPoint_value' />
<input type='hidden' value='${objectCardForm.isShowHistoryTask}' name='isShowHistoryTask' />
<input type='hidden' value='<%=objectCardGradeMembersJson%>'  id='MembersJson' />
<div id="date_panel">
   			
</div>
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>

 <div id='menu_'   style="background:#ffffff;border:1px solid black;width:250px;height:200px " class="common_border_color">
	
</div>

<div id="date_panel0" style="background:#ffffff;border:1px groove black;width:130;height:<%=height%>">
<%
	ObjectCardForm form = (ObjectCardForm) session.getAttribute("objectCardForm");
	String _planid = form.getPlanid();
	_planid = PubFunc.encrypt(_planid);
	String _object_id = form.getObject_id();
	_object_id = PubFunc.encrypt(_object_id);
%>
  <select name="date_box" onblur="Element.hide('date_panel0');" id="selectBOX" size="<%=tlist.size()%>" onclick='addItem("<%=_planid %>","<%=_object_id %>");'>    
  <logic:iterate id="element" property="tabList" name="objectCardForm" offset="0" indexId="index">
  <option value="<bean:write name="element" property="id"/>"/><bean:write name="element" property="name"/></option>
   
  </logic:iterate> 
  </select>
</div>
<div id="person_panel0" style="background:#ffffff;border:1px groove black;width:130;height:100px">

</div>


<div id="extJs_tree">
	<iframe id="iframe_main" src="" width="630" height="330" scrolling="auto" frameborder="0" name="iframe_main"></iframe>
	<table width="618" border="0" cellspacing="0" align="center" cellpadding="0">	
		<tr>
			<td align="right" style="height:10px">
				<input type="button" name="closeExt" class="mybutton" style="margin-right:0px;" value="<bean:message key="button.close"/>" onclick="hiddenExtJs();"/>
			</td>
		</tr>
	</table> 			
</div>

</html:form>

<script language='javascript'>
	document.getElementById('menu_').style.display="none";
	Element.hide('date_panel0');
	Element.hide('person_panel0');
	Element.hide('extJs_tree');
	giveValue("1");
	<% if((model.equals("4")||model.equals("2")||model.equals("1"))&&opt.equals("2")/*&&!mainbodyScoreStatus.equals("2")*/){ 
   if(!mainbodyScoreStatus.equals("2")&&!grading_auto_saving.equals("")&&!grading_auto_saving.equals("0"))
   {%>
     window.setInterval('auto_saveScore()',grading_auto_saving*1000);
   <%}}%>
   var _flag=document.getElementsByName("type");
   if(_flag[0]){
		_flag[0].checked=true;
		if(_flag[0].value=="0"){
			hiddenText(_flag[0],'spd','pfd','but','but1');
		}else if(_flag[0].value=="1"){
			hiddenText(_flag[0],'pfd','spd','but1','but');
		}
   }
   		var aa=document.getElementsByTagName("input");
		for(var i=0;i<aa.length;i++){
			if(aa[i].type=="text"){
				aa[i].className="inputtext";
			}
		}
   
</script>
<div id="ptaskFrame" name="planPage" style="position: absolute;padding: 0px;margin: 0px;border: 1px solid #d5d5d5;right: 0.3%; overflow:hidden;width: 50%;z-index: 100;display: none;">
	<iframe name="piframe_task" id="piframe_task" scrolling="no" frameborder="0" style="width: 100%;padding: 0px;margin: 0px;border: 0;height: 500px;"></iframe>
</div>
  
</body>


<script>
function getAbsoluteLeft(objectId) {
	o = document.getElementById(objectId)
	oLeft = o.offsetLeft            
	while(o.offsetParent!=null) { 
	oParent = o.offsetParent    
	oLeft += oParent.offsetLeft 
	o = oParent
	}
	return oLeft
	}

// 解决下的兼容问题 
(function () {
	
	// hidden函数被替换为布尔类型的false
	var out = document.querySelectorAll("[onmouseout^=hidden]");
	for (var i = 0; i < out.length; i++) {
		out[i].onmouseout = function() {
			Element.hide('date_panel');
		};
	}
	// 滚动条
	var container = document.getElementById("tbl-container");
	var aa = document.getElementById("aa");
	container.style.width = (document.documentElement.clientWidth*0.99)+"px";
	container.style.height = (document.documentElement.clientHeight - 100) + "px";
	var _top = <%=userView.getBosflag().equals("hcm") ? 10 : 22 %>;
	aa.style.top = (aa.offsetTop - _top) + "px";
		
	var ua = navigator.userAgent;
	
	var match = /(msie\s|trident.*rv:)([\w.]+)/i.exec(ua);
	if (match) {
		//补上ie8 ie9下的兼容模的情况	
		if ("11.0" === match[2]||"8.0" === match[2]||"7.0"===match[2]||"9.0"===match[2]||"5.0"==match[2]) {//5.0 IE9非兼容模式
			// 表格缺失线条的问题
//			var lockeds = document.querySelectorAll(".RecordRow_self_locked,.RecordRow_self_locked_last_top");
//			for (var i = 0; i < lockeds.length; i++) {
//				if (lockeds[i]) {
//					//lockeds[i].style.backgroundColor = "transparent";
//					lockeds[i].style.zIndex = "auto";
//				}
//			}
//			var last = document.querySelectorAll(".RecordRow_self_locked_last");
//			for (var i = 0; i < last.length; i++) {
//				if (last[i]) {
//					//last[i].style.backgroundColor = "transparent";
//				}
//			}
			
			// 浏览器兼容修改，把以下函数提到上面，所有浏览器都要执行以下操作  chent delete 20171225 start
			/* // IE11下hidden函数被替换为布尔类型的false
			var out = document.querySelectorAll("[onmouseout^=hidden]");
			for (var i = 0; i < out.length; i++) {
				out[i].onmouseout = function() {
					Element.hide('date_panel');
				};
			}*/
			// 浏览器兼容修改，把以下函数提到上面，所有浏览器都要执行以下操作  chent delete 20171225 end
		}
	}
})();
function showPersonPicker(strP0400){
	
	var z=cardPage.MembersJson[strP0400];
	var objectid=cardPage.MembersJson["objectid"];
	var p = new PersonPicker({
		multiple: true,
		isSelfUser:true,//是否选择自助用户
		nbases:'Usr',//限定为在职库
		text: "确定",
		deprecate:objectid,//评价人不能指定为考核对象
		defaultSelected:z,
		isPrivExpression:false,
		callback: function (c) {
			var staffids = "";
			for (var i = 0; i < c.length; i++) {
				staffids += c[i].id + "'";
			}
			if(z!=undefined &&z!='')
				editPersonAndOpinion(strP0400,staffids,'2');
			else
				editPersonAndOpinion(strP0400,staffids,'1');
		}
	},Ext.getBody());
	p.open();
}
function showOpinionWindow(strP0400,isedit){//打开签批意见窗口交易 
	cardPage.maxindex=0;
	var map = new HashMap();
	map.put("strP0400",strP0400);
	map.put("type",'1');
	map.put("object_id",'${objectCardForm.mdobject_id}');
	map.put("plan_id",'${objectCardForm.mdplanid}');
	map.put("body_id",'${objectCardForm.body_id}');
	Rpc({functionId:'30200710261',success: function(form){
		var result = Ext.decode(form.responseText);
		if(result.succeed){
			var list=result.opinion;
			var itemList=new Array;

			Ext.each(list, function(bean){
				var ismyself=bean.ismyself;
				var color= ismyself=="0"?"blue":"green";
				var t="<div id='OpinionText"+cardPage.maxindex+"' style='margin:0 0 10px 0'><font   color='"+color+"'>"+bean.name+"&nbsp&nbsp"+bean.time+"</font>";
				if(ismyself=='1'&&isedit=='1')
					t+="&nbsp<img  src='/images/delete.gif' height='10' width='10' style=\"cursor:pointer;margin-left:10px\"  title='撤销' alt='撤销' onclick=\"deleteOpinion('OpinionText"+cardPage.maxindex+"','"+getEncodeStr(bean.time)+"','"+strP0400+"')\">";
				var value=bean.value;
				value=value.replace(/&/g,"&amp;");
				value=value.replace(/</g,"&lt;");
				value=value.replace(/>/g,"&gt;");
				value=value.replace(/\r\n/g,"<br>");
				value=value.replace(/\n/g,"<br>");
				value=value.replace(/"/g,"&quot;");
				value=value.replace(/'/g,"&#39;");
				t+="<div style='margin:0 0 0 10px'>"+value+"</div></div>";
				itemList+=t;
				cardPage.maxindex++;
			});
			
			openOpinionWindow(itemList,strP0400,isedit);
		}else{
			Ext.showAlert(result.message);
		}
	}},map);

}

function openOpinionWindow(itemList,strP0400,isedit){//签批意见window

	var win=Ext.widget("window",{
        title:'签批意见',
        height:370,
        width:500,
        layout:'vbox',
		  modal:true,
		  closeAction:'destroy',
        items: [{
     		xtype:'panel',
     		border:false,
			items:[{
				xtype:'panel',
				id:'rejectCause',
				width:490,
				height:220,
				autoScroll: true,
				bodyStyle: 'border-width: 1px 1px 0 1px;',
				html:itemList
	        },{
	         	border:false,
				xtype:'textareafield',
				id:'opinionText',
				width:490,
				height:70
	        }]
        }],
        buttons:[
	          		{xtype:'tbfill'},
	          		{
	          			text:'关闭',
	          			handler:function(){
	          				win.close();
	          			}
	          		},
	          		{
	          			text:'发送',
	          			hidden:isedit=='1'?false:true,
	          			handler:function(){

	          						if(trim(Ext.getCmp("opinionText").value)==''){
	          							Ext.showAlert("请填写评论！");
	          							return;
	          						}
									var map = new HashMap();
									map.put("strP0400",strP0400);
									map.put("type",'2');
									map.put("content",getEncodeStr(Ext.getCmp("opinionText").value));
									map.put("object_id",'${objectCardForm.mdobject_id}');
									map.put("plan_id",'${objectCardForm.mdplanid}');
									map.put("body_id",'${objectCardForm.body_id}');
									Rpc({functionId:'30200710261',success: function(form){
										var result = Ext.decode(form.responseText);
										var isok=result.isok;
										if(result.succeed){
											if(isok=='1'){
												var name='<%=userFullName%>';
												var time=result.time;
												var text=Ext.getCmp('opinionText').getValue();
												var t="<font  color='green'>"+name+"&nbsp&nbsp"+time+"</font>";
												t+="&nbsp<img src='/images/delete.gif' height='10' width='10' style=\"cursor:pointer;margin-left:10px\" title='撤销' alt='撤销' onclick=\"deleteOpinion('OpinionText"+cardPage.maxindex+"','"+getEncodeStr(time)+"','"+strP0400+"')\">";
												text=text.replace(/&/g,"&amp;");
												text=text.replace(/</g,"&lt;");
												text=text.replace(/>/g,"&gt;");
												text=text.replace(/\n/g,"<br>");
												text=text.replace(/\"/g,"&quot;");
												text=text.replace(/\'/g,"&#39;");
												t+="<div style='margin:0 0 0 10px'>"+text+"</div>";

						          				var rejectCause=Ext.getCmp('rejectCause');
						          				Ext.getCmp('opinionText').setValue('');
						          				var parentid=rejectCause.body.dom.children[0].children[0].id;
						          				var parentdiv=document.getElementById(parentid);
						          				var newdiv=document.createElement('div');
						          				newdiv.id="OpinionText"+cardPage.maxindex;

						          				newdiv.innerHTML=t;
						          				newdiv.style.margin="0 0 10px 0";
						          				parentdiv.appendChild(newdiv);

						          				var d = rejectCause.body.dom;
						          				d.scrollTop = d.scrollHeight - d.offsetHeight;
						          				cardPage.maxindex++;
											}
										}else{
											Ext.showAlert(result.message);
										}
									}},map);
	          			}
	          		}

	           ],
	           listeners:{
	        	   close:function(e){
	        		   var otext="";
	        		   for(var i=0;i<cardPage.maxindex;i++){
	        			   var dom=document.getElementById("OpinionText"+i);
	        			   if(dom!=undefined){
	        				   var d=dom.getElementsByTagName('div')[0].innerHTML;
	        				   var f=dom.getElementsByTagName('font')[0].innerHTML;
	        				   f=f.replace(/&nbsp;/g," ");
	        				   d=d.replace(/&nbsp;/g," ");
	        				   otext+=f+"\n"+d+"\n";


	        			   }
	        		   }
	        		   otext=otext.replace(/<br>/g,"\n");
	        		   otext=otext.replace(/<BR>/g,"\n");
	        		   otext=otext.replace(/&lt;/g,"<");
	        		   otext=otext.replace(/&gt;/g,">");
	        		   otext=otext.replace(/&amp;/g,"&");
	        		   otext=otext.replace(/&quot;/g,"\"");
	        		   otext=otext.replace(/&#39;/g,"'");

	        		   document.getElementById(strP0400+'opinion').value=otext;

	        	   }
	           }
  });
  win.show(); 
  Ext.getCmp('opinionText').focus(false, 100);
  var d = Ext.getCmp('rejectCause').body.dom;
	d.scrollTop = d.scrollHeight - d.offsetHeight;
}

function deleteOpinion(id,time,strP0400){//删除评价
	Ext.Msg.confirm(common.button.promptmessage,"确认撤销？",
			function(mess){
				if(mess=='yes'){
				var map = new HashMap();
				map.put("strP0400",strP0400);
				map.put("type",'3');
				map.put("time",time);
				map.put("object_id",'${objectCardForm.mdobject_id}');
				map.put("plan_id",'${objectCardForm.mdplanid}');
				map.put("body_id",'${objectCardForm.body_id}');
				Rpc({functionId:'30200710261',success: function(form){
					var result = Ext.decode(form.responseText);
					var isok=result.isok;
					if(result.succeed){
						if(isok=='1'){
							var tdiv=document.getElementById(id);
							var rejectCause=Ext.getCmp('rejectCause');
			  				var parentid=rejectCause.body.dom.children[0].children[0].id;
			  				var parentdiv=document.getElementById(parentid);
			  				parentdiv.removeChild(tdiv);
						}
					}else{
						Ext.showAlert(result.message);
					}
				}},map);
			}else
				return;
		});
}


//type 1:添加评价人 2:更新评价人
function editPersonAndOpinion( strP0400, content, type)
{
	
	
	var map = new HashMap();
	map.put("strP0400",strP0400);
	map.put("type",type);
	map.put("content",content);
	map.put("object_id",'${objectCardForm.mdobject_id}');
	map.put("plan_id",'${objectCardForm.mdplanid}');
	map.put("body_id",'${objectCardForm.body_id}');
	Rpc({functionId:'30200710259',success: function(form){
		var result = Ext.decode(form.responseText);
		var type=result.type;
		var strP0400=result.strP0400;
		var a0100List=result.a0100List;
		cardPage.MembersJson[strP0400]=a0100List;
		var strA0101=result.strA0101;
		var strA0101_="";
		var value = strA0101.split(",");
		for(var i=0;i<value.length;i++){
            if(i==3){
                strA0101_+="...";
                break;
            }
		    if(i>0)
		        strA0101_+=",";
            strA0101_+=value[i];

        }
		document.getElementById(strP0400+'rater').value=strA0101_;
		document.getElementById(strP0400+'rater').title=strA0101;
	}},map);
}

Ext.onReady(function() {
	cardPage = this; 
	var f=document.getElementById('MembersJson').value;//评价人a0100 json 选人控件使用
	if(f!=undefined&&f!="")
		cardPage.MembersJson=JSON.parse(f);
	else
		cardPage.MembersJson="";
    Ext.get(document).on("click", function(e) {
        e = e || window.event;
        var target = e.target || e.srcElement;
        // 展示任务的弹出层关闭的时机: 计划界面任务列表被点击的任务及对应的任务界面之外的区域响应点击事件时关闭 
    	if (target.className.indexOf("ttNomal3")<0){
        	//点击任务详情页面外面,要关闭任务界面,但任务处于编辑状态,需要保存再关闭
    		if(document.getElementById("ptaskFrame").style.display == "block"){
        		document.getElementById("ptaskFrame").style.display = "none";
    		}
    	}
    });
});

if(!getBrowseVersion()){//兼容非IE浏览器 样式   wangb 20171208 
	var objectCardForm = document.getElementsByName('objectCardForm')[0];//设置 form 表单width属性 不出现滚动条
	objectCardForm.style.width = '99%';
	var tempTd = objectCardForm.parentNode;//设置模板td 不需要overflow样式 
	tempTd.style.overflow = '';
}


</script>
</html>
