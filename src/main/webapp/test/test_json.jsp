<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="net.sf.json.JSONArray" %>
<%@ page import="java.util.*" %>
<%@ page import="net.sf.json.JSONObject"%>
<%@page import="org.apache.commons.beanutils.DynaBean"%>
<%@page import="net.sf.json.xml.XMLSerializer"%>
<%@page import="com.hrms.test.Employee"%>



<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<%  
	String myname="cmq";
 	myname=request.getParameter("name");
	System.out.println("myname="+myname);
//Array ->JSON
boolean[] boolArray = new boolean[]{true,false,true};   
JSONArray jsonArray = JSONArray.fromObject( boolArray );   
String jscode= jsonArray.toString() ;
System.out.println( jsonArray.toString() );   
// List ->JSON
List list = new ArrayList();   
list.add( "first" );   
list.add( "second" );   
jsonArray = JSONArray.fromObject( list );   
System.out.println( jsonArray );   
//Map ->JSON
Map map = new HashMap();   
map.put( "name", "json" );   
map.put( "bool", Boolean.TRUE );   
map.put( "int", new Integer(1) );   
map.put( "arr", new String[]{"a","b"} );  
map.put( "func", "function(i){ return this.arr[i]; }" );   
  
JSONObject jsonObject = JSONObject.fromObject( map );   
System.out.println( jsonObject );   
//Beans ->JSON
jsonObject = JSONObject.fromObject( new Employee() );   
System.out.println( jsonObject );
//JSON->DynaBean
String json = "{name=\"json\",bool:true,int:1,double:2.2,func:function(a){ return a; },array:[1,2]}";   
jsonObject = JSONObject.fromObject( json );   
Object bean = JSONObject.toBean( jsonObject );   
DynaBean dybean=(DynaBean)bean;
System.out.println("--->"+dybean.get("name"));
//JSON->Bean
String emp="{age:0,name:\"jack\",sex:\"man\"}";
jsonObject = JSONObject.fromObject( emp );   
Employee employee = (Employee) JSONObject.toBean( jsonObject, Employee.class );  
System.out.println("--->"+employee.getName());
//JSON->XML
emp="{\"age\":0,\"name\":\"jack\",\"sex\":\"man\"}";
/*
try
{
	jsonObject = JSONObject.fromObject( emp );   
	XMLSerializer xser=new XMLSerializer();
	String xml = xser.write(jsonObject);
	System.out.println("xml-->"+xml);
}
catch(Exception exception)
{
	exception.printStackTrace();
}
*/
%>



<script type="text/javascript">
    function showAndroidToast(toast) {
        Android.showToast(toast);
      Android.dosomething();
    }
    
    function search(jsondata){
              var jsonobjs = eval(jsondata);
              var obj = document.getElementById("name");  
              obj.setAttribute("value",jsonobjs[0].name);
              //window.location.href='http://www.hjsoft.com.cn';
             // document.getElementById("myform").submit();
             document.getElementById("submitid").click();//提交表单
    }
</script>



</head>

<body>
	<form action="/test/test_json.jsp" id="myform"  method="post">
		<input type="text"  id="name" name="name"  value="i am cmq"  size="10">
		<input type="submit" id='submitid' name="submit" style="display: none" value="submit">		
	</form>
    <a href="http://www.hjsoft.com.cn">Link text</a> 
<input type="button" name="test" value="test" onclick="test();">
<input type="button" value="Say hello" onClick="showAndroidToast('Hello Android!')" />
</body>
</html>