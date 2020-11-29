<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hjsj.hrms.actionform.hire.employNetPortal.EmployPortalForm" %>
<html:form action="/hire/hireNetPortal/search_zp_position"> 
<style type="text/css"> 

</style>
<%
	EmployPortalForm epf=(EmployPortalForm)session.getAttribute("employPortalForm");
	String sy_message="";
	sy_message=epf.getSy_message();
	String hbType=epf.getHbType();
 %>
<script type="text/javascript">
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
		    	if (popH<=250){    //如果转化为数值的高度小于等于100
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
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link rel="stylesheet" type="text/css" href="/css/hireNetStyle.css"></link>
<table  width="100%" border="0" cellspacing="0" cellpadding="0" style='ALIGN:center'>
   <tr>
     <td width="90%">
     <div class="tcenter" id='tc'>
     <div class="center_bg" id='cms_pnl'>
     <%if(hbType.equalsIgnoreCase("0")){ %>
     <script language="javascript">
      var h = document.body.clientHeight;
      var ih = h-124-30-35;
      document.write("<img src='/images/zp_homepage_bck.gif' border='0' style='width:1000px;height:"+ih+"px;'/>");
      </script>
     
     <%}else{ %>
     <script language="javascript">
      var h = document.body.clientHeight;
      var ih = h-124-30-35;
      document.writeln("<object classid='clsid:D27CDB6E-AE6D-11cf-96B8-444553540000' codebase='http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0' width='1000px' height='"+ih+"px'>  ");
      document.writeln("<param name='movie' value='/images/zp_homepage_bck.swf'>");  
      document.writeln("<param name='wmode' value='transparent'>"); 
      document.writeln("<embed src='/images/zp_homepage_bck.swf' width='1000' height='"+ih+"' type='application/x-shockwave-flash' />");  
       document.writeln("</object>");
      </script>
     
     
     <%} %>
     </td>
   </tr>
</table>
<div id="winpop">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr height="46">
	<td width="151">
	<img src="/images/zp_rep.gif" border="0"/>
	</td>
	<td class="zp_sy_reportBg" width="529">
	<span id="down" class="close" onclick="tips_pop()"><img src="/images/down01.gif" border="0"/></span>&nbsp<span id="up" class="close" onclick="tips_pop()"><img src="/images/up01.gif" border="0"/></span>
	</td>
	
	</tr>
	
	</table>
	<div class="con"><%=sy_message %></div>
</div>
<script type="text/javascript">
<%
		if(sy_message!=null&&sy_message.length()!=0)
		{
	%>
		   //加载
		document.getElementById('winpop').style.height='0px';
		if(window.screen.width>1000){
			var popW=parseInt((window.screen.width-1000)/2);//将对象的宽度转化为数字
			document.getElementById('winpop').style.right=popW+"px";
		}
		setTimeout("tips_pop()",800);     //3秒后调用tips_pop()这个函数
	
	<%}%>
	</script>
</html:form>