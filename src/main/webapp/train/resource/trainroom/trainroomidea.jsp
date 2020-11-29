<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.businessobject.train.resource.TrainRoomBo"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
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
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
 <%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
    String bosflag="";
    String themes="default";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
%>
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
<script language="javascript">
	function closewin()
	{
		var declare = document.getElementById("declare").value;
		if ((null==declare)||(""==trim(declare)))
		{
			alert("请输入审批意见！");
			return;
		}
		var len = <%=TrainRoomBo.getR6113Length()%>;
		var length = lenStat(declare);  
		if(length>len){
			alert(TRAIN_ROOM_MORE_LENGTH1+len+TRAIN_ROOM_MORE_LENGTH2+len/2+TRAIN_ROOM_MORE_LENGTH3);
		}else{
			returnValue = getEncodeStr(declare);
			window.close();
		}
	}

	function lenStat(str) {
		var len = 0;
        for (var i = 0; i < str.length; i++) {
            var c = str.charCodeAt(i);
            //单字节加1 
            if ((c >= 0x0001 && c <= 0x007e) || (0xff60 <= c && c <= 0xff9f)) {
                len++;
            }
            else {
                len += 2;
            }
        }
        return len;

	}
</script>
<table width="96%" border="0" cellspacing="0" align="center" cellpadding="0" style="margin-top:5px; margin-right:5px; padding: 3px;">
	<tr>
		<td align="center" class="TableRow">
			&nbsp;审批意见&nbsp;
		</td>
	</tr>
	<tr>
		<td class="RecordRow" style="border-top: none;"><textarea rows="4" cols="20" style="height: 200px;width: 100%;" name="declare" id="declare"></textarea></td>
	</tr>
	<tr>
		<td align="center" style="padding-top: 5px;">
			<input type="button" value="确定" class="mybutton" onclick="closewin();"/>&nbsp;
			<input type="button" value="关闭" class="mybutton" onclick="window.close();"/>&nbsp;
		</td>
	</tr>
</table>