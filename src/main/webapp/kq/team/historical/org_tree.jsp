<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.hjsj.sys.EncryptLockClient"%>
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
	EncryptLockClient lock=(EncryptLockClient)pageContext.getServletContext().getAttribute("lock");
	int ver=lock.getVersion();
	ver=50;
%>
<script language="javascript">
function getRootid()
{
    var targetobj,hiddenobj;
    var currnode=Global.selectedItem;	
    if(currnode==null)
    	return;  
    var id = currnode.uid;     
    <%if(ver<=40){%>
      var currnode=parent.frames['b'];  
      if(currnode==null)
		return;
	  else{
	    if(id.indexOf("EP")==-1)
	   {
	      currnode.location.href="/kq/team/historical/group_tree.jsp?id="+id;
	   }
	 <%}%>    
	 <%if(ver>40){%>	 
       var currnode=parent.document.getElementById("a");       
       if(currnode==null)
		return;
	   else{
	    if(id.indexOf("EP")==-1)
	     {
	       currnode.src="/kq/team/historical/group_tree.jsp?id="+id;
	     }
	   }    
	 <%}%>    
}
</SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<link rel="stylesheet" href="<%=css_url%>" type="text/css">
<html:form action="/kq/team/array/search_array"> 
    <table width="100%" border="0" cellspacing="1"  align="center" cellpadding="1" >
       <tr>
           <td align="left"> 
            <div id="treemenu" onclick="getRootid()";> 
             <SCRIPT LANGUAGE=javascript>    
               <bean:write name="kqShiftForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>    
                  
    </table>
</html:form>


