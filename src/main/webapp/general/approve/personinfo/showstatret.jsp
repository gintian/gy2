<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="java.util.*"%>
<%@ page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript">
<!--

function showDiv(obj,dataname,state,orgid) 
{ 
linkDiv.style.left=getPosition(obj).x; 
linkDiv.style.top=getPosition(obj).y+obj.offsetHeight; 
linkDiv.style.position="absolute"; 
//linkDiv.innerHTML="";
getdata(dataname,state,orgid);
linkDiv.style.display=''; 
linkDiv.onmouseleave=function(){linkDiv.style.display='none'}; 
} 
function getPosition(el) 
{ 
for (var lx=0,ly=0;el!=null;lx+=el.offsetLeft,ly+=el.offsetTop,el=el.offsetParent); 
return {x:lx,y:ly} 
} 
function getdata(dataname,state,orgid){
	pars='dataname='+dataname+"&state="+state+"&orgid="+orgid;
 	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:getdataok,functionId:'0580010002'});
	
}
function getdataok(outparamters){


linkDiv.innerHTML=outparamters.getValue("tableStr");
}

function openpage(a00){
	var dbnameObj = document.getElementsByName("pdbflag")[0];
	var dbname = "";
	if (dbnameObj) {
		dbname = dbnameObj.value;
	}
   	var theurl="/general/approve/personinfo/showpersoninfo.do?b_query=link&pdbflag1="+dbname+"&a01001="+a00;
	var retvalue=	window.showModalDialog(theurl, false, 
        "dialogWidth:800px; dialogHeight:1000px;resizable:no;center:yes;scroll:yes;status:no;");   
	if(retvalue!='re'){
		window.location.reload();
	}
	if(retvalue=='re'){
		openpage(a00);
	
	}
	}
function selchange(){


	approvePersonForm.action=window.location;
	approvePersonForm.submit();

}
//-->
</script>
<%// 在标题栏显示当前用户和日期 2004-5-10 
			
			UserView uv = (UserView) session
					.getAttribute(WebConstant.userView);
			String dbname=uv.getDbname();
			if(dbname==null||"".equals(dbname)){
			dbname="usr";
			}
			
%>

<style>
   td{padding-left: 5px;}
</style>
<html:form action="/general/approve/personinfo/showstatret">
<table width="80%" align="center" border="0" cellspacing="0"  cellpadding="0">
	<tr><td width="100%" nowrap>
		<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0">
		<tr align="left" >
			<td   nowrap>
				<bean:message key='menu.base'/>
				&nbsp;<bean:write name="approvePersonForm" property="selstr" filter="false" />
				&nbsp;&nbsp;
			</td>
		</tr>
		</table>
	</td></tr>
	<tr><td width="100%" nowrap>
		 <div class="fixedDiv2"> 
		<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0" class="ListTable">
		<tr class="fixedHeaderTr">
			<td height='50'align='center' class="TableRow"nowrap>
			单位（部门）名称
			</td>
			<td height='50'align='center'class="TableRow" nowrap>
			编辑
			</td>
			<td height='50'align='center' class="TableRow" nowrap>
			报批
			</td>
			<td height='50'align='center' class="TableRow" nowrap>
			驳回
			</td>
			<td height='50'align='center' class="TableRow" nowrap>
			批准
			</td>
			<td height='50'align='center' class="TableRow" nowrap>
			申请
			</td>
			<td height='50'align='center' class="TableRow" nowrap>
			可修改
			</td>
		</tr>		
		<hrms:paginationdb id="element" name="approvePersonForm" sql_str="approvePersonForm.sql" table="" where_str="approvePersonForm.where" columns="approvePersonForm.column" order_by="" pagerows="10" page_id="pagination" indexes="indexes">
		<bean:define id="orgid" name="element" property="org"/>
		<tr>
		<td class="RecordRow" nowrap>&nbsp;
		 <hrms:codetoname codeid="UN" name="element" codevalue="org" codeitem="codeitem" scope="page"/>   
		   
          <bean:write name="codeitem" property="codename" />
		
			<hrms:codetoname codeid="UM" name="element" codevalue="org" codeitem="codeitems" scope="page" uplevel="${approvePersonForm.uplevel }"/>  
			<bean:write name="codeitems" property="codename" />
		&nbsp;
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state0">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=0&ff=a&fr=1"  onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","0","${orgid}")'>
		<bean:write name="element" property="state0"/>
		</a>
		</logic:notEqual>
		<logic:equal value='0' name="element" property="state0">
		<bean:write name="element" property="state0"/>
		</logic:equal>
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state1">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=1&ff=a&fr=1" onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","1","${orgid}")'>
		
		<bean:write name="element" property="state1"/>
		</a>
		</logic:notEqual>
				<logic:equal value='0' name="element" property="state1">
		<bean:write name="element" property="state1"/>
		</logic:equal>
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state2">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=2&ff=a&fr=1" onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","2","${orgid}")'>
		
		<bean:write name="element" property="state2"/>
		</a>
		</logic:notEqual>
				<logic:equal value='0' name="element" property="state2">
		<bean:write name="element" property="state2"/>
		</logic:equal>
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state3">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=3&ff=a&fr=1" onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","3","${orgid}")'>
		
		<bean:write name="element" property="state3"/>
		</a>
		</logic:notEqual>
				<logic:equal value='0' name="element" property="state3">
		<bean:write name="element" property="state3"/>
		</logic:equal>
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state4">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=4&ff=a&fr=1" onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","4","${orgid}")'>
		
		<bean:write name="element" property="state4"/>
		</a>
		</logic:notEqual>
				<logic:equal value='0' name="element" property="state4">
		<bean:write name="element" property="state4"/>
		</logic:equal>
		</td>
		<td class="RecordRow" nowrap>
		<logic:notEqual value='0' name="element" property="state5">
		<a href="/general/approve/personinfo/setre.do?b_query=link&a_code=um${orgid}&setid=A01&abkflag=a&dataname=${approvePersonForm.pdbflag}&state=5&ff=a&fr=1" onmouseover='showDiv(this,"${approvePersonForm.pdbflag}","5","${orgid}")'>
		
		<bean:write name="element" property="state5"/>
		</a>
		</logic:notEqual>
				<logic:equal value='0' name="element" property="state5">
		<bean:write name="element" property="state5"/>
		</logic:equal>
		</td>
		</tr>
		</hrms:paginationdb>
	</table>
	</div>
</td></tr>
<tr><td width="100%">
	<table width="100%" class="RecordRowP">
			<tr>
				<td  colspan="2" width="100%" valign="bottom" align="left" nowrap>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />	
				</td>
				<td width="60%" align="left" nowrap class="tdFontcolor">
					<p align="left">
					<hrms:paginationdblink name="approvePersonForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
					</p>
				</td>
				</tr>
			
		</table>
	</td></tr>
</table>
<div id="linkDiv" style="display:none;width:100px;height:150px;border:1px #000000;overflow: auto;">
</div>
</html:form>





