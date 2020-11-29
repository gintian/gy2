<!DOCTYPE html> 
<%@page import="com.hjsj.hrms.businessobject.sys.ConstantXml"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.sql.*"%>
<%@page import="com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
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
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=7;IE=8;IE=9;">
<script language="javascript" src="/train/resource/course/html5media.min.js"></script>
</head>
 <title></title>
<script language="javascript">
    var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
//	var ViewProperties=new ParameterSet();
	var webserver=<%=flag%>;
	
</script>
<script language="JavaScript" src="/module/utils/js/template.js"></script>
 <script type="text/javascript" src="/ext/rpc_command.js"></script>
 <script type="text/javascript" src="/ajax/basic.js"></script>
 <script language="javascript" src="/js/constant.js"></script>
<%
	
	String etoken =(String) request.getParameter("etoken");
	if(etoken != null && etoken.length() >0) {
		etoken = SafeCode.convert64BaseToString(SafeCode.keyWord_reback(etoken), "GBK");
		
		String up[] = etoken.split(",");
		String ssoUsername = up[0];
		String pwd = "";
		if(up.length==2){
			pwd = up[1];
		}
		// 登录用户对象
		UserView userViewa = (UserView)session.getAttribute("userView");
		// 是否登录标志
		Boolean islogon = (Boolean) session.getAttribute("islogon");
		if (userViewa != null && islogon != null && islogon) { 
			// System.out.println("--已经存在userview，无需再登陆,直接进入系统--");
			
		} else{
		    Connection conn = null;
		// 如果登录用户对象不存在，可以创建登录用户对象
			try {
			    conn = AdminDb.getConnection();
				userViewa = new UserView(ssoUsername, conn);
				// 调用canLogin方法后，可以直接获取该用户的权限
				if (userViewa.canLogin()) { 
					// 将用户对象保存到session中
					session.setAttribute("username", ssoUsername);
					// 将登录标志保存到session中
					session.setAttribute("islogon", true);
					// username是业务系统本身的登录用户属性名,
					session.setAttribute("userView", userViewa);
				} else { // 用户不存在或被禁用
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			    PubFunc.closeResource(conn);
			}
		}
	}
	//检查form是否存在，从主页直接进来需自行创建
    TrainProjectForm trainProjectForm = (TrainProjectForm)session.getAttribute("trainProjectForm");
    if(null == trainProjectForm)
    {
    	trainProjectForm = new TrainProjectForm();
    	trainProjectForm.setIsLearned("1");
    	session.setAttribute("trainProjectForm", trainProjectForm);
    }
    
    String learnState = request.getParameter("state");
	if("1".equalsIgnoreCase(learnState))
	    trainProjectForm.setIsLearned("0");
	else
	    trainProjectForm.setIsLearned("1");
    
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
	/**flowplayer脚本与hr中的js有冲突，不得已将代码写在了页面上**/
	MyLessonBo bo = new MyLessonBo();
	// 用户
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String a0100 = userView.getA0100();
	String nbase = userView.getDbname();
	// 标志是否是从我的课程进入该页面，用该值控制是否显示笔记和发表评论功能,me为是学习页面，sss是试听页面，my为自助上传课程浏览（为空时默认是sss）
	String opt = request.getParameter("opt");
	opt = opt == null || opt.length() < 1 ? "sss" : opt; 
	// 课程id
	String lessonId = request.getParameter("lesson");
	lessonId = PubFunc.decrypt(SafeCode.decode(lessonId));
	//课程名称
	String lessonName = "";
	if("me".equalsIgnoreCase(opt))
		lessonName = bo.getMyLessonName(lessonId,a0100,nbase);
	else {
		TrainCourseBo tbo = new TrainCourseBo(userView);
		String b0110 = tbo.getUnitIdByBusi();
		lessonName = bo.getLessonName(lessonId,b0110);
	}
	// 课程分类
	String classes = request.getParameter("classes");
	classes = PubFunc.decrypt(SafeCode.decode(classes));
	//课程状态，ing为正学，ed为已学(从我的课程进入才有值)
	String lessonState = request.getParameter("lessonState");
	lessonState = lessonState == null ?"":lessonState;
	
	// 获得课件id
	String courseId = request.getParameter("course");
	courseId = PubFunc.decrypt(SafeCode.decode(courseId));
	// 已学秒数
	int learnedTime = 0;
	// 视频长度
	int videoTimes = 0;
	// 课件类型
	String courseType = "";
	// 课件路径
	String coursePath = "";
	String courseContext = "";
	// 错误
	String erro = "";
	// 路径处理
	Map pathMap = new HashMap();
	// 课件学习状态
	String state = "";
	//当前课件内容
	String lessonContent = "";	
	//文件id
	String fileid = "";	
	
	//休息时间	
	String learnTime = "";
	//是否是浏览课件（1：是 ， 其他值：否，默认否）
	String show = request.getParameter("show");
	ResultSet rs = null;
	Connection conn = null;
	String speed = "0";
	
	try {
	    conn = AdminDb.getConnection();
	    
	    ConstantXml constantbo = new ConstantXml(conn,"TR_PARAM");
	    learnTime = constantbo.getNodeAttributeValue("/param/rest_hint", "interval");
	    speed = constantbo.getNodeAttributeValue("/param/lesson_hint", "speed");
	    speed = StringUtils.isEmpty(speed) ? "0" : speed;
	    
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buff = new StringBuffer();
		if (courseId == null || courseId.length() <= 0) {
			
			// 查询未学的第一个视频课件
			buff.append("select t.id,t.learnedhour,t.r5100,r.r5105,r.r5113,r.r5111,r.r5115,r.r5117,r.fileid");
			buff.append(" from tr_selected_course t left join r51 r ");
			buff.append("on r.r5100=t.r5100 where t.state<>2 and t.a0100='");
			buff.append(a0100);
			buff.append("' and t.nbase='");
			buff.append(nbase);
			buff.append("' and r.r5000=");
			buff.append(lessonId);
			buff.append(" and r.r5105='3' and t.learnedhour < r.r5117");
			buff.append(" order by t.r5100 desc");
		
			rs = dao.search(buff.toString());
			if (rs.next()) {
				learnedTime = rs.getInt("learnedhour") * 60;
				courseId = rs.getString("r5100");
				courseType = rs.getString("r5105");
				coursePath = rs.getString("r5113");
				courseContext = rs.getString("r5115");
				videoTimes = rs.getInt("r5117");
				videoTimes = rs.getInt("r5117");
				fileid = rs.getString("fileid");
				lessonContent = PubFunc.toHtml(rs.getString("r5111"));
			} else {
				// 查询未学的第一个课件
				buff.delete(0, buff.length());
				buff.append("select t.id,t.learnedhour,t.r5100,r.r5105,r.r5113,r.r5115,r.r5111,r.r5117,r.fileid");
				buff.append(" from tr_selected_course t left join r51 r ");
				buff.append("on r.r5100=t.r5100 where t.state<>2 and t.a0100='");
				buff.append(a0100);
				buff.append("' and t.nbase='");
				buff.append(nbase);
				buff.append("' and r.r5000=");
				buff.append(lessonId);
				buff.append(" order by t.r5100 desc");
				
				rs = dao.search(buff.toString());
				if (rs.next()) {
					learnedTime = rs.getInt("learnedhour") * 60;
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					fileid = rs.getString("fileid");
				}
			}
			
			if (courseId == null || courseId.length() <= 0) {
				// 查询第一个课件
				buff.delete(0, buff.length());
				buff.append("select t.id,t.learnedhour,t.r5100,r.r5105,r.r5113,r.r5115,r.r5111,r.r5117,r.fileid");
				buff.append(" from tr_selected_course t left join r51 r ");
				buff.append("on r.r5100=t.r5100 where t.a0100='");
				buff.append(a0100);
				buff.append("' and t.nbase='");
				buff.append(nbase);
				buff.append("' and r.r5000=");
				buff.append(lessonId);
				buff.append(" order by t.r5100 desc");
				
				rs = dao.search(buff.toString());
				if (rs.next()) {
					learnedTime = rs.getInt("learnedhour") * 60;
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					fileid = rs.getString("fileid");
				}
			}
			
			if(courseType == null || "".equals(courseType.trim())){ //查询当前课程下的第一个课件 如果是未选择的课程
				String sql = "select * from r50 r,r51 t where r.r5000 = t.r5000 and t.r5000 = '"+lessonId+"' order by t.r5100 desc";
				rs = dao.search(sql);
				if(rs.next()){
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					fileid = rs.getString("fileid");
				}
			}
		} else {
			// 查询课件
			buff.delete(0, buff.length());
			buff.append("select t.id,t.learnedhour,t.r5100,r.r5105,r.r5113,r.r5115,r.r5111,r.r5117,r.fileid");
			buff.append(" from tr_selected_course t left join r51 r ");
			buff.append("on r.r5100=t.r5100 where t.a0100='");
			buff.append(a0100);
			buff.append("' and t.nbase='");
			buff.append(nbase);
			buff.append("' ");
			buff.append(" and t.r5100=");
			buff.append(courseId);
			buff.append(" order by t.r5100 desc");
		
			rs = dao.search(buff.toString());
			if (rs.next()) {
				learnedTime = rs.getInt("learnedhour") * 60;
				courseId = rs.getString("r5100");
				courseType = rs.getString("r5105");
				coursePath = rs.getString("r5113");
				courseContext = rs.getString("r5115");
				videoTimes = rs.getInt("r5117");
				lessonContent = PubFunc.toHtml(rs.getString("r5111"));
				fileid = rs.getString("fileid");
			}
			
			if(courseType == null || "".equals(courseType.trim())){ //查询当前课程下的第一个课件 如果是未选择的课程
				buff.delete(0, buff.length());
				buff.append("select r5100,r5105,r5113,r5115,r5111,r5117,fileid");
				buff.append(" from r51 ");
				buff.append(" where r5100=");
				buff.append(courseId);
			
				rs = dao.search(buff.toString());
				if (rs.next()) {
					courseId = rs.getString("r5100");
					courseType = rs.getString("r5105");
					coursePath = rs.getString("r5113");
					courseContext = rs.getString("r5115");
					videoTimes = rs.getInt("r5117");
					lessonContent = PubFunc.toHtml(rs.getString("r5111"));
					fileid = rs.getString("fileid");
				}
			}
		
		
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(lessonContent == null)
			lessonContent = "";
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
		pathMap.put("showFile", "/servlet/vfsservlet?fileid=" + fileid);
	}

	if(!"1".equalsIgnoreCase(show))
		// 增加播放次数
		bo.addPlayCount(courseId);

	String openoffice = SystemConfig.getPropertyValue("openoffice");
	String swftools = SystemConfig.getPropertyValue("swftools");
	
 %>
 <logic:equal name="trainProjectForm" property="isLearned" value="0">
 	<%bo.updateLearnedState(courseId,userView); %>
 </logic:equal>
<script language="javascript" src="/general/flowplayer/js/flowplayer-3.2.6.min.js"></script>
<script language="javascript" src="/train/resource/mylessons/learncoursemenu.js"></script>
<script language="javascript" src="/train/resource/mylessons/learncoursecomment.js"></script>
<script type="text/javascript">
	var player = null;
var ttt = null;
var pos  = 0;
</script>
<logic:equal name="trainProjectForm" property="isLearned" value="0">
<SCRIPT type="text/javascript" for="WMP"  event="playStateChange(NewState)">
var learnTime = <%=learnedTime%>;
	var co = 1;
	switch(NewState){ 
    	case 8: 
    	pos = Ext.getDom("WMP").currentMedia.duration;
      	saveTime(); 
      	break; 
		case 6: 
 		//Ext.getDom("WMP").controls.SetCurrentEntry(<%=learnedTime%>); 
 		//alert(Ext.getDom("WMP").controls.currentPosition); 
 		if (co == 1&&<%=learnedTime%> < Ext.getDom("WMP").currentMedia.duration && '<%=learnedTime%>'.length > 0 && '<%=learnedTime%>'>'0')
 		Ext.getDom("WMP").controls.currentPosition = parseFloat(<%=learnedTime%>);
 		co++;   
      	break;
    	
	} 
</SCRIPT>
</logic:equal>
<style>
		.mediadiv{
			position: relative;
			width:100%;
			height:100%;
			border: solid 1px #C4D8EE;
			padding: 0;
		}
		ul{
			padding:0;
			margin:0;
			list-style: none;
		}
</style>
<hrms:themes/>
<% UserView userviw=(UserView)session.getAttribute(WebConstant.userView);
if("hcm".equals(userviw.getBosflag())){ 
	String themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());%>
  <script language="javascript">
    hcm_tabset_root="/images/hcm/themes/<%=themes %>/content/";
  </script>
  <%} %>
 <link rel="stylesheet" href="/train/resource/mylessons/learncourse.css" type="text/css"></link> 
<logic:equal name="trainProjectForm" property="isLearned" value="0">
<body>
</logic:equal>

<logic:notEqual name="trainProjectForm" property="isLearned" value="0">
<body >
</logic:notEqual>
	<div id="panel"></div>
	<div id="mediaDiv" style="width: 100%;height: 100%">
	<%
	if(userView.hasTheFunction("090905") || ("sss").equalsIgnoreCase(opt) || ("my").equalsIgnoreCase(opt)){
	%>
	<%if(lessonName!=null && !"".equals(lessonName)){ %>
		<div class="headdiv" style="width: 100%;height: 100%;">
			<div id="desclesson" style="display: none;">
			<iframe src="" id="lessondesc" name="desc" scrolling="auto" height="90%" width="25%" frameborder="0" style="position:absolute; z-index:899; left: 74%; top:15px; bottom: 15px;"></iframe>
			</div>     
			<%if ("3".equals(courseType)) {%>
			<div class="mediadiv common_border_color" id="divid" style="width: 100%;height: 100%;">	
			<%if (MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) { %>
 		
		
 			<% if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {%>
 				<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP' width="100%" height="100%">
		<PARAM NAME='Name' VALUE='WMP1'>
		
		<% String url = "mms://" + MediaServerParamBo.getMediaServerAddress();
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
			<%if ("2".equals(courseType)) { %>
			<div class="mediadiv common_border_color" id="divid" style="padding:8px;overflow: auto;">
			<%=courseContext %>				
			</div>
			<%} %>
			<%if ("1".equals(courseType)) {
			    String ppath = "";
			    if(coursePath != null && coursePath.length() > 0){
			    	ppath = coursePath.replaceAll(Matcher.quoteReplacement(System.getProperty("file.separator")),"/");
			%>
				<%if (coursePath.toLowerCase().endsWith(".doc") || coursePath.toLowerCase().endsWith(".docx") 
								|| coursePath.toLowerCase().endsWith(".xls") || coursePath.toLowerCase().endsWith(".xlsx") 
								|| coursePath.toLowerCase().endsWith(".pdf") || coursePath.toLowerCase().endsWith(".ppt") 
								|| coursePath.toLowerCase().endsWith(".pptx")) {
				if (openoffice != null && openoffice.length() > 0 && swftools != null && swftools.length() > 0) {
				%>
				<script type="text/javascript" src="/general/flexpaper/js/flexpaper_flash.js"></script>
				<div class="mediadiv common_border_color" id="divid" style="text-align: center;padding-top:1px;">			
			<a id="viewerPlaceHolder" style="width:100%;height:100%;display:block"></a>
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
				<div class="mediadiv common_border_color" id="divid" style="text-align: center;padding-top:160px;">
				<a href='/servlet/vfsservlet?fileid=<%=fileid %>'>下载</a>
				</div>
			<%} }%>				
	<%} else { %>

			<div class="mediadiv common_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;height: 310px;">
				<%if(ppath!=null&&ppath.toLowerCase().endsWith(".zip")){ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src="<%=ppath.substring(0,ppath.lastIndexOf("/")+1) + courseId + "/" %>"></iframe>
				<%}else{ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src="<%=ppath %>"></iframe>
				<%} %>
			</div>
			<%} %>				
			
			<%}else {
			    %>
			    <div class="mediadiv common_border_color" style="text-align: center;padding-top:1px;height: 325px;"></div>
			    <%
			}
			} %>
			<%if ("6".equals(courseType)) {%>
				<div  class="TableRow" style="width: 100%;height: 20px;border-bottom: 0px;padding: 3px;"><a style="border: 0px;" href="javascript:seturl('iframeid');">&nbsp;全屏&nbsp;</a></div>
				<div class="mediadiv common_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;">
				<%if(coursePath!=null&&coursePath.length()>0){ %>
				<iframe frameborder="0" id="iframeid" onload="SetWin(this)" src=""></iframe>
				<script type="text/javascript">
					Ext.getDom("iframeid").src="<%=coursePath %>";
					Ext.getDom("iframeid").width="100%";
					Ext.getDom("iframeid").height="100%";
				</script>
				<%} %>
				</div>
			<%} %>
			<%if ("4".equals(courseType)) {%>
				<div class="mediadiv common_border_color" id="divid" style="text-align: center;padding-top:0px;overflow: auto;">
				<table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%">
					<tr>
						<td height="100%">
						<logic:equal name="trainProjectForm" property="isLearned" value="0">
						<iframe width="100%" height="100%" frameborder="0" id="iframeid" src="/train/resouce/lessons.do?b_query=link&isLearn=1&classes=<%=SafeCode.encode(PubFunc.encrypt(classes)) %>&r5000=<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>&r5100=<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>"></iframe>
						</logic:equal>
						<logic:notEqual name="trainProjectForm" property="isLearned" value="0">
						<iframe width="100%" height="100%" frameborder="0" id="iframeid" src="/train/resouce/lessons.do?b_query=link&isLearn=0&classes=<%=SafeCode.encode(PubFunc.encrypt(classes)) %>&r5000=<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>&r5100=<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>"></iframe>
						</logic:notEqual>
						</td>
					</tr>
				</table>
				</div>
			<%} %>
		</div>
		<%if(!"1".equalsIgnoreCase(show)) { %>
			<div id='wait' style='border:1pt solid; position:absolute; display:none;width:350px;height:175px;z-Index:9999;padding-bottom: 20px;' class="common_border_color" >
				 <iframe style="width:350px;height:175px;position:absolute;z-index:-1;" frameborder="0"></iframe>    
				<table border="0" width="350" height="120" cellspacing="0" cellpadding="0"
					class="ListTableF" height="90" align="center" bgcolor="white">
					<tr height="20" class="fixedHeaderTr">
						<td align="left" style="BORDER-BOTTOM:1pt solid; border-right:0;" class="TableRow">
							&nbsp;<b>温馨提示</b>
						</td>
						<td align="right" style="BORDER-BOTTOM:1pt solid; border-left:0;" class="TableRow">
							<img src="/images/del.gif" border="0" onclick="closed();" style="cursor:hand;">&nbsp;
						</td>
					</tr>
					<tr>
						<td colSpan="2" align="center" valign="bottom" >
						<br/>
							您已经连续学习超过<%=learnTime %>分钟，请注意休息。<br/>是否继续学习?<br/><br/>
							<input id="timeText" type="text" name="timeText" disabled="true" class="textClass"/>(秒倒计时)<br/>
							 <input type="button" style="margin-top: 5px;" name="b_js" value='继续学习' onclick="continueToLean();" class="mybutton"/>&nbsp;&nbsp;
							 <input type="button" style="margin-top: 5px;" name="b_js" value='休息一会儿' onclick="takeABreak();" class="mybutton"/>
							 <br/><br/>
						</td>
					</tr>
				</table>
			</div>
		<%} %>
	<%}else{ %>
		<table width="30%"  border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:20px;">
			<tr><td class="TableRow" align="left">&nbsp;提示信息</td></tr>
			<tr><td style="border: solid 1px;height: 100px" class="common_border_color" align="center" valign="middle">您暂时还不能学习或查看该课程！</td></tr>
		</table>
	<%} %>
	<%}else{ %>
	<table width="30%"  border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:20px;">
			<tr><td class="TableRow" align="left">&nbsp;提示信息</td></tr>
			<tr><td style="border: solid 1px;height: 100px" class="common_border_color" align="center" valign="middle"><bean:message key="train.mylearning.nopiv"/></td></tr>
		</table>
	<%} %>
</div>
</body>
</html>
<script type="text/javascript">
var learnedTime = <%=learnedTime%>;
<%if(!"1".equalsIgnoreCase(show)) { %>
var lessonState = "<%=lessonState %>";
var isLearned = "${trainProjectForm.isLearned }";
var state = "<%=learnState%>"
var courseId = "<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>";
var opt = "<%=opt %>";
<%} else {%>
	learnedTime = 0;
<%} %>
var msg = true;
var newPlayTime = "<%=learnedTime %>";
Ext.onReady(function(){
 	var Panel = new Ext.Panel({      
 		xtype:'panel',
		id:'center',
		title:'<ul><li style="float: left;"><%=lessonName %></li><li style="float: right;"><a href="javascript:showdesc();">课程简介</a></li></ul>',
		html:'<div id="lesson" style="width: 100%;height: 100%;padding: 0;"></div>',
		region:'center',
		<%if(!"1".equalsIgnoreCase(show)) { %>
		padding: "0 0 8px 20px",
		<%}else{%>
		padding: "0 20px 8px 20px",
		<%}%>
		cls: 'background-c',
		border: false,
		listeners:{
			resize: function (Panel, width, height, oldWidth, oldHeight, eOpts){
				var video = document.getElementById("video1");
				if(video)
					video.height=height-20;
			}	
		}
	});
 	<%if(!"1".equalsIgnoreCase(show)) { %>
 	var east = new Ext.Panel({      
 		xtype:'panel',
		id:'east',
		html:'<div id="div" style="width: 100%;height: 100%;z-index:899;text-align:center"></div>',
		region:'east',
		width: 320, 
 		height:"100%",
 		collapsible: true,
		border: false,
		bodyStyle: 'background:url(/train/resource/mylessons/images/tabbar-b.jpg);'
	});
<%}%>

 	new Ext.Viewport({
        layout: "border",
        <%if("1".equalsIgnoreCase(show)) { %>
        	items: [Panel]
        <%} else {%>
        	items: [Panel,east]
        <%} %>
    });
	
 	<%if(!"1".equalsIgnoreCase(show)) { %>
 	var width="100%";	
 	var tabs=new Ext.TabPanel({
		renderTo: "div",
 		id: "tabs",
 		width:width,
 		height:"100%",
		border: false,
 		enableTabScroll:true,
 		activeTab:0,
 		padding:'5 5 10 10',
 		border: false,
 		items:[
 			{layout:'fit',title:"课件目录",courseFlag:'0', html:"<div id='coursemeun' style='height: 100%;width: 100%; overflow: auto;'></div>"}
 			<%if("me".equalsIgnoreCase(opt) || "sss".equalsIgnoreCase(opt)) { %>
 		    	,{layout:'fit',courseFlag:'1',title:"&nbsp评&nbsp论&nbsp", html:"<div id='coursecomments' style='height: 100%;width: 100%; '></div>"}
 		    <%}
 			
 			if("me".equalsIgnoreCase(opt)) {  %>
 		    	,{layout:'fit',courseFlag:'2',title:"&nbsp笔&nbsp记&nbsp", html:"<div id='coursenotes' style='height: 100%;width: 100%;'></div>"}
 		    <%} %>
            ],
        listeners:{ 
	        tabchange:function(tp,p){ 
	            if(p.courseFlag=="0")
	            	searchCourseMenu("<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>", "<%=SafeCode.encode(PubFunc.encrypt(classes)) %>","<%=SafeCode.encode(PubFunc.encrypt(filepath)) %>");
	            <%if("me".equalsIgnoreCase(opt) || "sss".equalsIgnoreCase(opt)) { %>
		        else if(p.courseFlag=="1")
	            	Company("<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>", "0");
		        <%}
	 			
	 			if("me".equalsIgnoreCase(opt)) { %>
	            else if(p.courseFlag=="2")
	            	Company("<%=SafeCode.encode(PubFunc.encrypt(courseId)) %>", "1");
	            <%} %>
	        } 
   		} 

        });
    <%}%>
    
 	Ext.getDom('lesson').appendChild(Ext.getDom('mediaDiv'));
 	Ext.getDom('mediaDiv').style.display="block";
 	<%if(!"1".equalsIgnoreCase(show)) { %>
 	Ext.getDom('east').style.background="url(/train/resource/mylessons/images/tabbar-b.jpg)";
 	searchCourseMenu("<%=SafeCode.encode(PubFunc.encrypt(lessonId)) %>", "<%=SafeCode.encode(PubFunc.encrypt(classes)) %>","<%=SafeCode.encode(PubFunc.encrypt(filepath)) %>");
 	<%}%>
});

	function showdesc(){
		Ext.getDom('lessondesc').src="/train/resource/mylessons/lessondesc.jsp?r5000=<%=SafeCode.encode(PubFunc.encrypt(lessonId.toString())) %>";
		Ext.getDom("desclesson").style.display="block";
	}
	
	function PlayFlv(divid,filePath){	debugger;
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
			var div = Ext.getDom(divid);
			var htmlStr ="";
			htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:100%;height:100%;'";  
			htmlStr += " id='"+ divid +"_player'></a>"; 
			div.innerHTML = htmlStr;
			if (fileNa.toLowerCase().indexOf('.mp3') != -1 )  {
				player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
					clip: {    
	           			provider: 'audio', 
	            		live: false,   
	            		autoBuffering: true,     //是否自动缓冲视频，默认true   
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
				//开始事件
				player.onBegin(
			    		function(){
							doThis();
							countDown();
			    		}
			    	);
				//播放事件 
				player.onResume(
						function(){
							<%if(!"".equals(learnTime) && learnTime != null){%>
							if(msg) {
								doThis();
								if(c < 1){
									c = 20+ (<%=learnTime%> * 60);
									countDown(); 
								} else
									c = 20+ (<%=learnTime%> * 60);
									
								document.getElementById("wait").style.display="none";
								saveTime();
							}
							
							msg = true;
							<%}%>
						}	
				);
				
				// 最后一秒的时候将时间保存
		        player.onLastSecond(
		            function() {
		                saveTime();
		                isStop = '0';
		                doThis();
		            }
		        );
		        //结束时保存时间 同事触发事件
		        player.onFinish(
		            function(){
		                saveTime();
		                isStop = 0;
		                doThis();
		            }
		        );
		} else {
			player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
					clip: {    
	            		live: false,   
	            		autoBuffering: true,     //是否自动缓冲视频，默认true   
	            		autoPlay: true, 
	            		url:filePath
	            		
        			}, 
        			   
       				plugins: { 
            			controls: {    
			                url: '/general/flowplayer/flowplayer.controls-3.2.5.swf',   
			                autoHide:'always',   
			                play: true,    
			                scrubber: true,    
			                playlist: true,  
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
			//开始事件
			player.onBegin(
		    		function(){
						doThis();
						countDown();
		    		}
		    	);
			//播放事件 
			player.onResume(
					function(){
						<%if(!"".equals(learnTime) && learnTime != null){%>
						if(msg) {
							doThis();
							if(c < 1){
								c = 20+ (<%=learnTime%> * 60);
								countDown(); 
							} else
								c = 20+ (<%=learnTime%> * 60);
								
							document.getElementById("wait").style.display="none";
							saveTime();
						}
						
						msg = true;
						<%}%>
					}	
			);
			// 最后一秒的时候将时间保存
	        player.onLastSecond(
	            function() {
	                saveTime();
	                isStop = '0';
	                doThis();
	            }
	        );
	        //结束时保存时间 同事触发事件
	        player.onFinish(
	            function(){
	                saveTime();
	                isStop = 0;
	                doThis();
	            }
	        );
    	}
	}else {
		var div = Ext.getDom(divid);
		div.innerHTML +="<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP'>";
		div.innerHTML +="<PARAM NAME='Name' VALUE='WMP1'>";
		div.innerHTML +="<PARAM NAME='URL' VALUE='"+ netConnectionUrl + "/" +filePath +"'>";
		div.innerHTML +="</OBJECT>";
	}
		
} 
	<%if(!"1".equalsIgnoreCase(show)) { %>
	//继续学习
	function continueToLean(){
		saveTime();
		Ext.getDom("wait").style.display="none";
		<%if(!"".equals(learnTime) && learnTime != null){%>
		c = 20+ (<%=learnTime%> * 60 ) + 2;
		//countDown();
		<%}%>
		player.resume();
	}
	//休息一会
	function takeABreak(){
		c = 30*60;
		saveTime();
		Ext.getDom("wait").style.display="none";
		player.pause();
	}
	//点击关闭图片隐藏div并保存进度
	function closed(){
		saveTime();
		Ext.getDom("wait").style.display="none";
	}
	
	//暂停
	function pauses(){
		if(isStop != 0){			
			Ext.getDom('wait').style.display='block';
			player.pause();
		}
	}
	function saveThis(){
		var t = setTimeout("saveTime();", 15 * 60 * 1000);
	}
	//弹出提示
	var isStop = 1;
	function doThis(){
		if(isStop == 1){			
			<%if(!"".equals(learnTime) && learnTime != null){%>
			 var t=setTimeout("pauses();",((<%=learnTime%>  * 60 * 1000 )+ (2*1000))); //休息时间的秒数
			 var a = Ext.getDom("wait");
			a.style.right = 0;//div 出现在右下角
			a.style.bottom = 0;
			<%}%>
		}else if(isStop == 0){
			Ext.getDom("wait").style.display="none";
		}
	}
	// 20秒倒计时
	<%if(!"".equals(learnTime) && learnTime != null){%>
	var c = 20+ (<%=learnTime%> * 60);
	
	var t1;
	function countDown(){
		if(c > 0){		
			Ext.getDom('timeText').value=c;
			c--;
			t1=setTimeout("countDown()",1000);
		}else{
			msg =false;
			Ext.getDom("wait").style.display = "none";
			player.seek(newPlayTime);
			player.resume();
			setTimeout("player.pause()", 3000);
		}
	}
	<%}
	}
	%>
	
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
		var div = Ext.getDom(divid);
		var htmlStr ="";
		htmlStr += "<a href='http://get.adobe.com/cn/flashplayer/download/' style='display:block;width:100%;height:100%;'";  
		htmlStr += " id='"+ divid +"_player'></a>"; 
		div.innerHTML = htmlStr;
		var playTime = 0;
		 
		player = flowplayer(divid + "_player", "/general/flowplayer/flowplayer-3.2.7.swf",{    
			
			clip: {    
           	provider: 'rtmp',        // 视频播放协议    分为'rtmp' 和 'http'
            live: false,   			 //RTMP流媒体直播流媒体服务器的支持，这意味着它可以设置视频摄像机或其他视频源实时视频流数据。默认为false
            autoBuffering: true,     //是否自动缓冲视频，默认true   
            autoPlay: true, 		 //是否自动播放视频，默认true
            url:filePath,			 //加载的视频文件的路径
            start:0.1			     //播放应开始的时间（秒）。
            
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
    	
    	<logic:equal name="trainProjectForm" property="isLearned" value="0">
    	player.onStart (
 			function(){
 				doThis();
 				<%if(!"".equals(learnTime) && learnTime != null){%>
 				countDown(); 
 				<%}%>
 			// 防止postion超过视频总长度
 			if (postion < player.getClip().duration){
 				player.seek(postion);
 			} 
  		});
    	player.onBegin(
    		function(){
				Ext.getDom("wait").style.display="none";
    		}
    	);
    	//播放事件 
		player.onResume(
				function(){
					<%if(!"".equals(learnTime) && learnTime != null){%>
					if(msg) {
						doThis();
						if(c < 1){
							c = 20+ (<%=learnTime%> * 60);
							countDown(); 
						} else
							c = 20+ (<%=learnTime%> * 60);
							
						document.getElementById("wait").style.display="none";
						saveTime();
					}
					
					msg = true;
					<%}%>
				}	
		);
    	player.onPause(
    		function(){
				//saveThis(); //暂停的时候开始计时
    		}	
    	);
 		// 最后一秒的时候将时间保存
 		player.onLastSecond(
 			function() {
 				saveTime();
 				isStop = '0';
 				doThis();
 			}
 		);
 		//结束时保存时间 同事触发事件
 		player.onFinish(
 			function(){
 				saveTime();
 				isStop = 0;
 				doThis();
 			}
 		);
 		// 禁止拖动
 		player.onBeforeSeek(function() {
 			playTime = player.getTime();
 		}); 

 		player.onSeek(function() {
 	 		var seekTime = player.getTime();
 	 		if("1" == <%=speed %> && seekTime > (postion + 3)  && seekTime > (playTime + 3))
 	 			player.seek(playTime);
			
 		} ); 
 		</logic:equal>
	}
	if ("microsoft" == media_server.toLowerCase()) {
		var div = Ext.getDom(divid);
	}
} 

<%if ("3".equals(courseType)) {%>
<%if (MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) { %>
 		<%if ("red5".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) { %>
 				showStreamPlayer("divid","rtmp://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() %>/elearning","<%=pathMap.get("filePath") %>","<%=MediaServerParamBo.getMediaServerType() %>",618,330,<%=learnedTime%>);
 			<%} else if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {%>
 			showStreamPlayer("divid","mms://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() +"/" + (SystemConfig.getPropertyValue("projectrootname").length() > 0 ? SystemConfig.getPropertyValue("projectrootname") : "elearning") %>","<%=pathMap.get("filePath") %>","<%=MediaServerParamBo.getMediaServerType() %>",620,330);
 			<%} else if ("HTTP".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {
 			    String mediaServerAddress = (String)MediaServerParamBo.getMediaServerAddress();
 			   if (coursePath != null) {
 			      coursePath = coursePath.replaceAll("\\\\","/");
 					int index = coursePath.lastIndexOf("coureware/");
 					coursePath = coursePath.substring(index + 10, coursePath.length());
 				} else {
 					coursePath = "";
 				}
 			 	String mediaServerPort = (String)MediaServerParamBo.getMediaServerPort();
 			 	coursePath = MyLessonBo.filePathToMp4(coursePath);
 			%>
 				var htmlStr ='<video id="video1" width="100%" height="95%" autobuffer="true" controls autoplay';
 				if ("1" != <%=show %> && "me" == opt && "ing" == lessonState)
 					htmlStr +=' oncanplay="initTime()" onseeked="saveTime()" onended="saveTime()"';

 				htmlStr +='>';
 				htmlStr += '<source src="http://<%=mediaServerAddress %>:<%=mediaServerPort %>/<%=coursePath %>" type="video/mp4"></source>';
 				htmlStr += '</video>';
 				var div = document.getElementById("divid");
 				div.innerHTML = htmlStr;
 			<%}%>
	<%}else{ %>
		alert(MEDIASERVERADDRESS_IS_NULL);
		//PlayFlv("divid","<%=pathMap.get("showFile") %>");
	<%}%>
<%}%>

<%if(!"1".equalsIgnoreCase(show)) { %>
function saveTime() {
	var map = new HashMap();
	if (player && player.getTime()) {
		player.onPause();
		newPlayTime = player.getTime();
		map.put("learned",player.getTime()+"");
		map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
		map.put("total",player.getClip().duration+"");
		map.put("type","<%=courseType%>");
		Rpc({functionId:'2020030172', success:succsaveTime},map);
	} 

	if (Ext.getDom("WMP")) {
		if (pos == 0) {
			pos = Ext.getDom("WMP").controls.currentPosition;
		}
		map.put("learned",pos+"");
		map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
		map.put("total",Ext.getDom("WMP").currentMedia.duration+"");
		map.put("type","<%=courseType%>");
		Rpc({functionId:'2020030172', success:succsaveTime},map);
	}
	
	if (myflowplayer && myflowplayer.getTime()) {
		map.put("learned",myflowplayer.getTime()+"");
		map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
		map.put("total",myflowplayer.getClip().duration+"");
		map.put("type","<%=courseType%>");
		Rpc({functionId:'2020030172', success:succsaveTime},map);
	} 
	var video = Ext.getDom("video1");
	if (video && video.currentTime) {
		map.put("learned",video.currentTime+"");
		map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
		map.put("total",video.duration+"");
		map.put("type","<%=courseType%>");
		Rpc({functionId:'2020030172',success:succsaveTime},map);
	} 
}

function succsaveTime() {
}

function initTime() {
	if (<%=learnedTime %> < 0) 
		return;
	
	var video = Ext.getDom("video1");
	if (video && video.currentTime) {
		video.currentTime = <%=learnedTime %>;
	}
}

function learnedCourse (courseId, coursetype) {
	var map = new HashMap();
	if("3" == coursetype){
		if(!"<%=pathMap.get("ppath") %>"){
			alert(STREAMPLAYER_NO_FILE);
			return;
		}

		map.put("R5100",courseId);
		map.put("type","3");// 1为其他课件，3为视频课件
		map.put("msg","1");//1为视频课件点击学习完毕链接
		map.put("total",player.getClip().duration+"");
	} else {
		map.put("R5100",courseId);
		map.put("type","1");// 1为其他课件，3为视频课件
	}
	Rpc({functionId:'2020030172',success:learnedCourseSucc},map);
}
<%} %>
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
	if(Ext.getDom(id)){
		var urlstr = Ext.getDom(id).src;
		Ext.getDom(id).src="";
		window.showModalDialog("learniframe2.jsp?url="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes;location:yes");
		Ext.getDom(id).src=urlstr;
		Ext.getDom(id).width="100%";
		Ext.getDom(id).height="100%";
	}
}

function seturl2(id){
	if(Ext.getDom(id)){
		var urlstr = Ext.getDom(id).src;
		Ext.getDom(id).src="";
		window.showModalDialog("/train/resouce/lessons.do?b_query=link&src="+urlstr,1,"dialogWidth:"+(screen.availWidth - 10)+"; dialogHeight:"+(screen.availHeight-50)+";resizable:yes;center:yes;scroll:yes;status:no;scrollbars:yes");
		Ext.getDom(id).src=urlstr;
		Ext.getDom(id).width="100%";
		Ext.getDom(id).height="100%";
	}
}

function setpos() {
	if (Ext.getDom("WMP")) {
		pos = Ext.getDom("WMP").controls.currentPosition;
	}
}

document.oncontextmenu = function() { return false;}
<%if(!"1".equalsIgnoreCase(show)) { %>
<logic:equal name="trainProjectForm" property="isLearned" value="0">
	setTimeout("saveTime()", 60000);
</logic:equal>
<%} %>
</script>

