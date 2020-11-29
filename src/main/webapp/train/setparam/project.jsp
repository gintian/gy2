<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html:form action="/train/setparam/project">
<hrms:tabset name="sys_param" width="100%" height="99%" type="true"> 
      <hrms:tab name="param1" label="train.b_plan.train.set" function_id="3237301" visible="true" url="/org/gzdatamaint/gzdatamaint.do?b_addsubclass=link&tagname=1&infor=1&gzflag=1">
      </hrms:tab>	
      <hrms:tab name="param2" label="train.b_plan.train.item" function_id="3237302" visible="true" url="/train/setparam/setPlanitem.do?b_query=link">
      </hrms:tab>
      <hrms:tab name="param3" label="train.b_plan.traincourse.setatt" function_id="3237303" mod_id="39" visible="true" url="/train/attendance/setTrainAttendance.do?b_query=link">
      </hrms:tab> 
	  <hrms:tab name="param4" label="train.b_plan.station.set" function_id="3237304" visible="true" url="/train/station/setTrainStation.do?b_query=link">
	  </hrms:tab> 
      <hrms:tab name="param5" label="train.b_plan.mediaserver.set" function_id="3237305" mod_id="39" visible="true" url="/train/setparam/setmediaserver.do?b_query=link&opt=querry">
      </hrms:tab> 	  
      <hrms:tab name="param6" label="train.setparam.integral.setting" function_id="3237306" mod_id="39" visible="true"  url="/train/station/integral.do?b_query=link">
      </hrms:tab>
      <hrms:tab name="param7" label="train.b_plan.lesson.progress" function_id="3237307" mod_id="39" visible="true" url="/train/setparam/lessonplan.do?b_query=link&opt=set">
      </hrms:tab>
      <hrms:tab name="param8" label="train.b_plan.budget.param" function_id="3237308" mod_id="39" visible="true" url="/train/setparam/trainbudge.do?b_query=link">
      </hrms:tab>
      <hrms:tab name="param9" label="train.b_plan.teacher.index" function_id="3237309" visible="true" url="/train/setparam/teacherfield.do?b_query=link">
      </hrms:tab>
</hrms:tabset> 
</html:form> 