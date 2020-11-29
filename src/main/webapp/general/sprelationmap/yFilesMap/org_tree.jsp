<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@page import="com.hrms.hjsj.sys.VersionControl,com.hrms.struts.constant.SystemConfig,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hjsj.hrms.actionform.general.sprelationmap.*,com.hrms.frame.codec.SafeCode"%>
<%
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
<hrms:themes></hrms:themes>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" ></link>
<script language="javascript" src="/general/sprelationmap/relationMap.js"></script>
	<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../../js/hjsjUrlEncode.js"></script>
	<script type="text/javascript">
//保存汇报关系显示页面 选中节点 信息，可以直接通过点击节点来设置汇报关系
var graphselectid;


var currId;
function spMaintain(){
	if(graphselectid)
		currId = graphselectid;
	else{
  		var currnode=Global.selectedItem;
  		currId=currnode.uid;
	}
  if(currId=='root'){
     alert("不能选择根节点进行审批关系维护！");
     return;
  }
  
  var src="/general/sprelationmap/relation_maintain.do?b_init=query`currId="+currId;
   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(src);
  /*
   var values= window.showModalDialog(iframe_url,null, 
		        "dialogWidth:700px; dialogHeight:660px;resizable:no;center:yes;scroll:yes;status:no");	
   
	if(values == "saved")
   		window.open('/general/sprelationmap/show_report_map.do?b_search=link&showQueryButton=showQueryButton&a_code='+currId,"mil_body");
   */
   
  var iTop = (window.screen.height-30-660)/2;       //获得窗口的垂直位置;
  var iLeft = (window.screen.width-10-700)/2;        //获得窗口的水平位置;
  window.open(iframe_url,'','height=660, width=700,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
}
function returnValue(values){
	if(values == "saved")
   		window.open('/general/sprelationmap/show_report_map.do?b_search=link&showQueryButton=showQueryButton&a_code='+currId,"mil_body");
}
</script>
</head>
<body style="overflow:auto; margin: 0px;">
<html:form action="/general/sprelationmap/select_tree">  
   <table width="100%" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px; margin: 0,0,0,0;">
	<tr align="left"  class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">
		<hrms:priv func_id="23052101">
			<a href="javascript:configParam('isyfiles');"><img src="/images/sys_config.gif" border="0" title="设置"/></a>
			</hrms:priv>
			<hrms:priv func_id="23052102">
			<a href="javascript:spMaintain();" ><img src="/images/edit.gif" border="0" title="审批关系维护"/></a>&nbsp;
			</hrms:priv>
			</td>
			</tr>
			<tr>
           <td align="left"> 
           <logic:notEqual value="3" name="relationMapForm" property="relationType">
             <hrms:orgtree action="/general/sprelationmap/show_report_map.do?b_search=link&showQueryButton=showQueryButton&relationType=${relationMapForm.relationType}" target="mil_body" viewunit="0" flag="<%=flag%>" dbtype="1" loadtype="0" priv="1" showroot="false" dbpre="" rootaction="0" rootPriv="0" nmodule="4"/>	           
           </logic:notEqual>
            <logic:equal value="3" name="relationMapForm" property="relationType">
            </logic:equal>
           </td>
           </tr>           
    </table> 
    <script>
      //root.getFirstChild().openURL();
    </script>
    
</html:form>
</body>
</html>