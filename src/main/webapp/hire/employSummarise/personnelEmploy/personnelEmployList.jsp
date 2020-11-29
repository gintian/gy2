<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="JavaScript" src="/js/constant.js"></script>
<SCRIPT Language="JavaScript">dateFormat='yyyy-mm-dd'</SCRIPT>
<%@ page import="java.util.*,
				com.hjsj.hrms.actionform.hire.employSummarise.PersonnelEmployForm,
				org.apache.commons.beanutils.LazyDynaBean,com.hjsj.hrms.utils.PubFunc,
				com.hrms.hjsj.sys.Des" %>
<html>
<HEAD>

<style>
.TEXT_NB1 {
	BACKGROUND-COLOR:transparent;
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: medium none; 
	BORDER-RIGHT: medium none; 
	BORDER-TOP: medium none;
}
</style>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script language='javascript'>
	//this.status ="招聘管理 / 员工录用";


	//保存报到时间
	function save(type,obj)
	{
		var dbName="${personnelEmployForm.dbName}";
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
		   if(value.length!=0&&!regx.test(value))
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
		hashvo.setValue("id",obj.name+"/z0513");
		hashvo.setValue("value",value);
		hashvo.setValue("dbName",dbName);
		var In_paramters="type="+type;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:returnInfo,functionId:'3000000128'},hashvo);
	}
	
	function returnInfo(outparamters)
	{
	
	}
	//设置状态
	function setState(state)
	{
	 	var toDbname=" ";
		if(state==43)
		{
			//var strurl="/hire/employSummarise/personnelEmploy.do?b_dbname=link";
			//var obj=window.showModalDialog(strurl,null,"dialogWidth=350px;dialogHeight=260px;resizable=yes;status=no;"); 
			//if(obj!=null)
			//{
			var dbName="${personnelEmployForm.dbName}";
			var type="${personnelEmployForm.type}";
			var obj=document.getElementById("temid");
			var id="";
			if("1"==type&&obj.options.length==0){
				alert("请选择模板!");
				return;
			}
			for(var i=0;i<obj.options.length;i++)
			{
			  if(obj.options[i].selected)
			  {
		    	  id=obj.options[i].value;
		    	  break;
		      }	  
			}
			
	        	var a0100="";
                var a0100s=document.getElementsByName("a0100");//eval("document.personnelEmployForm.a0100");
                var selectIndex=0;
		        var checkNum=0;
		        var num=0;
		        var a0100Array=new Array();
		       for(var i=0;i<document.personnelEmployForm.elements.length;i++)
  		       {
  			     if(document.personnelEmployForm.elements[i].type=='checkbox'&&document.personnelEmployForm.elements[i].name!='selbox')
  			      {
  			      	if(document.personnelEmployForm.elements[i].checked==true)
  			    	{
  			     		selectIndex=checkNum;
  			    		a0100Array.push(dbName+a0100s[selectIndex].value);
  			    		num++;
  			    	}
  			    	checkNum++;
  	    		}
  	    	  }
  	    	     if(num==0)
  	    	    {
  	    	    	alert(SELECT_PERSON+"!");
  	    	        return;
  	    	    }
  	    	  if(type=='1')
			 {
  	    	    var hashvo=new ParameterSet();
       		    hashvo.setValue("nbase",dbName);	
       	    	hashvo.setValue("a0100s",a0100Array);
       	    	hashvo.setValue("type","model");
       	    	hashvo.setValue("state",state);
       	    	hashvo.setValue("id",id);
       	    	var request=new Request({asynchronous:false,onSuccess:setInfoOk,functionId:'3020071029'},hashvo);         
  	    	 }
  	    	else
  	    	{
  	    	    if(id==null||id.length==0)
  	    	    {
  	    	       alert(SELECT_PERSON_DATABASE+"！");
  	    	       return;
  	    	    }
  	    	    var hashvo=new ParameterSet();
  	    	    hashvo.setValue("type","dbname");
       		    hashvo.setValue("nbase",dbName);	
       	    	hashvo.setValue("a0100s",a0100Array);
       	    	hashvo.setValue("state",state);
       	    	hashvo.setValue("id",id);
       	    	var request=new Request({asynchronous:false,onSuccess:setInfoOk,functionId:'3020071029'},hashvo);         
 
		    	//document.personnelEmployForm.action="/hire/employSummarise/personnelEmploy.do?b_setState=set&state="+state+"&toDbname="+id;
		    	//document.personnelEmployForm.submit();
			}
		
		}
		else
		{
			document.personnelEmployForm.action="/hire/employSummarise/personnelEmploy.do?b_setState=set&state="+state+"&toDbname="+toDbname;
			document.personnelEmployForm.submit();
		}
	}
	
	function isSuccess(outparamters)
	{
		var flag=outparamters.getValue("succeed");
		var tab_id=outparamters.getValue("tabid");
	  //  var warn_id=$F('wid');		
		if(flag=="false")
			return;	
      // var win=window.open("/general/template/edit_form.do?b_query=link&sp_flag=1&ins_id=0&returnflag=5&tabid="+tab_id,"_self");
        window.open("/general/template/edit_form.do?b_query=link&sp_flag=1&ins_id=0&businessModel=4&returnflag=hire&tabid="+tab_id,"aa");
     
     // var theURL ="/general/template/edit_form.do?b_query=link`sp_flag=1`ins_id=0`businessModel=4`returnflag=8`tabid="+tab_id;
     // var iframe_url="/general/email_template/iframe_gz_email.jsp?src="+theURL;
     // var objlist =window.showModalDialog(iframe_url,null,"dialogWidth="+window.screen.width+"px;dialogHeight="+window.screen.height+"px;resizable=yes;status=no;");  
	}
	
	function setInfoOk(outparameters)
	{
	    
	    var dbName="${personnelEmployForm.dbName}";
		var id=outparameters.getValue("id");
		var type=outparameters.getValue("type");
		var state=outparameters.getValue("state");
		var mess=outparameters.getValue("mess");
		var a0100Array = outparameters.getValue("a0100s");
		if(mess!=null){
			if(confirm(mess+"请确定要继续录用吗?")){
				
			}else{
				return;
			}

		}
		if(type=='dbname')
		{
		   document.personnelEmployForm.action="/hire/employSummarise/personnelEmploy.do?b_setState=set&state="+state+"&toDbname="+id;
		   document.personnelEmployForm.submit();
		}
		else
		{
		
	    var hashvo=new ParameterSet();
       	hashvo.setValue("tabid",id);	
       	hashvo.setValue("ins_id","0");
       	hashvo.setValue("objlist",a0100Array);
       	var request=new Request({asynchronous:false,onSuccess:isSuccess,functionId:'0570010135'},hashvo);         
  	   }
	}
	
		 //查询
   function query()
   {
		var In_paramters="tableName=personnelEmploy"; 	
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
    	 var extendSql=generalQuery("personnelEmploy",fields);  	
    	 if(extendSql)
    	 {
	    	 document.personnelEmployForm.extendWhereSql.value=extendSql;
    	 	 personnelEmployForm.action='/hire/employSummarise/personnelEmploy.do?b_query=link';
    		 personnelEmployForm.submit();
    	 }
   }

    //排序
   function taxis()
   {
   		var In_paramters="tableName=personnelEmploy"; 	
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
    	 var orderSql=taxisDialog("personnelEmploy",fields);
    	 if(orderSql)
    	 {
	    	 document.personnelEmployForm.orderSql.value=orderSql;
    	 	  personnelEmployForm.action='/hire/employSummarise/personnelEmploy.do?b_query=link';
    		 personnelEmployForm.submit();
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
		hashvo.setValue("tablename",'personnelEmploy');
	    hashvo.setValue("codeid","${personnelEmployForm.codeID}");
	    hashvo.setValue("extendSql","${personnelEmployForm.extendWhereSql}");
	    hashvo.setValue("orderSql","${personnelEmployForm.orderSql}");
	    var In_paramters="flag="+flag;  
	   	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:showfile,functionId:'0521010011'},hashvo);
		
	}

	 //输出 EXCEL OR PDF
    function showfile(outparamters)
	{
		var outName=outparamters.getValue("outName");
		var flag=outparamters.getValue("flag");
		outName = decode(outName);
		var win=open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
	}
	
	
	
	
   function setPageFormat()
   {
   		
   		var param_vo=oageoptions_selete("3","${personnelEmployForm.username}");
   
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
   		
   		window.open("/hire/employNetPortal/search_zp_position.do?b_resumeBrowse=browse&dbName="+dbname+"&a0100="+a0100+"&zp_pos_id="+zp_pos_id+"&personType=1","aa");
   }
	function sendmail()
	{
	    var dbName="${personnelEmployForm.dbName}";
        var isMailField="${personnelEmployForm.isMailField}";
        var id="";
        var a0100="";
        var ids=document.getElementsByName("z0501");//eval("document.personnelEmployForm.z0501");
        var a0100s=document.getElementsByName("a0100");//eval("document.personnelEmployForm.a0100");
        var z0301s=document.getElementsByName("z0301");
        
        var selectIndex=0;
		var checkNum=0;
		var num=0;
		for(var i=0;i<document.personnelEmployForm.elements.length;i++)
  		{
  			if(document.personnelEmployForm.elements[i].type=='checkbox'&&document.personnelEmployForm.elements[i].name!='selbox')
  			{
  				if(document.personnelEmployForm.elements[i].checked==true)
  				{
  					selectIndex=checkNum;
  					id+="`"+ids[selectIndex].value+"~"+z0301s[selectIndex].value;
  					a0100+="`"+a0100s[selectIndex].value;
  					num++;
  				}
  				checkNum++;
  			}
  		}
  		if(num==0)
  		{
  			alert(PLEASE_SELECT_PERON_TO_EMAIL+"!");
  		    return;
  		}
  		var iWidth = 700;
  		var iHeight = 520;
  		var iTop = (window.screen.availHeight-30-iHeight)/2;
  		var iLeft = (window.screen.availWidth-10-iWidth)/2;
      	window.open("/hire/interviewEvaluating/interviewArrange/initEmailTemplate.do?b_init=init&zploop=4&dbname="+dbName+"&id="+id+"&isMailField="+isMailField+"&a0100="+a0100,"_blank","hotkeys=0,menubar=no,height="+iHeight+",width="+iWidth+",top="+iTop+",left="+iLeft+",toolbar=no,location=no,status=no,resizable=no");
	}
	function _refrash()
	{   
		var pagenum=document.getElementById("pagenum").value;
		var zhengzhengshu=/^[0-9]*[1-9][0-9]*$/;///正整数
		if(!zhengzhengshu.test(pagenum)){
			alert("每页显示条数请输入正整数!");
			return;
		}
		personnelEmployForm.action="/hire/employSummarise/personnelEmploy.do?b_query=link";
	    personnelEmployForm.submit();
	}
	
</script>
</HEAD>
<BODY>
<hrms:themes></hrms:themes>
<html:form action="/hire/employSummarise/personnelEmploy">
<div>
<table style="margin-top:-3px;margin-left:-3px;"><tr><td>	
	<hrms:menubar menu="menu1" id="menubar1">
	<hrms:menuitem name="file" label="menu.file.label" >						
						<hrms:menuitem name="mitem6" label="button.cardpdf" icon="/images/print.gif" url="executeOutFile(1)"  />
						<hrms:menuitem name="mitem6" label="button.createescel" icon="/images/print.gif" url="executeOutFile(2)"  />
						<hrms:menuitem name="mitem6" label="kq.report.pagesetup" icon="/images/prop_ps.gif" url="setPageFormat()"  />
						
	</hrms:menuitem>
	<hrms:menuitem name="file" label="edit_report.status.bj" >						
						<hrms:menuitem name="mitem6" label="infor.menu.query" icon="/images/quick_query.gif" url="query()"  />
						<hrms:menuitem name="mitem6" label="label.zp_exam.sort" icon="/images/sort.gif" url="taxis()"  />					
	</hrms:menuitem>
	<hrms:menuitem name="file" label="system.infor.oper" >						
						<hrms:menuitem name="mitem6" label="hire.interviewEvaluating.dnotice" icon="/images/check.gif" url="setState(21)"  />
						<hrms:menuitem name="mitem6" label="hire.interviewEvaluating.ynotice" icon="/images/check.gif" url="setState(22)"  />
						<hrms:menuitem name="mitem6" label="hire.interviewEvaluating.notcontact" icon="/images/check.gif" url="setState(23)"  />
						<hrms:menuitem name="mitem7" label="label.zp_employ.sendmail" icon="/images/check.gif" url='sendmail("${personnelEmployForm.dbName}");'/>					
	</hrms:menuitem>
</hrms:menubar>
</td></tr></table>
	<iframe src="javascript:false" style="position:absolute;visibility:inherit;top:0px;left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
	</iframe>	
</div>
	<table id='ta'  width="95%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" style="margin-top:5px;">
		<thead>
		
		
		
			 <% 
           		   	 PersonnelEmployForm personnelEmployForm=(PersonnelEmployForm)session.getAttribute("personnelEmployForm");
           		   	 ArrayList columnsList=personnelEmployForm.getColumnsList();
           		   	 String dbName=personnelEmployForm.getDbName();
           		   	 dbName = PubFunc.encryption(dbName);
           	 %>
		
			<tr>
				
				<td align="center" class="TableRow" nowrap>
    			 <input type="checkbox" name="selbox" onclick="batch_select(this,'personnelEmployListform.select');" title='<bean:message key="label.query.selectall"/>'>
	      		</td>
						
				<logic:iterate id="element" name="personnelEmployForm" property="columnsList"  offset="1"> 
				 <td align="center" class="TableRow" nowrap>
					&nbsp;&nbsp;<bean:write name="element" property="itemdesc"   filter="false"/>&nbsp;&nbsp;
				 </td>
				</logic:iterate>
		
			</tr>	
		</thead>
		
		<% int i=0; String className="trShallow"; %>	
		<hrms:extenditerate id="element" name="personnelEmployForm" property="personnelEmployListform.list" indexes="indexes" pagination="personnelEmployListform.pagination" pageCount="${personnelEmployForm.pagerows}" scope="session">
			 <%
			  if(i%2==0)
			  	className="trShallow";
			  else
			  	className="trDeep";
			  i++;
			  
			  %>
			<tr class='<%=className%>' >
				<td align="center" class="RecordRow" nowrap>
				    <bean:define id="z05011" name="element" property="z0501"/>
				    <bean:define id="a01001" name="element" property="a0100"/>
				    <bean:define id="z03011" name="element" property="z0301"/>
				    <%
				     String z0501 = PubFunc.encrypt(z05011.toString());
				     String a0100 = PubFunc.encrypt(a01001.toString());
				     String z0301 = PubFunc.encrypt(z03011.toString());
                    %>
					&nbsp;<hrms:checkmultibox name="personnelEmployForm" property="personnelEmployListform.select" value="true" indexes="indexes" />&nbsp;
					<input type="hidden" name="z0501" value="<%=z0501 %>"/>
					<input type="hidden" name="a0100" value="<%=a0100 %>"/>
					<input type="hidden" name="z0301" value="<%=z0301 %>"/>
				
				</td>
				 <% 
           		   
           		   	 for(int a=1;a<columnsList.size();a++)
           		   	 {
           		   	 	LazyDynaBean abean=(LazyDynaBean)columnsList.get(a);
           		   	 	String itemid=(String)abean.get("itemid");
           		   	 	
           		    %>
	           		    <td align="center" class="RecordRow"  nowrap>
	           		    	<%  
	           		    		if(itemid.equals("a0101"))
	           		    		{
	    
	           		    		LazyDynaBean abean2=(LazyDynaBean)pageContext.getAttribute("element");
	           		            String a0100_canshu = (String)abean2.get("a0100_canshu");
	           		            String z0301_canshu = (String)abean2.get("z0301_canshu");
              
	           		    		
	           		    	%>
	           		    		 <a href='javascript:resumeBrowse("<%=a0100_canshu%>","<%=dbName%>","<%=z0301_canshu%>")'>		           
	           		    	<% 
	           		    		 }
	           		    	%>
	           		    	&nbsp;<bean:write name="element" property="<%=itemid%>" filter="false" />&nbsp;
	           		    	<%  
	           		    		if(itemid.equals("a0101"))
	           		    		{
	           		    			out.print("</a>");
	           		    		}
	           		    	%>
	           		    	
	           		    </td>
           		    
           		    <%
           		      }
           		    %>
			</tr>
		</hrms:extenditerate>
		<tr>
		<td colspan="<%=columnsList.size()%>" class="RecordRow">
			<table width="100%" align="center">
		<tr>
			<td valign="bottom" class="tdFontcolor">
				 <bean:message key="hmuster.label.d"/>
				<bean:write name="personnelEmployForm" property="personnelEmployListform.pagination.current" filter="true" />
				<bean:message key="label.page.sum"/>
				<bean:write name="personnelEmployForm" property="personnelEmployListform.pagination.count" filter="true" />
				<bean:message key="label.page.row"/>
				<bean:write name="personnelEmployForm" property="personnelEmployListform.pagination.pages" filter="true" />
				<bean:message key="label.page.page"/>
				 每页显示<html:text property="pagerows" name="personnelEmployForm" styleId="pagenum"  onkeypress="event.returnValue=IsDigit();" size="3"></html:text>条&nbsp;&nbsp;<a href="javascript:_refrash();">刷新</a>
			</td>
			<td align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationlink name="personnelEmployForm" property="personnelEmployListform.pagination" nameId="personnelEmployListform">
					</hrms:paginationlink>
			</td>
		</tr>
	</table>	
		</td>
		</tr>
	</table>	
<table  width="95%" border="0" cellspacing="0" align="left" cellpadding="0" class="ListTable" style="margin-top:5px;>
  <tr>
    <td align="left">
    <logic:equal value="0" name="personnelEmployForm" property="type">
     <bean:message key="hire.select.database"/>：&nbsp;<!-- 选择人员库 -->
    </logic:equal>
    <logic:equal value="1" name="personnelEmployForm" property="type">
     <bean:message key="hire.select.model"/>：&nbsp;<!-- 选择模板 -->
    </logic:equal>
    <hrms:optioncollection name="personnelEmployForm" property="templateList" collection="list" />
						 <html:select name="personnelEmployForm" styleId="temid" property="templateid" size="1">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
		&nbsp;
		<input type="button" class="mybutton" value="<bean:message key="hire.column.employee"/>" onclick="setState('43');"/><!-- 录用 -->
		<logic:equal value="dxt" name="personnelEmployForm" property="returnflag">
		<hrms:tipwizardbutton flag="retain" target="il_body" formname="personnelEmployForm"/> 	
		</logic:equal>
    </td>
  </tr>
</table>
	<input type='hidden' name='extendWhereSql' value="${personnelEmployForm.extendWhereSql}" />
	<input type='hidden' name='orderSql' value="${personnelEmployForm.orderSql}" />
			
</html:form>

</BODY>
</html>