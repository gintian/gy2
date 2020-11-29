<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
<%@ page import="org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.actionform.report.actuarial_report.fill_cycle.ReportCycleForm"%>
<script language="JavaScript">
function query(Report_id,id,unitcode,flag)
{
  
    if(Report_id.indexOf("U02")!=-1)
   {
     document.getElementsByName("report_id")[0].value=Report_id;
     document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?br_import=init";
  	document.reportCycleForm.submit();
   }else if(Report_id=="U03")
   {
   		 document.getElementsByName("report_id")[0].value=Report_id;
   		document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?br_import=init";
   		document.reportCycleForm.submit();
   }else if(Report_id=="U04")
   {
   	    document.getElementsByName("report_id")[0].value=Report_id;
   		document.reportCycleForm.action="/report/actuarial_report/fill_cycle.do?br_import=init";
   		document.reportCycleForm.submit();
   }
}

function collectData()
{
	if(confirm("您确定要执行汇总数据操作吗?"))
	{
		 var waitInfo=eval("wait");	   
         waitInfo.style.display="block";
		 var hashvo=new ParameterSet();
		 hashvo.setValue("cycle_id",'${reportCycleForm.id}');
		 hashvo.setValue("opt","2");
		 var request=new Request({method:'post',asynchronous:true,onSuccess:success,functionId:'03060000302'},hashvo); 
	
	}
}

function success(outparamters)
{
 var waitInfo=eval("wait");	
 waitInfo.style.display="none";
	alert("汇总成功!");
}
function MusterInitData()
{
	var waitInfo=eval("wait");	
	waitInfo.style.display="none";
}

</script>
<hrms:themes />
<html:form action="/report/actuarial_report/fill_cycle">
	<html:hidden styleId="report_id" name="reportCycleForm" property="report_id" />
	<%
	ReportCycleForm reportCycleForm =(ReportCycleForm) session.getAttribute("reportCycleForm");
	// System.out.println("parm:"+reportCycleForm.getCycleparm());
	 %>
	
	<br>
  <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
    <tr>         	 
      <td align="center" class="TableRow" width="70%" nowrap>报表名称</td>	
      <td align="center" class="TableRow" width="10%" nowrap>导入</td>		 
    </tr>
    <%
int i=0;
String flag="";
%>
    <hrms:extenditerate id="element" name="reportCycleForm" property="editreportForm.list" indexes="indexes"  pagination="editreportForm.pagination" pageCount="30" scope="session">
     <%
       LazyDynaBean abean=(LazyDynaBean)pageContext.getAttribute("element");
     //  System.out.println(abean.get("Report_id"));
       if(abean.get("Report_id").equals("U01")||abean.get("Report_id").equals("U05")){
      
       }else{
      
      if(i%2==0)
      {
     %>
     <tr class="trShallow">
     <%}
     else
     {%>
     <tr class="trDeep">
     <%
     }
     i++;          
     %> 
      <td align="left" class="RecordRow"  nowrap>   &nbsp;&nbsp;  
        <%
          out.print(abean.get("report_name"));
         %>
      </td>
      
      <td class="RecordRow" align="center" nowrap>  
      <a href="###" onclick="query('<%=abean.get("Report_id")%>','<%=abean.get("id")%>','<%=abean.get("unitcode")%>','<%=abean.get("flag")%>');">  
        <%if(abean.get("flag")!=null&&((String)abean.get("flag")).equals("-1"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("0"))
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }else if(((String)abean.get("flag")).equals("1"))
        {
           out.print("<img src='/images/view.gif' border=0>");
        }else 
        {
           out.print("<img src='/images/edit.gif' border=0>");
        }
        
        %>
        </a>    
      </td>
    </tr>
    <%} %>
   </hrms:extenditerate>
  </table>
  
  <table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable" style="margin-top:3px;">
  <tr><td align='left' >
  	 <input type='button' id="collect"  value='汇总数据'  onclick="collectData()" class="mybutton">
  	&nbsp;<hrms:submit styleClass="mybutton" property="br_return">
						<bean:message key="button.return" />
					</hrms:submit>
  </td></tr>
  </table>
  <Br>
  
 
   
</html:form>
<div id='wait' style='position: absolute; top: 200; left: 250;'>
	<table border="1" width="400" cellspacing="0" cellpadding="4"
	class="table_style"  height="87"
		align="center">
		<tr>
			<td  class="td_style" 
				height=24>
				正在汇总数据请稍候....
			</td>
		</tr>
		<tr>
			<td style="font-size: 12px; line-height: 200%" align=center>
				<marquee class="marquee_style"  direction="right"
					width="300" scrollamount="5" scrolldelay="10" >
					<table cellspacing="1" cellpadding="0">
						<tr height=8>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
							<td bgcolor=#3399FF width=8></td>
							<td></td>
						</tr>
					</table>
				</marquee>
			</td>
		</tr>
	</table>
</div>
<script language="javascript">
 MusterInitData();
</script>