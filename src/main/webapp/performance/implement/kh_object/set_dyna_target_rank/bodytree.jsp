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
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";

          //out.println("<link href='"+css_url+"' rel='stylesheet' type='text/css' >");  	 
	}
	 ImplementForm implementForm=(ImplementForm)session.getAttribute("implementForm");
     String opt = implementForm.getOptString();
     String codeid = implementForm.getCodeid();
     if(opt==null)
     opt="4";
%>
<SCRIPT LANGUAGE=javascript>
function historyDate(id){

    self.parent.location="/performance/implement/kh_object/set_dyna_target_rank/setdynatargetpropotion.do?b_ini=link&optString="+id;
     
}
</SCRIPT>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes />
<link rel="stylesheet" href="/css/xtree.css" type="text/css">
<html:form action="/performance/implement/kh_object/set_dyna_target_rank/setdynatargetpropotion"> 
   <table id="toolbarTable" align="left" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
   <tr>
   <td>
   <div id="toolbar" class="toolbar" style="overflow: auto;padding-left:0px;">
           <html:radio name="implementForm" property="optString" value="4" onclick='historyDate(4)'/>
           所属机构&nbsp;<html:radio name="implementForm" property="optString" value="10" onclick='historyDate(10)'/> 
           对象类别
		</div>
   </td>   
   
   </tr>
	<tr>  
		<td valign="top">
			<div id="treemenu" >
			</div>
		</td>
	 
	</tr>
</table>	
<SCRIPT LANGUAGE=javascript>
    window.onload=function(){
        var width = document.body.clientWidth || document.documentElement.clientWidth
        document.getElementById("toolbar").style.width = width+"px"
        document.getElementById("toolbarTable").style.width = width+"px"
    }
    window.onresize=function(){
        var width = document.body.clientWidth || document.documentElement.clientWidth
        document.getElementById("toolbar").style.width = width+"px"
        document.getElementById("toolbarTable").style.width = width+"px"
    }
//flag:-1 从人员库开始显示 0从顶层机构开始显示
    var m_sXMLFile	= "/performance/kh_plan/handImportObjs.jsp?flag=0&id=0&nbase=Usr,&planid=${implementForm.planid}&opt=<%=opt%>";		
    //var actionUrl = "/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_search=link&planid=${implementForm.planid}&codeid=${implementForm.codeid}";
	//actionUrl="/performance/implement/kh_mainbody/set_dyna_main_rank/welcome.html";//点击根节点显示空白页面
	actionUrl="";//一进入界面显示默认的第一个有效节点的数据啦 所以不用在此设置右边界面的反应了
	var newwindow;
	<%if(opt.equals("10")){%> 	
	var root=new xtreeItem("root","考核对象类别",actionUrl,"mil_body","考核对象类别","/images/add_all.gif",m_sXMLFile);
	
	<%}else{%>
	var root=new xtreeItem("root","组织机构",actionUrl,"mil_body","组织机构","/images/root.gif",m_sXMLFile);
	<%}%>
	Global.defaultInput=1;
	Global.showroot=false;
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

	if(obj)
	{	var codeitemid = obj.uid.split("`");
	    var codeid = codeitemid[3]+codeitemid[0];
		selectedClass("treeItem-text-"+obj.id);	
		parent.mil_body.location="/performance/implement/kh_object/set_dyna_target_rank/searchdynatargetpropotion.do?b_search=link&planid=${implementForm.planid}&codeid="+codeid;
	}
</SCRIPT>
</html:form>
