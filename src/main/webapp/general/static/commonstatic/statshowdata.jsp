<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.stat.StatForm,java.util.List,com.hjsj.hrms.actionform.general.statics.CrossStaticForm"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%@ page import="com.hrms.frame.codec.SafeCode" %>
<html>
<head>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/control.js"></script>
<script language="javascript" src="/ajax/dataset.js"></script>
<script language="javascript" src="/ajax/editor.js"></script>
<script language="javascript" src="/ajax/dropdown.js"></script>
<script language="javascript" src="/ajax/table.js"></script>
<script language="javascript" src="/ajax/menu.js"></script>
<script language="javascript" src="/ajax/tree.js"></script>
<script language="javascript" src="/ajax/pagepilot.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<!--<script language="javascript" src="/ajax/tripledes.js"></script>-->
<script language="javascript" src="/js/constant.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
</head>
<!-- 引入ext框架     wangb 20180207 -->
<script language="JavaScript" src="/module/utils/js/template.js"></script>
<%
  UserView userView=(UserView)session.getAttribute(WebConstant.userView);
  String url="";
  if(userView != null)
  {
     url=userView.getBosflag();
  
  }
  //liuy 2014-9-20 修改人员结构和占比分析：穿透饼形图，点下页，再返回二维交叉表页面，页面空白 start
    StatForm statForm=(StatForm)session.getAttribute("statForm");
  	//多维交叉页面
    CrossStaticForm crossStaticForm=(CrossStaticForm)session.getAttribute("crossStaticForm");
  	String home=statForm.getHome();
	if(request.getParameter("home")!=null)
	{					
		home = request.getParameter("home");
	}
	session.setAttribute("home", home);
  	String flag = statForm.getFlag();
  	String showflag = statForm.getShowflag();
    String crosstabtype=(String)session.getAttribute("crosstabtype");
	if(request.getParameter("crosstabtype")!=null)
	{			
		crosstabtype = request.getParameter("crosstabtype");
	}
	if(!"15".equals(flag)&&!"jgfx".equals(flag)&&!"zqct".equals(flag)){
		crosstabtype="";
	}
	session.setAttribute("crosstabtype", crosstabtype);
  //liuy 2014-9-20 end
  
  //liuy 2014-10-13 钻取穿透图 start
  	String subIndex=(String)session.getAttribute("subIndex");
  	String subStat=(String)session.getAttribute("substat");
	if(request.getParameter("subIndex")!=null&&request.getParameter("substat")!=null)
	{			
		subIndex = request.getParameter("subIndex");
		subStat = request.getParameter("substat");
	}
	if(!"zqct".equals(flag)){
		subIndex="";
		subStat="";
	}
	String subStatSize="";
	if(!"".equals(subStat)){
		String[] strlen = subStat.split(",");
		subStatSize=strlen.length-1+"";
	}
	subIndex=subIndex==null?"":subIndex;
	session.setAttribute("subIndex", subStatSize);
	session.setAttribute("substat", subStat);
  //liuy 2014-10-13 end
  //liuy 2014-12-30 6394：安徽高速：人工成本-主业、各公司预算占比，点统计图穿透以后的返回不对，返回到主页了。 start
  	String referer = "";
  	String returnvalue = "";
  	if(request.getHeader("Referer")!=null){  		
  		referer = request.getHeader("Referer");
  		if(referer.indexOf("b_msgchart=")!=-1){
  			returnvalue = "msgchart";
  		}else if(referer.indexOf("b_msgchart2=")!=-1){
  			returnvalue = "msgchart2";  			
  		}
  	}
  //liuy 2014-12-30 end
	String dbname=(String)session.getAttribute("dbname");
	if(request.getParameter("dbname")!=null)
	{			
		dbname = request.getParameter("dbname");
	}
	session.setAttribute("dbname", dbname);
%>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
	var webserver=1;
	
</script>
<script language="javascript">
  function winhref(url,target,a0100)
  {
   if(a0100=="")
      return false;
   var o_obj=document.getElementById('a0100');   
   if(o_obj)
     o_obj.value=a0100;   
   statForm.action=url;
   statForm.target=target;
   statForm.submit();
  }  

  document.oncontextmenu=function() 
   { 
//      return false; 
   }; 
   function back(flag,target)
   {
     if(flag=="char")
     {
        statForm.action="/general/static/commonstatic/statshow.do?b_retreechat=link";
        statForm.target=target;
        statForm.submit();
     }else if(flag=="char2")
     {
        statForm.action="/general/static/commonstatic/statshow.do?b_return=link";
        statForm.target=target;
        statForm.submit();
     }else if(flag=="double2")
     {
        statForm.action="/general/static/commonstatic/statshow.do?b_returndouble=link";
        statForm.target=target;
        statForm.submit();
     }else
     {
        statForm.action="/general/static/commonstatic/statshow.do?b_retreedouble=link";
        statForm.target=target;
        statForm.submit();
     }
     
   }
   function winopen(url,a0100)
  {
      if(a0100=="")
        return false;
        var o_obj=document.getElementById('a0100');   
      if(o_obj)
        o_obj.value=a0100;   
      //window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=yes,menubar=yes,location=yes,resizable=no,status=yes");
       statForm.action=url;
       statForm.target="_blank";
       statForm.submit();
  }
  function openSelfInfo(url,a0100)
  {
  	url=url+"`a0100="+a0100;
    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url+"`width="+(screen.availWidth)+"`height="+(screen.availHeight-70));
    /*
    var return_vo= window.showModalDialog(iframe_url,"", 
              "dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;scroll=no;status=no;");
    */
    window.open(iframe_url,'','width='+(window.screen.width-20)+'px,height='+(window.screen.height-20)+'px,toolbar=no,menubar=no,scrollbars=no,resizeable=no,location=no,status=no');
  }  
  function returnH(flag)
   {
	  var returnButton = document.getElementById("returnButton");
	  if(returnButton)
		  returnButton.disabled = true;
	  
      if(flag=="1")
      {
         statForm.action="/general/static/commonstatic/statshow.do?b_return=link";
         statForm.target="_self";
         statForm.submit();
      }else if(flag=="2")
      {
         statForm.action="/general/static/commonstatic/statshow.do?b_doubledata=link&filter_type=1&statenter=true";//直接返回二维统计页面，不需要多跳两次
         statForm.target="_self";
         statForm.submit();
      }else if(flag=="13")
      {
         //statForm.action="/general/static/commonstatic/statshowmsgchart.do?b_msgchart2=link&statid=${statForm.statid}&chart_type=${statForm.chart_type}";
         //statForm.action="/templates/index/bi_portal.do?br_query=link";
         /* 标识：2732  领导桌面：在有的上切换人员库后，再到没有人员库的统计上点数字为实际库统计的值，进入后值为那会选择后库的人员记录，不对。 xiaoyun 2014-7-23 start */
         //statForm.action="/templates/index/bi_portal.do?b_query=link";
         /* 标识：2732  领导桌面：在有的上切换人员库后，再到没有人员库的统计上点数字为实际库统计的值，进入后值为那会选择后库的人员记录，不对。 xiaoyun 2014-7-23 end */
         //statForm.target="_self";
         <%
         	if("msgchart2".equals(returnvalue)){
         %>
         statForm.action="/general/static/commonstatic/statshowmsgchart.do?b_msgchart2=link&statid=${statForm.statid}&chart_type=${statForm.chart_type}&sformula=${statForm.sformula}";
         <%		
         	}else{
         %>         
         statForm.action="/general/static/commonstatic/statshowmsgchart.do?b_msgchart=link&statid=${statForm.statid}&chart_type=${statForm.chart_type}&sformula=${statForm.sformula}";
         <%		
         	}
         %>
         statForm.submit();
      }else if(flag=="jgfx")
      {
         statForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link";         
         statForm.target="_self";
         statForm.submit();
      }else if(flag=="zqct")
      {
    	 statForm.action="/general/deci/statics/employmakeupanalyse.do?b_search=link&subIndex=<%=subStatSize%>&flag=zqct";         
         statForm.target="_self";
         statForm.submit();
      }else if(flag=="15"){ 																									<%-- 获取session中保存的hideFlag wangb 20180804 bug 39405 and 添加统计id号 和组织机构过滤参数 bug 56372--%>
      	 var statid = '${crossStaticForm.statid}';
      	 statid = !statid || statid=='null'? "":statid;
    	 statForm.action='/general/deci/statics/crosstab.do?b_query=link&hideFlag=<%=session.getAttribute("hideFlag")%>&type=<%=crosstabtype%>&home=<%=home%>&userbases='+$URL.encode("${statForm.userbases}")+'&dbname='+$URL.encode("${statForm.userbases}")+'&statid='+statid+'&filter_type=1&vtotal=${crossStaticForm.vtotal}&htotal=${crossStaticForm.htotal}&vnull=${crossStaticForm.vnull}&hnull=${crossStaticForm.hnull}';
    	 statForm.target="_self";
         statForm.submit();
      }
                   	
   } 
  function viewPhoto()
   {
       statForm.action="/general/static/commonstatic/statshow.do?b_view_photo=link";
       statForm.target="_self";
       statForm.submit();
   }  
   function change(obj)
   {
      statForm.action="/general/static/commonstatic/statshow.do?b_data=link&userbases="+obj.value+"&showflag=<%=showflag%>";      
      statForm.submit();
   }
   function openwin(url)
   {
     //alert(url);
     url = url.replace(/&/g,"`");
     var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   	 //iframe_url = iframe_url.replace(/&/g,"`");
     window.open(iframe_url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-70)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
   }
   function exportExcel(infokind,dbpre){
   		 //var thecodeurl="/workbench/query/query_interface.do?br_field=field&infokind="+infokind; 
   		 var thecodeurl="/workbench/query/query_interface.do?br_field=field`infokind="+infokind+"`callback=exportExcel"; 
   		 var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
	 var dw=540,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	 if(getBrowseVersion()){
		 var values= window.showModalDialog(iframe_url,null, 
			        "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:540px; dialogHeight:460px;resizable:no;center:yes;scroll:yes;status:no");
	     if(values) {
	    	 var wait = document.getElementById("wait");
	    	 if(wait)
	    		 wait.style.display="block";
	    	 
	    	 setTimeout("getExportFileName()",1000);
	         var obj=new Object();
	         obj=values;
	         var ids=obj[0];
	         var strwhere=obj[2];
	         var hashVo=new ParameterSet();
	         hashVo.setValue("dbpre",dbpre);
	         hashVo.setValue("ids",ids);
	         hashVo.setValue("infokind",infokind);
	         hashVo.setValue("strwhere",strwhere);
	         hashVo.setValue("querytype","1");
	         hashVo.setValue("service","true");//xiegh add bug:24047 date:20170705
	         hashVo.setValue("selectDate",obj[6]);
	         hashVo.setValue("times",obj[7]);
	         hashVo.setValue("selectField",obj[8]);
	         hashVo.setValue("where",obj[9]);
	         var request=new Request({method:'post',asynchronous:true,onSuccess:function(){},functionId:'0202001021'},hashVo);	
	     }	
	 }else{//非IE浏览器使用ext弹窗  wangb 20180207 bug 34607
	 	var dialog=[];dialog.dw=dw;dialog.dh=dh+100;dialog.iframe_url=iframe_url;
	 	openWin(dialog);
	 }		
   }
   //ext window 弹窗
   function openWin(dialog){
		    Ext.create("Ext.window.Window",{
		    	id:'export_excel',
		    	width:dialog.dw,
		    	height:dialog.dh,
		    	title:'<bean:message key="goabroad.collect.educe.excel"/>',
		    	resizable:false,
		    	modal:true,
		    	autoScroll:true,
		    	renderTo:Ext.getBody(),
		    	html:"<iframe style='background-color:#ffffff' frameborder='0' SCROLLING=NO height='100%' width='100%' src='"+dialog.iframe_url+"'></iframe>"
		    }).show();	
	}
	function winClose(){
		Ext.getCmp('export_excel').close();
	}
	//ext 弹窗返回数据调用方法   wangb 20180207
	function returnValue(values){
		winClose();
		if(values)
     	{
     		var wait = document.getElementById("wait");
	    	if(wait)
	    		wait.style.display="block";
			setTimeout("getExportFileName()",1000);
         	var obj=new Object();
         	obj=values;
         	var ids=obj[0];
         	var strwhere=obj[2];
         	var hashVo=new ParameterSet();
        	hashVo.setValue("dbpre",'${statForm.userbases}');
         	hashVo.setValue("ids",ids);
         	hashVo.setValue("infokind",'${statForm.infokind}');
         	hashVo.setValue("strwhere",strwhere);
         	hashVo.setValue("querytype","1");
         	hashVo.setValue("service","true");//xiegh add bug:24047 date:20170705
         	hashVo.setValue("selectDate",obj[6]);
	        hashVo.setValue("times",obj[7]);
	        hashVo.setValue("selectField",obj[8]);
	        hashVo.setValue("where",obj[9]);
         	var request=new Request({method:'post',asynchronous:true,onSuccess:function(){},functionId:'0202001021'},hashVo);	
     	}
	}

	function getExportFileName(){
		var hashVo=new ParameterSet();
	    hashVo.setValue("exportFlag","1");
	    var request=new Request({method:'post',asynchronous:true,onSuccess:exportFile,functionId:'0202001021'},hashVo);
	}

	function exportFile(outparameters){
		var msg = outparameters.getValue("msg");
		if("ok" != msg) {
			var wait = document.getElementById("wait");
			if(wait)
				 wait.style.display="none";
			
			alert(msg);
			return false;
		}
		
	   	var outName=outparameters.getValue("exportEmployeFileName");
	   	if(outName) {
			var wait = document.getElementById("wait");
			if(wait)
				 wait.style.display="none";
			 
			document.getElementById("exportRows").innerHTML="…";
		   	var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName,"excel");
	   	} else {
	   		var exportRows = outparameters.getValue("exportRows");
	   		var totalRows = outparameters.getValue("totalRows");
	   		if(totalRows)
	   			document.getElementById("exportRows").innerHTML="，已导出" + exportRows + "/" + totalRows + "条数据…";
	   		
	   		setTimeout("getExportFileName()",3000);
	   	}
	}
</script>
<%int i=0;%>
<hrms:themes/>
<%if("hcm".equalsIgnoreCase(url)){ %>
<style>
.ListTable{
	margin-left:2px;
	margin-top:4px;
}
.RecordRowP{
	margin-left:2px;
}
</style>
<%}else{ %>
<style>
.ListTable{
	margin-left:0px;
	margin-top:3px;
}
</style>
<%} %>
<body>
													<%-- bug 37364 BI平台 table的边框线和人员列表边线重叠，添加样式   wangb 20180703 --%>						
<html:form action="/general/static/commonstatic/statshow" style="margin-left:2px;width:99%">
<input type="hidden" name="a0100" id="a0100">
<logic:equal name="statForm" property="infokind" value="1">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td>
  <div id="scrollDiv">
   <table width="99%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
          <!--   <tr>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="B0110" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
            </td>           
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="E0122" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="E01A1" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />
	    </td>
            <td align="center" class="TableRow" nowrap>
             <hrms:fieldtoname name="statForm" fieldname="A0101" fielditem="fielditem"/>
	     <bean:write name="fielditem" property="dataValue" />   
	    </td>          	    	    	    		        	        	        
           </tr>
         -->
         <td align="center" class="TableRow" nowrap>
            人员库   
	    </td> 
           <logic:iterate id="element"    name="statForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="element" property="priv_status" value="0">               
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>  
  	          </logic:notEqual>	 
            </logic:iterate>
      	    <logic:notEqual name="statForm" property="tabid" value="-1">
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.base.info"/>          	
		    </td>	                
		    </logic:notEqual>            
            <td align="center" class="TableRow" nowrap>
		     	<bean:message key="tab.synthesis.info"/>          	
		    </td>        
   	  </thead>
   	  
          <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="statForm.columns" order_by="statForm.order_by" distinct="${statForm.distinct}" pagerows="20" page_id="pagination">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
       <!--     <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />
          	
	    </td>            
            <td align="left" class="RecordRow" nowrap>
          	<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem"  uplevel="${statForm.uplevel}" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />            
	    </td>
            <td align="left" class="RecordRow" nowrap>
                <hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          	<bean:write name="codeitem" property="codename" />     
	    </td>
	    -->
	    <bean:define id="db" name="element" property="db"></bean:define> 
	    <td align="left" class="RecordRow" nowrap>  
	    	<%=com.hrms.frame.utility.AdminCode.getCodeName("@@",((String)pageContext.getAttribute("db")).substring(1)) %>
	    </td>
	    <bean:define id="a0100" name="element" property="a0100"/>
        <% String a0100tran =PubFunc.encrypt(a0100.toString()); //"~" + SafeCode.encode(PubFunc.convertTo64Base(a0100.toString())); %>
	    <logic:iterate id="fielditem"  name="statForm"  property="fieldlist" indexId="index">
              <logic:notEqual name="fielditem" property="priv_status" value="0">             
              <td align="left" class="RecordRow" nowrap>  
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                   <logic:equal name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${statForm.uplevel}" scope="page"/>  	      
          	         <!-- 
          	            	//tianye update start
							//关联部门的指标支持指定单位（部门中查不出信息就去单位中查找）
          	            	-->
          	            	<logic:notEqual  name="codeitem" property="codename" value="">
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:notEqual>
          	          		<logic:equal  name="codeitem" property="codename" value="">
          	          		 	<hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page" uplevel="${statForm.uplevel}"/>  
          	           			<bean:write name="codeitem" property="codename" /> 
          	           		</logic:equal>   
          	           		<!-- end -->    
          	       </logic:equal>
          	        <logic:notEqual name="fielditem" property="codesetid" value="UM">
          	         <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	         <bean:write name="codeitem" property="codename" />  
          	       </logic:notEqual>                  
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>                                
	      </td>   
	      </logic:notEqual>	 	                          
         </logic:iterate>
         
         <logic:notEqual name="statForm" property="tabid" value="-1">
              <td align="center" class="RecordRow" nowrap>
				<a href="###" onclick="javascript:openwin('/general/inform/synthesisbrowse/synthesiscard.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&inforkind=${statForm.infokind}&a0100=<%=a0100tran%>&tabid=${statForm.tabid}&multi_cards=-1','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/table.gif" style="vertical-align: middle;" border="0"></a>		      	
		      </td>	                
         </logic:notEqual>	
            <td align="center" class="RecordRow">               
               <logic:equal name="statForm" property="flag" value="jgfx">
                  <!-- 人员库不对  <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<bean:write name="statForm" property="userbase" filter="true"/>&flag=notself&returnvalue=jgfx','nil_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" border="0"></a> -->
                   <% if("".equals(crosstabtype)){ %>
                  		<a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=jgfx','nil_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%}else{ %>
                        <a href="javascript:openSelfInfo('/workbench/browse/showselfinfo.do?b_search=link`userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>`flag=notself`returnvalue=jgfx','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%} %>
               </logic:equal>
               <logic:notEqual name="statForm" property="flag" value="jgfx">
                   <logic:equal name="statForm" property="home" value="0">
                   <% if("".equals(crosstabtype)){ %>
                       <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=5','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%}else{ %>
                       <a href="javascript:openSelfInfo('/workbench/browse/showselfinfo.do?b_search=link`userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>`flag=notself`returnvalue=5','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%} %>
	              </logic:equal>  
	              <logic:equal name="statForm" property="home" value="1">
	          	   <% if("".equals(crosstabtype)){ %>
                       <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=81&home=${statForm.home}','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%}else{ %>
                       <a href="javascript:openSelfInfo('/workbench/browse/showselfinfo.do?b_search=link`userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>`flag=notself`returnvalue=5','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	               <%} %>
	              </logic:equal>  
	              <logic:equal name="statForm" property="home" value="2">
                        <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=82','i_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	              </logic:equal> 
	              <logic:equal name="statForm" property="home" value="6"><!-- 总裁桌面连过来的 -->
	              <%if(url!=null&&url.equals("bi")){ %>
	                     <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=bi','i_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	              <%}else{ %>
	              <%--liuy 2015-2-4 7360：君正集团：矿业人资负责人，从导航图上点常用统计，如学历分布，查询在职人员，穿透后，点基本情况，看到的是别人的信息。点详细信息报没有权限，不对。 start --%>
	              	<logic:notEqual name="statForm" property="flag" value="13">
	                     <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=5','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	              	</logic:notEqual>
	              	<logic:equal name="statForm" property="flag" value="13">
	                     <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=5','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	              	</logic:equal> 
	              <%-- liuy 2015-2-4 end --%>	
	              <%} %>               
	              </logic:equal> 	
	              <logic:equal name="statForm" property="home" value="5">
                         <a href="###" onclick="javascript:winhref('/workbench/browse/showselfinfo.do?b_search=link&userbase=<%=((String)pageContext.getAttribute("db")).substring(1) %>&flag=notself&returnvalue=5','il_body','<bean:write name="element" property="a0100" filter="true"/>');"><img src="/images/view.gif" style="vertical-align: middle;" border="0"></a>
	              </logic:equal> 
               </logic:notEqual>	                 
	         </td>	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>  
   </table>
   </div>
 </td>             	    	    	    		        	        	        
 </tr> 
 <tr style="padding-top:0px;">
  <td>
    <table  width="100%" id="pageTable" class="RecordRowP" align="left"> 

		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>
</table>

<table width="100%" align="center" cellpadding="0" cellspacing="0" style="margin-top:5px;margin-left:3px;">
          <tr valign="middle" align="left">
            <td align="center"><!--bug36220 update by xiegh  -->
         <hrms:priv func_id="2601007,0303013">
         	<input type="button" name="addbutton" value="<bean:message key="goabroad.collect.educe.excel"/>" class="mybutton" onclick="exportExcel('${statForm.infokind}','${statForm.userbases}')">
         </hrms:priv>  
	 	   <input type="button" name="addbutton"  value="<bean:message key="button.query.viewphoto"/>" class="mybutton" onclick='viewPhoto();' >  	
	 	  <logic:equal name="statForm" property="flag" value="1">
	 	     <logic:equal name="statForm" property="home" value="1">
	 	     <%if(url!=null&&url.equals("ul")){ %> 
	 	         <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('char','i_body');">
	 	      <%}else{%>
	 	       <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('char','il_body');">
	 	      <%} %>
	 	    </logic:equal>
	 	     <logic:notEqual name="statForm" property="home" value="1">	 	        
	 	       <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('1');" >
	 	     </logic:notEqual>
            </logic:equal>
            
            
             <logic:equal name="statForm" property="flag" value="12">
                <logic:equal name="statForm" property="home" value="1">
	 	          <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('char2','il_body');">
	 	         </logic:equal>
	 	        <logic:notEqual name="statForm" property="home" value="1">
	 	          <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('1');" >
	 	         </logic:notEqual>     
             </logic:equal>
              <logic:equal name="statForm" property="flag" value="13">
                   <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('13');" >
              </logic:equal>
             <logic:equal name="statForm" property="flag" value="14">
                   <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('14');" >
              </logic:equal>
               <logic:equal name="statForm" property="flag" value="jgfx">
                   <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('jgfx');" >
              </logic:equal>
              <logic:equal name="statForm" property="flag" value="zqct">
                   <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('zqct');" >
              </logic:equal>
             <logic:equal name="statForm" property="flag" value="2">
                <logic:equal name="statForm" property="home" value="1">
                 <%if(url!=null&&url.equals("ul")){ %> 
	 	         <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('double','i_body');">
	 	         <%}else{%>
	 	           <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="back('double2','il_body');">
	 	          <%} %>
                   
                </logic:equal>
                <logic:notEqual name="statForm" property="home" value="1">
                   <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('2');" >
                </logic:notEqual>                    
	 	           
             </logic:equal>    
                <logic:equal name="statForm" property="flag" value="20">
                    <input type="button" name="addbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('2');" >
                </logic:equal>
                <logic:equal name="statForm" property="flag" value="15">
                   <input type="button" name="returnbutton" id="returnButton" value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('15');" >
                </logic:equal>     	   
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="statForm" property="infokind" value="2">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
 <tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
             <logic:iterate id="element"    name="statForm"  property="fieldlist" indexId="index">                    
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>    	         
  	        </logic:iterate>                 	    	    	    		        	        	        
           </tr>
   	  </thead>
   	  		
          <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="statForm.columns" order_by="statForm.order_by" distinct="${statForm.distinct}" pagerows="21" page_id="pagination">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
            <logic:iterate id="fielditem"  name="statForm"  property="fieldlist" indexId="index">
              <td align="left" class="RecordRow" nowrap> 
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                      <logic:equal name="fielditem" property="itemid" value="b0110">
                        <a href="/general/static/commonstatic/statshowinfodata.do?br_infodata=link&a0100=<bean:write name="element" property="${fielditem.itemid}" filter="true"/>" target="_self">
          	                  
          	                  <hrms:codetoname codeid="UN" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
                               <bean:write name="codeitem" property="codename" />
                          	  <hrms:codetoname codeid="UM" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" uplevel="${statForm.uplevel}" scope="page"/>  	      
                               <bean:write name="codeitem" property="codename" />
                          	</a>
                      </logic:equal>
                      <logic:notEqual name="fielditem" property="itemid" value="b0110">
                       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />             
                    </logic:notEqual>     
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>  
               </td>
            </logic:iterate>                   	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
    </table>
   </td>
  </tr>
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">

		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>        
</table>

<table  width="100%" align="center" style="margin-top:5px;margin-left:3px;">
          <tr>
            <td align="left">
       	 	      <logic:equal name="statForm" property="flag" value="1">
                    <hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	          </hrms:submit>          
                 </logic:equal>                
                 <logic:equal name="statForm" property="flag" value="2">
                    <hrms:submit styleClass="mybutton" property="b_returndouble">
            		<bean:message key="button.return"/>
	 	          </hrms:submit>  
                </logic:equal>
 	          	<logic:equal name="statForm" property="flag" value="13">
                  <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('13');" >
                </logic:equal>
                <logic:equal name="statForm" property="flag" value="15">
                   <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('15');" >
                </logic:equal>        	   
            </td>            
          </tr>          
</table>
<table  width="100%" align="center" style="margin-top:5px;margin-left:3px;">
          <tr>
            <td align="left">
             <logic:equal name="statForm" property="flag" value="12">
                <logic:equal name="statForm" property="home" value="1">
	 	          <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('char2','il_body');">
	 	         </logic:equal>
	 	        <logic:notEqual name="statForm" property="home" value="1">
	 	          <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('1');" >
	 	         </logic:notEqual>     
             </logic:equal>
            </td>            
          </tr>          
</table>
</logic:equal>
<logic:equal name="statForm" property="infokind" value="3">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
<tr>
  <td>
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
   	   <thead>
           <tr>
            <logic:iterate id="element"    name="statForm"  property="fieldlist" indexId="index">                    
                <td align="center" class="TableRow" nowrap>
                   <bean:write  name="element" property="itemdesc" filter="true"/>
	      		</td>    	         
  	        </logic:iterate>              	    	    	    		        	        	        
           </tr>
   	  </thead>
             <hrms:paginationdb id="element" name="statForm" sql_str="statForm.strsql" table="" where_str="statForm.cond_str" columns="statForm.columns" order_by="statForm.order_by" pagerows="21" distinct="${statForm.distinct}" page_id="pagination">
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %> 
            <logic:iterate id="fielditem"  name="statForm"  property="fieldlist" indexId="index">
              <td align="left" class="RecordRow" nowrap> 
                 <logic:notEqual name="fielditem" property="codesetid" value="0">
                     <logic:equal name="fielditem" property="codesetid" value="UM">
          	           <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem"  uplevel="${statForm.uplevel}" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />  
          	         </logic:equal>
                     <logic:equal name="fielditem" property="codesetid" value="@K">
                      <a href="/general/static/commonstatic/statshowinfodata.do?br_infodata=link&a0100=<bean:write name="element" property="e01a1" filter="true"/>" target="_self">
                      <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />
          	          </a>
          	                 
                    </logic:equal>
                    <logic:notEqual name="fielditem" property="codesetid" value="UM">
          	          <logic:notEqual name="fielditem" property="codesetid" value="@K">
                       <hrms:codetoname codeid="${fielditem.codesetid}" name="element" codevalue="${fielditem.itemid}" codeitem="codeitem" scope="page"/>  	      
          	           <bean:write name="codeitem" property="codename" />             
                      </logic:notEqual> 
                    </logic:notEqual>     
                 </logic:notEqual>
                 <logic:equal name="fielditem" property="codesetid" value="0">
                   <bean:write name="element" property="${fielditem.itemid}" filter="false"/>               
                 </logic:equal>  
          	   
          	 
	           </td>       
	        </logic:iterate>          	    	    	    		        	        	        
          </tr>
        </hrms:paginationdb>
   </table>
 </td>             	    	    	    		        	        	        
 </tr>        
 <tr>
  <td>
    <table  width="100%" class="RecordRowP" align="center">
		<tr>
		    <td valign="bottom" class="tdFontcolor">
		            <bean:message key="label.page.serial"/>
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum"/>
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row"/>
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page"/>
			</td>
	               <td  align="right" nowrap class="tdFontcolor">
		          <p align="right"><hrms:paginationdblink name="statForm" property="pagination" nameId="statForm" scope="page">
				</hrms:paginationdblink>
			</td>
		</tr>
   </table>
  </td>
 </tr>          
</table>
<table  width="100%" align="center" style="margin-top:5px;margin-left:3px;">
          <tr>
            <td align="left">
       	 	     <logic:equal name="statForm" property="flag" value="1">
                    <hrms:submit styleClass="mybutton" property="b_return">
            		<bean:message key="button.return"/>
	 	           </hrms:submit>          
                 </logic:equal>
                 <logic:equal name="statForm" property="flag" value="2">
                    <hrms:submit styleClass="mybutton" property="b_returndouble">
            		<bean:message key="button.return"/>
	 	           </hrms:submit>  
                </logic:equal>    
                <logic:equal name="statForm" property="flag" value="20">
                    <hrms:submit styleClass="mybutton" property="b_returndouble">
            		<bean:message key="button.return"/>
	 	            </hrms:submit>  
                </logic:equal>
                <logic:equal name="statForm" property="flag" value="13">
                   <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('13');" >
                </logic:equal>
                <logic:equal name="statForm" property="flag" value="12">
                   <logic:equal name="statForm" property="home" value="1">
	 	            <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick=" back('char2','il_body');">
	 	           </logic:equal>
	 	           <logic:notEqual name="statForm" property="home" value="1">
	 	            <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('1');" >
	 	           </logic:notEqual>     
               </logic:equal>
               <logic:equal name="statForm" property="flag" value="15">
                   <input type="button" name="returnbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('15');" >
                </logic:equal>
            </td>            
          </tr>          
</table>
</logic:equal>
</html:form>
<div id='wait' style='position:absolute;top:45%;left:35%;display:none;z-index: 999px;'>
  <table border="1" width="400" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>
             <td class="td_style" height="24">正在导出数据<span id='exportRows'>…</span></td>
           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="400" scrollamount="5" scrolldelay="10">
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
<script>
	var scrollDiv = document.getElementById('scrollDiv');
	scrollDiv.style.overflow='auto';
	scrollDiv.style.width = document.body.clientWidth-10;
	var btn_table  = scrollDiv.getElementsByTagName('table')[0];
	btn_table.style.width='99.8%';
	btn_table.setAttribute('width',document.body.clientWidth-10);
	var pageTable = document.getElementById('pageTable');
	pageTable.style.width = document.body.clientWidth - 10;
	window.onresize=function(){
		btn_table.setAttribute('width',document.body.clientWidth-10);
		scrollDiv.style.width = document.body.clientWidth - 10;
		pageTable.style.width = document.body.clientWidth - 10;
	}
if(getBrowseVersion() && getBrowseVersion()!=10){
	setTimeout(function(){
		//scrollDiv.style.height = btn_table.offsetHeight+23+'px';
	},1000);
}
</script>
</body>
</html>