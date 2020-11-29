<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,
				 java.text.DecimalFormat,
				 com.hjsj.hrms.actionform.performance.implement.DataGatherForm,
				 org.apache.commons.beanutils.LazyDynaBean,	
				 com.hjsj.hrms.utils.PubFunc,		 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant,
				 com.hrms.hjsj.sys.Des" %>
<%  
		DataGatherForm dataGatherForm=(DataGatherForm)session.getAttribute("dataGatherForm");		
		String plan_id =(String)dataGatherForm.getPlanId();
		plan_id = PubFunc.encryption(plan_id); %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<hrms:themes />
</head>
<script language='javascript' >

function return_bt()
{
		 dataGatherForm.action="/performance/kh_plan/performPlanList.do?b_query=return";
		 dataGatherForm.target="il_body";
		 dataGatherForm.submit(); 
}

function goback()
	{
	  document.dataGatherForm.action="/performance/implement/performanceImplement.do?b_int=link&plan_id=<%=plan_id%>";
	  document.dataGatherForm.target="il_body";
	  document.dataGatherForm.submit();
	
	}

</script>

<body>


<html:form action="/performance/implement/dataGather">

<table>
<tr><td>&nbsp;&nbsp;&nbsp;</td> <td   align='left' >
<div id="bc" style="display=none" />
		<logic:equal name="dataGatherForm" property="fromUrl"  value="0">
		<button  class="mybutton"     onclick='goback()' /><bean:message key="kq.search_feast.back"/></button>
		</logic:equal>
		<logic:notEqual name="dataGatherForm" property="fromUrl"  value="0" >
			<button   id="cl_return"   class="mybutton"    onclick="return_bt()"  allowPushDown="false" down="false"><bean:message key="button.return"/></button>
		</logic:notEqual>
		
</div>
		&nbsp;&nbsp; </td></tr>
</table>
</html:form>

<script language='javascript' >
setTimeout('show()',1500);
function show()
{
	document.getElementById("bc").style.display="block"; 
}

</script>

</body>
</html>