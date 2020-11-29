<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<!-- 单位，职位信息维护暂时去掉b,k -->
	<hrms:tabset   height="100%" name="aaa" type="true">
		<hrms:tab name="a" label="人员信息" visible="true"  url="/general/approve/personinfo/sumre.do?b_query=link&abkflag=a">     
		</hrms:tab>

	</hrms:tabset>



    