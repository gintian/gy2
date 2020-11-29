<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
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
	String chitemid=request.getParameter("chitemid");
	chitemid=chitemid!=null&&chitemid.trim().length()>0?chitemid:"";

	String orgcode=request.getParameter("orgcode");
	orgcode=orgcode!=null&&orgcode.trim().length()>0?orgcode:"";
		
	String dbpre=request.getParameter("dbpre");;
	dbpre=dbpre!=null&&dbpre.trim().length()>0?dbpre:"";
	String showroot=request.getParameter("showroot");
	showroot=showroot!=null&&showroot.trim().length()>0?showroot:"false";
	String nmodule = request.getParameter("nmodule");
	nmodule = nmodule==null?"":nmodule;
	String privtype = request.getParameter("privtype");
	privtype = privtype==null?"":privtype;
	String viewunit="0";
	if(request.getParameter("viewunit")!=null)
	   viewunit=request.getParameter("viewunit");
	
	String orgId=request.getParameter("orgId");
	orgId = orgId != null && orgId.trim().length() > 0 ? orgId : "";
	
	String orgTitle = request.getParameter("orgTitle");
	orgTitle = orgTitle != null && orgTitle.trim().length() > 0 ? orgTitle : "";
	String callbackFunc = request.getParameter("callbackFunc");
%>
<script LANGUAGE=javascript src="/js/xtree.js"></script> 
<hrms:themes></hrms:themes>
<link href="/css/xtree.css" rel="stylesheet" type="text/css" >
<script type="text/javascript">
<!--
	var obj = window.dialogArguments;
	if(obj){// 如果是非ie浏览器则obj为空，obj.orgIdList会报错 chent 20180330 update
		Global.checkvalue = obj.orgIdList;
	}
	
	var orgId = "<%=orgId %>";
	var orgTitle = "<%=orgTitle %>";
	function closeWin(){
		// 带有Microsoft标识判断是否是ie浏览器不准确，也有可能是Edge。chent 20180226 update
		// if(navigator.appName.indexOf("Microsoft")!= -1){ 
		if(window.showModalDialog){
			// ie下关闭窗口无需返回值 chent 20180226 delete 
			// window.returnValue=thevo; 
			parent.window.close();
		} else {
		    //为了严谨  加判断  wangbs 20190315
		    if(parent.parent.Ext){
                parent.parent.Ext.getCmp("select_org_dialog1_win").close();
			}
    	}
		
	}
	function getemploy() {
		var thevo=new Object();
		<logic:equal name="logonUserForm" property="selecttype" value="1">
		addOrRemoveCode();
		thevo.content = orgId;
		thevo.title = orgTitle;
		</logic:equal>
		
		<logic:notEqual name="logonUserForm" property="selecttype" value="1">
		thevo.content = root.getSelected();
		thevo.title = root.getSelectedTitle();
		</logic:notEqual>
		if(!thevo.content){
			if(window.showModalDialog){
				alert('<bean:message key="error.unit.please"/>');
			} else if(parent.parent.Ext){
				parent.parent.Ext.showAlert('<bean:message key="error.unit.please"/>');
	    	}
			return;
		}
		if(getBrowseVersion()){//IE
            parent.window.returnValue=thevo;
		}else{
            <%if(callbackFunc!=null){%>
            	eval(parent.parent.<%=callbackFunc%>)(thevo);
            <%}else{%>
				//该页面被多处共用 请特殊化回调方法 尽量避免冲突  wangbs 20190315
				if(parent.parent.copySelectOrgWinReturn){
                    parent.parent.copySelectOrgWinReturn(thevo);
				}else if(parent.parent.copySelectOrgWinReturn1){
                    parent.parent.copySelectOrgWinReturn1(thevo);
				}
            <%}%>
		}
		closeWin();
	}

	function addOrRemoveCode(){
		var hiddenValue = orgId.split(",");
		var hiddenTitle = orgTitle.split(",");
		var removeCodeValues = Global.removeCodeValues;
		orgId = "";
		orgTitle = "";
		
		for(var i = 0; i < hiddenValue.length;i++){
			if(!hiddenValue[i] || !hiddenTitle[i])
				continue;
	
			if(removeCodeValues.indexOf("," + hiddenValue[i] + ",") < 0){
				orgId += hiddenValue[i] + ",";
				orgTitle += hiddenTitle[i] + ",";
			}
		}

		var selectValue = root.getSelected();
		var selectTitle = root.getSelectedTitle();
		
		var selectValues = selectValue.split(",");
		var selectTitles = selectTitle.split(",");
		for(var i = 0; i < selectValues.length; i++){
			if(!selectValues[i])
				continue;

			if(!orgId || ("," + orgId).indexOf("," + selectValues[i] + ",") < 0){
				orgId +=  selectValues[i] + ",";
				orgTitle += selectTitles[i] + ",";
			}
		}
	}
	
	function bClear() {
		if (window.confirm('<bean:message key="button.affirm"/><bean:message key="system.sms.alle"/>?')) {
			Global.checkvalue = ",";
			var root = Global.selectedItem;
			while (root.uid != "root") {
				root = root.root();
			}
			root.allClear();
			
			orgId = "";
			orgTitle = "";
		} else {
			return false;
		}
	}
//-->
</script>

<html:form action="/system/logonuser/org_tree"> 
<%  if(StringUtils.isNotEmpty(orgId)) {
		orgId = "," + orgId;
    } %>
<hrms:orgtree privtype="<%=privtype %>" flag="${logonUserForm.flag}" loadtype="${logonUserForm.loadtype}" 
                 showroot="false" selecttype="${logonUserForm.selecttype}" 
                 dbtype="${logonUserForm.dbtype}" priv="${logonUserForm.priv}" 
                 isfilter="${logonUserForm.isfilter}" chitemid="<%=chitemid%>" orgcode="<%=orgcode%>"
                 	checkvalue="<%=orgId %>"
                  dbpre="<%=dbpre%>" nmodule="<%=nmodule %>" viewunit="<%=viewunit%>" divStyle="height: 320px; width: 300px; overflow: auto;"/>
   

<table align="center" style="margin-top:15px">
  <tr>
     <td align="center">
        <html:button styleClass="mybutton" property="b_save" onclick="getemploy();">
            		<bean:message key="button.ok"/>
	 	    </html:button>
	 	    <html:button styleClass="mybutton" property="" onclick="bClear();">
            		<bean:message key="system.sms.alle"/>
	 	    </html:button>
         	<html:button styleClass="mybutton" property="br_return" onclick="closeWin();">
            		<bean:message key="button.close"/>
	 	    </html:button> 
     </td>
  </tr>
</table>

</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion()==10){
		var treemenu = document.getElementById('treemenu');
		treemenu.style.overflow = 'auto';
	}
</script>