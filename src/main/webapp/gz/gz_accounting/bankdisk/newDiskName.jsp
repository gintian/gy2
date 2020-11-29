<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script type="text/javascript">
		function changeFocus(code){
			var rd=document.getElementsByName("attributeflag");
			var rd1=document.getElementsByName("attributeflag1");
			if(code==1){
				rd[0].checked=true;
				rd1[0].checked=false;
			}else{
				rd[0].checked=false;
				rd1[0].checked=true;
			}
		}
		function save(){
			var retval=new Array();   
			var name=document.getElementsByName("porjectname");
			retval.push(name[0].value);
			var rd=document.getElementsByName("attributeflag");
			var rd1=document.getElementsByName("attributeflag1");
			if(rd1[0].checked==true){
				retval.push('0');
			}
			if(rd[0].checked==true){
				retval.push('1');
			}
			window.returnValue=retval;  
			window.close();
		}
</script>
</head>
<% 
	String name=(String)request.getParameter("inputname");
%>
<body>
<html:form action="/gz/gz_accounting/gzprofilter">

<table width='100%' border="0" cellspacing="0" align="center"
		cellpadding="0" class="ListTable">
<tr>
<td align='center'>
<br>
<table width='97%' border="0" cellspacing="1" align="center"
		cellpadding="1" class="ListTable">
	<tr>
			<td align="left" class="RecordRow" rowspan='2' nowrap>
				&nbsp;&nbsp;
				<%=name %>
				
					
			</td>
			<td align="left" class="RecordRow" rowspan='1' nowrap>
			<input type="text" name="porjectname" size="35"
					value="" />
			</td>
		</tr>
		<tr>
		<td align='center' class="RecordRow">
					<input type="radio" name="attributeflag" onclick="changeFocus(1);" checked>&nbsp;
								<bean:message key="label.gz.private" />
					<input type="radio" name="attributeflag1" onclick="changeFocus(2);">&nbsp;
								<bean:message key="label.gz.public" />
		</td>
		</tr>
		
</table>
</td>
</tr>
<tr>
		<td align='center'class="ListRow">
				&nbsp;&nbsp;&nbsp;&nbsp;
		</td>
</tr>
<tr>
		<td align='center'class="ListRow">
					<input type="button" class='mybutton' name="ok" value="确定" onclick="save()">&nbsp;
					<input type="button" class='mybutton' name="cancel" value="取消" onclick="window.close();">&nbsp;
		</td>
		</tr>
</table>
</html:form>
</body>

</html>