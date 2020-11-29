<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<script>
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
	 
 	function showDia(iframe_url, width, height, callback, id) {
	   var config = {
		    width:width,
		    height:height,
		    type:'2',
		    id:id
		}

		modalDialog.showModalDialogs(iframe_url,id,config,callback);
   	}
 
   function add()
   {
       target_url="/performance/options/perDegreedescAdd.do?b_add=link`info=save"; 
       var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
       
       showDia(iframe_url, 470, 400, perde_add_ok, "perde_win");
       /* if(isIE6()){
       		var return_vo=window.showModalDialog(iframe_url,'perdreedescAddWin','dialogWidth:470px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
       }else{
       		var return_vo=window.showModalDialog(iframe_url,'perdreedescAddWin','dialogWidth:450px; dialogHeight:400px;resizable:no;center:yes;scroll:no;status:no');
       } */
   }
   
   function perde_add_ok(return_vo) {
	   if(return_vo==null)
			return false;	
	   if(return_vo.flag=="true") 
	   	 	reflesh(); 
   }
   function edit(id)
   {
	    var target_url="/performance/options/perDegreedescAdd.do?b_edit=link`id="+id+"`info=edit";
	    var iframe_url="/general/query/common/iframe_query.jsp?src="+$URL.encode(target_url);
	    /* if(isIE6()){
	        var return_vo=window.showModalDialog(iframe_url,'perdreedescEditWin','dialogWidth:460px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no');
	    }else{
	        var return_vo=window.showModalDialog(iframe_url,'perdreedescEditWin','dialogWidth:450px; dialogHeight:410px;resizable:no;center:yes;scroll:no;status:no');
	    } */
	    showDia(iframe_url, 470, 400, perde_add_ok, "perde_edit_win");
   }

	function checkdelete(){
			var str="";
			for(var i=0;i<document.perDegreedescForm.elements.length;i++)
			{
				if(document.perDegreedescForm.elements[i].type=="checkbox")
				{
					if(document.perDegreedescForm.elements[i].checked==true && document.perDegreedescForm.elements[i].name!="selbox")
					{
						str+=document.perDegreedescForm.elements[i+1].value+"/";
					}
				}
			}
			if(str.length==0)
			{
				alert("请选择要删除的等级项目！");
				return;
			}else{
				if(confirm("确认删除所选等级项目吗？"))
    			{
					perDegreedescForm.action="/performance/options/perDegreedescList.do?b_delete=link&deletestr="+str; 
				 	perDegreedescForm.submit();
				}
			}
	  }
	  
	  
	function IfWindowClosed() {
		if (newwindow.closed == true) { 
			window.clearInterval(timer)
			perDegreedescForm.action="/performance/options/perDegreedescList.do?b_query=link";
		    perDegreedescForm.submit();
		}
	}
	function reflesh()
	{		
			perDegreedescForm.action="/performance/options/perDegreedescList.do?b_query=link&degreeId=${perDegreedescForm.degreeId}";
		    perDegreedescForm.submit();
	} 

	

</script>
<style>
.fixedHeaderTr{
    /* 49263 V76封版绩效管理：ie兼容，参数设置/等级分类，拖动边框以后，缺线，见附件 haosl*/
    position: static !important;
}
.myfixedDiv2 
{ 
	overflow:auto;
	BORDER-BOTTOM: #C5C5C5 1pt solid; 
    BORDER-LEFT: #C5C5C5 1pt solid; 
    BORDER-RIGHT: #C5C5C5 1pt solid; 
    BORDER-TOP: #C5C5C5 1pt solid ; 
}
</style>
<hrms:themes />
<%
int i = 0;
%>
<html:form action="/performance/options/perDegreedescList">
	<input type="hidden" name="degreeId"
		value="${perDegreedescForm.degreeId}" />
	<table width="100%" border="0" cellspacing="5" align="center" cellpadding="0">
		<tr>
			<td align="left" nowrap>
<div>
	<table width="100%" border="0" cellspacing="0" align="center" id="idTable"
		cellpadding="0" class="ListTable myfixedDiv2 ">
			<tr class="fixedHeaderTr">
				<td align="center" class="TableRow_r common_background_color common_border_color" nowrap >
					<input type="checkbox" name="selbox" onclick="batch_select(this, 'setlistform.select');">
				</td>
				<td align="center" class="TableRow_r" nowrap >
					<bean:message key="kjg.gather.xuhao" />
				</td>
				<td align="center" class="TableRow_r" nowrap >
					<bean:message key="jx.param.degreepro" />
				</td>
				
				<logic:equal name="perDegreeForm" property="busitype" value="0">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.xishu" />
					</td>
				</logic:equal>
				
				<logic:equal name="perDegreedescForm" property="flag" value="0">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markup" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markdown" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="1">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.bilivalue" />
					</td>					
				</logic:equal>

				<logic:equal name="perDegreedescForm" property="flag" value="2">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markup" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markdown" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.bilivalue" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="3">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markup" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markdown" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.bilivalue" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="4">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markup" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markdown" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="5">
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markup" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.markdown" />
					</td>
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="gz.columns.desc" />
					</td>
				</logic:equal>
				
				<logic:equal name="perDegreeForm" property="busitype" value="0">				
					<td align="center" class="TableRow_r" nowrap >
						<bean:message key="jx.param.limit" />
					</td>
				</logic:equal>
				
				<td align="center" class="TableRow_left common_background_color common_border_color" nowrap style="border-top-width:0px;">
					<bean:message key="label.edit.user" />
				</td>
			</tr>
		<hrms:extenditerate id="element" name="perDegreedescForm"
			property="setlistform.list" indexes="indexes"
			pagination="setlistform.pagination" pageCount="1000" scope="session">
			<bean:define id="nid" name="element" property="string(id)" />
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
				<td align="center" class="RecordRow_right" nowrap>
					<hrms:checkmultibox name="perDegreedescForm"
						property="setlistform.select" value="true" indexes="indexes" />
				</td>
				<td align="left"  class="RecordRow" nowrap> &nbsp;
					<!--<bean:write name="element" property="string(id)" filter="true" />-->
					<%=i %>
					<Input type='hidden'
						value='<bean:write name="element" property="string(id)" filter="true"/>'
						name='id' />
				</td>
				<td align="left"  class="RecordRow" nowrap> &nbsp;
					<bean:write name="element" property="string(itemname)"
						filter="false" />
				</td>
				
				<logic:equal name="perDegreeForm" property="busitype" value="0">
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(xishu)"
							filter="false" />&nbsp;
					</td>
				</logic:equal>
				
				<logic:equal name="perDegreedescForm" property="flag" value="0">
					<td align="right"  class="RecordRow" nowrap>
						<bean:write name="element" property="string(topscore)"
							filter="true" />&nbsp;
					</td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(bottomscore)"
							filter="true" />&nbsp;
					</td>
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
							filter="false" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="1">
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
						filter="false" />
				    </td>
					<td align="right"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(percentvalue)"
							filter="true" />&nbsp;
					
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="2">
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(topscore)"
							filter="true" />&nbsp;
					</td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(bottomscore)"
							filter="true" />&nbsp;
					</td>
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
						filter="false" />
				    </td>
					<td align="right"  class="RecordRow" nowrap>
						<bean:write name="element" property="string(percentvalue)"
							filter="true" />&nbsp;
				
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="3">
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(topscore)"
							filter="true" />&nbsp;
					</td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(bottomscore)"
							filter="true" />&nbsp;
					</td>
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
						filter="false" />
				    </td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(percentvalue)"
							filter="true" />&nbsp;
					
					</td>
				</logic:equal>	
				<logic:equal name="perDegreedescForm" property="flag" value="4">
					<td align="right"  class="RecordRow" nowrap>
						<bean:write name="element" property="string(topscore)"
							filter="true" />&nbsp;
					</td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(bottomscore)"
							filter="true" />&nbsp;
					</td>
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
							filter="false" />
					</td>
				</logic:equal>
				<logic:equal name="perDegreedescForm" property="flag" value="5">
					<td align="right"  class="RecordRow" nowrap>
						<bean:write name="element" property="string(topscore)"
							filter="true" />&nbsp;
					</td>
					<td align="right"  class="RecordRow" nowrap> 
						<bean:write name="element" property="string(bottomscore)"
							filter="true" />&nbsp;
					</td>
					<td align="left"  class="RecordRow" nowrap> &nbsp;
						<bean:write name="element" property="string(itemdesc)"
							filter="false" />
					</td>
				</logic:equal>
				
				<logic:equal name="perDegreeForm" property="busitype" value="0">			
					<td align="right"  class="RecordRow" nowrap>
						<%if(i==1){ %>
						<bean:write name="element" property="string(strict)" filter="true" /> &nbsp;
						<%} %>
					</td>
				</logic:equal>	
				
				<td align="center"  class="RecordRow_left" nowrap>
					<a
						onclick="edit('<bean:write name="element" property="string(id)" filter="true"/>');"><img
							src="/images/edit.gif" border=0 style="cursor:hand;">
					</a>
				</td>
			</tr>
		</hrms:extenditerate>
	</table>
	</div>
	</td>
	</tr>
	<tr>
		<td>
			<table width="100%" border="0" >
		<tr>
			<td align="left">
	
					<input type='button' class="mybutton" property="b_add"
						onclick='add()' value='<bean:message key="jx.param.addegreepro"/>' />

					<input type='button' class="mybutton" property="b_delete"
						onclick='checkdelete()'
						value='<bean:message key="jx.param.deldegreepro"/>' />

			</td>
		</tr>
	</table>
		</td>
	</tr>
	</table>
	
</html:form>
<script>
	// 当表格只有表头时，设定表头单元格下边框的宽度为1px lium
	var table = document.getElementById("idTable");
	if (table) {
		var rows = table.rows;
		if (rows.length === 1) {
			var cells = rows[0].cells;
			for (var i = 0; i < cells.length; i++) {
				cells[i].style.borderBottomWidth = "1px";
			}
		}
	}
</script>