<%@ page contentType="text/html; charset=UTF-8"%>
<%@page import="java.util.HashMap"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
                 com.hjsj.hrms.actionform.performance.commend.insupportcommend.InSupportCommendForm" %>
<%
    InSupportCommendForm supportForm=(InSupportCommendForm)session.getAttribute("inSupportCommendForm");   
    String _dis=supportForm.getDisabled();
    String readOnly="";
    if (("disabled").equals(_dis)){
        readOnly="readOnly";
    }
    
%>  
<script type="text/javascript">
<!--
function trclick(trs,type){
	var str=document.getElementsByTagName("tr");
	for(var i=0;i<str.length;i++){
		if(str[i].id!="")
			$("text"+str[i].id).style.backgroundColor="";
		str[i].style.backgroundColor="";
	}
	trs.style.backgroundColor="#FFF8D2";
	if(type==1)
		$('text'+trs.id).style.backgroundColor="#FFF8D2";
}
function definite(){
	var dbpres="";
	var bodys="";
	var codes="";
	var counts="";
	var footer=document.getElementById("footer").value;
	var ckbs=document.getElementsByTagName("input");
	for(var i=0;i<ckbs.length;i++){
		if(ckbs[i].type=="checkbox"&&ckbs[i].checked){
			if(ckbs[i].id==""){
				bodys+=ckbs[i].name+",";
			}else if(ckbs[i].id.substring(0,2)=="db"){
				dbpres+=ckbs[i].name+",";
			}else{
				codes+=ckbs[i].name+",";
				var id=ckbs[i].id.substring(3);
				counts+=$F('text'+id)+",";
			}
		}
	}
	var obj=new Object();
	obj.dbpres=dbpres;
	obj.bodys=bodys;
	obj.codes=codes;
	obj.counts=counts;
	obj.footer=getEncodeStr(footer);
	returnValue=obj;
	window.close();
}
//-->
</script>
<table id="pres" width="100%" height="98%" border="0">
	<tr>
		<td width="83%" height="100%">
			<bean:define id="dis" name="inSupportCommendForm" property="disabled" />
			<fieldset style="height: 115px;margin-top: 0px;margin-left: 15px;padding: 12 8 0 8;">
                <legend>请选择候选人人员库：</legend>
                <div style="height: 83px;width: 100%; border: 1px solid #cccccc;overflow-y: scroll;overflow-x:auto;">
                	<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;" >
                		<logic:iterate id="dbpre" name="inSupportCommendForm" property="dbprelist" indexId="i">
                			<%String chkdb=""; %>
                			<logic:iterate id="dbs" name="inSupportCommendForm" property="dbpre_list">
		               			<logic:equal value="${dbs}" name="dbpre" property="dataName">
		               				<%chkdb="checked"; %>
		               			</logic:equal>
	               			</logic:iterate>
                			
                			<tr onclick="trclick(this,0)">
	                			<td width="25">&nbsp;<input type="checkbox" id="db${i }" name='<bean:write name="dbpre" property="dataName"/>' <%=chkdb %> <%=dis %>/>
	                			</td>
	                			<td><bean:write name="dbpre" property="dataValue"/></td>
                			</tr>
                		</logic:iterate>
                	</table>
                </div>
        	</fieldset>
			<fieldset style="height: 134px;margin-left: 15px;padding: 12 8 0 8;">
                <legend>请选择投票人类别：</legend>
                <div style="height: 102px;width: 100%; border: 1px solid #cccccc;overflow-y: scroll;overflow-x:auto;">
                	<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
                		<logic:iterate id="pres" name="inSupportCommendForm" property="preslist">
                			<%String chkbd=""; %>
                			<logic:iterate id="dbs" name="inSupportCommendForm" property="body_list">
		               			<logic:equal value="${dbs}" name="pres" property="string(body_id)">
		               				<%chkbd="checked"; %>
		               			</logic:equal>
	               			</logic:iterate>
	               			
                			<tr onclick="trclick(this,0)">
	                			<td width="25">&nbsp;<input type="checkbox" name='<bean:write name="pres" property="string(body_id)"/>' <%=chkbd %> <%=dis %>/>
	                			</td>
	                			<td><bean:write name="pres" property="string(name)"/></td>
                			</tr>
                		</logic:iterate>
                	</table>
                </div>
        	</fieldset>
        	<fieldset style="height: 159px;margin-left: 15px;padding: 12 8 0 8;">
                <legend>请选择推荐职务：</legend>
                <div style="height: 125px;width: 100%; border: 1px solid #cccccc;overflow-y: scroll;overflow-x:auto;">
               		<logic:iterate id="codes" name="inSupportCommendForm" property="codeslist" indexId="i">
               			<bean:define id="code" name="codes" property="dataName"></bean:define>
               			<%String chck="",vls=""; %>
               			<logic:iterate id="cds" name="inSupportCommendForm" property="pos_list">
	               			<logic:equal value="${cds[0]}" name="code">
	               				<%chck=((String[])cds)[1];vls=((String[])cds)[2]; %>
	               			</logic:equal>
	               		</logic:iterate>
	               		
                		<table border="0" cellpadding="0" cellspacing="0" style="width: 100%;">
	               			<tr id="${i }" onclick="trclick(this,1)">
	                			<td width="25" align="center">
	                				&nbsp;<input type="checkbox" id="ckb${i }" name='<bean:write name="codes" property="dataName"/>' <%=chck %> <%=dis %>/>
	                			</td>
	                			<td><bean:write name="codes" property="dataValue"/></td>
	                			<td width="60" align="right">推荐人数：</td>
	                			<td width="120">
	                				<input type="text" id="text${i }" style="border: 0px; border-bottom: 1px solid #000;width: 60px; " onpropertychange='if(/[^\d*]/.test(this.value)) this.value=this.value.replace(/[^\d*]/,"")' value="<%=vls %>" <%=dis %>/>
	                			</td>
	               			</tr>
	               		</table>
               		</logic:iterate>
                </div>
        	</fieldset>
        	<fieldset style="height: 85px;margin-left: 15px;padding: 12 0 0 0;">
                <legend>推荐表填写说明：</legend>
                <textarea rows="3" cols="70" name="footer" style="border: 1px solid #cccccc;margin-left: -7px;font-size: 12px;line-height: 15px;height: 50px;padding-left: 8px;" <%=readOnly %>>${inSupportCommendForm.footer }</textarea>
            </fieldset>
		</td>
		<td align="center" valign="top" style="padding-top: 30px;">
			<input type="button" value="确定" class="mybutton" onclick="definite();" <%=dis %>/><br/><br/>
			<input type="button" value="关闭" class="mybutton" onclick="javascript:window.close();"/>
		</td>
	</tr>
</table>