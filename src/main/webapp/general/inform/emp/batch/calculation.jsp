<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="./batch.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
    String bosflag="";
    if(userView!=null){
    	bosflag = userView.getBosflag();
    }
    String flag = request.getParameter("flag");
%>
<script type="text/javascript">
	var flag = "<%=flag%>";	
</script>
<style type="text/css">
#scroll_box {
    height: 280px;    
    width: 340px;            
    width: 335px\9\0;            
    overflow: auto;            
    margin: 0;
}
</style>
<div id='wait1' style='position:absolute;top:150px;left:40px;display:none;'>
		<table border="0" width="300" cellspacing="0" cellpadding="0" style="background-color: #FFFFFF; " height="87" align="center">
			<tr>
				<td class="TableRow" style="border-bottom: none;" height=24>
					正在计算,请稍候......
				</td>
			</tr>
			<tr>
				<td class="complex_border_color" style="font-size:12px;line-height:200%;border-top: none;" align=center>
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
<hrms:themes/>
<html:form action="/general/inform/emp/batch/calculation">
<div class="fixedDiv3">
<html:hidden name="indBatchHandForm" property="sortstr" styleId="sortstr" />
<html:hidden name="indBatchHandForm" property="count" styleId="count" />
<input type="hidden" name="id" id="id"/>
<%if("hcm".equals(bosflag)){ %>
<table width="100%" border="0" align="right" cellpadding="0px" cellspacing="0px">
<%}else{ %>
<table width="100%" border="0" align="right" cellpadding="0px" cellspacing="0px">
<%} %>
  <tr>
    <td height="280" align="center">
      <fieldset style="width:auto;width:350px;">
      <legend><bean:message key='kq.item.count'/></legend>
     	<table width="100%" border="0">
        	<tr> 
          		<td height="260" valign="top">
          		<div id="scroll_box">
          			${indBatchHandForm.tablestr}
          		</div>
          		</td>
        	</tr>
      	</table>
      	</fieldset>
     	</td>
    <td  valign="top" align="left" style="padding-left: 5px;">
    	<table width="100%" border="0"  cellpadding="0px" cellspacing="0px" align="left">
        <tr>
          <td height="44px" align="left" >
			<input type="button" name="Submit32" value="公式" onclick='setFormula("${indBatchHandForm.unit_type}","${indBatchHandForm.infor}","<%=request.getParameter("setname") %>");' Class="smallbutton"></td>
        </tr>
        <tr> 
          <td height="44px" align="left"> 
            <input type="button" name="Submit" value="<bean:message key='kq.shift.cycle.up'/>" 
            onclick="upSort();saveSort();" Class="smallbutton"> 
          </td>
        </tr>
        <tr> 
          <td height="44" align="left"> 
           <input type="button" name="Submit2" value="<bean:message key='kq.shift.cycle.down'/>" 
            onclick="downSort();saveSort();" Class="smallbutton">
          </td>
        </tr>
      </table></td>
  </tr>
  <tr> 
    <td colspan="2" style="padding-left:5px;"> 
    	<logic:equal name="indBatchHandForm" property="unit_type" value="2">
    	<input type="checkbox" name="results" value="1" id='results'>
       	<bean:message key='infor.menu.result.calculation'/>&nbsp;&nbsp;&nbsp;&nbsp; 
	    <input type="checkbox" name="history" id="history" value="1">
	    <bean:message key='infor.menu.history.calculation'/>
      </logic:equal>
      <logic:equal name="indBatchHandForm" property="unit_type" value="3">
    	<input type="checkbox" name="computeScope" id="computeScope" value="1">
       	<bean:message key='infor.menu.definition.computescope'/>&nbsp;&nbsp;&nbsp;&nbsp; 
      </logic:equal>
    </td>
  </tr>
  <tr> 
    <td colspan="2" align="center" height="35px"> 
    	<input type="button" name="Submit" id="submitId" value="<bean:message key='infor.menu.ok1'/><bean:message key='infor.menu.ok2'/>" Class="mybutton" onclick='colFormulaOk("${indBatchHandForm.dbname}","${indBatchHandForm.setname}","${indBatchHandForm.a_code}","${indBatchHandForm.viewsearch}","${indBatchHandForm.infor}");'> 
    	<input type="button" name="Submit2" id="returnId" value="<bean:message key='infor.menu.no1'/><bean:message key='infor.menu.no2'/>" onclick="openClose();" Class="mybutton"> 
    </td>
  </tr>
</table>
<html:hidden name="indBatchHandForm" property="unit_type" styleId="unit_type"/>
<html:hidden name="indBatchHandForm" property="entranceFlag" styleId="entranceFlag"/>
</table>
</div>
</html:form>
<script language="JavaScript">
//浏览器兼容性关闭弹窗方法  wangb 20180127
function openClose(){
	//if(getBrowseVersion()){
		//top.close();
	//}else{
		if(parent.parent.winClose)
			parent.parent.winClose();
		else
			window.close();
	//}
}	
// IE 11浏览器 未加兼容性  自助服务/员工信息/信息维护  计算页面 样式错误     bug 35051 wangb 20180302
var fieldset = document.getElementsByTagName('fieldset')[0];
fieldset.style.width='340px';
var iframe = parent.parent.document.getElementsByTagName('iframe')[1];
if(iframe)//bug35811
	iframe.setAttribute('height','99%');
defaultSelect("${indBatchHandForm.unit_type}");
</script>