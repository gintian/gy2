<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<html:form action="/kq/options/initcode/kq_setdata">
	<hrms:tabset name="kq_setdata" width="100%" height="100%" type="true">
		<hrms:tab name="param1" label="数据初始化" visible="true"
			url="/kq/options/initcode/kq_initcode.do?b_query=link" function_id="2703501">
		</hrms:tab>
		<hrms:tab name="param2" label="同步单位/部门/岗位数据" visible="true"
			url="/kq/options/initcode/kq_contrast.do?b_query=link" function_id="2703502">
		</hrms:tab><!--其他数据比对  -->
		<hrms:tab name="param5" label="清空参数设置" visible="true"
			url="/kq/options/initcode/kq_clearset.do?b_query=link" function_id="2703505">
		</hrms:tab>
		<hrms:tab name="param4" label="清理冗余数据" visible="true"
			url="/kq/options/initcode/kq_reddate.do?b_query=link" function_id="2703504">
		</hrms:tab>

			<hrms:tab name="param3" label="历史数据归档" visible="true"
				url="/kq/options/initcode/kq_filecode.do?b_query=link" function_id="2703503">
			</hrms:tab><!-- 归档封存数据 -->

	</hrms:tabset>
</html:form>