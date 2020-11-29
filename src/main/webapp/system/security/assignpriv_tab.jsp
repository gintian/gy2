<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.hjsj.sys.EncryptLockClient" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.hjsj.hrms.utils.PubFunc" %>
<%
	EncryptLockClient lockclient = (EncryptLockClient)session.getServletContext().getAttribute("lock");
  com.hjsj.hrms.actionform.sys.PrivForm form=(com.hjsj.hrms.actionform.sys.PrivForm)session.getAttribute("privForm");
  String viewflag=form.getViewflag();
  //用户管理授权 url加密  jingq add 2014.09.19
  String role_id = form.getRole_id();
  String user_flag = form.getUser_flag();
  String role_name = form.getRole_name();//选中用户名或角色名
//  form.setRole_name("");
  String userFlagText="1".equals(user_flag)?"操作角色：":"操作用户：";

  String funcstr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=funcpriv&a_flag="+user_flag+"&role_id="+role_id);
  String basestr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=dbpriv&a_flag="+user_flag+"&role_id="+role_id);
  String managestr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=managepriv&a_flag="+user_flag+"&role_id="+role_id);
  String busistr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=busipriv&a_flag="+user_flag+"&role_id="+role_id);
  String partystr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=partymanagepriv&a_flag="+user_flag+"&role_id="+role_id);
  String memberstr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=membermanagepriv&a_flag="+user_flag+"&role_id="+role_id);
  String tablestr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=tablepriv&a_flag="+user_flag+"&role_id="+role_id);
  String fieldstr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=fieldpriv&a_flag="+user_flag+"&role_id="+role_id);
  String mediastr = com.hjsj.hrms.utils.PubFunc.encrypt("current_tab=mediapriv&a_flag="+user_flag+"&role_id="+role_id);
  if(viewflag==null)
  	viewflag="0";
  pageContext.setAttribute("functionstr", funcstr);
  pageContext.setAttribute("basestr", basestr);
  pageContext.setAttribute("managestr", managestr);
  pageContext.setAttribute("busistr", busistr);
  pageContext.setAttribute("partystr", partystr);
  pageContext.setAttribute("memberstr", memberstr);
  pageContext.setAttribute("tablestr", tablestr);
  pageContext.setAttribute("fieldstr", fieldstr);
  pageContext.setAttribute("mediastr", mediastr);
%>
<script type="text/javascript" src="../../js/hjsjUrlEncode.js"></script>
<script language="JavaScript">
      /*资源分配*/
      function assign_res(flag,role_id)
      {
    	var target_url="/system/security/assign_resource.do?fromflag=3&flag="+flag+"&roleid="+$URL.encode(role_id)+"&role_name=<%=PubFunc.encrypt(role_name)%>";
    	window.open(target_url,'_self');        
      }     

</script>

<html:form action="/system/security/assignpriv_tab" style="height:96%;margin-left:-1px;margin-top:10px;">


<hrms:tabset name="sys_param" width="100%" height="90%" type="true">
      <hrms:tab function_id="3003801" name="param1" label="menu.function" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${functionstr }" >
      </hrms:tab> 
      <hrms:tab function_id="3003802" name="param2" label="menu.base" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${basestr }">
      </hrms:tab>  
      <%if(viewflag=="0") {%>      
      <hrms:tab function_id="3003803" name="param3" label="menu.manage" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${managestr }">
      </hrms:tab> 
      <%}%> 
      <logic:notEqual value="1" name="privForm" property="user_flag">
	      <hrms:tab function_id="3003807" name="param3" label="menu.manage.busi" visible="true" url="/system/security/assign_setfield.do?b_query=link&encryptParam=${busistr }">
	      </hrms:tab>
      </logic:notEqual>
      <%if(lockclient.isHaveBM(31)){ %>
      <hrms:tab function_id="30036" name="param7" label="menu.manage.party" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${partystr }">
      </hrms:tab>
      <hrms:tab function_id="30037" name="param8" label="menu.manage.member" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${memberstr }">
      </hrms:tab>  
      <%} %>      
      <hrms:tab function_id="3003804" name="param4" label="menu.table" visible="true" url="/system/security/assign_setfield.do?b_query=link&encryptParam=${tablestr }">
      </hrms:tab>    
      <hrms:tab function_id="3003805" name="param5" label="menu.field" visible="true" url="/system/security/assign_setfield.do?b_query=link&encryptParam=${fieldstr }">
      </hrms:tab>    
      <hrms:tab function_id="3003806" name="param6" label="menu.media" visible="true" url="/system/security/assign_func.do?b_query=link&encryptParam=${mediastr }">
      </hrms:tab>    
                                    
</hrms:tabset>
	<table cellpadding="0" cellspacing="0" style="padding-left:6px;width: 100%"><tr><td style="width: 100%;height: 35px">
	 	<logic:equal name="privForm" property="user_flag" value="0">
		<hrms:priv func_id="30034,0810">  	 	
    		<input style="float: left" type="button" name="b_resource" value="<bean:message key="button.resource.assign"/>" class="mybutton" onclick="assign_res('<bean:write name="privForm" property="user_flag" filter="true"/>','<bean:write name="privForm" property="role_id" filter="true"/>');">
        </hrms:priv>  
        </logic:equal>	 	
	 	<logic:equal name="privForm" property="user_flag" value="1">
         	    <hrms:submit style="float: left" styleClass="mybutton" property="br_return">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
	 	</logic:equal>
	 	<logic:equal name="privForm" property="user_flag" value="4">
         	    <hrms:submit style="float: left" styleClass="mybutton" property="br_return_login_user">
            		<bean:message key="button.return"/>
	 	    </hrms:submit>
	 	</logic:equal>
        <%if(StringUtils.isNotBlank(role_name)) {%>
        <div style="float: right; color: #5c6070;padding-right: 10px"><span style="margin-right: 5px"><%=userFlagText%></span><span><%=role_name%></span></div>
        <%}%>

    </td></tr></table>
</html:form>
<script>
if(!getBrowseVersion() || getBrowseVersion()==10){//处理非IE浏览器 样式问题  wangb 20190614 bug 46790
	var t1 = window.setInterval(function(){
		var _tabsetpane_sys_param = document.getElementById('_tabsetpane_sys_param');
		_tabsetpane_sys_param.style.height = parseInt(_tabsetpane_sys_param.style.height)-60;
		_tabsetpane_sys_param.style.width = document.body.clientWidth - 10;
		var tabsPanels = _tabsetpane_sys_param.getElementsByClassName('tabs-panels')[0];
		var tabsWrap = _tabsetpane_sys_param.getElementsByClassName('tabs-wrap')[0];
		var tabsHeader = _tabsetpane_sys_param.getElementsByClassName('tabs-header')[0];
		if(tabsPanels){
			window.clearInterval(t1);
			tabsPanels.style.height = parseInt(tabsPanels.style.height)-60;
			tabsPanels.style.width = document.body.clientWidth - 12;
			tabsHeader.style.width = document.body.clientWidth - 10;
			tabsWrap.style.width = document.body.clientWidth - 10;
			var panels = tabsPanels.children;
			for(var i = 0 ; i < panels.length ; i++){
				panels[i].style.height = parseInt(panels[i].style.height)-60;
				panels[i].firstChild.style.height = parseInt(panels[i].firstChild.style.height)-60;
				panels[i].style.width = document.body.clientWidth - 12;
				panels[i].firstChild.style.width = document.body.clientWidth - 12;
			}
		}
	},1000);
}

</script>