<%@page import="java.util.Date"%>
<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
if(userView != null){
	userView.getHm().put("fckeditorAccessTime", new Date().getTime());
}
%>
<head>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<link  rel="stylesheet" type="text/css"  href="/module/recruitment/css/newParameterSet.css"/>
<script type="text/javascript">
<!--
function fillout(){
   var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
   var oldInputs = document.getElementsByName("licenseAgreement");
   oldInputs[0].value = oEditor.GetXHTML(true);
   var len=0;
   if(trim(oldInputs[0].value).length>0)
      len=1;
  parameterForm2.action="/hire/parameterSet/configureParameter/licenseAgreement.do?b_save=save&opt=2&len="+len;
  parameterForm2.target="_self";
  parameterForm2.submit();
 
}
 <%if(request.getParameter("opt")!=null&&request.getParameter("opt").equals("2")){
      	
        out.print("var obj = new Object();\r\n");
        if(request.getParameter("len")!=null&&request.getParameter("len").equals("1"))
       {
         out.print("obj.len='1';\r\n");
       }
       else
       {
         out.print("obj.len='0';\r\n");
       }
       out.print("returnValue=obj;\r\n");
        out.print("judgeBro(returnValue);\r\n");
   }

%>
function clo()
{
 var obj=new Object();
 obj.len='2';
 returnValue=obj;
 
 judgeBro(returnValue);
}
function judgeBro(returnValue){
	if(window.parent.parent.me){
		window.parent.parent.me.setCallBack({returnValue:returnValue});
	    window.parent.parent.Ext.getCmp('window').close();
	}else
		window.close();
}
//-->
</script>
 <style type="text/css">
.RecordRow_top {
	border: inset 1px #94B6E6;	
	BORDER-BOTTOM: #94B6E6 0pt solid; 
	BORDER-LEFT: #94B6E6 0pt solid; 
	BORDER-RIGHT: #94B6E6 0pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
}	     
    </style>
    </head>
 <base id="mybase" target="_self">
<body>
<html:form action="/hire/parameterSet/configureParameter/licenseAgreement">
<div  style="overflow: visible">
 <table width="90%" border="0" cellpadding="0" cellspacing="0" align="center"><%--
 <tr>
 <td align="center">
 <font size="5"><strong>
 <logic:equal value="l" property="l_p_type" name="parameterForm2">
 <bean:message key="hire.agreement.hirepermit"/>
 </logic:equal>
 <logic:equal value="p" property="l_p_type" name="parameterForm2">
 <bean:message key="hire.lable.promptcontent"/>
 </logic:equal>
 </strong></font>
 </td>
 </tr>
  --%><tr>
           <td class='RecordRow' style="height:100%; padding-bottom:5px;padding-top:5px;">
                      <html:textarea name="parameterForm2" property="licenseAgreement" cols="80" rows="580" style="display:none"/>
                      <script type="text/javascript">
                      			  var bodyHeight = document.body.clientHeight;//获取当前页面的可用高度
                      			  var temHeight = 475;
                      			  //屏幕分辨率
							     	var screenHeight =  window.screen.height;
							     	var screenWidth = window.screen.width;
							     	//处理屏幕分辨率过小时页面显示不全问题
							     	if(screenHeight * screenWidth <= 1280*768)
								     	temHeight = bodyHeight - 78;
					              var oldInputs = document.getElementsByName('licenseAgreement'); 
					                                         
					              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;

					              oFCKeditor.BasePath	= '/fckeditor/';
                                  oFCKeditor.Height	= temHeight ;			
					              oFCKeditor.Width	=720;			            
					              oFCKeditor.ToolbarSet='Middle';
					              oFCKeditor.Value	= oldInputs[0].value;
					              oFCKeditor.Create() ;
					            
                       </script>
 		
 					</td>  
          </tr>
          <tr>
          <td align="center" style="padding-top:5px;" >
          <input type="button" class="mybutton" name="sav" onclick="fillout();" value="<bean:message key="button.save"/>"/>
          <input type="button" class="mybutton" name="clos" onclick="clo();" value="<bean:message key="button.close"/>"/>
          </td>
          </tr>
 </table>
 <html:hidden name="parameterForm2" property="l_p_type"/>
 </div>
</html:form>
</body>