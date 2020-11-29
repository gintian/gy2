<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.interviewEvaluating.InterviewExamineForm,
				org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
				com.hrms.hjsj.sys.Des" %>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<html>
<HEAD>
<% 
           		   	 InterviewExamineForm interviewExamineForm=(InterviewExamineForm)session.getAttribute("interviewExamineForm");
           		   	 ArrayList columnsList=interviewExamineForm.getTableColumnsList();
				   	 String code=interviewExamineForm.getSumrise();
					
%>

<% 
	
					String str= request.getParameter("z0101");
					
%>

<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
%>  


<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'>
	//this.status ="招聘管理 / 面试考核";
	 function replaceAll( str, from, to ) {
	    var idx = str.indexOf( from );
	    while ( idx > -1 ) {
	        str = str.replace( from, to ); 
	        idx = str.indexOf( from );
	    }
	   
	    return str;
	}


	//拟录用
	function hire(state)
	{	
		var selects="";
		var n=0;
		for(var i=0;i<document.forms[0].elements.length;i++)
		{
			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].name!='selbox')
			{
				if(document.forms[0].elements[i].checked==true)
				{
					selects+="/"+n;
					
				}
				n++;
			}
		}
		var IDS=document.getElementsByName("ID");
		var selected=new Array();
		var arr=selects.split("/");
		var obj=new Array();
		var n=0;
		for(var i=1;i<arr.length;i++)
		{
			var desc=IDS[arr[i]].value;
			var aa=desc.split("/");
			if(aa[3]=='41')
				continue;
			obj[n++]=IDS[arr[i]].value;			
		}	
		
		if(obj.length==0)
		{
			alert(SELECT_PERSON_OPTIONS+"！");
			return;	
		}
			
		var hashvo=new ParameterSet();
		hashvo.setValue("select",obj);
		var In_paramters="flag=state";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:validate,functionId:'3000000134'},hashvo);
		
		
	}
	
	
	function validate(outparamters)
	{
		var info=outparamters.getValue("info");
		if(info.length>0)
		{
			info=replaceAll(info,"#","\r\n")
		
			if(confirm(info))
			{
			   setState('41');
			}
			else
			{
				return;
			}
		}
		else
		{
			setState('41');
		}
	
	}

	function setState(state)
	{   
	var n=0;
   		for(var i=0;i<document.forms[0].elements.length;i++)
   		{
   			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true&&document.forms[0].elements[i].name!='selbox')
   			{
   				n++;
   			}
   		
   		}
   		if(n==0)
   		{
   			alert(SELECT_KP_OBJECT+"！");
   			return;
   		}
	    if(state=='del')
	    {
	       if(!confirm("确认删除？"))
	       {
	          return;
	       }
	    }
		interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_setState=set&state="+state
		interviewExamineForm.submit();
	}

		 //查询
   function query()
   {
		var In_paramters="tableName=zp_test_template"; 	
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
    	 var extendSql=generalQuery("Z05",fields);
    	 
    	 if(extendSql)
    	 {
	    	 document.interviewExamineForm.extendSql.value=extendSql;
    	 	 interviewExamineForm.action='/hire/interviewEvaluating/interviewExamine.do?b_query=${interviewExamineForm.linkDesc}';
    		 interviewExamineForm.submit();
    	 }
   }

    //排序
   function taxis()
   {
   		var In_paramters="tableName=zp_test_template"; 	
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
    	 var orderSql=taxisDialog("zp_test_template",fields);
    	 if(orderSql)
    	 {
	    	 document.interviewExamineForm.orderSql.value=orderSql;
    	 	  interviewExamineForm.action='/hire/interviewEvaluating/interviewExamine.do?b_query=${interviewExamineForm.linkDesc}';
    		 interviewExamineForm.submit();
    	 }
   }


	function executeOutFile2(flag)
	{
		var table = document.getElementById('ta');
		if(table.rows.length==1){
		    alert(NO_DATA_NO_OPERATOR+"!");
			return;
			}
		
		var cellCount = table.rows[1].cells.length;
		
		var fieldWidths;
		for(var i=0;i<cellCount;i++)
		{
			fieldWidths+="/"+table.rows[1].cells[i].offsetWidth;
		}
		
		var hashvo=new ParameterSet();
		hashvo.setValue("viewType","<%=(request.getParameter("viewType"))%>");
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename",'zp_test_template2');
	    hashvo.setValue("codeid","${interviewExamineForm.codeid}");
	    hashvo.setValue("extendSql","${interviewExamineForm.extendSql}");
	    hashvo.setValue("orderSql","${interviewExamineForm.orderSql}");
	    hashvo.setValue("z0101","${interviewExamineForm.z0101}");
	    hashvo.setValue("str","<%=code%>");
	    var In_paramters="flag="+flag;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	
	}

   
	function executeOutFile(flag)
	{
		var table = document.getElementById('ta');
		if(table.rows.length==1)
		   return;
		
		var cellCount = table.rows[1].cells.length;
		
		var fieldWidths;
		for(var i=0;i<cellCount;i++)
		{
			fieldWidths+="/"+table.rows[1].cells[i].offsetWidth;
		}

		var hashvo=new ParameterSet();
		hashvo.setValue("fieldWidths",fieldWidths.substring(10));
		hashvo.setValue("tablename",'zp_test_template');
	    hashvo.setValue("codeid","${interviewExamineForm.codeid}");
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
   //评测
   function grade()
   {
   		var n=0;
   		for(var i=0;i<document.forms[0].elements.length;i++)
   		{
   			if(document.forms[0].elements[i].type=='checkbox'&&document.forms[0].elements[i].checked==true&&document.forms[0].elements[i].name!='selbox')
   			{
   				n++;
   			}
   		
   		}
   		if(n>1)
   		{
   			alert(ONE_TIME_ONE_PERSON+"！");
   			return;
   		}
   		if(n==0)
   		{
   			alert(SELECT_KP_OBJECT+"！");
   			return;
   		}
   		document.interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_grade=grade";
   		document.interviewExamineForm.submit();
   		
   }
	
   function lookParticularGrade(ID,name)
   {
   		document.interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_allGrade=grade&a0100="+ID+"&name="+name;
   		document.interviewExamineForm.submit();
   }
   function lookParticularGradeForAndVance(ID,name,itemid){
  		document.interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_allGrade=grade&a0100="+ID+"&name="+name+"&itemid="+itemid;
   		document.interviewExamineForm.submit();
   }

   function goBack()
   {
  	    document.interviewExamineForm.extendSql.value="";
  	    document.interviewExamineForm.orderSql.value="";
   		document.interviewExamineForm.action="/hire/employSummarise/hireSummarise.do?b_query=link&operate=init";
   		document.interviewExamineForm.submit();
   
   }

	function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${interviewExamineForm.username}");
   
   }
   
    
   function goback(filk)
   {
   		var hashvo=new ParameterSet();
   		hashvo.setValue("flag",filk);
   		var In_paramters="flag="+filk; 	
		var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:goback2,functionId:'3000000148'});			

   }
   function goback2(outparamters)
   {	
   		window.open("/templates/menu/hire_m_menu2.do?b_query=link&module=7","i_body")
   }
   
   
   
   function resumeBrowse(a0100,dbname,zp_pos_id)
   {
   	 	window.open("/hire/employNetPortal/search_zp_position.do?b_resumeBrowse=browse&dbName="+dbname+"&a0100="+a0100+"&zp_pos_id="+zp_pos_id+"&personType=4","_blank");
   }
   function interviewEval(a0100,table)
   {
   		var theurl="/hire/interviewEvaluating/interviewExamine.do?b_evalu=link`a0100="+a0100;
		var iframe_url="/general/query/common/iframe_query.jsp?src="+theurl;
  		var returnVo='';
       	if(isIE6()){
       	 returnVo= window.showModalDialog(iframe_url, 'interview_win', 
	      				"dialogWidth:"+(window.screen.width-280)+"px; dialogHeight:"+(window.screen.height-450)+"px;resizable:no;center:yes;scroll:yes;status:no");
       	}else{
		 returnVo= window.showModalDialog(iframe_url, 'interview_win', 
	      				"dialogWidth:"+(window.screen.width-300)+"px; dialogHeight:"+(window.screen.height-450)+"px;resizable:no;center:yes;scroll:yes;status:no");
       	}
  
   }
   
   var preIndex="-1";
   function changeColor(index,className)
{
   var obj = document.getElementById("tr_"+index);
   if(preIndex!='-1'){
       if(preIndex%2==0)
       {
           document.getElementById("tr_"+preIndex).style.backgroundColor="";
       }
       else
       {
           document.getElementById("tr_"+preIndex).style.backgroundColor="";
       }
    }   
   obj.style.backgroundColor='#FFF8D2';
   preIndex=index;
}
	
	function _refrash()
	{
		var pagenum=document.getElementById("pagenum").value;
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		if(!zhengzhengshu.test(pagenum)){
			alert("每页显示条数请输入正整数!");
			return;
		}
	var z0101 = '<%=str%>' ;
	//标志，判断从哪个页面进入进行刷新
	var flg = '<%= request.getParameter("summary")%>';
	if(flg!=null&&flg=="summary"){
		//招聘总结中招聘人员界面刷新
		interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_query=link&viewType=1&codeset=UN&z0101="+z0101+"&code=summarise&model=7&operate=init&summary=summary";
	}else{
		//面试考核刷新
		interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_query=link";//&viewType=1&codeset=UN&z0101="+z0101+"&code=summarise&model=7&operate=init";
	}
	   interviewExamineForm.submit();
	}
	/*
	function _refrash()
	{   
		var pagenum=document.getElementById("pagenum").value;
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		if(!zhengzhengshu.test(pagenum)){
			alert("每页显示条数请输入正整数!");
			return;
		}
		interviewExamineForm.action="/hire/interviewEvaluating/interviewExamine.do?b_query=link";
	    interviewExamineForm.submit();
	}
	*/
</script>
<%
    if(bosflag!=null&&bosflag.equals("hcm")){
%>
<style>
.menubar{
    margin-top:0px;
    margin-left:0px;
}
</style>
<%
    }
%>
</HEAD>
<BODY>
<hrms:themes></hrms:themes>
<html:form action="/hire/interviewEvaluating/interviewExamine">

<% if(!code.equals("summarise"))	{ %>
<table width="100%" style="margin-top:-3px;margin-left:-3px;"><tr><td>	
	<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file" >						
						<hrms:menuitem name="mitem6" label="button.cardpdf" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem6" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
						<hrms:menuitem name="mitem6" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
	
	</hrms:menuitem>
	<hrms:menuitem name="file" label="edit_report.status.bj" >						
						<hrms:menuitem name="mitem6" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />					
	</hrms:menuitem>
	<hrms:menuitem name="file" label="system.infor.oper" >						
						<hrms:menuitem name="mitem6" label="hire.exam" icon="/images/check.gif" url="grade()"  function_id="310331,0A0901"  />	
						<hrms:menuitem name="mitem6" label="hire.first.exam" icon="/images/check.gif" url="setState(31)"  function_id="310332,0A0902"  />
						<hrms:menuitem name="mitem6" label="hire.second.exam" icon="/images/check.gif" url="setState(32)"  function_id="310332,0A0902"  />
						<hrms:menuitem name="mitem6" label="hire.simulate.ly" icon="/images/check.gif" url="hire(41)"  function_id="310332,0A0902"  />
						<hrms:menuitem name="mitem6" label="hire.no.through" icon="/images/check.gif" url="setState(13)"  function_id="310332,0A0902"  />	
						<hrms:menuitem name="mitem6" label="lable.tz_template.delete" icon="/images/del.gif" url="setState('del')"  function_id="310332,0A0902"  />
										
	</hrms:menuitem>
	
</hrms:menubar>
	</td></tr></table>

<% } else {%>
<table width="100%" style="margin-top:-3px;margin-left:-3px;"><tr><td>  
	<input type='button' value='<bean:message key="button.createescel"/>' class='mybutton' onclick='executeOutFile2(2)' />
	<input type='button' value='<bean:message key="button.createpdf"/>' class='mybutton' onclick='executeOutFile2(1)' />
	<input type='button' value='<bean:message key="kq.emp.button.return"/>' class='mybutton' onclick='goBack()' />
</td></tr></table>
<% } %>
	<table id='ta'  width="100%"  border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
		<thead>
			 
			<tr> 
				<% int j=0;%>
				<td align="center" class="TableRow" nowrap>
    			 <input type="checkbox" name="selbox" onclick="batch_select(this,'interviewExamineListform.select');" title='<bean:message key="label.query.selectall"/>'>
	      		</td>
				<logic:iterate id="element" name="interviewExamineForm" property="tableColumnsList"  offset="1">
					<% j++;
					   if(j==2){
					%>
					
					<logic:equal name="interviewExamineForm" property="examineNeedRecord" value="1">
					 	<td align="center" class="TableRow" nowrap>
							&nbsp;&nbsp;<bean:message key="hire.interview.evaluation" />&nbsp;&nbsp;
						 </td>
					</logic:equal>
					<% }%>
				 <td align="center" class="TableRow" nowrap>
					&nbsp;&nbsp;<bean:write name="element" property="itemdesc"   filter="false"/>&nbsp;&nbsp;
				 </td>
				</logic:iterate>
		
			</tr>	
		</thead>
		
		<% int i=0; String className="trShallow"; %>	
		<hrms:extenditerate id="element" name="interviewExamineForm" property="interviewExamineListform.list" indexes="indexes" pagination="interviewExamineListform.pagination" pageCount="${interviewExamineForm.pagerows}" scope="session">
			 <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			<tr class='<%=className%>' id="<%="tr_"+i %>" onclick="changeColor('<%=i%>','<%=className%>')">
				<td align="center" class="RecordRow" nowrap>
					&nbsp;<hrms:checkmultibox name="interviewExamineForm" property="interviewExamineListform.select" value="true" indexes="indexes" />&nbsp;
				    <INPUT type='hidden' name='ID' value='<bean:write name="element" property="z0301" filter="false" />/<bean:write name="element" property="z0315" filter="false" />/<bean:write name="element" property="a_z0311" filter="false" />/<bean:write name="element" property="a_state" filter="false" />' >
				</td>
				 <% 
           		   
           		   	 for(int a=1;a<columnsList.size();a++)
           		   	 {
           		   	 	LazyDynaBean abean=(LazyDynaBean)columnsList.get(a);
           		   	 	String itemid=(String)abean.get("itemid");
           		   	 	if(a==2){
           		    %>
           		    	<logic:equal name="interviewExamineForm" property="examineNeedRecord" value="1">
           		    		<td align="center" class="RecordRow"  nowrap>
           		    			<img src="/images/edit.gif" BORDER="0" style="cursor:hand;"
									onclick="interviewEval('<bean:write name="element" property="a0100" filter="true"/>','${interviewExamineForm.examineNeedRecordSet}')">
           		    		</td>
           		    	</logic:equal>
           		    	<% }%>
	           		    <td align="center" class="RecordRow"  nowrap>
	           		    <%
	           		    LazyDynaBean abean2=(LazyDynaBean)pageContext.getAttribute("element");
	           		    Des des=new Des();
	           		    String a0100 = (String)abean2.get("a0100");
	           		    String z0301 = (String)abean2.get("z0301");
	           		    String dbName = interviewExamineForm.getDbName();
	                    //a0100 = PubFunc.encryption(a0100);
                        //z0301 = PubFunc.encryption(z0301);
                        //dbName = PubFunc.encryption(dbName);
	           		    if(itemid.equalsIgnoreCase("a0101"))
	           		   	{
	           		    %>
	           		    <a href='javascript:resumeBrowse("<%=a0100%>","<%=dbName%>","<%=z0301%>")'>
	           		    <%} %>
	           		    	&nbsp;<bean:write name="element" property="<%=itemid%>" filter="false" />&nbsp;
	           		    	<%  
	           		    if(itemid.equalsIgnoreCase("a0101"))
	           		    	out.print("</a>");
	           		    %>
	           		    </td>
           		    
           		    <%
           		      }
           		    %>
           		  
						
			</tr>
		</hrms:extenditerate>
		
	</table>	
	
	
	<table  width="100%"  class='RecordRowP'  align='center' >
		<tr>
			<td valign="bottom" class="tdFontcolor">
				<bean:message key="hmuster.label.d"/>
				<bean:write name="interviewExamineForm" property="interviewExamineListform.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="interviewExamineForm" property="interviewExamineListform.pagination.count" filter="true" />
				<bean:message key="label.every.row"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="interviewExamineForm" property="interviewExamineListform.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				 每页显示<html:text property="pagerows" name="interviewExamineForm"  styleId="pagenum" styleClass="text4"  onkeypress="event.returnValue=IsDigit();"  size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="interviewExamineForm" property="interviewExamineListform.pagination" nameId="interviewExamineListform">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>
	<table  width="100%"  align='center' style="margin-left:-5px;">
	<tr>
	<td>	
	<logic:equal value="dxt" name="interviewExamineForm" property="returnflag">
	<hrms:tipwizardbutton flag="retain" target="il_body" formname="interviewExamineForm"/> 	
	</logic:equal>
	</td>
	</tr>
	</table>
	<input type='hidden' name='extendSql' value="${interviewExamineForm.extendSql}" />
	<input type='hidden' name='orderSql' value="${interviewExamineForm.orderSql}" />		
</html:form>

</BODY>
</html>