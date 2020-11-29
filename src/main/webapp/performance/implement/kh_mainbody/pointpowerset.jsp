<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,com.hjsj.hrms.actionform.performance.implement.ImplementForm,org.apache.commons.beanutils.LazyDynaBean"%>
<%
	    ImplementForm implementForm = (ImplementForm) session.getAttribute("implementForm");
	    ArrayList pointPowerHeadList = implementForm.getPointPowerHeadList();
	    ArrayList pointItemList=implementForm.getPointItemList();
	    ArrayList pointPowerList=implementForm.getPointPowerList();
	    ArrayList itemprivList=implementForm.getItemprivList();
	    int _index=0;
	    String _classname="t_cell_locked";
%>
<script language="JavaScript" src="../implement.js"></script>

<script type="text/javascript">
    var theWidth = (document.documentElement.clientWidth ||document.body.clientWidth)-10;
var IVersion=getBrowseVersion();

if(IVersion==8)
{
  	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked_8.css\" rel=\"stylesheet\" type=\"text/css\">");
}else
{
  	document.writeln("<link href=\"/performance/kh_plan/kh_planTableLocked.css\" rel=\"stylesheet\" type=\"text/css\">");
}

</script>

<!--
<link href="/performance/kh_plan/kh_planTableLocked.css" rel="stylesheet" type="text/css">
-->
<style>
div#tbl-container {
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
}
.tbl-container
{
	width:100%;
	overflow:auto;
	BORDER-BOTTOM:#94B6E6 1pt solid;
	BORDER-LEFT: #94B6E6 1pt solid;
	BORDER-RIGHT: #94B6E6 1pt solid;
	BORDER-TOP: #94B6E6 1pt solid;
}
.fixedHeaderTr
{
	position:relative;
	top:expression(this.offsetParent.scrollTop);

}
</style>

<script type="text/javascript">
	function resizeWindowRefrsh()
	{
		var aclientHeight=document.body.clientHeight || document.documentElement.clientHeight;
		document.getElementById('tbl-container').style.height=aclientHeight-110;
	}
function batch_selectXXX(obj,name)
  {
  	if(obj.checked)
  	  setCheckState(1,name);
  	else
  	  setCheckState(2,name);
  }

  function setCheckState(flag,name)
  {
      var chklist,objname,i,typeanme;
      chklist=document.getElementsByTagName('INPUT');
      if(!chklist)
        return;
	  for(i=0;i<chklist.length;i++)
	  {
	     typeanme=chklist[i].type.toLowerCase();
	     if(typeanme!="checkbox")
	        continue;
	     if(chklist[i].disabled)
	     	continue;
	     objname=chklist[i].name;
	     if(objname==name)
			{
	     if(flag=="1")
  	       chklist[i].checked=true;
  	     else
  	       chklist[i].checked=false;
  	       }
	  }
  }
</script>
<body onResize="resizeWindowRefrsh()" >
<html:form action="/performance/implement/kh_mainbody/powerset">
	 <table width="100%">
	 	<tr>
	 		<td style="height:30px"  align="left">
	对象类别
	<html:select name="implementForm" property="khObject" size="1" styleId="object_id"
		onchange="searchKhMainBody2('${implementForm.planid}','${implementForm.power_type}');" >
		<html:optionsCollection property="khObjectClassList" value="dataValue"
			label="dataName" />

	</html:select>
	&nbsp;&nbsp;
		主体类别
	<html:select name="implementForm" property="khKey" size="1" styleId="object_id"
		onchange="searchKhMainBody2('${implementForm.planid}','${implementForm.power_type}');" >

		<html:optionsCollection property="khKeyClassList" value="dataValue"
			label="dataName" />

	</html:select>


	</td>
	</tr>
	</table>
	<!--<div style='height:300;width:100%; overflow: auto;' id='aa' > <div class="myFixedDiv"> -->
	<script language='javascript' >
            var theHeight = document.body.clientHeight || document.documentElement.clientHeight;
			document.write("<div id=\"tbl-container\" class=\"tbl-container common_border_color\" style='position:absolute;left:5px;height:"+(theHeight-110)+"px;width:"+theWidth+"px;'  >");
 	</script>
	 <logic:equal name="implementForm" property="power_type" value="point">
	 	<table border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
		  <thead>
       			 <tr >

       			 <td align="center"  style="border-left:0px;border-top:0px;" rowspan="2"  class="TableRow" nowrap ><bean:message key="lable.appraisemutual.examineobject" /></td>
       			 <td align="center"  style="border-top:0px;"   class="TableRow" rowspan="2" nowrap><bean:message key="lable.performance.perMainBodySort" /></td>
       			 <td align="center"  style="border-top:0px;"   class="TableRow" rowspan="2" nowrap><bean:message key="lable.performance.perMainBody" /></td>
       			 <% for(int j=0;j<pointPowerHeadList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointPowerHeadList.get(j);
       			 		String pointname=(String)abean.get("pointname");
       			  %>
       			 <td align="center" style="border-top:0px;"  class="TableRow" ><%=pointname%></td>
       			 <% } %>
       			 </tr>

       			 <tr >
       			 <% for(int j=0;j<pointPowerHeadList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointPowerHeadList.get(j);
       			 		String pointname=(String)abean.get("pointname");
       			  %>
       			 <td align="center" class="TableRow"><input type="checkbox" name="_pointPriv<%=j %>" onclick="batch_selectXXX(this,'pointPriv<%=j %>');setAllPointPriv(this,'pointPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'></td>
       			 <% } %>
       			 </tr>
       		</thead>

       		<logic:iterate id="element" name="implementForm" property="pointPowerList" >
       		 <%
       			 _index++;
       		 	if(_index ==pointPowerList.size())
       		 		_classname="t_cell_locked_b";
       		 %>
       			 <tr>
       			     <td align="left" style="border-left:0px;"  class="RecordRow" nowrap > &nbsp;<bean:write name="element" property="objectname" filter="true"/></td>
	       			 <td align="left"   class="RecordRow" nowrap > &nbsp;<bean:write name="element" property="bodyType" filter="true"/></td>
	       			 <td align="left"   class="RecordRow" nowrap > &nbsp;<bean:write name="element" property="bodyname" filter="true"/></td>
	       			 <% for(int j=0;j<pointPowerHeadList.size();j++){
	       			 		LazyDynaBean abean=(LazyDynaBean)pointPowerHeadList.get(j);
	       			 		String point_id=(String)abean.get("point_id");
	       			 		point_id=point_id.toLowerCase();
	       			 %>

	        			<td align="center"   class="RecordRow"  nowrap>
	       			&nbsp;&nbsp;<input type='checkbox' name='pointPriv<%=j %>' value="<bean:write name="element" property="mainbody_id" filter="true"/>:<bean:write name="element" property="objectid" filter="true"/>:<%=point_id%>:${implementForm.planid}"  onchange='setPointPriv("<bean:write name="element" property="mainbody_id" filter="true"/>","<bean:write name="element" property="objectid" filter="true"/>","<%=point_id%>","${param.plan_id}",this)'
	       			value='1'
	       			<logic:equal name="element" property="<%=point_id%>"  value="1">checked</logic:equal>  />&nbsp;&nbsp;
	       		</td>
	       			 <% } %>
       			 </tr>
       		</logic:iterate>
	</table>
	 	<script language='javascript' >
	<% for(int j=0;j<pointPowerHeadList.size();j++){%>
	var temp=document.getElementsByName("pointPriv<%=j %>");
	var str="1";
	for(var i=0;i<temp.length;i++){
		if(!temp[i].checked){
			str="2";
			break;
		}
	}
	if(str=="1"){
		var temp1=document.getElementsByName("_pointPriv<%=j %>");
		temp1[0].checked=true;
	}
	<%}%>
 		</script>
	 </logic:equal>
	<logic:equal name="implementForm" property="power_type" value="item">
		<table   style="margin-top:-3"  border="0" cellspacing="0"  align="left" cellpadding="0" class="ListTable">
			<thead>
       			 <tr >
       			 <td align="center" style="border-left:0pt;" rowspan="2" class="TableRow" nowrap ><bean:message key="lable.appraisemutual.examineobject"/></td>
       			 <td align="center"  rowspan="2"  class="TableRow" nowrap ><bean:message key="lable.performance.perMainBodySort"/></td>
       			 <% for(int j=0;j<pointItemList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
       			 		String itemdesc=(String)abean.get("itemdesc");
       			  %>
       			 <td align="center"  class="TableRow"   ><%=itemdesc%></td>
       			 <% } %>
       			 </tr>

       			 <tr >
       			 <% for(int j=0;j<pointItemList.size();j++){
       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
       			 		String itemdesc=(String)abean.get("itemdesc");
       			  %>
       			 <td align="center"  class="TableRow"   ><input type="checkbox" name="_itemPriv<%=j %>" onclick="batch_selectXXX(this,'itemPriv<%=j %>');AllitemPriv(this,'itemPriv<%=j %>');" title='<bean:message key="label.query.selectall"/>'></td>
       			 <% } %>
       			 </tr>
       		</head>


       		<logic:iterate id="element" name="implementForm" property="itemprivList" >

       			 <tr>
       			 	<%-- 确认的主体复选框置灰 modify by 刘蒙 --%>
       			 	<bean:define id="disabled" value="" />
       			 	<logic:equal name="element" property="planBodyOpt" value="1">
       			 		<bean:define id="disabled" value="disabled" />
       			 	</logic:equal>
       			 	 <td align="left" style="border-left:0pt;""  class="RecordRow" nowrap > 	&nbsp;<bean:write name="element" property="a0101" filter="true"/></td>
	       			 <td align="left"  class="RecordRow" nowrap > 	&nbsp; <bean:write name="element" property="bodyName" filter="true"/></td>
	       			 <% for(int j=0;j<pointItemList.size();j++){
	       			 		LazyDynaBean abean=(LazyDynaBean)pointItemList.get(j);
	       			 		String item_id=((String)abean.get("item_id"));
	       			  %>

	       			 	<td align="center"   class="RecordRow"  >
	       	&nbsp;&nbsp;&nbsp;&nbsp;<input type='checkbox' name='itemPriv<%=j %>' ${disabled } value="<bean:write name="element" property="object_id" filter="true"/>:<bean:write name="element" property="body_id" filter="true"/>:<%=item_id%>:${implementForm.planid}"  onclick='setItemPriv("<bean:write name="element" property="object_id" filter="true"/>","<bean:write name="element" property="body_id" filter="true"/>","<%=item_id%>","${implementForm.planid}",this)'   value='1' <logic:equal name="element" property="<%=item_id%>"  value="1">checked</logic:equal>  />&nbsp;&nbsp;&nbsp;&nbsp;
	       			 	</td>

	       			 <% } %>

       			 </tr>
       		</logic:iterate>
			</table>
		<script language='javascript' >
<% for(int j=0;j<pointItemList.size();j++){%>

	var temp=document.getElementsByName("itemPriv<%=j %>");
	var str="1";
	for(var i=0;i<temp.length;i++){
		if(!temp[i].checked){
			str="2";
			break;
		}
	}
	if(str=="1"){
		var temp1=document.getElementsByName("_itemPriv<%=j %>");
		temp1[0].checked=true;
	}
<%}%>
 		</script>
	</logic:equal>
	</div>

	<script language='javascript' >
		document.write("<div  id='page'  style='position:absolute;left:5px;bottom:10px;width:"+theWidth+"px;'  >");
	</script>
	<table width='100%' align='center'>
		<tr>
			<td align='center'>
				<logic:equal name="implementForm" property="power_type" value="item">
					 <%if(itemprivList!=null && itemprivList.size()>0) {%>
					<input type='button' id="b3"
							value='&nbsp;<bean:message key='button.all.select' />&nbsp;' onclick='batch_selectAll("1");' class="mybutton">
					&nbsp;
					<input type='button' id="b4"
							value='&nbsp;<bean:message key='button.all.reset' />&nbsp;' onclick='batch_selectAll("0");' class="mybutton">
					&nbsp;
					<script type="text/javascript">
						// 复写batch_selectAll(implement.js:793) 全选/全撤项目权限 by 刘蒙
						function batch_selectAll(theFlag) {
							var temp = document.getElementsByTagName("input");
						  	var item_ids = new Array();
						  	for(var i = 0; i < temp.length; i++) {
						  		if (temp[i].type != "checkbox") continue;
						  		if (temp[i].disabled) continue; // 禁用的复选框弃之不理

							  	if(temp[i].name.substring(0, 8) == "itemPriv") {
							  		temp[i].checked = theFlag == '1' ? true : false;
									item_ids[item_ids.length] = temp[i].value;
								} else if(temp[i].name.substring(0, 9) == "_itemPriv") {
									temp[i].checked = theFlag == '1' ? true : false;
								}
							}

						  	if (item_ids.length === 0) {
						  		return;
						  	}

							var hashvo = new ParameterSet();
							hashvo.setValue("item_ids", item_ids);
							if (theFlag == '1') {
								hashvo.setValue("item_value", "1");
							} else {
							    hashvo.setValue("item_value", "0");
						    }
							hashvo.setValue("opt", "8");
							var request = new Request({method:'post', asynchronous:false, functionId:'9023000003'}, hashvo);
						}
					</script>
					<!--
						<hrms:priv func_id="326030123">
					<input type='button'
							value='&nbsp;<bean:message key='performance.implement.restoreitempower' />&nbsp;'
							class="mybutton" onclick='recoverPrivAll("${implementForm.planid}",$F("object_id"),"${implementForm.power_type}")'>
						</hrms:priv>
						-->
						<%} %>
				</logic:equal>

				<logic:equal name="implementForm" property="power_type" value="point">
									 <%if(pointPowerList!=null && pointPowerList.size()>0) {%>
					<input type='button' id="b1"
							value='&nbsp;<bean:message key='button.all.select' />&nbsp;' onclick='selectAll("1");' class="mybutton">
					<input type='button' id="b2"
							value='&nbsp;<bean:message key='button.all.reset' />&nbsp;' onclick='selectAll("0");' class="mybutton">
										<%} %>
				</logic:equal>

					<input type='button' id="button_goback"
							value='&nbsp;<bean:message key='button.close' />&nbsp;'
							onclick='parent.window.close();' class="mybutton">
			</td>
		</tr>
	</table>
</div>
</html:form>
</body>