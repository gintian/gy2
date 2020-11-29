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
    String bosflag = "";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");
      bosflag = userView.getBosflag();	 
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
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script language="javascript" src="/js/constant.js"></script>
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
        //点击部分功能后，找不到正确的位置创建Ext window了，所以最后用window.open()实现功能  wangbs 2019年3月12日16:46:32
        var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
        var theUrl = '/org/orginfo/searchorgtree.do?b_backdate=link';
        window.open(theUrl,'','height=320, width=400,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}
	//子页面回调该方法
	function openHistoryReturn(backdate) {
		if(backdate && backdate.length>9) {
			orgInfoForm.action = "/workbench/orginfo/searchorginfo.do?b_search=link&treetype=vorg&backdate=" + backdate + "&code=";
			orgInfoForm.target = "il_body"
			orgInfoForm.submit();
		}
    }
	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+(screen.availHeight-80)+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
	
		function refresh(){
		var currnode=parent.frames['nil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		//alert(currnode.uid);
		if(parent.frames['nil_body'].document.getElementById("wait1")){
		jindu();
		var request=new Request({method:'post',asynchronous:true,parameters:"path=<%=path %>",onSuccess:oncomplete,functionId:'16010000036'});
		function oncomplete(outparamters){
			var msg=outparamters.getValue("msg");
			if(msg=="ok"){
				if(currnode.load)
				while(currnode.childNodes.length){
					//alert(currnode.childNodes[0].uid);
					currnode.childNodes[0].remove();
				}
				currnode.load=true;
				currnode.loadChildren();
				currnode.reload(1);
			}
			closejindu();
		}
		}else{
			alert("请您回到机构列表界面进行刷新!");
		}
	}
function jindu(){
	//var x=parent.frames['mil_body'].document.body.scrollLeft+event.clientX;
    //var y=parent.frames['mil_body'].document.body.scrollTop+event.clientY-300;
	var waitInfo=parent.frames['nil_body'].document.getElementById("wait1");
	//waitInfo.style.top=y;
	//waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function closejindu(){
	var waitInfo=parent.frames['nil_body'].document.getElementById("wait1");
	waitInfo.style.display="none";
}
//-->
</script>


<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<hrms:themes /> 
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
	a{
	   margin-right:5px;
	}
-->
</style>
<body  border="0" cellspacing="0"  cellpadding="0">
<html:form action="/workbench/orginfo/searchorginfo"> 
    <table width="1000" cellspacing="0"  align="center" cellpadding="0" >
    	<%if(version){ %>
    	<tr align="left" class="toolbar" >
    	<!-- 非兼容模式浏览器兼容修改：style在tr上设置不起作用，设置到td上  guodd 2018-03-01 -->
		<td valign="middle" align="left" style="padding-left:10px;">
		<hrms:priv func_id="2306007,23151">
			<!-- 非兼容模式浏览器兼容修改：alt属性在IE11废弃了，下面<a>标签都改为title属性  guodd 2018-03-01 -->
			<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" title="历史时点查询" border="0" align="middle"></a>               
		</hrms:priv>
		<logic:notEqual value="leader" name="orgInfoForm" property="leader" >
			<hrms:priv func_id="23031">   
				<a href="###" onclick="openwin('/module/muster/mustermanage/MusterManage.html?musterType=2&moduleID=1')"><img align="middle" src="/images/prop_ps.gif" border=0 title="常用花名册"></a>
		    </hrms:priv>  
		    <hrms:priv func_id="23032">  
				<a href="###" onclick="openwin('/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=21&a_inforkind=2&result=0&closeWindow=1')" ><img  align="middle" src="/images/bm10.gif" border=0 title="高级花名册"></a>
		    </hrms:priv>
		    <hrms:priv func_id="2304">
		        <hrms:link href="###" onclick="openwin('/module/card/cardCommonSearch.jsp?inforkind=2&callbackfunc=window.close')" function_id="xxx"><img align="middle" src="/images/wjj_c.gif" border=0 title="登记表"></hrms:link>
		    </hrms:priv> 
		     <hrms:priv func_id="2317">  
		     	<a href="###" onclick="openwin('/general/static/commonstatic/statshow.do?b_ini=link&infokind=2&home=0')"><img align="middle" src="/images/img_f2.gif" border=0 title="统计分析"></a>
		     </hrms:priv> 
		     <hrms:priv func_id="230600A">
				<img src="/ext/resources/images/default/grid/refresh.gif" align="middle" title="刷新" onclick="return refresh();" style="cursor:pointer">
			</hrms:priv>
		</logic:notEqual>   
		</td>
		</tr>  
    	<%} %>
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