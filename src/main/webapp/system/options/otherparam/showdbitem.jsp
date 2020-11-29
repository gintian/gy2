
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_self.tax.SelfTaxForm"%>
<hrms:themes></hrms:themes>
<%
	UserView userView = (UserView)session.getAttribute(WebConstant.userView);
	String bosflag = userView.getBosflag();
%>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" language="javascript">
function getbasefield(dbpre){
	var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
	/*
	var return_vo= window.showModalDialog("/system/options/otherparam/employeeitemtree.do?b_query=link&param=root&froms=db&name="+dbpre, false, 
         "dialogLeft:"+dl+"px;dialogTop:"+dt+"px;dialogWidth:"+dw+"px; dialogHeight:"+dh+"px;resizable:no;center:yes;scroll:yes;status:no");
   if(return_vo==null){
   return;
   }else{
   if(return_vo.length<1){
   return_vo=",";
   }
   var pars="item="+return_vo+"/"+dbpre;
   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:updatedbitem,functionId:'1010021004'});
   }
   */
   //改用ext 弹窗显示  wangb 20190318
   var url = "/system/options/otherparam/employeeitemtree.do?b_query=link&param=root&froms=db&name="+dbpre;
   var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'选择指标',
			width:dw+20,
			height:dh+20,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo == null ){
   						return;
   					}else{
   						if(this.return_vo.length<1){
   							this.return_vo=",";
   						}
   						var pars="item="+this.return_vo+"/"+dbpre;
   						var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:updatedbitem,functionId:'1010021004'});
   					}
				}
			}
	});
   
}
function updatedbitem(){
	alert("修改成功！");
	sysOthParamForm.action="/system/options/otherparam/showdbitem.do?b_query=link";
	sysOthParamForm.submit();
}
function bcc(){
	sysOthParamForm.action="/system/options/param/set_sys_param.do?b_query=link";
	sysOthParamForm.submit();
}
function setvalid(flag){
	var pars="pars=db"+flag;
   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:setsucess,functionId:'1010021006'});
	
}
function setsucess(){
	alert("设置成功!");
	sysOthParamForm.action="/system/options/otherparam/showdbitem.do?b_query=link";
	sysOthParamForm.submit();
}
</script>
<html:form action="/system/options/otherparam/showdbitem"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable" <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
		<THEAD>		
		<tr>
		<td  align="left" class="TableRow" nowrap>
		<bean:message key="label.dbase"/>
		</td>
		<td  align="left" class="TableRow" nowrap>
		<bean:message key="system.option.zhibiaofw"/>
		</td>
		<td  align="left" class="TableRow" nowrap>
		<bean:message key="column.operation"/>
		</td>
		</tr>
		</THEAD>		
		<hrms:paginationdb id="element" name="sysOthParamForm" sql_str="sysOthParamForm.sql" table="" where_str="sysOthParamForm.where" columns="sysOthParamForm.column" order_by="sysOthParamForm.orderby" pagerows="5" page_id="pagination" indexes="indexes">	
		<bean:define id="dbpre" name="element" property="pre"></bean:define>
		<tr>
		<td  class="RecordRow" nowrap>
		<bean:write name="element" property="dbname"/>
		</td>
		<td  class="RecordRow" nowrap>
		<logic:iterate id="dbitem"  indexId="ind" name="sysOthParamForm" property="dbMap">
		<logic:notEmpty name="dbitem">
		<logic:equal value="${dbpre}" name="dbitem" property="key">
			<logic:iterate id="basefield" indexId="inds" name="dbitem" property="value">
				<logic:iterate id="fff" name="basefield" property="value">
				<bean:write name="fff" filter="false"/><br/>
				</logic:iterate>
			</logic:iterate>
		</logic:equal>
		</logic:notEmpty>
		</logic:iterate>
		&nbsp;
		</td>
		<td  class="RecordRow" nowrap>
		<button type="button" name='mybutton'  onclick='getbasefield("${dbpre}");' class="mybutton">
		<bean:message key="button.orgmapset"/>
		</button>
		</td>
		</tr>
		</hrms:paginationdb>
	</table>
	<table width="80%" align="center" class="RecordRowP">
		<tr>
		    <td align="left" nowrap id="tb1">
		     <logic:equal value="true" name="sysOthParamForm" property="view_check">
		       <logic:notEmpty name="sysOthParamForm" property="dbvalid">
		        <logic:equal value="false" name="sysOthParamForm" property="dbvalid">
		        <input type="checkbox" name="dbvalid" onclick="setvalid(this.checked);"/>
		        </logic:equal>
		        <logic:equal value="true" name="sysOthParamForm" property="dbvalid">
		        <input type="checkbox" name="dbvalid" onclick="setvalid(this.checked);" checked="true"/>
		        </logic:equal>
		        <logic:equal value="on" name="sysOthParamForm" property="dbvalid">
		        <input type="checkbox" name="dbvalid" onclick="setvalid(this.checked);" checked="true"/>
		        </logic:equal>
		       </logic:notEmpty>
		       <logic:empty name="sysOthParamForm" property="dbvalid">
		       <input type="checkbox" name="dbvalid" onclick="setvalid(this.checked);"/>
		       </logic:empty>
		        启用
		     </logic:equal>  
		    </td>
		
			<td width="40%" valign="bottom" align="left" class="tdFontcolor" nowrap>
				<bean:message key="label.page.serial" />
				<bean:write name="pagination" property="current" filter="true" />
				<bean:message key="label.page.sum" />
				<bean:write name="pagination" property="count" filter="true" />
				<bean:message key="label.page.row" />
				<bean:write name="pagination" property="pages" filter="true" />
				<bean:message key="label.page.page" />
			</td>
			<td width="60%" align="right" nowrap class="tdFontcolor">
				<p align="right">
					<hrms:paginationdblink name="sysOthParamForm" property="pagination" nameId="browseRegisterForm" scope="page">
					</hrms:paginationdblink>
			</td>
		</tr>

	</table>
	<!-- 
<table  width="80%" align="center"  >
		<tr>
		<td >
		<button type="button" name="bc" class="mybutton" onclick="bcc();">
		<bean:message key="button.return"/>
		</button>
		</td>
		</tr>
</table>	
-->
<input type="hidden" name="dbnames"/>
</html:form>
<script type="text/javascript" language="javascript">

</script>