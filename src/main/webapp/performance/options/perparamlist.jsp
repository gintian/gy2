<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script type="text/javascript" src="/general/tipwizard/returnT.js"></script>
<style>
.myfixedDiv 
{ 
	overflow:auto;
    BORDER-BOTTOM: #94B6E6 1pt solid;
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>
<hrms:themes />
<script>
   function add(){
	   var target_url="/performance/options/perparamstartadd.jsp"; 
	   //var return_vo= window.showModalDialog(target_url, "perparamlistAddWin", 
	   //           "dialogWidth:630px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:no");
	   
	   var config = {
		    width:680,
		    height:630,
		    type:'2'
		}

		modalDialog.showModalDialogs(target_url,"add_win",config,add_ok);
	   
	}
   
   function add_ok(return_vo) {
	   if(!return_vo)
			return false;
		if(return_vo.flag=="true")
			reflesh();	
   }
   function edit(id)
   {   			
	   var target_url="/performance/options/perParamAdd.do?b_edit=link`id="+id+"`info=edit";
	   var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	   //var return_vo=window.showModalDialog(iframe_url,'perparamlistEditWin','dialogWidth:630px; dialogHeight:630px;resizable:no;center:yes;scroll:no;status:no');
 	   
	   var config = {
		    width:660,
		    height:630,
		    type:'2'
		}

		modalDialog.showModalDialogs(iframe_url,"add_win",config,add_ok);
	}

	function checkdelete(){
			var str="";
			for(var i=0;i<document.perParamForm.elements.length;i++)
			{
				if(document.perParamForm.elements[i].type=="checkbox")
				{
					if(document.perParamForm.elements[i].checked==true  && document.perParamForm.elements[i].name!="selbox")
					{
						str+=document.perParamForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert('<bean:message key="jx.paramset.selDel"/>');
				return;
			}else{
				if(confirm("确认删除所选评语模板吗？"))
    			{
					perParamForm.action="/performance/options/perParamList.do?b_delete=link&from_eval=${perParamForm.from_eval}&deletestr="+str; 
				 	perParamForm.submit();
				}
				if(window.opener)
					window.opener.location.reload();
			}
	  }
	  
	  
	function IfWindowClosed() {
		if (newwindow.closed == true) { 
			window.clearInterval(timer)
			perParamForm.action="/performance/options/perParamList.do?b_query=link&modelflag=${param.modelflag}";
		    perParamForm.submit();
		}
	}
	function reflesh()
	{	
		if(window.opener)
			window.opener.location.reload();
		document.perParamForm.action="/performance/options/perParamList.do?b_query=link&from_eval=${perParamForm.from_eval}&modelflag=${param.modelflag}";
		document.perParamForm.submit();
	}
</script>
<%
int i = 0;
String temp=request.getParameter("modelflag");
%>
<html:form action="/performance/options/perParamList">
	<table width="100%" border="0">
		<tr>
			<td>
				<div id="myfixedDiv" class="myfixedDiv common_border_color">
					<table width="100%" style="border:0px" border="0" cellspacing="0" align="center"
						cellpadding="0" class="ListTableF1">
						<tr class="fixedHeaderTr1">
							<td align="center" class="TableRow" nowrap style="border-left:0px;">
								<input type="checkbox" name="selbox"
									onclick="batch_select(this, 'setlistform.select');">
							</td>
							<td align="center"  class="TableRow" nowrap>
								<bean:message key="report.number" />
							</td>
							<td align="center"  class="TableRow" nowrap>
								<bean:message key="column.name" />
							</td>
							<td align="center"  class="TableRow" nowrap>
								<bean:message key="conlumn.board.content" />
							</td>
							<td align="center" style="border-right:0px;" class="TableRow" nowrap>
								<bean:message key="lable.tz_template.edit" />
							</td>
						</tr>
						<hrms:extenditerate id="element" name="perParamForm"
							property="setlistform.list" indexes="indexes"
							pagination="setlistform.pagination" pageCount="1000"
							scope="session">
							<bean:define id="nid" name="element" property="string(id)" />
							<logic:notEqual name="element" property="string(id)" value="1">
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
									<td align="center" class="RecordRow" nowrap style="border-left:0px;">
										<hrms:checkmultibox name="perParamForm"
											property="setlistform.select" value="true" indexes="indexes" />
									</td>
									<td align="left"  class="RecordRow" nowrap>
										<bean:write name="element" property="string(id)" filter="true" />
										<Input type='hidden'
											value='<bean:write name="element" property="string(id)" filter="true"/>'
											name='id' />
									</td>
									<td align="left"  class="RecordRow" nowrap>
										<bean:write name="element" property="string(param_name)"
											filter="true" />
									</td>
									<td align="left"  class="RecordRow">
										<bean:write name="element" property="string(content)"
											filter="false" />
									</td>
									<td align="center" style="border-right:0px;" class="RecordRow" nowrap>
										<a
											onclick="edit('<bean:write name="element" property="string(id)" filter="true"/>')"><img
												src="/images/edit.gif" border=0 style="cursor:hand;">
										</a>
									</td>
								</tr>
							</logic:notEqual>
						</hrms:extenditerate>
					</table>
				</div>
			</td>
		</tr>
	</table>
	<table width="100%">
		<tr>
			<td align="center">

					<input type='button' class="mybutton" property="b_add"
						onclick='add()' value='<bean:message key="button.insert"/>' />
		
					<input type='button' class="mybutton" property="b_delete"
						onclick='checkdelete()'
						value='<bean:message key="button.delete"/>' />
		
				<logic:equal name="perParamForm" property="from_eval" value="0">
							 <%if("capability".equals(temp)){ %>
         <hrms:tipwizardbutton flag="capability" target="il_body" formname="perParamForm"/>  
         <%}else if("performance".equals(temp)){ %>
         <hrms:tipwizardbutton flag="performance" target="il_body" formname="perParamForm"/>  
         <%} %> 	 
				</logic:equal>
				<logic:equal name="perParamForm" property="from_eval" value="1">
					<input type='button' class="mybutton" property="b_return"
						onclick='window.close();'
						value='<bean:message key="button.close"/>' />
				</logic:equal>
			</td>
		</tr>
	</table>
    <script type="text/javascript">
        var myfixedDiv = document.getElementById("myfixedDiv");
        if(myfixedDiv){
            myfixedDiv.style.height=(document.body.clientHeight-100)+"px";
            myfixedDiv.style.width=(document.body.clientWidth-22)+"px";
        }
    </script>
</html:form>
