
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="org.apache.commons.beanutils.LazyDynaBean"%>
<%@ page import="com.hjsj.hrms.actionform.sys.export.ExportForm" %>

<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<script type="text/javascript" src="/js/wz_tooltip.js"></script>
<script type="text/javascript">
<!--
	function checkSelect() {
		check = false;
		input = document.getElementsByTagName("input");
		for(i = 0; i < input.length; i++) {
			if (input[i].type == "checkbox" && input[i].checked==true && input[i].name != "checkall") {
				check =true;
				break;
			} 	
		}
		return check;
	}
	
	function checkalls() {
		var input = document.getElementById("checkall");
		var num = true;
		
		if (input.checked == true) {
			num = true;
		} else {
			num = false;
		}
		
		var inputs = document.getElementsByTagName("input");
		for (i = 0; i < inputs.length; i++) {
			var obj = inputs[i];
			if (obj.type == "checkbox" && obj.name != "checkall") {
				obj.checked = num
			}
		}
	}
	function power() {
		check = checkSelect();
		if (!check) {
			window.alert("没有选择记录！");
			return;
		}
		var input = document.getElementsByTagName("input");
		var num = 0;
		var ids = "";
		for (i = 0; i < input.length; i++) {
			var obj = input[i];
			if (obj.type == "checkbox" && obj.name != "checkall") {
				if (obj.checked == true) {
					num++;

					ids = obj.value;
				}
			}
		}
		
		if (num > 1) {
			window.alert("手工执行只能执行一个作业类！");
			return;
		}
		
		var hashvo=new ParameterSet();
       	hashvo.setValue("id",ids);
		var waitInfo=eval("wait");	   
        waitInfo.style.display="block";
        var request=new Request({method:'post',asynchronous:true,onSuccess:showSelect,functionId:'1010040022'},hashvo);		
		
	}
	
	function showSelect(outparamters) { 
     var waitInfo=eval("wait");	   
     waitInfo.style.display="none";
     var info = outparamters.getValue("info");
     alert(info);
     
  	}
//-->
</script>
<%int i=0;%>
<html:form action="/system/export/searchclass_info">
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
<tr><td>
<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">

			<tr>
					<td align="center" class="TableRow" nowrap>
						<input id="checkall" type="checkbox" name="checkall" alt="全选" onclick="checkalls()"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.jobclass"/>id
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.description"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.jobclass"/>
					</td>
					<td align="center" width="90" class="TableRow" nowrap>
					<bean:message key="sys.export.jobparam"/>
					</td>	
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.job_time"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.trigger_flag"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="sys.export.status"/>
					</td>
					<td align="center" class="TableRow" nowrap>
					<bean:message key="column.operation"/>
					</td>
											
			</tr>
		<hrms:paginationdb id="element" allmemo="1" name="exportForm" sql_str="exportForm.sql" table="" where_str="exportForm.where" columns="exportForm.column" order_by="exportForm.orderby" pagerows="${exportForm.pagerows}"  page_id="pagination" indexes="indexes">	
		<bean:define id="id" name="element" property="job_id"/>
		<bean:define id="trigger" name="element" property="trigger_flag"/>
		<bean:define id="status_flag" name="element" property="status"/>
          <%
          if(i%2==0)
          {
          %>
          <tr class="trShallow" onMouseOver="javascript:tr_onclick(this,'')">
          <%}
          else
          {%>
          <tr class="trDeep" onMouseOver="javascript:tr_onclick(this,'E4F2FC')">
          <%
          }
          i++;
          LazyDynaBean vo=(LazyDynaBean)element;
          %>  
          	<td align="center" class="RecordRow">
          		<logic:equal name="status_flag" value="1">
					&nbsp;<input type="checkbox" name="checknum" value="<bean:write name="element" property="job_id"/>"/>
				</logic:equal>
				<logic:equal name="status_flag"  value="0">
					&nbsp;
				</logic:equal>	
				
			</td>
			<td class="RecordRow">
				&nbsp;<bean:write name="element" property="job_id"/>
			</td>
			<td class="RecordRow">
				&nbsp;<bean:write name="element" property="description"/>
			</td>
			<td class="RecordRow" align="left">
				&nbsp;<bean:write name="element" property="jobclass"/>
			</td>
			<%
             String tx= (String)vo.get("job_param");
            %> 
            <hrms:showitemmemo showtext="showtext" itemtype="M" setname="t_sys_jobs" tiptext="tiptext" text="<%=tx%>"></hrms:showitemmemo>             
            <td class="RecordRow" ${tiptext} nowrap> 
                &nbsp;${showtext}&nbsp;
            </td>
			<td class="RecordRow" align="left">
				&nbsp;<bean:write name="element" property="job_time"/>
			</td>
			<td class="RecordRow" align="center">
				<logic:equal name="trigger"  value="1">
					<bean:message key="label.sys.warn.domain.complex"/>
				</logic:equal>
				<logic:equal name="trigger"  value="0">
					<bean:message key="label.sys.warn.domain.simple"/>
				</logic:equal>		
			</td>
			<td class="RecordRow" align="center">
				<logic:equal name="status_flag" value="1">
					<bean:message key="label.zp_resource.status1"/>
				</logic:equal>
				<logic:equal name="status_flag"  value="0">
					<bean:message key="label.zp_resource.status0"/>
				</logic:equal>	
			</td>
			<%
				String job_id = (String)vo.get("job_id");
			 %>
			<td class="RecordRow" align="center">
				<a href="/system/export/editclass_info.do?b_query=link&encryptParam=<%=PubFunc.encrypt("flag=1&job_id="+job_id)%>"><img src="/images/edit.gif" border="0" alt="修改"></a>
			</td>
		</tr>
	</hrms:paginationdb>
	</table>
	</td></tr>
	<tr><td>
	<table   width="100%" border="0" class="RecordRowP" align="center">
	<tr>
		<td valign="bottom" class="tdFontcolor">
		<hrms:paginationtag name="exportForm" pagerows="${exportForm.pagerows}" property="pagination" scope="page" refresh="true"></hrms:paginationtag>
		</td>
	    <td   align="right" nowrap class="tdFontcolor">
		     <p align="right"><hrms:paginationdblink name="exportForm" property="pagination" nameId="exportForm" scope="page">
			</hrms:paginationdblink>
		</td>
	</tr>
</table>
</td></tr>
<tr>
	<td align="center" height="35px;"><input type="button" class="mybutton" name="bt" value="手工执行" onclick="power()"/></td>
</tr>
</table>
<div id='wait' style='position:absolute;top:200;left:250;display:none;'>
  <table border="1" width="300" cellspacing="0" cellpadding="4" class="table_style" height="87" align="center">
           <tr>

             <td class="td_style" height=24><bean:message key="classdata.isnow.wiat"/></td>

           </tr>
           <tr>
             <td style="font-size:12px;line-height:200%" align=center>
               <marquee class="marquee_style" direction="right" width="300" scrollamount="5" scrolldelay="10">
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
</html:form>
