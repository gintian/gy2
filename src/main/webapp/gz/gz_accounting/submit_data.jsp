<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/gz/salary.js"></script>
<link rel="stylesheet" href="/css/css1.css" type="text/css">
 <%
	int i=0;
%>
<hrms:themes />
<html:form action="/gz/gz_accounting/submit_data"> 
<br>
<br>
<br>

	<div id='wait' style='position:absolute;top:70;left:75;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					<bean:message key="label.gz.submitData"/>......
				</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10"  >
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
		<iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:315; height:87; 					    	
			   			 				z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';"></iframe>	
	</div>
<table align="center"  width="80%">

<tr>
<td>

  <fieldset align="center" style="width:100%;">
   <legend><bean:message key="label.gz.select.type"/></legend>
	<table width="100%" border="0" cellspacing="0" align="left" cellpadding="0" style="margin-left: -6px;">
	<tr><td>&nbsp;</td></tr>
	<tr>
	 <td width="80%">
		<table border="0" cellspacing="0" cellpadding="0">
		<tr> <td>&nbsp;&nbsp;&nbsp;</td>
 		   <td align='center' >
 		    	<div style="height: 300px;overflow: auto">
				<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">
				   	  <thead>
			           <tr>
				            <td align="center" class="TableRow" nowrap width="250">
								<bean:message key="set.label"/>&nbsp;
					    	</td>         
				            <td align="center" class="TableRow" nowrap >
								<bean:message key="label.gz.submit.type"/>&nbsp;
					    	</td>
			           </tr>
				   	  </thead>
				          <hrms:extenditerate id="element" name="batchForm" property="formulalistform.list" indexes="indexes"  pagination="formulalistform.pagination" pageCount="200" scope="session">
				          <%
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
				            <td align="left" class="RecordRow" nowrap>
				                <bean:write name="element" property="name" filter="true"/>&nbsp;
				                <html:hidden name="element" property="setid"/>
					    	</td>            
				            <td align="center" class="RecordRow" nowrap>
				    		    <html:select name="element" property="type" size="1" onchange="update()" >
	                                <html:optionsCollection property="typelist" value="dataValue" label="dataName"/>				    		
				    		    </html:select>						    	
				    		</td>
				          </tr>
				        </hrms:extenditerate>
				</table>
 		   </div>
    	   </td>
		</tr>
		</table>    
     </td>     
	</tr>

	</table>
	</fieldset>
</td>
</tr>
	<tr>
	<td>
		<table align="left" width='90%'>
    		<tr >
		  	  <td align='left' id='advance' style="display:<bean:write name="batchForm" property="isUpdateSet" filter="true"/>" >
		  	  
		  	  <button  name="advance" Class="mybutton" onclick='enter()' ><bean:message key="button.sys.cond"/>...</button>
		  	  
		  	  </td><td>
		  	  <% if(request.getParameter("type")!=null&&request.getParameter("type").equals("1")){  %>
				<button name="ok" Class="mybutton" onclick="sub_gz_type()"><bean:message key="button.ok"/></button>
			  <% } else if(request.getParameter("type")!=null&&request.getParameter("type").equals("2")){ %>
	           <button name="ok" Class="mybutton" onclick="submitgztype2('${batchForm.salaryid}','${batchForm.gz_module}','${batchForm.bosdate}','${batchForm.count}','${accountingForm.verify_ctrl}')"><bean:message key="button.ok"/></button>			  
			   <% } else if(request.getParameter("type")!=null&&request.getParameter("type").equals("3")){ %>
	           <button name="ok" Class="mybutton" onclick="submitgztype3('${batchForm.salaryid}','${batchForm.gz_module}','${batchForm.bosdate}','${batchForm.count}','${accountingForm.verify_ctrl}')"><bean:message key="button.ok"/></button>			  			   
			   <% } else { %>
			    <button name="ok" Class="mybutton" onclick="submitGzType()"><bean:message key="button.ok"/></button>
			  <% } %>
				<button name="cancel" Class="mybutton" onclick="window.close();"><bean:message key="button.cancel"/></button>
			  </td>
	    	</tr>
    	</table>
	</td>
	</tr>
</table>
<html:hidden name="accountingForm" property="filterWhl" />
<html:hidden name="accountingForm" property="reportSql" />

<script language='javascript'>
var info=dialogArguments;
var verify_ctrl='${accountingForm.verify_ctrl}';
 var subNoShowUpdateFashion='${accountingForm.subNoShowUpdateFashion}';
function sub_gz_type()
{
	if(verify_ctrl=='1')
	{
		 var hashvo=new ParameterSet();
	     hashvo.setValue("a_code","");
	     hashvo.setValue("condid","all");
	     hashvo.setValue("salaryid",${batchForm.salaryid});
	     hashvo.setValue("type","0");
	     var request=new Request({asynchronous:false,onSuccess:check_verify,functionId:'3020070016'},hashvo);	
	}
	else
	{
		var isHistory='${batchForm.isHistory}';
		if(isHistory=='1')
		{
			if(confirm(GZ_ACCOUNTING_INFO1+"(y/n)"))
			{
				validateOverTotalControl('${batchForm.salaryid}','${batchForm.gz_module}');
			}
		}
		else
		{
			validateOverTotalControl('${batchForm.salaryid}','${batchForm.gz_module}');
		}
	}
}

function check_verify(outparameters)
{
  var msg=outparameters.getValue("msg");
  if(msg=='0'||msg=='no')
  {
       var isHistory='${batchForm.isHistory}';
	   if(isHistory=='1')
	   {
			if(confirm(GZ_ACCOUNTING_INFO1+"(y/n)"))
			{
				validateOverTotalControl('${batchForm.salaryid}','${batchForm.gz_module}');
			}
	   }
	   else
	   {
			validateOverTotalControl('${batchForm.salaryid}','${batchForm.gz_module}');
	   }
  }
  else{
  	 alert("审核不通过，不允许操作!");
     var filename=outparameters.getValue("fileName");
     var fieldName = getDecodeStr(filename);
     var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}

function update()
{
	var flag="none";
	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='select-one')
		{	
			for(var j=0;j<document.forms[0].elements[i].options.length;j++)
			{
				if(document.forms[0].elements[i].options[j].selected==true&&document.forms[0].elements[i].options[j].value=='0')
					flag="block";
			}
			
		}
	}
	var obj=eval("advance");
	obj.style.display=flag;
	
}

var itemArray=new Array();
var typeArray=new Array();
function enter()
{
	var setid=$F('setid');
	var index=0;
	var setids="";
	for(var i=0;i<document.forms[0].elements.length;i++)
	{
		if(document.forms[0].elements[i].type=='select-one')
		{	
			for(var j=0;j<document.forms[0].elements[i].options.length;j++)
			{
				if(document.forms[0].elements[i].options[j].selected==true&&document.forms[0].elements[i].options[j].value=='0')
				{
					setids+="/"+setid[index];
				}
			}
			index++;
		}
	}
	var thecodeurl="/gz/gz_accounting/submit_data.do?b_updateFashion=link`sets="+setids; 
	var iframe_url="/general/query/common/iframe_query.jsp?src="+thecodeurl;
	var objlist =window.showModalDialog(iframe_url,null,"dialogWidth=450px;dialogHeight=450px;resizable=yes;status=no;");  
   
    if(objlist!=null)
    {
    	itemArray=objlist.items;
    	typeArray=objlist.types;
    }
}
function submitgztype3(salaryid,gz_module,bosdate,count,verify_ctrl1)
{
		if(verify_ctrl=='1')
		{
		   var a00z1=count;
		   var a00z0=bosdate;
		   if(a00z1==''||a00z0=='')
		   {
		     alert(GZ_SELECT_BOSDATEANDCOUNT+"！");
		     return;
		   }
		   var hashvo=new ParameterSet();
		   hashvo.setValue("a_code","");
		   hashvo.setValue("condid","all");
		   
		   hashvo.setValue("salaryid",salaryid);
		   hashvo.setValue("type","1");
		   hashvo.setValue("a00z0",count);
		   hashvo.setValue("a00z1",bosdate);
	     
		   hashvo.setValue("gz_module",gz_module);
		   hashvo.setValue("bosdate",bosdate);
		   hashvo.setValue("count",count);
		   hashvo.setValue("gzSpCollect","1");
		   hashvo.setValue("collectPoint",info[1]); 
		   hashvo.setValue("selectID",info[0].substring(1));
		   var request=new Request({asynchronous:false,onSuccess:check_ok33,functionId:'3020070016'},hashvo);	
		}
		else
		{
			validateOverTotalControl33(salaryid,gz_module,bosdate,count);
		}
}
function check_ok33(outparameters)
{
  var msg=outparameters.getValue("msg");
  var gz_module=outparameters.getValue("gz_module");
  var bosdate=outparameters.getValue("bosdate");
  var count=outparameters.getValue("count");
  var salaryid=outparameters.getValue("salaryid");
  if(msg=='0')
  {
    validateOverTotalControl33(salaryid,gz_module,bosdate,count);
  }
  else if(msg=='no')
  {
     validateOverTotalControl33(salaryid,gz_module,bosdate,count);
  }
  else{
  	 alert("审核不通过，不允许操作!");
     var filename=outparameters.getValue("fileName");
     var fieldName = getDecodeStr(filename);
     var win=open("/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}
//审批确认判断是否超过总额控制
function validateOverTotalControl33(salaryid,gz_module,bosdate,count)
{	
	if(subNoShowUpdateFashion=='0')
	{
		var waitInfo=eval("wait");		
		document.getElementById("wait_desc").innerHTML="正在提交数据......";	
		waitInfo.style.display="block";
		var bt=$('ok');
		bt.disabled=true;
	}
	if(subNoShowUpdateFashion=='1')
	{
		var waitInfo=eval("wait");	
		document.getElementById("wait_desc").innerHTML="正在提交数据......";		
		waitInfo.style.display="block";
	}
	var hashvo=new ParameterSet();
	hashvo.setValue("salaryid",salaryid);
	hashvo.setValue("gz_module",gz_module); 
	hashvo.setValue("bosdate",bosdate);
	hashvo.setValue("count",count); 
    hashvo.setValue("gzSpCollect","1");
    hashvo.setValue("collectPoint",info[1]); 
    hashvo.setValue("selectID",info[0].substring(1));
	var request=new Request({method:'post',asynchronous:true,onSuccess:totalControl33,functionId:'3020111007'},hashvo);
}
function totalControl33(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var bosdate=outparamters.getValue("bosdate");
	var count=outparamters.getValue("count");
	var info=getDecodeStr(outparamters.getValue("info"));
	if(typeof(salaryid)=='undefined'&&typeof(gz_module)=='undefined'&&typeof(bosdate)=='undefined'){
		setState(false);
		var waitInfo=eval("wait");				
		waitInfo.style.display="none";
		return;
	}
	if(info=='success')
		submit_gz_type33(salaryid,gz_module,bosdate,count);
	else
	{
		var waitInfo=eval("wait");			
		waitInfo.style.display="none";
		
		var isOver=outparamters.getValue("isOver"); 
		if(isOver=='0')
		{
			alert(info);
			var waitInfo=eval("wait");			
			waitInfo.style.display="block";
			submit_gz_type33(salaryid,gz_module,bosdate,count);
		}
		else
		{
			var ctrlType=outparamters.getValue("ctrlType");
			if(ctrlType=='1')
			{
	    		alert(info);
	    		return;
	        }else{
	           var alertInfo = getDecodeStr(outparamters.getValue("alertInfo"));
	           if(confirm(alertInfo))
	           {
				  var waitInfo=eval("wait");			
				  waitInfo.style.display="block";
			      submit_gz_type33(salaryid,gz_module,bosdate,count);
	           }else
	           {
	              return;
	           }
	        }
		}
	}
}
function submit_gz_type33(salaryid,gz_module,bosdate,count)
{
	var hashvo=new ParameterSet();
	if(subNoShowUpdateFashion=='0')
	{
		var setid=$F('setid');
		var type=$F('type');
		
		hashvo.setValue("setid",setid);
		hashvo.setValue("type",type);	
	}
	hashvo.setValue("gz_module",gz_module);
	if(subNoShowUpdateFashion=='0')
	{
		var item_str="";
		for(var i=0;i<itemArray.length;i++)
		{
			item_str+="/"+itemArray[i];
		}
		var type_str="";
		for(var i=0;i<typeArray.length;i++)
		{
			type_str+="/"+typeArray[i];
		}
		hashvo.setValue("items",item_str);
		hashvo.setValue("uptypes",type_str);
	}
	hashvo.setValue("subNoShowUpdateFashion",subNoShowUpdateFashion);
	hashvo.setValue("salaryid",salaryid); 
	hashvo.setValue("bosdate",bosdate);
	hashvo.setValue("count",count);
    hashvo.setValue("gzSpCollect","1");
    hashvo.setValue("collectPoint",info[1]); 
    hashvo.setValue("selectID",info[0].substring(1));
   	var request=new Request({method:'post',asynchronous:true,onSuccess:reloadIsOk33,functionId:'3020111006'},hashvo);
}
function reloadIsOk33(outparamters)
{
	var salaryid=outparamters.getValue("salaryid");
	var gz_module=outparamters.getValue("gz_module");
	var flag=outparamters.getValue("succeed");
	
	var bt=$('ok');
	if(bt)
		bt.disabled=false;
	
	if(flag=="false")
		return;	
	if(flag=="true"&&gz_module!=1)
		alert("提交成功!");	
	if(flag=="true"&&gz_module==1)
		alert("提交成功!");	
			
		
	var retvo=new Object();	
	retvo.success="1";
	retvo.salaryid=salaryid;
    window.returnValue=retvo;
	window.close();
}
</script>


</html:form>


  