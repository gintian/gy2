<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    // 在标题栏显示当前用户和日期 2004-5-10 
    String viewunit="1";
    String userName = null;
    String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	if(userView != null){
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	 css_url="/css/css1.css";
	  	 
	 	//if(userView.getStatus()==4||userView.isSuper_admin()){
		//	viewunit="0";
		//}
		/**liwc 业务用户走操作单位，没有操作单位时走管理范围=lmm*/
		//if(userView.getStatus()==0&&!userView.isSuper_admin()){
		//	String codeall = userView.getUnit_id();
		//	if(codeall==null||codeall.length()<3)
		//		viewunit="0";
		//}
	}
%>		
     
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<body style="margin-left:0px;margin-top:0px">

  <table width="200" border="0" cellspacing="0"  align="left" cellpadding="0" class="mainbackground" >   
	 <tr align="left">
		<td valign="middle"  class="toolbar" style="padding: 3 0 0 5px ;">
	
		  <hrms:priv func_id="27032">
			<input type="image" name="" src="/images/lawedit.gif" alt="假期设置" onclick="jiarishezhi();"> 
		  </hrms:priv>
		  <hrms:priv func_id="27034">                   
			<input type="image" name="" src="/images/prop_ps.gif" alt="公休日设置" onclick="gxrshezhi();"> 
		  </hrms:priv>
		  <hrms:priv func_id="0AC020101"> 
			<input type="image" name="" width="16" height="16" src="/images/img_o.gif" alt="<bean:message key='train.resource.course.setparam'/>" onclick="check();"> 
		  </hrms:priv>
		  <!--  
		  <hrms:priv func_id="0AC020111"> 
		  <input type="image" name="" src="/images/group_p.gif" alt="审批关系设置" onclick="spshezhi();" />
		  </hrms:priv>-->
		  <hrms:priv func_id="27033">
		  <input type="image" name="" src="/images/group.gif" alt="考勤项目设置" onclick="kqshezhi();">
		  </hrms:priv>
		</td>
	 </tr>          
      <tr>        
           <td align="left"> 
                 <hrms:orgtree action="/kq/month_kq/searchkqinfo.do?b_query=link&model=1" target="mil_body" flag="0" nmodule="6" loadtype="1" priv="1" showroot="false" dbpre="" rootaction="1"/>			           
           </td>
      </tr>
      <!--  <tr>
      		<td>
      			<div id="treemenu" style="display:none;"></div>
      		</td>
      </tr> -->      
   </table>

</body>
<script>
	root.openURL();
</script>

<script type="text/javascript">

	// var m_sXMLFile= "/kq/options/item_list.jsp?params=codeitemid%3Dparentid";
 	// var root=new xtreeItem("27","考勤项目","/kq/options/kq_item_details.do?b_query=link&codeitemid=","mil_body","考勤项目","/images/table.gif",m_sXMLFile);
 	// root.setup(document.getElementById("treemenu"));
	function jiarishezhi(){
		var syncurl = "/kq/options/search_feast.do?b_query=link`flag=gwkq`isshow=0`returnvalue=";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+syncurl;
		var return_vo= window.showModalDialog(iframe_url,"", 
	        "dialogWidth:600px; dialogHeight:600px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
	
	function gxrshezhi(){
		var syncurl = "/kq/options/kq_rest.do?b_query=link`flag=gwkq`returnvalue=";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+syncurl;
		var return_vo= window.showModalDialog(iframe_url,"", 
	        "dialogWidth:600px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}

	function check(){
		var syncurl="/kq/month_kq/searchkqinfo.do?b_set=link";
		var return_vo= window.showModalDialog(syncurl,"", 
	        "dialogWidth:400px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
	
	function kqshezhi(){
		var syncurl = "/kq/options/kq_item_detail.do?br_query=link&returnvalue=&flag=gwkq";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+syncurl;
		var return_vo= window.showModalDialog(iframe_url,"", 
	    "dialogWidth:1000px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	  // window.open(syncurl,"_blank");
	}
	
	function spshezhi(){
		var syncurl = "/general/relation/relationmaintence.do?b_query=link`isshowbutton=true";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+syncurl;
		var return_vo= window.showModalDialog(iframe_url,"", 
	    "dialogWidth:1000px; dialogHeight:700px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
	}
</script>	
