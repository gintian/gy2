<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<hrms:tabset name="sys_param" width="100%" height="99%" type="true" > 
      <hrms:tab name="param1" label="train.resource.mylessons.learnningcourse"  visible="true" url="/train/resource/mylessons.do?b_query=link&opt=ing">
      </hrms:tab>	
      <hrms:tab name="param2" label="train.resource.mylessons.learnedcourse" visible="true" url="/train/resource/mylessons.do?b_query=link&opt=ed">
      </hrms:tab>         
</hrms:tabset>


