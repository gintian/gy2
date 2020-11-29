<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
  <link href="./style.css" rel="stylesheet" />
  <script language='javascript'>
	Ext.onReady(function(){
		var map = new HashMap();
		map.put("flag", "showNoticeTitle");
		Rpc({
			functionId : 'ZP0000002113',
			success : function(obj){
				var param = Ext.decode(obj.responseText);
				var noticeTitle = param.info;
				var html = "<div class='hj-xxgs hj-xxgs-div'><ul type='circle'>";
				for(var i = 0;i<noticeTitle.length;i++){
					html += "<li><div class='hj-xxgs-div1' style='margin:auto 0'><img  style='width:30px;margin-top:6px;' src='../../images/hire/notice.gif'></div><div style='margin-left:40px'>";
					html += "<p>"+noticeTitle[i].createtime+"</p>";
					html += "<p><a href='/hire/hireNetPortal/showNoticeContent.html?id="+noticeTitle[i].id+"&flag=showNoticeContent' target='_blank'>"+noticeTitle[i].topic+"</a></p>";
					html += "</div></li>";
				}
				html += "</ul></div>";
				Ext.create('Ext.panel.Panel', {
					autoScroll:true,
					minHeight: document.documentElement.clientHeight-190,
					//bodyCls: 'tcenter',
					html: html,
					renderTo: "content"
				});
			}
		}, map);
		}
	);
  </script>

  </head>
  
  <body>
  <div id='content'></div>
  </body>
</html>
