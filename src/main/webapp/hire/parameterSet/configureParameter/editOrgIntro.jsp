
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
<html:html>

<head>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
<script type="text/javascript">
<!--
function fillout(){
  if(parameterForm2.contentTypeValue.value=='0'){
}
if(parameterForm2.contentTypeValue.value=='1')
{
   var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
   var oldInputs = document.getElementsByName("content");
   var html = oEditor.GetXHTML(true);
   // FCKeditor添加图片生成的标签为<input src="xxxx" ... type="image" />格式，查看是点击此图片会自动提交form。替换成<img>标签
   while(html.indexOf("<input src=")!=-1){
	   var inputStr = html.substring(html.indexOf('<input src='),html.indexOf('type="image" />')+15);
	   var newImgStr = inputStr.replace("input","img").replace('type="image"','');
	   html = html.replace(inputStr,newImgStr);
   }
   oldInputs[0].value = html;
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
  if(ss=='2')
  {
      var obj= new Object();
      obj.ss=ss;
      parent.window.returnValue=obj;
      
     /* window.parent.me.setCallBack({returnValue:returnValue});
 	  window.parent.Ext.getCmp('window').close();
      window.close();*/
      if(window.parent.window.opener){//非ie浏览器
			window.parent.window.opener.setEditValue(obj);
			window.parent.close();
			return;
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
      
      
      <logic:equal name="parameterForm2" property="type" value="0">
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
   <logic:equal name="parameterForm2" property="type" value="1">
    <table width="790" border="0" cellpadding="0" cellspacing="0" align="center" style="margin-top:10px;">
      <tr height="20">
       		<td  class="TableRow"><bean:message key="hire.web.content"/></td>  
       		</tr>
           <tr><td class="framestyle9" align="center" nowrap>
           <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable">
           <tr>
           <td>
                      <html:textarea name="parameterForm2" property="content" cols="100" rows="600" style="display:none"/>
                      <script type="text/javascript">
					          <!--//非ie兼容下报错，但是不影响使用  wangb 20190329
                              var oldInputs=document.getElementsByName('content');
                              var oFCKeditor = new FCKeditor( 'FCKeditor1' ) ;
                              oFCKeditor.BasePath	= '/fckeditor/';
                              oFCKeditor.Height	= 620 ;			
					          oFCKeditor.Width	=790;
                              oFCKeditor.ToolbarSet='Apply';
                              oFCKeditor.Value	= oldInputs[0].value;
                              oFCKeditor.Create() ;
                              //-->
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
            </td><html:hidden name="parameterForm2" property="codeitemid"/>
          </tr>          
      </table>
      <html:hidden name="parameterForm2" property="contentTypeValue"/>
</html:form>
</body>
</html:html>
<script type="text/javascript">
<!--
 isClose("${parameterForm2.isClose}");
//-->
if(getBrowseVersion() == 10){//ie11非兼容视图 样式 修改 wangb 20190323 
	var childFrame = parent.document.getElementById('childFrame');
	if(childFrame){
		parent.document.body.style.overflow='hidden';
	}
}
</script>
