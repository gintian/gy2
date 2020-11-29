<%@ page contentType="text/html; charset=UTF-8"%>
<html>
<body onload="toLoad()">
<!-- 
   <hrms:tabset name="pageset" width="100%" height="90%" type="true" align="center"> 
	  <hrms:tab name="tab1" label="menu.base" visible="true" url="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=dbpriv">
      </hrms:tab>	
	  <hrms:tab name="tab2" label="menu.table" visible="true" url="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=tablepriv">
      </hrms:tab>	
	  <hrms:tab name="tab3" label="menu.field" visible="true" url="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=fieldpriv">
      </hrms:tab>	
	</hrms:tabset>
 -->
 </body>
</html>
<script language='JavaScript'>
	function toLoad(){
		//window.location="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=dbpriv";
		window.location="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=tablepriv";
		//window.location="/recruitment/baseoptions/basesetfield.do?b_query=link&a_tab=fieldpriv";
	}
</script>
