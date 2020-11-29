<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar2.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>

<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.employSummarise.EmploySummariseForm,
				com.hrms.hjsj.sys.FieldItem,
				org.apache.commons.beanutils.LazyDynaBean" %>
<html>
<HEAD>
 <% 
           		   	 EmploySummariseForm employSummariseForm=(EmploySummariseForm)session.getAttribute("employSummariseForm");
           		   	 ArrayList columnsList=employSummariseForm.getColumnsList();
           		   	 String  viewType=employSummariseForm.getViewType();  //1:用工需求  2：招聘计划
           		   
 %>
 <% 
String str= request.getParameter("planID");
String flag= request.getParameter("flag");
           		   
 %>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'>
	//this.status ="招聘管理 / 招聘总结";
	function pos_view(flag)
	{
		var selects="";
		var n=0;
		var num=0;
		var IDS=$('ID');
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			{
				
				if(document.forms[0].elements[i].checked==true)
				{
					
					num++;
					if(IDS.length>1)
						selects+="~"+IDS[n].value;
					else
						selects+="~"+IDS.value;				
				}
				n++;
			}
		}
		if(num==0)
		{
			<%  if(viewType.equals("2")){  %>
				alert(SELECT_PLAN+"！");
			<%  } else  if(viewType.equals("1")){ %>
				alert(PLEASE_SELECT_POSITION_REQUIREMENT+"！");
			
			<% } %>
			return;
		}
		if(flag==1&&num>1)
		{
			alert(ONLY_SELECT_ONE_RECORD+"！");
			return ;
		}
		
		
		
		if(flag==1)	
			document.employSummariseForm.action="/hire/demandPlan/engagePlan.do?b_queryPosition=query&viewType=<%=viewType%>&operate=summarise&z0101="+selects.substring(1);
		if(flag==2)
			document.employSummariseForm.action="/hire/interviewEvaluating/interviewExamine.do?b_query=link&viewType=<%=viewType%>&codeset=UN&z0101="+selects.substring(1)+"&code=summarise&model=7&operate=init&summary=summary";
		
		document.employSummariseForm.submit();
	
	}
	
	
	function setState(state)//04发布 06结束
	{
		if(state=='del')
		{
		<%  if(viewType.equals("2")){  %>
			if(!confirm(WHEN_DELETE_PLAN_WILL_DELETE_HIRE_RECORD+"？"))
				return;
		<%   } else {  %>
			if(!confirm(PLEASE_CONFIRM_DELETE_REQUIREMENT+",(只能删除起草、已报批、驳回、结束状态的需求)？"))
				return;
		
		<% } %>
				
		}
		
		
		var selects="";
		var n=0;
		var num=0;
		var IDS=$('ID');
		var SpStates=document.getElementsByName('SpState');
		var SpState="";
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			{
				
				if(document.forms[0].elements[i].checked==true)
				{
					
					num++;
					SpState=SpStates[n].value;
					if(state=="04"&&SpState!="已批"&&SpState!="暂停"&&SpState!="结束"){
						alert("只能发布已批、暂停或结束状态的需求!");
						return;
					}

					if(state=="06"&&SpState!="已发布"&&SpState!="暂停"){
						alert("只能结束已发布或暂停状态的需求!");
						return;
					}
					if(state=="del"&&SpState!="起草"&&SpState!="已报批"&&SpState!="驳回"&&SpState!="结束"){
						alert("只能删除起草、已报批、驳回、结束状态的需求!");
						return;
					}
					if(IDS.length>1)
						selects+="~"+IDS[n].value;
					else
						selects+="~"+IDS.value;				
				}
				n++;
			}
		}
		if(num==0)
		{
			<%  if(viewType.equals("2")){  %>
				alert(SELECT_PLAN+"！");
			<%  } else  if(viewType.equals("1")){ %>
				alert(PLEASE_SELECT_POSITION_REQUIREMENT+"！");
			
			<% } %>
			return;
		}

		document.employSummariseForm.action="/hire/employSummarise/hireSummarise.do?b_operate=op&ids="+selects.substring(1)+"&state="+state;
		document.employSummariseForm.submit();
		
	}
	
	
	
		 //查询
   function query()
   {
   	<%  if(viewType.equals("2")){  %>
		var In_paramters="tableName=engagePlan"; 	
	<% } else { %>	
		var In_paramters="tableName=Z03"; 	
	<%  } %>
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnQuery,functionId:'3000000104'});			
  
   }
   
   function ReturnQuery(outparamters)
   {
   		
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var extendSql=generalQuery("engagePlan",fields);
    	 
    	 if(extendSql)
    	 {
	    	 document.employSummariseForm.extendWhereSql.value=extendSql;
    	 	 employSummariseForm.action='/hire/employSummarise/hireSummarise.do?b_query=link&flag=1';
    		 employSummariseForm.submit();
    	 }
   }

    //排序
   function taxis()
   {   			
	   <%  if(viewType.equals("2")){  %>
			var In_paramters="tableName=engagePlan"; 	
		<% } else { %>	
			var In_paramters="tableName=Z03"; 	
		<%  } %>
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:ReturnTaxis,functionId:'3000000104'});			
   }

	
   function ReturnTaxis(outparamters)
   {
   		var fields_temp=outparamters.getValue("fields");		
   		var fields=new Array();
   		for(var i=0;i<fields_temp.length;i++)
   		{
   			////数组[ 0:列名 1:列描述  2:列的类型 3:如果为代码型,则为代码值,否则为空 ]
   			var a_field=fields_temp[i].split("<@>");
   			fields[i]=a_field
   		}
    	 var orderSql=taxisDialog("engagePlan",fields);
    	 if(orderSql)
    	 {
	    	 document.employSummariseForm.orderSql.value=orderSql;
    	 	  employSummariseForm.action='/hire/employSummarise/hireSummarise.do?b_query=link';
    		 employSummariseForm.submit();
    	 }
   }
	
	
	
	function executeOutFile(flag)
	{
		var table = document.getElementById('ta');
		var cellCount = table.rows[0].cells.length;
		
		var fieldWidths;
		for(var i=0;i<cellCount;i++)
		{
			fieldWidths+="/"+table.rows[0].cells[i].offsetWidth;
		}

		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		<%  if(viewType.equals("2")){  %>
		hashvo.setValue("tablename",'engagePlan');
		<% }else if(viewType.equals("1")){ %>
		hashvo.setValue("tablename",'posList');
		<% }  %>
	
	    hashvo.setValue("codeid","0");
	    hashvo.setValue("extendSql","${employSummariseForm.extendWhereSql}");
	    hashvo.setValue("orderSql","${employSummariseForm.orderSql}");
	    var In_paramters="flag="+flag;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}

	 //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	
	 function setPageFormat()
	 {
	  	var param_vo=oageoptions_selete("3","${employSummariseForm.username}");
	 }
   
   function sub()
   {
   		  employSummariseForm.action='/hire/employSummarise/hireSummarise.do?b_query=link&flag=1&operate=init';
    	  employSummariseForm.submit();
   }
   
	 function sub2(planID)
    {
   		  employSummariseForm.action='/hire/employSummarise/hireSummarise.do?b_query=link&flag=1&planID='+planID+'&operate=init';
    	  employSummariseForm.submit();
   }
   
   	function _refrash()
	{
	var dd= <%=flag%>;
	if( dd ==1){
		var planID = <%=str%>;
		employSummariseForm.action='/hire/employSummarise/hireSummarise.do?b_query=link&flag=1&planID='+planID+'&operate=init';
	    employSummariseForm.submit();  
	    }
	    else {
			var pagenum=document.getElementById("pagenum").value;
			var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
			if(!zhengzhengshu.test(pagenum)){
				alert("每页显示条数请输入正整数!");
				return;
			}  
			employSummariseForm.action="/hire/employSummarise/hireSummarise.do?b_query=link";
		    employSummariseForm.submit();
		}
	}

</script>


</HEAD>
<BODY>
<hrms:themes></hrms:themes>
<html:form action="/hire/employSummarise/hireSummarise">
<table style="margin-top:-3px;margin-left:-3px;"><tr><td>	
	<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
						<hrms:menuitem name="mitem6" label="edit_report.importPDF" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem6" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
						<hrms:menuitem name="mitem6" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
						
	</hrms:menuitem>
	<hrms:menuitem name="file" label="infor.menu.edit" >						
						<hrms:menuitem name="mitem6" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />					
	</hrms:menuitem>
	<hrms:menuitem name="file" label="system.infor.oper" >	
						<hrms:menuitem name="mitem6" label="label.commend.execute" icon="/images/link.gif" url="setState('04')"  function_id="310421"  /><!-- 发布 -->				
						<hrms:menuitem name="mitem6" label="label.commend.finish" icon="/images/ac.gif" url="setState('06')"  function_id="310421"  /><!-- 结束 -->
						<hrms:menuitem name="mitem6" label="menu.gz.delete" icon="/images/del.gif" url="setState('del')"  function_id="310421"  />	<!-- 删除 -->
					 <%  if(viewType.equals("2")){  %>	
						<hrms:menuitem name="mitem6" label="lable.zp_plan_detail.pos_id" icon="/images/table.gif" url="pos_view(1)"  />		
					 <% } %>
						<hrms:menuitem name="mitem6" label="hire.applay.person" icon="/images/man.gif" url="pos_view(2)"  />		
	</hrms:menuitem>
</hrms:menubar>
	</td></tr></table>
	<html:radio name='employSummariseForm' onclick='sub()' property='viewType' value='1' /><bean:message key="hire.requirement"/>
	<html:radio name='employSummariseForm' onclick='sub()'  property='viewType' value='2' /><bean:message key="label.zp_job.plan"/>

	<table id='ta'  width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<thead>
			
		
			<tr>
				<td align="center" class="TableRow" nowrap>
    			 <input type="checkbox" name="selbox" onclick="batch_select(this,'engagePlanListform.select');" title='<bean:message key="label.query.selectall"/>'>
	      		</td>
				<logic:iterate id="element" name="employSummariseForm" property="columnsList"  offset="0"> 
				 <td align="center" class="TableRow" nowrap>
					<bean:write name="element" property="itemdesc"   filter="false"/>
				 </td>
				</logic:iterate>
		
			</tr>	
		</thead>
		
		<% int i=0; String className="trShallow"; %>	
		<hrms:extenditerate id="element" name="employSummariseForm" property="engagePlanListform.list" indexes="indexes" pagination="engagePlanListform.pagination" pageCount="${employSummariseForm.pagerows}" scope="session">
			 <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			<tr class='<%=className%>' >
				<td align="center" class="RecordRow" nowrap>
					<hrms:checkmultibox name="employSummariseForm" property="engagePlanListform.select" value="true" indexes="indexes" />
						 <%  if(viewType.equals("2")){  %>
						<Input type='hidden' name='ID' value='<bean:write name="element" property="z0101" filter="false" />' />
						<Input type='hidden' name='SpState' value='<bean:write name="element" property="z0129" filter="false" />' />
						 <%  } else if(viewType.equals("1")){ %>
						 <Input type='hidden' name='ID' value='<bean:write name="element" property="z0301" filter="false" />' />  
						  <Input type='hidden' name='SpState' value='<bean:write name="element" property="z0319" filter="false" />' />
						 <% } %>
						
				</td>
				 <% 
           		   
           		   	 for(int a=0;a<columnsList.size();a++)
           		   	 {
           		   	 	LazyDynaBean item=(LazyDynaBean)columnsList.get(a);
           		   	 	String itemid=(String)item.get("itemid");
           		   	 	String itemtype=(String)item.get("itemtype");
           		   	 	if(itemtype.equals("N"))
           		   	 		out.print("<td align='right' class='RecordRow'  nowrap>");
           		   	 	else
           		   	 		out.print("<td align='left' class='RecordRow'  nowrap>");
           		   	 	if(itemid.equalsIgnoreCase("z0115"))
           		   	 	{
           		   	 	%>
           		   	 		<a href='javascript:sub2("<bean:write name="element" property="z0101" filter="false" />")' >
           		    
           		      <%}%>
	           		    
	           		    	<bean:write name="element" property="<%=itemid%>" filter="false" />
	           			<%
	           			 	if(itemid.equalsIgnoreCase("z0115"))
	           			 		out.println("</a>");
	           			
	           			%>
	           		
	           		    </td>
           		    
           		    <%
           		      }
           		    %>
			</tr>
		</hrms:extenditerate>
		
	</table>	
	
	
	<table width="100%"    class='RecordRowP'  align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				 <bean:message key="hmuster.label.d"/>
				<bean:write name="employSummariseForm" property="engagePlanListform.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/><bean:message key="hmuster.label.total"/>
				<bean:write name="employSummariseForm" property="engagePlanListform.pagination.count" filter="true" />
				<bean:message key="label.every.row"/> <bean:message key="hmuster.label.total"/>
				<bean:write name="employSummariseForm" property="engagePlanListform.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				 每页显示<html:text property="pagerows" name="employSummariseForm"  styleId="pagenum" styleClass="text4"  onkeypress="event.returnValue=IsDigit();"  size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="employSummariseForm" property="engagePlanListform.pagination" nameId="engagePlanListform">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>	
		<table width="100%" align="center">
		<tr>
		<td>
		<logic:equal value="dxt" name="employSummariseForm" property="returnflag">
		<hrms:tipwizardbutton flag="retain" target="il_body" formname="employSummariseForm"/> 	
		</logic:equal>
		</td>
		</tr>
		</table>
	<input type='hidden' name='extendWhereSql' value="${employSummariseForm.extendWhereSql}" />
	<input type='hidden' name='orderSql' value="${employSummariseForm.orderSql}" />
			
</html:form>

</BODY>
</html>