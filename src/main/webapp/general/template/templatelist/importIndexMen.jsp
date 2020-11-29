<%@ page contentType="text/html; charset=UTF-8"%>
 <%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<html>
  <head>
    

  </head>
  <link href="/css/css1.css" rel="stylesheet" type="text/css">
  <script language='javascript' >
  function sub(flag)
  {
  
  	returnValue=flag;
	window.close();	
  }  
  
  </script>
  <hrms:themes></hrms:themes>
  <body>
 	<table width='90%' align='center' >
 		<tr><td>
 		<br>
 		模板中有未处理人员,是否清空模板并按模板的检索条件选人?
 		</td></tr>
 		<tr><td>
 		<br>是:清空当前模板的人员,重新按检索条件选人
		<br>否:不清空当前模板的人员,增加符合检索条件的人
		<br>取消:什么操作也不做,不清空不使用检索条件
 		</td></tr>
 		<tr><td align='center'>
 		<br><br>
 		   <input type="button" name="button1"   onclick="sub('1')" value="&nbsp;&nbsp;是&nbsp;&nbsp;" class="mybutton" />
 		  
 			<input type="button" name="button1" onclick="sub('2')"  value="&nbsp;&nbsp;否&nbsp;&nbsp;" class="mybutton" />
 				 
 			<input type="button" name="button1" onclick="sub('0')"  value="&nbsp;取消&nbsp;" class="mybutton" />
 		</td></tr>
 		
 	</table>
  </body>
</html>
