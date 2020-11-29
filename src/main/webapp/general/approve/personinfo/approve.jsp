<%@page import="com.hjsj.hrms.actionform.general.approve.personinfo.ApprovePersonForm"%>
<%@page import="com.hrms.hjsj.sys.DataDictionary"%>
<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hjsj.hrms.utils.PubFunc"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="javascript" src="/ajax/basic.js"></script>
<script language="javascript" src="/js/validate.js"></script>
<script type="text/javascript" src="../../../../js/hjsjUrlEncode.js"></script>
<html:form action="/general/approve/personinfo/approve">
<% 
String data_approve="true";

UserView userView=(UserView)session.getAttribute(WebConstant.userView);
String bosflag=userView.getBosflag();
ApprovePersonForm approvePersonForm = (ApprovePersonForm)session.getAttribute("approvePersonForm");
String returnflag = approvePersonForm.getReturnflag();
%>
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

function returnCheckbox(){
	var boxstr="";
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
	    if(tablevos[i].type=="checkbox"){
	    	if(tablevos[i].checked==true){
	    		if (tablevos[i].name != "box")
	    			boxstr+=tablevos[i].name+",";
	    	}
		}
    }
    return boxstr;
}
function checkFlagAll(flag){
    var boxstr = returnCheckbox();//tiany 田野添加操作前判断用户是否勾选 ，没有勾选进行提示
    if(boxstr==''){
        alert("请选择操作人员");
        return;
    }
	if(flag=='boflagall'){//update by xiegh on date20171125 修改自助服务-员工信息-信息审核：浏览器兼容问题   员工审核 
		if(confirm("确定退回选中的记录?")){
			//var boxstr = returnCheckbox();
			var url = "/general/approve/personinfo/backdate.jsp?callback=closeWindowAction`flag="+flag+"`boxstr="+boxstr;
			iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
			 window.open(iframe_url,'_blank','width=470,height=400,top=100px,left=50px,toolbar=no,location=no,resizable=no');
   		}
   	}else if(flag=='pflagall'){
   		if(confirm("确定批准选中的记录?")){
   			var url = "/general/approve/personinfo/pibackdate.jsp?callback=closeWindowAction`flag="+flag+"`boxstr="+boxstr;
   			iframe_url = "/general/query/common/iframe_query.jsp?src="+$URL.encode(url);
   			window.open(iframe_url,'_blank','width=470,height=400,top=100px,left=50px,toolbar=no,location=no,resizable=no');
   		}
   	}else if(flag=='delall'){
   		if(confirm("确定删除选中的记录?")){
			//var boxstr = returnCheckbox();
			approvePersonForm.action= "/general/approve/personinfo/approve.do?b_search=link&savEdit="+flag+"&code=${approvePersonForm.a_code}&chg_idall="+boxstr;	 
   			approvePersonForm.submit();
   		}
   	}
}
function closeWindowAction(arrays){
	//在IE浏览器中  console.log() 方法报错 不执行     bug 34780 wangb 20180227
	approvePersonForm.action= "/general/approve/personinfo/approve.do?b_search=link&savEdit="+$URL.encode(arrays[0])+"&code=${approvePersonForm.a_code}&chg_idall="+$URL.encode(arrays[1])+"&returnVa="+$URL.encode(getEncodeStr(arrays[2]));	
	approvePersonForm.submit();
}
function changeFlag(){
	approvePersonForm.action="/general/approve/personinfo/approve.do?b_search=link&code=${approvePersonForm.a_code}&param=0";	 
   	approvePersonForm.submit();
}
function querry() {
	approvePersonForm.action="/general/approve/personinfo/approve.do?b_search=link&code=${approvePersonForm.a_code}&app=1";	 
   	approvePersonForm.submit();
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
function returnH(url,target)
{
    approvePersonForm.action=url;
    approvePersonForm.target=target;
    approvePersonForm.submit();
}
</script>
<%int i = 0; %>
<hrms:themes />
<bean:define id="a_code" name="approvePersonForm" property="a_code"></bean:define>
<bean:define id="kind" name="approvePersonForm" property="kind"></bean:define>
<table align="center" border="0" cellspacing="0"  cellpadding="0" width="100%" style="margin-top: 8px">
	<tr>
		<td>
			<table align="left" border="0" cellspacing="0"  cellpadding="0">
				<tr>
					<td width="180" height="30">人员库
						<html:select name="approvePersonForm" onchange="changeFlag();" property="setname" style="width:100px"> 
							<html:optionsCollection  property="setnamelist" value="dataValue" label="dataName"/>
						</html:select>
					</td>
					<td width="180" height="30">审批状态
						<html:select name="approvePersonForm" onchange="changeFlag();" property="sp_flag" style="width:100px"> 
							<html:optionsCollection  property="spflaglist" value="dataValue" label="dataName"/>
						</html:select>
					</td>
					<td width="200" height="30">姓名
						<html:text name="approvePersonForm" property="employeeName" styleClass="text4" size="8" style="margin-top:-5px;"></html:text>
						&nbsp;<input name="sele" type="button" class="mybutton" value="<bean:message key="button.query"/>" onclick="querry();"/>
					</td>
					<td>&nbsp;</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td >
			<table width="100%" align="center" border="0" cellspacing="0"  cellpadding="0"  class="ListTable" >
				<tr>
					<td  align='center' class="TableRow" nowrap ><input type="checkbox" name="box" id="checkAll" onclick="selectAll()"/></td>
					<td  align='center' class="TableRow" nowrap ><%=DataDictionary.getFieldItem("b0110").getItemdesc() %></td>
					<td  align='center' class="TableRow" nowrap ><%=DataDictionary.getFieldItem("e0122").getItemdesc() %></td>
					<td  align='center' class="TableRow" nowrap ><%=DataDictionary.getFieldItem("e01a1").getItemdesc() %></td>
					<td  align='center' class="TableRow" nowrap >姓名</td>
					<td  align='center' class="TableRow" nowrap >变动内容</td>
					<td  align='center' class="TableRow" nowrap >变动时间</td>
					<td  align='center' class="TableRow" nowrap >批示</td>
					<td  align='center' class="TableRow" nowrap >状态</td>
				</tr>
				<hrms:paginationdb id="element" name="approvePersonForm" sql_str="approvePersonForm.sql" 
				table="" where_str="approvePersonForm.where" columns="approvePersonForm.column" 
				order_by="order by create_time desc,A0000" pagerows="10" page_id="pagination" indexes="indexes">
				<bean:define id="chg_id" name='element' property='chg_id'/>
			<%
				String chgId = PubFunc.encrypt(chg_id.toString());
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
					<td align="center" valign="middle" class="RecordRow" nowrap>			
							
						<logic:equal name="element" property="sp_flag" value="02">
							&nbsp;<input type="checkbox" name="<%=chgId %>">&nbsp;
						</logic:equal>	
						<logic:equal name="element" property="sp_flag" value="03">
						    <logic:equal name="approvePersonForm" property="checked_may_reject" value="true">
							&nbsp;<input type="checkbox" name="<%=chgId %>">&nbsp;
							</logic:equal>
						</logic:equal>	
					</td>
					<td align="center" valign="middle" class="RecordRow" nowrap>
						<hrms:codetoname codeid="UN" name="element" codevalue="b0110" codeitem="codeitem" scope="page"/>  	      
          		 		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					</td>
					<td align="center" valign="middle" class="RecordRow" nowrap>
						<hrms:codetoname codeid="UM" name="element" codevalue="e0122" codeitem="codeitem" scope="page"/>  	      
          		 		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					</td>
					<td align="center" valign="middle" class="RecordRow" nowrap>
						<hrms:codetoname codeid="@K" name="element" codevalue="e01a1" codeitem="codeitem" scope="page"/>  	      
          		 		&nbsp;<bean:write name="codeitem" property="codename" />&nbsp;
					</td>
					<td align="center" valign="middle" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element" property="a0101" />&nbsp;
					</td>
					<td align="center" class="RecordRow" nowrap>
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
								document.iframe_${chg_id}_user.location.href="/general/approve/personinfo/iframapp.do?b_query=link&a_code=${approvePersonForm.a_code}&chg_id=${chg_id}&fcheck="+fcheck;
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
							<iframe id="iframe_${chg_id}_user" name="iframe_${chg_id}_user" frameborder="0" scrolling="auto"  width="400" height="300" src="#"></iframe>
						</div>
						-->
			 			 <bean:define id="description" name="element" property="description"></bean:define>
						 <%
			 				String encryptParam = PubFunc.encrypt("a_code="+a_code.toString()+"&kind="+kind.toString()+"&chg_id="+chgId+"&fcheck=fopen&showinfo=change&returnflag=" + returnflag);
						 	String desc = description.toString();
						 	if(desc.endsWith(","))
						 	   desc = desc.substring(0, desc.length() - 1);
			 			 %>
						&nbsp;<a href="/general/approve/personinfo/iframapp.do?b_query=link&encryptParam=<%=encryptParam %>" title="内详">
							<%=desc %></a>&nbsp;
					</td>
					<td align="center" valign="middle" class="RecordRow" nowrap>
						<bean:define id="create_time" name='element' property='create_time'/>
						<script language="javascript">
							var createtime = "${create_time}";
							document.write(createtime);
						</script>
					</td>
					<td align="center" class="RecordRow" nowrap>
						<logic:equal name="element" property="sp_flag" value="07">
						&nbsp;<a href="/general/approve/personinfo/pishi.do?b_query=link&checkflag=search&chg_id=<%=chgId %>"><img src="/images/edit.gif" border="0"/></a>&nbsp;
						</logic:equal>
						<logic:equal name="element" property="sp_flag" value="03">
						&nbsp;<a href="/general/approve/personinfo/pishi.do?b_query=link&checkflag=check&chg_id=<%=chgId %>"><img src="/images/edit.gif" border="0"/></a>&nbsp;
						</logic:equal>
						<logic:equal name="element" property="sp_flag" value="02">
						&nbsp;<a href="/general/approve/personinfo/pishi.do?b_query=link&checkflag=check&chg_id=<%=chgId %>"><img src="/images/edit.gif" border="0"/></a>&nbsp;
						</logic:equal>
						<logic:equal name="element" property="sp_flag" value="01">
						&nbsp;<img src="/images/edit.gif" border="0"/>&nbsp;
						</logic:equal>
					</td>
					<td align="center" class="RecordRow" nowrap>
						<logic:equal name="element" property="sp_flag" value="01">&nbsp;起草&nbsp;</logic:equal>
						<logic:equal name="element" property="sp_flag" value="02">&nbsp;已报批&nbsp;</logic:equal>
						<logic:equal name="element" property="sp_flag" value="03">&nbsp;已批&nbsp;</logic:equal>
						<logic:equal name="element" property="sp_flag" value="07">&nbsp;退回&nbsp;</logic:equal>
					</td>
				</tr>
				</hrms:paginationdb>
			</table>
			<table width="100%" align="center" class="RecordRowP">
			<tr>
				<td width="50%" valign="bottom" align="left" class="tdFontcolor" nowrap>
				
					<bean:message key="label.page.serial" />
					<bean:write name="pagination" property="current" filter="true" />
					<bean:message key="label.page.sum" />
					<bean:write name="pagination" property="count" filter="true" />
					<bean:message key="label.page.row" />
					<bean:write name="pagination" property="pages" filter="true" />
					<bean:message key="label.page.page" />	
				</td>
				<td width="50%" align="right" nowrap>
				   <p align="right" style="padding-right:5px;">
					<hrms:paginationdblink name="approvePersonForm" property="pagination" nameId="browseRegisterForm" scope="page">
						</hrms:paginationdblink>
				   </p>
				</td>
				</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td style="padding-top:5px;">
 			<hrms:priv func_id="03082,260632">
			<input type="button" name="button11" onclick="checkFlagAll('pflagall')"  class="mybutton" value="批准">
			</hrms:priv>
			<hrms:priv func_id="03081,260631">
			<input type="button" name="button12" onclick="checkFlagAll('boflagall')"  class="mybutton" value="退回">
			</hrms:priv>
			<hrms:priv func_id="03085,260635">
			<input type="button" name="button13" onclick="checkFlagAll('delall')" class="mybutton" value="删除">
			</hrms:priv>
              <logic:equal name="approvePersonForm" property="returnflag" value="portal">
              <%if("hcm".equalsIgnoreCase(bosflag)){ %>
                 <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('/templates/index/hcm_portal.do?b_query=link','il_body');">
              <%}else{ %>
              	 <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('/templates/index/portal.do?b_query=link','il_body');">
              <%} %>
              </logic:equal>
              <logic:equal name="approvePersonForm" property="returnflag" value="tasklist">      
              	 <input type="button" name="addbutton"  value="<bean:message key="button.return"/>" class="mybutton" onclick="returnH('/general/template/matterList.do?b_query=link','il_body');">
              </logic:equal>
		</td>
	</tr>
</table>
<script language="javascript">


<logic:notEqual name="approvePersonForm" property="redundantInfo" value="">
 alert('${approvePersonForm.redundantInfo}');
</logic:notEqual>

</script>
</html:form>
