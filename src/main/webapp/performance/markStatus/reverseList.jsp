<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%@ page import="java.util.*,	
				 com.hjsj.hrms.actionform.performance.markStatus.markStatusForm,			 
				 com.hrms.hjsj.sys.DataDictionary,
				 com.hrms.hjsj.sys.FieldItem,
				 com.hrms.struts.taglib.CommonData,
				 com.hjsj.hrms.utils.ResourceFactory,
				 com.hrms.struts.valueobject.UserView,
				 com.hrms.struts.constant.WebConstant" %>
				 
<%  
		markStatusForm markStatusForm=(markStatusForm)session.getAttribute("markStatusForm");
		String object_type=(String)markStatusForm.getObject_type();	  // 1:部门  2:人员
		String selectFashion=(String)markStatusForm.getSelectFashion(); // 查询方式 1:按考核主体  2:考核对象
		String plan_id =(String)markStatusForm.getCheckPlanId(); // 考核计划号
		String b0110=(String)markStatusForm.getB0110(); // 单位	
		String e0122=(String)markStatusForm.getE0122(); // 部门
		String type=(String)markStatusForm.getType();  // 打分状态	
		
		UserView userView=(UserView)session.getAttribute(WebConstant.userView);
		
%>
				 
<html>
  <head>
<hrms:themes />
  </head>
  <script type="text/javascript" src="/js/constant.js"></script>
  
<style>

.myfixedDiv
{ 
	overflow:auto; 
	height:expression(document.body.clientHeight-150);
	width:expression(document.body.clientWidth-10); 
	BORDER-BOTTOM: #94B6E6 1pt solid; 
    BORDER-LEFT: #94B6E6 1pt solid; 
    BORDER-RIGHT: #94B6E6 1pt solid; 
    BORDER-TOP: #94B6E6 1pt solid ; 
}
</style>

<script type="text/javascript">

// 导出Excel
function ecportExcel()
{	
 	var hashVo=new ParameterSet();
 	hashVo.setValue("plan_id",getEncodeStr("<%=plan_id%>"));
 	hashVo.setValue("object_type","<%=object_type%>");
 	hashVo.setValue("selectFashion","<%=selectFashion%>");
   	hashVo.setValue("b0110","<%=b0110%>");
   	hashVo.setValue("e0122","<%=e0122%>");
   	hashVo.setValue("type","<%=type%>");
    var request=new Request({method:'post',asynchronous:false,onSuccess:sucess,functionId:'90100170289'},hashVo);			
}
function sucess(outparameters)
{
 	var outname=outparameters.getValue("name");
// 	var name=outname.substring(0,outname.length-1)+".xls";
// 	name=getEncodeStr(name);
  	window.location.target="_blank";
	//20/3/6 xus vfs改造
  	window.location.href = "/servlet/vfsservlet?fileid="+outname+"&fromjavafolder=true";
}

// 返回统计页面
function backScoreStatus()
{
   	markStatusForm.action="/performance/markStatus/markStatusList.do?b_select=query";
	markStatusForm.submit();
}

</script>

<html:form action="/performance/markStatus/reverseResultList">
   	<br/>
   	<table width="90%" border="0" cellspacing="0"  align="center" cellpadding="0">	
   		<tr>
   			<td width='100%'>
			   	<table width="100%" border="0" cellspacing="0"  align="center" cellpadding="0" class="ListTable">	
			   	  	<thead>
				   	     <%
							 	FieldItem fielditem = DataDictionary.getFieldItem("E0122");			  			 	
					     %>
				           <tr>
				            <td align="center"  class="TableRow" nowrap>
				            	<bean:message key="conlumn.mediainfo.info_id"/>
				             </td>
				            <td align="center"  class="TableRow" nowrap>
				        		&nbsp;<bean:message key="b0110.label"/>
				            </td>       
				   		 <%
							 	if((!object_type.equalsIgnoreCase("2")) && (selectFashion.equalsIgnoreCase("2"))){			  			 	
					     %>
				   			<td align="center"  class="TableRow" nowrap>
				        		&nbsp;<bean:message key="org.performance.unorum"/>
				            </td> 
				   		 <%}else{%>  		 
				   			<td align="center"   class="TableRow" nowrap>
				        		&nbsp;<%=fielditem.getItemdesc()%>
				            </td> 
				            <td align="center"   class="TableRow" nowrap>
				        		&nbsp;<bean:message key="e01a1.label"/>
				            </td> 
				            <td align="center"  class="TableRow" nowrap>
				        		&nbsp;<bean:message key="kq.card.emp.name"/>
				            </td> 
				         <%}%>
				         </tr>
			     	</thead>
			   
				   	<% int i=1; %>
				    <hrms:extenditerate id="element" name="markStatusForm" property="personListForm.list" indexes="indexes"  pagination="personListForm.pagination" pageCount="20" scope="session">
				   		<%
				          if(i%2==1)
				          {
				          %>
				          <tr class="trShallow">
				          <%}
				          else
				          {%>
				          <tr class="trDeep">
				          <%
				          }
				          %>
					         <td align="center" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="numbers" filter="false"/></td>
					         <td align="left" class="RecordRow" nowrap>&nbsp;<bean:write name="element" property="b0110" filter="false"/></td>
					            
					         <%
								 	if((!object_type.equalsIgnoreCase("2")) && (selectFashion.equalsIgnoreCase("2"))){			  			 	
						     %>
					   			<td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;
					            </td> 
					   		 <%}else{%> 
					   			<td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="e0122" filter="false"/>&nbsp;
					            </td> 
					            <td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="e01a1" filter="false"/>&nbsp;
					            </td> 
					            <td align="left" class="RecordRow" nowrap>
					        		&nbsp;<bean:write name="element" property="a0101" filter="false"/>&nbsp;
					            </td> 
					         <%}%>
				         </tr>
				         
				         <% i++; %>
				   	</hrms:extenditerate>
			    </table> 
    		</td>
		</tr>
		<tr>
			<td width='100%'>
			   	<table width="100%" align="center" class="RecordRowP">
					<tr>
						<td valign="bottom" class="tdFontcolor">
							<bean:message key="label.page.serial"/>
							<bean:write name="markStatusForm" property="personListForm.pagination.current" filter="true" />
							<bean:message key="label.page.sum"/>
							<bean:write name="markStatusForm" property="personListForm.pagination.count" filter="true" />
							<bean:message key="label.page.row"/>
							<bean:write name="markStatusForm" property="personListForm.pagination.pages" filter="true" />
							<bean:message key="label.page.page"/>
						</td>
					    <td align="right" nowrap class="tdFontcolor">
							<p align="right">
							<hrms:paginationlink name="markStatusForm" property="personListForm.pagination" nameId="personListForm" propertyId="roleListProperty">
							</hrms:paginationlink>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td width='100%' style="height:35px"> 	
				<input type="button" name="outExcel" class="mybutton" value="导出Excel" onclick="ecportExcel();"/>	
      			<input type="button" name="addbutton" value="<bean:message key="kq.search_feast.back"/>" class="mybutton" onclick="backScoreStatus();">     			  			
    		</td>
		</tr>
	</table>
   </html:form>
</html>
