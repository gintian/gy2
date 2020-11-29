<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.hjsj.hrms.utils.PubFunc,com.hjsj.hrms.actionform.stat.StatForm"%>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>   
  
  	<hrms:themes />
	<script type="text/javascript">
		function send(){
			var form = document.getElementById("form_common");
			if(form!=null){
				form.submit();
				jinduo();
			}
		}
		
		function jinduo(){
			var x=document.body.clientWidth/2-300;
		    var y=document.body.clientHeight/2-125;
			var waitInfo;
			waitInfo=eval("wait");
			waitInfo.style.top=y;
			waitInfo.style.left=x;
			waitInfo.style.display="block";
		}
	</script>
  </head>
  
  <body onload="send();">
  	<center>
			<div id="wait"
				style='position: absolute; top: 285; left: 120; display: none; width: 500px; heigth: 250px'>
				<table border="1" width="50%" cellspacing="0" cellpadding="4"
					class="table_style" height="100" align="center">
					<tr>
						<td class="td_style" height=24>
							<bean:message key="hmuster.label.wait" />
						</td>
					</tr>
					<tr>
						<td style="font-size: 12px; line-height: 200%" align=center>
							<marquee class="marquee_style" direction="right" width="400"
								scrollamount="5" scrolldelay="10">
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
				<iframe src="javascript:false"
					style="position: absolute; visibility: inherit; top: 0px; left: 0px; width: 285px; height: 120px; z-index: -1; filter ='progid: DXImageTransform . Microsoft . Alpha(style = 0, opacity = 0) ';">
				</iframe>
			</div>
			<div align="left" style="margin-top:3%;margin-left:150px"><font size="4">请点击左侧高级花名册分类下的名册！</font></div>
		</center>
  </body>
</html>
