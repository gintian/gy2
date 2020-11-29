<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script type="text/javascript">
<!--
  function test()
  {
  	var obj=$('printview');
  	var name=obj.HelloWorld("chenmengqing");
  	alert(name);
  }
//-->
</script>
<html:form action="/general/template/print_template">
   <jsp:plugin  type="applet" name="printview" archive="hj_client.jar,struts_extends.jar,hessian-3.0.20.jar,command.jar,rowset.jar" code="com.hjsj.hrms.client.print.PrintTemplateApplet.class"  width="475"  height="350" 
   codebase="/client">  
       <jsp:params>  
           <jsp:param  name="MESSAGE"  value="Your  Message  Here"  />  
           </jsp:params>  
           <jsp:fallback>
                 <p>Unable to start plugin.</p>
           </jsp:fallback>	           
   </jsp:plugin>
	<input type="button" name="aaa" value="aaa" onclick="test();">
</html:form>
