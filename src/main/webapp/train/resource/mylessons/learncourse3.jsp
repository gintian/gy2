<%@page import="com.hjsj.hrms.actionform.train.resource.TrainLessonForm"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hjsj.hrms.businessobject.train.MediaServerParamBo,com.hrms.frame.utility.AdminDb" %>
<%@ page import="java.util.HashMap,com.hrms.hjsj.utils.Sql_switcher" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.dao.ContentDAO"%>
<%@ page import="com.hjsj.hrms.businessobject.train.resource.MyLessonBo"%>
<%@ page import="java.util.*"%>
<%@page import="com.hrms.struts.constant.SystemConfig"%>
<%@page import="com.hrms.frame.codec.SafeCode,com.hjsj.hrms.utils.Office2Swf,java.util.regex.Matcher"%>
<%@page import="com.hjsj.hrms.actionform.train.resource.TrainProjectForm"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userViews=(UserView)session.getAttribute(WebConstant.userView);
	if(userViews != null)
	{
	  userName = userViews.getUserFullName();
	  css_url=userViews.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		
   String isMobile=request.getParameter("isMobile");
   isMobile = isMobile==null?"":isMobile;
   pageContext.setAttribute("isMobile",isMobile);
	
%>
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title></title>
<logic:notEqual value="1" name="isMobile">
<logic:notEqual value="2" name="isMobile">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 
<script type="text/javascript" src="/js/popcalendar.js"></script>-->
<script language="javascript" src="/js/constant.js"></script>
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
	var webserver=<%=flag%>;
	
</script>
</logic:notEqual>
	</logic:notEqual>
<%
	//检查form是否存在，从主页直接进来需自行创建
    TrainProjectForm trainProjectForm = (TrainProjectForm)session.getAttribute("trainProjectForm");
    if(null == trainProjectForm)
    {
    	trainProjectForm = new TrainProjectForm();
    	trainProjectForm.setIsLearned("0");
    	session.setAttribute("trainProjectForm", trainProjectForm);
    }

	String pathurl="http:\\\\"+request.getServerName()+":"+request.getServerPort();
	pathurl=SafeCode.encode(pathurl);

	
	String filepath = request.getSession().getServletContext().getRealPath("/");
	if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
	{
		filepath=session.getServletContext().getResource("/").getPath();//.substring(0);
	   if(filepath.indexOf(':')!=-1)
		  {
		   filepath=filepath.substring(1);   
		  }
		  else
		  {
			  filepath=filepath.substring(0);      
		  }
	   int nlen=filepath.length();
		  StringBuffer buf=new StringBuffer();
		  buf.append(filepath);
		  buf.setLength(nlen-1);
		  filepath=buf.toString();
	}
%>



<%
	/**flowplayer脚本与hr中的js有冲突，不得已将代码写在了页面上**/
	MyLessonBo bo = new MyLessonBo();
	// 用户
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String a0100 = userView.getA0100();
	String nbase = userView.getDbname();
	// 课程id
	String lessonId = request.getParameter("lesson");
	lessonId = PubFunc.decrypt(SafeCode.decode(lessonId));
	// 课程简介
	String lessonDesc = PubFunc.toHtml(bo.getLessonDesc(lessonId));
	//课程名称
	String lessonName = bo.getDiyLessonName(lessonId,a0100);
	// 课程分类
	String classes = request.getParameter("classes");
	classes = PubFunc.decrypt(SafeCode.decode(classes));
	// 标志是否是从我的课程进入该页面，me为是，其他值为否，用该值控制是否显示笔记和发表评论功能
	String opt = request.getParameter("opt");
	// 获得课件id
	String courseId = request.getParameter("course");
	// 已学秒数
	int learnedTime = 0;
	// 视频长度
	int videoTimes = 0;
	// 课件类型
	String courseType = "";
	// 课件路径
	String coursePath = "";
	// 评论列表
	ArrayList commentList = new ArrayList();
	String courseContext = "";
	// 错误
	String erro = "";
	// 路径处理
	Map pathMap = new HashMap();
	// 课件学习状态
	String state = "";
	//当前课件内容
	String lessonContent = "";

	Connection conn = null;
	ResultSet rs = null;
	try {
		conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buff = new StringBuffer();
		if (courseId == null || courseId.length() <= 0) {
			
			// 查询未学的第一个视频课件
			buff.append("select r.r5100,r.r5117,r.r5119,r.r5115,r.r5111,r.r5105,r.r5113 from ");
    		buff.append("r51 r,r50 r1 where r.r5000 = r1.r5000 ");
    		buff.append("and r1.create_user = '");
    		buff.append(userView.getA0100());
    		buff.append("' and r.r5000 =");
    		buff.append(lessonId);
			buff.append(" and r.r5105='3'");
			
		    if(nbase.equalsIgnoreCase("Usr")){
				rs = dao.search(buff.toString());
				if (rs.next()) {
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
				} else {
					// 查询未学的第一个课件
					buff.delete(0, buff.length());
					buff.append("select r.r5100,r.r5117,r.r5115,r.r5111,r.r5105,r.r5113 from ");
	    			buff.append("r51 r,r50 r1 where r.r5000 = r1.r5000 ");
	    			buff.append("and r1.create_user = '");
	    			buff.append(userView.getA0100());
	    			buff.append("' and r.r5000 =");
	    			buff.append(lessonId);
					rs = dao.search(buff.toString());
					if (rs.next()) {
						courseId = rs.getString("r5100");
						courseType = rs.getString("r5105");
						coursePath = rs.getString("r5113");
						courseContext = rs.getString("r5115");
						videoTimes = rs.getInt("r5117");
						lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					}
				}
			
				if (courseId == null || courseId.length() <= 0) {
					// 查询第一个课件
					buff.delete(0, buff.length());
					buff.append("select r.r5100,r.r5117,r.r5115,r.r5111,r.r5105,r.r5113 from ");
	    			buff.append("r51 r,r50 r1 where r.r5000 = r1.r5000 ");
	    			buff.append("and r1.create_user = '");
	    			buff.append(userView.getA0100());
	    			buff.append("' and r.r5000 =");
	    			buff.append(lessonId);
					rs = dao.search(buff.toString());
					if (rs.next()) {
						courseId = rs.getString("r5100");
						courseType = rs.getString("r5105");
						coursePath = rs.getString("r5113");
						courseContext = rs.getString("r5115");
						videoTimes = rs.getInt("r5117");
						lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					}
				}
			
			} else {
				// 查询课件
				buff.delete(0, buff.length());
				buff.append("select r.r5100,r.r5117,r.r5111,r.r5119,r.r5115,r.r5105,r.r5113 from ");
	    		buff.append("r51 r,r50 r1 where r.r5000 = r1.r5000 ");
	    		buff.append("and r1.create_user = '");
	    		buff.append(userView.getA0100());
	    		buff.append("' and r.r5100 =");
				buff.append(courseId);
			
				rs = dao.search(buff.toString());
				if (rs.next()) {
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
				}
		   }
		    
		   if(lessonContent == null)
			   lessonContent = "";
		
		   commentList = bo.getCommentList(courseId);	
	    }
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		try{
			if (rs != null) {
				rs.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		try{
			if (conn != null) {
				conn.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	if ("3".equals(courseType)) {
		pathMap = bo.handlerPath(coursePath);
	}

	// 增加播放次数
	bo.addPlayCount(courseId);
	ArrayList courseList = new ArrayList();
	if(nbase.equalsIgnoreCase("Usr")){
	 courseList = bo.getUploadCourseList(lessonId, userView);
	}

	String openoffice = SystemConfig.getPropertyValue("openoffice");
	String swftools = SystemConfig.getPropertyValue("swftools");
	

 %>
 <logic:equal name="trainProjectForm" property="isLearned" value="0">
 	<%bo.updateLearnedState(courseId,userView); %>
 </logic:equal>
<html>
	<head>
	
	</head>
	<script language="javascript" src="/js/constant.js"></script>

<script language="javascript" src="/general/flowplayer/js/flowplayer-3.2.6.min.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>

<script type="text/javascript">
	var player = null;
var ttt = null;
var pos  = 0;
</script>
<logic:equal name="trainProjectForm" property="isLearned" value="0">
<SCRIPT type="text/javascript" for="WMP"  event="playStateChange(NewState)">
	var co = 1;
	switch(NewState){ 
    	case 8: 
    	pos = document.getElementById("WMP").currentMedia.duration;
      	break; 
		case 6: 
 		//document.getElementById("WMP").controls.SetCurrentEntry(<%=learnedTime%>); 
 		//alert(document.getElementById("WMP").controls.currentPosition); 
 		if (co == 1&&<%=learnedTime%> < document.getElementById("WMP").currentMedia.duration && '<%=learnedTime%>'.length > 0 && '<%=learnedTime%>'>'0')
 		document.getElementById("WMP").controls.currentPosition = parseFloat(<%=learnedTime%>);
 		co++;   
      	break;
    	
	} 
</SCRIPT>
</logic:equal>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style>
		*{margin-left:auto;margin-right:auto;}
		.title{
			margin-left:200px;
		}
		.titles {
			background-position : center left;
			font-size: 12px;  
			BORDER-BOTTOM: #C4D8EE 0pt solid; 
			BORDER-LEFT: #C4D8EE 1pt solid; 
			BORDER-RIGHT: #C4D8EE 1pt solid; 
			BORDER-TOP: #C4D8EE 1pt solid;
			height:22px;
			font-weight: bold;
			background-color:#f4f7f7;	
			/*
			color:#336699;
			*/
			padding-top:3px;
			valign:middle;
			}
		.headdiv{
			position: relative;
			margin-left:200px;
			width:928px;
		}

		.leftdiv{
			position: relative;
			width:620px;
			margin-top:3px;
			float:left;
			
		}

		
		.rightdiv{
			position: relative;
			margin-left:5px;
			width:300px;
			height:355px;
			margin-top:3px;
			float:left;
		}
		
		.bottom{
			
			margin-left:200px;
		}
		.mediadiv{
			position: relative;
			width:620px;
			height:330px;
			border: solid 1px #C4D8EE;
			
		}
		
		.commentdiv{
			position: relative;
			width:100%;
			border-bottom:solid 1px #C4D8EE;
			margin-left:auto;
			margin-right:auto;
			text-align:left;
		}
		
</style>
<%if("hcm".equals(userView.getBosflag())){ 
	String themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
%>
  <script language="javascript">
    hcm_tabset_root="/images/hcm/themes/<%=themes %>/content/";
  </script>
  <%} %>
 <hrms:themes/>
<logic:equal name="trainProjectForm" property="isLearned" value="0">
<body>
</logic:equal>
<logic:notEqual name="trainProjectForm" property="isLearned" value="0">
<body >
</logic:notEqual>
	<br>
	<%if(lessonName != null && !"".equals(lessonName)){%>
		<table border="0" ><tr><td>
		</td></tr><tr><td>
		<div class="headdiv">
		<div class="leftdiv">
		<div class="titles tableRow" style="border-bottom: none;padding-top: 6px;">【<%=lessonName %>】</div>
			<%if ("3".equals(courseType)) {%>
			<div class="mediadiv complex_border_color" id="divid">	
			<%if (MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) {%>
 		
		
 			<% if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {%>
 				<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP' width="618" height="330">
		<PARAM NAME='Name' VALUE='WMP1'>
		
		<% String url = "mms://" + MediaServerParamBo.getMediaServerAddress();
		System.out.print("2");
		if (MediaServerParamBo.getMediaServerPort().length() > 0) {
			url += ":" + MediaServerParamBo.getMediaServerPort();
		}
		if (MediaServerParamBo.getMediaServerPubRoot().length() > 0) {
			url += "/" + MediaServerParamBo.getMediaServerPubRoot();
		}
		url += pathMap.get("filePath");
		%>	
		<!--播放的文件地址-->	
		<PARAM NAME='URL' VALUE='<%=url %>'>
		<param NAME='AutoStart' VALUE='-1'>
		
		<!--调整左右声道平衡,同上面旧播放器代码-->
		<param NAME="Balance" VALUE="0">		
		<!--播放器是否可人为控制-->
		<param name="enabled" value="-1">		
		<!--是否启用上下文菜单-->
		<param NAME="EnableContextMenu" VALUE="-1">
		<!--播放次数控制,为整数-->
		<param NAME="PlayCount" VALUE="1">
		<!--播放速率控制,1为正常,允许小数,1.0-2.0-->
		<param name="rate" value="1">
		<!--控件设置:当前位置-->
		<param name="currentPosition" value="-1">
		<!--控件设置:当前标记-->		
		<param name="currentMarker" value="-1">
		<!--显示默认框架-->
		<param name="defaultFrame" value="">
		<!--脚本命令设置:是否调用URL-->
		<param name="invokeURLs" value="0">
		<!--脚本命令设置:被调用的URL-->
		<param name="baseURL" value="">
		<!--是否按比例伸展-->
		<param name="stretchToFit" value="0">
		<!--默认声音大小0%-100%,50则为50%-->
		<param name="volume" value="50">
		<!--是否静音-->
		<param name="mute" value="0">
		<!--播放器显示模式:Full显示全部;mini最简化;None不显示播放控制,只显示视频窗口;invisible全部不显示-->
		<param name="uiMode" value="full">
		<!--如果是0可以允许全屏,否则只能在窗口中查看-->
		<param name="windowlessVideo" value="0">
		<!--开始播放是否自动全屏-->
		<param name="fullScreen" value="0">
		<!--是否启用错误提示报告-->
		<param name="enableErrorDialogs" value="-1">
		<!--SAMI样式-->
		<param name="SAMIStyle" value>
		<!--SAMI语言-->
		<param name="SAMILang" value>
		<!--字幕ID-->
		<param name="SAMIFilename" value>
		
		</OBJECT>
 			<%}%>
	<%}%>						
			</div>
			<%} %>
			<%if ("2".equals(courseType)) {%>
			<div class="mediadiv complex_border_color" id="divid" style="padding:8px;overflow: auto;">
			<%=courseContext %>				
			</div>
			<%} %>
			<%if ("1".equals(courseType)) {
			    String ppath = coursePath;
			    if(ppath != null && ppath.length() > 0)
					ppath = ppath.replaceAll(Matcher.quoteReplacement(System.getProperty("file.separator")),"/");
			%>

				<%if (coursePath != null && coursePath.length() > 0 && (coursePath.toLowerCase().endsWith(".doc") || coursePath.toLowerCase().endsWith(".docx") 
								|| coursePath.toLowerCase().endsWith(".xls") || coursePath.toLowerCase().endsWith(".xlsx") 
								|| coursePath.toLowerCase().endsWith(".pdf") || coursePath.toLowerCase().endsWith(".ppt") 
								|| coursePath.toLowerCase().endsWith(".pptx"))) {
								
				
				if (openoffice != null && openoffice.length() > 0 && swftools != null && swftools.length() > 0) {
				
				
				%>
				<script type="text/javascript" src="/general/flexpaper/js/flexpaper_flash.js"></script>
				<div class="mediadiv complex_border_color" id="divid" style="text-align: center;padding-top:1px;">			
			<a id="viewerPlaceHolder" style="width:615px;height:325px;display:block"></a>
			<script>
			var fp = new FlexPaperViewer(	
						 '/general/flexpaper/FlexPaperViewer',
						 'viewerPlaceHolder', { config : {
						 SwfFile : escape('<%=ppath.substring(0,ppath.lastIndexOf("/"))+ "/" + courseId + ".swf" %>'),
						 Scale : 0.6, 
						 ZoomTransition : 'easeOut',
						 ZoomTime : 0.5,
						 ZoomInterval : 0.5,
						 FitPageOnLoad : true,
						 FitWidthOnLoad : false,
						 PrintEnabled : true,
						 FullScreenAsMaxWindow : false,
						 ProgressiveLoading : false,
						 MinZoomSize : 0.2,
						 MaxZoomSize : 5,
						 SearchMatchAll : false,
						 InitViewMode : 'Portrait',
						 
						 ViewModeToolsVisible : false,
						 ZoomToolsVisible : true,
						 NavToolsVisible : true,
						 CursorToolsVisible : true,
						 SearchToolsVisible : false,
  						
  						 localeChain: 'zh_CN'
						 }});
			</script>
			</div>					
			<%} else { if ("1".equals(MediaServerParamBo.getIsDownload1(SafeCode.encode(PubFunc.encrypt(lessonId.toString()))))){ 
			     String filepaths = SafeCode.encode(PubFunc.encrypt(filepath + coursePath)); 
			%>	
				<div class="mediadiv complex_border_color" id="divid" style="text-align: center;padding-top:160px;">
				<a href='/DownLoadCourseware?url=<%=filepaths %>'>下载</a>
				</div>
			<%} }%>				
	<%} else if (coursePath != null && coursePath.length() > 0 && !"null".equalsIgnoreCase(coursePath)){ %>
			<div  class="TableRow" style="width: 620px;;height: 20px;border-bottom: 0px;padding: 3px;"><a style="border: 0px;" href="javascript:seturl('iframeid');">&nbsp;全屏&nbsp;</a></div>
			<div class="mediadiv complex_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;height: 310px;">
				<!--  <a href='/DownLoadCourseware?url=<%=(filepath + coursePath) %>'>下载</a>-->
				<%if(ppath!=null&&ppath.toLowerCase().endsWith(".zip")){ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src="<%=ppath.substring(0,ppath.lastIndexOf("/")+1) + courseId + "/" %>"></iframe>
				<%}else{ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src="<%=ppath %>"></iframe>
				<%} %>
			</div>
			<%} %>				
			
			<%} %>
			<%if ("6".equals(courseType)) {%>
				<div  class="TableRow" style="width: 620px;;height: 20px;border-bottom: 0px;padding: 3px;"><a style="border: 0px;" href="javascript:seturl('iframeid');">&nbsp;全屏&nbsp;</a></div>
				<div class="mediadiv complex_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;">
				<%if(coursePath!=null&&coursePath.length()>0){ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src=""></iframe>
				<script type="text/javascript">
					document.getElementById("iframeid").src="<%=coursePath %>";
					document.getElementById("iframeid").width="100%";
					document.getElementById("iframeid").height="100%";
				</script>
				<%} %>
				</div>
			<%} %>
			<%if ("4".equals(courseType)) {%>
				<div class="mediadiv complex_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;">
				<!--  <a href='/DownLoadCourseware?url=<%=(filepath + coursePath) %>'>下载</a>-->
				<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
					<tr class="fixedHeaderTr">
						<td class="TableRow" style="border: 0px;"><a style="border: 0px;padding: 3px;" href="javascript:seturl2('iframeid');">&nbsp;全屏&nbsp;</a></td>
					</tr>
					<tr>
						<td height="100%">
						<logic:equal name="trainProjectForm" property="isLearned" value="0">
						<iframe width="100%" height="100%" frameborder="0" id="iframeid" onload="SetWin(this)" src="/train/resouce/lessons.do?b_query=link&isLearn=0&classes=<%=SafeCode.encode(PubFunc.encrypt(classes)) %>&r5000=<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>&r5100=<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>"></iframe>
						</logic:equal>
						<logic:notEqual name="trainProjectForm" property="isLearned" value="0">
						<iframe width="100%" height="100%" frameborder="0" id="iframeid" onload="SetWin(this)" src="/train/resouce/lessons.do?b_query=link&isLearn=1&classes=<%=SafeCode.encode(PubFunc.encrypt(classes)) %>&r5000=<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>&r5100=<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>"></iframe>
						</logic:notEqual>
						</td>
					</tr>
				</table>
				</div>
			<%} %>
		</div>
		<div class="rightdiv" style="overflow: auto;">
			
			<table width="100%"  border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin:0px;">
				
				<tr>
					<td align="left" class="TableRow hideleftrighttopborder">
						&nbsp;&nbsp;【课件列表】&nbsp;
					</td>
				</tr>
				
				<tr>
					<td height="60" class="RecordRow hideleftrightborder" >
						<%
						for (int i = 0; i < courseList.size(); i++) { 
							
							Map map = (Map)courseList.get(i);
							
						%>
						<%
						if (i >= 0) { %>
						<hr color="#9A9A9A" width="100%" style="border: 1px dashed;"/>
						<%} %>
						<table border="0" width="250" style="margin-top:5px;margin-left:30px;margin-bottom:10px;">
							<tr>
								<td align="left" colspan="2">
									<%if (courseId.equals(map.get("r5100").toString())){ %>
										<font color="red">*</font>
									<%} %>
								<a href="/train/resource/mylessons/learncourse3.jsp?opt=me&classes=<%=SafeCode.encode(PubFunc.encrypt(classes)) %>&lesson=<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>" onclick="javascirpt:if (document.getElementById('WMP')) {pos=document.getElementById('WMP').controls.currentPosition;}"><%=map.get("r5103") %></a>
								</td>
								<td nowrap="nowrap">
								<%if ("1".equals(MediaServerParamBo.getIsDownload1(SafeCode.encode(PubFunc.encrypt(lessonId.toString()))))){
								if(map.get("r5113")!=null&&map.get("r5113").toString().length()>10&&!"6".equals(map.get("r5105"))){
								    String r5113 = map.get("r5113").toString();
								    if((r5113.startsWith("/") || r5113.startsWith("\\")) && (filepath.endsWith("/") || filepath.endsWith("\\")))
								        r5113 = r5113.substring(1);
								    String filepaths = SafeCode.encode(PubFunc.encrypt(filepath + r5113));
								 %>
								&nbsp;&nbsp;<a href='/DownLoadCourseware?url=<%=filepaths %>'><img src="/images/detail.gif" alt="下载" border="0" style="vertical-align: bottom;"/></a>
								<%}} %>
								</td>
								
							</tr>
							<tr>
								<% String count = map.get("r5119").toString();
								if(count == null || count.length() < 1)
							    count = "0";
							    %>
								<td colspan="2">已播放：<%=count %>次</td>
							</tr>
							<%if ("3".equals(map.get("r5105"))) {%>
								<tr>
									<td colspan="2">时&nbsp;&nbsp;长：<%=map.get("r5117")%>分钟</td>
								
								</tr>
							<%} %>
						</table>
						<%} %>	
					</td>
				</tr>	
		
			</table>
		</div>
		</div>
</td></tr>
		<tr >
			<td>
			<div class="bottom" >
				<hrms:tabset name="sys_param" width="926px" height="300" type="false" > 
			    <hrms:tab name="param2" label="课程简介" visible="true">
						<table width="100%" border="0" cellspacing="0" cellpadding="0" class="ListTable">
							<tr>
								<td style="padding-left: 10px;">
									<%=lessonDesc %>
								</td>
							</tr>		
						</table>
			    </hrms:tab>
     				<hrms:tab name="param1" label="课件简介"  visible="true"  >
     				<div >	
     					<table width="100%" border="0" cellspacing="0"  cellpadding="0" >
							<tr>
								<td style="padding-left: 10px;">
									<%=lessonContent %>
								</td>
							</tr>		
						</table>
					</div>
    			</hrms:tab>	
			     <hrms:tab label="评论" name="param3" visible="true">
			     	 
			<table width="100%" border="0" cellspacing="0"  cellpadding="0" class="ListTable" style="margin:0px;margin-top:3px;">
				<tr>
					<td align="left" class="TableRow">
						&nbsp;<img src="/images/tree_collapse.gif" onclick="hideShow(this);" alt="隐藏" border="0" style="cursor: hand;vertical-align: top;" />评论
					</td>
				</tr>
				<tr id="commenthide">
					<td height="60" class="RecordRow">
						<div id="commentDivId" style="position: relative;width:617px;height: 300px;float:none;overflow-y:auto;overflow-x:hidden;text-align:center; ">
							<%for (int i = 0; i < commentList.size(); i++){   %>
								<div class="commentdiv common_border_color">
									<%Map map = (HashMap) commentList.get(i); %>
									<p style="margin:10px 10px 0xp 10px;padding:0px;"><%=map.get("a0101") %>&nbsp;发表于&nbsp;<%=map.get("createtime") %></p>
									<hr color="#9A9A9A" width="100%" style="border: 1px dashed;"/>
									<p style="margin:0px 10px 10xp 10px;padding:0px;"><%=map.get("comments") %></p>
								</div>
							<%} %>
						</div>
					</td>
				</tr>
				<%if ("me".equalsIgnoreCase(opt)) {%>
				<tr>
					<td align="left" class="TableRow">
						&nbsp;&nbsp;发表您的留言
					</td>
				</tr>
				<tr>
					<td class="RecordRow">
						<textarea rows="1" cols="1" style="height:120px;width: 100%;border:0px;" id="comment" onkeyup="changetype(this,'leaveword');"></textarea>
					</td>
				</tr>
				<tr>
					<td class="RecordRow" height="40" align="center" style="height:35px;">
						<input type="button" value="提交" id="leaveword" class="mybutton" onclick="subComment()" disabled>
					</td>
				</tr>		
				<%} %>
			</table>
     	     </hrms:tab>
           </hrms:tabset>
         </div>
	  </td>
    </tr>
</table>
	<%}else{ %>
		<table width="30%"  border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:20px;">
			<tr><td class="TableRow" align="left">&nbsp;提示信息</td></tr>
			<tr><td style="border: solid 1px #C4D8EE;height: 100px" align="center" valign="middle">您没有权限浏览或查看该课程！</td></tr>
		</table>
	<%} %>
</body>
<logic:notEqual value="1" name="isMobile">
<logic:notEqual value="2" name="isMobile">
<script language="javascript">
  initDocument();
</script>
</logic:notEqual>
	</logic:notEqual>
</html>
<script type="text/javascript">
/**
	function learnedCourse (nbase,a0100,courseId) {
		var map = new HashMap();
		map.put("nbase",nbase);
		map.put("a0100",a0100);
		map.put("R5100",courseId);
		map.put("type","1");// 1为其他课件，3为视频课件
		Rpc({functionId:'2020030172',sync:true,success:learnedCourseSucc},map);
	}

	function learnedCourseSucc(response) {
		var value=response.responseText;
		var map=Ext.decode(value);
		if(map.succeed){
			document.getElementById("span_" + map.id).innerHTML = "已学";
			document.getElementById("learned_" + map.id).style.display = "none";
			
		}
	}
	*/
	function checkCount() {
		if (document.getElementById("noteText").value.length > 500) {
			document.getElementById("noteText").value = document.getElementById("noteText").value.substring(0, 500);
		}
	}	
	
	function PlayFlv(divid,filePath){	
		if (!filePath) {
			alert(STREAMPLAYER_NO_FILE);
			return ;
		}
	
		if (!divid) {
			alert(STREAMPLAYER_NO_DIV);
			return ;
		}
		
		var fileNa = filePath.substr(filePath.lastIndexOf(".")); 
		
		if (fileNa.toLowerCase().indexOf('.mp3') != -1 || fileNa.toLowerCase().indexOf('.mp4') != -1 || fileNa.toLowerCase().indexOf('.flv') != -1 || fileNa.toLowerCase().indexOf('.f4v') != -1) {
			var div = document.getElementById(divid);
			var htmlStr ="";
			htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:"+620+"px;height:"+330+"px'";  
			htmlStr += " id='"+ divid +"_player'></a>"; 
			div.innerHTML = htmlStr;
			if (fileNa.toLowerCase().indexOf('.mp3') != -1 )  {
				player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
					clip: {    
	           			provider: 'audio', 
	            		live: false,   
	            		autoBuffering: false,     //是否自动缓冲视频，默认true   
	            		autoPlay: true, 
	            		url:filePath
        			},    
       				plugins: { 
	        			audio: {
							url: '/general/flowplayer/flowplayer.audio-3.2.2.swf'
						},

            			controls: {    
		                	url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
		                	autoHide:'always',   
		                	play: true,    
		                	scrubber: true,    
		                	playlist: false,   
		                	tooltips: {    
		                    	buttons: true,    
		                    	play:'播放',   
		                    	fullscreen: '全屏' ,   
		                    	fullscreenExit:'退出全屏',   
		                    	pause:'暂停',   
		                    	mute:'静音',   
		                    	unmute:'取消静音'  
	                		}    
            			}   
        			}        
    		});
		} else {
			player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
					clip: {    
    //       	provider: 'audio', 
	            		live: false,   
	            		autoBuffering: false,     //是否自动缓冲视频，默认true   
	            		autoPlay: true, 
	            		url:filePath
	            		
        			}, 
        			   
       				plugins: { 
        	/**rtmp: {    
                url: '/general/flowplayer/flowplayer.rtmp-3.2.3.swf',    
                netConnectionUrl: netConnectionUrl    
            },   **/
            			controls: {    
			                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
			                autoHide:'always',   
			                play: true,    
			                scrubber: true,    
			                playlist: false,   
			                tooltips: {    
			                    buttons: true,    
			                    play:'播放',   
			                    fullscreen: '全屏' ,   
			                    fullscreenExit:'退出全屏',   
			                    pause:'暂停',   
			                    mute:'静音',   
			                    unmute:'取消静音'  
			                }    
            			}   
        		}   
        
    		}); 
    	}
	}else {
		var div = document.getElementById(divid);
		div.innerHTML +="<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP'>";
		div.innerHTML +="<PARAM NAME='Name' VALUE='WMP1'>";
		div.innerHTML +="<PARAM NAME='URL' VALUE='"+ netConnectionUrl + "/" +filePath +"'>";
		div.innerHTML +="</OBJECT>";
	}
} 

function showStreamPlayer(divid,netConnectionUrl,filePath,media_server,width,height,postion) {
	if (!netConnectionUrl) {
		alert(STREAMPLAYER_NO_NETURL);
		return ;
	}
	
	if (!postion) {
		postion = 0;
	}
	
	if (!filePath) {
		alert(STREAMPLAYER_NO_FILE);
		return ;
	}
	
	if (!media_server) {
		if ("red5" != media_server.toLowerCase() && "microsoft" != media_server.toLowerCase()) {
			alert(STREAMPLAYER_PROTOCOL_ERROR);
			return ;
		}
	}
	
	if (!divid) {
		alert(STREAMPLAYER_NO_DIV);
		return ;
	}
	
	if ("red5" == media_server.toLowerCase()) {
		var div = document.getElementById(divid);
		var htmlStr ="";
		htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:"+width+"px;height:"+height+"px'";  
		htmlStr += " id='"+ divid +"_player'></a>"; 
		div.innerHTML = htmlStr;
		
		 
		player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
			clip: {    
           	provider: 'rtmp', 
            live: false,   
            autoBuffering: true,     //是否自动缓冲视频，默认true   
            autoPlay: true, 
            url:filePath,
            start:0.1
           // streams:[{url:filePath,duration: 240}]
            
            
        },    
       	plugins: { 
        	rtmp: {    
                url: '/general/flowplayer/flowplayer.rtmp-3.2.3.swf',    
                netConnectionUrl: netConnectionUrl    
            },   
            controls: {    
                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
                autoHide:'always',   
                play: true,    
                scrubber: true,    
                playlist: false,   
                tooltips: {    
                    buttons: true,    
                    play:'播放',   
                    fullscreen: '全屏' ,   
                    fullscreenExit:'退出全屏',   
                    pause:'暂停',   
                    mute:'静音',   
                    unmute:'取消静音'  
                }    
            }   
        }   
        
    	});
    	
 		// 禁止拖动
 		/**player.onBeforeSeek(function() {
 			return false;
 		} ); **/
    	
    	
	}
	
	if ("microsoft" == media_server.toLowerCase()) {
		var div = document.getElementById(divid);
	}
} 

<%if ("3".equals(courseType)) {%>
<%if (MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) { %>
 		<%if ("red5".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) { %>
 				showStreamPlayer("divid","rtmp://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() %>/elearning","<%=pathMap.get("filePath") %>","<%=MediaServerParamBo.getMediaServerType() %>",618,330,<%=learnedTime%>);
 			<%} else if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {%>
 				showStreamPlayer("divid","mms://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() +"/" + (SystemConfig.getPropertyValue("projectrootname").length() > 0 ? SystemConfig.getPropertyValue("projectrootname") : "elearning") %>","<%=pathMap.get("filePath") %>","<%=MediaServerParamBo.getMediaServerType() %>",620,330);
 			<%}%>	
 		

	<%}else{ %>
		PlayFlv("divid","<%=pathMap.get("ppath") %>");
	<%}%>
<%}%>

function subComment() {
	// 获得填写的评论
	var comment = document.getElementById("comment").value;
	var map = new HashMap();
	map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
	var today = new Date();
	var year = today.getYear();
	var month = today.getMonth()+1;
	month = month < 10 ? "0" + month : month;
	var date = today.getDate();
	date = date < 10 ? "0" + date : date;
	var hour = today.getHours();
	hour = hour < 10 ? "0" + hour : hour;
	var minute =today.getMinutes();
	minute = minute < 10 ? "0" + minute : minute;
	var second = today.getSeconds();
	second = second < 10 ? "0" + second : second;
	var todaytime = year + "-" + month + "-" +date + " " + hour + ":" + minute + ":" + second;
	map.put("createtime",todaytime);
	map.put("state","0");
	map.put("comment",comment);  
	Rpc({functionId:'2020030170',success:addComment},map);
}

// 添加评论
function addComment(response) {
	var value=response.responseText;
		var map=Ext.decode(value);
		if(map.succeed)
		{
		var div = document.getElementById("commentDivId");
			div.innerHTML = div.innerHTML + "<div class='commentdiv common_border_color'><p style='margin:10px 10px 0xp 10px;padding:0px;'>" + map.a0101 + "&nbsp;发表于&nbsp;" + map.createtime + "</p><hr color='#9A9A9A' width='100%' style='border: 1px dashed;'/><p style='margin:0px 10px 10xp 10px;padding:0px;'>" + map.comment + "</p></div>";
			document.getElementById("comment").value = "";
			document.getElementById("leaveword").disabled = true;
		}
}

function hideShow(obj){
	if(obj.alt=='隐藏'){
		obj.src="/images/tree_expand.gif";
		obj.alt="显示";
		document.getElementById("commenthide").style.display='none';
	}else{
		obj.src="/images/tree_collapse.gif";
		obj.alt="隐藏";
		document.getElementById("commenthide").style.display='';
	}
}

function changetype(obj,id){
	if(obj.value.replace(/^\s+|\s+$/g,""))
		document.getElementById(id).disabled = false;
	else
		document.getElementById(id).disabled = true;
}
function SetWin(obj){
	var win=obj;
	if (document.getElementById){
		if (win && !window.opera){
			if (win.contentDocument && win.contentDocument.body.offsetHeight) 
   				win.height = win.contentDocument.body.offsetHeight; 
    		else if(win.Document && win.Document.body.scrollHeight)
   				win.height = win.Document.body.scrollHeight;
   			
   			if (win.contentDocument && win.contentDocument.body.offsetWidth) 
   				win.width = win.contentDocument.body.offsetWidth; 
    		else if(win.Document && win.Document.body.scrollWidth)
   				win.width = win.Document.body.scrollWidth;
		}
	}
}
function seturl(id){
	if(document.getElementById(id)){
		//window.open("/train/resouce/lessons.do?b_query=link&src="+document.getElementById(id).src);
		var urlstr = document.getElementById(id).src;
		document.getElementById(id).src="";
		window.showModalDialog("learniframe2.jsp?url="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes;location:yes");
		//window.showModalDialog("/train/resouce/lessons.do?b_query=link&src="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:yes;scrollbars:yes");
		document.getElementById(id).src=urlstr;
		document.getElementById(id).width="100%";
		document.getElementById(id).height="100%";
	}
}

function seturl2(id){
	if(document.getElementById(id)){
		//window.open("/train/resouce/lessons.do?b_query=link&src="+document.getElementById(id).src);
		var urlstr = document.getElementById(id).src;
		document.getElementById(id).src="";
		//window.showModalDialog("learniframe2.jsp?url="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:yes;scrollbars:yes;location:yes");
		window.showModalDialog("/train/resouce/lessons.do?b_query=link&src="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
		document.getElementById(id).src=urlstr;
		document.getElementById(id).width="100%";
		document.getElementById(id).height="100%";
	}
}
function setpos() {
	if (document.getElementById("WMP")) {
		pos = document.getElementById("WMP").controls.currentPosition;
	}
}
document.oncontextmenu = function() { return false;}
document.onselectstart=new Function('event.returnValue=false;');
//-->
</script>