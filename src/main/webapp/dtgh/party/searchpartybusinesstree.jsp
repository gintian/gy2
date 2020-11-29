<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.actionform.dtgh.party.PartyBusinessForm"%>
<%@ page import="com.hrms.struts.constant.WebConstant,com.hrms.hjsj.sys.IResourceConstant"%>
<%@page import="com.hrms.frame.utility.DateStyle,com.hjsj.hrms.businessobject.sys.SysParamBo"%>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="javascript" src="/js/constant.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<%!
	private String analyseManagePriv(String managed_str){
		if(managed_str.length()<3)
			return "";
		StringBuffer sb = new StringBuffer();
		String[] strS = managed_str.split(",");
 		 String ids="";
 		 for(int i=0;i<strS.length;i++){
 			 String id = strS[i];
 			 if(id!=null&&id.length()>1){
 				 boolean check = true;
 				 for(int j=0;j<strS.length;j++){
 					 String id_s = strS[j];
 					 if(id_s!=null&&id_s.length()>1){
 						 if(id.length()>id_s.length()){
 							if(id.substring(2,id.length()).startsWith(id_s.substring(2,id_s.length()))){
								 check = false;
								 ids=id_s;
								 break;
							 }
 						 }else{
 							 if(id.equalsIgnoreCase(id_s)){
 								 continue;
 							 }
 							 if(id_s.substring(2,id_s.length()).startsWith(id.substring(2,id.length()))){
 								 check = false;
 								ids=id_s;
 								 break;
 							 }
 						 }
 					 }
 				 }
 				 if(check){
 					if(sb.indexOf(id)==-1)
 						sb.append("','"+id.substring(2));
 				 }else{
 					 if(id.length()<ids.length()){
 						if(sb.indexOf(id)==-1)
 							sb.append("','"+id.substring(2));
 					 }
 				 }
 			 }
 		 }
 		if(sb.length()<4)
			return "";
		else
			return sb.substring(3);
	}
 %>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PartyBusinessForm partyBusinessForm = (PartyBusinessForm)session.getAttribute("partyBusinessForm");
	 String backdate = partyBusinessForm.getBackdate();
	 String bosflag="";
	    String themes="default";
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	  bosflag=userView.getBosflag(); 
      /*xuj added at 2014-4-18 for hcm themes*/
      themes=SysParamBo.getSysParamValue("THEMES",userView.getUserName());  
	}
 
 	int res_type = IResourceConstant.PARTY;
	if("65".equals(partyBusinessForm.getCodesetid()))
		 res_type = IResourceConstant.MEMBER;
		 
	String codevalue = userView.getResourceString(res_type);
	    	   if(codevalue.length()<3){
	    		   if(userView.isSuper_admin()&&!userView.isBThreeUser())
	    			   codevalue="ALL";
	    		   else{
	    			   if(codevalue.equals("64")||codevalue.equals("65"))
	    				   codevalue="ALL";
	    			   else
	    				   codevalue=""; 
	    		   }
	    	   }else{
	    		   codevalue=this.analyseManagePriv(codevalue);
	    			if(codevalue.length()<1)
	    			   codevalue="ALL";
	    	   }
		 
%>
<script type="text/javascript">
	function add(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
	}
	function add1(uid,text,action,target,title,imgurl,xml)
	{
		var currnode=Global.selectedItem;
		imgurl="/images/unit.gif";
		var tmp = new xtreeItem(uid,text,action,target,title,imgurl,xml);
		currnode.add(tmp);
	}
	function backDate(){
		var theUrl = '/dtgh/party/searchpartybusinesslist.do?br_backdate=link';
        //在谷歌下showModalDialog不生效 用open弹窗代替  wangb 20190312
        openHistoryWin(theUrl);
	}

	function openHistoryWin(theUrl){
        var iTop = (window.screen.height-30-305)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-470)/2;  //获得窗口的水平位置;
		window.open(theUrl,'','height=305, width=470,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}
	//open弹窗回调方法  wangb 20190312
	function openHistoryReturn(return_vo){
		if(return_vo && (return_vo).length > 9) {
        	var param="${partyBusinessForm.param}";
        	var target_url="/dtgh/party/searchpartybusinesstree.do?b_query1=link&param="+param+"&backdate="+return_vo;
        	partyBusinessForm.target="il_body";
        	partyBusinessForm.action=target_url;
        	partyBusinessForm.submit();
    	}
	}
	function setitem(){
        //用Ext的window组件替代showModalDialogs  wangbs 2019年3月6日14:52:19
        var theUrl="/system/options/standardduty_duty_item.do?b_search=link`submit=not";
        var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theUrl);
        createSetItemWin(iframe_url);
	}
	function createSetItemWin(iframe_url){
		var iTop = (window.screen.height-30-500)/2; //获得窗口的垂直位置;
		var iLeft = (window.screen.width-10-700)/2;  //获得窗口的水平位置;
		window.open(iframe_url,'','height=500, width=700,top='+iTop+',left='+iLeft+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no,status=no');
	}

	function openwin(url)
	{
	   window.open(url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=no,resizable=no,status=no");
	}
	
</script>

<style>
<!--
   a{
      margin-left:5px;
   }
-->
</style>
<HTML>
<HEAD>
<TITLE>
</TITLE>
   <link href="<%=css_url%>" rel="stylesheet" type="text/css">
   <link href="/css/xtree.css" rel="stylesheet" type="text/css" >
   <%if("hcm".equals(bosflag)){ %>
   <link href="/css/hcm/themes/<%=themes %>/content.css" rel="stylesheet" type="text/css" />
  
  <%} %> 
</HEAD><!-- 【7001】员工管理，组织机构，界面样式问题  jingq upd 2015.01.28 -->
<body style="margin:0px;padding:0px;overflow:auto;" onclick="">
<html:form action="/dtgh/party/searchpartybusinesslist" style="width:expression(document.body.clientWidth)">
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td>
		<table width="100%" border="0" cellspacing="0"  align="left" cellpadding="0" >
	    	<tr align="left" class="toolbar">
			<td valign="middle" align="left">
			<logic:equal value="Y" name="partyBusinessForm" property="param">
				<hrms:priv func_id="3501101">
					<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" title="撤销党组织机构查询" border="0"></a>
				</hrms:priv>
			</logic:equal>
			<logic:equal value="V" name="partyBusinessForm" property="param">
				<hrms:priv func_id="3502101">
					<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" title="撤销团组织机构查询" border="0"></a>
				</hrms:priv>
			</logic:equal>
			<logic:equal value="H" name="partyBusinessForm" property="param">
				<hrms:priv func_id="">
					<a href="###" onclick="return backDate();"><img src="/images/quick_query.gif" title="历史时点" border="0"></a>
				</hrms:priv>
				<hrms:priv func_id="25031">                   
						<a href="###" onclick="openwin('/module/muster/mustermanage/MusterManage.html?musterType=4&moduleID=1')"><img src="/images/prop_ps.gif" border=0 title="常用花名册"></a>
				</hrms:priv>
				<hrms:priv func_id="2501206">     
			    	<a href="###" onclick="openwin('/general/muster/hmuster/searchHroster.do?b_search=link&nFlag=51&a_inforkind=5&result=0&closeWindow=1')"><img src="/images/bm10.gif" border=0 title="高级花名册"></a>
				</hrms:priv> 
				<hrms:priv func_id="2501207">
				 <hrms:link href="###" onclick="openwin('/module/card/cardCommonSearch.jsp?inforkind=6&callbackfunc=window.close')" ><img align="middle" src="/images/wjj_c.gif" border=0 title="基准岗位说明书"></hrms:link>
			    </hrms:priv>
				<hrms:priv func_id="2501205">
			        <a href="###" onclick="setitem();"><img src="/images/sys_config.gif" title="设置对应指标" border="0"></a>
			    </hrms:priv>
			</logic:equal>
		</td></tr></table>
	</td></tr>
	<tr><td>
		<table width="100%" border="0" cellpadding="0" cellspacing="0">
			<tr><td>
				<div id="treemenu" style="height: expression(document.body.clientHeight-50);"></div>
			</td></tr> 
		</table> 
    <SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
     <SCRIPT LANGUAGE=javascript>
       
       
      // var m_sXMLFile="/servlet/sduty/getSdutyTree?param=root&target=mil_body&codesetid=${partyBusinessForm.codesetid}&codeitemid=<%=codevalue%>";
      // var root=new xtreeItem("root","${partyBusinessForm.codesetdesc}","/dtgh/party/searchpartybusinesslist.do?b_query=link&a_code=${partyBusinessForm.codesetid}","mil_body",'${partyBusinessForm.codesetdesc}',"/images/spread_all.gif",m_sXMLFile);
      // root.setup(document.getElementById("treemenu"));
      // root.openURL();
       var m_sXMLFile="/pos/posbusiness/get_code_tree.jsp?codesetid=${partyBusinessForm.codesetid}&codeitemid=&action=/dtgh/party/searchpartybusinesslist.do&checked=0&validateflag=1&backdate=<%=backdate%>";	 //
       var root=new xtreeItem("root","${partyBusinessForm.codesetdesc}","/dtgh/party/searchpartybusinesslist.do?b_query=init&a_code=${partyBusinessForm.codesetid}","mil_body","${posBusinessForm.codesetdesc}","/images/spread_all.gif",m_sXMLFile);
       root.setup(document.getElementById("treemenu"));
       root.openURL();
    </SCRIPT>
    </td></tr></table>
 </html:form>
<BODY>
</HTML>
