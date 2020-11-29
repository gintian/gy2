<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
com.hjsj.hrms.actionform.performance.implement.ImplementForm"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css"; 	 
	}
	 ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");
     String opt = implementForm.getOptString();
     String codeid = implementForm.getCodeid();
     if(opt==null)
     opt="3";
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

<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes />
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<script language='javascript' >
	var info=window.dialogArguments;
	function historyDate(id){

    self.parent.location="/performance/implement/kh_mainbody/set_dyna_main_rank/setdynamainbodypropotion.do?b_ini=link&optString="+id;
     
}
</script>
<html:form action="/performance/implement/kh_mainbody/set_dyna_main_rank/setdynamainbodypropotion">
  <div onresize="resize_table()" style="width:100%;">
    <table id="toolTable" style="width:100%;position:absolute;left:0px;top:0px;" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground">
	  		 <tr align="left" class="toolbar" style="padding-left:2px;overflow: auto;">
			      <td valign="middle" width="100%" >
           <html:radio name="implementForm" property="optString" value="3" onclick='historyDate(3)'/>
           所属机构&nbsp;<html:radio name="implementForm" property="optString" value="11" onclick='historyDate(11)'/> 
           对象类别

		</td>
		</tr>
	<tr>  
		<td valign="top">
			<div id="treemenu"></div>
		</td>
	 
	</tr>
</table>
</div>
<SCRIPT LANGUAGE=javascript>;
//flag:-1 从人员库开始显示 0从顶层机构开始显示
    var m_sXMLFile	= "/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr,&planid=${implementForm.planid}&opt=<%=opt%>";		
    //var actionUrl = "/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_search=link&planid=${implementForm.planid}&codeid=${implementForm.codeid}";
	//actionUrl="/performance/implement/kh_mainbody/set_dyna_main_rank/welcome.html";//点击根节点显示空白页面
	actionUrl="";//一进入界面显示默认的第一个有效节点的数据啦 所以不用在此设置右边界面的反应了
	var newwindow;
	//var root=new xtreeItem("root","组织机构",actionUrl,"mil_body","组织机构","/images/root.gif",m_sXMLFile);
	<%if(opt.equals("11")){%> 	
	var root=new xtreeItem("root","考核对象类别",actionUrl,"mil_body","考核对象类别","/images/add_all.gif",m_sXMLFile);
	
	<%}else{%>
	var root=new xtreeItem("root","组织机构",actionUrl,"mil_body","组织机构","/images/root.gif",m_sXMLFile);
	<%}%>
	Global.showroot=true;
	root.setup(document.getElementById("treemenu"));	
	//root.openURL();
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

	//计算table的宽度，之前的初始宽度太长了
	/*var toolTable = document.getElementById("toolTable");
	if (toolTable){
	    var theWidth = document.body.clientWidth || document.documentElement.clientWidth;
        toolTable.style.width = (theWidth-7)+"px";
    }*/

	if(obj)
	{				
		var codeitemid = obj.uid.split("`");		
		if(codeitemid=='root')
		{	
			parent.mil_body.location="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?br_search=link";
		
		}else
		{	
		    var codeid = codeitemid[3]+codeitemid[0];
			selectedClass("treeItem-text-"+obj.id);	
			parent.mil_body.location="/performance/implement/kh_mainbody/set_dyna_main_rank/searchdynamainbodypropotion.do?b_search=link&planid=${implementForm.planid}&codeid="+codeid;
		}
	}
    function resize_table() {
  	  //如果是ie下，菜单随着拖动会有一块空白，这里算出iframe的宽度，对应的调整
  	  if(getBrowseVersion())
  	  	document.getElementById("toolTable").style.width = parent.document.getElementById("center_iframe").clientWidth + "px";
    }
    
    resize_table();
</SCRIPT>
</html:form>

