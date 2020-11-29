<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.general.muster.hmuster.HmusterForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
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
	String a_code=userView.getManagePrivCode()+userView.getManagePrivCodeValue();
	HmusterForm hmusterForm=(HmusterForm)session.getAttribute("hmusterForm");
	String modelFlag=hmusterForm.getModelFlag();
	String infor_flag=hmusterForm.getInfor_Flag();
	if(infor_flag==null||infor_flag.equals(""))
	    infor_flag="1";
	 
	//zxj 20160613 花名册等报表功能不在区分专业版标准版
    //int versionFlag = 0;
	//if (userView != null)
	//	versionFlag = userView.getVersion_flag(); // 1:专业版 0:标准版		
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<LINK href="<%=css_url%>" rel="stylesheet" type="text/css">
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
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
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<!-- popcalendar.js xuj add 2013-7-29 解决extra="editor" dropDown="dropDownDate"使用统一日期控件 -->
<script type="text/javascript" src="/js/popcalendar.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
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
<script language="JavaScript" src="./function.js"></script>
<script type="text/javascript">
var dt=(window.screen.availHeight - 30 - 460) / 2;  //获得窗口的垂直位置
var dl=(window.screen.availWidth - 10 - 790) / 2; //获得窗口的水平位置 
var userAgent = window.navigator.userAgent; //取得浏览器的userAgent字符串  
var isOpera = userAgent.indexOf("Opera") > -1;
var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1 && !isOpera); 
function dragendSort()
{
   var currnode=Global.selectedItem;
    // 24035 员工管理  高级花名册 点击花名册前面的线  页面报错  鼠标在线上点击拖动时 无currnode.dragFrom 报错  changxy 20161108
	if(currnode.dragFrom&&currnode.dragFrom.uid=='root')
		return false;
	if(currnode.dragbool){
		if(currnode.dragFrom.uid.indexOf("X")!=-1||currnode.dragFrom.uid=='root')
			return false;
		var hashvo=new ParameterSet();
		hashvo.setValue("fromid",currnode.dragFrom.uid);
		hashvo.setValue("toid",currnode.uid);
		hashvo.setValue("table","muster_name");
		hashvo.setValue("enteryType","hmuster");
		var request=new Request({method:'post',asynchronous:false,functionId:'0520000004'},hashvo);
		if(currnode.uid.indexOf("X")!=-1||currnode.uid=='root'){
			currnode.dragFrom.remove();
			currnode.clearChildren();
			currnode.expand();
		}else{
			currnode.dragFrom.remove();
			currnode.parent.clearChildren();
			currnode.parent.load=true;
	  		currnode.parent.loadChildren();
	  		currnode.parent.reload(1);
		}
	}
}
function simpleQuery(type,a_code,tablename)
{
   var return_vo;
    var thecodeurl="/general/inform/search/generalsearch.do?b_query=link&moduleFlag=hmuster&type="+type+"&a_code="+a_code+"&tablename="+tablename;
    if(isIE){
	    return_vo= window.showModalDialog(thecodeurl, "", 
	                 	"dialogWidth:730px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no;");
    }else{
	      window.open(thecodeurl,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=730px,height=440px');

    }
    if(return_vo)
    {
        alert("查询结果已填写到结果表中，点击\"重填\"功能对当前选中花名册进行重新取数！ ");
        return;
    }   
}

function selectReturn(return_vo){
	if(return_vo)
    {
        alert("查询结果已填写到结果表中，点击\"重填\"功能对当前选中花名册进行重新取数！ ");
        return;
    }   
}
var openWindow;
function fzQuery()
{
   var return_vo;
   var height = 530;
   if(isIE6()){
       height +=30;
   }
   var thecodeurl="/workbench/query/complex_interface.do?b_gquery=link`fromFlag=1`returnvalue=";
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(thecodeurl);
   if(isIE){
	   return_vo= window.showModalDialog(iframe_url, "", 
            	"dialogWidth:710px; dialogHeight:"+height+"px;resizable:no;center:yes;scroll:no;status:no;");
   }else{
	   openWindow=window.open(iframe_url,"_blank",'toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top='+dt+'px,left='+dl+'px,width=710px,height='+height+'px');

   }
    
    if(return_vo)
    {
        alert("查询结果已填写到结果表中，点击\"重填\"功能对当前选中花名册进行重新取数！ ");
        return;
    }       
}

function returnValue(return_vo){
	if(return_vo)
    {
		if(openWindow)
			openWindow.close();
        alert("查询结果已填写到结果表中，点击\"重填\"功能对当前选中花名册进行重新取数！ ");
        return;
    }       
}

function reloadData()
{
    var obj=Global.selectedItem; 
    if(obj==null||obj.uid==null||obj.uid=='root'||obj.uid.indexOf("X")!=-1){
        alert("请选择花名册！");
        return;
   }
   if(confirm("重填将根据查询结果重填花名册，是否继续？"))
   {
      self.parent.nil_body.jinduo();
       self.parent.nil_body.location ="/general/muster/hmuster/select_muster_name.do?b_next2=b_next2&isGetData=0&history=1&queryScope=1&operateMethod=next&tabID="+obj.uid;
   }
}

</script>
<SCRIPT LANGUAGE="javascript" src="/js/xtree.js"></SCRIPT> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<hrms:themes />
<body onresize="resize();"><!-- 【7001】员工管理，组织机构，界面样式问题  jingq upd 2015.01.28 -->
<html:form action="/general/muster/hmuster/searchHroster">
<%if((infor_flag.equals("1")&&!modelFlag.equals("15")&&!modelFlag.equals("81")&&!modelFlag.equals("5"))||(infor_flag.equals("2")||infor_flag.equals("3"))){ %>
 <table id="tabID" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:expression(document.body.clientWidth);">
	<tr id="trID" align="left" class="toolbar" style="padding-left:2px;width:expression(document.body.clientWidth);overflow:auto;">
		<td valign="middle" align="left" width="100%">
		
			<!-- 非兼容模式浏览器兼容修改：alt属性在IE11废弃了，下面<a>标签都改为title属性  guodd 2018-03-01 -->
			&nbsp;<img src="/images/quick_query.gif" title="<bean:message key='infor.menu.squery'/>" style="cursor:pointer" border="0" onclick='simpleQuery("${hmusterForm.infor_Flag}","<%=a_code%>","all")'></img>
			<logic:equal value="1" name='hmusterForm' property='infor_Flag'>
			&nbsp;<img src="/images/new_module/search_blue.gif" title="复杂查询" style="cursor:pointer" border="0" onclick="fzQuery();"></img>  
			</logic:equal>     
    		&nbsp;<img src="/images/refillout.gif" title="重填" style="cursor:pointer" border="0" onclick="reloadData();"></img> 
			
		
		</td>
	</tr>
         <tr>
           <td align="left">  
            <div id="treemenu" ondragend="dragendSort();"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="hmusterForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table> 
<%}else{ %> 
<table align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:expression(document.body.clientWidth);">
<tr>
           <td align="left">  
            <div id="treemenu" ondragend="dragendSort();"> 
             <SCRIPT LANGUAGE=javascript>                 
               <bean:write name="hmusterForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>           
    </table> 
<%} %>
</html:form>
</body>
<script language="javascript">
//bug号：35084
if(document.getElementById("tabID"))
	document.getElementById("tabID").style.width=document.body.clientWidth-1;
if(document.getElementById("trID"))	
	document.getElementById("trID").style.width=document.body.clientWidth-1;
  initDocument();
  setDrag(true);
  function resize(){
	  if(document.getElementById("tabID"))
		  document.getElementById("tabID").style.width=document.body.clientWidth-1;
	  if(document.getElementById("trID"))
		  document.getElementById("trID").style.width=document.body.clientWidth-1;
  }
</script>