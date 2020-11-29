<%@page import="com.hrms.struts.constant.WebConstant"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.sys.options.parttimeparamset.ParttimeParamSetForm"%>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<script type="text/javascript">
<!--
function sub(){
if(parttimeParamSetForm.flag.checked==false){
  parttimeParamSetForm.flag.value="false";
 }else{
 	parttimeParamSetForm.flag.value="true";
 }
 if(parttimeParamSetForm.isk.checked==true){
   parttimeParamSetForm.takeup_quota.value="1";  
 }else
 {
    parttimeParamSetForm.takeup_quota.value="0";  
 }
 if(document.getElementById("coccupy_quota").checked==true)
	 document.getElementById("hoccupy_quota").value="1";
 else
	 document.getElementById("hoccupy_quota").value="0";
 parttimeParamSetForm.action="/system/options/parttimeparamset/saveParttimeParamSet.do?b_init=init";
 parttimeParamSetForm.submit();
}
function change(){
	checkflag();
var v = parttimeParamSetForm.setid.value;
  	var hashvo=new ParameterSet(); 	  	
    hashvo.setValue("setid",v);
   	var In_paramters="flag=1"; 	
	var request=new Request({method:'post',asynchronous:false,parameters:In_paramters,onSuccess:change_ok,functionId:'1010021009'},hashvo);			
}
function change_ok(outparamters){
	var unitList=outparamters.getValue("unitList");
	var appointList=outparamters.getValue("appointList");
	var poslist=outparamters.getValue("poslist");
	var itemlist=outparamters.getValue("itemlist");
	var nitemlist=outparamters.getValue("nitemlist");
	AjaxBind.bind(parttimeParamSetForm.unit,unitList);
	AjaxBind.bind(parttimeParamSetForm.dept,unitList);
	AjaxBind.bind(parttimeParamSetForm.pos,poslist);
	parttimeParamSetForm.isk.checked=false;
	parttimeParamSetForm.takeup_quota.value="0";
	Element.hide('wait');
	AjaxBind.bind(parttimeParamSetForm.appoint,appointList);
	parttimeParamSetForm.flag.checked=false;
	AjaxBind.bind(parttimeParamSetForm.order,nitemlist);
	AjaxBind.bind(parttimeParamSetForm.format,itemlist);
}
function ret(){
	window.location.href="/selfservice/param/otherparam.do?b_other=link";
}
function checkPosItem()
{
  var obj = parttimeParamSetForm.pos;  
  var itemid=obj.value;
  var hashvo=new ParameterSet();
  hashvo.setValue("itemid",itemid);    
  var request=new Request({method:'post',asynchronous:false,onSuccess:change_pos,functionId:'1010021010'},hashvo);		
}
function change_pos(outparamters)
{
  var isk=outparamters.getValue("isk");
  if(isk=="1")
  {
     Element.show('wait');
  }else
  {
    Element.hide('wait');
  }
}
function checkflag(){//zgd 2014-3-3 未设置兼职参数项，不让启用
	var setid = parttimeParamSetForm.setid.value;
	if(" " == setid){
		var cflag = document.getElementById('cflag');
	    cflag.checked=false;
     	Element.show('nocheckflag');
    	Element.hide('checkflag');
	}else{
     	Element.hide('nocheckflag');
    	Element.show('checkflag');
	}
}
//-->
</script>
<html:form action="/system/options/parttimeparamset/initParttimeParamSet"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
<table width="85%" border="0" cellpadding="0" cellspacing="0" align="center" class="ListTable" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
		         <thead>
           <tr>
            <td align="left" class="TableRow" colspan="3">
		<bean:message key="parttime.param.set"/>
		<input type="hidden" name="takeup_quota" value="${parttimeParamSetForm.takeup_quota}">      
            </td>            	        	        	        
           </tr>
   	  </thead>
		           <tr>
		               <td width="20%" align="right" class="RecordRow">
		                <bean:message key="parttime.param.setid"/>&nbsp;
		               </td>
		               <td align="left" class="RecordRow" colspan="2">&nbsp;
		               <hrms:optioncollection name="parttimeParamSetForm" property="setList" collection="list" />
						 <html:select name="parttimeParamSetForm" property="setid" size="1" style="width:150px" onchange="change();">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
		               
		               </td>
		               
		              
			      </tr>
		           <tr>
		           <td width="20%" align="right" class="RecordRow">
		          <bean:message key="parttime.param.unit"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow">&nbsp;		           
		             <hrms:optioncollection name="parttimeParamSetForm" property="unitList" collection="list" />
						 <html:select name="parttimeParamSetForm" property="unit" size="1" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;<font color='black'><bean:message key="parttime.param.un"/></font>		               
		           </td>	
		           <td rowspan="2" class="RecordRow">
		               &nbsp;&nbsp;<input type="checkbox" id="coccupy_quota"><bean:message key="parttime.param.occupy_quota"/>
		               <logic:equal name="parttimeParamSetForm" property="occupy_quota" value="1">
		                   <script>document.getElementById("coccupy_quota").checked = true;</script>
		               </logic:equal>
		               <input type="hidden" name="occupy_quota" id="hoccupy_quota"/>
		           </td>	         
		           </tr>
		            <tr>
		           <td width="20%" align="right" class="RecordRow">
		          <bean:message key="parttime.param.dept"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow">&nbsp;		           
		           <hrms:optioncollection name="parttimeParamSetForm" property="unitList" collection="list" />
						 <html:select name="parttimeParamSetForm" property="dept" size="1" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;<font color='black'><bean:message key="parttime.param.un"/></font>		               
		           </td>		         
		           </tr>
		            <tr>
		           <td width="20%" align="right" class="RecordRow">
		              <bean:message key="parttime.param.pos"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow" colspan="2">	
		           	 <table border="0" cellpadding="0" cellspacing="0" align="left">  
		           	  <tr>
		           	    <td>&nbsp;
		           	      <hrms:optioncollection name="parttimeParamSetForm" property="poslist" collection="list" />
						  <html:select name="parttimeParamSetForm" property="pos" size="1" onchange="checkPosItem();parttimeParamSetForm.isk.checked=false;" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				          </html:select>		
		           	    </td>
		           	    <td>
		           	     <div id='wait' style='display:none;'>
		           	       <logic:notEqual name="parttimeParamSetForm" property="takeup_quota" value="1">
		           	        &nbsp;  <input type="checkbox" name="isk" value="1">
		           	       </logic:notEqual>
		           	       <logic:equal name="parttimeParamSetForm" property="takeup_quota" value="1">
		           	        &nbsp;  <input type="checkbox" name="isk" value="1" checked>
		           	       </logic:equal> 
		           	        &nbsp; <bean:message key="parttime.param.takeup_quota"/>
		           	     </div>
		           	    </td>
		           	  </tr>
		           	 </table>        
		                            
		           </td>		         
		           </tr>
		           <tr>
		           <td width="20%" align="right" class="RecordRow">
		           <bean:message key="parttime.param.appoint"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow" colspan="2">&nbsp;
		           <hrms:optioncollection name="parttimeParamSetForm" property="appointList" collection="list" />
						 <html:select name="parttimeParamSetForm" property="appoint" size="1" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;<font color='black'><bean:message key="parttime.param.thirty"/></font>
		           </td>
		           </tr>
		           <tr>
		           <td width="20%" align="right" class="RecordRow">
		           <bean:message key="parttime.param.order"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow" colspan="2">&nbsp;
		           <hrms:optioncollection name="parttimeParamSetForm" property="nitemlist" collection="list" />
						 <html:select name="parttimeParamSetForm" property="order" size="1" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>
		           </td>
		           </tr>
		           <tr>
		           <td width="20%" align="right" class="RecordRow">
		           <bean:message key="parttime.param.format"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow" colspan="2">
		           <hrms:optioncollection name="parttimeParamSetForm" property="itemlist" collection="list" />
		           <table border="0" cellpadding="0" cellspacing="0">
		           	<tr>
		           	<td>&nbsp;
		           		<html:select name="parttimeParamSetForm" property="format" size="1" style="width:150px">
				             <html:options collection="list" property="dataValue" labelProperty="dataName"/>
				        </html:select>&nbsp;
		           	</td>
		           	<td>
		           		<font color='black'><bean:message key="parttime.param.format.desc"/></font>
		           	</td>
		           	</tr>
		           </table>
		           </td>
		           </tr>
		           <tr>
		           <td width="20%" align="right" class="RecordRow">
		           <bean:message key="parttime.param.flag"/>&nbsp;
		           </td>
		           <td align="left" class="RecordRow" colspan="2">
		           <div id="checkflag">
					<logic:equal name="parttimeParamSetForm" property="flag" value="1">
						&nbsp;<input type="checkbox" name="flag" value="true" id="cflag">
					</logic:equal>
					<logic:equal name="parttimeParamSetForm" property="flag" value="2">
						&nbsp;<input type="checkbox" name="flag" value="true" id="cflag" checked>
					</logic:equal>
				</div>
				<div id="nocheckflag"> 
						&nbsp;<input type="checkbox" name="flag" value="true" id="cflag" onclick="alert('请先设置兼职参数项后，再启用！');return false;">
				</div>
		           		           </td>
		           </tr>
		           

		    

<TR>
<td colspan="3" class="RecordRow" style="height: 35px" align="center">
	<!--bug 33959 wangb 20180115 add 在chick 事件中submit提交 不能再使用submit按钮 -->
	<!--<hrms:submit styleClass="mybutton" property="" onclick="sub();"><bean:message key="button.save"/></hrms:submit>-->
	<html:button styleClass="mybutton" property="" onclick="sub();"><bean:message key="button.save"/></html:button>
</td>
</TR>

</table>
</html:form>
<script type="text/javascript">
checkflag();
checkPosItem();
</script>
