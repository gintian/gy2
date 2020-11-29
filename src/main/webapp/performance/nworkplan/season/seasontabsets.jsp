<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<logic:equal name="newworkplanForm" property="isseason" value="1">
<hrms:tabset name="sys_param" width="100%" height="100%" type="true" > 
	<logic:equal name="newworkplanForm" property="opt" value="1">
		<hrms:priv func_id="0AB0301">
	      <hrms:tab name="param1" label="本人季计划与总结"  visible="true" function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=1&type=1&isdept=1">
	      </hrms:tab>
		</hrms:priv>
		<hrms:priv func_id="0AB0302">	
	      <hrms:tab name="param2" label="部门季计划与总结"  visible="true"  function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=1&type=1&isdept=2">
	      </hrms:tab> 
	    </hrms:priv> 
	</logic:equal>
	<logic:equal name="newworkplanForm" property="opt" value="2">
	<logic:equal name="newworkplanForm" property="belong_type" value="0">
	<hrms:priv func_id="0AB0301">
		<hrms:tab name="param1" label="个人季计划与总结"  visible="true" function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&&opt=${newworkplanForm.opt}&isread=${newworkplanForm.isread}&p0100=${newworkplanForm.p0100}&type=${newworkplanForm.isseason}&isdept=1">
	    </hrms:tab>
	</hrms:priv>
	</logic:equal>
	<logic:equal name="newworkplanForm" property="belong_type" value="1">
	<hrms:priv func_id="0AB0301">
		<hrms:tab name="param1" label="处室季计划与总结"  visible="true" function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&&opt=${newworkplanForm.opt}&isread=${newworkplanForm.isread}&p0100=${newworkplanForm.p0100}&type=${newworkplanForm.isseason}&isdept=1">
	      </hrms:tab>
	</hrms:priv>
	</logic:equal>	
	<logic:equal name="newworkplanForm" property="belong_type" value="2">
		<hrms:priv func_id="0AB0302">
	      <hrms:tab name="param2" label="部门季计划与总结"  visible="true"  function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=${newworkplanForm.opt}&isread=${newworkplanForm.isread}&p0100=${newworkplanForm.p0100}&type=${newworkplanForm.isseason}&isdept=2">
	      </hrms:tab> 
		</hrms:priv>
	 </logic:equal>
	</logic:equal>  
</hrms:tabset>
</logic:equal>
<logic:equal name="newworkplanForm" property="isseason" value="2">
<hrms:tabset name="sys_param" width="100%" height="100%" type="true" >
	<logic:equal name="newworkplanForm" property="opt" value="1">
	<hrms:priv func_id="0AB0401">
      <hrms:tab name="param1" label="本人年计划与总结"  visible="true" function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=1&type=2&isdept=1">
      </hrms:tab>
    </hrms:priv>
    <hrms:priv func_id="0AB0402">
      <hrms:tab name="param2" label="部门年计划与总结"  visible="true"  function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=1&type=2&isdept=2">
      </hrms:tab>
    </hrms:priv>
    </logic:equal>
    <logic:equal name="newworkplanForm" property="opt" value="2">
	    <logic:equal name="newworkplanForm" property="belong_type" value="1">
	      <hrms:priv func_id="0AB0401">
		      <hrms:tab name="param1" label="处室年计划与总结"  visible="true" function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=${newworkplanForm.opt}&type=${newworkplanForm.isseason}&isdept=1&isread=${newworkplanForm.isread}&p0100=${newworkplanForm.p0100}">
		      </hrms:tab>
	      </hrms:priv>	
	    </logic:equal>
	    <logic:equal name="newworkplanForm" property="opt" value="2">
	    <hrms:priv func_id="0AB0402">
		  <hrms:tab name="param2" label="部门年计划与总结"  visible="true"  function_id="" url="/performance/nworkplan/searchquarters.do?b_query=link&opt=${newworkplanForm.opt}&type=${newworkplanForm.isseason}&isdept=2&isread=${newworkplanForm.isread}&p0100=${newworkplanForm.p0100}">
		  </hrms:tab>  
		 </hrms:priv>
      </logic:equal>
    </logic:equal>       
</hrms:tabset>
</logic:equal>