<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
    
%>  
<html:html>
<head>
<script type="text/javascript">

function changeContent(){
	var zr=document.getElementsByName('zpReport')[0];
	var zrvalue=zr.value;
	//channelContentDetailForm.action="/hire/parameterSet/zpReport.do?b_change=change&zpReport="+zrvalue;
	//channelContentDetailForm.submit();
	var cont = "";
	if(zrvalue.indexOf(':') != -1) {//因为value是代码值不存在：的，如果是就说明是从35代码类型取得
		cont = zrvalue.substring(1);
		type = "1";
	}else if(zrvalue == 0){
		cont = 2;
		type = "0";
	}else if(zrvalue == 13) {
		cont = 13;
		type = "0";
	}

	//猎头招聘的公告; 猎头招聘暂不使用
	//if(zrvalue==4){
	//	cont = 5;
	//}
	//if(zrvalue==13){
	//	cont = 13;
	//}
	window.main.location="/selfservice/infomanager/board/searchboard.do?b_query=link&opt=2&announce="+cont+"&type="+type;
}
function saveContent(){
	var zr=document.getElementById('zpReport');
	var zrvalue=zr.value;
	var zrc=document.getElementById('zpReportContent');
	var oEditor = FCKeditorAPI.GetInstance('FCKeditor1');
    var oldInputs = document.getElementsByName("zpReportContent");
  	oldInputs[0].value = oEditor.GetXHTML(true);
  	
	//document.channelContentDetailForm.value=zrc.value;
	channelContentDetailForm.action="/hire/parameterSet/zpReport.do?b_save=save&zpReport="+zrvalue;
	channelContentDetailForm.submit();
}
//-->
</script>
<script type="text/javascript" src="/fckeditor/fckeditor.js"></script>
</head>
<body>

<html:form action="/sys/cms/addContentChannelDetail">
<%
    if(bosflag!=null&&!bosflag.equals("hcm")){
%>
<Br>
<%
}
%>
       <table width="98%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr>
            <td class="RecordRow_lrt">
               <table border="0" cellspacing="3"  cellpadding="0">     
                      <tr class="list3">
                          <td align="right" nowrap>公告类型:</td>
                          <td align="left" nowrap>
                          <hrms:optioncollection name="channelContentDetailForm" property="zpReportList" collection="list" />
							 <html:select name="channelContentDetailForm" property="zpReport" size="1" style="width:150px;" onchange="changeContent();">
					             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
					        </html:select>
		        		   </td>
                      </tr>               
                </table>
              </td>      
           </tr>          
          <tr height="20">
       		<td  align=center class="TableRow" id="topic">&nbsp;公告内容&nbsp;</td>
          </tr> 
          <tr height="20">
       		<td  align=left  class="RecordRow" id="topic" style="border-top:0; margin:0;height:610px;">
	       		<div id='a'  style="display:block"  >
	       		<iframe src="/selfservice/infomanager/board/searchboard.do?b_query=link&opt=2&announce=2&type=0" style='margin-bottom:0px;'width="100%" height="610" scrolling="auto" frameborder="0" name="main"></iframe>
	       		</div>
       		</td>
          </tr> 
                   
      </table>
</html:form>
</body>
</html:html>