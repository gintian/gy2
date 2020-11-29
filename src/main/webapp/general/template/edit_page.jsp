<%@ page contentType="text/html; charset=UTF-8"  %>

<%@ taglib uri="/tags/struts-bean" prefix="bean"  %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hrms.hjsj.utils.Sql_switcher"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
				com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hjsj.hrms.actionform.general.template.TemplateForm"%>
<%@ page import="java.util.*"%>  
<script language="JavaScript" src="/js/calendar.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>  
<script type="text/javascript" src="/js/function.js"></script>  
<script type='text/javascript' src='/module/utils/js/resource_zh_CN.js'></script>

<%@ page import="java.sql.*" %>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hrms.frame.codec.SafeCode"%>
<% 
	/**	
	/general/template/edit_form.do?b_query=link&amp;returnflag=noback&amp;business_model=0&amp;businessModel=0&amp;sp_flag=1&amp;ins_id=0&amp;tabid=48
	  @returnflag：noback 个性化设置，通过菜单链接直接进入模板，提交后仍返回模板当前页&不出现“返回”按钮&自助用户操作业务模板时无需考虑功能权限
	   
	 **/
	    String url_s=SystemConfig.getServerURL(request); 
		String url_p=url_s;
	    TemplateForm templateForm=(TemplateForm)session.getAttribute("templateForm");
	    String type=templateForm.getType();
	    String sp_flag=templateForm.getSp_flag();
	    String _static=templateForm.get_static();  //1:人事异动；2：是薪资管理	；8：保险变动；21：劳动合同；12：出国管理 ;10:单位管理机构调整;11:岗位管理机构调整
	    String isFinishedRecord=templateForm.getIsFinishedRecord()==null?"0":templateForm.getIsFinishedRecord();
	    UserView userView=(UserView)session.getAttribute(WebConstant.userView); 	
	    String  returnflag=templateForm.getReturnflag();
	    String bosflag= userView.getBosflag();
		if(bosflag.equalsIgnoreCase("bi"))
	    	returnflag="bi";
	    
	    
	   String path = request.getSession().getServletContext().getRealPath("/js");
	   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	   {
	  	  path=session.getServletContext().getResource("/js").getPath();//.substring(0);
	      if(path.indexOf(':')!=-1)
	  	  {
			 path=path.substring(1);   
	   	  }
	  	  else
	   	  {
			 path=path.substring(0);      
	   	  }
	      int nlen=path.length();
	  	  StringBuffer buf=new StringBuffer();
	   	  buf.append(path);
	  	  buf.setLength(nlen-1);
	   	  path=buf.toString();
	   }
	   userView.getHm().put("js_path",path);
	    
	    
	    
		String businessModel=templateForm.getBusinessModel();
		String pre_pendingID=templateForm.getPre_pendingID();
		String sp_objname=templateForm.getSp_objname();
		String no_sp_opinion="false";
		if(SystemConfig.getPropertyValue("no_sp_opinion")!=null&&SystemConfig.getPropertyValue("no_sp_opinion").trim().equalsIgnoreCase("true"))
			no_sp_opinion=SystemConfig.getPropertyValue("no_sp_opinion");
		String promptIndex_template="true"; //拥有检索条件模板自动提示
	    if(SystemConfig.getPropertyValue("promptIndex_template")!=null&&SystemConfig.getPropertyValue("promptIndex_template").trim().equalsIgnoreCase("false"))
			promptIndex_template="false";
		String index_template = templateForm.getIndex_template();
		if(index_template!=null&&index_template.equals("1"))
		promptIndex_template="false";
	    boolean bJobtitleVote=false;
	    if (userView.getHm().get("moduleFlag")!=null){//职称评审投票系统不是使用Ehr的用户，无权限 。
			if ("jobtitleVote".equals((String)userView.getHm().get("moduleFlag"))){
			  bJobtitleVote=true;
			}
		}
	    int usedday=0;	    
	    int _version=70;
		if (!bJobtitleVote){
			EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
			
		    String license=lockclient.getLicenseCount();	    
		    _version=userView.getVersion();
		    if(license.equals("0"))
		        _version=100+_version;
		        
		    usedday=lockclient.getUseddays();
		}
		
	    
	    String filter_by_factor=templateForm.getFilter_by_factor(); //手工选人、条件选人按检索条件过滤, 0不过滤(默认值),1过滤
		String isfilter_select="0";
		if(filter_by_factor.equals("1")&&templateForm.getSys_filter_factor()!=null&&templateForm.getSys_filter_factor().trim().length()>0)
		{
				isfilter_select="1";
		}
	    String otherObjectPrint="1";  //高级花名册|等级表是否出现打印按钮
	    if(_static.equals("2")&&!userView.isSuper_admin()&&!userView.hasTheFunction("324010103")){
  				if(isFinishedRecord.equals("1")&&userView.hasTheFunction("324010120")){
	    		
	    		}
	    		else
	    			otherObjectPrint="0";
	    }
	    
	    String infor_type=templateForm.getInfor_type();
	    String select_str="label.rsbd.candi";
	    if(!infor_type.equals("1"))
	    	select_str="column.select";
	   
	    
	    	
	    String filter_label="label.gz.condfilter";//人员筛选
	    if(infor_type.equals("2"))
	    	filter_label="label.gz.zzfilter";
	    if(infor_type.equals("3"))
	    	filter_label="label.gz.gwfilter";
		request.setAttribute("filter_label",filter_label);
		//String mServerUrl="http://"+request.getServerName()+":"+request.getServerPort()+"/iSignatureHTML/Service.jsp";
		String mServerUrl=userView.getServerurl()+"/iSignatureHTML/Service.jsp";
		String generalmessage = templateForm.getGeneralmessage();
		generalmessage = SafeCode.decode(generalmessage);
 %>

<link href="/css/css1_template.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
Ext.Loader.setConfig({
	enabled: true,
	scriptCharset:'UTF-8',
	paths: {
		'JobtitleUL': '/module/template/templatetoolbar/jobtitle',
		'SYSP':'/components/sysExtPlugins'
	}
});   
Ext.require("SYSP.CodeTreeCombox");
Ext.require("SYSP.BigTextField");
//<!--
   document.body.style.overflow="hidden";//郭峰修改 2013-9-13 解决日期型控件弹出日历时，日历有时候上下跳动。busiPage.jsp（业务申请页面）也做了修改
   var pageCount="${templateForm.pageCount}";
   var pageNum="${templateForm.page_num}";
   var template_refresh="${templateForm.refresh}";
   var isfilter_select='<%=isfilter_select%>';
   var otherObjectPrint='<%=otherObjectPrint%>';
   var promptIndex_template='<%=promptIndex_template%>';
   var no_sp_opinion='<%=no_sp_opinion%>';
   var no_priv_ctrl="${templateForm.no_priv_ctrl}";
   var isSendMessage="${templateForm.isSendMessage}";
   var url_s="<%=url_s%>"; 
   var tabid="${templateForm.tabid}";   	
   var sp_batch="${templateForm.sp_batch}";
   var batch_task="${templateForm.batch_task}";
   var setname="${templateForm.setname}";
   var operationtype='${templateForm.operationtype}'
   var sp_flag="${templateForm.sp_flag}";
   var taskid="${templateForm.taskid}";
   var ins_id="${templateForm.ins_id}";
   var cur_ins_id="${templateForm.ins_id}";
   var pageno="${templateForm.pageno}";
   var sp_mode="${templateForm.sp_mode}";
   var allow_def_flow_self="${templateForm.allow_def_flow_self}";
   var returnflag="<%=returnflag%>";   
   var staticid="${templateForm.staticid}";
   var isApplySpecialRole="${templateForm.isApplySpecialRole}";
   var businessModel="${templateForm.businessModel}";
   var businessModel_yp="${templateForm.businessModel_yp}";
   var pre_pendingID="${templateForm.pre_pendingID}";
   var ins_ids="${templateForm.ins_ids}";
   var sys_filter_factor='${templateForm.sys_filter_factor}';
   var filterStr="${templateForm.filterStr}";
   var num=${templateForm.num};
   var infor_type='${templateForm.infor_type}'
   var employee="";
   var autoInsertRecord=0;
   //前台不允许使用sql,这里去掉了 hmuster_sql
   var modeType="${templateForm.type}"; 
   var userStatus="<%=(userView.getStatus())%>";
   var DocumentID="";
   var DocumentrecordID="";  
   var   signxml = '${templateForm.signxml}';
   var   username = '${templateForm.username}';
   var initsignature='0';
   var batchsignatureid ='0';
   var XMLDoc;
      var recordbasepre="";
   var recorda0100="";
   var documentids="";
   var overflag ="";
   var nextNodeStr="${templateForm.nextNodeStr}";
   var generalmessage =  '${templateForm.generalmessage}';
   var attachment_count= '${templateForm.attachmentcount}';
   var attachmentareatotype='${templateForm.attachmentareatotype}';
   var attachmentArray = new Array(attachment_count);
   var bosflag = "<%=bosflag%>";
   initAttachmentArray();//初始化attachmentArray
   var controlHeadCount="1";  // 1:控制人员编制
	<%
	if(userView.getStatus()==4)
	{
	%>
	employee="&isEmployee=1";
	<%
	}
	%>
	
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
	
   	var selectAll=0;
    function printcardpdf(tabid)
    {
       var hashvo=new ParameterSet();
       var element=$('personlists');
       hashvo.setValue("nid",nids);
       hashvo.setValue("cardid",tabid);
       hashvo.setValue("cyear","${templateForm.cardparam.csyear}");
       hashvo.setValue("userpriv","noinfo");
       hashvo.setValue("istype","1");        
       hashvo.setValue("cmonth","${templateForm.cardparam.cmonth}");
       hashvo.setValue("season","${templateForm.cardparam.season}");
       hashvo.setValue("ctimes","${templateForm.cardparam.ctimes}");
       hashvo.setValue("cdatestart","${templateForm.cardparam.cdatestart}");
	   hashvo.setValue("cdateend","${templateForm.cardparam.cdateend}");
	   var a0100="";
	     var table=$("${templateForm.setname}");  
	    var dataset=table.getDataset();  
	     var basepre="";
	   if(infor_type=="1"){
	   hashvo.setValue("infokind","1");
	   a0100=dataset.getValue("a0100");
	    basepre=dataset.getValue("basepre"); 
	   }else if(infor_type=="2"){
	   hashvo.setValue("infokind","2");
	   a0100=dataset.getValue("B0110"); 
	   }else if(infor_type=="3"){
	   hashvo.setValue("infokind","3");
	   a0100=dataset.getValue("E01A1"); 
	   }
	   
	   hashvo.setValue("querytype","${templateForm.cardparam.queryflagtype}");
       hashvo.setValue("userbase",basepre);
       var nids=new Array();
       nids.push(a0100);
       hashvo.setValue("nid",nids);
       var In_paramters="exce=PDF";  
       var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showPDF,functionId:'07020100015'},hashvo);
    }
//-->
</script>
<script language="javascript" src="/general/sys/hjaxmanage.js"></script>
<script language="JavaScript" src="template.js"></script>

<script language="JavaScript" src="template_signature.js"></script>

<%
	int i=4; 
	
    String dbtype=String.valueOf(Sql_switcher.searchDbServer());
	String fields=userView.getFieldpriv().toString();
	String nodeprive = templateForm.getNodeprive();
	if(nodeprive!=null&&!nodeprive.equals("-1")&&nodeprive.trim().length()>3)
		fields =nodeprive;
	String tables=userView.getTablepriv().toString();

	String username=userView.getUserName();
	String userFullName=null;
	userFullName=userView.getUserFullName();
	String superUser="0";
	
	String setname=templateForm.getSetname();
	if(userView.isSuper_admin())
	  superUser="1";
	else
    {
       if(fields==null||fields.length()<=0)
	       fields=",";
	   if(tables==null||tables.length()<=0)
	       tables=","; 
	}
%>
<hrms:themes></hrms:themes>
<link rel="stylesheet" href="/components/tableFactory/tableGrid-theme/tableGrid-theme-all.css" type="text/css" />
<link href="/general/template/general.css" rel="stylesheet" type="text/css">

 <body  Onload="validateIndex();checkBrowserSettings();">
<html:form action="/general/template/edit_page">
<html:hidden name="templateForm" property="no_sp_yj"/>
<logic:notEqual name="templateForm" property="signxml" value="">
<OBJECT id="SignatureControl"  classid="clsid:D85C89BE-263C-472D-9B6B-5264CD85B36E" codebase="/iSignatureHTML/iSignatureHTML.cab#version=8,2,2,56" width=0 height=0 VIEWASTEXT>
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
   <table cellspacing="0" width="850" cellpadding="<%if("hcm".equals(bosflag)){ %>0<%}else{%>2<%}%>" border="0" borderColor="black" style="border-collapse: collapse;">
    <tr>
	  <td width="100">
	 	<span style="vertical-align: middle;">
        	<table extra="datapilot" id="${templateForm.setname}" dataset="${templateForm.setname}" buttons="movefirst,moveprev,movenext,movelast" cellspacing="1" cellpadding="0"></table>  
	 	</span>
	 </td>
      <td align="left" nowrap>
      	<span style="vertical-align: middle;">
      
      
      
<% if(businessModel==null||(!businessModel.equals("2")&&!businessModel.equals("61")&&!businessModel.equals("62")&&!businessModel.equals("71")&&!businessModel.equals("72"))){ %>
 		<logic:equal name="templateForm" property="ins_id" value="0">
  		 <logic:equal name="templateForm" property="operationtype" value="8">  
			<hrms:priv func_id=""> 
		
			<button extra="button" class="mybutton" id="mybutton" onclick='combine();'  allowPushDown="false" down="false">合并</button>
			</hrms:priv>
		</logic:equal>
		 <logic:equal name="templateForm" property="operationtype" value="9">  
			<hrms:priv func_id=""> 
		
			<button extra="button" class="mybutton" id="mybutton" onclick='combine();'  allowPushDown="false" down="false">划转</button>
			</hrms:priv>
		</logic:equal>
		</logic:equal>
      <logic:equal name="templateForm" property="sp_flag" value="1"> 
	     <logic:notEqual name="templateForm" property="taskState" value="5"> 
	     
	      <hrms:priv func_id="010730,32103,37003,37103,37203,37303,33001008,2701508,0C34808,32008,325010108,324010108,2306708,23110208,33101008,3800708">  
	       <!-- 撤销 --> <button extra="button"  class="mybutton" id="abolishButton" onclick='delete_obj("${templateForm.setname}","${templateForm.tabid}","${templateForm.ins_id}","${templateForm.pageno}");'><bean:message key="button.abolish"/></button>
		  </hrms:priv>
		  
		  
		 </logic:notEqual>
	   </logic:equal>
	    <logic:equal name="templateForm" property="ins_id" value="0">  
          <logic:notEqual name="templateForm" property="operationtype" value="0"> 
           <logic:notEqual name="templateForm" property="operationtype" value="5"> 
           <% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限  %>
	            <button extra="button"  class="mybutton" menu="menu2" allowPushDown="false"  down="false">
	           <bean:message key="<%=select_str%>"/><!-- 选人 -->
	            </button>
           
           <% }else{ %>
             <hrms:priv func_id="32109,37009,37109,37209,37309,33001009,33101009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709"> 
	            <button extra="button"  class="mybutton" menu="menu2" allowPushDown="false"  down="false">
	           <bean:message key="<%=select_str%>"/><!-- 选人 -->
	            </button>
             </hrms:priv>
           <% } %>    
           </logic:notEqual> 
          </logic:notEqual> 
	      <logic:equal name="templateForm" property="operationtype" value="0">   
	       <% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限  %> 
	      		 <button extra="button"  class="mybutton" onclick='add_newobj("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.insert"/></button>
	        <% }else{ %>
	        <hrms:priv func_id="32107,37007,37107,37207,37307,33001007,33101007,2701507,0C34807,32007,325010107,324010107,2306707,23110207,3800707"> 
	        <!-- 新增 --><button extra="button"  class="mybutton" onclick='add_newobj("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.insert"/></button>
            </hrms:priv>
             <% } %> 
             
          </logic:equal> 
           <logic:equal name="templateForm" property="operationtype" value="5">    
           <% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限   %> 
	       		<button extra="button"  class="mybutton" onclick='add_newobj("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.insert"/></button>
	        <% }else{ %>
	        <hrms:priv func_id="32107,37007,37107,37207,37307,33001007,33101007,2701507,0C34807,32007,325010107,324010107,2306707,23110207,3800707"> 
	        <!-- 新增 --><button extra="button"  class="mybutton" onclick='add_newobj("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.insert"/></button>
            </hrms:priv>
             <% } %> 
          </logic:equal> 
        </logic:equal>  
  <% } %>  
  
  
  
      
    <%if(businessModel==null||(!businessModel.equals("61")&&!businessModel.equals("62")&&!businessModel.equals("71")&&!businessModel.equals("72"))){ %>    
           
	   <logic:equal name="templateForm" property="sp_flag" value="1">   
	   	<hrms:priv func_id="0570010105"></hrms:priv> 
	        <hrms:commandbutton  name="save"  functionId="0570010105" refresh="${templateForm.refresh}" type="all-change" setname="${templateForm.setname}">
    	      <bean:message key="button.save" />
        	</hrms:commandbutton> 
	        <logic:equal name="templateForm" property="isDisSubMeetingButton" value="1"> 	
		    	<hrms:priv func_id="3800733" >
		        	<logic:notEqual name="templateForm" property="ins_id" value="0">  	
		        	<button extra="button"  class="mybutton" onclick='subMeeting()' ><bean:message key="t_template.jobtitle.submeeting" /> </button>
	         		</logic:notEqual>   
				</hrms:priv>        	
        	</logic:equal>   
        </logic:equal>
     <% } %>
        
    <% if(businessModel==null||!businessModel.equals("2")){ %>  
  	  <% if(businessModel==null||(!businessModel.equals("61")&&!businessModel.equals("62")&&!businessModel.equals("71")&&!businessModel.equals("72"))){
  		  StringBuffer pristr = userView.getFuncpriv();
  		  pristr.toString();
  		  %>
	    <logic:equal name="templateForm" property="sp_flag" value="1">
		    <logic:equal name="templateForm" property="sequence" value="1">
		    	<% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限  %> 
		    		<button extra="button"  class="mybutton" menu="menu4" allowPushDown="false"  down="false"><bean:message key="menu.gz.batch"/></button>
		    	<% }else{ %>
		    	<hrms:priv func_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,32105,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,010724,32124,37024,37124,37224,37324,33001028,33101028,2701528,0C34828,32028,324010124,325010124,2306724,23110224,3800719,3800705,3800728"  >
		    		<button extra="button"  class="mybutton" menu="menu4" allowPushDown="false"  down="false"><bean:message key="menu.gz.batch"/></button>
		        </hrms:priv>
 				<% } %>

		    </logic:equal>
		    <logic:notEqual name="templateForm" property="sequence" value="1">
		    	<% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限  %> 
		    		<button extra="button"  class="mybutton" menu="menu4" allowPushDown="false"  down="false"><bean:message key="menu.gz.batch"/></button>		    	
		    	<% }else{ %>
		    	<hrms:priv func_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,32105,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,3800719,3800705"  >
		    		<button extra="button"  class="mybutton" menu="menu4" allowPushDown="false"  down="false"><bean:message key="menu.gz.batch"/></button>
		        </hrms:priv>
		        <% } %>
		    </logic:notEqual>
        </logic:equal>	   
         <% } %> 
 
	  	<hrms:priv func_id="33001018,33101018,2701518,0C34818,32018,32118,37018,37118,37218,37318,324010118,325010118,010708,2306718,23110218,3800718">        
	  		<button  class="mybutton" extra="button" onclick="to_person_filter('${templateForm.tabid}');"><bean:message key="<%=filter_label %>"/></button><!--人员筛选（机构和岗位筛选） -->
	  	</hrms:priv>	
  	<% } %>  
  		<%  
  		if(_static.equals("2")&&!userView.isSuper_admin()&&!userView.hasTheFunction("324010103")){
  				if(isFinishedRecord.equals("1")){
  		 %>
  		 	 <button extra="button"  class="mybutton" menu="menu1" allowPushDown="false"  down="false"><bean:message key="infor.menu.print"/></button>
  		 	<% 
  				}
  		}else{ %>
  		<hrms:priv func_id="32104,37004,37104,37204,37304,33001003,33101003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,010705,3800703,3800703">
  			 <button extra="button"  class="mybutton" menu="menu1" allowPushDown="false"  down="false"><bean:message key="infor.menu.print"/></button>
  			 </hrms:priv>
  		<% 
  				}
  		%>
         
  <% if(businessModel==null||(!businessModel.equals("61")&&!businessModel.equals("62")&&!businessModel.equals("71")&&!businessModel.equals("72"))){ %>    	
     	  <logic:equal name="templateForm" property="sp_ctrl" value="1"> 
     	     <logic:equal name="templateForm" property="sp_mode" value="1">     	   
		    	<logic:equal name="templateForm" property="ins_id" value="0">          
	           		<button extra="button"  class="mybutton" id="applyButton" onclick='apply("${templateForm.setname}","${templateForm.sp_mode}");'><bean:message key="button.appeal"/></button><!-- 报批 -->
				  	<hrms:priv func_id="33001030,33101030,2701530,0C34830,32030,32126,37026,37126,37226,37226,324010130,325010130,010731,2306725,23110225,3800730">  
				  	   <logic:equal name="templateForm" property="def_flow_self" value="1">       
				  			<button extra="button"  class="mybutton"
				  			   onclick="showDefFlowSelf('${templateForm.tabid}');"><bean:message key="t_template.approve.selfdefflow"/></button>
				  	   </logic:equal>
				  	</hrms:priv>
	        	</logic:equal>  
		    	<logic:notEqual name="templateForm" property="ins_id" value="0">  
		    	  	    <logic:equal name="templateForm" property="sp_flag" value="1"> 
	           				 <logic:equal name="templateForm" property="isFinishTask" value="0"> 	
	           					<button  class="mybutton" extra="button" id="applyButton" onclick='assign("${templateForm.setname}","${templateForm.ins_id}");'><bean:message key="button.apply"/></button><!-- 审批 -->
				        	 </logic:equal>
				        	 <logic:equal name="templateForm" property="isFinishTask" value="1"> 
				        	 	<button  class="mybutton" extra="button" id="submitButton" onclick='finishedTask("${templateForm.ins_id}","${templateForm.taskid}")'><bean:message key="button.submit"/></button><!-- 提交 -->
				        	 </logic:equal>
				        </logic:equal>         
	        	</logic:notEqual>  
	        	
        	 </logic:equal>	        		                 
     	     <logic:equal name="templateForm" property="sp_mode" value="0">
		    	<logic:equal name="templateForm" property="ins_id" value="0">          
	           		<button extra="button"  class="mybutton" id="applyButton" onclick='apply("${templateForm.setname}","${templateForm.sp_mode}");'>${templateForm.sp_objname}</button>
	        	</logic:equal>  
		    	<logic:notEqual name="templateForm" property="ins_id" value="0">  
		    	  	    <logic:equal name="templateForm" property="sp_flag" value="1"> 
		    	  	     <logic:equal name="templateForm" property="isFinishTask" value="0"> 	
		    	  	       <logic:equal name="templateForm" property="startflag" value="0"> 
	           				
	           				  <button extra="button"  class="mybutton" id="regectButton" onclick='assign("${templateForm.setname}","${templateForm.ins_id}","2",this);'><bean:message key="button.reject"/></button>

						   </logic:equal>  	           				  
	           				<button extra="button"  class="mybutton" id="applyButton" onclick='assign("${templateForm.setname}","${templateForm.ins_id}","1",this);'>${templateForm.sp_objname}</button>
				       	 </logic:equal>
				       	 <logic:equal name="templateForm" property="isFinishTask" value="1"> 
				        	 	<button extra="button"  class="mybutton" id="submitButton" onclick='finishedTask("${templateForm.ins_id}","${templateForm.taskid}")'><bean:message key="button.submit"/></button>
				         </logic:equal>
				       	 
				        </logic:equal>         
	        	</logic:notEqual>  
    	     </logic:equal>	        		                 
	
       	 </logic:equal>
       	 
		 <logic:equal name="templateForm" property="ins_id" value="0">         	 
	        <logic:notEqual name="templateForm" property="sp_ctrl" value="1"> 
           		<button extra="button"  class="mybutton" id="submitButton" onclick='submitData("${templateForm.setname}","${templateForm.tabid}");'><bean:message key="button.submit"/></button>
            </logic:notEqual> 
	     </logic:equal> 
	     
	     
<% if(businessModel==null||!businessModel.equals("2")){ %>	     
	     <logic:equal name="templateForm" property="sp_flag" value="1"> 
	       <hrms:priv func_id="0C34831,2306726,23110226,32031,33101031,33001031,2701531,324010131,325010131,32127,37027,37127,37227,37327,32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,32111,37011,37111,37211,37309,33001004,33101004,2701504,0C34804,32004,324010104,325010104,2306704,23110204,32106,37006,37106,37206,37309,33001006,33101006,2701506,0C34806,32006,324010106,325010106,2306706,23110206,3800704,3800706,3800716,3800731"> 
	     	     
            <button  class="mybutton" extra="button" menu="menu3" allowPushDown="false"  down="false"><bean:message key="menu.gz.options"/></button><!-- 设置 -->
          </hrms:priv>
         </logic:equal>	 
<% } %>


<% } %>

<%if(returnflag==null||!returnflag.equalsIgnoreCase("noback")){  %>
     <hrms:priv func_id="010732,0C34832,2306727,23110227,32032,33101032,33001032,2701532,324010132,325010133,32128,37028,37128,37228,37328,3800732"> 

         <logic:equal name="templateForm" property="sp_ctrl" value="1"> 	
         <logic:notEqual  name="templateForm" property="ins_id" value="0">
          <button extra="button"  class="mybutton"   onclick="open_showyj()"  allowPushDown="false"  down="false">审批过程</button>

         </logic:notEqual>
         </logic:equal>

    </hrms:priv>
<%} %>
          
          
      <logic:equal name="templateForm" property="isFinishTask" value="0"> 	     
	<%   if(businessModel.equals("61")||businessModel.equals("71")){ %>
		
	<button extra="button"  class="mybutton" onclick='pubOpinion("${templateForm.taskid}","<%=(businessModel.charAt(0))%>");'  allowPushDown="false"  down="false" ><bean:message key="general.template.publishopinion"/></button>
	<%   }  %>
       </logic:equal>  
          
        
         <% if(businessModel!=null&&businessModel.equals("2")){ %>
        	 <button extra="button" allowPushDown="false" class="mybutton" onclick='javascript:window.parent.close();' down="false"><bean:message key="button.close"/></button>
         <% }else {
         
		         if(returnflag!=null&&(returnflag.equals("1")||returnflag.equals("list")||returnflag.equals("5")||returnflag.equals("7")||returnflag.equals("9")||returnflag.equals("bi")||returnflag.equals("10")||returnflag.equals("8")||returnflag.equals("3")||returnflag.equals("6"))&&(businessModel==null||!businessModel.equals("4"))&&!(request.getParameter("model")!=null&&((String)request.getParameter("model")).equals("yp"))&&!(sp_flag!=null&&sp_flag.equals("2"))){
		 
			 		 if(businessModel==null||(!businessModel.equals("2")&&!businessModel.equals("61")&&!businessModel.equals("62")&&!businessModel.equals("71")&&!businessModel.equals("72"))){ 
			          %>
			       		  <button extra="button" allowPushDown="false"  class="mybutton" onclick='showList("<%=returnflag%>","${templateForm.warn_id}","${templateForm.operationname}");' down="false"><bean:message key="kjg.title.listtable"/></button>
			          <% 
			          }
		   
		         }
		     if(returnflag==null ||!returnflag.equals("novisible")){ 
	         	 if(returnflag==null||!returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限
	         		 
	         		 if(type!=null&&!"".equals(type)&&type.charAt(0)=='t'){
	         %>
	         		  <button extra="button" allowPushDown="false"  class="mybutton" onclick='returnbrowseprint("<%=returnflag%>","${templateForm.warn_id}","${templateForm.operationname}","<%=bosflag%>");' down="false"><bean:message key="button.return"/></button>	
	         <%			 
	         		 }
	         		 else
	         		 {
	         %>
	 		 
		         <hrms:priv func_id="32114,37014,37114,37214,37314,010714,33001014,33101014,2701514,0C34814,32014,325010114,324010114,2306714,23110214,3800714"> 
		  	    	  <button extra="button" allowPushDown="false"  class="mybutton" onclick='returnbrowseprint("<%=returnflag%>","${templateForm.warn_id}","${templateForm.operationname}","<%=bosflag%>");' down="false"><bean:message key="button.return"/></button>	
		         </hrms:priv> 
      	<% 
	         		 }
	         	 }
	         	 
	       } 
      	 } %>
      <!--  
           <hrms:priv func_id="3206"> 
          <logic:equal name="templateForm" property="sp_flag" value="1"> 
           <logic:notEqual name="templateForm" property="signxml" value="">
          <button extra="button" allowPushDown="false" onclick='batchSignature(1);' down="false" nowrap  id="sign_div" >批量签章</button>
          </logic:notEqual>
          </logic:equal>
            </hrms:priv>
      -->
         </span>  
      </td>
      <td align="left" nowrap >                          
	 <% if(businessModel==null||!businessModel.equals("2")){ %><!-- 右侧的快捷查询框 -->
	     <logic:equal name="templateForm" property="ins_id" value="0">           
           <logic:notEqual name="templateForm" property="operationtype" value="0"> 
            <logic:notEqual name="templateForm" property="operationtype" value="5"> 
   	  	 	
   	  	 <hrms:priv func_id="32109,37009,37109,37209,37309,33001009,33101009,2701509,0C34809,32009,325010109,324010109,2306709,23110209,3800709"> 	
   	  	 	<logic:equal name="templateForm" property="infor_type" value="1">     
   	  	 	<bean:message key="label.title.name"/>
   	  	 	<input type="text" name="a0101" value=""  class="editor" style="width:100px;font-size:10pt;text-align:left" id="a0101" onkeyup='query("${templateForm.sys_filter_factor}");showSelectBox(this);' onkeydown="updown(event);" title='<%=generalmessage %>'>
   	  	 	</logic:equal>
   	  	 	<logic:equal name="templateForm" property="infor_type" value="2">     
   	  	 	<bean:message key="general.inform.org.organizationName"/>
   	  	 	<input type="text" name="a0101" value=""  class="editor" style="width:100px;font-size:10pt;text-align:left" id="a0101" onkeyup='query("${templateForm.sys_filter_factor}");showSelectBox(this);' onkeydown="updown(event);" title='<%=generalmessage %>'>
   	  	 	</logic:equal>
   	  	 	<logic:equal name="templateForm" property="infor_type" value="3">     
   	  	 	<bean:message key="kq.shift.employee.e01a1"/>
   	  	 	<input type="text" name="a0101" value=""  class="editor" style="width:100px;font-size:10pt;text-align:left" id="a0101" onkeyup='query("${templateForm.sys_filter_factor}");showSelectBox(this);' onkeydown="updown(event);" title='<%=generalmessage %>'>
   	  	 	</logic:equal>
   	  	</hrms:priv>
   	  	 	
            </logic:notEqual>  
           </logic:notEqual>  
	     </logic:equal>    
	     
	                               	           	 
	 <% } %>
  	<hrms:menubar menu="menu2" id="menubar2" container="" visible="false">
	  	<logic:notEqual name="templateForm" property="no_priv_ctrl" value="1"> 
       		<logic:equal name="templateForm" property="infor_type" value="1">     
       		<hrms:menuitem name="mitem1" label="menu.hand.select" icon="/images/quick_query.gif" url="get_hand_query('${templateForm.setname}');"  enabled="true" visible="true"/>
       		</logic:equal> 
       		<logic:notEqual name="templateForm" property="infor_type" value="1">     
       		<hrms:menuitem name="mitem1" label="jx.eval.handSel" icon="/images/quick_query.gif" url="get_hand_query('${templateForm.setname}');"  enabled="true" visible="true"/>
       		</logic:notEqual>
       	</logic:notEqual>
       		<hrms:menuitem name="mitem2" label="menu.simple.query" icon="/images/quick_query.gif" url="get_common_query('${templateForm.setname}','${templateForm.strpres}','1','');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem3" label="menu.general.query" icon="/images/quick_query.gif" url="get_common_query('${templateForm.setname}','${templateForm.strpres}','2','');"  enabled="true" visible="true"/>
  	</hrms:menubar>    
  	<hrms:menubar menu="menu3" id="menubar3" container="" visible="false">
       		<hrms:menuitem name="mitem1" label="menu.gz.appdate" icon="/images/waiting.gif" url="setapp_date();" function_id="0C34831,2306726,23110226,32031,33101031,33001031,2701531,324010131,325010132,32127,37027,37127,37227,37327,3800731" enabled="true" visible="true"/>
       	<%if(infor_type.equals("1")){ %>
       		<hrms:menuitem name="mitem2" label="menu.gz.variable" icon="" url="setTempVar('${templateForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716"  enabled="true" visible="true"/>
       	<% }else if(infor_type.equals("2")){ %>
       	    <hrms:menuitem name="mitem2" label="menu.gz.variable" icon="" url="setTempVar('${templateForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716"  enabled="true" visible="true"/> 
       	<%}else if(infor_type.equals("3")){ %>
       	    <hrms:menuitem name="mitem2" label="menu.gz.variable" icon="" url="setTempVar('${templateForm.tabid}');"  function_id="32110,37010,37110,37210,37310,33001016,33101016,2701516,0C34816,32016,324010116,325010116,2306716,23110216,3800716"  enabled="true" visible="true"/> 
       	<%} %>
       		<hrms:menuitem name="mitem3" label="menu.gz.formula" icon="" url="setFormula('${templateForm.tabid}');"  enabled="true" visible="true" function_id="32111,37011,37111,37211,37309,33001004,33101004,2701504,0C34804,32004,324010104,325010104,2306704,23110204,3800704"/>
       		<hrms:menuitem name="mitem4" label="校验公式" icon="" url="chkFormula('${templateForm.tabid}');"  enabled="true" visible="true" function_id="32106,37006,37106,37206,37309,33001006,33101006,2701506,0C34806,32006,324010106,325010106,2306706,23110206,3800706"/>
  	</hrms:menubar> 
  	<hrms:menubar menu="menu4" id="menubar4" container="" visible="false">
  	
  			<% if(returnflag.equalsIgnoreCase("noback")){ //自助用户通过链接直接进入模板，无需权限  %> 
  			<hrms:menuitem name="mitem3" label="批量修改" icon="" url="batchupdatefields('${templateForm.tabid}','${templateForm.pageno}')" enabled="true" visible="true"  />
            <logic:notEqual name="templateForm" property="sp_flag" value="2">   	
       			<hrms:menuitem name="mitem1" label="button.replace" icon="" url="copydata('${templateForm.setname}','${templateForm.ins_id}');"  enabled="true" visible="true"    />
           </logic:notEqual>  
             			
       		<hrms:menuitem name="mitem2" label="menu.gz.batch.compute" icon="" url="bz_computer(${templateForm.setname},'${templateForm.tabid}','${templateForm.ins_id}','${templateForm.pageno}');"  enabled="true" visible="true"    />
       		 <logic:equal name="templateForm" property="sequence" value="1"> 
       		<hrms:menuitem name="mitem2" label="menu.gz.create.sequence" icon="" url="filloutSequence();"  enabled="true" visible="true"  />
       		</logic:equal>
  			
  			<% }else{  %>
  	
  			<hrms:menuitem name="mitem3" label="批量修改" icon="" url="batchupdatefields('${templateForm.tabid}','${templateForm.pageno}')" enabled="true" visible="true" function_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719" />
            <logic:notEqual name="templateForm" property="sp_flag" value="2">   	
       			<hrms:menuitem name="mitem1" label="button.replace" icon="" url="copydata('${templateForm.setname}','${templateForm.ins_id}');"  enabled="true" visible="true"  function_id="33001019,33101019,2701519,0C34819,32019,32119,37019,37119,37219,37319,324010119,325010119,2306719,23110219,3800719"  />
           </logic:notEqual>  
             			
       		<hrms:menuitem name="mitem2" label="menu.gz.batch.compute" icon="" url="bz_computer(${templateForm.setname},'${templateForm.tabid}','${templateForm.ins_id}','${templateForm.pageno}');"  enabled="true" visible="true" function_id="32105,37005,37105,37205,37305,33001005,33101005,2701505,0C34805,32005,324010105,325010105,2306705,23110205,3800705"  />
       		 <logic:equal name="templateForm" property="sequence" value="1"> 
       		<hrms:menuitem name="mitem2" label="menu.gz.create.sequence" icon="" url="filloutSequence();"  enabled="true" visible="true" function_id="010724,32124,37024,37124,37224,37324,33001028,33101028,2701528,0C34828,32028,324010124,325010124,2306724,23110224,3800728"  />
       		</logic:equal>
       		<% } %>
  	</hrms:menubar>   
     	   
  	<hrms:menubar menu="menu1" id="menubar1" container="" visible="false">
  		
  		<%  
  		if(_static.equals("2")&&!userView.isSuper_admin()&&!userView.hasTheFunction("324010103")){
  				if(isFinishedRecord.equals("1")){
  		 %>
  		<hrms:menuitem name="mitem1" label="${templateForm.name}" icon="/images/print.gif" function_id="324010120" url="">
       		<hrms:menuitem name="mitem4" label="button.print" icon="/images/print.gif" url="judgelexpr();"  enabled="true" visible="true"    /><!--打印点击时调用加载控件 -->
       		<logic:equal name="templateForm" property="infor_type" value="1"> 
       		<hrms:menuitem name="mitem3" label="menu.gz.currpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="menu.gz.allpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="menu.gz.selpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal> 
       		<logic:equal name="templateForm" property="infor_type" value="2"> 
       		<hrms:menuitem name="mitem3" label="当前机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="全部机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="部分机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal>
       		<logic:equal name="templateForm" property="infor_type" value="3"> 
       		<hrms:menuitem name="mitem3" label="当前岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="全部岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="部分岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal>      		
    	</hrms:menuitem>
  		
  		<% 
  				}
  		}else{ %>
    	<hrms:menuitem name="mitem1" label="${templateForm.name}"  function_id="010705,32104,37004,37104,37204,37304,33001003,33101003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,3800703,3800703"  icon="/images/print.gif" url="">
       		<hrms:menuitem name="mitem4" label="button.print" icon="/images/print.gif" url="judgelexpr();"  enabled="true" visible="true"  /><!-- 打印点击时调用加载控件 -->
       		<logic:equal name="templateForm" property="infor_type" value="1"> 
       		<hrms:menuitem name="mitem3" label="menu.gz.currpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="menu.gz.allpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="menu.gz.selpdf" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal> 
       		<logic:equal name="templateForm" property="infor_type" value="2"> 
       		<hrms:menuitem name="mitem3" label="当前机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="全部机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="部分机构生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal>
       		<logic:equal name="templateForm" property="infor_type" value="3"> 
       		<hrms:menuitem name="mitem3" label="当前岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem2" label="全部岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','1','0','-1');"  enabled="true" visible="true"/>
       		<hrms:menuitem name="mitem5" label="部分岗位生成PDF" icon="/images/export.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','2','0','-1');"  enabled="true" visible="true"/>
       		</logic:equal>      		
    	</hrms:menuitem>
    	<% } %>
    	
    	<logic:equal name="templateForm" property="checkhmuster" value="1">
    		<hrms:menuitem name="mitem10" label="org.performance.print.highroster" icon="/images/print.gif" url="printInform('${templateForm.ins_id}','${templateForm.tabid}');"  enabled="true" visible="true" function_id=""/>
    	</logic:equal>
    	<logic:notEqual name="templateForm" property="checkhmuster" value="1">
    		<hrms:menuitem name="mitem10" label="org.performance.print.highroster" icon="/images/print.gif" url="printInform('${templateForm.ins_id}','${templateForm.tabid}');"  enabled="false" visible="false" function_id=""/>
    	</logic:notEqual>
    	
    	<% if(otherObjectPrint.equals("1")){ %>
    	<logic:iterate id="element"  name="templateForm"  property="outformlist" indexId="index">
            <%
            	LazyDynaBean item=(LazyDynaBean)pageContext.getAttribute("element");
            	String id=(String)item.get("id");
            	String name=(String)item.get("name");
            	String flag=(String)item.get("flag");
            	String tabid=(String)item.get("tabid");
            	String jsfunc="print(\""+id+"\",\""+flag+"\",\""+tabid+"\");";
            	String pactive="printActive(\""+id+"\",\"CardPreview1\",\""+setname+"\");";
            	String cardpdf="printcardpdf(\""+id+"\");";
            	if(flag!=null&&flag.equals("2")){
            		jsfunc=cardpdf;
            	}
            	++i;
            	
            %>        	
                  <hrms:menuitem name='<%="mitem"+i%>' label='<%=name%>' icon="" url='<%=jsfunc%>'  function_id="010705,324010120,32104,37004,37104,37204,37304,33001003,33101003,2701503,0C34803,32003,324010103,325010103,2306703,23110203,3800703,3800703"  command="" enabled="true" visible="true">
                    <%if(flag!=null&&flag.equals("2")){ %>
                     <hrms:menuitem name="mitem4" label="button.print" icon="/images/print.gif" url='<%=pactive%>'    enabled="true" visible="true"/><!-- 打印点击时调用加载控件 -->
       		        <logic:equal name="templateForm" property="infor_type" value="1"> 
	                     <hrms:menuitem name="mitem3" label="menu.gz.currpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
	                  </logic:equal>
	                  <logic:equal name="templateForm" property="infor_type" value="2"> 
	                     <hrms:menuitem name="mitem3" label="menu.gz.currorgpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
	                  </logic:equal>
	                  <logic:equal name="templateForm" property="infor_type" value="3"> 
	                     <hrms:menuitem name="mitem3" label="menu.gz.currkingpdf" icon="/images/export.gif" url='<%=cardpdf%>'  enabled="true" visible="true"/>
	                  </logic:equal>
                   <%} %>
                  </hrms:menuitem>
            
	  		
   		</logic:iterate>   
   		<% } %>  
   			
    	<logic:iterate id="pagebo"  name="templateForm"  property="noprintlist" indexId="index">
            <%
            	++i;
            %>        	
	  		<hrms:menuitem name='<%="mitem"+i%>' label="${pagebo.title}" icon="/images/print.gif" url="outpdf('${templateForm.setname}','${templateForm.ins_id}','0','1','${pagebo.pageid}');" command="" enabled="true" visible="true"/>
 
   		</logic:iterate>

  	</hrms:menubar>         
      </TD>
    </tr>
  </table>  
  <div id="a0101_pnl" style="border-style:none">
  	<select name="a0101_box" multiple="multiple" size="10" class="dropdown_frame" onkeydown="selectupdown(event);" ondblclick='setSelectValue("${templateForm.setname}");'>    
    </select>
  </div>  
  
  <a href="<%=url_s%>" style="display:none" id="hostname">for vpn</a>
 <input type='hidden' name='filterStr' />
</html:form>

</body>
<div id="TmplPreview1div">
<!-- 
<OBJECT
      id="TmplPreview1"
	  classid="clsid:3FF4C4CB-232A-4397-9B0C-0215C179FFDD"
	  codebase="/cs_deploy/AxTmplPreview.cab#version=1,0,68,0"
	  width="0" height="0" hspace="0"  vspace="0">
</OBJECT>
 -->
</div>
<div id="CardPreview1div">
</div>


<div id='wait' style='position:absolute;top:285;left:80;display:none;z-index:999;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style"  height="87" align="center">
           <tr>
             <td class="td_style" height=24>正在处理，请稍候...</td>
          </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10" >
                 <table cellspacing="1" cellpadding="0">
                   <tr height=8>
                     <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                         <td bgcolor=#3399FF width=8></td>
                         <td></td>
                    </tr>
                  </table>
               </marquee>
             </td>
          </tr>
        </table>
</div>


   
${templateForm.priv_html}
<style>
<!--  -->
.x-table-plain{
    cellpadding:0;
    cellspacing:0;
    margin-top:0;
}
</style>
<script language="javascript">
   var fixedtabdiv=document.getElementById("fixedtabdiv");
   fixedtabdiv.style.height=document.body.clientHeight-40;
   fixedtabdiv.style.width=document.body.clientWidth-10; 
   Element.hide('a0101_pnl');
   var dataset;
   dataset=${templateForm.setname};
   /**为登记表插件做数据 begin**/
   var dbtype= "<%=dbtype%>";
   var username = "<%=username%>";
   var userFullName = "<%=userFullName%>";
   var superUser = "<%=superUser%>";
   var nodeprive = "<%=nodeprive%>";
   var tables = "<%=tables%>";
   var _version = "<%=_version%>";
   var usedday = "<%=usedday%>";
   var fields = "<%=fields%>";
   /**为登记表插件做数据 end**/
   //initCard(hosturl/*'<%=url_s%>'*/,'<%=dbtype%>','<%=username%>','<%=userFullName%>','<%=superUser%>','<%=nodeprive%>','<%=tables%>','TmplPreview1','<%=_version%>','<%=usedday%>');
   //initAciveCard(hosturl,'<%=dbtype%>','<%=username%>','<%=userFullName%>','<%=superUser%>','<%=fields%>','<%=tables%>','CardPreview1');
   var findarr;
   var findvalue;
   if(infor_type=='1')
   {
	   findarr=new Array("a0100");
	   findarr.push("basepre");
	   findvalue=new Array("${templateForm.a0100}");
	   findvalue.push("${templateForm.basepre}");
   }
   else if(infor_type=='2')
   {
   	   var findarr=new Array("B0110"); 
	   var findvalue=new Array("${templateForm.b0110}");
   }
   else if(infor_type=='3')
   {
   	   var findarr=new Array("E01A1"); 
	   var findvalue=new Array("${templateForm.e01a1}");
   }
   var record=dataset.find(findarr,findvalue,null);
   var hosturl=$('hostname').href;
   
   if(record)
	   dataset.setRecord(record);  
   showObjectList("emplist",${templateForm.setname},"${templateForm.ins_id}","${templateForm.setname}");
   
   function ${templateForm.setname}_beforeChange(dataset,field,value)
   {
   	  var field_name=field.getName();
   	  var record;
   	  var a0100;
   	 <logic:equal name="templateForm" property="operationtype" value="0">  
   	  if(field_name=="a0101_2")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	a0100=record.getValue("basepre")+"|"+record.getValue("a0100");
   	  	refreshA0101(a0100,value);  	  
   	  }
   	 </logic:equal>
   	 <logic:equal name="templateForm" property="operationtype" value="5">  
   	  if(field_name=="codeitemdesc_2")
   	  {
   	  	record=dataset.getCurrent(); 
   	  	if(infor_type=='2')
	   	  	a0100=record.getValue("B0110");
   		else if(infor_type=='3')
			a0100=record.getValue("E01A1");   
   	  	refreshA0101(a0100+"|"+a0100,value);  	  
   	  }
   	 </logic:equal>
   	 
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
   	  	var ori_value=value;
   	  	value=getDeptParentId(value);
   	  	pfield=dataset.getField("e0122_2");
		if(pfield!=null&&typeof(pfield)!="undefined")
		{
			record.setValue("e0122_2",value);
		}
		}else{
   	   		selectfirstflag="0";
		}
		 
		if(value!=null&&typeof(value)!="undefined"&&trim(value).length==0)
		{ 
			if(ori_value!=null&&typeof(ori_value)!="undefined"&&trim(ori_value).length>0)
			{	
				value=getUnitParentId(ori_value);
				pfield=dataset.getField("b0110_2");
				if(pfield!=null&&typeof(pfield)!="undefined")
				{
					record.setValue("b0110_2",value);
				}
			}
		} 
   	  }
      if(field_name=="e0122_2")
   	  {
   	     
   	    if(selectfirstflag=="0")
   	   		selectfirstflag="2"; 
   	  	record=dataset.getCurrent(); 
   	  	value=record.getValue("e0122_2");
   	  	 
   	  	 if(selectfirstflag=="1"&&value!=null&&trim(value).length==0)
   	  	 { 
   	  	 	return;
   	  	 }
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
  
   
  // alert(hosturl);	
   //hosturl="https://hrvpn.citvc.com/prx/000/http/hjhr.citvc.com:8080";  	
   //alert(window.location.protocol+"//"+window.location.hostname+":"+window.location.port);
   
     

   if(window.parent.a0100_&&trim(window.parent.a0100_).length>0)
   {
  	 locaterec(window.parent.basepre_,window.parent.a0100_,window.parent.ins_id_)
   }
   else
   {
	   		//判断章是否存在
			if(signxml.length>0)
	   		{
		    	var record=dataset.getCurrent(); 
		    	if(record)
		    	{
			    	 if(infor_type=='1')
		             {
		            	var a0100=record.getValue("a0100");
				    	var basepre=record.getValue("basepre");
				    	initDocsignature(basepre,a0100);
		             }
		             else {
		             	initDocsignature("","");
		             }
		    	}
	    	}
	        var record=dataset.getCurrent();
	        if(record)
	        {
	            if(infor_type=='1')
	            {
	            	var a0100_=record.getValue("a0100");
	                var basepre_=record.getValue("basepre"); 
	                if(ins_id!="0")
			        	ins_id=record.getValue("ins_id"); 
	               	locaterec(basepre_,a0100_,ins_id);
	            }
	            else if(infor_type=='2')
	            {
	            	var b0110_=record.getValue("B0110");
	            	if(ins_id!="0")
			       		ins_id=record.getValue("ins_id"); 
	            	locaterec(b0110_,b0110_,ins_id);
	            }
	            else if(infor_type=='3')
	            {
	            	var e01a1_=record.getValue("E01A1");
	            	if(ins_id!="0")
			        	ins_id=record.getValue("ins_id"); 
	               	locaterec(e01a1_,e01a1_,ins_id);
	            }
	        }
   }
   
   //自动保存数据调用的方法
   function autoSaveData()
   {
   	   
	   var _save=new UpdateCommand("save");
	   _save.setAction("/ajax/ajaxService");
	   _save.setFunctionId("0570010105");
	   _save.setHint("");
	   _save.addDatasetInfo(${templateForm.setname},"all-change");
  	  return _save.autosave(); //20140925
   }
   function savesignature(flag2){
     updateDocumentid();
  	    var hashvo=new ParameterSet(); 
  	    while(signxml.indexOf("\"")!=-1){
  	         signxml =signxml.replace("\"","'");
  	         }
      	   hashvo.setValue("signxml", getEncodeStr(signxml));
     	   hashvo.setValue("table_name", '${templateForm.setname}');
     	   hashvo.setValue("infor_type", '${templateForm.infor_type}');
     	   hashvo.setValue("ins_id", '${templateForm.ins_id}');
     	   hashvo.setValue("flag", flag2);
     	   
          var In_paramters="";
          var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:exesignxml,functionId:'0570040049'},hashvo);
   }
   function exesignxml(outparamters){
var flag=outparamters.getValue("flag");
 //if(flag=="1")
 //alert("签章保存成功!");

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
function checkBrowserSettings()
{
    AxManager.checkBrowserSettings('<%=url_p%>');
}
</script>

 