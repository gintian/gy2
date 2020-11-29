<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page errorPage="error.jsp"%>
<%@ page import="javax.servlet.ServletException"%>

<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
 <%
    UserView userView=(UserView)session.getAttribute(WebConstant.userView);
 String bosflag = "";
 if(userView!=null){
     bosflag = userView.getBosflag();
 }
 %>
<script language="javascript" src="/module/utils/js/template.js"></script>
<hrms:themes></hrms:themes>
<style>
.titlestyle{width:100%;height:16px;border-left:8px solid #ffffff;padding-left:5px}
</style>
<script language="javascript">
   function save()
   {
      if(confirm("确认保存数据吗!!"))
      {
        sysParamForm.action="/system/options/param/set_sys_param.do?b_save=link";
        sysParamForm.submit();
      }
   }
function setDb(field_falg)
{
    var target_url="/system/options/param/set_sys_param.do?b_setdb=link`field_falg="+field_falg;
    target_url = $URL.encode(target_url);
    var iframe_url="/general/query/common/iframe_query.jsp?src="+target_url;
    /*
    var return_vo= window.showModalDialog(iframe_url,null, 
        "dialogWidth:300px; dialogHeight:275px;resizable:no;center:yes;scroll:yes;status:no;scrollbars:yes");
        */
    //改用ext 弹窗显示  wangb 20190319
    var win = Ext.create('Ext.window.Window',{
		id:'select_db',
		title:'选择人员库',
		width:320,
		height:295,
		resizable:'no',
		modal:true,
		autoScoll:false,
		autoShow:true,
		autoDestroy:true,
		html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+iframe_url+'"></iframe>',
		renderTo:Ext.getBody()
	});      
}
function check()	//招聘管理 可以设置相同    xieguiquan 2010 9 16
{
//	if(($F('chk')==$F('onlyname'))&&($F('chk')!="请选择"&&$F('onlyname')!="请选择")){
//		alert("身份证指标和唯一性指标不能相同！");
//		return false;
//	}
}
function getBusinessTemplate(param)
{
	var obj=(eval("document.sysParamForm."+param));
      var select_id=obj.value;
      var t_url="/dtgh/party/person/party_parameter.do?b_template=link&type=1&dr=1&select_id="+select_id;
      var dw=300,dh=400,dl=(screen.width-dw)/2;dt=(screen.height-dh)/2;
     //改用ext 弹窗显示  wangb 20190319
     var win = Ext.create('Ext.window.Window',{
			id:'select_org',
			title:'机构调整',
			width:320,
			height:420,
			resizable:false,
			modal:true,
			autoScoll:false,
			autoShow:true,
			autoDestroy:true,
			html:'<iframe style="background-color:#fff" frameborder="0" SCROLLING=NO height="100%" width="100%" src="'+t_url+'"></iframe>',
			renderTo:Ext.getBody(),
			listeners:{
				'close':function(){
					if(!this.return_vo)
            return false;
         else
          {
             			if(this.return_vo.flag=="true")
             {
                var afield=eval('fieldIds'+param);   
                			var name=this.return_vo.title;
                			var ids=this.return_vo.content;
                var a_name=name.split(",");
                var a_ids=ids.split(",");
                var bnames="&nbsp;";
                var ids="";
                for(var i=0;i<a_ids.length;i++)
                {
                   if(a_ids[i]==null||a_ids[i]=='')
                   continue;
                   bnames+=/*a_ids[i]+":"+*/a_name[i]+"<br>";
                   ids+=","+a_ids[i];
                }
                if(ids.length>0)
                    ids=ids.substring(1);
                 if(bnames=='')
                     bnames="&nbsp;";
                 afield.innerHTML=bnames;
                 obj.value=ids;
             }
          }   
				}
			}
	});  
             
}
</script>
<html:form action="/system/options/param/set_sys_param"><!-- 系统管理，参数设置，hr页面，内容与顶部之间间隔10px   jingq  add 2014.12.23 -->
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" <%if(!userView.isBbos()&&!"hcm".equals(bosflag)){%>style="display:none"<%} %> <%if(!"hcm".equals(bosflag)){ %>style="margin-top:10px;"<%} %>>
  <tr>  
    <td valign="top" align="left">
      <fieldset align="center" style="width:80%;">
          <legend ><bean:message key="edit_report.paramSet" /></legend>
          <table border="0" cellspacing="2"  align="center" cellpadding="0" >
          <tr height="30px;">
             <td align="right" style="padding-right: 5px;"><bean:message key="sys.options.param.type" /></td>
               <td width="250" align="left">
                <logic:iterate id="type" name="sysParamForm" property="type_list">
                	<input type="checkbox" name="typestr" value='<bean:write name="type" property="value" />' <bean:write name="type" property="check" /> > 
	                	<bean:write name="type" property="name" />
                </logic:iterate>
             </td>
         </tr>
         <hrms:priv module_id="18">         
         <tr height="30px;">
             <td align="right" style="padding-right: 5px;"><bean:message key="label.bos.goboard" /></td>
               <td width="125" align="left">
                <html:select name="sysParamForm" property="operationcode" size="0" style="width:150px;">
                <html:optionsCollection property="codelist" value="dataValue" label="dataName"/>
                </html:select> 
             </td>
         </tr>
         <tr height="30px;">
             <td align="right" style="padding-right: 5px;"><bean:message key="label.bos.goboardset" /></td>
               <td width="125" align="left">
                <html:select name="sysParamForm" property="goboardset" size="0" style="width:150px;">
                <html:optionsCollection property="setlist" value="dataValue" label="dataName"/>
                </html:select> 
             </td>
         </tr>
         </hrms:priv>      
        
       </table>
     </fieldset>

    </td>
  </tr>
</table>  
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" <%if(!userView.isBbos()&&!"hcm".equals(bosflag)){%>style="display:none"<%} %> >
	<tr>  
    <td valign="top" align="left">
      <fieldset align="center" style="width:80%;" style="padding-top:10px;">
		<legend><bean:message key="sys.options.param.idcardandcount"/></legend>
          <table border="0" cellspacing="2"  align="center" cellpadding="0" >
         <tr>
				<td colspan="2" width="10px" align="left" style="position: relative;left:-20px">
					<div class="titlestyle"><bean:message key="sys.options.param.uniquenesstargetset"/></div>
				</td>
			</tr>
			<tr>
				<td colspan="2"><input type="checkbox" name="validstr" value='uniquenessvalid' <bean:write name="sysParamForm" property="uniquenesscheck" /> ><bean:message key="sys.options.param.uniquenesstarget"/>
					<html:select name="sysParamForm" property="onlyname" size="0" style="width:150px;">
					<html:optionsCollection property="chklist" value="dataValue" label="dataName"/>
					</html:select>
					<input type="button" name="btnreturn" value='<bean:message key="leaderteam.leaderparam.dbsetting"/>' class="mybutton"  onclick="setDb('0');">
				</td>
			</tr>
			<tr>
				<td colspan="2" width="10px" align="left" style="position: relative;left:-20px">
				<br>
					<div class="titlestyle"><bean:message key="sys.options.param.idcardparam"/></div>
				</td>
			</tr>
			<tr>
				<td align="left" height="30" width="80" style="padding-left:3px"><bean:message key="sys.options.param.idtypetarget"/></td>
				<td>
					<html:select name="sysParamForm" property="idType" size="0" style="width:150px;">
						<html:optionsCollection property="idTypeList" value="dataValue" label="dataName"/>
					</html:select>
				</td>
			</tr>
			<tr>
				<td align="left" height='30' width="80" style="padding-left:3px" nowrap><bean:message key="sys.options.param.idcardtarget"/></td>
				<td>
					<html:select name="sysParamForm" property="chk" size="0" style="width:150px;">
						<html:optionsCollection property="chklist" value="dataValue" label="dataName"/>
					</html:select>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<input type="checkbox" name="validstr" value='chkvalid' <bean:write name="sysParamForm" property="chkcheck" /> >
					<bean:message key="sys.options.param.checkonly"/>
					<input type="button" name="btnreturn" value='<bean:message key="leaderteam.leaderparam.dbsetting"/>' class="mybutton"  onclick="setDb('1');">
				</td>
			</tr>
			<tr>
			<td height="30" colspan="2">
            <logic:equal value="0" name="sysParamForm" property="cardflag">
            <input type="checkbox" name="bycardnovalid" /> 
           </logic:equal>
           <logic:equal value="1" name="sysParamForm" property="cardflag">
            <input type="checkbox" name="bycardnovalid" checked="checked"/>
           
           </logic:equal>
             	<bean:message key="sys.options.param.aboutidcard"/>
             	<bean:message key="sys.options.param.birthday"/>
               <html:select name="sysParamForm" property="bycardnobirth" size="0" style="width:110px;">
               <html:option value=" "></html:option>
                <html:optionsCollection  property="destbirthlist" value="dataValue" label="dataName"/>
                </html:select> <bean:message key="sys.options.param.age"/>
                <html:select name="sysParamForm" property="bycardnoage" size="0" style="width:110px;">
                <html:option value=" "></html:option>
                <html:optionsCollection  property="agelist" value="dataValue" label="dataName"/>
                </html:select> 
                	<bean:message key="sys.options.param.sex"/><html:select name="sysParamForm" property="bycardnoax" size="0" style="width:110px;">
                <html:option value=" "></html:option>
                <html:optionsCollection  property="axlist" value="dataValue" label="dataName"/>
                </html:select> 
             </td>
            </tr>
            <tr>
				<td colspan="2" width="10px" align="left" style="position: relative;left:-20px">
				<br>
					<div class="titlestyle"><bean:message key="sys.options.param.otherparam"/></div>
				</td>
			</tr>
			<tr>
				<td colspan="2" height="30">
             <logic:equal value="0" name="sysParamForm" property="workflag">
             <input type="checkbox" name="byworkvalid"/>
             </logic:equal>
             <logic:equal value="1" name="sysParamForm" property="workflag">
             <input type="checkbox" name="byworkvalid" checked="checked"/>
             </logic:equal>
           
             <bean:message key="system.option.an"/>
                <html:select name="sysParamForm" property="byworksrc" size="0" style="width:110px;">
              
                <html:optionsCollection  property="destbirthlist" value="dataValue" label="dataName"/>
                </html:select> <bean:message key="button.computer"/>
               
                <html:select name="sysParamForm" property="byworkdest" size="0" style="width:110px;">
                <html:option value=" "></html:option>
                <html:optionsCollection  property="agelist" value="dataValue" label="dataName"/>
                </html:select> 
             </td>
            </tr>
            <tr>
				<td colspan="2" height="30">
             <logic:equal value="0" name="sysParamForm" property="orgflag">
             <input type="checkbox" name="byorgvalid"/>
             </logic:equal>
             <logic:equal value="1" name="sysParamForm" property="orgflag">
             <input type="checkbox" name="byorgvalid" checked="checked"/>
             </logic:equal>
          
             <bean:message key="system.option.an"/>
                <html:select name="sysParamForm" property="byorgsrc" size="0" style="width:110px;">
                <html:optionsCollection  property="destbirthlist" value="dataValue" label="dataName"/>
                </html:select> <bean:message key="button.computer"/>
                <html:select name="sysParamForm" property="byorgdest" size="0" style="width:110px;">
                  <html:option value=" "></html:option>
                <html:optionsCollection  property="agelist" value="dataValue" label="dataName"/>
                </html:select> 
             </td>
            </tr>
 				</table>
			</fieldset>
		</td>
	</tr>
</table>
<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0" <%if(!userView.isBbos()&&!"hcm".equals(bosflag)){%>style="display:none"<%} %> >
<html:hidden name="sysParamForm" property="transfer"/>
<html:hidden name="sysParamForm" property="combine"/>
<html:hidden name="sysParamForm" property="bolish"/>
<html:hidden name="sysParamForm" property="del"/>
	<tr>  
		<td valign="top" align="left">
			<fieldset align="center" style="width:80%;" style="padding-top:10px;">
 				<legend ><bean:message key="sys.options.param.peopleorganizationset"/></legend>
 				<table border="0" cellspacing="2"  align="center" cellpadding="0" style="margin-bottom:5px;">
					<tr height="20px;">
						<td ><bean:message key="sys.options.param.peopleorganizationset.transfer"/></td>
						<td class="unSelectedBackGroud common_border_color"  align='left' width='170'>
							<div id='fieldIdstransfer'  style="top:inherit">
		            			&nbsp;${sysParamForm.transferview }
		            		</div>
						</td>
						<td>
							<img alt="<bean:message key="sys.options.param.peopleorganizationset.set"/>" src="/images/code.gif" align="absmiddle" onclick='getBusinessTemplate("transfer");'/> 
						</td>
					</tr>
					<tr height="20px;">
						<td ><bean:message key="sys.options.param.peopleorganizationset.combine"/></td>
						<td class="unSelectedBackGroud common_border_color"  align='left' width='170'>
							<div id='fieldIdscombine'  style="top:inherit">
		            		&nbsp;${sysParamForm.combineview }
		            		</div>
						</td>
						<td>
							<img alt="<bean:message key="sys.options.param.peopleorganizationset.set"/>" src="/images/code.gif" align="absmiddle" onclick='getBusinessTemplate("combine");'/> 
						</td>
					</tr>
					<tr>
						<td ><bean:message key="sys.options.param.peopleorganizationset.bolish"/></td>
						<td class="unSelectedBackGroud common_border_color"  align='left' width='170'>
							<div id='fieldIdsbolish'  style="top:inherit">
		            		&nbsp;${sysParamForm.bolishview }
		            		</div>
						</td>
						<td>
							<img alt="<bean:message key="sys.options.param.peopleorganizationset.set"/>" src="/images/code.gif" align="absmiddle" onclick='getBusinessTemplate("bolish");'/> 
						</td>
					</tr>
					<tr>
						<td ><bean:message key="sys.options.param.peopleorganizationset.delete"/></td>
						<td class="unSelectedBackGroud common_border_color"  align='left' width='170'>
							<div id='fieldIdsdel'  style="top:inherit">
		            		&nbsp;${sysParamForm.delview }
		            		</div>
						</td>
						<td>
							<img alt="<bean:message key="sys.options.param.peopleorganizationset.set"/>" src="/images/code.gif" align="absmiddle" onclick='getBusinessTemplate("del");'/> 
						</td>
					</tr>
 				</table>
			</fieldset>
		</td>
	</tr>
</table>
          <table border="0" cellspacing="0"  align="center" cellpadding="0" >
          <tr>
           <td align="center" height="35px;">              
               <hrms:submit styleClass="mybutton" property="b_save" onclick="return check()">
      	  	<bean:message key="button.save"/>
	       </hrms:submit>
          </td></tr>
               </table>          
</html:form>
<script>
	if(!getBrowseVersion() || getBrowseVersion() == 10){// 非ie兼容模式下  样式修改
		var fieldset = document.getElementsByTagName('fieldset');
		for(var i = 0 ; i < fieldset.length; i++){
			fieldset[i].setAttribute('align','left');
			fieldset[i].style.margin = '0 auto';
		}
	}
</script>