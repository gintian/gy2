<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant, com.hjsj.hrms.actionform.performance.implement.ImplementForm"%>
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
<script language="JavaScript">
function pf_ChangeFocus() 
{
   key = window.event.keyCode;
   if ( key==0xD && event.srcElement.tagName!='TEXTAREA') /*0xD*/
   {
   	window.event.keyCode=9;
   }
   //按F5刷新问题,重复提交问题,右键菜单也设法去掉
   if ( key==116)
   {
   	window.event.keyCode=0;	
	window.event.returnValue=false;
   }   
 
   if ((window.event.ctrlKey)&&(key==82))//屏蔽 Ctrl+R  
   {    
        window.event.keyCode=0;	
	window.event.returnValue=false;
   } 
}
</script>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  }
	ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");	
	String planStatus=implementForm.getPlanStatus();	
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<hrms:themes />
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
<html:form action="/performance/implement/kh_object/dynaitem">
<div onresize="resize_table()" style="width:100%;">
<table id="table_dyna_menu" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;width:100%;height:90%">
  <tr align="left" class="toolbar" style="padding-left:2px;">  
    <td valign="middle">&nbsp;
				<%if(planStatus.equals("3") || planStatus.equals("5")){ %>
					  <a href="###" onclick="return copyRule();"> <img src="/images/copy.gif" border="0" align="middle" title="<bean:message key='jx.implement.copyRule' />" /></a>
				 	 <!-- <img src="/images/past.gif"  id='pastBt'  alt="<bean:message key='jx.implement.pastRule' />" disabled onClick="pastRule();"/> -->
				 	  <a href="###" onclick="return pastRule();">
						  <!-- 控制粘贴按钮是否可以点击 -->
						  <input type="hidden" id="isCanClick" value="0"/>
						  <img src="/images/past.gif" border="0" align="middle"  title="<bean:message key='jx.implement.pastRule' />"/>
					  </a>
				<%}%>
			</td>
		</tr>
		<tr>
			<td valign="top" align="left">
				<div id="treemenu">
				</div>
			</td>
		</tr>
	</table>
</div>
</html:form>
<script>
	var m_sXMLFile	= "/performance/implement/kh_object/set_dyna_item_rank/object_type_tree.jsp?planID=${implementForm.planid}";		
	var newwindow;
	var root=new xtreeItem("root","考核对象类别","","mil_body","考核对象类别","/images/add_all.gif",m_sXMLFile);
	//Global.defaultInput=1;
	Global.showroot=true;
	
	root.setup(document.getElementById("treemenu"));	
	if(newwindow!=null)
	{
		newwindow.focus();
	}
	if(parent.parent.myNewBody!=null)
	{
		parent.parent.myNewBody.cols="*,0"
	}
	var obj=root;
	var i=0;
	var ori_text="";
	if(obj.getFirstChild())
	{
		ori_text=obj.getFirstChild().text;
		obj.getFirstChild().expand();
		var a_obj=obj.getFirstChild();
		obj=a_obj;
	}

	if(obj)
	{	
		selectedClass("treeItem-text-"+obj.id);	
		parent.mil_body.location="/performance/implement/kh_object/dynaitem.do?b_query=link&objTypeId="+obj.uid;
	}
	
	var copyObjTypeid,copyObjTypeDesc,pastObjTypeid,pastObjTypeDesc;
	function copyRule()
	{	
    	var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;   
    	copyObjTypeid=currnode.uid; 
    	copyObjTypeDesc=currnode.text;
    	if(copyObjTypeid=='root')
    	{
    		copyObjTypeid=obj.uid; 
    		copyObjTypeDesc=obj.text;
    	}
    	document.getElementById("isCanClick").value='1'
	}
	function pastRule()
	{
		if(document.getElementById("isCanClick").value==='0')
			return;
		var currnode=Global.selectedItem;
    	if(currnode==null)
    	    return;
    	pastObjTypeid=currnode.uid; 
    	pastObjTypeDesc=currnode.text;
    	if(pastObjTypeid=='root')
    	{
    		pastObjTypeid=obj.uid; 
    		pastObjTypeDesc=obj.text;
    	}
    	if(confirm('确认将['+copyObjTypeDesc+']的任务规则粘帖给['+pastObjTypeDesc+']吗？'))
    	{
    		parent.mil_body.location="/performance/implement/kh_object/dynaitem.do?b_pastRule=link&objTypeId="+pastObjTypeid+"&copyObjTypeId="+copyObjTypeid;
    	}
	}
	
	function resize_table() {
	  //如果是ie下，菜单随着拖动会有一块空白，这里算出iframe的宽度，对应的调整
	  if(getBrowseVersion())
	  	document.getElementById("table_dyna_menu").style.width = parent.document.getElementById("center_iframe").clientWidth + "px";
  	}
	
	resize_table();
</script>
