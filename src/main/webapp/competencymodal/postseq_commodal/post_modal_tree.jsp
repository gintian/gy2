<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant,
com.hjsj.hrms.actionform.competencymodal.PostModalForm,
com.hjsj.hrms.utils.PubFunc"%>
<script language="javascript" src="/js/constant.js"></script>
<%
    	String css_url="/css/css1.css";
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	PostModalForm postModalForm=(PostModalForm)session.getAttribute("postModalForm");
	String codesetid = postModalForm.getCodesetid();
//	codesetid = PubFunc.encryption(codesetid);
	if(userView != null)
	{
	  css_url=userView.getCssurl();
	  if(css_url==null||css_url.equals(""))
	  	css_url="/css/css1.css";
	}
 
%>
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT>
<script language="JavaScript" src="/competencymodal/postseq_commodal/postmodal.js"></script>
<link href="<%=css_url%>" rel="stylesheet" type="text/css"/>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<link rel="stylesheet" type="text/css" href="/ajax/skin.css"></link>
<hrms:themes />
<body   topmargin="0" leftmargin="5" marginheight="0" marginwidth="0">
<html:form action="/competencymodal/postseq_commodal/post_modal_tree">
<table align="left" width="800" border="0" cellpadding="0" cellspacing="0" class="mainbackground" style="position:absolute;left:0px;top:0px;">
<tr align="left"  class="toolbar" style="padding-left:2px;">
		<td valign="middle" align="left">

		<logic:equal value="1" name="postModalForm" property="ishistory">
			&nbsp;
			<img src="/images/view.gif" alt="历史时点" style="cursor:pointer" border="0" onclick='historyDate("${postModalForm.object_type}");'></img>
			</logic:equal>
			<hrms:priv func_id="36020101,36020201,36020301">
				<img src="/images/wjj_c.gif" alt="指标分类" style="cursor:pointer" border="0" onclick='editClass("${postModalForm.object_type}","${postModalForm.historyDate}");'></img> 
			</hrms:priv>                   

		</td>
	</tr>

<logic:equal value="3" name="postModalForm" property="object_type">
 <tr>
    <td align="left"> 
   <div id="treemenu"> 
   <SCRIPT LANGUAGE=javascript>    
               <bean:write name="postModalForm" property="treeItem" filter="false"/>
   </SCRIPT>
   </div>      
   <SCRIPT LANGUAGE="javascript">
       root.openURL();
    </SCRIPT>
    </td>
    </tr>
</logic:equal> 	 
<logic:notEqual value="3" name="postModalForm" property="object_type">           
         <tr>
           <td align="left"> 
   <div id="treemenu" ></div>
     <SCRIPT LANGUAGE="javascript">
       var m_sXMLFile="/pos/posbusiness/get_code_tree.jsp?validateflag=${postModalForm.ishistory}&backdate=${postModalForm.historyDate}&codesetid=${postModalForm.codesetid}&codeitemid=&action=/competencymodal/postseq_commodal/post_modal_list.do";	 //

       var root=new xtreeItem("root","${postModalForm.rootDesc}","/competencymodal/postseq_commodal/post_modal_list.do?b_query=link&a_code=<%=codesetid%>&codeitem=${postModalForm.codeitemid}&&codesetdesc="+$URL.encode('${postModalForm.rootDesc}'),"mil_body","${postModalForm.rootDesc}","/images/spread_all.gif",m_sXMLFile);
       root.setup(document.getElementById("treemenu"));
       root.openURL();
    </SCRIPT>
    </td>
    </tr>
    </logic:notEqual>
    </table>
</html:form> 
<BODY>