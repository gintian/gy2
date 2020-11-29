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
   //System.out.println(buf.toString());
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
<script language="javascript" src="/module/utils/js/template.js"></script>
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
	function initPrivDate(){
		// var dw=350,dh=300,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
		//点击部分功能后，找不到正确的位置创建Ext window了，所以最后用window.open()实现功能  wangbs 2019年3月12日17:15:03
        var height = 290;
		if(isIE6){
		    height += 25;
		}
        var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
        var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
        var theUrl = '/org/orginfo/searchorgtree.do?br_initprivdate=link';
        window.open(theUrl,'','height='+height+', width=500,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');


	}
function initHistoryReturn(msg){
    if(msg && msg==1){
		orgInformationForm.action="/org/orginfo/searchorgtree.do?b_query=link&code=&query=&idordesc=";
		orgInformationForm.target="il_body"
		orgInformationForm.submit();
	}
}

function backDate(){
    var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
    var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
    var theUrl = '/org/orginfo/searchorgtree.do?b_backdate_new=link';
    window.open(theUrl,'','height=370, width=400,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
}
function openHistoryReturn(backdate){
    if(backdate && backdate.length>9) {
		orgInformationForm.action="/org/orginfo/searchorgtree.do?b_query=link&backdate="+ backdate +"&code=&query=&idordesc=";
		orgInformationForm.target="il_body"
		orgInformationForm.submit();
	}
}

	function refresh(){
		var currnode=parent.frames['mil_menu'].Global.selectedItem;
		if(currnode==null)
			return;
		//alert(currnode.uid);
		if(parent.frames['mil_body'].document.getElementById("wait")){
		jindu();
		var request=new Request({method:'post',asynchronous:true,parameters:"path=<%=path %>",onSuccess:oncomplete,functionId:'16010000036'});
		function oncomplete(outparamters){
			var msg=outparamters.getValue("msg");
			if(msg=="ok"){
				if(currnode.load){
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
		}
		}else{
			alert("请您回到机构列表界面进行刷新!");
		}
	}
function jindu(){
	//var x=parent.frames['mil_body'].document.body.scrollLeft+event.clientX;
    //var y=parent.frames['mil_body'].document.body.scrollTop+event.clientY-300; 
	var waitInfo=parent.frames['mil_body'].document.getElementById("wait");
	//waitInfo.style.top=y;
	//waitInfo.style.left=x;
	waitInfo.style.display="block";
}
function closejindu(){
	var waitInfo=parent.frames['mil_body'].document.getElementById("wait");
	waitInfo.style.display="none";
}
//-->
</script>
<hrms:themes></hrms:themes>
<style>
<!--
	body {
	margin-left: 0px;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	}
	img{
	  margin-left:5px;
	}
-->
</style>

<body>
<html:form action="/org/orginfo/searchorgtree" > 
   <table width="700" border="0" cellspacing="0"  align="center" cellpadding="0" >     
   <tr align="left" class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left" >
		<%if(version){ %> 
		<hrms:priv func_id="230500">   
			<!-- 非兼容模式浏览器兼容修改：ie11不支持alt xus 19/3/4 -->      
			<img src="/images/sb.gif" align="middle" title="历史时点初始化" onclick="return initPrivDate();" style=" cursor: pointer">   
		</hrms:priv>
		<hrms:priv func_id="230501">
			<!-- 非兼容模式浏览器兼容修改：ie11不支持alt xus 19/3/4 -->
			<img src="/images/quick_query.gif" align="middle" title="历史时点查询" onclick="return backDate();" style="cursor: pointer">               
		</hrms:priv>
		<%}else{ %> 
		<%} %>
		<hrms:priv func_id="2305013">
		<!-- 非兼容模式浏览器兼容修改：ie11不支持alt xus 19/3/4 -->
		<img src="/ext/resources/images/default/grid/refresh.gif" align="middle" title="刷新" onclick="return refresh();" style="cursor: pointer">
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