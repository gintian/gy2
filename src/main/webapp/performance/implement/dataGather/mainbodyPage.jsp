<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %>
<%@ taglib uri="/tags/struts-html" prefix="html" %>
<%@ taglib uri="/tags/struts-logic" prefix="logic" %>
<html>
  <head>
 
  </head>
  	

	
  <body>
  <html:form action="/performance/implement/dataGather">
     <bean:message key="lable.performance.perMainBody"/>:<Br><hr>
  	<table border=0  width='100%' id='a_table'  cellSpacing=0 >
  	
  	<% int i=0; %>
		<logic:iterate id="element" name="dataGatherForm" property="mainbodyList" >
			<tr id='<bean:write name="element" property="body_id" filter="true"/>' onclick='selectRow(this)' style='cursor:pointer;' <%=(i==0?" class='selectedBackGroud' ":"")%>  >
				<td width='10' ><img src='/images/man.gif' /></td> 
				<td  > <bean:write name="element" property="context" filter="true"/></td> 
			</tr>
			<%
			
			 i++; %>
		</logic:iterate>
	</table>
	
	<script language='javascript' >
	
	var table = document.getElementById('a_table');
	if(table.rows.length>0)
	{
		selectRow(document.getElementById(table.rows[0].id));
	}else if('${dataGatherForm.gather_type}'!='0')// 0 网上 1 机读 2:网上+机读
	{
		parent.r_body.location="/performance/implement/dataGather.do?b_grade=show&planId=${dataGatherForm.planId}&object_id=${dataGatherForm.object_id}";
	}
	var ori_obj;
	
	function selectRow(obj)
	{
		if(ori_obj)
			ori_obj.className='';
		obj.className='selectedBackGroud';
		ori_obj=obj;
		showGradePage(obj.id)
		
	}
	
	
	//展现打分界面
	function showGradePage(bodyId)
	{
		parent.r_body.location="/performance/implement/dataGather.do?b_grade=show&planId=${dataGatherForm.planId}&object_id=${dataGatherForm.object_id}&mainbody_id="+bodyId;
	}
	
	function  editMainBodyRecord(flag)
	{
		var temp=ori_obj.cells[1].innerHTML;
		var i=temp.indexOf("(");
		if(flag=='1')
			ori_obj.cells[1].innerHTML=temp.substring(0,i)+"("+EDITING+")";
		else if(flag=='2')
			ori_obj.cells[1].innerHTML=temp.substring(0,i)+"("+SUBED+")";
		else if(flag=='-1')
			ori_obj.cells[1].innerHTML=temp.substring(0,i)+"("+NOSCORE+")";
	}
	function  editMainBodyRecord2(flag)
	{   //0 未打分 2 都打分了 3 部分打分
		var temp=ori_obj.cells[1].innerHTML;
		var i=temp.indexOf("(");
		if(flag=='3')
			ori_obj.cells[1].innerHTML=temp.substring(0,i)+"(部分打分)";
		else if(flag=='2')
			ori_obj.cells[1].innerHTML=temp.substring(0,i);
		else if(flag=='0')
			ori_obj.cells[1].innerHTML=temp.substring(0,i)+"("+NOSCORE+")";
	}
	</script>
  </html:form>
  </body>
</html>
