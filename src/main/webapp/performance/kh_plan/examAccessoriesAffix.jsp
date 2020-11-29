<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
 <%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.hjsj.sys.ResourceFactory"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ taglib uri="/WEB-INF/tlds/FCKeditor.tld" prefix="fck"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String str= request.getParameter("plan_id");

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
      <style type="text/css">
          .text6_{
              border: 1px solid #C4D8EE !important;
          }
      </style>
<script>
/**author:zangxj *day2:2014-06-07 *模板文件上传  */
var srtplanId = <%=str%>
function up(){
		if(document.getElementById('file').value==''){
		   alert('请选择文件');
		   return false;
		}
		var path = document.getElementById('file').value;
		var name = getFileName(path);
		if(name.length>20){
			alert("文件名长度不能超过20！");
			return;
		}		
        singleGradeForm.action="/selfservice/performance/selfGrade.do?b_query3=link&plan_id=${param.plan_id}&opt=up";
		singleGradeForm.submit();
		//防止上传漏洞 xus 19/12/23 【55971 】 V76绩效管理：谷歌，考核计划，参数中上传报告，文件类型不对也提示上传成功，实际没有上传
		//alert("上传成功");
		//location.href='/selfservice/performance/selfGrade.do?b_query3=link&plan_id='+srtplanId
}
function getFileName(path){
	var pos1 = path.lastIndexOf('/');
	var pos2 = path.lastIndexOf('\\');
	var pos  = Math.max(pos1, pos2)
	if( pos<0 )
		return path;
	else
		return path.substring(pos+1,path.lastIndexOf("."));
}
/**author:zangxj *day2:2014-06-07 *模板文件删除  */
function del(){
	var palnid = <%=str%>;
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","del");
	hashvo.setValue("plan_id",palnid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showdelFieldList,functionId:'90100160022'},hashvo);
}	
function showdelFieldList(outparamters){
	var isnull=outparamters.getValue("isnull");
		if(isnull=='have'){
			alert("删除成功");
		}
		else{
			alert("没有上传模板");
				return;
		}
		location.replace('/selfservice/performance/selfGrade.do?b_query3=link&plan_id='+srtplanId)  
}

/**author:zangxj *day2:2014-06-07 *模板文件下载  */
function down(){
	var palnid = <%=str%>;
	var hashvo=new ParameterSet();
	hashvo.setValue("opt","down");
	hashvo.setValue("plan_id",palnid);
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'90100160022'},hashvo);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	var isnull=outparamters.getValue("isnull");
	if(isnull=="null"){
		alert("没有上传模板");
			return;
		}
	outName = decode(outName);
	if(outName!=null&&outName.length>1){
		window.location.href="/servlet/vfsservlet?fromjavafolder=true&fileid="+outName;
	}
}
function closewindow()
{
    if(window.showModalDialog) {
        parent.window.close();
    }else{
        window.open("about:blank","_top").close();
    }
}
</script>
    <base href="<%=basePath%>">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
  </head>
  <body>
		<form name="singleGradeForm" method="post"
			action="/selfservice/performance/selfGrade" enctype="multipart/form-data">
			<br>
			 <fieldset align="center" style="width:90%;">
        <legend ><bean:message key='jx.import.selectfile'/>(注意：文件名长度不能超过20)</legend>
        <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr > 
            <td align="center"><bean:message key='jx.import.file'/>
              <input type="file"  id="file" name="file" class="text6_" >
              </td>
          </tr>	  
          <tr>
          		<td align="center">
				<bean:message key='hire.jp.personinfo.template'/><bean:message key='conlumn.mediainfo.info_sort'/>:${singleGradeForm.filenametemplet}
          		</td>
          </tr>
        </table>
	</fieldset>
	<table width="90%" align="center">
		     <tr > 
            <td> </td>
          </tr>	
		  <tr> 
            <td align="center"> <input type="button" name="b_query3" value='上传' class="mybutton" onClick="up()">
						<!-- author:zangxj *day2:2014-06-07 *绩效模板文件控制按钮   -->
						<html:button styleClass="mybutton" property="b_query3" onclick="down()">
		            		<bean:message key='conlumn.resource_list.down'/>
						</html:button>
						<html:button styleClass="mybutton" property="b_query3" onclick="del()">
		            		<bean:message key='lable.tz_template.delete'/>
						</html:button>
						<html:button styleClass="mybutton" property="b_query4" onclick="closewindow()">
		            		<bean:message key='button.close'/>
						</html:button>
               </td>
          </tr>
	</table>
			</form>
  </body>

</html>
<script>



</script>