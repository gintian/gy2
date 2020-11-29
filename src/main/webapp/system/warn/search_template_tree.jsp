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
	}
	String type=request.getParameter("type");
	type=type==null?"":type;
%>
<script type="text/javascript">  
	//xus 18/3/7 关闭窗口方法
  function closeWin(){
		if (parent.Ext && parent.Ext.getCmp("setTemplateWin")) {
			parent.Ext.getCmp("setTemplateWin").close();
		}else if(parent.Ext && parent.Ext.getCmp("search_org")){
			parent.Ext.getCmp("search_org").close();
		}else{
			window.close(); 
		}
  }
  function subid()
  {
     var currnode=Global.selectedItem; 
	 var thevo=new Object();		
	 thevo.content=root.getSelected();
	 thevo.title=root.getSelectedTitle();	
	 thevo.flag="true";
   //xus 18/3/6 解决chrome浏览器无法得到window.showModalDialog返回值的问题
   if(parent.Ext && parent.Ext.getCmp("setTemplateWin"))
    parent.setTemplateByChildwin(thevo);
   else if(parent.Ext && parent.Ext.getCmp("search_org"))
   	parent.Ext.getCmp("search_org").return_vo = thevo;
   else
   	window.returnValue=thevo;
     //window.close();
     closeWin();
  }
</script>
   <style type="text/css">
	#treemenu {  
	height: 360px;overflow:auto;
	/*border-style:inset ;
	border-width:2px*/
	}
   </style>   
<SCRIPT LANGUAGE=javascript src="/js/xtree.js"></SCRIPT> 
<hrms:themes></hrms:themes>
<html:form action="/system/warn/config_maintenance"> 
   <table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" >
		 	            
         <tr>
           <td align="left"> 
            <div id="treemenu" style="width:290px;height:300px;" class="complex_border_color"> 
             <SCRIPT LANGUAGE=javascript>    
               Global.defaultInput=1; 
               <%--接口调整了树层级，前台设置层级未调整导致出不来复选框 bug 55289 wangb 20191114 --%>
               <%if(type.length()<1){%> 
               		Global.defaultchecklevel=3;  
               <%}else{%>
               		Global.defaultchecklevel=2;  
               <%}%>   
               Global.checkvalue=",${warnConfigForm.select_id}";      
               <bean:write name="warnConfigForm" property="bs_tree" filter="false"/>
             </SCRIPT>
             </div>             
           </td>
           </tr>   
           <tr>
            <td align="center" colspan="2" height="35px;">
         	<html:button styleClass="mybutton" property="b_save" onclick="subid();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="closeWin();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td>         
         </tr>          
    </table>  
    
<script type="text/javascript">
	//root.expandAll();
	root.expand2level();
	if(getBrowseVersion() || !getBrowseVersion()){// 非ie11兼容浏览器样式 修改   wangb 20190321
		var treemenu = document.getElementById('treemenu');
		treemenu.style.whiteSpace = 'nowrap';
	}
</script>
</html:form>
