<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/popcalendar.js"></script>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.interviewEvaluating.InterviewArrangeForm,
				org.apache.commons.beanutils.LazyDynaBean,com.hrms.frame.utility.AdminCode" %>

<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>

<%String desc=AdminCode.getCodeName("36","11"); 
  String menuDesc="退回"+desc;

%>
<style>
.TEXT_NB {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
	
	
}
div#tbl-container 
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid; 
}
.t_cell_locked0{
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 0pt solid;
	font-size: 12px;
	margin-top:-1px;
	height:22;

	BACKGROUND-COLOR: #ffffff;
	border-collapse:collapse; 
	background-position : center left;
	left: expression(document.getElementById("tbl-container").scrollLeft+1); /*IE5+ only*/
	position: relative;
	z-index: 10;
}

</style>
   <script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'><!--
	<%
		
		if(request.getParameter("b_query")!=null&&request.getParameter("b_query").equals("0"))
		{
	%>
	//this.status ="招聘管理 / 面试安排";
	<%
	}
	else
	{
	%>
	//this.status ="招聘管理 / 面试通知";
	<% } %>
	
	//设置状态
	function setState(obj)
	{
		var dbName="${interviewArrangeForm.dbName}"
		var hashvo=new ParameterSet();
		hashvo.setValue("id",obj.name);
		hashvo.setValue("state",obj.value);
		hashvo.setValue("dbName",dbName);
		hashvo.setValue("isMailField","${interviewArrangeForm.isMailField}");
		var In_paramters="flag=2";  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfoRefresh,functionId:'3000000123'},hashvo);
	}
	function returnInfoRefresh(outparameters)
	{
	     interviewArrangeForm.action='/hire/interviewEvaluating/interviewArrange.do?b_query=${interviewArrangeForm.linkDesc}';
    	 interviewArrangeForm.submit();
	}

	function returnInfo(outparamters)
	{
	}
	function sendMail(id,a0100,zp_pos_id){
	
	 var dbName="${interviewArrangeForm.dbName}";
     var isMailField="${interviewArrangeForm.isMailField}";
     var iWidth = 650;
     var iHeight = 580;
     var iTop = (window.screen.availHeight-30-iHeight)/2;
     var iLeft = (window.screen.availWidth-10-iWidth)/2;
   	 //window.open("/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init&zpbatch=&zploop=2&dbname="+dbName+"&id="+id+"&isMailField="+isMailField+"&a0100="+a0100+"&zp_pos_id="+zp_pos_id,"_blank","hotkeys=0,menubar=no,height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,location=no,status=no,resizable=no");
   	 var strurl="/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init`zpbatch=`zploop=2`dbname="+dbName+"`id="+id+"`isMailField="+isMailField+"`a0100="+a0100+"`zp_pos_id="+zp_pos_id;
     var iframe_url="/templates/index/iframe_query.jsp?src="+strurl; 
     var flag=window.showModalDialog(iframe_url, arguments, "dialogWidth:"+iWidth+"px; dialogHeight:"+iHeight+"px;top="+iTop+";left="+iLeft+";resizable:no;center:yes;scroll:no;status:no;toolbar=no;location=no;hotkeys=0;menubar=no"); 
     _refrash();
 	}
	function sendMailKG(id,a0100,zp_pos_id){
	
	 var dbName="${interviewArrangeForm.dbName}";
     var isMailField="${interviewArrangeForm.isMailField}";
     var iWidth = 650;
     var iHeight = 580;
     var iTop = (window.screen.availHeight-30-iHeight)/2;
     var iLeft = (window.screen.availWidth-10-iWidth)/2;
      var strurl="/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init`zploop=3`dbname="+dbName+"`id="+id+"`isMailField="+isMailField+"`a0100="+a0100+"`zp_pos_id="+zp_pos_id;
     var iframe_url="/templates/index/iframe_query.jsp?src="+strurl; 
     var flag=window.showModalDialog(iframe_url, arguments, "dialogWidth:"+iWidth+"px; dialogHeight:"+iHeight+"px;top="+iTop+";left="+iLeft+";resizable:no;center:yes;scroll:no;status:no;toolbar=no;location=no;hotkeys=0;menubar=no"); 
     _refrash();
  	}
	
	//选择考官
	function selectEmployer(obj)
	{
		//alert(obj.name+"  "+obj.value);
		var dbpre_str="${interviewArrangeForm.dbpre_str}";
		if(""==dbpre_str){///zzk 2014/1/23
			alert("没有认证人员库权限！");
			return;
		}
		var target_url="/selfservice/lawbase/add_law_text_role.do?b_relating=link&pri=0&chkflag=8&z0501="+obj.name+"&dbpre_str="+dbpre_str;
  	    //var return_vo=window.open(target_url,'glWin','toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,top=170,left=220,width=560,height=370'); 
  	    var return_vo= window.showModalDialog(target_url,null,"dialogWidth:560px; dialogHeight:380px;resizable:no;center:yes;scroll:yes;status:no");
	   
		// var return_vo=select_org_emp_byname_dialog(1,1,0,0); 
		 var z0501s=""; 	
		 if(return_vo)
		 {  
		 	var a_textValue="";
		 	var textValue=return_vo.title.split(",");
		 	var contents=return_vo.content.split(",");
		 	var num=0;
		 	for(var i=0;i<contents.length;i++)
		 	{
		 	    if(contents[i]=='')
		 	    {
		 	      continue;
		 	    }
		 	    //选考官 对于人名（00xxx） 只取人名展示
		 		if(textValue[i].indexOf("(")!=-1){
		 			textValue[i]=textValue[i].substring(0,textValue[i].indexOf("("));
		 		}
		 		
		 		if((z0501s+contents[i]+",").length>250){///数据库 z05表考官字段z0505长度为250字符 故加以控制
		 			alert("考官人数过多，"+textValue[i-1]+"以后人员不能选取！");
		 			break;
		 		}
		 		a_textValue+=textValue[i]+",";
		 		z0501s+=contents[i]+",";

		 		
		 	} 	
		 	obj.value=a_textValue; 
		 	var dbName="${interviewArrangeForm.dbName}"
			var hashvo=new ParameterSet();
			hashvo.setValue("id",obj.name);
			hashvo.setValue("value",z0501s);
			hashvo.setValue("dbName",dbName);
			var In_paramters="type=A";  
		   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000128'},hashvo);	
		 }
			 
	}
	
	function searchInterview()
	{
	   var codeid="${interviewArrangeForm.codeID}";
	   codeid=getEncodeStr(codeid);//dml 2011-04-01
	   var url="/hire/interviewEvaluating/interviewRevert.do?b_interview=view`type=1`code="+codeid;
	   //window.open(url,"_blank");
       var iframe_url="/general/query/common/iframe_query.jsp?src="+url; 
       var returnValue=window.showModalDialog(iframe_url,null,"dialogWidth:600px; dialogHeight:450px;resizable:no;center:yes;scroll:no;status:no");
	   
	}
	
	
	//保存值
	function save(type,obj)
	{
		var dbName="${interviewArrangeForm.dbName}";
		var value=obj.value;
		if(type=='D')
		{
	       if(value.length>19)
	       {
	          alert("输入的时间格式应为 yyyy-mm-dd hh:mm 或者 yyyy-mm-dd hh:mm:ss");
		      return;
	       }
		   if(value.length==19)
		   {
		      obj.value=value.substring(0,16);
		      value=value.substring(0,16);
		   }
		   var  regx=/^\d{4}-\d{2}-\d{2} \d{2}:\d{2}$/;
		   if(value.length!=0&&!regx.test(obj.value))
		   {
		      alert("输入的时间格式应为 yyyy-mm-dd hh:mm 或者 yyyy-mm-dd hh:mm:ss");
		     // obj.value="";
		     // obj.focus();
		      return;
		   }
		   if(value.length!=0&&value.length<=16)
		      value=value+":00";
		}
		var hashvo=new ParameterSet();
		hashvo.setValue("id",obj.name);
		hashvo.setValue("value",value);
		hashvo.setValue("dbName",dbName);
		var In_paramters="type="+type;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000128'},hashvo);
	}
function saveC(itemid,z0501,obj)
{
  var dbName="${interviewArrangeForm.dbName}";
  var hashvo=new ParameterSet();
  hashvo.setValue("id",z0501+"/"+itemid);
  hashvo.setValue("value",document.getElementById(z0501+"/"+itemid).value);
  hashvo.setValue("dbName",dbName);
  var In_paramters="type=A";  
  var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000128'},hashvo);
  
}

		 //查询
   function query()
   {
		var In_paramters="tableName=z05"; 	
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
	    	 document.interviewArrangeForm.extendWhereSql.value=extendSql;
    	 	 interviewArrangeForm.action='/hire/interviewEvaluating/interviewArrange.do?b_query=${interviewArrangeForm.linkDesc}&opt=firstPage';
    		 interviewArrangeForm.submit();
    	 }
   }


	 //排序
   function taxis()
   {
   		var In_paramters="tableName=z05"; 	
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
    	 var orderSql=taxisDialog("Z05",fields);
    	 if(orderSql)
    	 { 
	    	 document.interviewArrangeForm.orderSql.value=orderSql;
    	 	 interviewArrangeForm.action='/hire/interviewEvaluating/interviewArrange.do?b_query=${interviewArrangeForm.linkDesc}';
    		 interviewArrangeForm.submit();
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
		hashvo.setValue("tablename",'z05');
	    hashvo.setValue("codeid","${interviewArrangeForm.codeID}");
	    hashvo.setValue("extendSql","${interviewArrangeForm.extendWhereSql}");
	    hashvo.setValue("orderSql","${interviewArrangeForm.orderSql}");
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
   		
   		var param_vo=oageoptions_selete("3","${interviewArrangeForm.username}");
   
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
   
   
   function batchOperate()
   {
   		var selects=eval("document.interviewArrangeForm.id");
   		var selectIds="";
   		if(selects.length)
   		{
	   		for(var i=0;i<selects.length;i++)
	   		{
	   			if(selects[i].checked==true)
	   				selectIds+=selects[i].value+"#"	;
	   		}
	   	}
	   	else
	   	{
	   		if(selects.checked==true)
	   			selectIds+=selects.value+"#"	;
	    }
	    if(selectIds=="")
   		{
   			alert(SELECT_INTERRELATED_PERSON+"!");
   			return;
   		}
   		var aselects=batch_selectInfo();//批量修改
    	if(aselects&&aselects.length>0)
    	{  
    		document.interviewArrangeForm.selectIDs.value=selectIds;
    		//if(aselects[0] != null)
	    		document.interviewArrangeForm.zpdd.value=aselects[0];
    		//if(aselects[1] != null)
    			document.interviewArrangeForm.zykg.value=aselects[1];
    		//if(aselects[2] != null)
    			document.interviewArrangeForm.wykg.value=aselects[2];
    		//if(aselects[3] != null)
    			document.interviewArrangeForm.mmsj.value=aselects[3];
    		//if(aselects[4] != null)
    			document.interviewArrangeForm.state.value=aselects[4];
   			interviewArrangeForm.action="/hire/interviewEvaluating/interviewArrange.do?b_batchSave=save&b_query=${interviewArrangeForm.linkDesc}";
    		interviewArrangeForm.submit();
    	}
   }
 function thdx()
 {
       var selects=eval("document.interviewArrangeForm.id");
   		var selectIds="";
   		if(selects.length)
   		{
	   		for(var i=0;i<selects.length;i++)
	   		{
	   			if(selects[i].checked==true)
	   				selectIds+=selects[i].value+"#"	;
	   		}
	   	}
	   	else
	   	{
	   		if(selects.checked==true)
	   			selectIds+=selects.value+"#"	;
	    }
	    if(selectIds=="")
   		{
   			alert(SELECT_INTERRELATED_PERSON+"!");
   			return;
   		}
   		if(confirm("<%=menuDesc%>将删除面试安排信息，并将简历状态置为<%=desc%>\r\n确定执行操作？"))
   		{
   	    	var hashvo=new ParameterSet();
   	    	hashvo.setValue("z0501",selectIds);
   	    	var In_paramters="flag=1"; 	
   	    	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:thok,functionId:'3020072012'},hashvo);	
	    }		
   		
 }
 function thok(outparameters)
 {
     interviewArrangeForm.action='/hire/interviewEvaluating/interviewArrange.do?b_query=${interviewArrangeForm.linkDesc}';
     interviewArrangeForm.submit();
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
function batchsendMail(){
	 var dbName="${interviewArrangeForm.dbName}";
     var isMailField="${interviewArrangeForm.isMailField}";
     var iframe_url="/general/query/common/iframe_query.jsp?src="+"/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init`zploop=2`dbname="+dbName+"`isMailField="+isMailField+"`zpbatch=2`codeid=${interviewArrangeForm.codeID}`extendWhereSql1=${extendWhereSql1}"; 
     var returnValue=window.showModalDialog(iframe_url,null,"dialogWidth:700px; dialogHeight:550px;resizable:no;center:yes;scroll:no;status:no");//dml 2011年8月23日11:32:27
     if(returnValue){
         window.location.href=window.location.href;
     }
   	// window.open("/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init&zploop=2&dbname="+dbName+"&isMailField="+isMailField+"&zpbatch=2&codeid=${interviewArrangeForm.codeID}&extendWhereSql1="+selectIds,"_blank","hotkeys=0,menubar=0,height=470,width=700");
	}
	function _refrash()
	{   
		var pagenum=document.getElementById("pagenum").value;
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		if(!zhengzhengshu.test(pagenum)){
			alert("每页显示条数请输入正整数!");
			return;
		}
		interviewArrangeForm.action="/hire/interviewEvaluating/interviewArrange.do?b_query=link";
	    interviewArrangeForm.submit();
	}
--></script>
<%
	InterviewArrangeForm interviewArrangeForm=(InterviewArrangeForm)session.getAttribute("interviewArrangeForm");
	String interviewingRevertItemid=interviewArrangeForm.getInterviewingRevertItemid();	
%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag= userView.getBosflag();//得到系统的版本号
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
<hrms:themes></hrms:themes>
<html:form action="/hire/interviewEvaluating/interviewArrange">
<table border='0' width="100%" style="margin-top:-3px;margin-left:-3px;">
	<tr>
		<td>
		 <hrms:menubar menu="menu1" id="menubar1">
			<hrms:menuitem name="file" label="menu.file.label" >						
								<hrms:menuitem name="mitem1" label="button.createpdf" icon="/images/print.gif" url="executeOutFile(1)"  />
								<hrms:menuitem name="mitem2" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
								<hrms:menuitem name="mitem3" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
								<hrms:menuitem name="mitem3" label="群发消息" icon="/images/prop_ps.gif" url="batchsendMail()"  />
			</hrms:menuitem>
			<hrms:menuitem name="file" label="label.commend.edit" >						
								<hrms:menuitem name="mitem4" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
								<hrms:menuitem name="mitem5" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />	
								<hrms:menuitem name="mitem6" label="menu.gz.batch.update" icon="/images/edit.gif" url="batchOperate()"  /> 
								<hrms:menuitem name="mitem7" label="<%=menuDesc%>"  url="thdx();" icon="/images/add_del.gif" /> 
								<%
		
		if(request.getParameter("b_query")!=null&&request.getParameter("b_query").equals("0"))
		{
	    }
	    else
	    {
	     if(!(interviewingRevertItemid.equals("")||interviewingRevertItemid.equals("#")))
	     {
	    %>
    	<hrms:menuitem name="mitem7" label="hire.interview.revert" icon="/images/check.gif" url="searchInterview();"  />   	  									
	   <% 
	   }
	    }
	   %>
								
			</hrms:menuitem>		
		</hrms:menubar>
	    </td>
	</tr>
</table>

	<table id='ta'  width="100%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">

		<thead>
			<tr>
				<td align="center" class="TableRow" nowrap>
    			 <input type="checkbox" name="selbox" onclick="batch_select(this,'id');" title='<bean:message key="label.query.selectall"/>'>
	      		</td>
	      		<%int j=1;%>

				<logic:iterate id="element" name="interviewArrangeForm" property="columnsList"  offset="1"> 
				<%
				  String styleClass="TableRow";
	      		  if(j==1){
	      		  	styleClass="TableRow";
	      		  } %>
				 <td align="center" class="<%=styleClass%>" nowrap>
					<bean:write name="element" property="itemdesc"   filter="false"/>
				 </td>
				 <%j++; %>
				</logic:iterate>
		
			</tr>	
		</thead>
		<% int i=0; String className="trShallow"; %>	
		<hrms:extenditerate id="element" name="interviewArrangeForm" property="interviewArrangeListform.list" indexes="indexes" pagination="interviewArrangeListform.pagination" pageCount="${interviewArrangeForm.pagerows}" scope="session">
	
			 <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			<tr class='<%=className%>' id="<%="tr_"+i %>" onclick="changeColor('<%=i%>','<%=className%>')">
				 <% 
           		   	 
           		   	 ArrayList columnsList=interviewArrangeForm.getColumnsList();
           		   	 for(int a=0;a<columnsList.size();a++)
           		   	 {
           		   	 	LazyDynaBean abean=(LazyDynaBean)columnsList.get(a);
           		   	 	String itemid=(String)abean.get("itemid");
           		   	 	String csclass="RecordRow";
           		   	 	if(a==0||a==1){
           		   	 		csclass="RecordRow";
           		   	 	}
           		   	 	
           		    %>
	           		    <td align="center"  class="<%=csclass%>" nowrap>
	           		    	<bean:write name="element" property="<%=itemid%>" filter="false" />
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
				<bean:write name="interviewArrangeForm" property="interviewArrangeListform.pagination.current" filter="true" />
				<bean:message key="hmuster.label.paper"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="interviewArrangeForm" property="interviewArrangeListform.pagination.count" filter="true" />
				<bean:message key="label.every.row"/>
				<bean:message key="hmuster.label.total"/>
				<bean:write name="interviewArrangeForm" property="interviewArrangeListform.pagination.pages" filter="true" />
				<bean:message key="hmuster.label.paper"/>
			    
				 每页显示<html:text property="pagerows" name="interviewArrangeForm" styleId="pagenum" size="3" onkeypress="event.returnValue=IsDigit();" styleClass="text4" ></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="interviewArrangeForm" property="interviewArrangeListform.pagination" nameId="interviewArrangeListform">
					</hrms:paginationlink>
			</td>
		</tr>
		
	</table>
	<table  width="100%"  align='center' >
	<tr>
		<td colspan="2">
		<logic:equal value="dxt" name="interviewArrangeForm" property="returnflag">
		<hrms:tipwizardbutton flag="retain" target="il_body" formname="interviewArrangeForm"/> 
		</logic:equal>
		</td>
		</tr>
		</table>

			
	<input type='hidden' name='selectIDs' value=' ' />
	<input type='hidden' name='zpdd' value=' ' />
	<input type='hidden' name='zykg' value=' ' />
	<input type='hidden' name='wykg' value=' ' />
	<input type='hidden' name='mmsj' value=' ' />
	<input type='hidden' name='state' value='' />
			
			
	<input type='hidden' name='extendWhereSql' value="${interviewArrangeForm.extendWhereSql}" />
	<input type='hidden' name='orderSql' value="${interviewArrangeForm.orderSql}" />		
			
			
</html:form>