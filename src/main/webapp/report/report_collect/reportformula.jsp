<%@ page contentType="text/html; charset=UTF-8"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<SCRIPT LANGUAGE=javascript src="/js/validate.js"></SCRIPT>     
<SCRIPT LANGUAGE=javascript src="/js/function.js"></SCRIPT> 
<SCRIPT LANGUAGE=javascript src="/report/report_collect/reportCollect.js"></SCRIPT>
<%
  String type=request.getParameter("type");
  String str="表内计算";
  if(type!=null&&type.equals("2"))
  	str="表间计算";


 %>


<html>
<head>
<title></title>
</head>
<script language='javascript'>
var info=parent.info;

function selectAll()
{
	var a_select=eval("document.reportCollectForm.formula");
	for(var a=0;a<a_select.options.length;a++)
	{
		a_select.options[a].selected=true;
	}
	 
}

function calculates()
{

	var hashvo=new ParameterSet(); 
	hashvo.setValue("units",info[0]);	
	formulaArray=getSelectInfos("formula"); 
	if(formulaArray.length==0)
	{
		alert("请选择计算公式!");
		return;
	} 
	var formulastr="";
	for(var i = 0 ; i< formulaArray.length; i++){
			var temp1 = formulaArray[i];
			formulastr +=temp1;
			formulastr += "&&";
	}	
	
	if(!confirm("请确认执行批量计算?"))
		return;	
 	hashvo.setValue("formulastr",formulastr);
	hashvo.setValue("flag","<%=type%>");	
	var obj=eval("wait");
	obj.style.display="block";
	
	var obj2=document.getElementsByName("b_compute");
	obj2[0].disabled=true;
	obj2=document.getElementsByName("b_select");
	obj2[0].disabled=true;
	obj2=document.getElementsByName("b_close");
	obj2[0].disabled=true;
	var request=new Request({method:'post',asynchronous:true,onSuccess:returnInfo,functionId:'03030000034'},hashvo); 

}

function returnInfo(outparamters)
{
		var obj2=document.getElementsByName("b_compute");
	    obj2[0].disabled=false;
	    obj2=document.getElementsByName("b_select");
		obj2[0].disabled=false;
		obj2=document.getElementsByName("b_close");
		obj2[0].disabled=false;
	    
		var obj=eval("wait");
		obj.style.display="none";
		var ainfo=outparamters.getValue("info");
		if(ainfo=='null')
		{
			alert("计算成功!");
			 
		}
		else
		{
			if(ainfo.indexOf('#')==-1)
			{
				alert(ainfo);
			}
			else
			{
				var infos=ainfo.split('#');
				for(var i=0;i<infos.length;i++)
					alert(infos[i]);
			}
			return;
		}
}
function closeWindow()
{
	var valWin = parent.Ext.getCmp('showformula');
	if(valWin)
		valWin.close();
	else
		window.close();	
}
</script>
<hrms:themes />
 <html:form action="/report/edit_collect/reportCollect">


<div id='wait' style='position:absolute;top:150;left:70;display:none;'>
		<table border="1" width="67%" cellspacing="0" cellpadding="4"  class="table_style"  height="87" align="center">
			<tr>
				<td  class="td_style"  id='wait_desc'   height=24>
					正在计算，请稍候......
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


<table width='100%' height="100%" style="margin-top: -3px;">
	<tr width='100%'>
		<td align='left'>
			<table width="95%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
			   <thead>
			     <tr>
			        <td align="left" class="TableRow" nowrap colspan="3">选择 <%=str%> 公式</td>            	        	        	        
			     </tr>
			   	 </thead>
			   	 <tr>
			       <td width="100%" align="center" class="RecordRow" style="padding:0px" nowrap>
			          <select name="formula" multiple="multiple" size="10"  style="height:300px;width:100%;font-size:9pt;border:none;">
				         <logic:iterate id="element" name="reportCollectForm" property="formulaList"  > 
				             <option value='<bean:write name="element" property="value" />' ><bean:write name="element" property="name" /> </option>             
				         </logic:iterate>
			          </select>
			       </td>
			     </tr>
			     <tr>
			        <td align="center" class="RecordRow" nowrap  colspan="3" style="height:35px;">
			          <input type="button" name="b_select" value="<bean:message key="button.all.select"/>"  onclick="selectAll()"   class="mybutton"> 
			          <input type="button" name="b_compute" value="<bean:message key="infor.menu.compute"/>"  onclick="calculates()" style="margin-left: -2px;"  class="mybutton"> 
			          <input type="button" name="b_close" value="<bean:message key="button.return"/>"  onclick="closeWindow()" style="margin-left: -4px;" class="mybutton"> 
			        </td>
			     </tr>   
			</table>
		</td>
	</tr>
</table>
 </html:form>

</html>