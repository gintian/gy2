<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/validate.js"></script>
<script type="text/javascript" src="../../../ext/ext-all.js" ></script>
<%@ page import="com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm"%>
<%@page import="com.hrms.struts.constant.SystemConfig,com.hrms.frame.codec.SafeCode"%>
<%
    String iswindow = request.getParameter("iswindow");
    ParameterForm parameterForm =(ParameterForm)session.getAttribute("parameterForm2");
    String path=parameterForm.getPath();
     if(SystemConfig.getPropertyValue("webserver").equals("weblogic"))
       {
      	  path=session.getServletContext().getResource("/UserFiles").getPath();//.substring(0);
          if(path.indexOf(':')!=-1)
      	  {
	    	 path=path.substring(1);   
      	  }
     	  else
      	  {
	    	 path=path.substring(0);      
      	  }
          int nlen=path.length();
    	  StringBuffer buf=new StringBuffer();
     	  buf.append(path);
  	      buf.setLength(nlen-1);
   	      path=buf.toString();
   	      path=SafeCode.encode(path);
      }
 %>
 <script language="JavaScript" src="/js/constant.js"></script>
<script type="text/javascript" src="/general/sys/hjaxmanage.js"></script>
<script type="text/javascript">
function isIE() { //ie?  
    if (!!window.ActiveXObject || "ActiveXObject" in window)  
        return true;  
    else  
        return false;  
 }
<!--
var value = new Object();
function ret()
{
   var dir=parameterForm2.r_file.value;
   if(trim(dir).length==0)
   {
       alert(SELECT_BACKUP_FILE+"!");
       return;
   }
   if(!validateUploadFilePath(dir))
   {
		//alert("请选择正确的文件！");
		return;
   }
   var index=dir.lastIndexOf(".");
   if(dir.substring(index)!=".zip")
   {
       alert(FILE_DAT_IS_ZIP+"!");
       return;
   }
   //验证文件大小
	var fileSize = 0;          
	if (isIE && !dir.files) {  
		try {
	        var fileSystem = new ActiveXObject("Scripting.FileSystemObject");         
	        var file = fileSystem.GetFile (dir.value);  
	        fileSize = file.Size;
		} catch(e) {
			fileSize = 1024;
		}
	} else {     
	   fileSize = dir.files[0].size;      
	} 
		  
	var size = fileSize / 1024;
	if(size>100*1024){
	    alert(TRAIN_RESOURCE_FILE_SIZE);
		dir.outerHTML=dir.outerHTML;
		return;
	}
  value.dir = dir;
  if(window.parent.parent.me)
  	window.parent.parent.me.setCallBack({returnValue:value});
  else
	  window.parent.objlist.dir = dir;
  parameterForm2.action="/hire/parameterSet/configureParameter/select_reduction_file.do?b_reduction=reduction&isclose=2";
  parameterForm2.submit();
}
function clo()
{
   <% if(request.getParameter("isclose")!=null&&request.getParameter("isclose").equals("2")){%>
   		windowClose();
   <%}%>
}
function windowClose(){
	/**
	*许硕 取消按钮无法关闭
	*16/09/20
	**/
	//判断是否为window
	var iswindow=<%=iswindow%>;
	if(iswindow){
		window.top.close();
	}else{
		if(window.parent.parent.Ext && window.parent.parent.Ext.getCmp('huanyuan'))//系统管理 单位介绍  还原按钮 弹窗 关闭 wangb 20190522 bug 48173
			window.parent.parent.Ext.getCmp('huanyuan').close();
		else if(window.parent.parent.me)
		 	window.parent.parent.Ext.getCmp('window1').close();
		else{
	  		window.parent.Reduction_return();
	  		window.parent.closejinduo(2);
	  		window.parent.Ext.getCmp('window').close();
	   	}
	}
}
//-->
</script>

<html:form  method="post" action="/hire/parameterSet/configureParameter/select_reduction_file" enctype="multipart/form-data">
<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0">
<tr>
<td align="center" nowrap>
<fieldset style="width:340px;padding-top:8px;height:80px;">
<legend><bean:message key="hire.selectfile.backup"/></legend>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
<tr>
<td valign="middle" align="center" height="50px;">
<html:file name="parameterForm2" property="r_file" size="30" styleClass="complex_border_color"/>
<input type="hidden" name="path" value="<%=path%>"/>
</td>
</tr>
</table>
</fieldset>
</td>
</tr>
<tr>
<td align="center" nowrap height="35px;">
<input type="button" name="ok" value="<bean:message key="button.ok"/>" onclick="ret();" class="mybutton"/>
<input type="button" name="cancel" value="<bean:message key="button.cancel"/>" onclick="windowClose()" class="mybutton"/>
</td>
</tr>
</table>
<script type="text/javascript">
<!--
clo();
//-->
</script>
</html:form>
