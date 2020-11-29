<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="com.hjsj.hrms.businessobject.kq.set.TurnOvertime"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css"><hrms:themes /> <!-- 7.0css -->
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<%
	int i=0;	
	int r=0;
%>
<SCRIPT language=JavaScript>
 
function test(name)
{
   return false;
   if(name!=null&&name!=""&&name!="undefined"&&name.length>0)
   {
      var obj=$('cardset');
      obj.setSelectedTab(name);
   }
		
}  
function changeUrl(url)
{
   if(url=="result")
   {
      dataAnalyseForm.action="/kq/machine/analyse/analyse_result.do?b_search=link";
      dataAnalyseForm.submit();
   }else if(url=="exceptcard")
   {
      dataAnalyseForm.action="/kq/machine/analyse/exceptcard.do?b_search=link";
      dataAnalyseForm.submit();
   }else if(url=="tranovertime")
   {
      dataAnalyseForm.action="/kq/machine/analyse/tranovertime.do?b_search=link";
      dataAnalyseForm.submit();
   }else if(url=="cardtoovertime"){
   	  dataAnalyseForm.action="/kq/machine/analyse/cardtoovertime.do?b_search=link";
      dataAnalyseForm.submit();
   }else if(url=="busicompare"){
      dataAnalyseForm.action="/kq/machine/analyse/busicompare.do?b_search=link";
      dataAnalyseForm.submit();
   }
    
}
</SCRIPT>
<body onload="test('')">	
<html:form action="/kq/machine/analyse/data_analyse_data">
    <table border="0" cellspacing="0"  align="left" cellpadding="0" width="100%">
      <tr>
        <td width="100%" >
         <hrms:tabset name="cardset" width="100%" height="100%" type="true">         
	    <hrms:tab name="analyse_result" label="分析结果" visible="true" function_id="270620,0C3800" url="/kq/machine/analyse/analyse_result.do?b_search=link">
            </hrms:tab>	
            <hrms:tab name="exceptCard" label="异常刷卡" visible="true" function_id="270621,0C3801" url="/kq/machine/analyse/exceptcard.do?b_search=link">
            </hrms:tab>
            <hrms:tab name="overTime" label="延时加班" visible="true" function_id="270622,0C3802" url="/kq/machine/analyse/tranovertime.do?b_search=link">
            </hrms:tab>
            <%  
                TurnOvertime tot = new TurnOvertime();
            	if(tot.showTurnOvertimePage()){//判断是否启用了休息日转加班
            %>
            <hrms:tab name="cardToOverTime" label="休息日转加班" visible="true" function_id="270624,0C3804" url="/kq/machine/analyse/cardtoovertime.do?b_search=link">
            </hrms:tab>
            <%
            	}
            %>
            <hrms:tab name="busiCompare" label="申请对比" visible="true" function_id="270623,0C3803" url="/kq/machine/analyse/busicompare.do?b_search=link">
            </hrms:tab>
	 </hrms:tabset>
        </td>
      </tr>   
</table> 
  
</html:form>
</body>