<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<style type="text/css">
.appblack {
	BORDER-BOTTOM: #94B6E6 1pt solid; 
	BORDER-LEFT: #94B6E6 1pt solid; 
	BORDER-RIGHT: #94B6E6 1pt solid; 
	BORDER-TOP: #94B6E6 1pt solid;
	font-size: 12px;
	height:22;
 	background-image:url(/images/mainbg.jpg);
}
</style>
<script language="javascript">
var viewiframs ="";
var hideiframs ="";
/*显示*/
function toggles(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "block";
	}
}
/*隐藏*/
function hides(targetId){
	if (document.getElementById(targetId)){
		target = document.getElementById(targetId);
		target.style.display = "none";
	}
}
function spFlag(){
	if(confirm(APP_DATA_NOT_UPDATE+"?")){
		var tablevos=document.getElementsByTagName("input");
		var chg_id="";
		for(var i=0;i<tablevos.length;i++){
	    	if(tablevos[i].type=="checkbox"){
	    		if(tablevos[i].checked==true)
	    			if(tablevos[i].name!="selbox")
	    			chg_id+=tablevos[i].value+",";
			}
   	 	}
   	 	if(chg_id.length<1){
   	 		alert(SELECT_APPED+"!");
   	 		return;
   	 	}
   	 		
		selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link&savEdit=baopi&chg_id="+chg_id;	 
   		selfInfoForm.submit();
   	}
}
function delFlag(){
	if(confirm(DEL_INFO)){
		var tablevos=document.getElementsByTagName("input");
		var chg_id="";
		for(var i=0;i<tablevos.length;i++){
	    	if(tablevos[i].type=="checkbox"){
	    		if(tablevos[i].checked==true)
	    			if(tablevos[i].name!="selbox")
	    			chg_id+=tablevos[i].value+",";
			}
   	 	}
   	 	if(chg_id.length<1){
   	 		alert(SELECT_DELETED+"!");
   	 		return;
   	 	}
		selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link&savEdit=delAll&chg_id="+chg_id;	 
   		selfInfoForm.submit();
   }
}
function changeFlag(){
	selfInfoForm.action="/selfservice/selfinfo/inforchange.do?b_query=link";
   	selfInfoForm.submit();
}
function IsDigit(){ 
    return ((event.keyCode >= 47) && (event.keyCode <= 57)); 
}

//全选
function selectAll() {
	var checkAll = document.getElementById("checkAll");
	if (checkAll.checked == true) {//选中
		box = document.getElementsByTagName("input");
		for (i = 0; i < box.length; i ++) {
			if (box[i].type=="checkbox") {
				box[i].checked=true;
			}
		}	
	} else {//不选中
		box = document.getElementsByTagName("input");
		for (i = 0; i < box.length; i ++) {
			if (box[i].type=="checkbox") {
				box[i].checked=false;
			}
		}
	}
}
</script>
<hrms:themes />
<html:form action="/selfservice/selfinfo/inforchange">
<%int n=0;
int i = 0;
%>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0">
	<tr><!-- 【6101】我的变动信息明细，线条的颜色有点粗。jingq upd 2014.12.18 -->
		<td class="RecordRow" style="border-bottom:none;"><bean:message key='lable.zp_plan.status'/>
			<html:select name="selfInfoForm" onchange="changeFlag();" property="allflag" style="width:100px"> 
				<html:optionsCollection  property="spflaglist" value="dataValue" label="dataName"/>
			</html:select>
		</td>
	</tr>
</table>
<table width="80%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable1">
	<tr>
		<td width="5%" class="TableRow" align="center">
			<input type="checkbox" id="checkAll" name="selbox" onclick="selectAll()" title='<bean:message key="label.query.selectall"/>'>
		</td>
		<td class="TableRow" align="center"><bean:message key='workdiary.message.change.infor'/></td>
		<td class="TableRow" width="20%" align="center"><bean:message key='workdiary.message.change.time'/></td>
		<td width="10%" class="TableRow" align="center"><bean:message key='workdiary.message.leader.ship'/></td>
		<td width="10%" class="TableRow" align="center"><bean:message key='workdiary.message.all.state'/></td>
	</tr>
	<hrms:paginationdb id="element" name="selfInfoForm" sql_str="selfInfoForm.sql" table="" where_str="selfInfoForm.where" columns="selfInfoForm.column" order_by="order by create_time desc" pagerows="10" page_id="pagination" indexes="indexes">
	<bean:define id="chg_id" name='element' property='chg_id'/>
	<%
		String chgid = PubFunc.encrypt(chg_id.toString());
          if(i%2==0)
          {
          %>
          <tr class="trShallow">
          <%}
          else
          {%>
          <tr class="trDeep">
          <%
          }
          i++;          
          %>
		<td class="RecordRow" align="center" valign="center"  nowrap>
			<logic:equal name="element" property="sp_flag" value="01">
				<input type="checkbox" name="box_<%=n%>" value="<%=chgid %>">
			</logic:equal>
			<logic:equal name="element" property="sp_flag" value="07">
				<input type="checkbox" name="box_<%=n%>" value="<%=chgid %>">
			</logic:equal>
		</td>
		<td align="left" class="RecordRow"  nowrap>
		<!--
			<script language="javascript">
				function linkIframe${chg_id}(fcheck,checkView){
					hides(hideiframs);
					toggles(viewiframs);
					viewiframs = "div${chg_id}view";
					hideiframs = "div${chg_id}hide";
					if(checkView=='view'){
						toggles(hideiframs);
						hides(viewiframs);
						document.iframe_${chg_id}_user.location.href="/selfservice/selfinfo/iframapp.do?b_query=link&chg_id=${chg_id}";
					}else{
						hides(hideiframs);
						toggles(viewiframs);
					}
				}
			</script>
			<div id="div${chg_id}view">
				<a href="###" onclick="linkIframe${chg_id}('fopen','view');" title="显示">
				<img src="/images/to_bottom.jpg" border="0"></a>
			</div>
			<div id="div${chg_id}hide" style="display:none">
				<a href="###" onclick="linkIframe${chg_id}('fclose','hide');" title="隐藏">
				<img src="/images/to_top.gif" border="0"></a><p>
				<iframe id="iframe_${chg_id}_user" name="iframe_${chg_id}_user" frameborder="0" scrolling="auto"  width="100%" height="300" src="#"></iframe>
			</div>
			 -->
			&nbsp;<a href="/selfservice/selfinfo/iframapp.do?b_query=link&chg_id=<%=chgid %>" title="<bean:message key='workdiary.message.detail'/>">
				<bean:write name="element" property="description"/></a>&nbsp; 
		</td>
		<td class="RecordRow" align="center">
			<bean:define id="create_time" name='element' property='create_time'/>
			<script language="javascript">
				var createtime = "${create_time}";
				document.write(createtime);
			</script>
		</td>
		<td class="RecordRow" align="center">
			<a href="/general/approve/personinfo/pishi.do?b_query=link&checkflag=check&chg_id=<%=chgid %>">
				<img src="/images/view.gif" border="0"/></a>
		</td>
		<td class="RecordRow" align="center" nowrap>
			<logic:equal name="element" property="sp_flag" value="01"><bean:message key='hire.jp.pos.draftout'/></logic:equal>
			<logic:equal name="element" property="sp_flag" value="02"><bean:message key='workdiary.message.apped'/></logic:equal>
			<logic:equal name="element" property="sp_flag" value="03"><bean:message key='label.hiremanage.status3'/></logic:equal>
			<logic:equal name="element" property="sp_flag" value="07"><bean:message key='button.rejeect2'/></logic:equal>
		</td>
	</tr>
	<%n++;%>
	</hrms:paginationdb>
</table>
<table width="80%" align="center" class="RecordRowP">
			<tr>
				<td width="300" valign="bottom" align="left" class="tdFontcolor" nowrap>
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />	
				</td>
				<td align="right" nowrap>
					<!--<hrms:paginationlink name="selfInfoForm" property="selfInfoForm.pagination"
				nameId="selfInfoForm" propertyId="roleListProperty"></hrms:paginationlink>-->
				<hrms:paginationdblink name="selfInfoForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				</td>
				</tr>
</table>
<table width="80%" align="center">
	<tr>
		<td align="center" height="40">
			<hrms:priv func_id="0103010602">
				<input type="button" name="button1" onclick="spFlag();" class="mybutton" value="<bean:message key='label.hiremanage.status2'/>">&nbsp;&nbsp;&nbsp;&nbsp;
			</hrms:priv>
			<hrms:priv func_id="0103010603">
				<input type="button" name="button2" onclick="delFlag();" class="mybutton" value="<bean:message key='lable.tz_template.delete'/>">&nbsp;&nbsp;&nbsp;&nbsp;
			</hrms:priv>
		</td>
	</tr>
</table>
</html:form>
