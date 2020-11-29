<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@ page import="java.util.*,
				 com.hjsj.hrms.actionform.performance.options.PerDegreeForm,
				 org.apache.commons.beanutils.LazyDynaBean,				 
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
<% 
	PerDegreeForm perDegreeForm=(PerDegreeForm)session.getAttribute("perDegreeForm");	
	int dataSize = perDegreeForm.getExtpro().size();

%>				 

<style>
.myFixedDiv 
{
    overflow-y:auto;
    overflow-x:hidden;
	width:100%; 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 0pt solid ; 
}
</style>
<hrms:themes />
<script type="text/javascript">
	if('${param.oper}'=='close') {
		parent.window.close();
	}
	
	function showDia(iframe_url, width, height, callback, id) {
	   var config = {
		    width:width,
		    height:height,
		    type:'2',
		    id:id
		}

		modalDialog.showModalDialogs(iframe_url,id,config,callback);
   }
	function edit(num,plan_id)
	{
		var target_url="/performance/options/degreeHighSetAdd.do?b_edit=link`degreeID=${param.degreeID}`num="+num+"`plan_id="+plan_id; 
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	    //var return_vo=window.showModalDialog(iframe_url,'degreeHighSetEdit',"dialogWidth:430px; dialogHeight:420px;resizable:no;center:yes;scroll:no;status:no");
	    showDia(iframe_url, 430, 420, height_edit_ok, "edit_win");
	}
	function height_edit_ok(return_vo) {
		if(!return_vo)
			return false;	   
		if(return_vo.flag=="true")
		{   
			var degreeValues=return_vo.degreeValues;
			var num =return_vo.num;
			var mode=return_vo.mode;
			var value=return_vo.value;
			var grouped=return_vo.grouped;
			var oper=return_vo.oper;
			var UMGrade = return_vo.UMGrade;
			var paramStr="&num="+num+"&mode="+mode+"&oper="+oper+"&value="+value+"&grouped="+grouped+"&degreeValues="+degreeValues+"&UMGrade="+getEncodeStr(UMGrade);
			perDegreeForm.action="/performance/options/degreeHighSetList.do?b_update=link&degreeID=${param.degreeID}"+paramStr;
			perDegreeForm.submit();	
		}  
	}
	function add(plan_id)
	{
		var target_url="/performance/options/degreeHighSetAdd.do?b_add=link`degreeID=${param.degreeID}`plan_id="+plan_id; 
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
      	showDia(iframe_url, 430, 420, height_add_ok, "add_win");
	}
	
	function height_add_ok(return_vo) {
		if(!return_vo)
			return false;	   
		if(return_vo.flag=="true")
		{   
			var degreeValues=return_vo.degreeValues;
			var num =return_vo.num;
			var mode=return_vo.mode;
			var value=return_vo.value;
			var grouped=return_vo.grouped;
			var oper=return_vo.oper;
			var UMGrade = return_vo.UMGrade;
			var paramStr="&num="+num+"&mode="+mode+"&oper="+oper+"&value="+value+"&grouped="+grouped+"&degreeValues="+$URL.encode(degreeValues)+"&UMGrade="+$URL.encode(getEncodeStr(UMGrade));
			perDegreeForm.action="/performance/options/degreeHighSetList.do?b_update=link&degreeID=${param.degreeID}"+paramStr;
			perDegreeForm.submit();	
		} 
	}
	function save()
	{
		var qy = $('qy');
		var used='';
		if(qy.checked==true)
			used='1';
		else
			used='0';

		perDegreeForm.action="/performance/options/degreeHighSetList.do?b_save=link&degreeID=${param.degreeID}&oper=close&used="+used;
		perDegreeForm.submit();	
	}
	/**
	* 判断当前浏览器是否为ie6
	* 返回boolean 可直接用于判断 
	* @returns {Boolean}
	*/
	function isIE6() 
	{ 
		if(navigator.appName == "Microsoft Internet Explorer") 
		{ 
			if(navigator.userAgent.indexOf("MSIE 6.0")>0) 
			{ 
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	function toSorting()
	{
		var target_url="/performance/options/degreeHighSetSort.do?b_query=link`degreeID=${param.degreeID}"; 
      	var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	    var return_vo=window.showModalDialog(iframe_url,'degreeHighSetSort',"dialogWidth:400px; dialogHeight:430px;resizable:no;center:yes;scroll:yes;status:no");	
	    if(!return_vo)
			return false;	   
		if(return_vo.flag=="true")
		{   
			var sortStr = return_vo.sortStr;
			perDegreeForm.action="/performance/options/degreeHighSetList.do?b_sort=link&degreeID=${param.degreeID}&sortStr="+sortStr;
			perDegreeForm.submit();	
		}   
	}
	function del()
	{
		var ids="";
  		var vos=document.getElementsByTagName("input");
		for(var i=0;i<vos.length;i++)  
		{
	  	  if(vos[i].type=="checkbox" && vos[i].id.substring(0,2)=='id' && vos[i].checked==true)	    
	 	  {
	    	 ids +="@"+vos[i].value;	    	
		  }
   		}
   		if(ids=='')
   		{
   			alert('<bean:message key="label.select"/>!');
   			return;
   		}
   		if(confirm("确认删除所选记录吗？"))
    	{
   			perDegreeForm.action="/performance/options/degreeHighSetList.do?b_del=link&degreeID=${param.degreeID}&ids="+ids.substring(1);
   			perDegreeForm.submit();
   		}
	}	
		//移动记录
	function moveRecord(num,move)
	{
		var hashvo=new ParameterSet();
		hashvo.setValue("num",num);
		hashvo.setValue("move",move);		
		hashvo.setValue("opt","16");
		var request=new Request({method:'post',asynchronous:false,onSuccess:moveRecordResult,functionId:'9023000003'},hashvo);
	}
	function moveRecordResult(outparamters)
	{
		window.location='/performance/options/degreeHighSetSort.do?b_query=link&degreeID=${param.degreeID}';
	}
</script>
<html:form action="/performance/options/degreeHighSetList">
	<table width="790px" align="center">
		<tr>
			<td>
				<html:checkbox name="perDegreeForm" property="qy" styleId="qy" value="1"/>
				<bean:message key="jx.param.qyset" />
			按比例计算时取整方式参数:
			
			    <html:radio name="perDegreeForm" property="toRoundOff" value="0"/>取整
			    <html:radio name="perDegreeForm" property="toRoundOff" value="1"/>四舍五入
			</td>
		</tr>
		<tr>
			<td width="100%">
	<div id="myFixedDiv" class="myFixedDiv common_border_color">
		<table width="100%" border="0" cellspacing="0" align="center"
			cellpadding="0" class="ListTable">
			<thead>
				<tr>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows" style="border-left:0px;">
						<bean:message key="column.select" />
					</td>
					<td align="center" class="TableRow_2rows" nowrap rowspan="2">
		 			    <bean:message key="conlumn.mediainfo.info_id"/>
	    			 </td>   
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key="kq.wizard.wise" />
					</td>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key="jx.param.oper" />
					</td>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key="jx.param.value" />
					</td>
					<logic:notEqual name="perDegreeForm" property="itemCount" value="0">
						<td align="center" nowrap class="TableRow"
							colspan="<bean:write name="perDegreeForm" property="itemCount" filter="true"/>">
							<bean:message key="jx.param.degreeItem" />
						</td>
					</logic:notEqual>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key="jx.param.deptGroup2" />
					</td>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key="jx.param.org_grade" />
					</td>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows">
						<bean:message key='lable.tz_template.edit' />
					</td>
					<td align="center" nowrap rowspan="2" class="TableRow_2rows" style="border-right:0px;">
						<bean:message key="label.order"/>
					</td>
				</tr>
				<logic:notEqual name="perDegreeForm" property="itemCount" value="0">
					<tr>
						<logic:iterate id="element" name="perDegreeForm"
							property="degrees">
							<td align="center" nowrap class="TableRow">
								<bean:write name="element" property="itemname" filter="true" />
							</td>
						</logic:iterate>
					</tr>
				</logic:notEqual>
			</thead>
			<%int i=0; %>
			<logic:iterate id="element1" name="perDegreeForm" property="extpro">
				<%
					if (i % 2 == 0)
					{
			%>
				<tr class="trShallow">
					<%
						} else
						{
				%>
				
				<tr class="trDeep">
					<%
						}
						i++;
				%>
					<td align="center" class="RecordRow_right common_border_color" nowrap>
						<input type='checkbox' id='id<%=i%>'
							value='<bean:write name="element1" property="num" filter="true"/>' />
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<%=i %>
					</td>
					<td align="left" class="RecordRow" nowrap>&nbsp;
						<logic:equal name="element1" property="mode" value="1">
							<bean:message key="jx.param.percent" />
						</logic:equal>
						<logic:equal name="element1" property="mode" value="2">
							<bean:message key="jx.param.empCount" />
						</logic:equal>
					</td>
					<td align="left" class="RecordRow" nowrap>&nbsp;
						<logic:equal name="element1" property="oper" value="1">
							<bean:message key="jx.param.noless" />
						</logic:equal>
						<logic:equal name="element1" property="oper" value="2">
							<bean:message key="jx.param.nomore" />
						</logic:equal>
					</td>
					<td align="right" class="RecordRow" nowrap>
						<bean:write name="element1" property="value" filter="true" />&nbsp;
					</td>
					<logic:notEqual name="perDegreeForm" property="itemCount" value="0">
						<logic:iterate id="element2" name="perDegreeForm" property="degrees">
							<bean:define id="nid" name="element2" property="id" />
							<td align="center" class="RecordRow" nowrap>
								<input type="checkbox" disabled="disabled" 
									<logic:equal name="element1" property="degree${nid}" value="1"> 
										checked="checked"
									</logic:equal>	>											
							</td>
						</logic:iterate>
					</logic:notEqual>
					<td align="center" class="RecordRow" nowrap>
						
						<html:select name="element1" property="grouped" size="1" disabled="true" >
				  	  		<html:optionsCollection property="groupList" value="dataValue" label="dataName"/>
						</html:select>
					
					<!-- 
						<input type="checkbox" disabled="disabled"
							<logic:equal name="element1" property="grouped" value="1">checked="checked"</logic:equal>>
					-->
					</td>
					<td align="left" class="RecordRow" nowrap>
						&nbsp;<bean:write name="element1" property="UMGrade" filter="true" />
					</td>
					<td align="center" class="RecordRow" nowrap>
						<img src="/images/edit.gif" border=0 style="cursor:hand;"
							onclick="edit('<bean:write name="element1" property="num" filter="true"/>','<bean:write name="perDegreeForm" property="plan_id" />');">
					</td>
					<td align="center" class="RecordRow_left common_border_color" nowrap>
						<logic:notEqual name="element1" property="count" value="1">
							<a href="javaScript:moveRecord('<bean:write name="element1" property="num" filter="true"/>','up')">
							<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
						</logic:notEqual>
						<logic:equal name="element1" property="count" value="1">																		
							&nbsp;&nbsp;&nbsp;
						</logic:equal>
						<%	
							LazyDynaBean a_bean=(LazyDynaBean)pageContext.getAttribute("element1");									
							String count = null==(String)a_bean.get("count")?"0":(String)a_bean.get("count");
							if(Integer.parseInt(count)==dataSize){
						%>
							&nbsp;&nbsp;&nbsp;
						<% }else{%>
						
						<a href="javaScript:moveRecord('<bean:write name="element1" property="num" filter="true"/>','down')">
						<img src="../../images/down01.gif" width="12" height="17" border=0></a> 
						<% }%>
					</td>
				</tr>
			</logic:iterate>
		</table>
	</div>
		</td>
		</tr>
		</table>	
	<table width="60%" align="center">
		<tr>
			<td align="center">
				<input type='button' class="mybutton" property="b_add"
					onclick="add('<bean:write name="perDegreeForm" property="plan_id" />')"
					value='<bean:message key="button.insert"/>' />
		
				<input type='button' class="mybutton" property="b_delete"
					onclick='del()'
					value='<bean:message key="button.delete"/>' />
			
				<input type='button' class="mybutton" property="b_ok"
					onclick='save()' value='<bean:message key="button.ok"/>' />
					
				<input type='button' class="mybutton" property="b_cancel"
					onclick='parent.window.close();' value='<bean:message key="button.cancel"/>' />
			</td>
		</tr>
	</table>
    <script>
        var myFixedDiv = document.getElementById("myFixedDiv");
        if(myFixedDiv){
            myFixedDiv.style.height=(document.body.clientHeight-100)+"px";
        }
    </script>
</html:form>
