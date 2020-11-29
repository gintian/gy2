
<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@page import="com.hjsj.hrms.actionform.hire.parameterSet.ParameterForm"%>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<html:html>

<head>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script type="text/javascript">
function fillout(){
	if(parameterForm2.contentTypeValue.value=='0'){
	}
	if(parameterForm2.contentTypeValue.value=='1')
	{
		var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
		var oldInputs = document.getElementsByName("content");
		oldInputs[0].value = oEditor.GetXHTML(true);
	}
	parameterForm2.action="/hire/parameterSet/configureParameter/saveOrgBriefContent.do?b_save=save";
	parameterForm2.submit();
}
function displayTR(){
	var url = document.getElementById("1");
	var content=document.getElementById("2");
	var type=document.getElementsByName("contentTypeValue");
	
	if(type[0].value == 0){
		url.style.display="block";
		content.style.display="none";
	}
	if(type[0].value == 1){
		url.style.display="none";
		content.style.display="block";
	}
}
function isClose(ss)
{
	<%ParameterForm parameterForm2 = (ParameterForm)session.getAttribute("parameterForm2");
	parameterForm2.setIsClose("1");
	%>
  if(ss=='2')
  {
      var obj= new Object();
      obj.ss=ss;
      window.returnValue=obj;
      
     /* window.parent.me.setCallBack({returnValue:returnValue});
 	  window.parent.Ext.getCmp('window').close();
      window.close();*/
      if(window.parent.window.opener){//非ie浏览器
			window.parent.window.opener.setEditValue(returnValue);
			window.parent.close();
    	}
    window.close();
  }
}
//-->
</script>
</head>
 <base id="mybase" target="_self">
<body>
<html:form action="/hire/parameterSet/configureParameter/saveOrgBriefContent">
      
      
      <logic:equal name="moudleParameterForm" property="type" value="0">
       <table width="530" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;border-collapse:collapse;">
      <tr height="20">
       		<td  class="TableRow">
       		 <bean:message key="hire.web.adress"/></td>  
       		</tr>
       		<tr><td class="framestyle" align="center" nowrap>
       		<table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
       		<tr height="100px">
            
            <td align="center"><html:text name="parameterForm2" property="url" style="width:400px" styleClass="text4"/></td>

                      </tr> 
                      
                  </table> 
                   
                      
                      </td>
                      </tr>
                      </table>
                   </logic:equal>
   <logic:equal name="moudleParameterForm" property="type" value="1">
    <table width="790" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
      <tr height="20">
       		<td  class="TableRow"><bean:message key="hire.web.content"/></td>  
       		</tr>
           <tr><td class="framestyle9" align="center" nowrap>
           <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
           <tr>
           <td>
                      <html:textarea name="moudleParameterForm" property="content" cols="100" rows="600" style="display:none"/>
                      <script type="text/javascript">
					              var oldInputs = document.getElementsByName('content'); 
					                                         
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
					             
					              oFCKeditor.BasePath	= '/fckeditor/';
                                  oFCKeditor.Height	= 620 ;			
					              oFCKeditor.Width	=790;			            
					              oFCKeditor.ToolbarSet='Middle';
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
                       </script>
 		
 					</td>  
          </tr>
          </table>
          </td>
          </tr>
          </table>
                </logic:equal>   
                 <table width="80%" border="0" cellpadding="0" cellspacing="0" align="center">                                  
          <tr class="list3">
            <td align="center" colspan="4" height="35px;">
         	<html:button styleClass="mybutton" property="save" onclick="fillout();">
            		<bean:message key="button.save"/>
	 	    </html:button>
	       	<html:button styleClass="mybutton" property="br_return" onclick="javascript:window.parent.close();">
            		<bean:message key="button.close"/>
	 	    </html:button>            
            </td><html:hidden name="moudleParameterForm" property="codeitemid"/>
          </tr>          
      </table>
      <html:hidden name="moudleParameterForm" property="contentTypeValue"/>
</html:form>
</body>
</html:html>
<script type="text/javascript">
 isClose("${parameterForm2.isClose}");
</script>
