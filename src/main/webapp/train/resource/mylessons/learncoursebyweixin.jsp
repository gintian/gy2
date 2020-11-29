<!DOCTYPE html> 
<%@page import="com.hjsj.hrms.businessobject.train.TrainCourseBo"%>
<%@page import="com.hjsj.hrms.actionform.train.resource.TrainLessonForm"%>
<%@ page contentType="text/html; charset=utf-8"%>
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
<%@ page import="com.hjsj.hrms.utils.PubFunc,java.io.*" %>
<%
    // 鍦ㄦ爣棰樻爮鏄剧ず褰撳墠鐢ㄦ埛鍜屾棩鏈� 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userViews=(UserView)session.getAttribute(WebConstant.userView);
	if(userViews != null)
	{
	  userName = userViews.getUserFullName();
	  css_url=userViews.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	} else {
	
		PrintWriter writer = response.getWriter();
		writer.write("鐢ㄦ埛鏈櫥褰曪紝鏃犳硶鎾斁璇句欢");
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
<meta charset="utf-8"> 
<title></title>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
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

<%
	//妫�鏌orm鏄惁瀛樺湪锛屼粠涓婚〉鐩存帴杩涙潵闇�鑷鍒涘缓
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
	/**flowplayer鑴氭湰涓巋r涓殑js鏈夊啿绐侊紝涓嶅緱宸插皢浠ｇ爜鍐欏湪浜嗛〉闈笂**/
	MyLessonBo bo = new MyLessonBo();
	// 鐢ㄦ埛
	TrainCourseBo tbo = new TrainCourseBo(userViews);
	String a0100 = userViews.getA0100();
	String nbase = userViews.getDbname();

	// 璇剧▼id
	String lessonId = request.getParameter("lesson");
	lessonId = PubFunc.decrypt(SafeCode.decode(lessonId));
	
	// 鑾峰緱璇句欢id
	String courseId = request.getParameter("course");
	courseId = PubFunc.decrypt(SafeCode.decode(courseId));
	// 宸插绉掓暟
	int learnedTime = 0;
	// 瑙嗛闀垮害
	int videoTimes = 0;
	// 璇句欢绫诲瀷
	String courseType = "";
	// 璇句欢璺緞
	String coursePath = "";
	
	String courseContext = "";
	// 閿欒
	String erro = "";
	// 璺緞澶勭悊
	Map pathMap = new HashMap();
	// 璇句欢瀛︿範鐘舵��
	String state = "";
	//褰撳墠璇句欢鍐呭
	String lessonContent = "";


	Connection conn = null;
	ResultSet rs = null;
	try {
		conn = AdminDb.getConnection();
		ContentDAO dao = new ContentDAO(conn);
		StringBuffer buff = new StringBuffer();

			// 鏌ヨ璇句欢
			
			buff.append("select t.id,t.learnedhour,t.r5100,r.r5105,r.r5113,r.r5115,r.r5111,r.r5117");
				buff.append(" from tr_selected_course t left join r51 r ");
				buff.append("on r.r5100=t.r5100 where t.a0100='");
				buff.append(a0100);
				buff.append("' and t.nbase='");
				buff.append(nbase);
				buff.append("' and r.r5000=");
				buff.append(lessonId);
				buff.append(" and t.r5100=");
				buff.append(courseId);
				buff.append(" and r.r5100=");
				buff.append(courseId);
				buff.append("");
				
				
		
			rs = dao.search(buff.toString());
			if (rs.next()) {
				learnedTime = rs.getInt("learnedhour") * 60;
				courseId = rs.getString("r5100");
				courseType = rs.getString("r5105");
				coursePath = rs.getString("r5113");
				courseContext = rs.getString("r5115");
				videoTimes = rs.getInt("r5117");
				lessonContent = PubFunc.toHtml(rs.getString("r5111"));
			}
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(lessonContent == null)
		{
			lessonContent = "";
		}
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

	// 澧炲姞鎾斁娆℃暟
	bo.addPlayCount(courseId);
	
	
 %>
<%
 	bo.updateLearnedState(courseId,userViews); 
 	
 	String filePath = (String)pathMap.get("filePath");
 	//寰俊绔棰戞挱鏀惧繀椤绘槸MP4鎴杕p3鏍煎紡
 	filePath = MyLessonBo.filePathToMp4(filePath);
 	
 	String mediaServerAddress = (String)MediaServerParamBo.getMediaServerAddress();
 	String mediaServerPort = (String)MediaServerParamBo.getMediaServerPort();
 	
%>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/ext/adapter/ext/ext-base.js"></script>
<script language="javascript" src="/ext/ext-all.js"></script>
<script language="javascript" src="/ext/rpc_command.js"></script>

<link rel="stylesheet" href="/css/css1.css" type="text/css">
<style type="text/css">
#centervideo { 
    width:100%; 
    height:100%; 
    margin:auto;
} 
body{background:#FFFFFF;background-color:#FFFFFF; }
</style>

<body onunload="saveTime()" style="margin: 0;padding: 0;border: 0;">
<div id="centervideo">
<video id="video1" width="100%" title="<%=12312 %>" height="100%" controls="controls" autoplay="autoplay" oncanplay="initTime()" onseeked="saveTime()" onended="saveTime()">
    <source src="http://<%=mediaServerAddress %>:<%=mediaServerPort %><%=filePath %>" type="video/mp4"></source>
    鎮ㄧ殑娴忚鍣ㄤ笉鏀寔 HTML5 video 鏍囩銆�
  </video>
</div>
</body>
</html>
<script type="text/javascript">
//淇濆瓨鎾斁杩涘害
function saveTime() {
	var video = document.getElementById("video1");
	var map = new HashMap();
	if (video && video.currentTime) {
		map.put("learned",video.currentTime+"");
		map.put("R5100","<%=SafeCode.encode(PubFunc.encrypt(courseId))%>");
		map.put("total",video.duration+"");
		Rpc({functionId:'2020030172',success:succsaveTime},map);
	} 
}

function succsaveTime() {
}

function initTime() {
	if (<%=learnedTime %> < 0) 
		return;
	
	var video = document.getElementById("video1");
	video.currentTime = <%=learnedTime %>;
}

window.onbeforeunload = function() {
	saveTime();
};
//鏃犳硶鐩戝惉寰俊鑷甫鐨勮繑鍥炴寜閽簨浠讹紝鎵�浠ユ瘡闅斾竴鍒嗛挓淇濆瓨涓�娆℃挱鏀捐繘搴�
setTimeout("saveTime()",60000); 
</script>

