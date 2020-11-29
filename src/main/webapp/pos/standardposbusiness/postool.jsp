<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	boolean version = false;
	if(userView.getVersion()>=50){//版本号大于等于50才显示这些功能
		version = true;
	}
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
	 String path = request.getSession().getServletContext().getRealPath("/js");
   if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
   {
  	  path=session.getServletContext().getResource("/js").getPath();//.substring(0);
      if(path.indexOf(':')!=-1)
  	  {
		 path=path.substring(1);   
   	  }
  	  else
   	  {
		 path=path.substring(0);      
   	  }
      int nlen=path.length();
  	  StringBuffer buf=new StringBuffer();
   	  buf.append(path);
  	  buf.setLength(nlen-1);
   	  path=buf.toString();
   }
   path=path.replace('\\','`');
%>
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
<script language="javascript" src="/js/constant.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
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
<script type="text/javascript">
<!--
	function backDate(){
		var dw=300,dh=350,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		var backdate=showModalDialog('/train/resource/course/pos.do?br_backdate=link','_blank','dialogLeft:'+dl+'px;dialogTop:'+dt+'px;dialogHeight:300px;dialogWidth:350px;center:yes;help:no;resizable:no;status:no;');
		if(backdate) {
			<logic:equal value="1" name="standardPosForm" property="validateflag">
				standardPosForm.action="/train/resource/course/pos.do?b_tree=link&backdate="+backdate;
			</logic:equal>
			<logic:notEqual value="1" name="standardPosForm" property="validateflag">
				standardPosForm.action="/train/resource/course/pos.do?b_tree=link&checked="+backdate;
			</logic:notEqual>
			standardPosForm.target="il_body"
			standardPosForm.submit();
		}else
			return false;
	}
//-->
</script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
-->
</style>
<body  border="0" cellspacing="0"  cellpadding="0">
<html:form action="/pos/standardposbusiness/searchposbusinesstree"> 
    <table width="1000" border="0" cellspacing="0"  align="center" cellpadding="0" >
    	<tr align="left" class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left" >
		<hrms:priv func_id="">
			<logic:equal value="1" name="standardPosForm" property="validateflag">
				<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" alt="历史时点查询" border="0" align="middle"></a>               
			</logic:equal>
			<logic:notEqual value="1" name="standardPosForm" property="validateflag">
				<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" alt="无效时点查询" border="0" align="middle"></a>               
			</logic:notEqual>
		</hrms:priv>
		  
		</td>
		</tr>  
    </table>
</html:form>
<script language="javascript">
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
   
   var dropDownList=createDropDown("dropDownList");
   var __t=dropDownList;
   __t.type="list";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);   
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
	//__t.path="/system/gcodeselect.jsp";    
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
</script>
</body>