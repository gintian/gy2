 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<%@page import="com.hrms.frame.codec.SafeCode"%>
<%@page import="java.util.*, org.apache.commons.beanutils.LazyDynaBean"%>

<html>
<head>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script language="JavaScript">
 var currentpage=1;
  	var maxbutton=5;//页面能显示的按钮个数，实施人员在页面上最多画这些按钮。如果页面数多余该按钮数在最大值的按钮后面 画个...按钮
     var _checkBrowser=true;
	var _disableSystemContextMenu=false;
	var _processEnterAsTab=true;
	var _showDialogOnLoadingData=true;
	var _enableClientDebug=true;
	var _theme_root="/ajax/images";
	var _application_root="";
	var __viewInstanceId="968";
	var ViewProperties=new ParameterSet();
</script>
<%  EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
	if(employPortalForm.getUserName()==null||employPortalForm.getUserName().equals("")){
		employPortalForm.setA0100("");
	}
	String a0100=employPortalForm.getA0100();
	String dbName=employPortalForm.getDbName();
	String hirechannel=employPortalForm.getHireChannel();
	 String netHref=employPortalForm.getNetHref();
    String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=prl+"://"+aurl;
    String lftype=employPortalForm.getLfType();
    String child_id=(String)request.getParameter("chl_id");
    String title=SafeCode.decode((String)request.getParameter("title"));
    String cont="";
    ArrayList boardlist=employPortalForm.getBoardlist();
    String id=SafeCode.decode((String)request.getParameter("dml"));
    String hasfile=SafeCode.decode((String)request.getParameter("hasfile"));
%>
<LINK href="/css/hireNetStyle.css" type=text/css rel=stylesheet>
<title>公告内容</title>
<style  id="iframeCss">
.f12white {
	font-size: 12px;
	line-height: 140%;
	color: #ffffff;
	text-decoration: none;
	font-family: "Microsoft Sans Serif";
	font-weight:bold;
}
a:link {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
a:visited {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
a:hover {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
/*菜单背景颜色*/
.MenuRow {
	border: 0px;
	BORDER-BOTTOM: 0pt solid; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	border-collapse:collapse; 
	height:22;
	background-color:#7DC7FF;
	text-align:center;
}
.MenuRow_1 {
	border: 0px;
	border-bottom:1px solid #fff; 
	BORDER-LEFT: 0pt solid; 
	BORDER-RIGHT:0pt solid; 
	BORDER-TOP: 0pt solid;
	font-size: 20px;
	border-collapse:collapse; 
	height:25;
	background-color:#7DC7FF;
	text-align:center;
	/*background-color:#FFFFFF*/
}
/*第一层菜单背景颜色*/
.firstMenuRow{
/* color:#FFFFFF;*/
 background-image:url(../images/search_middle.jpg);
 background-repeat:repeat-x;
 size:13pt;
 margin-top:300px;
  cursor:hand;
 /*background-color:#006E6D*/
}
/*菜单字体*/
.MenuRowFont{
   color:#666;
   font-size:12px;
}
/*平铺菜单左侧圆角型图片*/
.MenuLeftHead
{
    background-image: url(../../images/search_left.jpg);
	background-repeat:no-repeat;
	background-position:center
}
/*平铺菜单右侧圆角型图片*/
.MenuRightHead
{
    background-image: url(../../images/search_right.jpg);
	background-repeat:no-repeat;
	/*background-color: #A2D9DC;
	background-color: #FFFFFF;*/
	background-position:center
}
</style>
</head>
<body>
<html:form action="/hire/hireNetPortal/search_zp_position">
	<TABLE cellSpacing=0 cellPadding=0 width='1000px' align=center border=0 style="width:1000px; left:expression((document.body.clientWidth-1000)/2+'px'">
  		<TBODY>
  		<tr>
  	
  		<%if(lftype.equals("1")){ %>
		      <td width="90%" >
		      <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="1000">  
		       <param name="movie" value="/images/hire_header.swf">  
		       <param name="wmode" value="transparent">  
		       <embed src="/images/hire_header.swf" width="1000"  type="application/x-shockwave-flash" />  
		      </object>
		     <%}else {
		    
		          if(netHref!=null&&netHref.length()>0){ %>
		            <td width="90%" >
		     <a href="<%=netHref%>" target="_blank"><img src="/images/hire_header.gif" border="0"/></a>
		     <%}else{ %>
		       <td width="90%"  class='header'>
		     <img src='/images/hire_header.gif' border='0'/>
		    <%} }%>
  		</td>
  		</tr>
  		
  		<tr>
  		<td>
				
			            		
			     				 <div class="body">   	
			     				 <div class="nav" style="margin-top:0px"></div>
							        <div class="tcenter" id='tc'>
							        	<div class="center_bg" id='cms_pnl'>
							        	<div class="cent"  style="float:center;height:400px;margin-left:150px;margin-bottom:100px">
							        	 <div class="zw zw1">
                    					<h3>公告内容</h3>
                    					</div>
							            	 <div class="jj">
                    							<h2><span class="els_r"><%if(hasfile!=null&&hasfile.equalsIgnoreCase("true")){ %><button onclick="downfujian('<%=id %>');">下载附件</button><%} %><button onclick="window.close()">关闭</button></span><span><%=title %></span></h2>
		                    					
		                    					<div style='width:677px;height:100px;border:1px;align:left;font-size:12;border:solid 1px #9eaac2;margin-top:10px;padding-top:10px;padding-left:10px'>
		                    					<%for(int i=0;i<boardlist.size();i++) {
		                    						LazyDynaBean bean=(LazyDynaBean)boardlist.get(i);
		                    						
		   										 	String ids=(String)bean.get("id");
		   										 	if(id!=null&&ids.equalsIgnoreCase(id)){
		   										 		cont=(String)bean.get("content")==null?"":(String)bean.get("content");
		   										 	}
		   										 	}
		                    					%>
		                    	 				<%=cont%>
		                    	 			
		                    				 </div>
		                    			</div>
		                    			
		                    	</div>
		                    	  <div class='footer' style='height0px;' > &nbsp;&nbsp;</div>   
			               </div>
			           </div>
			             
			        </div>
			       
			       
			        </td>
			        </tr>
			        <tr>
			        <td width="100%" align="center" class='tfooter'>
			        <font class="FontStyle">Copyright &copy; 2004-2010 China Academy of Building Research 京ICP备05039189号  中国建筑科学研究院 版权所有&nbsp;&nbsp;&nbsp;&nbsp;<hrms:counter/>(世纪制作)</font>
			        </td>
			        </tr>
			        </TBODY>
			        </TABLE>
			       
</html:form>           
			</body>
</html>