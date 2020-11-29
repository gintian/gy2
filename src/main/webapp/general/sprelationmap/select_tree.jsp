<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.general.sprelationmap.*,com.hrms.frame.codec.SafeCode"%>
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
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());
	}
	VersionControl ver = new VersionControl();
	RelationMapForm rmform = (RelationMapForm)session.getAttribute("relationMapForm");
	String relationType=rmform.getRelationType();
	String flag="1";//加载人员
	String loadtype="0";//=0加载职位，=1加载到部门
	if(relationType.equals("2")){
	   flag="0";
	   loadtype="0";
	}
%>
<html>
<head>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<LINK href="<%=css_url%>" rel="stylesheet" type="text/css">
<hrms:themes></hrms:themes>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
<script type="text/javascript">
function spMaintain(){
  var currnode=Global.selectedItem;
  var currId=currnode.uid;
  if(currId=='root'){
     alert("不能选择根节点进行审批关系维护！");
     return;
  }
  
  var src="/general/sprelationmap/relation_maintain.do?b_init=query`currId="+currId;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+src;
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:700px; dialogHeight:650px;resizable:no;center:yes;scroll:no;status:no");			
	return;
  
}
</script>
<%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
</head>
<body style="overflow: auto;" >
<html:form action="/general/sprelationmap/select_tree">  
    <table width="101%" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px; margin: 0,0,0,0;">
	<tr align="left"  class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">
		<hrms:priv func_id="23052101">
			<a href="javascript:configParam();"><img src="/images/sys_config.gif" border="0" title="设置"/></a>
			</hrms:priv>
			<hrms:priv func_id="23052102">
			<a href="javascript:spMaintain();" ><img src="/images/edit.gif" border="0" title="审批关系维护"/></a>&nbsp;
			</hrms:priv>
			</td>
			</tr>
			<tr>
           <td align="left"> 
           <logic:notEqual value="3" name="relationMapForm" property="relationType">
             <hrms:orgtree action="/general/sprelationmap/relation_map_drawable.do?b_init=init&showQueryButton=showQueryButton&relationType=${relationMapForm.relationType}" target="mil_body" viewunit="0" flag="<%=flag%>" dbtype="1" loadtype="0" priv="1" showroot="false" dbpre="" rootaction="0" rootPriv="0" nmodule="4"/>			           
           </logic:notEqual>
            <logic:equal value="3" name="relationMapForm" property="relationType">
            </logic:equal>
           </td>
           </tr>           
    </table>   
</html:form>
<script language="javascript">                                                                                                             
 var height=self.parent.mil_body.document.body.clientHeight;
 var width =self.parent.mil_body.document.body.clientWidth;
 // tiany update 根节点不查询顶级单位关系图，防止没有授权任何权限的人员查看点击根节点暴漏单位名称信息和审批关系相关信息
self.parent.mil_body.window.location.href("/general/sprelationmap/relation_map_drawable.do?b_init=init&showQueryButton=showQueryButton&relationType=${relationMapForm.relationType}" +"&clientHeight="+height+"&clientWidth="+width);
 //root.openURL();
 
</script>
</body>
</html>