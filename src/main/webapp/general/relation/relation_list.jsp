<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@page import="com.hrms.struts.valueobject.UserView"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<%@page import="com.hjsj.hrms.actionform.general.relation.GenRelationForm,com.hrms.struts.valueobject.PaginationForm"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<script language="JavaScript" src="/js/function.js"></script>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<script type="text/javascript" src="/js/hjsjUrlEncode.js"></script>
<style>
.myfixedDiv 
{ 
	overflow:auto; 
	/* height:expression(document.body.clientHeight-100); */
	width:100%;
	/* BORDER-BOTTOM: #94B6E6 1pt solid;  */
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes></hrms:themes>
<script>
   function add(){   
	   var target_url="/general/relation/relationmaintence.do?b_add=link`relation_id=";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no;");
	   if(return_vo==null)
			 return ;	   
	   if(return_vo.flag) 
	   		 reflesh();   	
	}
   function reflesh(){
		window.location.href="/general/relation/relationmaintence.do?b_query=link";
   }
function setType(aaa,relation_id){
 	var hashvo=new ParameterSet();
		if(aaa.checked)
		hashvo.setValue("validflag","1");
		else
		hashvo.setValue("validflag","0");
		hashvo.setValue("info","editvalidflag");
		hashvo.setValue("relation_id",relation_id);
		
		var request=new Request({method:'post',asynchronous:false,onSuccess:afterSave2,functionId:'1010070031'},hashvo);	   
	
}	
	function afterSave2(outparamters){
		}
function moveRecord(relation_id,move)
{
	
	var hashvo=new ParameterSet();
		hashvo.setValue("info","editorder");
		hashvo.setValue("relation_id",relation_id);
		hashvo.setValue("move",move);
		var request=new Request({method:'post',asynchronous:false,onSuccess:afterSaveOrder,functionId:'1010070031'},hashvo);
}	
function afterSaveOrder(outparamters){
		var errorinfo=outparamters.getValue("errorinfo");
		if(errorinfo!=null&&errorinfo.length>0){
			alert(errorinfo);
		}
		reflesh();
		}	
function checkdelete(){
			var str="";
			for(var i=0;i<document.genRelationForm.elements.length;i++)
			{
				if(document.genRelationForm.elements[i].type=="checkbox")
				{
					if(document.genRelationForm.elements[i].checked==true && document.genRelationForm.elements[i].name!="selbox"&&document.genRelationForm.elements[i].name.indexOf("operater")==-1)
					{
						str+=document.genRelationForm.elements[i+1].value+"/";
						
					}
				}
			}
			if(str.length==0)
			{
				alert("<bean:message key='jx.paramset.selDel'/>");
				return;
			}else
			{
				var message = "";
					message = "确认删除审批关系吗？";
				
				if(confirm(message))
    			{	
					//genRelationForm.action="/performance/options/checkBodyObjectList.do?b_delete=link&deletestr="+str+"&bodyType="+bodyType;
				 	//genRelationForm.submit();
				 	
				 	 	
				 	var hashvo=new ParameterSet();			
					hashvo.setValue("deletestr",str);
					hashvo.setValue("info","delete");
					var request=new Request({method:'post',asynchronous:false,onSuccess:afterSaveOrder,functionId:'1010070031'},hashvo);	
				}
			}
		}
function update(){
  
	var num=0;
	var str="";
	var relation_id="";
  			for(var i=0;i<document.genRelationForm.elements.length;i++)
			{
				if(document.genRelationForm.elements[i].type=="checkbox")
				{
					if(document.genRelationForm.elements[i].checked==true && document.genRelationForm.elements[i].name!="selbox"&&document.genRelationForm.elements[i].name.indexOf("operater")==-1)
					{
						str=document.genRelationForm.elements[i+2].value;
						relation_id=document.genRelationForm.elements[i+1].value;
  						num++;
					}
				}
			}
  		if(num==0)
  		{
  			alert("请选择需要修改的的审批关系！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert("每次只能修改一个审批关系！");
  		    return;
  		}
	   var target_url="/general/relation/relationmaintence.do?b_add=link`relation_id="+relation_id;
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   var return_vo= window.showModalDialog(iframe_url, "", 
	              "dialogWidth:400px; dialogHeight:300px;resizable:no;center:yes;scroll:no;status:no;");
	   if(return_vo==null)
			 return ;	   
	   if(return_vo.flag) 
	   		 reflesh();   

}	
function saveAs()
{
	var num=0;
	var str="";
	var relation_id="";
	var default_line="";
  			for(var i=0;i<document.genRelationForm.elements.length;i++)
			{
				if(document.genRelationForm.elements[i].type=="checkbox")
				{
					if(document.genRelationForm.elements[i].checked==true && document.genRelationForm.elements[i].name!="selbox"&&document.genRelationForm.elements[i].name.indexOf("operater")==-1)
					{
						str=document.genRelationForm.elements[i+2].value;
						relation_id=document.genRelationForm.elements[i+1].value;
						default_line=document.genRelationForm.elements[i+3].value;
  						num++;
					}
				}
			}
  		if(num==0)
  		{
  			alert("请选择需要另存的审批关系！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert("每次只能另存一个审批关系！");
  		    return;
  		}
		
		
		
		var name=window.prompt("请输入另存的审批关系：",str);
		if(name&&IsOverStrLength(trim(name),50))
		{
			alert("输入的审批关系名字过长!");
			return;
		}
		if(name&&trim(name).length>0)
		{
			name=replaceAll(name,"'","’");
			name=replaceAll(name,"\"","”");
			var hashvo=new ParameterSet();
            hashvo.setValue("cname",getEncodeStr(name));
            hashvo.setValue("info","saveas");
            hashvo.setValue("relation_id",relation_id);
            hashvo.setValue("default_line",default_line);
            var request=new Request({asynchronous:false,onSuccess:afterSaveOrder,functionId:'1010070031'},hashvo);		
		}
}
function saveRename()
{
	var num=0;
	var str="";
	var relation_id="";
  			for(var i=0;i<document.genRelationForm.elements.length;i++)
			{
				if(document.genRelationForm.elements[i].type=="checkbox")
				{
					if(document.genRelationForm.elements[i].checked==true && document.genRelationForm.elements[i].name!="selbox"&&document.genRelationForm.elements[i].name.indexOf("operater")==-1)
					{
						str=document.genRelationForm.elements[i+2].value;
						relation_id=document.genRelationForm.elements[i+1].value;
  						num++;
					}
				}
			}
  		if(num==0)
  		{
  			alert("请选择需要重命名的审批关系！");
  		    return;
  		}
		
		if(num>1)
		{
  			alert("每次只能重命名一个审批关系！");
  		    return;
  		}
		
		
		
		var name=window.prompt("请输入重命名的审批关系：",str);
		if(name&&IsOverStrLength(trim(name),50))
		{
			alert("输入的审批关系名字过长!");
			return;
		}
		if(name&&trim(name).length>0)
		{
			name=replaceAll(name,"'","’");
			name=replaceAll(name,"\"","”");
			var hashvo=new ParameterSet();
            hashvo.setValue("cname",getEncodeStr(name));
            hashvo.setValue("info","saverename");
            hashvo.setValue("relation_id",relation_id);
            var request=new Request({asynchronous:false,onSuccess:afterSaveOrder,functionId:'1010070031'},hashvo);		
		}
}
function edit(relationid,actor_type){
  url = "/general/relation/relationmaintence.do?b_int=link&relationid="+relationid+"&actor_type="+actor_type;
  window.location = url;
}
</script>
<%
UserView userView=(UserView)session.getAttribute(WebConstant.userView);
int i = 0;
int iend =0;
GenRelationForm genRelationForm = (GenRelationForm)session.getAttribute("genRelationForm");
if(genRelationForm!=null){
PaginationForm setlistform =	genRelationForm.getSetlistform();
 iend = setlistform.getList().size();
}
boolean privflag = userView.hasTheFunction("9A5111");
 
%>
<body onbeforeunload="">

	<html:form action="/general/relation/relationmaintence">

	<table width="80%" border="0" align="center">
		<tr>
			<td>
			<div class="myfixedDiv common_border_color">
		<table width="100%" border="0" cellspacing="0" align="center" cellpadding="1" class="ListTableF" style="border:0;">
			<thead>
				<tr class="fixedHeaderTr">
					<td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;" >
						<input type="checkbox" name="selbox"
							onclick="batch_select(this, 'setlistform.select');">
					</td>
					<td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
						<bean:message key="report.number" />
					</td>


					
						<td align="center" width="200" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
							审批关系
						</td>
						<td align="center" width="120" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
							依赖关系
						</td>
						<td align="center" width="120" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
							是否是主汇报关系
						</td>
						<td align="center" width="120" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
							审批对象类型
						</td>

						<td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px;" >
							有效
						</td>
						<td align="center"  width="120" class="TableRow" nowrap style="border-top:0px;border-left:0px;">
							审批关系设置
						</td>
						<td align="center" class="TableRow" nowrap style="border-top:0px;border-left:0px; border-right:0px;">
							排序
						</td>
				</tr>
			</thead>
			<hrms:extenditerate id="element" name="genRelationForm"
				property="setlistform.list" indexes="indexes"
				pagination="setlistform.pagination" pageCount="10000" scope="session">
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
					<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
							<hrms:checkmultibox name="genRelationForm"
								property="setlistform.select" value="true" indexes="indexes" />
								<Input type='hidden'
							value='<bean:write name="element" property="relation_id" filter="true"/>'
							name='relation_id' />
							<Input type='hidden'
							value='<bean:write name="element" property="cname" filter="true"/>'
							name='cname' />
								<Input type='hidden'
							value='<bean:write name="element" property="default_line" filter="true"/>'
							name='default_line' />
					</td>
					<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
						 &nbsp;<bean:write name="element" property="relation_id"
							filter="true" />
					</td>
					<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
						 &nbsp;<bean:write name="element" property="cname"
							filter="true" />
					</td>
					
				   <td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;"><!-- 依赖关系 -->
						 &nbsp;<bean:write name="element" property="relying"/>
					</td>
					<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;"><!-- 是否是主汇报关系 -->
					<logic:equal value="1" name="element" property="default_line">&nbsp;&nbsp;是</logic:equal>
					<logic:notEqual value="1"  name="element" property="default_line">&nbsp;&nbsp;否</logic:notEqual>	
					</td>
					<td align="left" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
						 &nbsp;<logic:equal name="element" property="actor_type"
							value="1">
							自助用户
							</logic:equal>
							<logic:equal name="element" property="actor_type"
							value="4">
							业务用户
							</logic:equal>
					</td>

					<td lign="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
					<%if(privflag){ 
					%>
						 &nbsp;<logic:equal name="element" property="validflag"
							value="0">
							<input type="checkbox" name="operater<%=i %>"  onclick="setType(this,'<bean:write name="element" property="relation_id" filter="true" />')" >
							</logic:equal>
							<logic:equal name="element" property="validflag"
							value="1">
							<input type="checkbox" name="operater<%=i %>"  onclick="setType(this,'<bean:write name="element" property="relation_id" filter="true" />')" checked >
							</logic:equal>
					<% 
					}else{
					%>
							&nbsp;<logic:equal name="element" property="validflag"
                            value="0">
                            <input type="checkbox" name="operater<%=i %>"  onclick="setType(this,'<bean:write name="element" property="relation_id" filter="true" />')" disabled>
                            </logic:equal>
                            <logic:equal name="element" property="validflag"
                            value="1">
                            <input type="checkbox" name="operater<%=i %>"  onclick="setType(this,'<bean:write name="element" property="relation_id" filter="true" />')" checked disabled>
                            </logic:equal>
					<%
					} 
					%>
					</td>

					<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;">
					
						<a
							onclick="edit('<bean:write name="element" property="relation_id" filter="true"/>','<bean:write name="element" property="actor_type" filter="true"/>');"><img
								src="/images/edit.gif" border=0 style="cursor:hand;"><!-- 审批关系设置 -->
						</a>

					</td>
					<td align="center" class="RecordRow" nowrap style="border-top:0px;border-left:0px;border-right:0px;">
									<% if(i!=1){%>
									&nbsp;<a href="javaScript:moveRecord('<bean:write name="element" property="relation_id" filter="true"/>','up',this)">
									<img src="../../images/up01.gif" width="12" height="17" border=0></a> 
									<% }else{%>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<% }if(i!=iend){%>
									<a href="javaScript:moveRecord('<bean:write name="element" property="relation_id" filter="true"/>','down',this)">
									<img src="../../images/down01.gif" width="12" height="17" border=0></a> &nbsp;
									<% }else{%>
									&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
									<% }%>
														
					</td>
				</tr>
				
			</hrms:extenditerate>
		</table>
		</div>
	</td>
				</tr>
	</table>

		<table width="100%">
			<tr>
				<td align="center">
						 <hrms:priv func_id="9A5101"> 
						<input type='button' class="mybutton" property="b_add"
							onclick='add()' value='<bean:message key="button.insert"/>' />
						</hrms:priv>
						<hrms:priv func_id="9A5101"> 
						  <input type='button' class="mybutton" property="b_update"
							onclick='update()' value='<bean:message key="label.edit"/>' />
						</hrms:priv>
						 <hrms:priv func_id="9A5102"> 
						<input type='button' class="mybutton" property="b_delete"
							onclick='checkdelete()'
							value='<bean:message key="button.delete"/>' />
							</hrms:priv>
							 <hrms:priv func_id="9A5103"> 
	         			<input type='button' class="mybutton"   onclick='saveAs()' value='<bean:message key="button.other_save"/>'  />
	         			</hrms:priv>
	         			 <hrms:priv func_id="9A5104"> 
						<input type='button' class="mybutton"   onclick='saveRename()' value='<bean:message key="button.rename"/>'  />
						</hrms:priv>
						<logic:equal name="genRelationForm" property="isshowbutton" value="true">
							<input type="button" class="mybutton" onclick="window.close();" value="关闭"/>
						</logic:equal>
				</td>
			</tr>
		</table>
	</html:form>
</body>
