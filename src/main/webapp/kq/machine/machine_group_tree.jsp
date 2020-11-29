<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.frame.utility.DateStyle"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
<%@ page import="com.hjsj.hrms.utils.PubFunc"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
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
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
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
</script>
<%
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String codetiem="";
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  	
	   if(userView.isSuper_admin())
	   {
		codetiem= "UN";
	   }else
	   {
		 if(userView.getManagePrivCodeValue()!=null&&userView.getManagePrivCodeValue().length()>0)
		 {
		  codetiem="UN"+userView.getManagePrivCodeValue();
		 }else
		 {
		   codetiem= "UN"+userView.getUserOrgId();
		 }
	   }
    } 
	String id = (String)request.getParameter("id");
    if(id!=null&&id.length()>0)
      codetiem=id;
    
    codetiem = PubFunc.encrypt(codetiem);
    String params = PubFunc.encrypt("gp");
    
    EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	int ver=lock.getVersion();	
	ver=50;
%>
<script language="javascript">
    var iniCols, noCols, o_mf, o_ms, s;
<%if(ver<=40){%>
    function ini() 
    {
	   o_mf = parent.document.getElementById("forum");	   
	   noCols = iniCols = o_mf.rows;
	   if ((pos = noCols.indexOf(",")) != -1) 
	   {
		 noCols = "100%,0" ;
       }
	   s = false;
    }
    function showCheck(outparamters)
    {
      var flag=outparamters.getValue("flag");
      var o_mf = parent.document.getElementById("forum");      
      if(flag=="0")
      {
         o_mf.rows="100%,0" ;
      }else
      {
         o_mf.rows="50%,50%";
      }
    }   
    function selectClass()
    {
       var hashvo=new ParameterSet(); 
       hashvo.setValue("id","<%=codetiem%>");
       var request=new Request({method:'post',asynchronous:false,onSuccess:showCheck,functionId:'15211001100'},hashvo);
   }
<%}%>
<%if(ver>40){%>
    function ini() 
    {
	  
    }
<%}%>
    
  
</script>
<HTML>
<HEAD>
  <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   	<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
   <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>  
   <SCRIPT LANGUAGE=javascript>
    	
   </SCRIPT>  
</HEAD>
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<table width="100%" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
 <tr>  
    <td valign="top">
	<div id="treemenu"></div>
    </td>
  </tr>
</table>
<BODY>
</HTML>

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript>
  var codetiem="<%=codetiem%>";
  var params="<%=params%>";
  function setCode(code)
  {
    codetiem=code;
    alert(codetiem);    
  }
  var m_sXMLFile= "/kq/team/array/group_list.jsp?params="+params+"&codetiem="+codetiem+"&straction=/kq/machine/search_card_data.do";	
  var root=new xtreeItem("root","所有班组","/kq/machine/search_card_data.do?b_search=link&group_id=&a_code=","mil_body","所有班组","/images/table.gif",m_sXMLFile);
  root.setup(document.getElementById("treemenu"));
</SCRIPT>
<script language="javascript">;
   var dropDownDate=createDropDown("dropDownDate");
   var __t=dropDownDate;
   __t.type="date";
   __t.tag="";
   _array_dropdown[_array_dropdown.length]=__t;
   initDropDown(__t);
</script>
<script language="javascript">
    var dropdownCode=createDropDown("dropdownCode");
    var __t=dropdownCode;
    __t.type="custom";
    __t.path="/general/muster/select_code_tree.do";
    __t.readFields="codeitemid";
    //__t.writeFields="xxxx";
    __t.cachable=true;__t.tag="";
    _array_dropdown[_array_dropdown.length]=__t;
    initDropDown(__t);
</script>
<script language="javascript">
  initDocument();
  <%if(ver<=40){%>
  ini();
  selectClass();
 <%}%> 
</script>