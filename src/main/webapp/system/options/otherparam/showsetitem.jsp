
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.gz.gz_self.tax.SelfTaxForm"%>
<hrms:themes></hrms:themes>
<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="javascript" src="/module/utils/js/template.js"></script>
<script type="text/javascript" language="javascript">
function getcodesetid(){
	var codesetid=sysOthParamForm.codesetid.value;
	var cod=codesetid.split("/");
	var vo=document.getElementsByName("fenleiset")[0];
    if(vo.checked==true)
    {
       if(confirm("该类别指标已定义为授权指标分类，切换类别指标则该授权分类将被取消，是否要继续更改类别指标?"))
       { 
         var itemid=sysOthParamForm.codesetid.value;
         var hashvo=new ParameterSet();
         hashvo.setValue("empty","1");    
         hashvo.setValue("itemid",itemid);
         var request=new Request({method:'post',asynchronous:false,functionId:'1010021011'},hashvo);
       }else
       {
          return false;
       }
    }
	sysOthParamForm.action="/system/options/otherparam/showsetitem.do?b_query=link&codesetid="+cod[0]+"&selected="+codesetid;
	sysOthParamForm.submit();
}
function getcodeItem(cid){
	var codesetid=sysOthParamForm.codesetid.value;
	/*
	var return_vo= window.showModalDialog("/system/options/otherparam/employeeitemtree.do?b_query=link&param=root&froms=item&name="+cid+"&codesetid="+codesetid, false, 
        "dialogWidth:300px; dialogHeight:400px;resizable:no;center:yes;scroll:yes;status:no");  
   if(return_vo==null){
   return;
   }else{
   if(return_vo.length<1){
   return_vo=",";
   }
   var pars="item="+return_vo+"/"+cid+"/"+codesetid;
   var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:updatedbitem,functionId:'1010021005'});
   }
   */
   //改用ext 弹窗显示  wangb 20190319
   var url = "/system/options/otherparam/employeeitemtree.do?b_query=link&param=root&froms=item&name="+cid+"&codesetid="+codesetid;
   var win = Ext.create('Ext.window.Window',{
			id:'select_field',
			title:'请选择',
			width:320,
			height:420,
			resizable:'no',
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(this.return_vo==null){
   						return;
   					}else{
   						if(this.return_vo.length<1){
   							this.return_vo=",";
   						}
   						var pars="item="+this.return_vo+"/"+cid+"/"+codesetid;
   						var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:updatedbitem,functionId:'1010021005'});
   					}
				}
			}
	});  
   
}
function updatedbitem(){
	alert("修改成功！");
	var codesetid=sysOthParamForm.codesetid.value;
	var cod=codesetid.split("/");
	sysOthParamForm.action="/system/options/otherparam/showsetitem.do?b_query=link&codesetid="+cod[0];
	sysOthParamForm.submit();
}
function bcc(){
	sysOthParamForm.action="/system/options/param/set_sys_param.do?b_query=link";
	sysOthParamForm.submit();
}
function setvalid(flag){
	var pars="pars="+flag;
   	var request=new Request({method:'post',asynchronous:false,parameters:pars,onSuccess:setsucess,functionId:'1010021006'});
	
}
function setsucess(){
	alert("设置成功!");
	var codesetid=sysOthParamForm.codesetid.value;
	var cod=codesetid.split("/");
	sysOthParamForm.action="/system/options/otherparam/showsetitem.do?b_query=link&codesetid="+cod[0];
	sysOthParamForm.submit();
}
function fenleisetpriv()
{
   var isf="0";
   var vo=document.getElementsByName("fenleiset")[0];
   if(vo.checked==true)
   {
     isf="1";
   }
   var itemid=sysOthParamForm.codesetid.value;
   var hashvo=new ParameterSet();
   hashvo.setValue("isf",isf);    
   hashvo.setValue("itemid",itemid);
   var request=new Request({method:'post',asynchronous:false,functionId:'1010021011'},hashvo);
}
</script>
<html:form action="/system/options/otherparam/showsetitem">

	<table width="80%" border="0" cellspacing="0" align="center" cellpadding="0" class="ListTable">
		<tr>
		<td colspan="3" align="left" nowrap>
		 <table width="100%" border="0" cellspacing="0" height="30" cellpadding="0">
		  <tr>
		    <td width="30%" nowrap style="padding-left:5px;">
		      <bean:message key="system.option.renyuanleibiezhibiao"/>&nbsp;<bean:write name="sysOthParamForm" property="selStr" filter="false"/>
		    </td>
		    <td nowrap>
		       <input type="checkbox" name="fenleiset" value="true" onclick="fenleisetpriv();">&nbsp;<bean:message key="system.option.renyuanleibiefenlei"/>
		    </td>
		  </tr>
		 </table>
		</td>
		</tr>
		<tr>
		<td align="left" class="TableRow" nowrap><bean:message key="system.option.renyuanleibie"/>
		</td>
		<td align="left" class="TableRow" nowrap><bean:message key="system.option.zhibiaofw"/>
		</td>
		<td align="left" class="TableRow" nowrap><bean:message key="column.operation"/>
		</td>
		</tr>
		<hrms:paginationdb id="element" name="sysOthParamForm" sql_str="sysOthParamForm.sql" table="" where_str="sysOthParamForm.where" columns="sysOthParamForm.column" order_by="sysOthParamForm.orderby" pagerows="10" page_id="pagination" indexes="indexes">	
		<bean:define id="cid" name="element" property="codeitemid"/>
		<tr>
		<td class="RecordRow" nowrap>
		<bean:write name="element" property="codeitemdesc"/>
		&nbsp;
		</td>
		<td class="RecordRow" nowrap>&nbsp;
		<logic:iterate id="itemd"  indexId="ind" name="sysOthParamForm" property="itemMap">
		<logic:notEmpty name="itemd">
		<logic:equal value="${cid}" name="itemd" property="key">
			<logic:iterate id="basefield" indexId="inds" name="itemd" property="value">
			
				<logic:iterate id="fff" name="basefield" property="value">
				<bean:write name="fff" filter="false"/><br/>
				</logic:iterate>
			</logic:iterate>
		</logic:equal>
		</logic:notEmpty>
		</logic:iterate>
		</td>
		<td class="RecordRow" nowrap>
		<button type="button" name="" class="mybutton" onclick="getcodeItem('${cid}')">
		<bean:message key="button.orgmapset"/>
		</button>&nbsp;
		</td>
		</tr>
		</hrms:paginationdb>
	</table>
	<table width="80%" align="center" class="RecordRowP">
		<tr>
		    <td align="left" nowrap>		       
		     <logic:equal value="true" name="sysOthParamForm" property="view_check">
		      <logic:notEmpty name="sysOthParamForm" property="itemvalid">
		       <logic:equal value="on" name="sysOthParamForm" property="itemvalid">
			    <INPUT type="checkbox" name="itemvalid" onclick="setvalid(this.checked);" checked="true"/>
		       </logic:equal>
		       <logic:equal value="true" name="sysOthParamForm" property="itemvalid">
			    <INPUT type="checkbox" name="itemvalid" onclick="setvalid(this.checked);" checked="true"/>
		       </logic:equal>
		       <logic:equal value="false" name="sysOthParamForm" property="itemvalid">
			     <INPUT type="checkbox" name="itemvalid" onclick="setvalid(this.checked);"/>
		       </logic:equal>
		      </logic:notEmpty>
		      <logic:empty name="sysOthParamForm" property="itemvalid">
		       <INPUT type="checkbox" name="itemvalid" onclick="setvalid(this.checked);" checked="true"/>
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
		<td colspan="2">
		<button type="button" name="bc" class="mybutton" onclick="bcc();">
		<bean:message key="button.return"/>
		</button>
		</td>
		</tr>
</table>
 -->
</html:form>
<script type="text/javascript" language="javascript">
function checkfenleisetpriv()
{
   var isf="0";
   var vo=document.getElementsByName("fenleiset")[0];
   if(vo.checked==true)
   {
     isf="1";
   }
   var itemid=sysOthParamForm.codesetid.value;
   var hashvo=new ParameterSet();
   hashvo.setValue("checked","1");    
   hashvo.setValue("itemid",itemid);
   var request=new Request({method:'post',asynchronous:false,onSuccess:rechecked,functionId:'1010021011'},hashvo);
}
function rechecked(outparamters)
{
    var ischecked=outparamters.getValue("ischecked");
    if(ischecked=="1")
    {
       var vo=document.getElementsByName("fenleiset")[0];
       vo.checked=true;      
    }
}
function recodesetid(){
	var codesetid=sysOthParamForm.codesetid.value;
	var cod=codesetid.split("/");
	sysOthParamForm.action="/system/options/otherparam/showsetitem.do?b_query=link&codesetid="+cod[0]+"&selected="+codesetid;
	sysOthParamForm.submit();
}
checkfenleisetpriv();
</script>