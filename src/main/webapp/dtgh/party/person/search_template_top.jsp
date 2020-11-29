<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@ page import="com.hrms.struts.constant.SystemConfig"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null)
	{
	  userName = userView.getUserFullName();
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
	String date = DateStyle.getSystemDate().getDateString();
	int flag=1;
	String webserver=SystemConfig.getPropertyValue("webserver");
	if(webserver.equalsIgnoreCase("websphere"))
		flag=2;
		

	
%>
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
<script language="JavaScript">
function pf_ChangeFocus(e) 
			{
				  e=e?e:(window.event?window.event:null);//xuj update 2011-5-11 兼容firefox、chrome
			      var key = window.event?e.keyCode:e.which;
			      var t=e.target?e.target:e.srcElement;
			      if ( key==0xD && t.tagName!='TEXTAREA') /*0xD*/
			      {    
			   		   if(window.event)
			   		   	e.keyCode=9;
			   		   else
			   		   	e.which=9;
			      }
			   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
			   if ( key==116)
			   {
			   		if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   }   
			   if ((e.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
			   {    
			        if(window.event){
			   		   	e.keyCode=0;
			   		   	e.returnValue=false;
			   		}else{
			   		   	e.which=0;
			   		   	e.preventDefault();
			   		}
			   } 
			}
  document.oncontextmenu = function() {return false;}

//屏蔽右键,实在没有办法的采用此办法,解决重复提交问题
/*oncontextmenu = "return false"*/

function gointo(param){
	var currnode=parent.frames['mil_menu'].Global.selectedItem;
	if(currnode.uid=='root'){
		alert("请您先选择非根节点的组织单元节点！");
		return;
	}
	personForm.action="/dtgh/party/person/searchbusinesslist.do?b_query=link&politics="+param;
	personForm.target="il_body";
	personForm.submit();
}
</script>
<link href="<%=css_url%>" rel="stylesheet" type="text/css">

<body>
<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0">
<tr valign="middle">
        <td align="left" valign="middle" nowrap>
<html:form action="/dtgh/party/person/searchbusinesslist">       	
<hrms:menubar menu="menu1" id="menubar1">
  <logic:equal value="Y" name="personForm" property="param">
  <logic:empty name="personForm" property="party">
  <hrms:menuitem name="party0" label="党员" enabled="false">
  </hrms:menuitem>
  </logic:empty>
  <logic:notEmpty name="personForm" property="party">
  <hrms:menuitem name="party0" label="党员" url="gointo('party');">
  </hrms:menuitem>
  </logic:notEmpty>
  <logic:empty name="personForm" property="preparty">
  <hrms:menuitem name="party1" label="预备党员" function_id="" url="" enabled="false">
  </hrms:menuitem>  
  </logic:empty>
  <logic:notEmpty name="personForm" property="preparty">
  <hrms:menuitem name="party1" label="预备党员" function_id="" url="gointo('preparty');">
  </hrms:menuitem>  
  </logic:notEmpty>
  
	<logic:empty name="personForm" property="important">
  <hrms:menuitem name="party3" label="重点发展对象" function_id="" url="" enabled="false">
  </hrms:menuitem>  
  </logic:empty>
  <logic:notEmpty name="personForm" property="important">
  <hrms:menuitem name="party3" label="重点发展对象" function_id="" url="gointo('important');">
  </hrms:menuitem>  
  </logic:notEmpty>
  <logic:empty name="personForm" property="active">
  <hrms:menuitem name="party2" label="入党积极分子" function_id="" url="" enabled="false">
  </hrms:menuitem> 
	</logic:empty>
	<logic:notEmpty name="personForm" property="active">
  <hrms:menuitem name="party2" label="入党积极分子" function_id="" url="gointo('active');">
  </hrms:menuitem> 
	</logic:notEmpty>
  <logic:empty name="personForm" property="application">
   <hrms:menuitem name="party5" label="申请入党人员" function_id="" url="" enabled="false">     
  </hrms:menuitem> 
  </logic:empty>
  <logic:notEmpty name="personForm" property="application">
   <hrms:menuitem name="party5" label="申请入党人员" function_id="" url="gointo('application');">     
  </hrms:menuitem> 
  </logic:notEmpty>
  <logic:empty name="personForm" property="person">
  <hrms:menuitem name="party4" label="群众" function_id="" url="" enabled="false"> 
  </hrms:menuitem> 
  </logic:empty>
  <logic:notEmpty name="personForm" property="person">
  <hrms:menuitem name="party4" label="群众" function_id="" url="gointo('person');"> 
  </hrms:menuitem> 
  </logic:notEmpty>
  </logic:equal>
  <logic:equal value="V" name="personForm" property="param">
  <logic:empty name="personForm" property="member">
   <hrms:menuitem name="party5" label="团员" function_id="" url="" enabled="false">     
  </hrms:menuitem> 
  </logic:empty>
  <logic:notEmpty name="personForm" property="member">
   <hrms:menuitem name="party5" label="团员" function_id="" url="gointo('member');">     
  </hrms:menuitem> 
  </logic:notEmpty>
  <logic:empty name="personForm" property="person">
  <hrms:menuitem name="party4" label="群众" function_id="" url="" enabled="false"> 
  </hrms:menuitem> 
  </logic:empty>
  <logic:notEmpty name="personForm" property="person">
  <hrms:menuitem name="party4" label="群众" function_id="" url="gointo('person');"> 
  </hrms:menuitem> 
  </logic:notEmpty>
  </logic:equal>
</hrms:menubar>
</html:form>
        </td>
   </tr> 
</table>

</body>

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