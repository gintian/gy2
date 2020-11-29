<%@ page contentType="text/html; charset=UTF-8" language="java"%>
<%@ page import="com.hrms.struts.valueobject.UserView"%>
<%@ page import="com.hrms.struts.constant.WebConstant"%>
<%@ taglib uri="/WEB-INF/tlds/taglib.tld" prefix="hrms" %>
<%
	
	String name= request.getParameter("name");
%>

<hrms:themes />
<script>


function returnName(){
	   var name = document.getElementById("name").value;
	   window.returnValue = name;
	   window.close();
}
</script>

<style>
.selfTdRow {
	border: inset 1px #C4D8EE;
	BORDER-BOTTOM: #C4D8EE 1pt solid; 
	BORDER-LEFT: #C4D8EE 1pt solid; 
	BORDER-RIGHT: #C4D8EE 1pt solid; 
	BORDER-TOP: #C4D8EE 1pt solid;
	font-size: 12px;
	border-collapse:collapse; 
}

</style>
<div class="fixedDiv3">
<table width="100%" align="center" class="RecordRow" style="border: none">
    <tr>
       <td align="center" class="TableRow common_border_color" >
                              修改考勤点
       </td>
    </tr>
    
    <tr>
      <td  align="center" class="selfTdRow common_border_color" height="150">
                                 考勤点名称 &nbsp; <input type="text" id="name" size="30" class="textColorWrite" value='<%=name %>'>
      </td>
    </tr>
    
    <tr>
    <td align="center" style="border: none">
         <br>
         <button class="mybutton" onclick="returnName()">确定</button>
         <button class="mybutton" onclick="top.close()">关闭</button>
    </td>
  </tr>
</table>
</div>