<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/js/dict.js"></script>
<script language="JavaScript" src="../template.js"></script>
<script language="JavaScript" src="../template_signature.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<script language="JavaScript" src="/general/sys/hjaxmanage.js"></script>
<link href="/css/css1_template.css" rel="stylesheet" type="text/css">
<link href="/general/template/general.css" rel="stylesheet" type="text/css">
<%@ page import="java.util.*,
				com.hrms.hjsj.utils.Sql_switcher,
				com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig,
				com.hjsj.hrms.actionform.general.template.TemplateForm,
				com.hrms.struts.valueobject.UserView,com.hrms.hjsj.sys.Des" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>	
		
<%
   UserView userView=(UserView)session.getAttribute(WebConstant.userView);
   EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
   String license=lockclient.getLicenseCount();
   int _version=userView.getVersion();
   if(license.equals("0"))
        _version=100+_version;
   int usedday=lockclient.getUseddays();
    String bosflag= userView.getBosflag();//得到系统的版本号
/**
String aurl = (String)request.getServerName();
	    String port=request.getServerPort()+"";
	    String prl=request.getProtocol();
	    int idx=prl.indexOf("/");
	    prl=prl.substring(0,idx);    
	    String url_s=prl+"://"+aurl+":"+port;
	    String url_p=prl+"://"+aurl+":"+port;
	*/
	String url_p=SystemConfig.getServerURL(request); 
	    
	    String dbtype=String.valueOf(Sql_switcher.searchDbServer());
		
		String fields="";//userView.getFieldpriv().toString();
//		if(fields==null||fields.trim().length()==0)
//			fields=userView.getEmp_fieldpriv().toString();
		String tables=userView.getTablepriv().toString();
	    
	    String username=userView.getUserName();
		String userFullName=null;
		userFullName=userView.getUserFullName();
		String superUser="0";
		if(userView.isSuper_admin())
			  superUser="1";
			  
		TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
		String businessModel=templateForm.getBusinessModel();
		String no_sp_opinion="false";
		if(SystemConfig.getPropertyValue("no_sp_opinion")!=null&&SystemConfig.getPropertyValue("no_sp_opinion").trim().equalsIgnoreCase("true"))
			no_sp_opinion=SystemConfig.getPropertyValue("no_sp_opinion");
		String mServerUrl=userView.getServerurl()+"/iSignatureHTML/Service.jsp";
 %>
<style>
.fixedtab 
{ 
	overflow:auto; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 	
}
</style> 
<a href="<%=url_p%>" style="display:none" id="hostname">for vpn</a>
<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
<script type="text/javascript">
Ext.Loader.setConfig({
    enabled: true,
    paths: {
        'JobtitleUL': '/module/template/templatetoolbar/jobtitle',
        'SYSP':'/components/sysExtPlugins'
    }
});  
Ext.require("SYSP.CodeTreeCombox");
Ext.require("SYSP.BigTextField");
<!--
	var isHeadCountControl=true; //编制控制
    document.body.style.overflow="hidden";///郭峰修改 2013-9-13 解决日期型控件弹出日历时，日历有时候上下跳动。edit_page.jsp也做了修改
    var template_refresh="${templateForm.refresh}";
	var url_s="<%=url_p%>";
	var nextNodeStr='${templateForm.nextNodeStr}';
	var setname='${templateForm.setname}';
	var sp_mode='${templateForm.sp_mode}';
	var modeType="${templateForm.type}"; 
	var tabid="${templateForm.tabid}"; 
	var pageno="${templateForm.pageno}";
	var isApplySpecialRole="${templateForm.isApplySpecialRole}";
	var allow_def_flow_self="${templateForm.allow_def_flow_self}";
	// 电子签章
	   var DocumentID="";
   var DocumentrecordID="";  
   var hosturl=$('hostname').href;
   var   signxml = '${templateForm.signxml}';
   var   username = '${templateForm.username}';
   var initsignature='0';
   var batchsignatureid ='0';
   var XMLDoc;
      var recordbasepre="";
   var recorda0100="";
   var documentids="";
   var returnflag='${templateForm.returnflag}'
	var infor_type='${templateForm.infor_type}';
	var businessModel="<%=businessModel%>";
	///以下三个参数为了控制附件//
	var attachment_count= '${templateForm.attachmentcount}';
	var attachmentareatotype='${templateForm.attachmentareatotype}';
	var attachmentArray = new Array(attachment_count);
	initAttachmentArray();//初始化attachmentArray
	
	function initAttachmentArray()
	{
		var areatotype = new Array();
		areatotype=attachmentareatotype.split(",");
		for (var i=0;i<areatotype.length ;i++ )
		{
			var innerarray = new Array();
			innerarray=areatotype[i].split("`");
			attachmentArray[innerarray[0]]=innerarray[1];
		}
	} 
	 // 电子签章 end
	function closeWindow()
	{
		window.parent.close();
	}
	
	
    function applysuccess(outparamters)
    {
     var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3)
	    alert(msgs);
    	<% if(businessModel.equals("0")){ %>
    	if(returnflag=='11')
    		window.open("/general/template/myapply/businessApplyList.do?b_query=link","_parent"); 
    	else if(returnflag=='12')
    	{
    			alert("报批成功!");
    			window.open("/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=12&tabid=${templateForm.tabid}","_parent"); 
    	}
    	else
			window.open("/general/template/myapply/busidesktop.do?br_query=link","_parent");  	
   		
   		<% }else if(businessModel.equals("1")){ %>
   		alert("报批成功!");
   		document.location="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&businessModel=1&tabid=${templateForm.tabid}";
   		
   		<% }else if(businessModel.equals("2")){  %>
    	alert("报批成功!");
    	var ins_id=outparamters.getValue("ins_id");
    	window.parent.returnValue=ins_id;
  		window.parent.close();
    	
    	
    	<% } %>
    }
    
    function apply0(setname,sp_mode)
    {
		 displayProcessBar();
		 setTimeout(function(){apply0_processBar(setname,sp_mode);closeProcessBar();},100);  
	}
    function apply0_processBar(setname,sp_mode)
    {
		  
    	 if(!autoSaveData()){
			closeProcessBar();
	 		return;
		 }
	     var hashvo=new ParameterSet();   	     
   	     
	     hashvo.setValue("flag","0");
	   
         var basepre="<%=(userView.getDbname())%>";
         var a0100="<%=(userView.getA0100())%>";          
	    hashvo.setValue("tabid","${templateForm.tabid}");
   	    hashvo.setValue("taskid","${templateForm.taskid}");
   	    hashvo.setValue("ins_id","${templateForm.ins_id}");
   	    hashvo.setValue("batch_task","${templateForm.batch_task}");
  	    hashvo.setValue("sp_flag","${templateForm.sp_flag}");
  	    hashvo.setValue("sp_batch","${templateForm.sp_batch}");
  	    hashvo.setValue("a0100",a0100);
  	    hashvo.setValue("pre",basepre);
  	    hashvo.setValue("id","id");  
  	   
	    var request=new Request({asynchronous:false,onSuccess:apply,onFailure:dealOnError,functionId:'0570010131'},hashvo); 
	    
	    
    }
    function isOverBz(tabid){
   		 var hashvo=new ParameterSet();
   		 hashvo.setValue("tabid",tabid); 
   		 var request=new Request({asynchronous:false,onSuccess:isOverBzSuccess,functionId:'0570040050'},hashvo); 
       		
   	}

   var hashvo0=null;
   var isSendMessage2=null;
   
   	function apply(outparamters)
   	{
   	  var judgeisllexpr=outparamters.getValue("judgeisllexpr");
	  var isSendMessage=outparamters.getValue("isSendMessage");
	  isSendMessage2 = isSendMessage;
	  if(judgeisllexpr!="1")
	  {
		 closeProcessBar();
	     alert(judgeisllexpr);
   		 return;
   	  }
      var dataset;
      dataset=${templateForm.setname};
  /*    if(dataset.getState()=="modify")
      {
      	 if(!ifcontinue())
      	   return;
      }	  
  */    
	        
	  if(sp_mode=="1")
	  {
	  
	  	var theurl="/general/template/submit_form.do?b_apply=link&tabid="+tabid;
   	        if(allow_def_flow_self=='true')
   	        	theurl="/general/template/submit_form.do?b_apply=link&tabid="+tabid+"&noObject=1";
	  	var obj_vo =window.showModalDialog(theurl,null,"dialogWidth=650px;dialogHeight=580px;status:no;scroll:no");   
		if(obj_vo)
		{
		    var param=new Object();
		    param.name=obj_vo.name;
		    param.fullname=obj_vo.fullname;		    
		    param.objecttype=obj_vo.objecttype;
		    param.pri=obj_vo.pri;
		    param.content=getEncodeStr(obj_vo.content);
		    param.sp_yj=obj_vo.sp_yj
		    //param.tabid="${templateForm.tabid}";
        	var hashvo=new ParameterSet();
        	hashvo.setValue("actor",param);
        	//hashvo.setValue("setname",setname);
       		hashvo.setValue("tabid","${templateForm.tabid}");        	
       		hashvo.setValue("sp_mode","1");
       		hashvo.setValue("selfapply","1");
       		hashvo.setValue("url_s",url_s);
       		hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
       		hashvo.setValue("specialOperate",obj_vo.specialOperate);
       		if(obj_vo.isSendMessage!=0)
       		{
       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
       		}
       		 
       		isHeadCountControl=true;
		    validateHeadCount("0","${templateForm.tabid}");
		    if(!isHeadCountControl){
				closeProcessBar();
		       	return;  
			}
       		 
       		hashvo0=hashvo;
	       	var hashvo1=new ParameterSet();
			hashvo1.setValue("sp_mode","1");
			hashvo1.setValue("tabid",tabid);
			hashvo1.setValue("objecttype",obj_vo.objecttype);
			hashvo1.setValue("name",obj_vo.name);
		    var request=new Request({asynchronous:false,onSuccess:getNextNode7,onFailure:dealOnError,functionId:'0570010155'},hashvo1);      		
	   	 //   var request=new Request({asynchronous:false,onSuccess:applysuccess,functionId:'0570010108'},hashvo); 
		}
	  }
	  else
	  {
	     //   if(iftqsp())
	        {
	        	var hashvo=new ParameterSet();
	        	hashvo.setValue("tabid","${templateForm.tabid}");
		   	     hashvo.setValue("taskid","${templateForm.taskid}");
		   	     hashvo.setValue("ins_id","${templateForm.ins_id}");
        		hashvo.setValue("sp_mode",sp_mode);
        		hashvo.setValue("selfapply","1");
        		 var request=new Request({asynchronous:false,onSuccess:getNextNode8,onFailure:dealOnError,functionId:'0570010155'},hashvo); 
	        	
	        	
	        	
        	}
	  }
	   	
   	}
   	
   	
   	function validateHeadCount(task_id,tabid)
    {
    	var hashvo=new ParameterSet();
    	hashvo.setValue("tabid",tabid);
    	hashvo.setValue("task_id","0");
    	hashvo.setValue("selfapply","1");
    	var request=new Request({asynchronous:false,onSuccess:headCountResult,functionId:'0570010167'},hashvo); 
    }
   	
   	function headCountResult(outparamters)
    {
    	var msgs=outparamters.getValue("msgs");
    	var flag=outparamters.getValue("flag");
    	if(flag=='ok')
    		isHeadCountControl=true;
    	else if(flag=='warn')
    	{
    		if(confirm(msgs))
    			isHeadCountControl=true;
    		else
    			isHeadCountControl=false;
    	}
    	else if(flag=='error')
    	{
    		alert(msgs);
    		isHeadCountControl=false;
    	}
    	
    }
   	
   	
    function getNextNode7(outparamters)
    {
    				var specialRoleRoleId=outparamters.getValue("specialRoleRoleId");   
    				
    				if(trim(specialRoleRoleId).length>2&&specialRoleRoleId.substring(0,2)=='$$')
    				{ 
    					 hashvo0.setValue("specialRoleUserStr",specialRoleRoleId.substring(2));
			        	 var request=new Request({asynchronous:false,onSuccess:applysuccess,onFailure:dealOnError,functionId:'0570010108'},hashvo0);
    				}
    				else
    				{
			        	if(specialRoleRoleId.length>0)
			        	{
			        		var temp0=specialRoleRoleId.split("`"); 
			        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=1&roleid="+temp0[0]+"&role_property="+temp0[1]+"&sp_mode=1&tabid="+tabid,null,"dialogWidth=650px;dialogHeight=450px;status:no");  
			        		if(obj_vo&&obj_vo.length>0)
			        		{ 
			        			 hashvo0.setValue("specialRoleUserStr",obj_vo);
			        			 var request=new Request({asynchronous:false,onSuccess:applysuccess,onFailure:dealOnError,functionId:'0570010108'},hashvo0);
			        		}
			        	}
			        	else
			        	{
			        		 hashvo0.setValue("specialRoleUserStr","");
			        		 var request=new Request({asynchronous:false,onSuccess:applysuccess,onFailure:dealOnError,functionId:'0570010108'},hashvo0);
			        	}  
			        }
    }
    function getNextNode8(outparamters)
    {
    				nextNodeStr=outparamters.getValue("nextNodeStr");   
    				var specialRoleNodeId="";  
    				if(trim(nextNodeStr).length>2&&nextNodeStr.substring(0,2)=='$$')
    				{ 
    					continue_apply(nextNodeStr.substring(2),isSendMessage2);  
    				}
    				else
    				{
	    	        	if(trim(nextNodeStr).length>0)
	    	        	{
	    	        		var _array0=nextNodeStr.split("`");
	    	        		for(var i=0;i<_array0.length;i++)
	    	        		{
	    	        			var _array1=_array0[i].split(":");
	    	        			if(_array1[1]=='1')
	    	        			{
	    	        				specialRoleNodeId+="`"+_array1[0];
	    	        			}
	    	        		}
	    	        	} 
	    	        	
	    	        	 
	    	        	if(specialRoleNodeId.length>0)
	    	        	{
	    	        		var obj_vo =window.showModalDialog("/general/template/myapply/busiPage.do?b_select=link&selfapply=1&sp_mode=0&specialRoleNodeId="+specialRoleNodeId,null,"dialogWidth=650px;dialogHeight=450px;status:no");  
	    	        		if(obj_vo&&obj_vo.length>0)
	    	        		{
	    	        			continue_apply(obj_vo,isSendMessage2); 
	    	        		}
	    	        	}
	    	        	else
	    	        	{
	    	        	
	    	        		continue_apply('',isSendMessage2);
	    	        	}
	    	        }
    }
   	function continue_apply(specialRoleUserStr,isSendMessage)
   	{
   	
   				 
   				var no_sp_opinion='<%=no_sp_opinion%>'
	        	var obj_vo;
	        	if(no_sp_opinion=='true')
	        	{
	        		obj_vo=new Object();
	        		obj_vo.sp_yj='01'
	        		obj_vo.pri='1';
	        		obj_vo.content='';
	        		obj_vo.isSendMessage=0;
	        		obj_vo.specialOperate='0';
	        		if(!iftqsp()){
						closeProcessBar();
	        			return;
					}
	        	}
	        	else
		        	obj_vo=window.showModalDialog("/general/template/submit_form.do?b_apply=link&isApplySpecialRole="+isApplySpecialRole+"&tabid="+tabid+"&noObject=1",null,"dialogWidth=650px;dialogHeight=540px;status:no;scroll:no");   
				if(obj_vo)
				{
				
				
	        	    var hashvo=new ParameterSet();
	        	    var param=new Object();
					param.pri=obj_vo.pri;
				    param.content=getEncodeStr(obj_vo.content);
				    param.sp_yj=obj_vo.sp_yj
	        	    hashvo.setValue("actor",param);
	        		hashvo.setValue("sp_mode","0");
	        		hashvo.setValue("tabid","${templateForm.tabid}");
	        		hashvo.setValue("selfapply","1");
	        		hashvo.setValue("isSendMessage",obj_vo.isSendMessage);
	        		hashvo.setValue("specialOperate",obj_vo.specialOperate);
	        		hashvo.setValue("specialRoleUserStr",specialRoleUserStr);
	        		
	        		
	        		isHeadCountControl=true;
				    validateHeadCount("0","${templateForm.tabid}");
				    if(!isHeadCountControl){
						closeProcessBar();
				       	return;  
					}
	        		
	        		
		       		if(obj_vo.isSendMessage!=0)
		       		{
		       			hashvo.setValue("user_h_s",obj_vo.user_h_s);
		       			hashvo.setValue("email_staff_value",obj_vo.email_staff_value);
		       		}
			       	hashvo.setValue("selectAll","1");
		   	        var request=new Request({asynchronous:false,onSuccess:applysuccess,onFailure:dealOnError,functionId:'0570010108'},hashvo); 
        		}
   	
   	
   	}
   	
   	function ifqrzx()
	{
		return ( confirm('您确认要执行此操作吗?') );
	}
	
   	function submitData(setname,tabid){
		displayProcessBar();
		setTimeout(function(){submitData_processbar(setname,tabid);},100);   
	}

   	
   	function submitData_processbar(setname,tabid)
	{
			if(!ifqrzx()){
				closeProcessBar();
			   return; 
			}
			if(!autoSaveData()){
				closeProcessBar();
	 			return;
			}
			 
			 var hashvo=new ParameterSet();
	   	     hashvo.setValue("tabid","${templateForm.tabid}");
	   	     hashvo.setValue("taskid","${templateForm.taskid}");
	   	     hashvo.setValue("ins_id","${templateForm.ins_id}");
	   	      hashvo.setValue("sp_flag","${templateForm.sp_flag}");
	  		 hashvo.setValue("flag","1");
	  	     hashvo.setValue("a0100","a0100");
	  	     hashvo.setValue("pre","pre");
	  	     hashvo.setValue("id","id");  	    
	  	     hashvo.setValue("selfapply","1");  
			 var request=new Request({asynchronous:false,onSuccess:subsuccess,onFailure:dealOnError,functionId:'0570010131'},hashvo);   
			
	}

	function subsuccess(outparamters)
	{
	      var judgeisllexpr=outparamters.getValue("judgeisllexpr");
		  if(judgeisllexpr!="1")
		  {
			closeProcessBar();
		    alert(judgeisllexpr);
		  }
		  else
		  {
		    
		    
		     var isSendMessage=outparamters.getValue("isSendMessage");
		    if(isSendMessage!=0)
		    {
		    	var thecodeurl="/general/template/submit_form.do?b_send=link`pt_type=0`isSendMessage="+isSendMessage+"`tabid="+tabid;
	   	  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
		    	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=650px;dialogHeight=500px;status:no");   
		    
		    }
		    
		    
		   isHeadCountControl=true;
		   validateHeadCount("0",tabid);
		   if(!isHeadCountControl){
			    closeProcessBar();
				return;  
		   }
		 	var hashvo=new ParameterSet();
	       	hashvo.setValue("tabid",tabid);
	       	hashvo.setValue("setname",setname);	
	       	hashvo.setValue("selfapply","1");
	        //	hashvo.setValue("a0100s",objarr);	 
	      
			       		
	   	    var request=new Request({asynchronous:false,onSuccess:isSuccess2,onFailure:dealOnError,functionId:'0570010118'},hashvo); 
		  }
	}
   	
   	 function filloutSequence(){
	   var hashvo=new ParameterSet();
      hashvo.setValue("setname",setname);
		hashvo.setValue("tabid",tabid);
	     var basepre="<%=(userView.getDbname())%>";
         var a0100="<%=(userView.getA0100())%>";          
  	    hashvo.setValue("a0100",a0100);
  	    hashvo.setValue("basepre",basepre);
	   	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010211'},hashvo); 
}
  function isSuccess(outparamters)
    {
   			document.location="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&tabid=${templateForm.tabid}";
	   
	} 	
   	 function isSuccess2(outparamters)
    {
   /* 	var basepre=outparamters.getValue("basepre");
    	var a0100=outparamters.getValue("a0100");
    	var isSendMessage=outparamters.getValue("isSendMessage");
    	if(isSendMessage==0)*/
    	{
		closeProcessBar();
    	 var msgs = outparamters.getValue("msgs");
	    if(msgs!=null&&msgs!="yes"&&msgs.length>3){
	    alert(msgs);
		}
    		<% if(businessModel.equals("0")){ %>
    		if(returnflag=='11')
    			window.open("/general/template/myapply/businessApplyList.do?b_query=link","_parent"); 
    		else if(returnflag=='12')
    			window.open("/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=12&tabid=${templateForm.tabid}","_parent"); 
    		else
    			window.open("/general/template/myapply/busidesktop.do?br_query=link","_parent"); 
	   		<% }else if(businessModel.equals("1")) {  %>
	   		alert("提交成功!");
   			document.location="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&businessModel=1&tabid=${templateForm.tabid}";
   		
	   		<% }else if(businessModel.equals("2")){ %>
	   		alert("提交成功!");
	    	var ins_id=outparamters.getValue("ins_id");
	    	window.parent.returnValue=ins_id;
	  		window.parent.close();
	   		
	   		<% } %>
	   
	    }
	/*    else 
	    {
	    	var temps=document.getElementsByName("obj");
			var objs="/<%=(userView.getDbname()+userView.getA0100())%>";
			
	    
	    	var thecodeurl="/general/template/submit_form.do?b_send=link`objs="+objs+"`isSendMessage="+isSendMessage+"`tabid="+tabid;
   	  	    var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	    	var obj_vo =window.showModalDialog(iframe_url,null,"dialogWidth=650px;dialogHeight=500px;status:no");   
	    	window.open("/general/template/myapply/busidesktop.do?br_query=link","_parent");
	    }   */
    }
   	
   	
<%
	Des des= new Des();
	String cardDbName = userView.getDbname();
	cardDbName = des.EncryPwdStr(cardDbName);
	String cardA0100 = userView.getA0100();
	cardA0100 = des.EncryPwdStr(cardA0100);
%>  	
   	/*
 =0 业务人员申请
 =1 审批
 =2 员工申请
*/
var TmplPreview1Flag=0;
var GlobalTabid = null;
function printout(tabid,printobj)
{
    if(tabid)
        GlobalTabid = tabid;
    if(!tabid)
        tabid = GlobalTabid;
      var divElement = document.getElementById("TmplPreview1div");
      if(!divElement){
          return;
      }
  if(!AxManager.setup("TmplPreview1div", "TmplPreview1", 0, 0, printout, AxManager.tmplpkgName))
           return;
      var obj = document.getElementById("TmplPreview1");
      var isload = isLoad(obj);
      if(isload==true){
          TmplPreview1Flag++;
          loadOkPrintout(obj,tabid);
      }else{
          var timer = setInterval(function(){ 
              TmplPreview1Flag++;
                obj= document.getElementById('TmplPreview1');  
                var _obj = isLoad(obj);
                if(_obj==true){
                    clearInterval(timer);
                    loadOkPrintout(obj,tabid);    
                }else if(TmplPreview1Flag==5){
                    alert("插件加载失败！");
                    clearInterval(timer);
                    TmplPreview1Flag=0;
                }    
                },2000);
      }
}
/**用于判断插件是否加载完成**/
function isLoad(obj){
    var flag = true;
    try{
        obj.SetUrl("test");
    }catch(e){
        flag = false;
    }
    return flag;
}
function loadOkPrintout(obj,tabid){
	initCard(hosturl,'<%=dbtype%>','<%=username%>','<%=userFullName%>','<%=superUser%>','<%=fields%>','<%=tables%>','TmplPreview1');
	 obj.SetTemplateID(tabid);
     /* 卡片类型：
        1: 模板
        2: 模板归档信息
        3: 员工申请临时表, g_templet_模板号
        4: 审批临时表, templet_模板号
     */
     var cardtype=3;
     obj.SetTemplateType(cardtype); 
     obj.ClearObjs();

       /* A0100参数格式：
        模板, 员工申请临时表:
        <NBASE></NBASE><A0100></A0100>
      模板归档:
        <ArchiveID></ArchiveID><NBASE></NBASE><A0100></A0100>
      审批临时表:
         <INS_ID>实例号</INS_ID><NBASE></NBASE><A0100></A0100>      
         */
     obj.AddObj('<NBASE><%=cardDbName%></NBASE><A0100><%=cardA0100%></A0100>');
     CreateSignatureJif("1");
     try { obj.SetJSessionId(AxManager.getJSessionId()); } catch(err) {}
     obj.ShowCardModal();
}
 //自动保存数据调用的方法
 function autoSaveData()
 {
   	   
	   var _save=new UpdateCommand("save");
	   _save.setAction("/ajax/ajaxService");
	   _save.setFunctionId("0570010105");
	   _save.setHint("");
	   _save.addDatasetInfo(${templateForm.setname},"all-change");
  	   return _save.autosave(); //20140925 报批报错时需有提示
 }
 
 
 
 function bz_computer(setname,tabid,ins_id,pageno)
{
	 if(!autoSaveData())
	 	return;
   	 var hashvo=new ParameterSet();	
	 bz_dataset=setname;
	 hashvo.setValue("basepre","<%=(userView.getDbname())%>");
	 hashvo.setValue("a0100","<%=(userView.getA0100())%>"); 
     hashvo.setValue("tabid",tabid);	
     hashvo.setValue("ins_id",ins_id);
     hashvo.setValue("ins_ids","");
     hashvo.setValue("pageno",pageno);
     var request=new Request({asynchronous:false,onSuccess:bz_computer2,functionId:'0570010156'},hashvo); 
}

function bz_computer2(outparamters)
{
		var tabid=outparamters.getValue("tabid");
		var ins_id=outparamters.getValue("ins_id");
		var ins_ids=outparamters.getValue("ins_ids"); 
	    var message=getDecodeStr(outparamters.getValue("message"));
	 	var pageno=outparamters.getValue("pageno"); 
	 	
	 	
		var midValue="";
		var iscontinue="1";
		if(message.length>0)
		{
			var temps=message.split(",");
			for(var i=0;i<temps.length;i++)
			{
				var temp=temps[i].split(":");
				var theURL="/general/template/templatelist/setMidVarValue.jsp?var="+getEncodeStr(temp[0])+"`type="+temp[1]+"`alength="+temp[2];
				var iframe_url="/general/query/common/iframe_query.jsp?src="+theURL;
				var return_vo =window.showModalDialog(iframe_url,null,"dialogWidth=400px;dialogHeight=200px;resizable=yes;status=no;"); 
				if(return_vo!=null&&trim(return_vo).length>0){
					midValue=midValue+","+temp[0]+":"+return_vo;
				}else
				{
					iscontinue="0";
					break;
				} 
			}
	 	}
	 	if(iscontinue==0)
	 		return;
	   	 var hashvo=new ParameterSet();	  
	     hashvo.setValue("basepre","<%=(userView.getDbname())%>");
	     hashvo.setValue("a0100","<%=(userView.getA0100())%>"); 
	     hashvo.setValue("midValue",getEncodeStr(midValue));
	     hashvo.setValue("tabid",tabid);	
	     hashvo.setValue("ins_id",ins_id);
	     hashvo.setValue("selfapply","1");
	     hashvo.setValue("ins_ids","");
	     hashvo.setValue("pageno",pageno);
	     var request=new Request({asynchronous:false,onSuccess:refreshData,functionId:'0570010132'},hashvo); 
      
}
 
 function refreshData(outparamters)
{
	var flag=outparamters.getValue("succeed");
	if(flag=="false")
		return;	
	alert("计算成功!");		
   	document.location="/general/template/myapply/busiTemplate.do?b_query=link&ins_id=0&returnflag=6&tabid=${templateForm.tabid}";
  
  }
//自定义审批流程	
function showDefFlowSelf(atabid)
{
	var win=window.open("/general/template/def_flow_self.do?b_query=init"
	      +"&task_id=0&ins_id=-1&node_id=0&tabid="+atabid+"&fromflag=myapply"    
	      ,"_parent");
	
}

//-->
</script>
<hrms:themes />
 <body  Onload="initgetDocumentid();checkBrowserSettings();"  >
<html:form action="/general/template/myapply/busiPage">
<logic:notEqual name="templateForm" property="signxml" value="">
<OBJECT id="SignatureControl"  classid="clsid:D85C89BE-263C-472D-9B6B-5264CD85B36E" codebase="/iSignatureHTML/iSignatureHTML.cab#version=8,2,2,56"  width=0  height=0  VIEWASTEXT>
<param name="ServiceUrl" value="<%=mServerUrl%>"><!--读去数据库相关信息-->
<param name="WebAutoSign" value="0">             <!--是否自动数字签名(0:不启用，1:启用)-->
<param name="PrintControlType" value="2">               <!--打印控制方式（0:不控制  1：签章服务器控制  2：开发商控制）-->
<param name="PrintWater" value="true">               	  <!--是否打印水印  -->
<param name="MenuDocVerify" value="true">                  <!--菜单验证文档-->
<logic:equal name="templateForm" property="sp_flag" value="1">
<param name="MenuServerVerify" value="false">               <!--菜单在线验证-->
<param name="MenuDigitalCert" value="false">                <!--菜单数字签名-->
<param name="MenuDocLocked" value=false>                  <!--菜单文档锁定-->
<param name="MenuDeleteSign" value=true>                 <!--菜单撤消签章-->
<param name="MenuMoveSetting" value="true">                <!--菜单禁止移动-->
</logic:equal>
<logic:notEqual name="templateForm" property="sp_flag" value="1">
<param name="MenuServerVerify" value="false">               <!--菜单在线验证-->
<param name="MenuDigitalCert" value="false">                <!--菜单数字签名-->
<param name="MenuDocLocked" value=false>                  <!--菜单文档锁定-->
<param name="MenuDeleteSign" value=false>                 <!--菜单撤消签章-->
<param name="MenuMoveSetting" value="false">                <!--菜单禁止移动-->
</logic:notEqual>
<!--param name="Weburl"  value="">        <签章服务器响应-->
</OBJECT>
</logic:notEqual>
   <bean:write name="templateForm" property="hmtlview" filter="false"/>
   <%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
  <table cellspacing="0" width="700" cellpadding="2" border="0" borderColor="black" style="border-collapse: collapse;left:4px;position:absolute">
<%
}else{
%>
  <table cellspacing="0" width="700" cellpadding="2" border="0" borderColor="black" style="border-collapse: collapse;left:3px; top:3px;position:absolute">  
<%
}
%>
 
    <tr>
          <td align="left">
           
	        <hrms:commandbutton name="save" functionId="0570010105" refresh="${templateForm.refresh}" type="all-change" setname="${templateForm.setname}">
    	      <bean:message key="button.save"/>
        	</hrms:commandbutton>
          
          <hrms:priv func_id="010704"> 	
        	<logic:equal name="templateForm" property="sp_ctrl" value="1">   
       			<button extra="button" id="applyButton" onclick="apply0('${templateForm.setname}','${templateForm.sp_mode}');">${templateForm.sp_objname}</button>
        	</logic:equal>
           </hrms:priv>
           <logic:equal name="templateForm" property="sp_ctrl" value="1">   
	     	  <logic:equal name="templateForm" property="sp_mode" value="1">    
                  <hrms:priv func_id="33001030,33101030,2701530,0C34830,32030,32126,37026,37126,37226,37226,324010130,325010130,010731,2306725,23110225,3800730">  	
				  	   <logic:equal name="templateForm" property="def_flow_self" value="1">       
				  			<button extra="button" 
				  			   onclick="showDefFlowSelf('${templateForm.tabid}');">
				  			   <bean:message key="t_template.approve.selfdefflow"/></button>
				  	   </logic:equal>
                   </hrms:priv> 
			  </logic:equal>	  
        	</logic:equal>
           <hrms:priv func_id="010703">    	 
	        <logic:notEqual name="templateForm" property="sp_ctrl" value="1">   
           		<button extra="button" id="submitButton" onclick='submitData("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.submit"/></button>
            </logic:notEqual> 
	       </hrms:priv>
	     
	       <hrms:priv func_id="010705"> 
        	<button extra="button" onclick="printout('${templateForm.tabid}','TmplPreview1');"><bean:message key="button.print"/></button>
  	       </hrms:priv>
  	        <logic:equal name="templateForm" property="sequence" value="1"> 
  	        <hrms:priv func_id="010724"> 
        	<button extra="button" onclick="filloutSequence();"><bean:message key="menu.gz.create.sequence"/></button>
  	       </hrms:priv>
  	       </logic:equal>
  	       
  	       <logic:notEqual  name="templateForm" property="returnflag" value="12"> 
	  	       <logic:notEqual  name="templateForm" property="businessModel" value="1"> 
	  	       <logic:notEqual  name="templateForm" property="businessModel" value="2"> 
	  	        <hrms:priv func_id="010714"> 	
	  	        <button extra="button" allowPushDown="false" onclick="returnbrowseprint('${templateForm.returnflag}','${templateForm.warn_id}','${templateForm.operationname}');" down="false"><bean:message key="button.return"/></button>
	            </hrms:priv>
	           </logic:notEqual>
	           </logic:notEqual>
	           <logic:equal  name="templateForm" property="businessModel" value="2"> 
	  	        <button extra="button" allowPushDown="false" onclick="closeWindow()" down="false"><bean:message key="button.close"/></button>
	           </logic:equal>
           </logic:notEqual>
           
                      
          </td>
    </tr>
  </table>

</html:form>
<div id='wait' style='position:absolute;top:285;left:80;display:none;z-index:999;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" style="font-size:18px;" height=24>
					正在处理，请稍候...
				</td>
			</tr>
		</table>
</div>
</body>
<div id="TmplPreview1div">
</div>

<script type="text/javascript">
    var fixedtabdiv=document.getElementById("fixedtabdiv");
	fixedtabdiv.style.height=document.body.clientHeight-40;
	fixedtabdiv.style.width=document.body.clientWidth-10; 
   function initCard(url,dbtype,username,userFullName,bsuper,fieldpriv,tablepriv,objname)
   {
      var obj = $(objname);      
      obj.SetURL(url);
      obj.SetDBType(dbtype);
      obj.SetUserName(username);     
      obj.SetSuperUser(bsuper);  // 1为超级用户,0非超级用户
      obj.SetUserMenuPriv(fieldpriv);  // 指标权限, 逗号分隔, 空表示全权
      obj.SetUserTablePriv(tablepriv);  // 子集权限, 逗号分隔, 空表示全权
      obj.SetHrpVersion("<%=_version%>");
      obj.SetTrialDays("<%=usedday%>","30");
  }
  
  function AxGetCodeDesc(CodeSetId, CodeItemId)
	{
	    var tmp="_"+CodeSetId+CodeItemId;
	    if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
		{
			if(CodeSetId=="UM"&&!(g_dm[tmp].P=="undefined"||g_dm[tmp].P==null||g_dm[tmp].P.length==0))
				value=g_dm[tmp].P;
			else
			    value=g_dm[tmp].V;
		}
		else
		{
			if(CodeSetId=="UN")
			{
				tmp="_UM"+CodeItemId;
				if(!(g_dm[tmp]=="undefined"||g_dm[tmp]==null))
		   			value=g_dm[tmp].V;
		   		else
		   			value="";
			}
			else
				value="";
		}
	     return  value;
	}
     function savesignature(flag){
     updateDocumentid();
  	    var hashvo=new ParameterSet(); 
  	    while(signxml.indexOf("\"")!=-1){
  	         signxml =signxml.replace("\"","'");
  	         }
      	   hashvo.setValue("signxml", getEncodeStr(signxml));
     	   hashvo.setValue("table_name", '${templateForm.setname}');
     	   hashvo.setValue("infor_type", '${templateForm.infor_type}');
     	   hashvo.setValue("ins_id", '${templateForm.ins_id}');
     	   hashvo.setValue("flag", flag);
     	   
          var In_paramters="";
          var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exesignxml,functionId:'0570040049'},hashvo);
   }
function exesignxml(outparamters){
}
	var selectfirstflag = "0";//1:先选择职位，2先选择部门 3选择单位
   
   function ${templateForm.setname}_afterChange(dataset,field,value)
   {
   	  var field_name=field.getName();
   	  var record,pfield;
   	  var value;
   	  if(selectfirstflag=="3")
   	  		return;
   	  if(field_name=="e01a1_2")
   	  {
   	  
   	  	record=dataset.getCurrent(); 
   	  	value=record.getValue("e01a1_2");
   	  	if(value!=""){
   	  	 if(selectfirstflag=="0")
   	   		selectfirstflag="1";
   	  	value=getDeptParentId(value);
   	  	pfield=dataset.getField("e0122_2");
		if(pfield!=null&&typeof(pfield)!="undefined")
		{
			record.setValue("e0122_2",value);
		}
		}else{
   	   		selectfirstflag="0";
		}
   	  }
      if(field_name=="e0122_2")
   	  {
   	  if(selectfirstflag=="0")
   	   selectfirstflag="2";
   	  	record=dataset.getCurrent(); 
   	  	value=record.getValue("e0122_2");
   	  	value=getUnitParentId(value);
   	  	pfield=dataset.getField("b0110_2");
		if(pfield!=null&&typeof(pfield)!="undefined")
		{
			record.setValue("b0110_2",value);
		}
		if(selectfirstflag=="2"){
		pfield=dataset.getField("e01a1_2");
		if(pfield!=null&&typeof(pfield)!="undefined")
		{
			
			selectfirstflag ="0";
			record.setValue("e01a1_2","");
			
		}
		}else{
			selectfirstflag ="0";
		}
   	  } 
   	   if(field_name=="b0110_2")
   	  {
	   	  if(selectfirstflag=="0")
   		   		selectfirstflag="3";
   		  if(selectfirstflag=="1")
   		  		selectfirstflag ="0"; 		
   	  	  record=dataset.getCurrent(); 
   	  	  if(selectfirstflag=="3"){
	   	  	  	pfield=dataset.getField("e0122_2");
				if(pfield!=null&&typeof(pfield)!="undefined")
				{  
					record.setValue("e0122_2","");  
				}
   	  	 		pfield=dataset.getField("e01a1_2");
				if(pfield!=null&&typeof(pfield)!="undefined")
				{  
					record.setValue("e01a1_2","");  
				}
   	  	  		selectfirstflag ="0";
   	  	  } 
   	  } 
   	   
   }
   
   
   
   
     
//////////////////处理附件 开始  郭峰//////////   
	var tempdataset=${templateForm.setname};
  	var record=tempdataset.getCurrent();
	if(record)
	{
		if(infor_type=='1')
		{
			var a0100_=record.getValue("a0100");
			var basepre_=record.getValue("basepre"); 
			executeAttachement(basepre_,a0100_);
			setCalcItemGrid(basepre_,a0100_);
		}
		else if(infor_type=='2')
		{
			var b0110_=record.getValue("B0110");
			executeAttachement(b0110_,b0110_);
	    }
		else if(infor_type=='3')
		{
			var e01a1_=record.getValue("E01A1");
			executeAttachement(e01a1_,e01a1_);
		}
	}
	        
	function executeAttachement(basepre,a0100)
	{
		for(var ii=0;ii<attachment_count;ii++)
		{
			var attachtypevalue=attachmentArray[ii];
			var attachmenturl="";
			if(infor_type=='1')
			{
				attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+a0100+"&basepre="+basepre+"&attachmenttype="+attachtypevalue+"&infor_type=1\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
			}
			else if(infor_type=='2')
			{
				attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=2\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
			}
			else if(infor_type=='3')
			{
				attachmenturl = "<iframe src=\"/general/template/upload_attachment.do?b_query=link&objectid="+a0100+"&basepre=&attachmenttype="+attachtypevalue+"&infor_type=3\" scrolling=\"auto\" width=\"100%\" height=\"100%\" frameborder=\"0\"></iframe>";
			}
			var tempobj=document.getElementById("attachmentid"+ii);
			if(tempobj!=null)
			{
				tempobj.innerHTML =attachmenturl;
			}
		}///循环 结束								
	}	
	
///////////////////调用附件 结束///////////////////////////
    
function setCalcItemGrid(inner_basepre,inner_a0100)
 {
//计算公式 wangrd 2013-12-30
    var objFormula = document.getElementsByName("item_calformula"); 
    if (objFormula!=null){  
        //template_refresh="true";//保存后 自动刷新
	 	var ctabname =tempdataset.id;
	    for (var i=0;i<objFormula.length;i++){
	       var obj =objFormula[i];	     
	       var gridid = obj.id; 
	   	   var hashvo=new ParameterSet();	
		   hashvo.setValue("gridid",gridid);	
		   hashvo.setValue("tabname",ctabname);	
		   hashvo.setValue("tabid",tabid);	
		//   hashvo.setValue("taskid",taskid);
		 // hashvo.setValue("ins_id",ins_id);
		//   hashvo.setValue("sp_batch",sp_batch);
		   hashvo.setValue("infor_type",infor_type);
		   hashvo.setValue("inner_basepre",inner_basepre);
		   hashvo.setValue("inner_a0100",inner_a0100);
		   hashvo.setValue("pageno",pageno);
		   hashvo.setValue("fromflag","myapply");
		   var request=new Request({asynchronous:false,
		        onSuccess:setCalcGrid,functionId:'0570010166'},hashvo);       
	    }
    }
 }
 
 
function setCalcGrid(outparamters)
 {
  	var calcValue=outparamters.getValue("calcValue"); 
  	var gridid=outparamters.getValue("gridid"); 
    var objFormula = document.getElementById(gridid); 
    if (objFormula!=null){      
    	objFormula.value =calcValue;    
    }	
 
 }        	        	        	
function checkBrowserSettings()
{
    AxManager.checkBrowserSettings('<%=url_p%>');
}
</script>
