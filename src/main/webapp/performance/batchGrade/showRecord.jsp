<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.batchGrade.BatchGradeForm,
				 com.hrms.struts.taglib.CommonData,
				 org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
				com.hrms.struts.constant.SystemConfig" %>
<%
	BatchGradeForm batchGradeForm=(BatchGradeForm)session.getAttribute("batchGradeForm");
//<td  class='RecordRow'  align='center'   nowrap ><a href='javascript:showDiary(1,3)' >((HashMap)dayList.get(3)).get(String.valueOf(3)) </a></td>
%>
<html>
  <head>
  </head>
  <body>

  	<table width="530" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable1">
  	  <caption style="font-size:18px;font-weight: bold;">日志填报情况统计表</caption>
  	  <!--  <tr><td height="5px"></td></tr> -->
	  <tr>
	    <td rowspan="2" align="center" class="TableRow" nowrap > 考核对象 </td>
	    <logic:equal name="batchGradeForm" property="showDay" value="1">
	    	<td colspan="4" align="center" class="TableRow" nowrap >日报</td>
	    </logic:equal>
	    <logic:equal name="batchGradeForm" property="showWeek" value="2">
	    	<td colspan="4" align="center" class="TableRow" nowrap >周报</td>
	    </logic:equal>
	    <logic:equal name="batchGradeForm" property="showMonth" value="3">
	    	<td colspan="4" align="center" class="TableRow" nowrap >月报</td>
	    </logic:equal>
	  </tr>
	  <tr>
	  	<logic:equal name="batchGradeForm" property="showDay" value="1">
		    <td align="center" class="TableRow" nowrap >未填</td>
		    <td align="center" class="TableRow" nowrap >已报</td>
		    <td align="center" class="TableRow" nowrap >已批</td>
		    <td align="center" class="TableRow" nowrap >驳回</td>
	    </logic:equal>
	    <logic:equal name="batchGradeForm" property="showWeek" value="2">
		     <td align="center" class="TableRow" nowrap >未填</td>
		    <td align="center" class="TableRow" nowrap >已报</td>
		    <td align="center" class="TableRow" nowrap >已批</td>
		    <td align="center" class="TableRow" nowrap >驳回</td>
	    </logic:equal>
	    <logic:equal name="batchGradeForm" property="showMonth" value="3">
		     <td align="center" class="TableRow" nowrap >未填</td>
		    <td align="center" class="TableRow" nowrap >已报</td>
		    <td align="center" class="TableRow" nowrap >已批</td>
		    <td align="center" class="TableRow" nowrap >驳回</td>
		</logic:equal>
	  </tr>
	 <% HashMap dataMap = batchGradeForm.getDataMap();
	 	for(int i=0;i<dataMap.size();i++){
	 		ArrayList recordList = (ArrayList) dataMap.get(String.valueOf(i));
	 		String kh_object = (String) recordList.get(0);
	 		ArrayList dayList = (ArrayList) recordList.get(1);
	 		ArrayList weekList = (ArrayList) recordList.get(2);
	 		ArrayList monthList = (ArrayList) recordList.get(3);
	 %>
	 <tr>
	 	<td  class='RecordRow'  align='center'   nowrap ><%=kh_object %></td>
	 	<logic:equal name="batchGradeForm" property="showDay" value="1">
	 		<td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)dayList.get(3)).get(String.valueOf(3)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)dayList.get(0)).get(String.valueOf(0)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)dayList.get(1)).get(String.valueOf(1)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)dayList.get(2)).get(String.valueOf(2)) %></td>
	 	</logic:equal>
	 	<logic:equal name="batchGradeForm" property="showWeek" value="2">
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)weekList.get(3)).get(String.valueOf(3)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)weekList.get(0)).get(String.valueOf(0)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)weekList.get(1)).get(String.valueOf(1)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)weekList.get(2)).get(String.valueOf(2)) %></td>
	    </logic:equal>
	    <logic:equal name="batchGradeForm" property="showMonth" value="3">
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)monthList.get(3)).get(String.valueOf(3)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)monthList.get(0)).get(String.valueOf(0)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)monthList.get(1)).get(String.valueOf(1)) %></td>
		    <td  class='RecordRow'  align='center'   nowrap ><%= ((HashMap)monthList.get(2)).get(String.valueOf(2)) %></td>
		</logic:equal>
	 </tr>
	 <% }%>
	</table>
  </body>
</html>
