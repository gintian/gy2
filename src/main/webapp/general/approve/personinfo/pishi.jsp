<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script language="javascript">
function goBlack(){
	approvePersonForm.action= "/general/approve/personinfo/approve.do?b_search=link";	 
   	approvePersonForm.submit();
}
function beSave(){
	var sp_idea = document.getElementById("sp_idea").value;
	sp_idea=getEncodeStr(sp_idea);

	var hashvo=new ParameterSet();
	hashvo.setValue("chg_id","${approvePersonForm.chg_id}");
	hashvo.setValue("sp_idea",sp_idea);	
	var pars="checkflag=update";  	
	var request=new Request({method:'post',parameters:pars,asynchronous:false,onSuccess:indCheck,functionId:'0580010011'},hashvo);
}
function indCheck(outparamters){
	history.go(-1);
}
</script>
<hrms:themes />
<html:form action="/general/approve/personinfo/pishi">
<table width="400" border="0" cellpadding="0" cellspacing="0" align="center">
    <tr height="20">
       <!--<td width="10" valign="top" class="tableft"></td>
       <logic:notEqual name="approvePersonForm" property="checkflag" value="check">
       <td width="100" align=center class="tabcenter">&nbsp;领导批示&nbsp;</td>
       </logic:notEqual>
       <logic:equal name="approvePersonForm" property="checkflag" value="check">
       <td width="100" align=center class="tabcenter">&nbsp;审批过程&nbsp;</td>
       </logic:equal>
       <td width="10" valign="top" class="tabright"></td>
       <td valign="top" class="tabremain" width="380"></td>   --> 
       
       <logic:notEqual name="approvePersonForm" property="checkflag" value="check">
       <td  align=left class="TableRow">领导批示</td>
       </logic:notEqual>
       <logic:equal name="approvePersonForm" property="checkflag" value="check">
       <td  align=left class="TableRow">审批过程</td>
       </logic:equal>            	      
    </tr> 
	<tr>
		<td class="framestyle9">
			<table border="0" cellspacing="1"  align="center" cellpadding="0" style="margin:5px;">
				<logic:equal name="approvePersonForm" property="checkflag" value="check">
				<tr class="list3">
					 <td align="right" nowrap valign="top">审批过程&nbsp;</td>
					<td>
						<logic:notEqual name="approvePersonForm" property="checkflag" value="check"><!-- 为textarea添加样式style="resize:none;"  不让其可拉伸 add by xiegh bug36141-->
							<html:textarea property="process" cols="50" rows="15" style="resize:none;" readonly="true" value="${approvePersonForm.sp_idea}"/>
							<html:textarea name="approvePersonForm" property="sp_idea" cols="50" rows="5" style="resize:none;" value=""/>
						</logic:notEqual>
						<logic:equal name="approvePersonForm" property="checkflag" value="check">
							<html:textarea name="approvePersonForm" property="sp_idea" cols="50" style="resize:none;" rows="20" readonly="true"/>
						</logic:equal>
					</td>
				</tr>
				</logic:equal>
				<logic:notEqual name="approvePersonForm" property="checkflag" value="check">
					<tr class="list3">
					 <td align="right" nowrap valign="top">审批过程&nbsp;</td>
					<td>
						<html:textarea property="process" cols="50" rows="15" style="resize:none;" readonly="true" value="${approvePersonForm.sp_idea}"/>
					</td>
				</tr>
				<tr class="list3">
					 <td align="right" nowrap valign="top">领导批示&nbsp;</td>
					<td>
						<html:textarea name="approvePersonForm" styleId="sp_idea" property="sp_idea" cols="50" rows="5" style="resize:none;" value=""/>
					</td>
				</tr>
				</logic:notEqual>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center" height="35" >
			<logic:notEqual name="approvePersonForm" property="checkflag" value="check">
				<input type="button" name="button1" onclick="beSave();" class="mybutton" value="确定">
			</logic:notEqual>
			<input type="button" name="button2" onclick="history.go(-1);" class="mybutton" value="返回">
		</td>
	</tr>
</table>
</html:form>
