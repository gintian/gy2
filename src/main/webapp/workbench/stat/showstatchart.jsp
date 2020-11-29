<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>

     <SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>
       <SCRIPT LANGUAGE=javascript>
   	/*******************************
   	 *设置统计信息
   	 *******************************/
    	function statset()
    	{
    	   target_url="/workbench/stat/statset.do?b_search=link&isoneortwo=1";
    	    newwindow=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no,top=170,left=220,width=400,height=468'); 
       }
   </SCRIPT>
   <br> 
<table  align="center">
<tr align="left">  
    <td valign="top"  nowrap>
    <hrms:priv func_id="04010101">
       <a href="javascript:statset()"><bean:message key="workbench.stat.statsettitle"/></a>  
       </hrms:priv> 
    &nbsp;&nbsp;
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=12">立体直方图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=11">平面直方图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=5">立体圆饼图</a>
    </td>
    <td valign="top"  nowrap>
      &nbsp;&nbsp;<a href="/workbench/stat/showstatchart.do?chart_type=20">平面圆饼图</a>
    </td>
  </tr>
  </table>
  <html:form action="/workbench/stat/statshow">
 <table  align="center">

          <tr>
            <td align="center" nowrap colspan="5">
	 	<hrms:chart name="statForm" title="${statForm.snamedisplay}" scope="session" legends="list" data="" width="670" height="530" chart_type="${statForm.chart_type}">
	 	</hrms:chart>
            </td>
          </tr>          
</table>
</html:form>

