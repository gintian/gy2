<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<script language="Javascript" src="/gz/salary.js"/></script>
<script type="text/javascript">
<!--
function allSelect(obj)
{
   var arr=document.getElementsByName("selectids");
   if(arr)
   {
     for(var i=0;i<arr.length;i++)
     {
        if(obj.checked)
        {
          arr[i].checked=true;
        }
        else
        {
           arr[i].checked=false;
        }
     }
   }
}
function dygs()
{
  var salaryid="${accountingForm.salaryid}";
  var strurl="/gz/templateset/spformula/sp_formula.do?b_query=link`returnType=1`opt=0`salaryid="+salaryid;
  var iframe_url="/general/query/common/iframe_query.jsp?src="+strurl; 
  var flag=window.showModalDialog(iframe_url,arguments,"dialogWidth=800px;dialogHeight=470px;resizable=yes;scroll=no;status=no;");  
  accountingForm.action="/gz/gz_accounting/sh_formula.do?b_query=link&opt=1&salaryid="+salaryid+"&condid=<%=request.getParameter("condid")%>&type=<%=request.getParameter("type")%>&a_code=<%=request.getParameter("a_code")%>&a00z0=<%=request.getParameter("a00z0")==null?"":request.getParameter("a00z0")%>&a00z1=<%=request.getParameter("a00z1")==null?"":request.getParameter("a00z1")%>";
  accountingForm.submit();
}
function beginSh()
{
   var salaryid="${accountingForm.salaryid}";
   var condid="${accountingForm.condid}";
   var a_code="${accountingForm.a_code}";
   var obj = document.getElementsByName("selectids");
   var num=0;
   var ids="";
   if(obj)
   {
     for(var i=0;i<obj.length;i++)
     {
        if(obj[i].checked)
        {
          num++;
          ids+=","+obj[i].value;
        }
     }
   }
   else
   {
     return;
   }
   if(num==0)
   {
      alert(GZ_SELECT_SHFORMULA+"！");
      return;
   }
   //3020070016
   ids=ids.substring(1);
   jinduo();
   var hashvo=new ParameterSet();
   hashvo.setValue("a_code",a_code);
   hashvo.setValue("ids",ids);
   hashvo.setValue("condid",condid);
   hashvo.setValue("salaryid",salaryid);
   hashvo.setValue("type","<%=request.getParameter("type")%>");
   hashvo.setValue("a00z0","<%=request.getParameter("a00z0")%>");
   hashvo.setValue("a00z1","<%=request.getParameter("a00z1")%>");
   var request=new Request({asynchronous:false,onSuccess:check_ok,functionId:'3020070016'},hashvo);		       
}
function check_ok(outparameters)
{
  var msg=outparameters.getValue("msg");
  closejinduo();
  if(msg=='no')
  {
     alert("审核完毕！");
     return;
  }
  else{
     var filename=outparameters.getValue("fileName");
     var name=filename.substring(0,filename.length-1)+".xls";
     var fieldName = getDecodeStr(name);
     var win=open( "/servlet/vfsservlet?fileid="+fieldName+"&fromjavafolder=true","excel");
  }
}
function jinduo(){
	var x=document.body.scrollLeft+event.clientX;
    var y=document.body.scrollTop+event.clientY; 
	var waitInfo;
	waitInfo=eval("wait");
	waitInfo.style.top=50;
	waitInfo.style.left=40;
	waitInfo.style.display="block";
}
	function closejinduo(){
	   var waitInfo;
	   waitInfo=eval("wait");
	   waitInfo.style.display="none";
     }
//-->
</script>
<html:form action="/gz/gz_accounting/sh_formula">
<div   id="wait" style='position:absolute;top:30;left:20;width:285px;heigth:120px;display:none'>
 
		<table border="1" width="37%" cellspacing="0" cellpadding="4"  class="table_style" height="87" align="center">
			<tr>
				<td class="td_style" height=24>正在进行审核，请稍侯...</td>
			</tr>
			<tr>
				<td style="font-size:12px;line-height:200%" align=center>
					<marquee  class="marquee_style"  direction="right" width="300" scrollamount="5" scrolldelay="10" >
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
		    <iframe src="javascript:false" style="position:absolute; visibility:inherit; top:0px; left:0px;width:285px;height:120px;z-index:-1;filter='progid:DXImageTransform.Microsoft.Alpha(style=0,opacity=0)';">
		    </iframe>	
	</div>
    <Br>
<table width="95%"  border="0" align="center">
<tr height="300">
<td align="left">
<fieldset style="height:310px;width:280px;overflow:auto">
<legend><bean:message key="label.gz.shformula"/></legend>
<div style="border:1px solid #eee;height:270px;width:240px;overflow:auto;margin:1em 1;">
<table width="100%" border="0" align="center" class="ListTable">
<thead>
<tr>
<td class="TableRow" align="center">
<input type="checkbox" name="al" value="1" onclick="allSelect(this);"checked/>
</td>
<td class="TableRow" align="center">
<bean:message key="workdiary.message.formula.name"/>
</td>
</tr>
</thead>
<logic:iterate id="element" name="accountingForm" property="shFormulaList" indexId="index" offset="0">
<tr>
<td align="center" class="RecordRow">
<input type="checkbox" name="selectids" value="<bean:write name="element" property="chkid"/>" checked/>
</td>
<td align="left" class="RecordRow">
<bean:write name="element" property="name"/>
</td>
</tr>
</logic:iterate>
</table>
</div>
</fieldset>
</td>
<td  align="center" valign="bottom">
<hrms:priv func_id="3240812,3250512">
<input type="button" class="mybutton" name="dy" onclick="dygs();" value="<bean:message key="infor.menu.definition.formula"/>"/>
<br>
</hrms:priv>
<input type="button" class="mybutton" name="sh" onclick="beginSh();" value="<bean:message key="workdiary.message.app.start"/>"/>
<br>
<input type="button" name="can" class="mybutton" onclick="window.close();" value="  <bean:message key="button.cancel"/>  "/> 
</td>
</tr>
</table>
</html:form>