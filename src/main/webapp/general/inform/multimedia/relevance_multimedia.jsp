<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="com.hrms.struts.valueobject.UserView,com.hjsj.hrms.utils.PubFunc"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ page
	import="com.hjsj.hrms.actionform.general.inform.multimedia.MultiMediaFileForm,java.util.*,org.apache.commons.beanutils.DynaBean"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%
	// 在标题栏显示当前用户和日期 2004-5-10 
	String css_url = "/css/css1.css";
	UserView userView = (UserView) session
			.getAttribute(WebConstant.userView);
	if (userView != null) {
		css_url = userView.getCssurl();
		if (css_url == null || css_url.equals(""))
			css_url = "/css/css1.css";
	}
	MultiMediaFileForm multiMediaFileForm = (MultiMediaFileForm) session
			.getAttribute("multiMediaFileForm");
	
%>
<%
	int i = 0;
%>
<script language="JavaScript" src="../../../module/utils/js/template.js"></script>
<script type="text/javascript">
function chooseAll(){
	var tablevos=document.getElementsByTagName("input");
	for(var i=0;i<tablevos.length;i++){
		if(tablevos[i].type=="checkbox"&&tablevos[i].name=="checkall"){
			if(tablevos[i].checked==true){
				for(var j=0;j<tablevos.length;j++){
					if(tablevos[j].type=="checkbox"){
						if(tablevos[j].name=='delete_record')
							continue;
						tablevos[j].checked=true;
			      	}
				}
			}else{
				for(var j=0;j<tablevos.length;j++){
					if(tablevos[j].type=="checkbox"){
						if(tablevos[j].name=='delete_record')
							continue;
						tablevos[j].checked=false;
			      	}
				}
			}
			break;
		}
   	}
}
function returnback(){
	document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_query=link&a0100=${multiMediaFileForm.a0100}&multimediaflag=${multiMediaFileForm.multimediaflag}&isvisible=${multiMediaFileForm.isvisible}";
	document.multiMediaFileForm.submit();
}
function save(){
	document.getElementById("btnSave").disabled = true;
	
	var delrecord = "";
	var hashvo=new HashMap();
	var i9999list = "";
	var checklist = document.getElementsByTagName("input");
	for(var i=0;i<checklist.length;i++)
 	{
		if((checklist[i].type=="checkbox"&&checklist[i].name!="checkall")&&(checklist[i].type=="checkbox"&&checklist[i].name!="delete_record")){
			if(checklist[i].checked==true)
			i9999list+=checklist[i].name+","
		}
	}
	if(document.getElementById("delete_record").checked){
		delrecord=1;
	}else{
		delrecord=0;
	}
	hashvo.put("dbflag", '${multiMediaFileForm.dbFlag}');
	hashvo.put("nbase", '${multiMediaFileForm.nbase}');
	hashvo.put("a0100", '${multiMediaFileForm.a0100}');
	hashvo.put("i9999", '${multiMediaFileForm.i9999}');
	hashvo.put("setid", '${multiMediaFileForm.setId}');
	hashvo.put("i9999list", i9999list);
	hashvo.put("type", 'validaffix');
	//var request=new Request({method:'post',onSuccess:saveOk,functionId:'1010090026'},hashvo);
	Rpc({functionId:'1010090026',async:false,success:saveOk,scope:this},hashvo);
	 
}
function saveOk(outparamters){//update by xiegh ondate 20180320 bug35626
	var result = Ext.decode(outparamters.responseText);
	var delrecord = "";
	var i9999list = result.i9999list;
	var checklist = document.getElementsByTagName("input");
	if(document.getElementById("delete_record").checked){
		delrecord=1;
	}else{
		delrecord=0;
	}
	var infomsg=result.infomsg;
	if ((infomsg!=null) &&(infomsg!="")){
		alert(infomsg);
	}
	document.multiMediaFileForm.action="/general/inform/multimedia/opermultimedia.do?b_saverelevance=link&delete_record="+delrecord+"&i9999list="+i9999list;
	document.multiMediaFileForm.submit();
}

</script>
<%
String kind =multiMediaFileForm.getKind();
String nbase = multiMediaFileForm.getNbase();
String a0100 = multiMediaFileForm.getA0100();
 %>
<hrms:themes></hrms:themes>
<html:form action="/general/inform/multimedia/opermultimedia" method="post">
	<html:hidden name="multiMediaFileForm" property="i9999" />
	<table width="100%" border="0" cellspacing="0" align="center" cellpadding="0">
		<tr>
			<td align="left" nowrap>
				(
				<logic:equal value="6" name="multiMediaFileForm" property="kind">
					<bean:message key="label.title.org" />: <bean:write
						name="multiMediaFileForm" property="unit" filter="true" />&nbsp;
        			<bean:message key="label.title.dept" />: <bean:write
						name="multiMediaFileForm" property="pos" filter="true" />&nbsp;
      				<bean:message key="label.title.name" />: <bean:write
						name="multiMediaFileForm" property="a0101" filter="true" />&nbsp;
				</logic:equal>
				<logic:notEqual value="6" name="multiMediaFileForm" property="kind">
					<logic:equal value="0" name="multiMediaFileForm" property="kind">
						<bean:message key="e01a1.label" />: <bean:write name="multiMediaFileForm"
							property="pos" filter="true" />&nbsp;
					</logic:equal>
					<logic:notEqual value="0" name="multiMediaFileForm" property="kind">

						<logic:equal value="9" name="multiMediaFileForm" property="kind">
							基准岗位: <bean:write
								name="multiMediaFileForm" property="pos" filter="true" />&nbsp;
				    </logic:equal>

					<logic:notEqual value="9" name="multiMediaFileForm" property="kind">
							<bean:message key="label.title.org" />: <bean:write
								name="multiMediaFileForm" property="unit" filter="true" />&nbsp;  
				    </logic:notEqual>

					</logic:notEqual>
				</logic:notEqual>
				)
				&nbsp;&nbsp;&nbsp;&nbsp;<bean:write name="multiMediaFileForm" property="fieldsetdesc" filter="true" />
			</td>
		</tr>
	</table>
	<table width="100%" border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
		<thead>
			<tr>
				<td align="center" class="TableRow" width="30" nowrap>
					<input type=checkbox name="checkall" onclick="chooseAll()" title=<bean:message key='label.query.selectall' />>
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.title" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.type" />
					&nbsp;
				</td>
				<td align="center" class="TableRow" nowrap>
					<bean:message key="conlumn.resource_list.down" />
					&nbsp;
				</td>
			</tr>
		</thead>
		<logic:iterate id="element" name="multiMediaFileForm" property="a00_mul_list">
		<bean:define id="elementid" name="element"></bean:define>
			<%
			DynaBean vo = (DynaBean)pageContext.getAttribute("elementid");
			String i9999 = (String)vo.get("i9999");
			String fileid = (String)vo.get("fileid");
				if (i % 2 == 0) {
			%>
			<tr class="trShallow">
				<%
					} else {
				%>
			
			<tr class="trDeep">
				<%
					}
								i++;
				%>
				<td align="center" class="RecordRow" width="30" nowrap>
					<input type="checkbox" name='<bean:write  name="element" property="i9999" filter="true"/>'>
				</td>
				<td align="center" class="RecordRow" nowrap>
					<a href="/servlet/vfsservlet?fileid=<%=fileid %>"  target="_self"><bean:write name="element" property="title" filter="true" />
					&nbsp;
				</td>
				<td align="center" class="RecordRow" nowrap>
					<bean:write name="element" property="ext" filter="true" />
				</td><!--update by xiegh on date 20180313 bug35419   -->
				<td align="center" class="RecordRow" nowrap width="50">
					<a href="/servlet/vfsservlet?fileid=<%=fileid %>"  target="_self">
						<img src="/images/download.png" border=0> </a>
				</td>
			</tr>
		</logic:iterate>
		<tr>
			<td align="left" colspan="4" class="RecordRow">
				<bean:message key="conlumn.resource_list.annotation" /><input type="checkbox"  id="delete_record" name="delete_record" value="0">
			</td>
		</tr>
	</table>
	<table width="100%" align="center">
		<tr>
			<td height="0px"></td>
		</tr>
		<tr>
			<td align=center>
				<input id="btnSave" type="button" class="mybutton" value="<bean:message key="conlumn.resource_list.relevance" />" onClick="save();" />
				<input type="button" class="mybutton" value="<bean:message key="button.return" />" onClick="returnback()" />
			</td>
		</tr>
	</table>
</html:form>