<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
 "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient,com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm"%>
<%@ page import="javax.servlet.http.Cookie"%>
<%@ page import="com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
	session.setAttribute("islogon",new Boolean("true"));
    String userName = null;
    String css_url="/css/css1.css";
    int flag=0;
    EncryptLockClient lockclient=(EncryptLockClient)session.getServletContext().getAttribute("lock");
    if(lockclient!=null)
    {
    	if(lockclient.isHaveBM(30))
    		flag=1;
    }
    else
    {
    	flag=-1;
    }
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	EmployPortalForm employPortalForm=(EmployPortalForm)session.getAttribute("employPortalForm");
	String netHref=employPortalForm.getNetHref();
	String aurl = (String)request.getServerName();
    String port=request.getServerPort()+"";
    String prl=request.getScheme();
    String url_p=prl+"://"+aurl;
    String lftype=employPortalForm.getLfType();
	String hbType=employPortalForm.getHbType();
	ArrayList boardlist=employPortalForm.getPageBoardList();
	int pageNum = employPortalForm.getPageNum();
	int pageCount = employPortalForm.getPageCount();
		
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" ></meta>
<title>诚聘英才</title>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>
<script language="javascript">

function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA'&&event.srcElement.type!='file') /*0xD*/
   {
   	window.event.keyCode=9;
   }
}
function pf_return(form,element) 
{
	document.forms[form].elements[element].focus();
	return false;
}
function redirectTO(cookieParameter1,cookieParameter2,formName,FactionURL,SactionURL)
{
       var bflag=false;
       var username="";
       var password="";
       var strCookie=document.cookie;
       if(strCookie!=null&&strCookie.length>0)
       {
         var arrCookie=strCookie.split("; ");
         for(var i=0;i<arrCookie.length;i++)
         { // 遍历cookie数组，处理每个cookie对
            var arr=arrCookie[i].split("=");
            if(arr[0]=='hjsj'+cookieParameter1)
            {
                username=unescape(arr[1]);
                bflag=true;
            }
             if(arr[0]=='hjsj'+cookieParameter2)
            {
                password=unescape(arr[1]);
                bflag=true;
            }
         }
       }
       if(document.forms[0])
       {
       if(bflag)
       {
         if(document.forms[0].name==formName)
		 {
            for(var i=0;i<document.forms[0].elements.length;i++)
	        {
			  if(document.forms[0].elements[i].name==cookieParameter1)
			  {
				 document.forms[0].elements[i].value=username;
			  }
			  if(document.forms[0].elements[i].name==cookieParameter2)
			  {
				 document.forms[0].elements[i].value=password;
			  }
			}
			//防止出现用户名不存在或者密码错误，这样会出现查不到人员姓名等错误，先进行检验
			var hashvo=new ParameterSet();
		    hashvo.setValue("loginName",username);
		    hashvo.setValue("password",password);
		    hashvo.setValue("sAction",getEncodeStr(SactionURL));
		    hashvo.setValue("fAction",getEncodeStr(FactionURL));
	     	var In_paramters="operate=ajax";  
	      	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnIn,functionId:'3000000159'},hashvo);	
		 }
		}
		else
		{
		   //document.forms[0].action=FactionURL;
		   //document.forms[0].submit();
		}
	}
}
function returnIn(outparameters)
{
  var sAction=getDecodeStr(outparameters.getValue("sAction"));
  var fAction=getDecodeStr(outparameters.getValue("fAction"));
  var info=outparameters.getValue("info");
  if(info==0)
  {
     // document.forms[0].action=fAction;
	  //document.forms[0].submit();
  }
  else
  {
    document.forms[0].action=sAction;
	document.forms[0].submit();
  }
}
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

<style>
.f12white {
	font-size: 12px;
	line-height: 140%;
	color: #ffffff;
	text-decoration: none;
	font-family: "Microsoft Sans Serif";
	font-weight:bold;
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

.pages {width:638px;margin-left: auto; margin-right: auto;}
.pages ul li { float:left; *float:left; height:30px;  line-height:30px; 
border:none; margin:0 10px 0 0; display:inline-block; *display:inline; *zoom:1;}
.pages ul li a{color:blue;}
.pages ul li a:visited {
	font-size: 12px;
	color: #0F0FFF;
	text-decoration: none;
}
.pages ul li a:hover {
	font-size: 12px;
	color: #E39E19;
	text-decoration: none;
}
.ul_pages {color:blue}
.index .gg .index_list ul li{
	font-size:12px !important;
}
</style>
<script language="javascript">
var width=window.screen.width-300;
var height=window.screen.height-760;

function SETTDCOLOR(obj,tdcolor){
   obj.style.backgroundColor =tdcolor;
}
function tips_pop(){
	var MsgPop=document.getElementById("winpop");//获取窗口这个对象,即ID为winpop的对象
	if(MsgPop){
		var popH=parseInt(MsgPop.style.height);//用parseInt将对象的高度转化为数字,以方便下面比较
		var upd=document.getElementById("down");
		var downu=document.getElementById("up");
		if (popH==0){         //如果窗口的高度是0
			MsgPop.style.display="block";//那么将隐藏的窗口显示出来
			downu.style.display="none";//那么将隐藏的窗口显示出来
			shows=setInterval("changeH('up')",2);//开始以每0.002秒调用函数changeH("up"),即每0.002秒向上移动一次
		}
		else {
			if (popH<=40){
				MsgPop.style.display="block";//那么将隐藏的窗口显示出来
				downu.style.display="none";//那么将隐藏的窗口显示出来       //否则
				upd.style.display="block";
				shows=setInterval("changeH('up')",2);//开始以每0.002秒调用函数changeH("up"),即每0.002秒向上移动一次
			} else{ 
				downu.style.display="block";//那么将隐藏的窗口显示出来       //否则
				upd.style.display="none";
				hides=setInterval("changeH('down')",2);//开始以每0.002秒调用函数changeH("down"),即每0.002秒向下移动一次
			}
		}
	}
	
}
function changeH(str) {
	var MsgPop=document.getElementById("winpop");
	if(MsgPop){
	    var popH=parseInt(MsgPop.style.height);
    	if(str=="up"){     //如果这个参数是UP
	    	if (popH<=225){    //如果转化为数值的高度小于等于100
	     		MsgPop.style.height=(popH+4).toString()+"px";//高度增加4个象素
     		}
    		else{
    			clearInterval(shows);//否则就取消这个函数调用,意思就是如果高度超过100象度了,就不再增长了
    		}
    	}
    	if(str=="down"){
    		if (popH>=35){       //如果这个参数是down
    			MsgPop.style.height=(popH-4).toString()+"px";//那么窗口的高度减少4个象素
    		}
    		else{        //否则
    			clearInterval(hides);    //否则就取消这个函数调用,意思就是如果高度小于4个象度的时候,就不再减了
    			MsgPop.style.display="block";  //因为窗口有边框,所以还是可以看见1~2象素没缩进去,这时候就把DIV隐藏掉
    		}
   		}
   	}
}

	
</script>
<link href="<%=css_url%>" rel="stylesheet" type="text/css" id="skin"></link>
<LINK href="/css/newHireStyle.css" type=text/css rel=stylesheet></LINK>
</head>
<body topmargin="0" bottommargin="0" onKeyDown="return pf_ChangeFocus();"  class="TotalBodyBackColor">
		
	<div class="center">
	<div class="index_bg" id='cms_pnl'  >
	<div class="flash"  <%if(boardlist==null||boardlist.size()==0){%> style='height:575px;'<%} %>>
	<%if(flag!=0){%>
	
		<%if(hbType.equalsIgnoreCase("0")){ %>
		<script language="javascript">
			var h = document.body.clientHeight;
			var ih = h-124-30-35;
			document.write("<img src='/images/zp_homepage_bck.gif' border='0' style='width:1000px;'/>");
		</script>
		
		<%}else{ %>
		<script language="javascript">
			var h = document.body.clientHeight;
			var ih = h-124-30-35;
			document.writeln("<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0' width='1000px' height='289px'>  ");
			document.writeln("<param name='movie' value='/images/zp_homepage_bck.swf'>");  
			document.writeln("<param name='wmode' value='transparent'>"); 
			document.writeln("<embed src='/images/zp_homepage_bck.swf' wmode=\"transparent\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\" width='1000' height='289' type='application/x-shockwave-flash' />");  
			document.writeln("</object>");
		</script>
		
		<%} %>
	
	<%}else { %>
		<bean:message key="label.sys.info"/>
	<%}%>
	</div>
	<div class="index" <%if(boardlist==null||boardlist.size()==0){out.print("style='display:none'");} %>>
	<div class="gg" style="margin-top:0px;display:block;padding-right:0px">
	
		<h2>
		</h2>
		<div class="index_list" id="ficont" style="FONT-SIZE: 14px;height:240px;overflow:auto; ">
			<ul>
			<% 
			if(boardlist!=null){
				for(int i=0;i<boardlist.size();i++){
					LazyDynaBean bean=(LazyDynaBean)boardlist.get(i);
					String id=(String)bean.get("id");
					String down=(String)bean.get("down");
					String hasfile=bean.get("hasfile")==null?null:(String)bean.get("hasfile");
					String title=(String)bean.get("title");
					String content=(String)bean.get("content");
					String ext=(String)bean.get("ext");
					String href=(String)bean.get("href");
					%>
					<li class="els">
					<%
					if(content!=null){
					%>
						<a href="./showBoardPage.jsp?br_showBoardPage=<%=id %>" target="_blank"  style="cursor:pointer"><%=title %></a>
					<%}else{ %>
						<a style="cursor:text"><%=title %></a>
					<%}
					
					if(hasfile!=null&&hasfile.trim().length()!=0&&hasfile.trim().equalsIgnoreCase("true")){ %>
						<a href='<%=href%>' style='margin-left:10px;margin-bottom:4px'><img src="/images/board_attach.gif" alt="附件下载" /></a>
					<%}%>
					</li>
				<%}
			}%>
				 
			</ul>
		                 
		</div>
		<div class="pages">
			<ul class="ul_pages">
				<li id="previous"><a>上一页</a></li>
				<li id="next"><a>下一页</a></li>
				<li>当前第<span class="color" id="pageId"><%=pageNum %></span><span>页</span></li>
				<li>共<span class="color"><%=pageCount %></span><span>页</span></li>
			</ul>
		</div>
	</div>
	</div>
	<%if(boardlist==null||boardlist.size()==0){%>
		<div style="height:25px;width:1000px;">
		&nbsp;&nbsp;&nbsp;
		</div>
	<%} %>
	</div>
	   
	</div>
	</div>

<script language="javascript">
	initDocument();
	<%
	if(session.getAttribute("isLogin")==null)
	{
		if(request.getParameter("b_search")!=null)
		{
			session.setAttribute("isLogin","1");
			%>
			redirectTO('loginName','password','employPortalForm','/hire/employNetPortal/search_zp_position.do?br_disembark=link','/hire/employNetPortal/search_zp_position.do?b_interviewlogin=login');
			
			<%
		}
		else if(request.getParameter("b_query")!=null)
		{
			session.setAttribute("isLogin","1");
			%>
			redirectTO('loginName','password','employPortalForm','/hire/employNetPortal/search_zp_position.do?b_query=link&operate=init','/hire/employNetPortal/search_zp_position.do?b_login=login');
			<%
		}
	}
	%>
	function cl(id){
		document.getElementById(id).style.display='none';
	}
	$("#previous").on("click",function(){
		var map = new HashMap();
		map.put("hireChannel","homepage");
		map.put("pageNum",parseInt($("#pageId").html()));
		map.put("operation","previous");
		Rpc({functionId : 'ZP0000002657',success : changeContent}, map);
	});
	$("#next").on("click",function(){
		var map = new HashMap();
		map.put("hireChannel","homepage");
		map.put("pageNum",parseInt($("#pageId").html()));
		map.put("operation","next");
		Rpc({functionId : 'ZP0000002657',success : changeContent}, map);
	});
	function changeContent(outparamters){
		var param = Ext.decode(outparamters.responseText);
		var boardlist = param.pageBoardList;
		$("#ficont ul:eq(0)").remove();
		$("#pageId").html(param.pageNum);
		var $div = $("#ficont");
		var $ul=$("<ul></ul>"); 
		for(var i = 0;i<boardlist.length;i++){
			var obj = boardlist[i];
			var $li = $("<li class='els'></li>");
			var a1 = $("<a style='cursor:text'>"+obj.title+"</a>");
			//公告内容地址
			if(obj.content)
				a1 = $("<a href='./showBoardPage.jsp?br_showBoardPage="+obj.id+"' style='cursor:pointer' target='_blank'>"+obj.title+"</a>");
			
			$li.append(a1);
			//公告附件地址
			if(obj.hasfile=="true"){
				var a2 = $("<a href='"+obj.href+"' style='margin-left:10px;margin-bottom:4px'><img src='/images/board_attach.gif' alt='附件下载' /></a>");
				$li.append(a2);
			}
			$ul.append($li);
			$div.append($ul);
		}
	}
</script>
</body>
</html>