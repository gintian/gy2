 <%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm,com.hjsj.hrms.businessobject.sys.SysParamBo,com.hjsj.hrms.businessobject.sys.SysParamConstant"%>

<html>
<head>
<script language="javascript" src="/ajax/constant.js"></script>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/ajax/common.js"></script>
<script language="javascript" src="/ajax/command.js"></script>
<script language="javascript" src="/ajax/format.js"></script>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<script language="JavaScript" src="/hire/hireNetPortal/employNetPortal.js"></script>
<script type="text/javascript" src="/jquery/jquery-3.3.1.min.js"></script>
<script language="JavaScript">
var JQuery=$.noConflict();//将jquery起个别名防止和系统中自定义的$()方法冲突
 var currentpage=1;
  	var maxbutton=4;//页面能显示的按钮个数，实施人员在页面上最多画这些按钮。如果页面数多余该按钮数在最大值的按钮后面 画个...按钮
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
    String username = employPortalForm.getUserName()==null?"":employPortalForm.getUserName();
    String port=request.getServerPort()+"";
    String prl=request.getProtocol();
    int idx=prl.indexOf("/");
    prl=prl.substring(0,idx);
    String url_p=prl+"://"+aurl;
    String lftype=employPortalForm.getLfType();
    String child_id=(String)request.getParameter("chl_id");
    
    Calendar c = Calendar.getInstance();
    int hour = c.get(Calendar.HOUR_OF_DAY); 
    
    String passwordTransEncrypt = SysParamBo.getSysParamValue(SysParamConstant.SYS_SYS_PARAM, "password_trans_encrypt");
%>
<LINK href="/css/newHireStyle.css" type=text/css rel=stylesheet>
<title>联系我们</title>
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
<input type="hidden" id="hour" value="<%=hour %>"/>
<input type="hidden" id="pTransEncrypt" value="<%=passwordTransEncrypt %>"/>
<div id="ly" style="position:absolute;top:0px;FILTER:alpha(opacity=0);background-color:#FFF;z-index:2;left:0px;display:none;"></div> 
<div id='wait1' style='position:absolute;top:285;left:80;display:none;'>
		<table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>
					激活邮件发送中,请稍候......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
<div id="biaodan">


</div>
<div id='chajian' ></div>
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
   		<OBJECT  id='SetIE'
				  classid="clsid:75533D3B-C507-4337-BD9A-FC7212DF7927"
				  codebase="/cs_deploy/hrpsetiesecurity.cab#version=1,0,0,7"
				  width=0
				  height=0
				  align=center
				  hspace=0
				  vspace=0>
		</OBJECT>
  		</td>
  		</tr>
  		<tr>
  		<td width="100%" align="center">
           <hrms:cms_channel chl_no="1" type="${employPortalForm.menuType}" chl_id="<%=child_id %>" showtye='2'></hrms:cms_channel>
    </td>
  		</tr>
  		<tr>
  		<td>
				
			            		<%
			          			if(a0100==null||(a0100!=null&&a0100.trim().length()==0)){     	
			     				 %>
			     				 <div class="body" style="widht:1002px;">   	
							        <div class="tcenter" id='tc'>
							        	<div class="center_bg" id='cms_pnl'>
							            	<div class="left">
			                	                	<div class="login">
                    	<h2>&nbsp;&nbsp;&nbsp;&nbsp;用户登录</h2>
                        <div class="dl">
								<table width="197" border="0" cellspacing="0" cellpadding="0">
								  <tr>
								    <td>&nbsp;</td>
								  </tr>
								  <tr>
								    <td>&nbsp;&nbsp;<div class="input_bg"><span>邮&nbsp;&nbsp; 箱</span><input class="s_input" id="loginName"  type="text" onkeydown="KeyDown()" name="loginName" value='<bean:write name="employPortalForm" property="loginName" />' /></div></td>
								  </tr>
								  <tr>
								    <td><div class="input_bg"><span>密&nbsp;&nbsp; 码</span><input class="s_input" id="password" type="password" onkeydown="KeyDown()" value='<bean:write name="employPortalForm" property="password" />' 				                 
						             name="password" /></div></td>
								  </tr>
								  <tr>
								    <td>
								    	<div class="input_bg"><span>验证码</span><input class="s_input" id="validatecode" type="text" onkeydown="KeyDown()" value="" name="validatecode" /></div>
								    </td>
								  </tr>
								  
								  <tr>
								    <td align="right" style="padding:2px 0 2px 0;border-bottom:dotted 1px #c6c6c6;">
								    <img align="absMiddle" src="/servlet/vaildataCode?channel=0&codelen=4" id="vaildataCode">
								    <img align="absMiddle" src="/images/refresh.png" height="15" width="15" title="换一张" onclick="validataCodeReload()">
								    <img align="absMiddle" style="cursor:hand;" src="/images/hire/dl.gif" title="登录"  onclick='hireloginvalidate(0);'/>&nbsp;</td>
								  </tr>
								  <tr>
								    <td>
								    <span>
								   		 <logic:equal value="0" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:T_BUTTON();'>注册</a>| 
						                  </logic:equal>
						                    <logic:equal value="1" name="employPortalForm" property="isDefinitinn">
						                   <a href='javascript:TR_BUTTON();'>注册</a>|
						                  </logic:equal>
						               		 <a href='javascript:getPasswordZPnew("<%=dbName%>","username","userpassword");'>找回密码</a>| 
						                  <logic:equal value="1" name="employPortalForm" property="acountBeActived">
						        
						                   <a href="javascript:hireloginvalidate(1);">激活账号</a>| 
						                  </logic:equal>

						                </span>
						             </td>
								  </tr>
								</table>
                        </div>
                    </div>
			                    <%}  else { %>
			                    <div class="body" style="widht:1002px;">
							    	
							          <div class="tcenter" id='tc'>
							        	<div class="center_bg" id='cms_pnl'>
							            	<div class="left">				            		
							                	<div class="login">
							                    	 <div class="dl_1">
							                    	   <div class="we"><b><bean:message key="hire.welcome.you"/>,
						                           <%if(userName.length()>6){ %>
						                           </b><b>
						                           <% } %>
						                           ${employPortalForm.userName}</b><bean:message key="hire.welcome.you.hint"/></div>
									            	      <ul class="dl_list">
									            	        <li><a href='javascript:resumeBrowse("<%=dbName%>","<%=a0100%>")'><bean:message key="hire.browse.resume"/></a></li>
									            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_showResumeList=show&setID=0&opt=1"><bean:message key="hire.my.resume"/></a></li>
									            	         <li><a href="javascript:void(0);" onclick='hasresume();'><bean:message key="hire.browsed.position"/></a></li>
									            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?b_applyedPosition=query"><bean:message key="hire.apply.position"/>
									            	        </a></li>
									            	        <li><a href="/hire/hireNetPortal/search_zp_position.do?br_editPassword=edit"><bean:message key="label.banner.changepwd"/></a></li>
									            	         <logic:equal value="1" name="employPortalForm" property="isDefinitionActive">
										            	        <logic:equal value="1" name="employPortalForm" property="activeValue">
										            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>关闭简历</a></li>
										            	        </logic:equal>
										            	        <logic:equal value="2" name="employPortalForm" property="activeValue">
										            	        	<li><a href='javascript:activeResume("<%=dbName%>","<%=a0100%>","${employPortalForm.activeValue}");'>激活简历</a></li>
										            	        </logic:equal>
									            	        </logic:equal>
									            	        <li><a href="javascript:exit()">退出登录</a></li>
									          	        </ul>
									          	     </div>
									          	    </div>
				                    <%} %>
				     <div class="promt">
                     ${employPortalForm.promptContent}
                     </div>
			        </div>
			                  <div class="right"  style="margin-bottom:-70px;">
			                  	<h2 class="cont"></h2>
                    <div class="content" id="content1" >
                    	<ul>
                        	<li class="ohter0" id="oth1"><h3>中国建筑科学研究院总部 </h3><span>China Academy of Building Research</span><p>地址：北京市北三环东路30号建研院人事处<br />邮编：100013<br />联系人：王美玲<br />E-mail：wml@cabr.com.cn</p></li>
                        	<li class="ohter1"><h3>建研科技股份有限公司 </h3><span>CABR Technology Co., Ltd.</span><p>地址：北京市北三环东路30号建研科技人力资源部<br />邮编：100013<br />联系人：马娟<br />E-mail：zhaopin2012@cabrtech.com</p></li>
                        	<li class="ohter2"><h3>中国建筑技术集团有限公司 </h3><span>China Building Technique Group Co., Ltd.</span><p>地址：北京市北三环东路30号中技集团人力资源部<br />邮编：100013<br />联系人：陈甜甜<br />E-mail：hr@cbtgc.com</p></li>
                        	<li class="ohter3"><h3>建研凯勃建设工程咨询有限公司</h3><span>CABR Construction Consulting Co., Ltd.</span><p>地址：北京市北三环东路30号凯勃公司人事部<br />邮编：100013<br />联系人：陈楠<br />E-mail：jykbgshr@yeah.net</p></li>
                        	<li class="ohter4"><h3>中国建筑科学研究院建筑设计院</h3><span>Institute of Architectural Design, CABR</span><p>地址：北京市北三环东路30号建筑设计院办公室<br />邮编：100013<br />联系人：徐亚军<br />E-mail：hr@cabr-design.com</p></li>
                        </ul>
                    </div>
                    <div class="content" id="content2" style="display:none;">
                    	<ul>
                        	<li class="ohter0" id="oth2"><h3>建研地基基础工程有限责任公司</h3><span>CABR Foundation Engineering Co., Ltd.</span><p>地址：北京市北三环东路30号地基公司人力资源部<br />邮编：100013<br />联系人：陶玲<br />E-mail：djryzp@163.com</p></li>
                        	<li class="ohter1"><h3>中国建筑科学研究院建筑环境与节能研究院</h3><span>Institute of Building Environment and Energy Efficiency, CABR</span><p>地址：北京市北三环东路30号建研院环能院（北区）人事部<br />邮编：100013<br />联系人：刘欣<br />E-mail：kts225227@yahoo.com.cn</p></li>
                        	<li class="ohter2"><h3>中国建筑科学研究院建筑工程检测中心</h3><span>Construction Engineering Testing Center, CABR</span><p>地址：北京市北三环东路30号检测中心办公室<br />邮编：100013<br />联系人：赵淑华<br />E-mail：cabrbetc@163.com</p></li>
                        	<li class="ohter3"><h3>北京建筑机械化研究院（北京部）</h3><span>Beijing Institute of Construction Mechanization（Beijing）</span><p>地址：北京市东城区安定门方家胡同21号办公室<br />邮编：100007<br />联系人：赵少莉<br />E-mail：jjs@cabr.com.cn</p></li>
                             <li class="ohter4"><h3>北京建筑机械化研究院（廊坊部）</h3><span>Beijing Institute of Construction Mechanization（Langfang）</span><p>地址：河北省廊坊市金光道61号办公室<br />邮编：065000<br />联系人：高铭<br />E-mail：gm_final@msn.com</p></li>
                             </ul>
                    </div>
                    <div class="content" id="content3" style="display:none;">
                    	<ul>
                        	<li class="ohter0" id="oth3"><h3>中国建筑科学研究院建筑防火研究所</h3><span>Institute of Building Fire Research, </span><p>地址：北京市北三环东路30号建研院防火所104室<br />邮编：100013<br />联系人：赵晓峰<br />E-mail：fhscabr@126.com</p></li>
                        	<li class="ohter1"><h3>建研城市规划设计研究院有限公司</h3><span>CABRCABR Urban Planning and Design Institute Co., Ltd</span><p>地址：北京市北三环东路30号规划院办公室<br />邮编：100013<br />联系人：张琦<br />E-mail： cabr_jygh@126.com</p></li>
                        	<li class="ohter2"><h3>中国建筑科学研究院深圳分院</h3><span>Shenzhen Branch Institute, </span><p>地址：深圳市南山区高新南一道富诚科技大厦<br />邮编：518057<br />联系人：丘旭霞<br />E-mail：yuanban@cabr-sz.com</p></li>
                        	<li class="ohter3"><h3>中国建筑科学研究院上海分院</h3><span>CABRShanghai Branch Institute, CABR</span><p>地址：上海市卢湾区打浦路88号海丽大厦<br />邮编：200023 <br />联系人：王丽琼<br />E-mail：wlq_cabr@hotmail.com</p></li>
                        	<li class="ohter4"><h3>中国建筑科学研究院天津分院</h3><span>Tianjin Branch Institute, CABR</span><p>地址：天津市华苑产业园区华天道8号海泰信息广场D座819、821室<br />邮编：300384<br />联系人：柯莹<br />E-mail：fairyky@126.com</p></li>
                        </ul>
                    </div>
                     <div class="content" id="content4" style="display:none;">
                    	<ul>
                        	<li class="ohter0" id="oth4"><h3>中国建筑科学研究院西南分院</h3><span>Xinan Branch Institute, CABR</span><p>地址：成都市高新区天韵路186号高新国际广场E座3楼<br />邮编：610041<br />联系人：王智海<br />E-mail：sw.cabr@yahoo.com.cn </p></li>
                        	<li class="ohter1" ><h3>中国建筑科学研究院海南分院</h3><span>Hainan Branch Institute, CABR</span><p>地址：海南省海口市美兰区海府路69号艺苑大厦404室<br />邮编：570203<br />联系人：柳欣<br />E-mail：gb@cabrhn.com</p></li>
                    		<li class="ohter2"><h3>&nbsp;&nbsp;&nbsp;&nbsp;</h3><span>&nbsp;&nbsp;&nbsp;</span><p>&nbsp;&nbsp;<br />&nbsp;&nbsp;<br />&nbsp;&nbsp;<br /></p></li>
                    		<li class="ohter3"><h3>&nbsp;&nbsp;&nbsp;&nbsp;</h3><span>&nbsp;&nbsp;&nbsp;</span><p>&nbsp;&nbsp;&nbsp;<br />&nbsp;&nbsp;<br />&nbsp;&nbsp;<br /></p></li>
                        	<li class="ohter4"><h3>&nbsp;&nbsp;&nbsp;&nbsp;</h3><span>&nbsp;&nbsp;&nbsp;</span><p>&nbsp;&nbsp;<br />&nbsp;&nbsp;<br />&nbsp;&nbsp;<br /></p></li>
                        	</ul>
                    </div>
                     
					<div class="page" style='height:50px;'>
							<span class="PIndex">
								<a class="Pindex" href="javascript:void(0);" onclick="showdiv(1);">首页</a>
								<a class="Pprev" href="javascript:void(0);" onclick="showdiv('up');">上一页</a>
							</span>
						<span class="Ppagelist" id='buttonlist'>
							<!-- 页面按钮，如果想增加按钮个数再次重复画<span></span>节点 并修改其中的属性 id=Ppage+页数 onclick=showdiv(页数)" 如果总页数 大于设置的 maubutton在最后一个按钮后多加一个<span class="Ppage" >
								<a href="javascript:void(0);" id='Ppage6' onclick="showdiv('sl');">...</a></span>节点 *实施人员修改*-->
							<span class="Ppage" >
								<a href="javascript:void(0);" onclick='showdiv(1);' id='Ppage1' class="Pmodern" target="_self">1</a>
							</span>
							<span class="Ppage" >
								<a href="javascript:void(0);" id='Ppage2' onclick="showdiv(2);">2</a>
							</span>
							<span class="Ppage">
								<a href="javascript:void(0);"  id='Ppage3' onclick="showdiv(3);">3</a>
							</span>
							<span class="Ppage" >
								<a href="javascript:void(0);" id='Ppage4' onclick="showdiv(4);">4</a>
							</span>
							
							
							
							<!-- 此处之前画按钮 按钮个数能比maxbutton少 不能多 *实施人员修改* -->
						</span>
						<span class="PEnd">
							<a class="Pnext" href="javascript:void(0);" onclick="showdiv('next');">下一页</a>
							<a class="Pend" href="javascript:void(0);" onclick="showdiv(4);">尾页</a><!-- 此处 showdiv中 填写 最后一页的页数值 *实施人员修改*-->
						</span>
						<span class="Pform">
							<input type="text" value="1" name="PageV" class="Pbd" >
							<input type="button" class="Pgo" value="跳转" onclick="showdiv('tz');">
						</span>
						</div>
			               </div>
			                <div class='footer' style='height:100px;' > &nbsp;&nbsp;</div>   
			               </div>
			               <input type='hidden' value='4' id='lastpage'><!-- 一共有多少页内容就是 上文中的div个数，必须填写有多少天多少  *实施人员修改* -->
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
		<html:hidden name="employPortalForm" property="isDefinitionActive"/>	       
</html:form>           
			</body>
</html>