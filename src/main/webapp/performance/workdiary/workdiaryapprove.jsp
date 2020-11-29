<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page import="com.hjsj.hrms.actionform.performance.workdiary.WorkdiaryForm,com.hjsj.hrms.utils.PubFunc"%>
<%
    WorkdiaryForm workdiaryForm=(WorkdiaryForm)session.getAttribute("workdiaryForm");
	String pendingCode = workdiaryForm.getPendingCode();
	String doneFlag = workdiaryForm.getDoneFlag();
	String saveFlag = request.getParameter("saveFlag");
	String _p0100 = (String)workdiaryForm.getP01Vo().getString("p0100");
	_p0100 = PubFunc.encrypt(_p0100);
	UserView userView=(UserView)session.getAttribute(WebConstant.userView);
	String hcmflag="";
	if(userView != null){
	  hcmflag=userView.getBosflag();
	}  
%>

<script language="JavaScript" src="/js/validate.js"></script>
<script language="JavaScript" src="/js/function.js"></script>
<script language="JavaScript" src="/js/popcalendar3.js"></script>
<style type="text/css"> 
.scroll_box {
    height: 200px;    
    width: 97%;            
    overflow: auto;            
   	BORDER-BOTTOM: #C4D8EE 1pt solid; 
    BORDER-LEFT: #C4D8EE 1pt solid; 
    BORDER-RIGHT: #C4D8EE 1pt solid; 
    BORDER-TOP: #C4D8EE 1pt solid ; 
}
</style>
<SCRIPT Language="JavaScript">
<%if("save".equals(saveFlag)){%>
	alert("保存成功！");
	<% }%>
dateFormat='yyyy-mm-dd'
//批准
function boks(){
		if(confirm(APPLICATION_OK+"？")){
		var pendingCode="<%=pendingCode%>";
	    if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	        document.getElementById("doneFlag").value="1";
	        workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_okfrominface=link&action=ok&fp=1";	 
    	    workdiaryForm.submit();
	    }else{
	    	//2016/1/26 wangjl 解决从我的任务进入日志审批、驳回后不返回主页的问题
	    	if("${workdiaryForm.home}"==5){
			    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_okmain=link&action=ok&fp=1";	 
		    	workdiaryForm.submit();
		    	}else{
			    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_ok=link&action=ok&fp=1";	 
		    	workdiaryForm.submit();
			    }
	    	}
    	}
}
//驳回
function b_backs(){
		if(confirm(DISMESSIONED_OK+"？")){
		var pendingCode="<%=pendingCode%>";
		if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	        document.getElementById("doneFlag").value="1";
    	    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_backfrominface=link&action=back&fp=1";
    		workdiaryForm.submit();
	    }else{
	    	//2016/1/26 wangjl 解决从我的任务进入日志审批、驳回后不返回主页的问题
	    	if("${workdiaryForm.home}"==5){
			    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_okmain=link&action=back&fp=1";	 
		    	workdiaryForm.submit();
		    	}else{
			    workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_back=link&action=back&fp=1";
	    		workdiaryForm.submit();
		    	}
	    	}
   		}
}
function returns(){
		if("${workdiaryForm.home}"==5)
			if('<%=hcmflag%>'=="hcm"){
 	      		 workdiaryForm.action='/templates/index/hcm_portal.do?b_query=link';      		
       		}else{
 	       		workdiaryForm.action='/templates/index/portal.do?b_query=link';      		
       		}
		else
	    	workdiaryForm.action="/performance/workdiary/workdiaryshow.do?br_return=link&sp_flag=1";
    	workdiaryForm.submit();
}
function b_saves(){
        var pendingCode="<%=pendingCode%>";
        if(pendingCode!=null && pendingCode!="" && pendingCode!='null'){
	        workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_okfrominface=link&action=save&fp=1";	 
    	    workdiaryForm.submit();
	    }else{
			workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_saveok=link&action=save&fp=1&saveFlag=save";
    		workdiaryForm.submit();
    	}
}
function hides(hide1,hiede2,hide3){
	//Element.hide(hide1);
	//Element.hide(hide3);
	//Element.toggle(hiede2);
	document.getElementById(hide1).style.display='none';
	document.getElementById(hide3).style.display='none';
	document.getElementById(hiede2).style.display='';
}
function toggles(toggles1,toggles2,toggles3){
	//Element.toggle(toggles1);
	//Element.toggle(toggles3);
	//Element.hide(toggles2);
	document.getElementById(toggles1).style.display='';
	document.getElementById(toggles3).style.display='';
	document.getElementById(toggles2).style.display='none';
}
function perPlan(plan_id,a0100){
	var theURL = "/performance/objectiveManage/objectiveCard.do?b_query=query`fromflag=rz`body_id=5`model=2`opt=0`planid="+plan_id+"`object_id="+a0100;
	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(theURL);
	window.showModalDialog(iframe_url,"",
			"dialogWidth=1000px;dialogHeight=500px;resizable=yes;scroll:yes;center:yes;status=no;"); 
	//window.open(iframe_url,"_blank","left=0,top=0,width="+screen.availWidth+",height="+screen.availHeight+",scrollbars=yes,toolbar=no,menubar=no,location=yes,resizable=yes,status=yes");  	
}
function uploadFile(fileid){
	var hashvo=new ParameterSet();
	hashvo.setValue("check","outfile");
	hashvo.setValue("fileid",fileid);
	hashvo.setValue("flag","56");
	var request=new Request({method:'post',asynchronous:false,onSuccess:showFieldList,functionId:'2020030040'},hashvo);
}	
function showFieldList(outparamters){
	var outName=outparamters.getValue("outname");
	if(outName!=null&&outName.length>1)
		window.open("/servlet/vfsservlet?fromjavafolder=true&fileid="+outName);
}
function b_appeal(){
	var request=new Request({method:'post',asynchronous:false,onSuccess:getSuperiorUser,functionId:'9020010015'},null);
}
function getSuperiorUser(outparamters){
	document.getElementById("curr_user").value="";
	if(outparamters.getValue("outname").length==1){
		document.getElementById("curr_user").value = (outparamters.getValue("outname")[0].split(":"))[0];
		workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_ok=link&action=app&fp=1";
    	if(confirm(APP_OK+"?"))
    		workdiaryForm.submit();
	}else if(outparamters.getValue("outname").length>1){
		var thecodeurl="/performance/workdiary/cat.jsp?outname="+outparamters.getValue("outname");
    	var return_vo= window.showModalDialog(thecodeurl, "_blank", 
              "dialogHeight:220px;dialogWidth:330px;center:yes;help:no;resizable:yes;status:no;scroll:no;");
       if(return_vo!=null && return_vo.length>=0){
       		document.getElementById("curr_user").value=return_vo;
			workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_ok=link&action=app&fp=1";		 
	    	if(confirm(APP_OK+"?"))
	    		workdiaryForm.submit();
       }
	}else{
		workdiaryForm.action="/performance/workdiary/workdiaryshow.do?b_ok=link&action=app&fp=1";		 
    	if(confirm(APP_OK+"?"))
    		workdiaryForm.submit();
	}
}
</SCRIPT><center>
<hrms:themes />
<html:form action="/performance/workdiary/workdiaryapprove">
<html:hidden property="doneFlag" name="workdiaryForm"/>
<br/>
<bean:define id="p0100" name="workdiaryForm" property="p01Vo.string(p0100)"/>
<html:hidden name="workdiaryForm" property="p01Vo.string(p0100)" />
	<fieldset align="center" style="width:80%;">
	<legend><bean:write name="workdiaryForm" property="p01Vo.string(a0101)"/><bean:message key="workdiary.message.work.log"/></legend>
	<br/>
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0" >	
		   <logic:notEqual value="no" name="workdiaryForm" property="perPlanTable">
			<tr>
				<td><bean:write name="workdiaryForm" property="p01Vo.string(a0101)"/>的目标卡</td>
				<td>
					${workdiaryForm.perPlanTable}
				</td>
			</tr>
			</logic:notEqual>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">									
					<bean:define id="fid" name="info" property="itemid"/>					
					<logic:equal value="p0104" name="fid">
					<tr>
					<td   nowrap>
						<bean:write name="info" property="itemdesc"/>
						</td>	
						<td   nowrap>
						
						<input type='text' name='startime' class='TEXT_NB' size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.startime}" disabled="true" />
						
						</td>
					</TR>
					</logic:equal>
			</logic:iterate>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">	
					<bean:define id="fid" name="info" property="itemid"/>
					<logic:equal value="p0106" name="fid">
					<tr>
					<td   nowrap>
						<bean:write name="info" property="itemdesc"/>	
						</td>
						<td   nowrap>
						<input type='text' name='endtime' class='TEXT_NB'   size='20'   onclick='popUpCalendar(this,this, dateFormat,"","",true,true)'  value="${workdiaryForm.endtime}" disabled="true"/>		
						</td>
					</TR>
					</logic:equal>
			</logic:iterate>
			<logic:iterate id="info" name="workdiaryForm" property="fieldlist">	
			<bean:define id="fid" name="info" property="itemid"/>
			<bean:define id="ftype" name="info" property="itemtype"/>
			<bean:define id="flen" name="info" property="itemlength"/>
			<bean:define id="disa" name="workdiaryForm" property="dis"/>
			<logic:notEqual value="p0100" name="fid">
			<logic:notEqual value="nbase" name="fid">
			<logic:notEqual value="p0115" name="fid">
			<logic:notEqual value="p0104" name="fid">
			<logic:notEqual value="p0106" name="fid">
			<logic:notEqual value="p0114" name="fid">
			<logic:notEqual value="p0113" name="fid">
			<logic:notEqual value="e0122" name="fid">
			<logic:notEqual value="e01a1" name="fid">
			<logic:notEqual value="b0110" name="fid">
			<logic:notEqual value="a0101" name="fid">
			<logic:equal value="A" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
			<html:text styleId="${fid}" name="workdiaryForm" property="p01Vo.string(${fid})" maxlength="${flen}"  disabled="${disa}"></html:text>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="D" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
			<html:text styleId="${fid}" name="workdiaryForm" property="p01Vo.date(${fid})" maxlength="${flen}" disabled="${disa}"></html:text>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="N" name="ftype">
			<tr>
			<td valign="top" width="55" style="word-break: break-all; word-wrap:break-word;">
			<bean:write name="info" property="itemdesc"/>
			</td>
			<td  style="word-break: break-all; word-wrap:break-word;">
			<html:text styleId="${fid}" name="workdiaryForm" property="p01Vo.double(${fid})" maxlength="${flen}" disabled="${disa}"></html:text>
			</td>
			</tr>
			</logic:equal>
			<logic:equal value="M" name="ftype">
			<tr>
			<td colspan="2">
				<table width="100%" border="0">
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
						<logic:equal value="" name="workdiaryForm" property="p01Vo.string(${fid})">
							<span id="${fid}view" style="display:none;">
							<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
							<span id="${fid}hide">
							<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
						</logic:equal>
						<logic:notEqual value="" name="workdiaryForm" property="p01Vo.string(${fid})">
							<span id="${fid}view">
							<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
							<span id="${fid}hide" style="display:none;">
							<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
							</span>
						</logic:notEqual>
						</td>
					</tr>
				</table>
				<logic:equal value="" name="workdiaryForm" property="p01Vo.string(${fid})">
				<span id="${fid}_view" style="display:none;">
				</logic:equal>
				<logic:notEqual value="" name="workdiaryForm" property="p01Vo.string(${fid})">
				<span id="${fid}_view">
				</logic:notEqual>
				<table width="100%" border="0">
					<tr>
						<td width="55">&nbsp;</td>
						<td>
							<logic:equal name="info" property="itemid" value="p0120">
							<logic:equal value="02" name="workdiaryForm" property="p01Vo.string(p0115)">
							<html:textarea styleId="${fid}" name="workdiaryForm" property="p01Vo.string(${fid})" cols='80'  rows='20'></html:textarea>
							</logic:equal>
							<logic:notEqual value="02" name="workdiaryForm" property="p01Vo.string(p0115)">
							<html:textarea styleId="${fid}" name="workdiaryForm" property="p01Vo.string(${fid})" readonly="${disa}" cols='80'  rows='20'></html:textarea>
							</logic:notEqual>
							</logic:equal>
							<logic:notEqual name="info" property="itemid" value="p0120">
							<html:textarea styleId="${fid}" name="workdiaryForm" property="p01Vo.string(${fid})" readonly="${disa}" cols='80'  rows='20'></html:textarea>
							</logic:notEqual>
						</td>
					</tr>
				</table>
				</span>
			</td>
			</tr>
			</logic:equal>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			</logic:notEqual>
			
			<logic:equal value="p0113" name="fid">
					<tr>
						<td colspan="2"  nowrap>
						<table width="100%" border="0">
							<tr>
								<td height="30" style="word-break: break-all; word-wrap:break-word;">
								<logic:equal value="1" name="workdiaryForm" property="appflag">
									<logic:equal value="" name="workdiaryForm" property="p01Vo.string(p0113)">
										<span id="${fid}view" style="display:none">
										<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
										<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
										</span>
										<span id="${fid}hide">
										<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
										<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
										</span>
									</logic:equal>
									<logic:notEqual value="" name="workdiaryForm" property="p01Vo.string(p0113)">
										<span id="${fid}view">
										<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
										<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
										</span>
										<span id="${fid}hide" style="display:none">
										<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
										<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
										</span>
									</logic:notEqual>
								</logic:equal>
								<logic:equal value="0" name="workdiaryForm" property="appflag">
									<span id="${fid}view">
									<a href="###" onclick='hides("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
									<a href="###"><img src="/images/button_vert1.gif" onclick='hides("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
									</span>
									<span id="${fid}hide" style="display:none">
									<a href="###" onclick='toggles("${fid}view","${fid}hide","${fid}_view");'><bean:write name="info" property="itemdesc"/></a>
									<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("${fid}view","${fid}hide","${fid}_view");' border="0"></a>
									</span>
								</logic:equal>
								</td>
							</tr>
						</table>
						<logic:equal value="1" name="workdiaryForm" property="appflag">
							<logic:equal value="" name="workdiaryForm" property="p01Vo.string(p0113)">
								<span id="p0113_view" style="display:none;">
							</logic:equal>
							<logic:notEqual value="" name="workdiaryForm" property="p01Vo.string(p0113)">
								<span id="p0113_view">
							</logic:notEqual>
						</logic:equal>
						<logic:equal value="0" name="workdiaryForm" property="appflag">
							<span id="p0113_view">
						</logic:equal>
						<table width="100%" border="0">
							<tr>
								<td width="55">&nbsp;</td>
								<td  nowrap>
								
								<logic:equal value="1" name="workdiaryForm" property="appflag">
									<html:textarea name="workdiaryForm" property="p01Vo.string(p0113)"  cols='80'  rows='5'  readonly="true"></html:textarea>
								</logic:equal>
								<logic:equal value="0" name="workdiaryForm" property="appflag">
									<html:textarea name="workdiaryForm" property="p01Vo.string(p0113)"  cols='80'  rows='5'  ></html:textarea>
								</logic:equal>
								 <!-- 
								 <html:textarea name="workdiaryForm" property="p01Vo.string(p0113)"  cols='80'  rows='5'  ></html:textarea>
								-->
								</td>
							</tr>
						</table>
						</td>
					</TR>
					</logic:equal>	
			</logic:iterate>
			<logic:equal value="1" name="workdiaryForm" property="existFile">
			<tr>
			<td colspan="2">
				<table width="100%" border="0">
					<tr>
						<td height="30" style="word-break: break-all; word-wrap:break-word;">
							<span id="file_idview">
							<a href="###" onclick='hides("file_idview","file_idhide","file_id_view");'>附件</a>
							<a href="###"><img src="/images/button_vert1.gif" onclick='hides("file_idview","file_idhide","file_id_view");' border="0"></a>
							</span>
							<span id="file_idhide" style="display:none">
							<a href="###" onclick='toggles("file_idview","file_idhide","file_id_view");'>附件</a>
							<a href="###"><img src="/images/button_vert2.gif" onclick='toggles("file_idview","file_idhide","file_id_view");' border="0"></a>
							</span>
						</td>
					</tr>
				</table>
				<span id="file_id_view">
				<table width="660" border="0" cellspacing="0" cellpadding="0" class="ListTable">
					<tr>
						<td width="55">&nbsp;</td>
						<td>
						<div class="scroll_box common_border_color">
						<table width="98%" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF" style="margin-top:5px;">
						<tr class="fixedHeaderTr">
							<td align="center" class="TableRow" nowrap>
								附件名称
							</td>
							<td align="center" width="60" class="TableRow" nowrap>
								下载
							</td>
						</tr>
						<%int i=0;%>
						<hrms:paginationdb id="element" name="workdiaryForm"
							sql_str="select file_id,name,P0100" table="" where_str="from per_diary_file where P0100='${p0100}'"
							columns="file_id,name,P0100" page_id="pagination"
							pagerows="100"
							order_by="order by file_id">
							<bean:define id="file_id" name="element" property="file_id" />
							 <% if(i%2==0){%>
          					<tr class="trShallow">
          					<%}else{%>
          					<tr class="trDeep">
          					<%}i++;%>  
							<td align="center" class="RecordRow" nowrap>
								<bean:write name="element" property="name" filter="true" />
							</td>
							<td align="center" class="RecordRow" nowrap>
								<a style="cursor:hand;color:#0000FF" href="javascript:uploadFile('${file_id}');">下载</a>
							</td>
							</tr>
						</hrms:paginationdb>
					</table>
					</div>
					</td>
					</tr>
			</table>
			</span>
			</td>
			</tr>
			</logic:equal>
			<tr><td colspan="2">&nbsp;</td></tr>
	</table>
</fieldset>	
<table width="80%" align="center">
	<tr>
	<td align="center" height="40">

	
	<hrms:operateworkdiary p0100="<%=_p0100%>">
	<logic:equal value="0" name="workdiaryForm" property="csflag">
	 <!-- 保存 -->
	 <BUTTON id="save" name="back" class="mybutton"  onclick="b_saves();" ><bean:message key='kq.kq_rest.submit'/></BUTTON>&nbsp;
	<!-- 2016/1/19 wangjl 判断是否已批的位置为了保留“保存”按钮（全总 领导批示完成后，还希望增加一些批示内容） -->
	 <logic:notEqual value="07" name="workdiaryForm" property="p01Vo.string(p0115)">
	<logic:notEqual value="03" name="workdiaryForm" property="p01Vo.string(p0115)">
	 <!-- 报批 
	 <logic:equal value="1" name="workdiaryForm" property="curr_user">
		 <BUTTON name="back" class="mybutton" onclick="b_appeal()"><bean:message key='info.appleal.state1'/></BUTTON>&nbsp;
		 <input type="hidden" name="curr_user" id="curr_user">
	 </logic:equal>-->
	 <!-- 批准 -->
    <logic:equal value="0" name="workdiaryForm" property="appflag">
	<hrms:priv func_id="03061">	
		<BUTTON id="approve" name="bok" class="mybutton"  onclick="boks();" ><bean:message key="approve.personinfo.oks"/></BUTTON>&nbsp;
	</hrms:priv>
	<!-- 驳回 -->
	<hrms:priv func_id="03062">			
		<BUTTON id="reject" name="back" class="mybutton"  onclick="b_backs();" ><bean:message key="info.appleal.state2"/></BUTTON>&nbsp;	
	</hrms:priv>	
	</logic:equal>
	</logic:notEqual>
	
	</logic:notEqual>
	
	</logic:equal>
	</hrms:operateworkdiary>
	

	<logic:notEqual value="4" name="workdiaryForm" property="home">
	<% if(pendingCode!=null && pendingCode.trim().length()>0 && !pendingCode.equals("null")) {%>
			 <input type="button" name="close" value="关闭" onclick="javascript:window.close();" class="mybutton"/>
			<% }else { %> 
	<BUTTON name="back" class="mybutton"  onclick="returns();" ><bean:message key="button.return"/></BUTTON>&nbsp;
	<% }  %>
	</logic:notEqual>
	</td>
	</tr>
	</table>
<script language="javascript">
   <% if(pendingCode!=null && pendingCode!="" && !pendingCode.equals("null") && doneFlag!=null && doneFlag.equals("1")){%>
	   document.getElementById("save").disabled=true; 
	   document.getElementById("approve").disabled=true; 
	   document.getElementById("reject").disabled=true; 
	<% }%>
</script>
<!-- wangjl 全国总工会需要右键菜单 -->
<script type="text/javascript">
window.onload = function(){
	document.oncontextmenu = function(e) {return true;}
}
</script>
</html:form>
</center>
