<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.businessobject.train.MediaServerParamBo" %>
<%@ page import="com.hrms.frame.utility.AdminDb,com.hrms.struts.constant.SystemConfig,java.sql.Connection,com.hrms.frame.dao.ContentDAO,javax.sql.RowSet,com.hjsj.hrms.utils.Office2Swf,java.net.URLEncoder" %>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.MyLessonBo"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
	<head>
		<script language="javascript" src="/train/resource/course/html5media.min.js"></script>
		<script language="javascript" src="/general/flowplayer/js/flowplayer-3.2.6.min.js"></script>
		<script language="javascript" src="/js/constant.js"></script>
		<script language="javascript" src="/js/common.js"></script>
		<link rel="stylesheet" href="/css/css1.css" type="text/css">
		<hrms:themes></hrms:themes>
	</head>
<body>
<html:form action="/train/resource/courseware">
	<html:hidden name="coursewareForm" property="a_code" />
	<html:hidden name="coursewareForm" property="r5000" />
	
	<%
		String aCode = request.getParameter("a_code");
		aCode = PubFunc.decrypt(SafeCode.decode(aCode));
		String r5100 = request.getParameter("r5100");
		r5100 = PubFunc.decrypt(SafeCode.decode(r5100));
		Connection conn = null;
		
		String r5103 = "";
		String filePath = "";
		String fileContent = "";
		String ppath="";
		String courseType = "";
		RowSet rs = null;
		try {
			conn = AdminDb.getConnection();
			ContentDAO dao = new ContentDAO(conn);
			String sql="select * from r51 where r5100='"+r5100+"'";
			rs = dao.search(sql);
			
			if (rs.next()) {
				r5103 = rs.getString("r5103");
				filePath = rs.getString("r5113");
				fileContent = rs.getString("r5111");
				courseType = rs.getString("r5105");
			}
			
			if(filePath!=null&&filePath.length()>0){
				//ppath=filePath.substring(filePath.lastIndexOf("coureware\\")-1);
				ppath=filePath.replaceAll("\\\\","/");
			}
			if (fileContent == null) {
				fileContent = "";
			}
			
			if (filePath != null) {
				int index = filePath.lastIndexOf("/");
				if (index == -1) {
					index = filePath.lastIndexOf("\\");
				}
				filePath = filePath.substring(index + 1, filePath.length());
			} else {
				filePath = "";
			}
			
			String abPath = "/";
			if (aCode != null && aCode.length() > 0) {
				if (aCode != null && aCode.length() > 0) {
					for (int i = 0; i < aCode.length() / 2; i++) {
						abPath += aCode.substring(0, 2 * (i + 1)) + "/";
					}
				}
			}
			
			if (abPath.length() > 1) {
				filePath = abPath + filePath;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try{
				if (rs != null) {
					rs.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			try{
				if (conn != null) {
					conn.close();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		
		%>
	
	<table border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top: 10px;">
	<tr><td style="padding: 0 5 0 5;">
	<table width="575" border="0" cellspacing="0" align="center" cellpadding="0">
		<thead>
			<tr>
				<td align="left" class="TableRow" style="border-bottom: none;">
					&nbsp;&nbsp;课件简介(<%=r5103 %>)
				</td>

			</tr>
		</thead>
		<tr>
			<td height="60" class="RecordRow" style="border-bottom: none;">
				<!-- <div style="height:60px;margin:2px;overflow-y: auto;"><%=fileContent %></div> -->
				<textarea rows="1" cols="1" style="height:60px;width: 100%;border:0px;" readonly><%=fileContent %></textarea>
			</td>
		</tr>
		
		<tr>
			<td class="RecordRow" align="center" style="padding: 0;">
				<div id="divid" style="padding: 0;width: 575px;height: 360px;">
					
					<%if ("3".equals(courseType) && MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) { %>
 		
		
 			<% if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) { %>
 				<OBJECT CLASSID='clsid:6BF52A52-394A-11D3-B153-00C04F79FAA6' ID='WMP' width="518" height="330">
		<PARAM NAME='Name' VALUE='WMP1'>
		<% String url = "mms://" + MediaServerParamBo.getMediaServerAddress();
		if (MediaServerParamBo.getMediaServerPort().length() > 0) {
			url += ":" + MediaServerParamBo.getMediaServerPort();
		}
		if (MediaServerParamBo.getMediaServerPubRoot().length() > 0) {
			url += "/" + MediaServerParamBo.getMediaServerPubRoot();
		}
		url += filePath;
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
	<%} else {%>
		<a id="viewerPlaceHolder" style="width:515px;height:325px;display:block"></a>	
	<%} %>		
				</div>
			</td>
		</tr>
		
	</table>
	</td>
	</tr>
		<tr>
			
			<td align="center" style="padding-top: 5px;">
				<!--  <input type="button" class="mybutton" value='<bean:message key='reportcheck.return'/>' onclick="returnback1();"/>-->
			</td>
		</tr>
	</table>
	<%if (filePath.toLowerCase().endsWith(".doc") || filePath.toLowerCase().endsWith(".docx") 
								|| filePath.toLowerCase().endsWith(".xls") || filePath.toLowerCase().endsWith(".xlsx") 
								|| filePath.toLowerCase().endsWith(".pdf") || filePath.toLowerCase().endsWith(".ppt") 
								|| filePath.toLowerCase().endsWith(".pptx")) {%>
			<script type="text/javascript" src="/general/flexpaper/js/flexpaper_flash.js"></script>
		<script>
			
				var fp = new FlexPaperViewer(	
						 '/general/flexpaper/FlexPaperViewer',
						 'viewerPlaceHolder', { config : {
						 SwfFile : escape('<%=ppath.substring(0,ppath.lastIndexOf("/"))+"/"+r5100 + ".swf" %>'),
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

	<%} else { %>

   <%if (MediaServerParamBo.getMediaServerAddress() !=null && MediaServerParamBo.getMediaServerAddress().length() > 0
 			&& MediaServerParamBo.getMediaServerType() != null && MediaServerParamBo.getMediaServerType().length() > 0) { %>
 			<script>
 			<%if ("red5".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) { %>
 				showStreamPlayer("divid","rtmp://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() %>/elearning","<%=filePath%>","<%=MediaServerParamBo.getMediaServerType() %>",520,330);
 			<%} else if ("microsoft".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {%>
 				//showStreamPlayer("divid","mms://<%=MediaServerParamBo.getMediaServerAddress() %>:<%=MediaServerParamBo.getMediaServerPort() +"/" + (SystemConfig.getPropertyValue("projectrootname").length() > 0 ? SystemConfig.getPropertyValue("projectrootname") : "elearning") %>","<%=filePath%>","<%=MediaServerParamBo.getMediaServerType() %>",520,330);
 			<%} else if ("HTTP".equalsIgnoreCase(MediaServerParamBo.getMediaServerType())) {
 			    String mediaServerAddress = (String)MediaServerParamBo.getMediaServerAddress();
 			 	String mediaServerPort = (String)MediaServerParamBo.getMediaServerPort();
 			 	filePath = MyLessonBo.filePathToMp4(filePath);
 			%>
 				var htmlStr ='<video width="100%" height="100%" controls autoplay>';
 				htmlStr += '<source src="http://<%=mediaServerAddress %>:<%=mediaServerPort %>/<%=filePath %>" type="video/mp4"></source>';
 				htmlStr += '</video>';
 				var div = document.getElementById("divid");
 				div.innerHTML = htmlStr;
 			<%}%>
		</script>
	<%}else{ %>
		<script>
		PlayFlv("divid","<%=ppath%>");
		</script>
	<%}%>
	
	<%} %>
	
</html:form>
</body>
</html>

