<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.actionform.kq.team.ArrayGroupForm"%>
<%@ page
	import="java.util.*,org.apache.commons.beanutils.LazyDynaBean"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<hrms:themes /> <!-- 7.0css -->
<style>
<!--
.divTable {
	margin-top: 10px;
	border: 1px solid #C4D8EE;
	overflow-x: scroll;
	height: 350px;
	width: 500px;
	margin-left: 25px;
}

.divTable table {
	border-right: 0px;
	border-top: 0px;
}


.divTableP {
	border: 1px solid #C4D8EE;
	overflow: hidden;
	height: 30px;
	width: 500px;
	margin-left: 25px;
	border-top:none;
}

.divTableB {
	overflow: hidden;
	height: 35px;
	width: 500px;
	margin-left: 25px;
}
-->
</style>
<script language="javascript">
	function getSelect()
	{
		var nabse = document.getElementById("nbaseid").value;
		var groupid = document.getElementById("groupid").value;
		var str="";
        for(var i=0;i<document.arrayGroupForm.elements.length;i++)
	    {
	        if(document.arrayGroupForm.elements[i].type!="checkbox")
		        continue;
	      
		    if(document.arrayGroupForm.elements[i].checked==true&&document.arrayGroupForm.elements[i].name!="aa")
		    {
		        var ov=document.arrayGroupForm.elements[i].value;
		        str=str+ov+",";
		    }
	    }
	    
		var t_url="/kq/team/array_group/load_zidong_class.do?b_search=link";
		var iframe_url="/general/query/common/iframe_query.jsp?src="+t_url;
		var return_vo= window.showModalDialog(iframe_url,'rr', 
		    "dialogWidth:620px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
		if(!return_vo)
		    return false;
		 
		if(return_vo.flag=="true")
		{
			var hashvo=new ParameterSet();
		 	hashvo.setValue("group_id",groupid);
		 	hashvo.setValue("start_date",return_vo.start_date);
		 	hashvo.setValue("end_date",return_vo.end_date);
		 	hashvo.setValue("nbase",nabse);
		 	hashvo.setValue("a0100s",str);
		 	var request=new Request({asynchronous:false,onSuccess:searchemp,functionId:'15221300009'},hashvo);
		}
	}
	
	function searchemp(outparamters)
    {
      var save_flag=outparamters.getValue("save_flag"); 
      if(save_flag=="true")
      { //alert('bbb');
        //arrayGroupForm.action="/kq/team/array_group/search_array_emp_data.do?b_search=link&group_id=${arrayGroupForm.group_id}";
        //arrayGroupForm.submit();
    	window.returnValue = "true";
      }else
      {
         alert("添加人员失败！");
      }
    }
    
	var checkflag = "false";
	function selAll()
	{
	    var len=document.arrayGroupForm.elements.length;
	    var i;
	
	   if(checkflag=="false")
	   {
	      for (i=0;i<len;i++)
	      {
	         if (document.arrayGroupForm.elements[i].type=="checkbox")
	          {
	            document.arrayGroupForm.elements[i].checked=true;
	          }
	       }
	      checkflag="true";
	   } else {
	      for (i=0;i<len;i++)
	      {
	        if (document.arrayGroupForm.elements[i].type=="checkbox")
	        {
	          document.arrayGroupForm.elements[i].checked=false;
	        }
	      }
	      checkflag = "false";    
	  } 
	}
</script>

<html:form action="/kq/team/array_group/load_host_data_record">
	<%
		int i = 0;
	%>
	<div style="margin-left: 25px"><bean:message key="kq.group.auto.assign.emp.hint"/></div>
	<div class="divTable common_border_color">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<tr>
				<td class="TableRow" align="center" nowrap style="border-top:none;border-left:none">
					<input type="checkbox" name="aa" value="true" onclick="selAll()">
				</td>
				<td align="center" class="TableRow" nowrap style="border-top:none">
                                                      人员库
                </td>
				<td align="center" class="TableRow" nowrap style="border-top:none">
					单位名称
				</td>
				<td align="center" class="TableRow" nowrap style="border-top:none">
					部门
				</td>
				<td align="center" class="TableRow" nowrap style="border-top:none">
					姓名
				</td>
				<td align="center" class="TableRow" nowrap style="border-top:none;border-right:none">
					当前班组
				</td>
				<html:hidden name="arrayGroupForm" property="nbase"
					styleClass="text" styleId="nbaseid" />
				<html:hidden name="arrayGroupForm" property="group_id"
					styleClass="text" styleId="groupid" />
			</tr>
			<hrms:paginationdb id="element" name="arrayGroupForm"
				sql_str="arrayGroupForm.sqlstr" table="" where_str=""
				columns="arrayGroupForm.column"
				order_by="order by dbid,b0110,e0122,e01a1,a0100" page_id="pagination"
				pagerows="14" distinct="" keys="" indexes="indexes">
				<%
					if (i % 2 == 0) {
				%>
				<tr class="trShallow">
					<%
						} else {
					%>
				
				<tr class="trDeep">
					<%
						}
					    i++;
					%>
					<td align="center" class="RecordRow" nowrap style="border-left:none">
						<input type="checkbox" name="checkbox"
							value='<bean:write  name="element" property="nbase" filter="true"/><bean:write  name="element" property="a0100" filter="true"/>'/>
					</td>
					<td align="left" class="RecordRow" nowrap>
	                    &nbsp;<bean:write name="element" property="dbname" filter="true" />				
					</td>
					<td align="left" class="RecordRow" nowrap>
						<hrms:codetoname codeid="UN" name="element" codevalue="b0110"
							codeitem="codeitem" scope="page" />
						&nbsp;
						<bean:write name="codeitem" property="codename" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap>
						<hrms:codetoname codeid="UM" name="element" codevalue="e0122"
							codeitem="codeitem" scope="page" />
						&nbsp;
						<bean:write name="codeitem" property="codename" />
						&nbsp;
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;
						<bean:write name="element" property="a0101" filter="true" />
						&nbsp;
					</td>
					<td align="center" class="RecordRow" nowrap style="border-right:none">
						<%
							LazyDynaBean abean = (LazyDynaBean) pageContext.getAttribute("element");
						    String aobject_id = (String) abean.get("group_id");
						%>
						<hrms:kqhostgroup value='<%=aobject_id%>' />
					</td>
				</tr>
			</hrms:paginationdb>
		</table>
	</div>
	<div class="divTableP common_border_color">
	<table width="100%">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td align="right" nowrap class="tdFontcolor">
					<hrms:paginationdblink name="arrayGroupForm" property="pagination"
						nameId="arrayGroupForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>
	</table>
	</div>
	<div class="divTableB">
			<table width="100%" height="100%">
				<tr>
					<td align="center" valign="bottom" nowrap>
						<input type="button" name="Submit2"
							value="<bean:message key="button.ok"/>" class="mybutton"
							onclick="getSelect();window.close();">
						&nbsp;
						<input type="button" name="Submit2"
							value="<bean:message key="button.cancel"/>" class="mybutton"
							onclick="window.close();">
						&nbsp;
					</td>
				</tr>
			</table>
	</div>

</html:form>
