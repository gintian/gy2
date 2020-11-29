<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
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
%>
<SCRIPT LANGUAGE="javascript" src="/js/xtree.js"></SCRIPT>
<link href="/css/xtree.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
<!--
	function savecode() {
		var thevo=new Object();
		thevo.content=root.getSelected();
		thevo.title=root.getSelectedTitle();
         // window.returnValue=thevo;
     	// window.close();
        parent.return_vo = thevo;
        if(parent.Ext.getCmp('customreport')){
            parent.Ext.getCmp('customreport').close();
        }
    }
//-->
</script> 
<hrms:themes></hrms:themes> 
<html:form action="/general/muster/hmuster/searchHroster">
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >          
         <tr>
           <td align="left">  
            <div id="treemenu" style="width:470px;height: 380px;overflow:auto;" class="complex_border_color"> 
             <SCRIPT LANGUAGE=javascript>  
             	Global.defaultInput=2;  
               	Global.defaultradiolevel=1; 
               	Global.showroot = false;        	                 
               <bean:write name="customReportForm" property="treeCode" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>
           <tr>
           	<td align="center" height="35px;">
    <%--<input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();window.close();">--%>
    <input type="button" name="btnok" value="确定" class="mybutton" onclick="savecode();">
    <input type="button" name="btncancel" value="取消" class="mybutton" onclick="parent.winClose();">
    <%--<input type="button" name="btncancel" value="取消" class="mybutton" onclick="window.close();">--%>
           	</td>
           </tr>           
    </table>
         
</html:form>
